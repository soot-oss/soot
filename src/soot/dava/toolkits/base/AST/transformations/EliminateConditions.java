/* Soot - a J*va Optimization Framework
 * Copyright (C) 2006 Nomair A. Naeem
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

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import soot.BooleanType;
import soot.G;
import soot.Local;
import soot.SootClass;
import soot.Type;
import soot.Value;
import soot.dava.internal.AST.ASTCondition;
import soot.dava.internal.AST.ASTControlFlowNode;
import soot.dava.internal.AST.ASTDoWhileNode;
import soot.dava.internal.AST.ASTForLoopNode;
import soot.dava.internal.AST.ASTIfElseNode;
import soot.dava.internal.AST.ASTIfNode;
import soot.dava.internal.AST.ASTLabeledBlockNode;
import soot.dava.internal.AST.ASTLabeledNode;
import soot.dava.internal.AST.ASTMethodNode;
import soot.dava.internal.AST.ASTNode;
import soot.dava.internal.AST.ASTStatementSequenceNode;
import soot.dava.internal.AST.ASTSwitchNode;
import soot.dava.internal.AST.ASTTryNode;
import soot.dava.internal.AST.ASTUnaryCondition;
import soot.dava.internal.AST.ASTWhileNode;
import soot.dava.internal.SET.SETNodeLabel;
import soot.dava.internal.javaRep.DIntConstant;
import soot.dava.internal.javaRep.DNotExpr;
import soot.dava.toolkits.base.AST.analysis.DepthFirstAdapter;
import soot.dava.toolkits.base.AST.traversals.ASTParentNodeFinder;

/*
 * if (true)   ---> remove conditional copy ifbody to parent
 * 
 * if(false) eliminate in all entirety
 * 
 * if(true)
 *    bla1
 * else 
 *    bla2        remove conditional copy bla1 to parent
 *    
 * if(false)
 *    bla1
 *  else
 *    bla2      remoce conditional copy bla2 to parent
 *    
 *       
 * while(false)  eliminate in entirety... notice this is not an Uncondition loop but a ASTWhileNode
 * 
 * do{ .... } while(false)  eliminate loop copy body to parent
 * 
 * for(int i =0;false;i++)   remove for .  copy init stmts to parent
 */
public class EliminateConditions extends DepthFirstAdapter {

	public static boolean DEBUG=false;
	public boolean modified=false;
	
	ASTParentNodeFinder finder;
	ASTMethodNode AST;
	List bodyContainingNode=null;
	
	
	public EliminateConditions(ASTMethodNode AST) {
		super();
		finder = new ASTParentNodeFinder();
		this.AST=AST;
	}

	
	public EliminateConditions(boolean verbose,ASTMethodNode AST) {
		super(verbose);
		finder = new ASTParentNodeFinder();
		this.AST=AST;
	}
	
	
	
	public void normalRetrieving(ASTNode node){
		modified=false;
		if(node instanceof ASTSwitchNode){
			do{
				modified=false;
				dealWithSwitchNode((ASTSwitchNode)node);
			}while(modified);				   
			return;
		}
		//from the Node get the subBodes
		Iterator sbit = node.get_SubBodies().iterator();
		while (sbit.hasNext()) {
		    List subBody = (List)sbit.next();
		    Iterator it = subBody.iterator();
		    ASTNode temp=null;
		    Boolean returned=null;
		    while (it.hasNext()){
		    	temp = (ASTNode) it.next();
		    	
		    	//only check condition if this is a control flow node
		    	if(temp instanceof ASTControlFlowNode){ 
		    		bodyContainingNode=null;
		    		returned = eliminate(temp);
		    		if(returned!=null && canChange(returned,temp)){
		    			break;
		    		}
		    		else{
		    			if(DEBUG)
		    				System.out.println("returned is null"+temp.getClass());
		    			bodyContainingNode=null;
		    		}
		    	}
		    	temp.apply(this);				    	
		    }//end while going through nodes in subBody
		    		    
		    boolean changed = change(returned,temp);
		    if(changed)
		    	modified=true;		    	
		}//end of going over subBodies
		
		if(modified){
			//repeat the whole thing
			normalRetrieving(node);
		}
	}

    
	
	public Boolean eliminate(ASTNode node){
		ASTCondition cond=null;
		if(node instanceof ASTControlFlowNode)
			cond = ((ASTControlFlowNode)node).get_Condition();
		else
			return null;
		
		if( cond == null || !(cond instanceof ASTUnaryCondition))
			return null;
		
		ASTUnaryCondition unary = (ASTUnaryCondition)cond;
		Value unaryValue = unary.getValue();
	
		boolean notted=false;
		if(unaryValue instanceof DNotExpr){
			notted=true;
			unaryValue= ((DNotExpr)unaryValue).getOp();
		}
		
		Boolean isBoolean = isBooleanConstant(unaryValue);
		if(isBoolean == null){
			//not a constant
			return null;
		}
		
		boolean trueOrFalse = isBoolean.booleanValue();
		if(notted){
			//since it is notted we reverse the booleans
			trueOrFalse= !trueOrFalse;
		}
	
		AST.apply(finder);
	
		Object temp = finder.getParentOf(node);
		if(temp == null)
			return null;
			
		ASTNode parent = (ASTNode)temp;
		List subBodies = parent.get_SubBodies();
		Iterator it = subBodies.iterator();
			

		int index=-1;
		while(it.hasNext()){
			bodyContainingNode = (List)it.next();
			index = bodyContainingNode.indexOf(node);
			if(index<0){
				bodyContainingNode=null;
			}
			else{
				//bound the body containing Node
				return new Boolean(trueOrFalse);
			}			
		}
		return null;

	}
	
		
		

	
	
	
	
    /*
     * Method returns null if the Value is not a constant or not a boolean constant
     * return true if the constant is true
     * return false if the constant is false
     */
    public Boolean isBooleanConstant(Value internal){
    	
    	if(! (internal instanceof DIntConstant))
    		return null;
				
    	if(DEBUG)
    		System.out.println("Found Constant");
    	
    	DIntConstant intConst = (DIntConstant)internal;
    		
    	if(! (intConst.type instanceof BooleanType) )
    		return null;
    				
    	//either true or false
    	if(DEBUG)
    		System.out.println("Found Boolean Constant");

    	if(intConst.value == 1){
    		return new Boolean(true);
    	}
    	else if(intConst.value == 0){
    		return new Boolean(false);
    	}
    	else
    		throw new RuntimeException("BooleanType found with value different than 0 or 1");
    }


    
    
    
    
    public Boolean eliminateForTry(ASTNode node){
    	ASTCondition cond=null;
    	if(node instanceof ASTControlFlowNode)
    		cond = ((ASTControlFlowNode)node).get_Condition();
    	else
    		return null;
    		
    	if( cond == null || !(cond instanceof ASTUnaryCondition))
    		return null;
    		
    	ASTUnaryCondition unary = (ASTUnaryCondition)cond;
    	Value unaryValue = unary.getValue();
    	
    	boolean notted=false;
    	if(unaryValue instanceof DNotExpr){
    		notted=true;
    		unaryValue= ((DNotExpr)unaryValue).getOp();
    	}
    		
    	Boolean isBoolean = isBooleanConstant(unaryValue);
    	if(isBoolean == null){
    		//not a constant
    		return null;
    	}
    		
    	boolean trueOrFalse = isBoolean.booleanValue();
    	if(notted){
    		//since it is notted we reverse the booleans
    		trueOrFalse= !trueOrFalse;
    	}
    	
    	AST.apply(finder);
    		
    	Object temp = finder.getParentOf(node);
    	if(temp == null)
    		return null;
    			
    	if(!(temp instanceof ASTTryNode ))
    		throw new RuntimeException("eliminateTry called when parent was not a try node");
    		
    	ASTTryNode parent = (ASTTryNode)temp;
    		
    	List tryBody = parent.get_TryBody();
    		
    	int index = tryBody.indexOf(node);
    	if(index>=0){
    		//bound the body containing Node
    		bodyContainingNode=tryBody;
    		return new Boolean(trueOrFalse);
    	}
    			
    	List catchList = parent.get_CatchList();
    	Iterator it = catchList.iterator();
    	while (it.hasNext()) {
    		ASTTryNode.container catchBody = (ASTTryNode.container)it.next();
    		
    		List body = (List)catchBody.o;
    		index = body.indexOf(node);
    		if(index>=0){
    			//bound the body containing Node
    			bodyContainingNode=body;
    			return new Boolean(trueOrFalse);
    		}
				
    	}
    	return null;
    }
    
    
    
    
    
    
    
    
    public void caseASTTryNode(ASTTryNode node){
		modified=false;
    	inASTTryNode(node);
    	//get try body iterator 
    	Iterator it = node.get_TryBody().iterator();
    		
    	Boolean returned=null;
    	ASTNode temp=null;
    	while (it.hasNext()){
    		temp = (ASTNode) it.next();    		
    		//only check condition if this is a control flow node
    		if(temp instanceof ASTControlFlowNode){ 	
    			bodyContainingNode=null;
    			returned = eliminateForTry(temp);
    			if(returned!=null && canChange(returned,temp))
    				break;
    			else
    				bodyContainingNode=null;
    		}
    		temp.apply(this);
    	}//end while

    	boolean	changed = change(returned, temp);
    	if(changed)
    		modified=true;    		
    
    	//get catch list and apply on the following
    	// a, type of exception caught  ......... NO NEED
    	// b, local of exception ............... NO NEED
    	// c, catchBody
    	List catchList = node.get_CatchList();
    	Iterator itBody=null;
    	it = catchList.iterator();
    	while (it.hasNext()) {
    		ASTTryNode.container catchBody = (ASTTryNode.container)it.next();
    		List body = (List)catchBody.o;
    		itBody = body.iterator();
    		
    		returned=null;
    		temp=null;
    		//go over the ASTNodes and apply
    		while (itBody.hasNext()){
    			temp = (ASTNode) itBody.next();
    			//System.out.println("Next node is "+temp);
    			//only check condition if this is a control flow node
    			if(temp instanceof ASTControlFlowNode){ 		
    				bodyContainingNode=null;
    				returned = eliminateForTry(temp);
    				if(returned!=null && canChange(returned,temp))
    					break;
    				else
    					bodyContainingNode=null;
    			}	
    			temp.apply(this);
    		}
    		changed = change(returned, temp);
    		if(changed)
    			modified=true;
    	}
    	outASTTryNode(node);
    	if(modified){
    		//repeat the whole thing
    		caseASTTryNode(node);
    	}
    }

    

    public boolean canChange(Boolean returned, ASTNode temp){
    	return true;
    }
    
    
    public boolean change(Boolean returned, ASTNode temp){
    	if(bodyContainingNode!=null && returned!=null && temp!=null){

    		int index = bodyContainingNode.indexOf(temp);
			if(DEBUG) System.out.println("in change");
    		if(temp instanceof ASTIfNode ){
    			bodyContainingNode.remove(temp);
        		
    			if (returned.booleanValue()){
    				//if statement and value was true put the body of if into the code
    				//if its a labeled stmt we need a labeled block instead
    				//notice that its okkay to put a labeled block since other transformations might remove it
    				String label = ((ASTLabeledNode)temp).get_Label().toString();
    				if(label != null){
    					ASTLabeledBlockNode labeledNode = new ASTLabeledBlockNode( ((ASTLabeledNode)temp).get_Label(), (List)temp.get_SubBodies().get(0)  );
    					bodyContainingNode.add(index,labeledNode);
    				}
    				else{ 	   				
    					bodyContainingNode.addAll(index,(List)temp.get_SubBodies().get(0));
    				}
    			}
    			if(DEBUG) System.out.println("Removed if"+temp);
    			return true;
    		}
    		else if(temp instanceof ASTIfElseNode){
    			bodyContainingNode.remove(temp);
    			if(returned.booleanValue()){
    				//true so the if branch's body has to be added
    				//if its a labeled stmt we need a labeled block instead
    				//notice that its okkay to put a labeled block since other transformations might remove it
    				String label = ((ASTLabeledNode)temp).get_Label().toString();
    				if(label != null){
    					ASTLabeledBlockNode labeledNode = new ASTLabeledBlockNode( ((ASTLabeledNode)temp).get_Label(), (List)temp.get_SubBodies().get(0)  );
    					bodyContainingNode.add(index,labeledNode);
    				}
    				else{ 	   				
    					bodyContainingNode.addAll(index,(List)temp.get_SubBodies().get(0));
    				}
    			}
    			else{
    				//if its a labeled stmt we need a labeled block instead
    				//notice that its okkay to put a labeled block since other transformations might remove it
    				String label = ((ASTLabeledNode)temp).get_Label().toString();
    				if(label != null){
    					ASTLabeledBlockNode labeledNode = new ASTLabeledBlockNode( ((ASTLabeledNode)temp).get_Label(), (List)temp.get_SubBodies().get(1)  );
    					bodyContainingNode.add(index,labeledNode);
    				}
    				else{ 	   				
    					bodyContainingNode.addAll(index,(List)temp.get_SubBodies().get(1));
    				}
    			}
    			return true;
    		}
    		else if(temp instanceof ASTWhileNode && returned.booleanValue()==false){
    			//notice we only remove if ASTWhileNode has false condition
    			bodyContainingNode.remove(temp);
    			return true;
    		}
    		else if(temp instanceof ASTDoWhileNode && returned.booleanValue()==false){
    			//System.out.println("in try dowhile false");
    			//remove the loop copy the body out since it gets executed once
    			bodyContainingNode.remove(temp);
    			bodyContainingNode.addAll(index,(List)temp.get_SubBodies().get(0));
    			return true;
    		}
    		else if(temp instanceof ASTForLoopNode && returned.booleanValue()==false){
    			bodyContainingNode.remove(temp);
    			ASTStatementSequenceNode newNode = new ASTStatementSequenceNode(((ASTForLoopNode)temp).getInit());
    			bodyContainingNode.add(index,newNode);			    			
    			return true;
    		}
    	}
    	return false;
    }

	
	

    
    
    
    public void dealWithSwitchNode(ASTSwitchNode node){
		List indexList = node.getIndexList();
		Map index2BodyList = node.getIndex2BodyList();

		Iterator it = indexList.iterator();
		while (it.hasNext()) {
			// going through all the cases of the switch statement
		    Object currentIndex = it.next();
		    List body = (List) index2BodyList.get( currentIndex);
		    
		    if (body != null){
			// this body is a list of ASTNodes

		    	Iterator itBody = body.iterator();
		    	Boolean returned=null;
		    	ASTNode temp = null;
		    	while (itBody.hasNext()){
		    		temp = (ASTNode) itBody.next();
		    		if(temp instanceof ASTControlFlowNode){ 			
		    			bodyContainingNode=null;
		    			returned = eliminate(temp);
		    			if(returned!=null && canChange(returned,temp))
		    				break;
		    			else
		    				bodyContainingNode=null;
		    		}					    
		    		temp.apply(this);
		    	}
		    	boolean changed=change(returned,temp);
		    	if(changed)
		    		modified=true;
		    }//end while changed
		}
    }
}
	
	
	




