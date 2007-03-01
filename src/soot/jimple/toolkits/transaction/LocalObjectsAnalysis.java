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

	Map classToClassLocalObjectsAnalysis; // ClassLocalObjectsAnalysis for own class as scope
	
	public LocalObjectsAnalysis(DataFlowAnalysis dfa) //, boolean threadBased)
	{
		this.dfa = dfa;
		
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
	
	public boolean isObjectLocal(Value localOrRef, SootMethod sm)
	{
		ClassLocalObjectsAnalysis cloa = getClassLocalObjectsAnalysis(sm.getDeclaringClass());
		return cloa.isObjectLocal(localOrRef, sm);
	}
	
	public boolean isFieldLocal(SootField sf)
	{
		ClassLocalObjectsAnalysis cloa = getClassLocalObjectsAnalysis(sf.getDeclaringClass());
		return cloa.fieldIsLocal(sf);
	}
	
	public boolean hasNonLocalEffects(SootMethod context, InvokeExpr ie)
	{
		SootMethod target = ie.getMethodRef().resolve();
		MutableDirectedGraph dataFlowGraph = dfa.getMethodDataFlowGraph(target);
		if(ie instanceof StaticInvokeExpr)
		{
			return hasFieldTypeSourcesOrSinks(dataFlowGraph);
		}
		else if(ie instanceof InstanceInvokeExpr)
		{
			InstanceInvokeExpr iie = (InstanceInvokeExpr) ie;
			if( isObjectLocal(iie.getBase(), context) )
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
							if( !isFieldLocal(fr.getFieldRef().resolve()) )
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
								if( !isObjectLocal(ie.getArg(pr.getIndex()), context) )
									return true;
							}
						}
					}
				}
			}
			else
			{
				return hasFieldTypeSourcesOrSinks(dataFlowGraph);
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

