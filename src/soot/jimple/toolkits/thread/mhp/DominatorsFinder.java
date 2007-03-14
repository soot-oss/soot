
package soot.jimple.toolkits.thread.mhp;

import soot.toolkits.scalar.*; 
import soot.toolkits.graph.*;
import soot.jimple.internal.*;
import soot.jimple.toolkits.thread.mhp.stmt.JPegStmt;
import soot.tagkit.*;
import soot.util.*;
import java.util.*;

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

public class DominatorsFinder{
	private Map unitToDominators;
	private DirectedGraph peg;
	
	DominatorsFinder(Chain chain, DirectedGraph pegGraph){
		unitToDominators = new HashMap(); 
		peg = pegGraph;
		find(chain);
		//testUnitToDominators();
	}
	
	private void find(Chain chain) {
		
		boolean change = true;
		
		Iterator chainIt;
		
		FlowSet fullSet = new ArraySparseSet();
		FlowSet temp = new ArraySparseSet();
		
		{
			chainIt = chain.iterator();
			while (chainIt.hasNext()){
				fullSet.add(chainIt.next());
				
			}
		}
		
		List heads = peg.getHeads();
		if (heads.size() != 1){
			System.err.println("The size of heads of peg is not equal to 1!");
			System.exit(1);
		}
		else{
			FlowSet dominators = new ArraySparseSet();
			Object head = heads.get(0);
			dominators.add(head);
			unitToDominators.put(head, dominators);
		}
		{
			chainIt = chain.iterator();
			while (chainIt.hasNext()){
				Object n = chainIt.next();
				if (heads.contains(n)) continue;
				FlowSet domin = new ArraySparseSet();
				fullSet.copy(domin);
				unitToDominators.put(n, domin);
			}
		}
		System.out.println("===finish init unitToDominators===");
		System.err.println("===finish init unitToDominators===");
		
		// testUnitToDominators();
		
		do {
			change = false;
			Iterator it = chain.iterator();
			while(it.hasNext()){
				Object n = it.next();
				if (heads.contains(n)) continue;
				else{
					fullSet.copy(temp);
					
					Iterator predsIt = peg.getPredsOf(n).iterator();
					while (predsIt.hasNext()){
						Object p = predsIt.next();
						FlowSet dom = getDominatorsOf(p); 
						temp.intersection(dom);
					}
					FlowSet d = new ArraySparseSet();
					FlowSet nSet = new ArraySparseSet();
					nSet.add(n);
					nSet.union(temp, d);
					FlowSet dominN = getDominatorsOf(n);
					if (!d.equals(dominN)){
						change = true;
						dominN = d;
					}
				}
			}
		}while(!change);
		
		
	}
	public FlowSet getDominatorsOf(Object s){
		if(!unitToDominators.containsKey(s))
			throw new RuntimeException("Invalid stmt" + s);
		return (FlowSet)unitToDominators.get(s);
		
	}
	
	private void testUnitToDominators(){
		System.out.println("=====test Dominators ");
		Set maps = unitToDominators.entrySet();
		for(Iterator iter=maps.iterator(); iter.hasNext();){
			Map.Entry entry = (Map.Entry)iter.next();
			JPegStmt key = (JPegStmt)entry.getKey();
			Tag tag = (Tag)key.getTags().get(0);
			System.out.println("---key=  "+tag+" "+key);
			FlowSet  value  = (FlowSet)entry.getValue();
			
			System.out.println("---value=  "+value);
			
		}
		System.out.println("=========unitToDominators--ends--------");	
	}
	
}
