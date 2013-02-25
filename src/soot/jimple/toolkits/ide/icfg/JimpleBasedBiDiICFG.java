package soot.jimple.toolkits.ide.icfg;

import java.util.List;

import soot.Body;
import soot.SootMethod;
import soot.Unit;
import soot.toolkits.graph.DirectedGraph;

/**
 * A {@link JimpleBasedInterproceduralCFG} which also supports the computation
 * of predecessors.
 */
public class JimpleBasedBiDiICFG extends JimpleBasedInterproceduralCFG implements BiDiInterproceduralCFG<Unit,SootMethod> {

	public List<Unit> getPredsOf(Unit u) {
		Body body = unitToOwner.get(u);
		DirectedGraph<Unit> unitGraph = getOrCreateUnitGraph(body);
		return unitGraph.getPredsOf(u);
	}
}
