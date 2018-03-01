package com.petterroea.redstonelogicscript.compiler.elements;

import com.petterroea.redstonelogicscript.blockAbstraction.Model;
import com.petterroea.redstonelogicscript.compiler.CompilerException;
import com.petterroea.redstonelogicscript.compiler.CompilerState;
import com.petterroea.redstonelogicscript.compiler.ModuleExpression;
import com.petterroea.redstonelogicscript.compiler.StringCursor;
import com.petterroea.redstonelogicscript.compiler.elements.ConnectionPoint.ConnectionPointType;
import com.petterroea.redstonelogicscript.utils.IntegerVector3;
import com.petterroea.redstonelogicscript.utils.Logger;

public class ModuleParser {
	public static Module parseModule(StringCursor cursor, String moduleName) {
		Module module = new Module(moduleName);
		cursor.skipSpacesAndNewlines();
		
		cursor.expectChar('{');
		
		cursor.skipSpacesAndNewlines();
		while(cursor.peekChar() != '}') {
			cursor.skipSpacesAndNewlines();
			String word = cursor.readUntilNotAlphanumeretic();
			switch(word) {
			case "input":
				cursor.expectChar(' ');
				
				String name = cursor.readUntilNotAlphanumeretic();
				ConnectionPoint cp = new ConnectionPoint(name, ConnectionPointType.IN, module);
				module.addConnectionPoint(name, cp);
				
				cursor.expectChar(';');
				break;
			case "output":
				cursor.expectChar(' ');
				
				name = cursor.readUntilNotAlphanumeretic();
				
				cursor.skipSpacesAndNewlines();
				
				if(cursor.peekChar() == '!') {
					
				} else {
					cursor.expectChar(';');
				}
				
				cp = new ConnectionPoint(name, ConnectionPointType.OUT, module);
				module.addConnectionPoint(name, cp);
				break;
			case "bidirectional":
				throw new CompilerException("bidirectional connections are not supported");
			case "internal":
				cursor.expectChar(' ');
				
				String internalType = cursor.readUntilNotAlphanumeretic();
				cursor.expectChar(' ');
				name = cursor.readUntilNotAlphanumeretic();
				
				module.addInternalValue(name, internalType);
				
				cursor.expectChar(';');
				break;
			case "model":
				Model model = parseModel(module, cursor, moduleName);
				module.addModel(model.getName(), model);
				break;
			case "operator":
				cursor.skipSpacesAndNewlines();		

				String op = cursor.readUntil(' ');
				cursor.readChar();
				String leftSide = cursor.readUntilNotAlphanumeretic();
				cursor.skipSpacesAndNewlines();
				cursor.expectChar(',');
				cursor.skipSpacesAndNewlines();
				String rightSide = cursor.readUntilNotAlphanumeretic();
				cursor.skipSpacesAndNewlines();
				cursor.expectChar(':');
				cursor.skipSpacesAndNewlines();
				String result = cursor.readUntilNotAlphanumeretic();
				cursor.expectChar(';');
				
				Logger.logVerbose(module, "Registered operator " + leftSide + " " + op + " " + rightSide + " -> " + result);
				
				OperatorModule opModule = new OperatorModule(op, module, leftSide, rightSide, result, CompilerState.state.getCurrentFile(), CompilerState.state.getLineNumber());
				module.operators.add(opModule);
				CompilerState.state.RegisterOperator(op, opModule);
				
				
				break;
			default:
				//It's an operation
				leftSide = word+cursor.readUntil('=');
				cursor.readChar();
				cursor.skipSpacesAndNewlines();
				rightSide = cursor.readUntil(';');
				cursor.readChar();
				Logger.logVerbose(module, "Connecting " + leftSide + " to " + rightSide);
				ModuleExpression exp = new ModuleExpression(leftSide, rightSide, CompilerState.state.getLineNumber(), CompilerState.state.getCurrentFile());
				module.addExpression(exp);
			}
			cursor.skipSpacesAndNewlines();
		}
		cursor.readChar();
		Logger.logVerbose(module, "Done parsing module " + module.getName());
		return module;
	}
	
	private static Model parseModel(Module module, StringCursor cursor, String moduleName) {
		cursor.expectChar(' ');
		
		//Read size
		String size = cursor.readUntilNotAlphanumeretic();
		if(!size.contains("x")) {
			throw new CompilerException("Malformed model dimension: " + size);
		}
		String[] dimensions = size.split("x");
		if(dimensions.length != 2) {
			throw new CompilerException("Model size expects 2 dimensions, not " + dimensions.length);
		}
		int xSize;
		int zSize;
		
		try {
			xSize = Integer.parseInt(dimensions[0]);
			zSize = Integer.parseInt(dimensions[1]);
		} catch(Exception e) {
			throw new CompilerException("Invalid model size parameter");
		}
		
		cursor.expectChar(' ');
		
		//Read name
		String modelName = cursor.readUntilNotAlphanumeretic();
		
		if(module.containsModel(modelName)) {
			throw new CompilerException("Model " + modelName + " is already defined for module " + moduleName);
		}
		
		cursor.expectChar(' ');
		
		//Read flags
		boolean defaultModel = false;
		if(cursor.peekChar() == '!') {
			cursor.readChar();
			String flag = cursor.readUntilNotAlphanumeretic();
			if(flag.equals("default")) {
				defaultModel = true;
			} else {
				throw new CompilerException("Invalid model flag " + flag);
			}
		}
		
		Model model = new Model(modelName, xSize, zSize, defaultModel);
		
		//Read data
		cursor.skipSpacesAndNewlines();
		cursor.expectChar('{');
		cursor.skipSpacesAndNewlines();
		String modelPayload = null;
		while(cursor.peekChar() != '}') {
			if(cursor.peekChar() == '[') {
				if(modelPayload != null) {
					throw new CompilerException("Model " + modelName + " already has a payload (module " + moduleName + ")");
				}
				cursor.readChar();
				modelPayload = cursor.readUntil(']');
				Logger.logVerbose(module, "Model payload for model " + modelName + ": " + modelPayload);
				
				String[] floors = modelPayload.split(",");
				
				//Remove string markers
				for(int i = 0; i < floors.length; i++) {
					floors[i] = floors[i].substring(floors[i].indexOf("\"")+1, floors[i].lastIndexOf("\""));
				}
				
				model.compilePayload(floors);
				cursor.readChar();
			} else if(cursor.peekChar() == ',') {
				cursor.readChar();
				cursor.skipSpacesAndNewlines();
				continue;
			} else {
				String pointName = cursor.readUntilNotAlphanumeretic();
				
				cursor.skipSpacesAndNewlines();
				cursor.expectChar('=');
				cursor.skipSpacesAndNewlines();
				
				cursor.expectChar('[');
				String location = cursor.readUntil(']');
				if(location.split(",").length != 3) {
					throw new CompilerException("Invalid format on point assignment vector");
				}
				String[] axes = location.split(",");
				
				IntegerVector3 assignmentLocation = null;
				try {
					assignmentLocation = new IntegerVector3(Integer.parseInt(axes[0].trim()), Integer.parseInt(axes[1].trim()), Integer.parseInt(axes[2].trim()));
				} catch(Exception e) {
					throw new CompilerException("Invalid format on point assignment vector");
				}
				cursor.expectChar(']');
				model.addAssignment(pointName, assignmentLocation);
			}
			cursor.skipSpacesAndNewlines();
		}
		cursor.readChar();
		Logger.logVerbose(module, "Done parsing model " + modelName);
		return model;
	}
}
