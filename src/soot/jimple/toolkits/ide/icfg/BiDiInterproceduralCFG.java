package soot.jimple.toolkits.ide.icfg;

import java.util.List;

import soot.Unit;
import heros.InterproceduralCFG;

/**
 * An {@link InterproceduralCFG} which supports the computation of predecessors.
 */
public interface BiDiInterproceduralCFG<N, M> extends InterproceduralCFG<N, M> {

	public List<Unit> getPredsOf(Unit u);

}
