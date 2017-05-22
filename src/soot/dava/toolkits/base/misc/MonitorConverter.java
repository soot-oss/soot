/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Jerome Miecznikowski
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package soot.dava.toolkits.base.misc;

import soot.*;
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

    public static MonitorConverter v() { return G.v().soot_dava_toolkits_base_misc_MonitorConverter(); }

    private final SootMethod v, enter, exit; 

    public void convert( DavaBody body)
    {
    	for (AugmentedStmt mas : body.get_MonitorFacts()) {
	    MonitorStmt ms = (MonitorStmt) mas.get_Stmt();

	    body.addToImportList("soot.dava.toolkits.base.DavaMonitor.DavaMonitor");
	    
	    ArrayList arg = new ArrayList();
	    arg.add( ms.getOp());

	    if (ms instanceof EnterMonitorStmt)
		mas.set_Stmt( new GInvokeStmt( new DVirtualInvokeExpr( new DStaticInvokeExpr( v.makeRef(), new ArrayList()), enter.makeRef(), arg, new HashSet<Object>())));
	    else
		mas.set_Stmt( new GInvokeStmt( new DVirtualInvokeExpr( new DStaticInvokeExpr( v.makeRef(), new ArrayList()), exit.makeRef(), arg, new HashSet<Object>())));
	}
    }
}
