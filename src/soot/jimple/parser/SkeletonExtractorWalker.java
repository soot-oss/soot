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
   Walks a jimple AST and extracts the fields, and method signatures and produces 
   a new squeleton SootClass instance.   
*/
   
public class SkeletonExtractorWalker extends Walker
{
           
    private SootResolver mResolver;


    public SkeletonExtractorWalker(SootResolver aResolver, SootClass aSootClass) 
    {	
	mResolver = aResolver;
	mSootClass = aSootClass;
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
	

	mProductions.pop(); // not needed




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
	List implementsList = null;
	String superClass = null;

	String classType = null;
	
	if(node.getImplementsClause() != null) {	   
	    implementsList = (List) mProductions.pop();
	}
	if(node.getExtendsClause() != null) {
	    superClass = (String) mProductions.pop();
	}
	classType = (String) mProductions.pop();
		
	int modifierFlags = processModifiers(node.getModifier());

	
	if(classType.equals("interface"))
	    modifierFlags |= Modifier.INTERFACE;	

	mSootClass.setModifiers(modifierFlags);
		
	if(superClass != null) {
	    mSootClass.setSuperclass(mResolver.getResolvedClass(superClass));
	}
	
	if(implementsList != null) {
	    Iterator implIt = implementsList.iterator();
	    while(implIt.hasNext()) {
		SootClass interfaceClass = mResolver.getResolvedClass((String) implIt.next());
		mSootClass.addInterface(interfaceClass);
	    }
	}
	
	mProductions.push(mSootClass);
	
	/* xxx take this junk out of the scene; then delete the following
	Iterator it = Scene.v().getClassesToResolve().iterator();	
	*/
    } 



    /*
      member =
      {field}  modifier* type name semicolon |
      {method} modifier* type name l_paren parameter_list? r_paren throws_clause? method_body;
    */    
    
       
    public void caseAMethodMember(AMethodMember node)
    {
	inAMethodMember(node);
	{
	    Object temp[] = node.getModifier().toArray();
	    for(int i = 0; i < temp.length; i++)
		{
		    ((PModifier) temp[i]).apply(this);
		}
	}
	if(node.getType() != null)
	    {
		node.getType().apply(this);
	    }
	if(node.getName() != null)
	    {
		node.getName().apply(this);
	    }
	if(node.getLParen() != null)
	    {
		node.getLParen().apply(this);
	    }
	if(node.getParameterList() != null)
	    {
		node.getParameterList().apply(this);
	    }
	if(node.getRParen() != null)
	    {
		node.getRParen().apply(this);
	    }
	if(node.getThrowsClause() != null)
	    {
		node.getThrowsClause().apply(this);
	    }
	/*if(node.getMethodBody() != null)
	  {
	  node.getMethodBody().apply(this);
	  }*/
	outAMethodMember(node);
    }
      public void outAMethodMember(AMethodMember node)
    {
	int modifier = 0;
	Type type;
	String name;
	List parameterList = null;
	List throwsClause = null;
	JimpleBody methodBody = null;

	/* if(node.getMethodBody() instanceof AFullMethodBody)
	    methodBody = (JimpleBody) mProductions.pop();
	*/
	
	if(node.getThrowsClause() != null)
	    throwsClause = (List) mProductions.pop();
	
	if(node.getParameterList() != null) {
	    parameterList = (List) mProductions.pop();
	}
	else {
	    parameterList = new ArrayList();
	} 

	Object o = mProductions.pop();


	name = (String) o;
	type = (Type) mProductions.pop();
	modifier = processModifiers(node.getModifier());

	SootMethod method;

	if(throwsClause != null)
	    method =  new SootMethod(name, parameterList, type, modifier, throwsClause);
	else 	    
	    method =  new SootMethod(name, parameterList, type, modifier);

	mSootClass.addMethod(method);	
    }


    /*
      throws_clause =
      throws class_name_list;
    */    
    public void outAThrowsClause(AThrowsClause node)
    {
	List l = (List) mProductions.pop();
	Iterator it = l.iterator();
	List exceptionClasses = new ArrayList(l.size());
      
	while(it.hasNext()) {	 	  
	    String className = (String) it.next();
	  
	    //	  exceptionClasses.add(new SootClass("dummy exception class"));
	    exceptionClasses.add(mResolver.getResolvedClass(className));
	}

	mProductions.push(exceptionClasses);
    }

} 
