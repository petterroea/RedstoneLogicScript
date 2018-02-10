package com.petterroea.redstonelogicscript.utils;

public class DoubleVector3 {
	private double x, y, z;
	public DoubleVector3(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public DoubleVector3(DoubleVector3 copy) {
		this.x = copy.getX();
		this.y = copy.getY();
		this.z = copy.getZ();
	}
	
	public DoubleVector3(IntegerVector3 copy) {
		this.x = copy.getX();
		this.y = copy.getY();
		this.z = copy.getZ();
	}
	
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
	public double getZ() {
		return z;
	}
	public void setZ(double z) {
		this.z = z;
	}
	
	public double getLength() {
		return Math.sqrt(x*x+y*y+z*z);
	}
	
	public DoubleVector3 normalize() {
		return divide(getLength());
	}
	
	public DoubleVector3 add(DoubleVector3 rightSide) {
		return new DoubleVector3(
				x+rightSide.getX(),
				y+rightSide.getY(),
				z+rightSide.getZ()
				);
	}
	
	public String toString() {
		return "[" + x + "," + y + "," + z + "]";
	}
	public DoubleVector3 divide(double divisor) {
		return new DoubleVector3(x/divisor, y/divisor, z/divisor);
	}
	
	public IntegerVector3 toIntegerVector() {
		return new IntegerVector3((int)x, (int)y, (int)z);
	}
	public DoubleVector3 multiply(double factor) {
		return new DoubleVector3(x*factor, y*factor, z*factor);
	}
	public IntegerVector3 roundUpIntegerVector() {
		return new IntegerVector3((int)Math.ceil(x), (int)Math.ceil(y), (int)Math.ceil(z));
	}
}
