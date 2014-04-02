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
	@Override
	public List<Unit> getSuccsOf(Unit n) {
		return delegate.getPredsOf(n);
	}

	//swapped
	@Override
	public Collection<Unit> getStartPointsOf(SootMethod m) {
		return delegate.getEndPointsOf(m);
	}
	
	//swapped
	@Override
	public List<Unit> getReturnSitesOfCallAt(Unit n) {
		return delegate.getPredsOfCallAt(n);
	}

	//swapped
	@Override
	public boolean isExitStmt(Unit stmt) {
		return delegate.isStartPoint(stmt);
	}

	//swapped
	@Override
	public boolean isStartPoint(Unit stmt) {
		return delegate.isExitStmt(stmt);
	}
	
	//swapped
	@Override
	public Set<Unit> allNonCallStartNodes() {
		return delegate.allNonCallEndNodes();
	}
	
	//swapped
	@Override
	public List<Unit> getPredsOf(Unit u) {
		return delegate.getSuccsOf(u);
	}

	//swapped
	@Override
	public Collection<Unit> getEndPointsOf(SootMethod m) {
		return delegate.getStartPointsOf(m);
	}

	//swapped
	@Override
	public List<Unit> getPredsOfCallAt(Unit u) {
		return delegate.getSuccsOf(u);
	}

	//swapped
	@Override
	public Set<Unit> allNonCallEndNodes() {
		return delegate.allNonCallStartNodes();
	}

	//same
	@Override
	public SootMethod getMethodOf(Unit n) {
		return delegate.getMethodOf(n);
	}

	//same
	@Override
	public Collection<SootMethod> getCalleesOfCallAt(Unit n) {
		return delegate.getCalleesOfCallAt(n);
	}

	//same
	@Override
	public Collection<Unit> getCallersOf(SootMethod m) {
		return delegate.getCallersOf(m);
	}

	//same
	@Override
	public Set<Unit> getCallsFromWithin(SootMethod m) {
		return delegate.getCallsFromWithin(m);
	}

	//same
	@Override
	public boolean isCallStmt(Unit stmt) {
		return delegate.isCallStmt(stmt);
	}

	//same
	@Override
	public DirectedGraph<Unit> getOrCreateUnitGraph(SootMethod m) {
		return delegate.getOrCreateUnitGraph(m);
	}

	//same
	@Override
	public List<Value> getParameterRefs(SootMethod m) {
		return delegate.getParameterRefs(m);
	}

	@Override
	public boolean isFallThroughSuccessor(Unit stmt, Unit succ) {
		throw new UnsupportedOperationException("not implemented because semantics unclear");
	}

	@Override
	public boolean isBranchTarget(Unit stmt, Unit succ) {
		throw new UnsupportedOperationException("not implemented because semantics unclear");
	}

	//swapped
	@Override
	public boolean isReturnSite(Unit n) {
		for (Unit pred : getSuccsOf(n))
			if (isCallStmt(pred))
				return true;
		return false;
	}

}
