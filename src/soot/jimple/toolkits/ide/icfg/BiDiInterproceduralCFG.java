package soot.jimple.toolkits.ide.icfg;

import heros.InterproceduralCFG;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import soot.Unit;
import soot.Value;
import soot.toolkits.graph.DirectedGraph;

/**
 * An {@link InterproceduralCFG} which supports the computation of predecessors.
 */
public interface BiDiInterproceduralCFG<N, M> extends InterproceduralCFG<N, M> {

	public List<Unit> getPredsOf(N u);
	
	public Collection<Unit> getEndPointsOf(M m);

	public List<Unit> getPredsOfCallAt(N u);

	public Set<Unit> allNonCallEndNodes();
		
	//also exposed to some clients who need it
	public DirectedGraph<Unit> getOrCreateUnitGraph(M body);

	public List<Value> getParameterRefs(M m);
	
	/**
	 * Gets whether the given statement is a return site of at least one call
	 * @param n The statement to check
	 * @return True if the given statement is a return site, otherwise false
	 */
	public boolean isReturnSite(N n);

}
