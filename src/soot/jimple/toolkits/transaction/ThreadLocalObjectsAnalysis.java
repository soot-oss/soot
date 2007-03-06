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

// ThreadLocalObjectsAnalysis written by Richard L. Halpert, 2007-03-05
// Runs LocalObjectsAnalysis for the special case where we want to know
// if a reference is local to all threads from which it is reached.

public class ThreadLocalObjectsAnalysis extends LocalObjectsAnalysis
{
	List threadClasses;
	
	public ThreadLocalObjectsAnalysis(DataFlowAnalysis dfa, List threadClasses) // must include main class
	{
		super(dfa);
		this.threadClasses = threadClasses;
	}
	
	// Determines if a RefType Local or a FieldRef is Thread-Local
	public boolean isObjectThreadLocal(Value localOrRef, SootMethod sm)
	{
		G.v().out.println("--- Checking if " + localOrRef + " in " + sm + " is thread-local");
		Iterator threadClassesIt = threadClasses.iterator();
		while(threadClassesIt.hasNext())
		{
			SootClass threadClass = (SootClass) threadClassesIt.next();
			if(!isObjectLocalToContext(localOrRef, sm, threadClass))
				return false;
		}
		return true;
	}
	
	public boolean isFieldThreadLocal(SootField sf, SootMethod sm) // this is kind of meaningless..., if we're looking in a particular method, we'd use isObjectThreadLocal
	{
		G.v().out.println("--- Checking if " + sf + " in " + sm + " is thread-local");
		Iterator threadClassesIt = threadClasses.iterator();
		while(threadClassesIt.hasNext())
		{
			SootClass threadClass = (SootClass) threadClassesIt.next();
			if(!isFieldLocalToContext(sf, sm, threadClass))
				return false;
		}
		return true;
	}
	
	public boolean hasNonThreadLocalEffects(SootMethod containingMethod, InvokeExpr ie)
	{
		G.v().out.println("--- Checking if " + ie + " in " + containingMethod + " has non-thread-local effects");
		Iterator threadClassesIt = threadClasses.iterator();
		while(threadClassesIt.hasNext())
		{
			SootClass threadClass = (SootClass) threadClassesIt.next();
			if(hasNonLocalEffects(containingMethod, ie, threadClass))
				return true;
		}
		return false;
	}
}
