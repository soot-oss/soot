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
    public ThrowNullConverter( Singletons.Global g ) {}
    public static ThrowNullConverter v() { return G.v().ThrowNullConverter(); }

    private RefType npeRef = RefType.v( Scene.v().loadClassAndSupport( "java.lang.NullPointerException"));

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
