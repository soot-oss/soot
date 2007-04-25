package soot.jimple.toolkits.infoflow;

import soot.*;
import java.util.*;
import soot.toolkits.graph.*;
import soot.toolkits.scalar.*;
import soot.jimple.toolkits.callgraph.*;
import soot.jimple.*;

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
	InfoFlowAnalysis dfa;
	UseFinder uf;
	CallGraph cg;

	Map classToClassLocalObjectsAnalysis;
	
	Map mergedContextsCache;
	Map mloaCache;
	
	public LocalObjectsAnalysis(InfoFlowAnalysis dfa)
	{
		this.dfa = dfa;
		this.uf = new UseFinder();
		this.cg = Scene.v().getCallGraph();
		
		classToClassLocalObjectsAnalysis = new HashMap();
		mergedContextsCache = new HashMap();
		mloaCache = new HashMap();
	}
	
	public ClassLocalObjectsAnalysis getClassLocalObjectsAnalysis(SootClass sc)
	{
		if(!classToClassLocalObjectsAnalysis.containsKey(sc))
		{
			ClassLocalObjectsAnalysis cloa = newClassLocalObjectsAnalysis(this, dfa, uf, sc);
			classToClassLocalObjectsAnalysis.put(sc, cloa);
		}
		return (ClassLocalObjectsAnalysis) classToClassLocalObjectsAnalysis.get(sc);
	}
	
	// meant to be overridden by specialty local objects analyses
	protected ClassLocalObjectsAnalysis newClassLocalObjectsAnalysis(LocalObjectsAnalysis loa, InfoFlowAnalysis dfa, UseFinder uf, SootClass sc)
	{
		return new ClassLocalObjectsAnalysis(loa, dfa, uf, sc);
	}
	
	public boolean isObjectLocalToParent(Value localOrRef, SootMethod sm)
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
		return cloa.isFieldLocal(sf);
	}
	
	public boolean isObjectLocalToContext(Value localOrRef, SootMethod sm, SootMethod context)
	{		
		// Handle special case
		if(sm == context)
		{
//			G.v().out.println("      Directly Reachable: ");
			boolean isLocal = isObjectLocalToParent(localOrRef, sm);
			if(dfa.printDebug())
				G.v().out.println("    " + (isLocal ? 
					"LOCAL  (Directly Reachable from " + context.getDeclaringClass().getShortName() + "." + context.getName() + ")" :
					"SHARED (Directly Reachable from " + context.getDeclaringClass().getShortName() + "." + context.getName() + ")"));
			return isLocal;
		}
	
		// Handle obvious case
		if( localOrRef instanceof StaticFieldRef )
		{
			if(dfa.printDebug())
				G.v().out.println("    SHARED (Static             from " + context.getDeclaringClass().getShortName() + "." + context.getName() + ")");
			return false;
		}

		// Handle uncheckable case
		if(!sm.isConcrete())
		{
			// no way to tell... and how do we have access to a Local anyways???
			throw new RuntimeException("Attempted to check if a local variable in a non-concrete method is shared/local.");
		}

		// For Resulting Merged Context, check if localOrRef is local
		Body b = sm.retrieveActiveBody(); // sm is guaranteed concrete (see above)
		// Check if localOrRef is Local in smContext
/*		SmartMethodLocalObjectsAnalysis mloa = null;
//		Pair mloaKey = new Pair(sm, mergedContext);
		if( mloaCache.containsKey(sm) )
		{
			mloa = (SmartMethodLocalObjectsAnalysis) mloaCache.get(sm);
//			G.v().out.println("      Retrieved mloa From Cache: ");
		}
		else
		{
			UnitGraph g = new ExceptionalUnitGraph(b);
			mloa = new SmartMethodLocalObjectsAnalysis(g, dfa);
//			G.v().out.println("        Caching mloa (smdfa " + SmartMethodInfoFlowAnalysis.counter + 
//				" smloa " + SmartMethodLocalObjectsAnalysis.counter + ") for " + sm.getName() + " on goal:");
			mloaCache.put(sm, mloa);
		}
//*/

		CallLocalityContext mergedContext = getClassLocalObjectsAnalysis(context.getDeclaringClass()).getMergedContext(sm);
		if(mergedContext == null)
		{
			if(dfa.printDebug())
				G.v().out.println("      ------ (Unreachable        from " + context.getDeclaringClass().getShortName() + "." + context.getName() + ")");
			return true; // it's not non-local...
		}

		// with the completed mergedContext...
		// localOrRef can actually be a field ref
		if( localOrRef instanceof InstanceFieldRef )
		{
			InstanceFieldRef ifr = (InstanceFieldRef) localOrRef;
			
			Local thisLocal = null;
			try{ thisLocal = b.getThisLocal(); }
			catch(RuntimeException re) { /* Couldn't get thisLocal */ }
			
			if(ifr.getBase() == thisLocal)
			{
				boolean isLocal = mergedContext.isFieldLocal(dfa.getNodeForFieldRef(sm, ifr.getField()));
				if(dfa.printDebug())
				{
					if(isLocal)
					{
						G.v().out.println("      LOCAL  (this  .localField  from " + context.getDeclaringClass().getShortName() + "."
																				   + context.getName() + ")");
					}
					else
					{
						G.v().out.println("      SHARED (this  .sharedField from " + context.getDeclaringClass().getShortName() + "." 
																				   + context.getName() + ")");
					}
				}
				return isLocal;
			}
			else
			{
				boolean isLocal = SmartMethodLocalObjectsAnalysis.isObjectLocal(dfa, sm, mergedContext, ifr.getBase());
				if(isLocal)
				{
					ClassLocalObjectsAnalysis cloa = getClassLocalObjectsAnalysis(context.getDeclaringClass());
					isLocal = !cloa.getInnerSharedFields().contains(ifr.getField());
					if(dfa.printDebug())
					{
						if(isLocal)
						{
							G.v().out.println("      LOCAL  (local .localField  from " + context.getDeclaringClass().getShortName() + "."
																					   + context.getName() + ")");
						}
						else
						{
							G.v().out.println("      SHARED (local .sharedField from " + context.getDeclaringClass().getShortName() + "."
																					   + context.getName() + ")");
						}
					}
					return isLocal;
				}
				else
				{
					if(dfa.printDebug())
						G.v().out.println("      SHARED (shared.someField   from " + context.getDeclaringClass().getShortName() + "."
																			   + context.getName() + ")");
					return isLocal;
				}
			}
		}

		boolean isLocal = SmartMethodLocalObjectsAnalysis.isObjectLocal(dfa, sm, mergedContext, localOrRef);
		if(dfa.printDebug())
		{
			if(isLocal)
			{	
				G.v().out.println("      LOCAL  ( local             from " + context.getDeclaringClass().getShortName() + "."
																		   + context.getName() + ")");
			}
			else
			{
				G.v().out.println("      SHARED (shared             from " + context.getDeclaringClass().getShortName() + "."
																		   + context.getName() + ")");
			}
		}
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
	
	public CallChain getNextCallChainBetween(SootMethod start, SootMethod goal, List previouslyFound)
	{
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
//			Set methodsInAnyChain = new HashSet();
//			methodsInAnyChain.add(goal);
			return getNextCallChainBetween(rm, start, goal, null, null, previouslyFound);
		}
		
		return null; // new ArrayList();
	}

	Map callChainsCache = new HashMap();

	public CallChain getNextCallChainBetween(ReachableMethods rm, SootMethod start, SootMethod end, Edge endToPath, CallChain path, List previouslyFound)
	{
		Pair cacheKey = new Pair(start, end);
		if(callChainsCache.containsKey(cacheKey))
		{
//        	G.v().out.print("C");
			return null;
//			return (CallChain) callChainsCache.get(cacheKey);
		}
		path = new CallChain(endToPath, path); // initially, path and endToPath can be null
		if(start == end)
		{
//			if(previouslyFound.contains(path)) // don't return a call chain that was already returned in a previous run
//			{
//				G.v().out.print("P");
//				return null;
//			}

//			G.v().out.print("F");
			return path;

//			List ret = new ArrayList();
//			ret.add(path);
//			G.v().out.print("F");
//			return ret;
        }

        if(!rm.contains(end))
        {
//        	G.v().out.print("U");
        	return null; // new ArrayList(); // no paths
        }

//		List paths = new ArrayList(); // no paths

		Iterator edgeIt = cg.edgesInto(end);
		while(edgeIt.hasNext())
		{
			Edge e = (Edge) edgeIt.next();
			SootMethod node = e.src();
			if(!path.containsMethod(node) && e.isExplicit() && e.srcStmt().containsInvokeExpr())
			{
//	        	G.v().out.print("R");
				CallChain newpath = getNextCallChainBetween(rm, start, node, e, path, previouslyFound); // node is supposed to be a method
				if(newpath != null)
				{
//		        	G.v().out.print("|");
					if(!previouslyFound.contains(newpath))
						return newpath;
				}
//				Iterator newpathsIt = newpaths.iterator();
//				while(newpathsIt.hasNext())
//				{
//					paths.addAll(newpaths);
//				}
			}
			else
			{
//	        	G.v().out.print("S");
			}
		}
//		G.v().out.print("(" + paths.size() + ")");
//		if(paths.size() < 100)
		if(previouslyFound.size() == 0)
			callChainsCache.put(cacheKey, null);
//		G.v().out.print("|");
		return null;
	}

/*	
	// callChains go from current to goal
	public void getCallChainsBetween(SootMethod start, SootMethod current, SootMethod goal, ReachableMethods rm, List callChains, Set methodsInAnyChain)
	{
		List oldCallChains = new ArrayList();
		oldCallChains.addAll(callChains);
		callChains.clear();

		Pair cacheKey = new Pair(start, current);
		if(callChainsCache.containsKey(cacheKey))
		{
			List cachedChains = (List) callChainsCache.get(cacheKey);

			Iterator cachedChainsIt = cachedChains.iterator();
			while(cachedChainsIt.hasNext())
			{
				CallChain cachedChain = (CallChain) cachedChainsIt.next();
				Iterator oldCallChainsIt = oldCallChains.iterator();
				while(oldCallChainsIt.hasNext())
				{
					CallChain oldChain = (CallChain) oldCallChainsIt.next();
					callChains.add(cachedChain.cloneAndExtend(oldChain));
				}
			}
			
			// We now have chains from start to goal
			
			G.v().out.print("C");
			return;
		}

		// For each edge into goal, clone the existing call chains and add that edge to the beginning, then call self with new goal
		Iterator edgeIt = cg.edgesInto(current);
		while(edgeIt.hasNext())
		{
			Edge e = (Edge) edgeIt.next();
//			Stmt currentCallerStmt = e.srcStmt();
			SootMethod currentCaller = e.src();
			
			// If the source of this edge is unreachable, ignore it
			if( !rm.contains(currentCaller) )
			{
				G.v().out.print("U");
				continue;
			}

			// If this would introduce an SCC, skip it (TODO: Deal with it, instead)
			boolean currentCallerIsAlreadyInAChain = false;
			Iterator oldCallChainsIt = oldCallChains.iterator();
			while(oldCallChainsIt.hasNext())
			{
				CallChain oldCallChain = (CallChain) oldCallChainsIt.next();
				if(oldCallChain.containsMethod(currentCaller))
				{
					currentCallerIsAlreadyInAChain = true;
					break;
				}
			}
				
			if( ( currentCaller == goal ) || currentCallerIsAlreadyInAChain) // methodsInAnyChain.contains(goalCaller) )
			{
				G.v().out.print("S");
				continue; // if this goalCaller would be an SCC, ignore it
			}

			// If this is the type of edge that we'd like to include in our call chains
			if(e.isExplicit())// && goalCallerStmt.containsInvokeExpr())
			{
				// Make a copy of all call chains
//				List newCallChains = cloneCallChains(oldCallChains);
				List newCallChains = new ArrayList();
				
				if(oldCallChains.size() == 0)
				{
					newCallChains.add(new CallChain(e, null));
				}
				else
				{
					// Add this edge to each call chain
					oldCallChainsIt = oldCallChains.iterator();
					while(oldCallChainsIt.hasNext())
					{
						CallChain oldCallChain = (CallChain) oldCallChainsIt.next();
						newCallChains.add(new CallChain(e, oldCallChain));
					}
				}
				
//				methodsInAnyChain.add(goalCaller);
					
				// If the call chains don't now start from start, then get ones that do (recursively)
				if(currentCaller != start)
				{
					G.v().out.print("R");

					// Call self to extend these new call chains all the way to start
					getCallChainsBetween(start, currentCaller, goal, rm, newCallChains, methodsInAnyChain);
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
		if(callChains.size() > 0)
			callChainsCache.put(new Pair(start, goal), callChains);
	}
*/
/*
	// returns a 1-deep clone of a List of Lists
	private List cloneCallChains(List callChains)
	{
		List ret = new ArrayList();
		Iterator callChainsIt = callChains.iterator();
		while(callChainsIt.hasNext())
			ret.add( ((LinkedList) callChainsIt.next()).clone() ); // add a clone of each call chain
		return ret;
	}
*/	
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
			callingContext = new CallLocalityContext(dfa.getMethodInfoFlowSummary(callingMethod).getNodes()); // just keeps a map from NODE to SHARED/LOCAL
			
			// We will use the containing context that we have to determine if base/args are local
			if(callingMethod.isConcrete())
			{
				Body b = containingMethod.retrieveActiveBody();
				
				SmartMethodLocalObjectsAnalysis mloa = null;
//				Pair mloaKey = new Pair(containingMethod, containingContext);
				if( mloaCache.containsKey(containingMethod) )
				{
					mloa = (SmartMethodLocalObjectsAnalysis) mloaCache.get(containingMethod);
//					G.v().out.println("        Retrieved mloa for " + containingMethod.getName() + " on Call Chain: ");
				}
				else
				{		
					UnitGraph g = new ExceptionalUnitGraph(b);
					mloa = new SmartMethodLocalObjectsAnalysis(g, dfa);
//					G.v().out.println("        Caching mloa (smdfa " + SmartMethodInfoFlowAnalysis.counter + 
//						" smloa " + SmartMethodLocalObjectsAnalysis.counter + ") for " + containingMethod.getName() + " on Call Chain:");
					mloaCache.put(containingMethod, mloa);
				}
			
				// check base
				if(ie instanceof InstanceInvokeExpr)
				{
					InstanceInvokeExpr iie = (InstanceInvokeExpr) ie;
					if( !containingMethod.isStatic() && iie.getBase().equivTo(b.getThisLocal()) )
					{
						// calling another method on same object... basically copy the previous context
						Iterator localRefsIt = containingContext.getLocalRefs().iterator();
						while(localRefsIt.hasNext())
						{
							EquivalentValue rEqVal = (EquivalentValue) localRefsIt.next();
							Ref r = (Ref) rEqVal.getValue();
							if(r instanceof InstanceFieldRef)
							{
								EquivalentValue newRefEqVal = dfa.getNodeForFieldRef(callingMethod, ((FieldRef) r).getFieldRef().resolve());
								if(callingContext.containsField(newRefEqVal)) // if not, then we're probably calling a parent class's method, so some fields are missing
									callingContext.setFieldLocal(newRefEqVal); // must make a new eqval for the method getting called
							}
							else if(r instanceof ThisRef)
								callingContext.setThisLocal();
						}
					}
					else if(mloa.isObjectLocal(iie.getBase(), containingContext))
					{
						// calling a method on a local object
						callingContext.setAllFieldsLocal();
						callingContext.setThisLocal();
					}
					else
					{
						// calling a method on a shared object
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
					if(mloa.isObjectLocal(ie.getArg(param), containingContext))
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
			if(dfa.printDebug())
				G.v().out.println("      " + callingMethod.getName() + " " + callingContext.toShortString());
		}
		return callingContext;
	}
	
	private CallLocalityContext getInitialContext(SootMethod startingMethod, Edge e)
	{
		// get first calling method and calling context
		InvokeExpr ie = e.srcStmt().getInvokeExpr();
		SootMethod callingMethod = e.tgt();
//		if(dfa.getMethodDataFlowGraph(callingMethod) == null)
//			throw new RuntimeException("Data Flow Graph not found for " + callingMethod);
//		if(dfa.getMethodDataFlowGraph(callingMethod).getNodes().size() == 0)
//			throw new RuntimeException("Data Flow Graph with no nodes found for " + callingMethod);
		CallLocalityContext callingContext = new CallLocalityContext(dfa.getMethodInfoFlowSummary(callingMethod).getNodes()); // just keeps a map from NODE to SHARED/LOCAL
		
		// check base
		if(ie instanceof InstanceInvokeExpr)
		{
			InstanceInvokeExpr iie = (InstanceInvokeExpr) ie;
			if( !startingMethod.isStatic() && iie.getBase().equivTo(startingMethod.retrieveActiveBody().getThisLocal()) )
			{
				// calling another method on same object... basically copy the previous context
				ClassLocalObjectsAnalysis cloa = getClassLocalObjectsAnalysis(startingMethod.getDeclaringClass());
				CallLocalityContext containingContext = cloa.getContextFor(startingMethod);

				Iterator localRefsIt = containingContext.getLocalRefs().iterator();
				while(localRefsIt.hasNext())
				{
					EquivalentValue rEqVal = (EquivalentValue) localRefsIt.next();
					Ref r = (Ref) rEqVal.getValue();
					if(r instanceof InstanceFieldRef)
					{
						EquivalentValue newRefEqVal = dfa.getNodeForFieldRef(callingMethod, ((FieldRef) r).getFieldRef().resolve());
						if(callingContext.containsField(newRefEqVal)) // if not, then we're probably calling a parent class's method, so some fields are missing
							callingContext.setFieldLocal(newRefEqVal); // must make a new eqval for the method getting called
					}
					else if(r instanceof ThisRef)
						callingContext.setThisLocal();
				}
			}
			else if(isObjectLocalToParent(iie.getBase(), startingMethod))
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
	
	private CallLocalityContext getContextViaCallChain(SootMethod startingMethod, List callChain) // destroys callChain
	{
		// SHOULD PERFORM CACHING, since it's likely that we'll ask about several values in the same method
		
	
		// Get context and method from the first call
		Edge e = (Edge) callChain.get(0);
		CallLocalityContext callContext = getInitialContext(startingMethod, e); // gets calllocalitycontext of first call
		SootMethod callMethod = e.tgt();
		if(dfa.printDebug())
			G.v().out.println("      " + callMethod.getName() + " " + callContext.toShortString());
		
		// Remove the first call, and get context at end of chain (method is sm)
		callChain.remove(0);
		CallLocalityContext endContext = getContextAtEndOfCallChain(callContext, callMethod, callChain);
		return endContext;
	}
	
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
	
	public boolean hasNonLocalEffects(SootMethod containingMethod, InvokeExpr ie, SootMethod context)
	{
		SootMethod target = ie.getMethodRef().resolve();
		MutableDirectedGraph dataFlowGraph = dfa.getMethodInfoFlowSummary(target); // TODO actually we want a graph that is sensitive to scalar data, too
		
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

