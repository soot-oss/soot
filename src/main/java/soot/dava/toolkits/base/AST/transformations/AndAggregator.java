package soot.dava.toolkits.base.AST.transformations;

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

import java.util.Iterator;
import java.util.List;

import soot.G;
import soot.dava.internal.AST.ASTAndCondition;
import soot.dava.internal.AST.ASTCondition;
import soot.dava.internal.AST.ASTIfNode;
import soot.dava.internal.AST.ASTNode;
import soot.dava.internal.AST.ASTStatementSequenceNode;
import soot.dava.internal.AST.ASTTryNode;
import soot.dava.internal.SET.SETNodeLabel;
import soot.dava.internal.asg.AugmentedStmt;
import soot.dava.internal.javaRep.DAbruptStmt;
import soot.dava.toolkits.base.AST.analysis.DepthFirstAdapter;
import soot.jimple.Stmt;

/*
 Nomair A. Naeem 18-FEB-2005

 The class is responsible to do the following transformation on the AST

 if(A){                   if(A && B){
 if(B){                    Body1
 Body1   ----->    }
 }                    Body2
 }
 Body2

 The most important thing to check is that there is only one If Statement inside the
 outer if and that there is NO other statement.

 TO MAKE CODE EFFECIENT BLOCK THE ANALYSIS TO GOING INTO STATEMENTS
 this is done by overriding the caseASTStatementSequenceNode
 */

public class AndAggregator extends DepthFirstAdapter {

  public AndAggregator() {
  }

  public AndAggregator(boolean verbose) {
    super(verbose);
  }

  public void caseASTStatementSequenceNode(ASTStatementSequenceNode node) {
  }

  public void outASTIfNode(ASTIfNode node) {
    List<Object> bodies = node.get_SubBodies();
    if (bodies.size() == 1) { // this should always be one since there is
      // only one body of an if statement
      List body = (List) bodies.get(0);
      // this is the if body check to see if this is a single if Node
      if (body.size() == 1) {
        // size is good
        ASTNode bodyNode = (ASTNode) body.get(0);
        if (bodyNode instanceof ASTIfNode) {
          /*
           * We can do AndAggregation at this point node contains the outer if (ASTIfNode)bodyNode is the inner if
           */

          ASTCondition outerCond = node.get_Condition();
          ASTCondition innerCond = ((ASTIfNode) bodyNode).get_Condition();

          SETNodeLabel outerLabel = (node).get_Label();
          SETNodeLabel innerLabel = ((ASTIfNode) bodyNode).get_Label();

          SETNodeLabel newLabel = null;
          if (outerLabel.toString() == null && innerLabel.toString() == null) {
            newLabel = outerLabel;
          } else if (outerLabel.toString() != null && innerLabel.toString() == null) {
            newLabel = outerLabel;
          } else if (outerLabel.toString() == null && innerLabel.toString() != null) {
            newLabel = innerLabel;
          } else if (outerLabel.toString() != null && innerLabel.toString() != null) {
            newLabel = outerLabel;
            // however we have to change all occurance of inner
            // label to point to that
            // of outerlabel now
            changeUses(outerLabel.toString(), innerLabel.toString(), bodyNode);
          }

          // aggregate the conditions
          ASTCondition newCond = new ASTAndCondition(outerCond, innerCond);

          // Get the body of the inner Node that will be the overall
          // body
          List<Object> newBodyList = ((ASTIfNode) bodyNode).get_SubBodies();

          // retireve the actual body List
          if (newBodyList.size() == 1) {
            // should always be one since
            // this is body of IF
            List<Object> newBody = (List<Object>) newBodyList.get(0);
            node.replace(newLabel, newCond, newBody);
            // System.out.println("ANDDDDDD AGGREGATING !!!");
            G.v().ASTTransformations_modified = true;
          }
        } else {
          // not an if node
        }
      } else { // IfBody has more than 1 nodes cant do AND aggregation
      }
    }
  }

  private void changeUses(String to, String from, ASTNode node) {
    // remember this method is only called when "to" and "from" are both non
    // null
    List<Object> subBodies = node.get_SubBodies();
    Iterator<Object> it = subBodies.iterator();
    while (it.hasNext()) {
      // going over all subBodies
      if (node instanceof ASTStatementSequenceNode) {
        // check for abrupt stmts

        ASTStatementSequenceNode stmtSeq = (ASTStatementSequenceNode) node;
        for (AugmentedStmt as : stmtSeq.getStatements()) {
          Stmt s = as.get_Stmt();

          if (s instanceof DAbruptStmt) {
            DAbruptStmt abStmt = (DAbruptStmt) s;
            if (abStmt.is_Break() || abStmt.is_Continue()) {
              SETNodeLabel label = abStmt.getLabel();
              String labelBroken = label.toString();

              if (labelBroken != null) {
                // stmt breaks some label
                if (labelBroken.compareTo(from) == 0) {
                  // have to replace the "from" label to "to"
                  // label
                  label.set_Name(to);
                }
              }
            }
          }
        }
      } else {
        // need to recursively call changeUses
        List subBodyNodes = null;

        if (node instanceof ASTTryNode) {
          ASTTryNode.container subBody = (ASTTryNode.container) it.next();
          subBodyNodes = (List) subBody.o;
        } else {
          subBodyNodes = (List) it.next();
        }
        Iterator nodesIt = subBodyNodes.iterator();
        while (nodesIt.hasNext()) {
          changeUses(to, from, (ASTNode) nodesIt.next());
        }
      }

    } // going through subBodies

  }

}