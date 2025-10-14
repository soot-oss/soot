package dk.brics.soot;
import java.io.File;
import java.util.List;

import dk.brics.soot.analyses.SimpleVeryBusyExpressions;
import dk.brics.soot.analyses.VeryBusyExpressions;
import soot.Body;
import soot.NormalUnitPrinter;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.UnitPrinter;
import soot.options.Options;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.jimple.internal.*;

public class RunVeryBusyAnalysis
{
	public static void main(String[] args) {
		args = new String[] {"testers.VeryBusyClass"};
		
		if (args.length == 0) {
			System.out.println("Usage: java RunVeryBusyAnalysis class_to_analyse");
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
			System.out.println(m.toString());
			
			UnitGraph graph = new ExceptionalUnitGraph(b);
			VeryBusyExpressions vbe = new SimpleVeryBusyExpressions(graph);
			
			for (Unit u : graph) {
				List<AbstractBinopExpr> before = vbe.getBusyExpressionsBefore(u);
				List<AbstractBinopExpr> after = vbe.getBusyExpressionsAfter(u);
				UnitPrinter up = new NormalUnitPrinter(b);
				up.setIndent("");
				
				System.out.println("---------------------------------------");			
				u.toString(up);			
				System.out.println(up.output());
				System.out.print("Busy in: {");
				sep = "";
				for (AbstractBinopExpr e : before) {
					System.out.print(sep);
					System.out.print(e.toString());
					sep = ", ";
				}
				System.out.println("}");
				System.out.print("Busy out: {");
				sep = "";
				for (AbstractBinopExpr e : after) {
					System.out.print(sep);
					System.out.print(e.toString());
					sep = ", ";
				}			
				System.out.println("}");			
				System.out.println("---------------------------------------");
			}
			
			System.out.println("=======================================");
		}
	}
}
