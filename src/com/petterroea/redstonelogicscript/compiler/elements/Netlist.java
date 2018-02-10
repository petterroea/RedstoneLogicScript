package com.petterroea.redstonelogicscript.compiler.elements;

import java.util.LinkedList;

import com.petterroea.redstonelogicscript.compiler.CompilerException;
import com.petterroea.redstonelogicscript.compiler.elements.ConnectionPoint.ConnectionPointType;

public class Netlist {
	private LinkedList<String> points = new LinkedList<String>();
	public Netlist() {
		
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
}
