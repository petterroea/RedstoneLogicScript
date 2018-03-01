package com.petterroea.redstonelogicscript.utils;

import com.petterroea.redstonelogicscript.compiler.CompilerSettings;
import com.petterroea.redstonelogicscript.compiler.elements.Module;

public class Logger {
	public static void logVerbose(Module module, String message) {
		if(CompilerSettings.settingsSingleton.getVerboseFlag())
			System.out.println("[" + module.getName() + "] " + message);
	}
	public static void log(Module module, String message) {
		System.out.println("[" + module.getName() + "] " + message);
	}
	public static void logVerbose(com.petterroea.redstonelogicscript.compiler.Compiler compiler, String message) {
		if(CompilerSettings.settingsSingleton.getVerboseFlag())
			System.out.println("COMPILER -> " + message);
	}
	public static void log(com.petterroea.redstonelogicscript.compiler.Compiler compiler, String message) {
		System.out.println("COMPILER -> " + message);
	}
}
