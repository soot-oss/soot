package soot.jimple.interproc.ifds;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import soot.jimple.interproc.ifds.flowfunc.FlowFunctions;
import soot.jimple.interproc.ifds.flowfunc.SimpleFlowFunction;
import soot.jimple.interproc.ifds.pathedges.PathEdge;

/**
 * 
 * @author eric
 *
 * @param <N> Type of CFG nodes
 * @param <A> Abstraction type
 */
public class TabulationSolver<N,A> {
	
	protected Collection<PathEdge<N>> worklist = new HashSet<PathEdge<N>>();
	
	protected Set<PathEdge<N>> pathEdges = new HashSet<PathEdge<N>>();

	protected SuperGraph<N> superGraph;
	
	protected FixedUniverse<A> domain;
	
	protected final int ZERO_VALUE = -1;
	
	protected FlowFunctions<N> flowFunctions = null;
	
	private void solve() {
		N entryPoint = null;
		PathEdge<N> initEdge = new PathEdge<N>(entryPoint, ZERO_VALUE, entryPoint, ZERO_VALUE);
		pathEdges.add(initEdge);
		worklist.add(initEdge);
		forwardTabulate();
	}

	private void forwardTabulate() {
		while(!worklist.isEmpty()) {
			//pop edge
			Iterator<PathEdge<N>> iter = worklist.iterator();
			PathEdge<N> edge = iter.next();
			iter.remove();
			
			propagate(edge);
			if(superGraph.isCallStmt(edge.getTarget())) {
				processCall(edge);
			} else if(superGraph.isReturnStmt(edge.getTarget())) {
				processReturn(edge);
			} else {
			}
		}
	}

	private void propagate(PathEdge<N> edge) {
		if(!pathEdges.contains(edge)) {
			pathEdges.add(edge);
			worklist.add(edge);
		}
	}

	private void processReturn(PathEdge<N> edge) {
		// TODO Auto-generated method stub
		
	}

	private void processCall(PathEdge<N> edge) {
		N call = edge.getTarget();
		int factAtCall = edge.factAtTarget();
		for(N succ: superGraph.getCalleesOfCallAt(call)) {
			SimpleFlowFunction function = flowFunctions.getCallFlowFunction(call, succ);
			Set<Integer> res = function.computeTargets(factAtCall);
			for(Integer d3: res) {
				propagate(new PathEdge<N>(call, factAtCall, succ, d3));
			}
		}
		
		
		N retSite = superGraph.getReturnSiteOfCallAt(call);
		SimpleFlowFunction retFunction = flowFunctions.getCallToReturnFlowFunction(call, retSite);
		Set<Integer> retRes = retFunction.computeTargets(factAtCall);
		retRes.addAll(summaryEdgesFor(call,factAtCall));		
		for(Integer d3: retRes) {
			propagate(new PathEdge<N>(call, factAtCall, retSite, d3));
		}
		
	}

	private Set<Integer> summaryEdgesFor(N call, int factAtCall) {
		// TODO Auto-generated method stub
		return null;
	}

}
