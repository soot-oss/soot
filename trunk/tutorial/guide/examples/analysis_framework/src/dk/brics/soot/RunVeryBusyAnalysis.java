package dk.brics.soot;
import java.util.Iterator;
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
		
		SootClass sClass = Scene.v().loadClassAndSupport(args[0]);		
		sClass.setApplicationClass();
		
		Iterator methodIt = sClass.getMethods().iterator();
		while (methodIt.hasNext()) {
			SootMethod m = (SootMethod)methodIt.next();
			Body b = m.retrieveActiveBody();
			
			System.out.println("=======================================");			
			System.out.println(m.toString());
			
			UnitGraph graph = new ExceptionalUnitGraph(b);
			VeryBusyExpressions vbe = new SimpleVeryBusyExpressions(graph);
			
			Iterator gIt = graph.iterator();
			while (gIt.hasNext()) {
				Unit u = (Unit)gIt.next();
				List before = vbe.getBusyExpressionsBefore(u);
				List after = vbe.getBusyExpressionsAfter(u);
				UnitPrinter up = new NormalUnitPrinter(b);
				up.setIndent("");
				
				System.out.println("---------------------------------------");			
				u.toString(up);			
				System.out.println(up.output());
				System.out.print("Busy in: {");
				String sep = "";
				Iterator befIt = before.iterator();
				while (befIt.hasNext()) {
					AbstractBinopExpr e = (AbstractBinopExpr)befIt.next();
					System.out.print(sep);
					System.out.print(e.toString());
					sep = ", ";
				}
				System.out.println("}");
				System.out.print("Busy out: {");
				sep = "";
				Iterator aftIt = after.iterator();
				while (aftIt.hasNext()) {
					AbstractBinopExpr e = (AbstractBinopExpr)aftIt.next();
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
