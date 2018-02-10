package com.petterroea.redstonelogicscript.minecraft;

import com.petterroea.redstonelogicscript.compiler.CompilerException;
import com.petterroea.redstonelogicscript.utils.Rectangle;
import com.petterroea.redstonelogicscript.utils.IntegerVector3;

public class Block implements BlockProvider{
	private IntegerVector3 position;
	private byte metadata;
	
	public Block(IntegerVector3 position, byte metadata) {
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
	public boolean doesCollide(IntegerVector3 coord, IntegerVector3 transform) {
		return position.getX() == coord.getX()+transform.getX() && position.getY() == coord.getY()+transform.getY() && position.getZ() == coord.getZ()+transform.getZ();
	}
	@Override
	public boolean providesPowerTo(IntegerVector3 coord) {
		// TODO Auto-generated method stub
		return false;
	}

	public static Block getByChar(IntegerVector3 position, char currentChar, byte metadata) {
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

	@Override
	public boolean doesCollide(BlockProvider provider, IntegerVector3 translation) {
		return provider.doesCollide(position.add(translation), new IntegerVector3(0, 0, 0));
	}

	@Override
	public Rectangle getBoundingBox() {
		return new Rectangle(position);
	}

}
