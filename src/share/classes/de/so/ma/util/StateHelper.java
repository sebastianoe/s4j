package de.so.ma.util;

public class StateHelper {
	public static boolean usingJtreg() {
		return System.getProperty("test.src") != null;
	}
}
