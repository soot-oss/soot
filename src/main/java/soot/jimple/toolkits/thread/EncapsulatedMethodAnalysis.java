package soot.jimple.toolkits.thread;

import soot.*;
import java.util.*;
import soot.toolkits.graph.*;
import soot.jimple.*;

// EncapsulatedMethodAnalysis written by Richard L. Halpert, 2006-12-26

public class EncapsulatedMethodAnalysis // extends ForwardFlowAnalysis
{
	boolean isMethodPure;
	boolean isMethodConditionallyPure;
	
	public EncapsulatedMethodAnalysis(UnitGraph g)
	{		
		isMethodPure = true; // innocent until proven guilty :-)
		isMethodConditionallyPure = true;
		
		// Check if accesses any static object
		Iterator stmtIt = g.iterator();
		while(stmtIt.hasNext())
		{
			Stmt s = (Stmt) stmtIt.next();
			if(s.containsFieldRef())
			{
				FieldRef ref = s.getFieldRef();
				if( (ref instanceof StaticFieldRef) && 
				    (Type.toMachineType(((StaticFieldRef) ref).getType()) instanceof RefLikeType) )
				{
					isMethodPure = false; // kills purity
					isMethodConditionallyPure = false; // kills conditional purity
					return;
				}
			}
		}
		
		// Check if takes any object parameters
		Iterator paramTypesIt = g.getBody().getMethod().getParameterTypes().iterator();
		while(paramTypesIt.hasNext())
		{
			Type paramType = (Type) paramTypesIt.next();
			if( Type.toMachineType(paramType) != IntType.v() )
			{
				isMethodPure = false; // kills purity
				return;
			}
		}
		
		// If neither of the above, it may be object-pure
		//   if this is an <init> function, it's definitely object-pure
		//   if all called functions in the class may be object-pure, then they all are object-pure
		
		// This is conservative... many "may-be-pure" functions can be proven pure if we track what fields are object-pure
		// Also, if we do this, we should be able to track what fields refer to object-local objects, which will allow
		// lock assignment to work when inner objects are read/written (because they can be ignored for read/write sets).
	}
	
	public boolean isPure()
	{
		return isMethodPure;
	}
	
	public boolean isConditionallyPure()
	{
		return isMethodConditionallyPure;
	}
/*
	protected void merge(Object in1, Object in2, Object out)
	{
		FlowSet inSet1 = (FlowSet) in1;
		FlowSet inSet2 = (FlowSet) in2;
		FlowSet outSet = (FlowSet) out;
		
		inSet1.intersection(inSet2, outSet);
	}

	protected void flowThrough(Object inValue, Object unit, Object outValue)
	{
		FlowSet in  = (FlowSet) inValue;
		FlowSet out = (FlowSet) outValue;
		Stmt stmt = (Stmt) unit;
		
		in.copy(out);
	}
	
	protected void copy(Object source, Object dest)
	{
		
		FlowSet sourceSet = (FlowSet) source;
		FlowSet destSet   = (FlowSet) dest;
		
		sourceSet.copy(destSet);		
	}
	
	protected Object entryInitialFlow()
	{
		return new ArraySparseSet();
	}
	
	protected Object newInitialFlow()
	{
		return new ArraySparseSet();
	}	
//*/
}

