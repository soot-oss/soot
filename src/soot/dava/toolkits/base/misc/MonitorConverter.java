package soot.dava.toolkits.base.misc;

import soot.*;
import soot.util.*;
import java.util.*;
import soot.dava.*;
import soot.jimple.*;
import soot.grimp.internal.*;
import soot.dava.internal.asg.*;
import soot.dava.internal.javaRep.*;

public class MonitorConverter
{
    public MonitorConverter( Singletons.Global g )
    {
	SootClass davaMonitor = new SootClass(
                "soot.dava.toolkits.base.DavaMonitor.DavaMonitor",
                Modifier.PUBLIC );
        davaMonitor.setSuperclass(
                Scene.v().loadClassAndSupport("java.lang.Object"));

        LinkedList objectSingleton = new LinkedList();
        objectSingleton.add( RefType.v("java.lang.Object") );
	v = new SootMethod(
                "v",
                new LinkedList(),
                RefType.v("soot.dava.toolkits.base.DavaMonitor.DavaMonitor"),
                Modifier.PUBLIC | Modifier.STATIC );
	enter = new SootMethod(
                "enter",
                objectSingleton,
                VoidType.v(),
                Modifier.PUBLIC | Modifier.SYNCHRONIZED );
	exit = new SootMethod(
                "exit",
                objectSingleton,
                VoidType.v(),
                Modifier.PUBLIC | Modifier.SYNCHRONIZED );
        davaMonitor.addMethod( v );
        davaMonitor.addMethod( enter );
        davaMonitor.addMethod( exit );

        Scene.v().addClass( davaMonitor );
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
		mas.set_Stmt( new GInvokeStmt( new DVirtualInvokeExpr( new DStaticInvokeExpr( v, new ArrayList()), enter, arg, new HashSet())));
	    else
		mas.set_Stmt( new GInvokeStmt( new DVirtualInvokeExpr( new DStaticInvokeExpr( v, new ArrayList()), exit, arg, new HashSet())));
	}
    }
}
