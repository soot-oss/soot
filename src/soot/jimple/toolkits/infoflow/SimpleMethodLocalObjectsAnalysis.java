package soot.jimple.toolkits.infoflow;

import soot.*;
import java.util.*;
import soot.toolkits.graph.*;

// SimpleMethodLocalObjectsAnalysis written by Richard L. Halpert, 2007-02-23
// Finds objects that are local to the scope of the LocalObjectsScopeAnalysis
// that is provided.
// This is a specialized version of SimpleMethodInfoFlowAnalysis, in which the data
// source is the abstract "shared" data source.

public class SimpleMethodLocalObjectsAnalysis extends SimpleMethodInfoFlowAnalysis
{
	public static int mlocounter = 0;

	public SimpleMethodLocalObjectsAnalysis(UnitGraph g, ClassLocalObjectsAnalysis cloa, InfoFlowAnalysis dfa)
	{
		super(g, dfa, true, true); // special version doesn't run analysis yet
		
		mlocounter++;
		
		printMessages = false;

		SootMethod method = g.getBody().getMethod();
		
		AbstractDataSource sharedDataSource = new AbstractDataSource(new String("SHARED"));
		
		// Add a source for every parameter that is shared
		for(int i = 0; i < method.getParameterCount(); i++) // no need to worry about return value... 
		{
			EquivalentValue paramEqVal = dfa.getNodeForParameterRef(method, i);
			if(!cloa.parameterIsLocal(method, paramEqVal))
			{
				addToEntryInitialFlow(sharedDataSource, paramEqVal.getValue());
				addToNewInitialFlow(sharedDataSource, paramEqVal.getValue());
			}
		}
		
		// Add a source for every field that is shared (DOES THIS INCLUDE GLOBALS?)
		for(Iterator it = cloa.getSharedFields().iterator(); it.hasNext();)
		{
			SootField sf = (SootField) it.next();
			EquivalentValue fieldRefEqVal = dfa.getNodeForFieldRef(method, sf);
			addToEntryInitialFlow(sharedDataSource, fieldRefEqVal.getValue());
			addToNewInitialFlow(sharedDataSource, fieldRefEqVal.getValue());
		}
		
		if(printMessages)
			G.v().out.println("----- STARTING SHARED/LOCAL ANALYSIS FOR " + g.getBody().getMethod() + " -----");
		doFlowInsensitiveAnalysis();
		if(printMessages)
			G.v().out.println("----- ENDING   SHARED/LOCAL ANALYSIS FOR " + g.getBody().getMethod() + " -----");
	}
	
	public SimpleMethodLocalObjectsAnalysis(UnitGraph g, CallLocalityContext context, InfoFlowAnalysis dfa)
	{
		super(g, dfa, true, true); // special version doesn't run analysis yet
		
		mlocounter++;

		printMessages = false;

		SootMethod method = g.getBody().getMethod();
		
		AbstractDataSource sharedDataSource = new AbstractDataSource(new String("SHARED"));
		
		List sharedRefs = context.getSharedRefs();
		Iterator sharedRefEqValIt = sharedRefs.iterator(); // returns a list of (correctly structured) EquivalentValue wrapped refs that should be treated as shared
		while(sharedRefEqValIt.hasNext())
		{
			EquivalentValue refEqVal = (EquivalentValue) sharedRefEqValIt.next();
			addToEntryInitialFlow(sharedDataSource, refEqVal.getValue());
			addToNewInitialFlow(sharedDataSource, refEqVal.getValue());
		}
		
		if(printMessages)
		{
			G.v().out.println("----- STARTING SHARED/LOCAL ANALYSIS FOR " + g.getBody().getMethod() + " -----");
			G.v().out.print("      " + context.toString().replaceAll("\n","\n      "));
			G.v().out.println("found " + sharedRefs.size() + " shared refs in context.");
		}	
		doFlowInsensitiveAnalysis();
		if(printMessages)
			G.v().out.println("----- ENDING   SHARED/LOCAL ANALYSIS FOR " + g.getBody().getMethod() + " -----");
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
	
	// 
	public boolean isObjectLocal(Value local) // to this analysis of this method (which depends on context)
	{
		EquivalentValue source = new EquivalentValue(new AbstractDataSource(new String("SHARED")));
		if(infoFlowGraph.containsNode(source))
		{
			List sinks = infoFlowGraph.getSuccsOf(source);
			if(printMessages)
				G.v().out.println("      Requested value " + local + " is " + ( !sinks.contains(new EquivalentValue(local)) ? "Local" : "Shared" ) + " in " + sm + " ");
			return !sinks.contains(new EquivalentValue(local));
		}
		else
		{
			if(printMessages)
				G.v().out.println("      Requested value " + local + " is Local (LIKE ALL VALUES) in " + sm + " ");
			return true; // no shared data in this method
		}
	}
}

