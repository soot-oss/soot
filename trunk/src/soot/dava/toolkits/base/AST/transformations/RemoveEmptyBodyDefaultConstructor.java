package soot.dava.toolkits.base.AST.transformations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import soot.Body;
import soot.SootClass;
import soot.SootMethod;
import soot.dava.DavaBody;
import soot.dava.internal.AST.ASTMethodNode;
import soot.dava.internal.AST.ASTNode;
import soot.util.Chain;



/*
 * It has been seen that Dava's output contains the default constructor with just the invocation
 * to super()
 * 
 * According to the java specs the default constructor is not needed unless the constructor has been
 * overloaded.
 * 
 * The analysis checks whether there is only one constructor in the class being decompiled.
 * If this is true and the constructor IS THE DEFAULT CONSTRUCTOR i.e. no arguments
 * and has an empty body (there will always be a call to super
 * but that is invoked automatically....) we can remove the constructor from the code
 * also check tht the call to super has no arguments i.e. default super
 */
public class RemoveEmptyBodyDefaultConstructor{
	public static boolean DEBUG=false;
	
	public static void checkAndRemoveDefault(SootClass s){
		debug("\n\nRemoveEmptyBodyDefaultConstructor----"+s.getName());
		List methods = s.getMethods();
		Iterator it = methods.iterator();
		List<SootMethod> constructors = new ArrayList<SootMethod>();
		
		while(it.hasNext()){
			SootMethod method = (SootMethod)it.next();
			debug("method name is"+method.getName());
			if(method.getName().indexOf("<init>")>-1){
				//constructor add to constructor list
				constructors.add(method);
			}
		}
		
		if(constructors.size()!=1){
			//cant do anything since there are more than one constructors
			debug("class has more than one constructors cant do anything");
			return;
		}
		
		//only one constructor check its default (no arguments)
		SootMethod constructor = constructors.get(0);
		if(constructor.getParameterCount()!=0){
			//can only deal with default constructors
			debug("constructor is not the default constructor");
			return;
		}
		
		debug("Check that the body is empty....and call to super contains no arguments and delete");
		
		if(!constructor.hasActiveBody()){
			debug("No active body found for the default constructor");
			return;
		}

		Body body = constructor.getActiveBody();
        Chain units = ((DavaBody) body).getUnits();

        if (units.size() != 1) {
        	debug(" DavaBody AST does not have single root");
        	return;
        }

        ASTNode AST = (ASTNode) units.getFirst();
        if(! (AST instanceof ASTMethodNode))
        	throw new RuntimeException("Starting node of DavaBody AST is not an ASTMethodNode");

		ASTMethodNode methodNode = (ASTMethodNode)AST;
		debug("got methodnode check body is empty and super has nothing in it");
		
		List<Object> subBodies = methodNode.get_SubBodies();
		if(subBodies.size()!=1){
			debug("Method node does not have one subBody!!!");
			return;
		}
        
		List methodBody = (List)subBodies.get(0);
		if(methodBody.size()!=0){
			debug("Method body size is greater than 1 so cant do nothing");
			return;
		}

		debug("Method body is empty...check super call is empty");
		
		if(((DavaBody)body).get_ConstructorExpr().getArgCount() != 0){
			debug("call to super not empty");
			return;
		}
		
		debug("REMOVE METHOD");
		s.removeMethod(constructor);
	}
	
	
	
	
	public static void debug(String debug){		
		if(DEBUG)
			System.out.println("DEBUG: "+debug);
	}

}
