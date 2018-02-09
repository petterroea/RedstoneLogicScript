package com.petterroea.redstonelogicscript.minecraft;

import com.petterroea.redstonelogicscript.compiler.CompilerException;
import com.petterroea.redstonelogicscript.utils.Vector3;

public class Block implements BlockProvider{
	private Vector3 position;
	
	public Block(Vector3 position) {
		this.position = position;
	}
	
	public int getMinecraftId() {
		throw new RuntimeException("GetMinecraftId not implemented");
	}
	public int getMetadata() {
		throw new RuntimeException("getMetadata not implemented");
	}
	@Override
	public boolean doesCollide(Vector3 coord) {
		return position.getX() == coord.getX() && position.getY() == coord.getY() && position.getZ() == coord.getZ();
	}
	@Override
	public boolean providesPowerTo(Vector3 coord) {
		// TODO Auto-generated method stub
		return false;
	}

	public static Block getByChar(char currentChar) {
		switch(currentChar) {
		case 'T':
			break;
		case 'B':
			break;
		case 'R':
			break;
		default:
			throw new CompilerException("Invalid char used in model: " + currentChar);
		}
		return null;
	}

}
