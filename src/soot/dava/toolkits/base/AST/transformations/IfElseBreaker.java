/* Soot - a J*va Optimization Framework
 * Copyright (C) 2005 Nomair A. Naeem
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

package soot.dava.toolkits.base.AST.transformations;

import java.util.*;
import soot.jimple.*;
import soot.dava.internal.SET.*;
import soot.dava.internal.AST.*;
import soot.dava.internal.asg.*;
import soot.dava.internal.javaRep.*;


/*
  Nomair A. Naeem 03-MARCH-2005

PATTERN 1:
  if(cond1){                     if(cond1){
     break label_1                   break label_1
  }                      ----->   }
  else{                           Body1
    Body1
  }


PATTERN 2:
  if(!cond1){                     if(cond1){
     Body1                            break label_1
  }                      ----->   }
  else{                           Body1
    break label_1
  }

  The assumption is that if cond1 is true there is an abrupt edge and Body1
  will not be executed. So this works only if the else in the original
  has an abrupt control flow. This can be break/continue/return


  TO MAKE CODE EFFECIENT BLOCK THE ANALYSIS TO GOING INTO STATEMENTS
  this is done by overriding the caseASTStatementSequenceNode
*/
public class IfElseBreaker{
    ASTIfNode newIfNode;
    List<Object> remainingBody;

    public IfElseBreaker(){
	newIfNode=null;
	remainingBody=null;
    }

    public boolean isIfElseBreakingPossiblePatternOne(ASTIfElseNode node){
	List<Object> ifBody = node.getIfBody();
	if(ifBody.size()!=1){
	    //we are only interested if size is one
	    return false;
	}
	
	ASTNode onlyNode=(ASTNode)ifBody.get(0);
	boolean check = checkStmt(onlyNode,node);
	if(!check){
	    return false;
	}

	//breaking is possible
	//break and store
	newIfNode = new ASTIfNode(((ASTLabeledNode)node).get_Label(),node.get_Condition(),ifBody);
	remainingBody = node.getElseBody();
	
	return true;
    }


    public boolean isIfElseBreakingPossiblePatternTwo(ASTIfElseNode node){
	List<Object> elseBody = node.getElseBody();
	if(elseBody.size()!=1){
	    //we are only interested if size is one
	    return false;
	}
	
	ASTNode onlyNode=(ASTNode)elseBody.get(0);
	boolean check = checkStmt(onlyNode,node);
	if(!check){
	    return false;
	}
	//breaking is possible

	ASTCondition cond = node.get_Condition();
	//flip
	cond.flip();

	newIfNode = new ASTIfNode(((ASTLabeledNode)node).get_Label(),cond,elseBody);
	remainingBody = node.getIfBody();
	
	return true;
    }




    private boolean checkStmt(ASTNode onlyNode,ASTIfElseNode node){
	if(!(onlyNode instanceof ASTStatementSequenceNode)){
	    //only interested in StmtSeq nodes
	    return false;
	}
	
	ASTStatementSequenceNode stmtNode=(ASTStatementSequenceNode)onlyNode;
	List<AugmentedStmt> statements = stmtNode.getStatements();
	if(statements.size()!=1){
	    //need one stmt only
	    return false;
	}
	
	AugmentedStmt as = statements.get(0);
	Stmt stmt = as.get_Stmt();

	if(!(stmt instanceof DAbruptStmt)){
	    //interested in abrupt stmts only
	    return false;
	}
	DAbruptStmt abStmt = (DAbruptStmt)stmt;
	if(!(abStmt.is_Break() || abStmt.is_Continue())){
	    //interested in breaks and continues only
	    return false;
	}

	//make sure that the break is not that of the if
	//unliekly but good to check
	SETNodeLabel ifLabel = ((ASTLabeledNode)node).get_Label();
	
	if (ifLabel!=null){
	    if(ifLabel.toString()!=null){
		if(abStmt.is_Break()){
		    String breakLabel = abStmt.getLabel().toString();
		    if(breakLabel!=null){
			if(breakLabel.compareTo(ifLabel.toString())==0){
			    //is a break of this label
			    return false;
			}
		    }
		}
	    }
	}
	return true;
    }





    /*
      The purpose of this method is to replace the ASTIfElseNode
      given by the var nodeNumber with the new ASTIfNode
      and to add the remianing list of bodies after this ASTIfNode

      The new body is then returned;

    */
    public List<Object> createNewBody(List<Object> oldSubBody, int nodeNumber){
	if(newIfNode == null)
	    return null;

	List<Object> newSubBody = new ArrayList<Object>();

	if(oldSubBody.size()<= nodeNumber){
	    //something is wrong since the oldSubBody has lesser nodes than nodeNumber
	    return null;
	}

	Iterator<Object> oldIt = oldSubBody.iterator();
	int index=0;
	while(index!=nodeNumber){
	    newSubBody.add(oldIt.next());
	    index++;
	}

	//check to see that the next is an ASTIfElseNode
	ASTNode temp = (ASTNode)oldIt.next();
	if(!(temp instanceof ASTIfElseNode))
	    return null;
	
	newSubBody.add(newIfNode);

	newSubBody.addAll(remainingBody);


	//copy any remaining nodes
	while(oldIt.hasNext()){
	    newSubBody.add(oldIt.next());
	}
	
	return newSubBody;
    }
}
