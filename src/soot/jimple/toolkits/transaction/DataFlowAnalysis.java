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
// Constructs data flow tables for each method of every application class.
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
    	    classToClassDataFlowAnalysis.put(appClass, new ClassDataFlowAnalysis(appClass));
		}
	}
	
	// Returns a list of Refs that r flows to when method sm is called
	public List flowsTo(SootMethod sm, Ref source)
	{
		ClassDataFlowAnalysis cdfa = (ClassDataFlowAnalysis) classToClassDataFlowAnalysis.get(sm.getDeclaringClass());
		DirectedGraph g = cdfa.getDataFlowGraphOf(sm);
		List sinksEqVal = g.getPredsOf(new EquivalentValue(source));
		List sinks = new ArrayList();
		Iterator it = sinksEqVal.iterator();
		while(it.hasNext())
		{
			Ref sink = (Ref) ((EquivalentValue) it.next()).getValue();
			sinks.add(sink);
		}
		return sinks;
	}
	
	// Returns a list of Refs that r may flow to from InvokeExpr ie
	public List flowsTo(InvokeExpr ie, Ref source)
	{
		// TODO: make this work!
		return new ArrayList();
	}
}

