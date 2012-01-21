package soot.jimple.interproc.ifds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.Body;
import soot.MethodOrMethodContext;
import soot.PatchingChain;
import soot.Scene;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.Stmt;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.jimple.toolkits.callgraph.ReachableMethods;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;

//FIXME use basic blocks instead of units to save edges
public class DefaultInterproceduralCFG implements InterproceduralCFG<Unit,SootMethod> {
	
	private final CallGraph cg;
	
	private final Map<Unit,Body> unitToOwner = new HashMap<Unit,Body>();
	
	private final Map<Body,UnitGraph> bodyToUnitGraph = new HashMap<Body,UnitGraph>();

	public DefaultInterproceduralCFG() {
		cg = Scene.v().getCallGraph();
		
		List<MethodOrMethodContext> eps = new ArrayList<MethodOrMethodContext>();
		eps.addAll(Scene.v().getEntryPoints());
		ReachableMethods reachableMethods = new ReachableMethods(cg, eps.iterator());
		reachableMethods.update();
		
		for(Iterator<MethodOrMethodContext> iter = reachableMethods.listener(); iter.hasNext(); ) {
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

//	/* Returns the first unit of the body of each method that is an entry point.
//	 */
//	public Set<Unit> entryPoints() {
//		Set<Unit> res = new HashSet<Unit>();
//		for (SootMethod m: Scene.v().getEntryPoints()) {
//			Unit startPoint = getStartPointOf(m);
//			if(startPoint!=null) res.add(startPoint);
//		}		
//		return res;
//	}

	public SootMethod getMethodOf(Unit u) {
		return unitToOwner.get(u).getMethod();
	}

	public List<Unit> getSuccsOf(Unit u) {
		Body body = unitToOwner.get(u);
		UnitGraph unitGraph = getOrCreateUnitGraph(body);
		return unitGraph.getSuccsOf(u);
	}

	private UnitGraph getOrCreateUnitGraph(Body body) {
		UnitGraph unitGraph = bodyToUnitGraph.get(body);
		if(unitGraph==null) {
			unitGraph = new ExceptionalUnitGraph(body); 
			bodyToUnitGraph.put(body, unitGraph);
		}
		return unitGraph;
	}

	public Set<SootMethod> getCalleesOfCallAt(Unit u) {
		//TODO implement soft cache
		Set<SootMethod> res = new HashSet<SootMethod>();
		for(Iterator<Edge> edgeIter = cg.edgesOutOf(u); edgeIter.hasNext(); ) {
			Edge edge = edgeIter.next();
			SootMethod m = edge.getTgt().method();
			if(m.hasActiveBody())
			res.add(m);
		}
		return res; 
	}

	public List<Unit> getReturnSitesOfCallAt(Unit u) {
		return getSuccsOf(u);
	}

	public boolean isCallStmt(Unit u) {
		return ((Stmt)u).containsInvokeExpr();
	}

	public boolean isExitStmt(Unit u) {
		Body body = unitToOwner.get(u);
		UnitGraph unitGraph = getOrCreateUnitGraph(body);
		return unitGraph.getTails().contains(u);
	}

	public Set<Unit> getCallersOf(SootMethod m) {
		//TODO implement soft cache
		Set<Unit> res = new HashSet<Unit>();
		for(Iterator<Edge> edgeIter = cg.edgesInto(m); edgeIter.hasNext(); ) {
			Edge edge = edgeIter.next();
			res.add(edge.srcUnit());			
		}
		return res;
	}

	public Unit getStartPointOf(SootMethod m) {
		if(m.hasActiveBody()) {
			Body b = m.getActiveBody();
			PatchingChain<Unit> units = b.getUnits();
			if(!units.isEmpty()) {
				return units.getFirst();
			} 
		}
		return null;
	}

}
