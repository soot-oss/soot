package soot.jimple.toolkits.thread;

import soot.*;

import java.util.*;
import soot.jimple.toolkits.infoflow.*;
import soot.jimple.toolkits.thread.mhp.*;
import soot.jimple.*;

// ThreadLocalObjectsAnalysis written by Richard L. Halpert, 2007-03-05
// Runs LocalObjectsAnalysis for the special case where we want to know
// if a reference is local to all threads from which it is reached.

public class ThreadLocalObjectsAnalysis extends LocalObjectsAnalysis implements IThreadLocalObjectsAnalysis 
{
	MhpTester mhp;
	List<AbstractRuntimeThread> threads;
	InfoFlowAnalysis primitiveDfa;
	static boolean printDebug = false;
	
	Map valueCache;
	Map fieldCache;
	Map invokeCache;
	
	public ThreadLocalObjectsAnalysis(MhpTester mhp) // must include main class
	{
		super(new InfoFlowAnalysis(false, true, printDebug)); // ref-only, with inner fields
		this.mhp = mhp;
		this.threads = mhp.getThreads();
		this.primitiveDfa = new InfoFlowAnalysis(true, true, printDebug); // ref+primitive, with inner fields

		valueCache = new HashMap();
		fieldCache = new HashMap();
		invokeCache = new HashMap();
	}
	
	// Forces the majority of computation to take place immediately, rather than on-demand
	// might occasionally compute more than is necessary
	public void precompute()
	{
		for(AbstractRuntimeThread thread : threads)
		{
			for(Object item : thread.getRunMethods())
			{
				SootMethod runMethod = (SootMethod) item;
				if(runMethod.getDeclaringClass().isApplicationClass())
					getClassLocalObjectsAnalysis(runMethod.getDeclaringClass());
			}
		}
	}

	// override
	protected ClassLocalObjectsAnalysis newClassLocalObjectsAnalysis(LocalObjectsAnalysis loa, InfoFlowAnalysis dfa, UseFinder uf, SootClass sc)
	{
		// find the right run methods to use for threads of type sc
		List<SootMethod> runMethods = new ArrayList<SootMethod>();
		Iterator<AbstractRuntimeThread> threadsIt = threads.iterator();
		while(threadsIt.hasNext())
		{
			AbstractRuntimeThread thread = threadsIt.next();
			Iterator<Object> runMethodsIt = thread.getRunMethods().iterator();
			while(runMethodsIt.hasNext())
			{
				SootMethod runMethod = (SootMethod) runMethodsIt.next();
				if( runMethod.getDeclaringClass() == sc )
					runMethods.add(runMethod);
			}
		}
		
		return new ClassLocalObjectsAnalysis(loa, dfa, primitiveDfa, uf, sc, runMethods);
	}
	
	// Determines if a RefType Local or a FieldRef is Thread-Local
	public boolean isObjectThreadLocal(Value localOrRef, SootMethod sm)
	{
		if(threads.size() <= 1)
			return true;
//		Pair cacheKey = new Pair(new EquivalentValue(localOrRef), sm);
//		if(valueCache.containsKey(cacheKey))
//		{
//			return ((Boolean) valueCache.get(cacheKey)).booleanValue();
//		}
		
		if(printDebug)
			G.v().out.println("- " + localOrRef + " in " + sm + " is...");
		Collection<AbstractRuntimeThread> mhpThreads = mhp.getThreads();
		if (mhpThreads != null)
		    for(AbstractRuntimeThread thread : mhpThreads)
		    {
		        for(Object meth : thread.getRunMethods())
		        {
		            SootMethod runMethod = (SootMethod) meth;
	
					if( runMethod.getDeclaringClass().isApplicationClass() &&
						!isObjectLocalToContext(localOrRef, sm, runMethod))
					{
						if(printDebug)
							G.v().out.println("  THREAD-SHARED (simpledfa " + ClassInfoFlowAnalysis.methodCount + 
															" smartdfa " + SmartMethodInfoFlowAnalysis.counter + 
															" smartloa " + SmartMethodLocalObjectsAnalysis.counter + ")");
	//					valueCache.put(cacheKey, Boolean.FALSE);
	//					escapesThrough(localOrRef, sm);
						return false;
					}
				}
			}
		if(printDebug)
			G.v().out.println("  THREAD-LOCAL (simpledfa " + ClassInfoFlowAnalysis.methodCount + 
							 " smartdfa " + SmartMethodInfoFlowAnalysis.counter + 
							 " smartloa " + SmartMethodLocalObjectsAnalysis.counter + ")");// (" + localOrRef + " in " + sm + ")");
//		valueCache.put(cacheKey, Boolean.TRUE);
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
		if(threads.size() <= 1)
			return true;
		return true;
/*
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
					G.v().out.println("THREAD-VISIBLE (simpledfa " + ClassInfoFlowAnalysis.methodCount + 
													" smartdfa " + SmartMethodInfoFlowAnalysis.counter + 
													" smartloa " + SmartMethodLocalObjectsAnalysis.counter + ")");// (" + ie + " in " + containingMethod + ")");
					invokeCache.put(cacheKey, Boolean.TRUE);
					return true;
				}
			}
		}
		G.v().out.println("THREAD-PRIVATE (simpledfa " + ClassInfoFlowAnalysis.methodCount + 
												" smartdfa " + SmartMethodInfoFlowAnalysis.counter + 
												" smartloa " + SmartMethodLocalObjectsAnalysis.counter + ")");// (" + ie + " in " + containingMethod + ")");
		invokeCache.put(cacheKey, Boolean.FALSE);
		return false;
//*/
	}
	
    /**
        Returns a list of thread-shared sources and sinks.
        Returns empty list if not actually a shared value.
     */
	public List escapesThrough(Value sharedValue, SootMethod containingMethod)
	{
		List ret = new ArrayList();
			
	    // The containingMethod might be called from multiple threads
	    // It is possible for interestingValue to be thread-shared from some threads but not others,
	    // so we must look at each thread separately.
	    for(AbstractRuntimeThread thread : mhp.getThreads())
	    {
	        // Each "abstract thread" from the MHP analysis actually represents a "Thread.start()" statement that could
	        // be starting one of several different kinds of threads.  We must consider each kind separately.
	        for(Object meth : thread.getRunMethods())
	        {
	            SootMethod runMethod = (SootMethod) meth;

	            // We can only analyze application classes for TLO
	            if( runMethod.getDeclaringClass().isApplicationClass() &&
	            	!isObjectLocalToContext(sharedValue, containingMethod, runMethod))
	            {
	                // This is one of the threads for which sharedValue is thread-shared
	                // so now we will look for which object it escapes through
	                ClassLocalObjectsAnalysis cloa = getClassLocalObjectsAnalysis(containingMethod.getDeclaringClass());
	                CallLocalityContext clc = cloa.getMergedContext(containingMethod);

					// Get the method info flow analysis object
	                SmartMethodInfoFlowAnalysis smifa = dfa.getMethodInfoFlowAnalysis(containingMethod);

	                // Get an IFA node for our sharedValue
	                EquivalentValue sharedValueEqVal;
	                if(sharedValue instanceof InstanceFieldRef)
	                        sharedValueEqVal = InfoFlowAnalysis.getNodeForFieldRef(containingMethod, ((FieldRef) sharedValue).getField());
	                else
	                        sharedValueEqVal = new EquivalentValue(sharedValue);

	                // Get the sources of our interesting value
	                List<EquivalentValue> sources = smifa.sourcesOf(sharedValueEqVal);
	                for(EquivalentValue source : sources)
	                {
	                    if(source.getValue() instanceof Ref)
	                    {
	                        if(clc != null && !clc.isFieldLocal(source)) // (bail out if clc is null)
	                        {
	                        	ret.add(source);
//	                            System.out.println(sharedValue + " in " + containingMethod + " escapes through " + source);
	                        }
	                    }
	                }
	            }
	        }
	    }
	    return ret;
	}
}
