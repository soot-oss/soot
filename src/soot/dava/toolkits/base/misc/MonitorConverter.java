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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

import soot.G;
import soot.Modifier;
import soot.RefType;
import soot.Scene;
import soot.Singletons;
import soot.SootClass;
import soot.SootMethod;
import soot.VoidType;
import soot.dava.DavaBody;
import soot.dava.internal.asg.AugmentedStmt;
import soot.dava.internal.javaRep.DStaticInvokeExpr;
import soot.dava.internal.javaRep.DVirtualInvokeExpr;
import soot.grimp.internal.GInvokeStmt;
import soot.jimple.EnterMonitorStmt;
import soot.jimple.MonitorStmt;

public class MonitorConverter {
	public MonitorConverter(Singletons.Global g) {
		SootClass davaMonitor = new SootClass("soot.dava.toolkits.base.DavaMonitor.DavaMonitor", Modifier.PUBLIC);
		davaMonitor.setSuperclass(Scene.v().loadClassAndSupport("java.lang.Object"));

		LinkedList objectSingleton = new LinkedList();
		objectSingleton.add(RefType.v("java.lang.Object"));
		v = Scene.v().makeSootMethod("v", new LinkedList(),
				RefType.v("soot.dava.toolkits.base.DavaMonitor.DavaMonitor"), Modifier.PUBLIC | Modifier.STATIC);
		enter = Scene.v().makeSootMethod("enter", objectSingleton, VoidType.v(),
				Modifier.PUBLIC | Modifier.SYNCHRONIZED);
		exit = Scene.v().makeSootMethod("exit", objectSingleton, VoidType.v(), Modifier.PUBLIC | Modifier.SYNCHRONIZED);
		davaMonitor.addMethod(v);
		davaMonitor.addMethod(enter);
		davaMonitor.addMethod(exit);

		Scene.v().addClass(davaMonitor);
	}

	public static MonitorConverter v() {
		return G.v().soot_dava_toolkits_base_misc_MonitorConverter();
	}

	private final SootMethod v, enter, exit;

	public void convert(DavaBody body) {
		for (AugmentedStmt mas : body.get_MonitorFacts()) {
			MonitorStmt ms = (MonitorStmt) mas.get_Stmt();

			body.addToImportList("soot.dava.toolkits.base.DavaMonitor.DavaMonitor");

			ArrayList arg = new ArrayList();
			arg.add(ms.getOp());

			if (ms instanceof EnterMonitorStmt)
				mas.set_Stmt(new GInvokeStmt(new DVirtualInvokeExpr(new DStaticInvokeExpr(v.makeRef(), new ArrayList()),
						enter.makeRef(), arg, new HashSet<Object>())));
			else
				mas.set_Stmt(new GInvokeStmt(new DVirtualInvokeExpr(new DStaticInvokeExpr(v.makeRef(), new ArrayList()),
						exit.makeRef(), arg, new HashSet<Object>())));
		}
	}
}
