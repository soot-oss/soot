package soot.jimple.toolkits.graph;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2002 Florian Loitsch
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Body;
import soot.BodyTransformer;
import soot.G;
import soot.Singletons;
import soot.Unit;
import soot.UnitBox;
import soot.jimple.Jimple;
import soot.options.Options;
import soot.util.Chain;

/**
 * removes all critical edges.<br>
 * A critical edge is an edge from Block A to block B, if B has more than one predecessor and A has more the one
 * successor.<br>
 * As an example: If we wanted a computation to be only on the path A-&gt;B this computation must be directly on the edge.
 * Otherwise it is either executed on the path through the second predecessor of A or throught the second successor of B.<br>
 * Our critical edge-remover overcomes this problem by introducing synthetic nodes on this critical edges.<br>
 * Exceptions will be ignored.
 */
public class CriticalEdgeRemover extends BodyTransformer {
  private static final Logger logger = LoggerFactory.getLogger(CriticalEdgeRemover.class);

  public CriticalEdgeRemover(Singletons.Global g) {
  }

  public static CriticalEdgeRemover v() {
    return G.v().soot_jimple_toolkits_graph_CriticalEdgeRemover();
  }

  /**
   * performs critical edge-removing.
   */
  protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
    if (Options.v().verbose()) {
      logger.debug("[" + b.getMethod().getName() + "]     Removing Critical Edges...");
    }
    removeCriticalEdges(b);
    if (Options.v().verbose()) {
      logger.debug("[" + b.getMethod().getName() + "]     Removing Critical Edges done.");
    }

  }

  /**
   * inserts a Jimple<code>Goto</code> to <code> target, directly after
   * <code>node</code> in the given <code>unitChain</code>.<br>
   * As we use <code>JGoto</code> the chain must contain Jimple-stmts.
   *
   * @param unitChain
   *          the Chain where we will insert the <code>Goto</code>.
   * @param node
   *          the <code>Goto</code> will be inserted just after this node.
   * @param target
   *          is the Unit the <code>goto</code> will jump to.
   * @return the newly inserted <code>Goto</code>
   */
  private static Unit insertGotoAfter(Chain<Unit> unitChain, Unit node, Unit target) {
    Unit newGoto = Jimple.v().newGotoStmt(target);
    unitChain.insertAfter(newGoto, node);
    return newGoto;
  }

  /**
   * inserts a Jimple<code>Goto</code> to <code> target, directly before
   * <code>node</code> in the given <code>unitChain</code>.<br>
   * As we use <code>JGoto</code> the chain must contain Jimple-stmts.
   *
   * @param unitChain
   *          the Chain where we will insert the <code>Goto</code>.
   * @param node
   *          the <code>Goto</code> will be inserted just before this node.
   * @param target
   *          is the Unit the <code>goto</code> will jump to.
   * @return the newly inserted <code>Goto</code>
   */
  /* note, that this method has slightly more overhead than the insertGotoAfter */
  private static Unit insertGotoBefore(Chain<Unit> unitChain, Unit node, Unit target) {
    Unit newGoto = Jimple.v().newGotoStmt(target);
    unitChain.insertBefore(newGoto, node);
    newGoto.redirectJumpsToThisTo(node);
    return newGoto;
  }

  /**
   * takes <code>node</code> and redirects all branches to <code>oldTarget</code> to <code>newTarget</code>.
   *
   * @param node
   *          the Unit where we redirect
   * @param oldTarget
   * @param newTarget
   */
  private static void redirectBranch(Unit node, Unit oldTarget, Unit newTarget) {
    for (UnitBox targetBox : node.getUnitBoxes()) {
      Unit target = targetBox.getUnit();
      if (target == oldTarget) {
        targetBox.setUnit(newTarget);
      }
    }
  }

  /**
   * splits critical edges by introducing synthetic nodes.<br>
   * This method <b>will modify</b> the <code>UnitGraph</code> of the body. Synthetic nodes are always <code>JGoto</code>s.
   * Therefore the body must be in <tt>Jimple</tt>.<br>
   * As a side-effect, after the transformation, the direct predecessor of a block/node with multiple predecessors will will
   * not fall through anymore. This simplifies the algorithm and is nice to work with afterwards.
   *
   * @param b
   *          the Jimple-body that will be physicly modified so that there are no critical edges anymore.
   */
  /*
   * note, that critical edges can only appear on edges between blocks!. Our algorithm will *not* take into account
   * exceptions. (this is nearly impossible anyways)
   */
  private void removeCriticalEdges(Body b) {
    Chain<Unit> unitChain = b.getUnits();
    int size = unitChain.size();
    Map<Unit, List<Unit>> predecessors = new HashMap<Unit, List<Unit>>(2 * size + 1, 0.7f);

    /*
     * First get the predecessors of each node (although direct predecessors are predecessors too, we'll not include them in
     * the lists)
     */
    {
      Iterator<Unit> unitIt = unitChain.snapshotIterator();
      while (unitIt.hasNext()) {
        Unit currentUnit = (Unit) unitIt.next();

        Iterator<UnitBox> succsIt = currentUnit.getUnitBoxes().iterator();
        while (succsIt.hasNext()) {
          Unit target = succsIt.next().getUnit();
          List<Unit> predList = predecessors.get(target);
          if (predList == null) {
            predList = new ArrayList<Unit>();
            predList.add(currentUnit);
            predecessors.put(target, predList);
          } else {
            predList.add(currentUnit);
          }
        }
      }
    }

    {
      /*
       * for each node: if we have more than two predecessors, split these edges if the node at the other end has more than
       * one successor.
       */

      /* we need a snapshotIterator, as we'll modify the structure */
      Iterator<Unit> unitIt = unitChain.snapshotIterator();

      Unit currentUnit = null;
      Unit directPredecessor;
      while (unitIt.hasNext()) {
        directPredecessor = currentUnit;
        currentUnit = unitIt.next();

        List<Unit> predList = predecessors.get(currentUnit);
        int nbPreds = (predList == null) ? 0 : predList.size();
        if (directPredecessor != null && directPredecessor.fallsThrough()) {
          nbPreds++;
        }

        if (nbPreds >= 2) {
          /*
           * redirect the directPredecessor (if it falls through), so we can easily insert the synthetic nodes. This
           * redirection might not be necessary, but is pleasant anyways (see the Javadoc for this method)
           */
          if (directPredecessor != null && directPredecessor.fallsThrough()) {
            directPredecessor = insertGotoAfter(unitChain, directPredecessor, currentUnit);
          }

          /*
           * if the predecessors have more than one successor insert the synthetic node.
           */
          Iterator<Unit> predIt = predList.iterator();
          while (predIt.hasNext()) {
            Unit predecessor = predIt.next();
            /*
             * Although in Jimple there should be only two ways of having more than one successor (If and Case) we'll do it
             * the hard way:)
             */
            int nbSuccs = predecessor.getUnitBoxes().size();
            nbSuccs += predecessor.fallsThrough() ? 1 : 0;
            if (nbSuccs >= 2) {
              /*
               * insert synthetic node (insertGotoAfter should be slightly faster)
               */
              if (directPredecessor == null) {
                directPredecessor = insertGotoBefore(unitChain, currentUnit, currentUnit);
              } else {
                directPredecessor = insertGotoAfter(unitChain, directPredecessor, currentUnit);
              }
              /* update the branch */
              redirectBranch(predecessor, currentUnit, directPredecessor);
            }
          }
        }
      }
    }
  }
}
