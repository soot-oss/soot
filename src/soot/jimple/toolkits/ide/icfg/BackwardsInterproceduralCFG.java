package soot.jimple.toolkits.ide.icfg;

import soot.Body;
import soot.Unit;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.graph.InverseGraph;

/**
 * Same as {@link JimpleBasedInterproceduralCFG} but based on inverted unit graphs.
 * This should be used for backward analyses.
 */
public class BackwardsInterproceduralCFG extends JimpleBasedInterproceduralCFG {

	@Override
	protected DirectedGraph<Unit> makeGraph(Body body) {
		return new InverseGraph<Unit>(super.makeGraph(body));
	}
	
}
