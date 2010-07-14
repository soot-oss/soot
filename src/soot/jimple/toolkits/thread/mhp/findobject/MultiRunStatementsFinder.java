package soot.jimple.toolkits.thread.mhp.findobject;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.thread.mhp.TargetMethodsFinder;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.ArraySparseSet;
import soot.toolkits.scalar.FlowSet;
import soot.toolkits.scalar.ForwardFlowAnalysis;

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

public class MultiRunStatementsFinder extends ForwardFlowAnalysis<Unit,BitSet>
{
	Set<Unit> multiRunStatements = new HashSet<Unit>();
	
    protected Map<Object,Integer> nodeToIndex;
    protected int lastIndex = 0;
	
	//add soot method here just for debug
	public MultiRunStatementsFinder(UnitGraph g, SootMethod sm, Set<SootMethod> multiCalledMethods, CallGraph cg)
	{
		super(g);
		
		nodeToIndex = new HashMap<Object, Integer>();
				
		//      System.out.println("===entering MultiObjectAllocSites==");	
		doAnalysis();
		
		//testMultiObjSites(sm);
		findMultiCalledMethodsIntra(multiCalledMethods, cg);
		
		// testMultiObjSites(sm);
	}
	
	private void findMultiCalledMethodsIntra(Set<SootMethod> multiCalledMethods, CallGraph callGraph){
		Iterator<Unit> it = multiRunStatements.iterator();
		while (it.hasNext()){
			Stmt stmt = (Stmt) it.next();
			if (stmt.containsInvokeExpr()){
				
				InvokeExpr invokeExpr =stmt.getInvokeExpr();
				
				List<SootMethod> targetList = new ArrayList<SootMethod>();
				SootMethod method = invokeExpr.getMethod();
				if (invokeExpr instanceof StaticInvokeExpr){
					targetList.add(method);
				}
				else if (invokeExpr instanceof InstanceInvokeExpr) {
					if (method.isConcrete() && !method.getDeclaringClass().isLibraryClass()){
						TargetMethodsFinder tmd = new TargetMethodsFinder();
						targetList = tmd.find(stmt, callGraph, true, true);
					}
				}
				
				if (targetList != null){
					Iterator<SootMethod> iterator = targetList.iterator();
					while (iterator.hasNext()){
						SootMethod obj = iterator.next();
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
	protected void merge(BitSet in1, BitSet in2, BitSet out)
	{
		out.clear();
		out.or(in1);
		out.or(in2);
	}
	
	
//	STEP 5: Define flow equations.
//	in(s) = ( out(s) minus defs(s) ) union uses(s)
//	
	protected void flowThrough(BitSet in, Unit unit,
			BitSet out)
	{
		out.clear();
		out.or(in);
		
		if (!out.get(indexOf(unit))){
			out.set(indexOf(unit));
//			System.out.println("add to out: "+unit);
		}
		else{
			multiRunStatements.add(unit);
		}
		
//		System.out.println("in: "+in);
//		System.out.println("out: "+out);
		
	}
	
	protected void copy(BitSet source, BitSet dest)
	{
		dest.clear();
		dest.or(source);
	}
	
//	STEP 6: Determine value for start/end node, and
//	initial approximation.
//	
//	start node:              empty set
//	initial approximation:   empty set
	protected BitSet entryInitialFlow()
	{
		return new BitSet();
	}
	
	protected BitSet newInitialFlow()
	{
		return new BitSet();
	}
	
	public FlowSet getMultiRunStatements(){
		FlowSet res = new ArraySparseSet();
		for (Unit u : multiRunStatements) {
			res.add(u);
		}
		return res;
	}
	
    protected int indexOf(Object o) {
        Integer index = nodeToIndex.get(o);
        if(index==null) {
            index = lastIndex;
            nodeToIndex.put(o,index);
            lastIndex++;
        }
        return index;
    }
	
}

