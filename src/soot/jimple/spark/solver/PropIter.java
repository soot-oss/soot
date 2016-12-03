/* Soot - a J*va Optimization Framework
 * Copyright (C) 2002 Ondrej Lhotak
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

package soot.jimple.spark.solver;

import soot.jimple.spark.pag.*;
import soot.jimple.spark.sets.*;
import soot.*;
import soot.util.queue.*;
import java.util.*;

/**
 * Propagates points-to sets along pointer assignment graph using iteration.
 * 
 * @author Ondrej Lhotak
 */

public final class PropIter extends Propagator {
	public PropIter(PAG pag) {
		this.pag = pag;
	}

	/** Actually does the propagation. */
	public final void propagate() {
		final OnFlyCallGraph ofcg = pag.getOnFlyCallGraph();
		new TopoSorter(pag, false).sort();
		for (Object object : pag.allocSources()) {
			handleAllocNode((AllocNode) object);
		}
		int iteration = 1;
		boolean change;
		do {
			change = false;
			TreeSet<VarNode> simpleSources = new TreeSet<VarNode>(pag.simpleSources());
			if (pag.getOpts().verbose()) {
				G.v().out.println("Iteration " + (iteration++));
			}
			for (VarNode object : simpleSources) {
				change = handleSimples(object) | change;
			}
			if (ofcg != null) {
				QueueReader<Node> addedEdges = pag.edgeReader();
				for (VarNode src : pag.getVarNodeNumberer()) {
					ofcg.updatedNode(src);
				}
				ofcg.build();

				while (addedEdges.hasNext()) {
					Node addedSrc = (Node) addedEdges.next();
					Node addedTgt = (Node) addedEdges.next();
					change = true;
					if (addedSrc instanceof VarNode) {
						PointsToSetInternal p2set = ((VarNode) addedSrc).getP2Set();
						if (p2set != null)
							p2set.unFlushNew();
					} else if (addedSrc instanceof AllocNode) {
						((VarNode) addedTgt).makeP2Set().add(addedSrc);
					}
				}
				if (change) {
					new TopoSorter(pag, false).sort();
				}
			}
			for (FieldRefNode object : pag.loadSources()) {
				change = handleLoads(object) | change;
			}
			for (VarNode object : pag.storeSources()) {
				change = handleStores(object) | change;
			}
			for (NewInstanceNode object : pag.assignInstanceSources()) {
				change = handleNewInstances(object) | change;
			}
		} while (change);
	}

	/* End of public methods. */
	/* End of package methods. */

	/**
	 * Propagates new points-to information of node src to all its successors.
	 */
	protected final boolean handleAllocNode(AllocNode src) {
		boolean ret = false;
		Node[] targets = pag.allocLookup(src);
		for (Node element : targets) {
			ret = element.makeP2Set().add(src) | ret;
		}
		return ret;
	}

	protected final boolean handleSimples(VarNode src) {
		boolean ret = false;
		PointsToSetInternal srcSet = src.getP2Set();
		if (srcSet.isEmpty())
			return false;
		Node[] simpleTargets = pag.simpleLookup(src);
		for (Node element : simpleTargets) {
			ret = element.makeP2Set().addAll(srcSet, null) | ret;
		}
		
		Node[] newInstances = pag.newInstanceLookup(src);
		for (Node element : newInstances) {
			ret = element.makeP2Set().addAll(srcSet, null) | ret;
		}
		
		return ret;
	}

	protected final boolean handleStores(VarNode src) {
		boolean ret = false;
		final PointsToSetInternal srcSet = src.getP2Set();
		if (srcSet.isEmpty())
			return false;
		Node[] storeTargets = pag.storeLookup(src);
		for (Node element : storeTargets) {
			final FieldRefNode fr = (FieldRefNode) element;
			final SparkField f = fr.getField();
			ret = fr.getBase().getP2Set().forall(new P2SetVisitor() {
				public final void visit(Node n) {
					AllocDotField nDotF = pag.makeAllocDotField((AllocNode) n, f);
					if (nDotF.makeP2Set().addAll(srcSet, null)) {
						returnValue = true;
					}
				}
			}) | ret;
		}
		return ret;
	}

	protected final boolean handleLoads(FieldRefNode src) {
		boolean ret = false;
		final Node[] loadTargets = pag.loadLookup(src);
		final SparkField f = src.getField();
		ret = src.getBase().getP2Set().forall(new P2SetVisitor() {
			public final void visit(Node n) {
				AllocDotField nDotF = ((AllocNode) n).dot(f);
				if (nDotF == null)
					return;
				PointsToSetInternal set = nDotF.getP2Set();
				if (set.isEmpty())
					return;
				for (Node element : loadTargets) {
					VarNode target = (VarNode) element;
					if (target.makeP2Set().addAll(set, null)) {
						returnValue = true;
					}
				}
			}
		}) | ret;
		return ret;
	}
	
	protected final boolean handleNewInstances(final NewInstanceNode src) {
		boolean ret = false;
		final Node[] newInstances = pag.assignInstanceLookup(src);
		for (final Node instance : newInstances) {
			ret = src.getP2Set().forall(new P2SetVisitor() {
				
				@Override
				public void visit(Node n) {
					if (n instanceof ClassConstantNode) {
						ClassConstantNode ccn = (ClassConstantNode) n;
						Type ccnType = RefType.v(ccn.getClassConstant().getValue().replaceAll("/", "."));
						
						// If the referenced class has not been loaded, we do this now
						SootClass targetClass = ((RefType) ccnType).getSootClass();
						if (targetClass.resolvingLevel() == SootClass.DANGLING)
							Scene.v().forceResolve(targetClass.getName(), SootClass.SIGNATURES);
						
						instance.makeP2Set().add(pag.makeAllocNode(src.getValue(), ccnType, ccn.getMethod()));
					}
				}
				
			});
		}
		return ret;
	}

	protected PAG pag;
}
