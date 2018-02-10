package com.petterroea.redstonelogicscript.compiler.elements;

import com.petterroea.redstonelogicscript.minecraft.ModuleContainer;
import com.petterroea.redstonelogicscript.utils.IntegerVector3;

public class NetlistPoint {
	private String pointName;
	private ModuleContainer owner;
	
	public NetlistPoint() {
		
	}

	public ModuleContainer getOwner() {
		return owner;
	}

	public String getPointName() {
		return pointName;
	}
	
	public IntegerVector3 getPosition() {
		return owner.getPosition().add(owner.getModule().getPointPosition(pointName));
	}
}
