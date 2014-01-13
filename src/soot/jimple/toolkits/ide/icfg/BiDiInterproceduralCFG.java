package soot.jimple.toolkits.ide.icfg;

import java.util.List;
import java.util.Set;

import soot.Body;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.toolkits.graph.DirectedGraph;
import heros.InterproceduralCFG;

/**
 * An {@link InterproceduralCFG} which supports the computation of predecessors.
 */
public interface BiDiInterproceduralCFG<N, M> extends InterproceduralCFG<N, M> {

	public List<Unit> getPredsOf(Unit u);
	
	public Set<Unit> getEndPointsOf(SootMethod m);

	public List<Unit> getPredsOfCallAt(Unit u);

	public Set<Unit> allNonCallEndNodes();
		
	//also exposed to some clients who need it
	public DirectedGraph<Unit> getOrCreateUnitGraph(Body body);

	public List<Value> getParameterRefs(SootMethod m);

}
