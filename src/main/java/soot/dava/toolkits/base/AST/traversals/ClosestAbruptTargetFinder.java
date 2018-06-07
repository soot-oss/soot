package soot.dava.toolkits.base.AST.traversals;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2005 Nomair A. Naeem
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

import soot.G;
import soot.Singletons;
import soot.dava.internal.AST.ASTDoWhileNode;
import soot.dava.internal.AST.ASTForLoopNode;
import soot.dava.internal.AST.ASTLabeledNode;
import soot.dava.internal.AST.ASTNode;
import soot.dava.internal.AST.ASTSwitchNode;
import soot.dava.internal.AST.ASTUnconditionalLoopNode;
import soot.dava.internal.AST.ASTWhileNode;
import soot.dava.internal.SET.SETNodeLabel;
import soot.dava.internal.javaRep.DAbruptStmt;
import soot.dava.toolkits.base.AST.analysis.DepthFirstAdapter;
import soot.jimple.Stmt;

/**
 * This class has been created because we need the immediate target of a implicit break/continue statement i.e. a
 * break/continue statement which does not break/continue a particular label explicitly.
 *
 * Notice that this is only allowed for while do while, unconditional loop for loop switch construct.
 *
 * Notice continue is not allowed for switch also
 *
 * Explicit breaks can on the other hand break any label (that on a construct) which we are not worried about in this
 * analysis
 */
public class ClosestAbruptTargetFinder extends DepthFirstAdapter {

  public ClosestAbruptTargetFinder(Singletons.Global g) {
  }

  public static ClosestAbruptTargetFinder v() {
    return G.v().soot_dava_toolkits_base_AST_traversals_ClosestAbruptTargetFinder();
  }

  HashMap<DAbruptStmt, ASTNode> closestNode = new HashMap<DAbruptStmt, ASTNode>();// a mapping of each abrupt statement to
                                                                                  // the node they are targeting
  ArrayList<ASTLabeledNode> nodeStack = new ArrayList<ASTLabeledNode>(); // the last element will always be the "currentNode"
                                                                         // meaning the closest
                                                                         // target to a abrupt stmt

  /**
   * To be invoked by other analyses. Given an abrupt stmt as input this method will locate the closest target and return it
   */
  public ASTNode getTarget(DAbruptStmt ab) {
    Object node = closestNode.get(ab);
    if (node != null) {
      return (ASTNode) node;
    } else {
      throw new RuntimeException("Unable to find target for AbruptStmt");
    }
  }

  /**
   * Following methods add a new node to the end of the nodeStack arrayList Since that node becomes the closest target of an
   * implicit break or continue
   */

  public void inASTWhileNode(ASTWhileNode node) {
    nodeStack.add(node);
  }

  public void inASTDoWhileNode(ASTDoWhileNode node) {
    nodeStack.add(node);
  }

  public void inASTUnconditionalLoopNode(ASTUnconditionalLoopNode node) {
    nodeStack.add(node);
  }

  public void inASTForLoopNode(ASTForLoopNode node) {
    nodeStack.add(node);
  }

  public void inASTSwitchNode(ASTSwitchNode node) {
    nodeStack.add(node);
  }

  /**
   * Following methods remove the last node from the end of the nodeStack arrayList Since the previous node now becomes the
   * closest target to an implicit break or continue
   */

  public void outASTWhileNode(ASTWhileNode node) {
    if (nodeStack.isEmpty()) {
      throw new RuntimeException("trying to remove node from empty stack: ClosestBreakTargetFinder");
    }
    nodeStack.remove(nodeStack.size() - 1);
  }

  public void outASTDoWhileNode(ASTDoWhileNode node) {
    if (nodeStack.isEmpty()) {
      throw new RuntimeException("trying to remove node from empty stack: ClosestBreakTargetFinder");
    }
    nodeStack.remove(nodeStack.size() - 1);
  }

  public void outASTUnconditionalLoopNode(ASTUnconditionalLoopNode node) {
    if (nodeStack.isEmpty()) {
      throw new RuntimeException("trying to remove node from empty stack: ClosestBreakTargetFinder");
    }
    nodeStack.remove(nodeStack.size() - 1);
  }

  public void outASTForLoopNode(ASTForLoopNode node) {
    if (nodeStack.isEmpty()) {
      throw new RuntimeException("trying to remove node from empty stack: ClosestBreakTargetFinder");
    }
    nodeStack.remove(nodeStack.size() - 1);
  }

  public void outASTSwitchNode(ASTSwitchNode node) {
    if (nodeStack.isEmpty()) {
      throw new RuntimeException("trying to remove node from empty stack: ClosestBreakTargetFinder");
    }
    nodeStack.remove(nodeStack.size() - 1);
  }

  public void inStmt(Stmt s) {
    if (s instanceof DAbruptStmt) {
      // breaks and continues are abrupt statements
      DAbruptStmt ab = (DAbruptStmt) s;

      SETNodeLabel label = ab.getLabel();
      if (label != null) {
        if (label.toString() != null) {
          // not considering explicit breaks
          return;
        }
      }

      // the break is an implict break
      if (ab.is_Break()) {
        // get the top of the stack
        int index = nodeStack.size() - 1;
        if (index < 0) {
          // error
          throw new RuntimeException("nodeStack empty??" + nodeStack.toString());
        }
        ASTNode currentNode = nodeStack.get(nodeStack.size() - 1);
        closestNode.put(ab, currentNode);
      } else if (ab.is_Continue()) {
        // need something different because continues dont target switch
        int index = nodeStack.size() - 1;
        if (index < 0) {
          // error
          throw new RuntimeException("nodeStack empty??" + nodeStack.toString());
        }

        ASTNode currentNode = nodeStack.get(index);
        while (currentNode instanceof ASTSwitchNode) {
          if (index > 0) {
            // more elements present in nodeStack
            index--;
            currentNode = nodeStack.get(index);
          } else {
            // error
            throw new RuntimeException("Unable to find closest break Target");
          }
        }
        // know that the currentNode is not an ASTSwitchNode
        closestNode.put(ab, currentNode);
      }
    }
  }

  /*
   * public void outASTMethodNode(ASTMethodNode node){ Iterator it = closestNode.keySet().iterator(); while(it.hasNext()){
   * DAbruptStmt ab = (DAbruptStmt)it.next();
   * System.out.println("Closest to "+ab+" is "+((ASTNode)closestNode.get(ab)).toString()+"\n\n"); }
   *
   * }
   */
}
