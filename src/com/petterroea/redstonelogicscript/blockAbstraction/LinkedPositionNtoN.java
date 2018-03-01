package com.petterroea.redstonelogicscript.blockAbstraction;

import java.util.LinkedList;

import com.petterroea.redstonelogicscript.utils.IntegerVector3;

/**
 * Many to many data structure with multiple entrances or exits
 * @author petterroea
 *
 */
public class LinkedPositionNtoN {
	
	private IntegerVector3 position;
	
	private LinkedList<LinkedPositionNtoN> upstream = new LinkedList<LinkedPositionNtoN>(); //Torwards outputs
	private LinkedList<LinkedPositionNtoN> downstream = new LinkedList<LinkedPositionNtoN>(); //Torwards inputs
	
	private LinkedList<ConnectionPointEntry> upstreamOutputs = new LinkedList<ConnectionPointEntry>(); //Torwards outputs
	private LinkedList<ConnectionPointEntry> downstreamInputs = new LinkedList<ConnectionPointEntry>(); //Torwards inputs
	
	public LinkedPositionNtoN(IntegerVector3 position) {
		this.position = position;
	}
	
	public void addUpstreamPosition(LinkedPositionNtoN upstream) {
		if(this.upstream.contains(upstream))
			return;
		this.upstream.add(upstream);
		upstream.addDownstreamPosition(this);
	}
	
	public void addDownstreamPosition(LinkedPositionNtoN downstream) {
		if(this.downstream.contains(downstream))
			return;
		this.downstream.add(downstream);
		downstream.addUpstreamPosition(this);
	}
	
	public void decoupleUpstreamPosition(LinkedPositionNtoN upstream) {
		if(!this.upstream.contains(upstream))
			return;
		this.upstream.remove(upstream);
		upstream.decoupleDownstreamPosition(this);
	}
	
	public void decoupleDownstreamPosition(LinkedPositionNtoN downstream) {
		if(!this.downstream.contains(downstream))
			return;
		this.downstream.remove(downstream);
		downstream.decoupleUpstreamPosition(this);
	}
	
	public void clearUpstreamOutputs() {
		if(upstreamOutputs.size()==0)
			return;
		upstreamOutputs.clear();
		for(LinkedPositionNtoN up : upstream) {
			up.clearUpstreamOutputs();
		}
	}
	
	public void clearDownstreamInputs() {
		if(downstreamInputs.size()==0)
			return;
		downstreamInputs.clear();
		for(LinkedPositionNtoN down : downstream) {
			down.clearDownstreamInputs();
		}
	}
	
	public void addUpstreamOutput(String outputName, int distance) {
		this.upstreamOutputs.add(new ConnectionPointEntry(outputName, distance));
		for(LinkedPositionNtoN downstreamEntry : downstream) {
			downstreamEntry.addUpstreamOutput(outputName, distance+1);
		}
	}
	
	public void addDownstreamInput(String inputName, int distance) {
		this.downstreamInputs.add(new ConnectionPointEntry(inputName, distance));
		for(LinkedPositionNtoN upstreamEntry : upstream) {
			upstreamEntry.addDownstreamInput(inputName, distance+1);
		}
	}
}

class ConnectionPointEntry {
	private int distance;
	private String name;
	
	public ConnectionPointEntry(String name, int distance) {
		this.name = name;
		this.distance = distance;
	}
	
	public String getName() {
		return name;
	}
	
	public int getDistance() {
		return distance;
	}
}