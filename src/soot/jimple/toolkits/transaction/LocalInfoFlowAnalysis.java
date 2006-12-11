package soot.jimple.toolkits.transaction;

import soot.*;
import soot.util.*;
import java.util.*;
import soot.toolkits.mhp.*;
import soot.toolkits.graph.*;
import soot.toolkits.scalar.*;
import soot.jimple.toolkits.callgraph.*;
import soot.tagkit.*;
import soot.jimple.internal.*;
import soot.jimple.*;
import soot.jimple.spark.sets.*;
import soot.jimple.spark.pag.*;
import soot.toolkits.scalar.*;

// LocalInfoFlowAnalysis written by Richard L. Halpert, 2006-12-04

public class LocalInfoFlowAnalysis extends ForwardFlowAnalysis
{
	List useStmts;
	List redefStmts;
	Stmt useStmt;
	Local useLocal;
	
	public LocalInfoFlowAnalysis(UnitGraph g, SootMethod sm)
	{
		super(g);
		
		useStmts = null;
		useStmt = null;
		useLocal = null;

		// Do not do analysis until it is requested
	}
	
	public boolean mustPointToSameObj/*IfRun*/(Stmt firstStmt, Local firstLocal, Stmt secondStmt, Local secondLocal)
	{
		if(! ((Value) firstLocal).equivTo( (Value) secondLocal) )
			return false;
		
		if(useStmt != firstStmt || useLocal != firstLocal)
		{// If analysis has not been run for this unit/local combo, then run it
			useStmts = new ArrayList();
			useStmts.add(firstStmt);
			useStmts.add(secondStmt);
			redefStmts = new ArrayList();
			useStmt = firstStmt;	
			useLocal = firstLocal;
			doAnalysis();
		}

//		G.v().out.println("FLOW_BEFORE secondStmt (" + secondStmt + ") IS:\n" + (FlowSet) getFlowBefore((Unit) secondStmt));

		// If any redefinition reaches the second statement, return false
		FlowSet fs = (FlowSet) getFlowBefore((Unit) secondStmt);
		Iterator redefIt = redefStmts.iterator();
		while(redefIt.hasNext())
		{
			if(fs.contains((Stmt) redefIt.next()))
			{
				return false;
			}
		}
		return true;
	}
	
	public boolean mustPointToSameObj/*IfRun*/(List useStmts, Local useLocal)
	{
		this.useStmts = useStmts;
		this.redefStmts = new ArrayList();
		this.useStmt = null;
		this.useLocal = useLocal;
		doAnalysis();

		// If any redefinition reaches any use statement, return false
		Iterator useIt = useStmts.iterator();
		while(useIt.hasNext())
		{
			FlowSet fs = (FlowSet) getFlowBefore((Unit) useIt.next());
			Iterator redefIt = redefStmts.iterator();
			while(redefIt.hasNext())
			{
				if(fs.contains((Stmt) redefIt.next()))
				{
					return false;
				}
			}
		}
		return true;
	}

	protected void merge(Object in1, Object in2, Object out)
	{
		FlowSet inSet1 = (FlowSet) in1;
		FlowSet inSet2 = (FlowSet) in2;
		FlowSet outSet = (FlowSet) out;
		
		
		inSet1.union(inSet2, outSet);
	}
	
	protected void flowThrough(Object inValue, Object unit,
			Object outValue)
	{
		FlowSet in  = (FlowSet) inValue;
		FlowSet out = (FlowSet) outValue;
		Stmt stmt = (Stmt) unit;
		
		in.copy(out);

		// get list of definitions at this unit
		List newDefs = new ArrayList();
		if(stmt instanceof DefinitionStmt)
		{
			Value leftOp = ((DefinitionStmt)stmt).getLeftOp();
			if(leftOp instanceof Local)
				newDefs.add((Local) leftOp);
		}
		
		// remove useStmts if useLocal has been redefined
		if(newDefs.contains(useLocal))
		{
			if(out.size() > 0)
			{
				redefStmts.add(stmt); // mark this as an active redef stmt
//				out.clear(); // kill uses (and other redef statements... only one need exist)
			}
		}

		if( redefStmts.contains(stmt) )
		{
			out.add(stmt); // add this redef statement to flow set
		}
		
		if( useStmts.contains(stmt) )
		{
			// if flow set contains no DEF statements, then add this USE to flow set
//			boolean containsRedef = false;
//			Iterator redefIt = redefStmts.iterator();
//			while(redefIt.hasNext())
//			{
//				if(out.contains((Stmt) redefIt.next()))
//				{
//					containsRedef = true;
//					break;
//				}
//			}
//			if(!containsRedef)
				out.add(stmt);
		}
	}
	
	protected void copy(Object source, Object dest)
	{
		
		FlowSet sourceSet = (FlowSet) source;
		FlowSet destSet   = (FlowSet) dest;
		
		sourceSet.copy(destSet);
		
	}
	
	protected Object entryInitialFlow()
	{
		return new ArraySparseSet();
	}
	
	protected Object newInitialFlow()
	{
		return new ArraySparseSet();
	}	
}

