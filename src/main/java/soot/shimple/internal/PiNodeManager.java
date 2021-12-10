package soot.shimple.internal;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Navindra Umanee <navindra@cs.mcgill.ca>
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
import java.util.NoSuchElementException;
import java.util.Stack;

import soot.Local;
import soot.PatchingChain;
import soot.Unit;
import soot.UnitBox;
import soot.Value;
import soot.ValueBox;
import soot.jimple.AssignStmt;
import soot.jimple.IfStmt;
import soot.jimple.Jimple;
import soot.jimple.LookupSwitchStmt;
import soot.jimple.TableSwitchStmt;
import soot.jimple.toolkits.scalar.CopyPropagator;
import soot.jimple.toolkits.scalar.DeadAssignmentEliminator;
import soot.shimple.PiExpr;
import soot.shimple.Shimple;
import soot.shimple.ShimpleBody;
import soot.shimple.ShimpleFactory;
import soot.toolkits.graph.Block;
import soot.toolkits.graph.DominanceFrontier;
import soot.toolkits.graph.DominatorNode;
import soot.toolkits.graph.DominatorTree;
import soot.toolkits.graph.ReversibleGraph;
import soot.util.HashMultiMap;
import soot.util.MultiMap;

/**
 * This class does the real high-level work. It takes a Jimple body or Jimple/Shimple hybrid body and produces pure Shimple.
 *
 * <p>
 * The work is done in two main steps:
 *
 * <ol>
 * <li>Trivial Phi nodes are added.
 * <li>A renaming algorithm is executed.
 * </ol>
 *
 * <p>
 * This class can also translate out of Shimple by producing an equivalent Jimple body with all Phi nodes removed.
 *
 * <p>
 * Note that this is an internal class, understanding it should not be necessary from a user point-of-view and relying on it
 * directly is not recommended.
 *
 * @author Navindra Umanee
 * @see soot.shimple.ShimpleBody
 * @see <a href="http://citeseer.nj.nec.com/cytron91efficiently.html">Efficiently Computing Static Single Assignment Form and
 *      the Control Dependence Graph</a>
 */
public class PiNodeManager {

  protected final ShimpleBody body;
  protected final ShimpleFactory sf;
  protected final boolean trimmed;

  protected ReversibleGraph<Block> cfg;
  protected MultiMap<Local, Block> varToBlocks;

  /**
   * Transforms the provided body to pure SSA form.
   */
  public PiNodeManager(ShimpleBody body, boolean trimmed, ShimpleFactory sf) {
    this.body = body;
    this.trimmed = trimmed;
    this.sf = sf;
  }

  public void update() {
    ReversibleGraph<Block> oldCfg = this.cfg;
    this.cfg = sf.getReverseBlockGraph();
    if (oldCfg != this.cfg) {
      // If the CFG was rebuilt, clear the map because Blocks are stale
      this.varToBlocks = null;
    }
  }

  public boolean insertTrivialPiNodes() {
    update();

    this.varToBlocks = new HashMultiMap<Local, Block>();
    final MultiMap<Local, Block> localsToUsePoints = new SHashMultiMap<Local, Block>();

    // compute localsToUsePoints and varToBlocks
    for (Block block : cfg) {
      for (Unit unit : block) {
        for (ValueBox next : unit.getUseBoxes()) {
          Value use = next.getValue();
          if (use instanceof Local) {
            localsToUsePoints.put((Local) use, block);
          }
        }

        if (Shimple.isPiNode(unit)) {
          varToBlocks.put(Shimple.getLhsLocal(unit), block);
        }
      }
    }

    boolean change = false;
    {
      final DominatorTree<Block> dt = sf.getReverseDominatorTree();
      final DominanceFrontier<Block> df = sf.getReverseDominanceFrontier();

      /* Routine initialisations. */
      int iterCount = 0;
      int[] workFlags = new int[cfg.size()];
      int[] hasAlreadyFlags = new int[cfg.size()];
      Stack<Block> workList = new Stack<Block>();

      /* Main Cytron algorithm. */
      for (Local local : localsToUsePoints.keySet()) {
        iterCount++;

        // initialise worklist
        for (Block block : localsToUsePoints.get(local)) {
          workFlags[block.getIndexInMethod()] = iterCount;
          workList.push(block);
        }

        while (!workList.empty()) {
          Block block = workList.pop();
          for (DominatorNode<Block> frontierNode : df.getDominanceFrontierOf(dt.getDode(block))) {
            Block frontierBlock = frontierNode.getGode();

            int fBIndex = frontierBlock.getIndexInMethod();
            if (hasAlreadyFlags[fBIndex] < iterCount) {
              hasAlreadyFlags[fBIndex] = iterCount;
              insertPiNodes(local, frontierBlock);
              change = true;

              if (workFlags[fBIndex] < iterCount) {
                workFlags[fBIndex] = iterCount;
                workList.push(frontierBlock);
              }
            }
          }
        }
      }
    }

    if (change) {
      sf.clearCache();
    }
    return change;
  }

  public void insertPiNodes(Local local, Block frontierBlock) {
    if (varToBlocks.get(local).contains(frontierBlock.getSuccs().get(0))) {
      return;
    }

    Unit u = frontierBlock.getTail();

    TRIMMED: {
      if (trimmed) {
        for (ValueBox vb : u.getUseBoxes()) {
          if (vb.getValue().equals(local)) {
            break TRIMMED;
          }
        }
        return;
      }
    }

    if (u instanceof IfStmt) {
      piHandleIfStmt(local, (IfStmt) u);
    } else if ((u instanceof LookupSwitchStmt) || (u instanceof TableSwitchStmt)) {
      piHandleSwitchStmt(local, u);
    } else {
      throw new RuntimeException("Assertion failed: Unhandled stmt: " + u);
    }
  }

  public void piHandleIfStmt(Local local, IfStmt u) {
    final PatchingChain<Unit> units = body.getUnits();

    Unit target = u.getTarget();
    Unit addt = Jimple.v().newAssignStmt(local, Shimple.v().newPiExpr(local, u, Boolean.TRUE));
    Unit addf = Jimple.v().newAssignStmt(local, Shimple.v().newPiExpr(local, u, Boolean.FALSE));

    // insert after should be safe; a new block should result if
    // the Unit originally after the IfStmt had another predecessor.
    // what about SPatchingChain? seems sane.
    units.insertAfter(addf, u);

    /*
     * we need to be careful with insertBefore, if target already had some other predecessors.
     */

    // handle immediate predecessor if it falls through
    // *** FIXME: Does SPatchingChain do the right thing?
    {
      Unit predOfTarget;
      try {
        predOfTarget = units.getPredOf(target);
      } catch (NoSuchElementException e) {
        predOfTarget = null;
      }
      if (predOfTarget != null && predOfTarget.fallsThrough()) {
        units.insertAfter(Jimple.v().newGotoStmt(target), predOfTarget);
      }
    }

    // we do not want to move the pointers for other branching statements
    units.getNonPatchingChain().insertBefore(addt, target);
    u.setTarget(addt);
  }

  public void piHandleSwitchStmt(Local local, Unit u) {
    List<UnitBox> targetBoxes = new ArrayList<UnitBox>();
    List<Object> targetKeys = new ArrayList<Object>();

    if (u instanceof LookupSwitchStmt) {
      LookupSwitchStmt lss = (LookupSwitchStmt) u;
      targetBoxes.add(lss.getDefaultTargetBox());
      targetKeys.add("default");
      for (int i = 0, e = lss.getTargetCount(); i < e; i++) {
        targetBoxes.add(lss.getTargetBox(i));
      }
      targetKeys.addAll(lss.getLookupValues());
    } else if (u instanceof TableSwitchStmt) {
      TableSwitchStmt tss = (TableSwitchStmt) u;
      int low = tss.getLowIndex();
      int hi = tss.getHighIndex();

      targetBoxes.add(tss.getDefaultTargetBox());
      targetKeys.add("default");
      for (int i = 0, e = (hi - low); i <= e; i++) {
        targetBoxes.add(tss.getTargetBox(i));
      }
      for (int i = low; i <= hi; i++) {
        targetKeys.add(i);
      }
    } else {
      throw new RuntimeException("Assertion failed.");
    }

    for (int count = 0, e = targetBoxes.size(); count < e; count++) {
      UnitBox targetBox = targetBoxes.get(count);
      Unit target = targetBox.getUnit();

      PatchingChain<Unit> units = body.getUnits();

      /*
       * we need to be careful with insertBefore, if target already had some other predecessors.
       */

      // handle immediate predecessor if it falls through
      // *** FIXME: Does SPatchingChain do the right thing?
      {
        Unit predOfTarget;
        try {
          predOfTarget = units.getPredOf(target);
        } catch (NoSuchElementException ex) {
          predOfTarget = null;
        }
        if (predOfTarget != null && predOfTarget.fallsThrough()) {
          units.insertAfter(Jimple.v().newGotoStmt(target), predOfTarget);
        }
      }

      // we do not want to move the pointers for other branching statements
      Unit add1 = Jimple.v().newAssignStmt(local, Shimple.v().newPiExpr(local, u, targetKeys.get(count)));
      units.getNonPatchingChain().insertBefore(add1, target);
      targetBox.setUnit(add1);
    }
  }

  public void eliminatePiNodes(boolean smart) {
    if (smart) {
      Map<Value, Value> newToOld = new HashMap<Value, Value>();
      List<ValueBox> boxes = new ArrayList<ValueBox>();

      for (Iterator<Unit> unitsIt = body.getUnits().iterator(); unitsIt.hasNext();) {
        Unit u = unitsIt.next();
        PiExpr pe = Shimple.getPiExpr(u);
        if (pe != null) {
          newToOld.put(Shimple.getLhsLocal(u), pe.getValue());
          unitsIt.remove();
        } else {
          boxes.addAll(u.getUseBoxes());
        }
      }

      for (ValueBox box : boxes) {
        Value value = box.getValue();
        Value old = newToOld.get(value);
        if (old != null) {
          box.setValue(old);
        }
      }

      DeadAssignmentEliminator.v().transform(body);
      CopyPropagator.v().transform(body);
      DeadAssignmentEliminator.v().transform(body);
    } else {
      for (Unit u : body.getUnits()) {
        PiExpr pe = Shimple.getPiExpr(u);
        if (pe != null) {
          ((AssignStmt) u).setRightOp(pe.getValue());
        }
      }
    }
  }

  public static List<ValueBox> getUseBoxesFromBlock(Block block) {
    List<ValueBox> useBoxesList = new ArrayList<ValueBox>();
    for (Unit next : block) {
      useBoxesList.addAll(next.getUseBoxes());
    }
    return useBoxesList;
  }
}
