// package ca.mcgill.sable.soot.virtualCalls;

package ca.mcgill.sable.soot.jimple.toolkit.invoke;

//import java.util.*;
import java.io.*;
import ca.mcgill.sable.soot.jimple.*;
import ca.mcgill.sable.soot.grimp.*;
import ca.mcgill.sable.util.*;
import java.util.*;
import ca.mcgill.sable.soot.baf.*; 
// import ca.mcgill.sable.soot.sideEffect.*;
import ca.mcgill.sable.soot.*;

public class Optimizer {


	
  private InvokeExpr currInvokeExpr;

  private InterfaceInvokeExpr currInterfaceInvokeExpr;

  private SpecialInvokeExpr currSpecialInvokeExpr;

  private StaticInvokeExpr currStaticInvokeExpr;

  private VirtualInvokeExpr currVirtualInvokeExpr;

  private boolean interfaceInvoked;

  private boolean specialInvoked;

  private boolean staticInvoked;

  private boolean virtualInvoked;

  private boolean assignflag;

  private boolean invokeflag;

  private JimpleBody listBody;  

  private JimpleBody melistBody;

  private Map localsHT = new HashMap();
  
  private SootMethod currmethod;

  private SootMethod me;

  private BinopExpr correctbinopexpr;

  private UnopExpr correctunopexpr;

  private InvokeExpr correctinvokeexpr;

  private Stmt correctstmt;

  private Value op1;

  private Value op2;

  private Value op;

  private Value returnvariable;

  private List args;

  private int numstmts;

  private Stmt returnstmt;

  private Local dummyreturn;

  private int inlinenumstmts;

  private int targetnumstmts;

  private StmtList targetmeth;

  private StmtList inlinablemeth;

  private Constant correctconstant;

  private Type correcttype;

  private Jimple jimple = Jimple.v();

  Integer priv = new Integer ( 0 );

  Integer def = new Integer ( 1 );

  Integer prot = new Integer ( 2 );

  Integer pub = new Integer ( 3 );

  HashMap classesHT = new HashMap();

  HashMap methodsHT = new HashMap();

  HashMap fieldsHT = new HashMap();

  HashMap resolverclassesHT;

  HashMap resolvermethodsHT;

  HashMap resolverfieldsHT;

  SootClassManager scm;

  ClassGraphBuilder clgb;

  CallGraphBuilder cagb;

  Resolver resolver;

  Set changedclasses = new HashSet();

  Set incorrectlyjimplified;

  Type returnvartype;

  Set removedmethods = new HashSet();



  /*

 public void collectThisVars( SootMethod meth, JimpleBody listbody ) {


      currmethod = meth;

      parameterHT.clear();

      Iterator stmtIter = listbody.getStmtList().iterator();
	   
      while ( stmtIter.hasNext() )
      {
	    
       try {

	Stmt stmt = (Stmt)stmtIter.next();
	   
	stmt.apply( new AbstractStmtSwitch(){

	 public void caseIdentityStmt(IdentityStmt s){

	  if ( s.getRightOp() instanceof ThisRef )
	  {

	    // String localname = currmethod.getSignature()+(((Local)
s.getLeftOp()).getName());

	   String localname = ( ( Local ) s.getLeftOp() ).getName();

	   String actualthis = ( ( Local ) currInvokeExpr.getBase() ).getName();

	   parameterHT.put( localname, actualthis );

	  } // IF S.GETRIGHTOP() 
	  else if ( s.getRightOp() instanceof ParameterRef )
	  {

	   String parametername = ((Local) s.getLeftOp()).getName();
	   
	   try {
       
	    String actualparameter = ( ( Local ) currInvokeExpr.getBase()
).getName();

	    int parameterindex = ((ParameterRef) s.getRightOp()).getIndex();
  
	    parameterHT.put(currmethod.getSignature()+"$"+parameterindex,
currmethod.getSignature()+parametername);
	
	   } catch ( java.lang.RuntimeException e ) {}

	  }

	}

       });

      } catch ( java.lang.RuntimeException e){}

     } // WHILE STMTITER

    }




 */







  private int j = 0; 

  private int returnj = 0;

  private int nullcheckj = 0;

  private int addressj = 0;

  private int throwj = 0;




  public Optimizer () {}

  public void dummy() {}





  public Optimizer ( CallGraphBuilder callgb ) {

   cagb = callgb;

  }



  HashMap methodsToMinDepth = new HashMap();



  public List setMethodDepths ( Collection methods ) {

   List methodsQ = new ArrayList();

   List reachedmethods = new ArrayList();

   Iterator methodsit = methods.iterator();

   Integer zero = new Integer(0);

   while ( methodsit.hasNext() )
   {

    try {

    MethodNode mn = ( MethodNode ) methodsit.next();

    System.out.println ( "DEPTHMETH = "+mn.getMethod().getSignature() );

    System.out.println ( "DEPTHINCOMING = "+mn.incomingedges );

    if ( ( ( mn.incomingedges == 0 ) || clgb.sources.contains ( mn.getMethod().getSignature() ) )  && ( ! mn.isRedundant ) ) 
    {

     // reachedmethods.add ( mn );

     System.out.println ( "DEPTH CALC 1 "+mn.getMethod().getSignature() );

    if ( ( ( Integer ) methodsToMinDepth.get ( mn.getMethod().getSignature()   ) ) != null )
    System.out.println ( "CAUTION 1 BCOZ "+mn.getMethod().getSignature() );

     methodsToMinDepth.put ( mn.getMethod().getSignature(), zero );

     System.out.println ( "PUT IN "+zero );

     System.out.println ( "DEPTHHT SIZE = "+methodsToMinDepth.size() );

     try {

     Set adjacentnodes = mn.getAllPossibleMethods();

     if ( adjacentnodes.size() > 0 )
     {

     Iterator adjacentit = adjacentnodes.iterator();

     while ( adjacentit.hasNext() )
     {

      MethodNode adjmn = ( MethodNode ) adjacentit.next();

      if ( ( ( ( Integer ) methodsToMinDepth.get ( adjmn.getMethod().getSignature() ) ) == null ) )
      {

      if ( ! methodsQ.contains ( adjmn ) )
      {
      
       if ( ( ! ( ( adjmn.incomingedges == 0 ) || clgb.sources.contains ( adjmn.getMethod().getSignature() ) ) )  && ( ! adjmn.isRedundant ) )
       methodsQ.add ( adjmn );

       System.out.println ( "METHODSQ ADDED 1 "+adjmn.getMethod().getSignature() );

      }

      }

     }

     }

    } catch ( java.lang.RuntimeException e ) {}

    }

    } catch ( java.lang.RuntimeException e ) { System.out.println ( "EXX1" );}
     
   }     

   int nextlevelindex = methodsQ.size();  

   int currentlevel = 1;

   Integer currentLevel = new Integer ( currentlevel );

   while ( ! methodsQ.isEmpty() )
   {

    try {

    MethodNode nextmethod = ( MethodNode ) methodsQ.get( 0 );

    // reachedmethods.add ( nextmethod );

    System.out.println ( "DEPTH CALC 2 "+nextmethod.getMethod().getSignature() );

    System.out.println ( "DEPTHMETH = "+nextmethod.getMethod().getSignature() );
    
    if ( ( ( Integer ) methodsToMinDepth.get ( nextmethod.getMethod().getSignature() ) ) != null )
    System.out.println ( "CAUTION 2 BCOZ "+nextmethod.getMethod().getSignature() 
);    

    methodsToMinDepth.put ( nextmethod.getMethod().getSignature(), currentLevel );

    System.out.println ( "PUT IN "+currentLevel );

    System.out.println ( "DEPTHHT SIZE = "+methodsToMinDepth.size() );


    try {

    Set adjacentnodes = nextmethod.getAllPossibleMethods();

    System.out.println ( "ADJACENTSIZE = "+adjacentnodes.size() );

    if ( adjacentnodes.size() > 0 )
    {

    Iterator adjacentit = adjacentnodes.iterator();

    while ( adjacentit.hasNext() )
    {

      MethodNode adjnode = ( MethodNode ) adjacentit.next();

      System.out.println ( " ADJNODE = "+adjnode.getMethod().getSignature() );

      if ( ( ( ( Integer ) methodsToMinDepth.get ( adjnode.getMethod().getSignature() ) ) == null ) )
      {
       
      System.out.println ( "PASSED 1 " );

      if ( ! methodsQ.contains ( adjnode ) )
      {

       System.out.println ( "PASSED 2 " );

       methodsQ.add ( adjnode );

       System.out.println ( "METHODSQ ADDED 2 "+adjnode.getMethod().getSignature() );


      }

      }

    }
    
    }

    } catch ( java.lang.RuntimeException e ) { }

    } catch ( java.lang.RuntimeException e ) { System.out.println ( "EXX2");}

    methodsQ.remove ( 0 );

    nextlevelindex--;

    if ( nextlevelindex == 0 )
    { 

      nextlevelindex = methodsQ.size(); 

      currentlevel++;

      currentLevel = new Integer ( currentlevel );

    }

   } // WHILE


   Iterator methit = methods.iterator();

   while ( methit.hasNext() )
   {

    MethodNode methn = ( MethodNode ) methit.next();

    if ( ( ( Integer ) methodsToMinDepth.get ( methn.getMethod().getSignature() ) ) != null )
    reachedmethods.add ( methn );

   }

   return reachedmethods;

  }







 public List sortByMethodDepths ( List methodslist ) {

  List sortedmethods = new ArrayList();

   if ( methodslist.size() > 0 )   
   {

    boolean searchforfirst = true;

    Iterator it = methodslist.iterator();   

    while ( ( it.hasNext() ) && searchforfirst )
    {

     MethodNode firstmn = ( MethodNode ) it.next();

     if ( ! firstmn.isRedundant )
     { 

      sortedmethods.add ( firstmn );

      searchforfirst = false;
     }

     System.out.println ( "FIRST SORT BY "+firstmn.getMethod().getSignature() );

     System.out.println ( " INTEGER HT "+( ( Integer ) methodsToMinDepth.get ( firstmn.getMethod().getSignature() ) ) );

    }

    while ( it.hasNext() )
    {
    
     MethodNode mn = ( MethodNode ) it.next();

     if ( ! mn.isRedundant )
     {

     System.out.println ( "SSORT BY "+mn.getMethod().getSignature() );

     System.out.println ( " INTEGER HT "+( ( Integer ) methodsToMinDepth.get ( mn.getMethod().getSignature() ) ) );

     int depth = ( ( Integer ) methodsToMinDepth.get ( mn.getMethod().getSignature() ) ).intValue();

     Iterator sortedmethodsit = sortedmethods.iterator();

     boolean inserted = false;

     while ( ( sortedmethodsit.hasNext() ) && ( inserted == false ) ) 
     {

       MethodNode nextmethod = ( MethodNode ) sortedmethodsit.next();

       System.out.println ( "NEXT SORT BY "+nextmethod.getMethod().getSignature() );

       System.out.println ( " NEXT INTEGER HT "+( ( Integer ) methodsToMinDepth.get ( nextmethod.getMethod().getSignature() ) ) );

       int nextdepth = ( ( Integer ) methodsToMinDepth.get ( nextmethod.getMethod().getSignature() ) ).intValue();

        // if ( depth > nextdepth )       

       if ( depth < nextdepth )
       {

        inserted = true;

        sortedmethods.add ( sortedmethods.indexOf( nextmethod), mn );

       }         

      }

      if ( inserted == false )
      {

       sortedmethods.add ( sortedmethods.size(), mn );

      }

      }

     }
    
    }

    System.out.println ( "SORTSSIZE = "+sortedmethods.size() );

    return sortedmethods;

  }




  public List returnImportantMethods ( Collection methods ) {

   List importantmethods = new ArrayList();

   Iterator it1 = methods.iterator();

   int i = 2;

   int startindex = 0;

   while ( it1.hasNext() )
   {

    MethodNode nextmn = ( MethodNode ) it1.next();


    int nextdepth = ( ( Integer ) methodsToMinDepth.get ( nextmn.getMethod().getSignature() ) ).intValue();

    System.out.println ("IMPORTANCE "+nextmn.getMethod().getSignature()+ " "+nextdepth );

    
    if ( nextdepth < i ) 
    {
 
     importantmethods.add ( startindex, nextmn ); 

    }
    else
    {

     startindex = importantmethods.size();

     i = i + 2;

     importantmethods.add ( startindex, nextmn );

    }

   }

   return importantmethods;

 }




/*


   while ( i < ( numBenchmarkNodes/2 ) )
   {
     
    MethodNode nextmn = ( MethodNode ) it1.next();

    if ( ! isLibraryNode ( nextmn.getMethod().getDeclaringClass().getName() ) )
    {

     i++;

     if ( ( nextmn.numloops > 0 ) || ( cagb.recursiveMethods.contains ( nextmn.getMethod().getSignature() ) ) )
     importantmethods.add ( nextmn );

    }

   }

   int importantindex = importantmethods.size();

   while ( it1.hasNext() )
   {

    MethodNode nextmn = ( MethodNode ) it1.next();

    importantmethods.add ( importantindex, nextmn );

   }

   return importantmethods;

  }

*/


  public int numBenchmarkNodes = 0;





  public List sortMethods ( Collection methods ) {

   List sortedmethods = new ArrayList();

   if ( methods.size() > 0 )   
   {

    Iterator it = methods.iterator();   

    MethodNode firstmn = ( MethodNode ) it.next();

    if ( ! isLibraryNode ( firstmn.getMethod().getDeclaringClass().getName() ) )
    numBenchmarkNodes++;
   

    sortedmethods.add ( firstmn );

    while ( it.hasNext() )
    {
    
     MethodNode mn = ( MethodNode ) it.next();

     if ( ! isLibraryNode ( mn.getMethod().getDeclaringClass().getName() ) )
     numBenchmarkNodes++;



     Iterator sortedmethodsit = sortedmethods.iterator();

     boolean inserted = false;

     while ( ( sortedmethodsit.hasNext() ) && ( inserted == false ) ) 
     {

       MethodNode nextmethod = ( MethodNode ) sortedmethodsit.next();

       if ( nextmethod.getMethod().getSignature().compareTo ( mn.getMethod().getSignature() )  < 0 )       
       {

        inserted = true;

        sortedmethods.add ( sortedmethods.indexOf( nextmethod), mn );

       }         

      }

      if ( inserted == false )
      {

       sortedmethods.add ( sortedmethods.size(), mn );

      }

     }
    
    }

    return sortedmethods;

   }







  public boolean unimportantmethod = false;

  public boolean syncflag = false;

  


  public void examineMethod ( MethodNode mn ) {
    
   SootMethod meth = mn.getMethod();

 if ( ( ! meth.getName().equals ( "<clinit>" ) ) && ( ! Modifier.isNative( meth.getModifiers() ) ) )

// if ( meth.getName().equals( new String("main") ) )
 {

   if ( allowedToChange ( meth.getDeclaringClass().getName() ) )
   {

   System.out.println ( "TRYING TO INLINE INSIDE TARGET "+meth.getSignature()+" "+( ( Integer ) methodsToMinDepth.get ( meth.getSignature() ) ) );

   try {

/*
    if ( ( mn.numloops > 0 ) || ( cagb.recursiveMethods.contains ( mn.getMethod().getSignature() ) ) )
    unimportantmethod = false;
*/
    currmethod = meth;

    listBody = Jimplifier.getJimpleBody( meth );
    
    Iterator stmtIter = listBody.getStmtList().iterator();

    numstmts = 0; 

    System.out.println ( "STMT LIST = "+listBody.getStmtList().size() );

    while ( stmtIter.hasNext() ){

     try {

      Stmt stmt = (Stmt)stmtIter.next();

      numstmts++;

      System.out.println( stmt.toString() );

      assignflag = false;

      invokeflag = false;

      stmt.apply( new AbstractStmtSwitch(){

	public void caseInvokeStmt(InvokeStmt s){

	 invokeflag = true;

	 currInvokeExpr = ( InvokeExpr ) s.getInvokeExpr();

	}

	public void caseAssignStmt(AssignStmt s){

	 if( s.getRightOp() instanceof InvokeExpr )
	 {

	  assignflag = true;

	  returnvariable = s.getLeftOp();

	  currInvokeExpr = ( InvokeExpr ) s.getRightOp();

	 }

	}

      });

      // CallSite cs = mn.getCallSite( currInvokeExpr );

      Iterator CSiter = mn.getCallSites().iterator();                   

      CallSite cs = null;

      boolean search = true;

      while ( ( CSiter.hasNext() )&&(search == true) )
      {
         
       try {
 
        cs = (CallSite) CSiter.next();
     
        InvokeExpr invExpr = cs.getInvokeExpr();  

        if ( invExpr.equals(currInvokeExpr) )
        {
   
         search = false;
   
        }

       } catch ( java.lang.RuntimeException e ) {} 
 
      }


     if ( search == false )
     {


     interfaceInvoked = false;

     specialInvoked = false;

     staticInvoked = false;

     virtualInvoked = false;
     

     currInvokeExpr.apply( new AbstractJimpleValueSwitch() {

      public void caseInterfaceInvokeExpr(InterfaceInvokeExpr v) {

       currInterfaceInvokeExpr = v;

       interfaceInvoked = true;

      }

      public void caseSpecialInvokeExpr(SpecialInvokeExpr v) {

       currSpecialInvokeExpr = v;

       specialInvoked = true;

      }

      public void caseStaticInvokeExpr(StaticInvokeExpr v) {

       currStaticInvokeExpr = v;

       staticInvoked = true;

      }
      
      
      public void caseVirtualInvokeExpr(VirtualInvokeExpr v) {

       currVirtualInvokeExpr = v;

       virtualInvoked = true;

      }
    
     });

//     System.out.println ( " CONSIDERING "+currInvokeExpr+" "+cs.getMethods().size() );
//     System.out.println ( " INVOKE FLAG = "+invokeflag );
//     System.out.println ( " ASSIGN FLAG = "+assignflag );
   

     if ( ( cs.getMethods().size() == 1 ) && ( invokeflag || assignflag ) ) 
     {

      MethodNode men = (MethodNode) cs.getMethods().iterator().next();
      
      me = men.getMethod();

      if ( ! ( /* incorrectlyjimplified.contains ( me.getDeclaringClass().getName() ) || */ me.getName().equals("<init>") ) ) 
      {


//      if ( me.getName().equals( new String ("addArray") ) )
//      {

 //     System.out.println("POSSIBLY INLINE "+me.getSignature() );


      if ( ! ( staticInvoked || specialInvoked ) )
      {

       if ( satisfiesInvokeSpecialSafety ( currInvokeExpr ) )
       {

        changedclasses.add ( scm.getClass ( meth.getDeclaringClass().getName() ) );

        int argcount = currInvokeExpr.getArgCount();

        List arguments = new ArrayList(); 

        int argindex = 0;

        while ( argindex < argcount )
        {

         arguments.add ( currInvokeExpr.getArg ( argindex ) );         

         argindex++;

        }

        SpecialInvokeExpr specialinvokeexpr = jimple.newSpecialInvokeExpr ( ( Local ) ( ( NonStaticInvokeExpr ) currInvokeExpr).getBase(), me , arguments );           

        if ( invokeflag )
        {

         ( ( InvokeStmt ) stmt ).setInvokeExpr ( specialinvokeexpr );       

        }
        else if ( assignflag )
        {

         ( ( AssignStmt ) stmt ).setRightOp ( specialinvokeexpr );

        }

       }

      }


      }


  //    }

      } // ADDED NOW


    } //IF SEARCH

    } catch ( java.lang.RuntimeException e ){}

    }

   } catch ( java.lang.RuntimeException e ){}  


   List locals = listBody.getLocals();

   Iterator localsit = locals.iterator();
/*
   while ( localsit.hasNext() )
   {
    
    Local local = ( Local ) localsit.next();

    System.out.println(local.getName()+" FINALTYPE : "+local.getType());

   }

   Iterator stmtit1 = listBody.getStmtList().iterator();

   while ( stmtit1.hasNext() )
   {

    try {
 
     Stmt s = ( Stmt ) stmtit1.next();

     System.out.println( s.toString() );

     if ( s instanceof GotoStmt )
     {

      Stmt targetstmt = ( Stmt ) ( ( GotoStmt ) s ).getTarget();      

      System.out.println( "TARGET STMT : "+targetstmt.toString() );

     }
     else if ( s instanceof IfStmt )
     {

      Stmt targetstmt = ( Stmt ) ( ( IfStmt ) s ).getTarget();
     
      System.out.println( "TARGET STMT : "+targetstmt.toString() );

     }

    } catch ( java.lang.RuntimeException e ) {

     System.out.println("STMT IGNORED");

     }

   }
*/



   System.out.println ( " NEW JIMPLE CODE FOR "+meth.getSignature() );

   PrintWriter out = new PrintWriter(System.out, true);

   listBody.printTo ( out );
/*
   Iterator changedit = changedclasses.iterator();

 //  System.out.println ( "+++++++++NO. OF CHANGED CLASSES "+changedclasses.size() );

   while ( changedit.hasNext () )
   {      

     // try {

    SootClass changedclass = ( SootClass ) changedit.next();



    // ( scm.getClass ( meth.getDeclaringClass().getName() ) ).write(new StoredBody(Jimple.v()));

    changedclass.printTo( new StoredBody( Jimple.v() ), out );
   
    changedclass.write(new StoredBody(Jimple.v()));

    delay();

    System.out.println ( "DONE WITH "+ changedclass.getName() );

    //    } catch ( java.lang.RuntimeException e ) { System.out.println ( 
    // e.toString() ); e.printStackTrace ( System.out ); }

   }

*/


  } // IF

  } // IF
   
 }  
   



 private List emptylist = new ArrayList();








  public void examineMethods ( Collection callgraph, Resolver res ) {

  resolver = res;

  scm = resolver.getManager();

  clgb = resolver.getClassGraphBuilder();

  incorrectlyjimplified = clgb.getIncorrectlyJimplifiedClasses();

  System.out.println ( "INCORRECTLY JIMPLIFIED" );

  Iterator incorrectlyjimplifiedit = incorrectlyjimplified.iterator();

  while ( incorrectlyjimplifiedit.hasNext() )
  {

    System.out.println ( ( String ) incorrectlyjimplifiedit.next() );

  }


  resolverclassesHT = resolver.getClassesHT();

  resolvermethodsHT = resolver.getMethodsHT();

  resolverfieldsHT = resolver.getFieldsHT();

  List sortedcallgraph = sortMethods ( callgraph );

  List reachedcallgraph = setMethodDepths ( sortedcallgraph );

  List sortedbydepths = sortByMethodDepths ( reachedcallgraph );

  // Iterator iter = callgraph.iterator();


  // Iterator iter = sortedcallgraph.iterator();



  LoopDetector loopd = new LoopDetector();

  Iterator numloopsit = sortedbydepths.iterator();

  while ( numloopsit.hasNext() )
  {

   MethodNode loopsMN = ( MethodNode ) numloopsit.next();

   loopd.setLoopCountFor ( loopsMN );

  }

  
  // List importantmethods = returnImportantMethods ( sortedbydepths );




 Iterator iter = sortedbydepths.iterator();

 // Iterator iter = importantmethods.iterator();

  while ( iter.hasNext() )
  {

   MethodNode tempMN = (MethodNode) iter.next();

   examineMethod( tempMN );
  
  }

 
  // removeMethods();

  PrintWriter out = new PrintWriter(System.out, true);

  Iterator changedit = changedclasses.iterator();

   System.out.println ( "+++++++++ NO. OF CHANGED CLASSES "+changedclasses.size() );

   while ( changedit.hasNext () )
   {      

     // try {


    ArrayList usefulmethods = new ArrayList();

    SootClass changedclass = ( SootClass ) changedit.next();

    System.out.println ( "CHANGGED CLASS "+changedclass );

    Iterator methit = changedclass.getMethods().iterator();

    System.out.println ( "NO OF MTHDS = "+changedclass.getMethods().size() );

/*
    Jimple jimple = Jimple.v();
   
    BodyExpr storedclass = new StoredBody ( ClassFile.v() );
  
*/
 
    while ( methit.hasNext() ) 
    {

     SootMethod changedmethod = ( SootMethod ) methit.next();

     System.out.println ( " REACHED EX 0 FOR "+changedmethod.getSignature() );

     try {

     System.out.println ( " REACHED EX .50 " );

     MethodNode changednode  = ( MethodNode ) cagb.getNode ( changedmethod );

     System.out.println ( " REACHED EX .75 " );

     JimpleBody changedjb = null;
    
     if ( changedmethod.hasActiveBody() )
     changedjb = ( JimpleBody ) changedmethod.getActiveBody();
     else
     throw new java.lang.RuntimeException();      

      // ( new StoredBody( Jimple.v() ) ).resolveFor ( changedmethod );

    System.out.println ("PACKING LOCALS" );

    // Transformations.packLocals ( changedjb );

    System.out.println ("CLEANING UP CODE" );

//    gotoEliminate ( changedjb );
//    Transformations.cleanupCode ( changedjb );
//    Transformations.removeUnusedLocals ( changedjb ); 

//    Transformations.packLocals ( changedjb ); 
//    Transformations.removeUnusedLocals ( changedjb ); 

    System.out.println ( " REACHED EX .90 " );


    changedmethod.setActiveBody ( new GrimpBody ( changedjb ) );

    usefulmethods.add ( changedmethod );
     
     } catch ( java.lang.RuntimeException e ) {

//      System.out.println ( " REACHED EX 1" );

      System.out.println ("BUILDING NEW BODY NOW FOR "+changedmethod.getSignature() );

      //     BuildAndStoreBody changedbasb = new BuildAndStoreBody ( Jimple.v(), new StoredBody ( ClassFile.v() ) );

     JimpleBody changedjb = new JimpleBody( new ClassFileBody( changedmethod ), BuildJimpleBodyOption.USE_PACKING );

     changedmethod.setActiveBody ( new GrimpBody ( changedjb ) );


//      System.out.println ( " REACHED EX 2" );

     // JimpleBody changedjb = ( JimpleBody ) changedbasb.resolveFor ( changedmethod );

//      System.out.println ( " REACHED EX 3" );

     } 

    }


  //  changedclass.setMethods ( usefulmethods );


    System.out.println ( " TRYING TO GENERATE "+ changedclass.getName() +" SIZE = "+changedclass.getMethods().size() );

    changedclass.printTo( out );
   
    changedclass.write();

    // changedclass = null;

/*

      Jimple jimple = Jimple.v();
            
      BodyExpr storedclass = new StoredBody ( ClassFile.v() );
            
      ClassFileBody clbd = ( ClassFileBody ) storedclass.resolveFor ( method );

*/


    // delay();

    System.out.println ( " GENERATED "+ changedclass.getName() );

    //    } catch ( java.lang.RuntimeException e ) { System.out.println ( 
    // e.toString() ); e.printStackTrace ( System.out ); }

   }





 }







 

  public boolean satisfiesInvokeSpecialSafety ( InvokeExpr speinvexpr ) { 

//   SootClass inlinableclass = method.getDeclaringClass();

   SootClass targetclass = currmethod.getDeclaringClass();

   try {

       SootMethod meth = speinvexpr.getMethod();

       SootClass dec = scm.getClass ( meth.getDeclaringClass().getName() ); 

       if ( ! ( meth.getName().equals("<init>") ) )
       {

        if ( ( isStrictSuperClass ( targetclass, dec ) ) )
        return false;

       }

   } catch ( java.lang.RuntimeException e ) { return false; }

   return true;

  }













 public boolean isStrictSuperClass ( SootClass sc1, SootClass sc2 ) {

  boolean result = false;

  SootClass parent = sc1;
  
  while ( ( parent.hasSuperClass() ) && ( result == false ) )
  {

   parent = parent.getSuperClass();

   if ( parent.getName().equals( sc2.getName() ) )
   result = true;

  }

  return result;

 }




 public boolean allowedToChange ( String cname ) {

  boolean result = true;

  try {

/*
  Iterator incorrectit = incorrectlyjimplified.iterator();

  while ( incorrectit.hasNext() )
  {

    String nextname = ( String ) incorrectit.next();

    if ( nextname.equals ( cname ) )
    result = false;

  }

*/
  result = result && ( ! incorrectlyjimplified.contains ( cname ) );

  // System.out.println ( "ALLOWED 1 "+ cname+ " "+result );

  SootClass cn = scm.getClass ( cname );

  result = result && ( ! Modifier.isAbstract( cn.getModifiers() ) );

  // System.out.println ( "ALLOWED 2 "+ cname+ " "+result );

  result = result && ( ! isLibraryNode ( cname ) );

  // System.out.println ( "ALLOWED 3 "+ cname+ " "+result  );


  // result = result && ( cname.equals ( "GreyImage" ) );

  // result = result && ( isSamePackage ( getPackageName ( cname ), "ca.mcgill.sable.soot.jimple" ) );
  
  } catch ( java.lang.RuntimeException e ) { 

    return false;

  }

  return result;

 }







 public boolean isLibraryNode ( String cname ) {

  boolean isJava = ClassGraphBuilder.isLibraryNode("java.",cname);
       
  boolean isSun = ClassGraphBuilder.isLibraryNode("sun.",cname);

  boolean isSunw = ClassGraphBuilder.isLibraryNode("sunw.",cname);

  return ( isJava || isSun || isSunw );

 }









}
































































































































