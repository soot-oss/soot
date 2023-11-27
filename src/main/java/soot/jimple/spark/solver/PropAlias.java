package soot.jimple.spark.solver;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2002 Ondrej Lhotak
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.Type;
import soot.jimple.spark.pag.AllocNode;
import soot.jimple.spark.pag.ClassConstantNode;
import soot.jimple.spark.pag.FieldRefNode;
import soot.jimple.spark.pag.NewInstanceNode;
import soot.jimple.spark.pag.Node;
import soot.jimple.spark.pag.PAG;
import soot.jimple.spark.pag.SparkField;
import soot.jimple.spark.pag.VarNode;
import soot.jimple.spark.sets.EmptyPointsToSet;
import soot.jimple.spark.sets.P2SetVisitor;
import soot.jimple.spark.sets.PointsToSetInternal;
import soot.util.HashMultiMap;
import soot.util.MultiMap;
import soot.util.queue.QueueReader;

/**
 * Propagates points-to sets along pointer assignment graph using a relevant aliases.
 * 
 * @author Ondrej Lhotak
 */

public class PropAlias extends Propagator {
  private static final Logger logger = LoggerFactory.getLogger(PropAlias.class);
  protected final Set<VarNode> varNodeWorkList = new TreeSet<VarNode>();
  protected Set<VarNode> aliasWorkList;
  protected Set<FieldRefNode> fieldRefWorkList = new HashSet<FieldRefNode>();
  protected Set<FieldRefNode> outFieldRefWorkList = new HashSet<FieldRefNode>();

  public PropAlias(PAG pag) {
    this.pag = pag;
    loadSets = new HashMap<FieldRefNode, PointsToSetInternal>();
  }

  /** Actually does the propagation. */
  public void propagate() {
    ofcg = pag.getOnFlyCallGraph();
    new TopoSorter(pag, false).sort();
    for (Object object : pag.loadSources()) {
      final FieldRefNode fr = (FieldRefNode) object;
      fieldToBase.put(fr.getField(), fr.getBase());
    }
    for (Object object : pag.storeInvSources()) {
      final FieldRefNode fr = (FieldRefNode) object;
      fieldToBase.put(fr.getField(), fr.getBase());
    }
    for (Object object : pag.allocSources()) {
      handleAllocNode((AllocNode) object);
    }

    boolean verbose = pag.getOpts().verbose();
    do {
      if (verbose) {
        logger.debug("Worklist has " + varNodeWorkList.size() + " nodes.");
      }
      aliasWorkList = new HashSet<VarNode>();
      while (!varNodeWorkList.isEmpty()) {
        VarNode src = varNodeWorkList.iterator().next();
        varNodeWorkList.remove(src);
        aliasWorkList.add(src);
        handleVarNode(src);
      }
      if (verbose) {
        logger.debug("Now handling field references");
      }

      for (VarNode src : aliasWorkList) {

        for (FieldRefNode srcFr : src.getAllFieldRefs()) {
          SparkField field = srcFr.getField();
          for (VarNode dst : fieldToBase.get(field)) {
            if (src.getP2Set().hasNonEmptyIntersection(dst.getP2Set())) {
              FieldRefNode dstFr = dst.dot(field);
              aliasEdges.put(srcFr, dstFr);
              aliasEdges.put(dstFr, srcFr);
              fieldRefWorkList.add(srcFr);
              fieldRefWorkList.add(dstFr);
              if (makeP2Set(dstFr).addAll(srcFr.getP2Set().getOldSet(), null)) {
                outFieldRefWorkList.add(dstFr);
              }
              if (makeP2Set(srcFr).addAll(dstFr.getP2Set().getOldSet(), null)) {
                outFieldRefWorkList.add(srcFr);
              }
            }
          }
        }
      }
      for (FieldRefNode src : fieldRefWorkList) {
        for (FieldRefNode dst : aliasEdges.get(src)) {
          if (makeP2Set(dst).addAll(src.getP2Set().getNewSet(), null)) {
            outFieldRefWorkList.add(dst);
          }
        }
        src.getP2Set().flushNew();
      }
      fieldRefWorkList = new HashSet<FieldRefNode>();
      for (FieldRefNode src : outFieldRefWorkList) {
        PointsToSetInternal set = getP2Set(src).getNewSet();
        if (set.isEmpty()) {
          continue;
        }
        Node[] targets = pag.loadLookup(src);
        for (Node element0 : targets) {
          VarNode target = (VarNode) element0;
          if (target.makeP2Set().addAll(set, null)) {
            addToWorklist(target);
          }
        }
        getP2Set(src).flushNew();
      }
      outFieldRefWorkList = new HashSet<FieldRefNode>();
    } while (!varNodeWorkList.isEmpty());
  }

  /* End of public methods. */
  /* End of package methods. */

  /**
   * Propagates new points-to information of node src to all its successors.
   */
  protected boolean handleAllocNode(AllocNode src) {
    boolean ret = false;
    Node[] targets = pag.allocLookup(src);
    for (Node element : targets) {
      if (element.makeP2Set().add(src)) {
        addToWorklist((VarNode) element);
        ret = true;
      }
    }
    return ret;
  }

  /**
   * Propagates new points-to information of node src to all its successors.
   */
  protected boolean handleVarNode(final VarNode src) {
    boolean ret = false;

    if (src.getReplacement() != src) {
      throw new RuntimeException("Got bad node " + src + " with rep " + src.getReplacement());
    }

    final PointsToSetInternal newP2Set = src.getP2Set().getNewSet();
    if (newP2Set.isEmpty()) {
      return false;
    }

    if (ofcg != null) {
      QueueReader<Node> addedEdges = pag.edgeReader();
      ofcg.updatedNode(src);
      ofcg.build();

      while (addedEdges.hasNext()) {
        Node addedSrc = (Node) addedEdges.next();
        Node addedTgt = (Node) addedEdges.next();
        ret = true;
        if (addedSrc instanceof VarNode) {
          VarNode edgeSrc = (VarNode) addedSrc;
          if (addedTgt instanceof VarNode) {
            VarNode edgeTgt = (VarNode) addedTgt;
            if (edgeTgt.makeP2Set().addAll(edgeSrc.getP2Set(), null)) {
              addToWorklist(edgeTgt);
            }
          } else if (addedTgt instanceof NewInstanceNode) {
            NewInstanceNode edgeTgt = (NewInstanceNode) addedTgt.getReplacement();
            if (edgeTgt.makeP2Set().addAll(edgeSrc.getP2Set(), null)) {
              for (Node element : pag.assignInstanceLookup(edgeTgt)) {
                addToWorklist((VarNode) element);
              }
            }
          }
        } else if (addedSrc instanceof AllocNode) {
          AllocNode edgeSrc = (AllocNode) addedSrc;
          VarNode edgeTgt = (VarNode) addedTgt;
          if (edgeTgt.makeP2Set().add(edgeSrc)) {
            addToWorklist(edgeTgt);
          }
        } else if (addedSrc instanceof NewInstanceNode && addedTgt instanceof VarNode) {
          final NewInstanceNode edgeSrc = (NewInstanceNode) addedSrc.getReplacement();
          final VarNode edgeTgt = (VarNode) addedTgt.getReplacement();
          addedSrc.getP2Set().forall(new P2SetVisitor() {

            @Override
            public void visit(Node n) {
              if (n instanceof ClassConstantNode) {
                ClassConstantNode ccn = (ClassConstantNode) n;
                Type ccnType = ccn.getClassConstant().toSootType();

                // If the referenced class has not been loaded,
                // we do this now
                SootClass targetClass = ((RefType) ccnType).getSootClass();
                if (targetClass.resolvingLevel() == SootClass.DANGLING) {
                  Scene.v().forceResolve(targetClass.getPathPlusClassName(), SootClass.SIGNATURES);
                }

                edgeTgt.makeP2Set().add(pag.makeAllocNode(edgeSrc.getValue(), ccnType, ccn.getMethod()));
                addToWorklist(edgeTgt);
              }
            }

          });
        }

        FieldRefNode frn = null;
        if (addedSrc instanceof FieldRefNode) {
          frn = (FieldRefNode) addedSrc;
        }
        if (addedTgt instanceof FieldRefNode) {
          frn = (FieldRefNode) addedTgt;
        }
        if (frn != null) {
          VarNode base = frn.getBase();
          if (fieldToBase.put(frn.getField(), base)) {
            aliasWorkList.add(base);
          }
        }
      }
    }

    Node[] simpleTargets = pag.simpleLookup(src);
    for (Node element : simpleTargets) {
      if (element.makeP2Set().addAll(newP2Set, null)) {
        addToWorklist((VarNode) element);
        ret = true;
      }
    }

    Node[] storeTargets = pag.storeLookup(src);
    for (Node element : storeTargets) {
      final FieldRefNode fr = (FieldRefNode) element;
      if (fr.makeP2Set().addAll(newP2Set, null)) {
        fieldRefWorkList.add(fr);
        ret = true;
      }
    }

    src.getP2Set().flushNew();
    return ret;
  }

  protected final PointsToSetInternal makeP2Set(FieldRefNode n) {
    PointsToSetInternal ret = loadSets.get(n);
    if (ret == null) {
      ret = pag.getSetFactory().newSet(null, pag);
      loadSets.put(n, ret);
    }
    return ret;
  }

  protected final PointsToSetInternal getP2Set(FieldRefNode n) {
    PointsToSetInternal ret = loadSets.get(n);
    if (ret == null) {
      return EmptyPointsToSet.v();
    }
    return ret;
  }

  private boolean addToWorklist(VarNode n) {
    if (n.getReplacement() != n) {
      throw new RuntimeException("Adding bad node " + n + " with rep " + n.getReplacement());
    }
    return varNodeWorkList.add(n);
  }

  protected PAG pag;
  protected MultiMap<SparkField, VarNode> fieldToBase = new HashMultiMap<SparkField, VarNode>();
  protected MultiMap<FieldRefNode, FieldRefNode> aliasEdges = new HashMultiMap<FieldRefNode, FieldRefNode>();
  protected Map<FieldRefNode, PointsToSetInternal> loadSets;
  protected OnFlyCallGraph ofcg;
}
