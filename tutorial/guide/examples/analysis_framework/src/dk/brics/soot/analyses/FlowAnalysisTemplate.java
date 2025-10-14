package dk.brics.soot.analyses;

import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.scalar.BackwardFlowAnalysis;

// Change to extend ForwardFlowAnalysis or others as appropriate
public class FlowAnalysisTemplate<N, A> extends BackwardFlowAnalysis<N, A> {
	public FlowAnalysisTemplate(DirectedGraph<N> g) {
		super(g);
		// some other initializations
		doAnalysis();
	}

	@Override
	protected void merge(A in1, A in2, A out) {
		// must analysis => out <- in1 union in2
		// may analysis => out <- in1 intersection in2
	}

	@Override
	protected void copy(A source, A dest) {
		// copy from source to dest
	}

	@Override
	protected A newInitialFlow() {
		// return e.g., the empty set
		return null;
	}

	@Override
	protected A entryInitialFlow() {
		// return e.g., the empty set
		return null;
	}
	
	@Override
	protected void flowThrough(A in, N node, A out) {
		// perform flow from in to out, through node
	}
}
