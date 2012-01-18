package soot.jimple.interproc.ifds;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.SootMethod;
import soot.jimple.interproc.ifds.flowfunc.FlowFunctions;
import soot.jimple.interproc.ifds.flowfunc.SimpleFlowFunction;
import soot.jimple.interproc.ifds.pathedges.PathEdge;
import soot.toolkits.scalar.Pair;

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
	
	protected Map<SootMethod,SummaryEdges> methodToSummaries = new HashMap<SootMethod, SummaryEdges>();
	
	public void solve() {
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
			
			if(superGraph.isCallStmt(edge.getTarget())) {
				processCall(edge);
			} else if(superGraph.isReturnStmt(edge.getTarget())) {
				processReturn(edge);
			} else {
				processNormalFlow(edge);
			}
		}
	}

	private void propagate(N src, int dataAtSource, N tgt, int dataAtTgt) {
		PathEdge<N> edge = new PathEdge<N>(src, dataAtSource, tgt, dataAtTgt);
		if(!pathEdges.contains(edge)) {
			pathEdges.add(edge);
			worklist.add(edge);
		}
	}
	
	/**
	 * Lines 33-37 of the algorithm.
	 * @param edge
	 */
	private void processNormalFlow(PathEdge<N> edge) {
		N n = edge.getTarget(); 
		int d2 = edge.factAtTarget();
		for (N m : superGraph.getSuccsOf(n)) {
			SimpleFlowFunction flowFunction = flowFunctions.getNormalFlowFunction(n,m);
			Set<Integer> res = flowFunction.computeTargets(d2);
			for (Integer d3 : res) {
				propagate(n, d2, m, d3);
			}
		}
	}

	/**
	 * Lines 21-32 of the algorithm.
	 * With respect to summary edges, we follow the approach also taken in Wala:
	 * In the original algorithm, summaries are associated with the call site,
	 * however this is hard to compute, because it requires backwards computation
	 * of the flow function (see line [23], in which d4 needs to be computed from d1).
	 * Instead, as in Wala, we associate summaries with the callee function, storing
	 * summaries edges of the form (s_p,d1,e_p,d2), thus avoiding this problem.
	 * The only drawback should be that the single edges at method entires/exit
	 * need to be re-computed more often. 
	 * @param edge an edge where the target resembles an exit node
	 */
	private void processReturn(PathEdge<N> edge) {
		N n = edge.getTarget(); // an exit node; line 21...
		SootMethod methodThatNeedsSummary = superGraph.getMethodOf(n);
		SummaryEdges summaries = summaryEdgesOf(methodThatNeedsSummary);
		summaries.insertEdge(edge.factAtSource(),edge.factAtTarget());	
		
		SimpleFlowFunction retFunction = flowFunctions.getReturnFlowFunction();
		Set<Integer> targets = retFunction.computeTargets(edge.factAtTarget());
		Set<Pair<N,Integer>> callersP = getCallersOfCallAt(edge.getSource(),edge.factAtSource());
		for (Pair<N, Integer> pair : callersP) {
			N sProcOfC = pair.getO1();
			int d3 = pair.getO2();//FIXME must be start node of caller procedure
			for(int d5: targets) {
				propagate(sProcOfC, d3, retSiteOfCall(sProcOfC), d5);
			}
		}
		
	}

	private N retSiteOfCall(N sProcOfC) {
		// TODO Auto-generated method stub
		return null;
	}

	private Set<Pair<N, Integer>> getCallersOfCallAt(N source, int factAtSource) {
		// TODO Auto-generated method stub
		return null;
	}

	private SummaryEdges summaryEdgesOf(SootMethod methodThatNeedsSummary) {
		SummaryEdges summaries = methodToSummaries.get(methodThatNeedsSummary);
		if(summaries==null) {
			summaries = new SummaryEdges();
			methodToSummaries.put(methodThatNeedsSummary, summaries);
		}
		return summaries;
	}

	/**
	 * Lines 13-20 of the algorithm; processing a call site in the caller's context
	 * @param edge an edge whose target node resembles a method call
	 */
	private void processCall(PathEdge<N> edge) {
		N n = edge.getTarget(); // a call node; line 14...
		int d2 = edge.factAtTarget();
		List<N> callees = superGraph.getCalleesOfCallAt(n);
		N returnSiteN = superGraph.getReturnSiteOfCallAt(n); 
		for(N sCalledProcN: callees) { //still line 14
			SimpleFlowFunction function = flowFunctions.getCallFlowFunction(n, sCalledProcN);
			Set<Integer> res = function.computeTargets(d2);
			for(Integer d3: res) {
				propagate(sCalledProcN, d3, sCalledProcN, d3); //line 15
			}
			
			//line 17 for SummaryEdge (here callee-side)
			SummaryEdges summaryEdges = summaryEdgesOf(superGraph.getMethodOf(sCalledProcN));
			for(Integer d3: summaryEdges.targetsOf(d2)) {
				N sP = edge.getSource();  
				int d1 = edge.factAtSource();
				propagate(sP, d1, returnSiteN, d3); //line 18
			}		
		}		
		
		SimpleFlowFunction retFunction = flowFunctions.getCallToReturnFlowFunction(n, returnSiteN); //line 17 for E_Hash
		Set<Integer> retRes = retFunction.computeTargets(d2);
		
		for(Integer d3: retRes) {
			N sP = edge.getSource();  
			int d1 = edge.factAtSource();
			propagate(sP, d1, returnSiteN, d3); //line 18
		}		
	}

}
