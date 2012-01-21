package soot.jimple.interproc.ifds;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
	protected final Map<MultiKey,Set<Integer>> pathEdges = new HashMap<MultiKey, Set<Integer>>();

	protected final Map<SootMethod,SummaryEdges> methodToSummaries = new HashMap<SootMethod, SummaryEdges>();

	protected final int ZERO_VALUE = 0;

	protected final InterproceduralCFG<N> icfg;
	
	protected final FlowFunctions<N,A> flowFunctions;

	protected final FixedUniverse<A> universe;

	private final Map<SootMethod, Set<A>> initialSeeds;
	
	public TabulationSolver(InterproceduralCFG<N> icfg, FixedUniverse<A> universe, FlowFunctions<N,A> flowFunctions, Map<SootMethod,Set<A>> initialSeeds) {
		this.icfg = icfg;
		this.universe = universe;
		this.flowFunctions = flowFunctions;
		this.initialSeeds = initialSeeds;
	}

	public void solve() {
		for(Entry<SootMethod, Set<A>> seed: initialSeeds.entrySet()) {
			SootMethod entryPoint = seed.getKey();
			Set<A> initialAbstraction = seed.getValue();
			N startPoint = icfg.getStartPointOf(entryPoint);
			propagate(startPoint, ZERO_VALUE, startPoint, ZERO_VALUE);
			for (A val : initialAbstraction) {
				int index = universe.indexOf(val);
				propagate(startPoint, index, startPoint, index);
			}
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
			SimpleFlowFunction<A> function = flowFunctions.getCallFlowFunction(n, sCalledProcN);
			Set<A> res = function.computeTargets(universe.elementAt(d2));
			for(A d3val: res) {
				int d3 = universe.indexOf(d3val);
				propagate(sCalledProcN, d3, sCalledProcN, d3); //line 15

				//line 17 for SummaryEdge (here callee-side)
				SummaryEdges summaryEdges = summaryEdgesOf(icfg.getMethodOf(sCalledProcN));
				for(Integer d3Prime: summaryEdges.targetsOf(d3)) {  
					N sP = edge.getSource();  
					int d1 = edge.factAtSource();
					for (N returnSiteN : returnSiteNs) {
						SimpleFlowFunction<A> retFunction  = flowFunctions.getReturnFlowFunction();
						Set<A> d3Prime2vals = retFunction.computeTargets(universe.elementAt(d3Prime));
						for (A d3Prime2val : d3Prime2vals) { //d3prime2 is the d3 at line 17/18 (note that we use callee summaries)
							int d3Prime2 = universe.indexOf(d3Prime2val);
							propagate(sP, d1, returnSiteN, d3Prime2); //line 18
						}
					}
				}		
			}
			
		}		
		
		N sP = edge.getSource();  
		int d1 = edge.factAtSource();
		for (N returnSiteN : returnSiteNs) {
			SimpleFlowFunction<A> retFunction = flowFunctions.getCallToReturnFlowFunction(n,returnSiteN); //line 17 for E_Hash
			Set<A> retRes = retFunction.computeTargets(universe.elementAt(d2));			
			for(A d3val: retRes) {
				int d3 = universe.indexOf(d3val);
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
		
		SimpleFlowFunction<A> retFunction = flowFunctions.getReturnFlowFunction();
		Set<A> targets = retFunction.computeTargets(universe.elementAt(edge.factAtTarget()));
		Set<N> callersP = icfg.getCallersOf(methodThatNeedsSummary);
		int d1 = edge.factAtSource();
		for (N c : callersP) {
			SimpleFlowFunction<A> callFlowFunction = flowFunctions.getCallFlowFunction(c, icfg.getStartPointOf(icfg.getMethodOf(n)));
			Set<A> d4s = callFlowFunction.computeSources(universe.elementAt(d1));
			for(A d4val: d4s) {
				int d4 = universe.indexOf(d4val);
				for(A d5val: targets) {
					int d5 = universe.indexOf(d5val);
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
		N sP = edge.getSource();
		int d1 = edge.factAtSource();
		N n = edge.getTarget(); 
		int d2 = edge.factAtTarget();
		for (N m : icfg.getSuccsOf(n)) {
			SimpleFlowFunction<A> flowFunction = flowFunctions.getNormalFlowFunction(n,m);
			Set<A> res = flowFunction.computeTargets(universe.elementAt(d2));
			for (A d3val : res) {
				int d3 = universe.indexOf(d3val);
				propagate(sP, d1, m, d3);
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
			
			StringBuffer result = new StringBuffer();
			result.append("<");
			result.append(src);
			result.append(",");
			A elementAt = universe.elementAt(dataAtSource);
			result.append(elementAt);
			result.append("> -> <");
			result.append(tgt);
			result.append(",");
			result.append(universe.elementAt(dataAtTgt));
			result.append(">");
			System.err.println(result.toString());
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
