package soot.jimple.toolkits.ide.icfg;

import heros.DontSynchronize;
import heros.InterproceduralCFG;
import heros.SynchronizedBy;
import heros.ThreadSafe;
import heros.solver.IDESolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.Body;
import soot.MethodOrMethodContext;
import soot.PatchingChain;
import soot.Scene;
import soot.SootMethod;
import soot.Unit;
import soot.UnitBox;
import soot.jimple.Stmt;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.jimple.toolkits.callgraph.EdgePredicate;
import soot.jimple.toolkits.callgraph.Filter;
import soot.jimple.toolkits.callgraph.ReachableMethods;
import soot.toolkits.exceptions.UnitThrowAnalysis;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.graph.ExceptionalUnitGraph;

import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;


/**
 * Default implementation for the {@link InterproceduralCFG} interface.
 * Includes all statements reachable from {@link Scene#getEntryPoints()} through
 * explicit call statements or through calls to {@link Thread#start()}.
 * 
 * This class is designed to be thread safe, and subclasses of this class must be designed
 * in a thread-safe way, too.
 */
@ThreadSafe
public class JimpleBasedInterproceduralCFG implements InterproceduralCFG<Unit,SootMethod> {
	
	//retains only callers that are explicit call sites or Thread.start()
	protected static class EdgeFilter extends Filter {		
		protected EdgeFilter() {
			super(new EdgePredicate() {
				public boolean want(Edge e) {				
					return e.kind().isExplicit() || e.kind().isThread();
				}
			});
		}
	}
	
	@DontSynchronize("readonly")
	protected final CallGraph cg;
	
	@DontSynchronize("written by single thread; read afterwards")
	protected final Map<Unit,Body> unitToOwner = new HashMap<Unit,Body>();	
	
	@SynchronizedBy("by use of synchronized LoadingCache class")
	protected final LoadingCache<Body,DirectedGraph<Unit>> bodyToUnitGraph =
			IDESolver.DEFAULT_CACHE_BUILDER.build( new CacheLoader<Body,DirectedGraph<Unit>>() {
				public DirectedGraph<Unit> load(Body body) throws Exception {
					return makeGraph(body);
				}
			});
	
	@SynchronizedBy("by use of synchronized LoadingCache class")
	protected final LoadingCache<Unit,Set<SootMethod>> unitToCallees =
			IDESolver.DEFAULT_CACHE_BUILDER.build( new CacheLoader<Unit,Set<SootMethod>>() {
				public Set<SootMethod> load(Unit u) throws Exception {
					Set<SootMethod> res = new LinkedHashSet<SootMethod>();
					//only retain callers that are explicit call sites or Thread.start()
					Iterator<Edge> edgeIter = new EdgeFilter().wrap(cg.edgesOutOf(u));					
					while(edgeIter.hasNext()) {
						Edge edge = edgeIter.next();
						SootMethod m = edge.getTgt().method();
						if(m.hasActiveBody())
							res.add(m);
						else if(IDESolver.DEBUG) 
							System.err.println("Method "+m.getSignature()+" is referenced but has no body!");
					}
					return res; 
				}
			});

	@SynchronizedBy("by use of synchronized LoadingCache class")
	protected final LoadingCache<SootMethod,Set<Unit>> methodToCallers =
			IDESolver.DEFAULT_CACHE_BUILDER.build( new CacheLoader<SootMethod,Set<Unit>>() {
				public Set<Unit> load(SootMethod m) throws Exception {
					Set<Unit> res = new LinkedHashSet<Unit>();					
					//only retain callers that are explicit call sites or Thread.start()
					Iterator<Edge> edgeIter = new EdgeFilter().wrap(cg.edgesInto(m));					
					while(edgeIter.hasNext()) {
						Edge edge = edgeIter.next();
						res.add(edge.srcUnit());			
					}
					return res;
				}
			});

	@SynchronizedBy("by use of synchronized LoadingCache class")
	protected final LoadingCache<SootMethod,Set<Unit>> methodToCallsFromWithin =
			IDESolver.DEFAULT_CACHE_BUILDER.build( new CacheLoader<SootMethod,Set<Unit>>() {
				public Set<Unit> load(SootMethod m) throws Exception {
					Set<Unit> res = new LinkedHashSet<Unit>();
					//only retain calls that are explicit call sites or Thread.start()
					Iterator<Edge> edgeIter = new EdgeFilter().wrap(cg.edgesOutOf(m));
					while(edgeIter.hasNext()) {
						Edge edge = edgeIter.next();
						res.add(edge.srcUnit());			
					}
					return res;
				}
			});

	
	public JimpleBasedInterproceduralCFG() {
		cg = Scene.v().getCallGraph();		
		initializeUnitToOwner();
	}

	protected void initializeUnitToOwner() {
		for(Iterator<MethodOrMethodContext> iter = Scene.v().getReachableMethods().listener(); iter.hasNext(); ) {
			SootMethod m = iter.next().method();
			if(m.hasActiveBody()) {
				Body b = m.getActiveBody();
				PatchingChain<Unit> units = b.getUnits();
				for (Unit unit : units) {
					unitToOwner.put(unit, b);
				}
			}
		}
	}

	@Override
	public SootMethod getMethodOf(Unit u) {
		return unitToOwner.get(u).getMethod();
	}

	@Override
	public List<Unit> getSuccsOf(Unit u) {
		Body body = unitToOwner.get(u);
		DirectedGraph<Unit> unitGraph = getOrCreateUnitGraph(body);
		return unitGraph.getSuccsOf(u);
	}

	private DirectedGraph<Unit> getOrCreateUnitGraph(Body body) {
		return bodyToUnitGraph.getUnchecked(body);
	}

	protected synchronized DirectedGraph<Unit> makeGraph(Body body) {
		return new ExceptionalUnitGraph(body, UnitThrowAnalysis.v() ,true);
	}

	@Override
	public Set<SootMethod> getCalleesOfCallAt(Unit u) {
		return unitToCallees.getUnchecked(u);
	}

	@Override
	public List<Unit> getReturnSitesOfCallAt(Unit u) {
		return getSuccsOf(u);
	}

	@Override
	public boolean isCallStmt(Unit u) {
		return ((Stmt)u).containsInvokeExpr();
	}

	@Override
	public boolean isExitStmt(Unit u) {
		Body body = unitToOwner.get(u);
		DirectedGraph<Unit> unitGraph = getOrCreateUnitGraph(body);
		return unitGraph.getTails().contains(u);
	}

	@Override
	public Set<Unit> getCallersOf(SootMethod m) {
		return methodToCallers.getUnchecked(m);
	}
	
	@Override
	public Set<Unit> getCallsFromWithin(SootMethod m) {
		return methodToCallsFromWithin.getUnchecked(m);		
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
	public boolean isStartPoint(Unit u) {
		Body body = unitToOwner.get(u);
		DirectedGraph<Unit> unitGraph = getOrCreateUnitGraph(body);		
		return unitGraph.getHeads().contains(u);
	}

	@Override
	//TODO do we need to replace call by return for backwards analysis?
	public Set<Unit> allNonCallStartNodes() {
		Set<Unit> res = new LinkedHashSet<Unit>(unitToOwner.keySet());
		for (Iterator<Unit> iter = res.iterator(); iter.hasNext();) {
			Unit u = iter.next();
			if(isStartPoint(u) || isCallStmt(u)) iter.remove();
		}
		return res;
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
}
