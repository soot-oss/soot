package soot.jimple.toolkits.annotation.arraycheck;

import soot.*;
import soot.jimple.*;

class MethodParameter
{
    private SootMethod m;
    private int param;

    public MethodParameter(SootMethod m, int i)
    {
	this.m = m;
	this.param = i;
    }

    public Type getType()
    {
	return m.getParameterType(param);
    }

    public int hashCode()
    {
	return m.hashCode()+param;
    }

    public SootMethod getMethod()
    {
	return m;
    }

    public int getIndex()
    {
	return param;
    }

    public boolean equals(Object other)
    {
	if (other instanceof MethodParameter)
	{
	    MethodParameter another = (MethodParameter)other;
	    
	    return (m.equals(another.getMethod()) && param == another.getIndex());
	}
	
	return false;
    }

    public String toString()
    {
        return "["+m.getSignature()+" : P"+param+"]";
    }
}















