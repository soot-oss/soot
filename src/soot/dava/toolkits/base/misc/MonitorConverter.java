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
    private MonitorConverter() {}
    private static MonitorConverter instance = new MonitorConverter();

    public static MonitorConverter v()
    {
	return instance;
    }

    public void convert( DavaBody body)
    {
	ArrayList parameterTypes = new ArrayList();
	parameterTypes.add( RefType.v());

	SootClass davaMonitor = new SootClass( "DavaMonitor");

	SootMethod 
	    v     = new SootMethod( "v", new ArrayList(), RefType.v()),
	    enter = new SootMethod( "enter", parameterTypes, VoidType.v()),
	    exit  = new SootMethod( "exit", parameterTypes, VoidType.v());

	davaMonitor.addMethod( v);
	davaMonitor.addMethod( enter);
	davaMonitor.addMethod( exit);

	Iterator mfit = body.get_MonitorFacts().iterator();
	while (mfit.hasNext()) {
	    AugmentedStmt mas = (AugmentedStmt) mfit.next();
	    MonitorStmt ms = (MonitorStmt) mas.get_Stmt();

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
