package com.petterroea.redstonelogicscript.compiler;

public class CompilerException extends RuntimeException {
	public CompilerException(String message, String file, int line) {
		super(file +  " L" + line + ": " + message);
	}
	
	public CompilerException(String message) {
		this(message, CompilerState.state.getCurrentFile(), CompilerState.state.getLineNumber());
	}
}
