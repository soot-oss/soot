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

// LocalObjectsAnalysis written by Richard L. Halpert, 2007-02-24
// Constructs data flow tables for each method of every application class.  Ignores indirect flow.
// These tables conservatively approximate how data flows from parameters,
// fields, and globals to parameters, fields, globals, and the return value.
// Note that a ref-type parameter (or field or global) might allow access to a
// large data structure, but that entire structure will be represented only by
// the parameter's one node in the data flow graph.
// Provides a high level interface to access the data flow information.



public class LocalObjectsAnalysis
{
	DataFlowAnalysis dfa;
	CallGraph cg;

	Map classToClassLocalObjectsAnalysis; // ClassLocalObjectsAnalysis for own class as scope
	
	Map mergedContextsCache;
	Map mloaCache;
	
	public LocalObjectsAnalysis(DataFlowAnalysis dfa) //, boolean threadBased)
	{
		this.dfa = dfa;
		this.cg = Scene.v().getCallGraph();
		
		classToClassLocalObjectsAnalysis = new HashMap();
		mergedContextsCache = new HashMap();
		mloaCache = new HashMap();
	}
	
	public ClassLocalObjectsAnalysis getClassLocalObjectsAnalysis(SootClass sc)
	{
		if(!classToClassLocalObjectsAnalysis.containsKey(sc)) // only application classes are precomputed
		{
			// Create the needed flow analysis object
			ClassLocalObjectsAnalysis cloa = new ClassLocalObjectsAnalysis(this, dfa, sc);
			
			// Put the preliminary simple conservative results here in case they
			// are needed by the fixed-point version.  This method will be
			// reentrant if any method we are analyzing is reentrant, so we
			// must do this to prevent an infinite recursive loop.
			classToClassLocalObjectsAnalysis.put(sc, cloa);
		}
		return (ClassLocalObjectsAnalysis) classToClassLocalObjectsAnalysis.get(sc);
	}
	
	public boolean isObjectLocalToParent(Value localOrRef, SootMethod sm) // To parent class!
	{
		// Handle obvious case
		if( localOrRef instanceof StaticFieldRef )
			return false;

		ClassLocalObjectsAnalysis cloa = getClassLocalObjectsAnalysis(sm.getDeclaringClass());
		return cloa.isObjectLocal(localOrRef, sm);
	}
	
	public boolean isFieldLocalToParent(SootField sf) // To parent class!
	{
		// Handle obvious case
		if( sf.isStatic() )
			return false;

		ClassLocalObjectsAnalysis cloa = getClassLocalObjectsAnalysis(sf.getDeclaringClass());
		return cloa.fieldIsLocal(sf);
	}
	
	public boolean isObjectLocalToContext(Value localOrRef, SootMethod sm, SootClass context)
	{
//		G.v().out.print("    Checking if " + localOrRef + " in " + sm + " is local to " + context + ": ");
		
		// Handle special case
		if(sm.getDeclaringClass() == context)
		{
//			G.v().out.println("Directly Reachable: ");
			boolean isLocal = isObjectLocalToParent(localOrRef, sm);
			return isLocal;
		}
	
		// Handle obvious case
		if( localOrRef instanceof StaticFieldRef )
			return false;

		// Handle uncheckable case
		if(!sm.isConcrete())
			return false; // no way to tell... and how do we have access to a Local anyways???

		CallLocalityContext mergedContext = null;
		if( mergedContextsCache.containsKey(new Pair(sm, context)) )
		{
			mergedContext = (CallLocalityContext) mergedContextsCache.get(new Pair(sm, context));
//			G.v().out.println("\n      Retrieved mergedContext From Cache: ");
		}
		else
		{
	
			// The rest of the time, we must find all call chains from context to sm
			// if it's local on all of them, then return true.
			
			// Find Call Chains (separate chains for separate possible virtual call targets)
			// TODO right now we discard reentrant call chains... but this is UNSAFE
			// TODO right now we are stupid about virtual calls... but this is UNSAFE
			
			// for each method in the context class (OR JUST FROM THE RUN METHOD IF IT'S A THREAD?)
			List classMethods = getAllMethodsForClass(context); // gets methods in context class and superclasses
			List callChains = new ArrayList();
			List startingMethods = new ArrayList();
			Iterator classMethodsIt = classMethods.iterator();
			while(classMethodsIt.hasNext())
			{
				SootMethod classMethod = (SootMethod) classMethodsIt.next();
//				if(classMethod.getDeclaringClass().isApplicationClass())
				List methodCallChains = getCallChainsBetween(classMethod, sm);
				Iterator methodCallChainsIt = methodCallChains.iterator();
				while(methodCallChainsIt.hasNext())
				{
					callChains.add(methodCallChainsIt.next());
					startingMethods.add(classMethod); // need to add this once for each method call chain being added
				}
			}
			
			G.v().out.println("");
			
			if(callChains.size() == 0)
			{
//				G.v().out.println("Unreachable: treat as local.");
				return true; // it's not non-local...
			}
			else
			{
				int chainLength = 0;
				for(int i = 0; i < callChains.size(); i++)
				{
					List callChain = (List) callChains.get(i);
					chainLength = chainLength + callChain.size();
				}
				G.v().out.println("      Following " + callChains.size() + " Call Chains of average length " + ( ((float) chainLength) / (float) callChains.size() ) + ": ");
			}
//			G.v().out.println("      Found " + callChains.size() + " Call Chains...");
	//		for(int i = 0; i < callChains.size(); i++)
	//			G.v().out.println("      " + callChains.get(i));
			
			
			// Check Call Chains
			mergedContext = new CallLocalityContext(dfa.getMethodDataFlowGraph(sm).getNodes());
			for(int i = 0; i < callChains.size(); i++)
			{
				List callChain = (List) callChains.get(i);
//				G.v().out.println(callChain);
				mergedContext.merge(getContextViaCallChain(context, (SootMethod) startingMethods.get(i), callChain));
				if(mergedContext.isAllShared(true)) // if all reftype nodes are shared
				{
					G.v().out.println("      mergedContext is All Shared after " + (i + 1) + " call chains");
					break;
				}
	//			if(!isObjectLocalToContextViaCallChain(localOrRef, sm, context, (SootMethod) startingMethods.get(i), callChain))
	//			{
	//				G.v().out.println("      SHARED");
	//				return false;
	//			}
			}
			// Cache the resulting merged context!
			mergedContextsCache.put(new Pair(sm, context), mergedContext);
		}
		
		// For Resulting Merged Context, check if localOrRef is local
		Body b = sm.retrieveActiveBody(); // sm is guaranteed concrete (see above)

		// localOrRef can actually be a field ref
		if( localOrRef instanceof InstanceFieldRef )
		{
			InstanceFieldRef ifr = (InstanceFieldRef) localOrRef;
			
			Local thisLocal = null;
			try{ thisLocal = b.getThisLocal(); }
			catch(RuntimeException re) { /* Couldn't get thisLocal */ }
			
			if(ifr.getBase() == thisLocal)
			{
				boolean isLocal = mergedContext.isFieldLocal(new EquivalentValue(localOrRef));
//				if(isLocal)
//					G.v().out.println("      LOCAL");
//				else
//					G.v().out.println("      SHARED");
				return isLocal;
			}
			
			localOrRef = ifr.getBase(); // search for the ref through which the field is accessed
		}
		
		// Check if localOrRef is Local in smContext
		MethodLocalObjectsAnalysis mloa = null;
		Pair mloaKey = new Pair(sm, mergedContext);
		if( mloaCache.containsKey(mloaKey) )
		{
			mloa = (MethodLocalObjectsAnalysis) mloaCache.get(mloaKey);
//			G.v().out.println("      Retrieved mloa From Cache: ");
		}
		else
		{		
			UnitGraph g = new ExceptionalUnitGraph(b);
			mloa = new MethodLocalObjectsAnalysis(g, mergedContext, dfa);
			G.v().out.println("        Caching mloa (smdfa " + ClassDataFlowAnalysis.methodCount + " mdfa " + MethodDataFlowAnalysis.counter + " mloa " + MethodLocalObjectsAnalysis.mlocounter + ") for " + sm.getName() + ":" + mergedContext.toShortString() + " on goal:");
			mloaCache.put(mloaKey, mloa);
		}
		boolean isLocal = mloa.isObjectLocal(localOrRef);
		
//		if(isLocal)
//			G.v().out.println("      LOCAL");
//		else
//			G.v().out.println("      SHARED");
		return isLocal;
	}

/*	BROKEN	
	public boolean isFieldLocalToContext(SootField sf, SootMethod sm, SootClass context)
	{
		G.v().out.println("    Checking if " + sf + " in " + sm + " is local to " + context + ":");

		if(sm.getDeclaringClass() == context) // special case
		{
			boolean isLocal = isFieldLocalToParent(sf);
			G.v().out.println("      Directly Reachable: " + (isLocal ? "LOCAL" : "SHARED"));
			return isLocal;
		}
	
		// The rest of the time, we must find all call chains from context to sm
		// if it's local on all of them, then return true.
		
		// Find Call Chains (separate chains for separate possible virtual call targets)
		// TODO right now we discard reentrant call chains... but this is UNSAFE
		// TODO right now we are stupid about virtual calls... but this is UNSAFE
		
		// for each method in the context class (OR JUST FROM THE RUN METHOD IF IT'S A THREAD?)
		List classMethods = getAllMethodsForClass(context); // gets methods in context class and superclasses
		List callChains = new ArrayList();
		List startingMethods = new ArrayList();
		Iterator classMethodsIt = classMethods.iterator();
		while(classMethodsIt.hasNext())
		{
			SootMethod classMethod = (SootMethod) classMethodsIt.next();
			List methodCallChains = getCallChainsBetween(classMethod, sm);
			Iterator methodCallChainsIt = methodCallChains.iterator();
			while(methodCallChainsIt.hasNext())
			{
				callChains.add(methodCallChainsIt.next());
				startingMethods.add(classMethod); // need to add this once for each method call chain being added
			}
		}
		
		if(callChains.size() == 0)
		{
			G.v().out.println("      Unreachable: treat as local.");
			return true; // it's not non-local...
		}
		G.v().out.println("      Found " + callChains.size() + " Call Chains...");
//		for(int i = 0; i < callChains.size(); i++)
//			G.v().out.println("      " + callChains.get(i));
		
		// Check Call Chains
		for(int i = 0; i < callChains.size(); i++)
		{
			List callChain = (List) callChains.get(i);
			if(!isFieldLocalToContextViaCallChain(sf, sm, context, (SootMethod) startingMethods.get(i), callChain))
			{
				G.v().out.println("      SHARED");
				return false;
			}
		}
		G.v().out.println("      LOCAL");
		return true;
	}
*/
	Map rmCache = new HashMap();
	
	public List getCallChainsBetween(SootMethod start, SootMethod goal)
	{
		List callChains = new ArrayList();
//		callChains.add(new LinkedList()); // Represents the one way to get from goal to goal (which is to already be there)

		// Is this worthwhile?  Fast?  Slow?  Broken?  Applicable inside the recursive method?
		// If method is unreachable, don't bother trying to make chains
		// CACHEABLE?
		ReachableMethods rm = null;
		if(rmCache.containsKey(start))
			rm = (ReachableMethods) rmCache.get(start);
		else
		{
			List entryPoints = new ArrayList();
			entryPoints.add(start);
			rm = new ReachableMethods(cg, entryPoints);
			rm.update();
			rmCache.put(start, rm);
		}
		
		if(rm.contains(goal))
		{
			Set methodsInAnyChain = new HashSet();
			methodsInAnyChain.add(goal);
			getCallChainsBetween(start, goal, rm, callChains, methodsInAnyChain);
		}
		
		return callChains;
	}
	
	Map callChainsCache = new HashMap();
	
	public void getCallChainsBetween(SootMethod start, SootMethod goal, ReachableMethods rm, List callChains, Set methodsInAnyChain) // assume these are call chains from goal to the original goal
	{
		Pair cacheKey = new Pair(start, goal);
		if(callChainsCache.containsKey(cacheKey))
		{
			callChains.clear();
			callChains.addAll(cloneCallChains( (List) callChainsCache.get(cacheKey) ));
			G.v().out.print("C");
			return;
		}
		
		List oldCallChains = cloneCallChains(callChains);
		callChains.clear();

		// For each edge into goal, clone the existing call chains and add that edge to the beginning, then call self with new goal
		Iterator edgeIt = cg.edgesInto(goal);
		while(edgeIt.hasNext())
		{
			Edge e = (Edge) edgeIt.next();
			Stmt goalCallerStmt = e.srcStmt();
			SootMethod goalCaller = e.src();
			
			// If the source of this edge is unreachable, ignore it
			if( !rm.contains(goalCaller) )
			{
				G.v().out.print("U");
				continue;
			}

			// If this would introduce an SCC, skip it (TODO: Deal with it, instead)
/*			boolean goalCallerIsAlreadyInAChain = false;
			ListIterator oldCallChainsIt = oldCallChains.listIterator();
			while(oldCallChainsIt.hasNext())
			{
				List oldCallChain = (List) oldCallChainsIt.next();
				if(oldCallChain.contains(e))
				{
					goalCallerIsAlreadyInAChain = true;
					break;
				}
			}
*/
				
			if( ( goalCaller == goal ) || methodsInAnyChain.contains(goalCaller) )
			{
				G.v().out.print("S");
				continue; // if this goalCaller would be an SCC, ignore it
			}

			// If this is the type of edge that we'd like to include in our call chains
			if(e.isExplicit() && goalCallerStmt.containsInvokeExpr())
			{
				// Make a copy of all call chains
				List newCallChains = cloneCallChains(oldCallChains);
				
				if(newCallChains.size() == 0)
				{
					newCallChains.add(new LinkedList());
				}
				
				// Add this edge to each call chain
				ListIterator newCallChainsIt = newCallChains.listIterator();
				while(newCallChainsIt.hasNext())
				{
					List newCallChain = (List) newCallChainsIt.next();
					newCallChain.add(0, e);
				}
				
				methodsInAnyChain.add(goalCaller);
					
				// If the call chains don't now start from start, then get ones that do (recursively)
				if(goalCaller != start)
				{
					G.v().out.print("R");

					// Call self to extend these new call chains all the way to start
					getCallChainsBetween(start, goalCaller, rm, newCallChains, methodsInAnyChain);
				}
				else
				{
					G.v().out.print("F");
				}
				
				// Add all the new call chains to our set
				callChains.addAll(newCallChains);
			}
		}
		G.v().out.print("(" + callChains.size() + ")");
		callChainsCache.put(cacheKey, cloneCallChains(callChains));
	}
	
	// returns a 1-deep clone of a List of Lists
	private List cloneCallChains(List callChains)
	{
		List ret = new ArrayList();
		Iterator callChainsIt = callChains.iterator();
		while(callChainsIt.hasNext())
			ret.add( ((LinkedList) callChainsIt.next()).clone() ); // add a clone of each call chain
		return ret;
	}
	
/*
	public List getCallChainsBetween(SootMethod start, SootMethod goal)
	{
		G.v().out.print("Q");
		List callChains = new ArrayList();
		Iterator edgeIt = cg.edgesInto(goal);
		while(edgeIt.hasNext())
		{
			Edge e = (Edge) edgeIt.next();
			Stmt goalCallerStmt = e.srcStmt();
			SootMethod goalCaller = e.src();
			if(e.isExplicit() && goalCallerStmt.containsInvokeExpr()) // if not, we're not interested
			{
				List edgeCallChains = null;
				if(goalCaller == start)
				{
					edgeCallChains = new ArrayList();
					edgeCallChains.add(new LinkedList());
				}
				else
				{
					edgeCallChains = getCallChainsBetween(start, goalCaller);
				}

				Pair pair = new Pair(new EquivalentValue(goalCallerStmt.getInvokeExpr()), goal);
				Iterator edgeCallChainsIt = edgeCallChains.iterator();
				while(edgeCallChainsIt.hasNext())
				{
					List edgeCallChain = (List) edgeCallChainsIt.next();
					if( !edgeCallChain.contains(pair) ) // SCC, we must ignore, sadly... TODO FIX THIS
					{
						edgeCallChain.add(pair);
						callChains.add(edgeCallChain);
					}
				}
			}
		}
		return callChains;
	}
*/

	private CallLocalityContext getContextAtEndOfCallChain(CallLocalityContext startingContext, SootMethod startingMethod, List callChain)
	{
		CallLocalityContext containingContext = null;
		CallLocalityContext callingContext = startingContext;
		SootMethod containingMethod = null;
		SootMethod callingMethod = startingMethod;
		
		// each call is a pair: invoke expression and the method it calls
		
		Iterator callIt = callChain.iterator();
		// for later calls, we must find out if the base object and parameters are local to the calling method based on whether its base object and parameters were local to the containing method
		while(callIt.hasNext())
		{
			// calling method and calling context from last iteration now become containing method and containing context
			containingMethod = callingMethod;
			containingContext = callingContext;
			
			// get new calling method and calling context
			Edge e = (Edge) callIt.next();
			InvokeExpr ie = e.srcStmt().getInvokeExpr();
			callingMethod = e.tgt();
			callingContext = new CallLocalityContext(dfa.getMethodDataFlowGraph(callingMethod).getNodes()); // just keeps a map from NODE to SHARED/LOCAL
			
			// We will use the containing context that we have to determine if base/args are local
			if(callingMethod.isConcrete())
			{
				Body b = containingMethod.retrieveActiveBody();
				
				MethodLocalObjectsAnalysis mloa = null;
				Pair mloaKey = new Pair(containingMethod, containingContext);
				if( mloaCache.containsKey(mloaKey) )
				{
					mloa = (MethodLocalObjectsAnalysis) mloaCache.get(mloaKey);
					G.v().out.println("        Retrieved mloa for " + containingMethod.getName() + ":" + containingContext.toShortString() + " on Call Chain: ");
				}
				else
				{		
					UnitGraph g = new ExceptionalUnitGraph(b);
					mloa = new MethodLocalObjectsAnalysis(g, containingContext, dfa);
					G.v().out.println("        Caching mloa (smdfa " + ClassDataFlowAnalysis.methodCount + " mdfa " + MethodDataFlowAnalysis.counter + " mloa " + MethodLocalObjectsAnalysis.mlocounter + ") for " + containingMethod.getName() + ":" + containingContext.toShortString() + " on Call Chain:");
					mloaCache.put(mloaKey, mloa);
				}
			
				// check base
				if(ie instanceof InstanceInvokeExpr)
				{
					InstanceInvokeExpr iie = (InstanceInvokeExpr) ie;
					if(mloa.isObjectLocal(iie.getBase()))
					{
						callingContext.setAllFieldsLocal();
						callingContext.setThisLocal();
					}
					else
					{
						callingContext.setAllFieldsShared();
						callingContext.setThisShared();
					}
				}
				else
				{
					callingContext.setAllFieldsShared();
					callingContext.setThisShared();
				}
				
				// check args
				for(int param = 0; param < ie.getArgCount(); param++)
				{
					if(mloa.isObjectLocal(ie.getArg(param)))
						callingContext.setParamLocal(param);
					else
						callingContext.setParamShared(param);
				}
			}
			else
			{
				// The only conservative solution for a bodyless method is to assume everything is shared
				callingContext.setAllFieldsShared();
				callingContext.setThisShared();				
				callingContext.setAllParamsShared();
			}
		}
		return callingContext;
	}
	
	private CallLocalityContext getInitialContext(SootClass context, SootMethod startingMethod, Edge e)
	{
		// get first calling method and calling context
		InvokeExpr ie = e.srcStmt().getInvokeExpr();
		SootMethod callingMethod = e.tgt();
//		if(dfa.getMethodDataFlowGraph(callingMethod) == null)
//			throw new RuntimeException("Data Flow Graph not found for " + callingMethod);
//		if(dfa.getMethodDataFlowGraph(callingMethod).getNodes().size() == 0)
//			throw new RuntimeException("Data Flow Graph with no nodes found for " + callingMethod);
		CallLocalityContext callingContext = new CallLocalityContext(dfa.getMethodDataFlowGraph(callingMethod).getNodes()); // just keeps a map from NODE to SHARED/LOCAL
		
		// check base
		if(ie instanceof InstanceInvokeExpr)
		{
			InstanceInvokeExpr iie = (InstanceInvokeExpr) ie;
			if(isObjectLocalToParent(iie.getBase(), startingMethod))
			{
				callingContext.setAllFieldsLocal();
				callingContext.setThisLocal();
			}
			else
			{
				callingContext.setAllFieldsShared();
				callingContext.setThisShared();
			}
		}
		else
		{
			callingContext.setAllFieldsShared();
			callingContext.setThisShared();
		}
		
		// check args
		for(int param = 0; param < ie.getArgCount(); param++)
		{
			if(isObjectLocalToParent(ie.getArg(param), startingMethod))
				callingContext.setParamLocal(param);
			else
				callingContext.setParamShared(param);
		}
		return callingContext;
	}
	
/*
	private boolean areCallChainsEqual(List cc1, List cc2)
	{
		if(cc1.size() != cc2.size())
			return false;
		Iterator cc1It = cc1.iterator();
		Iterator cc2It = cc2.iterator();
		while(cc1It.hasNext())
		{
			Pair cc1call = (Pair) cc1It.next();
			Pair cc2call = (Pair) cc2It.next();
			InvokeExpr cc1ie = (InvokeExpr) cc1call.getO1();
			SootMethod cc1method = (SootMethod) cc1call.getO2();
			InvokeExpr cc2ie = (InvokeExpr) cc2call.getO1();
			SootMethod cc2method = (SootMethod) cc2call.getO2();

			if(cc1ie != cc2ie) return false;
			if(cc1method != cc2method) return false;
		}
		return true;
	}
*/
	
	private CallLocalityContext getContextViaCallChain(SootClass context, SootMethod startingMethod, List callChain) // destroys callChain
	{
		// SHOULD PERFORM CACHING, since it's likely that we'll ask about several values in the same method
		
	
		// Get context and method from the first call
		Edge e = (Edge) callChain.get(0);
		CallLocalityContext callContext = getInitialContext(context, startingMethod, e); // gets calllocalitycontext of first call
		SootMethod callMethod = e.tgt();
		
		// Remove the first call, and get context at end of chain (method is sm)
		callChain.remove(0);
		CallLocalityContext endContext = getContextAtEndOfCallChain(callContext, callMethod, callChain);
		return endContext;
	}
	
	public boolean isObjectLocalToContextViaCallChain(Value localOrRef, SootMethod sm, SootClass context, SootMethod startingMethod, List callChain) // can be partial call chain... just calls isObjectLocalToContext on top of chain
	{
		// Filter out illegal inputs
		if(!sm.isConcrete())
			return false; // no way to tell... and how do we have access to a Local anyways???
			
		// check if call chain reaches sm... if not then gripe
		if( sm != (SootMethod) ((Pair) callChain.get(callChain.size() - 1)).getO2() )
			throw new RuntimeException("Cannot determine if a Local is local via a call chain that does not end in the Local's containing method");

		// Handle obvious case
		if( localOrRef instanceof StaticFieldRef )
			return false;

		// get the local/shared info for sm given callChain from context, startingMethod
		CallLocalityContext smContext = getContextViaCallChain(context, startingMethod, callChain);
		
		//
		Body b = sm.retrieveActiveBody();

		// localOrRef can actually be a field ref
		if( localOrRef instanceof InstanceFieldRef )
		{
			InstanceFieldRef ifr = (InstanceFieldRef) localOrRef;
			if(ifr.getBase() == b.getThisLocal())
				return smContext.isFieldLocal(new EquivalentValue(localOrRef));
			
			localOrRef = ifr.getBase(); // search for the ref through which the field is accessed
		}
		
		// Check if localOrRef is Local in smContext
		UnitGraph g = new ExceptionalUnitGraph(b);
		MethodLocalObjectsAnalysis mloa = new MethodLocalObjectsAnalysis(g, smContext, dfa);
		return mloa.isObjectLocal(localOrRef);
	}

/*
	// BROKEN
	// It's not recommended to use this... sf must be a field of sm.getDeclaringClass()
	public boolean isFieldLocalToContextViaCallChain(SootField sf, SootMethod sm, SootClass context, SootMethod startingMethod, List callChain) // can be partial call chain... just calls isObjectLocalToContext on top of chain
	{
		// Filter out illegal inputs
		if(sf.getDeclaringClass() != sm.getDeclaringClass())
			throw new RuntimeException("Misuse of isFieldLocalToContextViaCallChain(): field in question must be a field of the containing method's declaring class.");
		
		if(!sm.isConcrete())
			return false; // no way to tell... and how do we have access to a Local anyways???
			
		// check if call chain reaches sm... if not then gripe
		if( sm != (SootMethod) ((Pair) callChain.get(callChain.size() - 1)).getO2() )
			throw new RuntimeException("Cannot determine if a Local is local via a call chain that does not end in the Local's containing method");

		// get the local/shared info for sm given callChain from context, startingMethod
		CallLocalityContext smContext = getContextViaCallChain(context, startingMethod, callChain);
		G.v().out.print(smContext.toString());
		return smContext.isFieldLocal(dfa.getEquivalentValueFieldRef(sm, sf));
	}
*/
	
	// returns a list of all methods that can be invoked on an object of type sc
	public List getAllMethodsForClass(SootClass sootClass)
	{
		// Determine which methods are reachable in this program
		ReachableMethods rm = Scene.v().getReachableMethods();

		// Get list of reachable methods declared in this class
		// Also get list of fields declared in this class
		List scopeMethods = new ArrayList();
		Iterator scopeMethodsIt = sootClass.methodIterator();
		while(scopeMethodsIt.hasNext())
		{
			SootMethod scopeMethod = (SootMethod) scopeMethodsIt.next();
			if(rm.contains(scopeMethod))
				scopeMethods.add(scopeMethod);
		}
		
		// Add reachable methods and fields declared in superclasses
		SootClass superclass = sootClass;
		if(superclass.hasSuperclass())
			superclass = sootClass.getSuperclass();
		while(superclass.hasSuperclass()) // we don't want to process Object
		{
	        Iterator scMethodsIt = superclass.methodIterator();
	        while(scMethodsIt.hasNext())
	        {
				SootMethod scMethod = (SootMethod) scMethodsIt.next();
				if(rm.contains(scMethod))
					scopeMethods.add(scMethod);
	        }
			superclass = superclass.getSuperclass();
		}
		return scopeMethods;
	}
	
	public boolean hasNonLocalEffects(SootMethod containingMethod, InvokeExpr ie, SootClass context)
	{
		SootMethod target = ie.getMethodRef().resolve();
		MutableDirectedGraph dataFlowGraph = dfa.getMethodDataFlowGraph(target); // TODO actually we want a graph that is sensitive to scalar data, too
		
		// For a static invoke, check if any fields or any shared params are read/written
		if(ie instanceof StaticInvokeExpr)
		{
			Iterator graphIt = dataFlowGraph.iterator();
			while(graphIt.hasNext())
			{
				EquivalentValue nodeEqVal = (EquivalentValue) graphIt.next();
				Ref node = (Ref) nodeEqVal.getValue();
				if(node instanceof FieldRef)
				{
					if( dataFlowGraph.getPredsOf(nodeEqVal).size() > 0 ||
						dataFlowGraph.getSuccsOf(nodeEqVal).size() > 0 )
					{
						return true;
					}
				}
				else if(node instanceof ParameterRef)
				{
					if( dataFlowGraph.getPredsOf(nodeEqVal).size() > 0 ||
						dataFlowGraph.getSuccsOf(nodeEqVal).size() > 0 )
					{
						ParameterRef pr = (ParameterRef) node;
						if(pr.getIndex() != -1)
						{
							if( !isObjectLocalToContext(ie.getArg(pr.getIndex()), containingMethod, context) )
								return true;
						}
					}
				}
			}
		}
		else if(ie instanceof InstanceInvokeExpr)
		{
			// For a instance invoke on local object, check if any static fields or any shared params are read/written
			InstanceInvokeExpr iie = (InstanceInvokeExpr) ie;
			if( isObjectLocalToContext(iie.getBase(), containingMethod, context) )
			{
				Iterator graphIt = dataFlowGraph.iterator();
				while(graphIt.hasNext())
				{
					EquivalentValue nodeEqVal = (EquivalentValue) graphIt.next();
					Ref node = (Ref) nodeEqVal.getValue();
					if(node instanceof StaticFieldRef)
					{
						if( dataFlowGraph.getPredsOf(nodeEqVal).size() > 0 ||
							dataFlowGraph.getSuccsOf(nodeEqVal).size() > 0 )
						{
							return true;
						}
					}
					else if(node instanceof ParameterRef)
					{
						if( dataFlowGraph.getPredsOf(nodeEqVal).size() > 0 ||
							dataFlowGraph.getSuccsOf(nodeEqVal).size() > 0 )
						{
							ParameterRef pr = (ParameterRef) node;
							if(pr.getIndex() != -1)
							{
								if( !isObjectLocalToContext(ie.getArg(pr.getIndex()), containingMethod, context) )
									return true;
							}
						}
					}
				}
			}
			// For a instance invoke on shared object, check if any fields or any shared params are read/written
			else
			{
				Iterator graphIt = dataFlowGraph.iterator();
				while(graphIt.hasNext())
				{
					EquivalentValue nodeEqVal = (EquivalentValue) graphIt.next();
					Ref node = (Ref) nodeEqVal.getValue();
					if(node instanceof FieldRef)
					{
						if( dataFlowGraph.getPredsOf(nodeEqVal).size() > 0 ||
							dataFlowGraph.getSuccsOf(nodeEqVal).size() > 0 )
						{
							return true;
						}
					}
					else if(node instanceof ParameterRef)
					{
						if( dataFlowGraph.getPredsOf(nodeEqVal).size() > 0 ||
							dataFlowGraph.getSuccsOf(nodeEqVal).size() > 0 )
						{
							ParameterRef pr = (ParameterRef) node;
							if(pr.getIndex() != -1)
							{
								if( !isObjectLocalToContext(ie.getArg(pr.getIndex()), containingMethod, context) )
									return true;
							}
						}
					}
				}
			}
		}
		return false;
	}
}

