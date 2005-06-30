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
import soot.dava.*;
import soot.dava.internal.SET.*;
import soot.dava.internal.AST.*;
import soot.dava.toolkits.base.AST.analysis.*;


/*
  Nomair A. Naeem 21-FEB-2005

  In the depthFirstAdaptor children of a ASTNode
  are gotten in three ways
  a, ASTStatementSequenceNode uses one way see caseASTStatementSequenceNode in DepthFirstAdapter
  b, ASTTryNode uses another way see caseASTTryNode in DepthFirstAdapter
  c, All other nodes use normalRetrieving method to retrieve the children

  TO MAKE CODE EFFECIENT BLOCK THE ANALYSIS TO GOING INTO STATEMENTS
  this is done by overriding the caseASTStatementSequenceNode

*/

public class ForLoopCreator extends DepthFirstAdapter{

    public ForLoopCreator(){
    }

    public ForLoopCreator(boolean verbose){
	super(verbose);
    }

    
    public void caseASTStatementSequenceNode(ASTStatementSequenceNode node){
    }

    /*
      Note the ASTNode in this case can be any of the following:

      ASTMethodNode                  ASTSwitchNode               ASTIfNode         ASTIfElseNode
      ASTUnconditionalWhileNode      ASTWhileNode                ASTDoWhileNode    ASTForLoopNode
      ASTLabeledBlockNode            ASTSynchronizedBlockNode
    */
    public void normalRetrieving(ASTNode node){
	if(node instanceof ASTSwitchNode){
	    dealWithSwitchNode((ASTSwitchNode)node);
	    return;
	}

	//from the Node get the subBodes
	Iterator sbit = node.get_SubBodies().iterator();

	//onlyASTIfElseNode has 2 subBodies but we need to deal with that
	int subBodyNumber=0; 
	while (sbit.hasNext()) {
	    List subBody = (List)sbit.next();
	    Iterator it = subBody.iterator();

	    int nodeNumber=0;
	    //go over the ASTNodes in this subBody and apply
	    while (it.hasNext()){
		ASTNode temp = (ASTNode) it.next();
		if(temp instanceof ASTStatementSequenceNode){
		    //need to check if this is followed by a while node
		    if(it.hasNext()){
			//there is a next node
			ASTNode temp1 = (ASTNode)subBody.get(nodeNumber+1);
			if(temp1 instanceof ASTWhileNode){
			    //a statement sequence node followed by a while node

			    ForLoopCreationHelper helper = 
				new ForLoopCreationHelper((ASTStatementSequenceNode)temp,(ASTWhileNode)temp1);

			    if(helper.checkPattern()){
				//pattern matched
				
				List newBody = helper.createNewBody(subBody,nodeNumber);
				if(newBody!= null){
				    if(node instanceof ASTIfElseNode){
					if(subBodyNumber==0){
					    //the if body was modified
					    List subBodies = node.get_SubBodies();
					    List ifElseBody = (List)subBodies.get(1);
					    ((ASTIfElseNode)node).replaceBody(newBody,ifElseBody);
					    G.v().ASTTransformations_modified = true;
					    //System.out.println("FOR LOOP CREATED");
					    return;
					}
					else if(subBodyNumber==1){
					    //else body was modified
					    List subBodies = node.get_SubBodies();
					    List ifBody = (List)subBodies.get(0);
					    ((ASTIfElseNode)node).replaceBody(ifBody,newBody);
					    G.v().ASTTransformations_modified = true;
					    //System.out.println("FOR LOOP CREATED");
					    return;
					}
					else{
					    throw new RuntimeException("Please report benchmark to programmer.");
					}
				    }
				    else{
					if(node instanceof ASTMethodNode){
					    ((ASTMethodNode)node).replaceBody(newBody);
					    G.v().ASTTransformations_modified = true;
					    //System.out.println("FOR LOOP CREATED");
					    return;
					    
					}
					else if(node instanceof ASTSynchronizedBlockNode){
					    ((ASTSynchronizedBlockNode)node).replaceBody(newBody);
					    G.v().ASTTransformations_modified = true;
					    //System.out.println("FOR LOOP CREATED");
					    return;
					}
					else if(node instanceof ASTLabeledBlockNode){
					    ((ASTLabeledBlockNode)node).replaceBody(newBody);
					    G.v().ASTTransformations_modified = true;
					    //System.out.println("FOR LOOP CREATED");
					    return;
					}
					else if(node instanceof ASTUnconditionalLoopNode){
					    ((ASTUnconditionalLoopNode)node).replaceBody(newBody);
					    G.v().ASTTransformations_modified = true;
					    //System.out.println("FOR LOOP CREATED");
					    return;
					}
					else if(node instanceof ASTIfNode){
					    ((ASTIfNode)node).replaceBody(newBody);
					    G.v().ASTTransformations_modified = true;
					    //System.out.println("FOR LOOP CREATED");
					    return;
					}
					else if(node instanceof ASTWhileNode){
					    ((ASTWhileNode)node).replaceBody(newBody);
					    G.v().ASTTransformations_modified = true;
					    //System.out.println("FOR LOOP CREATED");
					    return;
					}
					else if(node instanceof ASTDoWhileNode){
					    ((ASTDoWhileNode)node).replaceBody(newBody);
					    G.v().ASTTransformations_modified = true;
					    //System.out.println("FOR LOOP CREATED");
					    return;
					}
					else if(node instanceof ASTForLoopNode){
					    ((ASTForLoopNode)node).replaceBody(newBody);
					    G.v().ASTTransformations_modified = true;
					    //System.out.println("FOR LOOP CREATED");
					    return;
					}
					else {
					   throw new RuntimeException("Please report benchmark to programmer.");   
					}
				    }
				}//newBody was not null
			    }//for loop creation pattern matched
			}//the next node was a whilenode
		    }//there is a next node
		}//temp is a stmtSeqNode
		temp.apply(this);
		nodeNumber++;
	    }//end of going over nodes
	    subBodyNumber++;
	}//end of going over subBodies
    }

    public void caseASTTryNode(ASTTryNode node){
	inASTTryNode(node);

	//get try body 
	List tryBody = node.get_TryBody();
	Iterator it = tryBody.iterator();

	int nodeNumber=0;
	//go over the ASTNodes and apply
	while (it.hasNext()){
	    ASTNode temp = (ASTNode) it.next();
	    if(temp instanceof ASTStatementSequenceNode){
		//need to check if this is followed by a while node
		if(it.hasNext()){
		    //there is a next node
		    ASTNode temp1 = (ASTNode)tryBody.get(nodeNumber+1);
		    if(temp1 instanceof ASTWhileNode){
			//a statement sequence node followed by a while node
			    
			ForLoopCreationHelper helper = 
			    new ForLoopCreationHelper((ASTStatementSequenceNode)temp,(ASTWhileNode)temp1);
			    
			if(helper.checkPattern()){
			    //pattern matched
			    
			    List newBody = helper.createNewBody(tryBody,nodeNumber);
			    if(newBody!= null){
				//something did not go wrong
				node.replaceTryBody(newBody);
				G.v().ASTTransformations_modified = true;
				//System.out.println("FOR LOOP CREATED");
				return;
			    }//newBody was not null
			}//for loop creation pattern matched
		    }//the next node was a whilenode
		}//there is a next node
	    }//temp is a stmtSeqNode
	    temp.apply(this);
	    nodeNumber++;
	}//end of while going through tryBody




	Map exceptionMap = node.get_ExceptionMap();
	Map paramMap = node.get_ParamMap();
	//get catch list and apply on the following
	// a, type of exception caught
	// b, local of exception
	// c, catchBody
	List catchList = node.get_CatchList();
	Iterator itBody=null;
        it = catchList.iterator();
	while (it.hasNext()) {
	    ASTTryNode.container catchBody = (ASTTryNode.container)it.next();
	    
	    SootClass sootClass = ((SootClass)exceptionMap.get(catchBody));
	    Type type = sootClass.getType();
	    
	    //apply on type of exception
	    caseType(type);

	    //apply on local of exception
	    Local local = (Local)paramMap.get(catchBody);
	    decideCaseExprOrRef(local);

	    //apply on catchBody
	    List body = (List)catchBody.o;
	    itBody = body.iterator();

	    nodeNumber=0;
	    //go over the ASTNodes and apply
	    while (itBody.hasNext()){
		ASTNode temp = (ASTNode) itBody.next();

		if(temp instanceof ASTStatementSequenceNode){
		    //need to check if this is followed by a while node
		    if(itBody.hasNext()){
			//there is a next node
			ASTNode temp1 = (ASTNode)body.get(nodeNumber+1);
			if(temp1 instanceof ASTWhileNode){
			    //a statement sequence node followed by a while node
			    
			    ForLoopCreationHelper helper = 
				new ForLoopCreationHelper((ASTStatementSequenceNode)temp,(ASTWhileNode)temp1);
			    
			    if(helper.checkPattern()){
				//pattern matched
			    
				List newBody = helper.createNewBody(body,nodeNumber);
				if(newBody!= null){
				    //something did not go wrong
				    catchBody.replaceBody(newBody);
				    G.v().ASTTransformations_modified = true;
				    //System.out.println("FOR LOOP CREATED");
				    return;
				}//newBody was not null
			    }//for loop creation pattern matched
			}//the next node was a whilenode
		    }//there is a next node
		}//temp is a stmtSeqNode
		
		temp.apply(this);
		nodeNumber++;
	    }
	}
	
	outASTTryNode(node);
    }


    private void dealWithSwitchNode(ASTSwitchNode node){
	//do a depthfirst on elements of the switchNode

	List indexList = node.getIndexList();
	Map index2BodyList = node.getIndex2BodyList();

	Iterator it = indexList.iterator();
	while (it.hasNext()) {//going through all the cases of the switch statement
	    Object currentIndex = it.next();
	    List body = (List) index2BodyList.get( currentIndex);
	    
	    if (body != null){
		//this body is a list of ASTNodes 

		Iterator itBody = body.iterator();
		int nodeNumber=0;
		//go over the ASTNodes and apply
		while (itBody.hasNext()){
		    ASTNode temp = (ASTNode) itBody.next();

		    if(temp instanceof ASTStatementSequenceNode){
			//need to check if this is followed by a while node
			if(itBody.hasNext()){
			    //there is a next node
			    ASTNode temp1 = (ASTNode)body.get(nodeNumber+1);
			    if(temp1 instanceof ASTWhileNode){
				//a statement sequence node followed by a while node
				
				ForLoopCreationHelper helper = 
				    new ForLoopCreationHelper((ASTStatementSequenceNode)temp,(ASTWhileNode)temp1);
				
				if(helper.checkPattern()){
				    //pattern matched
				    
				    List newBody = helper.createNewBody(body,nodeNumber);
				    if(newBody!= null){
					//something did not go wrong

					//put this body in the Map
					index2BodyList.put(currentIndex,newBody);
					//replace in actual switchNode
					node.replaceIndex2BodyList(index2BodyList);

					G.v().ASTTransformations_modified = true;
					//System.out.println("FOR LOOP CREATED");
					return;
				    }//newBody was not null
				}//for loop creation pattern matched
			    }//the next node was a whilenode
			}//there is a next node
		    }//temp is a stmtSeqNode
		    temp.apply(this);
		    nodeNumber++;
		}
	    }
	}
    }
}