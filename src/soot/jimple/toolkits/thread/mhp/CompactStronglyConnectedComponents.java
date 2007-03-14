package soot.jimple.toolkits.thread.mhp;


import soot.jimple.*;
import soot.jimple.toolkits.invoke.*;
import soot.jimple.toolkits.thread.mhp.stmt.JPegStmt;
import soot.toolkits.scalar.*;
//import soot.jimple.internal.*;
import soot.toolkits.graph.*;
import soot.util.*;
import soot.tagkit.*;
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

public class CompactStronglyConnectedComponents{
	
	long compactNodes = 0;
	long add = 0;

	public CompactStronglyConnectedComponents(PegGraph pg){
		Chain mainPegChain = pg.getMainPegChain();	
		compactGraph(mainPegChain, pg);	
		compactStartChain(pg);
		//PegToDotFile printer = new PegToDotFile(pg, false, "compact");
		System.err.println("compact SCC nodes: "+compactNodes);
		System.err.println(" number of compacting scc nodes: "+add);
	}
	
	private void compactGraph(Chain chain, PegGraph peg){
		
		Set canNotBeCompacted = peg.getCanNotBeCompacted();
//		testCan(speg.getMainPegChain(), canNotBeCompacted);	
//		SCC scc = new SCC(chain, peg);
		SCC scc = new SCC(chain.iterator(), peg);
		List sccList = scc.getSccList();	
		//testSCC(sccList);
		Iterator sccListIt = sccList.iterator();
		while (sccListIt.hasNext()){
			List s = (List)sccListIt.next();
			if (s.size()>1){
				//printSCC(s);
				if (!checkIfContainsElemsCanNotBeCompacted(s, canNotBeCompacted)){
					add++;
					compact(s, chain, peg);		        
					
				}
			}
			
		}
		//testListSucc(peg);
		
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
	private void testCan(Chain mainPegChain, Set canNotBeCompacted){
		Iterator it = mainPegChain.iterator();
		while (it.hasNext()){
			JPegStmt s = (JPegStmt)it.next();
			if (canNotBeCompacted.contains(s)) System.out.println("**contains "+s);
		}
	}
	private void testListSucc(PegGraph pg){
		Iterator it = pg.iterator();
		while (it.hasNext()){
			Object o = it.next();
			if (o instanceof List){
				System.out.println("find list in unitToSuccs: "+o);
				System.out.println("succs are: "+pg.getSuccsOf(o));
			}
		}
	}
	private boolean checkIfContainsElemsCanNotBeCompacted(List list, 
			Set canNotBeCompacted ){
		Iterator sccIt = list.iterator();
		//	System.out.println("sccList: ");
		while (sccIt.hasNext()){
			JPegStmt node = (JPegStmt)sccIt.next();
			//	    System.out.println("elem of scc:");
			if (canNotBeCompacted.contains(node)){
				//	System.out.println("find a syn method!!");
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
		List newPreds = new ArrayList();
		List newSuccs = new ArrayList();
		
		while (it.hasNext()){
			JPegStmt s = (JPegStmt)it.next();
			//Replace the SCC with a list node.
			{
				Iterator predsIt = peg.getPredsOf(s).iterator();
				while (predsIt.hasNext()){
					Object pred = predsIt.next();
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
					Object succ =succsIt.next();
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
				JPegStmt s = (JPegStmt)it.next();     
				chain.remove(s);
				allNodes.remove(s);
				unitToSuccs.remove(s);
				unitToPreds.remove(s);
			}
			
		}
		//System.out.println("inside compactSCC");
//		testListSucc(peg);
		
		// add for get experimental results
		compactNodes += list.size();
	}
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
			while(it.hasNext()){
				Object obj = it.next();
				if (fs.contains(obj)){
					
					fs.add(list);
					break;
					
					// System.out.println("add list to monitor: "+entry.getKey());
					
				}
				
			}
			
		}
		//System.out.println("=======update monitor==end====");		     
	}
	
	private void testSCC(List list){
		System.out.println("=========test SCC=======");
		Iterator sccListIt = list.iterator();
		while (sccListIt.hasNext()){
			List scc =(List)sccListIt.next();
			Iterator sccit = scc.iterator();
			//	    System.out.println(scc);
			System.out.println("scc list: ");
			while (sccit.hasNext()){
				JPegStmt s  = (JPegStmt)sccit.next();
				
				
				Tag tag = (Tag)((JPegStmt)s).getTags().get(0);
				System.out.println(tag + " " + s );
			}
			
			
			
			
		}
		System.out.println("======end==test SCC=======");
	}
	private void testList(List list){
		Iterator it = list.iterator();
		while (it.hasNext()){
			Object o = it.next();
			if (o instanceof JPegStmt){
				JPegStmt  unit = (JPegStmt)o;
				
				Tag tag = (Tag)unit.getTags().get(0);
				System.out.println(tag+" "+unit);
				
			}
			
			
			else{
				System.out.println("---list---");
				Iterator listIt = ((List)o).iterator();
				while (listIt.hasNext()){
					Object oo = listIt.next();
					if (oo instanceof JPegStmt){
						JPegStmt  unit = (JPegStmt)oo;
						Tag tag = (Tag)unit.getTags().get(0);
						System.out.println(tag+" "+unit);
					}
					else
						System.out.println(oo);
				}
				System.out.println("---list--end-");
			}
		}
	}
	private void printSCC(List list){
		Iterator sccIt = list.iterator();
		System.out.println("scc list with tag:");
		while (sccIt.hasNext()){
			Object o = sccIt.next();
			
			if (o instanceof JPegStmt){
				Tag tag = (Tag)((JPegStmt)o).getTags().get(0);
				System.out.println(tag + " " + o );
			}
		}
	}
}
