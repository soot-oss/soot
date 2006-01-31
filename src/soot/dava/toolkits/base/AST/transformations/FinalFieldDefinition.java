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
import soot.jimple.*;
import soot.jimple.internal.*;
import soot.grimp.internal.*;
import soot.dava.internal.AST.*;
import soot.dava.internal.asg.*;
import soot.dava.internal.javaRep.*;
import soot.dava.toolkits.base.AST.analysis.*;
import soot.dava.toolkits.base.AST.traversals.*;
import soot.dava.toolkits.base.AST.structuredAnalysis.*;


import java.util.*;
/**
 * Maintained by: Nomair A. Naeem
 */


/**
 * CHANGE LOG:  30th January 2006: Class was created to get rid of the field might not be initialized 
 *                                 error that used to show up when recompiling decompiled code
                                   Will be throughly covered in "Programmer Friendly Code" Sable Tech Report (2006)
 *
 */ 

/*
 * This class makes sure there is an initialization of all final variables (static or non static). If we
 * cant guarantee initialization (may be initialized on multiple paths but not all) then we remove the final
 * keyword
 */
public class FinalFieldDefinition {// extends DepthFirstAdapter{
    SootClass sootClass;
    SootMethod sootMethod;

    List cancelFinalModifier;

    public FinalFieldDefinition(ASTMethodNode node){
	DavaBody davaBody = node.getDavaBody();
	sootMethod = davaBody.getMethod();
	sootClass = sootMethod.getDeclaringClass();
	
	String subSignature = sootMethod.getName();
	if(!(subSignature.compareTo("<clinit>")==0  || subSignature.compareTo("<init>")==0 )){	
	    //dont care about these since we want only static block and constructors
	    //System.out.println("\n\nName"+sootMethod.getName()+" SubSignature:"+sootMethod.getSubSignature());
	    return;
	}
	
	//create a list of interesting vars
	ArrayList interesting = findFinalFields();
	if(interesting.size()==0){
	    //no final fields of interest
	    return;
	}

	cancelFinalModifier = new ArrayList();
	analyzeMethod(node, interesting);




	Iterator it = cancelFinalModifier.iterator();
	while(it.hasNext()){
	    SootField field = (SootField)it.next();
	    field.setModifiers((soot.Modifier.FINAL^0xFFFF) & field.getModifiers());
	}
    }







    /*
     * this method finds all the final fields in this class and
     * assigns them to the finalFields list

     * Note this stores a list of SootFields!!!

     * Fields which are initialized in their declaration should not be added
     */
    public ArrayList findFinalFields(){

	//first thing is to get a list of all final fields in the class
	ArrayList interestingFinalFields = new ArrayList();

	Iterator fieldIt = sootClass.getFields().iterator();
	while(fieldIt.hasNext()){
	    SootField tempField = (SootField)fieldIt.next();
	    if(tempField.isFinal()){
		
		//if its static final and method is static add
		if(tempField.isStatic() && sootMethod.getName().compareTo("<clinit>")==0){
		    interestingFinalFields.add(tempField);
		}
		
		//if its non static and final and method is constructor add
		if((!tempField.isStatic()) && sootMethod.getName().compareTo("<init>")==0){
		    interestingFinalFields.add(tempField);
		}
	    }
	}
	return interestingFinalFields;
    }











    public void analyzeMethod(ASTMethodNode node, List varsOfInterest){
	//System.out.println("****************START MUST ANALYSIS******************************");
	MustMayInitialize must = new MustMayInitialize(node,MustMayInitialize.MUST);
	//System.out.println("****************END MUST ANALYSIS******************************");


	Iterator it = varsOfInterest.iterator();
	while(it.hasNext()){
	    SootField interest = (SootField)it.next();
	    //System.out.println("\n\n>>>>>>>>>>>>>>>>>>>>>>>>>>>>>Var of interest is:"+interest);
	    if(!(must.isMustInitialized(interest))){
		//might not be initialized on all paths which is the ideal case we want


		//System.out.println("SootField: "+interest+" might not be initialized on all paths");
		//System.out.println("\n\n\nChecking if it is initialized on any path......MAY Analysis");
		MustMayInitialize may = new MustMayInitialize(node,MustMayInitialize.MAY);
		if(may.isMayInitialized(interest)){
		    //System.out.println("It is initialized on some path just not all paths\n");










		    List defs = must.getDefs(interest);
		    if(defs == null)
			throw new RuntimeException("Sootfield: " + interest+ " is mayInitialized but the defs is null");
		    
		    handleAssignOnSomePaths(node,interest,defs);
		}
		else{
		    //not initialized on any path.
		    //SHOULD ASSIGN DEFAULT VALUE
		    //System.out.println("Final field is not initialized on any path----------MAYBE ASSIGN DEFAULT VALUE");
		    
		    maybeAssignDefault(node,interest);
		    //System.out.println("out of trying to assign default val....maybe didnt assign it though");
		}

	    }
	    else{//was initialized on all paths couldnt ask for more
		//System.out.println("Is Initialized on all paths so life is great");
	    }
	}
    }


    /*
     * If we get to this method we know the field is a final since we are dealing with final fields only in this class
     * if we get here and the field is static then this means the node must be the static initializer because
     * only in that method are static finals interesting
     */
    public void maybeAssignDefault(ASTMethodNode node, SootField f){
	Type fieldType = f.getType();
	if(fieldType instanceof DoubleType && f.hasTag("DoubleConstantValueTag")){
	    return;
	}
	else if (fieldType instanceof FloatType && f.hasTag("FloatConstantValueTag")){
	    return;
	}
	else if (fieldType instanceof LongType && f.hasTag("LongConstantValueTag")){
	    return;
	}
	else if (fieldType instanceof CharType && f.hasTag("IntegerConstantValueTag")){
	    return;
	}
	else if (fieldType instanceof BooleanType && f.hasTag("IntegerConstantValueTag")){
	    return;
	}
	else if ( (fieldType instanceof IntType || fieldType instanceof ByteType || fieldType instanceof ShortType) && 
		  f.hasTag("IntegerConstantValueTag")){
	    return;
	}
	else if(f.hasTag("StringConstantValueTag")){
	    return;
	}
	else{
	    //System.out.println("Did not find CONSTANT TAG Assignment........going for default");
	    assignDefault(node,f);
	}
    }


    /*
     * One gets to this method only if there was NO definition of a static final field in the static body
     * At the same time no TAG with a constant value matched, so we know the static final was not initialized at declaration time
     * If this happens: though it shouldnt unless u come from non-java compilers...insert default value initialization into
     * the static method...right at the end to make things easy
     */
    public void assignDefault(ASTMethodNode node, SootField f){

	//create initialization stmt
	AugmentedStmt defaultStmt = createDefaultStmt(f);

	if(defaultStmt == null)
	    return;



	List subBodies = (List)node.get_SubBodies();
	if(subBodies.size()!=1)
	    throw new RuntimeException("SubBodies size of method node not equal to 1");

	List body = (List)subBodies.get(0);

	//check if the bodys last node is an ASTStatementSequenceNode where we might be able to add
	
	boolean done = false;
	if(body.size()!=0){
	    ASTNode lastNode = (ASTNode)body.get(body.size()-1);
	    if(lastNode instanceof ASTStatementSequenceNode){
		List stmts = ((ASTStatementSequenceNode)lastNode).getStatements();
		if(stmts.size()!=0){
		    Stmt s = ((AugmentedStmt)stmts.get(0)).get_Stmt();
		    if(!(s instanceof DVariableDeclarationStmt)){
			//can add statement here
			stmts.add(defaultStmt);

			ASTStatementSequenceNode newNode = new ASTStatementSequenceNode(stmts);
			//replace this node with the original node

			body.remove(body.size()-1);
			body.add(newNode);
			
			node.replaceBody(body);
			done=true;
		    }
		}
	    }
	}
	if(!done){
	    List newBody = new ArrayList();
	    newBody.add(defaultStmt);

	    ASTStatementSequenceNode newNode = new ASTStatementSequenceNode(newBody);
	    body.add(newNode);
	    
	    node.replaceBody(body);
	}

    }


    public AugmentedStmt createDefaultStmt(Object field){

	Value ref  = null;
	Type fieldType = null;
	if(field instanceof SootField){
	    //have to make a static field ref
	    SootFieldRef tempFieldRef = ((SootField)field).makeRef();

	    fieldType = ((SootField)field).getType();	    
	    if(((SootField)field).isStatic())
	       ref = new DStaticFieldRef(tempFieldRef,true);
	    else
		ref = new DInstanceFieldRef(new JimpleLocal("this",fieldType) ,tempFieldRef,new HashSet());

	}
	else if(field instanceof Local){
	    ref = (Local)field;
	    fieldType = ((Local)field).getType();
	}

	GAssignStmt assignStmt=null;


	if(fieldType instanceof RefType){
	    assignStmt = new GAssignStmt(ref,NullConstant.v());
	}		
	else if(fieldType instanceof DoubleType){
	    assignStmt = new GAssignStmt(ref,DoubleConstant.v(0));
	}
	else if (fieldType instanceof FloatType){
	    assignStmt = new GAssignStmt(ref,FloatConstant.v(0));
	}
	else if (fieldType instanceof LongType){
	    assignStmt = new GAssignStmt(ref,LongConstant.v(0));
	}
	else if ( fieldType instanceof IntType || 
		  fieldType instanceof ByteType || 
		  fieldType instanceof ShortType || 
		  fieldType instanceof CharType || 
		  fieldType instanceof BooleanType){

	    assignStmt = new GAssignStmt(ref,DIntConstant.v(0,fieldType));
	}
	
	if(assignStmt!=null){
	    //System.out.println("AssignStmt is"+assignStmt);
	    AugmentedStmt as = new AugmentedStmt(assignStmt);
	    return as;	    
	}
	else
	    return null;

    }










    /*
     * A sootfield gets to this method if it was an interesting field i.e static final for clinit
     * and only final but non static for init and there was atleast one place that this var was defined
     * but it was not defined on all paths and hence the recompilation will result in an error
     *
     *         try{
     *              staticFinal = defined;
     *            }
     *            catch(Exception e){}
     */

    public void handleAssignOnSomePaths(ASTMethodNode node, SootField field,List defs){
	//System.out.println("Defs of final field: "+field+" are:"+defs);
	if(defs.size()!=1){
	    //one definition we might still be able to handle but not more than those REMOVE FINAL KEYWORD
	    //System.out.println("Setting field to non final");
	    cancelFinalModifier.add(field);
	}
	else{
	    //we know there is only one definition lets just make sure there is no use of def

	    //check if there are no uses
	    AllVariableUses varUses = new AllVariableUses(node);
	    node.apply(varUses);
	    //System.out.println("Done finding all varUses\n\n");
	    
	    List allUses = varUses.getUsesForField(field);

	    if(allUses != null && allUses.size()!=0){
		//in that case remove final keyword since we dont want to get into trying to initialize before use
		//System.out.println("REMOVED FINAL since there was use of a field and it is not initialized on all paths");
		cancelFinalModifier.add(field);
	    }
	    else{
		
		//if there is no use its worth a try to fix definition using the indirect assignment approach
		//System.out.println("Going to fix it");


		/*
		 * so lets recap till now what we know about this field
		 * Field is of interest meaning its definetly final
		 *
		 * It is not initialized on all paths
		 * It is however initialized on some path
		 * There is only one initialization of it
		 * The field is not used anywhere in this method
		 */
		



		//Introduce dummy local var of that Field's type

		Type localType = field.getType(); 
		Local newLocal = new JimpleLocal("DavaTemp_"+field.getName(),localType);

		DVariableDeclarationStmt varStmt = null;
		varStmt = new DVariableDeclarationStmt(localType);
		varStmt.addLocal(newLocal);
		AugmentedStmt as = new AugmentedStmt(varStmt);

		//System.out.println("Var Decl stmt"+as);

		
		//STORE IT IN Methods Declaration Node
		ASTStatementSequenceNode declNode = node.getDeclarations();
		List stmts = declNode.getStatements();
		stmts.add(as);

		declNode = new ASTStatementSequenceNode(stmts);

		
		List subBodies = (List)node.get_SubBodies();
		if(subBodies.size()!=1)
		    throw new RuntimeException("ASTMethodNode does not have one subBody");

		List body = (List)subBodies.get(0);

		body.remove(0);
		body.add(0,declNode);
		
		node.replaceBody(body);

		node.setDeclarations(declNode);		




		//initialize it to null
		AugmentedStmt initialization = createDefaultStmt(newLocal);
		//System.out.println(initialization);

		//we know that the first node in the ASTMethodNode is a declaration node
		//check if the second node is an ASTStatementNode if yes add initialization there
		// otherwise add new ASTStatementSequencenode with initialization

		if(body.size()<2)
		    throw new RuntimeException("Size of body is less than 1");


		//body has to be atleast 1 since we have declarations if its greater than 1 check the next node
		//body has to be greater than 1 since we know the original field is initialized on some path
		ASTNode nodeSecond = (ASTNode)body.get(1);
		if(nodeSecond instanceof ASTStatementSequenceNode){
		    //the second node is a stmt seq node just add the stmt here
		    List stmts1 = ((ASTStatementSequenceNode)nodeSecond).getStatements();
		    stmts1.add(initialization);
		    nodeSecond = new ASTStatementSequenceNode(stmts1);
		    //System.out.println("Init added in exisiting node");
		    body.remove(1);
		}
		else{
		    //System.out.println("had to add new node");
		    List tempList = new ArrayList();
		    tempList.add(initialization);
		    nodeSecond = new ASTStatementSequenceNode(tempList);
		}
		body.add(1,nodeSecond);
		node.replaceBody(body);
		

		
		
		//replace the definition of final field with the definition of the local just created
		//System.out.println("About to replace def of"+defs.get(0));

		((GAssignStmt)defs.get(0)).setLeftOp(newLocal);

		//HAVE TO ADD field = newLocal stmt to end of MethodNode


		//have to make a static field ref
		SootFieldRef tempFieldRef = ((SootField)field).makeRef();
	    
		Value ref;
		if(field.isStatic())
		    ref = new DStaticFieldRef(tempFieldRef,true);
		else{
		    ref = new DInstanceFieldRef(new JimpleLocal("this",field.getType()) ,tempFieldRef,new HashSet());
		    //throw new RuntimeException("STOPPED");
		}

		    
		GAssignStmt assignStmt= new GAssignStmt(ref,newLocal);
		AugmentedStmt assignStmt1 = new AugmentedStmt(assignStmt);

		//System.out.println("Last stmt"+assignStmt);




		//body contains the currentBody of the methodnode
		//check last node
		//we know body is atleast 2

		ASTNode lastNode = (ASTNode)body.get(body.size()-1);
		if(lastNode instanceof ASTStatementSequenceNode){
		    //the last node is a stmt seq node just add the stmt here
		    List stmtsLast = ((ASTStatementSequenceNode)lastNode).getStatements();
		    stmtsLast.add(assignStmt1);
		    lastNode = new ASTStatementSequenceNode(stmtsLast);
		    //System.out.println("Last stmt added to existing node");
		    body.remove(body.size()-1);
		}
		else{
		    //System.out.println("had to add new node");
		    List tempList = new ArrayList();
		    tempList.add(assignStmt1);
		    lastNode = new ASTStatementSequenceNode(tempList);
		}		
		body.add(lastNode);
		node.replaceBody(body);
		
	    }
	}
    }
    
}