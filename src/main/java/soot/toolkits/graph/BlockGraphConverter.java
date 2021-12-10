package soot.toolkits.graph;

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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import soot.Body;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;

/**
 * This utility class can convert any BlockGraph to a single-headed and single-tailed graph by inserting appropriate Start or
 * Stop nodes. It can also fully reverse the graph, something that might be useful e.g. when computing control dependences
 * with a dominators algorithm.
 *
 * <p>
 * Note: This class may be retracted in a future release when a suitable replacement becomes available.
 * </p>
 *
 * @author Navindra Umanee
 **/
public class BlockGraphConverter {
  /**
   * Transforms a multi-headed and/or multi-tailed BlockGraph to a single-headed singled-tailed BlockGraph by inserting a
   * dummy start and stop nodes.
   **/
  public static void addStartStopNodesTo(BlockGraph graph) {
    ADDSTART: {
      List<Block> heads = graph.getHeads();
      int headCount = heads.size();
      if (headCount == 0) {
        break ADDSTART;
      }
      if ((headCount == 1) && (heads.get(0) instanceof DummyBlock)) {
        break ADDSTART;
      }

      List<Block> blocks = graph.getBlocks();
      DummyBlock head = new DummyBlock(graph.getBody(), 0);
      head.makeHeadBlock(heads);

      graph.mHeads = Collections.<Block>singletonList(head);

      for (Block block : blocks) {
        block.setIndexInMethod(block.getIndexInMethod() + 1);
      }

      List<Block> newBlocks = new ArrayList<Block>();
      newBlocks.add(head);
      newBlocks.addAll(blocks);
      graph.mBlocks = newBlocks;
    }

    ADDSTOP: {
      List<Block> tails = graph.getTails();
      int tailCount = tails.size();
      if (tailCount == 0) {
        break ADDSTOP;
      }
      if ((tailCount == 1) && (tails.get(0) instanceof DummyBlock)) {
        break ADDSTOP;
      }

      List<Block> blocks = graph.getBlocks();
      DummyBlock tail = new DummyBlock(graph.getBody(), blocks.size());
      tail.makeTailBlock(tails);

      graph.mTails = Collections.<Block>singletonList(tail);

      blocks.add(tail);
    }
  }

  /**
   * Reverses a BlockGraph by making the heads tails, the tails heads and reversing the edges. It does not change the
   * ordering of Units in individual blocks, nor does it change the Block labels. This utility could be useful when
   * calculating control dependences with a dominators algorithm.
   **/
  public static void reverse(BlockGraph graph) {
    // Issue: Do we change indexInMethod? No...
    // Issue: Do we reverse the Units list in the Block?
    // Issue: Do we need to implement an equals method in Block?
    // When are two Blocks from two different BlockGraphs
    // equal?
    for (Block block : graph.getBlocks()) {
      List<Block> succs = block.getSuccs();
      List<Block> preds = block.getPreds();
      block.setSuccs(preds);
      block.setPreds(succs);
    }

    List<Block> heads = graph.getHeads();
    List<Block> tails = graph.getTails();

    graph.mHeads = new ArrayList<Block>(tails);
    graph.mTails = new ArrayList<Block>(heads);
  }

  public static void main(String[] args) {
    // assumes 2 args: Class + Method

    Scene.v().loadClassAndSupport(args[0]);
    SootClass sc = Scene.v().getSootClass(args[0]);
    SootMethod sm = sc.getMethod(args[1]);
    Body b = sm.retrieveActiveBody();
    CompleteBlockGraph cfg = new CompleteBlockGraph(b);
    System.out.println(cfg);
    BlockGraphConverter.addStartStopNodesTo(cfg);
    System.out.println(cfg);
    BlockGraphConverter.reverse(cfg);
    System.out.println(cfg);
  }
}

/**
 * Represents Start or Stop node in the graph.
 *
 * @author Navindra Umanee
 **/
class DummyBlock extends Block {
  DummyBlock(Body body, int indexInMethod) {
    super(null, null, body, indexInMethod, 0, null);
  }

  void makeHeadBlock(List<Block> oldHeads) {
    setPreds(new ArrayList<Block>());
    setSuccs(new ArrayList<Block>(oldHeads));

    for (Block oldHead : oldHeads) {
      List<Block> newPreds = new ArrayList<Block>();
      newPreds.add(this);

      List<Block> oldPreds = oldHead.getPreds();
      if (oldPreds != null) {
        newPreds.addAll(oldPreds);
      }

      oldHead.setPreds(newPreds);
    }
  }

  void makeTailBlock(List<Block> oldTails) {
    setSuccs(new ArrayList<Block>());
    setPreds(new ArrayList<Block>(oldTails));

    Iterator<Block> tailsIt = oldTails.iterator();
    while (tailsIt.hasNext()) {
      Block oldTail = tailsIt.next();

      List<Block> newSuccs = new ArrayList<Block>();
      newSuccs.add(this);

      List<Block> oldSuccs = oldTail.getSuccs();
      if (oldSuccs != null) {
        newSuccs.addAll(oldSuccs);
      }

      oldTail.setSuccs(newSuccs);
    }
  }

  @Override
  public Iterator<Unit> iterator() {
    return Collections.emptyIterator();
  }
}
