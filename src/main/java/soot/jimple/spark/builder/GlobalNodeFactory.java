/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Ondrej Lhotak
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

package soot.jimple.spark.builder;

import soot.jimple.spark.pag.*;
import soot.*;
import soot.toolkits.scalar.Pair;

/**
 * Factory for nodes not specific to a given method.
 * 
 * @author Ondrej Lhotak
 */
public class GlobalNodeFactory {
	public GlobalNodeFactory(PAG pag) {
		this.pag = pag;
	}

	final public Node caseDefaultClassLoader() {
		AllocNode a = pag.makeAllocNode(PointsToAnalysis.DEFAULT_CLASS_LOADER,
				AnySubType.v(RefType.v("java.lang.ClassLoader")), null);
		VarNode v = pag.makeGlobalVarNode(PointsToAnalysis.DEFAULT_CLASS_LOADER_LOCAL,
				RefType.v("java.lang.ClassLoader"));
		pag.addEdge(a, v);
		return v;
	}

	final public Node caseMainClassNameString() {
		AllocNode a = pag.makeAllocNode(PointsToAnalysis.MAIN_CLASS_NAME_STRING, RefType.v("java.lang.String"), null);
		VarNode v = pag.makeGlobalVarNode(PointsToAnalysis.MAIN_CLASS_NAME_STRING_LOCAL, RefType.v("java.lang.String"));
		pag.addEdge(a, v);
		return v;
	}

	final public Node caseMainThreadGroup() {
		AllocNode threadGroupNode = pag.makeAllocNode(PointsToAnalysis.MAIN_THREAD_GROUP_NODE,
				RefType.v("java.lang.ThreadGroup"), null);
		VarNode threadGroupNodeLocal = pag.makeGlobalVarNode(PointsToAnalysis.MAIN_THREAD_GROUP_NODE_LOCAL,
				RefType.v("java.lang.ThreadGroup"));
		pag.addEdge(threadGroupNode, threadGroupNodeLocal);
		return threadGroupNodeLocal;
	}

	final public Node casePrivilegedActionException() {
		AllocNode a = pag.makeAllocNode(PointsToAnalysis.PRIVILEGED_ACTION_EXCEPTION,
				AnySubType.v(RefType.v("java.security.PrivilegedActionException")), null);
		VarNode v = pag.makeGlobalVarNode(PointsToAnalysis.PRIVILEGED_ACTION_EXCEPTION_LOCAL,
				RefType.v("java.security.PrivilegedActionException"));
		pag.addEdge(a, v);
		return v;
	}

	final public Node caseCanonicalPath() {
		AllocNode a = pag.makeAllocNode(PointsToAnalysis.CANONICAL_PATH, RefType.v("java.lang.String"), null);
		VarNode v = pag.makeGlobalVarNode(PointsToAnalysis.CANONICAL_PATH_LOCAL, RefType.v("java.lang.String"));
		pag.addEdge(a, v);
		return v;
	}

	final public Node caseMainThread() {
		AllocNode threadNode = pag.makeAllocNode(PointsToAnalysis.MAIN_THREAD_NODE, RefType.v("java.lang.Thread"),
				null);
		VarNode threadNodeLocal = pag.makeGlobalVarNode(PointsToAnalysis.MAIN_THREAD_NODE_LOCAL,
				RefType.v("java.lang.Thread"));
		pag.addEdge(threadNode, threadNodeLocal);
		return threadNodeLocal;
	}

	final public Node caseFinalizeQueue() {
		return pag.makeGlobalVarNode(PointsToAnalysis.FINALIZE_QUEUE, RefType.v("java.lang.Object"));
	}

	final public Node caseArgv() {
		AllocNode argv = pag.makeAllocNode(PointsToAnalysis.STRING_ARRAY_NODE,
				ArrayType.v(RefType.v("java.lang.String"), 1), null);
		VarNode sanl = pag.makeGlobalVarNode(PointsToAnalysis.STRING_ARRAY_NODE_LOCAL,
				ArrayType.v(RefType.v("java.lang.String"), 1));
		AllocNode stringNode = pag.makeAllocNode(PointsToAnalysis.STRING_NODE, RefType.v("java.lang.String"), null);
		VarNode stringNodeLocal = pag.makeGlobalVarNode(PointsToAnalysis.STRING_NODE_LOCAL,
				RefType.v("java.lang.String"));
		pag.addEdge(argv, sanl);
		pag.addEdge(stringNode, stringNodeLocal);
		pag.addEdge(stringNodeLocal, pag.makeFieldRefNode(sanl, ArrayElement.v()));
		return sanl;
	}

	final public Node caseNewInstance(VarNode cls) {
		if (cls instanceof ContextVarNode)
			cls = pag.findLocalVarNode(cls.getVariable());
		VarNode local = pag.makeGlobalVarNode(cls, RefType.v("java.lang.Object"));
		for (SootClass cl : Scene.v().dynamicClasses()) {
			AllocNode site = pag.makeAllocNode(new Pair<VarNode, SootClass>(cls, cl), cl.getType(), null);
			pag.addEdge(site, local);
		}
		return local;
	}

	public Node caseThrow() {
		VarNode ret = pag.makeGlobalVarNode(PointsToAnalysis.EXCEPTION_NODE, RefType.v("java.lang.Throwable"));
		ret.setInterProcTarget();
		ret.setInterProcSource();
		return ret;
	}
	/* End of public methods. */
	/* End of package methods. */

	protected PAG pag;
}
