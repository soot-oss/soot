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

import soot.dava.toolkits.base.AST.analysis.*;
import soot.dava.internal.javaRep.*;
import soot.dava.internal.AST.*;
import soot.*;
import soot.jimple.*;

/*
  TO MAKE CODE EFFECIENT BLOCK THE ANALYSIS TO GOING INTO STATEMENTS
  this is done by overriding the caseASTStatementSequenceNode
*/
public class BooleanConditionSimplification extends DepthFirstAdapter{

    public BooleanConditionSimplification(boolean verbose){
	super(verbose);
    }


    public void caseASTStatementSequenceNode(ASTStatementSequenceNode node){
    }

    public BooleanConditionSimplification(){
    }
    /*
      The method checks whether a particular ASTBinaryCondition 
      is a comparison of a local with a boolean
      If so the ASTBinaryCondition is replaced by a ASTUnaryCondition
    */
    public void outASTIfNode(ASTIfNode node){
	ASTCondition condition = node.get_Condition();
	if(condition instanceof ASTBinaryCondition){
	    ConditionExpr condExpr = ((ASTBinaryCondition)condition).getConditionExpr();
	    Value unary = checkBooleanUse(condExpr);
	    if(unary != null){
		node.set_Condition(new ASTUnaryCondition(unary));
	    }
	}
    }

    public void outASTIfElseNode(ASTIfElseNode node){
	ASTCondition condition = node.get_Condition();
	if(condition instanceof ASTBinaryCondition){
	    ConditionExpr condExpr = ((ASTBinaryCondition)condition).getConditionExpr();
	    Value unary = checkBooleanUse(condExpr);
	    if(unary != null){
		node.set_Condition(new ASTUnaryCondition(unary));
	    }
	}
    }

    public void outASTWhileNode(ASTWhileNode node){
	ASTCondition condition = node.get_Condition();
	if(condition instanceof ASTBinaryCondition){
	    ConditionExpr condExpr = ((ASTBinaryCondition)condition).getConditionExpr();
	    Value unary = checkBooleanUse(condExpr);
	    if(unary != null){
		node.set_Condition(new ASTUnaryCondition(unary));
	    }
	}
    }

    public void outASTDoWhileNode(ASTDoWhileNode node){
	ASTCondition condition = node.get_Condition();
	if(condition instanceof ASTBinaryCondition){
	    ConditionExpr condExpr = ((ASTBinaryCondition)condition).getConditionExpr();
	    Value unary = checkBooleanUse(condExpr);
	    if(unary != null){
		node.set_Condition(new ASTUnaryCondition(unary));
	    }
	}
    }

    private Value checkBooleanUse(ConditionExpr condition){
	//check whether the condition qualifies as a boolean use
	if(condition instanceof NeExpr || condition instanceof EqExpr){
	    Value op1 = condition.getOp1();
	    Value op2 = condition.getOp2();
	    if(op1 instanceof DIntConstant){
		Type op1Type = ((DIntConstant)op1).type;
		if( op1Type instanceof BooleanType){
		    return decideCondition(op2,((DIntConstant)op1).toString(),condition);
		}
	    }
	    else if(op2 instanceof DIntConstant){
		Type op2Type = ((DIntConstant)op2).type;
		if( op2Type instanceof BooleanType){
		    return decideCondition(op1,((DIntConstant)op2).toString(),condition);
		}
	    }
	    
	    else
		return null;//meaning no Value used as boolean found
	}
	return null; //meaning no local used as boolean found
    }

    /*
      Used to decide what the condition should be if we are converting from ConditionExpr to Value
      A != false/0  -->  A
      A != true/1   --> !A
      A == false/0  --> !A
      A == true/1   -->  A
    */
    private Value decideCondition(Value A, String truthString, ConditionExpr condition){
	int truthValue =0;
	boolean notEqual=false;

	//find out whether we are dealing with a false or true
	if(truthString.compareTo("false")==0)
	    truthValue=0;
	else if(truthString.compareTo("true")==0)
	    truthValue=1;
	else
	    throw new RuntimeException();

	

	//find out whether the comparison operator is != or ==
	if(condition instanceof NeExpr ){
	    notEqual=true;
	}
	else if( condition instanceof EqExpr){	
	    notEqual=false;
	}
	else
	    throw new RuntimeException();


	//decide and return
	if( notEqual && truthValue==0){ //A != false -->A
	    return A;
	}
	else if(notEqual && truthValue==1){//A != true --> !A
	    if(A instanceof DNotExpr){//A is actually !B 
		return ((DNotExpr)A).getOp();
	    }
	    else
		return (new DNotExpr(A));
	}
	else if(!notEqual && truthValue==0){//A == false --> !A
	    if(A instanceof DNotExpr){//A is actually !B 
		return ((DNotExpr)A).getOp();
	    }
	    else
		return new DNotExpr(A);
	}
	else if(!notEqual && truthValue==1){//A == true --> A
	    return A;
	}
	else
	    throw new RuntimeException();
    }

}