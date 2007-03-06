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
	
	public LocalObjectsAnalysis(DataFlowAnalysis dfa) //, boolean threadBased)
	{
		this.dfa = dfa;
		this.cg = Scene.v().getCallGraph();
		
		classToClassLocalObjectsAnalysis = new HashMap();
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
		ClassLocalObjectsAnalysis cloa = getClassLocalObjectsAnalysis(sm.getDeclaringClass());
		return cloa.isObjectLocal(localOrRef, sm);
	}
	
	public boolean isFieldLocalToParent(SootField sf) // To parent class!
	{
		ClassLocalObjectsAnalysis cloa = getClassLocalObjectsAnalysis(sf.getDeclaringClass());
		return cloa.fieldIsLocal(sf);
	}
	
	public boolean isObjectLocalToContext(Value localOrRef, SootMethod sm, SootClass context)
	{
		G.v().out.println("    Checking if " + localOrRef + " in " + sm + " is local to " + context + ":");
		
		if(sm.getDeclaringClass() == context) // special case
		{
			boolean isLocal = isObjectLocalToParent(localOrRef, sm);
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
//			G.v().out.println("      Unreachable: treat as local.");
			return true; // it's not non-local...
		}
		G.v().out.println("      Found " + callChains.size() + " Call Chains...");
		for(int i = 0; i < callChains.size(); i++)
			G.v().out.println("      " + callChains.get(i));
		
		
		// Check Call Chains
		for(int i = 0; i < callChains.size(); i++)
		{
			List callChain = (List) callChains.get(i);
			if(!isObjectLocalToContextViaCallChain(localOrRef, sm, context, (SootMethod) startingMethods.get(i), callChain))
			{
				G.v().out.println("      SHARED");
				return false;
			}
		}
		G.v().out.println("      LOCAL");
		return true;
	}
	
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
//			G.v().out.println("      Unreachable: treat as local.");
			return true; // it's not non-local...
		}
		G.v().out.println("      Found " + callChains.size() + " Call Chains...");
		for(int i = 0; i < callChains.size(); i++)
			G.v().out.println("      " + callChains.get(i));
		
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
	
	public List getCallChainsBetween(SootMethod start, SootMethod goal)
	{
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

				Pair pair = new Pair(goalCallerStmt.getInvokeExpr(), goal);
				Iterator edgeCallChainsIt = edgeCallChains.iterator();
				while(edgeCallChainsIt.hasNext())
				{
					List edgeCallChain = (List) edgeCallChainsIt.next();
					edgeCallChain.add(pair);
				}
				callChains.addAll(edgeCallChains);
			}
		}
		return callChains;
	}
	
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
			Pair call = (Pair) callIt.next();
			InvokeExpr ie = (InvokeExpr) call.getO1();
			callingMethod = (SootMethod) call.getO2();
			callingContext = new CallLocalityContext(dfa.getMethodDataFlowGraph(callingMethod).getNodes()); // just keeps a map from NODE to SHARED/LOCAL
			
			// We will use the containing context that we have to determine if base/args are local
			if(callingMethod.isConcrete())
			{
				Body b = callingMethod.retrieveActiveBody();
				UnitGraph g = new ExceptionalUnitGraph(b);
				MethodLocalObjectsAnalysis mloa = new MethodLocalObjectsAnalysis(g, containingContext, dfa);
			
				// check base
				if(ie instanceof InstanceInvokeExpr)
				{
					InstanceInvokeExpr iie = (InstanceInvokeExpr) ie;
					if(mloa.isObjectLocal(iie.getBase()))
						callingContext.setAllFieldsLocal();
					else
						callingContext.setAllFieldsShared();
				}
				else
				{
					callingContext.setAllFieldsShared();
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
				callingContext.setAllParamsShared();
			}
		}
		return callingContext;
	}
	
	private CallLocalityContext getInitialContext(SootClass context, SootMethod startingMethod, Pair call)
	{
		// get first calling method and calling context
		InvokeExpr ie = (InvokeExpr) call.getO1();
		SootMethod callingMethod = (SootMethod) call.getO2();
//		if(dfa.getMethodDataFlowGraph(callingMethod) == null)
//			throw new RuntimeException("Data Flow Graph not found for " + callingMethod);
//		if(dfa.getMethodDataFlowGraph(callingMethod).getNodes().size() == 0)
//			throw new RuntimeException("Data Flow Graph with no nodes found for " + callingMethod);
		CallLocalityContext callingContext = new CallLocalityContext(dfa.getMethodDataFlowGraph(callingMethod).getNodes()); // just keeps a map from NODE to SHARED/LOCAL
		
		// check base
		if(ie instanceof InstanceInvokeExpr)
		{
			InstanceInvokeExpr iie = (InstanceInvokeExpr) ie;
			if(isObjectLocalToContext(iie.getBase(), startingMethod, context))
				callingContext.setAllFieldsLocal();
			else
				callingContext.setAllFieldsShared();
		}
		else
		{
			callingContext.setAllFieldsShared();
		}
		
		// check args
		for(int param = 0; param < ie.getArgCount(); param++)
		{
			if(isObjectLocalToContext(ie.getArg(param), startingMethod, context))
				callingContext.setParamLocal(param);
			else
				callingContext.setParamShared(param);
		}
		return callingContext;
	}
	
	private CallLocalityContext getContextViaCallChain(SootClass context, SootMethod startingMethod, List callChain) // destroys callChain
	{
		// SHOULD PERFORM CACHING, since it's likely that we'll ask about several values in the same method
	
		// Get context and method from the first call
		Pair call = (Pair) callChain.get(0);
		CallLocalityContext callContext = getInitialContext(context, startingMethod, call); // gets calllocalitycontext of first call
		SootMethod callMethod = (SootMethod) call.getO2();
		
		// Remove the first call, and get context at end of chain (method is sm)
		callChain.remove(0);
		return getContextAtEndOfCallChain(callContext, callMethod, callChain);
	}
	
	public boolean isObjectLocalToContextViaCallChain(Value localOrRef, SootMethod sm, SootClass context, SootMethod startingMethod, List callChain) // can be partial call chain... just calls isObjectLocalToContext on top of chain
	{
		// Filter out illegal inputs
		if(!sm.isConcrete())
			return false; // no way to tell... and how do we have access to a Local anyways???
			
		// check if call chain reaches sm... if not then gripe
		if( sm != (SootMethod) ((Pair) callChain.get(callChain.size() - 1)).getO2() )
			throw new RuntimeException("Cannot determine if a Local is local via a call chain that does not end in the Local's containing method");

		// get the local/shared info for sm given callChain from context, startingMethod
		CallLocalityContext smContext = getContextViaCallChain(context, startingMethod, callChain);
		
		G.v().out.print(smContext.toString());
		
		// Check if localOrRef is Local in smContext
		Body b = sm.retrieveActiveBody();
		UnitGraph g = new ExceptionalUnitGraph(b);
		MethodLocalObjectsAnalysis mloa = new MethodLocalObjectsAnalysis(g, smContext, dfa);
		return mloa.isObjectLocal(localOrRef);
	}

	public boolean isFieldLocalToContextViaCallChain(SootField sf, SootMethod sm, SootClass context, SootMethod startingMethod, List callChain) // can be partial call chain... just calls isObjectLocalToContext on top of chain
	{
		// Filter out illegal inputs
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
		MutableDirectedGraph dataFlowGraph = dfa.getMethodDataFlowGraph(target);
		if(ie instanceof StaticInvokeExpr)
		{
			return true; // hasFieldTypeSourcesOrSinks(dataFlowGraph); // this commented out code is unsafe: could read/write parameters that are shared
		}
		else if(ie instanceof InstanceInvokeExpr)
		{
			InstanceInvokeExpr iie = (InstanceInvokeExpr) ie;
			if( isObjectLocalToContext(iie.getBase(), containingMethod, context) )
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
							FieldRef fr = (FieldRef) node;
							if( !isFieldLocalToContext(fr.getFieldRef().resolve(), containingMethod, context) )
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
			else
			{
				return true; // hasFieldTypeSourcesOrSinks(dataFlowGraph); // this commented out code is unsafe: could read/write parameters that are shared
			}
		}
		return false;
	}
	
	private boolean hasFieldTypeSourcesOrSinks(MutableDirectedGraph dataFlowGraph)
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
		}
		return false;
	}
}

