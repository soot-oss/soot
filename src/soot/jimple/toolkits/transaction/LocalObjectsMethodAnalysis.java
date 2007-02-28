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

// LocalObjectsMethodAnalysis written by Richard L. Halpert, 2007-02-23
// Finds objects that are local to the scope of the LocalObjectsScopeAnalysis
// that is provided.
// This is a specialized version of MethodDataFlowAnalysis, in which the data
// source is the abstract "shared" data source.

public class LocalObjectsMethodAnalysis extends MethodDataFlowAnalysis
{
	public LocalObjectsMethodAnalysis(UnitGraph g, LocalObjectsScopeAnalysis losa, DataFlowAnalysis dfa)
	{
		super(g, dfa, true, true); // special version doesn't run analysis yet
				
		printMessages = false;

		SootMethod method = g.getBody().getMethod();
		
		AbstractDataSource sharedDataSource = new AbstractDataSource("SHARED");
		
		// Add a source for every parameter that is shared
		for(int i = 0; i < method.getParameterCount(); i++) // no need to worry about return value... if it shares things, it doesn't matter, because the method exits right then anyways
		{
			EquivalentValue paramEqVal = dfa.getEquivalentValueParameterRef(method, i);
			if(!losa.parameterIsLocal(method, paramEqVal))
			{
				addToEntryInitialFlow(sharedDataSource, paramEqVal.getValue());
				addToNewInitialFlow(sharedDataSource, paramEqVal.getValue());
			}
		}
		
		// Add a source for every field that is shared
		for(Iterator it = losa.getSharedFields().iterator(); it.hasNext();)
		{
			SootField sf = (SootField) it.next();
			EquivalentValue fieldRefEqVal = dfa.getEquivalentValueFieldRef(method, sf);
			addToEntryInitialFlow(sharedDataSource, fieldRefEqVal.getValue());
			addToNewInitialFlow(sharedDataSource, fieldRefEqVal.getValue());
		}
		
		if(printMessages)
			G.v().out.println("----- STARTING SHARED/LOCAL ANALYSIS FOR " + g.getBody().getMethod() + " -----");
		doFlowInsensitiveAnalysis();
		if(printMessages)
			G.v().out.println("-----   ENDING SHARED/LOCAL ANALYSIS FOR " + g.getBody().getMethod() + " -----");
	}
	
	// Interesting sources are summarized (and possibly printed)
	public boolean isInterestingSource(Value source)
	{
		return (source instanceof AbstractDataSource);
	}

	// Interesting sinks are possibly printed
	public boolean isInterestingSink(Value sink)
	{
		return true; //(sink instanceof Local); // we're interested in all values
	}
}

