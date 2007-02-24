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
// Finds objects that are local to the given scope.
// Begins by finding objects created in the given scope.  Then, creates lists
// of constraints for each of these objects to truly be local.

public class LocalObjectsMethodAnalysis extends ForwardFlowAnalysis
{
	DataFlowAnalysis dfa;
	
	public LocalObjectsMethodAnalysis(UnitGraph g, DataFlowAnalysis dfa)
	{
		super(g);
		this.dfa = dfa;
		
		doAnalysis();
	}
	
    protected void merge(Object in1, Object in2, Object out)
    {
	    FlowSet inSet1 = (FlowSet) in1;
	    FlowSet inSet2 = (FlowSet) in2;
	    FlowSet outSet = (FlowSet) out;

        inSet1.intersection(inSet2, outSet);
    }

    protected void copy(Object source, Object dest)
    {
        FlowSet sourceIn = (FlowSet)source;
        FlowSet destOut = (FlowSet)dest;
        
        sourceIn.copy(destOut);
    }
   
    protected void flowThrough(Object inValue, Object unit,
            Object outValue)
    {
		FlowSet in  = (FlowSet) inValue;
		FlowSet out = (FlowSet) outValue;
		Stmt stmt = (Stmt) unit;
		
		in.copy(out);
    }

	protected Object entryInitialFlow()
	{
		return new ArraySparseSet();
	}
	
	protected Object newInitialFlow()
	{
		return new ArraySparseSet();
	}	
}

