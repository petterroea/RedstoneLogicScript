package com.petterroea.redstonelogicscript.minecraft;

import com.petterroea.redstonelogicscript.compiler.elements.Module;
import com.petterroea.redstonelogicscript.utils.Vector3;

public class ModuleContainer extends BlockContainer {
	
	private Vector3 position = new Vector3(0,0,0);
	private Module module;
	
	public ModuleContainer(Module module) {
		this.module = module;
	}

	public Vector3 getPosition() {
		return position;
	}

	public void setPosition(Vector3 position) {
		this.position = position;
	}
	
	public Module getModule() {
		return module;
	}

	public void generateStructures() {
		//This module is a model, so we hit the bottom of the tree
		if(module.hasModels()) {
			module.getModel("default");
		}
	}
}
