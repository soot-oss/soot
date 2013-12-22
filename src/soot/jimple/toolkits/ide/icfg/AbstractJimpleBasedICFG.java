package soot.jimple.toolkits.ide.icfg;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import heros.DontSynchronize;
import heros.InterproceduralCFG;
import heros.SynchronizedBy;
import heros.solver.IDESolver;
import soot.Body;
import soot.SootMethod;
import soot.Unit;
import soot.UnitBox;
import soot.Value;
import soot.jimple.Stmt;
import soot.toolkits.exceptions.UnitThrowAnalysis;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.graph.ExceptionalUnitGraph;

public abstract class AbstractJimpleBasedICFG implements InterproceduralCFG<Unit,SootMethod> {

	@DontSynchronize("written by single thread; read afterwards")
	protected final Map<Unit,Body> unitToOwner = new HashMap<Unit,Body>();
	
	@SynchronizedBy("by use of synchronized LoadingCache class")
	protected final LoadingCache<Body,DirectedGraph<Unit>> bodyToUnitGraph = IDESolver.DEFAULT_CACHE_BUILDER.build( new CacheLoader<Body,DirectedGraph<Unit>>() {
					@Override
					public DirectedGraph<Unit> load(Body body) throws Exception {
						return makeGraph(body);
					}
				});
	
	@SynchronizedBy("by use of synchronized LoadingCache class")
	protected final LoadingCache<SootMethod,List<Value>> methodToParameterRefs = IDESolver.DEFAULT_CACHE_BUILDER.build( new CacheLoader<SootMethod,List<Value>>() {
					@Override
					public List<Value> load(SootMethod m) throws Exception {
						return m.getActiveBody().getParameterRefs();
					}
				});

	@SynchronizedBy("by use of synchronized LoadingCache class")
	protected final LoadingCache<SootMethod,Set<Unit>> methodToCallsFromWithin = IDESolver.DEFAULT_CACHE_BUILDER.build( new CacheLoader<SootMethod,Set<Unit>>() {
					@Override
					public Set<Unit> load(SootMethod m) throws Exception {
						Set<Unit> res = new LinkedHashSet<Unit>();
						for(Unit u: m.getActiveBody().getUnits()) {
							if(isCallStmt(u))
								res.add(u);
						}
						return res;
					}
				});

	@Override
	public SootMethod getMethodOf(Unit u) {
		assert unitToOwner.containsKey(u);
		return unitToOwner.get(u).getMethod();
	}

	@Override
	public List<Unit> getSuccsOf(Unit u) {
		Body body = unitToOwner.get(u);
		DirectedGraph<Unit> unitGraph = getOrCreateUnitGraph(body);
		return unitGraph.getSuccsOf(u);
	}

	protected DirectedGraph<Unit> getOrCreateUnitGraph(Body body) {
		return bodyToUnitGraph.getUnchecked(body);
	}

	protected synchronized DirectedGraph<Unit> makeGraph(Body body) {
		return new ExceptionalUnitGraph(body, UnitThrowAnalysis.v() ,true);
	}

	@Override
	public boolean isExitStmt(Unit u) {
		Body body = unitToOwner.get(u);
		DirectedGraph<Unit> unitGraph = getOrCreateUnitGraph(body);
		return unitGraph.getTails().contains(u);
	}

	@Override
	public boolean isStartPoint(Unit u) {
		Body body = unitToOwner.get(u);
		DirectedGraph<Unit> unitGraph = getOrCreateUnitGraph(body);		
		return unitGraph.getHeads().contains(u);
	}

	@Override
	public boolean isFallThroughSuccessor(Unit u, Unit succ) {
		assert getSuccsOf(u).contains(succ);
		if(!u.fallsThrough()) return false;
		Body body = unitToOwner.get(u);
		return body.getUnits().getSuccOf(u) == succ;
	}

	@Override
	public boolean isBranchTarget(Unit u, Unit succ) {
		assert getSuccsOf(u).contains(succ);
		if(!u.branches()) return false;
		for (UnitBox ub : succ.getUnitBoxes()) {
			if(ub.getUnit()==succ) return true;
		}
		return false;
	}

	public List<Value> getParameterRefs(SootMethod m) {
		return methodToParameterRefs.getUnchecked(m);
	}

	@Override
	public Set<Unit> getStartPointsOf(SootMethod m) {
		if(m.hasActiveBody()) {
			Body body = m.getActiveBody();
			DirectedGraph<Unit> unitGraph = getOrCreateUnitGraph(body);
			return new LinkedHashSet<Unit>(unitGraph.getHeads());
		}
		return null;
	}

	@Override
	public boolean isCallStmt(Unit u) {
		return ((Stmt)u).containsInvokeExpr();
	}

	@Override
	public Set<Unit> allNonCallStartNodes() {
		Set<Unit> res = new LinkedHashSet<Unit>(unitToOwner.keySet());
		for (Iterator<Unit> iter = res.iterator(); iter.hasNext();) {
			Unit u = iter.next();
			if(isStartPoint(u) || isCallStmt(u)) iter.remove();
		}
		return res;
	}

	@Override
	public List<Unit> getReturnSitesOfCallAt(Unit u) {
		return getSuccsOf(u);
	}

	@Override
	public Set<Unit> getCallsFromWithin(SootMethod m) {
		return methodToCallsFromWithin.getUnchecked(m);		
	}

}