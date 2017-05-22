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
  
  label_1:{                                  label_1:{ 
     label_0:{                                  if(cond1 || cond2){
         if(cond1){                                 Body1
            break label_0;                      }
         }                                   }
         if(!cond2){                         Body2    
            break label_1;     ------>  
	 }                                     Cant remove label_1 as Body 1 
    }//end of label_0                          might have reference to it  
    Body1                                      can however set some flag if we are
  }//end of label_1                            sure that label_1 is not broken
  Body2                                        and later removed


  TO MAKE CODE EFFECIENT BLOCK THE ANALYSIS TO GOING INTO STATEMENTS
  this is done by overriding the caseASTStatementSequenceNode
*/

public class OrAggregatorOne extends DepthFirstAdapter{

    public OrAggregatorOne(){
    }
    public OrAggregatorOne(boolean verbose){
	super(verbose);
    }

    public void caseASTStatementSequenceNode(ASTStatementSequenceNode node){
    }

    public void outASTLabeledBlockNode(ASTLabeledBlockNode node){
	String outerLabel = node.get_Label().toString();
	if(outerLabel==null)
	    return;

	String innerLabel=null;

	ASTLabeledBlockNode secondLabeledBlockNode = isLabelWithinLabel(node);
	if(secondLabeledBlockNode == null){
	    //node doesnt have an immediate label following it
	    return;
	}

	//store the labelname
	innerLabel=secondLabeledBlockNode.get_Label().toString();
	if(innerLabel==null){
	    //empty or marked for deletion
	    return;
	}
	
	List secondLabelsBodies= getSecondLabeledBlockBodies(secondLabeledBlockNode);

	boolean allIfs = checkAllAreIfsWithProperBreaks(secondLabelsBodies.iterator(),outerLabel,innerLabel);
	if(!allIfs){
	    //pattern doesnt match
	    return;
	}

	//the pattern has been matched do the transformation
	
	// Create a list of conditions to be Ored together 
	// remembering that the last ones condition is to be flipped
	List<ASTCondition> conditions = getConditions(secondLabelsBodies.iterator());

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
	

	//will contain the Body of the ASTIfNode
	List<Object> newIfBody = new ArrayList<Object>();

	//get_SubBodies of upper labeled block
	List<Object> subBodies = node.get_SubBodies();
	//we know that there is only one SubBody for this node retrieve that

	List labeledBlockBody = (List)subBodies.get(0);
	//from the isLabelWithinLabel method we know that the first is the labeled block
	//discard that keep the rest
	Iterator subBodiesIt = labeledBlockBody.iterator();
	subBodiesIt.next();//discarding first
	while(subBodiesIt.hasNext()){
	    ASTNode temp = (ASTNode)subBodiesIt.next();
	    newIfBody.add(temp);
	}

	ASTIfNode newNode = new ASTIfNode(new SETNodeLabel(),newCond,newIfBody);

	List<Object> newLabeledBlockBody = new ArrayList<Object>();
	newLabeledBlockBody.add(newNode);

	G.v().ASTTransformations_modified = true;
	//System.out.println("OR AGGREGATING ONE!!!");
	node.replaceBody(newLabeledBlockBody);


	/*
	  See if the outer label can be marked as useless
	*/

	UselessLabelFinder.v().findAndKill(node);
    }
    

    private ASTLabeledBlockNode isLabelWithinLabel(ASTLabeledBlockNode node){
	List<Object> subBodies = node.get_SubBodies(); 
	if(subBodies.size()==0){ //shouldnt happen
	    //labeledBlockNodes size is zero this is a useless label
	    //marked for removal by setting an empty SETNodeLabel
	    node.set_Label(new SETNodeLabel());
	    return null;
	}
	
	//LabeledBlockNode had SubBody we know there is always only one
	List bodies = (List)subBodies.get(0);
	//these bodies should be a labelled block followed by something
	if(bodies.size()==0){
	    //there is nothing inside this labeledBlock
	    //an empty Body is useless
	    node.set_Label(new SETNodeLabel());
	    return null;
	}

	//there is some ASTNode there
	ASTNode firstBody = (ASTNode)bodies.get(0);
	if(!(firstBody instanceof ASTLabeledBlockNode)){
	    //first body is not a labeledBlock node thus the pattern
	    //does not have the shape 
	    //                      label_1:
	    //                          label_0   
	    return null;
	}
	
	//Pattern is fine return the ASTLabeledBlockNode found
	return (ASTLabeledBlockNode)firstBody;
    }
    

    private List getSecondLabeledBlockBodies(ASTLabeledBlockNode secondLabeledBlockNode){
	//retrieve the SubBodies of this second labeledblock
	List<Object> secondLabelsSubBodies = secondLabeledBlockNode.get_SubBodies();
	if(secondLabelsSubBodies.size()==0){
	    //there is nothing in the labeledblock
	    //highlly unlikely but if yes then set labeledBlockNode for not printing/deletion
	    secondLabeledBlockNode.set_Label(new SETNodeLabel());
	    return null;
	}
	/*
	  there is atleast one body in there and infact it should be only one 
	  since this is a labeledBlockNode
	*/
	List secondLabelsBodies = (List)secondLabelsSubBodies.get(0);
	
	//return the list
	return secondLabelsBodies;
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
	    //check if the single stmt is a break stmt
	    String labelBroken = breaksLabel(stmt);
	    if(labelBroken == null){
		//stmt does not break a label
		return false;
	    }
	    
	    //check if this is the inner label broken and is not the last in the iterator
	    if(labelBroken.compareTo(innerLabel)==0 && it.hasNext())
		continue;
	    //check if this is the outer label broken and is the last in the iterator
	    if(labelBroken.compareTo(outerLabel)==0 && !it.hasNext())
		continue;

	    //if we get here that means this is not a break breaking the label we want
	    return false;
	}//end of while going through all the bodies of the secondlabel

	//if we get here that means everything was according to the pattern
	return true;
    }




    /*
      If the stmt is a break stmt then this method
      returns the labels name
      else returns null
    */
    private String breaksLabel(Stmt stmt){
	if(!(stmt instanceof DAbruptStmt)){
	    //this is not a break stmt
	    return null;
	}
	DAbruptStmt abStmt = (DAbruptStmt)stmt;
	if(!abStmt.is_Break()){
	    //not a break stmt
	    return null;
	}
	SETNodeLabel label = abStmt.getLabel();
	return label.toString();
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



    /*
      The method will go through the iterator because of the sequence
      of methods called before in the outASTLabeledBlockNode
      it knows the following:
      1, All nodes are ASTIFNodes
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
		cond.flip();
		toReturn.add(cond);
	    }
	}//end of while
	return toReturn;
    }
}