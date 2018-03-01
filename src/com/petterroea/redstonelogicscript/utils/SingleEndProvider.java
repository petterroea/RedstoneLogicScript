package com.petterroea.redstonelogicscript.utils;

public class SingleEndProvider implements ValidEndProvider {
	
	private IntegerVector3 end;
	
	public SingleEndProvider(IntegerVector3 end) {
		this.end = end;
	}

	@Override
	public boolean isValidEndPoint(IntegerVector3 point) {
		return point == end;
	}

}
