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
import soot.dava.internal.AST.*;


/*
  Nomair A. Naeem 21-FEB-2005

*/

public class EmptyElseRemover{

    public static void removeElseBody(ASTNode node,ASTIfElseNode ifElseNode ,int subBodyNumber, int nodeNumber){
	if(!(node instanceof ASTIfElseNode)){
	    //these are the nodes which always have one subBody
	    List<Object> subBodies = node.get_SubBodies();
	    if(subBodies.size()!=1){
		//there is something wrong
		throw new RuntimeException("Please report this benchmark to the programmer");
	    }
	    List<Object> onlySubBody = (List<Object>)subBodies.get(0);

	    /*
	      The onlySubBody contains the ASTIfElseNode whose elsebody has to be removed
	      at location given by the nodeNumber variable
	    */
	    List<Object> newBody = createNewNodeBody(onlySubBody,nodeNumber,ifElseNode);
	    if(newBody==null){
		//something went wrong
		return;
	    }
	    if(node instanceof ASTMethodNode){
		((ASTMethodNode)node).replaceBody(newBody);
		G.v().ASTTransformations_modified = true;
		//System.out.println("REMOVED ELSE BODY");
	    }
	    else if(node instanceof ASTSynchronizedBlockNode){
		((ASTSynchronizedBlockNode)node).replaceBody(newBody);
		G.v().ASTTransformations_modified = true;
		//System.out.println("REMOVED ELSE BODY");
	    }
	    else if(node instanceof ASTLabeledBlockNode){
		((ASTLabeledBlockNode)node).replaceBody(newBody);
		G.v().ASTTransformations_modified = true;
		//System.out.println("REMOVED ELSE BODY");
	    }
	    else if(node instanceof ASTUnconditionalLoopNode){
		((ASTUnconditionalLoopNode)node).replaceBody(newBody);
		G.v().ASTTransformations_modified = true;
		//System.out.println("REMOVED ELSE BODY");
	    }
	    else if(node instanceof ASTIfNode){
		((ASTIfNode)node).replaceBody(newBody);
		G.v().ASTTransformations_modified = true;
		//System.out.println("REMOVED ELSE BODY");
	    }
	    else if(node instanceof ASTWhileNode){
		((ASTWhileNode)node).replaceBody(newBody);
		G.v().ASTTransformations_modified = true;
		//System.out.println("REMOVED ELSE BODY");
	    }
	    else if(node instanceof ASTDoWhileNode){
		((ASTDoWhileNode)node).replaceBody(newBody);
		G.v().ASTTransformations_modified = true;
		//System.out.println("REMOVED ELSE BODY");
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
	      The toModifySubBody contains the ASTIfElseNode to be removed
	      at location given by the nodeNumber variable
	    */
	    List<Object> newBody = createNewNodeBody(toModifySubBody,nodeNumber,ifElseNode);
	    if(newBody==null){
		//something went wrong
		return;
	    }
	    if(subBodyNumber==0){
		//the if body was modified
		//System.out.println("REMOVED ELSE BODY");
		G.v().ASTTransformations_modified = true;
		((ASTIfElseNode)node).replaceBody(newBody,(List<Object>)subBodies.get(1));
	    }
	    else if(subBodyNumber==1){
		//else body was modified
		//System.out.println("REMOVED ELSE BODY");
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






    public static List<Object> createNewNodeBody(List<Object> oldSubBody,int nodeNumber,ASTIfElseNode ifElseNode){
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

	//at this point the iterator is pointing to the ASTIfElseNode to be removed
	//just to make sure check this
	ASTNode toRemove = (ASTNode)it.next();
	if(!(toRemove instanceof ASTIfElseNode)){
	    //something is wrong 
	    return null;
	}
	else{
	    ASTIfElseNode toRemoveNode = (ASTIfElseNode)toRemove;

	    //just double checking that this is a empty else node
	    List<Object> elseBody = toRemoveNode.getElseBody();
	    if(elseBody.size()!=0){
		//something is wrong we cant remove a non empty elsebody
		return null;
	    }
	    
	    //so this is the ElseBody to remove

	    //need to create an ASTIfNode from the ASTIfElseNode
	    ASTIfNode newNode = new ASTIfNode(toRemoveNode.get_Label(),toRemoveNode.get_Condition(),toRemoveNode.getIfBody());

	    //add this node to the newSubBody
	    newSubBody.add(newNode);
	}

	//add any remaining nodes in the oldSubBody to the new one
	while(it.hasNext()){
	    newSubBody.add(it.next());
	}

	//newSubBody is ready return it
	return newSubBody;
    }
}