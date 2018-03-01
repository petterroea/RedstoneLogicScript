package com.petterroea.redstonelogicscript.blockAbstraction;

import java.util.LinkedList;

import com.petterroea.redstonelogicscript.utils.Rectangle;
import com.petterroea.redstonelogicscript.utils.IntegerVector3;

public class BlockContainer implements BlockProvider{
	
	private IntegerVector3 position = new IntegerVector3(0, 0, 0);
	private Rectangle boundingBox = new Rectangle();
	
	protected LinkedList<BlockProvider> blockList = new LinkedList<BlockProvider>(); 
	
	public LinkedList<BlockProvider> getBlockList() {
		return blockList;
	}
	
	public IntegerVector3 getPosition() {
		return position;
	}
	
	public void setPosition(IntegerVector3 position) {
		this.position = position;
	}
	
	public void translate(IntegerVector3 integerVector) {
		this.position = this.position.add(integerVector);
		
	}

	
	public void buildBoundingBox() {
		boundingBox = new Rectangle();
		for(BlockProvider b : blockList) {
			boundingBox.add(b.getBoundingBox());
		}
	}
	
	@Override
	public boolean doesCollide(IntegerVector3 coord, IntegerVector3 transform) {
		for(BlockProvider bp : blockList) {
			if(bp.doesCollide(coord, transform.add(this.position)))
				return true;
		}
		return false;
	}
	
	@Override
	public boolean doesCollide(BlockProvider provider, IntegerVector3 position) {
		for(BlockProvider bp : blockList) {
			if(bp.doesCollide(provider, position.add(this.position)))
				return true;
		}
		return false;
	}

	@Override
	public boolean providesPowerTo(IntegerVector3 coord) {
		for(BlockProvider bp : blockList) {
			if(bp.providesPowerTo(coord))
				return true;
		}
		return false;
	}

	@Override
	public Rectangle getBoundingBox() {
		// TODO Auto-generated method stub
		return boundingBox;
	}

}
