package soot.dava.internal.javaRep;

import soot.*;
import soot.jimple.*;

public class DThisRef extends ThisRef
{
    public DThisRef(RefType thisType)
    {
	super( thisType);
    }
    
    public String toString()
    {
        return "this: "+ getType();
    }

    public String toBriefString()
    {
        return "this";
    }

    public Object clone()
    {
	return new DThisRef( (RefType) getType());
    }
}
