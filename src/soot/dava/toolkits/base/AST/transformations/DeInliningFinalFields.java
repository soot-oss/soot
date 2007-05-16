/* Soot - a J*va Optimization Framework
 * Copyright (C) 2006 Nomair A. Naeem (nomair.naeem@mail.mcgill.ca)
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
import soot.dava.*;
import soot.tagkit.*;
import soot.util.Chain;
import soot.jimple.*;
//import soot.jimple.internal.*;
//import soot.grimp.internal.*;
import soot.dava.internal.AST.*;
import soot.dava.internal.asg.*;
import soot.dava.internal.javaRep.*;
import soot.dava.toolkits.base.AST.analysis.*;
//import soot.dava.toolkits.base.AST.traversals.*;
//import soot.dava.toolkits.base.AST.structuredAnalysis.*;


import java.util.*;

/**
 * Maintained by: Nomair A. Naeem
 */


/**
 * CHANGE LOG:  2nd February 2006: 
 *
 */ 

/*
 * Both static and non-static BUT FINAL fields if initialized with constants get inlined
 * A final initialized with an object (even if its a string) is NOT inlined
 *  e.g. 
 *      public static final String temp = "hello";   //use of temp will get inlined
 *      public static final String temp1 = new String("hello");   //use of temp will NOT get inlined
 *
 *
 If its a static field we can get the info from a tag
 in the case of a non static we cant decide since the field is initialized inside a constructor and depending
 on different constructors there coul dbe different values...conservative....

 *
 * Need to be very clear when a SootField can be used
 * It can be used in the following places:

 * a, NOT used inside a Synchronized Block ........ HOWEVER ADD IT SINCE I DONT SEE WHY THIS RESTRICTION EXISTS!!!  TICK
 * b, CAN BE USED in a condition            TICK
 * c, CAN BE USED in the for init for update   TICK
 * d, CAN BE USED in a switch             TICK
 * e, CAN BE USED in a stmt    TICK
 *
 * These are the exact places to look for constants...a constant is 
 * StringConstant
 * DoubleConstant
 * FloatConstant
 * IntConstant   (shortype, booltype, charType intType, byteType
 * LongConstant
 * 
 */


public class DeInliningFinalFields extends DepthFirstAdapter{
    SootClass sootClass=null;
    SootMethod sootMethod=null; 
    DavaBody davaBody=null;  

    HashMap<Comparable, SootField> finalFields;
    
    //ASTParentNodeFinder parentFinder;

    public DeInliningFinalFields(){
    }
    public DeInliningFinalFields(boolean verbose){
	super(verbose);
    }


    public void inASTMethodNode(ASTMethodNode node){
	DavaBody davaBody = node.getDavaBody();
	sootMethod = davaBody.getMethod();
	//System.out.println("Deiniling  method: "+sootMethod.getName());
	sootClass = sootMethod.getDeclaringClass();

	finalFields = new HashMap<Comparable, SootField>();

	ArrayList fieldChain = new ArrayList();

	Chain appClasses = Scene.v().getApplicationClasses();
	Iterator it = appClasses.iterator();
	while(it.hasNext()){
		SootClass tempClass = (SootClass)it.next(); 
		//System.out.println("DeInlining"+tempClass.getName());
		Chain tempChain = tempClass.getFields();
		Iterator tempIt = tempChain.iterator();
		while(tempIt.hasNext()){
			fieldChain.add(tempIt.next());
		}
			
	}
	
	
//	Iterator fieldIt = sootClass.getFields().iterator();
	Iterator fieldIt = fieldChain.iterator();
	while(fieldIt.hasNext()){
	    SootField f = (SootField)fieldIt.next();
	    if(f.isFinal()){

		//check for constant value tags
		Type fieldType = f.getType();
		if(fieldType instanceof DoubleType && f.hasTag("DoubleConstantValueTag")){
		    double val = ((DoubleConstantValueTag)f.getTag("DoubleConstantValueTag")).getDoubleValue();
		    finalFields.put(new Double(val),f);
		}
		else if (fieldType instanceof FloatType && f.hasTag("FloatConstantValueTag")){
		    float val = ((FloatConstantValueTag)f.getTag("FloatConstantValueTag")).getFloatValue();
		    finalFields.put(new Float(val),f);
		}
		else if (fieldType instanceof LongType && f.hasTag("LongConstantValueTag")){
		    long val = ((LongConstantValueTag)f.getTag("LongConstantValueTag")).getLongValue();
		    finalFields.put(new Long(val),f);
		}
		else if (fieldType instanceof CharType && f.hasTag("IntegerConstantValueTag")){
		    int val = ((IntegerConstantValueTag)f.getTag("IntegerConstantValueTag")).getIntValue();
		    finalFields.put(new Integer(val),f);
		}
		else if (fieldType instanceof BooleanType && f.hasTag("IntegerConstantValueTag")){
		    int val = ((IntegerConstantValueTag)f.getTag("IntegerConstantValueTag")).getIntValue();
		    if (val ==0)
			finalFields.put(new Boolean(false),f);
		    else
			finalFields.put(new Boolean(true),f);
		}
		else if ( (fieldType instanceof IntType || fieldType instanceof ByteType || fieldType instanceof ShortType) && 
			  f.hasTag("IntegerConstantValueTag")){
		    int val = ((IntegerConstantValueTag)f.getTag("IntegerConstantValueTag")).getIntValue();
		    finalFields.put(new Integer(val),f);
		}
		else if(f.hasTag("StringConstantValueTag")){
		    String val = ((StringConstantValueTag)f.getTag("StringConstantValueTag")).getStringValue();
		    //System.out.println("adding string constant"+val);
		    finalFields.put(val,f);
		}
	    }//end if final
	}//going through fields
    }




    /*
     * StringConstant
     * DoubleConstant
     * FloatConstant
     * IntConstant   (shortype, booltype, charType intType, byteType
     * LongConstant
     */
    private boolean isConstant(Value val){
	if(val instanceof StringConstant || val instanceof DoubleConstant ||
	   val instanceof FloatConstant || val instanceof IntConstant || val instanceof LongConstant){
	    return true;
	}
	return false;
    }





    /*
     * Notice as things stand synchblocks cant have the use of a SootField
     */
    public void inASTSynchronizedBlockNode(ASTSynchronizedBlockNode node){
	//hence nothing is implemented here
    }



    public void checkAndSwitch(ValueBox valBox){
    	Value val =valBox.getValue();

    	Object finalField = check(val);
    	if(finalField!=null){
    		//System.out.println("Final field with this value exists"+finalField);
		
    		/*
    		 * If the final field belongs to the same class then we should supress declaring class
    		 */
    		SootField field = (SootField)finalField;
    		
    		if(sootClass.declaresField(field.getName(),field.getType())){
    			//this field is of this class so supress the declaring class
    			if(valBox.canContainValue(new DStaticFieldRef(field.makeRef(),true))){
    				valBox.setValue(new DStaticFieldRef(field.makeRef(),true));
    			}
    		}
    		else{
    			if(valBox.canContainValue(new DStaticFieldRef(field.makeRef(),true))){
    				valBox.setValue(new DStaticFieldRef(field.makeRef(),false));
    			}
    		}

    	}
    	//else
    	//  System.out.println("Final field not found");
    }



    public Object check(Value val){
	Object finalField=null;
	if(isConstant(val)){
	    //System.out.println("Found constant in code"+val);
	    
	    //can be a byte or short or char......or an int ...in the case of int you also have to check for Booleans
	    if(val instanceof StringConstant){
		String myString = ((StringConstant)val).toString();
		myString = myString.substring(1,myString.length()-1);
		//System.out.println("looking for:"+myString);
		finalField = finalFields.get(myString);
	    }
	    else if(val instanceof DoubleConstant){
		String myString = ((DoubleConstant)val).toString();
		
		finalField = finalFields.get(new Double(myString));
	    }
	    else if(val instanceof FloatConstant){
		String myString = ((FloatConstant)val).toString();
		
		finalField = finalFields.get(new Float(myString));
	    } 
	    else if(val instanceof LongConstant){
		String myString = ((LongConstant)val).toString();
		
		finalField = finalFields.get(new Long(myString.substring(0,myString.length()-1)));
	    }
	    else if(val instanceof IntConstant){
		String myString = ((IntConstant)val).toString();
		if(myString.length()==0)
		    return null;

		Type valType = ((IntConstant)val).getType();		


		Integer myInt=null;
		try{
		    if(myString.charAt(0)=='\''){//character
			if(myString.length()<2)
			    return null;

			myInt = new Integer(myString.charAt(1));
		    }
		    else
			myInt = new Integer(myString);
		}
		catch(Exception e){
		    //System.out.println("exception occured...gracefully exitting method..string was"+myString);
		    return finalField;
		}


		if(valType instanceof ByteType){
		    finalField = finalFields.get(myInt);
		}
		else if(valType instanceof IntType){
		    if(myString.equals("false"))
			finalField = finalFields.get(new Boolean(false));
		    else  if(myString.equals("true"))
			finalField = finalFields.get(new Boolean(true));
		    else {
			finalField = finalFields.get(myInt);
		    }
		} 
		else if(valType instanceof ShortType){
		    finalField = finalFields.get(myInt);
		}
	    }
	}
	return finalField;
    }










    /*
      The key in a switch stmt can be a local or a SootField or a value
      which can contain constant

      Hence the some what indirect approach........notice we will work with valueBoxes so that
      by changing the value in the value box we can deInline any field
    */
    public void inASTSwitchNode(ASTSwitchNode node){
	Value val = node.get_Key();

	if(isConstant(val)){
	    //find if there is a SootField with this constant
	    //System.out.println("Found constant as key to switch");

	    checkAndSwitch(node.getKeyBox());
	    return;
	}
	//val is not a constant but it might have other constants in it
	
	Iterator it = val.getUseBoxes().iterator();
	while(it.hasNext()){
	    ValueBox tempBox = (ValueBox)it.next();
	    //System.out.println("Checking useBox of switch key");
	    checkAndSwitch(tempBox);
	}
    }














    public void inASTStatementSequenceNode(ASTStatementSequenceNode node){
    	List<Object> statements = node.getStatements();
    	Iterator<Object> it = statements.iterator();
	
    	while(it.hasNext()){
    		AugmentedStmt as = (AugmentedStmt)it.next();
    		Stmt s = as.get_Stmt();
    		Iterator tempIt = s.getUseBoxes().iterator();
    		while(tempIt.hasNext()){
    			ValueBox tempBox = (ValueBox)tempIt.next();
    			//System.out.println("Checking useBox of stmt");
    			checkAndSwitch(tempBox);
    		}
    	}
    }








  public void inASTForLoopNode(ASTForLoopNode node){

	//checking uses in init
	List<Object> init = node.getInit();
	Iterator<Object> it = init.iterator();
	while(it.hasNext()){
	    AugmentedStmt as = (AugmentedStmt)it.next();
	    Stmt s = as.get_Stmt();
	    Iterator tempIt = s.getUseBoxes().iterator();
	    while(tempIt.hasNext()){
		ValueBox tempBox = (ValueBox)tempIt.next();
		//System.out.println("Checking useBox of init stmt");
		checkAndSwitch(tempBox);
	    }
	}

	//checking uses in condition
	ASTCondition cond = node.get_Condition();
	checkConditionalUses(cond,node);

	
	//checking uses in update
	List<Object> update = node.getUpdate();
	it = update.iterator();
	while(it.hasNext()){
	    AugmentedStmt as = (AugmentedStmt)it.next();
	    Stmt s = as.get_Stmt();
	    Iterator tempIt = s.getUseBoxes().iterator();
	    while(tempIt.hasNext()){
		ValueBox tempBox = (ValueBox)tempIt.next();
		//System.out.println("Checking useBox of update stmt");
		checkAndSwitch(tempBox);
	    }
	}
    }



    /*
     * checking for unary conditions doesnt matter since this was definetly lost.
     */
    public void checkConditionalUses(Object cond,ASTNode node){
	if(cond instanceof ASTAggregatedCondition){
	    checkConditionalUses((((ASTAggregatedCondition)cond).getLeftOp()),node);
	    checkConditionalUses(((ASTAggregatedCondition)cond).getRightOp(),node );
	    return;
	}
	else if(cond instanceof ASTBinaryCondition){
	    //get uses from binaryCondition
	    Value val = ((ASTBinaryCondition)cond).getConditionExpr();
	    Iterator tempIt = val.getUseBoxes().iterator();
	    while(tempIt.hasNext()){
		ValueBox tempBox = (ValueBox)tempIt.next();
		//System.out.println("Checking useBox of binary condition");
		checkAndSwitch(tempBox);
	    }
	}
    }








    /*
     * The condition of an if node can use a local
     *
     */
    public void inASTIfNode(ASTIfNode node){
	ASTCondition cond = node.get_Condition();
	checkConditionalUses(cond,node);
    }


    /*
     * The condition of an ifElse node can use a local
     *
     */
    public void inASTIfElseNode(ASTIfElseNode node){
	ASTCondition cond = node.get_Condition();
	checkConditionalUses(cond,node);
    }




    /*
     * The condition of a while node can use a local
     *
     */
    public void inASTWhileNode(ASTWhileNode node){
	ASTCondition cond = node.get_Condition();
	checkConditionalUses(cond,node);
    }



    /*
     * The condition of a doWhile node can use a local
     *
     */
    public void inASTDoWhileNode(ASTDoWhileNode node){
	ASTCondition cond = node.get_Condition();
	checkConditionalUses(cond,node);
    }
}