package soot.dava.internal.javaRep;

import soot.*;
import java.util.*;
import soot.grimp.internal.*;

public class DIdentityStmt extends GIdentityStmt
{
    public DIdentityStmt(Value local, Value identityValue)
    {
        super( local, identityValue);
    }

    protected String toString(boolean isBrief, Map stmtToName, String indentation)
    {
        if (isBrief) 
	    return indentation + ((ToBriefString) getLeftOpBox().getValue()).toBriefString() + " = " + 
		((ToBriefString) getRightOpBox().getValue()).toBriefString();
        else
            return indentation + getLeftOpBox().getValue().toString() + " = " + getRightOpBox().getValue().toString();
    }    
}
