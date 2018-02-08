package com.petterroea.redstonelogicscript.compiler;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;

public class StringCursor {
	private String fileContents;
	private String fileName;
	
	private int currentLineNumber = 1; // Bookkeeping for the compiler state
	private int lineNumberSaved = 1;
	
	private int cursorPosition = 0;
	private int cursorPositionSaved = 0;
	
	public StringCursor(File file) {
		fileContents = com.petterroea.util.FileUtils.readFile(file);
		fileName = file.getAbsolutePath();
		CompilerState.state.setCurrentFile(fileName);
	}
	
	private void handleState(char ch) {
		CompilerState.state.setCurrentFile(fileName);
		CompilerState.state.setLineNumber(currentLineNumber);
		if(ch == '\n') {
			CompilerState.state.setLineNumber(++currentLineNumber);
		} else if(ch == '#') {
			while(fileContents.charAt(cursorPosition) != '\n') {
				cursorPosition++;
			}
			//cursorPosition++;
			CompilerState.state.setLineNumber(++currentLineNumber);
		}
	}
	
	public char readChar() {
		handleState(fileContents.charAt(cursorPosition));
		return fileContents.charAt(cursorPosition++);
	}
	
	public char expectChar(char c) {
		char read = readChar();
		if(read != c) {
			throw new CompilerException("Invalid character, expected " + c + ", got " + read);
		}
		return read;
	}
	
	public char peekChar() {
		return fileContents.charAt(cursorPosition);
	}
	
	public String readString(int count) {
		StringBuilder sb = new StringBuilder(count);
		for(int i = 0; i < count; i++) {
			handleState(fileContents.charAt(cursorPosition+i));
			sb.append(fileContents.charAt(cursorPosition+i));
		}
		cursorPosition += count;
		return sb.toString();
	}
	
	public String peekString(int count) {
		return fileContents.substring(cursorPosition, cursorPosition+count);
	}
	
	public String readUntilNotAlphanumeretic() {
		StringBuilder sb = new StringBuilder(fileContents.length()-cursorPosition);
		while(cursorPosition < fileContents.length()) {
			char currentChar = fileContents.charAt(cursorPosition);
			if(!(Character.isAlphabetic(currentChar) || Character.isDigit(currentChar))) {
				return sb.toString();
			}
			sb.append(currentChar);
			cursorPosition++;
		}
		return sb.toString();
	}
	
	public String readString() {
		StringBuilder sb = new StringBuilder(fileContents.length()-cursorPosition);
		while(cursorPosition < fileContents.length()) {
			char currentChar = fileContents.charAt(cursorPosition);
			if(currentChar == '"') {
				return sb.toString();
			}
			else if(currentChar == '\\') {
				sb.append(fileContents.charAt(++cursorPosition));
			} else {
				sb.append(currentChar);
			}
			cursorPosition++;
		}
		return sb.toString();
	}
	public String readUntil(char c) {
		StringBuilder sb = new StringBuilder(fileContents.length()-cursorPosition);
		while(cursorPosition < fileContents.length()) {
			char currentChar = fileContents.charAt(cursorPosition);
			if(currentChar == c) {
				return sb.toString();
			}
			else if(currentChar == '\n') {
				throw new CompilerException("Expected " + c + ", not newline");
			}
			sb.append(currentChar);
			handleState(currentChar);
			cursorPosition++;
		}
		return sb.toString();
	}
	
	public int skipSpaces() {
		int skipped = 0;
		while(fileContents.charAt(cursorPosition) == ' ' || fileContents.charAt(cursorPosition) == '\t' || fileContents.charAt(cursorPosition) == '#') {
			handleState(fileContents.charAt(cursorPosition));
			cursorPosition++;
			skipped++;
		}
		handleState(fileContents.charAt(cursorPosition));
		return skipped;
	}
	
	public int skipSpacesAndNewlines() {
		int skipped = 0;
		while(cursorPosition < fileContents.length() && 
				(fileContents.charAt(cursorPosition) == ' ' || 
				fileContents.charAt(cursorPosition) == '\n' || 
				fileContents.charAt(cursorPosition) == '\t' || 
				fileContents.charAt(cursorPosition) == '#')) {
			handleState(fileContents.charAt(cursorPosition));
			cursorPosition++;
			skipped++;
		}
		if(cursorPosition==fileContents.length()) {
			return skipped;
		}
		handleState(fileContents.charAt(cursorPosition));
		return skipped;
	}
	
	public void saveCursorPosition() {
		cursorPositionSaved = cursorPosition;
		lineNumberSaved = currentLineNumber;
	}
	
	public void revertCursorPosition() {
		cursorPosition = cursorPositionSaved;
		currentLineNumber = lineNumberSaved;
	}
	
	public boolean isDone() {
		return cursorPosition >= fileContents.length();
	}

}
