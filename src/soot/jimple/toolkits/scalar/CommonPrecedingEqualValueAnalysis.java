package soot.jimple.toolkits.scalar;

import soot.*;
import java.util.*;
import soot.toolkits.graph.*;
import soot.toolkits.scalar.*;
import soot.jimple.*;

// EqualLocalsAnalysis written by Richard L. Halpert, 2006-12-04
// Finds all values at the given statement from which all of the listed uses
// come.

public class CommonPrecedingEqualValueAnalysis extends BackwardFlowAnalysis
{
	Map unitToAliasSet;
	Stmt s;
	
	public CommonPrecedingEqualValueAnalysis(UnitGraph g)
	{
		super(g);
		
		unitToAliasSet = null;
		s = null;
		
		// analysis is done on-demand, not now
	}

	/** Returns a list of EquivalentLocals that must always be equal to l at s */
	public List getCommonAncestorValuesOf(Map unitToAliasSet, Stmt s)
	{
		this.unitToAliasSet = unitToAliasSet;
		this.s = s;

		doAnalysis();

		FlowSet fs = (FlowSet) getFlowAfter(s);
		List ancestorList = new ArrayList(fs.size());
		for (Object o : fs)
			ancestorList.add(o);

		return ancestorList;
	}

	protected void merge(Object in1, Object in2, Object out)
	{
		FlowSet inSet1 = (FlowSet) in1;
		FlowSet inSet2 = (FlowSet) in2;
		FlowSet outSet = (FlowSet) out;
		
		inSet1.intersection(inSet2, outSet);
//		inSet1.union(inSet2, outSet);
	}
	
	protected void flowThrough(Object inValue, Object unit,
			Object outValue)
	{
		FlowSet in  = (FlowSet) inValue;
		FlowSet out = (FlowSet) outValue;
		Stmt stmt = (Stmt) unit;
		
		in.copy(out);

		// get list of definitions at this unit
		List<EquivalentValue> newDefs = new ArrayList<EquivalentValue>();
		Iterator newDefBoxesIt = stmt.getDefBoxes().iterator();
		while( newDefBoxesIt.hasNext() )
		{
			newDefs.add( new EquivalentValue( ((ValueBox) newDefBoxesIt.next()).getValue()) );
		}
		
		// If the local of interest was defined in this statement, then we must
		// generate a new list of aliases to it starting here
		if( unitToAliasSet.keySet().contains(stmt) )
		{
			out.clear();
			List aliases = (List) unitToAliasSet.get(stmt);
			Iterator aliasIt = aliases.iterator();
			while(aliasIt.hasNext())
				out.add( aliasIt.next() );
		}
		else if( stmt instanceof DefinitionStmt )
		{
			Iterator<EquivalentValue> newDefsIt = newDefs.iterator();
			while(newDefsIt.hasNext())
				out.remove( newDefsIt.next() );
			// to be smarter, we could also add the right side to the list of aliases...
		}

//		G.v().out.println(stmt + " HAS ALIASES in" + in + " out" + out);
	}
	
	protected void copy(Object source, Object dest)
	{
		
		FlowSet sourceSet = (FlowSet) source;
		FlowSet destSet   = (FlowSet) dest;
		
		sourceSet.copy(destSet);
		
	}
	
	protected Object entryInitialFlow()
	{
		return new ArraySparseSet(); // should be a full set, not an empty one
	}
	
	protected Object newInitialFlow()
	{
		return new ArraySparseSet(); // should be a full set, not an empty one
	}	
}

