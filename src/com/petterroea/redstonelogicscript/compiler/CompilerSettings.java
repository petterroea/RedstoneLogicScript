package com.petterroea.redstonelogicscript.compiler;

import java.io.File;
import java.util.LinkedList;

public class CompilerSettings {
	public enum CompilerMode {
		COMPILE,
		PARSE
	}
	private boolean verbose = false;
	private CompilerMode mode = CompilerMode.PARSE;
	private LinkedList<File> libraryLocations = new LinkedList<File>();
	
	public static CompilerSettings settingsSingleton;
	
	public CompilerSettings() {
		settingsSingleton = this;
	}
	
	public void setVerboseFlag(boolean flag) {
		this.verbose = flag;
	}
	
	public void setCompilerMode(CompilerMode mode) {
		this.mode = mode;
	}
	
	public CompilerMode getCompilerMode() {
		return this.mode;
	}
	
	public boolean getVerboseFlag() {
		return this.verbose;
	}
	
	public void addLibraryLocation(String libraryLocation) {
		File f = new File(libraryLocation);
		if(!f.exists()) {
			throw new RuntimeException("Library folder \"" + libraryLocation + "\" doesn't exist");
		}
		if(!f.isDirectory()) {
			throw new RuntimeException("Library location \"" + libraryLocation + "\" must be a folder");
		}
		libraryLocations.add(f);
	}
	
	public String[] getLibraryLocations() {
		return (String[])libraryLocations.toArray();
	}

	public File findLibraryInIncludePath(String name) {
		File rootFile = new File(name);
		if(rootFile.exists())
			return rootFile;
		
		for(File f : libraryLocations) {
			for(File c : f.listFiles()) {
				if(c.getName().equals(name)) {
					return c;
				}
			}
		}
		return null;
	}
}
