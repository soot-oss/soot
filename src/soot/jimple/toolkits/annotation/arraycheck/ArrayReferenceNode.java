package soot.jimple.toolkits.annotation.arraycheck;

import soot.*;
import soot.jimple.*;

class ArrayReferenceNode
{
    private SootMethod m;
    private Local l;

    public ArrayReferenceNode(SootMethod method, Local local)
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
	return m.hashCode()+l.hashCode()+1;
    }

    public boolean equals(Object other)
    {
	if (other instanceof ArrayReferenceNode)
	{
	    ArrayReferenceNode another = (ArrayReferenceNode)other;
	    return m.equals(another.getMethod()) && l.equals(another.getLocal()) ;
	}
	
	return false;
    }

    public String toString()
    {
	return "["+m.getSignature()+" : "+l.toString()+"[ ]";
    }
}





