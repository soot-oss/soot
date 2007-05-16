
package soot.jimple.toolkits.thread.mhp;

import soot.util.*;
import soot.toolkits.scalar.*;
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

public class CompactSequentNodes{
	long compactNodes = 0;
	long add = 0;
	
	public CompactSequentNodes(PegGraph pg){	
		Chain mainPegChain = pg.getMainPegChain();
		compactGraph(mainPegChain, pg);
		compactStartChain(pg);
//		PegToDotFile printer = new PegToDotFile(pg, false, "sequence");
		System.err.println("compact seq. node: "+compactNodes);
		System.err.println("number of compacting seq. nodes: "+ add);
	}
	
	private void compactGraph(Chain chain, PegGraph peg){
		Set canNotBeCompacted = peg.getCanNotBeCompacted();
		List<List<Object>> list = computeSequentNodes(chain, peg);
//		printSeq(list);
		Iterator<List<Object>> it = list.iterator();	
		while (it.hasNext()){
			List s = it.next();
			
			if (!checkIfContainsElemsCanNotBeCompacted(s, canNotBeCompacted)){
				add++;
				compact(s, chain, peg);
			}
			
		}
	}
	
	private void compactStartChain(PegGraph graph){
		Set maps = graph.getStartToThread().entrySet();
		for(Iterator iter=maps.iterator(); iter.hasNext();){
			Map.Entry entry = (Map.Entry)iter.next();
			List runMethodChainList = (List)entry.getValue();
			Iterator it = runMethodChainList.iterator();
			while (it.hasNext()){
				Chain chain=(Chain)it.next();
				compactGraph(chain, graph);
			}
		}
		
	}   
	private List<List<Object>> computeSequentNodes(Chain chain, PegGraph pg){
		Set<Object> gray = new HashSet<Object>();
		List<List<Object>> sequentNodes = new ArrayList<List<Object>>();
		
		Set canNotBeCompacted = pg.getCanNotBeCompacted();
		TopologicalSorter ts = new TopologicalSorter(chain, pg);
		ListIterator<Object>  it = ts.sorter().listIterator();
		while (it.hasNext()){
			Object node = it.next();
			List<Object> list = new ArrayList<Object>();
			if (!gray.contains(node)){
				visitNode(pg, node, list, canNotBeCompacted, gray);
				if (list.size()>1){	
					gray.addAll(list);			 
					sequentNodes.add(list);
				}
				
				
			}
		}
		return  sequentNodes;
	}
	
	private void visitNode(PegGraph pg, Object node, List<Object> list,
			Set canNotBeCompacted, Set<Object> gray){
		//System.out.println("node is: "+node);
		if (pg.getPredsOf(node).size() ==1 && pg.getSuccsOf(node).size()==1 &&
				!canNotBeCompacted.contains(node) && !gray.contains(node)){
			list.add(node);
			Iterator it = pg.getSuccsOf(node).iterator();
			while (it.hasNext()){
				Object o = it.next();
				visitNode(pg, o, list, canNotBeCompacted, gray);
			}
			
		}
		
		return;
		
	}
	
	
	private boolean checkIfContainsElemsCanNotBeCompacted(List list,
			Set canNotBeCompacted ){
		Iterator sccIt = list.iterator();
		while (sccIt.hasNext()){
			Object  node = sccIt.next();
			
			
			if (canNotBeCompacted.contains(node)){
				//System.out.println("find a syn method!!");
				return true;
			}
		}
		return false;
	}
	
	
	private void compact(List list, Chain chain, PegGraph peg){
		
		Iterator it = list.iterator();
		FlowSet allNodes = peg.getAllNodes();
		HashMap unitToSuccs = peg.getUnitToSuccs();
		HashMap unitToPreds = peg.getUnitToPreds();
		List<Object> newPreds = new ArrayList<Object>();
		List<Object> newSuccs = new ArrayList<Object>();
		
		while (it.hasNext()){
			Object s = it.next();
			{
				Iterator predsIt = peg.getPredsOf(s).iterator();
				while (predsIt.hasNext()){
					Object  pred = predsIt.next();
					List succsOfPred = peg.getSuccsOf(pred);
					succsOfPred.remove(s);
					if (!list.contains(pred)) {
						newPreds.add(pred); 
						succsOfPred.add(list); 
					}
				}
			}
			{
				Iterator succsIt = peg.getSuccsOf(s).iterator();
				while (succsIt.hasNext()){
					Object succ = succsIt.next();
					List predsOfSucc =  peg.getPredsOf(succ);
					predsOfSucc.remove(s);
					if (!list.contains(succ)){
						newSuccs.add(succ); 
						predsOfSucc.add(list);
					}
				}
			}
			
			
		}
		unitToSuccs.put(list, newSuccs);
		//System.out.println("put list"+list+"\n"+ "newSuccs: "+newSuccs);
		unitToPreds.put(list, newPreds);
		allNodes.add(list);
		chain.add(list);
		updateMonitor(peg, list);
		{
			it = list.iterator();
			while (it.hasNext()){
				Object s = it.next();     
				chain.remove(s);
				allNodes.remove(s);
				unitToSuccs.remove(s);
				unitToPreds.remove(s);
			}
			
		}
//		System.out.println("inside compactSCC");
//		testListSucc(peg);
		compactNodes+=list.size();
	}
	
	// The compacted nodes may inside some monitors. We need to update monitor objects.
	
	
	private void updateMonitor(PegGraph pg, List list){
		//System.out.println("=======update monitor===");
		//add list to corresponding monitor objects sets 
		Set maps = pg.getMonitor().entrySet();
		
		//System.out.println("---test list----");
		//testList(list);
		
		for(Iterator iter=maps.iterator(); iter.hasNext();){
			Map.Entry entry = (Map.Entry)iter.next();
			FlowSet fs = (FlowSet)entry.getValue();
			
			Iterator it = list.iterator();
			while(it.hasNext() ){
				Object obj = it.next();
				if (fs.contains(obj)){
					
					fs.add(list);
					//flag = true;
					break;
					// System.out.println("add list to monitor: "+entry.getKey());
					
				}
				
			}
			
		}
		//System.out.println("=======update monitor==end====");		     
	}
}
