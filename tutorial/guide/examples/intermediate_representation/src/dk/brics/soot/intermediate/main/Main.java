package dk.brics.soot.intermediate.main;

import java.io.IOException;

import dk.brics.soot.intermediate.foonalasys.*;

public class Main { 
	
	public static void main(String[] args) throws IOException {
		String program_name = null;
		long time0 = System.currentTimeMillis();
		
		System.out.println("Loading classes...");
		for (int i = 0 ; i < args.length ; i++) {
			String classname = args[i];
			if (classname.endsWith(".class")) {
				classname = classname.substring(0,classname.length()-6).replace('/','.');
			}
			Foonalasys.loadClass(classname);
			if (program_name == null) {
				program_name = classname;
			}
		}
		
		long time1 = System.currentTimeMillis();
		System.out.println("Analyzing...");
		System.out.flush();
		
		Foonalasys fn = new Foonalasys();
		
		
		long time2 = System.currentTimeMillis();
		
		long time3 = System.currentTimeMillis();
		
		System.out.println("Loading time: "+time(time1-time0));
		System.out.println("Analysis time: "+time(time2-time1));
		System.out.println("Extraction time: "+time(time3-time2));
	}
	
	private static String time(long t) {
		return t/1000 + "." + String.valueOf(1000+(t%1000)).substring(1);
	}
	
}
