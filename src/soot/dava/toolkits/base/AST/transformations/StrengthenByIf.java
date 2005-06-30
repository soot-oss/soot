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

import soot.*;
import java.util.*;
import soot.jimple.*;
import soot.dava.internal.SET.*;
import soot.dava.internal.AST.*;
import soot.dava.internal.asg.*;
import soot.dava.internal.javaRep.*;
import soot.dava.toolkits.base.AST.analysis.*;
/*
  Nomair A. Naeem 18-FEB-2005

	PATTERN ONE:
	   label_1:                                                 
           while(cond1){                       
               if(cond2){                      while(cond1 && !cond2){
                   Body              
               }                               }
           }                                   //if body is a break label_1
	

	    PATTERN TWO:
	      lable_1:
              while(true){                while(!cond){
                if(cond){                 }     
                    Body                  Body(minus the break)
                }
              }
              This pattern can be applied only if
	      1: the loop is an UnconditionalLoopNode
	      2, The body should be a statement sequence (could do the general
	         case but its too complex
	      2, The body contains an abrupt edge out of the loop

  TO MAKE CODE EFFECIENT BLOCK THE ANALYSIS TO GOING INTO STATEMENTS
  this is done by overriding the caseASTStatementSequenceNode
*/

public class StrengthenByIf{
    /*
      We know this method is called when there is a while node which has a body
      consisting entirely of one ASTIfNode
    */
    public static List getNewNode(ASTNode loopNode, ASTIfNode ifNode){
	List ifBody = ifNode.getIfBody();
	String label = isItOnlyBreak(ifBody);
	if(label != null){
	    //only one break statement and it is breaking some label
	    
	    //make sure its breaking the label on the loop
	    if(((ASTLabeledNode)loopNode).get_Label().toString()!=null){

		if(((ASTLabeledNode)loopNode).get_Label().toString().compareTo(label)==0){
		    //the if has a single break breaking the loop
		    //pattern 1 matched
		    
		    if(loopNode instanceof ASTWhileNode){
			ASTCondition outerCond = ((ASTWhileNode)loopNode).get_Condition();
			//flip the inner condition
			ASTCondition innerCond = ifNode.get_Condition();
			innerCond.flip();
			//aggregate the two conditions
			ASTCondition newCond = new ASTAndCondition(outerCond,innerCond);
			//make empty body
			List newWhileBody = new ArrayList();
			//SETNodeLabel newLabel = ((ASTWhileNode)loopNode).get_Label();

			// dont need any label name since the body of the while is empty
			SETNodeLabel newLabel = new SETNodeLabel();

			//make new ASTWhileNode
			List toReturn = new ArrayList();
			toReturn.add(new ASTWhileNode(newLabel,newCond,newWhileBody));
			return toReturn;
			
		    }
		    else if(loopNode instanceof ASTDoWhileNode){
			/*
			  What to do when the ASTDoWhileNode only has one
			  body which is a break of the whileNode???
			*/
			return null;
		    }
		    else if (loopNode instanceof ASTUnconditionalLoopNode){
			/*
			  An UnconditionalLoopNode has a single If Condition
			  which breaks the loop
			  In this case 
			  Create an ASTWhileLoop Node with the flipped Condition
			  of the If statement
			*/

			//flip the inner condition
			ASTCondition innerCond = ifNode.get_Condition();
			innerCond.flip();

			//make empty body
			List newWhileBody = new ArrayList();
			//SETNodeLabel newLabel = ((ASTUnconditionalLoopNode)loopNode).get_Label();

			// dont need any label name since the body of the while is empty
			SETNodeLabel newLabel = new SETNodeLabel();
			
			//make new ASTWhileNode
			List toReturn = new ArrayList();
			toReturn.add(new ASTWhileNode(newLabel,innerCond,newWhileBody));
			return toReturn;
		    }
		}//if the labels match
	    }
	}//the first Pattern was a match
	else if(loopNode instanceof ASTUnconditionalLoopNode && ifBody.size()==1){
	    //try the UnconditionalLoopNode pattern

	    //we need one stmtSeq Node
	    ASTNode tempNode = (ASTNode)ifBody.get(0);
	    if(tempNode instanceof ASTStatementSequenceNode){
		//a stmtSeq
		List statements = ((ASTStatementSequenceNode)tempNode).getStatements();
		Iterator stIt = statements.iterator();
		while(stIt.hasNext()){
		    AugmentedStmt as = (AugmentedStmt)stIt.next();
		    Stmt stmt = as.get_Stmt();
		    if(stmt instanceof DAbruptStmt && !(stIt.hasNext())){
			//this is an abrupt stmt and the last stmt
			DAbruptStmt abStmt = (DAbruptStmt)stmt;
			if(abStmt.is_Break()){
			    //last statement and that too a break
			    String loopLabel = ((ASTLabeledNode)loopNode).get_Label().toString();
			    String breakLabel= abStmt.getLabel().toString();
			    if(loopLabel != null && breakLabel!=null){
				if(loopLabel.compareTo(breakLabel)==0){
				    
				    //pattern matched
				    //flip the inner condition
				    ASTCondition innerCond = ifNode.get_Condition();
				    innerCond.flip();
				    
				    //make empty body
				    List newWhileBody = new ArrayList();
				    SETNodeLabel newLabel = ((ASTUnconditionalLoopNode)loopNode).get_Label();
				    
				    //make new ASTWhileNode
				    List toReturn = new ArrayList();
				    toReturn.add(new ASTWhileNode(newLabel,innerCond,newWhileBody));
				    
				    //  Add the statementSequenceNode AFTER the whileNode except for the laststmt
				    Iterator tempIt = statements.iterator();
				    List newStmts = new ArrayList();
				    while(tempIt.hasNext()){
					Object tempStmt = tempIt.next();
					if(tempIt.hasNext()){
					    newStmts.add(tempStmt);
					}
				    }
				    toReturn.add(new ASTStatementSequenceNode(newStmts));
				    return toReturn;
				}//labels are the same
			    }//labels are non null
			}//is a break stmt
		    }
		    else if (stmt instanceof ReturnStmt || stmt instanceof ReturnVoidStmt){
			if(!(stIt.hasNext())){
			    //return obj/return;
			    //flip cond
			    ASTCondition innerCond = ifNode.get_Condition();
			    innerCond.flip();
			    
			    //make empty body
			    List newWhileBody = new ArrayList();
			    //SETNodeLabel newLabel = ((ASTUnconditionalLoopNode)loopNode).get_Label();
			    

			    // dont need any label name since the body of the while is empty
			    SETNodeLabel newLabel = new SETNodeLabel();

			    //make new ASTWhileNode
			    List toReturn = new ArrayList();
			    toReturn.add(new ASTWhileNode(newLabel,innerCond,newWhileBody));
			    
			    //  Add the statementSequenceNode AFTER the whileNode except for the laststmt
			    Iterator tempIt = statements.iterator();
			    List newStmts = new ArrayList();
			    while(tempIt.hasNext()){
				newStmts.add(tempIt.next());
			    }
			    toReturn.add(new ASTStatementSequenceNode(newStmts));
			    return toReturn;
			}
		    }
		}//end of going through statements
	    }//end of stmtSEq node
	}//end of else if
	return null;
    }//end of method




    /*
      Given a body of a node the method checks for the following:
      1, the body has only one node
      2, the node is a statementSequenceNode
      3, There is only one statement in the stmt seq node
      4, the stmt is a break stmt

      If the conditions are true the label of the break stmt is returned
      otherwise null is returned
    */
    private static String isItOnlyBreak(List body){
	if(body.size()!=1){
	    //this is more than one we need one stmtSeq Node
	    return null;
	}
	ASTNode tempNode = (ASTNode)body.get(0);
	if(!(tempNode instanceof ASTStatementSequenceNode)){
	    //not a stmtSeq
	    return null;
	}
	  
	List statements = ((ASTStatementSequenceNode)tempNode).getStatements();
	if(statements.size()!=1){
	    //we need one break
	    return null;
	}
	AugmentedStmt as = (AugmentedStmt)statements.get(0);
	Stmt stmt = as.get_Stmt();
	if(!(stmt instanceof DAbruptStmt)){
	    //this is not a break stmt
	    return null;
	}
	DAbruptStmt abStmt = (DAbruptStmt)stmt;
	if(!(abStmt.is_Break())){
	    //we need a break
	    return null;
	}
	return abStmt.getLabel().toString();
    }
}


