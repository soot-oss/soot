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
	Map classToClassDataFlowAnalysis;
	
	public DataFlowAnalysis()
	{
		 classToClassDataFlowAnalysis = new HashMap();
		 
		 doAnalysis();
	}
	
	private void doAnalysis()
	{
    	Iterator appClassesIt = Scene.v().getApplicationClasses().iterator();
    	while (appClassesIt.hasNext()) 
    	{
    	    SootClass appClass = (SootClass) appClassesIt.next();
    	    classToClassDataFlowAnalysis.put(appClass, new ClassDataFlowAnalysis(appClass, this));
		}
	}
		
	// Returns a list of EquivalentValue wrapped Refs that source flows to when method sm is called
	public List getSinksOf(SootMethod sm, EquivalentValue source)
	{
		ClassDataFlowAnalysis cdfa = getClassDataFlowAnalysis(sm.getDeclaringClass());
		MutableDirectedGraph g = cdfa.getDataFlowGraphOf(sm);
		List sinks = null;
		if(g.containsNode(source))
			sinks = g.getSuccsOf(source);
		else
			sinks = new ArrayList();
		return sinks;
	}
	
	// Returns a list of EquivalentValue wrapped Refs that sink flows from when method sm is called
	public List getSourcesOf(SootMethod sm, EquivalentValue sink)
	{
		ClassDataFlowAnalysis cdfa = getClassDataFlowAnalysis(sm.getDeclaringClass());
		MutableDirectedGraph g = cdfa.getDataFlowGraphOf(sm);
		List sources = null;
		if(g.containsNode(sink))
			sources = g.getPredsOf(sink);
		else
			sources = new ArrayList();
		return sources;
	}
	
	public ClassDataFlowAnalysis getClassDataFlowAnalysis(SootClass sc)
	{
		if(!classToClassDataFlowAnalysis.containsKey(sc)) // only application classes are precomputed
		{
			// Create the needed flow analysis object
			ClassDataFlowAnalysis cdfa = new ClassDataFlowAnalysis(sc, this);
			
			// Put the preliminary flow-insensitive results here in case they
			// are needed by the flow-sensitive version.  This method will be
			// reentrant if any method we are analyzing is reentrant, so we
			// must do this to prevent an infinite recursive loop.
			classToClassDataFlowAnalysis.put(sc, cdfa);
			
			// Now calculate the flow-sensitive version.  If this classes methods
			// are reentrant, it will call this method and receive the flow
			// insensitive version that is already cached.
			cdfa.doFlowSensitiveAnalysis();
		}
		return (ClassDataFlowAnalysis) classToClassDataFlowAnalysis.get(sc);
	}
	
	protected MutableDirectedGraph getDataFlowGraphOf(SootMethod sm)
	{
		ClassDataFlowAnalysis cdfa = getClassDataFlowAnalysis(sm.getDeclaringClass());
		return cdfa.getDataFlowGraphOf(sm);
	}
	
/*
	protected MutableDirectedGraph getDataFlowGraphOf(InvokeExpr ie)
	{
		// get the data flow graph for each possible target of ie,
		// then combine them conservatively and return the result.
	}
*/
	
	// Returns an EquivalentValue wrapped Ref based on sfr
	// that is suitable for comparison to the nodes of a Data Flow Graph
	public EquivalentValue getEquivalentValueFieldRef(SootMethod sm, SootField sf)
	{
		if(sf.isStatic())
		{
			return new EquivalentValue( Jimple.v().newStaticFieldRef(sf.makeRef()) );
		}
		else
		{
			// Jimple.v().newThisRef(sf.getDeclaringClass().getType())
			return new EquivalentValue( Jimple.v().newInstanceFieldRef(sm.retrieveActiveBody().getThisLocal(), sf.makeRef()) );
		}
	}
	
	// Returns an EquivalentValue wrapped Ref for @parameter i
	// that is suitable for comparison to the nodes of a Data Flow Graph
	public EquivalentValue getEquivalentValueParameterRef(SootMethod sm, int i)
	{
		return new EquivalentValue(new ParameterRef(sm.getParameterType(i), i));
	}
	
	// Returns an EquivalentValue wrapped Ref for the return value
	// that is suitable for comparison to the nodes of a Data Flow Graph
	public EquivalentValue getEquivalentValueReturnRef(SootMethod sm)
	{
		return new EquivalentValue(new ParameterRef(sm.getReturnType(), -1));
	}
}

