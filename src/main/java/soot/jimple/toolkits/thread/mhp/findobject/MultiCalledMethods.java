package soot.jimple.toolkits.thread.mhp.findobject;

import soot.Scene;
import soot.SootMethod;
import soot.toolkits.graph.CompleteUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.FlowSet;
import soot.util.*;
import java.util.*;

import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.thread.mhp.pegcallgraph.PegCallGraph;
/*
 import soot.tagkit.*;
 import soot.toolkits.scalar.*;
 */
// *** USE AT YOUR OWN RISK ***
// May Happen in Parallel (MHP) analysis by Lin Li.
// This code should be treated as beta-quality code.
// It was written in 2003, but not incorporated into Soot until 2006.
// As such, it may contain incorrect assumptions about the usage
// of certain Soot classes.
// Some portions of this MHP analysis have been quality-checked, and are
// now used by the Transactions toolkit.
//
// -Richard L. Halpert, 2006-11-30

public class MultiCalledMethods{
	
	Set<SootMethod> multiCalledMethods = new HashSet<SootMethod>();
	
	MultiCalledMethods(PegCallGraph pcg, Set<SootMethod> mcm){
		multiCalledMethods = mcm;	
		byMCalledS0(pcg);
		finder1(pcg);
		finder2(pcg);
		propagate(pcg);
	}
	
	private void byMCalledS0(PegCallGraph pcg) {
		Iterator it = pcg.iterator();
		while (it.hasNext()){
			SootMethod sm = (SootMethod)it.next();
			UnitGraph graph = new CompleteUnitGraph(sm.getActiveBody());
			CallGraph callGraph = Scene.v().getCallGraph();
			MultiRunStatementsFinder finder = new MultiRunStatementsFinder(graph, sm, multiCalledMethods, callGraph);
			FlowSet fs = finder.getMultiRunStatements();
		}
		
	}

	private void propagate(PegCallGraph pcg){
		Set<SootMethod> visited = new HashSet();
		List<SootMethod> reachable = new  ArrayList<SootMethod>();
		reachable.addAll(multiCalledMethods);
	    while(reachable.size()>=1)
	    {
	    	SootMethod popped = reachable.remove(0);
	    	if(visited.contains(popped))  continue;
	    	if (!multiCalledMethods.contains(popped)) multiCalledMethods.add(popped);
	    	visited.add(popped);
	    	Iterator succIt = pcg.getSuccsOf(popped).iterator();
		    while (succIt.hasNext()){
		    	Object succ  = succIt.next();
		    	reachable.add((SootMethod)succ);
			
		    }
	    }
	}
	
	//Use breadth first search to find methods are called more than once in call graph
	private void finder1(PegCallGraph pcg){
		Set clinitMethods = pcg.getClinitMethods();    
		Iterator it = pcg.iterator();
		while (it.hasNext()){
			Object head = it.next();
			//breadth first scan
			Set<Object> gray = new HashSet<Object>();
			LinkedList<Object> queue = new LinkedList<Object>();
			queue.add(head);
			
			while (queue.size()>0){
				Object root = queue.getFirst();
				
				Iterator succsIt = pcg.getSuccsOf(root).iterator();
				while (succsIt.hasNext()){
					Object succ = succsIt.next();
					
					if (!gray.contains(succ)){
						gray.add(succ);
						queue.addLast(succ);
					}
					else if(clinitMethods.contains(succ))  continue;
					else{
						multiCalledMethods.add((SootMethod) succ);
					}
				}
				queue.remove(root);
			}
			
		}
		
	}
	
	//Find multi called methods relavant to recusive method invocation
	private void finder2(PegCallGraph pcg){
		
		pcg.trim();
		Set<SootMethod> first = new HashSet<SootMethod>();
		Set<SootMethod> second = new HashSet<SootMethod>();
		// Visit each node
		Iterator it = pcg.iterator();
		while (it.hasNext()){
			SootMethod s = (SootMethod) it.next();
			
			if (!second.contains(s)){
				
				visitNode(s, pcg, first, second);
			}
		}
		
		
	}
	
	private void visitNode(SootMethod node, PegCallGraph pcg, Set<SootMethod> first, Set<SootMethod> second){
		if (first.contains(node)){
			second.add(node);
			if (!multiCalledMethods.contains(node)){
				multiCalledMethods.add(node);
			}
		}
		else	first.add(node);
		
		Iterator it = pcg.getTrimSuccsOf(node).iterator();
		while (it.hasNext()){
			SootMethod succ = (SootMethod) it.next();
			if (!second.contains(succ)){
				visitNode(succ, pcg, first, second);
			}
		}
	}
	
	public Set<SootMethod> getMultiCalledMethods(){
		return multiCalledMethods;
	}
	
}
