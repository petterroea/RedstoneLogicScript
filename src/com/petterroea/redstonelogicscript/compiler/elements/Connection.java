package com.petterroea.redstonelogicscript.compiler.elements;

import com.petterroea.redstonelogicscript.compiler.CompilerException;
import com.petterroea.redstonelogicscript.compiler.elements.ConnectionPoint.ConnectionPointType;

public class Connection {
	private boolean isDirectional = true;
	private ConnectionPoint a, b;
	public Connection(ConnectionPoint a, ConnectionPoint b) {
		this.a = a;
		this.b = b;
		a.hook(this);
		b.hook(this);
		validateConnection();
	}
	
	private void validateConnection() {
		if(a == b) {
			throw new CompilerException("Point " + a.toString() + " is connected to itself");
		}
		if(a.getType() != ConnectionPointType.BIDIRECTIONAL && a.getType() == b.getType()) {
			throw new CompilerException("Point " + a.toString() + " cannot be connected to " + b.toString());
		}
	}
}
