package dk.brics.soot;
import soot.*;
import soot.options.Options;
import soot.toolkits.graph.*;
import soot.toolkits.scalar.*;

import java.io.File;
import java.util.List;

public class RunLiveAnalysis
{
	public static void main(String[] args) {
		args = new String[] {"testers.LiveVarsClass"};
		
		if (args.length == 0) {
			System.out.println("Usage: java RunLiveAnalysis class_to_analyse");
			System.exit(0);
		}

		String sep = File.separator;
		String pathSep = File.pathSeparator;
		String path = System.getProperty("java.home") + sep + "lib" + sep
				+ "rt.jar";
		path += pathSep + "." + sep + "tutorial" + sep + "guide" + sep
				+ "examples" + sep + "analysis_framework" + sep + "src";
		Options.v().set_soot_classpath(path);

		SootClass sClass = Scene.v().loadClassAndSupport(args[0]);
		sClass.setApplicationClass();
		Scene.v().loadNecessaryClasses();
		
		for (SootMethod m : sClass.getMethods()) {
			Body b = m.retrieveActiveBody();
			
			System.out.println("=======================================");			
			System.out.println(m.getName());
			
			UnitGraph graph = new ExceptionalUnitGraph(b);
			SimpleLiveLocals sll = new SimpleLiveLocals(graph);
			
			for (Unit u : graph) {
				List<Local> before = sll.getLiveLocalsBefore(u);
				List<Local> after = sll.getLiveLocalsAfter(u);
				UnitPrinter up = new NormalUnitPrinter(b);
				up.setIndent("");
				
				System.out.println("---------------------------------------");			
				u.toString(up);			
				System.out.println(up.output());
				System.out.print("Live in: {");
				sep = "";
				for (Local l : before) {
					System.out.print(sep);
					System.out.print(l.getName() + ": " + l.getType());
					sep = ", ";
				}
				System.out.println("}");
				System.out.print("Live out: {");
				sep = "";
				for (Local l : after) {
					System.out.print(sep);
					System.out.print(l.getName() + ": " + l.getType());
					sep = ", ";
				}			
				System.out.println("}");			
				System.out.println("---------------------------------------");
			}
			System.out.println("=======================================");
		}
	}
}
