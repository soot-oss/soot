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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import soot.dava.internal.AST.ASTAndCondition;
import soot.dava.internal.AST.ASTCondition;
import soot.dava.internal.AST.ASTIfElseNode;
import soot.dava.internal.AST.ASTLabeledNode;
import soot.dava.internal.AST.ASTNode;
import soot.dava.internal.AST.ASTStatementSequenceNode;
import soot.dava.internal.AST.ASTUnconditionalLoopNode;
import soot.dava.internal.AST.ASTWhileNode;
import soot.dava.internal.SET.SETNodeLabel;
import soot.dava.internal.asg.AugmentedStmt;
import soot.dava.internal.javaRep.DAbruptStmt;
import soot.jimple.ReturnStmt;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.Stmt;

/*
 Nomair A. Naeem 18-FEB-2005

 PATTERN ONE:
 label_1:                                                 
 while(cond1){                       label_1:
 if(cond2){                      while(cond1 && cond2){
 Body 1                              Body1
 }                               }
 else{                           
 break label_1
 }
 }                 

 The important thing is that Body2 should contain an abrupt
 edge out of the while loop. If Body2 is just a break and nothing
 else the body2 in the transformed version is empty 


 PATTERN TWO:
 label_1:                                                 
 while(true){                       label_1:
 if(cond2){                      while(cond2){
 Body 1                              Body1
 }                               }
 else{                           Body2
 Body 2
 }
 }                 



 TO MAKE CODE EFFECIENT BLOCK THE ANALYSIS TO GOING INTO STATEMENTS
 this is done by overriding the caseASTStatementSequenceNode
 */

public class StrengthenByIfElse {
  /*
   * We know this method is called when there is a loop node which has a body consisting entirely of one ASTIfElseNode
   */
  public static List<ASTNode> getNewNode(ASTNode loopNode, ASTIfElseNode ifElseNode) {
    // make sure that elsebody has only a stmtseq node
    List<Object> elseBody = (ifElseNode).getElseBody();
    if (elseBody.size() != 1) {
      // this is more than one we need one stmtSeq Node
      return null;
    }
    ASTNode tempNode = (ASTNode) elseBody.get(0);
    if (!(tempNode instanceof ASTStatementSequenceNode)) {
      // not a stmtSeq
      return null;
    }

    List<AugmentedStmt> statements = ((ASTStatementSequenceNode) tempNode).getStatements();
    Iterator<AugmentedStmt> stmtIt = statements.iterator();
    while (stmtIt.hasNext()) {
      AugmentedStmt as = stmtIt.next();
      Stmt stmt = as.get_Stmt();
      if (stmt instanceof DAbruptStmt) {
        // this is a abrupt stmt
        DAbruptStmt abStmt = (DAbruptStmt) stmt;
        if (!(abStmt.is_Break())) {
          // we need a break
          return null;
        } else {
          if (stmtIt.hasNext()) {
            // a break should be the last stmt
            return null;
          }
          SETNodeLabel label = abStmt.getLabel();
          String labelBroken = label.toString();
          String loopLabel = ((ASTLabeledNode) loopNode).get_Label().toString();
          if (labelBroken != null && loopLabel != null) {
            // stmt
            // breaks
            // some
            // label
            if (labelBroken.compareTo(loopLabel) == 0) {
              // we have found a break breaking this label

              // make sure that if the orignal was an ASTWhileNode
              // then there was
              // ONLY a break statement
              if (loopNode instanceof ASTWhileNode) {
                if (statements.size() != 1) {
                  // more than 1 statement
                  return null;
                }
              }

              // pattern matched
              ASTWhileNode newWhileNode = makeWhileNode(ifElseNode, loopNode);
              if (newWhileNode == null) {
                return null;
              }
              List<ASTNode> toReturn = new ArrayList<ASTNode>();
              toReturn.add(newWhileNode);

              // Add the statementSequenceNode AFTER the whileNode
              // except for the laststmt
              if (statements.size() != 1) {
                // size 1 means that the only stmt is a break
                // stmt

                Iterator<AugmentedStmt> tempIt = statements.iterator();
                List<AugmentedStmt> newStmts = new ArrayList<AugmentedStmt>();
                while (tempIt.hasNext()) {
                  AugmentedStmt tempStmt = tempIt.next();
                  if (tempIt.hasNext()) {
                    newStmts.add(tempStmt);
                  }
                }
                toReturn.add(new ASTStatementSequenceNode(newStmts));
              }
              return toReturn;

            } // labels matched
          } // non null labels
        } // end of break stmt
      } // stmt is an abrupt stmt
      else if (stmt instanceof ReturnStmt || stmt instanceof ReturnVoidStmt) {
        if (!(loopNode instanceof ASTUnconditionalLoopNode)) {
          // this pattern is only possible for while(true)
          return null;
        }

        if (stmtIt.hasNext()) {
          // returns should come in the end
          return null;
        }

        // pattern matched
        ASTWhileNode newWhileNode = makeWhileNode(ifElseNode, loopNode);
        if (newWhileNode == null) {
          return null;
        }
        List<ASTNode> toReturn = new ArrayList<ASTNode>();
        toReturn.add(newWhileNode);

        // Add the statementSequenceNode AFTER the whileNode
        List<AugmentedStmt> newStmts = new ArrayList<AugmentedStmt>(statements);
        toReturn.add(new ASTStatementSequenceNode(newStmts));
        return toReturn;
      } // if stmt was a return stmt
    } // going through the stmts
    return null;
  } // end of method

  private static ASTWhileNode makeWhileNode(ASTIfElseNode ifElseNode, ASTNode loopNode) {
    ASTCondition outerCond = null;
    ASTCondition innerCond = ifElseNode.get_Condition();
    ASTCondition newCond = null;

    if (loopNode instanceof ASTWhileNode) {
      outerCond = ((ASTWhileNode) loopNode).get_Condition();
      newCond = new ASTAndCondition(outerCond, innerCond);
    } else if (loopNode instanceof ASTUnconditionalLoopNode) {
      newCond = innerCond;
    } else {
      // not dealing with the case of ASTDoWhileNode
      return null;
    }
    List<Object> loopBody = ifElseNode.getIfBody();
    SETNodeLabel newLabel = ((ASTLabeledNode) loopNode).get_Label();

    // make new ASTWhileNode
    return new ASTWhileNode(newLabel, newCond, loopBody);
  }

} // end class