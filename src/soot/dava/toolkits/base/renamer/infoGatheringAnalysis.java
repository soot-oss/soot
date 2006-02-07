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

package soot.dava.toolkits.base.renamer;

import soot.dava.toolkits.base.AST.analysis.*;
import soot.*;
import soot.jimple.*;
import java.util.*;
import soot.util.*;
import soot.dava.*;
import soot.grimp.*;
import soot.grimp.internal.*;
import soot.dava.internal.javaRep.*;
import soot.dava.internal.asg.*;
import soot.jimple.internal.*;
import soot.dava.internal.AST.*;




public class infoGatheringAnalysis extends DepthFirstAdapter{
    public final static int CLASSNAME=0;
    public final static int METHODNAME=1;
    public final static int GETSET=2;
    public final static int IF=3;
    public final static int WHILE=4;
    public final static int SWITCH=5;
    public final static int ARRAYINDEX=6;
    public final static int MAINARG=7;
    public final static int FIELDASSIGN=8;
    public final static int FORLOOPUPDATE=9;

    public final static int NUMBITS=10;

    //dataset to store all information gathered
    heuristicSet info;

    //if we are within a subtree rooted at a definitionStmt this boolean is true
    boolean inDefinitionStmt=false;

    //whenever there is a definition to a local definedLocal will contain a ref to the local
    Local definedLocal = null;

    //if we are within a subtree rooted at a ifNode or IfElseNode this boolean is true
    boolean inIf = false;

    //if we are within a subtree rooted at a WhileNode or DoWhileNode this boolean is true
    boolean inWhile = false;

    //if we are within a subtree rooted at a ForLoop this boolean is true
    boolean inFor = false;

    public infoGatheringAnalysis(DavaBody davaBody){
	info = new heuristicSet();

	/*
	  Get locals info out of davaBody
	  Copied with modifications from DavaPrinter method printLocalsInBody
	*/
	HashSet params = new HashSet();
	//	params.addAll(davaBody.get_ParamMap().values());
	//params.addAll(davaBody.get_CaughtRefs());
	HashSet thisLocals = davaBody.get_ThisLocals();

	//System.out.println("params"+params);

	Iterator localIt = davaBody.getLocals().iterator();
	List localList = new ArrayList();
	while (localIt.hasNext()) {
	    Local local = (Local) localIt.next();
	    
	    if (params.contains(local) || thisLocals.contains(local))
		continue;
	    localList.add(local);
	}

	//localList is a list with all locals
	//initialize the info Set with empty info for each local
	Iterator it = localList.iterator();
	while(it.hasNext()){
	    Local local = (Local)it.next();
	    info.add(local,NUMBITS);
	}

	
	/*
	  Check if we are dealing with a main method
	  In which case set the MAINARG heuristic of the param
	*/
	//System.out.println("METHOD:"+davaBody.getMethod());
	SootMethod method = davaBody.getMethod();
	//System.out.println(method.getSubSignature());
	if(method.getSubSignature().compareTo("void main(java.lang.String[])")==0){
	    //means we are currently working on the main method
	    it = davaBody.get_ParamMap().values().iterator();
	    int num=0;
	    Local param = null;
	    while(it.hasNext()){
		num++;
		param = (Local)it.next();
	    }
	    if (num>1){
		System.out.println("main method has greater than 1 args!!");
	    }
	    else{
		info.setHeuristic(param,infoGatheringAnalysis.MAINARG);
	    }
	}
    }


    /*
      This can be either an assignment or an identity statement.
      We are however only concerned with stmts which assign values to locals
      
      The method sets the inDefinitionStmt flag to true and if this is a local assignment
      The ref to the local is stored in definedLocal 
    */
    public void inDefinitionStmt(DefinitionStmt s){
	inDefinitionStmt=true;
	//System.out.println(s);
	Value v = s.getLeftOp();
	if(v instanceof Local){
	    //System.out.println("This is a local:"+v);
	    definedLocal = (Local)v;
	}
	else{
	    //System.out.println("Not a local"+v);
	}
    }

    public void outDefinitionStmt(DefinitionStmt s){
	inDefinitionStmt=false;
	definedLocal=null;
    }



    /*
      Deals with cases in which a local is assigned a value from a static field
           int local = field
	   int local = class.field
    */
    public void inStaticFieldRef(StaticFieldRef sfr){
	if(inDefinitionStmt && (definedLocal != null)){
	    SootField field = sfr.getField();
	    info.setFieldName(definedLocal,field.getName());
	}
    }








    /*
      Deals with cases in which a local is assigned a value from a field
           int local = field
	or int local = obj.field
    */

    public void inInstanceFieldRef(InstanceFieldRef ifr){
	if(ifr instanceof AbstractInstanceFieldRef){
	    if(inDefinitionStmt && (definedLocal != null)){
		SootField field = ((AbstractInstanceFieldRef)ifr).getField();
		//System.out.println(definedLocal+" is being assigned field:"+field.getName());
		info.setFieldName(definedLocal,field.getName());
	    }
	}
    }


    public void outInvokeExpr(InvokeExpr ie){
	//If this is within a definitionStmt of a local 
	if(inDefinitionStmt && (definedLocal != null)){
	    //if its a new object being created
	    if(ie instanceof NewInvokeExpr){
		//System.out.println("new object being created retrieve the name");
		RefType ref = ((NewInvokeExpr)ie).getBaseType();
		String className = ref.getClassName();
		info.setObjectClassName(definedLocal,className);
	    }
	    else{
		SootMethodRef methodRef = ie.getMethodRef();
		String name = methodRef.name();
		//System.out.println(name);
		info.setMethodName(definedLocal,name);
	    }
	}
    }



    /*
      This is the object for a flag use in a conditional
      If the value is a local set the appropriate heuristic
    */
    public void inASTUnaryCondition(ASTUnaryCondition uc){
	Value val = uc.getValue();
	if(val instanceof Local){
	    if(inIf)
		info.setHeuristic((Local)val,infoGatheringAnalysis.IF);
	    if(inWhile)
		info.setHeuristic((Local)val,infoGatheringAnalysis.WHILE);
	}
    }

    
    public void inASTBinaryCondition(ASTBinaryCondition bc){
	ConditionExpr condition = bc.getConditionExpr();

	Local local=checkBooleanUse(condition);
	if(local != null){
	System.out.println("HERRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRRR");
	    if(inIf)
		info.setHeuristic(local,infoGatheringAnalysis.IF);
	    if(inWhile)
		info.setHeuristic(local,infoGatheringAnalysis.WHILE);
	}
    }


    /*
      Setting if to true in inASTIfNode so that later we know whether this is a flag use in an if
    */
    public void inASTIfNode(ASTIfNode node){
	inIf=true;
    }

    /*
      Going out of if set flag to false
    */
    public void outASTIfNode(ASTIfNode node){
	inIf=false;
    }
    



    /*
      Setting if to true in inASTIfElseNode so that later we know whether this is a flag use in an ifElse
    */
    public void inASTIfElseNode(ASTIfElseNode node){
	inIf=true;
    }

    /*
      Going out of ifElse set flag to false
    */
    public void outASTIfElseNode(ASTIfElseNode node){
	inIf=false;
    } 



    /*
      Setting if to true in inASTWhileNode so that later we know whether this is a flag use in a WhileNode
    */
    public void inASTWhileNode(ASTWhileNode node){
	inWhile=true;
    }


    /*
      setting flag to false
    */
    public void outASTWhileNode(ASTWhileNode node){
	inWhile=false;
    }


    /*
      Setting if to true in inASTDoWhileNode so that later we know whether this is a flag use in a WhileNode
    */
    public void inASTDoWhileNode(ASTDoWhileNode node){
	inWhile=true;
    }


    /*
      setting flag to false
    */
    public void outASTDoWhileNode(ASTDoWhileNode node){
	inWhile=false;
    }

    /*
      Check the key of the switch statement to see if its a local
    */
    public void inASTSwitchNode(ASTSwitchNode node){
	Value key = node.get_Key();
	if(key instanceof Local)
	    info.setHeuristic((Local)key,infoGatheringAnalysis.SWITCH);
    }




    public void inArrayRef(ArrayRef ar){
	Value index = ar.getIndex();
	if(index instanceof Local)
	    info.setHeuristic((Local)index,infoGatheringAnalysis.ARRAYINDEX);
    }






    public void inASTTryNode(ASTTryNode node){

	Map exceptionMap = node.get_ExceptionMap();
	Map paramMap = node.get_ParamMap();
	//get catch list and for each local in the catch set the type to that of the type of exception

	List catchList = node.get_CatchList();
        Iterator it = catchList.iterator();
	while (it.hasNext()) {
	    ASTTryNode.container catchBody = (ASTTryNode.container)it.next();
	    
	    SootClass sootClass = ((SootClass)exceptionMap.get(catchBody));
	    String name = sootClass.getJavaStyleName();

	    Local local = (Local)paramMap.get(catchBody);
	    info.setObjectClassName(local,name);

	}
    }



    /*
      setting flag to true
    */
    public void inASTForLoopNode(ASTForLoopNode node){
	System.out.println("In a for loop");
	inFor=true;

	Iterator updateIt = node.getUpdate().iterator();
	while(updateIt.hasNext()){
	    AugmentedStmt as = (AugmentedStmt)updateIt.next();
	    Stmt s = as.get_Stmt();
	    if(s instanceof GAssignStmt){
		System.out.println("Update Stmt is"+s);
		Value leftOp = ((GAssignStmt)s).getLeftOp();
		if(leftOp instanceof Local){
		    info.setHeuristic((Local)leftOp,infoGatheringAnalysis.FORLOOPUPDATE);
		}
	    }
	}
    }




    /*
      setting flag to false
    */
    public void outASTForLoopNode(ASTForLoopNode node){
	System.out.println("out of a for loop");
	inFor=false;
    }



    /*
      If there are any locals at this point who do not have any className set
      it might be a good idea to store that information
    */
    public void outASTMethodNode(ASTMethodNode node){
	

	System.out.println("SET START");
	info.print();
	System.out.println("SET END");
    }










    /*
      The method checks whether a particular ConditionExpr 
      is a comparison of a local with a boolean
      If so the local is returned
    */
    private Local checkBooleanUse(ConditionExpr condition){
	boolean booleanUse=false;

	//check whether the condition qualifies as a boolean use
	if(condition instanceof NeExpr || condition instanceof EqExpr){
	    Value op1 = condition.getOp1();
	    Value op2 = condition.getOp2();
	    if(op1 instanceof DIntConstant){
		Type op1Type = ((DIntConstant)op1).type;
		if( op1Type instanceof BooleanType)
		    booleanUse=true;
	    }
	    else if(op2 instanceof DIntConstant){
		Type op2Type = ((DIntConstant)op2).type;
		if( op2Type instanceof BooleanType)
		    booleanUse=true;
	    }
	    if(booleanUse){
		//at this point we know that one of the values op1 or op2 was a boolean
		//check whether the other is a local
		if(op1 instanceof Local)
		    return (Local)op1;
		else if (op2 instanceof Local)
		    return (Local)op2;
	    }
	    else
		return null;//meaning no local used as boolean found
	}
	return null; //meaning no local used as boolean found
    }




    public heuristicSet getHeuristicSet(){
	return info;
    }
}
