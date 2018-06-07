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
import java.util.List;

import soot.G;
import soot.dava.internal.AST.ASTCondition;
import soot.dava.internal.AST.ASTIfElseNode;
import soot.dava.internal.AST.ASTIfNode;
import soot.dava.internal.AST.ASTNode;
import soot.dava.internal.AST.ASTOrCondition;
import soot.dava.internal.AST.ASTStatementSequenceNode;
import soot.dava.internal.SET.SETNodeLabel;
import soot.dava.internal.asg.AugmentedStmt;
import soot.dava.internal.javaRep.DAbruptStmt;
import soot.dava.toolkits.base.AST.analysis.DepthFirstAdapter;
import soot.jimple.Stmt;

/*
  Nomair A. Naeem 21-FEB-2005
  The class is responsible to one of the following two transformation on the AST

  PRIORITY 1:
  if(cond1){              if(cond1 || cond2)       if(cond1 || cond2){
     A                            A                      A
  }                       }                         }
  else{          --->     else{               --->
     if(cond2){              empty else body
        A                 }
     }
  }

The removal of the empty else body is done by a different Transformation since
we need a reference to the parent node of this if


  PRIORITY 2:
  If the above pattern fails to match the following pattern is checked:

  if(cond1){                   if(!cond1){
     break label_1;                   Body1
  }                   ---->    }
  else{                        else{
      Body1                          break label_1
  }                            }

 The idea behind this is that this type of flipping of conditions
 will help in moving a condition into a cycle node

  TO MAKE CODE EFFECIENT BLOCK THE ANALYSIS TO GOING INTO STATEMENTS
  this is done by overriding the caseASTStatementSequenceNode
*/
public class OrAggregatorTwo extends DepthFirstAdapter {

  public OrAggregatorTwo() {
    DEBUG = false;
  }

  public OrAggregatorTwo(boolean verbose) {
    super(verbose);
    DEBUG = false;
  }

  public void caseASTStatementSequenceNode(ASTStatementSequenceNode node) {
  }

  public void outASTIfElseNode(ASTIfElseNode node) {
    // check whether the else body has another if and nothing else

    List<Object> ifBody = node.getIfBody();
    List<Object> elseBody = node.getElseBody();

    List<Object> innerIfBody = checkElseHasOnlyIf(elseBody);

    if (innerIfBody == null) {
      // pattern 1 did not match

      // check for pattern 2
      matchPatternTwo(node);

      return;
    }
    // pattern 1 is fine till now
    // compare the ifBody with the innerIfBody
    // They need to match exactly

    if (ifBody.toString().compareTo(innerIfBody.toString()) != 0) {
      matchPatternTwo(node);
      return;
    }

    ASTCondition leftCond = node.get_Condition();
    ASTCondition rightCond = getRightCond(elseBody);
    ASTCondition newCond = new ASTOrCondition(leftCond, rightCond);

    /*
     * The outer if and inner if could both have labels. Note that if the inner if had a label which was broken from inside
     * its body the two bodies would not have been the same since the outerifbody could not have the same label.
     *
     * We therefore keep the outerIfElse label
     */
    // System.out.println("OR AGGREGATOR TWO");
    node.set_Condition(newCond);

    /*
     * Always have to follow with a parse to remove unwanted empty ElseBodies
     */
    node.replaceElseBody(new ArrayList<Object>());

    G.v().ASTTransformations_modified = true;
  }

  public ASTCondition getRightCond(List<Object> elseBody) {
    // We know from checkElseHasOnlyIf that there is only one node
    // in this body and it is an ASTIfNode
    ASTIfNode innerIfNode = (ASTIfNode) elseBody.get(0);
    return innerIfNode.get_Condition();
  }

  public List<Object> checkElseHasOnlyIf(List<Object> elseBody) {
    if (elseBody.size() != 1) {
      // there should only be on IfNode here
      return null;
    }
    // there is only one node check that its a ASTIFNode
    ASTNode temp = (ASTNode) elseBody.get(0);
    if (!(temp instanceof ASTIfNode)) {
      // should have been an If node to match the pattern
      return null;
    }
    ASTIfNode innerIfNode = (ASTIfNode) temp;
    List<Object> innerIfBody = innerIfNode.getIfBody();
    return innerIfBody;
  }

  public void matchPatternTwo(ASTIfElseNode node) {
    debug("OrAggregatorTwo", "matchPatternTwo", "Did not match patternOne...trying patternTwo");
    List<Object> ifBody = node.getIfBody();
    if (ifBody.size() != 1) {
      // we are only interested if size is one
      return;
    }
    ASTNode onlyNode = (ASTNode) ifBody.get(0);
    if (!(onlyNode instanceof ASTStatementSequenceNode)) {
      // only interested in StmtSeq nodes
      return;
    }
    ASTStatementSequenceNode stmtNode = (ASTStatementSequenceNode) onlyNode;
    List<AugmentedStmt> statements = stmtNode.getStatements();
    if (statements.size() != 1) {
      // there is more than one statement
      return;
    }

    // there is only one statement
    AugmentedStmt as = statements.get(0);
    Stmt stmt = as.get_Stmt();

    if (!(stmt instanceof DAbruptStmt)) {
      // this is not a break/continue stmt
      return;
    }
    DAbruptStmt abStmt = (DAbruptStmt) stmt;
    if (!(abStmt.is_Break() || abStmt.is_Continue())) {
      // not a break/continue
      return;
    }

    // pattern matched
    // flip condition and switch bodies
    ASTCondition cond = node.get_Condition();
    cond.flip();

    List<Object> elseBody = node.getElseBody();
    SETNodeLabel label = node.get_Label();

    node.replace(label, cond, elseBody, ifBody);
    debug("", "", "REVERSED CONDITIONS AND BODIES");
    debug("", "", "elseBody is" + elseBody);
    debug("", "", "ifBody is" + ifBody);

    G.v().ASTIfElseFlipped = true;
  }

}
