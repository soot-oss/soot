package dk.brics.soot;
import soot.*;
import soot.options.Options;
import soot.toolkits.graph.*;
import soot.toolkits.scalar.*;

import java.io.File;
import java.util.*;

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
		
		Iterator<SootMethod> methodIt = sClass.getMethods().iterator();
		while (methodIt.hasNext()) {
			SootMethod m = (SootMethod)methodIt.next();
			Body b = m.retrieveActiveBody();
			
			System.out.println("=======================================");			
			System.out.println(m.getName());
			
			UnitGraph graph = new ExceptionalUnitGraph(b);
			SimpleLiveLocals sll = new SimpleLiveLocals(graph);
			
			Iterator<Unit> gIt = graph.iterator();
			while (gIt.hasNext()) {
				Unit u = gIt.next();
				List before = sll.getLiveLocalsBefore(u);
				List after = sll.getLiveLocalsAfter(u);
				UnitPrinter up = new NormalUnitPrinter(b);
				up.setIndent("");
				
				System.out.println("---------------------------------------");			
				u.toString(up);			
				System.out.println(up.output());
				System.out.print("Live in: {");
				sep = "";
				Iterator befIt = before.iterator();
				while (befIt.hasNext()) {
					Local l = (Local)befIt.next();
					System.out.print(sep);
					System.out.print(l.getName() + ": " + l.getType());
					sep = ", ";
				}
				System.out.println("}");
				System.out.print("Live out: {");
				sep = "";
				Iterator aftIt = after.iterator();
				while (aftIt.hasNext()) {
					Local l = (Local)aftIt.next();
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
