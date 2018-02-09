package com.petterroea.redstonelogicscript.compiler;

import java.util.HashMap;

import com.petterroea.redstonelogicscript.compiler.elements.Module;
import com.petterroea.redstonelogicscript.compiler.elements.OperatorModule;

//Its a singleton
public class CompilerState {
	private String currentFile = "<no file>";
	private String currentModule = "<no module>";
	private int lineNumber = 0;
	private Module initModule;
	
	private HashMap<String, OperatorModule> operators = new HashMap<String, OperatorModule>();
	
	public static CompilerState state = new CompilerState();
	
	public CompilerState() {
		
	}

	public String getCurrentFile() {
		return currentFile;
	}

	public void setCurrentFile(String currentFile) {
		this.currentFile = currentFile;
	}

	public String getCurrentModule() {
		return currentModule;
	}

	public void setCurrentModule(String currentModule) {
		this.currentModule = currentModule;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}
	
	public void RegisterOperator(String operatorString, OperatorModule operatorModule) {
		if(operators.containsKey(operatorString))
			throw new CompilerException("Operator " + operatorString + " is already defined");
		operators.put(operatorString, operatorModule);
	}
	
	public OperatorModule getOperator(String name) {
		return operators.get(name);
	}

	public Module getInitModule() {
		return initModule;
	}
	
	public void setInitModule(Module module) {
		this.initModule = module;
	}
	
}
