package com.petterroea.redstonelogicscript.minecraft;

import com.petterroea.redstonelogicscript.utils.Vector3;

public interface BlockProvider {
	public boolean doesCollide(Vector3 coord);
	public boolean providesPowerTo(Vector3 coord);
}
