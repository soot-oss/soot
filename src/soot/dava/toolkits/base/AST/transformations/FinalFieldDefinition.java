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
	MustMayInitialize must = new MustMayInitialize(node,MustMayInitialize.MUST);

	Iterator it = varsOfInterest.iterator();
	while(it.hasNext()){
	    SootField interest = (SootField)it.next();

	    //check for constant value tags
	    Type fieldType = interest.getType();
	    if(fieldType instanceof DoubleType && interest.hasTag("DoubleConstantValueTag")){
		continue;
	    }
	    else if (fieldType instanceof FloatType && interest.hasTag("FloatConstantValueTag")){
		continue;
	    }
	    else if (fieldType instanceof LongType && interest.hasTag("LongConstantValueTag")){
		continue;
	    }
	    else if (fieldType instanceof CharType && interest.hasTag("IntegerConstantValueTag")){
		continue;
	    }
	    else if (fieldType instanceof BooleanType && interest.hasTag("IntegerConstantValueTag")){
		continue;
	    }
	    else if ( (fieldType instanceof IntType || fieldType instanceof ByteType || fieldType instanceof ShortType) && 
		      interest.hasTag("IntegerConstantValueTag")){
		continue;
	    }
	    else if(interest.hasTag("StringConstantValueTag")){
		continue;
	    }

	    if(must.isMustInitialized(interest)){
		//was initialized on all paths couldnt ask for more
		continue;
	    }

	    //System.out.println("SootField: "+interest+" not initialized. checking may analysis");
	    MustMayInitialize may = new MustMayInitialize(node,MustMayInitialize.MAY);
	    if(may.isMayInitialized(interest)){
		//System.out.println("It is initialized on some path just not all paths\n");
		List defs = must.getDefs(interest);
		if(defs == null)
		    throw new RuntimeException("Sootfield: " + interest+ " is mayInitialized but the defs is null");

		handleAssignOnSomePaths(node,interest,defs);
	    }
	    else{
		//not initialized on any path., assign default
		//System.out.println("Final field is not initialized on any path--------ASSIGN DEFAULT VALUE");
		assignDefault(node,interest);		    
	    }
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
		 * February 2nd, 2006: Laurie mentioned that apart from direct uses we also have to be conservative
		 * about method calls since we are dealing with fields here
		 * What if some method was invoked and it tried to use a field whose initialization we are about
		 * to delay. Since that should not happen we are going to check if there is any method call
		 * made from this point to the end of the method.
		 * Done by implementing a small analysis......however we need to first find the location of
		 * the initialization which we are about to delay...that is given by defs.get(0)
		 */
		MethodCallFinder myMethodCallFinder = new MethodCallFinder((GAssignStmt)defs.get(0));
		node.apply(myMethodCallFinder);

		if(myMethodCallFinder.anyMethodCalls()){
		    //there was some method call after the definition stmt so we cant continue
		    //remove the final modifier and leave
		    System.out.println("Method invoked somewhere after definition");
		    cancelFinalModifier.add(field);
		    return;
		}
		

		/*
		 * so lets recap till now what we know about this field
		 * Field is of interest meaning its definetly final
		 *
		 * It is not initialized on all paths
		 * It is however initialized on some path
		 * There is only one initialization of it
		 * The field is not used anywhere in this method
		 * And neither is there and method call after the original definition stmt
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


class MethodCallFinder extends DepthFirstAdapter{
    
    GAssignStmt def;
    boolean foundIt = false;
    boolean anyMethodCalls=false;


    public MethodCallFinder(GAssignStmt def){
	this.def=def;
    }


    public MethodCallFinder(boolean verbose,GAssignStmt def){
	super(verbose);
	this.def=def;
    }



    public void outDefinitionStmt(DefinitionStmt s){
	if(s instanceof GAssignStmt){
	    if(( (GAssignStmt)s).equals(def)){
		foundIt=true;
		System.out.println("Found it"+s);
	    }
	}
    }



    public void inInvokeExpr(InvokeExpr ie){
	System.out.println("In invoke Expr");
	if(foundIt){
	    System.out.println("oops invoking something after definition");
	    anyMethodCalls=true;
	}
    }



    /*
     * Method will return false if there were no method calls made after the definition stmt
     */
    public boolean anyMethodCalls(){
	return anyMethodCalls;
	//return false;
    }

}