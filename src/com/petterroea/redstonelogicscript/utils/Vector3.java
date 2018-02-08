package com.petterroea.redstonelogicscript.utils;

public class Vector3 {
	private int x, y, z;
	public Vector3(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public Vector3(Vector3 copy) {
		this.x = copy.getX();
		this.y = copy.getY();
		this.z = copy.getZ();
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public int getZ() {
		return z;
	}
	public void setZ(int z) {
		this.z = z;
	}
	
	public Vector3 add(Vector3 rightSide) {
		return new Vector3(
				x+rightSide.getX(),
				y+rightSide.getY(),
				z+rightSide.getZ()
				);
	}
	
	public String toString() {
		return "[" + x + "," + y + "," + z + "]";
	}
}
