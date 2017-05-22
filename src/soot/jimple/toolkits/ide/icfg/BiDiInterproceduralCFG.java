package soot.jimple.toolkits.ide.icfg;

import heros.InterproceduralCFG;

import java.util.Collection;
import java.util.List;
import java.util.Set;

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
	public DirectedGraph<N> getOrCreateUnitGraph(M body);

	public List<Value> getParameterRefs(M m);
	
	/**
	 * Gets whether the given statement is a return site of at least one call
	 * @param n The statement to check
	 * @return True if the given statement is a return site, otherwise false
	 */
	public boolean isReturnSite(N n);
	
	/**
	 * Checks whether the given statement is rachable from the entry point
	 * @param u The statement to check
	 * @return True if there is a control flow path from the entry point of the
	 * program to the given statement, otherwise false
	 */
	public boolean isReachable(N u);

}
