// package ca.mcgill.sable.soot.sideEffect;
package ca.mcgill.sable.soot.jimple.toolkit.invoke;

import ca.mcgill.sable.soot.jimple.*;
import ca.mcgill.sable.soot.baf.*;
import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.util.*;
import java.util.*;
import java.util.StringTokenizer;


public class CallGraphBuilder{

  ClassGraphBuilder classGBuilder;
      
  // CR
  //Map MethodNodeHT = new Hashtable();

  Map MethodNodeHT = new HashMap();

  int num = 0;

  static List SCCs ;

  static long TotalStmts = 0;



  
  public CallGraphBuilder( ClassGraphBuilder c ){

    classGBuilder = c;

  }






  Set methodSet = new HashSet();

  Set supermethodSet = new HashSet();

  SootMethod currentMethod;

  Map invokeToContainerMethod = new HashMap();



  public MethodNode getContainerMethod ( InvokeExpr ie ) {
   
   MethodNode mn = (MethodNode) invokeToContainerMethod.get ( ie );
   
   if ( mn == null )
   throw new UnknownDeclaringMethodException("The declaring method of the invoke expression "+ie+" was not specified while building the invoke graph");

   return mn;

  }





  public SootMethod getSuperMethod( ClassNode classNode , SootMethod originalMethod , SootMethod currMethod ){

    boolean searching = true;

    SootMethod returnedmethod = originalMethod;

    SootMethod candMethod = null;

    while ( searching )
    {

     try {

      candMethod = classNode.getSootClass().getMethod( originalMethod.getName() , originalMethod.getParameterTypes() );

     } catch ( ca.mcgill.sable.soot.NoSuchMethodException e ){

	    // System.err.println( e.toString()+" IN  CGB.GETSUPERMETHOD() 1");

     }


     if (!(candMethod == null ))
     {

       if ( Modifier.isPrivate( candMethod.getModifiers() ) )
       {

         if ( ((candMethod.getDeclaringClass()).getName()).equals((currMethod.getDeclaringClass()).getName()) )
         { 

           returnedmethod = candMethod; 

           searching = false; 

         }
         else  
         {

           try  {

	    if ( classNode.getSootClass().hasSuperClass() )
	    classNode = classGBuilder.getNode( classNode.getSootClass().getSuperClass().getName() );
	    else
	    searching = false;

           } catch ( java.lang.RuntimeException e ){ }
		    
          } // ELSE

         } // IF MODIFIER.ISPRIVATE
	 else
         { 

          returnedmethod = candMethod; 

          searching = false; 
     
         }
	    
        } // IF CANDMETHOD == NULL
	else
	{

	  try  {

           if ( classNode.getSootClass().hasSuperClass() )
	   {
		 
            classNode = classGBuilder.getNode(classNode.getSootClass().getSuperClass().getName() );

           }
           else
           searching = false;


	  } catch ( java.lang.RuntimeException e ){ }

	 }  

	}


       if ( ! ((classNode.getSootClass()).hasSuperClass()) )
       {

	searching = false;

	try {

	 candMethod = classNode.getSootClass().getMethod( originalMethod.getName() , originalMethod.getParameterTypes() );
        
	} catch ( java.lang.RuntimeException e ) {} 

       }


       if (candMethod == null)
      returnedmethod = candMethod;

      return returnedmethod;

   }











    Set filterSubMethods( Set allsubmethods , SootMethod currMethod ) {

     HashSet candMethods = ( HashSet) allsubmethods;
  
     HashSet filteredMethods = new HashSet();

     filteredMethods.clear();

     Iterator Methiter = candMethods.iterator();

     while ( Methiter.hasNext() )
     {

      SootMethod candMethod = ( SootMethod ) Methiter.next();
 
      if ( Modifier.isPrivate( candMethod.getModifiers() ) /*|| Modifier.isProtected( candMethod.getModifiers() )*/ )
      {

	if ( ((candMethod.getDeclaringClass()).getName()).equals((currMethod.getDeclaringClass()).getName()) )
	filteredMethods.add(candMethod); 

       }   
       else
       filteredMethods.add(candMethod);       

      }

      return filteredMethods;
  
    }









    Set getAllSubMethodsOf( ClassNode classNode , SootMethod originalMethod ){
    
     methodSet.clear();

     List workQ = new ArrayList();

     try {

      Iterator childIter = classNode.getSubClasses().iterator();
    
      while( childIter.hasNext() )
      {

          ClassNode tempcl = ( ClassNode ) childIter.next();

          workQ.add(tempcl);

       }

      } catch ( ca.mcgill.sable.soot.jimple.toolkit.invoke.NoSuchClassNodeException e ){ }




      while ( !workQ.isEmpty() ) 
      { 
	
	classNode = (ClassNode)workQ.remove(0);

        // System.out.println ( "SUB "+classNode.getSootClass().getName() );
 
        try {

	 SootMethod subMethod = classNode.getSootClass().getMethod( originalMethod.getName() , originalMethod.getParameterTypes() ); 

         // System.out.println ("SUBMETHD "+subMethod );

	 methodSet.add( subMethod );

        } catch ( ca.mcgill.sable.soot.NoSuchMethodException e ) { }


        try {

	  Iterator subNodesIter = classNode.getSubClasses().iterator();
	  while( subNodesIter.hasNext() )
          {	  

	    workQ.add( (ClassNode) subNodesIter.next() );

	  }

        } catch ( ca.mcgill.sable.soot.jimple.toolkit.invoke.NoSuchClassNodeException e ){ }

      }

      return methodSet;

  }
  




  Set possibleMethods = new HashSet();





  Set getAllPossibleMethodsOf( InvokeExpr invokeExpr , SootMethod currmethod )
  {
  
    possibleMethods.clear();
  
    currentMethod = currmethod;
  
    invokeExpr.apply( new AbstractJimpleValueSwitch(){

      public void caseInterfaceInvokeExpr(InterfaceInvokeExpr v){
 
	List workQ = new ArrayList();

	// String intName = v.getMethod().getDeclaringClass().getName();

	String intName = v.getBase().getType().toString();

        if ( intName.endsWith ( "[]" ) )
        intName = v.getMethod().getDeclaringClass().getName();

	try{

	  ClassNode intNode = classGBuilder.getNode( intName );

	  workQ.add( intNode );

	  while( !workQ.isEmpty() )
          {

	    intNode = (ClassNode)workQ.remove(0);

            if ( ! intNode.isInterface() )
            {
 
             boolean success = true;
  
             SootMethod supermethod = null;
  
             try {

             supermethod = getSuperMethod( intNode, v.getMethod(), currentMethod );

             if ( supermethod == null )
             success = false;
  
             } catch ( ca.mcgill.sable.soot.NoSuchMethodException e ) {}

            if (success)
            possibleMethods.add( supermethod );

            //if ( ! v.getBase().getType().toString().endsWith ( "[]" ) )
            if ( ! intName.endsWith( "[]" ) )
            {

             HashSet allsubmethods = (HashSet) getAllSubMethodsOf( intNode , v.getMethod() );
         
             HashSet posssubmethods = (HashSet) filterSubMethods( allsubmethods , currentMethod );
         
             possibleMethods.addAll( posssubmethods );

            }

           } 
           else 
           {

	    Iterator implementersIter = intNode.getImplementers().iterator();

            // System.out.println ( "IMP SIZE "+intNode.getImplementers().size() );
	
	    while( implementersIter.hasNext() )
            {

	      ClassNode implementerNode = (ClassNode)implementersIter.next();

              // System.out.println ( "IMP NODE "+implementerNode.getSootClass().getName() );

	      if( implementerNode.isInterface() )
	      {

          	  if( !workQ.contains( implementerNode ) )
		  workQ.add( implementerNode );

              }
	      else
              {

	       boolean success = true;

	       SootMethod supermethod = null;

	       try {

		supermethod = getSuperMethod( implementerNode, v.getMethod(), currentMethod );

		if ( supermethod == null )
		success = false;  

	       }
	       catch ( ca.mcgill.sable.soot.NoSuchMethodException e ) {}

               // System.out.println ( "SUCCESS "+success );

	       if (success)
	       possibleMethods.add( supermethod );

               HashSet allsubmethods = ( HashSet) getAllSubMethodsOf( implementerNode , v.getMethod() );

               HashSet posssubmethods = ( HashSet ) filterSubMethods( allsubmethods , currentMethod );

               possibleMethods.addAll( posssubmethods );

             } // ELSE

	    } // WHILE IMPLEMENTERSITER

            } // ELSE

       	   } // WHILE !WORKQ

       	  } catch ( NoSuchClassNodeException e ) { } 

         } // CASE INTERFACEINVOKE
      
        public void caseSpecialInvokeExpr(SpecialInvokeExpr v)
	{

          String className = v.getMethod().getDeclaringClass().getName();
        
          try{
         
            ClassNode classNode = classGBuilder.getNode( className );

            if ( ! ( ( v.getMethod().getName().equals("<init>") ) || ( Modifier.isPrivate ( v.getMethod().getModifiers() ) ) ) )
            {

             try {

              boolean lookupflag = isLookupPossible ( currentMethod.getDeclaringClass(), v.getMethod().getDeclaringClass() ); 

              if ( lookupflag ) 
              classNode = classGBuilder.getNode ( currentMethod.getDeclaringClass().getSuperClass().getName() );

              } catch ( java.lang.RuntimeException e ) {}

             }


	    boolean success = true;
         
	    SootMethod supermethod = null;
      
	    try {   

	     supermethod = getSuperMethod( classNode, v.getMethod(), currentMethod );
          
	     if ( supermethod == null )
	     success = false;
             else
             {

              supermethod = v.getMethod();

              success = true;

             } 
          
	    }       
	    catch ( ca.mcgill.sable.soot.NoSuchMethodException e ) {}      

            if (success)
	    possibleMethods.add( supermethod );
            
          } catch ( ca.mcgill.sable.soot.jimple.toolkit.invoke.NoSuchClassNodeException e ){ }

	}  // CASE SPECIALINVOKE
      

      public void caseStaticInvokeExpr(StaticInvokeExpr v)
      {

       possibleMethods.add( v.getMethod() ); 

       // if ( v.getMethod().getName().equals ( "newInstance" ) )
       // System.out.println ( "NEWINSTANCE() IN "+currentMethod.getSignature() );

      }
      
      public void caseVirtualInvokeExpr(VirtualInvokeExpr v)
      {

	  //String className = v.getMethod().getDeclaringClass().getName();

	List workQ = new ArrayList();

	// String intName = v.getMethod().getDeclaringClass().getName();

	String intName = v.getBase().getType().toString();

    //    System.out.println ( "SIGNATURE = "+v.getMethod().getDeclaringClass().getName());
    //    System.out.println ( "BASE = "+intName);

        if ( intName.endsWith ( "[]" ) )
        intName = v.getMethod().getDeclaringClass().getName();

	try{

	  ClassNode intNode = classGBuilder.getNode( intName );

	  workQ.add( intNode );

	  while( !workQ.isEmpty() )
          {

	    intNode = (ClassNode)workQ.remove(0);

            if ( ! intNode.isInterface() )
            {
 
             boolean success = true;
  
             SootMethod supermethod = null;
  
             try {

               //             System.out.println ("IN GETSUPERMETHOD");

             supermethod = getSuperMethod( intNode, v.getMethod(), currentMethod );

             if ( supermethod == null )
             success = false;
  
             } catch ( ca.mcgill.sable.soot.NoSuchMethodException e ) {}

            if (success)
            possibleMethods.add( supermethod );

            //if ( ! v.getBase().getType().toString().endsWith ( "[]" ) )
            if ( ! intName.endsWith( "[]" ) )
            {

             HashSet allsubmethods = (HashSet) getAllSubMethodsOf( intNode , v.getMethod() );
         
             HashSet posssubmethods = (HashSet) filterSubMethods( allsubmethods , currentMethod );
         
             possibleMethods.addAll( posssubmethods );

            }

           } 
           else 
           {

	    Iterator implementersIter = intNode.getImplementers().iterator();

            // System.out.println ( "IMP SIZE "+intNode.getImplementers().size() );
	
	    while( implementersIter.hasNext() )
            {

	      ClassNode implementerNode = (ClassNode)implementersIter.next();

              // System.out.println ( "IMP NODE "+implementerNode.getSootClass().getName() );

	      if( implementerNode.isInterface() )
	      {

          	  if( !workQ.contains( implementerNode ) )
		  workQ.add( implementerNode );

              }
	      else
              {

	       boolean success = true;

	       SootMethod supermethod = null;

	       try {

		supermethod = getSuperMethod( implementerNode, v.getMethod(), currentMethod );

		if ( supermethod == null )
		success = false;  

	       }
	       catch ( ca.mcgill.sable.soot.NoSuchMethodException e ) {}

               // System.out.println ( "SUCCESS "+success );

	       if (success)
	       possibleMethods.add( supermethod );

               HashSet allsubmethods = ( HashSet) getAllSubMethodsOf( implementerNode , v.getMethod() );

               HashSet posssubmethods = ( HashSet ) filterSubMethods( allsubmethods , currentMethod );

               possibleMethods.addAll( posssubmethods );

             } // ELSE

	    } // WHILE IMPLEMENTERSITER

            } // ELSE

       	   } // WHILE !WORKQ

       	  } catch ( NoSuchClassNodeException e ) { } 


/*

	  String className = v.getBase().getType().toString();

          if ( className.endsWith ( "[]" ) )
          className = v.getMethod().getDeclaringClass().getName();

	  try{
	    
	    ClassNode classNode = classGBuilder.getNode( className );

	    boolean success = true;
  
	    SootMethod supermethod = null;
  
	    try {

	     supermethod = getSuperMethod( classNode, v.getMethod(), currentMethod );

	     if ( supermethod == null )
	     success = false;
  
	    }
	    catch ( ca.mcgill.sable.soot.NoSuchMethodException e ) {}

            if (success)
	    possibleMethods.add( supermethod );

            // if ( ! v.getBase().getType().toString().endsWith ( "[]" ) )
            if ( ! className.endsWith ( "[]" ) )
            {

             HashSet allsubmethods = (HashSet) getAllSubMethodsOf( classNode , v.getMethod() );
         
             HashSet posssubmethods = (HashSet) filterSubMethods( allsubmethods , currentMethod );
         
             possibleMethods.addAll( posssubmethods );

            }
	    
	  } catch ( ca.mcgill.sable.soot.jimple.toolkit.invoke..NoSuchClassNodeException e ){ }


*/

     } // CASE VIRTUALINVOKE


    });

    return possibleMethods;

  }
  






  public boolean isLookupPossible ( SootClass baseclass, SootClass topclass ) {

   boolean found = false;

   while ( ( baseclass.hasSuperClass() ) && ( ! found ) )
   {

    baseclass = baseclass.getSuperClass();

    if ( baseclass.getName().equals( topclass.getName() ) )
    found = true;

   }
   
   return found;

  }
  







  int methodcount = 0;







  MethodNode CreateNodeAndAddToHT( SootMethod method ){

    MethodNode  mNode = new MethodNode( method );

    if ( !((MethodNode) MethodNodeHT.get( mNode.getName() ) == null ) )
    {
	mNode = (MethodNode) MethodNodeHT.get( mNode.getName() );
    }
    else
    {

      // if ( Modifier.isNative ( method.getModifiers() ) )
      //  System.out.println ( "NATIVE NODE "+method.getSignature() );

        mNode.code = methodcount++;

        if ( ( methodcount % 10 ) == 0 )
        System.out.print(".");

	MethodNodeHT.put( mNode.getName() , mNode );
    }    

    return mNode;
  }







   public MethodNode getNode( SootMethod method ) {

    MethodNode methodNode ;

    String methodName = Helper.getFullMethodName( method );

    if ( (methodNode = (MethodNode)MethodNodeHT.get( methodName )) != null )
    return methodNode;

    throw new NoSuchMethodNodeException( "Cannot find method : " + methodName );
  
   }







  void removeNode( SootMethod method ) {
   
   MethodNode methodNode ;

   String methodName = Helper.getFullMethodName( method );

   if ( (methodNode = (MethodNode)MethodNodeHT.get( methodName )) != null )
   MethodNodeHT.remove(methodName);

  }










  /*
  public void ReplaceStrConnComp(){

    SCCs = new SCCDetector().computeSCCs( MethodNodeHT.values() );
    
    Iterator iter = SCCs.iterator();
    
    while( iter.hasNext() ){
      
      List aSCC = (List)iter.next();
      
      // if the scc contains more than 1 node then it is really
      // a scc.
      if ( aSCC.size() > 1 ){

	SCCNode aSCCNode = new SCCNode( aSCC );
	aSCCNode.updateLinks();
	MethodNodeHT.put( aSCCNode.getName() , aSCCNode );

	// !!! DEBUG
	// remove from HT all method in a SCC
	//for( int a = 0 ; a < SCCs[i].size() ; a++ )
	  
	//  ((ca.mcgill.sable.util.Hashtable)MethodNodeHT).remove( ((MethodNode)SCCs[i].get(a)).getName() );
	  

	// !!! DEBUG
	//aSCCNode.printContents();

      }
    }
  }

  */










  // DEBUG
  // returns the SCC ( a List here ) containing mNode 
  // returns null if mNode doesn't belong to any SCC.

    static List belongsTo( MethodNode mNode ){

    Iterator iter = SCCs.iterator();
    
    while( iter.hasNext() ){
      List aSCC = (List)iter.next();
      if( aSCC.indexOf( mNode ) != -1 )
	return aSCC;
    }

    //OLD verison
    //for ( int i = 0 ; i < SCCs.length ; i++ ){
    //  if( SCCs[i].indexOf( mNode ) != -1 )
    //return SCCs[i];
    //}

    return null;
  }
  








  
  /* final */ List invokeExprs = new ArrayList();








  /**
   * returns all invokeExprs found in method.
   */

   List getInvokeExprs( SootMethod method ){

     // invokeExprs.clear();

     invokeExprs = new ArrayList();

    try {

    
      JimpleBody jimpleBody = Jimplifier.getJimpleBody( method );
    
      Iterator stmtIter = jimpleBody.getStmtList().iterator();

      while ( stmtIter.hasNext() ){

	TotalStmts++;

	try {

	  Stmt stmt = (Stmt)stmtIter.next();

	  stmt.apply( new AbstractStmtSwitch(){

	
	    public void caseInvokeStmt(InvokeStmt s){

	      invokeExprs.add( s.getInvokeExpr() );

	    }
	
	    public void caseAssignStmt(AssignStmt s){

	      if( s.getRightOp() instanceof InvokeExpr ){

		invokeExprs.add( s.getRightOp() );

	      }

	    }
	
	    public void caseReturnStmt(ReturnStmt s){

	      if( s.getReturnValue() instanceof InvokeExpr )
	      invokeExprs.add( s.getReturnValue() );

	    }

	  });

	} catch (java.lang.RuntimeException e ){}

      } // WHILE 

    } catch(java.lang.RuntimeException e ){}  

    return invokeExprs;

  }





  int sites=0, edges=0;




  public int getSitesNum () {

   return sites;

  }





  public int getEdgesNum () {

   return edges;

  } 




  
  Map clinitHT = new HashMap();

  Map finalizeHT = new HashMap();




  public Set recursiveMethods = new HashSet();

  boolean addedinitialize = false;

  


  public void buildCallGraph( SootMethod method ){    

    // if method has already been analysed, do nothing
    if( MethodNodeHT.get( Helper.getFullMethodName(method) ) != null ) 
    return;
    
    String methodName = Helper.getFullMethodName( method );

    MethodNode methodNode = CreateNodeAndAddToHT( method );

    List workQ = new ArrayList();

    workQ.add( methodNode );

    while( !workQ.isEmpty() ){

      try {
    
	methodNode = (MethodNode)workQ.remove( 0 );

	SootMethod cmethod = methodNode.getMethod();

	SootClass cclass = cmethod.getDeclaringClass();

	List paramList = new ArrayList();
           
	if (cclass.declaresMethod(new String("<clinit>") , paramList ))
	{

	    if ( ( ( ( String ) clinitHT.get(cclass.getName())) == null ) ) 
            {

	      clinitHT.put(cclass.getName(),cclass.getName());

	      MethodNode clinitMNode = CreateNodeAndAddToHT( cclass.getMethod(new String("<clinit>") , paramList) );

	      workQ.add(clinitMNode);

	    }

	}


        if (cclass.declaresMethod(new String("finalize") , paramList ))
        {
        
            if ( ( ( ( String ) finalizeHT.get(cclass.getName())) == null ) )
            {

              finalizeHT.put(cclass.getName(),cclass.getName());

              MethodNode finalizeMNode = CreateNodeAndAddToHT( cclass.getMethod(new String("finalize") , paramList) );

              workQ.add(finalizeMNode);
             
            }
         
        }



        if ( cclass.getName().equals("java.lang.System") )
        {
        
           if ( ! addedinitialize )
           {

              MethodNode initializeMNode = CreateNodeAndAddToHT( cclass.getMethod(new String("initializeSystemClass") , paramList) );

              addedinitialize = true; 

              workQ.add(initializeMNode);
             
            }
         
        }


	List invokeExprs = getInvokeExprs( methodNode.getMethod() );

    methodNode.setInvokeExprs(invokeExprs);

    // System.out.println ("INVOKEEXPRS NUM = "+invokeExprs.size());

	//CR
	// used in the Id of an invokeExpr
	int invokeExprNum = 1;

	Iterator iter = invokeExprs.iterator();

	while( iter.hasNext() ){ 

	  try {
    
	    InvokeExpr invokeExpr = (InvokeExpr)iter.next();

        invokeToContainerMethod.put ( invokeExpr, methodNode ); 

             //System.out.println ( "INVOKEEXPR "+invokeExpr );

	    // CR
	    String invokeExprId = Helper.getInvokeExprId( invokeExprNum++ , invokeExpr.getMethod() );

	    CallSite callSite = new CallSite( invokeExprId , invokeExpr );
            callSite.setCallerID ( methodNode.getMethod().getSignature()+"$"+( invokeExprNum - 1 ) );
 
	    //VIJAY
	    //CallSite callSite = new CallSite( invokeExpr );
        
	    sites++;

	    methodNode.addCallSite( callSite, new Integer ( invokeExprNum -1 ) );
      
            //System.out.println ( "ADDED CS NO "+invokeExprNum );

	    Set possibleMethods = getAllPossibleMethodsOf( invokeExpr ,methodNode.getMethod());

            //System.out.println ("POSS MTHDSIZE "+possibleMethods.size() );
        /*
            if ( possibleMethods.size() < 1 )
             System.out.println ( "ZZERO INVOKE "+invokeExpr+" AT "+callSite.getCallerID() );
             */


	    Iterator methodIter = possibleMethods.iterator();

	    while( methodIter.hasNext() )
            {

	     try {
	  
	      SootMethod possibleMethod = (SootMethod)methodIter.next();

              // System.out.println ( "POSS MTHD 1 "+possibleMethod.getSignature() );

              possibleMethod = classGBuilder.getNode ( 
possibleMethod.getDeclaringClass().getName() ).getSootClass().getMethod ( 
possibleMethod.getName(), possibleMethod.getParameterTypes() );

             // System.out.println ( "POSS MTHD 2 "+possibleMethod.getSignature() );

	  
	      MethodNode possibleMNode;
	  
		// if possibleMethod has not been yet inserted into the HT then ...
		if ( ( possibleMNode = (MethodNode)MethodNodeHT.get(Helper.getFullMethodName(possibleMethod)) ) == null )
                {

		  possibleMNode = CreateNodeAndAddToHT( possibleMethod );

		  workQ.add( possibleMNode ); 

		}
	  
		callSite.addMethod( possibleMNode );

		edges++;

        possibleMNode.addInvokingSite( callSite );
		possibleMNode.addCaller( methodNode );
		possibleMNode.incomingedges++;
        /*
                System.out.println ( "MTHD "+possibleMNode.getMethod().getSignature()+
                                     " POSSIBLY CALLED BY "+
                                      methodNode.getMethod().getSignature() );
                                      */
                if ( possibleMNode.getMethod().getSignature().equals ( methodNode.getMethod().getSignature() ) )
                recursiveMethods.add ( possibleMNode.getMethod().getSignature() );                                 

	    } catch( RuntimeException e ){ }

 	   } // WHILE
 
	 } catch( RuntimeException e ){

            System.out.println("\n\n--- in buildCallGraph(method) : getting possible methods reached by an invokeExpr  " + method.getSignature()+"\n\t "+ e.toString() );
            
	 }

	} // WHILE ITER.HASNEXT

      } catch( RuntimeException e ){

        System.out.println("\n\n--- in buildCallGraph(method) :   " +method.getSignature() +"\n\t " + e.toString() );

      } 

    } // WHILE !WORKQ

  }










  //CR
  public void buildCallGraphForThreads(){

    classGBuilder.getStartAndRunMethods();

    System.out.println("Run methods num : " + classGBuilder.runmethods.size());

    for( Iterator runMethodIter = classGBuilder.runmethods.iterator() ;
	 runMethodIter.hasNext() ; ){

      SootMethod method = (SootMethod)runMethodIter.next();

      buildCallGraph( method );
    }

    for( Iterator startMethodIter = classGBuilder.startmethods.iterator() ;
	 startMethodIter.hasNext() ; ){

      SootMethod method = (SootMethod)startMethodIter.next();

      buildCallGraph( method );
    }
  }












/*  
    //VIJAY
    public Map getCallGraph(){
    return MethodNodeHT;
    }
*/    




  //CR
  public Collection getCallGraph(){
    //CR
    return MethodNodeHT.values();
  }









  // CR
  public void printSummary(){

    System.out.println( "\nCallGraph Contains: " + MethodNodeHT.size() +
			" nodes"  );
    
    System.out.println("TOTAL NO. OF SITES : "+sites); 
    System.out.println("TOTAL NO. OF EDGES : "+edges);    
    System.out.println("TOTAL NO. OF JIMPLE STMTs : " + TotalStmts);    
  }    






   public void buildCallGraph( String className , String methodName , List paramList ){
    
    try{

    //  SootClassManager cm = classGBuilder.getManager();

    //  SootClass bClass = cm.getClass( className );


      SootClass bClass = classGBuilder.getNode ( className ).getSootClass();   

      SootMethod method = Helper.getMethod( bClass , methodName , paramList );
      
      buildCallGraph( method );
     
      Iterator clasnodeit = classGBuilder.getClassNodes().iterator();

      while ( clasnodeit.hasNext() )
      {
    
      SootClass scl = ( ( ClassNode ) clasnodeit.next() ).getSootClass();

      Iterator meeit = scl.getMethods().iterator();

      while ( meeit.hasNext() )
      {

       SootMethod meem = ( SootMethod ) meeit.next();
   
      try {

//      JimpleBody meejb = ( JimpleBody ) ( new StoredBody ( Jimple.v() ) ).resolveFor ( meem );

        JimpleBody meejb = ( JimpleBody ) meem.getActiveBody();

      } catch ( java.lang.RuntimeException ee ) {

        // System.out.println ( "DOESNT HAVE CLJIMPLEBODY OF "+meem.getSignature() );

      }

      }

      }




      //ReplaceStrConnComp();


      // DEBUG : check if any cycle left
      /*{

	List graphNodes = new VectorList();
	
	// get all nodes in the callgraph
	Object[] keys = MethodNodeHT.keySet().toArray();

	for ( int i = 0 ; i < keys.length ; i++ ){
	graphNodes.add( MethodNodeHT.get( keys[i] ) );
	}


	transposeGraph( false );

	DFS dfs = new DFS( graphNodes );

	dfs.checkCycle = true;

	dfs.doDFS();
	}*/
      
       
      }catch ( Exception e ){

      // Exception could be anything wrong.
      
      /*System.err.println("\\ Impossible to build call graph for: " 
	+ className + "*" 
	+ mSig );*/
      
      System.err.println("\n\nError building CallGraph for:   " + className +"\n\t " + e.toString() );
      e.printStackTrace();
    }

  }

















  /**
   * Builds the call graph for all methods of all the classes that are 
   * in the classgraphbuilder.
   */

  public void buildCallGraph( ClassGraphBuilder classBuilder ){    

   ClassNode classNode = null;

   System.out.println("\n\n\nBUILDING CALLGRAPH FOR ALL CLASSES :  \n");

   try{

    for( Iterator classNodeIter = classBuilder.getClassNodes().iterator() ; classNodeIter.hasNext() ;  )
    {
	
	try{

	  classNode = (ClassNode)classNodeIter.next();
	  
	  for( Iterator methodIter=classNode.getSootClass().getMethods().iterator(); methodIter.hasNext() ; )
          {
	  
	    SootMethod method = (SootMethod)methodIter.next();
	    
	    // if method has not yet been analysed

	    if( MethodNodeHT.get( Helper.getFullMethodName(method) ) == null ) 
	    buildCallGraph( method );

	  }

	  //ReplaceStrConnComp();

	} catch ( RuntimeException e ){

	  System.out.println("\n\n----Error building CallGraph for methods of ClassNode:   " + classNode.getName() + "\n" + e );

	}

      } // FOR

      //Jimplifier.setCallGraphBuilder( null );

    } catch ( Exception e ){

      // Exception could be anything wrong.
      System.out.println("\n\n???? Error building CallGraph for methods of ClassNode:   " + classNode.getName() +"\n\t " );
      e.printStackTrace();
      System.exit(-1);

    }

  }







  public MethodNode getNode( String methodName ) throws NoSuchMethodNodeException{

    MethodNode methodNode ;

    if ( (methodNode = (MethodNode)MethodNodeHT.get( methodName )) != null )
    return methodNode;
    
    throw new NoSuchMethodNodeException( "The method " + methodName+" not found in the invoke graph" );

  }








  public ClassGraphBuilder getClassGraphBuilder(){

    return classGBuilder;

  }








  /**
   * set to null all SootMethod Nodes in the callGraph
   */
    void deleteMethodNodeHT(){
    if( MethodNodeHT != null ){
      Object[] keyArr = MethodNodeHT.keySet().toArray();
      for ( int i = 0 ; i < keyArr.length ; i++ ){
	    
	((MethodNode)MethodNodeHT.get(keyArr[i])).prepareForGC() ;
      }
    }
  }











  /**
   * 
   */
  public void printContents(){
    // print HT contents
    Object[] keys = MethodNodeHT.keySet().toArray();

    MethodNode mNode;

    // print  relations
    {
      System.out.println(" -----------------------------------");
      System.out.println(" ------------ CALL GRAPH -----------");
      System.out.println(" -----------------------------------");
      for( int i = 0 ; i < keys.length ; i++ ){
	System.out.println("\nMethod:  " + (String)keys[i] + " ->"  );
	
        mNode = (MethodNode)MethodNodeHT.get(keys[i]);


	if( mNode.getCallSites() != null ){
	
	  //	  Object[] skeys = mNode.getCallSites().keySet().toArray();

	  //for ( int c = 0 ; c < skeys.length ; c++ ){

	  Iterator iter = mNode.getCallSites().iterator();
	  while( iter.hasNext() ){
	    
	    CallSite callSite = (CallSite)iter.next();

	    try{
	      //CR
	      /*
		System.out.println( "\n\t" + callSite.getInvokeExprId()
		.toString() + "\t -might calls :\n");
		*/

	    } catch ( NoInvokeExprException e ){
	      // Only SCCNodes don't have an invokeExpr
	      System.out.println( "\n\t SCCNode callSite: \t -might calls:\n");
	    }
	    
	    Object[] methods = callSite.getMethods().toArray();
	 
	    for( int m = 0 ; m < methods.length ; m++ )
	      System.out.println( "\t\t" + ((MethodNode)methods[m]).getName());
	  }	  
	}


	if( mNode.getCallers() != null ){

	  Object[] methods = mNode.getCallers().toArray();
	  System.out.println( "\n\t -calledBY");
	  for( int m = 0 ; m < methods.length ; m++ )
	    System.out.println( "\t\t" + ((MethodNode)methods[m]).getName()  );

	  System.out.println();
	}
      }
    }
    
    System.out.println( "\nCallGraph Contains: " + keys.length + " nodes"  );
  }
    



}















