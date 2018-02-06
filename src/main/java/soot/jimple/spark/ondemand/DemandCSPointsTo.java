/* Soot - a J*va Optimization Framework
 * Copyright (C) 2007 Manu Sridharan
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */
package soot.jimple.spark.ondemand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.AnySubType;
import soot.ArrayType;
import soot.Context;
import soot.G;
import soot.Local;
import soot.PointsToAnalysis;
import soot.PointsToSet;
import soot.RefType;
import soot.Scene;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import soot.jimple.spark.ondemand.genericutil.ArraySet;
import soot.jimple.spark.ondemand.genericutil.HashSetMultiMap;
import soot.jimple.spark.ondemand.genericutil.ImmutableStack;
import soot.jimple.spark.ondemand.genericutil.Predicate;
import soot.jimple.spark.ondemand.genericutil.Propagator;
import soot.jimple.spark.ondemand.genericutil.Stack;
import soot.jimple.spark.ondemand.pautil.AssignEdge;
import soot.jimple.spark.ondemand.pautil.ContextSensitiveInfo;
import soot.jimple.spark.ondemand.pautil.OTFMethodSCCManager;
import soot.jimple.spark.ondemand.pautil.SootUtil;
import soot.jimple.spark.ondemand.pautil.ValidMatches;
import soot.jimple.spark.ondemand.pautil.SootUtil.FieldToEdgesMap;
import soot.jimple.spark.pag.AllocNode;
import soot.jimple.spark.pag.FieldRefNode;
import soot.jimple.spark.pag.GlobalVarNode;
import soot.jimple.spark.pag.LocalVarNode;
import soot.jimple.spark.pag.Node;
import soot.jimple.spark.pag.PAG;
import soot.jimple.spark.pag.SparkField;
import soot.jimple.spark.pag.VarNode;
import soot.jimple.spark.sets.EmptyPointsToSet;
import soot.jimple.spark.sets.EqualsSupportingPointsToSet;
import soot.jimple.spark.sets.HybridPointsToSet;
import soot.jimple.spark.sets.P2SetVisitor;
import soot.jimple.spark.sets.PointsToSetEqualsWrapper;
import soot.jimple.spark.sets.PointsToSetInternal;
import soot.jimple.toolkits.callgraph.VirtualCalls;
import soot.toolkits.scalar.Pair;
import soot.util.NumberedString;

/**
 * Tries to find imprecision in points-to sets from a previously run analysis.
 * Requires that all sub-results of previous analysis were cached.
 * 
 * @author Manu Sridharan
 * 
 */
public final class DemandCSPointsTo implements PointsToAnalysis {

	@SuppressWarnings("serial")
	protected static final class AllocAndContextCache extends
			HashMap<AllocAndContext, Map<VarNode, CallingContextSet>> {
	}

	protected static final class CallingContextSet extends
			ArraySet<ImmutableStack<Integer>> {
	}

	protected final static class CallSiteAndContext extends
			Pair<Integer, ImmutableStack<Integer>> {

		public CallSiteAndContext(Integer callSite,
				ImmutableStack<Integer> callingContext) {
			super(callSite, callingContext);
		}
	}

	protected static final class CallSiteToTargetsMap extends
			HashSetMultiMap<CallSiteAndContext, SootMethod> {
	}

	protected static abstract class IncomingEdgeHandler {

		public abstract void handleAlloc(AllocNode allocNode,
				VarAndContext origVarAndContext);

		public abstract void handleMatchSrc(VarNode matchSrc,
				PointsToSetInternal intersection, VarNode loadBase,
				VarNode storeBase, VarAndContext origVarAndContext,
				SparkField field, boolean refine);

		abstract Object getResult();

		abstract void handleAssignSrc(VarAndContext newVarAndContext,
				VarAndContext origVarAndContext, AssignEdge assignEdge);

		abstract boolean shouldHandleSrc(VarNode src);

		boolean terminate() {
			return false;
		}

	}

	protected static class VarAndContext {

		final ImmutableStack<Integer> context;

		final VarNode var;

		public VarAndContext(VarNode var, ImmutableStack<Integer> context) {
		    assert var != null;
		    assert context != null;
			this.var = var;
			this.context = context;
		}

		public boolean equals(Object o) {
			if (o != null && o.getClass() == VarAndContext.class) {
				VarAndContext other = (VarAndContext) o;
				return var.equals(other.var) && context.equals(other.context);
			}
			return false;
		}

		public int hashCode() {
			return var.hashCode() + context.hashCode();
		}

		public String toString() {
			return var + " " + context;
		}
	}

	protected final static class VarContextAndUp extends VarAndContext {

		final ImmutableStack<Integer> upContext;

		public VarContextAndUp(VarNode var, ImmutableStack<Integer> context,
				ImmutableStack<Integer> upContext) {
			super(var, context);
			this.upContext = upContext;
		}

		public boolean equals(Object o) {
			if (o != null && o.getClass() == VarContextAndUp.class) {
				VarContextAndUp other = (VarContextAndUp) o;
				return var.equals(other.var) && context.equals(other.context)
						&& upContext.equals(other.upContext);
			}

			return false;
		}

		public int hashCode() {
			return var.hashCode() + context.hashCode() + upContext.hashCode();
		}

		public String toString() {
			return var + " " + context + " up " + upContext;
		}
	}

	public static boolean DEBUG = false;

	protected static final int DEBUG_NESTING = 15;

	protected static final int DEBUG_PASS = -1;

	protected static final boolean DEBUG_VIRT = DEBUG && true;

	protected static final int DEFAULT_MAX_PASSES = 10;

	protected static final int DEFAULT_MAX_TRAVERSAL = 75000;

	protected static final boolean DEFAULT_LAZY = true;

	/**
	 * if <code>true</code>, refine the pre-computed call graph
	 */
	private boolean refineCallGraph = true;
	
	protected static final ImmutableStack<Integer> EMPTY_CALLSTACK = ImmutableStack.<Integer> emptyStack();

  /**
	 * Make a default analysis. Assumes Spark has already run.
	 * 
	 * @return
	 */
	public static DemandCSPointsTo makeDefault() {
		return makeWithBudget(DEFAULT_MAX_TRAVERSAL, DEFAULT_MAX_PASSES, DEFAULT_LAZY);
	}

	public static DemandCSPointsTo makeWithBudget(int maxTraversal,
			int maxPasses, boolean lazy) {
		PAG pag = (PAG) Scene.v().getPointsToAnalysis();
		ContextSensitiveInfo csInfo = new ContextSensitiveInfo(pag);
		return new DemandCSPointsTo(csInfo, pag, maxTraversal, maxPasses, lazy);
	}

	protected final AllocAndContextCache allocAndContextCache = new AllocAndContextCache();

	protected Stack<Pair<Integer, ImmutableStack<Integer>>> callGraphStack = new Stack<Pair<Integer, ImmutableStack<Integer>>>();

	protected final CallSiteToTargetsMap callSiteToResolvedTargets = new CallSiteToTargetsMap();

	protected HashMap<List<Object>, Set<SootMethod>> callTargetsArgCache = new HashMap<List<Object>, Set<SootMethod>>();

	protected final Stack<VarAndContext> contextForAllocsStack = new Stack<VarAndContext>();

	protected Map<VarAndContext, Pair<PointsToSetInternal, AllocAndContextSet>> contextsForAllocsCache = new HashMap<VarAndContext, Pair<PointsToSetInternal, AllocAndContextSet>>();

	protected final ContextSensitiveInfo csInfo;

	/**
	 * if <code>true</code>, compute full points-to set for queried
	 * variable
	 */
	protected boolean doPointsTo;

	protected FieldCheckHeuristic fieldCheckHeuristic;

	protected HeuristicType heuristicType;
	
	protected FieldToEdgesMap fieldToLoads;

	protected FieldToEdgesMap fieldToStores;

	protected final int maxNodesPerPass;

	protected final int maxPasses;

	protected int nesting = 0;

	protected int numNodesTraversed;
	
	protected int numPasses = 0;

	protected final PAG pag;

	protected AllocAndContextSet pointsTo = null;

	protected final Set<CallSiteAndContext> queriedCallSites = new HashSet<CallSiteAndContext>();

	protected int recursionDepth = -1;

	protected boolean refiningCallSite = false;

	protected OTFMethodSCCManager sccManager;

	protected Map<VarContextAndUp, Map<AllocAndContext, CallingContextSet>> upContextCache = new HashMap<VarContextAndUp, Map<AllocAndContext, CallingContextSet>>();

	protected ValidMatches vMatches;
	
	protected Map<Local,PointsToSet> reachingObjectsCache, reachingObjectsCacheNoCGRefinement;

    protected boolean useCache;

	private final boolean lazy;

	public DemandCSPointsTo(ContextSensitiveInfo csInfo, PAG pag) {
		this(csInfo, pag, DEFAULT_MAX_TRAVERSAL, DEFAULT_MAX_PASSES, DEFAULT_LAZY);
	}

	public DemandCSPointsTo(ContextSensitiveInfo csInfo, PAG pag,
			int maxTraversal, int maxPasses, boolean lazy) {
		this.csInfo = csInfo;
		this.pag = pag;
		this.maxPasses = maxPasses;
		this.lazy = lazy;
		this.maxNodesPerPass = maxTraversal / maxPasses;
		this.heuristicType = HeuristicType.INCR;
		this.reachingObjectsCache = new HashMap<Local, PointsToSet>();
		this.reachingObjectsCacheNoCGRefinement = new HashMap<Local, PointsToSet>();
        this.useCache = true;
	}

	private void init() {
		this.fieldToStores = SootUtil.storesOnField(pag);
        this.fieldToLoads = SootUtil.loadsOnField(pag);
        this.vMatches = new ValidMatches(pag, fieldToStores);
	}

	public PointsToSet reachingObjects(Local l) {
		if(lazy)
			/*
			 * create a lazy points-to set; this will not actually compute context information until we ask whether this points-to set
			 * has a non-empty intersection with another points-to set and this intersection appears to be non-empty; when this is the case
			 * then the points-to set will call doReachingObjects(..) to refine itself
			 */			
			return new LazyContextSensitivePointsToSet(l,new WrappedPointsToSet((PointsToSetInternal) pag.reachingObjects(l)),this);
		else
			return doReachingObjects(l);
	}

	public PointsToSet doReachingObjects(Local l) {
		//lazy initialization
		if(fieldToStores==null) {
	        init();
		}
		PointsToSet result;
        Map<Local, PointsToSet> cache;
	    if(refineCallGraph) {  //we use different caches for different settings  
            cache = reachingObjectsCache;
	    } else {
            cache = reachingObjectsCacheNoCGRefinement;
	    }
        result = cache.get(l);           
	    if(result==null) {
    		result = computeReachingObjects(l);
    		if(useCache) {
	            cache.put(l, result);
    		}
	    } 	    
	    assert consistentResult(l,result);
	    return result;
	}

    /**
     * Returns <code>false</code> if an inconsistent computation occurred, i.e. if result
     * differs from the result computed by {@link #computeReachingObjects(Local)} on l.
     */
    private boolean consistentResult(Local l, PointsToSet result) {
        PointsToSet result2 = computeReachingObjects(l);
        if(!(result instanceof EqualsSupportingPointsToSet) || !(result2 instanceof EqualsSupportingPointsToSet)) {
            //cannot compare, assume everything is fine
            return true;
        }
        EqualsSupportingPointsToSet eq1 = (EqualsSupportingPointsToSet) result;
        EqualsSupportingPointsToSet eq2 = (EqualsSupportingPointsToSet) result2;
        return new PointsToSetEqualsWrapper(eq1).equals(new PointsToSetEqualsWrapper(eq2)); 
    }

    /**
     * Computes the possibly refined set of reaching objects for l.
     */
    protected PointsToSet computeReachingObjects(Local l) {
        VarNode v = pag.findLocalVarNode(l);
		if (v == null) {
		  //no reaching objects
		  return EmptyPointsToSet.v();
		}
        PointsToSet contextSensitiveResult = computeRefinedReachingObjects(v);
        if(contextSensitiveResult == null ) {
            //had to abort; return Spark's points-to set in a wrapper
            return new WrappedPointsToSet(v.getP2Set());
        } else {
            return contextSensitiveResult;    		    
        }
    }

    /**
     * Computes the refined set of reaching objects for l.
     * Returns <code>null</code> if refinement failed.
     */
    protected PointsToSet computeRefinedReachingObjects(VarNode v) {
        // must reset the refinement heuristic for each query
        this.fieldCheckHeuristic = HeuristicType.getHeuristic(
            heuristicType, pag.getTypeManager(), getMaxPasses());
        doPointsTo = true;
        numPasses = 0;
        PointsToSet contextSensitiveResult = null;
        while (true) {
        	numPasses++;
        	if (DEBUG_PASS != -1 && numPasses > DEBUG_PASS) {
        		break;
        	}
        	if (numPasses > maxPasses) {
        		break;
        	}
        	if (DEBUG) {
        		G.v().out.println("PASS " + numPasses);
        		G.v().out.println(fieldCheckHeuristic);
        	}
        	clearState();
        	pointsTo = new AllocAndContextSet();
        	try {
        		refineP2Set(new VarAndContext(v, EMPTY_CALLSTACK), null);
        		contextSensitiveResult = pointsTo;
        	} catch (TerminateEarlyException e) {
        	}
        	if (!fieldCheckHeuristic.runNewPass()) {
        		break;
        	}
        }
        return contextSensitiveResult;
    }

	protected boolean callEdgeInSCC(AssignEdge assignEdge) {
		boolean sameSCCAlready = false;
		assert assignEdge.isCallEdge();
		// assert assignEdge.getSrc() instanceof LocalVarNode :
		// assignEdge.getSrc() + " not LocalVarNode";
		if (!(assignEdge.getSrc() instanceof LocalVarNode)
				|| !(assignEdge.getDst() instanceof LocalVarNode)) {
			return false;
		}
		LocalVarNode src = (LocalVarNode) assignEdge.getSrc();
		LocalVarNode dst = (LocalVarNode) assignEdge.getDst();
		if (sccManager.inSameSCC(src.getMethod(), dst.getMethod())) {
			sameSCCAlready = true;
		}
		return sameSCCAlready;
	}

	protected CallingContextSet checkAllocAndContextCache(
			AllocAndContext allocAndContext, VarNode targetVar) {
		if (allocAndContextCache.containsKey(allocAndContext)) {
			Map<VarNode, CallingContextSet> m = allocAndContextCache
					.get(allocAndContext);
			if (m.containsKey(targetVar)) {
				return m.get(targetVar);
			}
		} else {
			allocAndContextCache.put(allocAndContext,
					new HashMap<VarNode, CallingContextSet>());
		}
		return null;
	}

	protected PointsToSetInternal checkContextsForAllocsCache(
			VarAndContext varAndContext, AllocAndContextSet ret,
			PointsToSetInternal locs) {
		PointsToSetInternal retSet = null;
		if (contextsForAllocsCache.containsKey(varAndContext)) {
			for (AllocAndContext allocAndContext : contextsForAllocsCache.get(
					varAndContext).getO2()) {
				if (locs.contains(allocAndContext.alloc)) {
					ret.add(allocAndContext);
				}
			}
			final PointsToSetInternal oldLocs = contextsForAllocsCache.get(
					varAndContext).getO1();
			final PointsToSetInternal tmpSet = new HybridPointsToSet(locs
					.getType(), pag);
			locs.forall(new P2SetVisitor() {

				@Override
				public void visit(Node n) {
					if (!oldLocs.contains(n)) {
						tmpSet.add(n);
					}
				}
			});
			retSet = tmpSet;
			oldLocs.addAll(tmpSet, null);
		} else {
			PointsToSetInternal storedSet = new HybridPointsToSet(locs
					.getType(), pag);
			storedSet.addAll(locs, null);
			contextsForAllocsCache.put(varAndContext,
					new Pair<PointsToSetInternal, AllocAndContextSet>(
							storedSet, new AllocAndContextSet()));
			retSet = locs;
		}
		return retSet;
	}

	/**
	 * check the computed points-to set of a variable against some predicate
	 * 
	 * @param v
	 *            the variable
	 * @param heuristic
	 *            how to refine match edges
	 * @param p2setPred
	 *            the predicate on the points-to set
	 * @return true if the p2setPred holds for the computed points-to set, or if
	 *         a points-to set cannot be computed in the budget; false otherwise
	 */
	protected boolean checkP2Set(VarNode v, HeuristicType heuristic,
			Predicate<Set<AllocAndContext>> p2setPred) {
		doPointsTo = true;
		// DEBUG = v.getNumber() == 150;
		this.fieldCheckHeuristic = HeuristicType.getHeuristic(heuristic, pag
				.getTypeManager(), getMaxPasses());
		numPasses = 0;
		while (true) {
			numPasses++;
			if (DEBUG_PASS != -1 && numPasses > DEBUG_PASS) {
				return true;
			}
			if (numPasses > maxPasses) {
				return true;
			}
			if (DEBUG) {
				G.v().out.println("PASS " + numPasses);
				G.v().out.println(fieldCheckHeuristic);
			}
			clearState();
			pointsTo = new AllocAndContextSet();
			boolean success = false;
			try {
				success = refineP2Set(new VarAndContext(v, EMPTY_CALLSTACK),
						null);
			} catch (TerminateEarlyException e) {
				success = false;
			}
			if (success) {
				if (p2setPred.test(pointsTo)) {
					return false;
				}
			} else {
				if (!fieldCheckHeuristic.runNewPass()) {
					return true;
				}
			}
		}

	}

	// protected boolean upContextsSane(CallingContextSet ret, AllocAndContext
	// allocAndContext, VarContextAndUp varContextAndUp) {
	// for (ImmutableStack<Integer> context : ret) {
	// ImmutableStack<Integer> fixedContext = fixUpContext(context,
	// allocAndContext, varContextAndUp);
	// if (!context.equals(fixedContext)) {
	// return false;
	// }
	// }
	// return true;
	// }
	//
	// protected CallingContextSet fixAllUpContexts(CallingContextSet contexts,
	// AllocAndContext allocAndContext, VarContextAndUp varContextAndUp) {
	// if (DEBUG) {
	// debugPrint("fixing up contexts");
	// }
	// CallingContextSet ret = new CallingContextSet();
	// for (ImmutableStack<Integer> context : contexts) {
	// ret.add(fixUpContext(context, allocAndContext, varContextAndUp));
	// }
	// return ret;
	// }
	//
	// protected ImmutableStack<Integer> fixUpContext(ImmutableStack<Integer>
	// context, AllocAndContext allocAndContext, VarContextAndUp
	// varContextAndUp) {
	//        
	// return null;
	// }

	protected CallingContextSet checkUpContextCache(
			VarContextAndUp varContextAndUp, AllocAndContext allocAndContext) {
		if (upContextCache.containsKey(varContextAndUp)) {
			Map<AllocAndContext, CallingContextSet> allocAndContextMap = upContextCache
					.get(varContextAndUp);
			if (allocAndContextMap.containsKey(allocAndContext)) {
				return allocAndContextMap.get(allocAndContext);
			}
		} else {
			upContextCache.put(varContextAndUp,
					new HashMap<AllocAndContext, CallingContextSet>());
		}
		return null;
	}

	protected void clearState() {
		allocAndContextCache.clear();
		callGraphStack.clear();
		callSiteToResolvedTargets.clear();
		queriedCallSites.clear();
		contextsForAllocsCache.clear();
		contextForAllocsStack.clear();
		upContextCache.clear();
		callTargetsArgCache.clear();
		sccManager = new OTFMethodSCCManager();
		numNodesTraversed = 0;
		nesting = 0;
		recursionDepth = -1;
	}

	/**
	 * compute a flows-to set for an allocation site. for now, we use a simple
	 * refinement strategy; just refine as much as possible, maintaining the
	 * smallest set of flows-to vars
	 * 
	 * @param alloc
	 * @param heuristic
	 * @return
	 */
	protected Set<VarNode> computeFlowsTo(AllocNode alloc,
			HeuristicType heuristic) {
		this.fieldCheckHeuristic = HeuristicType.getHeuristic(heuristic, pag
				.getTypeManager(), getMaxPasses());
		numPasses = 0;
		Set<VarNode> smallest = null;
		while (true) {
			numPasses++;
			if (DEBUG_PASS != -1 && numPasses > DEBUG_PASS) {
				return smallest;
			}
			if (numPasses > maxPasses) {
				return smallest;
			}
			if (DEBUG) {
				G.v().out.println("PASS " + numPasses);
				G.v().out.println(fieldCheckHeuristic);
			}
			clearState();
			Set<VarNode> result = null;
			try {
				result = getFlowsToHelper(new AllocAndContext(alloc,
						EMPTY_CALLSTACK));
			} catch (TerminateEarlyException e) {

			}
			if (result != null) {
				if (smallest == null || result.size() < smallest.size()) {
					smallest = result;
				}
			}
			if (!fieldCheckHeuristic.runNewPass()) {
				return smallest;
			}
		}
	}

	protected void debugPrint(String str) {
		if (nesting <= DEBUG_NESTING) {
			if (DEBUG_PASS == -1 || DEBUG_PASS == numPasses) {
				G.v().out.println(":" + nesting + " " + str);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see AAA.summary.Refiner#dumpPathForBadLoc(soot.jimple.spark.pag.VarNode,
	 *      soot.jimple.spark.pag.AllocNode)
	 */
	protected void dumpPathForLoc(VarNode v, final AllocNode badLoc,
			String filePrefix) {
		final HashSet<VarNode> visited = new HashSet<VarNode>();
		final DotPointerGraph dotGraph = new DotPointerGraph();
		final class Helper {
			boolean handle(VarNode curNode) {
				assert curNode.getP2Set().contains(badLoc);
				visited.add(curNode);
				Node[] newEdges = pag.allocInvLookup(curNode);
				for (int i = 0; i < newEdges.length; i++) {
					AllocNode alloc = (AllocNode) newEdges[i];
					if (alloc.equals(badLoc)) {
						dotGraph.addNew(alloc, curNode);
						return true;
					}
				}
				for (AssignEdge assignEdge : csInfo.getAssignEdges(curNode)) {
					VarNode other = assignEdge.getSrc();
					if (other.getP2Set().contains(badLoc)
							&& !visited.contains(other) && handle(other)) {
						if (assignEdge.isCallEdge()) {
							dotGraph.addCall(other, curNode, assignEdge
									.getCallSite());
						} else {
							dotGraph.addAssign(other, curNode);
						}
						return true;
					}
				}
				Node[] loadEdges = pag.loadInvLookup(curNode);
				for (int i = 0; i < loadEdges.length; i++) {
					FieldRefNode frNode = (FieldRefNode) loadEdges[i];
					SparkField field = frNode.getField();
					VarNode base = frNode.getBase();
					PointsToSetInternal baseP2Set = base.getP2Set();
					for (Pair<VarNode, VarNode> store : fieldToStores
							.get(field)) {
						if (store.getO2().getP2Set().hasNonEmptyIntersection(
								baseP2Set)) {
							VarNode matchSrc = store.getO1();
							if (matchSrc.getP2Set().contains(badLoc)
									&& !visited.contains(matchSrc)
									&& handle(matchSrc)) {
								dotGraph.addMatch(matchSrc, curNode);
								return true;
							}
						}
					}
				}
				return false;
			}
		}
		Helper h = new Helper();
		h.handle(v);
		// G.v().out.println(dotGraph.numEdges() + " edges on path");
		dotGraph.dump("tmp/" + filePrefix + v.getNumber() + "_"
				+ badLoc.getNumber() + ".dot");
	}

	protected Collection<AssignEdge> filterAssigns(final VarNode v,
			final ImmutableStack<Integer> callingContext, boolean forward,
			boolean refineVirtCalls) {
		Set<AssignEdge> assigns = forward ? csInfo.getAssignEdges(v) : csInfo
				.getAssignBarEdges(v);
		Collection<AssignEdge> realAssigns;
		boolean exitNode = forward ? SootUtil.isParamNode(v) : SootUtil
				.isRetNode(v);
		final boolean backward = !forward;
		if (exitNode && !callingContext.isEmpty()) {
			Integer topCallSite = callingContext.peek();
			realAssigns = new ArrayList<AssignEdge>();
			for (AssignEdge assignEdge : assigns) {
				assert (forward && assignEdge.isParamEdge())
						|| (backward && assignEdge.isReturnEdge()) : assignEdge;

				Integer assignEdgeCallSite = assignEdge.getCallSite();
				assert csInfo.getCallSiteTargets(assignEdgeCallSite).contains(
						((LocalVarNode) v).getMethod()) : assignEdge;
				if (topCallSite.equals(assignEdgeCallSite)
						|| callEdgeInSCC(assignEdge)) {
					realAssigns.add(assignEdge);
				}
			}
			// assert realAssigns.size() == 1;
		} else {
			if (assigns.size() > 1) {
				realAssigns = new ArrayList<AssignEdge>();
				for (AssignEdge assignEdge : assigns) {
					boolean enteringCall = forward ? assignEdge.isReturnEdge()
							: assignEdge.isParamEdge();
					if (enteringCall) {
						Integer callSite = assignEdge.getCallSite();
						if (csInfo.isVirtCall(callSite) && refineVirtCalls) {
							Set<SootMethod> targets = refineCallSite(assignEdge
									.getCallSite(), callingContext);
							LocalVarNode nodeInTargetMethod = forward ? (LocalVarNode) assignEdge
									.getSrc()
									: (LocalVarNode) assignEdge.getDst();
							if (targets
									.contains(nodeInTargetMethod.getMethod())) {
								realAssigns.add(assignEdge);
							}
						} else {
							realAssigns.add(assignEdge);
						}
					} else {
						realAssigns.add(assignEdge);
					}
				}
			} else {
				realAssigns = assigns;
			}
		}
		return realAssigns;
	}

	protected AllocAndContextSet findContextsForAllocs(
			final VarAndContext varAndContext, PointsToSetInternal locs) {
		if (contextForAllocsStack.contains(varAndContext)) {
			// recursion; check depth
			// we're fine for x = x.next
			int oldIndex = contextForAllocsStack.indexOf(varAndContext);
			if (oldIndex != contextForAllocsStack.size() - 1) {
				if (recursionDepth == -1) {
					recursionDepth = oldIndex + 1;
					if (DEBUG) {
						debugPrint("RECURSION depth = " + recursionDepth);
					}
				} else if (contextForAllocsStack.size() - oldIndex > 5) {
					// just give up
					throw new TerminateEarlyException();
				}
			}
		}
		contextForAllocsStack.push(varAndContext);
		final AllocAndContextSet ret = new AllocAndContextSet();
		final PointsToSetInternal realLocs = checkContextsForAllocsCache(
				varAndContext, ret, locs);
		if (realLocs.isEmpty()) {
			if (DEBUG) {
				debugPrint("cached result " + ret);
			}
			contextForAllocsStack.pop();
			return ret;
		}
		nesting++;
		if (DEBUG) {
			debugPrint("finding alloc contexts for " + varAndContext);
		}
		try {
			final Set<VarAndContext> marked = new HashSet<VarAndContext>();
			final Stack<VarAndContext> worklist = new Stack<VarAndContext>();
			final Propagator<VarAndContext> p = new Propagator<VarAndContext>(
					marked, worklist);
			p.prop(varAndContext);
			IncomingEdgeHandler edgeHandler = new IncomingEdgeHandler() {

				@Override
				public void handleAlloc(AllocNode allocNode,
						VarAndContext origVarAndContext) {
					if (realLocs.contains(allocNode)) {
						if (DEBUG) {
							debugPrint("found alloc " + allocNode);
						}
						ret.add(new AllocAndContext(allocNode,
								origVarAndContext.context));
					}
				}

				@Override
				public void handleMatchSrc(final VarNode matchSrc,
						PointsToSetInternal intersection, VarNode loadBase,
						VarNode storeBase, VarAndContext origVarAndContext,
						SparkField field, boolean refine) {
					if (DEBUG) {
						debugPrint("handling src " + matchSrc);
						debugPrint("intersection " + intersection);
					}
					if (!refine) {
						p.prop(new VarAndContext(matchSrc, EMPTY_CALLSTACK));
						return;
					}
					AllocAndContextSet allocContexts = findContextsForAllocs(
							new VarAndContext(loadBase,
									origVarAndContext.context), intersection);
					if (DEBUG) {
						debugPrint("alloc contexts " + allocContexts);
					}
					for (AllocAndContext allocAndContext : allocContexts) {
						if (DEBUG) {
							debugPrint("alloc and context " + allocAndContext);
						}
						CallingContextSet matchSrcContexts;
						if (fieldCheckHeuristic.validFromBothEnds(field)) {
							matchSrcContexts = findUpContextsForVar(
									allocAndContext, new VarContextAndUp(
											storeBase, EMPTY_CALLSTACK,
											EMPTY_CALLSTACK));
						} else {
							matchSrcContexts = findVarContextsFromAlloc(
									allocAndContext, storeBase);
						}
						for (ImmutableStack<Integer> matchSrcContext : matchSrcContexts) {
							// ret
							// .add(new Pair<AllocNode,
							// ImmutableStack<Integer>>(
							// (AllocNode) n,
							// matchSrcContext));
							// ret.addAll(findContextsForAllocs(matchSrc,
							// matchSrcContext, locs));
							p
									.prop(new VarAndContext(matchSrc,
											matchSrcContext));
						}

					}

				}

				@Override
				Object getResult() {
					return ret;
				}

				@Override
				void handleAssignSrc(VarAndContext newVarAndContext,
						VarAndContext origVarAndContext, AssignEdge assignEdge) {
					p.prop(newVarAndContext);
				}

				@Override
				boolean shouldHandleSrc(VarNode src) {
					return realLocs.hasNonEmptyIntersection(src.getP2Set());
				}

			};
			processIncomingEdges(edgeHandler, worklist);
			// update the cache
			if (recursionDepth != -1) {
				// if we're beyond recursion, don't cache anything
				if (contextForAllocsStack.size() > recursionDepth) {
					if (DEBUG) {
						debugPrint("REMOVING " + varAndContext);
						debugPrint(contextForAllocsStack.toString());
					}
					contextsForAllocsCache.remove(varAndContext);
				} else {
					assert contextForAllocsStack.size() == recursionDepth : recursionDepth
							+ " " + contextForAllocsStack;
					recursionDepth = -1;
					if (contextsForAllocsCache.containsKey(varAndContext)) {
						contextsForAllocsCache.get(varAndContext).getO2()
								.addAll(ret);
					} else {
						PointsToSetInternal storedSet = new HybridPointsToSet(
								locs.getType(), pag);
						storedSet.addAll(locs, null);
						contextsForAllocsCache
								.put(
										varAndContext,
										new Pair<PointsToSetInternal, AllocAndContextSet>(
												storedSet, ret));

					}
				}
			} else {
				if (contextsForAllocsCache.containsKey(varAndContext)) {
					contextsForAllocsCache.get(varAndContext).getO2().addAll(
							ret);
				} else {
					PointsToSetInternal storedSet = new HybridPointsToSet(locs
							.getType(), pag);
					storedSet.addAll(locs, null);
					contextsForAllocsCache.put(varAndContext,
							new Pair<PointsToSetInternal, AllocAndContextSet>(
									storedSet, ret));

				}
			}
			nesting--;
			return ret;
		} catch (CallSiteException e) {
			contextsForAllocsCache.remove(varAndContext);
			throw e;
		} finally {
			contextForAllocsStack.pop();
		}
	}

	protected CallingContextSet findUpContextsForVar(
			AllocAndContext allocAndContext, VarContextAndUp varContextAndUp) {
		final AllocNode alloc = allocAndContext.alloc;
		final ImmutableStack<Integer> allocContext = allocAndContext.context;
		CallingContextSet tmpSet = checkUpContextCache(varContextAndUp,
				allocAndContext);
		if (tmpSet != null) {
			return tmpSet;
		}
		final CallingContextSet ret = new CallingContextSet();
		upContextCache.get(varContextAndUp).put(allocAndContext, ret);
		nesting++;
		if (DEBUG) {
			debugPrint("finding up context for " + varContextAndUp + " to "
					+ alloc + " " + allocContext);
		}
		try {
			final Set<VarAndContext> marked = new HashSet<VarAndContext>();
			final Stack<VarAndContext> worklist = new Stack<VarAndContext>();
			final Propagator<VarAndContext> p = new Propagator<VarAndContext>(
					marked, worklist);
			p.prop(varContextAndUp);
			class UpContextEdgeHandler extends IncomingEdgeHandler {

				@Override
				public void handleAlloc(AllocNode allocNode,
						VarAndContext origVarAndContext) {
					VarContextAndUp contextAndUp = (VarContextAndUp) origVarAndContext;
					if (allocNode == alloc) {
						if (allocContext.topMatches(contextAndUp.context)) {
							ImmutableStack<Integer> reverse = contextAndUp.upContext
									.reverse();
							ImmutableStack<Integer> toAdd = allocContext
									.popAll(contextAndUp.context).pushAll(
											reverse);
							if (DEBUG) {
								debugPrint("found up context " + toAdd);
							}
							ret.add(toAdd);
						} else if (contextAndUp.context
								.topMatches(allocContext)) {
							ImmutableStack<Integer> toAdd = contextAndUp.upContext
									.reverse();
							if (DEBUG) {
								debugPrint("found up context " + toAdd);
							}
							ret.add(toAdd);
						}
					}
				}

				@Override
				public void handleMatchSrc(VarNode matchSrc,
						PointsToSetInternal intersection, VarNode loadBase,
						VarNode storeBase, VarAndContext origVarAndContext,
						SparkField field, boolean refine) {
					VarContextAndUp contextAndUp = (VarContextAndUp) origVarAndContext;
					if (DEBUG) {
						debugPrint("CHECKING " + alloc);
					}
					PointsToSetInternal tmp = new HybridPointsToSet(alloc
							.getType(), pag);
					tmp.add(alloc);
					AllocAndContextSet allocContexts = findContextsForAllocs(
							new VarAndContext(matchSrc, EMPTY_CALLSTACK), tmp);
					// Set allocContexts = Collections.singleton(new Object());
					if (!refine) {
						if (!allocContexts.isEmpty()) {
							ret.add(contextAndUp.upContext.reverse());
						}
					} else {
						if (!allocContexts.isEmpty()) {
							for (AllocAndContext t : allocContexts) {
								ImmutableStack<Integer> discoveredAllocContext = t.context;
								if (!allocContext
										.topMatches(discoveredAllocContext)) {
									continue;
								}
								ImmutableStack<Integer> trueAllocContext = allocContext
										.popAll(discoveredAllocContext);
								AllocAndContextSet allocAndContexts = findContextsForAllocs(
										new VarAndContext(storeBase,
												trueAllocContext), intersection);
								for (AllocAndContext allocAndContext : allocAndContexts) {
									// if (DEBUG)
									// G.v().out.println("alloc context "
									// + newAllocContext);
									// CallingContextSet upContexts;
									if (fieldCheckHeuristic
											.validFromBothEnds(field)) {
										ret
												.addAll(findUpContextsForVar(
														allocAndContext,
														new VarContextAndUp(
																loadBase,
																contextAndUp.context,
																contextAndUp.upContext)));
									} else {
										CallingContextSet tmpContexts = findVarContextsFromAlloc(
												allocAndContext, loadBase);
										// upContexts = new CallingContextSet();
										for (ImmutableStack<Integer> tmpContext : tmpContexts) {
											if (tmpContext
													.topMatches(contextAndUp.context)) {
												ImmutableStack<Integer> reverse = contextAndUp.upContext
														.reverse();
												ImmutableStack<Integer> toAdd = tmpContext
														.popAll(
																contextAndUp.context)
														.pushAll(reverse);
												ret.add(toAdd);
											}

										}
									}
								}
							}
						}
					}

				}

				@Override
				Object getResult() {
					return ret;
				}

				@Override
				void handleAssignSrc(VarAndContext newVarAndContext,
						VarAndContext origVarAndContext, AssignEdge assignEdge) {
					VarContextAndUp contextAndUp = (VarContextAndUp) origVarAndContext;
					ImmutableStack<Integer> upContext = contextAndUp.upContext;
					ImmutableStack<Integer> newUpContext = upContext;
					if (assignEdge.isParamEdge()
							&& contextAndUp.context.isEmpty()) {
						if (upContext.size() < ImmutableStack.getMaxSize()) {
							newUpContext = pushWithRecursionCheck(upContext,
									assignEdge);
						}
						;
					}
					p.prop(new VarContextAndUp(newVarAndContext.var,
							newVarAndContext.context, newUpContext));
				}

				@Override
				boolean shouldHandleSrc(VarNode src) {
					if (src instanceof GlobalVarNode) {
						// TODO properly handle case of global here; rare
						// but possible
						// reachedGlobal = true;
						// // for now, just give up
						throw new TerminateEarlyException();
					}
					return src.getP2Set().contains(alloc);
				}

			}
			;
			UpContextEdgeHandler edgeHandler = new UpContextEdgeHandler();
			processIncomingEdges(edgeHandler, worklist);
			nesting--;
			// if (edgeHandler.reachedGlobal) {
			// return fixAllUpContexts(ret, allocAndContext, varContextAndUp);
			// } else {
			// assert upContextsSane(ret, allocAndContext, varContextAndUp);
			// return ret;
			// }
			return ret;
		} catch (CallSiteException e) {
			upContextCache.remove(varContextAndUp);
			throw e;
		}
	}

	protected CallingContextSet findVarContextsFromAlloc(
			AllocAndContext allocAndContext, VarNode targetVar) {

		CallingContextSet tmpSet = checkAllocAndContextCache(allocAndContext,
				targetVar);
		if (tmpSet != null) {
			return tmpSet;
		}
		CallingContextSet ret = new CallingContextSet();
		allocAndContextCache.get(allocAndContext).put(targetVar, ret);
		try {
			HashSet<VarAndContext> marked = new HashSet<VarAndContext>();
			Stack<VarAndContext> worklist = new Stack<VarAndContext>();
			Propagator<VarAndContext> p = new Propagator<VarAndContext>(marked,
					worklist);
			AllocNode alloc = allocAndContext.alloc;
			ImmutableStack<Integer> allocContext = allocAndContext.context;
			Node[] newBarNodes = pag.allocLookup(alloc);
			for (int i = 0; i < newBarNodes.length; i++) {
				VarNode v = (VarNode) newBarNodes[i];
				p.prop(new VarAndContext(v, allocContext));
			}
			while (!worklist.isEmpty()) {
				incrementNodesTraversed();
				VarAndContext curVarAndContext = worklist.pop();
				if (DEBUG) {
					debugPrint("looking at " + curVarAndContext);
				}
				VarNode curVar = curVarAndContext.var;
				ImmutableStack<Integer> curContext = curVarAndContext.context;
				if (curVar == targetVar) {
					ret.add(curContext);
				}
				// assign
				Collection<AssignEdge> assignEdges = filterAssigns(curVar,
						curContext, false, true);
				for (AssignEdge assignEdge : assignEdges) {
					VarNode dst = assignEdge.getDst();
					ImmutableStack<Integer> newContext = curContext;
					if (assignEdge.isReturnEdge()) {
						if (!curContext.isEmpty()) {
							if (!callEdgeInSCC(assignEdge)) {
								assert assignEdge.getCallSite().equals(
										curContext.peek()) : assignEdge + " "
										+ curContext;
								newContext = curContext.pop();
							} else {
								newContext = popRecursiveCallSites(curContext);
							}
						}
					} else if (assignEdge.isParamEdge()) {
						if (DEBUG)
							debugPrint("entering call site "
									+ assignEdge.getCallSite());
						// if (!isRecursive(curContext, assignEdge)) {
						// newContext = curContext.push(assignEdge
						// .getCallSite());
						// }
						newContext = pushWithRecursionCheck(curContext,
								assignEdge);
					}
					if (assignEdge.isReturnEdge() && curContext.isEmpty()
							&& csInfo.isVirtCall(assignEdge.getCallSite())) {
						Set<SootMethod> targets = refineCallSite(assignEdge
								.getCallSite(), newContext);
						if (!targets.contains(((LocalVarNode) assignEdge
								.getDst()).getMethod())) {
							continue;
						}
					}
					if (dst instanceof GlobalVarNode) {
						newContext = EMPTY_CALLSTACK;
					}
					p.prop(new VarAndContext(dst, newContext));
				}
				// putfield_bars
				Set<VarNode> matchTargets = vMatches.vMatchLookup(curVar);
				Node[] pfTargets = pag.storeLookup(curVar);
				for (int i = 0; i < pfTargets.length; i++) {
					FieldRefNode frNode = (FieldRefNode) pfTargets[i];
					final VarNode storeBase = frNode.getBase();
					SparkField field = frNode.getField();
					// Pair<VarNode, FieldRefNode> putfield = new Pair<VarNode,
					// FieldRefNode>(curVar, frNode);
					for (Pair<VarNode, VarNode> load : fieldToLoads.get(field)) {
						final VarNode loadBase = load.getO2();
						final PointsToSetInternal loadBaseP2Set = loadBase
								.getP2Set();
						final PointsToSetInternal storeBaseP2Set = storeBase
								.getP2Set();
						final VarNode matchTgt = load.getO1();
						if (matchTargets.contains(matchTgt)) {
							if (DEBUG) {
								debugPrint("match source " + matchTgt);
							}
							PointsToSetInternal intersection = SootUtil
									.constructIntersection(storeBaseP2Set,
											loadBaseP2Set, pag);

							boolean checkField = fieldCheckHeuristic
									.validateMatchesForField(field);
							if (checkField) {
								AllocAndContextSet sharedAllocContexts = findContextsForAllocs(
										new VarAndContext(storeBase, curContext),
										intersection);
								for (AllocAndContext curAllocAndContext : sharedAllocContexts) {
									CallingContextSet upContexts;
									if (fieldCheckHeuristic
											.validFromBothEnds(field)) {
										upContexts = findUpContextsForVar(
												curAllocAndContext,
												new VarContextAndUp(loadBase,
														EMPTY_CALLSTACK,
														EMPTY_CALLSTACK));
									} else {
										upContexts = findVarContextsFromAlloc(
												curAllocAndContext, loadBase);
									}
									for (ImmutableStack<Integer> upContext : upContexts) {
										p.prop(new VarAndContext(matchTgt,
												upContext));
									}
								}
							} else {
								p.prop(new VarAndContext(matchTgt,
										EMPTY_CALLSTACK));
							}
							// h.handleMatchSrc(matchSrc, intersection,
							// storeBase,
							// loadBase, varAndContext, checkGetfield);
							// if (h.terminate())
							// return;
						}
					}

				}
			}
			return ret;
		} catch (CallSiteException e) {
			allocAndContextCache.remove(allocAndContext);
			throw e;
		}
	}

	@SuppressWarnings("unchecked")
	protected Set<SootMethod> getCallTargets(PointsToSetInternal p2Set,
			NumberedString methodStr, Type receiverType,
			Set<SootMethod> possibleTargets) {
		List<Object> args = Arrays.asList(p2Set, methodStr, receiverType,
				possibleTargets);
		if (callTargetsArgCache.containsKey(args)) {
			return callTargetsArgCache.get(args);
		}
		Set<Type> types = p2Set.possibleTypes();
		Set<SootMethod> ret = new HashSet<SootMethod>();
		for (Type type : types) {
			ret.addAll(getCallTargetsForType(type, methodStr, receiverType,
					possibleTargets));
		}
		callTargetsArgCache.put(args, ret);
		return ret;
	}

	protected Set<SootMethod> getCallTargetsForType(Type type,
			NumberedString methodStr, Type receiverType,
			Set<SootMethod> possibleTargets) {
		if (!pag.getTypeManager().castNeverFails(type, receiverType))
			return Collections.<SootMethod> emptySet();
		if (type instanceof AnySubType) {
			AnySubType any = (AnySubType) type;
			RefType refType = any.getBase();
			if (pag.getTypeManager().getFastHierarchy().canStoreType(
					receiverType, refType)
					|| pag.getTypeManager().getFastHierarchy().canStoreType(
							refType, receiverType)) {
				return possibleTargets;
			} else {
				return Collections.<SootMethod> emptySet();
			}
		}
		if (type instanceof ArrayType) {
			// we'll invoke the java.lang.Object method in this
			// case
			// Assert.chk(varNodeType.toString().equals("java.lang.Object"));
			type = Scene.v().getSootClass("java.lang.Object").getType();
		}
		RefType refType = (RefType) type;
		SootMethod targetMethod = null;
		targetMethod = VirtualCalls.v().resolveNonSpecial(refType, methodStr);
		return Collections.<SootMethod> singleton(targetMethod);

	}

	protected Set<VarNode> getFlowsToHelper(AllocAndContext allocAndContext) {
		Set<VarNode> ret = new ArraySet<VarNode>();

		try {
			HashSet<VarAndContext> marked = new HashSet<VarAndContext>();
			Stack<VarAndContext> worklist = new Stack<VarAndContext>();
			Propagator<VarAndContext> p = new Propagator<VarAndContext>(marked,
					worklist);
			AllocNode alloc = allocAndContext.alloc;
			ImmutableStack<Integer> allocContext = allocAndContext.context;
			Node[] newBarNodes = pag.allocLookup(alloc);
			for (int i = 0; i < newBarNodes.length; i++) {
				VarNode v = (VarNode) newBarNodes[i];
				ret.add(v);
				p.prop(new VarAndContext(v, allocContext));
			}
			while (!worklist.isEmpty()) {
				incrementNodesTraversed();
				VarAndContext curVarAndContext = worklist.pop();
				if (DEBUG) {
					debugPrint("looking at " + curVarAndContext);
				}
				VarNode curVar = curVarAndContext.var;
				ImmutableStack<Integer> curContext = curVarAndContext.context;
				ret.add(curVar);
				// assign
				Collection<AssignEdge> assignEdges = filterAssigns(curVar,
						curContext, false, true);
				for (AssignEdge assignEdge : assignEdges) {
					VarNode dst = assignEdge.getDst();
					ImmutableStack<Integer> newContext = curContext;
					if (assignEdge.isReturnEdge()) {
						if (!curContext.isEmpty()) {
							if (!callEdgeInSCC(assignEdge)) {
								assert assignEdge.getCallSite().equals(
										curContext.peek()) : assignEdge + " "
										+ curContext;
								newContext = curContext.pop();
							} else {
								newContext = popRecursiveCallSites(curContext);
							}
						}
					} else if (assignEdge.isParamEdge()) {
						if (DEBUG)
							debugPrint("entering call site "
									+ assignEdge.getCallSite());
						// if (!isRecursive(curContext, assignEdge)) {
						// newContext = curContext.push(assignEdge
						// .getCallSite());
						// }
						newContext = pushWithRecursionCheck(curContext,
								assignEdge);
					}
					if (assignEdge.isReturnEdge() && curContext.isEmpty()
							&& csInfo.isVirtCall(assignEdge.getCallSite())) {
						Set<SootMethod> targets = refineCallSite(assignEdge
								.getCallSite(), newContext);
						if (!targets.contains(((LocalVarNode) assignEdge
								.getDst()).getMethod())) {
							continue;
						}
					}
					if (dst instanceof GlobalVarNode) {
						newContext = EMPTY_CALLSTACK;
					}
					p.prop(new VarAndContext(dst, newContext));
				}
				// putfield_bars
				Set<VarNode> matchTargets = vMatches.vMatchLookup(curVar);
				Node[] pfTargets = pag.storeLookup(curVar);
				for (int i = 0; i < pfTargets.length; i++) {
					FieldRefNode frNode = (FieldRefNode) pfTargets[i];
					final VarNode storeBase = frNode.getBase();
					SparkField field = frNode.getField();
					// Pair<VarNode, FieldRefNode> putfield = new Pair<VarNode,
					// FieldRefNode>(curVar, frNode);
					for (Pair<VarNode, VarNode> load : fieldToLoads.get(field)) {
						final VarNode loadBase = load.getO2();
						final PointsToSetInternal loadBaseP2Set = loadBase
								.getP2Set();
						final PointsToSetInternal storeBaseP2Set = storeBase
								.getP2Set();
						final VarNode matchTgt = load.getO1();
						if (matchTargets.contains(matchTgt)) {
							if (DEBUG) {
								debugPrint("match source " + matchTgt);
							}
							PointsToSetInternal intersection = SootUtil
									.constructIntersection(storeBaseP2Set,
											loadBaseP2Set, pag);

							boolean checkField = fieldCheckHeuristic
									.validateMatchesForField(field);
							if (checkField) {
								AllocAndContextSet sharedAllocContexts = findContextsForAllocs(
										new VarAndContext(storeBase, curContext),
										intersection);
								for (AllocAndContext curAllocAndContext : sharedAllocContexts) {
									CallingContextSet upContexts;
									if (fieldCheckHeuristic
											.validFromBothEnds(field)) {
										upContexts = findUpContextsForVar(
												curAllocAndContext,
												new VarContextAndUp(loadBase,
														EMPTY_CALLSTACK,
														EMPTY_CALLSTACK));
									} else {
										upContexts = findVarContextsFromAlloc(
												curAllocAndContext, loadBase);
									}
									for (ImmutableStack<Integer> upContext : upContexts) {
										p.prop(new VarAndContext(matchTgt,
												upContext));
									}
								}
							} else {
								p.prop(new VarAndContext(matchTgt,
										EMPTY_CALLSTACK));
							}
							// h.handleMatchSrc(matchSrc, intersection,
							// storeBase,
							// loadBase, varAndContext, checkGetfield);
							// if (h.terminate())
							// return;
						}
					}

				}
			}
			return ret;
		} catch (CallSiteException e) {
			allocAndContextCache.remove(allocAndContext);
			throw e;
		}
	}

	protected int getMaxPasses() {
		return maxPasses;
	}

	protected void incrementNodesTraversed() {
		numNodesTraversed++;
		if (numNodesTraversed > maxNodesPerPass) {
			throw new TerminateEarlyException();
		}
	}

	@SuppressWarnings("unused")
	protected boolean isRecursive(ImmutableStack<Integer> context,
			AssignEdge assignEdge) {
		boolean sameSCCAlready = callEdgeInSCC(assignEdge);
		if (sameSCCAlready) {
			return true;
		}
		Integer callSite = assignEdge.getCallSite();
		if (context.contains(callSite)) {
			Set<SootMethod> toBeCollapsed = new ArraySet<SootMethod>();
			int callSiteInd = 0;
			for (; callSiteInd < context.size()
					&& !context.get(callSiteInd).equals(callSite); callSiteInd++)
				;
			for (; callSiteInd < context.size(); callSiteInd++) {
				toBeCollapsed.add(csInfo.getInvokingMethod(context
						.get(callSiteInd)));
			}
			sccManager.makeSameSCC(toBeCollapsed);
			return true;
		}
		return false;
	}

	protected boolean isRecursiveCallSite(Integer callSite) {
		SootMethod invokingMethod = csInfo.getInvokingMethod(callSite);
		SootMethod invokedMethod = csInfo.getInvokedMethod(callSite);
		return sccManager.inSameSCC(invokingMethod, invokedMethod);
	}

	@SuppressWarnings("unused")
	protected Set<VarNode> nodesPropagatedThrough(final VarNode source,
			final PointsToSetInternal allocs) {
		final Set<VarNode> marked = new HashSet<VarNode>();
		final Stack<VarNode> worklist = new Stack<VarNode>();
		Propagator<VarNode> p = new Propagator<VarNode>(marked, worklist);
		p.prop(source);
		while (!worklist.isEmpty()) {
			VarNode curNode = worklist.pop();
			Node[] assignSources = pag.simpleInvLookup(curNode);
			for (int i = 0; i < assignSources.length; i++) {
				VarNode assignSrc = (VarNode) assignSources[i];
				if (assignSrc.getP2Set().hasNonEmptyIntersection(allocs)) {
					p.prop(assignSrc);
				}
			}
			Set<VarNode> matchSources = vMatches.vMatchInvLookup(curNode);
			for (VarNode matchSrc : matchSources) {
				if (matchSrc.getP2Set().hasNonEmptyIntersection(allocs)) {
					p.prop(matchSrc);
				}
			}
		}
		return marked;
	}

	protected ImmutableStack<Integer> popRecursiveCallSites(
			ImmutableStack<Integer> context) {
		ImmutableStack<Integer> ret = context;
		while (!ret.isEmpty() && isRecursiveCallSite(ret.peek())) {
			ret = ret.pop();
		}
		return ret;
	}

	protected void processIncomingEdges(IncomingEdgeHandler h,
			Stack<VarAndContext> worklist) {
		while (!worklist.isEmpty()) {
			incrementNodesTraversed();
			VarAndContext varAndContext = worklist.pop();
			if (DEBUG) {
				debugPrint("looking at " + varAndContext);
			}
			VarNode v = varAndContext.var;
			ImmutableStack<Integer> callingContext = varAndContext.context;
			Node[] newEdges = pag.allocInvLookup(v);
			for (int i = 0; i < newEdges.length; i++) {
				AllocNode allocNode = (AllocNode) newEdges[i];
				h.handleAlloc(allocNode, varAndContext);
				if (h.terminate()) {
					return;
				}
			}
			Collection<AssignEdge> assigns = filterAssigns(v, callingContext,
					true, true);
			for (AssignEdge assignEdge : assigns) {
				VarNode src = assignEdge.getSrc();
				// if (DEBUG) {
				// G.v().out.println("assign src " + src);
				// }
				if (h.shouldHandleSrc(src)) {
					ImmutableStack<Integer> newContext = callingContext;
					if (assignEdge.isParamEdge()) {
						if (!callingContext.isEmpty()) {
							if (!callEdgeInSCC(assignEdge)) {
								assert assignEdge.getCallSite().equals(
										callingContext.peek()) : assignEdge
										+ " " + callingContext;
								newContext = callingContext.pop();
							} else {
								newContext = popRecursiveCallSites(callingContext);
							}
						}
						// } else if (refiningCallSite) {
						// if (!fieldCheckHeuristic.aggressiveVirtCallRefine())
						// {
						// // throw new CallSiteException();
						// }
						// }
					} else if (assignEdge.isReturnEdge()) {
						if (DEBUG)
							debugPrint("entering call site "
									+ assignEdge.getCallSite());
						// if (!isRecursive(callingContext, assignEdge)) {
						// newContext = callingContext.push(assignEdge
						// .getCallSite());
						// }
						newContext = pushWithRecursionCheck(callingContext,
								assignEdge);
					}
					if (assignEdge.isParamEdge()) {
						Integer callSite = assignEdge.getCallSite();
						if (csInfo.isVirtCall(callSite) && !weirdCall(callSite)) {
							Set<SootMethod> targets = refineCallSite(callSite,
									newContext);
							if (DEBUG) {
								debugPrint(targets.toString());
							}
							SootMethod targetMethod = ((LocalVarNode) assignEdge
									.getDst()).getMethod();
							if (!targets.contains(targetMethod)) {
								if (DEBUG) {
									debugPrint("skipping call because of call graph");
								}
								continue;
							}
						}
					}
					if (src instanceof GlobalVarNode) {
						newContext = EMPTY_CALLSTACK;
					}
					h.handleAssignSrc(new VarAndContext(src, newContext),
							varAndContext, assignEdge);
					if (h.terminate()) {
						return;
					}

				}
			}
			Set<VarNode> matchSources = vMatches.vMatchInvLookup(v);
			Node[] loads = pag.loadInvLookup(v);
			for (int i = 0; i < loads.length; i++) {
				FieldRefNode frNode = (FieldRefNode) loads[i];
				final VarNode loadBase = frNode.getBase();
				SparkField field = frNode.getField();
				// Pair<VarNode, FieldRefNode> getfield = new Pair<VarNode,
				// FieldRefNode>(v, frNode);
				for (Pair<VarNode, VarNode> store : fieldToStores.get(field)) {
					final VarNode storeBase = store.getO2();
					final PointsToSetInternal storeBaseP2Set = storeBase
							.getP2Set();
					final PointsToSetInternal loadBaseP2Set = loadBase
							.getP2Set();
					final VarNode matchSrc = store.getO1();
					if (matchSources.contains(matchSrc)) {
						if (h.shouldHandleSrc(matchSrc)) {
							if (DEBUG) {
								debugPrint("match source " + matchSrc);
							}
							PointsToSetInternal intersection = SootUtil
									.constructIntersection(storeBaseP2Set,
											loadBaseP2Set, pag);

							boolean checkGetfield = fieldCheckHeuristic
									.validateMatchesForField(field);

							h.handleMatchSrc(matchSrc, intersection, loadBase,
									storeBase, varAndContext, field,
									checkGetfield);
							if (h.terminate())
								return;
						}
					}
				}
			}
		}
	}

	protected ImmutableStack<Integer> pushWithRecursionCheck(
			ImmutableStack<Integer> context, AssignEdge assignEdge) {
		boolean foundRecursion = callEdgeInSCC(assignEdge);
		if (!foundRecursion) {
			Integer callSite = assignEdge.getCallSite();
			if (context.contains(callSite)) {
				foundRecursion = true;
				if (DEBUG) {
					debugPrint("RECURSION!!!");
				}
				// TODO properly collapse recursive methods
				if (true) {
					throw new TerminateEarlyException();
				}
				Set<SootMethod> toBeCollapsed = new ArraySet<SootMethod>();
				int callSiteInd = 0;
				for (; callSiteInd < context.size()
						&& !context.get(callSiteInd).equals(callSite); callSiteInd++)
					;
				// int numToPop = 0;
				for (; callSiteInd < context.size(); callSiteInd++) {
					toBeCollapsed.add(csInfo.getInvokingMethod(context
							.get(callSiteInd)));
					// numToPop++;
				}
				sccManager.makeSameSCC(toBeCollapsed);
				// ImmutableStack<Integer> poppedContext = context;
				// for (int i = 0; i < numToPop; i++) {
				// poppedContext = poppedContext.pop();
				// }
				// if (DEBUG) {
				// debugPrint("new stack " + poppedContext);
				// }
				// return poppedContext;
			}
		}
		if (foundRecursion) {
			ImmutableStack<Integer> popped = popRecursiveCallSites(context);
			if (DEBUG) {
				debugPrint("popped stack " + popped);
			}
			return popped;
		} else {
			return context.push(assignEdge.getCallSite());
		}
	}

	protected boolean refineAlias(VarNode v1, VarNode v2,
			PointsToSetInternal intersection, HeuristicType heuristic) {
		if (refineAliasInternal(v1, v2, intersection, heuristic))
			return true;
		if (refineAliasInternal(v2, v1, intersection, heuristic))
			return true;
		return false;
	}

	protected boolean refineAliasInternal(VarNode v1, VarNode v2,
			PointsToSetInternal intersection, HeuristicType heuristic) {
		this.fieldCheckHeuristic = HeuristicType.getHeuristic(heuristic, pag
				.getTypeManager(), getMaxPasses());
		numPasses = 0;
		while (true) {
			numPasses++;
			if (DEBUG_PASS != -1 && numPasses > DEBUG_PASS) {
				return false;
			}
			if (numPasses > maxPasses) {
				return false;
			}
			if (DEBUG) {
				G.v().out.println("PASS " + numPasses);
				G.v().out.println(fieldCheckHeuristic);
			}
			clearState();
			boolean success = false;
			try {
				AllocAndContextSet allocAndContexts = findContextsForAllocs(
						new VarAndContext(v1, EMPTY_CALLSTACK), intersection);
				boolean emptyIntersection = true;
				for (AllocAndContext allocAndContext : allocAndContexts) {
					CallingContextSet upContexts = findUpContextsForVar(
							allocAndContext, new VarContextAndUp(v2,
									EMPTY_CALLSTACK, EMPTY_CALLSTACK));
					if (!upContexts.isEmpty()) {
						emptyIntersection = false;
						break;
					}
				}
				success = emptyIntersection;
			} catch (TerminateEarlyException e) {
				success = false;
			}
			if (success) {
				G.v().out.println("took " + numPasses + " passes");
				return true;
			} else {
				if (!fieldCheckHeuristic.runNewPass()) {
					return false;
				}
			}
		}
	}

	protected Set<SootMethod> refineCallSite(Integer callSite,
			ImmutableStack<Integer> origContext) {
		CallSiteAndContext callSiteAndContext = new CallSiteAndContext(
				callSite, origContext);
		if (queriedCallSites.contains(callSiteAndContext)) {
			// if (DEBUG_VIRT) {
			// final SootMethod invokedMethod =
			// csInfo.getInvokedMethod(callSite);
			// final VarNode receiver =
			// csInfo.getReceiverForVirtCallSite(callSite);
			// debugPrint("call of " + invokedMethod + " on " + receiver + " "
			// + origContext + " goes to "
			// + callSiteToResolvedTargets.get(callSiteAndContext));
			// }
			return callSiteToResolvedTargets.get(callSiteAndContext);
		}
		if (callGraphStack.contains(callSiteAndContext)) {
			return Collections.<SootMethod> emptySet();
		} else {
			callGraphStack.push(callSiteAndContext);
		}
		final VarNode receiver = csInfo.getReceiverForVirtCallSite(callSite);
		final Type receiverType = receiver.getType();
		final SootMethod invokedMethod = csInfo.getInvokedMethod(callSite);
		final NumberedString methodSig = invokedMethod
				.getNumberedSubSignature();
		final Set<SootMethod> allTargets = csInfo.getCallSiteTargets(callSite);
		if (!refineCallGraph) {
		 callGraphStack.pop();
		 return allTargets;
		}
		if (DEBUG_VIRT) {
			debugPrint("refining call to " + invokedMethod + " on " + receiver
					+ " " + origContext);
		}
		final HashSet<VarAndContext> marked = new HashSet<VarAndContext>();
		final Stack<VarAndContext> worklist = new Stack<VarAndContext>();
		final class Helper {

			void prop(VarAndContext varAndContext) {
				if (marked.add(varAndContext)) {
					worklist.push(varAndContext);
				}
			}
		}
		;
		final Helper h = new Helper();
		h.prop(new VarAndContext(receiver, origContext));
		while (!worklist.isEmpty()) {
			incrementNodesTraversed();
			VarAndContext curVarAndContext = worklist.pop();
			if (DEBUG_VIRT) {
				debugPrint("virt looking at " + curVarAndContext);
			}
			VarNode curVar = curVarAndContext.var;
			ImmutableStack<Integer> curContext = curVarAndContext.context;
			// Set<SootMethod> curVarTargets = getCallTargets(curVar.getP2Set(),
			// methodSig, receiverType, allTargets);
			// if (curVarTargets.size() <= 1) {
			// for (SootMethod method : curVarTargets) {
			// callSiteToResolvedTargets.put(callSiteAndContext, method);
			// }
			// continue;
			// }
			Node[] newNodes = pag.allocInvLookup(curVar);
			for (int i = 0; i < newNodes.length; i++) {
				AllocNode allocNode = (AllocNode) newNodes[i];
				for (SootMethod method : getCallTargetsForType(allocNode
						.getType(), methodSig, receiverType, allTargets)) {
					callSiteToResolvedTargets.put(callSiteAndContext, method);
				}
			}
			Collection<AssignEdge> assigns = filterAssigns(curVar, curContext,
					true, true);
			for (AssignEdge assignEdge : assigns) {
				VarNode src = assignEdge.getSrc();
				ImmutableStack<Integer> newContext = curContext;
				if (assignEdge.isParamEdge()) {
					if (!curContext.isEmpty()) {
						if (!callEdgeInSCC(assignEdge)) {
							assert assignEdge.getCallSite().equals(
									curContext.peek());
							newContext = curContext.pop();
						} else {
							newContext = popRecursiveCallSites(curContext);
						}
					} else {
						callSiteToResolvedTargets.putAll(callSiteAndContext,
								allTargets);
						// if (DEBUG) {
						// debugPrint("giving up on virt");
						// }
						continue;
					}
				} else if (assignEdge.isReturnEdge()) {
					// if (DEBUG)
					// G.v().out.println("entering call site "
					// + assignEdge.getCallSite());
					// if (!isRecursive(curContext, assignEdge)) {
					// newContext = curContext.push(assignEdge.getCallSite());
					// }
					newContext = pushWithRecursionCheck(curContext, assignEdge);
				} else if (src instanceof GlobalVarNode) {
					newContext = EMPTY_CALLSTACK;
				}
				h.prop(new VarAndContext(src, newContext));
			}
			// TODO respect heuristic
			Set<VarNode> matchSources = vMatches.vMatchInvLookup(curVar);
			final boolean oneMatch = matchSources.size() == 1;
			Node[] loads = pag.loadInvLookup(curVar);
			for (int i = 0; i < loads.length; i++) {
				FieldRefNode frNode = (FieldRefNode) loads[i];
				final VarNode loadBase = frNode.getBase();
				SparkField field = frNode.getField();
				for (Pair<VarNode, VarNode> store : fieldToStores.get(field)) {
					final VarNode storeBase = store.getO2();
					final PointsToSetInternal storeBaseP2Set = storeBase
							.getP2Set();
					final PointsToSetInternal loadBaseP2Set = loadBase
							.getP2Set();
					final VarNode matchSrc = store.getO1();
					if (matchSources.contains(matchSrc)) {
						// optimize for common case of constructor init
						boolean skipMatch = false;
						if (oneMatch) {
							PointsToSetInternal matchSrcPTo = matchSrc
									.getP2Set();
							Set<SootMethod> matchSrcCallTargets = getCallTargets(
									matchSrcPTo, methodSig, receiverType,
									allTargets);
							if (matchSrcCallTargets.size() <= 1) {
								skipMatch = true;
								for (SootMethod method : matchSrcCallTargets) {
									callSiteToResolvedTargets.put(
											callSiteAndContext, method);
								}
							}
						}
						if (!skipMatch) {
							final PointsToSetInternal intersection = SootUtil
									.constructIntersection(storeBaseP2Set,
											loadBaseP2Set, pag);
							AllocAndContextSet allocContexts = null;
							boolean oldRefining = refiningCallSite;
							int oldNesting = nesting;
							try {
								refiningCallSite = true;
								allocContexts = findContextsForAllocs(
										new VarAndContext(loadBase, curContext),
										intersection);
							} catch (CallSiteException e) {
								callSiteToResolvedTargets.putAll(
										callSiteAndContext, allTargets);
								continue;
							} finally {
								refiningCallSite = oldRefining;
								nesting = oldNesting;
							}
							for (AllocAndContext allocAndContext : allocContexts) {
								CallingContextSet matchSrcContexts;
								if (fieldCheckHeuristic
										.validFromBothEnds(field)) {
									matchSrcContexts = findUpContextsForVar(
											allocAndContext,
											new VarContextAndUp(storeBase,
													EMPTY_CALLSTACK,
													EMPTY_CALLSTACK));
								} else {
									matchSrcContexts = findVarContextsFromAlloc(
											allocAndContext, storeBase);
								}
								for (ImmutableStack<Integer> matchSrcContext : matchSrcContexts) {
									VarAndContext newVarAndContext = new VarAndContext(
											matchSrc, matchSrcContext);
									h.prop(newVarAndContext);
								}
							}

						}

					}
				}
			}

		}
		if (DEBUG_VIRT) {
			debugPrint("call of " + invokedMethod + " on " + receiver + " "
					+ origContext + " goes to "
					+ callSiteToResolvedTargets.get(callSiteAndContext));
		}
		callGraphStack.pop();
		queriedCallSites.add(callSiteAndContext);
		return callSiteToResolvedTargets.get(callSiteAndContext);

	}

	protected boolean refineP2Set(VarAndContext varAndContext,
			final PointsToSetInternal badLocs) {
		nesting++;
		if (DEBUG) {
			debugPrint("refining " + varAndContext);
		}
		final Set<VarAndContext> marked = new HashSet<VarAndContext>();
		final Stack<VarAndContext> worklist = new Stack<VarAndContext>();
		final Propagator<VarAndContext> p = new Propagator<VarAndContext>(
				marked, worklist);
		p.prop(varAndContext);
		IncomingEdgeHandler edgeHandler = new IncomingEdgeHandler() {

			boolean success = true;

			@Override
			public void handleAlloc(AllocNode allocNode,
					VarAndContext origVarAndContext) {
				if (doPointsTo && pointsTo != null) {
					pointsTo.add(new AllocAndContext(allocNode,
							origVarAndContext.context));
				} else {
					if (badLocs.contains(allocNode)) {
						success = false;
					}
				}
			}

			@Override
			public void handleMatchSrc(VarNode matchSrc,
					PointsToSetInternal intersection, VarNode loadBase,
					VarNode storeBase, VarAndContext origVarAndContext,
					SparkField field, boolean refine) {
				AllocAndContextSet allocContexts = findContextsForAllocs(
						new VarAndContext(loadBase, origVarAndContext.context),
						intersection);
				for (AllocAndContext allocAndContext : allocContexts) {
					if (DEBUG) {
						debugPrint("alloc and context " + allocAndContext);
					}
					CallingContextSet matchSrcContexts;
					if (fieldCheckHeuristic.validFromBothEnds(field)) {
						matchSrcContexts = findUpContextsForVar(
								allocAndContext, new VarContextAndUp(storeBase,
										EMPTY_CALLSTACK, EMPTY_CALLSTACK));
					} else {
						matchSrcContexts = findVarContextsFromAlloc(
								allocAndContext, storeBase);
					}
					for (ImmutableStack<Integer> matchSrcContext : matchSrcContexts) {
						if (DEBUG)
							debugPrint("match source context "
									+ matchSrcContext);
						VarAndContext newVarAndContext = new VarAndContext(
								matchSrc, matchSrcContext);
						p.prop(newVarAndContext);
					}
				}
			}

			Object getResult() {
				return Boolean.valueOf(success);
			}

			@Override
			void handleAssignSrc(VarAndContext newVarAndContext,
					VarAndContext origVarAndContext, AssignEdge assignEdge) {
				p.prop(newVarAndContext);
			}

			@Override
			boolean shouldHandleSrc(VarNode src) {
				if (doPointsTo) {
					return true;
				} else {
					return src.getP2Set().hasNonEmptyIntersection(badLocs);
				}
			}

			boolean terminate() {
				return !success;
			}
		};
		processIncomingEdges(edgeHandler, worklist);
		nesting--;
		return (Boolean) edgeHandler.getResult();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see AAA.summary.Refiner#refineP2Set(soot.jimple.spark.pag.VarNode,
	 *      soot.jimple.spark.sets.PointsToSetInternal)
	 */
	protected boolean refineP2Set(VarNode v, PointsToSetInternal badLocs,
			HeuristicType heuristic) {
		// G.v().out.println(badLocs);
		this.doPointsTo = false;
		this.fieldCheckHeuristic = HeuristicType.getHeuristic(heuristic, pag
				.getTypeManager(), getMaxPasses());
		try {
			numPasses = 0;
			while (true) {
				numPasses++;
				if (DEBUG_PASS != -1 && numPasses > DEBUG_PASS) {
					return false;
				}
				if (numPasses > maxPasses) {
					return false;
				}
				if (DEBUG) {
					G.v().out.println("PASS " + numPasses);
					G.v().out.println(fieldCheckHeuristic);
				}
				clearState();
				boolean success = false;
				try {
					success = refineP2Set(
							new VarAndContext(v, EMPTY_CALLSTACK), badLocs);
				} catch (TerminateEarlyException e) {
					success = false;
				}
				if (success) {

					return true;
				} else {
					if (!fieldCheckHeuristic.runNewPass()) {
						return false;
					}
				}
			}
		} finally {
		}
	}

	protected boolean weirdCall(Integer callSite) {
		SootMethod invokedMethod = csInfo.getInvokedMethod(callSite);
		return SootUtil.isThreadStartMethod(invokedMethod)
				|| SootUtil.isNewInstanceMethod(invokedMethod);
	}

	/**
	 * Currently not implemented.
	 * 
	 * @throws UnsupportedOperationException
	 *             always
	 */
	public PointsToSet reachingObjects(Context c, Local l) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Currently not implemented.
	 * 
	 * @throws UnsupportedOperationException
	 *             always
	 */
	public PointsToSet reachingObjects(Context c, Local l, SootField f) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Currently not implemented.
	 * 
	 * @throws UnsupportedOperationException
	 *             always
	 */
	public PointsToSet reachingObjects(Local l, SootField f) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Currently not implemented.
	 * 
	 * @throws UnsupportedOperationException
	 *             always
	 */
	public PointsToSet reachingObjects(PointsToSet s, SootField f) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Currently not implemented.
	 * 
	 * @throws UnsupportedOperationException
	 *             always
	 */
	public PointsToSet reachingObjects(SootField f) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Currently not implemented.
	 * 
	 * @throws UnsupportedOperationException
	 *             always
	 */
	public PointsToSet reachingObjectsOfArrayElement(PointsToSet s) {
		throw new UnsupportedOperationException();
	}

	/**
	 * @return returns the (SPARK) pointer assignment graph
	 */
	public PAG getPAG() {
		return pag;
	}
	
	/**
	 * @return <code>true</code> is caching is enabled
	 */
	public boolean usesCache() {
	    return useCache;
	}
	
	/**
	 * enables caching 
	 */
	public void enableCache() {
	    useCache = true;
	}
	
	/**
	 * disables caching
	 */
	public void disableCache() {
	    useCache = false;
	}
	
	/**
	 * clears the cache
	 */
	public void clearCache() {
	    reachingObjectsCache.clear();
        reachingObjectsCacheNoCGRefinement.clear();
	}

    public boolean isRefineCallGraph() {
        return refineCallGraph;
    }

    public void setRefineCallGraph(boolean refineCallGraph) {
        this.refineCallGraph = refineCallGraph;
    }

    public HeuristicType getHeuristicType() {
      return heuristicType;
    }

    public void setHeuristicType(HeuristicType heuristicType) {
      this.heuristicType = heuristicType;
      clearCache();
    }
}
