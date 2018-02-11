package com.petterroea.redstonelogicscript.minecraft;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

import com.petterroea.redstonelogicscript.compiler.CompilerException;
import com.petterroea.redstonelogicscript.compiler.CompilerSettings;
import com.petterroea.redstonelogicscript.compiler.CompilerState;
import com.petterroea.redstonelogicscript.compiler.elements.Module;
import com.petterroea.redstonelogicscript.compiler.elements.ModuleExpression;
import com.petterroea.redstonelogicscript.compiler.elements.OperatorModule;
import com.petterroea.redstonelogicscript.utils.Rectangle;
import com.petterroea.redstonelogicscript.utils.DoubleVector3;
import com.petterroea.redstonelogicscript.utils.IntegerVector3;
import com.petterroea.util.MiscUtils;

public class ModuleContainer extends BlockContainer {
	//Used for generation
	private LinkedList<Netlist> netlists = new LinkedList<Netlist>();
	private HashMap<String, Netlist> namedNetlists = new HashMap<String, Netlist>();
	
	private HashMap<String, ModuleContainer> internals = new HashMap<String, ModuleContainer>();
	
	private LinkedList<ModuleContainer> operators = new LinkedList<ModuleContainer>();
	
	HashMap<String, IntegerVector3> connectionPoints = new HashMap<String, IntegerVector3>();
	
	Module module;
	
	public ModuleContainer(Module module) {
		this.module = module;
	}

	public Module getModule() {
		return module;
	}

	public void generateStructures(com.petterroea.redstonelogicscript.compiler.Compiler compiler) {
		//This module is a model, so we hit the bottom of the tree
		if(module.hasModels()) {
			Model model = module.getDefaultModel();
			getBlockList().add(model);
			Iterator it = model.getAssignments().entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry pair = (Map.Entry)it.next();
		        
		        this.connectionPoints.put((String)pair.getKey(), (IntegerVector3)pair.getValue());
		    }
		    if(CompilerSettings.settingsSingleton.getVerboseFlag())
		    	System.out.println("Finished adding model module " + this.module.getName() + ", with " + connectionPoints.size() + " connections.");
		} else {
			//Put internal values in their internals and netlists
			for(String name : module.getInternalValues().keySet()) {
				String moduleType = module.getInternalValues().get(name);
				if(!moduleType.equals("point")) {
					if(CompilerSettings.settingsSingleton.getVerboseFlag())
						System.out.println("Creating internal of type " + moduleType);
					
					ModuleContainer container = new ModuleContainer(compiler.getModule(moduleType));
					container.generateStructures(compiler);
					
					internals.put(name, container);
				} else {
					Netlist netlist = new Netlist(this);
					netlists.add(netlist);
					namedNetlists.put(name, netlist);
				}
			}
			
			if(CompilerSettings.settingsSingleton.getVerboseFlag())
				System.out.println("Building netlist for " + module.getName());
			//Build netlist
			for(ModuleExpression exp : module.getExpressions()) {
				String rightSide = exp.getRightSide();
				String leftSide = exp.getLeftSide();
				
				connectPoints(leftSide, rightSide, compiler);
			}
			
			if(CompilerSettings.settingsSingleton.getVerboseFlag())
				System.out.println("Netlist for " + module.getName() + " has " + netlists.size() + " entries.");
			
			LinkedList<ModuleContainer> placedModules = new LinkedList<ModuleContainer>();
			int modulePadding = 5;
			//Loop until everything is placed
			while(true) {
				while(placedModules.size() < internals.size()) {
					//Find the biggest part
					int biggestVolume = 0;
					ModuleContainer currentModule = null;
					String currentModuleName = "";
					
					Iterator it = internals.entrySet().iterator();
				    while (it.hasNext()) {
				        Map.Entry pair = (Map.Entry)it.next();
				        
				        ModuleContainer container = (ModuleContainer) pair.getValue();
				        int volume = container.getBoundingBox().volume();
				        if(!placedModules.contains(container) && volume > biggestVolume) {
				        	currentModule = container;
				        	biggestVolume = volume;
				        	currentModuleName = (String) pair.getKey();
				        }
				        
				        //it.remove(); // avoids a ConcurrentModificationException
				    }
				    //We can't really place any traces if no other modules exist
				    if(placedModules.size()==0) {
				    	getBlockList().add(currentModule);
				    	placedModules.add(currentModule);
				    	
				    	it = currentModule.getConnectionPoints().entrySet().iterator();
					    while (it.hasNext()) {
					        Map.Entry pair = (Map.Entry)it.next();
					        
					        connectionPoints.put(currentModuleName + "." + (String)pair.getKey(), (IntegerVector3)pair.getValue());
					        //it.remove(); // avoids a ConcurrentModificationException
					    }
					    if(CompilerSettings.settingsSingleton.getVerboseFlag())
							System.out.println("Added first module");
				    	continue;
				    }
				    
				    //Now, accumulate points it is connected to which already exist in the world.
				    IntegerVector3 accumulatedPoints = new IntegerVector3(0,0,0);
				    int accumulatedCount = 0;
				    
				    it = currentModule.getConnectionPoints().entrySet().iterator();
				    while (it.hasNext()) {
				        Map.Entry pair = (Map.Entry)it.next();
				        
				        Netlist connectedNet = null;
				        for(Netlist net : netlists) {
				        	if(net.hasPoint(currentModuleName + "." + (String)pair.getKey())) {
				        		connectedNet = net;
				        		break;
				        	}
				        }
				        
				        if(connectedNet == null) {
				        	//This point is not used, so we can safely ignore it.
				        	continue;
				        }
				        
				        for(String s : connectedNet.getPoints()) {
				        	if(connectionPoints.containsKey(s)) {
				        		accumulatedPoints.add(connectionPoints.get(s));
				        		accumulatedCount++;
				        	}
				        }
				        
				        //it.remove(); // avoids a ConcurrentModificationException
				    }
				    if(accumulatedCount != 0) {
				    	accumulatedPoints = accumulatedPoints.divide(accumulatedCount);
				    }
				    
				    //Next, find the average direction
				    IntegerVector3 averageDirection = new IntegerVector3(0,0,0);
				    int directionCount = 0;
				    it = currentModule.getConnectionPoints().entrySet().iterator();
				    while (it.hasNext()) {
				        Map.Entry pair = (Map.Entry)it.next();
				        
				        Netlist connectedNet = null;
				        for(Netlist net : netlists) {
				        	if(net.hasPoint(currentModuleName + "." + (String)pair.getKey())) {
				        		connectedNet = net;
				        		break;
				        	}
				        }
				        
				        if(connectedNet == null) {
				        	//This point is not used, so we can safely ignore it.
				        	continue;
				        }
				        
				        for(String s : connectedNet.getPoints()) {
				        	if(connectionPoints.containsKey(s)) {
				        		averageDirection.add(connectionPoints.get(s).minus(accumulatedPoints));
				        		directionCount++;
				        	}
				        }
				        
				        //it.remove(); // avoids a ConcurrentModificationException
				    }
				    DoubleVector3 direction = new DoubleVector3(averageDirection).normalize();
				    DoubleVector3 position = new DoubleVector3(accumulatedPoints);
				    boolean isRandomDirection = false;
				    if(directionCount == 0) {
				    	direction = new DoubleVector3(random.nextDouble(), 0, random.nextDouble()).normalize();
				    	isRandomDirection = true;
				    }
				   
				    
				    if(CompilerSettings.settingsSingleton.getVerboseFlag())
						System.out.println("Found average placement position for module " + currentModuleName + " in " + module.getName() + ": " + position.toString() + ", direction " + direction.toString());
				    
				    currentModule.setPosition(position.toIntegerVector());
				    //Next, we move as far as we can before colliding
				    if(!isRandomDirection) {
				    	while(!this.doesCollide(currentModule, getPosition())) {
					    	currentModule.translate(direction.toIntegerVector());
					    }
				    	if(CompilerSettings.settingsSingleton.getVerboseFlag())
					    	System.out.println("Finished snuggling with closest module");
				    	
				    	currentModule.translate(direction.multiply(-1*modulePadding).toIntegerVector());
					    
					    if(CompilerSettings.settingsSingleton.getVerboseFlag())
					    	System.out.println("Translated module for current padding level " + modulePadding + ", position " + currentModule.getPosition().toString());
					    
					    while(this.doesCollide(currentModule, getPosition())) {
					    	if(CompilerSettings.settingsSingleton.getVerboseFlag())
						    	System.out.println("The padding translation put us in a place which is occupated, translating further: " + currentModule.getPosition().toString());
					    	IntegerVector3 translationDir = direction.multiply(-1*modulePadding).roundUpIntegerVector();
					    	if(CompilerSettings.settingsSingleton.getVerboseFlag())
					    		System.out.println("Translating in direction " + translationDir.toString());
					    	currentModule.translate(translationDir);
					    }
					    if(CompilerSettings.settingsSingleton.getVerboseFlag())
					    	System.out.println("Finished finding a position");
				    } else {
				    	//The net is new, so we just find a position that is legal
				    	while(this.doesCollide(currentModule, getPosition())) {
				    		currentModule.translate(direction.multiply(modulePadding).toIntegerVector());
				    	}
				    	while(true) {
				    		for(int i = 0; i < modulePadding; i++) {
					    		if(this.doesCollide(currentModule, getPosition())) {
					    			i = 0;
					    		}
					    		currentModule.translate(direction.multiply(modulePadding).toIntegerVector());
					    	}
					    	boolean safe = true;
					    	for(int i = 0; i < modulePadding; i++) {
					    		if(this.doesCollide(currentModule, getPosition())) {
					    			safe = false;
					    			break;
					    		}
					    		currentModule.translate(direction.multiply(modulePadding).toIntegerVector());
					    	}
					    	if(safe) {
					    		currentModule.translate(direction.multiply(modulePadding*-3).toIntegerVector());
					    		break;
					    	}
				    	}
				    }
				    
				    this.blockList.add(currentModule);
				    placedModules.add(currentModule);
				    
				    if(CompilerSettings.settingsSingleton.getVerboseFlag())
				    	System.out.println("Adding module " + currentModuleName + " at position " + currentModule.getPosition().toString());
				    
				    it = currentModule.getConnectionPoints().entrySet().iterator();
				    while (it.hasNext()) {
				        Map.Entry pair = (Map.Entry)it.next();
				        
				        this.connectionPoints.put(currentModuleName+"."+(String)pair.getKey(), (IntegerVector3)pair.getValue());
				    }
				}
				//Cable placing time. If all netlists are built successfully, we are golden.
				for(Netlist currentNetlist : netlists) {
					currentNetlist.buildNet(this);
				}
				
			}
			
			//Disregard the code below
			//Because operatators might add extra internals, we need to iterate expressions looking for operators
			/*
			for(ModuleExpression exp : module.getExpressions()) {
				String rightSide = exp.getRightSide();
				String valueBuilder = "";
				boolean forcedOperator = false;
				for(int i = 0; i < rightSide.length(); i++) {
					char c = rightSide.charAt(i);
					if(c == ' ') {
						if(valueBuilder.length() != 0) {
							forcedOperator = true;
						}
					} else if(forcedOperator || !(Character.isAlphabetic(c) || c == '.')) {
						String operatorName = c+"";
						if(rightSide.charAt(i+1) != ' ' && !Character.isAlphabetic(rightSide.charAt(i+1))) {
							operatorName += rightSide.charAt(++i);
						}
						OperatorModule operator = CompilerState.state.getOperator(operatorName);
						operators.add(new ModuleContainer(operator.getModule()));
						break;
					} else if(Character.isAlphabetic(c) || c == '.') {
						valueBuilder += c;
					}
				}
			}
			*/
		}
		this.buildBoundingBox();
		if(CompilerSettings.settingsSingleton.getVerboseFlag())
			System.out.println("Bounding box for " + module.getName() + " built: " + getBoundingBox().toString());
	}
	
	private HashMap<String, IntegerVector3> getConnectionPoints() {
		// TODO Auto-generated method stub
		return connectionPoints;
	}

	private void connectPoints(String leftSide, String rightSide, com.petterroea.redstonelogicscript.compiler.Compiler compiler) {
		OperatorModule om = getOperator(rightSide);
		
		//Left side internal point check
		if(namedNetlists.containsKey(leftSide)) {
			//Left side is found, finish route to right side and add to the netlist
			if(om == null) {
				namedNetlists.get(leftSide).addPoint(rightSide);
			} else {
				expandOperator(namedNetlists.get(leftSide), rightSide, om, compiler);
			}
		} else {
			boolean foundInNormalNetlist = false;
			//Left side normal netlist check
			for(Netlist net : netlists) {
				//Check the left side first
				if(net.hasPoint(leftSide)) {
					foundInNormalNetlist = true;
					if(om == null) {
						net.addPoint(rightSide);
					} else {
						expandOperator(net, rightSide, om, compiler);
					}
					break;
				} 
			}
			if(!foundInNormalNetlist) {
				//This means that the left side has never been seen in a netlist before. Make a new netlist for it.
				Netlist net = new Netlist(this);
				netlists.add(net);
				net.addPoint(leftSide);
				if(om == null) {
					net.addPoint(rightSide);
				} else {
					expandOperator(net, rightSide, om, compiler);
				}
			}
		}
	}
	
	private void expandOperator(Netlist leftNet, String rightSide, OperatorModule om, com.petterroea.redstonelogicscript.compiler.Compiler compiler) {
		//Since there is an operator, we have to invent our own internal module and add it.
		String randomName = generateRandomName();
		ModuleContainer operatorContainer = new ModuleContainer(om.getModule());
		operatorContainer.generateStructures(compiler);
		internals.put(randomName, operatorContainer);
		leftNet.addPoint(randomName+"."+om.getOutputName());
		
		String[] args = rightSide.split(Pattern.quote(om.getOperator()));
		
		//if(CompilerSettings.settingsSingleton.getVerboseFlag())
			//System.out.println("Creating new internal model " + randomName + "which binds " + leftSide + " to the inputs \"" + args[0].trim() + "\" into " + om.getLeftSideName() + " and \"" + args[1].trim() + "\" into " + om.getRightSideName() + "..");
		
		connectPoints(randomName+"."+om.getLeftSideName(), args[0], compiler);
		connectPoints(randomName+"."+om.getRightSideName(), args[1], compiler);
	}
	
	private Random random = new Random();
	//Generates random names for internal operators, such that the chance of them colliding with user-specified internal modules is almost impossible.
	public String generateRandomName() {
		byte[] data = new byte[16];
		random.nextBytes(data);
		return MiscUtils.getMd5(data);
	}
	
	public OperatorModule getOperator(String sideArgument) {
		String valueBuilder = "";
		boolean forcedOperator = false;
		for(int i = 0; i < sideArgument.length(); i++) {
			char c = sideArgument.charAt(i);
			if(c == ' ') {
				if(valueBuilder.length() != 0) {
					forcedOperator = true;
				}
			} else if(forcedOperator || !(Character.isAlphabetic(c) || c == '.')) {
				String operatorName = c+"";
				if(sideArgument.charAt(i+1) != ' ' && !Character.isAlphabetic(sideArgument.charAt(i+1))) {
					operatorName += sideArgument.charAt(++i);
				}
				OperatorModule operator = CompilerState.state.getOperator(operatorName);
				return operator;
			} else if(Character.isAlphabetic(c) || c == '.') {
				valueBuilder += c;
			}
		}
		return null;
	}
}
