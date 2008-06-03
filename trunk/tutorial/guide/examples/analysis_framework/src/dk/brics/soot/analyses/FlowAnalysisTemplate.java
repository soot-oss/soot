package dk.brics.soot.analyses;

import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.scalar.BackwardFlowAnalysis;

// Change to extend ForwardFlowAnalysis or others as appropriate
public class FlowAnalysisTemplate extends BackwardFlowAnalysis {
	public FlowAnalysisTemplate(DirectedGraph g) {
		super(g);
		// some other initializations
		doAnalysis();
	}

	@Override
	protected void merge(Object in1, Object in2, Object out) {
		// must analysis => out <- in1 union in2
		// may analysis => out <- in1 intersection in2
	}

	@Override
	protected void copy(Object source, Object dest) {
		// copy from source to dest
	}

	@Override
	protected Object newInitialFlow() {
		// return e.g., the empty set
		return null;
	}

	@Override
	protected Object entryInitialFlow() {
		// return e.g., the empty set
		return null;
	}
	
	@Override
	protected void flowThrough(Object in, Object node, Object out) {
		// perform flow from in to out, through node
	}
}
