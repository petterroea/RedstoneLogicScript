package com.petterroea.redstonelogicscript.utils;

public class Rectangle {
	private int minX=0, sizeX = 0, minY=0, sizeY = 0, minZ = 0, sizeZ = 0;
	
	public Rectangle() {
		
	}
	public Rectangle(IntegerVector3 beginning) {
		add(beginning);
	}
	
	public boolean isEmpty() {
		return sizeX == 0 || sizeY == 0 || sizeZ == 0;
	}
	public boolean contains(IntegerVector3 vec) {
		return !isEmpty() && vec.getX()>=minX && vec.getX() < minX+sizeX && 
							 vec.getY() >= minY && vec.getY() < minY+sizeY && 
							 vec.getZ() >= minZ && vec.getZ() < minZ+sizeZ;
	}
	public boolean contains(Rectangle rect) {
		if (isEmpty())
			return false;
		if( rect.getMaxX() >  getMaxX())
			return false;
		if( rect.getMaxY() >  getMaxY())
			return false;
		if( rect.getMaxZ() >  getMaxZ())
			return false;
		if(rect.getMinX() < minX)
			return false;
		if(rect.getMinY() < minY)
			return false;
		if(rect.getMinZ() < minZ)
			return false;
		return true;
	}
	public boolean add(IntegerVector3 vec) {
		if(contains(vec))
			return false;
		boolean empty = isEmpty();
		if(vec.getX()<minX || empty) {
			sizeX += sizeX==0?1:minX-vec.getX();
			minX = vec.getX();
		}
		if(vec.getY()<minY || empty) {
			sizeY += sizeY==0?1:minY-vec.getY();
			minY = vec.getY();
		}
		if(vec.getZ()<minZ || empty) {
			sizeZ += sizeZ==0?1:minZ-vec.getZ();
			minZ = vec.getZ();
		}
		if(vec.getX()>getMaxX())
			sizeX = vec.getX()-minX+1;
		if(vec.getY()>getMaxY())
			sizeY = vec.getY()-minY+1;
		if(vec.getZ()>getMaxZ())
			sizeZ = vec.getZ()-minZ+1;
		return true;
	}
	
	public boolean add(Rectangle rect) {
		if(contains(rect) || rect.isEmpty()) 
			return false;
		
		boolean empty = isEmpty();
		if(rect.getMinX()<minX||empty) {
			sizeX += sizeX==0?1:minX-rect.getMinX();
			minX = rect.getMinX();
		}
		if(rect.getMinY()<minY||empty) {
			sizeY += sizeY==0?1:minY-rect.getMinY();
			minY = rect.getMinY();
		}
		if(rect.getMinZ()<minZ||empty) {
			sizeZ += sizeZ==0?1:minZ-rect.getMinZ();
			minZ = rect.getMinZ();
		}
		
		if(rect.getMaxX()>getMaxX())
			sizeX += rect.getMaxX()-getMaxX();
		if(rect.getMaxY()>getMaxY())
			sizeY += rect.getMaxY()-getMaxY();
		if(rect.getMaxZ()>getMaxZ())
			sizeZ += rect.getMaxZ()-getMaxZ();
		return true;
	}
	
	public Rectangle union(Rectangle rect) {
		Rectangle r = new Rectangle();
		r.add(this);
		r.add(rect);
		return r;
	}
	
	public int volume() {
		return sizeX*sizeY*sizeZ;
	}
	
	public int getMinX() {
		return minX;
	}
	public int getMaxX() {
		return sizeX==0?0:minX+sizeX-1;
	}
	
	public int getMinY() {
		return minY;
	}
	public int getMaxY() {
		return sizeY==0?0:minY+sizeY-1;
	}
	
	public int getMinZ() {
		return minZ;
	}
	public int getMaxZ() {
		return sizeZ==0?0:minZ+sizeZ-1;
	}
	
	public int getSizeX() {
		return sizeX;
	}
	public int getSizeY() {
		return sizeY;
	}
	public int getSizeZ() {
		return sizeZ;
	}
	
	public void translate(IntegerVector3 vec) {
		this.minX += vec.getX();
		this.minY += vec.getY();
		this.minZ += vec.getZ();
	}
	
	public String toString() {
		return "min: [" + minX + "," + minY + "," + minZ + "]" + " size: [" + sizeX + "," + sizeY + "," + sizeZ + "]" + " max: [" + getMaxX() + "," + getMaxY() + "," + getMaxZ() + "]";
	}
}
