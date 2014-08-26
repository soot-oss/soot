/* Soot - a J*va Optimization Framework
 * Copyright (C) 2005-2006 Nomair A. Naeem (nomair.naeem@mail.mcgill.ca)
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





/*
 * Deal with the problem that super has to be the first stmt in a method body
 * The problem is as follows:
 * Suppose you had a call to super with some complicated arguments
 * or suppose an Aspect added some preinitialization
 * In both cases some code gets decompiled BEFORE the call to super
 * this is just initializing the arguments.
 * However the call to super() has to be the first stmt
 * 
 *
 * January 23rd 2006 Writing New Algorithm...
 * 
 *  Original constructor         ONE:Changed Constructor
 *
 *  B(args1){                       B(args1){
 *    ----------                       this(..args1..,B.preInit1(args1));
 *  X ----------                    }  
 *    ----------
 *    super(args2);
 *    ----------
 *  Y ----------
 *    ----------
 *
 * 
 ********************************************************************
 *  New method in Class being Decompiled
 *  
 *  private static preInit1(args1){
 *    
 *      ----------
 *    X ----------
 *      ----------
 *  
 *    DavaSuperHandler handler = new DavaSuperHandler();
 *    //code to evaluate all args in args2
 *  
 *    //evaluate 1st arg in args2
 *     ---------
 *    handler.store(firstArg);
 *
 *    //evaluate 2nd arg in args2
 *     ---------
 *    handler.store(secondArg);
 *
 *    //AND SO ON TILL ALL ARGS ARE FINISHED
 *  
 *    return handler;
 *  } 
 *
 *  
 ********************************************************************
 *  New Constructor Introduced
 *
 * B(..args1.., DavaSuperHandler handler){
 *
 *    super(
 *         (CAST-TYPE)handler.get(0),
 *         ((CAST-TYPE)handler.get(1)).CONVERSION(),
 *          .......);
 *
 *    ----------
 *  Y ----------
 *    ----------
 * }
 *
 *  
 ***********************************************************************
 *  New Class Created  (Inner class will work best)
 * 
 *  class DavaSuperHandler{
 *     Vector myVector = new Vector();
 *
 *     public Object get(int pos){
 *        return myVector.elementAt(pos);
 *     }
 *
 *     public void store(Object obj){
 *           myVector.add(obj);
 *     }
 *  }
 * 
 ***********************************************************************
 *
 *  
 *
 * ONE: Important to check that the parent of the call to super is the ASTMethodNode
 *      This is important because of the super call is nested within some control flow
 *      we cannot simply remove the super call (Although this shouldnt happen but just for completness)
 *
 * TWO: It is necessary to run on AST Analysis on the new SootMethod since 	
 *      the DavaBody usually invokes these anlayses
 *      but our newly created method will never go through that process
 *
 */


package soot.dava.toolkits.base.AST.transformations;

import java.util.*;

import soot.*;
import soot.jimple.*;
import soot.jimple.internal.*;
import soot.dava.*;
import soot.grimp.internal.*;
import soot.dava.internal.AST.*;
import soot.dava.internal.asg.*;
import soot.dava.internal.javaRep.*;
import soot.dava.toolkits.base.AST.analysis.*;
import soot.dava.toolkits.base.AST.traversals.*;


public class SuperFirstStmtHandler extends DepthFirstAdapter{
    
	public final boolean DEBUG = false;
	
    ASTMethodNode originalASTMethod;    //contains the entire method which was being decompiled
    DavaBody originalDavaBody;  //originalASTMethod.getDavaBody
    Unit originalConstructorUnit;    //contains this.init<>
    InstanceInvokeExpr originalConstructorExpr;    //contains the expr within the ConstructorUni
    SootMethod originalSootMethod; //originalDavaBody.getMethod();
    SootClass originalSootClass; //originalSootMethod.getDeclaringClass();
    Map originalPMap;
    
    
    List argsOneTypes=null; //initialized to the ParameterTypes of the original method
    List argsOneValues=null; //initialized to the values of these parameters of the original method

    List argsTwoValues=null; //initialized to the Parameter values of the call to super
    List argsTwoTypes=null; //initialized to the ParameterTypes of the call to super
    
    //PreInit fields
    SootMethod newSootPreInitMethod=null;//only initialized by createMethod
    DavaBody newPreInitDavaBody=null; //only initialized by createMethod
    ASTMethodNode newASTPreInitMethod=null; //only initialized by createNewASTPreInitMethod
    
    //Constructor fields
    SootMethod newConstructor = null; //initialized by createNewConstructor
    DavaBody newConstructorDavaBody=null; //initialized by createNewConstructor
    ASTMethodNode newASTConstructorMethod=null; //only initialized by createNewASTConstructor


    List<Local> mustInitialize;
    int mustInitializeIndex=0;

    public SuperFirstStmtHandler(ASTMethodNode AST){
    	this.originalASTMethod=AST;
    	initialize();
    }

    public SuperFirstStmtHandler(boolean verbose,ASTMethodNode AST){
    	super(verbose);
    	this.originalASTMethod=AST;
    	initialize();
    }

    public void initialize(){
    	originalDavaBody        = originalASTMethod.getDavaBody();
    	originalConstructorUnit = originalDavaBody.get_ConstructorUnit();

    	originalConstructorExpr = originalDavaBody.get_ConstructorExpr();
    	if(originalConstructorExpr != null){
    		//should get the values and the types of these into lists for later use
    		argsTwoValues = originalConstructorExpr.getArgs();

    		argsTwoTypes = new ArrayList();

    		//get also the types of these args
    		Iterator valIt = argsTwoValues.iterator();
    		while(valIt.hasNext()){
    			Value val = (Value)valIt.next();
    			Type type = val.getType();
    			argsTwoTypes.add(type);
    		}
    	}


    	originalSootMethod      = originalDavaBody.getMethod();
    	originalSootClass       = originalSootMethod.getDeclaringClass();

    	originalPMap            = originalDavaBody.get_ParamMap();
	
    	argsOneTypes            = originalSootMethod.getParameterTypes();

    	//initialize argsOneValues
    	argsOneValues = new ArrayList();
    	Iterator typeIt = argsOneTypes.iterator();
    	int count = 0;
    	while (typeIt.hasNext()) {
    		Type t = (Type) typeIt.next();
    		argsOneValues.add(originalPMap.get(new Integer(count)));
    		count++;
    	}
    }



    /*
     * looking for an init stmt
     */
    public void inASTStatementSequenceNode(ASTStatementSequenceNode node){
    	List<Object> stmts = node.getStatements();
    	Iterator<Object> it = stmts.iterator();
    	while(it.hasNext()){
    		AugmentedStmt as = (AugmentedStmt)it.next();
    		Unit u = as.get_Stmt();
    		
    		if (u == originalConstructorUnit){
    			//System.out.println("Found the constructorUnit"+u);

    			//ONE: make sure the parent of the super() call is an ASTMethodNode
    			ASTParentNodeFinder parentFinder = new ASTParentNodeFinder();
    			originalASTMethod.apply(parentFinder);

    			Object tempParent = parentFinder.getParentOf(node);
    			if( tempParent != originalASTMethod){
    				//System.out.println("ASTMethod node is not the parent of constructorUnit");
    				//notice since we cant remove one call of super there is no point
    				//in trying to remove any other calls to super
    				removeInit();
    				return;
    			}

		//only gets here if the call to super is not nested within some other construct

		/**********************************************************/
		/****************** CREATING PREINIT METHOD ***************/
		/**********************************************************/
		
		//CREATE UNIQUE METHOD
		createSootPreInitMethod(); //new method is initalized in newSootPreInitMethod
		
		//Create ASTMethodNode for this SootMethod
		createNewASTPreInitMethod(node);  //the field newASTPreInitMethod is initialized

		//newASTPreInitMethod should be non null if we can go ahead
		
		if(newASTPreInitMethod == null){
		    //could not create ASTMethodNode for some reason or the other
		    //just silently return
		    //System.out.println(">>>>>>>>>>>>>>>>Couldnt not create ASTMethodNode for the new PreInitMethod");
		    removeInit();
		    return;
		}

		if(!finalizePreInitMethod()){
		    //shouldnt be creating PreInit
		    //System.out.println(">>>>>>>>>>>>>>SHOULDNT BE CREATING PREINIT");
		    removeInit();
		    return;
		}



		/**********************************************************/
		/************** CREATING NEW CONSTRUCTOR ******************/
		/**********************************************************/
		
		//create SootMethod for the constructor
		createNewConstructor();
		
		createNewASTConstructor(node);

		if(!createCallToSuper()){
		    //could not create call to super
		    //still safe to simply exit
		    //System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>Could not create call to super...SuperFirstStmtHandler");
		    removeInit();
		    return;
		}
		finalizeConstructor();



		/**********************************************************/
		/************** CHANGE ORIGINAL CONSTRUCTOR ***************/
		/**********************************************************/
		if(changeOriginalAST()){
		    //System.out.println("Done Done Done");

			debug("SuperFirstStmtHandler....inASTStatementSeuqneNode","Added PreInit");
		    G.v().SootMethodAddedByDava=true;
		    G.v().SootMethodsAdded.add(newSootPreInitMethod);
		    G.v().SootMethodsAdded.add(newConstructor);


		    /**********************************************************/
		    /****************** CREATING INNER CLASS ******************/
		    /**********************************************************/
		    
		    //notice that inner class is created by DavaPrinter in the printTo method
		    //all we do is set a Global to true and later on when the SootClass is being
		    //output the inner class will be output also
		    G.v().SootClassNeedsDavaSuperHandlerClass.add(originalSootClass);

		    //System.out.println("\n\nSet SootMethodAddedByDava to true\n\n");
		}
		

	    }
	}
    } 

    /*
     * When we havent created the indirection to take care of super bug be it cos we dont need to
     * or that we CANT cos of some limitation....should atleast remove the jimple this.init call from the
     * statements
     */
    public void removeInit(){
    	//remove constructorUnit from originalASTMethod
    	List<Object> newBody = new ArrayList<Object>();
	
    	List<Object> subBody = originalASTMethod.get_SubBodies();
    	if(subBody.size()!=1)
    		return;

    	List oldBody = (List)subBody.get(0);
    	Iterator oldIt = oldBody.iterator();
    	while(oldIt.hasNext()){
    		//going through each node in the old body
    		ASTNode node = (ASTNode)oldIt.next();

    		//copy the node as is unless its an ASTStatementSequence
    		if(!(node instanceof ASTStatementSequenceNode)){
    			newBody.add(node);
    			continue;
    		}

    		//if the node is an ASTStatementSequenceNode
    		//copy all stmts unless it is a constructorUnit
    		ASTStatementSequenceNode seqNode = (ASTStatementSequenceNode)node;
    		
    		List<Object> newStmtList = new ArrayList<Object>();

    		List<Object> stmts = seqNode.getStatements();
    		Iterator<Object> it = stmts.iterator();
    		while(it.hasNext()){
    			AugmentedStmt augStmt = (AugmentedStmt)it.next();
    			Stmt stmtTemp = augStmt.get_Stmt();
    			if(stmtTemp == originalConstructorUnit){
    				//do nothing
    			}
    			else{
    				newStmtList.add(augStmt);
    			}
    		}
    		if(newStmtList.size()!=0){
    			newBody.add(new ASTStatementSequenceNode(newStmtList));
    		}
    	}
    	originalASTMethod.replaceBody(newBody);
    }






    /*
     * Remove the entire body and replace with the statement
     * this(args1, B.preInit(args1));
     */
    public boolean changeOriginalAST(){
	//originalDavaBody has to be changed
	
	//fix up the call within the constructorUnit....the args should be argsOne followed by a method call to preInit
	if(originalConstructorExpr == null){
	    //hmm that means there was no call to super in the original code
	    //System.out.println("originalConstructorExpr is null");
	    return false;
	}

	List thisArgList = new ArrayList();
	thisArgList.addAll(argsOneValues);

	DStaticInvokeExpr newInvokeExpr = new DStaticInvokeExpr(newSootPreInitMethod.makeRef(),argsOneValues);
	thisArgList.add(newInvokeExpr);
	

	//the methodRef of themethod to be called is the new constructor we created
	InstanceInvokeExpr tempExpr = 
	    new DSpecialInvokeExpr(originalConstructorExpr.getBase(),newConstructor.makeRef(),thisArgList);

	originalDavaBody.set_ConstructorExpr(tempExpr);
    
	//create Invoke Stmt with tempExpr as the expression
	GInvokeStmt s = new GInvokeStmt(tempExpr);
	
	originalDavaBody.set_ConstructorUnit(s);

	//originalASTMethod has to be made empty
	originalASTMethod.setDeclarations(new ASTStatementSequenceNode(new ArrayList<Object>()));
	originalASTMethod.replaceBody(new ArrayList<Object>());
	return true;

    }

    private SootMethodRef makeMethodRef(String methodName,ArrayList args){
	//make MethodRef for methodName
	
	SootMethod method = new SootMethod(methodName,args,RefType.v("java.lang.Object"));

 	//set the declaring class of new method to be the DavaSuperHandler class
 	method.setDeclaringClass(new SootClass("DavaSuperHandler"));
	return method.makeRef();
    }


    /*
     *    super(
     *         (CAST-TYPE)handler.get(0),
     *         ((CAST-TYPE)handler.get(1)).CONVERSION(),
     *          .......);
     * 
     * returns false if we cant create the call to super
     */     
    private boolean createCallToSuper(){
	
    	//check that whether this call is even to be made or not
    	if (originalConstructorExpr == null) {
    		//hmm that means there was no call to super in the original code
    		//System.out.println("originalConstructorExpr is null");
    		return false;
    	}
    	//System.out.println("ConstructorExpr is non null...call to super has to be made");

    	//find the parent class of the current method being decompiled
    	SootClass parentClass =originalSootClass.getSuperclass();
	
    	//retrieve the constructor of the super class that we want to call
    	//remember argsTwoTypes contains the ParameterType to the call to the super method
    	if(!(parentClass.declaresMethod("<init>",argsTwoTypes))){
    		//System.out.println("parentClass does not have a constructor with this name and ParamTypes");
    		return false;
    	}

    	SootMethod superConstructor = parentClass.getMethod("<init>",argsTwoTypes);

    	//create InstanceInvokeExpr

    	//need Value base: this??
    	//need SootMethod Ref...make sootmethoref of the super constructor found
    	//need list of thisLocals.........try empty arrayList since it doesnt seem to be used anywhere??

    	List argsForConstructor = new ArrayList();
    	int count=0;

    	//have to make arg as "handler.get(0)"

    	//create new ReftType for DavaSuperHandler
    	RefType type = (new SootClass("DavaSuperHandler")).getType();

    	//make JimpleLocal to be used in each arg
    	Local jimpleLocal = new JimpleLocal("handler",type);//takes care of handler

    	//make reference to a method of name get takes one int arg belongs to DavaSuperHandler
    	ArrayList tempList = new ArrayList();
    	tempList.add(IntType.v());
    	SootMethodRef getMethodRef = makeMethodRef("get",tempList);
    	
    	List tempArgList = null;

    	Iterator typeIt = argsTwoTypes.iterator();
    	while(typeIt.hasNext()){
    		Type tempType = (Type)typeIt.next();

    		DIntConstant arg = DIntConstant.v(count,IntType.v());//takes care of the index
    		count++;
    		tempArgList = new ArrayList();
    		tempArgList.add(arg);
    		
    		DVirtualInvokeExpr tempInvokeExpr = 	
    			new DVirtualInvokeExpr(jimpleLocal,getMethodRef,tempArgList,new HashSet<Object>());

    		//NECESASARY CASTING OR RETRIEVAL OF PRIM TYPES TO BE DONE HERE
    		Value toAddExpr = getProperCasting(tempType,tempInvokeExpr);
    		if(toAddExpr == null)
    			throw new DecompilationException("UNABLE TO CREATE TOADDEXPR:"+tempType);

    		//the above virtualInvokeExpr is one of the args for the constructor
    		argsForConstructor.add(toAddExpr);
    	}
    	mustInitializeIndex=count;
    	    	
    	//we are done with creating all necessary args to the virtualinvoke expr constructor

    	DVirtualInvokeExpr virtualInvoke = new DVirtualInvokeExpr(originalConstructorExpr.getBase(),superConstructor.makeRef(),
    			argsForConstructor,new HashSet<Object>());

    	//set the constructors constructorExpr
    	newConstructorDavaBody.set_ConstructorExpr(virtualInvoke);

    	//create Invoke Stmt with virtualInvoke as the expression
    	GInvokeStmt s = new GInvokeStmt(virtualInvoke);
    	
    	newConstructorDavaBody.set_ConstructorUnit(s);

    	//return true if super call created
    	return true;
    }

    
    
    
    
    
    
    public Value getProperCasting(Type tempType,DVirtualInvokeExpr tempInvokeExpr){
		if(tempType instanceof RefType){
			//System.out.println("This is a reftype:"+tempType);
			return new GCastExpr(tempInvokeExpr,tempType);
		}
		else if(tempType instanceof PrimType){
			PrimType t = (PrimType)tempType;
			//BooleanType, ByteType, CharType, DoubleType, FloatType, IntType, LongType, ShortType
			
			if (t == BooleanType.v()){
				Value tempExpr = new GCastExpr(tempInvokeExpr,RefType.v("java.lang.Boolean"));
				//booleanValue

				SootMethod tempMethod = new SootMethod("booleanValue",new ArrayList(),BooleanType.v());
				tempMethod.setDeclaringClass(new SootClass("java.lang.Boolean"));

				SootMethodRef tempMethodRef = tempMethod.makeRef();
				return	new DVirtualInvokeExpr(tempExpr,tempMethodRef,new ArrayList(),new HashSet<Object>());
			}
			else if (t == ByteType.v()){
				Value tempExpr = new GCastExpr(tempInvokeExpr,RefType.v("java.lang.Byte"));	
				//byteValue

				SootMethod tempMethod = new SootMethod("byteValue",new ArrayList(),ByteType.v());
				tempMethod.setDeclaringClass(new SootClass("java.lang.Byte"));

				SootMethodRef tempMethodRef = tempMethod.makeRef(); 
					return new DVirtualInvokeExpr(tempExpr,tempMethodRef,new ArrayList(),new HashSet<Object>());
			}
            else if (t == CharType.v()){
            	Value tempExpr = new GCastExpr(tempInvokeExpr,RefType.v("java.lang.Character"));
            	//charValue
            	
            	SootMethod tempMethod = new SootMethod("charValue",new ArrayList(),CharType.v());
            	tempMethod.setDeclaringClass(new SootClass("java.lang.Character"));

            	SootMethodRef tempMethodRef = tempMethod.makeRef(); 	
            	return new DVirtualInvokeExpr(tempExpr,tempMethodRef,new ArrayList(),new HashSet<Object>());
            }
            else if (t == DoubleType.v()){
            	Value tempExpr = new GCastExpr(tempInvokeExpr,RefType.v("java.lang.Double"));
            	//doubleValue

            	SootMethod tempMethod = new SootMethod("doubleValue",new ArrayList(),DoubleType.v());
            	tempMethod.setDeclaringClass(new SootClass("java.lang.Double"));

            	SootMethodRef tempMethodRef = tempMethod.makeRef();
            	return	new DVirtualInvokeExpr(tempExpr,tempMethodRef,new ArrayList(),new HashSet<Object>());
            }
            else if (t == FloatType.v()){
            	Value tempExpr = new GCastExpr(tempInvokeExpr,RefType.v("java.lang.Float"));
            	//floatValue
            	
            	SootMethod tempMethod = new SootMethod("floatValue",new ArrayList(),FloatType.v());
            	tempMethod.setDeclaringClass(new SootClass("java.lang.Float"));
            	
            	SootMethodRef tempMethodRef = tempMethod.makeRef();
            	return new DVirtualInvokeExpr(tempExpr,tempMethodRef,new ArrayList(),new HashSet<Object>());
            }
            else if (t == IntType.v()){
            	Value tempExpr = new GCastExpr(tempInvokeExpr,RefType.v("java.lang.Integer"));
            	//intValue

            	SootMethod tempMethod = new SootMethod("intValue",new ArrayList(),IntType.v());
            	tempMethod.setDeclaringClass(new SootClass("java.lang.Integer"));

            	SootMethodRef tempMethodRef = tempMethod.makeRef();
            	return	new DVirtualInvokeExpr(tempExpr,tempMethodRef,new ArrayList(),new HashSet<Object>());
            }
            else if (t == LongType.v()){
            	Value tempExpr = new GCastExpr(tempInvokeExpr,RefType.v("java.lang.Long"));
            	//longValue

            	SootMethod tempMethod = new SootMethod("longValue",new ArrayList(),LongType.v());
            	tempMethod.setDeclaringClass(new SootClass("java.lang.Long"));

            	SootMethodRef tempMethodRef = tempMethod.makeRef();
            	return	new DVirtualInvokeExpr(tempExpr,tempMethodRef,new ArrayList(),new HashSet<Object>());
            }
            else if (t == ShortType.v()){
            	Value tempExpr = new GCastExpr(tempInvokeExpr,RefType.v("java.lang.Short"));
            	//shortValue
            	
            	SootMethod tempMethod = new SootMethod("shortValue",new ArrayList(),ShortType.v());
            	tempMethod.setDeclaringClass(new SootClass("java.lang.Short"));
            	
            	SootMethodRef tempMethodRef = tempMethod.makeRef();
            	return	new DVirtualInvokeExpr(tempExpr,tempMethodRef,new ArrayList(),new HashSet<Object>());
            }
            else {
            	throw new DecompilationException("Unhandle primType:"+tempType);
            }
		}
		else{
			throw new DecompilationException("The type:"+tempType+" was not a reftye or primtype. PLEASE REPORT.");
		}

    }
    private void finalizeConstructor(){
    	//set davaBody...totally redundant but have to do as this is checked by toString of ASTMethodNode
    	newASTConstructorMethod.setDavaBody(newConstructorDavaBody);

    	newConstructorDavaBody.getUnits().clear();
    	newConstructorDavaBody.getUnits().addLast(newASTConstructorMethod);
	
    	System.out.println("Setting declaring class of method"+newConstructor.getSubSignature());
    	newConstructor.setDeclaringClass(originalSootClass);
    }


    //method should return false if the PreInit body(ASTBody that is) is empty meaning there is no need to create it all
    private boolean finalizePreInitMethod(){
    	//set davaBody...totally redundant but have to do as this is checked by toString of ASTMethodNode
    	newASTPreInitMethod.setDavaBody(newPreInitDavaBody);

    	//newPreInitDavaBody is the active body
    	newPreInitDavaBody.getUnits().clear();
    	newPreInitDavaBody.getUnits().addLast(newASTPreInitMethod);
			    
    	//check whether there is something in side the ASTBody
    	//if its empty (maybe there were only declarations and that got removed
    	//then no point in creating the preInit method
    	List<Object> subBodies = newASTPreInitMethod.get_SubBodies();
    	if(subBodies.size()!=1)
    		return false;
    	
    	List body = (List)subBodies.get(0);

    	//body is NOT allowed to contain one declaration node with whatever in it
    	//after that it is NOT allowed all ASTStatement nodes with empty bodies

    	Iterator it = body.iterator();
    	boolean empty = true; //indicating that method is empty
    	
    	while(it.hasNext()){
    		ASTNode tempNode = (ASTNode)it.next();
    		if(!(tempNode instanceof ASTStatementSequenceNode)){
    			//found some node other than stmtseq...body not empty return true
    			empty = false;
    			break;
    		}

    		List<Object> stmts = ((ASTStatementSequenceNode)tempNode).getStatements();		    

    		//all declaration stmts are allowed
    		Iterator<Object> stmtIt = stmts.iterator();
    		while(stmtIt.hasNext()){
    			AugmentedStmt as = (AugmentedStmt)stmtIt.next();
    			Stmt s = as.get_Stmt();
    			if(!(s instanceof DVariableDeclarationStmt)){
    				empty=false;
    				break;
    			}
    		}
    		if(!empty)
    			break;
    	}

    	if(empty){
    		//System.out.println("Method is empty not creating it");
    		return false;//should not be creating the method
    	}
	     
    	//about to return true enter all DavaSuperHandler stmts to make it part of the preinit method
    	createDavaStoreStmts();
    	return true;
    }






    public void createNewASTConstructor(ASTStatementSequenceNode initNode){

    	List<Object> newConstructorBody = new ArrayList<Object>();
    	List<Object> newStmts = new ArrayList<Object>();
    	/*
    	 * add any definitions to live variables that might be in body X
    	 * 	
    	 */
    	//we have gotten argsTwoType size() out of the handler so thats the index count

    	//mustInitialize has the live variables that need to be initialized
		
    	//	create new ReftType for DavaSuperHandler
    	RefType type = (new SootClass("DavaSuperHandler")).getType();

    	//make JimpleLocal to be used in each arg
    	Local jimpleLocal = new JimpleLocal("handler",type);//takes care of handler
    	
    	//make reference to a method of name get takes one int arg belongs to DavaSuperHandler
    	ArrayList tempList = new ArrayList();
    	tempList.add(IntType.v());
    	SootMethodRef getMethodRef = makeMethodRef("get",tempList);

    	//Iterator typeIt = argsTwoTypes.iterator();
    	if(mustInitialize != null){
    		Iterator<Local> initIt = mustInitialize.iterator();
    		while(initIt.hasNext()){
    			Local initLocal = initIt.next();
    			Type tempType = initLocal.getType();
			
    			DIntConstant arg = DIntConstant.v(mustInitializeIndex,IntType.v());//takes care of the index
    			mustInitializeIndex++;
		
    			ArrayList tempArgList = new ArrayList();
    			tempArgList.add(arg);
    		
    			DVirtualInvokeExpr tempInvokeExpr =	new DVirtualInvokeExpr(jimpleLocal,getMethodRef,tempArgList,new HashSet<Object>());

    			//NECESASARY CASTING OR RETRIEVAL OF PRIM TYPES TO BE DONE HERE
    			Value toAddExpr = getProperCasting(tempType,tempInvokeExpr);
    			if(toAddExpr == null)
    				throw new DecompilationException("UNABLE TO CREATE TOADDEXPR:"+tempType);
			
    			//need to create a def stmt with the local on the left and toAddExpr on the right
    			GAssignStmt assign = new GAssignStmt(initLocal,toAddExpr);
    			newStmts.add(new AugmentedStmt(assign));
    		}
    	}
	
    	//add any statements following the this.<init> statement
    	Iterator<Object> it = initNode.getStatements().iterator();
    	while(it.hasNext()){
    		AugmentedStmt augStmt = (AugmentedStmt)it.next();
    		Stmt stmtTemp = augStmt.get_Stmt();
    		if(stmtTemp == originalConstructorUnit){
    			break;
    		}
    	}
    	while(it.hasNext()){
    		/*
    		 notice we dont need to clone these because these will be removed from the other method from which
    		 we are copying these
    		 */
    		newStmts.add(it.next());
    	}

	if(newStmts.size()>0){
	    newConstructorBody.add(new ASTStatementSequenceNode(newStmts));
	}	


	//adding body Y now
	List<Object> originalASTMethodSubBodies = originalASTMethod.get_SubBodies();
	if(originalASTMethodSubBodies.size() != 1)
	    throw new CorruptASTException("size of ASTMethodNode subBody not 1");

	List<Object> oldASTBody = (List<Object>)originalASTMethodSubBodies.get(0);

	it = oldASTBody.iterator();
	boolean sanity=false;
	while(it.hasNext()){
	    //going through originalASTMethodNode's ASTNodes
	    ASTNode tempNode = (ASTNode)it.next();
	    
	    //enter only if its not the initNode
	    if(tempNode instanceof ASTStatementSequenceNode){
		if( (((ASTStatementSequenceNode)tempNode).getStatements()).equals(initNode.getStatements()) ){
		    sanity = true;
		    break;
		}
	    }
	}

	if(!sanity){
	    //means we never found the initNode which shouldnt happen
	    throw new DecompilationException("never found the init node");
	}

	//so we have found the init node
	//Y are all the nodes following the initNode
	while(it.hasNext()){
	    newConstructorBody.add(it.next());
	}



	//setDeclarations in newNode
	//The LocalVariableCleaner which is called in the end of DavaBody will clear up any declarations that are not required

	List<Object> newConstructorDeclarations = new ArrayList<Object>();
	Iterator<Object> originalDeclarationsIterator = originalASTMethod.getDeclarations().getStatements().iterator();
	while(originalDeclarationsIterator.hasNext()){
	    AugmentedStmt as = (AugmentedStmt)originalDeclarationsIterator.next();
	    DVariableDeclarationStmt varDecStmt = (DVariableDeclarationStmt)as.get_Stmt();
	    newConstructorDeclarations.add(new AugmentedStmt((DVariableDeclarationStmt)varDecStmt.clone()));
	}
	ASTStatementSequenceNode newDecs = new ASTStatementSequenceNode(new ArrayList<Object>());
	if(newConstructorDeclarations.size()>0){
	    newDecs = new ASTStatementSequenceNode(newConstructorDeclarations);
	    //DONT FORGET TO SET THE DECLARATIONS IN THE METHOD ONCE IT IS CREATED
	    //newASTConstructorMethod.setDeclarations(newDecs);

	    //declarations are always the first element
	    newConstructorBody.add(0,newDecs);
	}


	//so we have any declarations followed by body Y


	//have to put the newConstructorBody into an list of subBodies which goes into the newASTConstructorMethod
	newASTConstructorMethod = new ASTMethodNode(newConstructorBody);

	//dont forget to set the declarations
	newASTConstructorMethod.setDeclarations(newDecs);
    }

























    private void createNewConstructor(){
	
	//the constructor name has to be <init>
	String uniqueName = "<init>";

 	//NOTICE args of this constructor are argsOne followed by the DavaSuperHandler
	
	List args = new ArrayList();
	args.addAll(argsOneTypes);

	//create new ReftType for DavaSuperHandler
	RefType type = (new SootClass("DavaSuperHandler")).getType();
	args.add(type);

	//create SOOTMETHOD
	newConstructor = new SootMethod(uniqueName,args,IntType.v());

 	//set the declaring class of new method to be the originalSootClass
 	newConstructor.setDeclaringClass(originalSootClass);

	//set method to public
	newConstructor.setModifiers(soot.Modifier.PUBLIC);

	//initalize a new DavaBody, notice this causes all DavaBody vars to be null
	newConstructorDavaBody = Dava.v().newBody(newConstructor);

	//setting params is really important if you want the args to have proper names in the new method

	//make a copy of the originalHashMap
	Map tempMap = new HashMap();
        Iterator typeIt = argsOneTypes.iterator();
        int count = 0;
        while (typeIt.hasNext()) {
            Type t = (Type) typeIt.next();
	    
	    tempMap.put(new Integer(count),originalPMap.get(new Integer(count)));
	    count++;
	}

	//add the DavaSuperHandler var name in the Parameters
	tempMap.put(new Integer(argsOneTypes.size()),"handler");

	//add the ParamMap to the constructor's DavaBody
	newConstructorDavaBody.set_ParamMap(tempMap);

	//set as activeBody
	newConstructor.setActiveBody(newConstructorDavaBody);
    }








    /*
     * January 23rd New Algorithm
     * Leave originalASTMethod unchanged
     * Clone everything and copy only those which are needed in the newASTPreInitMethod
     */
    private void createNewASTPreInitMethod(ASTStatementSequenceNode initNode){	
    	List<Object> newPreinitBody = new ArrayList<Object>();
    	//start adding ASTNodes into newPreinitBody from the originalASTMethod's body until we reach initNode

    	List<Object> originalASTMethodSubBodies = originalASTMethod.get_SubBodies();
    	if(originalASTMethodSubBodies.size() != 1)
    		throw new CorruptASTException("size of ASTMethodNode subBody not 1");

    	List<Object> oldASTBody = (List<Object>)originalASTMethodSubBodies.get(0);

    	Iterator<Object> it = oldASTBody.iterator();
    	boolean sanity=false;
    	while(it.hasNext()){
    		//going through originalASTMethodNode's ASTNodes
    		ASTNode tempNode = (ASTNode)it.next();
	    
    		//enter only if its not the initNode
    		if(tempNode instanceof ASTStatementSequenceNode){
    			if( (((ASTStatementSequenceNode)tempNode).getStatements()).equals(initNode.getStatements()) ){
    				sanity = true;
    				break;
    			}
    			else{
    				//this was not the initNode so we add
    				newPreinitBody.add(tempNode);
    			}
    		}
    		else{
    			//not a stmtseq so simply add it
    			newPreinitBody.add(tempNode);
    		}
    	}
    	if(!sanity){
    		//means we never found the initNode which shouldnt happen
    		throw new DecompilationException("never found the init node");
    	}
	

    	//at this moment newPreinitBody contains all of X except for any stmts above the this.init call in the stmtseq node
    	//copy those
    	List<Object> newStmts = new ArrayList<Object>();

    	it = initNode.getStatements().iterator();
    	while(it.hasNext()){
    		AugmentedStmt augStmt = (AugmentedStmt)it.next();
    		Stmt stmtTemp = augStmt.get_Stmt();
    		if(stmtTemp == originalConstructorUnit){
    			break;
    		}
    		//adding any stmt until constructorUnit into topList for newMethodNode
    		/*
    		 notice we dont need to clone these because these will be removed from the other method from which
    		 we are copying these
    		 */
    		newStmts.add(augStmt);
    	}			
    	if(newStmts.size()>0){
    		newPreinitBody.add(new ASTStatementSequenceNode(newStmts));
    	}




    	//setDeclarations in newNode
    	//The LocalVariableCleaner which is called in the end of DavaBody will clear up any declarations that are not required
    	List<Object> newPreinitDeclarations = new ArrayList<Object>();
    	Iterator<Object> originalDeclarationsIterator = originalASTMethod.getDeclarations().getStatements().iterator();
    	while(originalDeclarationsIterator.hasNext()){
    		AugmentedStmt as = (AugmentedStmt)originalDeclarationsIterator.next();
    		DVariableDeclarationStmt varDecStmt = (DVariableDeclarationStmt)as.get_Stmt();
    		newPreinitDeclarations.add(new AugmentedStmt((DVariableDeclarationStmt)varDecStmt.clone()));
    	}
    	ASTStatementSequenceNode newDecs = new ASTStatementSequenceNode(new ArrayList<Object>());
    	if(newPreinitDeclarations.size()>0){
    		newDecs = new ASTStatementSequenceNode(newPreinitDeclarations);
    		//DONT FORGET TO SET THE DECLARATIONS IN THE METHOD ONCE IT IS CREATED
    		//newASTPreInitMethod.setDeclarations(newDecs);

    		//when we copied the body X the first Node copied was the Declarations from the originalASTMethod
    		//replace that with this new one
    		
    		newPreinitBody.remove(0);
    		newPreinitBody.add(0,newDecs);
    	}


    	//At this moment we have the newPreInitBody containing declarations followed by code X
    	//we need to check whether this actually contains anything cos otherwise super is infact the first stmt
    	if(newPreinitBody.size()<1){
    		//System.out.println("Method node empty doing nothing returning");
    		newASTPreInitMethod = null;//meaning ASTMethodNode for this method not created
    		return;
    	}


    	//so we have any declarations followed by body X


    	//NEXT THING SHOULD BE CODE TO CREATE A DAVAHANDLER AND STORE THE ARGS TO SUPER IN IT

    	//HOWEVER WE WILL DELAY THIS TILL UNTIL WE ARE READY TO FINALIZE the PREINIT

    	//reason for delaying is that even though we know that the body is not empty the body
    	//could be made empty by the transformations which act in the finalize method


	
    	//have to put the newPreinitBody into an list of subBodies which goes into the newASTPreInitMethod
    	newASTPreInitMethod = new ASTMethodNode(newPreinitBody);
	
    	//dont forget to set the declarations
    	newASTPreInitMethod.setDeclarations(newDecs);
    }













    /*
     * Create a unique private static method name starts with preInit
     */
    private void createSootPreInitMethod(){
    	//get a unique name for the method
    	String uniqueName = getUniqueName();

    	//NOTICE WE ARE DEFINING ARGS AS SAME AS THE ORIGINAL METHOD
    	newSootPreInitMethod = new SootMethod(uniqueName,argsOneTypes,(new SootClass("DavaSuperHandler")).getType());

    	//set the declaring class of new method to be the originalSootClass
    	newSootPreInitMethod.setDeclaringClass(originalSootClass);

    	//set method to private and static
    	newSootPreInitMethod.setModifiers(soot.Modifier.PRIVATE | soot.Modifier.STATIC);

    	//initalize a new DavaBody, notice this causes all DavaBody vars to be null
    	newPreInitDavaBody = Dava.v().newBody(newSootPreInitMethod);

    	//setting params is really important if you want the args to have proper names in the new method
    	newPreInitDavaBody.set_ParamMap(originalPMap);

    	//set as activeBody
    	newSootPreInitMethod.setActiveBody(newPreInitDavaBody);
    }
    




    /*
     * Check the sootClass that it doesnt have a name we have suggested
     * ALSO VERY IMPORTANT TO CHECK THE NAMES IN THE SOOTMETHODSADDED Variable since these will be added
     * to this sootclass by the PackManager
     */
    private String getUniqueName(){
	String toReturn = "preInit";
	int counter=0;

	List methodList = originalSootClass.getMethods();

	boolean done = false; //havent found the name
	while(!done){//as long as name not found
	    done = true; //assume name found
	    Iterator it = methodList.iterator();
	    while(it.hasNext()){
		Object temp = it.next();
		if(temp instanceof SootMethod){
		    SootMethod method = (SootMethod)temp;
		    String name = method.getName();
		    if(toReturn.compareTo(name)==0){
			//method exists with this name so change the name
			counter++;
			toReturn = "preInit"+counter;
			done = false; //name was not found
			break;//breaks the inner while since the name has been changed
		    }
		}
		else
		    throw new DecompilationException("SootClass returned a non SootMethod method");
	    }
	    
	    //if we get here this means that the orignal names are different
	    //check the to be added names also
	    it = G.v().SootMethodsAdded.iterator();
	    while(it.hasNext()){
		//are sure its a sootMethod
		SootMethod method = (SootMethod)it.next();
		String name = method.getName();
		if(toReturn.compareTo(name)==0){
		    //method exists with this name so change the name
		    counter++;
		    toReturn = "preInit"+counter;
		    done = false; //name was not found
		    break;//breaks the inner while since the name has been changed
		}
	    }
		
	}// end outer while
	return toReturn;
    }













    /*
     *    Create the following code:
     *
     *    DavaSuperHandler handler;
     *    handler = new DavaSuperHandler();
     *    //code to evaluate all args in args2
     *  
     *    //evaluate 1st arg in args2
     *     ---------
     *    handler.store(firstArg);
     *
     *    //evaluate 2nd arg in args2
     *     ---------
     *    handler.store(secondArg);
     *
     *    //AND SO ON TILL ALL ARGS ARE FINISHED
     *  
     *    return handler;
     *
     */


    private void createDavaStoreStmts(){
    	List<Object> davaHandlerStmts = new ArrayList<Object>();


    	//create object of DavaSuperHandler handler
    	SootClass sootClass = new SootClass("DavaSuperHandler");

    	Type localType = sootClass.getType(); 	
    	Local newLocal = new JimpleLocal("handler",localType);




    	/*
    	 Create      *    DavaSuperHandler handler;  *
    	 */
    	DVariableDeclarationStmt varStmt = null;
    	varStmt = new DVariableDeclarationStmt(localType,newPreInitDavaBody);
    	varStmt.addLocal(newLocal);
    	AugmentedStmt as = new AugmentedStmt(varStmt);
    	davaHandlerStmts.add(as);




    	/*
    	 * create   *    handler = new DavaSuperHandler();  *
    	 */
	
    	//create RHS
    	DNewInvokeExpr invokeExpr = 	
    		new DNewInvokeExpr(RefType.v(sootClass),makeMethodRef("DavaSuperHandler",new ArrayList()),new ArrayList());

    	//create LHS
    	GAssignStmt initialization = new GAssignStmt(newLocal,invokeExpr);

    	//add to stmts
    	davaHandlerStmts.add(new AugmentedStmt(initialization));







    	/*		
    	 * create    *    handler.store(firstArg);  *
    	 */
    	//best done in a loop for all args
    	Iterator typeIt = argsTwoTypes.iterator();
    	Iterator valIt = argsTwoValues.iterator();

    	//make reference to a method of name store takes one Object arg belongs to DavaSuperHandler

    	ArrayList tempList = new ArrayList();
    	tempList.add(RefType.v("java.lang.Object"));//SHOULD BE OBJECT

    	SootMethod method = new SootMethod("store",tempList,VoidType.v()); 

    	//set the declaring class of new method to be the DavaSuperHandler class
    	method.setDeclaringClass(sootClass);
    	SootMethodRef getMethodRef = method.makeRef();


    	//everything is ready all we need is the object argument before we can create the invokeStmt with the invokeexpr
    	//once that is done wrap it in augmented stmt and add to davaHandlerStmt


    	while(typeIt.hasNext() && valIt.hasNext()){
    		Type tempType = (Type)typeIt.next();
    		Value tempVal = (Value)valIt.next();

    		AugmentedStmt toAdd = createStmtAccordingToType(tempType,tempVal,newLocal,getMethodRef);
			davaHandlerStmts.add(toAdd);			
    	}//end of going through all the types and vals
    	//sanity check
    	if(typeIt.hasNext() || valIt.hasNext())
    		throw new DecompilationException("Error creating DavaHandler stmts");


    	/*
    	 * code to add defs
    	 */
    	List<Local> uniqueLocals = addDefsToLiveVariables();
    	Iterator<Local> localIt = uniqueLocals.iterator();
    	while(localIt.hasNext()){
    		Local local = localIt.next();
    		AugmentedStmt toAdd = createStmtAccordingToType(local.getType(),local,newLocal,getMethodRef);
			davaHandlerStmts.add(toAdd);
    	}
    	
    	//set the mustInitialize field to uniqueLocals so that before Y we can assign these locals
    	mustInitialize = uniqueLocals;
    	
    	/*
    	 *  create        *    return handler;       *
    	 */

    	GReturnStmt returnStmt = new GReturnStmt(newLocal);
    	davaHandlerStmts.add(new AugmentedStmt(returnStmt));


    	//the appropriate dava handler stmts are all in place within davaHandlerStmts
	
    	//store them in an ASTSTatementSequenceNode
    	ASTStatementSequenceNode addedNode = new ASTStatementSequenceNode(davaHandlerStmts);


    	//add to method body
    	List<Object> subBodies = newASTPreInitMethod.get_SubBodies();
    	if(subBodies.size()!=1)
    		throw new CorruptASTException("ASTMethodNode does not have one subBody");
    	List<Object> body = (List<Object>)subBodies.get(0);
    	body.add(addedNode);

    	newASTPreInitMethod.replaceBody(body);
    }



    
    
    
    
    public AugmentedStmt createStmtAccordingToType(Type tempType, Value tempVal,Local newLocal, SootMethodRef getMethodRef){
		if(tempType instanceof RefType){
			//simply add this to the handler using handler.store(tempVal);
			//System.out.println("This is a reftype:"+tempType);
	
			return createAugmentedStmtToAdd(newLocal,getMethodRef,tempVal);
		}
		else if(tempType instanceof PrimType){
			//The value is a primitive type 		
			//create wrapper object new Integer(tempVal)
			PrimType t = (PrimType)tempType;
	
			//create ArgList to be sent to DNewInvokeExpr constructor
			ArrayList argList = new ArrayList();
			argList.add(tempVal);

			//BooleanType, ByteType, CharType, DoubleType, FloatType, IntType, LongType, ShortType
			if (t == BooleanType.v()){
				
				//create TypeList to be sent to makeMethodRef
				ArrayList typeList = new ArrayList();
				typeList.add(IntType.v());
	    
				DNewInvokeExpr argForStore = 	
					new DNewInvokeExpr(RefType.v("java.lang.Boolean"),
							makeMethodRef("Boolean",typeList),argList);
	    
				return createAugmentedStmtToAdd(newLocal,getMethodRef,argForStore);
			}
			else if (t == ByteType.v()){
				//create TypeList to be sent to makeMethodRef
				ArrayList typeList = new ArrayList();
				typeList.add(ByteType.v());
				
				DNewInvokeExpr argForStore = 	
					new DNewInvokeExpr(RefType.v("java.lang.Byte"),
							makeMethodRef("Byte",typeList),argList);
				
				return createAugmentedStmtToAdd(newLocal,getMethodRef,argForStore);
			}
			else if (t == CharType.v()){
				//create TypeList to be sent to makeMethodRef
				ArrayList typeList = new ArrayList();
				typeList.add(CharType.v());
				
				DNewInvokeExpr argForStore = 	
					new DNewInvokeExpr(RefType.v("java.lang.Character"),
							makeMethodRef("Character",typeList),argList);
	      
				return createAugmentedStmtToAdd(newLocal,getMethodRef,argForStore);
			}
			else if (t == DoubleType.v()){
				//create TypeList to be sent to makeMethodRef
				ArrayList typeList = new ArrayList();
				typeList.add(DoubleType.v());
				
				DNewInvokeExpr argForStore = 	
					new DNewInvokeExpr(RefType.v("java.lang.Double"),
							makeMethodRef("Double",typeList),argList);
	      
				return createAugmentedStmtToAdd(newLocal,getMethodRef,argForStore);
			}
			else if (t == FloatType.v()){
				//create TypeList to be sent to makeMethodRef
				ArrayList typeList = new ArrayList();
				typeList.add(FloatType.v());
				
				DNewInvokeExpr argForStore = 	
					new DNewInvokeExpr(RefType.v("java.lang.Float"),
							makeMethodRef("Float",typeList),argList);
	    
				return createAugmentedStmtToAdd(newLocal,getMethodRef,argForStore);
			}
			else if (t == IntType.v()){
				//create TypeList to be sent to makeMethodRef
				ArrayList typeList = new ArrayList();
				typeList.add(IntType.v());

				DNewInvokeExpr argForStore = 
					new DNewInvokeExpr(RefType.v("java.lang.Integer"),
							makeMethodRef("Integer",typeList),argList);
	    
				return createAugmentedStmtToAdd(newLocal,getMethodRef,argForStore);
			}
			else if (t == LongType.v()){
				//create TypeList to be sent to makeMethodRef
				ArrayList typeList = new ArrayList();
				typeList.add(LongType.v());

				DNewInvokeExpr argForStore = 
					new DNewInvokeExpr(RefType.v("java.lang.Long"),
							makeMethodRef("Long",typeList),argList);
	    
				return createAugmentedStmtToAdd(newLocal,getMethodRef,argForStore);
			}
			else if (t == ShortType.v()){
				//create TypeList to be sent to makeMethodRef
				ArrayList typeList = new ArrayList();
				typeList.add(ShortType.v());

				DNewInvokeExpr argForStore = 	
					new DNewInvokeExpr(RefType.v("java.lang.Short"),
							makeMethodRef("Short",typeList),argList);
				
				return createAugmentedStmtToAdd(newLocal,getMethodRef,argForStore);
			}
			else {
				throw new DecompilationException("UNHANDLED PRIMTYPE:"+tempType);
			}
		}//end of primitivetypes			    
		else{
			throw new DecompilationException("The type:"+tempType+" is neither a reftype or a primtype");
		}
    }
    
    
    
    
    /*
     * newASTPreInitMethod at time of invocation just contains body X
     * find all defs for this body
     */
    private List<Local> addDefsToLiveVariables(){
    	//get all defs within x
    	AllDefinitionsFinder finder = new AllDefinitionsFinder();
    	newASTPreInitMethod.apply(finder);
    	
    	List<DefinitionStmt> allDefs = finder.getAllDefs();
    	
    	List<Local> uniqueLocals = new ArrayList<Local>();
    	List<DefinitionStmt> uniqueLocalDefs = new ArrayList<DefinitionStmt>();
    	//remove any defs for fields, and any which are done multiple times
    	
    	Iterator<DefinitionStmt> it = allDefs.iterator();
    	while(it.hasNext()){
    		DefinitionStmt s = it.next();
    		Value left = s.getLeftOp();
    		if(left instanceof Local){
    			if(uniqueLocals.contains(left)){
    				//a def for this local already encountered
            int index = uniqueLocals.indexOf(left);
    				uniqueLocals.remove(index);
    				uniqueLocalDefs.remove(index);
    			}
    			else{
    				//no def for this local yet
    				uniqueLocals.add((Local)left);
    				uniqueLocalDefs.add(s);
    			}
    		}
    	}
    	//at this point unique locals contains all locals defined and uniqueLocaldef list has a list of the corresponding definitions
    	
    	//Now remove those unique locals and localdefs whose stmtseq node does not have the ASTMEthodNode as a parent
    	//This is a conservative step!!
    	ASTParentNodeFinder parentFinder = new ASTParentNodeFinder();
    	newASTPreInitMethod.apply(parentFinder);
    	
    	List<DefinitionStmt> toRemoveDefs = new ArrayList<DefinitionStmt>();
    	it  = uniqueLocalDefs.iterator();
    	while(it.hasNext()){
    		DefinitionStmt s = it.next();
    		Object parent = parentFinder.getParentOf(s);
    		if(parent == null || (!(parent instanceof ASTStatementSequenceNode)) ){
    			//shouldnt happen but if it does add this s to toRemove list
    			toRemoveDefs.add(s);
    		}
    		
    		//parent is an ASTStatementsequence node. check that its parent is the ASTMethodNode
    		Object grandParent = parentFinder.getParentOf(parent);
    		if(grandParent == null || (!(grandParent instanceof ASTMethodNode))){
    			//can happen if obfuscators are really smart. add s to toRemove list
    			toRemoveDefs.add(s);
    		}
    	}
    	
//    	remove any defs and corresponding locals if present in the toRemoveDefs list
    	it = toRemoveDefs.iterator();
    	while(it.hasNext()){
    		DefinitionStmt s = it.next();
    		int index = uniqueLocalDefs.indexOf(s);
    		uniqueLocals.remove(index);
    		uniqueLocalDefs.remove(index);
    	}



    	//the uniqueLocalDefs contains all those definitions to unique locals which are not deeply nested in the X body
    	
    	//find all the uses of these definitions in the original method body
    	toRemoveDefs = new ArrayList<DefinitionStmt>();
    	
    	ASTUsesAndDefs uDdU = new ASTUsesAndDefs(originalASTMethod);
    	originalASTMethod.apply(uDdU);
    	
    	it = uniqueLocalDefs.iterator();
    	while(it.hasNext()){
    		DefinitionStmt s = it.next();
    		Object temp = uDdU.getDUChain(s);
    		
    		if(temp == null){
    			//couldnt find uses
    			toRemoveDefs.add(s);
    			continue;
    		}
    		
    		ArrayList uses = (ArrayList) temp;
    		//the uses list contains all stmts / nodes which use the definedLocal

    		//check if uses is non-empty
    		if (uses.size() == 0) {
    			toRemoveDefs.add(s);
    		}

    		//check for all the non zero uses
    		Iterator useIt = uses.iterator();
    		boolean onlyInConstructorUnit=true;
    		while (useIt.hasNext()) {
    			//a use is either a statement or a node(condition, synch, switch , for etc)
    			Object tempUse = useIt.next();
    			if(tempUse != originalConstructorUnit){
    				onlyInConstructorUnit=false;
    			}
    		}
    		
    		if(onlyInConstructorUnit){
    			//mark it to be removed
    			toRemoveDefs.add(s);
    		}	
    	}
    	
    	//remove any defs and corresponding locals if present in the toRemoveDefs list
    	it = toRemoveDefs.iterator();
    	while(it.hasNext()){
    		DefinitionStmt s = it.next();
    		int index = uniqueLocalDefs.indexOf(s);
    		uniqueLocals.remove(index);
    		uniqueLocalDefs.remove(index);
    	}
    	

    	//the remaining uniquelocals are the ones which are needed for body Y
    	return uniqueLocals;    	
    }





    private AugmentedStmt createAugmentedStmtToAdd(Local newLocal,SootMethodRef getMethodRef,  Value tempVal){
    	ArrayList tempArgList = new ArrayList();
    	tempArgList.add(tempVal);
	
    	DVirtualInvokeExpr tempInvokeExpr = 	
    		new DVirtualInvokeExpr(newLocal,getMethodRef,tempArgList,new HashSet<Object>());
	
    	//create Invoke Stmt with virtualInvoke as the expression
    	GInvokeStmt s = new GInvokeStmt(tempInvokeExpr);
	
    	return  new AugmentedStmt(s);
    }

	public void debug(String methodName, String debug){		
		if(DEBUG)
			System.out.println(methodName+ "    DEBUG: "+debug);
	}
}
