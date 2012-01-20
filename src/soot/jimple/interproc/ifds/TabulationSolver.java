package soot.jimple.interproc.ifds;

import java.util.Collection;
import java.util.Collections;
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

/**
 * 
 * @author eric
 *
 * @param <N> Type of CFG nodes
 * @param <A> Abstraction type
 */
public class TabulationSolver<N,A> {
	
	protected Collection<PathEdge<N>> worklist = new HashSet<PathEdge<N>>();
	
	/*
	 * We store the "list" of path edges <s_p,d1> -> <n,d2> as a mapping that maps
	 * <s_p,n,d2> to all possible d1. This is because at line [26] we need to do a
	 * reverse lookup, computing d1 (called d3 in line [26]) from the remaining items.
	 * The map allows to look this up quickly.
	 */
	protected Map<MultiKey,Set<Integer>> pathEdges = new HashMap<MultiKey, Set<Integer>>();

	protected InterproceduralCFG<N> icfg;
	
	protected FixedUniverse<A> domain;
	
	protected final int ZERO_VALUE = -1;
	
	protected FlowFunctions<N> flowFunctions = null;
	
	protected Map<SootMethod,SummaryEdges> methodToSummaries = new HashMap<SootMethod, SummaryEdges>();
	
	public void solve() {
		for (N entryPoint : icfg.entryPoints()) {
			addPathEdge(entryPoint, ZERO_VALUE, entryPoint, ZERO_VALUE);
			PathEdge<N> initEdge = new PathEdge<N>(entryPoint, ZERO_VALUE, entryPoint, ZERO_VALUE);
			worklist.add(initEdge);
		}
		forwardTabulateSLRPs();		
	}
	
	/*
	 * Forward-tabulates the same-level realizable paths.
	 */
	private void forwardTabulateSLRPs() {
		while(!worklist.isEmpty()) {
			//pop edge
			Iterator<PathEdge<N>> iter = worklist.iterator();
			PathEdge<N> edge = iter.next();
			iter.remove();
			
			if(icfg.isCallStmt(edge.getTarget())) {
				processCall(edge);
			} else if(icfg.isReturnStmt(edge.getTarget())) {
				processExit(edge);
			} else {
				processNormalFlow(edge);
			}
		}
	}
	
	/**
	 * Lines 13-20 of the algorithm; processing a call site in the caller's context
	 * @param edge an edge whose target node resembles a method call
	 */
	private void processCall(PathEdge<N> edge) {
		N n = edge.getTarget(); // a call node; line 14...
		int d2 = edge.factAtTarget();
		Set<N> callees = icfg.getCalleesOfCallAt(n);
		List<N> returnSiteNs = icfg.getReturnSitesOfCallAt(n);
		for(N sCalledProcN: callees) { //still line 14
			SimpleFlowFunction function = flowFunctions.getCallFlowFunction(n, sCalledProcN);
			Set<Integer> res = function.computeTargets(d2);
			for(Integer d3: res) {
				propagate(sCalledProcN, d3, sCalledProcN, d3); //line 15

				//line 17 for SummaryEdge (here callee-side)
				SummaryEdges summaryEdges = summaryEdgesOf(icfg.getMethodOf(sCalledProcN));
				for(Integer d3Prime: summaryEdges.targetsOf(d3)) {  
					N sP = edge.getSource();  
					int d1 = edge.factAtSource();
					for (N returnSiteN : returnSiteNs) {
						SimpleFlowFunction retFunction  = flowFunctions.getReturnFlowFunction();
						for (Integer d3Prime2 : retFunction.computeTargets(d3Prime)) { //d3prime2 is the d3 at line 17/18 (note that we use callee summaries)
							propagate(sP, d1, returnSiteN, d3Prime2); //line 18
						}
					}
				}		
			}
			
		}		
		
		N sP = edge.getSource();  
		int d1 = edge.factAtSource();
		for (N returnSiteN : returnSiteNs) {
			SimpleFlowFunction retFunction = flowFunctions.getCallToReturnFlowFunction(n,returnSiteN); //line 17 for E_Hash
			Set<Integer> retRes = retFunction.computeTargets(d2);			
			for(Integer d3: retRes) {
				propagate(sP, d1, returnSiteN, d3); //line 18
			}		
		}
	}
	
	/**
	 * Lines 21-32 of the algorithm.
	 * With respect to summary edges, we follow the approach also taken in Wala:
	 * In the original algorithm, summaries are associated with the call site.
	 * Instead, as in Wala, we associate summaries with the callee function, storing
	 * summaries edges of the form (s_p,d1,e_p,d2).
	 * The only drawback should be that the single edges at method entries/exit
	 * need to be re-computed more often. 
	 * @param edge an edge where the target resembles an exit node
	 */
	private void processExit(PathEdge<N> edge) {
		N n = edge.getTarget(); // an exit node; line 21...
		SootMethod methodThatNeedsSummary = icfg.getMethodOf(n);
		SummaryEdges summaries = summaryEdgesOf(methodThatNeedsSummary);
		summaries.insertEdge(edge.factAtSource(),edge.factAtTarget());			
		
		SimpleFlowFunction retFunction = flowFunctions.getReturnFlowFunction();
		Set<Integer> targets = retFunction.computeTargets(edge.factAtTarget());
		Set<N> callersP = icfg.getCallersOf(methodThatNeedsSummary);
		int d1 = edge.factAtSource();
		for (N c : callersP) {
			SimpleFlowFunction callFlowFunction = flowFunctions.getCallFlowFunction(c, icfg.getStartPointOf(icfg.getMethodOf(n)));
			Set<Integer> d4s = callFlowFunction.computeSources(d1);
			for(int d4: d4s) {
				for(int d5: targets) {
					N sProcOfC = icfg.getStartPointOf(icfg.getMethodOf(c));
					for(int d3: getDataValuesAtSourcePathEdge(sProcOfC, c, d4)) {
						for(N retSiteC: icfg.getReturnSitesOfCallAt(n)) {
							propagate(sProcOfC, d3, retSiteC, d5);
						}
					}
				}
			}
		}
		
	}
	
	/**
	 * Lines 33-37 of the algorithm.
	 * @param edge
	 */
	private void processNormalFlow(PathEdge<N> edge) {
		N n = edge.getTarget(); 
		int d2 = edge.factAtTarget();
		for (N m : icfg.getSuccsOf(n)) {
			SimpleFlowFunction flowFunction = flowFunctions.getNormalFlowFunction(n,m);
			Set<Integer> res = flowFunction.computeTargets(d2);
			for (Integer d3 : res) {
				propagate(n, d2, m, d3);
			}
		}
	}

	private SummaryEdges summaryEdgesOf(SootMethod methodThatNeedsSummary) {
		SummaryEdges summaries = methodToSummaries.get(methodThatNeedsSummary);
		if(summaries==null) {
			summaries = new SummaryEdges();
			methodToSummaries.put(methodThatNeedsSummary, summaries);
		}
		return summaries;
	}

	private void addPathEdge(N source, int dataAtSource, N target, int dataAtTarget) {
		MultiKey key = new MultiKey(source, target, dataAtTarget);
		Set<Integer> dataValuesAtSource = pathEdges.get(key);
		if(dataValuesAtSource==null) {
			dataValuesAtSource = new HashSet<Integer>();
			pathEdges.put(key, dataValuesAtSource);
		}
		dataValuesAtSource.add(dataAtSource);
	}
	
	private Set<Integer> getDataValuesAtSourcePathEdge(N source, N target, int dataAtTarget) {
		MultiKey key = new MultiKey(source, target, dataAtTarget);
		Set<Integer> dataValuesAtSource = pathEdges.get(key);
		if(dataValuesAtSource==null)
			return Collections.emptySet();
		else 
			return dataValuesAtSource;		
	}
	
	private void propagate(N src, int dataAtSource, N tgt, int dataAtTgt) {
		MultiKey key = new MultiKey(src, tgt, dataAtTgt);
		Set<Integer> dataValuesAtSource = pathEdges.get(key);
		if(dataValuesAtSource==null) {
			dataValuesAtSource = new HashSet<Integer>();
			pathEdges.put(key, dataValuesAtSource);
		}
		if(!dataValuesAtSource.contains(dataAtSource)) {
			dataValuesAtSource.add(dataAtSource);
			
			PathEdge<N> edge = new PathEdge<N>(src, dataAtSource, tgt, dataAtTgt);
			worklist.add(edge);
		}
	}
	

	
	
	class MultiKey {
		private final N src;
		private final N tgt;
		private final int dataAtTgt;
		
		public MultiKey(N src, N tgt, int dataAtTgt) {
			this.src = src;
			this.tgt = tgt;
			this.dataAtTgt = dataAtTgt;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + dataAtTgt;
			result = prime * result + ((src == null) ? 0 : src.hashCode());
			result = prime * result + ((tgt == null) ? 0 : tgt.hashCode());
			return result;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			@SuppressWarnings("unchecked")
			MultiKey other = (MultiKey) obj;
			if (dataAtTgt != other.dataAtTgt)
				return false;
			if (src == null) {
				if (other.src != null)
					return false;
			} else if (!src.equals(other.src))
				return false;
			if (tgt == null) {
				if (other.tgt != null)
					return false;
			} else if (!tgt.equals(other.tgt))
				return false;
			return true;
		}

		public N getSource() {
			return src;
		}

		public N getTarget() {
			return tgt;
		}

		public int getDataAtTarget() {
			return dataAtTgt;
		}
		
	}

}
