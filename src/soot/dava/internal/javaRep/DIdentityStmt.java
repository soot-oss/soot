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

    public void toString( UnitPrinter up ) {
        getLeftOpBox().toString(up);
        up.literal(" := ");
        getRightOpBox().toString(up);
    }

    public String toString()
    {
        return getLeftOpBox().getValue().toString() + " = " + getRightOpBox().getValue().toString();
    }    
}
