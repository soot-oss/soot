package soot.jimple.parser;

import soot.baf.*;
import soot.*;
import soot.jimple.*;

import soot.jimple.parser.parser.*;
import soot.jimple.parser.lexer.*;
import soot.jimple.parser.node.*;
import soot.jimple.parser.analysis.*;

import java.io.*;
import java.util.*;


/* 
   Walks a jimple AST and constructs the method bodies for all the methods of 
   the SootClass associated with this walker (see constructor). 
   note: Contrary to the plain "Walker", this walker does not create a SootClass,
   or interact with the scene. It merely adds method bodies for each of the methods of
   the SootClass it was initialized with.
*/
   
public class BodyExtractorWalker extends Walker
{
           
    public BodyExtractorWalker(SootClass sc) 
    {
	mSootClass = sc;		
    }
    
    /*
      file = 
      modifier* file_type class_name extends_clause? implements_clause? file_body; 
    */
    public void inAFile(AFile node)
    {
	if(debug)
	    System.out.println("reading class " + node.getClassName());
    } 
    
    public void caseAFile(AFile node)
    {
	inAFile(node);
        {
            Object temp[] = node.getModifier().toArray();
            for(int i = 0; i < temp.length; i++)
            {
                ((PModifier) temp[i]).apply(this);
            }
        }
        if(node.getFileType() != null)
        {
            node.getFileType().apply(this);
        }
        if(node.getClassName() != null)
        {
            node.getClassName().apply(this);
        }
	
	String className = (String) mProductions.pop();
	if(!className.equals(mSootClass.getName()))
	    throw new RuntimeException("expected:  " + className + ", but got: " + mSootClass.getName());

        if(node.getExtendsClause() != null)
        {
            node.getExtendsClause().apply(this);
        }
        if(node.getImplementsClause() != null)
        {
            node.getImplementsClause().apply(this);
        }
        if(node.getFileBody() != null)
        {
            node.getFileBody().apply(this);
        }
        outAFile(node);	
    }

    public void outAFile(AFile node)
    {	
	if(node.getImplementsClause() != null) 
	    mProductions.pop(); // implements_clause
	
	if(node.getExtendsClause() != null) 
	    mProductions.pop(); // extends_clause
	
	mProductions.pop(); // file_type
	
	mProductions.push(mSootClass);
    } 


    /*
      member =
      {field}  modifier* type name semicolon |
      {method} modifier* type name l_paren parameter_list? r_paren throws_clause? method_body;
    */    
    public void outAFieldMember(AFieldMember node)
    {
	mProductions.pop(); // name
	mProductions.pop(); // type
    }

    public void outAMethodMember(AMethodMember node)
    {
	int modifier = 0;
	Type type;
	String name;
	List parameterList = new ArrayList();
	List throwsClause = null;
	JimpleBody methodBody = null;

	if(node.getMethodBody() instanceof AFullMethodBody)
	    methodBody = (JimpleBody) mProductions.pop();
	
	if(node.getThrowsClause() != null)
	    throwsClause = (List) mProductions.pop();
	
	if(node.getParameterList() != null) {
	    parameterList = (List) mProductions.pop();
	}

	name = (String) mProductions.pop(); // name
	type = (Type) mProductions.pop(); // type
	SootMethod sm = null;
	try {	
	    sm = mSootClass.getMethod(SootMethod.getSubSignature(name, parameterList, type));
	} catch (soot.NoSuchMethodException e) {
	    System.out.println(" >> " + SootMethod.getSubSignature(name, parameterList, type));
	    Iterator it = mSootClass.getMethods().iterator();
	    while(it.hasNext()) {
		SootMethod next = (SootMethod) it.next();
		System.out.println(next.getSubSignature());
	    }
	    
	}

	if(sm.isConcrete()) {
	    methodBody.setMethod(sm);

	    sm.setActiveBody(methodBody);
	} else if(node.getMethodBody() instanceof AFullMethodBody) {
	    if(sm.isPhantom() )
	       System.out.println("Is phantom...");
	    throw new RuntimeException("Impossible: !concrete => ! instanceof " + sm.getName() );	
	}
    }
  
} 
