package com.petterroea.redstonelogicscript.compiler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.management.RuntimeErrorException;

import com.petterroea.redstonelogicscript.compiler.CompilerSettings.CompilerMode;
import com.petterroea.redstonelogicscript.compiler.elements.Module;
import com.petterroea.redstonelogicscript.compiler.elements.ModuleParser;
import com.petterroea.redstonelogicscript.minecraft.ModuleContainer;

public class Compiler {
	
	private CompilerSettings settings;
	
	private HashMap<String, Module> modules = new HashMap<String, Module>();
	private ArrayList<String> includedFiles = new ArrayList<String>();
	
	private String destination;
	
	public Compiler(CompilerSettings settings, String destFile) {
		this.settings = settings;
		this.destination = destFile;
	}
	
	public void parseFile(String filename, boolean isMainFile) {
		System.out.println("Parsing " + filename);
		
		File file = settings.findLibraryInIncludePath(filename);
		if(file == null) {
			throw new RuntimeException("Source file " + filename + " does not exist in any import location");
		}
		if(file.isDirectory()) {
			throw new RuntimeException("Source file " + filename + " is a directory");
		}
		StringCursor cursor = new StringCursor(file);
		cursor.skipSpacesAndNewlines(); //Put us at the first interesting character
		while(!cursor.isDone()) {
			String currentWord = cursor.readUntilNotAlphanumeretic();
			switch(currentWord) {
			case "module":
				cursor.readChar();
				cursor.skipSpaces();
				String moduleName = cursor.readUntilNotAlphanumeretic();
				
				if(modules.containsKey(moduleName)) {
					throw new CompilerException("Module " + moduleName + " is already defined");
				}
				cursor.skipSpaces();
				boolean isMain = false;
				if(cursor.peekChar() == '!') {
					cursor.readChar();
					String flag = cursor.readUntilNotAlphanumeretic();
					
					if(!flag.equals("main")) {
						throw new CompilerException("Unknown module flag " + flag);
					}
					isMain = true;
				}
				
				Module module = ModuleParser.parseModule(cursor, moduleName);
				
				if(isMain) {
					CompilerState.state.setInitModule(module);
				}
				
				modules.put(moduleName, module);
				break;
			case "include":
				cursor.expectChar(' ');
				cursor.expectChar('"');
				String name = cursor.readString();
				cursor.expectChar('"');
				if(!includedFiles.contains(name)) {
					System.out.println("Importing " + name);
					parseFile(name, false);
				} else {
					if(settings.getVerboseFlag())
						System.out.println("Skipping already imported file " + name);
				}
				cursor.skipSpacesAndNewlines();
				cursor.expectChar(';');
				cursor.skipSpacesAndNewlines();
				break;
			default:
				throw new CompilerException("Expected \"module\", \"include\", not " + currentWord);
			}
			cursor.skipSpacesAndNewlines();
		}
		
	}
	
	public void validateModules() {
		Iterator it = modules.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        
	        ((Module)pair.getValue()).validateExpressions(this);
	        
	        it.remove(); // avoids a ConcurrentModificationException
	    }
	}
	
	public Module getModule(String name) {
		return modules.get(name);
	}

	public static void main(String[] args) {
		System.out.println("Redstone Logic script compiler");
		if(args.length == 0) {
			displayHelpAndExit();
		}
		
		CompilerSettings settings = new CompilerSettings();
		
		String sourceFile = "";
		String destFile = "";
		
		for(int i = 0; i < args.length; i++) {
			//System.out.println(args[i]);
			switch(args[i]) {
			case "-C":
				settings.setCompilerMode(CompilerMode.COMPILE);
				break;
			case "-D":
				settings.setCompilerMode(CompilerMode.PARSE);
				break;
			case "-V":
				settings.setVerboseFlag(true);
				break;
			case "-L":
				settings.addLibraryLocation(args[++i]);
				break;
			default:
				if(sourceFile.length() == 0) {
					sourceFile = args[i];
				} else if(destFile.length() == 0) {
					destFile = args[i];
				} else {
					System.out.println("Encountered an invalid argument: " + args[i]);
					displayHelpAndExit();
				}
				break;
			}
		}
		if(sourceFile.length() == 0) {
			throw new RuntimeException("No input file is specified");
		}
		if(destFile.length() == 0) {
			throw new RuntimeException("No destination file is specified");
		}
		Compiler compiler = new Compiler(settings, destFile);
		long startTime = System.currentTimeMillis();
		compiler.parseFile(sourceFile, true);
		compiler.validateModules();
		
		if(CompilerState.state.getInitModule() == null) {
			throw new CompilerException("No main module defined", "<No file>", 0);
		}
		ModuleContainer world = new ModuleContainer(CompilerState.state.getInitModule());
		world.generateStructures();
		
		System.out.println("Finished parsing and validation in " + (System.currentTimeMillis() - startTime) + "ms.");
	}
	
	private static void displayHelpAndExit() {
		System.out.println("Usage:");
		System.out.println("rls [options] [sourcefile]");
		System.out.println("Options:");
		System.out.println("    -C compile mode");
		System.out.println("    -D dry run(only parsing)");
		System.out.println("    -V verbose mode");
		System.out.println("    -L specify a library location");
		System.exit(0);
	}

}
