package soot.jimple.toolkits.annotation.callgraph;

import soot.*;
import soot.jimple.toolkits.callgraph.*;
import soot.tagkit.*;
import java.util.*;
import soot.jimple.*;

public class CallGraphTagger extends BodyTransformer {

	public CallGraphTagger( Singletons.Global g ) {}
    public static CallGraphTagger v() { return G.v().CallGraphTagger(); }
    
	protected void internalTransform(
			Body b, String phaseName, Map options)
	{
		//G.v().out.println("Running: "+b.getMethod());
		
		if (Scene.v().hasCallGraph()) {
			CallGraph cg = Scene.v().getCallGraph();
		
			Iterator stmtIt = b.getUnits().iterator();

			while (stmtIt.hasNext()){
			
				Stmt s = (Stmt) stmtIt.next();
				Iterator targets = new Targets(cg.targetsOf(s));
				
				while (targets.hasNext()){
					SootMethod m = (SootMethod)targets.next();
					//G.v().out.println("Target Method: "+m.toString());
					s.addTag(new StringTag("CallGraph: Target Method: "+m.toString()));
				}
			
			}

			SootMethod m = b.getMethod();
			Iterator callers = new Sources(cg.callersOf(m));
			while (callers.hasNext()){
				SootMethod methodCaller = (SootMethod)callers.next();			
				//G.v().out.println("Source Method: "+methodCaller.toString());
				m.addTag(new StringTag("CallGraph: Source Method: "+methodCaller.toString()));
			}
		}
		else {
			System.out.println("No CallGraph found in Scene.");
		}
	}

}

