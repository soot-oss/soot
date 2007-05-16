
package soot.jimple.toolkits.thread.mhp;

import soot.toolkits.graph.*;
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


public class SCC{
	
	private Set<Object> gray;
//	private int time;
	private final LinkedList<Object> finishedOrder;
	private final List<List<Object>> sccList;
	
	//    public SCC(Chain chain, DirectedGraph g){
	public SCC(Iterator it, DirectedGraph g){
		
		gray = new HashSet<Object>();
		finishedOrder = new LinkedList<Object>();
		sccList	= new ArrayList<List<Object>>();
		
		// Visit each node
		{
			
			while (it.hasNext()){
				Object s =it.next();
				if (!gray.contains(s)){
					
					visitNode(g, s);
				}
			}
			
		}
		
		//Re-color all nodes white
		gray = new HashSet<Object>();
		
		//visit nodes via tranpose edges according decreasing order of finish time of nodes
		
		{
			
			Iterator<Object> revNodeIt = finishedOrder.iterator();
			while (revNodeIt.hasNext()){
				Object s =revNodeIt.next();
				if (!gray.contains(s)){
					
					List<Object> scc = new ArrayList<Object>();
					
					visitRevNode(g, s, scc);
					sccList.add(scc);
				}
				
			}
		}
	}
	
	
	private void visitNode(DirectedGraph g, Object s ){
		//System.out.println("visit "+s);
		gray.add(s);
//		time++; // begin visit time
		Iterator it = g.getSuccsOf(s).iterator();
		//	System.out.println("succs are: "+g.getSuccsOf(s));
		if (g.getSuccsOf(s).size()>0){
			while (it.hasNext()){
				Object succ = it.next();
				if (!gray.contains(succ)){
					
					visitNode(g, succ);
				}
			}
		}
//		time++;//end time
		finishedOrder.addFirst(s);
		//System.out.println("add "+s+ " to finished order ");
		
	}
	
	private void visitRevNode(DirectedGraph g, Object s, List<Object> scc){
		
		scc.add(s);
		gray.add(s);
		
		if (g.getPredsOf(s) != null){
			Iterator predsIt = g.getPredsOf(s).iterator();
			if (g.getPredsOf(s).size()>0){
				while (predsIt.hasNext()){
					Object pred = predsIt.next();
					if (!gray.contains(pred)){
						visitRevNode(g, pred, scc);
						
					}
				}
				
			}
		}
	}
	
	public List<List<Object>> getSccList(){
		return sccList;
	}
	public LinkedList<Object> getFinishedOrder(){
		return finishedOrder;
	}
}
