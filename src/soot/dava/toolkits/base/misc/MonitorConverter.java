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
    public MonitorConverter( Singletons.Global g )
    {
	SootClass davaMonitor = Scene.v().loadClassAndSupport( "soot.dava.toolkits.base.DavaMonitor.DavaMonitor");

	v = davaMonitor.getMethodByName( "v");
	enter = davaMonitor.getMethodByName( "enter");
	exit = davaMonitor.getMethodByName( "exit");
    }

    public static MonitorConverter v() { return G.v().MonitorConverter(); }

    private SootMethod v, enter, exit; 

    public void convert( DavaBody body)
    {
	Iterator mfit = body.get_MonitorFacts().iterator();
	while (mfit.hasNext()) {
	    AugmentedStmt mas = (AugmentedStmt) mfit.next();
	    MonitorStmt ms = (MonitorStmt) mas.get_Stmt();

	    body.addPackage( "soot.dava.toolkits.base.DavaMonitor");
	    
	    ArrayList arg = new ArrayList();
	    arg.add( ms.getOp());

	    if (ms instanceof EnterMonitorStmt)
		mas.set_Stmt( new GInvokeStmt( new GVirtualInvokeExpr( new GStaticInvokeExpr( v, new ArrayList()), enter, arg)));
	    else
		mas.set_Stmt( new GInvokeStmt( new GVirtualInvokeExpr( new GStaticInvokeExpr( v, new ArrayList()), exit, arg)));
	}
    }
}
