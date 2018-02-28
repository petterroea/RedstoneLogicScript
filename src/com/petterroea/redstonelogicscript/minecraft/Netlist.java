package com.petterroea.redstonelogicscript.minecraft;

import java.util.LinkedList;

import com.petterroea.redstonelogicscript.compiler.CompilerException;
import com.petterroea.redstonelogicscript.compiler.elements.ConnectionPoint.ConnectionPointType;
import com.petterroea.redstonelogicscript.utils.IntegerVector3;

public class Netlist {
	private LinkedList<String> points = new LinkedList<String>();
	final int PATHFINDING_SIZE = 100;
	private short[][][] netlistPathfindMap = new short[PATHFINDING_SIZE][PATHFINDING_SIZE][PATHFINDING_SIZE];
	
	private ModuleContainer moduleContainer;
	
	public Netlist(ModuleContainer moduleContainer) {
		this.moduleContainer = moduleContainer;
	}
	
	public void addPoint(String point) {
		points.add(point);
	}
	
	public boolean hasPoint(String point) {
		return points.contains(point);
	}
	
	public String[] getPoints() {
		String[] list = new String[points.size()];
		list = points.toArray(list);
		return list;
	}

	public void buildNet(ModuleContainer moduleContainer) {
		System.out.println("Building netlist...");
		double longestDistance = 0.0;
		String longestp1;
		String longestp2;
		
		LinkedList<String> connectedPoints = new LinkedList<String>();
		
		while(connectedPoints.size() < points.size()) {
			if(points.size() - connectedPoints.size() == 1) {
				//For this point, pathfind 
				return;
			} else {
				for(String s : points) {
					if(connectedPoints.contains(s))
						continue;
					IntegerVector3 vec1 = moduleContainer.connectionPoints.get(s);
					for(String e : points) {
						if(e.equals(s))
							continue;
						if(connectedPoints.contains(e))
							continue;
						IntegerVector3 vec2 = moduleContainer.connectionPoints.get(e);
						double distance = vec2.minus(vec1).getLength();
						if(distance>longestDistance) {
							longestp1 = s;
							longestp2 = e;
							longestDistance = distance;
						}
					}
				}
			}
		}		
	}
	
	public boolean pathfind(ModuleContainer moduleContainer, IntegerVector3 pointA, IntegerVector3 pointB, LinkedList<IntegerVector3> currentNet) {
		LinkedList<IntegerVector3> pointQueue = new LinkedList<IntegerVector3>();
		pointQueue.add(pointA);
		IntegerVector3 solveVector = null;
		
		while(pointQueue.size() != 0 && solveVector == null) {
			for(IntegerVector3 vector : pointQueue) {
				if(vector.equals(pointB)) {
					solveVector = pointB;
					break;
				}
				if(currentNet.contains(vector)) {
					solveVector = vector;
					break;
				}
				doPoint(vector.minus(new IntegerVector3(0,0,0)), pointQueue);
				
				pointQueue.remove(vector);
			}
		}
		return false;
	}
	
	private void clearMatrix() {
		for(int x = 0; x < PATHFINDING_SIZE; x++) {
			for(int y = 0; y < PATHFINDING_SIZE; y++) {
				for(int z = 0; z < PATHFINDING_SIZE; z++) {
					netlistPathfindMap[x][y][z] = 0;
				}
			}
		}
	}

	private void doPoint(IntegerVector3 point, LinkedList<IntegerVector3> pointQueue) {
		/* Requirements to be added:
		 * 
		 * No blocks next to it may provide power
		 * No blocks ajacently above it may provide power
		 * block has to be free
		 * block below has to be free
		 * block below block below has to not provide power
		 * Blocks ajacent, one level down, may not provide power
		 */
		boolean valid = true;
		
		IntegerVector3 matrixAjusted = point.add(new IntegerVector3(50,50,50));
		if(matrixAjusted.getX()<0 || matrixAjusted.getY()<0 || matrixAjusted.getZ()<0 || matrixAjusted.getX()>= PATHFINDING_SIZE || matrixAjusted.getY()>= PATHFINDING_SIZE || matrixAjusted.getZ()>= PATHFINDING_SIZE ) {
			return;
		}
		
		if(netlistPathfindMap[matrixAjusted.getX()][matrixAjusted.getY()][matrixAjusted.getZ()] == -1) {
			//This point is a known bad point, so we can safely ignore it.
			return;
		}
		
		valid = valid && !moduleContainer.doesCollide(point.add(new IntegerVector3(0,0,0)), new IntegerVector3(0,0,0));
		valid = valid && !moduleContainer.doesCollide(point.add(new IntegerVector3(0,-1,0)), new IntegerVector3(0,0,0));
		valid = valid && !moduleContainer.doesCollide(point.add(new IntegerVector3(0,-2,0)), new IntegerVector3(0,0,0));
		
		valid = valid && !moduleContainer.doesCollide(point.add(new IntegerVector3(1,0,0)), new IntegerVector3(0,0,0));
		valid = valid && !moduleContainer.doesCollide(point.add(new IntegerVector3(-1,0,0)), new IntegerVector3(0,0,0));
		valid = valid && !moduleContainer.doesCollide(point.add(new IntegerVector3(0,0,1)), new IntegerVector3(0,0,0));
		valid = valid && !moduleContainer.doesCollide(point.add(new IntegerVector3(0,0,-1)), new IntegerVector3(0,0,0));
		
		valid = valid && !moduleContainer.doesCollide(point.add(new IntegerVector3(1,1,0)), new IntegerVector3(0,0,0));
		valid = valid && !moduleContainer.doesCollide(point.add(new IntegerVector3(-1,1,0)), new IntegerVector3(0,0,0));
		valid = valid && !moduleContainer.doesCollide(point.add(new IntegerVector3(0,1,1)), new IntegerVector3(0,0,0));
		valid = valid && !moduleContainer.doesCollide(point.add(new IntegerVector3(0,1,-1)), new IntegerVector3(0,0,0));
		
		valid = valid && !moduleContainer.doesCollide(point.add(new IntegerVector3(1,-1,0)), new IntegerVector3(0,0,0));
		valid = valid && !moduleContainer.doesCollide(point.add(new IntegerVector3(-1,-1,0)), new IntegerVector3(0,0,0));
		valid = valid && !moduleContainer.doesCollide(point.add(new IntegerVector3(0,-1,1)), new IntegerVector3(0,0,0));
		valid = valid && !moduleContainer.doesCollide(point.add(new IntegerVector3(0,-1,-1)), new IntegerVector3(0,0,0));
		
		if(!valid) {
			netlistPathfindMap[matrixAjusted.getX()][matrixAjusted.getY()][matrixAjusted.getZ()] = -1;
		} else {
			short lowestPoint = Short.MAX_VALUE;
			//lowestPoint = netlistPathfindMap[point.getX()][point.getY()][point.getZ()] < lowestPoint
		}
	}
}
