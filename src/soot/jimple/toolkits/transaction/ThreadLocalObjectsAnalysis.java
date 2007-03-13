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
	List threads;
	
	Map valueCache;
	Map fieldCache;
	Map invokeCache;
	
	public ThreadLocalObjectsAnalysis(DataFlowAnalysis dfa, List threads) // must include main class
	{
		super(dfa);
		this.threads = threads;

		valueCache = new HashMap();
		fieldCache = new HashMap();
		invokeCache = new HashMap();
	}
	
	// Determines if a RefType Local or a FieldRef is Thread-Local
	public boolean isObjectThreadLocal(Value localOrRef, SootMethod sm)
	{
		Pair cacheKey = new Pair(new EquivalentValue(localOrRef), sm);
		if(valueCache.containsKey(cacheKey))
		{
			return ((Boolean) valueCache.get(cacheKey)).booleanValue();
		}
			
		G.v().out.print("- " + localOrRef + " in " + sm + " is ");
		Iterator threadsIt = threads.iterator();
		while(threadsIt.hasNext())
		{
			AbstractRuntimeThread thread = (AbstractRuntimeThread) threadsIt.next();
			Iterator runMethodsIt = thread.getRunMethods().iterator();
			while(runMethodsIt.hasNext())
			{
				SootMethod runMethod = (SootMethod) runMethodsIt.next();
				if( runMethod.getDeclaringClass().isApplicationClass() &&
					!isObjectLocalToContext(localOrRef, sm, runMethod))
				{
					G.v().out.println("THREAD-SHARED (simpledfa " + ClassDataFlowAnalysis.methodCount + 
													" smartdfa " + SmartMethodDataFlowAnalysis.counter + 
													" smartloa " + SmartMethodLocalObjectsAnalysis.counter + ")");
					valueCache.put(cacheKey, Boolean.FALSE);
					return false;
				}
			}
		}
		G.v().out.println("THREAD-LOCAL (simpledfa " + ClassDataFlowAnalysis.methodCount + 
						 " smartdfa " + SmartMethodDataFlowAnalysis.counter + 
						 " smartloa " + SmartMethodLocalObjectsAnalysis.counter + ")");// (" + localOrRef + " in " + sm + ")");
		valueCache.put(cacheKey, Boolean.TRUE);
		return true;
	}

/*	
	public boolean isFieldThreadLocal(SootField sf, SootMethod sm) // this is kind of meaningless..., if we're looking in a particular method, we'd use isObjectThreadLocal
	{
		G.v().out.println("- Checking if " + sf + " in " + sm + " is thread-local");
		Iterator threadClassesIt = threadClasses.iterator();
		while(threadClassesIt.hasNext())
		{
			SootClass threadClass = (SootClass) threadClassesIt.next();
			if(!isFieldLocalToContext(sf, sm, threadClass))
			{
				G.v().out.println("  THREAD-SHARED");
				return false;
			}
		}
		G.v().out.println("  THREAD-LOCAL");// (" + sf + " in " + sm + ")");
		return true;
	}
*/
	
	public boolean hasNonThreadLocalEffects(SootMethod containingMethod, InvokeExpr ie)
	{
		Pair cacheKey = new Pair(new EquivalentValue(ie), containingMethod);
		if(invokeCache.containsKey(cacheKey))
		{
			return ((Boolean) invokeCache.get(cacheKey)).booleanValue();
		}
			
		G.v().out.print("- " + ie + " in " + containingMethod + " has ");
		Iterator threadsIt = threads.iterator();
		while(threadsIt.hasNext())
		{
			AbstractRuntimeThread thread = (AbstractRuntimeThread) threadsIt.next();
			Iterator runMethodsIt = thread.getRunMethods().iterator();
			while(runMethodsIt.hasNext())
			{
				SootMethod runMethod = (SootMethod) runMethodsIt.next();
				if( runMethod.getDeclaringClass().isApplicationClass() &&
					hasNonLocalEffects(containingMethod, ie, runMethod))
				{
					G.v().out.println("THREAD-VISIBLE (simpledfa " + ClassDataFlowAnalysis.methodCount + 
													" smartdfa " + SmartMethodDataFlowAnalysis.counter + 
													" smartloa " + SmartMethodLocalObjectsAnalysis.counter + ")");// (" + ie + " in " + containingMethod + ")");
					invokeCache.put(cacheKey, Boolean.TRUE);
					return true;
				}
			}
		}
		G.v().out.println("THREAD-PRIVATE (simpledfa " + ClassDataFlowAnalysis.methodCount + 
												" smartdfa " + SmartMethodDataFlowAnalysis.counter + 
												" smartloa " + SmartMethodLocalObjectsAnalysis.counter + ")");// (" + ie + " in " + containingMethod + ")");
		invokeCache.put(cacheKey, Boolean.FALSE);
		return false;
	}
}
