package com.petterroea.redstonelogicscript.blockAbstraction;

import java.util.LinkedList;

import com.petterroea.redstonelogicscript.compiler.CompilerException;
import com.petterroea.redstonelogicscript.compiler.elements.ConnectionPoint;
import com.petterroea.redstonelogicscript.compiler.elements.ConnectionPoint.ConnectionPointType;
import com.petterroea.redstonelogicscript.utils.IntegerVector3;
import com.petterroea.redstonelogicscript.utils.PathfindingEngine;

public class Netlist {
	private LinkedList<String> points = new LinkedList<String>();
	private PathfindingEngine pathfinder;
	private LinkedList<LinkedPositionNtoN> segments = new LinkedList<LinkedPositionNtoN>();
	
	private ModuleContainer moduleContainer;
	
	public Netlist(ModuleContainer moduleContainer) {
		this.moduleContainer = moduleContainer;
		this.pathfinder = new PathfindingEngine(50, moduleContainer, new RedstonePathfindingProvider());
	}
	
	public void addPoint(String point, ConnectionPoint.ConnectionPointType type) {
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
		System.out.println("Curent building netlist:");
		for(String s : points) {
			System.out.println(s);
		}
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
}
