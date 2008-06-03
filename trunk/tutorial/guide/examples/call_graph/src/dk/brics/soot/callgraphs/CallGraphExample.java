package dk.brics.soot.callgraphs;

import java.util.Iterator;

import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.toolkits.callgraph.CHATransformer;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Targets;
import soot.options.Options;

public class CallGraphExample
{	
	public static void main(String[] args) {
		Options.v().set_whole_program(true);
				
		SootClass mainClass = Scene.v().loadClassAndSupport("testers.CallGraphs");
		mainClass.setApplicationClass();
		Scene.v().setMainClass(mainClass);
		
		SootClass a = Scene.v().loadClassAndSupport("testers.A");
		a.setApplicationClass();
		
		CHATransformer.v().transform();
		
		SootMethod src = mainClass.getMethodByName("doStuff");
		CallGraph cg = Scene.v().getCallGraph();
		
		Iterator targets = new Targets(cg.edgesOutOf(src));
		while (targets.hasNext()) {
			SootMethod tgt = (SootMethod)targets.next();
			System.out.println(src + " may call " + tgt);
		}
	}
}
