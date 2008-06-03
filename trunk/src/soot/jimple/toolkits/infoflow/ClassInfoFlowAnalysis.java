package soot.jimple.toolkits.infoflow;

import soot.*;

import java.util.*;
import soot.toolkits.graph.*;
import soot.jimple.*;

// ClassInfoFlowAnalysis written by Richard L. Halpert, 2007-02-22
// Constructs data flow tables for each method of a class.  Ignores indirect flow.
// These tables conservatively approximate how data flows from parameters,
// fields, and globals to parameters, fields, globals, and the return value.
// Note that a ref-type parameter (or field or global) might allow access to a
// large data structure, but that entire structure will be represented only by
// the parameter's one node in the data flow graph.

public class ClassInfoFlowAnalysis
{
	SootClass sootClass;
	InfoFlowAnalysis dfa; // used to access the data flow analyses of other classes
	
	Map<SootMethod, SmartMethodInfoFlowAnalysis> methodToInfoFlowAnalysis;
	Map<SootMethod, HashMutableDirectedGraph> methodToInfoFlowSummary;
	
	public static int methodCount = 0;
	
	public ClassInfoFlowAnalysis(SootClass sootClass, InfoFlowAnalysis dfa)
	{
		 this.sootClass = sootClass;
		 this.dfa = dfa;
		 methodToInfoFlowAnalysis = new HashMap<SootMethod, SmartMethodInfoFlowAnalysis>();
		 methodToInfoFlowSummary = new HashMap<SootMethod, HashMutableDirectedGraph>();
		 
//		 doSimpleConservativeDataFlowAnalysis();
	}
	
	public SmartMethodInfoFlowAnalysis getMethodInfoFlowAnalysis(SootMethod method)
	{
		if(!methodToInfoFlowAnalysis.containsKey(method))
		{
			methodCount++;

			// First do simple version that doesn't follow invoke expressions
			// The "smart" version will be computed later, but since it may
			// request its own DataFlowGraph, we need this simple version first.
			if(!methodToInfoFlowSummary.containsKey(method))
			{
				HashMutableDirectedGraph dataFlowGraph = simpleConservativeInfoFlowAnalysis(method);
				methodToInfoFlowSummary.put(method, dataFlowGraph);
			}
			
			// Then do smart version that does follow invoke expressions, if possible
			if(method.isConcrete())
			{
				Body b = method.retrieveActiveBody();
				UnitGraph g = new ExceptionalUnitGraph(b);
				SmartMethodInfoFlowAnalysis smdfa = new SmartMethodInfoFlowAnalysis(g, dfa);

				methodToInfoFlowAnalysis.put(method, smdfa);
				methodToInfoFlowSummary.remove(method);
				methodToInfoFlowSummary.put(method, smdfa.getMethodInfoFlowSummary());
				return smdfa;
//				G.v().out.println(method + " has SMART infoFlowGraph: ");
//				printDataFlowGraph(mdfa.getMethodDataFlowGraph());
			}
		}

		return methodToInfoFlowAnalysis.get(method);
	}
	
	public MutableDirectedGraph getMethodInfoFlowSummary(SootMethod method) { return getMethodInfoFlowSummary(method, true); }
	public HashMutableDirectedGraph getMethodInfoFlowSummary(SootMethod method, boolean doFullAnalysis)
	{
		if(!methodToInfoFlowSummary.containsKey(method))
		{
			methodCount++;

			// First do simple version that doesn't follow invoke expressions
			// The "smart" version will be computed later, but since it may
			// request its own DataFlowGraph, we need this simple version first.
			HashMutableDirectedGraph dataFlowGraph = simpleConservativeInfoFlowAnalysis(method);
			methodToInfoFlowSummary.put(method, dataFlowGraph);
			
			// Then do smart version that does follow invoke expressions, if possible
			if(method.isConcrete() && doFullAnalysis)// && method.getDeclaringClass().isApplicationClass())
			{
				Body b = method.retrieveActiveBody();
				UnitGraph g = new ExceptionalUnitGraph(b);
				SmartMethodInfoFlowAnalysis smdfa = new SmartMethodInfoFlowAnalysis(g, dfa);

				methodToInfoFlowAnalysis.put(method, smdfa);
				methodToInfoFlowSummary.remove(method);
				methodToInfoFlowSummary.put(method, smdfa.getMethodInfoFlowSummary());

//				G.v().out.println(method + " has SMART infoFlowGraph: ");
//				printDataFlowGraph(mdfa.getMethodDataFlowGraph());
			}
		}

		return methodToInfoFlowSummary.get(method);
	}
	
/*	public void doFixedPointDataFlowAnalysis()
	{
		Iterator it = sootClass.getMethods().iterator();
		while(it.hasNext())
		{
			SootMethod method = (SootMethod) it.next();
			
			if(method.isConcrete())
			{
				Body b = method.retrieveActiveBody();
				UnitGraph g = new ExceptionalUnitGraph(b);
				SmartMethodInfoFlowAnalysis smdfa = new SmartMethodInfoFlowAnalysis(g, dfa, true);
				if(methodToInfoFlowSummary.containsKey(method))
					methodToInfoFlowSummary.remove(method);
				else
					methodCount++;
				methodToInfoFlowSummary.put(method, smdfa.getMethodDataFlowSummary());

//				G.v().out.println(method + " has FLOW SENSITIVE infoFlowGraph: ");
//				printDataFlowGraph(mdfa.getMethodDataFlowGraph());
			}
			else
			{
				if(methodToInfoFlowSummary.containsKey(method))
					methodToInfoFlowSummary.remove(method);
				else
					methodCount++;
				methodToInfoFlowSummary.put(method, triviallyConservativeDataFlowAnalysis(method));

//				G.v().out.println(method + " has TRIVIALLY CONSERVATIVE infoFlowGraph: ");
//				printDataFlowGraph((MutableDirectedGraph) methodToInfoFlowSummary.get(method));
			}
		}
	}
//*/	
/*
	private void doSimpleConservativeDataFlowAnalysis()
	{
		Iterator it = sootClass.getMethods().iterator();
		while(it.hasNext())
		{
			SootMethod method = (SootMethod) it.next();
			MutableDirectedGraph infoFlowGraph = simpleConservativeDataFlowAnalysis(method);
			if(methodToInfoFlowSummary.containsKey(method))
				methodToInfoFlowSummary.remove(method);
			else
				methodCount++;
			methodToInfoFlowSummary.put(method, infoFlowGraph);

//			G.v().out.println(method + " has infoFlowGraph: ");
//			printDataFlowGraph(infoFlowGraph);
		}
	}
//*/	
	/** Does not require any fixed point calculation */
	private HashMutableDirectedGraph simpleConservativeInfoFlowAnalysis(SootMethod sm)
	{
		// Constructs a graph representing the data flow between fields, parameters, and the
		// return value of this method.  The graph nodes are EquivalentValue wrapped Refs.
		// This version is rather stupid... it just assumes all values flow to all others,
		// except for the return value, which is flowed to by all, but flows to none.
		
		// This version is also broken... it can't handle the ThisRef without
		// flow sensitivity.
		
		// If this method cannot have a body, then we can't analyze it... 
		if(!sm.isConcrete())
			return triviallyConservativeInfoFlowAnalysis(sm);
			
		Body b = sm.retrieveActiveBody();
		UnitGraph g = new ExceptionalUnitGraph(b);
		HashSet<EquivalentValue> fieldsStaticsParamsAccessed = new HashSet<EquivalentValue>();		

		// Get list of fields, globals, and parameters that are accessed
		Iterator stmtIt = g.iterator();
		while(stmtIt.hasNext())
		{
			Stmt s = (Stmt) stmtIt.next();
			if( s instanceof IdentityStmt )
			{
				IdentityStmt is = (IdentityStmt) s;
				IdentityRef ir = (IdentityRef) is.getRightOp();
				if( ir instanceof ParameterRef )
				{
					ParameterRef pr = (ParameterRef) ir;
					fieldsStaticsParamsAccessed.add(InfoFlowAnalysis.getNodeForParameterRef(sm, pr.getIndex()));
				}
			}
			if(s.containsFieldRef())
			{
				FieldRef ref = s.getFieldRef();
				if( ref instanceof StaticFieldRef )
				{
					// This should be added to the list of fields accessed
					// static fields "belong to everyone"
					StaticFieldRef sfr = (StaticFieldRef) ref;
					fieldsStaticsParamsAccessed.add(InfoFlowAnalysis.getNodeForFieldRef(sm, sfr.getField()));
				}
				else if( ref instanceof InstanceFieldRef )
				{
					// If this field is a field of this class,
					// then this should be added to the list of fields accessed
					InstanceFieldRef ifr = (InstanceFieldRef) ref;
					Value base = ifr.getBase();
					if(base instanceof Local)
					{
						if( dfa.includesInnerFields() || ((!sm.isStatic()) && base.equivTo(b.getThisLocal())) )
							fieldsStaticsParamsAccessed.add(InfoFlowAnalysis.getNodeForFieldRef(sm, ifr.getField()));
		            }
				}
			}
		}
		
		// Each accessed field, global, and parameter becomes a node in the graph
		HashMutableDirectedGraph dataFlowGraph = new MemoryEfficientGraph();
		Iterator<EquivalentValue> accessedIt1 = fieldsStaticsParamsAccessed.iterator();
		while(accessedIt1.hasNext())
		{
			Object o = accessedIt1.next();
			dataFlowGraph.addNode(o);
		}
		
		// Add all of the nodes necessary to ensure that this is a complete data flow graph
		// Add every parameter of this method
		for(int i = 0; i < sm.getParameterCount(); i++)
		{
			EquivalentValue parameterRefEqVal = InfoFlowAnalysis.getNodeForParameterRef(sm, i);
			if(!dataFlowGraph.containsNode(parameterRefEqVal))
				dataFlowGraph.addNode(parameterRefEqVal);
		}
		
		// Add every relevant field of this class (static methods don't get non-static fields)
		for(Iterator it = sm.getDeclaringClass().getFields().iterator(); it.hasNext(); )
		{
			SootField sf = (SootField) it.next();
			if(sf.isStatic() || !sm.isStatic())
			{
				EquivalentValue fieldRefEqVal = InfoFlowAnalysis.getNodeForFieldRef(sm, sf);
				if(!dataFlowGraph.containsNode(fieldRefEqVal))
					dataFlowGraph.addNode(fieldRefEqVal);
			}
		}
		
		// Add every field of this class's superclasses
		SootClass superclass = sm.getDeclaringClass();
		if(superclass.hasSuperclass())
			superclass = sm.getDeclaringClass().getSuperclass();
		while(superclass.hasSuperclass()) // we don't want to process Object
		{
	        Iterator scFieldsIt = superclass.getFields().iterator();
	        while(scFieldsIt.hasNext())
	        {
				SootField scField = (SootField) scFieldsIt.next();
				if(scField.isStatic() || !sm.isStatic())
				{
					EquivalentValue fieldRefEqVal = InfoFlowAnalysis.getNodeForFieldRef(sm, scField);
					if(!dataFlowGraph.containsNode(fieldRefEqVal))
						dataFlowGraph.addNode(fieldRefEqVal);
				}
	        }
			superclass = superclass.getSuperclass();
		}
		
		// The return value also becomes a node in the graph
		ParameterRef returnValueRef = null;
		if(sm.getReturnType() != VoidType.v())
		{
			returnValueRef = new ParameterRef(sm.getReturnType(), -1);
			dataFlowGraph.addNode(InfoFlowAnalysis.getNodeForReturnRef(sm));
		}
		
//		ThisRef thisRef = null;
		if(!sm.isStatic())
		{
//			thisRef = new ThisRef(sootClass.getType());
			dataFlowGraph.addNode(InfoFlowAnalysis.getNodeForThisRef(sm));
			fieldsStaticsParamsAccessed.add(InfoFlowAnalysis.getNodeForThisRef(sm));
		}
		
		// Create an edge from each node (except the return value) to every other node (including the return value)
		// non-Ref-type nodes are ignored
		accessedIt1 = fieldsStaticsParamsAccessed.iterator();
		while(accessedIt1.hasNext())
		{
			Object r = accessedIt1.next();
			Ref rRef = (Ref) ((EquivalentValue) r).getValue();
			if( !(rRef.getType() instanceof RefLikeType) && !dfa.includesPrimitiveInfoFlow())
				continue;
			Iterator<EquivalentValue> accessedIt2 = fieldsStaticsParamsAccessed.iterator();
			while(accessedIt2.hasNext())
			{
				Object s = accessedIt2.next();
				Ref sRef = (Ref) ((EquivalentValue) s).getValue();
				if( rRef instanceof ThisRef && sRef instanceof InstanceFieldRef )
					; // don't add this edge
				else if( sRef instanceof ThisRef && rRef instanceof InstanceFieldRef )
					; // don't add this edge
				else if( sRef instanceof ParameterRef && dfa.includesInnerFields())
					; // don't add edges to parameters if we are including inner fields
				else if( sRef.getType() instanceof RefLikeType )
					dataFlowGraph.addEdge(r, s);
			}
			if( returnValueRef != null && (returnValueRef.getType() instanceof RefLikeType || dfa.includesPrimitiveInfoFlow()))
				dataFlowGraph.addEdge(r, InfoFlowAnalysis.getNodeForReturnRef(sm));
		}
		
		return dataFlowGraph;
	}
	
	/** Does not require the method to have a body */
	public HashMutableDirectedGraph triviallyConservativeInfoFlowAnalysis(SootMethod sm)
	{
		HashSet<EquivalentValue> fieldsStaticsParamsAccessed = new HashSet<EquivalentValue>();
		
		// Add all of the nodes necessary to ensure that this is a complete data flow graph
		// Add every parameter of this method
		for(int i = 0; i < sm.getParameterCount(); i++)
		{
			EquivalentValue parameterRefEqVal = InfoFlowAnalysis.getNodeForParameterRef(sm, i);
			fieldsStaticsParamsAccessed.add(parameterRefEqVal);
		}
		
		// Add every relevant field of this class (static methods don't get non-static fields)
		for(Iterator it = sm.getDeclaringClass().getFields().iterator(); it.hasNext(); )
		{
			SootField sf = (SootField) it.next();
			if(sf.isStatic() || !sm.isStatic())
			{
				EquivalentValue fieldRefEqVal = InfoFlowAnalysis.getNodeForFieldRef(sm, sf);
				fieldsStaticsParamsAccessed.add(fieldRefEqVal);
			}
		}
		
		// Add every field of this class's superclasses
		SootClass superclass = sm.getDeclaringClass();
		if(superclass.hasSuperclass())
			superclass = sm.getDeclaringClass().getSuperclass();
		while(superclass.hasSuperclass()) // we don't want to process Object
		{
	        Iterator scFieldsIt = superclass.getFields().iterator();
	        while(scFieldsIt.hasNext())
	        {
				SootField scField = (SootField) scFieldsIt.next();
				if(scField.isStatic() || !sm.isStatic())
				{
					EquivalentValue fieldRefEqVal = InfoFlowAnalysis.getNodeForFieldRef(sm, scField);
					fieldsStaticsParamsAccessed.add(fieldRefEqVal);
				}
	        }
			superclass = superclass.getSuperclass();
		}
		
		// Don't add any static fields outside of the class... unsafe???

		// Each field, global, and parameter becomes a node in the graph
		HashMutableDirectedGraph dataFlowGraph = new MemoryEfficientGraph();
		Iterator<EquivalentValue> accessedIt1 = fieldsStaticsParamsAccessed.iterator();
		while(accessedIt1.hasNext())
		{
			Object o = accessedIt1.next();
			dataFlowGraph.addNode(o);
		}
		
		// The return value also becomes a node in the graph
		ParameterRef returnValueRef = null;
		if(sm.getReturnType() != VoidType.v())
		{
			returnValueRef = new ParameterRef(sm.getReturnType(), -1);
			dataFlowGraph.addNode(InfoFlowAnalysis.getNodeForReturnRef(sm));
		}
		
		ThisRef thisRef = null;
		if(!sm.isStatic())
		{
			thisRef = new ThisRef(sootClass.getType());
			dataFlowGraph.addNode(InfoFlowAnalysis.getNodeForThisRef(sm));
			fieldsStaticsParamsAccessed.add(InfoFlowAnalysis.getNodeForThisRef(sm));
		}
		
		// Create an edge from each node (except the return value) to every other node (including the return value)
		// non-Ref-type nodes are ignored
		accessedIt1 = fieldsStaticsParamsAccessed.iterator();
		while(accessedIt1.hasNext())
		{
			Object r = accessedIt1.next();
			Ref rRef = (Ref) ((EquivalentValue) r).getValue();
			if( !(rRef.getType() instanceof RefLikeType) && !dfa.includesPrimitiveInfoFlow() )
				continue;
			Iterator<EquivalentValue> accessedIt2 = fieldsStaticsParamsAccessed.iterator();
			while(accessedIt2.hasNext())
			{
				Object s = accessedIt2.next();
				Ref sRef = (Ref) ((EquivalentValue) s).getValue();
				if( rRef instanceof ThisRef && sRef instanceof InstanceFieldRef )
					; // don't add this edge
				else if( sRef instanceof ThisRef && rRef instanceof InstanceFieldRef )
					; // don't add this edge
				else if( sRef.getType() instanceof RefLikeType )
					dataFlowGraph.addEdge(r, s);
			}
			if( returnValueRef != null && (returnValueRef.getType() instanceof RefLikeType || dfa.includesPrimitiveInfoFlow()) )
				dataFlowGraph.addEdge(r, InfoFlowAnalysis.getNodeForReturnRef(sm));
		}
		
		return dataFlowGraph;
	}
	

}

