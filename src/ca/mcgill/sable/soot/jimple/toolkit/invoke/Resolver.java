// package ca.mcgill.sable.soot.virtualCalls;

package ca.mcgill.sable.soot.jimple.toolkit.invoke;

import java.io.*;
import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.soot.jimple.*;
import ca.mcgill.sable.soot.grimp.*;
import ca.mcgill.sable.util.*;
import java.util.*;
import ca.mcgill.sable.soot.baf.*; 
// import ca.mcgill.sable.soot.sideEffect.*;

public class Resolver {





 private SootClassManager scm; /* = new SootClassManager(); */ 

 private HashMap classesHT = new HashMap();

 private HashMap methodsHT = new HashMap();

 private HashMap fieldsHT = new HashMap();

 private HashSet errormethods = new HashSet();

 private HashSet errorinvokeexprs = new HashSet();

 private HashSet uninlinableexprs = new HashSet();

 private Integer priv = new Integer ( 0 );

 private Integer def = new Integer ( 1 );

 private Integer prot = new Integer ( 2 );

 private Integer pub = new Integer ( 3 );

 private ClassGraphBuilder classgraphbuilder;

 
 public Resolver( ClassGraphBuilder clgb ) { 

  scm = clgb.getManager();

  classgraphbuilder = clgb; 

  
  Iterator classnodesit = clgb.getClassNodes().iterator();

  while ( classnodesit.hasNext() )
  {  

   SootClass sootclass = ( ( ClassNode ) classnodesit.next() ).getSootClass();

   try {

   if ( ! ( scm.managesClass ( sootclass.getName() ) ) )
   {

     // System.out.println ( " ADDED " + sootclass.getName() );

    scm.addClass ( sootclass ); 

   }
   else
   {

    scm.removeClass ( scm.getClass ( sootclass.getName() ) );

    //    System.out.println ( " REMOVED "+sootclass.getName() );

    scm.addClass ( sootclass );

    // System.out.println ( " ADDED1 "+sootclass.getName() );

   }


   } catch ( ca.mcgill.sable.soot.AlreadyManagedException e ) {

     // System.out.println ("ALREADY MANAGED EXCEPTION " );

     Iterator debugit = scm.getClasses().iterator();

     while ( debugit.hasNext() )
     {

      SootClass debugcl = ( SootClass ) debugit.next();

      //      System.out.println ("MANAGING "+debugcl.getName() );

//      System.out.println ( "DEBUGMOD "+Modifier.toString(debugcl.getModifiers() ) );

//      System.out.println ( debugcl );

     }

    }   

  }

 }







 public SootClassManager getManager () {

  return scm;

 }










 public ClassGraphBuilder getClassGraphBuilder () {

  return classgraphbuilder;

 }









 public HashMap getClassesHT () {

  return classesHT;

 }








 public HashMap getMethodsHT () {

  return methodsHT;

 }






 public HashMap getFieldsHT () {

  return fieldsHT;

 }




 

 public HashSet getErrorMethods () {

  return errormethods;

 }




  

 

 public HashSet getErrorInvokeExprs () {

  return errorinvokeexprs;

 }







 public void resolveMethods ( Collection callgraph ) {

  System.out.println();
  System.out.print("Resolving methods to detect possible illegal accesses");

  Iterator iter = callgraph.iterator();

  int mnum = 0;

  while ( iter.hasNext() )
  {

     mnum++;

    if ( ( mnum % 10 ) == 0 )
    System.out.print(".");

   MethodNode tempMN = (MethodNode) iter.next();

   resolveAccessedClassesFromMethod( tempMN.getMethod() );

//   setUnInlinableInvokeExprs ( tempMN.getMethod() );
  
  }

  System.out.println ("Done");

 }







  public void resolveAccessedClassesFromMethod ( SootMethod method ) {

    //   System.out.println ("Resolving methods to detect possible illegal accesses");

    //   System.out.println ( "RESOLVING METHOD "+method.getSignature() );

   boolean ref = false;

   boolean samepackage = false;

   boolean sameclass = false;

   boolean sameprotected = false;

   SootClass sc = null;

//   Iterator iter = scm.getClasses().iterator();

//   while ( iter.hasNext() )
//   scm.removeClass( ((SootClass) iter.next()) );

   SootClass currclass = scm.getClass ( method.getDeclaringClass().getName() ); 

   String currname = currclass.getName();

   String currpackagename = getPackageName ( currname );

/*
   BuildAndStoreBody buildAndStoreBody = new BuildAndStoreBody(Jimple.v() , new StoredBody(ClassFile.v()));

   JimpleBody stmtListBody = (JimpleBody) buildAndStoreBody.resolveFor(method);
*/


   try {

   JimpleBody stmtListBody = Jimplifier.getJimpleBody ( method );

   List localslist = stmtListBody.getLocals();

   Iterator localsit = localslist.iterator();

   while ( localsit.hasNext() )
   {
     
    ref = false;

    samepackage = false;

    Local loc = ( Local ) localsit.next();

    if ( loc.getType() instanceof RefType )
    {
     
     ref = true;

     String locName = loc.getType().toString();

//     System.out.println ("----"+locName);

     sc = scm.getClass ( locName );
     
     samepackage = isSamePackage ( getPackageName ( locName ), currpackagename );

    }  
    else if ( loc.getType() instanceof ArrayType )
    {

     Type t = ( ( ArrayType ) loc.getType() ).baseType;

     if ( t instanceof RefType )
     {
      
      ref = true;

      String locName = t.toString(); 

//      System.out.println ("----"+locName);

      sc = scm.getClass( locName );

      samepackage = isSamePackage ( getPackageName ( locName ), currpackagename );

     }

    } 
 
//    System.out.println ("---------------------------");

    if ( ref )
    {
 
     if ( ( ( Integer ) classesHT.get ( sc.getName() ) ) == null )  
     classesHT.put ( sc.getName(), pub );

//     System.out.println ( " MODS : "+Modifier.toString( sc.getModifiers() )+sc );

     if ( ! ( ( ( Modifier.isPublic ( sc.getModifiers() ) ) || samepackage ) ) )
     {  

       // System.out.println ( " MODIFIERS : "+ Modifier.toString ( sc.getModifiers() ) );

       // System.out.println ( " ILLEGAL ERROR ( LOCAL DECLARATION ) WHEN TRYING TO ACCESS CLASS : "+sc.getName()+" FROM METHOD : "+method.getSignature() );

      classesHT.put ( sc.getName(), def ); 

      errormethods.add ( method.getSignature() );

     }   

    }


   }
    

   StmtList l = stmtListBody.getStmtList();

   Iterator it = l.iterator();

   while ( it.hasNext() ) 
   {

    Stmt s = ( Stmt ) it.next();

    // System.out.println ( "RESOLVING "+s);

    List boxes = s.getUseAndDefBoxes();

    // System.out.println ( "RESOLVED "+s );

    Iterator boxit = boxes.iterator();

    while ( boxit.hasNext() )
    {

      sameclass = false;

      samepackage = false;

      sameprotected = false;

      ValueBox vb = ( ValueBox ) boxit.next();

      Value v = vb.getValue();

      SootClass dec = null;

      SootField field = null;

      Value im = null;
      
      if ( v instanceof InstanceFieldRef )
      {

       InstanceFieldRef ifr = ( InstanceFieldRef ) v;

       im = ifr.getBase();

       String basetype = im.getType().toString();

       field = ifr.getField();

       dec = scm.getClass ( field.getDeclaringClass().getName() ); 

       if ( currclass.getName().equals ( dec.getName() ) )
       sameclass = true;

       samepackage = isSamePackage ( getPackageName ( dec.getName() ), currpackagename );

       if ( ( ( Integer ) classesHT.get ( dec.getName() ) ) == null )  
       classesHT.put ( dec.getName(), pub );


       if ( ! ( ( ( Modifier.isPublic ( dec.getModifiers() ) ) || samepackage ) ) )
       {  
      
         // System.out.println ( " MODIFIERS : "+ Modifier.toString ( dec.getModifiers() ) );
    
         // System.out.println ( " ILLEGAL ERROR ( INSTANCE FIELD REF ) WHEN TRYING TO ACCESS CLASS : "+dec.getName()+" FROM METHOD : "+method.getSignature() );
       
	classesHT.put ( dec.getName(), def );

        errormethods.add ( method.getSignature() );

       } 


       if ( im.getType() instanceof ArrayType )
       sameprotected = currclass.getName().equals ( "java.lang.Object" );
       else
       sameprotected = isSameProtected ( dec, currclass, basetype, currpackagename ); 

       if ( ( ( ( Integer ) fieldsHT.get ( field.getSignature() ) ) == null ) )
       fieldsHT.put ( field.getSignature(), pub );


       if ( ( Modifier.isPrivate ( field.getModifiers() ) ) && ( ! sameclass ) ) 
       {       

	fieldsHT.put ( field.getSignature(), priv );

    //	System.out.println( "ILLEGAL ERROR WHEN TRYING TO ACCESS PRIVATE INSTANCE FIELD : "+field.getSignature()+" FROM METHOD "+method.getSignature() );

        errormethods.add ( method.getSignature() );

       }
       else if ( ( Modifier.isProtected ( field.getModifiers() ) ) && ( ! sameprotected ) )
       {

	if ( ( ( Integer ) fieldsHT.get ( field.getSignature() ) ).intValue() > 2 )
	fieldsHT.put ( field.getSignature(), prot );

    //	 System.out.println( "ILLEGAL ERROR WHEN TRYING TO ACCESS PROTECTED INSTANCE FIELD : "+field.getSignature()+" FROM METHOD "+method.getSignature() ); 

         errormethods.add ( method.getSignature() );

       }
       else if ( ! ( ( Modifier.isProtected( field.getModifiers() ) || Modifier.isPrivate( field.getModifiers() ) ) || Modifier.isPublic (field.getModifiers() ) ) ) 
       {
	
	if ( ! samepackage )
	{
	
	 if ( ( ( Integer ) fieldsHT.get ( field.getSignature() ) ).intValue() > 1 )
	 fieldsHT.put ( field.getSignature(), def );

     //	 System.out.println( "ILLEGAL ERROR WHEN TRYING TO ACCESS DEFAULT MODIFIER INSTANCE FIELD : "+field.getSignature()+" FROM METHOD "+method.getSignature() );

         errormethods.add ( method.getSignature() ); 

	}

       }

      }
      else if ( v instanceof StaticFieldRef ) 
      {

       StaticFieldRef sfr = ( StaticFieldRef ) v;

       field = sfr.getField();
      
       dec = scm.getClass ( field.getDeclaringClass().getName() );

      
       if ( currclass.getName().equals ( dec.getName() ) )
       sameclass = true;
       
       samepackage = isSamePackage ( getPackageName( dec.getName() ), currpackagename );

       if ( ( ( Integer ) classesHT.get ( dec.getName() ) ) == null )  
       classesHT.put ( dec.getName(), pub );
     
       if ( ! ( ( ( Modifier.isPublic ( dec.getModifiers() ) ) || samepackage ) ) )
       {
      
         // System.out.println ( " MODIFIERS : "+ Modifier.toString ( dec.getModifiers() ) );
      
         // System.out.println ( " ILLEGAL ERROR ( STATIC FIELD REF ) WHEN TRYING TO ACCESS CLASS : "+dec.getName()+" FROM METHOD : "+method.getSignature() );

        classesHT.put ( dec.getName(), def );
	
        errormethods.add ( method.getSignature() );

       }


       sameprotected = isSameStaticProtected ( dec, currclass, currpackagename );

       if ( ( ( ( Integer ) fieldsHT.get ( field.getSignature() ) ) == null ) )
       fieldsHT.put ( field.getSignature(), pub );


       if ( ( Modifier.isPrivate ( field.getModifiers() ) ) && ( ! sameclass ) ) 
       {
       
	fieldsHT.put ( field.getSignature(), priv );

    //	System.out.println( "ILLEGAL ERROR WHEN TRYING TO ACCESS PRIVATE STATIC FIELD : "+field.getSignature()+" FROM METHOD "+method.getSignature() );
       
        errormethods.add ( method.getSignature() );

       }
       else if ( ( Modifier.isProtected ( field.getModifiers() ) ) && ( ! sameprotected ) )
       {
       
	if ( ( ( Integer ) fieldsHT.get ( field.getSignature() ) ).intValue() > 2 )
	fieldsHT.put ( field.getSignature(), prot );

    //	 System.out.println( "ILLEGAL ERROR WHEN TRYING TO ACCESS PROTECTED STATIC FIELD : "+field.getSignature()+" FROM METHOD "+method.getSignature() ); 

         errormethods.add ( method.getSignature() );

       }
       else if ( ! ( ( Modifier.isProtected( field.getModifiers() ) || Modifier.isPrivate( field.getModifiers() ) ) || Modifier.isPublic ( field.getModifiers() ) ) ) 
       {
	
	if ( ! samepackage )
	{
	 
	 if ( ( ( Integer ) fieldsHT.get ( field.getSignature() ) ).intValue() > 1 )
	 fieldsHT.put ( field.getSignature(), def );
       
     // System.out.println( "ILLEGAL ERROR WHEN TRYING TO ACCESS DEFAULT MODIFIER STATIC FIELD : "+field.getSignature()+" FROM METHOD "+ method.getSignature() );
        
         errormethods.add ( method.getSignature() );

	}

       }
       
      }
      else if ( v instanceof CastExpr )
      {

       ref = false;

       CastExpr ce = ( CastExpr ) v; 
       
       Type t = ce.getType();   

       if ( t instanceof RefType )
       {
     
	ref = true;

	String locName = t.toString();

	sc = scm.getClass ( locName );
       
	samepackage = isSamePackage ( getPackageName ( locName ), currpackagename );
    
       }  
       else if ( t instanceof ArrayType )
       {

	Type ty = ( ( ArrayType ) t ).baseType;

	if ( ty instanceof RefType )
	{
      
	 ref = true;

	 String locName = ty.toString(); 

	 sc = scm.getClass ( locName );

	 samepackage = isSamePackage ( getPackageName ( locName ), currpackagename );

	}

       }

       if ( ref )
       {

        if ( ( ( Integer ) classesHT.get ( sc.getName() ) ) == null )  
        classesHT.put ( sc.getName(), pub );

	if ( ! ( ( ( Modifier.isPublic ( sc.getModifiers() ) ) || samepackage ) ) )
	{  

      //	 System.out.println ( " MODIFIERS : "+ Modifier.toString ( sc.getModifiers() ) );

      //	 System.out.println ( " ILLEGAL ERROR ( CAST EXPR ) WHEN TRYING TO ACCESS CLASS : "+sc.getName()+" FROM METHOD : "+method.getSignature() );

         classesHT.put ( sc.getName(), def );

         errormethods.add ( method.getSignature() );

	}   

       }

      }
      else if ( v instanceof InstanceOfExpr )
      {

       ref = false;

       InstanceOfExpr ie = ( InstanceOfExpr ) v; 
       
       Type t = ie.getCheckType();   

       if ( t instanceof RefType )
       {
     
	ref = true;

	String locName = t.toString();
     
	sc = scm.getClass( locName );

	samepackage = isSamePackage ( getPackageName ( locName ), currpackagename );
    
       }  
       else if ( t instanceof ArrayType )
       {

	Type ty = ( ( ArrayType ) t ).baseType;

	if ( ty instanceof RefType )
	{

	 ref = true;
      
	 String locName = ty.toString(); 

	 sc = scm.getClass( locName );

	 samepackage = isSamePackage ( getPackageName ( locName ), currpackagename );

	}

       }

       if ( ref )
       {    

        if ( ( ( Integer ) classesHT.get ( sc.getName() ) ) == null )  
        classesHT.put ( sc.getName(), pub );

	if ( ! ( ( ( Modifier.isPublic ( sc.getModifiers() ) ) || samepackage ) ) )
	{  
 
      //	  System.out.println ( " MODIFIERS : "+ Modifier.toString ( sc.getModifiers() ) );

      //	  System.out.println ( " ILLEGAL ERROR ( INSTANCEOF EXPR ) WHEN TRYING TO ACCESS CLASS : "+sc.getName()+" FROM METHOD : "+method.getSignature() );

          classesHT.put ( sc.getName(), def );

          errormethods.add ( method.getSignature() );

	}   

       }

      }
      else if ( v instanceof NewExpr )
      {

       NewExpr newexpr = ( NewExpr ) v; 
       
       RefType t = newexpr.getBaseType();   

       String locName = t.toString();

       sc = scm.getClass ( locName );
     
       samepackage = isSamePackage ( getPackageName ( locName ), currpackagename );
       
       if ( ( ( Integer ) classesHT.get ( sc.getName() ) ) == null )  
       classesHT.put ( sc.getName(), pub );
       
       if ( ! ( ( ( Modifier.isPublic ( sc.getModifiers() ) ) || samepackage ) ) )
       {  
 
         //	 System.out.println ( " MODIFIERS : "+ Modifier.toString ( sc.getModifiers() ) );

         // System.out.println ( " ILLEGAL ERROR ( NEW EXPR ) WHEN TRYING TO ACCESS CLASS : "+sc.getName()+" FROM METHOD : "+method.getSignature() );

         classesHT.put ( sc.getName(), def );

         errormethods.add ( method.getSignature() );

       }   

      }
      else if ( v instanceof NewArrayExpr )
      {

       ref = false;

       NewArrayExpr newarrayexpr = ( NewArrayExpr ) v; 
       
       Type t = newarrayexpr.getBaseType();   

       if ( t instanceof RefType )
       {
     
	ref = true;

	String locName = t.toString();

	sc = scm.getClass( locName );
     
	samepackage = isSamePackage ( getPackageName ( locName ), currpackagename );
    
       }  
       else if ( t instanceof ArrayType )
       {

	Type ty = ( ( ArrayType ) t ).baseType;

	if ( ty instanceof RefType )
	{

	 ref = true;
      
	 String locName = ty.toString(); 

	 sc = scm.getClass( locName );

	 samepackage = isSamePackage ( getPackageName ( locName ), currpackagename );

	}

       }

       if ( ref )
       {

        if ( ( ( Integer ) classesHT.get ( sc.getName() ) ) == null )  
        classesHT.put ( sc.getName(), pub );

	if ( ! ( ( ( Modifier.isPublic ( sc.getModifiers() ) ) || samepackage ) ) )
	{  
 
      //	  System.out.println ( " MODIFIERS : "+ Modifier.toString ( sc.getModifiers() ) );

      //	  System.out.println ( " ILLEGAL ERROR ( NEWARRAY EXPR ) WHEN TRYING TO ACCESS CLASS : "+sc.getName()+" FROM METHOD : "+method.getSignature() );

          classesHT.put ( sc.getName(), def );

          errormethods.add ( method.getSignature() );
         

	}   

       }

      }
      else if ( v instanceof NewMultiArrayExpr )
      {

       ref = false;

       NewMultiArrayExpr newmultiarrayexpr = ( NewMultiArrayExpr ) v; 
       
       Type t = newmultiarrayexpr.getBaseType();   

       if ( t instanceof RefType )
       {
     
	ref = true;

	String locName = t.toString();
     
	sc = scm.getClass ( locName );

	samepackage = isSamePackage ( getPackageName ( locName ), currpackagename );
    
       }  
       else if ( t instanceof ArrayType )
       {

	Type ty = ( ( ArrayType ) t ).baseType;

	if ( ty instanceof RefType )
	{
      
	 ref = true;

	 String locName = ty.toString(); 

	 sc = scm.getClass ( locName );

	 samepackage = isSamePackage ( getPackageName ( locName ), currpackagename );

	}

       }

       if ( ref )
       {

        if ( ( ( Integer ) classesHT.get ( sc.getName() ) ) == null )  
        classesHT.put ( sc.getName(), pub );

	if ( ! ( ( ( Modifier.isPublic ( sc.getModifiers() ) ) || samepackage ) ) )
	{  
 
      //	 System.out.println ( " MODIFIERS : "+ Modifier.toString ( sc.getModifiers() ) );

      //	 System.out.println ( " ILLEGAL ERROR ( NEWMULTIARRAY ) WHEN TRYING TO ACCESS CLASS : "+sc.getName()+" FROM METHOD : "+method.getSignature() );

         classesHT.put ( sc.getName(), def );

         errormethods.add ( method.getSignature() );

	}   

       }

      }
      else if ( v instanceof StaticInvokeExpr )
      {

       StaticInvokeExpr stinvexpr = ( StaticInvokeExpr ) v;
   
       int argcount = stinvexpr.getArgCount();
    
       int counter = 0;

       while ( counter < argcount )
       {

	samepackage = false;

	ref = false;

       if ( stinvexpr.getMethod().getParameterType( counter ) instanceof RefType )
       {
       
	ref = true;

	String argtype = stinvexpr.getMethod().getParameterType( counter ).toString();
       
	sc = scm.getClass ( argtype );

	samepackage = isSamePackage ( getPackageName ( argtype ), currpackagename );
       
       }
       else if ( stinvexpr.getMethod().getParameterType( counter ) instanceof ArrayType )
       {
       
	Type t = ( ( ArrayType ) stinvexpr.getMethod().getParameterType( counter ) ).baseType;
       
	if ( t instanceof RefType )
	{ 

	 ref = true;

	 String argtype = t.toString();

	 sc = scm.getClass ( argtype );
   
	 samepackage = isSamePackage ( getPackageName ( argtype ), currpackagename );
	
	}
	
       }


       if ( ref )
       {

        if ( ( ( Integer ) classesHT.get ( sc.getName() ) ) == null )  
        classesHT.put ( sc.getName(), pub );

	if ( ! ( ( ( Modifier.isPublic ( sc.getModifiers() ) ) || samepackage ) ) )
	{  
 
      //	 System.out.println ( " MODIFIERS : "+ Modifier.toString ( sc.getModifiers() ) );

      //	 System.out.println ( " ILLEGAL ERROR ( STATIC INVOKE EXPR PARAMETER ) WHEN TRYING TO ACCESS CLASS : "+sc.getName()+" FROM METHOD : "+method.getSignature() );

         classesHT.put ( sc.getName(), def );

         errormethods.add ( method.getSignature() );

	}   

       }

       counter++;

      }

       samepackage = false;

       SootMethod meth = stinvexpr.getMethod();

       dec = scm.getClass ( meth.getDeclaringClass().getName() ); 

       if ( currclass.getName().equals ( dec.getName() ) )
       sameclass = true;

       samepackage = isSamePackage ( getPackageName ( dec.getName() ), currpackagename );

       if ( ( ( Integer ) methodsHT.get ( meth.getSignature() ) ) == null )  
       methodsHT.put ( meth.getSignature(), pub );

       if ( ( ( Integer ) classesHT.get ( dec.getName() ) ) == null )
       classesHT.put ( dec.getName(), pub );

       if ( ! ( ( ( Modifier.isPublic ( dec.getModifiers() ) ) || samepackage ) ) )
       {
      
         //	System.out.println ( " MODIFIERS : "+ Modifier.toString ( dec.getModifiers() ) );
      
         //	System.out.println ( " ILLEGAL ERROR ( STATIC INVOKE EXPR BASE ) WHEN TRYING TO ACCESS CLASS : "+dec.getName()+" FROM METHOD : "+method.getSignature() );
	
        classesHT.put ( dec.getName(), def );       

        errormethods.add ( method.getSignature() );

       }


       sameprotected = isSameStaticProtected ( dec, currclass, currpackagename ); 


       if ( ( Modifier.isPrivate ( meth.getModifiers() ) ) && ( ! sameclass ) ) 
       {

	methodsHT.put ( meth.getSignature(), priv );

        adjustSuperMethods ( meth, priv );

        //	System.out.println( "ILLEGAL ERROR WHEN TRYING TO ACCESS PRIVATE STATIC METHOD : "+meth.getSignature()+" FROM METHOD "+method.getSignature() );

        errormethods.add ( method.getSignature() );

        errorinvokeexprs.add ( stinvexpr );
       
       }
       else if ( ( Modifier.isProtected ( meth.getModifiers() ) ) && ( ! sameprotected ) )
       {
	
	if ( ( ( Integer ) methodsHT.get ( meth.getSignature() ) ).intValue() > 2 )
	methodsHT.put ( meth.getSignature(), prot );

        adjustSuperMethods ( meth, prot );

        //	 System.out.println( "ILLEGAL ERROR WHEN TRYING TO ACCESS PROTECTED STATIC METHOD : "+meth.getSignature()+" FROM METHOD "+method.getSignature() ); 

         errormethods.add ( method.getSignature() );
       
         errorinvokeexprs.add ( stinvexpr );

       }
       else if ( ! ( ( Modifier.isProtected( meth.getModifiers() ) || Modifier.isPrivate( meth.getModifiers() ) ) || Modifier.isPublic ( meth.getModifiers() ) ) ) 
       {
	
	if ( ! samepackage )
	{
	 
	 if ( ( ( Integer ) methodsHT.get ( meth.getSignature() ) ).intValue() > 1 )
	 methodsHT.put ( meth.getSignature(), def );

         adjustSuperMethods ( meth, def );

         //	 System.out.println( "ILLEGAL ERROR WHEN TRYING TO ACCESS DEFAULT MODIFIER STATIC METHOD : "+meth.getSignature()+" FROM METHOD "+method.getSignature() );

         errormethods.add ( method.getSignature() );

         errorinvokeexprs.add ( stinvexpr );

	}
       
       }

      }
      
      else if ( v instanceof InvokeExpr )
      {

       InvokeExpr invexpr = ( InvokeExpr ) v;

       int argcount = invexpr.getArgCount();
    
       int counter = 0;

       while ( counter < argcount )
       {

	ref = false;

	samepackage = false;

       if ( invexpr.getMethod().getParameterType( counter ) instanceof RefType )
       {
	 
	ref = true;

	String argtype = invexpr.getMethod().getParameterType( counter ).toString();

	sc = scm.getClass ( argtype );
	 
	samepackage = isSamePackage ( getPackageName ( argtype ), currpackagename );
	
       }
       else if ( invexpr.getMethod().getParameterType( counter ) instanceof ArrayType )
       {
       
	Type t = ( ( ArrayType ) invexpr.getMethod().getParameterType( counter ) ).baseType;
       
	if ( t instanceof RefType )
	{
	
	 ref = true;

	 String argtype = t.toString();
      
	 sc = scm.getClass ( argtype );

	 samepackage = isSamePackage ( getPackageName ( argtype ), currpackagename );
	
	}
	
       }


       if ( ref )
       {

        if ( ( ( Integer ) classesHT.get ( sc.getName() ) ) == null )  
        classesHT.put ( sc.getName(), pub );

	if ( ! ( ( ( Modifier.isPublic ( sc.getModifiers() ) ) || samepackage ) ) )
	{  
 
      //	 System.out.println ( " MODIFIERS : "+ Modifier.toString ( sc.getModifiers() ) );

      //	 System.out.println ( " ILLEGAL ERROR ( INVOKE EXPR PARAMETER ) "+invexpr+" WHEN TRYING TO ACCESS CLASS : "+sc.getName()+" FROM METHOD : "+method.getSignature() );

         classesHT.put ( sc.getName(), def );

         errormethods.add ( method.getSignature() );

	}   

       }

       counter++;

      }

       samepackage = false; 

       if ( invexpr instanceof SpecialInvokeExpr )
       im = ( ( SpecialInvokeExpr ) invexpr).getBase();
       else if ( invexpr instanceof VirtualInvokeExpr )
       im = ( ( VirtualInvokeExpr ) invexpr).getBase();
       else if ( invexpr instanceof InterfaceInvokeExpr )
       im = ( ( InterfaceInvokeExpr ) invexpr).getBase();


       String basetype = im.getType().toString();

       SootMethod meth = invexpr.getMethod();

       dec = scm.getClass ( meth.getDeclaringClass().getName() ); 

       if ( currclass.getName().equals ( dec.getName() ) )
       sameclass = true;

       samepackage = isSamePackage ( getPackageName ( dec.getName() ), currpackagename );

       if ( ( ( Integer ) methodsHT.get ( meth.getSignature() ) ) == null )  
       methodsHT.put ( meth.getSignature(), pub );

       if ( ( ( Integer ) classesHT.get ( dec.getName() ) ) == null )
       classesHT.put ( dec.getName(), pub );


       if ( ! ( ( ( Modifier.isPublic ( dec.getModifiers() ) ) || samepackage ) ) )
       {
      
         //	System.out.println ( " MODIFIERS : "+ Modifier.toString ( dec.getModifiers() ) );
      
         //	System.out.println ( " ILLEGAL ERROR ( INVOKE EXPR BASE ) WHEN TRYING TO ACCESS CLASS : "+dec.getName()+" FROM METHOD : "+method.getSignature() + dec);
	
        classesHT.put ( dec.getName(), def );

        errormethods.add ( method.getSignature() );

       }


       if ( im.getType() instanceof ArrayType )
       sameprotected = currclass.getName().equals ( "java.lang.Object" );
       else
       sameprotected = isSameProtected ( dec, currclass, basetype, currpackagename ); 


       if ( ( Modifier.isPrivate ( meth.getModifiers() ) ) && ( ! sameclass ) ) 
       {
	
	methodsHT.put ( meth.getSignature(), priv );
	
        adjustSuperMethods ( meth, priv );

        //	System.out.println( "ILLEGAL ERROR WHEN TRYING TO ACCESS PRIVATE INSTANCE METHOD : "+meth.getSignature()+" FROM METHOD "+method.getSignature() );
       
        errormethods.add ( method.getSignature() );

        errorinvokeexprs.add ( invexpr );

       }
       else if ( ( Modifier.isProtected ( meth.getModifiers() ) ) && ( ! sameprotected ) )
       {
	
	if ( ( ( Integer ) methodsHT.get ( meth.getSignature() ) ).intValue() > 2 )
	methodsHT.put ( meth.getSignature(), prot );

        adjustSuperMethods ( meth, prot );

        //	 System.out.println( "ILLEGAL ERROR WHEN TRYING TO ACCESS PROTECTED INSTANCE METHOD : "+meth.getSignature()+" FROM METHOD "+method.getSignature() ); 
       
         errormethods.add ( method.getSignature() );

         errorinvokeexprs.add ( invexpr );

       
       }
       else if ( ! ( ( Modifier.isProtected( meth.getModifiers() ) || Modifier.isPrivate( meth.getModifiers() ) ) || Modifier.isPublic ( meth.getModifiers() ) ) ) 
       {
	
	if ( ! samepackage )
	{
	 
	 if ( ( ( Integer ) methodsHT.get ( meth.getSignature() ) ).intValue() > 1 )
	 methodsHT.put ( meth.getSignature(), def );

         adjustSuperMethods ( meth, def );
	 
         //	 System.out.println( "ILLEGAL ERROR WHEN TRYING TO ACCESS DEFAULT MODIFIER INSTANCE METHOD : "+meth.getSignature()+" FROM METHOD "+method.getSignature() );

         errormethods.add ( method.getSignature() );

         errorinvokeexprs.add ( invexpr );

	}

       }

      }
        
    }

   }

   } catch ( java.lang.RuntimeException e ) { /* System.err.println ( "Jimple cannot handle : "+method.getSignature() ); */ }


   //   stmtListBody = null;

  }













  public void adjustSuperMethods ( SootMethod m, Integer integer ) {

   SootClass sc = scm.getClass ( m.getDeclaringClass().getName() );

   while ( sc.hasSuperClass() )
   {
 
    sc = scm.getClass ( sc.getSuperClass().getName() );

    if ( sc.declaresMethod ( m.getName(), m.getParameterTypes() ) )
    {

     SootMethod scmeth = sc.getMethod ( m.getName(), m.getParameterTypes() );     

     if ( ( ( Integer ) methodsHT.get ( scmeth.getSignature() ) ) == null )
     methodsHT.put ( scmeth.getSignature(), integer );
     else 
     {

      if ( ( ( Integer ) methodsHT.get ( scmeth.getSignature() ) ).intValue() > integer.intValue() )
      methodsHT.put ( scmeth.getSignature(), integer );

     }

    }

   }

  }









  public String getPackageName ( String classname ) {

   int index = classname.lastIndexOf ( '.' );

   String packagename = null;

   if ( index > -1 )
   packagename = classname.substring ( 0, index );

   return packagename; 

  }







  public boolean isSamePackage ( String s1, String s2 ) {


   if ( ( s1 == null ) && ( s2 == null ) )
   return true;
   else if ( ( s1 != null ) && ( s2 != null ) )
   {

    if ( ( s1.compareTo ( s2 ) ) == 0 )
    return true;

   } 

   return false;

  }










  public boolean isSameProtected ( SootClass declaringclass, SootClass currclass, String baseType, String currpackage ) {

   boolean answer = false;

   if ( isSamePackage ( getPackageName ( declaringclass.getName() ), currpackage ) )   
   return true;
   else
   {

    boolean searching = true;

    SootClass sclass = currclass;

    if ( sclass.getName().equals ( declaringclass.getName() ) )
    searching = false;

    while ( ( sclass.hasSuperClass() ) && ( searching == true ) )   
    {

     sclass = sclass.getSuperClass();

     if ( sclass.getName().equals ( declaringclass.getName() ) )
     searching = false; 
     
    }

    if ( searching == true )
    return false;
    else
    {

     searching = true;
      
     sclass = scm.getClass ( baseType ); 

     if ( sclass.getName().equals ( currclass.getName() ) )
     searching = false;

     while ( ( sclass.hasSuperClass() ) && ( searching == true ) )   
     {

      sclass = sclass.getSuperClass();

      if ( sclass.getName().equals ( currclass.getName() ) )
      searching = false; 
     
     } 

     if ( searching == true )
     return false;
     else
     return true;

    }

   }

  }











  public boolean isSameStaticProtected ( SootClass declaringclass, SootClass currclass, String currpackage ) {

   boolean answer = false;

   if ( isSamePackage ( getPackageName ( declaringclass.getName() ), currpackage ) )   
   return true;
   else
   {

    boolean searching = true;

    SootClass sclass = currclass;

    if ( sclass.getName().equals ( declaringclass.getName() ) )
    searching = false;

    while ( ( sclass.hasSuperClass() ) && ( searching == true ) )   
    {

     sclass = sclass.getSuperClass();

     if ( sclass.getName().equals ( declaringclass.getName() ) )
     searching = false; 
     
    }

    if ( searching == true )
    return false;
    else
    return true;

   }

  }



  public Set getUnInlinableInvokeExprs() {

   return uninlinableexprs;

  }


/*

   public void setUnInlinableInvokeExprs ( SootMethod method ) {

    try {

     JimpleBody stmtListBody = Jimplifier.getJimpleBody ( method );
  
     StmtList l = stmtListBody.getStmtList();

     Iterator it = l.iterator();

     while ( it.hasNext() ) 
     {

      try {

      Stmt s = ( Stmt ) it.next();

      if ( s instanceof AssignStmt ) 
      {
        
       AssignStmt as = ( AssignStmt ) s;

       if ( as.getRightOp() instanceof InvokeExpr )
       {

        InvokeExpr ie = ( InvokeExpr ) as.getRightOp();

        if ( uninitialised > 0 )
        uninlinableexprs.add( ie );

       }
       else if ( as.getRightOp() instanceof NewExpr )
       {

        uninitialised++;

       } 
         
      }
      else if ( s instanceof InvokeStmt )
      {

       InvokeStmt is = ( InvokeStmt ) s;

       InvokeExpr ie = is.getInvokeExpr();

       SootMethod meth = ie.getMethod();

       if ( uninitialised > 0 )
       {

        if ( meth.getName().equals ( "<init>" ) )
        uninitialised--;

        if ( uninitialised > 0 )
        uninlinableexprs.add ( ie );

       }

     }

    } catch ( java.lang.RuntimeException e ) { }

   }     

  } catch ( java.lang.RuntimeException e1 ) { }
 
 }


*/




}


















































