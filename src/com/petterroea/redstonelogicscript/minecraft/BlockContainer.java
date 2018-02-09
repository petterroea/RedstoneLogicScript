package com.petterroea.redstonelogicscript.minecraft;

import java.util.LinkedList;

import com.petterroea.redstonelogicscript.utils.Vector3;

public class BlockContainer implements BlockProvider{
	
	private Vector3 position = new Vector3(0, 0, 0);
	
	protected LinkedList<BlockProvider> contents = new LinkedList<BlockProvider>(); 
	

	@Override
	public boolean doesCollide(Vector3 coord) {
		for(BlockProvider bp : contents) {
			if(bp.doesCollide(coord))
				return true;
		}
		return false;
	}

	@Override
	public boolean providesPowerTo(Vector3 coord) {
		for(BlockProvider bp : contents) {
			if(bp.providesPowerTo(coord))
				return true;
		}
		return false;
	}

}
