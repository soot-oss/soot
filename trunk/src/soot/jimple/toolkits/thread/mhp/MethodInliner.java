package soot.jimple.toolkits.thread.mhp;

import soot.jimple.toolkits.thread.mhp.stmt.JPegStmt;
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

public class MethodInliner
{
	
	// private ArrayList inlineSite;
	MethodInliner(){
		//	inlineSite = new ArrayList();
	}
	public static void inline(ArrayList sites){
		Iterator it = sites.iterator();
		while (it.hasNext()){
			ArrayList element = (ArrayList)it.next();
			JPegStmt stmt = (JPegStmt)element.get(0);
			Chain chain = (Chain)element.get(1);
			PegGraph p1 = (PegGraph)element.get(2);
			PegGraph p2 = (PegGraph)element.get(3);
			// testHeads(p2);
			// System.out.println("before inlining: stmt:"+stmt);
			//  System.out.println(p1);
			
			
			inline(stmt, chain, p1, p2);
			// System.out.println("after inlining: stmt:"+stmt);
			
			
			//System.out.println(p1);
		}
		
	}
	private static void inline(JPegStmt invokeStmt,Chain chain, PegGraph container, PegGraph inlinee){
		//System.out.println("==inside inline===");	
//		PegToDotFile printer = new PegToDotFile(inlinee, false, "before_addPeg_inlinee"+invokeStmt.getName());
		if (!container.addPeg(inlinee, chain)) {
			System.out.println("heads >1 stm: "+invokeStmt);
			System.exit(1);
		}
		
//		printer = new PegToDotFile(container, false, "after_addPeg_"+invokeStmt);
		container.buildSuccsForInlining(invokeStmt, chain, inlinee);
		//printer = new PegToDotFile(container, false, "after_bu_succ_"+invokeStmt.getName());
		
		//	System.out.println(container);
		container.buildMaps(inlinee);
		container.buildPreds();
		//	container.testStartToThread();
		
	}
	
	
	
}
