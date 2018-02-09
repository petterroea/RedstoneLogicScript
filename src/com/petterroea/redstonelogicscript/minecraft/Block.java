package com.petterroea.redstonelogicscript.minecraft;

import com.petterroea.redstonelogicscript.compiler.CompilerException;
import com.petterroea.redstonelogicscript.utils.Vector3;

public class Block implements BlockProvider{
	private Vector3 position;
	private byte metadata;
	
	public Block(Vector3 position, byte metadata) {
		this.position = position;
		this.metadata = metadata;
	}
	
	public int getMinecraftId() {
		throw new RuntimeException("GetMinecraftId not implemented");
	}
	public byte getMetadata() {
		return metadata;
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

	public static Block getByChar(Vector3 position, char currentChar, byte metadata) {
		switch(currentChar) {
		case 'T':
			return new TorchBlock(position, metadata);
		case 'B':
			return new StructureBlock(position, metadata);
		case 'R':
			return new CircuitBlock(position, metadata);
		default:
			throw new CompilerException("Invalid char used in model: " + currentChar);
		}
	}

}
