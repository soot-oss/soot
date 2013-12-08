/* Soot - a J*va Optimization Framework
 * Copyright (C) 2002 Florian Loitsch
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

/*
 * Modified by the Sable Research Group and others 1997-2002.
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */

package soot.jimple.toolkits.graph;
import soot.options.*;


import soot.*;
import soot.util.*;
import java.util.*;

import soot.jimple.*;
import soot.toolkits.graph.*;

/**
 * "unrolls" the condition of while/for loops.<br>
 * before the first test of a while-loop, we can't be sure, if the body will be
 * taken or not, and several optimizations (especially LCM) can't be done. In
 * this class we try to solve this problem by unrolling the condition of the
 * while-block: we make a copy of the condition-block, and redirect the
 * back-edge of the while-loop to the new block.<br>
 * After this transformation the edge between the original condition-block and
 * the loop-body is only executed once (and hence suitable for LCM) and we can
 * be sure, that the loop-body will get executed.<br>
 * Exceptions are ignored (the transformation is done on a
 * <code>BriefBlockGraph</code>.
 */
public class LoopConditionUnroller extends BodyTransformer {
  /**
   * contained blocks are currently visiting successors. We need this to find
   * back-edges. The "visitedBlocks" is not enough, as Java Bytecodes might not
   * be in tree-form.
   */
  private Set<Block> visitingSuccs;

  private Set<Block> visitedBlocks;
  private int maxSize;
  private Body body;

  private Map<Unit, List> unitsToTraps;

  /**
   * unrolls conditions.
   */
  /* this implementation still fails in finding all possible while-loops, but
   * does a good job. */
  protected void internalTransform(Body body, String phaseName, Map options) {
    if(Options.v().verbose())
      G.v().out.println("[" + body.getMethod().getName() +
                         "]     Unrolling Loop Conditions...");

    visitingSuccs = new HashSet<Block>();
    visitedBlocks = new HashSet<Block>();
    this.body = body;
    this.maxSize = PhaseOptions.getInt(options, "maxSize");
    
    BlockGraph bg = new BriefBlockGraph(body);
    Iterator headIter = bg.getHeads().iterator();
    while (headIter.hasNext())
      unrollConditions((Block)headIter.next());

    if(Options.v().verbose())
      G.v().out.println("[" + body.getMethod().getName() +
                         "]     Unrolling Loop Conditions done.");
  }
    
  /**
   * inserts a Jimple<code>Goto</code> to <code> target, directly after
   * <code>node</code> in the <code>unitChain</code> of the body.<br>
   * As we use <code>JGoto</code> the chain must contain Jimple-stmts.
   *
   * @param node the <code>Goto</code> will be inserted just after this node.
   * @param target is the Unit the <code>goto</code> will jump to.
   * @return the newly inserted <code>Goto</code>
   */
  private Unit insertGotoAfter(Unit node, Unit target) {
    Unit newGoto = Jimple.v().newGotoStmt(target);
    body.getUnits().insertAfter(newGoto, node);
    return newGoto;
  }

  /**
   * inserts a clone of <code>toClone</code> after <code>node</code> in the
   * <code>unitChain</code>.<br>
   * Everything is done in Jimple.
   *
   * @param node the Unit after which we insert the clone.
   * @param toClone the Unit that will get cloned and then inserted.
   */
  private Unit insertCloneAfter(Chain unitChain, Unit node, Unit toClone)
  {
    Unit clone = (Unit)toClone.clone();
    body.getUnits().insertAfter(clone, node);
    return clone;
  }

  /**
   * "calculates" the length of the given block in Units.
   *
   * @param block
   * @return the size of <code>block</code>.
   */
  private int getSize(Block block) {
    int size = 0;
    Chain unitChain = body.getUnits();
    for (Unit unit = block.getHead(); unit != block.getTail();
         unit = (Unit)unitChain.getSuccOf(unit))
      size++;
    size++; //add 1 for the tail we did not take into account.
    return size;
  }

  /**
   * returns a mapping of units to trap-changes. whenever the scope of
   * a trap changes (ie. a trap opens or closes), an entry is added in
   * the map, and the unit is mapped to the trap. The values
   * associated to the keys are lists, as more than one exception can
   * change at a unit.<br> Even if a trap opens and closes at a unit,
   * this trap is only reported once (ie. is only once in the list).
   *
   * @return the map of units to changing traps.  */
  private Map<Unit, List> getTraps() {
    /* if we already did the "calculation" return the cached result.*/
    if (unitsToTraps != null)
      return unitsToTraps;
    unitsToTraps = new HashMap<Unit, List>();
    Iterator trapsIt = body.getTraps().iterator();
    while (trapsIt.hasNext()) {
      Trap trap = (Trap)trapsIt.next();
      Unit beginUnit = trap.getBeginUnit();
      List<Trap> unitTraps = unitsToTraps.get(beginUnit);
      if (unitTraps == null) {
        unitTraps = new ArrayList<Trap>();
        unitsToTraps.put(beginUnit, unitTraps);
      }
      unitTraps.add(trap);
      Unit endUnit = trap.getEndUnit();
      if (endUnit != beginUnit) {
        unitTraps = unitsToTraps.get(endUnit);
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
   * puts a copy (clone) of the given block in the unitChain. The
   * block is ensured to have the same exceptions as the original
   * block. (So we will modify the exception-chain). Furthermore the
   * inserted block will not change the behaviour of the program.<br>
   * Without any further modifications the returned block is
   * unreachable. To make it reachable one must <code>goto</code> to
   * the returned head of the new block.
   *
   * @param block the Block to clone.
   * @return the head of the copied block.  */
  private Unit copyBlock(Block block) {
    Map<Unit, List> traps = getTraps();
    Set<Trap> openedTraps = new HashSet<Trap>();
    Map<Trap, Trap> copiedTraps = new HashMap<Trap, Trap>();
    Chain unitChain = body.getUnits();

    Unit tail = block.getTail();
    Unit immediateSucc = (Unit)unitChain.getSuccOf(tail);
    Unit newGoto = insertGotoAfter(tail, immediateSucc);
    Unit last = newGoto; //the last inserted unit.
    boolean first = true;
    Unit copiedHead = null;
    for (Unit currentUnit = block.getHead(); currentUnit != newGoto;
         currentUnit = (Unit)unitChain.getSuccOf(currentUnit)) { 
      last = insertCloneAfter(unitChain, last, currentUnit);
      if (first) {
        first = false;
        copiedHead = last;
      }
      /* the traps...: if a trap is closed (in the original block) that hasn't
       * been opened before, we have to open it at the beginning of the copied
       * block. If a trap gets opened, but not closed, we only have to close it
       * at the end of the (original) block (as it will be open at the end of
       * the copied block.)*/
      List currentTraps = traps.get(currentUnit);
      if (currentTraps != null) {
        Iterator trapIt = currentTraps.iterator();
        while(trapIt.hasNext()) {
          Trap trap = (Trap)trapIt.next();
          if (trap.getBeginUnit() == currentUnit) {
            Trap copiedTrap = (Trap)trap.clone();
            copiedTrap.setBeginUnit(last);
            copiedTraps.put(trap, copiedTrap);

            openedTraps.add(copiedTrap);
            // insertAfter(toInsert, point)
	    body.getTraps().insertAfter(copiedTrap, trap);

          }
          if (trap.getEndUnit() == currentUnit) {
            Trap copiedTrap = copiedTraps.get(trap);
            if (copiedTrap == null) {
              /* trap has been opened before the current block */
              copiedTrap = (Trap)trap.clone();
              copiedTrap.setBeginUnit(copiedHead);

              body.getTraps().insertAfter(copiedTrap, trap);
            } else {
	      openedTraps.remove(copiedTrap);
	    }

            copiedTrap.setEndUnit(last);
          }
        }
      }
    }
    /* close all open traps */
    Iterator<Trap> openedIterator = openedTraps.iterator();
    while(openedIterator.hasNext()) {      
      openedIterator.next().setEndUnit(last);
    }
    return copiedHead;
  }

  /**
   * recursively searches for back-edges. if the found block is a
   * condition-block makes a clone and redirects the back-edge.
   *
   * @param block the current Block.
   */
  private void unrollConditions(Block block) {
    /* if the block was already visited we can leave... */
    if (visitedBlocks.contains(block)) return; // should never happen
    visitedBlocks.add(block);
    visitingSuccs.add(block); //currently visiting successors
    Iterator succsIt = block.getSuccs().iterator();
    while (succsIt.hasNext()) {
      Block succ = (Block)succsIt.next();

      if (visitedBlocks.contains(succ)) {
        if (succ != block && visitingSuccs.contains(succ)) {
          /* we only want blocks with at least 2 predecessors, to avoid that a
           * copied while-condition gets copied again in a future pass of
           * unrollConditions */
          if (succ.getPreds().size() >= 2 && succ.getSuccs().size() == 2) {
            Block condition = succ; //just renaming for clearer code
            Block loopTailBlock = block; //just renaming for clearer code

            if (getSize(condition) <= maxSize) {
              Unit copiedHead = copyBlock(condition);
              /* now just redirect the tail of the loop-body */
              Unit loopTail = loopTailBlock.getTail();
              if (loopTail instanceof GotoStmt)
                ((GotoStmt)loopTail).setTarget(copiedHead);
              else if (loopTail instanceof IfStmt) {
                if (((IfStmt)loopTail).getTarget() == condition.getHead())
                  ((IfStmt)loopTail).setTarget(copiedHead);
                else
                  insertGotoAfter(loopTail, copiedHead);
              } else
                insertGotoAfter(loopTail, copiedHead);
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
