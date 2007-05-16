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
import soot.dava.internal.AST.*;
import soot.dava.internal.asg.*;
import soot.dava.internal.javaRep.*;

/*
  Nomair A. Naeem 21-FEB-2005
  The class is responsible to do the following transformation on the AST
  if(cond1){              if(cond1 || cond2)       
     A                            A                
  }                       }                        
  if(cond2){         
     A              
  }                 

  Notice that this kind of conversion is only possible if A is an
  abrupt edge like:
     a, break label_0;
     b, continue label_0;
     c, return;

  i.e. we need to make sure that the A bodies are single statements
  and that too an abrupt control flow

  Also note that in the new aggregated or condition it is very important
  that cond2 be checked AFTER cond1 has been checked and failed.
  The reason for this being side effects that these conditions can cause.

*/
public class OrAggregatorThree {


    public static void checkAndTransform(ASTNode node,ASTIfNode ifOne,ASTIfNode ifTwo, int nodeNumber,int subBodyNumber){

	if(!(node instanceof ASTIfElseNode)){
	    //these are the nodes which always have one subBody
	    List<Object> subBodies = node.get_SubBodies();
	    if(subBodies.size()!=1){
		//there is something wrong
		throw new RuntimeException("Please report this benchmark to the programmer");
	    }
	    List<Object> onlySubBody = (List<Object>)subBodies.get(0);

	    /*
	      The onlySubBody contains the Two consective if nodes
	      at location given by nodeNumber and nodeNumber+1
	    */
	    
	    //match the pattern and get the newBody
	    List<Object> newBody = createNewNodeBody(onlySubBody,nodeNumber,ifOne,ifTwo);


	    if(newBody==null){
		//something went wrong, pattern didnt match or some other problem
		return;
	    }
	    if(node instanceof ASTMethodNode){
		((ASTMethodNode)node).replaceBody(newBody);
		G.v().ASTTransformations_modified = true;
		//System.out.println("OR AGGREGATOR THREE");
	    }
	    else if(node instanceof ASTSynchronizedBlockNode){
		((ASTSynchronizedBlockNode)node).replaceBody(newBody);
		G.v().ASTTransformations_modified = true;
		//System.out.println("OR AGGREGATOR THREE");
	    }
	    else if(node instanceof ASTLabeledBlockNode){
		((ASTLabeledBlockNode)node).replaceBody(newBody);
		G.v().ASTTransformations_modified = true;
		//System.out.println("OR AGGREGATOR THREE");
	    }
	    else if(node instanceof ASTUnconditionalLoopNode){
		((ASTUnconditionalLoopNode)node).replaceBody(newBody);
		G.v().ASTTransformations_modified = true;
		//System.out.println("OR AGGREGATOR THREE");
	    }
	    else if(node instanceof ASTIfNode){
		((ASTIfNode)node).replaceBody(newBody);
		G.v().ASTTransformations_modified = true;
		//System.out.println("OR AGGREGATOR THREE");
	    }
	    else if(node instanceof ASTWhileNode){
		((ASTWhileNode)node).replaceBody(newBody);
		G.v().ASTTransformations_modified = true;
		//System.out.println("OR AGGREGATOR THREE");
	    }
	    else if(node instanceof ASTDoWhileNode){
		((ASTDoWhileNode)node).replaceBody(newBody);
		G.v().ASTTransformations_modified = true;
		//System.out.println("OR AGGREGATOR THREE");
	    }
	    else {
		//there is no other case something is wrong if we get here
		return;
	    }
	}
	else{//its an ASTIfElseNode
	    //if its an ASIfElseNode then check which Subbody has the labeledBlock
	    if(subBodyNumber!=0 && subBodyNumber!=1){
		//something bad is happening dont do nothin
		//System.out.println("Error-------not modifying AST");
		return;
	    }
	    List<Object> subBodies = node.get_SubBodies();
	    if(subBodies.size()!=2){
		//there is something wrong
		throw new RuntimeException("Please report this benchmark to the programmer");
	    }

	    List<Object> toModifySubBody = (List<Object>)subBodies.get(subBodyNumber);

	    /*
	      The toModifySubBody contains the two consective if nodes in question
	      at location given by the nodeNumber and nodeNumer+1
	    */
	    List<Object> newBody = createNewNodeBody(toModifySubBody,nodeNumber,ifOne,ifTwo);
	    if(newBody==null){
		//something went wrong, the pattern didnt match or something else
		return;
	    }
	    if(subBodyNumber==0){
		//the if body was modified
		//System.out.println("OR AGGREGATOR THREE");
		G.v().ASTTransformations_modified = true;
		((ASTIfElseNode)node).replaceBody(newBody,(List<Object>)subBodies.get(1));
	    }
	    else if(subBodyNumber==1){
		//else body was modified
		//System.out.println("OR AGGREGATOR THREE");
		G.v().ASTTransformations_modified = true;
		((ASTIfElseNode)node).replaceBody((List<Object>)subBodies.get(0),newBody);
	    }
	    else{//realllly shouldnt come here
		//something bad is happening dont do nothin
		//System.out.println("Error-------not modifying AST");
		return;
	    }

	}//end of ASTIfElseNode
    }


    /*
      This method does the following:
      1, check that the OrAggregatorThree pattern matches for node ifOne and ifTwo
      2, if pattern does not match return null
      3, if pattern matches create and return a newSubBody which has:
         a, ifOne with its condition ORED with that of ifTwo
	 b, ifTwo has been removed from the subBody
    */

    public static List<Object> createNewNodeBody(List<Object> oldSubBody,int nodeNumber,ASTIfNode ifOne ,ASTIfNode ifTwo){
	if(!matchPattern(ifOne,ifTwo)){
	    //pattern did not match
	    return null;
	}
	
	//create a new SubBody
	List<Object> newSubBody = new ArrayList<Object>();
	
	//this is an iterator of ASTNodes
	Iterator<Object> it = oldSubBody.iterator();
	
	//copy to newSubBody all nodes until you get to nodeNumber
	int index=0;
	while(index!=nodeNumber ){
	    if(!it.hasNext()){
		return null;
	    }
	    newSubBody.add(it.next());
	    index++;
	}

	//at this point the iterator is pointing to the ASTIFNode
	//just to make sure check this
	ASTNode isItIfOne = (ASTNode)it.next();

	if(!(isItIfOne instanceof ASTIfNode)){
	    //something is wrong 
	    return null;
	}

	//get the next node that should also be an ASTIfNode
	ASTNode isItIfTwo = (ASTNode)it.next();
	if(!(isItIfTwo instanceof ASTIfNode)){
	    //something is wrong 
	    return null;
	}


	//double check by invoking matchPattern on these
	//if speed is an issue this check can be removed
	if(!matchPattern((ASTIfNode)isItIfOne,(ASTIfNode)isItIfTwo)){
	    //pattern did not match
	    return null;
	}
	
	//we are sure that we have the two nodes

	//create new node
	ASTIfNode firstOne = (ASTIfNode)isItIfOne;
	ASTIfNode secondOne = (ASTIfNode)isItIfTwo;
	
	//create new condition
	ASTCondition firstCond = firstOne.get_Condition();
	ASTCondition secondCond = secondOne.get_Condition();

	ASTCondition newCond = new ASTOrCondition(firstCond,secondCond);

	ASTIfNode newNode = new ASTIfNode(firstOne.get_Label(),newCond,firstOne.getIfBody());
	
	//add the new node
	newSubBody.add(newNode);


	//add any remaining nodes in the oldSubBody to the new one
	while(it.hasNext()){
	    newSubBody.add(it.next());
	}

	//newSubBody is ready return it
	return newSubBody;
    }


    /*
      Given two IfNodes as input the pattern checks the following:
      a, each if node has a single ASTSTatementSequenceNode in the body
      b, Each StatementSequenceNode is a single statement
      c, The statement is the same in both nodes
      d, The statement is an abrupt control flow statement
    */
    private static boolean matchPattern(ASTIfNode one, ASTIfNode two){
	List<Object> subBodiesOne=one.get_SubBodies();
	List<Object> subBodiesTwo=two.get_SubBodies();
	
	if(subBodiesOne.size()!=1 || subBodiesTwo.size()!=1){
	    //these are both if nodes they should always have one subBody
	    return false;
	}
	List onlySubBodyOne = (List)subBodiesOne.get(0);
	List onlySubBodyTwo = (List)subBodiesTwo.get(0);

	if(onlySubBodyOne.size()!=1 || onlySubBodyTwo.size()!=1){
	    //these subBodies are expected to have a single StatementSequenceNode
	    return false;
	}
	
	ASTNode onlyASTNodeOne = (ASTNode)onlySubBodyOne.get(0);
	ASTNode onlyASTNodeTwo = (ASTNode)onlySubBodyTwo.get(0);

	if( !(onlyASTNodeOne instanceof ASTStatementSequenceNode) || !(onlyASTNodeTwo instanceof ASTStatementSequenceNode)){
	    //need both of these nodes to be StatementSequnceNodes
	    return false;
	}

	ASTStatementSequenceNode stmtSeqOne = (ASTStatementSequenceNode)onlyASTNodeOne;
	ASTStatementSequenceNode stmtSeqTwo = (ASTStatementSequenceNode)onlyASTNodeTwo;

	List<Object> stmtsOne = stmtSeqOne.getStatements();
	List<Object> stmtsTwo = stmtSeqTwo.getStatements();

	if(stmtsOne.size()!=1 || stmtsTwo.size()!=1){
	    //there should only be one statement
	    return false;
	}

	AugmentedStmt asOne = (AugmentedStmt)stmtsOne.get(0);
	AugmentedStmt asTwo = (AugmentedStmt)stmtsTwo.get(0);


	Stmt s1 = asOne.get_Stmt();
	Stmt s2 = asTwo.get_Stmt();

	if(s1.toString().compareTo(s2.toString())!=0){
	    //the two stmts are not the same
	    return false;
	}

	//check if they are abrupt statements
	if(s1 instanceof DAbruptStmt && s2 instanceof DAbruptStmt){
	    //takes care of break <label> and continue <label>
	    return true;
	}
	else if(s1 instanceof ReturnStmt && s2 instanceof ReturnStmt){
	    //takes care of return <var>
	    return true;
	}
	else if (s1 instanceof ReturnVoidStmt && s2 instanceof ReturnVoidStmt){
	    //takes care of return;
	    return true;
	}
	else
	    return false;
    }
}