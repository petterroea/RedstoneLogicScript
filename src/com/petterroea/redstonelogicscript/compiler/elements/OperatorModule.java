package com.petterroea.redstonelogicscript.compiler.elements;

import com.petterroea.redstonelogicscript.compiler.CompilerException;

public class OperatorModule {
	private Module module;
	private String leftSideName;
	private String rightSideName;
	private String outputName;
	private String file;
	private String operator;
	private int lineNumber;
	
	public OperatorModule(String operator, Module module, String leftSide, String rightSide, String output, String file, int lineNumber) {
		if(operator.length()>2) {
			throw new CompilerException("Operators must be a maximum of two characters wide");
		}
		this.module = module;
		this.leftSideName = leftSide;
		this.rightSideName = rightSide;
		this.outputName = output;
		this.file = file;
		this.lineNumber = lineNumber;
		this.operator = operator;
	}
	
	public String getOperator() {
		return operator;
	}
	
	public int getLineNumber() {
		return lineNumber;
	}
	
	public String getFileName() {
		return file;
	}

	public Module getModule() {
		return module;
	}

	public String getLeftSideName() {
		return leftSideName;
	}

	public String getRightSideName() {
		return rightSideName;
	}
	
	public String getOutputName() {
		return outputName;
	}

}
