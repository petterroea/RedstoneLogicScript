package com.petterroea.redstonelogicscript.minecraft;

import java.util.HashMap;

import com.petterroea.redstonelogicscript.compiler.CompilerException;
import com.petterroea.redstonelogicscript.compiler.CompilerSettings;
import com.petterroea.redstonelogicscript.utils.Vector3;

public class Model extends BlockContainer{
	
	private String name;
	private int xSize, zSize;
	private HashMap<String, Vector3> assignments = new HashMap<String, Vector3>();
	
	public Model(String name, int xSize, int zSize) {
		this.name = name;
		this.xSize = xSize;
		this.zSize = zSize;
	}
	
	public String getName() {
		return name;
	}
	
	public void compilePayload(String[] payload) {
		if(CompilerSettings.settingsSingleton.getVerboseFlag())
			System.out.println("Compiling model payload for model " + name);
		for(String floor : payload) {
			if(floor.length() != xSize*zSize) {
				throw new CompilerException("Invalid model data size in " + name + ": " + floor.length());
			}
		}

		for(int y = 0; y < payload.length; y++) {
			String floor = payload[y];
			for(int x = 0; x < xSize; x++) {
				for(int z = 0; z < zSize; z++) {
					char currentChar = floor.charAt(x+z*xSize);
					if(currentChar != ' ') {
						contents.add(Block.getByChar(new Vector3(x, y, z), currentChar, (byte)0)); //TODO metadata
					}
				}
			}
		}
	}
	public void addAssignment(String pointName, Vector3 assignmentLocation) {
		if(CompilerSettings.settingsSingleton.getVerboseFlag())
			System.out.println("Assignment for model " + name + ": " + pointName + " " + assignmentLocation.toString());
		if(assignments.containsKey(pointName))
			throw new CompilerException("Invalid model I/O assignment: Point " + pointName + " is already defined");
		assignments.put(pointName, assignmentLocation);
	}

}
