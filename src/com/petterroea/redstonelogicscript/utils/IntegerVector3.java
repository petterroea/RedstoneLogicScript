package com.petterroea.redstonelogicscript.utils;

public class IntegerVector3 {
	public static IntegerVector3 NORTH = new IntegerVector3(0, 0, -1);
	public static IntegerVector3 WEST = new IntegerVector3(-1, 0, 0);
	public static IntegerVector3 SOUTH = new IntegerVector3(0, 0, 1);
	public static IntegerVector3 EAST = new IntegerVector3(1, 0, 0);
	
	public static IntegerVector3 UP = new IntegerVector3(0, 1, 0);
	public static IntegerVector3 DOWN = new IntegerVector3(0, -1, 0);
	
	public static IntegerVector3[] COMPASS_DIRECTIONS = new IntegerVector3[] {NORTH, WEST, SOUTH, EAST};
	public static IntegerVector3[] DIRECTIONS = new IntegerVector3[] {NORTH, WEST, SOUTH, EAST, UP, DOWN};
	
	private int x, y, z;
	public IntegerVector3(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public IntegerVector3(IntegerVector3 copy) {
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
	
	public double getLength() {
		return Math.sqrt(x*x+y*y+z*z);
	}
	
	public IntegerVector3 add(IntegerVector3 rightSide) {
		return new IntegerVector3(
				x+rightSide.getX(),
				y+rightSide.getY(),
				z+rightSide.getZ()
				);
	}
	
	public String toString() {
		return "[" + x + "," + y + "," + z + "]";
	}
	public IntegerVector3 divide(int divisor) {
		return new IntegerVector3(x/divisor, y/divisor, z/divisor);
	}
	public IntegerVector3 minus(IntegerVector3 rightSide) {
		return new IntegerVector3(
				x-rightSide.getX(),
				y-rightSide.getY(),
				z-rightSide.getZ()
				);
	}
}
