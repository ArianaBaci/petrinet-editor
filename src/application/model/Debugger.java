package application.model;

import java.util.ArrayList;

public class Debugger {

	private static final ArrayList<String> log = new ArrayList<String>();
	private static final boolean printDebug = true;
	
	public static void log(String str) {
		log.add(str);
		if (printDebug) {
			System.out.println(str);
		}
	}
}
