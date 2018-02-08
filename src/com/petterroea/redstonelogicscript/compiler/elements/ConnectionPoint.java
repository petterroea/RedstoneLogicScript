package com.petterroea.redstonelogicscript.compiler.elements;

import java.util.LinkedList;

public class ConnectionPoint {
	public enum ConnectionPointType {
		IN,
		OUT,
		BIDIRECTIONAL,
		INTERNAL
	}
	
	private ConnectionPointType type;
	private String name;
	private Module module;
	
	private LinkedList<Connection> connections = new LinkedList<Connection>();
	
	public ConnectionPoint(String name, ConnectionPointType type, Module module) {
		this.type = type;
		this.name = name;
		this.module = module;
	}
	
	public void hook(Connection conn) {
		connections.add(conn);
	}
	
	public void unhook(Connection conn) {
		connections.remove(conn);
	}

	public ConnectionPointType getType() {
		return type;
	}

	public String getName() {
		return name;
	}
	
	public String toString() {
		return type.name() + "ConnectionPoint \"" + name + "\" in module " + module.getName();
	}
}
