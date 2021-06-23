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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import soot.IdentityUnit;
import soot.Local;
import soot.Trap;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.AssignStmt;
import soot.jimple.Jimple;
import soot.shimple.PhiExpr;
import soot.shimple.Shimple;
import soot.shimple.ShimpleBody;
import soot.shimple.ShimpleFactory;
import soot.toolkits.graph.Block;
import soot.toolkits.graph.BlockGraph;
import soot.toolkits.graph.DominanceFrontier;
import soot.toolkits.graph.DominatorNode;
import soot.toolkits.graph.DominatorTree;
import soot.toolkits.scalar.ValueUnitPair;
import soot.util.Chain;
import soot.util.HashMultiMap;
import soot.util.MultiMap;

/**
 * @author Navindra Umanee
 * @see soot.shimple.ShimpleBody
 * @see <a href="http://citeseer.nj.nec.com/cytron91efficiently.html">Efficiently Computing Static Single Assignment Form and
 *      the Control Dependence Graph</a>
 */
public class PhiNodeManager {

  protected final ShimpleBody body;
  protected final ShimpleFactory sf;

  protected BlockGraph cfg;
  protected MultiMap<Local, Block> varToBlocks;
  protected Map<Unit, Block> unitToBlock;

  public PhiNodeManager(ShimpleBody body, ShimpleFactory sf) {
    this.body = body;
    this.sf = sf;
  }

  public void update() {
    BlockGraph oldCfg = this.cfg;
    this.cfg = sf.getBlockGraph();
    if (oldCfg != this.cfg) {
      // If the CFG was rebuilt, clear the maps because Blocks are stale
      this.unitToBlock = null;
      this.varToBlocks = null;
    }
  }

  /**
   * Phi node Insertion Algorithm from Cytron et al 91, P24-5,
   *
   * <p>
   * Special Java case: If a variable is not defined along all paths of entry to a node, a Phi node is not needed.
   * </p>
   */
  public boolean insertTrivialPhiNodes() {
    update();

    this.varToBlocks = new HashMultiMap<Local, Block>();
    final Map<Local, List<Block>> localsToDefPoints = new LinkedHashMap<Local, List<Block>>();

    // compute localsToDefPoints and varToBlocks
    for (Block block : cfg) {
      for (Unit unit : block) {
        for (ValueBox vb : unit.getDefBoxes()) {
          Value def = vb.getValue();
          if (def instanceof Local) {
            Local local = (Local) def;
            List<Block> def_points = localsToDefPoints.get(local);
            if (def_points == null) {
              def_points = new ArrayList<Block>();
              localsToDefPoints.put(local, def_points);
            }
            def_points.add(block);
          }
        }

        if (Shimple.isPhiNode(unit)) {
          varToBlocks.put(Shimple.getLhsLocal(unit), block);
        }
      }
    }

    boolean change = false;
    {
      final DominatorTree<Block> dt = sf.getDominatorTree();
      final DominanceFrontier<Block> df = sf.getDominanceFrontier();

      /* Routine initialisations. */
      int iterCount = 0;
      int[] workFlags = new int[cfg.size()];
      Stack<Block> workList = new Stack<Block>();
      Map<Integer, Integer> has_already = new HashMap<Integer, Integer>();
      for (Block block : cfg) {
        has_already.put(block.getIndexInMethod(), 0);
      }

      /* Main Cytron algorithm. */
      for (Map.Entry<Local, List<Block>> e : localsToDefPoints.entrySet()) {
        iterCount++;

        // initialise worklist
        {
          assert (workList.isEmpty());
          List<Block> def_points = e.getValue();
          // if the local is only defined once, no need for phi nodes
          if (def_points.size() == 1) {
            continue;
          }
          for (Block block : def_points) {
            workFlags[block.getIndexInMethod()] = iterCount;
            workList.push(block);
          }
        }

        Local local = e.getKey();
        while (!workList.empty()) {
          Block block = workList.pop();
          for (DominatorNode<Block> dn : df.getDominanceFrontierOf(dt.getDode(block))) {
            Block frontierBlock = dn.getGode();

            if (!frontierBlock.iterator().hasNext()) {
              continue;
            }

            int fBIndex = frontierBlock.getIndexInMethod();
            if (has_already.get(fBIndex) < iterCount) {
              has_already.put(fBIndex, iterCount);
              prependTrivialPhiNode(local, frontierBlock);
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

    return change;
  }

  /**
   * Inserts a trivial Phi node with the appropriate number of arguments.
   */
  public void prependTrivialPhiNode(Local local, Block frontierBlock) {
    PhiExpr pe = Shimple.v().newPhiExpr(local, frontierBlock.getPreds());
    pe.setBlockId(frontierBlock.getIndexInMethod());
    Unit trivialPhi = Jimple.v().newAssignStmt(local, pe);

    // is it a catch block?
    Unit head = frontierBlock.getHead();
    if (head instanceof IdentityUnit) {
      frontierBlock.insertAfter(trivialPhi, head);
    } else {
      frontierBlock.insertBefore(trivialPhi, head);
    }

    varToBlocks.put(local, frontierBlock);
  }

  /**
   * Exceptional Phi nodes have a huge number of arguments and control flow predecessors by default. Since it is useless
   * trying to keep the number of arguments and control flow predecessors in synch, we might as well trim out all redundant
   * arguments and eliminate a huge number of copy statements when we get out of SSA form in the process.
   */
  public void trimExceptionalPhiNodes() {
    Set<Unit> handlerUnits = new HashSet<Unit>();
    for (Trap trap : body.getTraps()) {
      handlerUnits.add(trap.getHandlerUnit());
    }

    for (Block block : cfg) {
      // trim relevant Phi expressions
      if (handlerUnits.contains(block.getHead())) {
        for (Unit unit : block) {
          // if(!(newPhiNodes.contains(unit)))
          PhiExpr phi = Shimple.getPhiExpr(unit);
          if (phi != null) {
            trimPhiNode(phi);
          }
        }
      }
    }
  }

  /**
   * @see #trimExceptionalPhiNodes()
   */
  public void trimPhiNode(PhiExpr phiExpr) {
    /*
     * A value may appear many times in an exceptional Phi. Hence, the same value may be associated with many UnitBoxes. We
     * build the MultiMap valueToPairs for convenience.
     */
    MultiMap<Value, ValueUnitPair> valueToPairs = new HashMultiMap<Value, ValueUnitPair>();
    for (ValueUnitPair argPair : phiExpr.getArgs()) {
      valueToPairs.put(argPair.getValue(), argPair);
    }

    /*
     * Consider each value and see if we can find the dominating UnitBoxes. Once we have found all the dominating UnitBoxes,
     * the rest of the redundant arguments can be trimmed.
     */
    for (Value value : valueToPairs.keySet()) {
      // although the champs list constantly shrinks, guaranteeing
      // termination, the challengers list never does. This could
      // be optimised.
      Set<ValueUnitPair> pairsSet = valueToPairs.get(value);
      List<ValueUnitPair> champs = new LinkedList<ValueUnitPair>(pairsSet);
      List<ValueUnitPair> challengers = new LinkedList<ValueUnitPair>(pairsSet);

      // champ is the currently assumed dominator
      ValueUnitPair champ = champs.remove(0);
      Unit champU = champ.getUnit();

      // hopefully everything will work out the first time, but
      // if not, we will have to try a new champion just in case
      // there is more that can be trimmed.

      for (boolean retry = true; retry;) {
        retry = false;

        // go through each challenger and see if we dominate them
        // if not, the challenger becomes the new champ
        for (Iterator<ValueUnitPair> itr = challengers.iterator(); itr.hasNext();) {
          ValueUnitPair challenger = itr.next();
          if (challenger.equals(champ)) {
            continue;
          }
          Unit challengerU = challenger.getUnit();
          if (dominates(champU, challengerU)) {
            // kill the challenger
            phiExpr.removeArg(challenger);
            itr.remove();
          } else if (dominates(challengerU, champU)) {
            // we die, find a new champ
            phiExpr.removeArg(champ);
            champ = challenger;
            champU = champ.getUnit();
          } else {
            // neither wins, oops! we'll have to try the next available champ
            // at the next pass. It may very well be inevitable that we will
            // have two identical value args in an exceptional PhiExpr, but the
            // more we can trim the better.
            retry = true;
          }
        }

        if (retry) {
          if (champs.isEmpty()) {
            break;
          } else {
            champ = champs.remove(0);
            champU = champ.getUnit();
          }
        }
      }
    }
    /*
     * { List preds = phiExpr.getPreds();
     *
     * for(int i = 0; i < phiExpr.getArgCount(); i++){ ValueUnitPair vup = phiExpr.getArgBox(i); Value value =
     * vup.getValue(); Unit unit = vup.getUnit();
     *
     * PhiExpr innerPhi = Shimple.getPhiExpr(unit); if(innerPhi == null) continue;
     *
     * Value innerValue = Shimple.getLhsLocal(unit); if(!innerValue.equals(value)) continue;
     *
     * boolean canRemove = true; for(int j = 0; j < innerPhi.getArgCount(); j++){ Unit innerPred = innerPhi.getPred(j);
     * if(!preds.contains(innerPred)){ canRemove = false; break; } }
     *
     * if(canRemove) phiExpr.removeArg(vup); } }
     */
  }

  /**
   * Returns true if champ dominates challenger. Note that false doesn't necessarily mean that challenger dominates champ.
   */
  public boolean dominates(Unit champ, Unit challenger) {
    if (champ == null || challenger == null) {
      throw new RuntimeException("Assertion failed.");
    }

    // self-domination
    if (champ.equals(challenger)) {
      return true;
    }

    Map<Unit, Block> unitToBlock = this.unitToBlock;
    if (unitToBlock == null) {
      unitToBlock = getUnitToBlockMap(cfg);
      this.unitToBlock = unitToBlock;
    }

    Block champBlock = unitToBlock.get(champ);
    Block challengerBlock = unitToBlock.get(challenger);
    if (champBlock.equals(challengerBlock)) {
      for (Unit unit : champBlock) {
        if (unit.equals(champ)) {
          return true;
        }
        if (unit.equals(challenger)) {
          return false;
        }
      }
      throw new RuntimeException("Assertion failed.");
    }

    DominatorTree<Block> dt = sf.getDominatorTree();
    DominatorNode<Block> champNode = dt.getDode(champBlock);
    DominatorNode<Block> challengerNode = dt.getDode(challengerBlock);
    // *** FIXME: System.out.println("champ: " + champNode);
    // System.out.println("chall: " + challengerNode);
    return dt.isDominatorOf(champNode, challengerNode);
  }

  /**
   * Eliminate Phi nodes in block by naively replacing them with shimple assignment statements in the control flow
   * predecessors. Returns true if new locals were added to the body during the process, false otherwise.
   */
  public boolean doEliminatePhiNodes() {
    // flag that indicates whether we created new locals during the elimination process
    boolean addedNewLocals = false;

    // List of Phi nodes to be deleted.
    List<Unit> phiNodes = new ArrayList<Unit>();

    // This stores the assignment statements equivalent to each
    // (and every) Phi. We use lists instead of a Map of
    // non-determinate order since we prefer to preserve the order
    // of the assignment statements, i.e. if a block has more than
    // one Phi expression, we prefer that the equivalent
    // assignments be placed in the same order as the Phi expressions.
    List<AssignStmt> equivStmts = new ArrayList<AssignStmt>();

    // Similarly, to preserve order, instead of directly storing
    // the pred, we store the pred box so that we follow the
    // pointers when SPatchingChain moves them.
    List<ValueUnitPair> predBoxes = new ArrayList<ValueUnitPair>();

    final Jimple jimp = Jimple.v();
    final Chain<Unit> units = body.getUnits();
    for (Unit unit : units) {
      PhiExpr phi = Shimple.getPhiExpr(unit);
      if (phi != null) {
        Local lhsLocal = Shimple.getLhsLocal(unit);
        for (ValueUnitPair vup : phi.getArgs()) {
          predBoxes.add(vup);
          equivStmts.add(jimp.newAssignStmt(lhsLocal, vup.getValue()));
        }
        phiNodes.add(unit);
      }
    }

    if (equivStmts.size() != predBoxes.size()) {
      throw new RuntimeException("Assertion failed.");
    }

    for (ListIterator<ValueUnitPair> it = predBoxes.listIterator(); it.hasNext();) {
      Unit pred = it.next().getUnit();
      if (pred == null) {
        throw new RuntimeException("Assertion failed.");
      }
      AssignStmt stmt = equivStmts.get(it.previousIndex());

      // if we need to insert the copy statement *before* an
      // instruction that happens to be *using* the Local being
      // defined, we need to do some extra work to make sure we
      // don't overwrite the old value of the local
      if (pred.branches()) {
        boolean needPriming = false;
        Local lhsLocal = (Local) stmt.getLeftOp();
        Local savedLocal = jimp.newLocal(lhsLocal.getName() + "_", lhsLocal.getType());

        for (ValueBox useBox : pred.getUseBoxes()) {
          if (lhsLocal.equals(useBox.getValue())) {
            needPriming = true;
            addedNewLocals = true;
            useBox.setValue(savedLocal);
          }
        }

        if (needPriming) {
          body.getLocals().add(savedLocal);
          units.insertBefore(jimp.newAssignStmt(savedLocal, lhsLocal), pred);
        }

        // this is all we really wanted to do!
        units.insertBefore(stmt, pred);
      } else {
        units.insertAfter(stmt, pred);
      }
    }

    for (Unit removeMe : phiNodes) {
      units.remove(removeMe);
      removeMe.clearUnitBoxes();
    }

    return addedNewLocals;
  }

  /**
   * Convenience function that maps units to blocks. Should probably be in BlockGraph.
   */
  public static Map<Unit, Block> getUnitToBlockMap(BlockGraph blocks) {
    Map<Unit, Block> unitToBlock = new HashMap<Unit, Block>();
    for (Block block : blocks) {
      for (Unit unit : block) {
        unitToBlock.put(unit, block);
      }
    }
    return unitToBlock;
  }
}
