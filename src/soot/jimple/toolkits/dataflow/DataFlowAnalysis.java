package soot.jimple.toolkits.dataflow;

import java.util.*;

import soot.*;
import soot.util.dot.*;
import soot.toolkits.graph.*;
import soot.jimple.internal.*;
import soot.jimple.*;

// DataFlowAnalysis written by Richard L. Halpert, 2007-02-24
// Constructs data flow tables for each method of every application class.  Ignores indirect flow.
// These tables conservatively approximate how data flows from parameters,
// fields, and globals to parameters, fields, globals, and the return value.
// Note that a ref-type parameter (or field or global) might allow access to a
// large data structure, but that entire structure will be represented only by
// the parameter's one node in the data flow graph.
// Provides a high level interface to access the data flow information.

public class DataFlowAnalysis
{
	boolean includePrimitiveDataFlow;
	boolean includeInnerFields;

	Map classToClassDataFlowAnalysis;
	
	public DataFlowAnalysis(boolean includePrimitiveDataFlow, boolean includeInnerFields)
	{
		this.includePrimitiveDataFlow = includePrimitiveDataFlow;
		this.includeInnerFields = includeInnerFields;
		classToClassDataFlowAnalysis = new HashMap();
	}
	
	public boolean includesPrimitiveDataFlow()
	{
		return includePrimitiveDataFlow;
	}
	
	public boolean includesInnerFields()
	{
		return includeInnerFields;
	}
	
/*
	public void doApplicationClassesAnalysis()
	{
    	Iterator appClassesIt = Scene.v().getApplicationClasses().iterator();
    	while (appClassesIt.hasNext()) 
    	{
    	    SootClass appClass = (SootClass) appClassesIt.next();

			// Create the needed flow analysis object
			ClassDataFlowAnalysis cdfa = new ClassDataFlowAnalysis(appClass, this);
			
			// Put the preliminary flow-insensitive results here in case they
			// are needed by the flow-sensitive version.  This method will be
			// reentrant if any method we are analyzing is reentrant, so we
			// must do this to prevent an infinite recursive loop.
			classToClassDataFlowAnalysis.put(appClass, cdfa);
		}
		
    	Iterator appClassesIt2 = Scene.v().getApplicationClasses().iterator();
    	while (appClassesIt2.hasNext()) 
    	{
    	    SootClass appClass = (SootClass) appClassesIt2.next();
			// Now calculate the flow-sensitive version.  If this classes methods
			// are reentrant, it will call this method and receive the flow
			// insensitive version that is already cached.
			ClassDataFlowAnalysis cdfa = (ClassDataFlowAnalysis) classToClassDataFlowAnalysis.get(appClass);
			cdfa.doFixedPointDataFlowAnalysis();
		}
	}
*/		

	private ClassDataFlowAnalysis getClassDataFlowAnalysis(SootClass sc)
	{
		if(!classToClassDataFlowAnalysis.containsKey(sc))
		{
			ClassDataFlowAnalysis cdfa = new ClassDataFlowAnalysis(sc, this);
			classToClassDataFlowAnalysis.put(sc, cdfa);
		}
		return (ClassDataFlowAnalysis) classToClassDataFlowAnalysis.get(sc);
	}
	
	public SmartMethodDataFlowAnalysis getMethodDataFlowAnalysis(SootMethod sm)
	{
		ClassDataFlowAnalysis cdfa = getClassDataFlowAnalysis(sm.getDeclaringClass());
		return cdfa.getMethodDataFlowAnalysis(sm);
	}
	
	/** Returns a BACKED MutableDirectedGraph whose nodes are EquivalentValue 
	  * wrapped Refs. It's perfectly safe to modify this graph, just so long as 
	  * new nodes are EquivalentValue wrapped Refs. */
	public MutableDirectedGraph getMethodDataFlowGraph(SootMethod sm) { return getMethodDataFlowGraph(sm, true); }
	public MutableDirectedGraph getMethodDataFlowGraph(SootMethod sm, boolean doFullAnalysis)
	{
		ClassDataFlowAnalysis cdfa = getClassDataFlowAnalysis(sm.getDeclaringClass());
		return cdfa.getMethodDataFlowGraph(sm, doFullAnalysis);
	}
	
	/** Returns an unmodifiable list of EquivalentValue wrapped Refs that source
	  * flows to when method sm is called. */
/*	public List getSinksOf(SootMethod sm, EquivalentValue source)
	{
		ClassDataFlowAnalysis cdfa = getClassDataFlowAnalysis(sm.getDeclaringClass());
		MutableDirectedGraph g = cdfa.getMethodDataFlowGraph(sm);
		List sinks = null;
		if(g.containsNode(source))
			sinks = g.getSuccsOf(source);
		else
			sinks = new ArrayList();
		return sinks;
	}
*/	
	/** Returns an unmodifiable list of EquivalentValue wrapped Refs that sink
	  * flows from when method sm is called. */
/*	public List getSourcesOf(SootMethod sm, EquivalentValue sink)
	{
		ClassDataFlowAnalysis cdfa = getClassDataFlowAnalysis(sm.getDeclaringClass());
		MutableDirectedGraph g = cdfa.getMethodDataFlowGraph(sm);
		List sources = null;
		if(g.containsNode(sink))
			sources = g.getPredsOf(sink);
		else
			sources = new ArrayList();
		return sources;
	}
*/	
	// Returns an EquivalentValue wrapped Ref based on sfr
	// that is suitable for comparison to the nodes of a Data Flow Graph
	public static EquivalentValue getEquivalentValueFieldRef(SootMethod sm, SootField sf) { return getEquivalentValueFieldRef(sm, sf, null); }
	public static EquivalentValue getEquivalentValueFieldRef(SootMethod sm, SootField sf, Local realLocal)
	{
		if(sf.isStatic())
		{
			return new EquivalentValue( Jimple.v().newStaticFieldRef(sf.makeRef()) );
		}
		else
		{
			// Jimple.v().newThisRef(sf.getDeclaringClass().getType())
			if(sm.isConcrete() && !sm.isStatic() && sm.getDeclaringClass() == sf.getDeclaringClass() && realLocal == null)
			{
				JimpleLocal fakethis = new FakeJimpleLocal("fakethis", sf.getDeclaringClass().getType(), sm.retrieveActiveBody().getThisLocal());
				
				return new EquivalentValue( Jimple.v().newInstanceFieldRef(fakethis, sf.makeRef()) ); // fake thisLocal
			}
			else
			{
				// Pretends to be a this.<somefield> ref for a method without a body,
				// for a static method, or for an inner field
				JimpleLocal fakethis = new FakeJimpleLocal("fakethis", sf.getDeclaringClass().getType(), realLocal);
				
				return new EquivalentValue( Jimple.v().newInstanceFieldRef(fakethis, sf.makeRef()) ); // fake thisLocal
			}
		}
	}
	
	// Returns an EquivalentValue wrapped Ref for @parameter i
	// that is suitable for comparison to the nodes of a Data Flow Graph
	public static EquivalentValue getEquivalentValueParameterRef(SootMethod sm, int i)
	{
		return new EquivalentValue(new ParameterRef(sm.getParameterType(i), i));
	}
	
	// Returns an EquivalentValue wrapped Ref for the return value
	// that is suitable for comparison to the nodes of a Data Flow Graph
	public static EquivalentValue getEquivalentValueReturnRef(SootMethod sm)
	{
		return new EquivalentValue(new ParameterRef(sm.getReturnType(), -1));
	}
	
	// Returns an EquivalentValue wrapped ThisRef
	// that is suitable for comparison to the nodes of a Data Flow Graph
	public static EquivalentValue getEquivalentValueThisRef(SootMethod sm)
	{
		return new EquivalentValue(new ThisRef(sm.getDeclaringClass().getType()));
	}
	
	protected MutableDirectedGraph getInvokeDataFlowGraph(InvokeExpr ie, SootMethod context)
	{
		// get the data flow graph for each possible target of ie,
		// then combine them conservatively and return the result.
		SootMethodRef methodRef = ie.getMethodRef();
		return getMethodDataFlowGraph(methodRef.resolve(), context.getDeclaringClass().isApplicationClass());
	}
	
	public static void printDataFlowGraph(DirectedGraph g)
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
				Value v = ((EquivalentValue) sourcesIt.next()).getValue();
				if(v instanceof FieldRef)
				{
					FieldRef fr = (FieldRef) v;
					String name = fr.getFieldRef().name();
					lastnamelength = name.length();
					if(lastnamelength > sourcesnamelength)
						sourcesnamelength = lastnamelength;
					G.v().out.print(name);
				}
				else if(v instanceof ParameterRef)
				{
					ParameterRef pr = (ParameterRef) v;
					lastnamelength = 11;
					if(lastnamelength > sourcesnamelength)
						sourcesnamelength = lastnamelength;
					G.v().out.print("@parameter" + pr.getIndex());
				}
				else
				{
					String name = v.toString();
					lastnamelength = name.length();
					if(lastnamelength > sourcesnamelength)
						sourcesnamelength = lastnamelength;
					G.v().out.print(name);
				}
				if(sourcesIt.hasNext())
					G.v().out.print("\n      ");
			}
			for(int i = 0; i < sourcesnamelength - lastnamelength; i++)
				G.v().out.print(" ");
			G.v().out.println(" ] --> " + node.toString());
		}
	}
		
	public static void printGraphToDotFile(String filename, DirectedGraph graph, String graphname, boolean onePage)
	{
		int sequence=0;
		
		// this makes the node name unique
		nodecount = 0; // reset node counter first.
		Hashtable nodeindex = new Hashtable(graph.size());
		
		// file name is the method name + .dot
		DotGraph canvas = new DotGraph(filename);
		if (!onePage) {
			canvas.setPageSize(8.5, 11.0);
		}
		
		canvas.setNodeShape(DotGraphConstants.NODE_SHAPE_BOX);
		canvas.setGraphLabel(graphname);
		
		Iterator nodesIt = graph.iterator();
		while (nodesIt.hasNext())
		{
			Object node = nodesIt.next();
			String nodeName = getNodeName(node);
			canvas.drawNode(nodeName);
			canvas.getNode(nodeName).setLabel(getNodeLabel(node));
			Iterator succsIt = graph.getSuccsOf(node).iterator();
			
			while (succsIt.hasNext())
			{
				Object s= succsIt.next();
				canvas.drawEdge(nodeName, getNodeName(s));
			}
		}
		
		canvas.plot(filename + ".dot");
	}

	static int nodecount = 0;
	static Map nodeToNodeName = new HashMap();
	public static String getNodeName(Object o)
	{
		if(!nodeToNodeName.containsKey(o))
			nodeToNodeName.put(o, "N" + (nodecount++));
			
		return (String) nodeToNodeName.get(o);
	}
	
	public static String getNodeLabel(Object o)
	{
		Value node = ((EquivalentValue) o).getValue();
		if(node instanceof FieldRef)
		{
			FieldRef fr = (FieldRef) node;
			return fr.getField().getDeclaringClass().getShortName() + "." + fr.getFieldRef().name();
		}
		return node.toString();
	}
}

