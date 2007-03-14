package soot.jimple.toolkits.thread.mhp;

import soot.*;
import soot.jimple.spark.*;
import soot.jimple.toolkits.callgraph.*;
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

public class TargetMethodsFinder{
	
	public List find(Unit unit, CallGraph cg, boolean canBeNullList, boolean canBeNative){
		// target method list can be null during build methodsNeedingInlining, otherwise NOT.
		Set clinit = new HashSet(); 
		List target = new ArrayList(); 
		List t = new ArrayList();
		Iterator it = cg.edgesOutOf(unit);
		//System.out.println("***unit is: "+unit);
		while (it.hasNext()){
			Edge edge = (Edge)it.next();   
			SootMethod targetMethod = edge.tgt();
			//System.out.println("kind: "+edge.kind());
			//System.out.println("isExplicit: "+edge.isExplicit());
			//System.out.println("targetmethod: "+targetMethod);
			t.add(targetMethod);
			if (targetMethod.isNative() ){
				if (canBeNative)
					//System.out.println("isNative: "+targetMethod);
					target.add(targetMethod);
				else
					continue;
				
			}
			
			if (edge.kind() == Kind.CLINIT ) {
				clinit.add(targetMethod);
				
//				continue;
			}
			
			//		if (!targetMethod.getName().equals("run") && edge.kind() == 5 ) continue;
			target.add(targetMethod);
		}
		if (target.size()>1){
//			System.out.println("clinit: "+clinit);
			Iterator targetIt = target.iterator();
			while (targetIt.hasNext()){
				SootMethod sm = (SootMethod)targetIt.next();
				if (clinit.contains(sm)){
					targetIt.remove();
					//System.out.println("remove(clinit) : "+sm);
				}
			}
		}
		if (target.size() < 1 && !canBeNullList){
			throw new RuntimeException("No target method for: "+unit);
			
			
		}
		/*		if (t.size() != 1){
		 System.out.println("t.size(): "+t.size());		
		 System.out.println("=====t list for: "+unit);
		 System.out.println(target);
		 }
		 */		
		return (List)target;
	}
}

