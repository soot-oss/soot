package soot.toolkits.graph;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1999 Patrice Pominville, Raja Vallee-Rai
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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.Body;
import soot.Trap;
import soot.Unit;
import soot.jimple.NopStmt;
import soot.util.Chain;

/**
 * <p>
 * Represents the control flow graph of a {@link Body} at the basic block level. Each node of the graph is a {@link Block}
 * while the edges represent the flow of control from one basic block to the next.
 * </p>
 *
 * <p>
 * This is an abstract base class for different variants of {@link BlockGraph}, where the variants differ in how they analyze
 * the control flow between individual units (represented by passing different variants of {@link UnitGraph} to the
 * <code>BlockGraph</code> constructor) and in how they identify block leaders (represented by overriding
 * <code>BlockGraph</code>'s definition of {@link computeLeaders()}.
 */
public abstract class BlockGraph implements DirectedGraph<Block> {
  protected Body mBody;
  protected Chain<Unit> mUnits;
  protected List<Block> mBlocks;
  protected List<Block> mHeads = new ArrayList<Block>();
  protected List<Block> mTails = new ArrayList<Block>();

  /**
   * Create a <code>BlockGraph</code> representing at the basic block level the control flow specified, at the
   * <code>Unit</code> level, by a given {@link UnitGraph}.
   *
   * @param unitGraph
   *          A representation of the control flow at the level of individual {@link Unit}s.
   */
  protected BlockGraph(UnitGraph unitGraph) {
    mBody = unitGraph.getBody();
    mUnits = mBody.getUnits();
    Set<Unit> leaders = computeLeaders(unitGraph);
    buildBlocks(leaders, unitGraph);
  }

  /**
   * <p>
   * Utility method for computing the basic block leaders for a {@link Body}, given its {@link UnitGraph} (i.e., the
   * instructions which begin new basic blocks).
   * </p>
   *
   * <p>
   * This implementation designates as basic block leaders :
   *
   * <ul>
   *
   * <li>Any <code>Unit</code> which has zero predecessors (e.g. the <code>Unit</code> following a return or unconditional
   * branch) or more than one predecessor (e.g. a merge point).</li>
   *
   * <li><code>Unit</code>s which are the target of any branch (even if they have no other predecessors and the branch has no
   * other successors, which is possible for the targets of unconditional branches or degenerate conditional branches which
   * both branch and fall through to the same <code>Unit</code>).</li>
   *
   * <li>All successors of any <code>Unit</code> which has more than one successor (this includes the successors of
   * <code>Unit</code>s which may throw an exception that gets caught within the <code>Body</code>, as well the successors of
   * conditional branches).</li>
   *
   * <li>The first <code>Unit</code> in any <code>Trap</code> handler. (Strictly speaking, if <code>unitGraph</code> were a
   * <code>ExceptionalUnitGraph</code> that included only a single unexceptional predecessor for some handler&mdash;because
   * no trapped unit could possibly throw the exception that the handler catches, while the code preceding the handler fell
   * through to the handler's code&mdash;then you could merge the handler into the predecessor's basic block; but such
   * situations occur only in carefully contrived bytecode.)
   *
   * </ul>
   * </p>
   *
   * @param unitGraph
   *          is the <code>Unit</code>-level CFG which is to be split into basic blocks.
   *
   * @return the {@link Set} of {@link Unit}s in <code>unitGraph</code> which are block leaders.
   */
  protected Set<Unit> computeLeaders(UnitGraph unitGraph) {
    Body body = unitGraph.getBody();
    if (body != mBody) {
      throw new RuntimeException("BlockGraph.computeLeaders() called with a UnitGraph that doesn't match its mBody.");
    }
    Set<Unit> leaders = new HashSet<Unit>();

    // Trap handlers start new basic blocks, no matter how many
    // predecessors they have.
    Chain<Trap> traps = body.getTraps();
    for (Iterator<Trap> trapIt = traps.iterator(); trapIt.hasNext();) {
      Trap trap = trapIt.next();
      leaders.add(trap.getHandlerUnit());
    }

    for (Iterator<Unit> unitIt = body.getUnits().iterator(); unitIt.hasNext();) {
      Unit u = unitIt.next();
      List<Unit> predecessors = unitGraph.getPredsOf(u);
      int predCount = predecessors.size();
      List<Unit> successors = unitGraph.getSuccsOf(u);
      int succCount = successors.size();

      if (predCount != 1) { // If predCount == 1 but the predecessor
        leaders.add(u); // is a branch, u will get added by that
      } // branch's successor test.
      if ((succCount > 1) || (u.branches())) {
        for (Iterator<Unit> it = successors.iterator(); it.hasNext();) {
          leaders.add((Unit) it.next()); // The cast is an
        } // assertion check.
      }
    }
    return leaders;
  }

  /**
   * <p>
   * A utility method that does most of the work of constructing basic blocks, once the set of block leaders has been
   * determined, and which designates the heads and tails of the graph.
   * </p>
   *
   * <p>
   * <code>BlockGraph</code> provides an implementation of <code>buildBlocks()</code> which splits the {@link Unit}s in
   * <code>unitGraph</code> so that each <code>Unit</code> in the passed set of block leaders is the first unit in a block.
   * It defines as heads the blocks which begin with <code>Unit</code>s which are heads in <code>unitGraph</code>, and
   * defines as tails the blocks which end with <code>Unit</code>s which are tails in <code>unitGraph</code>. Subclasses
   * might override this behavior.
   *
   * @param leaders
   *          Contains <code>Unit</code>s which are to be block leaders.
   *
   * @param unitGraph
   *          Provides information about the predecessors and successors of each <code>Unit</code> in the <code>Body</code>,
   *          for determining the predecessors and successors of each created {@link Block}.
   *
   * @return a {@link Map} from {@link Unit}s which begin or end a block to the block which contains them.
   */
  protected Map<Unit, Block> buildBlocks(Set<Unit> leaders, UnitGraph unitGraph) {
    List<Block> blockList = new ArrayList<Block>(leaders.size());
    Map<Unit, Block> unitToBlock = new HashMap<Unit, Block>(); // Maps head
    // and tail
    // units to
    // their blocks, for building
    // predecessor and successor lists.
    Unit blockHead = null;
    int blockLength = 0;
    Iterator<Unit> unitIt = mUnits.iterator();
    if (unitIt.hasNext()) {
      blockHead = unitIt.next();
      if (!leaders.contains(blockHead)) {
        throw new RuntimeException("BlockGraph: first unit not a leader!");
      }
      blockLength++;
    }
    Unit blockTail = blockHead;
    int indexInMethod = 0;

    while (unitIt.hasNext()) {
      Unit u = unitIt.next();
      if (leaders.contains(u)) {
        addBlock(blockHead, blockTail, indexInMethod, blockLength, blockList, unitToBlock);
        indexInMethod++;
        blockHead = u;
        blockLength = 0;
      }
      blockTail = u;
      blockLength++;
    }
    if (blockLength > 0) {
      // Add final block.
      addBlock(blockHead, blockTail, indexInMethod, blockLength, blockList, unitToBlock);
    }

    // The underlying UnitGraph defines heads and tails.
    for (Iterator<Unit> it = unitGraph.getHeads().iterator(); it.hasNext();) {
      Unit headUnit = (Unit) it.next();
      Block headBlock = unitToBlock.get(headUnit);
      if (headBlock.getHead() == headUnit) {
        mHeads.add(headBlock);
      } else {
        throw new RuntimeException("BlockGraph(): head Unit is not the first unit in the corresponding Block!");
      }
    }
    for (Iterator<Unit> it = unitGraph.getTails().iterator(); it.hasNext();) {
      Unit tailUnit = (Unit) it.next();
      Block tailBlock = unitToBlock.get(tailUnit);
      if (tailBlock.getTail() == tailUnit) {
        mTails.add(tailBlock);
      } else {
        throw new RuntimeException("BlockGraph(): tail Unit is not the last unit in the corresponding Block!");
      }
    }

    for (Iterator<Block> blockIt = blockList.iterator(); blockIt.hasNext();) {
      Block block = blockIt.next();

      List<Unit> predUnits = unitGraph.getPredsOf(block.getHead());
      List<Block> predBlocks = new ArrayList<Block>(predUnits.size());
      for (Iterator<Unit> predIt = predUnits.iterator(); predIt.hasNext();) {
        Unit predUnit = predIt.next();
        Block predBlock = unitToBlock.get(predUnit);
        if (predBlock == null) {
          throw new RuntimeException("BlockGraph(): block head mapped to null block!");
        }
        predBlocks.add(predBlock);
      }

      if (predBlocks.size() == 0) {
        block.setPreds(Collections.<Block>emptyList());

        // If the UnreachableCodeEliminator is not eliminating
        // unreachable handlers, then they will have no
        // predecessors, yet not be heads.
        /*
         * if (! mHeads.contains(block)) { throw new RuntimeException("Block with no predecessors is not a head!" );
         *
         * // Note that a block can be a head even if it has // predecessors: a handler that might catch an exception //
         * thrown by the first Unit in the method. }
         */
      } else {
        block.setPreds(Collections.unmodifiableList(predBlocks));
        if (block.getHead() == mUnits.getFirst()) {
          mHeads.add(block); // Make the first block a head
          // even if the Body is one huge loop.
        }
      }

      List<Unit> succUnits = unitGraph.getSuccsOf(block.getTail());
      List<Block> succBlocks = new ArrayList<Block>(succUnits.size());
      for (Iterator<Unit> succIt = succUnits.iterator(); succIt.hasNext();) {
        Unit succUnit = succIt.next();
        Block succBlock = unitToBlock.get(succUnit);
        if (succBlock == null) {
          throw new RuntimeException("BlockGraph(): block tail mapped to null block!");
        }
        succBlocks.add(succBlock);
      }

      if (succBlocks.size() == 0) {
        block.setSuccs(Collections.<Block>emptyList());
        if (!mTails.contains(block)) {
          // if this block is totally empty and unreachable, we remove it
          if (block.getPreds().isEmpty() && block.getHead() == block.getTail() && block.getHead() instanceof NopStmt) {
            blockIt.remove();
          } else {
            throw new RuntimeException("Block with no successors is not a tail!: " + block.toString());
            // Note that a block can be a tail even if it has
            // successors: a return that throws a caught exception.
          }
        }
      } else {
        block.setSuccs(Collections.unmodifiableList(succBlocks));
      }
    }
    mBlocks = Collections.unmodifiableList(blockList);
    mHeads = Collections.unmodifiableList(mHeads);
    if (mTails.size() == 0) {
      mTails = Collections.emptyList();
    } else {
      mTails = Collections.unmodifiableList(mTails);
    }
    return unitToBlock;
  }

  /**
   * A utility method which creates a new block and adds information about it to data structures used to build the graph.
   *
   * @param head
   *          The first unit in the block.
   * @param tail
   *          The last unit in the block.
   * @param index
   *          The index of this block this {@link Body}.
   * @param length
   *          The number of units in this block.
   * @param blockList
   *          The list of blocks for this method. <code>addBlock()</code> will add the newly created block to this list.
   * @param unitToBlock
   *          A map from units to blocks. <code>addBlock()</code> will add mappings from <code>head</code> and
   *          <code>tail</code> to the new block
   */
  private void addBlock(Unit head, Unit tail, int index, int length, List<Block> blockList, Map<Unit, Block> unitToBlock) {
    Block block = new Block(head, tail, mBody, index, length, this);
    blockList.add(block);
    unitToBlock.put(tail, block);
    unitToBlock.put(head, block);
  }

  /**
   * Returns the {@link Body} this {@link BlockGraph} is derived from.
   *
   * @return The {@link Body} this {@link BlockGraph} is derived from.
   */
  public Body getBody() {
    return mBody;
  }

  /**
   * Returns a list of the Blocks composing this graph.
   *
   * @return A list of the blocks composing this graph in the same order as they partition underlying Body instance's unit
   *         chain.
   * @see Block
   */
  public List<Block> getBlocks() {
    return mBlocks;
  }

  public String toString() {

    Iterator<Block> it = mBlocks.iterator();
    StringBuffer buf = new StringBuffer();
    while (it.hasNext()) {
      Block someBlock = it.next();

      buf.append(someBlock.toString() + '\n');
    }

    return buf.toString();
  }

  /* DirectedGraph implementation */
  public List<Block> getHeads() {
    return mHeads;
  }

  public List<Block> getTails() {
    return mTails;
  }

  public List<Block> getPredsOf(Block b) {
    return b.getPreds();
  }

  public List<Block> getSuccsOf(Block b) {
    return b.getSuccs();
  }

  public int size() {
    return mBlocks.size();
  }

  public Iterator<Block> iterator() {
    return mBlocks.iterator();
  }
}
