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
 * CHANGE LOG: 30th January 2006: Class was created to get rid of the field
 * might not be initialized error that used to show up when recompiling
 * decompiled code Will be throughly covered in "Programmer Friendly Code" Sable
 * Tech Report (2006)
 * 
 */

/*
 * This class makes sure there is an initialization of all final variables
 * (static or non static). If we cant guarantee initialization (may be
 * initialized on multiple paths but not all) then we remove the final keyword
 */
public class FinalFieldDefinition {// extends DepthFirstAdapter{
	SootClass sootClass;

	SootMethod sootMethod;

	DavaBody davaBody;
	
	List cancelFinalModifier;

	public FinalFieldDefinition(ASTMethodNode node) {
		davaBody = node.getDavaBody();
		sootMethod = davaBody.getMethod();
		sootClass = sootMethod.getDeclaringClass();

		String subSignature = sootMethod.getName();
		if (!(subSignature.compareTo("<clinit>") == 0 || subSignature
				.compareTo("<init>") == 0)) {
			// dont care about these since we want only static block and
			// constructors
			// System.out.println("\n\nName"+sootMethod.getName()+"
			// SubSignature:"+sootMethod.getSubSignature());
			return;
		}

		// create a list of interesting vars
		ArrayList interesting = findFinalFields();
		if (interesting.size() == 0) {
			// no final fields of interest
			return;
		}

		cancelFinalModifier = new ArrayList();
		analyzeMethod(node, interesting);

		Iterator it = cancelFinalModifier.iterator();
		while (it.hasNext()) {
			SootField field = (SootField) it.next();
			field.setModifiers((soot.Modifier.FINAL ^ 0xFFFF)
					& field.getModifiers());
		}
	}

	/*
	 * this method finds all the final fields in this class and assigns them to
	 * the finalFields list
	 * 
	 * Note this stores a list of SootFields!!!
	 * 
	 * Fields which are initialized in their declaration should not be added
	 */
	public ArrayList findFinalFields() {

		// first thing is to get a list of all final fields in the class
		ArrayList interestingFinalFields = new ArrayList();

		Iterator fieldIt = sootClass.getFields().iterator();
		while (fieldIt.hasNext()) {
			SootField tempField = (SootField) fieldIt.next();
			if (tempField.isFinal()) {

				// if its static final and method is static add
				if (tempField.isStatic()
						&& sootMethod.getName().compareTo("<clinit>") == 0) {
					interestingFinalFields.add(tempField);
				}

				// if its non static and final and method is constructor add
				if ((!tempField.isStatic())
						&& sootMethod.getName().compareTo("<init>") == 0) {
					interestingFinalFields.add(tempField);
				}
			}
		}
		return interestingFinalFields;
	}

	public void analyzeMethod(ASTMethodNode node, List varsOfInterest) {
		MustMayInitialize must = new MustMayInitialize(node,
				MustMayInitialize.MUST);

		Iterator it = varsOfInterest.iterator();
		while (it.hasNext()) {
			SootField interest = (SootField) it.next();

			// check for constant value tags
			Type fieldType = interest.getType();
			if (fieldType instanceof DoubleType
					&& interest.hasTag("DoubleConstantValueTag")) {
				continue;
			} else if (fieldType instanceof FloatType
					&& interest.hasTag("FloatConstantValueTag")) {
				continue;
			} else if (fieldType instanceof LongType
					&& interest.hasTag("LongConstantValueTag")) {
				continue;
			} else if (fieldType instanceof CharType
					&& interest.hasTag("IntegerConstantValueTag")) {
				continue;
			} else if (fieldType instanceof BooleanType
					&& interest.hasTag("IntegerConstantValueTag")) {
				continue;
			} else if ((fieldType instanceof IntType
					|| fieldType instanceof ByteType || fieldType instanceof ShortType)
					&& interest.hasTag("IntegerConstantValueTag")) {
				continue;
			} else if (interest.hasTag("StringConstantValueTag")) {
				continue;
			}

			if (must.isMustInitialized(interest)) {
				// was initialized on all paths couldnt ask for more
				continue;
			}

			// System.out.println("SootField: "+interest+" not initialized.
			// checking may analysis");
			MustMayInitialize may = new MustMayInitialize(node,
					MustMayInitialize.MAY);
			if (may.isMayInitialized(interest)) {
				// System.out.println("It is initialized on some path just not
				// all paths\n");
				List defs = must.getDefs(interest);
				if (defs == null)
					throw new RuntimeException("Sootfield: " + interest
							+ " is mayInitialized but the defs is null");

				handleAssignOnSomePaths(node, interest, defs);
			} else {
				// not initialized on any path., assign default
				// System.out.println("Final field is not initialized on any
				// path--------ASSIGN DEFAULT VALUE");
				assignDefault(node, interest);
			}
		}
	}

	/*
	 * One gets to this method only if there was NO definition of a static final
	 * field in the static body At the same time no TAG with a constant value
	 * matched, so we know the static final was not initialized at declaration
	 * time If this happens: though it shouldnt unless u come from non-java
	 * compilers...insert default value initialization into the static
	 * method...right at the end to make things easy
	 */
	public void assignDefault(ASTMethodNode node, SootField f) {

		// create initialization stmt
		AugmentedStmt defaultStmt = createDefaultStmt(f);

		if (defaultStmt == null)
			return;

		List subBodies = (List) node.get_SubBodies();
		if (subBodies.size() != 1)
			throw new RuntimeException(
					"SubBodies size of method node not equal to 1");

		List body = (List) subBodies.get(0);

		// check if the bodys last node is an ASTStatementSequenceNode where we
		// might be able to add

		boolean done = false;
		if (body.size() != 0) {
			ASTNode lastNode = (ASTNode) body.get(body.size() - 1);
			if (lastNode instanceof ASTStatementSequenceNode) {
				List stmts = ((ASTStatementSequenceNode) lastNode)
						.getStatements();
				if (stmts.size() != 0) {
					Stmt s = ((AugmentedStmt) stmts.get(0)).get_Stmt();
					if (!(s instanceof DVariableDeclarationStmt)) {
						// can add statement here
						stmts.add(defaultStmt);

						ASTStatementSequenceNode newNode = new ASTStatementSequenceNode(
								stmts);
						// replace this node with the original node

						body.remove(body.size() - 1);
						body.add(newNode);

						node.replaceBody(body);
						done = true;
					}
				}
			}
		}
		if (!done) {
			List newBody = new ArrayList();
			newBody.add(defaultStmt);

			ASTStatementSequenceNode newNode = new ASTStatementSequenceNode(
					newBody);
			body.add(newNode);

			node.replaceBody(body);
		}

	}

	public AugmentedStmt createDefaultStmt(Object field) {

		Value ref = null;
		Type fieldType = null;
		if (field instanceof SootField) {
			// have to make a static field ref
			SootFieldRef tempFieldRef = ((SootField) field).makeRef();

			fieldType = ((SootField) field).getType();
			if (((SootField) field).isStatic())
				ref = new DStaticFieldRef(tempFieldRef, true);
			else
				ref = new DInstanceFieldRef(new JimpleLocal("this", fieldType),
						tempFieldRef, new HashSet());

		} else if (field instanceof Local) {
			ref = (Local) field;
			fieldType = ((Local) field).getType();
		}

		GAssignStmt assignStmt = null;

		if (fieldType instanceof RefType) {
			assignStmt = new GAssignStmt(ref, NullConstant.v());
		} else if (fieldType instanceof DoubleType) {
			assignStmt = new GAssignStmt(ref, DoubleConstant.v(0));
		} else if (fieldType instanceof FloatType) {
			assignStmt = new GAssignStmt(ref, FloatConstant.v(0));
		} else if (fieldType instanceof LongType) {
			assignStmt = new GAssignStmt(ref, LongConstant.v(0));
		} else if (fieldType instanceof IntType
				|| fieldType instanceof ByteType
				|| fieldType instanceof ShortType
				|| fieldType instanceof CharType
				|| fieldType instanceof BooleanType) {

			assignStmt = new GAssignStmt(ref, DIntConstant.v(0, fieldType));
		}

		if (assignStmt != null) {
			// System.out.println("AssignStmt is"+assignStmt);
			AugmentedStmt as = new AugmentedStmt(assignStmt);
			return as;
		} else
			return null;

	}

	/*
	 * A sootfield gets to this method if it was an interesting field i.e static
	 * final for clinit and only final but non static for init and there was
	 * atleast one place that this var was defined but it was not defined on all
	 * paths and hence the recompilation will result in an error
	 * 
	 * try{ staticFinal = defined; } catch(Exception e){}
	 */

	public void handleAssignOnSomePaths(ASTMethodNode node, SootField field,
			List defs) {
	
		if (defs.size() != 1) {
			// give up by removing "final" if there are more than one defs
			cancelFinalModifier.add(field);
		} else {
			// if there is only one definition 

			// see if there is no use of def
			AllVariableUses varUses = new AllVariableUses(node);
			node.apply(varUses);

			List allUses = varUses.getUsesForField(field);

			if (allUses != null && allUses.size() != 0) {
				/*
				 * if the number of uses is not 0 then we dont want to get into
				 * trying to delay initialization just before assignment.
				 * Easier to remove "final"
				 */
				cancelFinalModifier.add(field);
			} 
			else {
				/*
				 * we have a final field with 1 def and 0 uses but is not initialized on all paths
				 * we can try to delay initialization using an indirect approach
				 *                         STMT0        TYPE DavaTemp_fieldName;            
				 *                         STMT1        DavaTemp_fieldname = DEFAULT
				 * try{                                  try{
				 *     field = ...         STMT2            DavaTemp_fieldname = ... 
				 *    }                                         X
 				 *    catch(...){                        }catch(..){
				 *       ....                                   ....
				 *    }                                  }
				 *                        STMT3          field = Dava_tempVar
				 * 
				 * Notice the following code will try to place the field assignment
				 * as close to the original assignment as possible.
				 * 
				 * TODO: However there might still be issues with delaying this assignment
				 * e.g. what if the place marked by X (more specifically between
				 * the original def and the new def includes a method invocation
				 * which access the delayed field.
				 * 
				 * Original Comment February 2nd, 2006: Laurie mentioned that apart from direct
				 * uses we also have to be conservative about method calls since
				 * we are dealing with fields here What if some method was
				 * invoked and it tried to use a field whose initialization we
				 * are about to delay. This can be done by implementing a small
				 * analysis. (See end of this class file. 	
				 * 	 	
				 *  TODO: SHOULD BE CHECKED FOR CODE BETWEEN THE OLD DEF AND THE NEW ASSIGNMENT
				 *        Currently checks from some point till end of method
				 *
				 * MethodCallFinder myMethodCallFinder = new MethodCallFinder( (GAssignStmt) defs.get(0));
				 * node.apply(myMethodCallFinder);
				 * if (myMethodCallFinder.anyMethodCalls()) {
				 *		// there was some method call after the definition stmt so
				 *  	// we cant continue
				 *	 	 // remove the final modifier and leave
				 *	 	  //System.out.println("Method invoked somewhere after definition");
				 *	 	   cancelFinalModifier.add(field);
				 *	 	   return;
				 * 	 	   }
				 */
				
				
				
				// Creating STMT0
				Type localType = field.getType();
				Local newLocal = new JimpleLocal("DavaTemp_" + field.getName(),localType);

				DVariableDeclarationStmt varStmt = new DVariableDeclarationStmt(localType,davaBody);
				
				varStmt.addLocal(newLocal);
				AugmentedStmt as = new AugmentedStmt(varStmt);

				// System.out.println("Var Decl stmt"+as);

				// STORE IT IN Methods Declaration Node
				ASTStatementSequenceNode declNode = node.getDeclarations();
				List stmts = declNode.getStatements();
				stmts.add(as);
    			
				declNode = new ASTStatementSequenceNode(stmts);

				List subBodies = (List) node.get_SubBodies();
				if (subBodies.size() != 1)
					throw new DecompilationException("ASTMethodNode does not have one subBody");

				List body = (List) subBodies.get(0);

				body.remove(0);
				body.add(0, declNode);

				node.replaceBody(body);

				node.setDeclarations(declNode);

				
				// STMT1 initialization
				AugmentedStmt initialization = createDefaultStmt(newLocal);
				/*
				 * The first node in a method is the declarations
				 * we know there is a second node because originaly the
				 * field was initialized on some path
				 */
				if (body.size() < 2)
					throw new RuntimeException("Size of body is less than 1");

				/* 
				 * If the second node is a stmt seq we put STMT1 there
				 * otherwise we create a new stmt seq node
				 */ 

				ASTNode nodeSecond = (ASTNode) body.get(1);
				if (nodeSecond instanceof ASTStatementSequenceNode) {
					// the second node is a stmt seq node just add the stmt here
					List stmts1 = ((ASTStatementSequenceNode) nodeSecond).getStatements();
					stmts1.add(initialization);
					nodeSecond = new ASTStatementSequenceNode(stmts1);
					// System.out.println("Init added in exisiting node");
					body.remove(1);
				} else {
					//System.out.println("had to add new node");
					List tempList = new ArrayList();
					tempList.add(initialization);
					nodeSecond = new ASTStatementSequenceNode(tempList);
				}
				body.add(1, nodeSecond);
				node.replaceBody(body);

				
				
				//STMT2
				//done by simply replacing the leftop in the original stmt
				((GAssignStmt) defs.get(0)).setLeftOp(newLocal);
				
				
				//STMT3
				
				// have to make a field ref
				SootFieldRef tempFieldRef = ((SootField) field).makeRef();

				Value ref;
				if (field.isStatic())
					ref = new DStaticFieldRef(tempFieldRef, true);
				else {
					ref = new DInstanceFieldRef(new JimpleLocal("this", field
							.getType()), tempFieldRef, new HashSet());
					// throw new RuntimeException("STOPPED");
				}

				GAssignStmt assignStmt = new GAssignStmt(ref, newLocal);
				AugmentedStmt assignStmt1 = new AugmentedStmt(assignStmt);



				/*
				 * 14th February 2006
				 * Should add this statement to the first place in the code where
				 * we will have a mustInitialize satisfied
				 */

				//the def is at (GAssignStmt) defs.get(0)
				//its parent is ASTStatementSequence and its parent is now needed
				soot.dava.toolkits.base.AST.traversals.ASTParentNodeFinder parentFinder = 
					new soot.dava.toolkits.base.AST.traversals.ASTParentNodeFinder();
				node.apply(parentFinder);
				
				
				Object parent = parentFinder.getParentOf(defs.get(0));
				if(!(parent instanceof ASTStatementSequenceNode)){
					throw new DecompilationException("Parent of stmt was not a stmt seq node");
				}
				
				Object grandParent = parentFinder.getParentOf(parent);
				if(grandParent == null){
					throw new DecompilationException("Parent of stmt seq node was null");
				}
				
				//so we have the parent stmt seq node and the grandparent node
				//so it is the grandparent which is causing the error in MUSTINitialize
				//we should move our assign right after the grandParent is done
				
				
				MustMayInitialize must = new MustMayInitialize(node,MustMayInitialize.MUST);
				while(!must.isMustInitialized(field)) {
					
					//System.out.println("not must initialized");
					Object parentOfGrandParent = parentFinder.getParentOf(grandParent);
					if( !(grandParent instanceof ASTMethodNode) && parentOfGrandParent == null){
						throw new DecompilationException("Parent of non method node was null");
					}
					boolean notResolved=false;
					// look for grandParent in parentOfGrandParent
					ASTNode ancestor = (ASTNode)parentOfGrandParent;
					List ancestorBodies = ancestor.get_SubBodies();
					Iterator it = ancestorBodies.iterator();
					while(it.hasNext()){
						List ancestorSubBody = null;
					
						if (ancestor instanceof ASTTryNode)
							ancestorSubBody = (List) ((ASTTryNode.container) it.next()).o;
						else
							ancestorSubBody = (List) it.next();
					 
						if(ancestorSubBody.indexOf(grandParent) > -1) {
							//grandParent is present in this body
							int index = ancestorSubBody.indexOf(grandParent); 
							//check the next index
						
							if(index+1 < ancestorSubBody.size() && ancestorSubBody.get(index+1) instanceof ASTStatementSequenceNode ){
								//there is an stmt seq node node after the grandParent
								ASTStatementSequenceNode someNode = (ASTStatementSequenceNode)ancestorSubBody.get(index+1);
							 
								//add the assign stmt here
							 
								List stmtsLast = ((ASTStatementSequenceNode) someNode).getStatements();
								List newStmts = new ArrayList();
								newStmts.add(assignStmt1);
								newStmts.addAll(stmtsLast);
								someNode.setStatements(newStmts);
								//System.out.println("here1");
								//check if problem is solved else remove the assign and change parents
								must = new MustMayInitialize(node,MustMayInitialize.MUST);
								if(!must.isMustInitialized(field)){
									//problem not solved remove the stmt just added
									someNode.setStatements(stmtsLast);
									notResolved=true;
								}
							}
							else{
								//create a new stmt seq node and add it here
									List tempList = new ArrayList();
									tempList.add(assignStmt1);
									ASTStatementSequenceNode lastNode = new ASTStatementSequenceNode(tempList);
									ancestorSubBody.add(index+1,lastNode);
									//node.replaceBody(body);
									//System.out.println("here2");
									//check if problem is solved else remove the assign and change parents
									must = new MustMayInitialize(node,MustMayInitialize.MUST);
									if(!must.isMustInitialized(field)){
										//problem not solved remove the stmt just added
										ancestorSubBody.remove(index+1);
										notResolved=true;
									}									
							}
							break;//break the loop going through subBodies
						} //if ancestor was found
							 	
					 }	//next subBody
					if(notResolved){
						//meaning we still dont have must initialization 
						//we should put assign in one level above than current
						grandParent = parentFinder.getParentOf(grandParent);
						//System.out.println("Going one level up");						
					}									
				}//while ! ismustinitialized
			}
		}
	}
}



/*
 * TODO: Change the analysis below to find method calls between GAssignStmt def
 * and the new Assign Stmt.
 */

class MethodCallFinder extends DepthFirstAdapter {

	GAssignStmt def;

	boolean foundIt = false;

	boolean anyMethodCalls = false;

	public MethodCallFinder(GAssignStmt def) {
		this.def = def;
	}

	public MethodCallFinder(boolean verbose, GAssignStmt def) {
		super(verbose);
		this.def = def;
	}

	public void outDefinitionStmt(DefinitionStmt s) {
		if (s instanceof GAssignStmt) {
			if (((GAssignStmt) s).equals(def)) {
				foundIt = true;
				//System.out.println("Found it" + s);
			}
		}
	}

	public void inInvokeExpr(InvokeExpr ie) {
		//System.out.println("In invoke Expr");
		if (foundIt) {
			//System.out.println("oops invoking something after definition");
			anyMethodCalls = true;
		}
	}

	/*
	 * Method will return false if there were no method calls made after the
	 * definition stmt
	 */
	public boolean anyMethodCalls() {
		return anyMethodCalls;
		//return false;
	}

}