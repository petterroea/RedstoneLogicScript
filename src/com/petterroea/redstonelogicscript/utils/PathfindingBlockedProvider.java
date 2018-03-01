package com.petterroea.redstonelogicscript.utils;

import com.petterroea.redstonelogicscript.blockAbstraction.BlockProvider;

public interface PathfindingBlockedProvider {
	public boolean isValidPoint(IntegerVector3 position, BlockProvider world);
}
