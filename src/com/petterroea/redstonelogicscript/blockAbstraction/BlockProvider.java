package com.petterroea.redstonelogicscript.blockAbstraction;

import com.petterroea.redstonelogicscript.utils.Rectangle;
import com.petterroea.redstonelogicscript.utils.IntegerVector3;

public interface BlockProvider {
	public boolean doesCollide(IntegerVector3 coord, IntegerVector3 transform);
	public boolean doesCollide(BlockProvider provider, IntegerVector3 position);
	public boolean providesPowerTo(IntegerVector3 coord);
	public Rectangle getBoundingBox();
	
}
