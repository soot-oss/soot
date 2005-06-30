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


package soot.dava.toolkits.base.AST.structuredAnalysis;

import java.util.*;
import soot.jimple.*;
import soot.*;
import soot.dava.internal.AST.*;
import soot.dava.internal.asg.*;
import soot.dava.internal.javaRep.*;


/*
 * This class is meant to be extended to write structred analyses.
 * The analysis is invoked by calling the process method.
 */
public abstract class StructuredAnalysis{
    final DavaFlowSet NOPATH = new DavaFlowSet();
    int MERGETYPE;

    final int UNION=1;
    final int INTERSECTION=2;


    HashMap beforeSets,afterSets;

    public StructuredAnalysis(){
	beforeSets = new HashMap();
	afterSets = new HashMap();
	MERGETYPE=0;
	setMergeType();
    }

    /*
     * This method should be used to set the variable MERGETYPE
     * use StructuredAnalysis.UNION for union
     * use StructuredAnalysis.INTERSECTION for intersection
     */
    public abstract void setMergeType();

    /*
     * Returns the flow object corresponding to the initial values for
     * the catch statements
     */
    public abstract Object newInitialFlow();

    public abstract Object cloneFlowSet(Object flowSet);

    public abstract Object processStatement(Stmt s, Object input);

    public abstract Object processUnaryBinaryCondition(ASTUnaryBinaryCondition cond,Object input);

    public abstract Object processSynchronizedLocal(Local local,Object input);

    public abstract Object processSwitchKey(Value key,Object input);

    public void print(Object toPrint){
	System.out.println(toPrint.toString());
    }


    public Object processCondition(ASTCondition cond,Object input){
	if(cond instanceof ASTUnaryBinaryCondition){
	    return processUnaryBinaryCondition((ASTUnaryBinaryCondition)cond,input);
	}
	else if (cond instanceof ASTAggregatedCondition){
	    ASTCondition left = ((ASTAggregatedCondition)cond).getLeftOp();
	    Object output1 = processCondition(left,input);

	    ASTCondition right = ((ASTAggregatedCondition)cond).getRightOp();
	    Object output2 = processCondition(right,output1);

	    return merge(output1,output2);
	}
	else{
	    throw new RuntimeException("Unknown ASTCondition found in structred flow analysis");
	}
    }




    /*     
     * The parameter body contains the body to be analysed
     * The in is any data that is gathered plus any info needed for making
     * decisions during the analysis
     */
    
    public Object process(Object body, Object input){
	if(!(input instanceof DavaFlowSet))
	    System.out.println("not DavaFlowSet"+body);

	if(body instanceof ASTNode){
	    beforeSets.put(body,input);
	    Object temp=processASTNode((ASTNode)body,input);
	    afterSets.put(body,temp);
	    return temp;
	}
	else if(body instanceof Stmt){
	    beforeSets.put(body,input);
	    Object result=processStatement((Stmt)body,input);
	    afterSets.put(body,result);
	    return result;
	}
	else if (body instanceof AugmentedStmt){
	    AugmentedStmt as = (AugmentedStmt)body;
	    Stmt s = as.get_Stmt();

	    beforeSets.put(s,input);
	    Object result=processStatement(s,input);
	    afterSets.put(s,result);
	    return result;

	}
	else if (body instanceof List){
	    //this should always be a list of ASTNodes
	    Iterator it = ((List)body).iterator();
	    Object result=input;
	    while(it.hasNext()){
		Object temp = it.next();
		if(temp instanceof ASTNode){
		    /*
		      As we are simply going through a list of ASTNodes
		      The output of the previous becomes the input of the next
		    */
		    beforeSets.put(temp,result);
		    result= processASTNode((ASTNode)temp,result);
		    afterSets.put(temp,result);
		}
		else
		    throw new RuntimeException("Body sent to be processed by "+
					       "StructuredAnalysis contains a list which does not have ASTNodes");
	    }//end of going through list

	    //at this point the result var contains the result of processing the List
	    return result;
	}
	else{
	    throw new RuntimeException("Body sent to be processed by "+
				       "StructuredAnalysis is not a valid body");
	}
    }


    /*
     * This method internally invoked by the process method decides which ASTNode
     * specialized method to call
     */
    public Object processASTNode(ASTNode node, Object input){
	if(node instanceof ASTDoWhileNode){
	    return processASTDoWhileNode((ASTDoWhileNode)node,input);
	}
	else if(node instanceof ASTForLoopNode){
	    return processASTForLoopNode((ASTForLoopNode)node,input);
	}
	else if(node instanceof ASTIfElseNode){
	    return processASTIfElseNode((ASTIfElseNode)node,input);
	}
	else if(node instanceof ASTIfNode){
	    return processASTIfNode((ASTIfNode)node,input);
	}
	else if(node instanceof ASTLabeledBlockNode){
	    return processASTLabeledBlockNode((ASTLabeledBlockNode)node,input);
	}
	else if(node instanceof ASTMethodNode){
	    //System.out.println("processASTNode Case method");
	    Object temp =  processASTMethodNode((ASTMethodNode)node,input);
	    //System.out.println("processASTNode Case method DONE");
	    return temp;
	}
	else if(node instanceof ASTStatementSequenceNode){
	    return processASTStatementSequenceNode((ASTStatementSequenceNode)node,input);
	}
	else if(node instanceof ASTSwitchNode){
	    return processASTSwitchNode((ASTSwitchNode)node,input);
	}
	else if(node instanceof ASTSynchronizedBlockNode){
	    return processASTSynchronizedBlockNode((ASTSynchronizedBlockNode)node,input);
	}
	else if(node instanceof ASTTryNode){
	    return processASTTryNode((ASTTryNode)node,input);
	}
	else if(node instanceof ASTWhileNode){
	    return processASTWhileNode((ASTWhileNode)node,input);
	}
	else if(node instanceof ASTUnconditionalLoopNode){
	    return processASTUnconditionalLoopNode((ASTUnconditionalLoopNode)node,input);
	}
	else{
	    throw new RuntimeException("processASTNode called using unknown node type");
	}
    }



    public final Object processSingleSubBodyNode(ASTNode node, Object input){
	//get the subBodies
	List subBodies = node.get_SubBodies();
	if(subBodies.size()!=1){
	    throw new RuntimeException("processSingleSubBodyNode called with a node without one subBody");
	}
	//we know there is only one
	List subBody = (List)subBodies.get(0);

	return process(subBody,input);
    }


    private String getLabel(ASTNode node){
	if(node instanceof ASTLabeledNode){
	    return ((ASTLabeledNode)node).get_Label().toString();
	}
	else
	    return null;
    }


    public Object processASTWhileNode(ASTWhileNode node,Object input){
	//System.out.println("processing while");
	//print(input);

	Object lastin=null;
	Object initialInput = cloneFlowSet(input);
	
	String label = getLabel(node);
	Object output=null;

	input = processCondition(node.get_Condition(),input);

	do{
	    lastin = cloneFlowSet(input);
	    output = processSingleSubBodyNode(node,input);

	    //handle continue
	    if(label!=null)
		output = handleContinue(label,output);

	    //merge with the initial input
	    input = merge(initialInput,output);
	    input = processCondition(node.get_Condition(),input);
	} while(isDifferent(lastin,input));

	/*
	  CHANGED 21st JUNE 2005
	  from output contains the result of the fixed point to input
	  contains the result of the fixed point

	  NOT TESTED
	*/
	//input contains the result of the fixed point
	//System.out.println("processing while done");
	if(label!=null){
	    //System.out.println("handling break");
	    Object temp= handleBreak(label,input);
	    //System.out.println("handling break done");
	    //print(temp);
	    return temp;
	}
	else{
	    //print(input);
	    return input;
	}
	
    }



    public Object processASTDoWhileNode(ASTDoWhileNode node, Object input){
	//System.out.println("processing do while loop");
	//print(input);

	/*
	  A do while node gets executed atleast once
	*/
	Object output = processSingleSubBodyNode(node,input);

	//we need to handle the info about continues in the body
	String label = getLabel(node);
	if(label!=null)
	    input = handleContinue(label,output);
	else
	    input=output;

	input = processCondition(node.get_Condition(),input);




	/*
	  do a fixed point of the dowhile body
	*/
	Object initialInput = cloneFlowSet(input);

	Object lastin=null;
	do{
	    lastin = cloneFlowSet(input);
	    output = processSingleSubBodyNode(node,input);
	  
	    //handle continues
	    if(label!=null){
		
		//System.out.println("PROCESSING CONTINUE");
		//System.out.println("before handleContinue:"+output.toString());
		output = handleContinue(label,output);
		//System.out.println("after handleContinue:"+output.toString());
	    }


	    output = processCondition(node.get_Condition(),output);

	  	    
	    //need to merge this info with the initial input
	    input = merge(initialInput,output);
	}while(isDifferent(lastin,input));

	//output contains the result of the fixed point
	//System.out.println("processing do while loop done");
	    
	//handle breaks
	if(label!=null){
	    //System.out.println("before handleBreak:"+output.toString());
	    Object temp = handleBreak(label,output);
	    //System.out.println("after handleBreak:"+temp.toString());
	    return temp;
	}
	else
	    return output;
    }








    public Object processASTForLoopNode(ASTForLoopNode node,Object input){
	List init = node.getInit();
	Iterator it = init.iterator();
	while(it.hasNext()){
	    AugmentedStmt as = (AugmentedStmt)it.next();
	    Stmt s = as.get_Stmt();
	    input = process(s,input);
	}
	Object initialInput = cloneFlowSet(input);

	input = processCondition(node.get_Condition(),input);	
	Object lastin = null;
	String label = getLabel(node);
	Object output2=null;
	do{
	    lastin = cloneFlowSet(input);
	    
	    //process body
	    Object output1 = processSingleSubBodyNode(node,input);

	    //handle continues
	    if(label!=null)
		output1 = handleContinue(label,output1);
	    
	    
	    //handle update
	    output2 = cloneFlowSet(output1);//if there is nothing in update

	    List update = node.getUpdate();
	    it = update.iterator();
	    while(it.hasNext()){
		AugmentedStmt as = (AugmentedStmt)it.next();
		Stmt s = as.get_Stmt();
		/*
		  Since we are just going over a list of statements
		  the output of each statement is the input of the next
		*/
		output2 = process(s,output2);
	    }

	    //output2 is the final result
	    
	    //merge this with the input
	    input = merge(initialInput,output2);
	    input = processCondition(node.get_Condition(),input);	

	}while(isDifferent(lastin,input));


	//System.out.println("processing for loop done");
	
	//handle break
	if(label!=null)
	    return handleBreak(label,input);
	else{
	    //changing return here from output2 to input since i 
	    //think that is what should be returned
	    return input;
	}
    }




    public Object processASTIfElseNode(ASTIfElseNode node,Object input){
	//System.out.println("processing ifelse");
	//print("input of ifelse"+input);

	//get the subBodies
	List subBodies = node.get_SubBodies();
	if(subBodies.size()!=2){
	    throw new RuntimeException("processASTIfElseNode called with a node without two subBodies");
	}
	//we know there is only two subBodies
	List subBodyOne = (List)subBodies.get(0);
	List subBodyTwo = (List)subBodies.get(1);


	Object clonedInput = cloneFlowSet(input);
	clonedInput = processCondition(node.get_Condition(),clonedInput);
	Object output1 = process(subBodyOne,clonedInput);


	clonedInput = cloneFlowSet(input);
	clonedInput = processCondition(node.get_Condition(),clonedInput);
	Object output2 = process(subBodyTwo,clonedInput);

	String label = getLabel(node);
	if(label!=null){
	    output1 = handleBreak(label,output1);
	    output2 = handleBreak(label,output2);
	}

	//System.out.println("processing ifelse done");
	Object temp=merge(output1,output2);
	//print("output of ifelse"+temp);
	return temp;
    }




    public Object processASTIfNode(ASTIfNode node,Object input){
	//System.out.println("processing if");
	//print("input of if"+input);

	input = processCondition(node.get_Condition(),input);
	Object output1 = processSingleSubBodyNode(node,input);

	//merge with input which tells if the cond did not evaluate to true
	Object output2 = merge(input,output1);

	//System.out.println("processing if done");
	//handle break
	String label = getLabel(node);
	if(label != null){
	    Object temp= handleBreak(label,output2);
	    //print("output of if"+temp);
	    return temp;
	}
	else{
	    //print("output of if"+output2);
	    return output2;
	}
    }




    public Object processASTLabeledBlockNode(ASTLabeledBlockNode node,Object input){
	//System.out.println("processing labeled block ");
	Object output1 = processSingleSubBodyNode(node,input);
	
	//System.out.println("processing labeled block done");
	//handle break
	String label = getLabel(node);
	if(label!=null)
	    return handleBreak(label,output1);
	else
	    return output1;
    }



    /*
     * Notice Right now the output of the processing of method bodies
     * is returned as the output. This only works for INTRA procedural
     * Analysis. For accomodating INTER procedural analysis one needs
     * to have a return list of all possible returns (stored in the flowset)
     * And merge Returns with the output of normal execution of the body
     */
    public Object processASTMethodNode(ASTMethodNode node,Object input){
	//System.out.println("processing method");
	Object temp = processSingleSubBodyNode(node,input);
	//System.out.println("processing method done");
	return temp;
    }




    public Object processASTStatementSequenceNode(ASTStatementSequenceNode node,Object input){
	//System.out.println("processing stmt seq");
	List statements = node.getStatements();
	Iterator it = statements.iterator();
	
	Object output = cloneFlowSet(input);
	while(it.hasNext()){
	    AugmentedStmt as = (AugmentedStmt)it.next();
	    Stmt s = as.get_Stmt();
	    /*
	      Since we are processing a list of statements the output of
	      previous is input of next
	    */
	    //print("input to stmt"+output);
	    output=process(s,output);
	    //print("output from stmt"+output);
	}
	//System.out.println("processing stmt seq done");
	return output;
    }










    public Object processASTUnconditionalLoopNode(ASTUnconditionalLoopNode node,Object input){
	//System.out.println("processing unconditional loop");

	//an unconditional loop behaves almost like a conditional While loop
	Object initialInput = cloneFlowSet(input);
	Object lastin=null;

	String label = getLabel(node);
	Object output=null;
	do{
	    lastin = cloneFlowSet(input);
	    output = processSingleSubBodyNode(node,input);

	    //handle continue
	    if(label!=null)
		output = handleContinue(label,output);

	    //merge this with the initial input
	    input = merge(initialInput,output);
	} while(isDifferent(lastin,input));

	//System.out.println("processing unconditional loop done");
	    
	if(label!=null)
	    return getMergedBreakList(label,output);
	else{
	    return output;
	}
    }



    public Object processASTSynchronizedBlockNode(ASTSynchronizedBlockNode node,Object input){
	//System.out.println("processing synch block ");
	
	input = processSynchronizedLocal(node.getLocal(),input);

	Object output = processSingleSubBodyNode(node,input);
	String label = getLabel(node);
	//System.out.println("processing synch block done");
	if(label!=null)
	    return handleBreak(label,output);
	else
	    return output;
    }





    public Object processASTSwitchNode(ASTSwitchNode node,Object input){
	//System.out.println("processing switch ");
	List indexList = node.getIndexList();
	Map index2BodyList = node.getIndex2BodyList();

	Iterator it = indexList.iterator();
	

	input=processSwitchKey(node.get_Key(),input);
	Object initialIn = cloneFlowSet(input);

	Object out = null;
	while (it.hasNext()) {//going through all the cases of the switch statement
	    Object currentIndex = it.next();
	    List body = (List) index2BodyList.get( currentIndex);

	    out=process(body,input);

	    //the input to the next can be a fall through or directly input
	    input=merge(out,initialIn);
	}

	//have to handle the case when no case matches. The input is the output
	Object output = merge(initialIn,out);

	//System.out.println("processing switch done");
	//handle break
	String label = getLabel(node);
	if(label!=null)
	    return handleBreak(label,output);
	else
	    return output;
    }





    public Object processASTTryNode(ASTTryNode node,Object input){
	//System.out.println("processing try ");
	List tryBody = node.get_TryBody();

	Object tryBodyOutput = process(tryBody,input);


	/*
	  By default take either top or bottom as the input to the catch statements
	  Which goes in depends on the type of analysis.
	*/
	Object inputCatch = newInitialFlow();

	
	List catchList = node.get_CatchList();
        Iterator it = catchList.iterator();

	List catchOutput = new ArrayList();

	while (it.hasNext()) {
	    ASTTryNode.container catchBody = (ASTTryNode.container)it.next();
	    
	    List body = (List)catchBody.o;
	    //list of ASTNodes

	    //result because of going through the catchBody
	    catchOutput.add(process(body,cloneFlowSet(inputCatch)));
	}
		
	//handle breaks
	String label = getLabel(node);
	Object out = tryBodyOutput;
	if(label!=null)
	    out = handleBreak(label,tryBodyOutput);

	it = catchOutput.iterator();
	while(it.hasNext()){
	    out = merge(out,it.next());
	}
	//System.out.println("processing try done");
	return out;
    }




    /*
      MERGETYPE var has to be set
       0, means no type set
       1, means union
       2, means intersection
    */

    public Object merge(Object obj1, Object obj2){
	if(MERGETYPE==0)
	    throw new RuntimeException("Use the setMergeType method to set the type of merge used in the analysis");

	if(obj1 instanceof DavaFlowSet && obj2 instanceof DavaFlowSet){
	    DavaFlowSet in1 = (DavaFlowSet)obj1;
	    DavaFlowSet in2 = (DavaFlowSet)obj2;

	    DavaFlowSet out = new DavaFlowSet();
	    if(in1 == NOPATH && in2 != NOPATH){
		out = (DavaFlowSet)in2.clone();
		out.copyInternalDataFrom(in1);
		return out;
	    }
	    else if(in1 != NOPATH && in2 == NOPATH){
		out = (DavaFlowSet)in1.clone();
		out.copyInternalDataFrom(in2);
		return out;
	    }
	    else if(in1 == NOPATH && in2 == NOPATH){
		out = (DavaFlowSet)in1.clone();
		out.copyInternalDataFrom(in2);
		return out; //meaning return NOPATH
	    }
	    else{
		if(MERGETYPE==1){
		    //union
		    ((DavaFlowSet)obj1).union((DavaFlowSet)obj2, out);
		}
		else if(MERGETYPE==2){
		    //intersection
		    ((DavaFlowSet)obj1).intersection((DavaFlowSet)obj2, out);
		}
		else{
		    throw new RuntimeException("Merge type value"+MERGETYPE+" not recognized");
		}
		out.copyInternalDataFrom(obj1);
		out.copyInternalDataFrom(obj2);
		return out;
	    }
	}
	else
	    throw new RuntimeException("merge not implemented for other flowSet types");

    }


    public Object handleContinue(String label,Object output){
	/*
	  get the list with this label from the continueList
	*/
	if(output instanceof DavaFlowSet){
	    List continueSet = ((DavaFlowSet)output).getContinueSet(label);
	    
	    if(continueSet ==null){
		//there is no list associated with this label
		//hence no merging to be done
		//System.out.println("here1");
		return ((DavaFlowSet)output).clone();
	    }
	    else if(continueSet.size()==0){
		//the list is empty
		//hence no merging to be done
		//System.out.println("here2");
		return ((DavaFlowSet)output).clone();

	    }
	    else{
		//System.out.println("here3");
		//continueSet is a list of DavaFlowSets
		Iterator it = continueSet.iterator();

		//we know there is atleast one element
		Object toReturn = merge(output,it.next());

		while(it.hasNext()){
		    //merge this with toReturn
		    toReturn = merge(toReturn,it.next());
		}
		return toReturn;
	    }//a non empty continueSet was found
	}//output was a DavaFlowSet
	else{
	    throw new RuntimeException("handleContinue is only implemented for DavaFlowSet type");
	}
    }







    public Object handleBreak(String label,Object output){

	/*
	  get the list with this label from the breakList
	*/
	if(output instanceof DavaFlowSet){
	    List breakSet = ((DavaFlowSet)output).getBreakSet(label);
	    if(breakSet ==null){
		//there is no list associated with this label
		//hence no merging to be done
		return ((DavaFlowSet)output).clone();
	    }
	    else if(breakSet.size()==0){
		//the list is empty
		//hence no merging to be done
		return ((DavaFlowSet)output).clone();

	    }
	    else{
		//breakSet is a list of DavaFlowSets
		Iterator it = breakSet.iterator();

		//we know there is atleast one element
		Object toReturn = merge(output,it.next());

		while(it.hasNext()){
		    //merge this with toReturn
		    toReturn = merge(toReturn,it.next());
		}
		return toReturn;
	    }//a non empty breakSet was found
	}//output was a DavaFlowSet
	else{
	    throw new RuntimeException("handleBreak is only implemented for DavaFlowSet type");
	}
    }





    public Object getMergedBreakList(String label,Object output){
	if(output instanceof DavaFlowSet){
	    List breakSet = ((DavaFlowSet)output).getBreakSet(label);
	    if(breakSet ==null){
		//there is no list associated with this label
		//hence no merging to be done
		//WHAT ARE WE RETURNING REALLY?? SINCE THIS IS CALLED FROM AN UNCONDITIONAL LOOP
		return NOPATH;
	    }
	    else if(breakSet.size()==0){
		//the list is empty
		//hence no merging to be done
		//WHAT ARE WE RETURNING REALLY?? SINCE THIS IS CALLED FROM AN UNCONDITIONAL LOOP
		return NOPATH;

	    }
	    else{
		//breakSet is a list of DavaFlowSets
		Iterator it = breakSet.iterator();

		//we know there is atleast one element
		Object toReturn = it.next();

		while(it.hasNext()){
		    //merge this with toReturn
		    toReturn = merge(toReturn,it.next());
		}
		return toReturn;
	    }//a non empty breakSet was found
	}//output was a DavaFlowSet
	else{
	    throw new RuntimeException("handleBreak is only implemented for DavaFlowSet type");
	}
    }




    public boolean isDifferent(Object oldObj, Object newObj){
       if(oldObj instanceof DavaFlowSet && newObj instanceof DavaFlowSet){
	   if (((DavaFlowSet)oldObj).equals(newObj) && ((DavaFlowSet)oldObj).internalDataMatchesTo(newObj)){
	       //set matches and breaks and continues also match
	       return false;
	   }
	   else{
	       //System.out.println(oldObj);
	       //System.out.println(newObj);
	       return true;
	   }
	}
       else
	    throw new RuntimeException("isDifferent not implemented for other flowSet types");
    }


    public Object getBeforeSet(Object beforeThis){
	return beforeSets.get(beforeThis);
    }

    public Object getAfterSet(Object afterThis){
	return afterSets.get(afterThis);
    }
}