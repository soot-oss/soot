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

				Iterator edges = cg.targetsOf(s); 
				
				while (edges.hasNext()){
					Edge e = (Edge)edges.next();
					SootMethod m = e.tgt();
					//G.v().out.println("Target Method: "+m.toString());
					s.addTag(new StringTag("CallGraph: Type: "+e.typeToString(e.type())+" Target Method: "+m.toString()));
				}
			
			}

			SootMethod m = b.getMethod();
			Iterator callerEdges = cg.callersOf(m);
			while (callerEdges.hasNext()){
				Edge callEdge = (Edge)callerEdges.next();
				SootMethod methodCaller = callEdge.src();			
				//G.v().out.println("Source Method: "+methodCaller.toString());
				m.addTag(new StringTag("CallGraph: Source Type: "+callEdge.typeToString(callEdge.type())+" Source Method: "+methodCaller.toString()));
			}
		}
		else {
			System.out.println("No CallGraph found in Scene.");
		}
	}

}

