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

public class LoopFinder{
	private Map chainToLoop = new HashMap();
	
	LoopFinder(PegGraph peg){
		Chain chain = peg.getMainPegChain();
		DfsForBackEdge dfsForBackEdge = new DfsForBackEdge(chain, (DirectedGraph)peg);
		Map backEdges = dfsForBackEdge.getBackEdges();
		LoopBodyFinder lbf = new LoopBodyFinder(backEdges, (DirectedGraph)peg);
		Set loopBody = 	lbf.getLoopBody();
		testLoops(loopBody);
		chainToLoop.put(chain, loopBody);
		
	}
	private void testLoops(Set loopBody){
		System.out.println("====loops===");
		Iterator it = loopBody.iterator();
		while (it.hasNext()){
			Set loop = (Set)it.next();
			Iterator loopIt = loop.iterator();
			System.out.println("---loop---");
			while (loopIt.hasNext()){
				JPegStmt o = (JPegStmt)loopIt.next();
				Tag tag = (Tag)o.getTags().get(0);
				System.out.println(tag+" "+o);
			}
		}
		System.out.println("===end===loops===");
	}
	
	
	
	
}
