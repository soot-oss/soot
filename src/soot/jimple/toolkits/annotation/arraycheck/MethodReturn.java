package soot.jimple.toolkits.annotation.arraycheck;

import soot.*;
import soot.jimple.*;

class MethodReturn
{
    private SootMethod m;
    public MethodReturn(SootMethod m)
    {
	this.m = m;
    }

    public SootMethod getMethod()
    {
	return m;
    }

    public Type getType()
    {
	return m.getReturnType();
    }

    public int hashCode()
    {
	return m.hashCode()+m.getParameterCount();
    }

    public boolean equals(Object other)
    {
	if (other instanceof MethodReturn)
	{
	    return m.equals( ((MethodReturn)other).getMethod() );
	}

	return false;
    }

    public String toString()
    {
    	return "["+m.getSignature()+" : R]";
    }
}
		
