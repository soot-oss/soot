package soot.jimple.toolkits.annotation.arraycheck;

import soot.*;
import soot.jimple.*;

class MethodLocal
{
    private SootMethod m;
    private Local l;

    public MethodLocal(SootMethod method, Local local)
    {
	m = method;
	l = local;
    }

    public SootMethod getMethod()
    {
	return m;
    }

    public Local getLocal()
    {
	return l;
    }

    public int hashCode()
    {
	return m.hashCode()+l.hashCode();
    }

    public boolean equals(Object other)
    {
	if (other instanceof MethodLocal)
	{
	    MethodLocal another = (MethodLocal)other;
	    return m.equals(another.getMethod()) && l.equals(another.getLocal()) ;
	}
	
	return false;
    }

    public String toString()
    {
	return "["+m.getSignature()+" : "+l.toString()+"]";
    }
}





