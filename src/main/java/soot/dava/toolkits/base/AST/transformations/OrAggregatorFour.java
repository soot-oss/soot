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

  The class is responsible to do the following transformation on the AST
  
  label_1:                                                 
     while(cond){                                    label_1:
         BodyA;                                        while(cond){
         label_2:{                                         BodyA;
            if(cond1){                                     if(cond1 || ..... || !cond2){
                break label_2;                                     BodyB
            }                                              }
            //same as above                            }//end of while
            .
            .                                        remove label_1 if BodyA and BodyB
            if(cond2){                               dont have any reference to label_1 (highly likely)
                continue label_1;     ------>        should be done as a separate analysis
	    }                             
         }//end of label_2                     
         BodyB                                
  }//end while                           


  This pattern is applicable to the four cycle nodes representing
  while(true), while(cond) and dowhile(cond) for loops

  TO MAKE CODE EFFECIENT BLOCK THE ANALYSIS TO GOING INTO STATEMENTS
  this is done by overriding the caseASTStatementSequenceNode
*/

public class OrAggregatorFour extends DepthFirstAdapter{

    public OrAggregatorFour(){
    }
    public OrAggregatorFour(boolean verbose){
	super(verbose);
    }

    public void caseASTStatementSequenceNode(ASTStatementSequenceNode node){
    }




    public void outASTForLoopNode(ASTForLoopNode node){
	String label = node.get_Label().toString();
	if(label==null)
	    return;
	
	List<Object> subBodies=node.get_SubBodies();
	List<Object> newBody=matchPattern(label,subBodies);
	if(newBody!=null){
	    node.replaceBody(newBody);
	    //System.out.println("OR AGGREGATOR FOUR");
	    G.v().ASTTransformations_modified = true;
	}
	
	/*
	  see if we can remove the label from this construct
	*/
	UselessLabelFinder.v().findAndKill(node);
    }



    public void outASTWhileNode(ASTWhileNode node){
	String label = node.get_Label().toString();
	if(label==null)
	    return;
	
	List<Object> subBodies=node.get_SubBodies();
	List<Object> newBody=matchPattern(label,subBodies);
	if(newBody!=null){
	    node.replaceBody(newBody);
	    //System.out.println("OR AGGREGATOR FOUR");
	    G.v().ASTTransformations_modified = true;
	}
	
	/*
	  see if we can remove the label from this construct
	*/
	UselessLabelFinder.v().findAndKill(node);
    }

    public void outASTDoWhileNode(ASTDoWhileNode node){
	String label = node.get_Label().toString();
	if(label==null)
	    return;

	List<Object> subBodies=node.get_SubBodies();
	List<Object> newBody=matchPattern(label,subBodies);
	if(newBody!=null){
	    node.replaceBody(newBody);
	    //System.out.println("OR AGGREGATOR FOUR");
	    G.v().ASTTransformations_modified = true;
	}
	/*
	  see if we can remove the label from this construct
	*/
	UselessLabelFinder.v().findAndKill(node);
    }

    public void outASTUnconditionalLoopNode(ASTUnconditionalLoopNode node){
	String label = node.get_Label().toString();
	if(label==null)
	    return;

	List<Object> subBodies=node.get_SubBodies();
	List<Object> newBody=matchPattern(label,subBodies);
	if(newBody!=null){
	    node.replaceBody(newBody);
	    //System.out.println("OR AGGREGATOR FOUR");
	    G.v().ASTTransformations_modified = true;
	}
	/*
	  see if we can remove the label from this construct
	*/
	UselessLabelFinder.v().findAndKill(node);
    }

    public List<Object> matchPattern(String whileLabel,List<Object> subBodies){
	//since the subBodies are coming from a cycle node we know
	//there is only one subBody
	if(subBodies.size()!=1){
	    //size should be one
	    return null;
	}

	List subBody = (List)subBodies.get(0);
	Iterator it = subBody.iterator();
	int nodeNumber=0;
	while(it.hasNext()){//going through the ASTNodes
	    //look for a labeledBlock
	    ASTNode temp = (ASTNode)it.next();
	    if(temp instanceof ASTLabeledBlockNode){
		//see if the inner pattern matches
		ASTLabeledBlockNode labeledNode = (ASTLabeledBlockNode)temp;
		String innerLabel=labeledNode.get_Label().toString();
		if(innerLabel==null){//label better not be null
		    nodeNumber++;
		    continue;
		}
		
		//get labeledBlocksBodies
		List<Object> labeledBlocksSubBodies = labeledNode.get_SubBodies();
		if(labeledBlocksSubBodies.size()!=1){
		    //should always be one
		    nodeNumber++;
		    continue;
		}

		//get the subBody
		List labeledBlocksSubBody = (List)labeledBlocksSubBodies.get(0);
		
		boolean allIfs = checkAllAreIfsWithProperBreaks(labeledBlocksSubBody.iterator(),whileLabel,innerLabel);
		if(!allIfs){
		    //pattern doesnt match
		    nodeNumber++;
		    continue;
		}

		//the pattern has been matched do the transformation

		//nodeNumber is the location of the ASTLabeledBlockNode
		List<Object> whileBody = createWhileBody(subBody,labeledBlocksSubBody,nodeNumber);
		if(whileBody!=null){
		    return whileBody;
		}
	    }//if its an ASTLabeledBlockNode
	    nodeNumber++;
	}//end of going through ASTNodes
	return null;
    }

    private List<Object> createWhileBody(List subBody,List labeledBlocksSubBody,int nodeNumber){
	//create BodyA, Nodes from 0 to nodeNumber
	List<Object> bodyA = new ArrayList<Object>();
	
	//this is an iterator of ASTNodes
	Iterator it = subBody.iterator();
	
	//copy to bodyA all nodes until you get to nodeNumber
	int index=0;
	while(index!=nodeNumber ){
	    if(!it.hasNext()){
		return null;
	    }
	    bodyA.add(it.next());
	    index++;
	}
       
	
	//create ASTIfNode
	// Create a list of conditions to be Ored together 
	// remembering that the last ones condition is to be flipped
	List<ASTCondition> conditions = getConditions(labeledBlocksSubBody.iterator());

	//create an aggregated condition
	Iterator<ASTCondition> condIt = conditions.iterator();
	ASTCondition newCond=null;;
	while(condIt.hasNext()){
	    ASTCondition next = condIt.next();
	    if(newCond==null)
		newCond=next;
	    else
		newCond=new ASTOrCondition(newCond,next);
	}

	
	//create BodyB
	it.next();//this skips the LabeledBlockNode
	List<Object> bodyB = new ArrayList<Object>();
	while(it.hasNext()){
	    bodyB.add(it.next());
	}
	
	ASTIfNode newNode = new ASTIfNode(new SETNodeLabel(),newCond,bodyB);


	//add this node to the bodyA
	bodyA.add(newNode);
	return bodyA;
    }

    /*
      The method will go through the iterator because of the sequence
      of methods called before in the outASTLabeledBlockNode
      it knows the following:
       All nodes are ASTIFNodes
    */  
    private List<ASTCondition> getConditions(Iterator it){
	List<ASTCondition> toReturn = new ArrayList<ASTCondition>();
	while(it.hasNext()){
	    //safe cast since we know these are all ASTIfNodes
	    ASTIfNode node = (ASTIfNode)it.next();
	    
	    ASTCondition cond = node.get_Condition();
	    //check if this is the last in which case we need to flip
	    if(it.hasNext()){
		//no need to flip
		toReturn.add(cond);
	    }
	    else{
		//need to flip condition
		//System.out.println("old:"+cond);
		cond.flip();
		//System.out.println("new"+cond);
		toReturn.add(cond);
	    }
	}//end of while
	return toReturn;
    }




    private boolean checkAllAreIfsWithProperBreaks(Iterator it,String outerLabel, String innerLabel){
	//the pattern says that ALL bodies in this list should be IF statements
	while(it.hasNext()){
	    ASTNode secondLabelsBody = (ASTNode)it.next();
	    
	    //check that this is a ifNode with a single statement

	    Stmt stmt = isIfNodeWithOneStatement(secondLabelsBody);
	    if(stmt == null){
		//pattern is broken
		return false;
	    }
	
	    //check if the single stmt follows the pattern
	    boolean abrupt = abruptLabel(stmt,outerLabel,innerLabel,it.hasNext());

	    if(!abrupt){
		//stmt does not follow the pattern
		return false;
	    }
	}//end of while going through all the bodies of the secondlabel

	//if we get here that means everything was according to the pattern
	return true;
    }








    /*
      If the stmt is a break stmt then see it breaks the inner label and thee boolean is true  return true
      If the stmt is a continue then see it continues the outer label and the boolean is false return true
      else return false
    */
    private boolean abruptLabel(Stmt stmt, String outerLabel, String innerLabel, boolean hasNext){
	if(!(stmt instanceof DAbruptStmt)){
	    //this is not a break/continue stmt
	    return false;
	}
	DAbruptStmt abStmt = (DAbruptStmt)stmt;
	SETNodeLabel label = abStmt.getLabel();
	String abruptLabel=label.toString();


	if(abruptLabel==null)
	    return false;

	if(abStmt.is_Break() && abruptLabel.compareTo(innerLabel)==0  && hasNext){
	    return true;
	}
	else if (abStmt.is_Continue() && abruptLabel.compareTo(outerLabel)==0  && !hasNext){
	    return true;
	}
	else
	    return false;
    }












    private Stmt isIfNodeWithOneStatement(ASTNode secondLabelsBody){
	if(!(secondLabelsBody instanceof ASTIfNode)){
	    //pattern broken as this should be a IfNode
	    return null;
	}
	//check that the body of ASTIfNode has a single ASTStatementSequence

	ASTIfNode ifNode =(ASTIfNode)secondLabelsBody;
	List<Object> ifSubBodies =ifNode.get_SubBodies();
	if(ifSubBodies.size()!=1){
	    //if body should always have oneSubBody
	    return null;
	}
	
	//if has one SubBody
	List ifBody = (List)ifSubBodies.get(0);
	
	//Looking for a statement sequence node with a single stmt
	if(ifBody.size()!=1){
	    //there should only be one body 
	    return null;
	}

	//The only subBody has one ASTNode
	ASTNode ifBodysBody = (ASTNode)ifBody.get(0);
	if(!(ifBodysBody instanceof ASTStatementSequenceNode)){
	    //had to be a STMTSEQ node
	    return null;
	}

	//the only ASTnode is a ASTStatementSequence 
	List<AugmentedStmt> statements = ((ASTStatementSequenceNode)ifBodysBody).getStatements();
	if(statements.size()!=1){
	    //there is more than one statement
	    return null;
	}

	//there is only one statement return the statement
	AugmentedStmt as = statements.get(0);
	Stmt s = as.get_Stmt();
	return s;
    }

}