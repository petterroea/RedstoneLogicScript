package com.petterroea.redstonelogicscript.compiler;

public class ModuleExpression {
	private String leftSide;
	private String rightSide;
	private int lineNumber = 0;
	private String fileName;
	
	public ModuleExpression(String leftSide, String rightSide, int lineNumber, String fileName) {
		this.leftSide = leftSide.trim();
		this.rightSide = rightSide.trim();
		this.lineNumber = lineNumber;
		this.fileName = fileName;
	}

	public String getLeftSide() {
		return leftSide;
	}

	public String getRightSide() {
		return rightSide;
	}

	public int getLineNumber() {
		return lineNumber;
	}
	
	public String getFileName() {
		return fileName;
	}
	
}
