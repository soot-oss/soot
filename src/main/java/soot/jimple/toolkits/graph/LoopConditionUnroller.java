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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Body;
import soot.BodyTransformer;
import soot.PhaseOptions;
import soot.Trap;
import soot.Unit;
import soot.jimple.GotoStmt;
import soot.jimple.IfStmt;
import soot.jimple.Jimple;
import soot.options.Options;
import soot.toolkits.graph.Block;
import soot.toolkits.graph.BlockGraph;
import soot.toolkits.graph.BriefBlockGraph;
import soot.util.Chain;

/**
 * "unrolls" the condition of while/for loops.<br>
 * before the first test of a while-loop, we can't be sure, if the body will be taken or not, and several optimizations
 * (especially LCM) can't be done. In this class we try to solve this problem by unrolling the condition of the while-block:
 * we make a copy of the condition-block, and redirect the back-edge of the while-loop to the new block.<br>
 * After this transformation the edge between the original condition-block and the loop-body is only executed once (and hence
 * suitable for LCM) and we can be sure, that the loop-body will get executed.<br>
 * Exceptions are ignored (the transformation is done on a <code>BriefBlockGraph</code>.
 */
public class LoopConditionUnroller extends BodyTransformer {
  private static final Logger logger = LoggerFactory.getLogger(LoopConditionUnroller.class);

  /**
   * contained blocks are currently visiting successors. We need this to find back-edges. The "visitedBlocks" is not enough,
   * as Java Bytecodes might not be in tree-form.
   */
  private Set<Block> visitingSuccs;
  private Set<Block> visitedBlocks;
  private int maxSize;
  private Chain<Unit> unitChain;
  private Chain<Trap> trapChain;
  private Map<Unit, List<Trap>> unitsToTraps;

  /**
   * unrolls conditions.
   */
  /*
   * this implementation still fails in finding all possible while-loops, but does a good job.
   */
  @Override
  protected void internalTransform(Body body, String phaseName, Map<String, String> options) {
    if (Options.v().verbose()) {
      logger.debug("[" + body.getMethod().getName() + "]     Unrolling Loop Conditions...");
    }

    this.visitingSuccs = new HashSet<Block>();
    this.visitedBlocks = new HashSet<Block>();
    this.maxSize = PhaseOptions.getInt(options, "maxSize");
    this.unitChain = body.getUnits();
    this.trapChain = body.getTraps();
    this.unitsToTraps = mapBeginEndUnitToTrap(trapChain);

    BlockGraph bg = new BriefBlockGraph(body);
    for (Block b : bg.getHeads()) {
      unrollConditions(b);
    }

    if (Options.v().verbose()) {
      logger.debug("[" + body.getMethod().getName() + "]     Unrolling Loop Conditions done.");
    }
  }

  /**
   * inserts a Jimple<code>Goto</code> to <code> target, directly after
   * <code>node</code> in the <code>unitChain</code> of the body.<br>
   * As we use <code>JGoto</code> the chain must contain Jimple-stmts.
   *
   * @param node
   *          the <code>Goto</code> will be inserted just after this node.
   * @param target
   *          is the Unit the <code>goto</code> will jump to.
   * @return the newly inserted <code>Goto</code>
   */
  private Unit insertGotoAfter(Unit node, Unit target) {
    Unit newGoto = Jimple.v().newGotoStmt(target);
    unitChain.insertAfter(newGoto, node);
    return newGoto;
  }

  /**
   * inserts a clone of <code>toClone</code> after <code>node</code> in the <code>unitChain</code>.<br>
   * Everything is done in Jimple.
   *
   * @param node
   *          the Unit after which we insert the clone.
   * @param toClone
   *          the Unit that will get cloned and then inserted.
   */
  private Unit insertCloneAfter(Unit node, Unit toClone) {
    Unit clone = (Unit) toClone.clone();
    unitChain.insertAfter(clone, node);
    return clone;
  }

  /**
   * "calculates" the length of the given block in Units.
   *
   * @param block
   * @return the size of <code>block</code>.
   */
  private int getSize(Block block) {
    int size = 1; // add 1 for the tail not counted by the loop below
    Chain<Unit> chain = this.unitChain;
    for (Unit unit = block.getHead(), e = block.getTail(); unit != e; unit = chain.getSuccOf(unit)) {
      size++;
    }
    return size;
  }

  /**
   * returns a mapping of units to trap-changes. whenever the scope of a trap changes (ie. a trap opens or closes), an entry
   * is added in the map, and the unit is mapped to the trap. The values associated to the keys are lists, as more than one
   * exception can change at a unit.<br>
   * Even if a trap opens and closes at a unit, this trap is only reported once (ie. is only once in the list).
   *
   * @return the map of units to changing traps.
   */
  private static Map<Unit, List<Trap>> mapBeginEndUnitToTrap(Chain<Trap> trapChain) {
    Map<Unit, List<Trap>> unitsToTraps = new HashMap<Unit, List<Trap>>();
    for (Trap trap : trapChain) {
      Unit beginUnit = trap.getBeginUnit();
      {
        List<Trap> unitTraps = unitsToTraps.get(beginUnit);
        if (unitTraps == null) {
          unitTraps = new ArrayList<Trap>();
          unitsToTraps.put(beginUnit, unitTraps);
        }
        unitTraps.add(trap);
      }
      Unit endUnit = trap.getEndUnit();
      if (endUnit != beginUnit) {
        List<Trap> unitTraps = unitsToTraps.get(endUnit);
        if (unitTraps == null) {
          unitTraps = new ArrayList<Trap>();
          unitsToTraps.put(endUnit, unitTraps);
        }
        unitTraps.add(trap);
      }
    }
    return unitsToTraps;
  }

  /**
   * puts a copy (clone) of the given block in the unitChain. The block is ensured to have the same exceptions as the
   * original block. (So we will modify the exception-chain). Furthermore the inserted block will not change the behaviour of
   * the program.<br>
   * Without any further modifications the returned block is unreachable. To make it reachable one must <code>goto</code> to
   * the returned head of the new block.
   *
   * @param block
   *          the Block to clone.
   * @return the head of the copied block.
   */
  private Unit copyBlock(Block block) {
    final Set<Trap> openedTraps = new HashSet<Trap>();
    final Map<Trap, Trap> copiedTraps = new HashMap<Trap, Trap>();
    final Chain<Unit> chain = this.unitChain;

    final Unit tail = block.getTail();
    final Unit newGoto = insertGotoAfter(tail, chain.getSuccOf(tail));

    Unit last = newGoto; // the last inserted unit
    Unit copiedHead = null;
    for (Unit curr = block.getHead(); curr != newGoto; curr = chain.getSuccOf(curr)) {
      last = insertCloneAfter(last, curr);
      if (copiedHead == null) {
        copiedHead = last;
        assert (copiedHead != null);
      }
      /*
       * the traps...: if a trap is closed (in the original block) that hasn't been opened before, we have to open it at the
       * beginning of the copied block. If a trap gets opened, but not closed, we only have to close it at the end of the
       * (original) block (as it will be open at the end of the copied block.)
       */
      List<Trap> currentTraps = unitsToTraps.get(curr);
      if (currentTraps != null) {
        for (Trap trap : currentTraps) {
          if (trap.getBeginUnit() == curr) {
            Trap copiedTrap = (Trap) trap.clone();
            copiedTrap.setBeginUnit(last);
            copiedTraps.put(trap, copiedTrap);
            openedTraps.add(copiedTrap);

            trapChain.insertAfter(copiedTrap, trap);
          }
          if (trap.getEndUnit() == curr) {
            Trap copiedTrap = copiedTraps.get(trap);
            if (copiedTrap == null) {
              /* trap has been opened before the current block */
              copiedTrap = (Trap) trap.clone();
              copiedTrap.setBeginUnit(copiedHead);

              trapChain.insertAfter(copiedTrap, trap);
            } else {
              openedTraps.remove(copiedTrap);
            }

            copiedTrap.setEndUnit(last);
          }
        }
      }
    }
    /* close all open traps */
    for (Trap t : openedTraps) {
      t.setEndUnit(last);
    }
    return copiedHead;
  }

  /**
   * recursively searches for back-edges. if the found block is a condition-block makes a clone and redirects the back-edge.
   *
   * @param block
   *          the current Block.
   */
  private void unrollConditions(Block block) {
    /* if the block was already visited we can leave... */
    if (visitedBlocks.contains(block)) {
      return; // should never happen
    }
    visitedBlocks.add(block);
    visitingSuccs.add(block); // currently visiting successors
    for (Block succ : block.getSuccs()) {
      if (visitedBlocks.contains(succ)) {
        if (succ != block && visitingSuccs.contains(succ)) {
          /*
           * we only want blocks with at least 2 predecessors, to avoid that a copied while-condition gets copied again in a
           * future pass of unrollConditions
           */
          if (succ.getPreds().size() >= 2 && succ.getSuccs().size() == 2) {
            Block condition = succ; // just renaming for clearer code
            Block loopTailBlock = block; // just renaming for clearer code

            if (getSize(condition) <= maxSize) {
              Unit copiedHead = copyBlock(condition);
              /* now just redirect the tail of the loop-body */
              Unit loopTail = loopTailBlock.getTail();
              if (loopTail instanceof GotoStmt) {
                ((GotoStmt) loopTail).setTarget(copiedHead);
              } else if (loopTail instanceof IfStmt) {
                IfStmt tailIf = (IfStmt) loopTail;
                if (tailIf.getTarget() == condition.getHead()) {
                  tailIf.setTarget(copiedHead);
                } else {
                  insertGotoAfter(loopTail, copiedHead);
                }
              } else {
                insertGotoAfter(loopTail, copiedHead);
              }
            }
          }
        }
      } else {
        /* unvisited successor */
        unrollConditions(succ);
      }
    }
    visitingSuccs.remove(block);
  }
}
