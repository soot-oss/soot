package soot.dava.toolkits.base.misc;

import soot.*;
import soot.util.*;
import java.util.*;
import soot.dava.*;
import soot.jimple.*;
import soot.grimp.internal.*;
import soot.dava.internal.asg.*;
import soot.dava.internal.javaRep.*;

public class ThrowNullConverter
{
    private ThrowNullConverter() 
    {
	String npe = "soot.NullPointerException";

	if (Scene.v().getClasses().contains( npe) == false)
	    Scene.v().addClass( new SootClass( npe));

	npeRef = RefType.v( npe);
    }
    
    private static ThrowNullConverter instance = new ThrowNullConverter();

    public static ThrowNullConverter v()
    {
	return instance;
    }

    private SootClass nullPointerException;
    private RefType npeRef;

    public void convert( DavaBody body)
    {
	Iterator it = body.getUnits().iterator();
	while (it.hasNext()) {
	    Unit u = (Unit) it.next();

	    if (u instanceof ThrowStmt) {
		ValueBox opBox = ((ThrowStmt) u).getOpBox();
		Value op = opBox.getValue();

		if (op.getType() instanceof NullType)
		    opBox.setValue( new DNewInvokeExpr( npeRef, null, new ArrayList()));
	    }
	}
    }
}
