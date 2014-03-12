package soot.jimple.toolkits.ide.icfg;

import heros.InterproceduralCFG;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import soot.Body;
import soot.Value;
import soot.toolkits.graph.DirectedGraph;

/**
 * An {@link InterproceduralCFG} which supports the computation of predecessors.
 */
public interface BiDiInterproceduralCFG<N, M> extends InterproceduralCFG<N, M> {

	public List<N> getPredsOf(N u);
	
	public Collection<N> getEndPointsOf(M m);

	public List<N> getPredsOfCallAt(N u);

	public Set<N> allNonCallEndNodes();
		
	//also exposed to some clients who need it
	public DirectedGraph<N> getOrCreateUnitGraph(Body body);

	public List<Value> getParameterRefs(M m);

}
