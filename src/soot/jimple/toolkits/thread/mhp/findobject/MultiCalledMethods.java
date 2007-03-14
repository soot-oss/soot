package soot.jimple.toolkits.thread.mhp.findobject;

import soot.*;
import soot.util.*;
import java.util.*;

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
	
	Set visited = new HashSet();
	Set multiCalledMethods = new HashSet();
	
	MultiCalledMethods(PegCallGraph pcg, Set mcm){
//		System.out.println("==inside MultiCaleedMethods==");
		//checkScc(pcg);
		multiCalledMethods = mcm;	
		propagate(pcg); 
		finder1(pcg);
		finder2(pcg);
//		test();
		
	}
	private void propagate(PegCallGraph pcg){
		/* If a method call inside a loop, this method may be called more than one,
		 * and this is done with MultiRunStatementsFinder.
		 * This information should be propagated through call graph.
		 * This method implements the propagation.
		 */
		Iterator it = multiCalledMethods.iterator();
		Set visited = new ArraySet();
		while (it.hasNext()){
			Object obj = it.next();
			Iterator succIt = pcg.getSuccsOf(obj).iterator();
			while (succIt.hasNext()){
				if (!visited.contains(obj)){
					dfsVisit(obj, pcg);
				}
			}
		}
	}
	private void dfsVisit(Object obj, PegCallGraph pcg){
		if (!multiCalledMethods.contains(obj))  multiCalledMethods.add(obj);
		Iterator succIt = pcg.getSuccsOf(obj).iterator();
		while (succIt.hasNext()){
			if (!visited.contains(obj)){
				dfsVisit(obj, pcg);
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
			Set gray = new HashSet();
			LinkedList queue = new LinkedList();
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
						multiCalledMethods.add(succ);
					}
				}
				queue.remove(root);
			}
			
		}
		
	}
	
	//Find multi called methods relavant to recusive method invocation
	private void finder2(PegCallGraph pcg){
		
		pcg.trim();
		Set first = new HashSet();
		Set second = new HashSet();
		// Visit each node
		Iterator it = pcg.iterator();
		while (it.hasNext()){
			Object s =it.next();
			
			if (!second.contains(s)){
				
				visitNode(s, pcg, first, second);
			}
		}
		
		
	}
	
	private void visitNode(Object node, PegCallGraph pcg, Set first, Set second){
		if (first.contains(node)){
			second.add(node);
			if (!multiCalledMethods.contains(node)){
				multiCalledMethods.add(node);
			}
		}
		else	first.add(node);
		
		Iterator it = pcg.getTrimSuccsOf(node).iterator();
		while (it.hasNext()){
			Object succ = it.next();
			if (!second.contains(succ)){
				visitNode(succ, pcg, first, second);
			}
		}
	}
	
	public Set getMultiCalledMethods(){
		return (Set)multiCalledMethods;
	}
	private void test(){
		System.out.println("===multiCalledMethods===");
		Iterator it = multiCalledMethods.iterator();
		while (it.hasNext()){
			System.out.println(it.next());
		}
		System.out.println("===multiCalledMethods===end==");
	}
	
}
