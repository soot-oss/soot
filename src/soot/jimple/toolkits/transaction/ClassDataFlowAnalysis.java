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

// ClassDataFlowAnalysis written by Richard L. Halpert, 2007-02-22
// Constructs data flow tables for each method of a class.  Ignores indirect flow.
// These tables conservatively approximate how data flows from parameters,
// fields, and globals to parameters, fields, globals, and the return value.
// Note that a ref-type parameter (or field or global) might allow access to a
// large data structure, but that entire structure will be represented only by
// the parameter's one node in the data flow graph.

public class ClassDataFlowAnalysis
{
	SootClass sootClass;
	DataFlowAnalysis dfa; // used to access the data flow analyses of other classes
	
	Map methodToDataFlowGraph;
	
	public ClassDataFlowAnalysis(SootClass sootClass, DataFlowAnalysis dfa)
	{
		 this.sootClass = sootClass;
		 this.dfa = dfa;
		 methodToDataFlowGraph = new HashMap();
		 
		 doFlowInsensitiveAnalysis();
	}
	
	public MutableDirectedGraph getDataFlowGraphOf(SootMethod sm)
	{
		return (MutableDirectedGraph) methodToDataFlowGraph.get(sm);
	}
	
	public void doFlowSensitiveAnalysis()
	{
		// TODO: store new, improved data flow graph
		Iterator it = sootClass.getMethods().iterator();
		while(it.hasNext())
		{
			SootMethod method = (SootMethod) it.next();
			
			if(method.isConcrete())
			{
				Body b = method.retrieveActiveBody();
				UnitGraph g = new ExceptionalUnitGraph(b);
				MethodDataFlowAnalysis mdfa = new MethodDataFlowAnalysis(g, dfa, true);
				if(methodToDataFlowGraph.containsKey(method))
					methodToDataFlowGraph.remove(method);
				methodToDataFlowGraph.put(method, mdfa.getDataFlowGraph());

				G.v().out.println(method + " has FLOW SENSITIVE dataFlowGraph: ");
				printDataFlowGraph(mdfa.getDataFlowGraph());
			}
			else
			{
				if(methodToDataFlowGraph.containsKey(method))
					methodToDataFlowGraph.remove(method);
				methodToDataFlowGraph.put(method, triviallyConservativeDataFlowGraph(method));
			}
		}
	}
	
	private void doFlowInsensitiveAnalysis()
	{
		Iterator it = sootClass.getMethods().iterator();
		while(it.hasNext())
		{
			SootMethod method = (SootMethod) it.next();
			MutableDirectedGraph dataFlowGraph = flowInsensitiveMethodAnalysis(method);
			if(methodToDataFlowGraph.containsKey(method))
				methodToDataFlowGraph.remove(method);
			methodToDataFlowGraph.put(method, dataFlowGraph);
//			G.v().out.println(method + " has dataFlowGraph: ");
//			printDataFlowGraph(dataFlowGraph);
		}
	}
	
	private MutableDirectedGraph flowInsensitiveMethodAnalysis(SootMethod sm)
	{
		// Constructs a graph representing the data flow between fields, parameters, and the
		// return value of this method.  The graph nodes are EquivalentValue wrapped Refs.
		// This version is rather stupid... it just assumes all values flow to all others,
		// except for the return value, which is flowed to by all, but flows to none.
		
		// This version is also broken... it can't handle the ThisRef without
		// flow sensitivity.
		
		// If this method cannot have a body, then we can't analyze it... 
		if(!sm.isConcrete())
			return triviallyConservativeDataFlowGraph(sm);
			
		Body b = sm.retrieveActiveBody();
		UnitGraph g = new ExceptionalUnitGraph(b);
		HashSet fieldsStaticsParamsAccessed = new HashSet();		

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
					fieldsStaticsParamsAccessed.add(new EquivalentValue(ir));
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
					fieldsStaticsParamsAccessed.add(new EquivalentValue(sfr));
				}
				else if( ref instanceof InstanceFieldRef )
				{
					// If this field is a field of this class,
					// then this should be added to the list of fields accessed
					InstanceFieldRef ifr = (InstanceFieldRef) ref;
					Value base = ifr.getBase();
					if(base instanceof Local)
					{
						if( (!sm.isStatic()) && base.equivTo(g.getBody().getThisLocal()) )
							fieldsStaticsParamsAccessed.add(new EquivalentValue(ifr));
		            }
				}
			}
		}
		
		// Each field, global, and parameter becomes a node in the graph
		MutableDirectedGraph dataFlowGraph = new MemoryEfficientGraph();
		Iterator accessedIt1 = fieldsStaticsParamsAccessed.iterator();
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
			dataFlowGraph.addNode(new EquivalentValue(returnValueRef));
		}
		
		// Create an edge from each node (except the return value) to every other node (including the return value)
		// non-Ref-type nodes are ignored
		accessedIt1 = fieldsStaticsParamsAccessed.iterator();
		while(accessedIt1.hasNext())
		{
			Object r = accessedIt1.next();
			Ref rRef = (Ref) ((EquivalentValue) r).getValue();
			if( !(rRef.getType() instanceof RefType) )
				continue;
			Iterator accessedIt2 = fieldsStaticsParamsAccessed.iterator();
			while(accessedIt2.hasNext())
			{
				Object s = accessedIt2.next();
				Ref sRef = (Ref) ((EquivalentValue) s).getValue();
				if(	sRef.getType() instanceof RefType )
					dataFlowGraph.addEdge(r, s);
			}
			if( returnValueRef != null && !(returnValueRef.getType() instanceof RefType) )
				dataFlowGraph.addEdge(r, new EquivalentValue(returnValueRef));
		}
		
		return dataFlowGraph;
	}
	
	public MutableDirectedGraph triviallyConservativeDataFlowGraph(SootMethod sm)
	{
		HashSet fieldsStaticsParamsAccessed = new HashSet();
		
		// Add every parameter of the method
		for(int i = 0; i < sm.getParameterCount(); i++)
		{
			fieldsStaticsParamsAccessed.add(dfa.getEquivalentValueParameterRef(sm, i));
		}
		
		// Add every field of the class
		for(Iterator it = sootClass.getFields().iterator(); it.hasNext(); )
		{
			SootField sf = (SootField) it.next();
			fieldsStaticsParamsAccessed.add(dfa.getEquivalentValueFieldRef(sm, sf));
		}
		
		// Don't add any static fields outside of the class... unsafe???

		// Each field, global, and parameter becomes a node in the graph
		MutableDirectedGraph dataFlowGraph = new MemoryEfficientGraph();
		Iterator accessedIt1 = fieldsStaticsParamsAccessed.iterator();
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
			dataFlowGraph.addNode(new EquivalentValue(returnValueRef));
		}
		
		// Create an edge from each node (except the return value) to every other node (including the return value)
		// non-Ref-type nodes are ignored
		accessedIt1 = fieldsStaticsParamsAccessed.iterator();
		while(accessedIt1.hasNext())
		{
			Object r = accessedIt1.next();
			Ref rRef = (Ref) ((EquivalentValue) r).getValue();
			if( !(rRef.getType() instanceof RefType) )
				continue;
			Iterator accessedIt2 = fieldsStaticsParamsAccessed.iterator();
			while(accessedIt2.hasNext())
			{
				Object s = accessedIt2.next();
				Ref sRef = (Ref) ((EquivalentValue) s).getValue();
				if(	sRef.getType() instanceof RefType )
					dataFlowGraph.addEdge(r, s);
			}
			if( returnValueRef != null && !(returnValueRef.getType() instanceof RefType) )
				dataFlowGraph.addEdge(r, new EquivalentValue(returnValueRef));
		}
		
		return dataFlowGraph;
	}
	
	private void printDataFlowGraph(DirectedGraph g)
	{
		Iterator nodeIt = g.iterator();
		if(!nodeIt.hasNext())
			G.v().out.println("    " + " --> ");
		while(nodeIt.hasNext())
		{
			Object node = nodeIt.next();
			List sources = g.getPredsOf(node);
			Iterator sourcesIt = sources.iterator();
			if(!sourcesIt.hasNext())
				continue;
			G.v().out.print("    [ ");
			int sourcesnamelength = 0;
			int lastnamelength = 0;
			while(sourcesIt.hasNext())
			{
				Ref r = (Ref) ((EquivalentValue) sourcesIt.next()).getValue(); // unwrap the Ref
				if(r instanceof FieldRef)
				{
					FieldRef fr = (FieldRef) r;
					String name = fr.getFieldRef().name();
					lastnamelength = name.length();
					if(lastnamelength > sourcesnamelength)
						sourcesnamelength = lastnamelength;
					G.v().out.print(name);
				}
				else if(r instanceof ParameterRef)
				{
					ParameterRef pr = (ParameterRef) r;
					lastnamelength = 11;
					if(lastnamelength > sourcesnamelength)
						sourcesnamelength = lastnamelength;
					G.v().out.print("@parameter" + pr.getIndex());
				}
				else
				{
					String name = r.toString();
					lastnamelength = name.length();
					if(lastnamelength > sourcesnamelength)
						sourcesnamelength = lastnamelength;
					G.v().out.println(name);
				}
				if(sourcesIt.hasNext())
					G.v().out.print("\n      ");
			}
			for(int i = 0; i < sourcesnamelength - lastnamelength; i++)
				G.v().out.print(" ");
			G.v().out.println(" ] --> " + node.toString());
		}
	}

}

