package soot.jimple.toolkits.thread.mhp.findobject;

import soot.*;
import soot.util.*;
import java.util.*;
import soot.toolkits.graph.*;
import soot.toolkits.scalar.*;
import soot.jimple.toolkits.callgraph.*;
import soot.jimple.toolkits.thread.mhp.TargetMethodsFinder;
import soot.tagkit.*;
import soot.jimple.internal.*;
import soot.jimple.*;
import soot.toolkits.scalar.*;

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

public class MultiRunStatementsFinder extends ForwardFlowAnalysis
{
	Set visited = new HashSet();
	FlowSet multiRunStatements = new ArraySparseSet();
	//add soot method here just for debug
	public MultiRunStatementsFinder(UnitGraph g, SootMethod sm, Set multiCalledMethods, CallGraph cg)
	{
		super(g);
		//      System.out.println("===entering MultiObjectAllocSites==");	
		doAnalysis();
		
		//testMultiObjSites(sm);
		findMultiCalledMethodsIntra(multiCalledMethods, cg);
		
		// testMultiObjSites(sm);
	}
	
	private void testMultiRunStatements(SootMethod sm){
		if (multiRunStatements.size()>0){
			System.out.println("==multiRunStatements==for "+sm);
			Iterator it = multiRunStatements.iterator();
			while (it.hasNext()){
				System.out.println(it.next()); 
				
			}
			System.out.println("==multiRunStatements=== end==");
		}
	}
	
	private void findMultiCalledMethodsIntra(Set multiCalledMethods, CallGraph callGraph){
		Iterator it = multiRunStatements.iterator();
		while (it.hasNext()){
			Unit unit = (Unit)it.next();
			if (((Stmt)unit).containsInvokeExpr()){
				
				Value invokeExpr =(Value)((Stmt)unit).getInvokeExpr();
				
				List targetList = new ArrayList();
				SootMethod method =  ((InvokeExpr)invokeExpr).getMethod();
				if (invokeExpr instanceof StaticInvokeExpr){
					
					targetList.add(method);
				}
				else if (invokeExpr instanceof InstanceInvokeExpr) { 
					//System.out.println("unit: "+unit);
					
					if (method.isConcrete() && !method.getDeclaringClass().isLibraryClass()){
						
						TargetMethodsFinder tmd = new TargetMethodsFinder();
						//	targetList = tmd.find(unit, callGraph, false, true);
						targetList = tmd.find(unit, callGraph, true, true); // list could be empty... that's ok
					}
					
					
				}
				if (targetList != null){
					Iterator iterator = targetList.iterator();
					while (iterator.hasNext()){
						SootMethod obj = (SootMethod)iterator.next();
						if (!obj.isNative()){
							multiCalledMethods.add(obj);
						}
					}
				}
				
			}
			
		}
	}
	
//	STEP 4: Is the merge operator union or intersection?
//	UNION
	protected void merge(Object in1, Object in2, Object out)
	{
		FlowSet inSet1 = (FlowSet) in1;
		FlowSet inSet2 = (FlowSet) in2;
		FlowSet outSet = (FlowSet) out;
		
		inSet1.union(inSet2, outSet);
		
	}
	
	
//	STEP 5: Define flow equations.
//	in(s) = ( out(s) minus defs(s) ) union uses(s)
//	
	protected void flowThrough(Object inValue, Object unit,
			Object outValue)
	{
		FlowSet in  = (FlowSet) inValue;
		FlowSet out = (FlowSet) outValue;
		//JPegStmt s = (JPegStmt)unit;
		
		in.copy(out);
		if (!out.contains(unit)){
			out.add(unit);
//			System.out.println("add to out: "+unit);
		}
		else{
			multiRunStatements.add(unit);
		}
		
//		System.out.println("in: "+in);
//		System.out.println("out: "+out);
		
	}
	
	protected void copy(Object source, Object dest)
	{
		
		FlowSet sourceSet = (FlowSet) source;
		FlowSet destSet   = (FlowSet) dest;
		
		sourceSet.copy(destSet);
		
	}
	
//	STEP 6: Determine value for start/end node, and
//	initial approximation.
//	
//	start node:              empty set
//	initial approximation:   empty set
	protected Object entryInitialFlow()
	{
		return new ArraySparseSet();
	}
	
	protected Object newInitialFlow()
	{
		return new ArraySparseSet();
	}
	public FlowSet getMultiRunStatements(){
		return (FlowSet)multiRunStatements;
	}
	
}

