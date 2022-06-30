package soot.dava.toolkits.base.finders;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Jerome Miecznikowski
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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import soot.G;
import soot.Singletons;
import soot.dava.Dava;
import soot.dava.DavaBody;
import soot.dava.RetriggerAnalysisException;
import soot.dava.internal.SET.SETBasicBlock;
import soot.dava.internal.SET.SETLabeledBlockNode;
import soot.dava.internal.SET.SETNode;
import soot.dava.internal.SET.SETStatementSequenceNode;
import soot.dava.internal.SET.SETTryNode;
import soot.dava.internal.SET.SETUnconditionalWhileNode;
import soot.dava.internal.asg.AugmentedStmt;
import soot.dava.internal.asg.AugmentedStmtGraph;
import soot.util.IterableSet;

public class LabeledBlockFinder implements FactFinder {
  public LabeledBlockFinder(Singletons.Global g) {
  }

  public static LabeledBlockFinder v() {
    return G.v().soot_dava_toolkits_base_finders_LabeledBlockFinder();
  }

  private final HashMap<SETNode, Integer> orderNumber = new HashMap();

  public void find(DavaBody body, AugmentedStmtGraph asg, SETNode SET) throws RetriggerAnalysisException {
    Dava.v().log("LabeledBlockFinder::find()");

    Iterator bit = SET.get_Body().iterator();
    while (bit.hasNext()) {
      SET.find_SmallestSETNode((AugmentedStmt) bit.next());
    }

    SET.find_LabeledBlocks(this);
  }

  public void perform_ChildOrder(SETNode SETParent) {
    Dava.v().log("LabeledBlockFinder::perform_ChildOrder()");

    if (SETParent instanceof SETStatementSequenceNode) {
      return;
    }

    Iterator<IterableSet> sbit = SETParent.get_SubBodies().iterator();
    while (sbit.hasNext()) {

      IterableSet body = sbit.next();
      IterableSet children = SETParent.get_Body2ChildChain().get(body);

      HashSet<SETBasicBlock> touchSet = new HashSet<SETBasicBlock>();
      IterableSet childOrdering = new IterableSet();
      LinkedList worklist = new LinkedList();
      List<SETBasicBlock> SETBasicBlocks = null;

      if (SETParent instanceof SETUnconditionalWhileNode) {
        SETNode startSETNode = ((SETUnconditionalWhileNode) SETParent).get_CharacterizingStmt().myNode;

        while (children.contains(startSETNode) == false) {
          startSETNode = startSETNode.get_Parent();
        }

        SETBasicBlocks = build_Connectivity(SETParent, body, startSETNode);
        worklist.add(SETBasicBlock.get_SETBasicBlock(startSETNode));
      }

      else if (SETParent instanceof SETTryNode) {
        SETNode startSETNode = null;

        Iterator bit = body.iterator();
        find_entry_loop: while (bit.hasNext()) {
          AugmentedStmt as = (AugmentedStmt) bit.next();

          Iterator pbit = as.cpreds.iterator();
          while (pbit.hasNext()) {
            if (body.contains(pbit.next()) == false) {
              startSETNode = as.myNode;
              break find_entry_loop;
            }
          }
        }
        if (startSETNode == null) {
          startSETNode = ((SETTryNode) SETParent).get_EntryStmt().myNode;
        }

        while (children.contains(startSETNode) == false) {
          startSETNode = startSETNode.get_Parent();
        }

        SETBasicBlocks = build_Connectivity(SETParent, body, startSETNode);
        worklist.add(SETBasicBlock.get_SETBasicBlock(startSETNode));
      }

      else {
        SETBasicBlocks = build_Connectivity(SETParent, body, null);

        Iterator cit = children.iterator();
        while (cit.hasNext()) {
          SETNode child = (SETNode) cit.next();

          if (child.get_Predecessors().isEmpty()) {
            worklist.add(SETBasicBlock.get_SETBasicBlock(child));
          }
        }
      }

      while (worklist.isEmpty() == false) {
        SETBasicBlock sbb = (SETBasicBlock) worklist.removeFirst();

        // extract and append the basic block to child ordering
        Iterator bit = sbb.get_Body().iterator();
        while (bit.hasNext()) {
          childOrdering.addLast(bit.next());
        }

        touchSet.add(sbb);

        /*
         * ************************************************ * Basic orderer.
         */

        TreeSet sortedSuccessors = new TreeSet();

        Iterator sit = sbb.get_Successors().iterator();
        SETBasicBlock_successor_loop: while (sit.hasNext()) {
          SETBasicBlock ssbb = (SETBasicBlock) sit.next();

          if (touchSet.contains(ssbb)) {
            continue;
          }

          Iterator psit = ssbb.get_Predecessors().iterator();
          while (psit.hasNext()) {
            if (touchSet.contains(psit.next()) == false) {
              continue SETBasicBlock_successor_loop;
            }
          }

          sortedSuccessors.add(ssbb);
        }

        sit = sortedSuccessors.iterator();
        while (sit.hasNext()) {
          worklist.addFirst(sit.next());
        }

        /*
         * End of Basic orderer. ************************************************
         */

      }

      int count = 0;

      Iterator it = childOrdering.iterator();
      while (it.hasNext()) {
        orderNumber.put((SETNode) it.next(), new Integer(count++));
      }

      children.clear();
      children.addAll(childOrdering);
    }
  }

  private List<SETBasicBlock> build_Connectivity(SETNode SETParent, IterableSet body, SETNode startSETNode) {
    Dava.v().log("LabeledBlockFinder::build_Connectivity()");

    IterableSet children = SETParent.get_Body2ChildChain().get(body);

    /*
     * First task: establish the connectivity between the children of the current node.
     */

    // look through all the statements in the current SETNode
    Iterator it = body.iterator();
    while (it.hasNext()) {
      AugmentedStmt as = (AugmentedStmt) it.next();

      // for each statement, examine each of it's successors
      Iterator sit = as.csuccs.iterator();
      while (sit.hasNext()) {
        AugmentedStmt sas = (AugmentedStmt) sit.next();

        if (body.contains(sas)) {

          // get the child nodes that contain the source and destination statements
          SETNode srcNode = as.myNode;
          SETNode dstNode = sas.myNode;

          while (children.contains(srcNode) == false) {
            srcNode = srcNode.get_Parent();
          }

          while (children.contains(dstNode) == false) {
            dstNode = dstNode.get_Parent();
          }

          if (srcNode == dstNode) {
            continue;
          }

          // hook up the src and dst nodes
          if (srcNode.get_Successors().contains(dstNode) == false) {
            srcNode.get_Successors().add(dstNode);
          }

          if (dstNode.get_Predecessors().contains(srcNode) == false) {
            dstNode.get_Predecessors().add(srcNode);
          }
        }
      }
    }

    Dava.v().log("LabeledBlockFinder::build_Connectivity() - built connectivity");

    /*
     * Second task: build the basic block graph between the node.
     */

    // first create the basic blocks
    LinkedList<SETBasicBlock> basicBlockList = new LinkedList<SETBasicBlock>();

    Iterator cit = children.iterator();
    while (cit.hasNext()) {
      SETNode child = (SETNode) cit.next();

      if (SETBasicBlock.get_SETBasicBlock(child) != null) {
        continue;
      }

      // build a basic block for every node with != 1 predecessor
      SETBasicBlock basicBlock = new SETBasicBlock();
      while (child.get_Predecessors().size() == 1) {

        if ((startSETNode != null) && (child == startSETNode)) {
          break;
        }

        SETNode prev = (SETNode) child.get_Predecessors().getFirst();
        if ((SETBasicBlock.get_SETBasicBlock(prev) != null) || (prev.get_Successors().size() != 1)) {
          break;
        }

        child = prev;
      }

      basicBlock.add(child);

      while (child.get_Successors().size() == 1) {
        child = (SETNode) child.get_Successors().getFirst();

        if ((SETBasicBlock.get_SETBasicBlock(child) != null) || (child.get_Predecessors().size() != 1)) {
          break;
        }

        basicBlock.add(child);
      }

      basicBlockList.add(basicBlock);
    }

    Dava.v().log("LabeledBlockFinder::build_Connectivity() - created basic blocks");

    // next build the connectivity between the nodes of the basic block graph
    Iterator<SETBasicBlock> bblit = basicBlockList.iterator();
    while (bblit.hasNext()) {
      SETBasicBlock sbb = bblit.next();
      SETNode entryNode = sbb.get_EntryNode();

      Iterator pit = entryNode.get_Predecessors().iterator();
      while (pit.hasNext()) {
        SETNode psn = (SETNode) pit.next();

        SETBasicBlock psbb = SETBasicBlock.get_SETBasicBlock(psn);

        if (sbb.get_Predecessors().contains(psbb) == false) {
          sbb.get_Predecessors().add(psbb);
        }

        if (psbb.get_Successors().contains(sbb) == false) {
          psbb.get_Successors().add(sbb);
        }
      }
    }

    Dava.v().log("LabeledBlockFinder::build_Connectivity() - done");

    return basicBlockList;
  }

  public void find_LabeledBlocks(SETNode SETParent) {
    Dava.v().log("LabeledBlockFinder::find_LabeledBlocks()");

    Iterator<IterableSet> sbit = SETParent.get_SubBodies().iterator();
    while (sbit.hasNext()) {
      IterableSet curBody = sbit.next();
      IterableSet children = SETParent.get_Body2ChildChain().get(curBody);

      Iterator it = children.snapshotIterator();
      if (it.hasNext()) {
        SETNode curNode = (SETNode) it.next(), prevNode = null;

        // Look through all the children of the current SET node.
        while (it.hasNext()) {
          prevNode = curNode;
          curNode = (SETNode) it.next();
          AugmentedStmt entryStmt = curNode.get_EntryStmt();

          SETNode minNode = null;
          boolean build = false;

          // For each SET node, check the edges that come into it.
          Iterator pit = entryStmt.cpreds.iterator();
          while (pit.hasNext()) {
            AugmentedStmt pas = (AugmentedStmt) pit.next();

            if (curBody.contains(pas) == false) {
              continue;
            }

            SETNode srcNode = pas.myNode;

            while (children.contains(srcNode) == false) {
              srcNode = srcNode.get_Parent();
            }

            if (srcNode == curNode) {
              continue;
            }

            if (srcNode != prevNode) {
              build = true;

              if ((minNode == null) || (orderNumber.get(srcNode).intValue() < orderNumber.get(minNode).intValue())) {
                minNode = srcNode;
              }
            }
          }

          if (build) {
            IterableSet labeledBlockBody = new IterableSet();

            Iterator cit = children.iterator(minNode);
            while (cit.hasNext()) {
              SETNode child = (SETNode) cit.next();
              if (child == curNode) {
                break;
              }

              labeledBlockBody.addAll(child.get_Body());
            }

            SETLabeledBlockNode slbn = new SETLabeledBlockNode(labeledBlockBody);
            orderNumber.put(slbn, orderNumber.get(minNode));

            cit = children.snapshotIterator(minNode);
            while (cit.hasNext()) {
              SETNode child = (SETNode) cit.next();
              if (child == curNode) {
                break;
              }

              SETParent.remove_Child(child, children);
              slbn.add_Child(child, slbn.get_Body2ChildChain().get(slbn.get_SubBodies().get(0)));
            }

            SETParent.insert_ChildBefore(slbn, curNode, children);
          }
        }
      }
    }
  }
}
