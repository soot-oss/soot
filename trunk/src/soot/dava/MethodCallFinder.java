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

/**
 * This class is created by the DavaStaticBlockCleaner classes method staticBlockInlining
 * It is only invoked for the bodies of clinit methods
 */

package soot.dava;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.dava.internal.AST.ASTDoWhileNode;
import soot.dava.internal.AST.ASTForLoopNode;
import soot.dava.internal.AST.ASTIfElseNode;
import soot.dava.internal.AST.ASTIfNode;
import soot.dava.internal.AST.ASTLabeledBlockNode;
import soot.dava.internal.AST.ASTMethodNode;
import soot.dava.internal.AST.ASTNode;
import soot.dava.internal.AST.ASTStatementSequenceNode;
import soot.dava.internal.AST.ASTSwitchNode;
import soot.dava.internal.AST.ASTSynchronizedBlockNode;
import soot.dava.internal.AST.ASTTryNode;
import soot.dava.internal.AST.ASTUnconditionalLoopNode;
import soot.dava.internal.AST.ASTWhileNode;
import soot.dava.internal.asg.AugmentedStmt;
import soot.dava.toolkits.base.AST.analysis.DepthFirstAdapter;
import soot.dava.toolkits.base.AST.traversals.ASTParentNodeFinder;
import soot.grimp.internal.GNewInvokeExpr;
import soot.grimp.internal.GThrowStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.Stmt;
import soot.jimple.StringConstant;

public class MethodCallFinder extends DepthFirstAdapter{
    ASTMethodNode underAnalysis;

    DavaStaticBlockCleaner cleaner;

    public MethodCallFinder(DavaStaticBlockCleaner cleaner){
    	this.cleaner=cleaner;
    	underAnalysis=null;
    }

    public MethodCallFinder(boolean verbose,DavaStaticBlockCleaner cleaner){
    	super(verbose);
    	this.cleaner=cleaner;
    	underAnalysis=null;
    }

    public void inASTMethodNode(ASTMethodNode node){
    	underAnalysis=node;
    }

    /*
     * some ASTConstuct{                                       ASTConstruct{
     *    Some bodies                                                Some Bodies
     *    Statement SequenceNode                                     New Stmt seq node with some stmts
     *        some stmts                       ---------->           Body of method to inline
     *        the invoke stmt                                        New Stmt seq node with other stmts
     *        some other stmts                                       Some other bodies
     *    Some other bodies                                     End ASTConstruct
     * End ASTConstruct
     */


    /*
     * Notice that since this class is only invoked for clinit methods this invoke statement is some
     * invocation that occured within the clinit method
     */
    public void inInvokeStmt(InvokeStmt s){
    	InvokeExpr invokeExpr = s.getInvokeExpr();
    	SootMethod maybeInline = invokeExpr.getMethod();

    	//check whether we want to inline
    	ASTMethodNode toInlineASTMethod = cleaner.inline(maybeInline);
    	if(toInlineASTMethod ==null){
    		//not to inline
    		return;
    	}
    	else{//yes we want to inline 
    		// we know that the method to be inlined has no declarations.
    		List<Object> subBodies = toInlineASTMethod.get_SubBodies();
    		if(subBodies.size() != 1){
    			throw new RuntimeException ("Found ASTMEthod node with more than one subBodies");
    		}
    		List body = (List)subBodies.get(0);

	    
    		ASTParentNodeFinder finder = new ASTParentNodeFinder();
    		underAnalysis.apply(finder);
	    
    		List<ASTStatementSequenceNode> newChangedBodyPart = createChangedBodyPart(s,body,finder);


    		boolean replaced = replaceSubBody(s,newChangedBodyPart,finder);

	    
    		if(replaced){
    			//so the invoke stmt has been replaced with the body of the method invoked

    			/*
    			 * if the inlined method contained an assignment to a static field
    			 * we want to replace that with a throw stmt
    			 */
    			StaticDefinitionFinder defFinder = new StaticDefinitionFinder(maybeInline);
    			toInlineASTMethod.apply(defFinder);
    			
    			if(defFinder.anyFinalFieldDefined()){
    				//create throw stmt to be added to inlined method

    				//create a SootMethodRef
    				SootClass runtime = Scene.v().loadClassAndSupport("java.lang.RuntimeException");
    				if(runtime.declaresMethod("void <init>(java.lang.String)")){
			SootMethod sootMethod = runtime.getMethod("void <init>(java.lang.String)");
			SootMethodRef methodRef = sootMethod.makeRef();
			RefType myRefType = RefType.v(runtime);
			StringConstant tempString = StringConstant.v("This method used to have a definition of a final variable. "+
								     "Dava inlined the definition into the static initializer");
			List list = new ArrayList();
			list.add(tempString);
			
			GNewInvokeExpr newInvokeExpr = new GNewInvokeExpr(myRefType,methodRef,list);

			GThrowStmt throwStmt = new GThrowStmt(newInvokeExpr);
						
			AugmentedStmt augStmt = new AugmentedStmt(throwStmt);
			List<Object> sequence = new ArrayList<Object>();
			sequence.add(augStmt);
			ASTStatementSequenceNode seqNode = new ASTStatementSequenceNode(sequence);
			List<Object> subBody = new ArrayList<Object>();
			subBody.add(seqNode);

			toInlineASTMethod.replaceBody(subBody);
		    }
		}
	    }

	}
    }

    public List<Object> getSubBodyFromSingleSubBodyNode(ASTNode node){
    	List<Object> subBodies = node.get_SubBodies();
    	if(subBodies.size() != 1)
    		throw new RuntimeException("Found a single subBody node with more than 1 subBodies");

    	return (List<Object>)subBodies.get(0);
    }


    public List<Object> createNewSubBody(List<Object> orignalBody, List<ASTStatementSequenceNode> partNewBody,Object stmtSeqNode){

	List<Object> newBody = new ArrayList<Object>();

	Iterator<Object> it = orignalBody.iterator();
	while(it.hasNext()){
	    Object temp = it.next();
	    if(temp != stmtSeqNode)
		newBody.add(temp);
	    else{
		//breaks out of the loop as soon as stmtSeqNode is reached
		break;
	    }
	}
	//dont add this stmt sequence node instead add the modified stmt seq nodes and the to be inline method
	newBody.addAll(partNewBody);

	//add remaining stuff drom the orignalBody
	while(it.hasNext()){
	    newBody.add(it.next());
	}
	
	return newBody;
    }


    public boolean replaceSubBody(InvokeStmt s, List<ASTStatementSequenceNode> newChangedBodyPart,ASTParentNodeFinder finder){

	//get the stmt seq node of invoke stmt
	Object stmtSeqNode = finder.getParentOf(s);
	
	// find the parent node of the stmt seq node
	Object ParentOfStmtSeq = finder.getParentOf(stmtSeqNode);
	
	if(ParentOfStmtSeq ==null){
	    throw new RuntimeException ("MethodCall FInder: parent of stmt seq node not found");
	}
	
	ASTNode node = (ASTNode)ParentOfStmtSeq;

	//the decision what to replace and how to replace depends on the type of ASTNode
	
	if(node instanceof ASTMethodNode){
	    //get the subBody to replace
	    List<Object> subBodyToReplace = getSubBodyFromSingleSubBodyNode(node);
	    List<Object> newBody = createNewSubBody(subBodyToReplace,newChangedBodyPart,stmtSeqNode);
	    ((ASTMethodNode)node).replaceBody(newBody);
	    return true;
	}
	else if(node instanceof ASTSynchronizedBlockNode){
	    //get the subBody to replace
	    List<Object> subBodyToReplace = getSubBodyFromSingleSubBodyNode(node);
	    List<Object> newBody = createNewSubBody(subBodyToReplace,newChangedBodyPart,stmtSeqNode);
	    ((ASTSynchronizedBlockNode)node).replaceBody(newBody);
	    return true;
	}
	else if(node instanceof ASTLabeledBlockNode){
	    //get the subBody to replace
	    List<Object> subBodyToReplace = getSubBodyFromSingleSubBodyNode(node);
	    List<Object> newBody = createNewSubBody(subBodyToReplace,newChangedBodyPart,stmtSeqNode);
	    ((ASTLabeledBlockNode)node).replaceBody(newBody);
	    return true;
	}
	else if(node instanceof ASTUnconditionalLoopNode){
	    //get the subBody to replace
	    List<Object> subBodyToReplace = getSubBodyFromSingleSubBodyNode(node);
	    List<Object> newBody = createNewSubBody(subBodyToReplace,newChangedBodyPart,stmtSeqNode);
	    ((ASTUnconditionalLoopNode)node).replaceBody(newBody);
	    return true;
	}
	else if(node instanceof ASTIfNode){
	    //get the subBody to replace
	    List<Object> subBodyToReplace = getSubBodyFromSingleSubBodyNode(node);
	    List<Object> newBody = createNewSubBody(subBodyToReplace,newChangedBodyPart,stmtSeqNode);
	    ((ASTIfNode)node).replaceBody(newBody);
	    return true;
	}
	else if(node instanceof ASTWhileNode){
	    //get the subBody to replace
	    List<Object> subBodyToReplace = getSubBodyFromSingleSubBodyNode(node);
	    List<Object> newBody = createNewSubBody(subBodyToReplace,newChangedBodyPart,stmtSeqNode);
	    ((ASTWhileNode)node).replaceBody(newBody);
	    return true;
	}
	else if(node instanceof ASTDoWhileNode){
	    //get the subBody to replace
	    List<Object> subBodyToReplace = getSubBodyFromSingleSubBodyNode(node);
	    List<Object> newBody = createNewSubBody(subBodyToReplace,newChangedBodyPart,stmtSeqNode);
	    ((ASTDoWhileNode)node).replaceBody(newBody);
	    return true;
	}
	else if(node instanceof ASTForLoopNode){
	    //get the subBody to replace
	    List<Object> subBodyToReplace = getSubBodyFromSingleSubBodyNode(node);
	    List<Object> newBody = createNewSubBody(subBodyToReplace,newChangedBodyPart,stmtSeqNode);
	    ((ASTForLoopNode)node).replaceBody(newBody);
	    return true;
	}
	else if(node instanceof ASTIfElseNode){
	    List<Object> subBodies = node.get_SubBodies();
	    if(subBodies.size() != 2)
		throw new RuntimeException("Found an ifelse ASTNode which does not have two bodies");
	    List<Object> ifBody = (List<Object>)subBodies.get(0);
	    List<Object> elseBody = (List<Object>)subBodies.get(1);

	    //find out which of these bodies has the stmt seq node with the invoke stmt
	    int subBodyNumber=-1;
	    Iterator<Object> it = ifBody.iterator();
	    while(it.hasNext()){
		Object temp = it.next();
		if(temp == stmtSeqNode){
		    subBodyNumber=0;
		    break;
		}
	    }
	    if(subBodyNumber!= 0){
		it = elseBody.iterator();
		while(it.hasNext()){
		    Object temp = it.next();
		    if(temp == stmtSeqNode){
			subBodyNumber=1;
			break;
		    }
		}
	    }
	    
	    List<Object> subBodyToReplace = null;
	    if(subBodyNumber==0)
		subBodyToReplace=ifBody;
	    else if (subBodyNumber==1)
		subBodyToReplace=elseBody;
	    else
		throw new RuntimeException("Could not find the related ASTNode in the method");
		
	    List<Object> newBody = createNewSubBody(subBodyToReplace,newChangedBodyPart,stmtSeqNode);

	    if(subBodyNumber==0){
		((ASTIfElseNode)node).replaceBody(newBody,elseBody);
		return true;
	    }
	    else if(subBodyNumber==1){
		((ASTIfElseNode)node).replaceBody(ifBody,newBody);
		return true;
	    }
	}
	else if(node instanceof ASTTryNode){

	    //NOTE THAT method INLINING Is currently only done in the tryBody and not the catchBody
	    //THe only reason for this being that mostly method calls are made in the try and not the catch
				    
	    //get try body 
	    List<Object> tryBody = ((ASTTryNode)node).get_TryBody();
	    Iterator<Object> it = tryBody.iterator();
		
	    //find whether stmtSeqNode is in the tryBody
	    boolean inTryBody = false;
	    while (it.hasNext()){
		ASTNode temp = (ASTNode) it.next();
		if(temp == stmtSeqNode){
		    inTryBody=true;
		    break;
		}
	    }
	    if(!inTryBody){
		//return without making any changes
		return false;
	    }
	    
	    
	    List<Object> newBody = createNewSubBody(tryBody,newChangedBodyPart,stmtSeqNode);
	    ((ASTTryNode)node).replaceTryBody(newBody);
	    return true;
	}
	else if (node instanceof ASTSwitchNode){

	    List<Object> indexList = ((ASTSwitchNode)node).getIndexList();
	    Map<Object, List<Object>> index2BodyList = ((ASTSwitchNode)node).getIndex2BodyList();
				    
	    Iterator<Object> it = indexList.iterator();
	    while (it.hasNext()) {//going through all the cases of the switch statement
		Object currentIndex = it.next();
		List<Object> body = index2BodyList.get( currentIndex);
					
		if (body != null){
		    //this body is a list of ASTNodes 

		    //see if it contains stmtSeqNode
		    boolean found=false;
		    Iterator<Object> itBody = body.iterator();
		    while (itBody.hasNext()){
			ASTNode temp = (ASTNode) itBody.next();
			if(temp == stmtSeqNode){
			    found=true;
			    break;
			}
		    }
		    if(found){
			//this is the body which has the stmt seq node
			List<Object> newBody = createNewSubBody(body,newChangedBodyPart,stmtSeqNode);

			//put this body in the Map
			index2BodyList.put(currentIndex,newBody);
			//replace in actual switchNode
			((ASTSwitchNode)node).replaceIndex2BodyList(index2BodyList);
			return true;
		    }
		}//if body not null
	    }//going through all cases
	}
	return false;
    }



    /*
     * Given an invoke stmt this method finds the parent of this stmt which should always be a StatementSequenceNode
     * Then the sequence is broken into three parts.
     * The first part contains stmts till above the invoke stmt. The second part contains the body argument which is the
     * body of the inlined method and the third part are the stmts below the invoke stmt
     */
    
    public List<ASTStatementSequenceNode> createChangedBodyPart(InvokeStmt s, List body, ASTParentNodeFinder finder){
	//get parent node of invoke stmt
	Object parent = finder.getParentOf(s);
	if(parent == null){
	    throw new RuntimeException ("MethodCall FInder: parent of invoke stmt not found");
	}
	
	ASTNode parentNode = (ASTNode)parent;
	if(!(parentNode instanceof ASTStatementSequenceNode)){
	    throw new RuntimeException ("MethodCall FInder: parent node not a stmt seq node");
	}    
	
	ASTStatementSequenceNode orignal = (ASTStatementSequenceNode)parentNode;


	//copying the stmts till above the inoke stmt into one stmt sequence node
	List<Object> newInitialNode = new ArrayList<Object>();
	Iterator<Object> it = orignal.getStatements().iterator();
	while(it.hasNext()){
	    AugmentedStmt as = (AugmentedStmt)it.next();
	    Stmt tempStmt = as.get_Stmt();
	    if(tempStmt != s){
	    	newInitialNode.add(as);
	    }
	    else{
	    	//the first time we get to a stmt which points to the invoke stmt we break
	    	break;
	    }
	}
	
	//copy remaining stmts into the AFTER stmt sequence node
	List<Object> newSecondNode = new ArrayList<Object>();
	while(it.hasNext()){
	    newSecondNode.add(it.next());
	}
	
	List<ASTStatementSequenceNode> toReturn = new ArrayList<ASTStatementSequenceNode>();

	if(newInitialNode.size()!=0)
	    toReturn.add(new ASTStatementSequenceNode(newInitialNode));
	
	//add inline methods body
	toReturn.addAll(body);
	
	if(newSecondNode.size()!=0)
	    toReturn.add(new ASTStatementSequenceNode(newSecondNode));
	
	return toReturn;
    }	

		

}