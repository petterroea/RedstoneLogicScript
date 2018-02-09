package com.petterroea.redstonelogicscript.compiler.elements;

import java.util.ArrayList;
import java.util.HashMap;

import com.petterroea.redstonelogicscript.compiler.CompilerException;
import com.petterroea.redstonelogicscript.compiler.CompilerSettings;
import com.petterroea.redstonelogicscript.compiler.CompilerState;
import com.petterroea.redstonelogicscript.compiler.StringCursor;
import com.petterroea.redstonelogicscript.compiler.elements.ConnectionPoint.ConnectionPointType;
import com.petterroea.redstonelogicscript.minecraft.Model;
import com.petterroea.redstonelogicscript.utils.Vector3;

//Some type of object orientation
public class Module {
	private String name;
	
	private HashMap<String, ConnectionPoint> points = new HashMap<String, ConnectionPoint>();
	private HashMap<String, String> internalValues = new HashMap<String, String>();
	private HashMap<String, Model> models = new HashMap<String, Model>();
	private ArrayList<ModuleExpression> expressions = new ArrayList<ModuleExpression>();
	ArrayList<OperatorModule> operators = new ArrayList<OperatorModule>();
	
	public Module(String name) {
		this.name = name;
		if(CompilerSettings.settingsSingleton.getVerboseFlag()) {
			System.out.println("New module " + name);
		}
	}
	
	protected void addConnectionPoint(String name, ConnectionPoint value) {
		if(points.containsKey(name)) {
			throw new CompilerException("The I/O point " + name + " is already defined for module " + this.name);
		}
		if(internalValues.containsKey(name)) {
			throw new CompilerException("The I/O point " + name + " is already defined as an internal value");
		}
		if(CompilerSettings.settingsSingleton.getVerboseFlag()) 
			System.out.println("Added point " + name);
		points.put(name, value);
	}
	
	protected void addInternalValue(String name, String type) {
		if(internalValues.containsKey(name)) {
			throw new CompilerException("The internal value " + name + " is already defined for module " + this.name);
		}
		if(points.containsKey(name)) {
			throw new CompilerException("The internal value " + name + " is already defined as a I/O point");
		}
		if(CompilerSettings.settingsSingleton.getVerboseFlag()) 
			System.out.println("Added internal value " + name);
		internalValues.put(name, type);
	}
	
	public HashMap<String, ConnectionPoint> getPoints() {
		return points;
	}
	
	protected void addExpression(ModuleExpression expression) {
		expressions.add(expression);
	}
	
	public String getName() {
		return name;
	}
	
	public boolean containsModel(String modelName) {
		return models.containsKey(modelName);
	}
	
	public void addModel(String name, Model model) {
		this.models.put(name, model);
	}
	
	public void validateExpressions(com.petterroea.redstonelogicscript.compiler.Compiler compiler) {
		for(OperatorModule m : operators) {
			if(!points.containsKey(m.getLeftSideName()) || points.get(m.getLeftSideName()).getType() != ConnectionPointType.IN) {
				throw new CompilerException("Operator " + m.getOperator() + " has an invalid left side declaration: " + m.getLeftSideName(), m.getFileName(), m.getLineNumber());
			}
			if(!points.containsKey(m.getRightSideName()) || points.get(m.getRightSideName()).getType() != ConnectionPointType.IN) {
				throw new CompilerException("Operator " + m.getOperator() + " has an invalid right side declaration: " + m.getRightSideName(), m.getFileName(), m.getLineNumber());
			}
			
			if(!points.containsKey(m.getOutputName()) || points.get(m.getOutputName()).getType() != ConnectionPointType.OUT) {
				throw new CompilerException("Operator " + m.getOperator() + " has an invalid output declaration: " + m.getOutputName(), m.getFileName(), m.getLineNumber());
			}
		}
		for(ModuleExpression e : expressions) {
			validateExpression(e.getLeftSide(), e, compiler, false);
			validateExpression(e.getRightSide(), e, compiler, true);
			if(e.getLeftSide().equals(e.getRightSide()))
				throw new CompilerException("Circular expression detected", e.getFileName(), e.getLineNumber());
		}
	}
	
	private void validateExpression(String exp, ModuleExpression e, com.petterroea.redstonelogicscript.compiler.Compiler compiler, boolean operatorAllowed) {
		//System.out.println("Validating " + exp);
		String valueBuilder = "";
		OperatorModule operator = null;
		boolean forcedOperator = false;
		for(int i = 0; i < exp.length(); i++) {
			char c = exp.charAt(i);
			if(c == ' ') {
				if(valueBuilder.length() != 0) {
					forcedOperator = true;
					validateValue(valueBuilder, e, compiler, operatorAllowed);
				}
			} else if(forcedOperator || !(Character.isAlphabetic(c) || c == '.')) {
				if(!operatorAllowed)
					throw new CompilerException("Operator not allowed on left side of an expression", e.getFileName(), e.getLineNumber());
				if(operator != null)
					throw new CompilerException("Max 1 operator per expression side", e.getFileName(), e.getLineNumber());
				String operatorName = c+"";
				if(exp.charAt(i+1) != ' ' && !Character.isAlphabetic(exp.charAt(i+1))) {
					operatorName += exp.charAt(++i);
				}
				operator = CompilerState.state.getOperator(operatorName);
				if(operator == null)
					throw new CompilerException("Nonexistent operator " + operatorName, e.getFileName(), e.getLineNumber());
				forcedOperator = false;
				valueBuilder = "";
			} else if(Character.isAlphabetic(c) || c == '.') {
				valueBuilder += c;
			}
		}
		validateValue(valueBuilder, e, compiler, operatorAllowed);
	}
	private void validateValue(String value, ModuleExpression e, com.petterroea.redstonelogicscript.compiler.Compiler compiler, boolean rightSide) {
		//System.out.println("Checking value " + value);
		if(value.contains(".")) {
			String[] parts = value.split("\\.");
			if(parts.length != 2)
				throw new CompilerException("An expression may only touch its own fields, or the fields of its internally dependent modules.", e.getFileName(), e.getLineNumber());
			if(!internalValues.containsKey(parts[0])) 
				throw new CompilerException("The internal field " + parts[0] + " does not exist.", e.getFileName(), e.getLineNumber());
			if(!internalValues.containsKey(parts[0]))
				throw new CompilerException("There is no internal field by the name " + parts[0], e.getFileName(), e.getLineNumber());
			if(internalValues.get(parts[0]).equals("point"))
				throw new CompilerException("Attemptet to access property on internal point", e.getFileName(), e.getLineNumber());
			Module m = compiler.getModule(internalValues.get(parts[0]));
			if(m == null)
				throw new CompilerException("Undefined module type", e.getFileName(), e.getLineNumber());
			if(!m.getPoints().containsKey(parts[1]))
				throw new CompilerException("Module " + parts[0] + " does not contain point " + parts[1], e.getFileName(), e.getLineNumber());
			if(rightSide) {
				if(!(m.getPoints().get(parts[1]).getType() == ConnectionPointType.OUT || m.getPoints().get(parts[1]).getType() == ConnectionPointType.BIDIRECTIONAL) )
					throw new CompilerException("I/O point on right hand side of property expression must be of type OUT or BIDIRECTIONAL:" + parts[1], e.getFileName(), e.getLineNumber());
			} else {
				if(!(m.getPoints().get(parts[1]).getType() == ConnectionPointType.IN || m.getPoints().get(parts[1]).getType() == ConnectionPointType.BIDIRECTIONAL) )
					throw new CompilerException("I/O point on left hand side of property expression must be of type IN or BIDIRECTIONAL:" + parts[1], e.getFileName(), e.getLineNumber());
			}
		} else {
			if(!points.containsKey(value)) {
				if(!internalValues.containsKey(value))
					throw new CompilerException("Undefined point " + value + ".", e.getFileName(), e.getLineNumber());
				if(!internalValues.get(value).equals("point"))
					throw new CompilerException(value + " is not a point, access the child value you wish to bind to", e.getFileName(), e.getLineNumber());
			} else {
				if(rightSide) {
					if( !(points.get(value).getType() == ConnectionPointType.IN) || points.get(value).getType() == ConnectionPointType.BIDIRECTIONAL) 
						throw new CompilerException("I/O point on right side of expression must be of type IN or BIDIRECTIONAL" + value, e.getFileName(), e.getLineNumber());
				} else {
					if( !(points.get(value).getType() == ConnectionPointType.OUT) || points.get(value).getType() == ConnectionPointType.BIDIRECTIONAL)
						throw new CompilerException("I/O point on left side of expression must be of type OUT or BIDIRECTIONAL" + value, e.getFileName(), e.getLineNumber());
				}
			}
		}
	}
}
