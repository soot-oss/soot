package soot.jimple.toolkits.thread.mhp.pegcallgraph;

import soot.jimple.toolkits.thread.mhp.SCC;
import soot.toolkits.graph.*;
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

public class CheckRecursiveCalls{
	List newSccList = null;
	public CheckRecursiveCalls(PegCallGraph pcg, Set methodNeedExtent){
		Iterator it = pcg.iterator();
		//PegCallGraphToDot pcgtd = new PegCallGraphToDot(pcg, false, "pegcallgraph");
		SCC scc = new SCC(it, pcg);
		List sccList = (List)scc.getSccList();
		//printSCC(sccList);
		newSccList = updateScc(sccList, pcg);
		
		//System.out.println("after update scc");
		//printSCC(newSccList);
		check(newSccList, methodNeedExtent);
	}
	private List updateScc(List sccList, PegCallGraph pcg){
		List newList = new ArrayList();    
		Iterator listIt = sccList.iterator();
		while (listIt.hasNext()){
			List s = (List)listIt.next();
			if (s.size() == 1){
				Object o = s.get(0);
				
				if (((List)pcg.getSuccsOf(o)).contains(o) || ((List)pcg.getPredsOf(o)).contains(o)){
					//sccList.remove(s);	 
					newList.add(s);
				}
			}
			else
				newList.add(s);
		}
		return 	(List)newList;
	}
	private void check(List sccList, Set methodNeedExtent){
		Iterator listIt = sccList.iterator(); 	    
		while (listIt.hasNext()){
			List s = (List)listIt.next();
			//printSCC(s);
			if (s.size()>0){
				Iterator it = s.iterator();
				while (it.hasNext()){
					Object o = it.next();
					if (methodNeedExtent.contains(o)){
						//if (((Boolean)methodsNeedingInlining.get(o)).booleanValue() == true){
						System.err.println("Fail to compute MHP because interested method call relate to recursive calls!");
						System.err.println("interested method: " + o);
						System.exit(1);
						// }
					}
				}
			}
		}
	}
	private void printSCC(List list){
		System.out.println("size of scclist: "+list.size());
		Iterator it = list.iterator();
		while (it.hasNext()){
			Object o = it.next();
			
			if (o instanceof List){
				Iterator sccIt = ((List)o).iterator();
				System.out.println("***scc List:*****");
				while (sccIt.hasNext()){
					System.out.println(sccIt.next());
				}
			}
			
			
		}
	}
}
