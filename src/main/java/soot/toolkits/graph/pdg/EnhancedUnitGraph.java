package soot.toolkits.graph.pdg;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1999 - 2010 Hossein Sadat-Mohtasham
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
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import soot.Body;
import soot.Trap;
import soot.Unit;
import soot.jimple.ThrowStmt;
import soot.jimple.internal.JNopStmt;
import soot.toolkits.graph.DominatorNode;
import soot.toolkits.graph.MHGDominatorsFinder;
import soot.toolkits.graph.MHGPostDominatorsFinder;
import soot.toolkits.graph.UnitGraph;
import soot.util.Chain;

/**
 *
 * This class represents a control flow graph which behaves like an ExceptionalUnitGraph and BriefUnitGraph when there are no
 * exception handling construct in the method; at the presence of such constructs, the CFG is constructed from a brief graph
 * by adding a concise representation of the exceptional flow as well as START/STOP auxiliary nodes. In a nutshell, the
 * exceptional flow is represented at the level of try-catch-finally blocks instead of the Unit level to allow a more useful
 * region analysis.
 *
 * @author Hossein Sadat-Mohtasham
 *
 */

public class EnhancedUnitGraph extends UnitGraph {

  // This keeps a map from the beginning of each guarded block
  // to the corresponding special EHNopStmt.
  // protected Hashtable<GuardedBlock, Unit> try2nop = null;
  protected Hashtable<Unit, Unit> try2nop = null;
  // Keep the real header of the handler block
  protected Hashtable<Unit, Unit> handler2header = null;

  public EnhancedUnitGraph(Body body) {
    super(body);

    // try2nop = new Hashtable<GuardedBlock, Unit>();
    try2nop = new Hashtable<Unit, Unit>();
    handler2header = new Hashtable<Unit, Unit>();

    // there could be a maximum of traps.size() of nop
    // units added to the CFG plus potentially START/STOP nodes.
    int size = unitChain.size() + body.getTraps().size() + 2;

    unitToSuccs = new HashMap<Unit, List<Unit>>(size * 2 + 1, 0.7f);
    unitToPreds = new HashMap<Unit, List<Unit>>(size * 2 + 1, 0.7f);

    /*
     * Compute the head and tails at each phase because other phases might rely on them.
     */
    buildUnexceptionalEdges(unitToSuccs, unitToPreds);
    addAuxiliaryExceptionalEdges();
    buildHeadsAndTails();
    handleExplicitThrowEdges();
    buildHeadsAndTails();
    handleMultipleReturns();
    buildHeadsAndTails();

    /**
     * Remove bogus heads (these are useless goto's)
     */

    removeBogusHeads();
    buildHeadsAndTails();
  }

  /**
   * This method adds a STOP node to the graph, if necessary, to make the CFG single-tailed.
   */

  protected void handleMultipleReturns() {
    if (this.getTails().size() > 1) {
      Unit stop = new ExitStmt();
      List<Unit> predsOfstop = new ArrayList<Unit>();

      for (Unit tail : this.getTails()) {
        predsOfstop.add(tail);

        List<Unit> tailSuccs = this.unitToSuccs.get(tail);
        if (tailSuccs == null) {
          tailSuccs = new ArrayList<Unit>();
          this.unitToSuccs.put(tail, tailSuccs);
        }
        tailSuccs.add(stop);
      }

      this.unitToPreds.put(stop, predsOfstop);
      this.unitToSuccs.put(stop, new ArrayList<Unit>());

      Chain<Unit> units = body.getUnits().getNonPatchingChain();
      if (!units.contains(stop)) {
        units.addLast(stop);
      }
    }
  }

  /**
   * This method removes all the heads in the CFG except the one that corresponds to the first unit in the method.
   */

  protected void removeBogusHeads() {
    Chain<Unit> units = body.getUnits();
    Unit trueHead = units.getFirst();

    while (this.getHeads().size() > 1) {
      for (Unit head : this.getHeads()) {
        if (trueHead == head) {
          continue;
        }

        this.unitToPreds.remove(head);
        for (Unit succ : this.unitToSuccs.get(head)) {
          List<Unit> tobeRemoved = new ArrayList<Unit>();
          List<Unit> predOfSuccs = this.unitToPreds.get(succ);
          if (predOfSuccs != null) {
            for (Unit pred : predOfSuccs) {
              if (pred == head) {
                tobeRemoved.add(pred);
              }

            }
            predOfSuccs.removeAll(tobeRemoved);
          }
        }

        this.unitToSuccs.remove(head);

        if (units.contains(head)) {
          units.remove(head);
        }
      }

      this.buildHeadsAndTails();
    }
  }

  protected void handleExplicitThrowEdges() {
    MHGDominatorTree<Unit> dom = new MHGDominatorTree<Unit>(new MHGDominatorsFinder<Unit>(this));
    MHGDominatorTree<Unit> pdom = new MHGDominatorTree<Unit>(new MHGPostDominatorsFinder<Unit>(this));

    // this keeps a map from the entry of a try-catch-block to a selected
    // merge point
    Hashtable<Unit, Unit> x2mergePoint = new Hashtable<Unit, Unit>();

    List<Unit> tails = this.getTails();

    TailsLoop: for (Iterator<Unit> itr = tails.iterator(); itr.hasNext();) {
      Unit tail = itr.next();
      if (!(tail instanceof ThrowStmt)) {
        continue;
      }

      DominatorNode<Unit> x = dom.getDode(tail);
      DominatorNode<Unit> parentOfX = dom.getParentOf(x);
      Unit xgode = x.getGode();
      DominatorNode<Unit> xpdomDode = pdom.getDode(xgode);
      Unit parentXGode = parentOfX.getGode();
      DominatorNode<Unit> parentpdomDode = pdom.getDode(parentXGode);
      // while x post-dominates its dominator (parent in dom)
      while (pdom.isDominatorOf(xpdomDode, parentpdomDode)) {
        x = parentOfX;
        parentOfX = dom.getParentOf(x);

        // If parent is null we must be at the head of the graph
        if (parentOfX == null) {
          // throw new
          // RuntimeException("This should never have happened!");
          break;
        }

        xgode = x.getGode();
        xpdomDode = pdom.getDode(xgode);
        parentXGode = parentOfX.getGode();
        parentpdomDode = pdom.getDode(parentXGode);
      }

      if (parentOfX != null) {
        x = parentOfX;
      }

      xgode = x.getGode();
      xpdomDode = pdom.getDode(xgode);

      Unit mergePoint = null;

      if (x2mergePoint.containsKey(xgode)) {
        mergePoint = x2mergePoint.get(xgode);
      } else {
        // Now get all the children of x in the dom

        List<DominatorNode<Unit>> domChilds = dom.getChildrenOf(x);

        Unit child1god = null;
        Unit child2god = null;

        for (Iterator<DominatorNode<Unit>> domItr = domChilds.iterator(); domItr.hasNext();) {
          DominatorNode<Unit> child = domItr.next();
          Unit childGode = child.getGode();
          DominatorNode<Unit> childpdomDode = pdom.getDode(childGode);

          // we don't want to make a loop!
          List<Unit> path = this.getExtendedBasicBlockPathBetween(childGode, tail);

          // if(dom.isDominatorOf(child, dom.getDode(tail)))
          if (!(path == null || path.isEmpty())) {
            continue;
          }

          if (pdom.isDominatorOf(childpdomDode, xpdomDode)) {
            mergePoint = child.getGode();
            break;
          }

          // gather two eligible childs
          if (child1god == null) {
            child1god = childGode;
          } else if (child2god == null) {
            child2god = childGode;
          }

        }

        if (mergePoint == null) {
          if (child1god != null && child2god != null) {
            DominatorNode<Unit> child1 = pdom.getDode(child1god);
            DominatorNode<Unit> child2 = pdom.getDode(child2god);

            // go up the pdom tree and find the common parent of
            // child1 and child2
            DominatorNode<Unit> comParent = child1.getParent();
            while (comParent != null) {
              if (pdom.isDominatorOf(comParent, child2)) {
                mergePoint = comParent.getGode();
                break;
              }
              comParent = comParent.getParent();
            }
          } else if (child1god != null || child2god != null) {

            DominatorNode<Unit> y = null;

            if (child1god != null) {
              y = pdom.getDode(child1god);
            } else if (child2god != null) {
              y = pdom.getDode(child2god);
            }

            DominatorNode<Unit> initialY = dom.getDode(y.getGode());
            DominatorNode<Unit> yDodeInDom = initialY;

            while (dom.isDominatorOf(x, yDodeInDom)) {
              y = y.getParent();

              // If this is a case where the childs of a
              // conditional
              // are all throws, or returns, just forget it!
              if (y == null) {
                break;
              }
              yDodeInDom = dom.getDode(y.getGode());
            }
            if (y != null) {
              mergePoint = y.getGode();
            } else {
              mergePoint = initialY.getGode();
            }
          }
        }

        // This means no (dom) child of x post-dominates x, so just use
        // the child that is
        // immediately
        /*
         * if(mergePoint == null) { //throw new RuntimeException("No child post-dominates x."); mergePoint =
         * potentialMergePoint;
         *
         * }
         */
        // This means no (dom) child of x post-dominates x, so just use
        // the child that is
        // immediately. this means there is no good reliable merge
        // point. So we just fetch the succ
        // of x in CFg so that the succ does not dominate the throw, and
        // find the first
        // post-dom of the succ so that x does not dom it.
        //
        if (mergePoint == null) {
          List<Unit> xSucc = this.unitToSuccs.get(x.getGode());
          if (xSucc != null) {
            for (Unit u : xSucc) {
              if (dom.isDominatorOf(dom.getDode(u), dom.getDode(tail))) {
                continue;
              }

              DominatorNode<Unit> y = pdom.getDode(u);

              while (dom.isDominatorOf(x, y)) {
                y = y.getParent();
                // If this is a case where the childs of a
                // conditional
                // are all throws, or returns, just forget it!
                if (y == null) {
                  continue TailsLoop;
                }
              }
              mergePoint = y.getGode();
              break;
            }
          }
        }
        // the following happens if the throw is the only exit in the
        // method (even if return stmt is present.)
        else if (dom.isDominatorOf(dom.getDode(mergePoint), dom.getDode(tail))) {
          continue TailsLoop;
        }

        // turns out that when the method ends with a throw, the control
        // get here.
        if (mergePoint == null) {
          continue TailsLoop;
        }

        x2mergePoint.put(xgode, mergePoint);
      }
      // add an edge from the tail (throw) to the merge point

      List<Unit> throwSuccs = this.unitToSuccs.get(tail);
      if (throwSuccs == null) {
        throwSuccs = new ArrayList<Unit>();
        this.unitToSuccs.put(tail, throwSuccs);
      }

      throwSuccs.add(mergePoint);

      List<Unit> mergePreds = this.unitToPreds.get(mergePoint);
      if (mergePreds == null) {
        mergePreds = new ArrayList<Unit>();
        this.unitToPreds.put(mergePoint, mergePreds);
      }
      mergePreds.add(tail);

    }

  }

  /**
   * Add an exceptional flow edge for each handler from the corresponding auxiliary nop node to the beginning of the handler.
   */
  protected void addAuxiliaryExceptionalEdges() {

    // Do some preparation for each trap in the method
    for (Iterator<Trap> trapIt = body.getTraps().iterator(); trapIt.hasNext();) {
      Trap trap = trapIt.next();

      /**
       * Find the real header of this handler block
       *
       */
      Unit handler = trap.getHandlerUnit();

      Unit pred = handler;
      while (this.unitToPreds.get(pred).size() > 0) {
        pred = this.unitToPreds.get(pred).get(0);
      }

      handler2header.put(handler, pred);
      /***********/

      /*
       * Keep this here for possible future changes.
       */
      /*
       * GuardedBlock gb = new GuardedBlock(trap.getBeginUnit(), trap.getEndUnit()); Unit ehnop; if(try2nop.containsKey(gb))
       * ehnop = try2nop.get(gb); else { ehnop = new EHNopStmt(); try2nop.put(gb, ehnop); }
       */

      Unit ehnop;
      if (try2nop.containsKey(trap.getBeginUnit())) {
        ehnop = try2nop.get(trap.getBeginUnit());
      } else {
        ehnop = new EHNopStmt();
        try2nop.put(trap.getBeginUnit(), ehnop);
      }

    }

    // Only add a nop once
    Hashtable<Unit, Boolean> nop2added = new Hashtable<Unit, Boolean>();

    // Now actually add the edge
    AddExceptionalEdge: for (Iterator<Trap> trapIt = body.getTraps().iterator(); trapIt.hasNext();) {

      Trap trap = trapIt.next();
      Unit b = trap.getBeginUnit();
      Unit handler = trap.getHandlerUnit();
      handler = handler2header.get(handler);

      /**
       * Check if this trap is a finally trap that handles exceptions of an adjacent catch block; what differentiates such
       * trap is that it's guarded region has the same parent as the handler of the trap itself, in the dom tree.
       *
       * The problem is that we don't have a complete DOM tree at this transient state.
       *
       * The work-around is to not process a trap that has already an edge pointing to it.
       *
       */

      if (this.unitToPreds.containsKey(handler)) {
        List<Unit> handlerPreds = this.unitToPreds.get(handler);
        for (Iterator<Unit> preditr = handlerPreds.iterator(); preditr.hasNext();) {
          if (try2nop.containsValue(preditr.next())) {
            continue AddExceptionalEdge;
          }
        }

      } else {
        continue;
      }

      // GuardedBlock gb = new GuardedBlock(b, e);
      Unit ehnop = try2nop.get(b);

      if (!nop2added.containsKey(ehnop)) {
        List<Unit> predsOfB = getPredsOf(b);
        List<Unit> predsOfehnop = new ArrayList<Unit>(predsOfB);

        for (Unit a : predsOfB) {
          List<Unit> succsOfA = this.unitToSuccs.get(a);
          if (succsOfA == null) {
            succsOfA = new ArrayList<Unit>();
            this.unitToSuccs.put(a, succsOfA);
          } else {
            succsOfA.remove(b);
          }
          succsOfA.add((Unit) ehnop);
        }

        predsOfB.clear();
        predsOfB.add((Unit) ehnop);

        this.unitToPreds.put((Unit) ehnop, predsOfehnop);
      }

      List<Unit> succsOfehnop = this.unitToSuccs.get(ehnop);
      if (succsOfehnop == null) {
        succsOfehnop = new ArrayList<Unit>();
        this.unitToSuccs.put(ehnop, succsOfehnop);
      }

      if (!succsOfehnop.contains(b)) {
        succsOfehnop.add(b);
      }

      succsOfehnop.add(handler);

      List<Unit> predsOfhandler = this.unitToPreds.get(handler);
      if (predsOfhandler == null) {
        predsOfhandler = new ArrayList<Unit>();
        this.unitToPreds.put(handler, predsOfhandler);
      }

      predsOfhandler.add((Unit) ehnop);

      Chain<Unit> units = body.getUnits().getNonPatchingChain();

      if (!units.contains(ehnop)) {
        units.insertBefore((Unit) ehnop, b);
      }

      nop2added.put(ehnop, Boolean.TRUE);
    }

  }
}

/**
 * This class represents a block of code guarded by a trap. Currently, this is not used but it might well be put to use in
 * later updates.
 *
 * @author Hossein Sadat-Mohtasham
 *
 */
class GuardedBlock {

  Unit start, end;

  public GuardedBlock(Unit s, Unit e) {
    this.start = s;
    this.end = e;
  }

  public int hashCode() {
    // Following Joshua Bloch's recipe in "Effective Java", Item 8:
    int result = 17;
    result = 37 * result + this.start.hashCode();
    result = 37 * result + this.end.hashCode();
    return result;
  }

  public boolean equals(Object rhs) {
    if (rhs == this) {
      return true;
    }
    if (!(rhs instanceof GuardedBlock)) {
      return false;
    }
    GuardedBlock rhsGB = (GuardedBlock) rhs;
    return ((this.start == rhsGB.start) && (this.end == rhsGB.end));
  }
}

/**
 *
 * @author Hossein Sadat-Mohtasham Feb 2010
 *
 *         This class represents a special nop statement that marks the beginning of a try block at the Jimple level. This is
 *         going to be used in the CFG enhancement.
 *
 */

@SuppressWarnings("serial")
class EHNopStmt extends JNopStmt {
  public EHNopStmt() {
  }

  public Object clone() {
    return new EHNopStmt();
  }

  public boolean fallsThrough() {
    return true;
  }

  public boolean branches() {
    return false;
  }

}

/**
 *
 * @author Hossein Sadat-Mohtasham Feb 2010
 *
 *         This class represents a special nop statement that marks the beginning of method body at the Jimple level. This is
 *         going to be used in the CFG enhancement.
 *
 */
@SuppressWarnings("serial")
class EntryStmt extends JNopStmt {
  public EntryStmt() {
  }

  public Object clone() {
    return new EntryStmt();
  }

  public boolean fallsThrough() {
    return true;
  }

  public boolean branches() {
    return false;
  }

}

/**
 *
 * @author Hossein Sadat-Mohtasham Feb 2010
 *
 *         This class represents a special nop statement that marks the exit of method body at the Jimple level. This is
 *         going to be used in the CFG enhancement.
 *
 */

@SuppressWarnings("serial")
class ExitStmt extends JNopStmt {
  public ExitStmt() {
  }

  public Object clone() {
    return new ExitStmt();
  }

  public boolean fallsThrough() {
    return true;
  }

  public boolean branches() {
    return false;
  }

}
