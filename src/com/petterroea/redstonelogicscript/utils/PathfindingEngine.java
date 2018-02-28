package com.petterroea.redstonelogicscript.utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

import com.petterroea.redstonelogicscript.compiler.CompilerException;
import com.petterroea.redstonelogicscript.minecraft.BlockProvider;

public class PathfindingEngine {
	
	private int sx, sy, sz;
	private int[][][] pathfindingGrid;
	private BlockProvider world; 
	/*
	 * Allows chance of correct generation to increase by randomizing which path we pick when equal paths are found.
	 * 
	 * If a static direction is preferred every time, we might meet conjested spaces ebtween modules when another path might leave extra room.
	*/
	private Random rand;
	
	public PathfindingEngine(int size, BlockProvider world) {
		this(size, size, size, world);
	}
	
	public PathfindingEngine(int sx, int sy, int sz, BlockProvider world) {
		this.sx = sx;
		this.sy = sy;
		this.sz = sz;
		this.world = world;
		pathfindingGrid = new int[sx*2][sy*2][sz*2];
		rand = new Random();
	}
	
	public void reuse() {
		for(int x = 0; x < sx*2; x++) {
			for(int y = 0; y < sy*2; y++) {
				for(int z = 0; z < sz*2; z++) {
					pathfindingGrid[x][y][z] = 0;
				}
			}
		}
	}
	
	public boolean isWithinBounds(int x, int y, int z) {
		return x < sx/2 && x >= sx/(-2) &&
			   y < sy/2 && y >= sy/(-2) &&
			   z < sz/2 && z >= sz/(-2);
	}
	
	public boolean isWithinBounds(IntegerVector3 vec) {
		return isWithinBounds(vec.getX(), vec.getY(), vec.getZ());
	}
	
	public IntegerVector3[] getPath(IntegerVector3 start, IntegerVector3 end) {
		LinkedList<IntegerVector3> pointQueue = new LinkedList<IntegerVector3>();
		if(!isWithinBounds(start)) {
			throw new CompilerException("Tried to pathfind from a point outside bounds, up the bounds maybe?");
		}
		
		if(!isWithinBounds(end)) {
			throw new CompilerException("Tried to pathfind to a point outside bounds, up the bounds maybe?");
		}
		
		doPoint(pointQueue, start);
		
		//Build map
		while(pointQueue.size() != 0) {
			pointQueue = gridPopulationIteration(pointQueue, end);
			if(pointQueue == null)
				break;
		}
		if(pointQueue != null) {
			//Pathfinding failed, let's break
			return null;
		}
		//backwards-iterate through the grid
		LinkedList<IntegerVector3> path = new LinkedList<IntegerVector3>();
		IntegerVector3 currPoint = end;
		while(currPoint != start) {
			currPoint = getLowestPoint(currPoint);
			
		}
		
		return (IntegerVector3[]) path.toArray();
		
	}
	
	private IntegerVector3 getLowestPoint(IntegerVector3 point) {
		ArrayList<PointCandidate> candidates = new ArrayList<PointCandidate>();
		
		for(IntegerVector3 v : IntegerVector3.DIRECTIONS) {
			IntegerVector3 p = v.add(point);
			if(!isWithinBounds(v))
				continue;
			if(candidates.size() == 0) {
				candidates.add(new PointCandidate(p, getScoreAtPoint(p)));
			} else {
				int score = getScoreAtPoint(p);
				if(score == 0)
					continue;
				if(candidates.get(0).getScore() > score) {
					candidates.clear();
				}
				candidates.add(new PointCandidate(p, score));
			}
		}
		return candidates.get(rand.nextInt(candidates.size())).getPoint();
	}
	
	private int getScoreAtPoint(IntegerVector3 p) {
		IntegerVector3 matrixPos = p.add(new IntegerVector3(sx, sy, sz));
		return pathfindingGrid[matrixPos.getX()][matrixPos.getY()][matrixPos.getZ()];
	}

	/*
	 * Returns null if the end target is reached, a LinkedList of integerVectors to come if not.
	 */
	private LinkedList<IntegerVector3> gridPopulationIteration(LinkedList<IntegerVector3> pointQueue, IntegerVector3 end) {
		LinkedList<IntegerVector3> newList = new LinkedList<IntegerVector3>();
		for(IntegerVector3 currentVector : pointQueue) {
			if(currentVector == end) {
				return null;
			} else {
				doPoint(newList, currentVector);
			}
		}
		return newList;
	}
	
	private void doPoint(LinkedList<IntegerVector3> pointQueue, IntegerVector3 point) {
		//TODO add all neighbour points that fit the rules required by a custom callback i need to implement
	}

}

class PointCandidate {
	private IntegerVector3 point;
	private int score;
	public PointCandidate(IntegerVector3 point, int score) {
		this.point = point;
		this.score = score;
	}
	
	public int getScore() {
		return score;
	}
	
	public IntegerVector3 getPoint() {
		return point;
	}
}
