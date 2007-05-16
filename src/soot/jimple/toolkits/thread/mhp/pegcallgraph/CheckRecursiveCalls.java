package soot.jimple.toolkits.thread.mhp.pegcallgraph;

import soot.jimple.toolkits.thread.mhp.SCC;
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
	List<List> newSccList = null;
	public CheckRecursiveCalls(PegCallGraph pcg, Set<Object> methodNeedExtent){
		Iterator it = pcg.iterator();
		//PegCallGraphToDot pcgtd = new PegCallGraphToDot(pcg, false, "pegcallgraph");
		SCC scc = new SCC(it, pcg);
		List<List<Object>> sccList = scc.getSccList();
		//printSCC(sccList);
		newSccList = updateScc(sccList, pcg);
		
		//System.out.println("after update scc");
		//printSCC(newSccList);
		check(newSccList, methodNeedExtent);
	}
	private List<List> updateScc(List<List<Object>> sccList, PegCallGraph pcg){
		List<List> newList = new ArrayList<List>();    
		Iterator<List<Object>> listIt = sccList.iterator();
		while (listIt.hasNext()){
			List s = listIt.next();
			if (s.size() == 1){
				Object o = s.get(0);
				
				if ((pcg.getSuccsOf(o)).contains(o) || (pcg.getPredsOf(o)).contains(o)){
					//sccList.remove(s);	 
					newList.add(s);
				}
			}
			else
				newList.add(s);
		}
		return 	newList;
	}
	private void check(List<List> sccList, Set<Object> methodNeedExtent){
		Iterator<List> listIt = sccList.iterator(); 	    
		while (listIt.hasNext()){
			List s = listIt.next();
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
}
