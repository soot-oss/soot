package soot.dava.toolkits.base.misc;

import soot.*;
import soot.util.*;
import java.util.*;
import soot.dava.*;
import soot.jimple.*;
import soot.grimp.internal.*;
import soot.dava.internal.asg.*;

public class MonitorConverter
{
    private static MonitorConverter instance = new MonitorConverter();

    public static MonitorConverter v()
    {
	return instance;
    }

    private SootClass davaMonitor;
    private SootMethod v, enter, exit;

    private MonitorConverter()
    {
	SootClass davaMonitor = new SootClass( "DavaMonitor");

	ArrayList parameterTypes = new ArrayList();
	parameterTypes.add( RefType.v());


	SootMethod 
	    v     = new SootMethod( "v", new ArrayList(), RefType.v()),
	    enter = new SootMethod( "enter", parameterTypes, VoidType.v()),
	    exit  = new SootMethod( "exit", parameterTypes, VoidType.v());

	davaMonitor.addMethod( v);
	davaMonitor.addMethod( enter);
	davaMonitor.addMethod( exit);
    }

    public void convert( DavaBody body)
    {
	Iterator mfit = body.get_MonitorFacts().iterator();
	while (mfit.hasNext()) {
	    AugmentedStmt mas = (AugmentedStmt) mfit.next();
	    MonitorStmt ms = (MonitorStmt) mas.get_Stmt();

	    body.addPackage( "soot.dava.toolkits.base.misc");

	    ArrayList args = new ArrayList();
	    args.add( ms.getOp());
	    
	    GInvokeStmt gis = null;

	    if (ms instanceof EnterMonitorStmt)
		gis = new GInvokeStmt( new GVirtualInvokeExpr( new GStaticInvokeExpr( v, new ArrayList()), enter, args));
	    else 
		gis = new GInvokeStmt( new GVirtualInvokeExpr( new GStaticInvokeExpr( v, new ArrayList()), exit, args));
	
	    mas.set_Stmt( gis);
	}
    }
}
