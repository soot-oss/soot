package soot.jimple.spark.solver;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2002 - 2006 Ondrej Lhotak
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

import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.Type;
import soot.jimple.spark.pag.AllocDotField;
import soot.jimple.spark.pag.AllocNode;
import soot.jimple.spark.pag.ClassConstantNode;
import soot.jimple.spark.pag.FieldRefNode;
import soot.jimple.spark.pag.NewInstanceNode;
import soot.jimple.spark.pag.Node;
import soot.jimple.spark.pag.PAG;
import soot.jimple.spark.pag.SparkField;
import soot.jimple.spark.pag.VarNode;
import soot.jimple.spark.sets.P2SetVisitor;
import soot.jimple.spark.sets.PointsToSetInternal;
import soot.util.queue.QueueReader;

/**
 * Propagates points-to sets along pointer assignment graph using a worklist.
 * 
 * @author Ondrej Lhotak
 */

public final class PropWorklist extends Propagator {
  private static final Logger logger = LoggerFactory.getLogger(PropWorklist.class);
  protected final Set<VarNode> varNodeWorkList = new TreeSet<VarNode>();

  public PropWorklist(PAG pag) {
    this.pag = pag;
  }

  /** Actually does the propagation. */
  public final void propagate() {
    ofcg = pag.getOnFlyCallGraph();
    new TopoSorter(pag, false).sort();
    for (AllocNode object : pag.allocSources()) {
      handleAllocNode(object);
    }

    boolean verbose = pag.getOpts().verbose();
    do {
      if (verbose) {
        logger.debug("Worklist has " + varNodeWorkList.size() + " nodes.");
      }
      while (!varNodeWorkList.isEmpty()) {
        VarNode src = varNodeWorkList.iterator().next();
        varNodeWorkList.remove(src);
        handleVarNode(src);
      }
      if (verbose) {
        logger.debug("Now handling field references");
      }
      for (Object object : pag.storeSources()) {
        final VarNode src = (VarNode) object;
        Node[] targets = pag.storeLookup(src);
        for (Node element0 : targets) {
          final FieldRefNode target = (FieldRefNode) element0;
          target.getBase().makeP2Set().forall(new P2SetVisitor() {
            public final void visit(Node n) {
              AllocDotField nDotF = pag.makeAllocDotField((AllocNode) n, target.getField());
              if (ofcg != null) {
                ofcg.updatedFieldRef(nDotF, src.getP2Set());
              }
              nDotF.makeP2Set().addAll(src.getP2Set(), null);
            }
          });
        }
      }
      HashSet<Object[]> edgesToPropagate = new HashSet<Object[]>();
      for (Object object : pag.loadSources()) {
        handleFieldRefNode((FieldRefNode) object, edgesToPropagate);
      }
      Set<PointsToSetInternal> nodesToFlush = Collections.newSetFromMap(new IdentityHashMap<PointsToSetInternal, Boolean>());
      for (Object[] pair : edgesToPropagate) {
        PointsToSetInternal nDotF = (PointsToSetInternal) pair[0];
        PointsToSetInternal newP2Set = nDotF.getNewSet();
        VarNode loadTarget = (VarNode) pair[1];
        if (loadTarget.makeP2Set().addAll(newP2Set, null)) {
          varNodeWorkList.add(loadTarget);
        }
        nodesToFlush.add(nDotF);
      }
      for (PointsToSetInternal nDotF : nodesToFlush) {
        nDotF.flushNew();
      }
    } while (!varNodeWorkList.isEmpty());
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
      if (element.makeP2Set().add(src)) {
        varNodeWorkList.add((VarNode) element);
        ret = true;
      }
    }
    return ret;
  }

  /**
   * Propagates new points-to information of node src to all its successors.
   */
  protected final boolean handleVarNode(final VarNode src) {
    boolean ret = false;
    boolean flush = true;

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
          VarNode edgeSrc = (VarNode) addedSrc.getReplacement();
          if (addedTgt instanceof VarNode) {
            VarNode edgeTgt = (VarNode) addedTgt.getReplacement();

            if (edgeTgt.makeP2Set().addAll(edgeSrc.getP2Set(), null)) {
              varNodeWorkList.add(edgeTgt);
              if (edgeTgt == src) {
                flush = false;
              }
            }
          } else if (addedTgt instanceof NewInstanceNode) {
            NewInstanceNode edgeTgt = (NewInstanceNode) addedTgt.getReplacement();
            if (edgeTgt.makeP2Set().addAll(edgeSrc.getP2Set(), null)) {
              for (Node element : pag.assignInstanceLookup(edgeTgt)) {
                varNodeWorkList.add((VarNode) element);
                if (element == src) {
                  flush = false;
                }
              }
            }
          }
        } else if (addedSrc instanceof AllocNode) {
          VarNode edgeTgt = (VarNode) addedTgt.getReplacement();
          if (edgeTgt.makeP2Set().add(addedSrc)) {
            varNodeWorkList.add(edgeTgt);
            if (edgeTgt == src) {
              flush = false;
            }
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
                  Scene.v().forceResolve(targetClass.getName(), SootClass.SIGNATURES);
                }

                // We can only create alloc nodes for types that
                // we know
                edgeTgt.makeP2Set().add(pag.makeAllocNode(edgeSrc.getValue(), ccnType, ccn.getMethod()));
                varNodeWorkList.add(edgeTgt);
              }
            }

          });
          if (edgeTgt.makeP2Set().add(addedSrc)) {
            if (edgeTgt == src) {
              flush = false;
            }
          }
        }
      }
    }

    Node[] simpleTargets = pag.simpleLookup(src);
    for (Node element : simpleTargets) {
      if (element.makeP2Set().addAll(newP2Set, null)) {
        varNodeWorkList.add((VarNode) element);
        if (element == src) {
          flush = false;
        }
        ret = true;
      }
    }

    Node[] storeTargets = pag.storeLookup(src);
    for (Node element : storeTargets) {
      final FieldRefNode fr = (FieldRefNode) element;
      final SparkField f = fr.getField();
      ret = fr.getBase().getP2Set().forall(new P2SetVisitor() {
        public final void visit(Node n) {
          AllocDotField nDotF = pag.makeAllocDotField((AllocNode) n, f);
          if (nDotF.makeP2Set().addAll(newP2Set, null)) {
            returnValue = true;
          }
        }
      }) | ret;
    }

    final HashSet<Node[]> storesToPropagate = new HashSet<Node[]>();
    final HashSet<Node[]> loadsToPropagate = new HashSet<Node[]>();
    for (final FieldRefNode fr : src.getAllFieldRefs()) {
      final SparkField field = fr.getField();
      final Node[] storeSources = pag.storeInvLookup(fr);
      if (storeSources.length > 0) {
        newP2Set.forall(new P2SetVisitor() {
          public final void visit(Node n) {
            AllocDotField nDotF = pag.makeAllocDotField((AllocNode) n, field);
            for (Node element : storeSources) {
              Node[] pair = { element, nDotF.getReplacement() };
              storesToPropagate.add(pair);
            }
          }
        });
      }

      final Node[] loadTargets = pag.loadLookup(fr);
      if (loadTargets.length > 0) {
        newP2Set.forall(new P2SetVisitor() {
          public final void visit(Node n) {
            AllocDotField nDotF = pag.makeAllocDotField((AllocNode) n, field);
            if (nDotF != null) {
              for (Node element : loadTargets) {
                Node[] pair = { nDotF.getReplacement(), element };
                loadsToPropagate.add(pair);
              }
            }
          }
        });
      }
    }
    if (flush) {
      src.getP2Set().flushNew();
    }
    for (Node[] p : storesToPropagate) {
      VarNode storeSource = (VarNode) p[0];
      AllocDotField nDotF = (AllocDotField) p[1];
      if (nDotF.makeP2Set().addAll(storeSource.getP2Set(), null)) {
        ret = true;
      }
    }
    for (Node[] p : loadsToPropagate) {
      AllocDotField nDotF = (AllocDotField) p[0];
      VarNode loadTarget = (VarNode) p[1];
      if (loadTarget.makeP2Set().addAll(nDotF.getP2Set(), null)) {
        varNodeWorkList.add(loadTarget);
        ret = true;
      }
    }
    return ret;
  }

  /**
   * Propagates new points-to information of node src to all its successors.
   */
  protected final void handleFieldRefNode(FieldRefNode src, final HashSet<Object[]> edgesToPropagate) {
    final Node[] loadTargets = pag.loadLookup(src);
    if (loadTargets.length == 0) {
      return;
    }
    final SparkField field = src.getField();

    src.getBase().getP2Set().forall(new P2SetVisitor() {

      public final void visit(Node n) {
        AllocDotField nDotF = pag.makeAllocDotField((AllocNode) n, field);
        if (nDotF != null) {
          PointsToSetInternal p2Set = nDotF.getP2Set();
          if (!p2Set.getNewSet().isEmpty()) {
            for (Node element : loadTargets) {
              Object[] pair = { p2Set, element };
              edgesToPropagate.add(pair);
            }
          }
        }
      }
    });
  }

  protected PAG pag;
  protected OnFlyCallGraph ofcg;
}
