/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-2013 Eric Bodden and others
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
package soot.jimple.toolkits.ide.icfg;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import soot.Body;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.toolkits.graph.DirectedGraph;


/**
 * Same as {@link JimpleBasedInterproceduralCFG} but based on inverted unit graphs.
 * This should be used for backward analyses.
 */
public class BackwardsInterproceduralCFG implements BiDiInterproceduralCFG<Unit,SootMethod> {
	
	protected final BiDiInterproceduralCFG<Unit,SootMethod> delegate;
	
	public BackwardsInterproceduralCFG(BiDiInterproceduralCFG<Unit,SootMethod> fwICFG) {
		delegate = fwICFG;		
	}

	//swapped
	public List<Unit> getSuccsOf(Unit n) {
		return delegate.getPredsOf(n);
	}

	//swapped
	public Collection<Unit> getStartPointsOf(SootMethod m) {
		return delegate.getEndPointsOf(m);
	}
	
	//swapped
	public List<Unit> getReturnSitesOfCallAt(Unit n) {
		return delegate.getPredsOfCallAt(n);
	}

	//swapped
	public boolean isExitStmt(Unit stmt) {
		return delegate.isStartPoint(stmt);
	}

	//swapped
	public boolean isStartPoint(Unit stmt) {
		return delegate.isExitStmt(stmt);
	}
	
	//swapped
	public Set<Unit> allNonCallStartNodes() {
		return delegate.allNonCallEndNodes();
	}
	
	//swapped
	public List<Unit> getPredsOf(Unit u) {
		return delegate.getSuccsOf(u);
	}

	//swapped
	public Collection<Unit> getEndPointsOf(SootMethod m) {
		return delegate.getStartPointsOf(m);
	}

	//swapped
	public List<Unit> getPredsOfCallAt(Unit u) {
		return delegate.getSuccsOf(u);
	}

	//swapped
	public Set<Unit> allNonCallEndNodes() {
		return delegate.allNonCallStartNodes();
	}

	//same
	public SootMethod getMethodOf(Unit n) {
		return delegate.getMethodOf(n);
	}

	//same
	public Set<SootMethod> getCalleesOfCallAt(Unit n) {
		return delegate.getCalleesOfCallAt(n);
	}

	//same
	public Set<Unit> getCallersOf(SootMethod m) {
		return delegate.getCallersOf(m);
	}

	//same
	public Set<Unit> getCallsFromWithin(SootMethod m) {
		return delegate.getCallsFromWithin(m);
	}

	//same
	public boolean isCallStmt(Unit stmt) {
		return delegate.isCallStmt(stmt);
	}

	//same
	public DirectedGraph<Unit> getOrCreateUnitGraph(Body body) {
		return delegate.getOrCreateUnitGraph(body);
	}

	//same
	public List<Value> getParameterRefs(SootMethod m) {
		return delegate.getParameterRefs(m);
	}

	public boolean isFallThroughSuccessor(Unit stmt, Unit succ) {
		throw new UnsupportedOperationException("not implemented because semantics unclear");
	}

	public boolean isBranchTarget(Unit stmt, Unit succ) {
		throw new UnsupportedOperationException("not implemented because semantics unclear");
	}

}
