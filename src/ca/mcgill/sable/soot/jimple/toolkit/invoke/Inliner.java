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

public class Inliner {


	
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

  private Integer priv = new Integer ( 0 );

  private Integer def = new Integer ( 1 );

  private Integer prot = new Integer ( 2 );

  private Integer pub = new Integer ( 3 );

  private HashMap classesHT = new HashMap();

  private HashMap methodsHT = new HashMap();

  private HashMap fieldsHT = new HashMap();

  private HashMap resolverclassesHT;

  private HashMap resolvermethodsHT;

  private HashMap resolverfieldsHT;

  private SootClassManager scm;

  private ClassGraphBuilder clgb;

  private CallGraphBuilder cagb;

  private Resolver resolver;

  private Set changedclasses = new HashSet();

  private Set incorrectlyjimplified;

  private Type returnvartype;

  private Set removedmethods = new HashSet();








  private int j = 0; 

  private int returnj = 0;

  private int nullcheckj = 0;

  private int addressj = 0;

  private int throwj = 0;




  public Inliner () {}





  public Inliner ( CallGraphBuilder callgb ) {

   cagb = callgb;

  }



  private HashMap methodsToMinDepth = new HashMap();


  private List classesToProcess = null;

  private boolean includeLibraries = false;



  public void setClassesToProcess( List toProcess, boolean includeLibraries )
  {

   this.classesToProcess = toProcess;
   
   includeLibraries = includeLibraries;

  }







  public List setMethodDepths ( Collection methods ) {

   List methodsQ = new ArrayList();

   List reachedmethods = new ArrayList();

   Iterator methodsit = methods.iterator();

   Integer zero = new Integer(0);

   while ( methodsit.hasNext() )
   {

    try {

     MethodNode mn = ( MethodNode ) methodsit.next();
      
     if ( ( ( mn.incomingedges == 0 ) || clgb.sources.contains ( mn.getMethod().getSignature() ) )  && ( ! mn.isRedundant ) ) 
     {

      // reachedmethods.add ( mn );

      methodsToMinDepth.put ( mn.getMethod().getSignature(), zero );

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

          }

         }

        }

       }

      } catch ( java.lang.RuntimeException e ) {}

     }

    } catch ( java.lang.RuntimeException e ) {}
     
   }     

   int nextlevelindex = methodsQ.size();  

   int currentlevel = 1;

   Integer currentLevel = new Integer ( currentlevel );

   while ( ! methodsQ.isEmpty() )
   {

    try {

     MethodNode nextmethod = ( MethodNode ) methodsQ.get( 0 );

     // reachedmethods.add ( nextmethod );

     methodsToMinDepth.put ( nextmethod.getMethod().getSignature(), currentLevel );

     try {

      Set adjacentnodes = nextmethod.getAllPossibleMethods();

      if ( adjacentnodes.size() > 0 )
      {

       Iterator adjacentit = adjacentnodes.iterator();

       while ( adjacentit.hasNext() )
       {

        MethodNode adjnode = ( MethodNode ) adjacentit.next();

        if ( ( ( ( Integer ) methodsToMinDepth.get ( adjnode.getMethod().getSignature() ) ) == null ) )
        {
       
         if ( ! methodsQ.contains ( adjnode ) )
         {

          methodsQ.add ( adjnode );

         }

        }

       }
    
      }

     } catch ( java.lang.RuntimeException e ) {}

    } catch ( java.lang.RuntimeException e ) {}

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

  // List sortedmethods = new ArrayList();

   List sortedmethods = new LinkedList();

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

    }

    while ( it.hasNext() )
    {
    
     MethodNode mn = ( MethodNode ) it.next();

     if ( ! mn.isRedundant )
     {

      int depth = ( ( Integer ) methodsToMinDepth.get ( mn.getMethod().getSignature() ) ).intValue();

      ListIterator sortedmethodsit = sortedmethods.listIterator(0);

      int sortedmethnum = 0;

      boolean inserted = false;

      while ( ( sortedmethodsit.hasNext() ) && ( inserted == false ) ) 
      {

       MethodNode nextmethod = ( MethodNode ) sortedmethodsit.next();

       int nextdepth = ( ( Integer ) methodsToMinDepth.get ( nextmethod.getMethod().getSignature() ) ).intValue();

       if ( depth > nextdepth )       
       // if ( depth < nextdepth )
       {

        inserted = true;

        sortedmethodsit.add ( /* sortedmethods.indexOf( nextmethod) sortedmethnum, */ mn );

       }         

       sortedmethnum++;

      }

      if ( inserted == false )
      {

       sortedmethodsit.add ( /* sortedmethnum, */ mn );

      }

     }

    }
    
   }

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







  private int numBenchmarkNodes = 0;






  public List sortMethods ( Collection methods ) {

   List sortedmethods = new /* Array */ LinkedList();

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

     ListIterator sortedmethodsit = sortedmethods.listIterator(0);

     int sortedmethnum = 0;

     boolean inserted = false;

     while ( ( sortedmethodsit.hasNext() ) && ( inserted == false ) ) 
     {

      MethodNode nextmethod = ( MethodNode ) sortedmethodsit.next();

      if ( nextmethod.getMethod().getSignature().compareTo ( mn.getMethod().getSignature() )  < 0 )       
      {

        inserted = true;

        sortedmethodsit.add ( /* sortedmethnum , */ mn );

      }         

      sortedmethnum++;

     }

     if ( inserted == false )
     {

       sortedmethodsit.add ( /* sortedmethnum , */ mn );

     }

    }
    
   }

   return sortedmethods;

  }








  private boolean unimportantmethod = false;

  private boolean syncflag = false;

  private HashMap invokeExprsToMethods = new HashMap();

  private boolean looking = true;
 
  private MethodNode inliningInsideMethod;

  private ArrayList workQ;






 private int numpotentiallyinlined = 0, numactuallyseen = 0, allowedtochange = 0, 
     initnum = 0, criteria0 = 0, criteria1 = 0, criteria2 = 0, criteria3 = 0, 
     criteria4 = 0, criteria5 = 0, criteria6 = 0, criteria7 = 0, criteria8 = 
     0, inlinemono = 0, actuallyinlined = 0;

 



 public void examineCallSite ( InvokeExpr inlinableinvoke ) {

  numpotentiallyinlined++;

  localsHT.clear(); 

  int stmtsAtSite = 0; 

  workQ = new ArrayList();

  workQ.add ( inlinableinvoke );

  while ( ! workQ.isEmpty() )
  {

   numactuallyseen++;

   localsHT.clear();

   InvokeExpr ie = ( InvokeExpr ) workQ.remove(0);

   currInvokeExpr = ie;

   MethodNode mn = ( MethodNode ) invokeExprsToMethods.get ( ie );

   inliningInsideMethod = mn;

   if ( ! ( mn == null ) )
   {

    SootMethod meth = mn.getMethod();

    // System.out.println ( "TRYING TO INLINE INSIDE TARGET "+meth.getSignature());
    if ( ( ! meth.getName().equals ( "<clinit>" ) ) && ( ! Modifier.isNative( meth.getModifiers() ) ) )
    {

     if ( allowedToChange ( meth.getDeclaringClass().getName() ) )
     {

      allowedtochange++;

      // System.out.println ( "TRYING TO INLINE INSIDE TARGET "+meth.getSignature());

      try {

       currmethod = meth;

       listBody = Jimplifier.getJimpleBody( meth );

       int origSize = ( ( Integer ) origSizeHT.get ( meth.getSignature() ) ).intValue(); 

       List locals = listBody.getLocals();
 
       Iterator localsit = locals.iterator();

       while ( localsit.hasNext() )
       {

        Local newlocal = ( Local ) localsit.next();

        localsHT.put ( newlocal.getName(), newlocal );

       }

       Iterator stmtIter = listBody.getStmtList().iterator();

      // if ( listBody.getStmtList().size() < 200 )

       // if ( stmtsAtSite < 60  )

       if ( ( stmtsAtSite < 20  ) && ( listBody.getStmtList().size() < ( 4*origSize ) ) )

       // if ( stmtsAtSite < 10  )
       {

        numstmts = 0; 

        looking = true;

        while ( ( stmtIter.hasNext() ) && looking ) 
        {

         try {

          Stmt stmt = (Stmt) stmtIter.next();

          numstmts++;

          assignflag = false;

          invokeflag = false;

          stmt.apply( new AbstractStmtSwitch(){

           public void caseInvokeStmt(InvokeStmt s){

            invokeflag = true;

            if ( currInvokeExpr.equals ( ( InvokeExpr ) s.getInvokeExpr() ) )
            looking = false;

           }

           public void caseAssignStmt(AssignStmt s){

            if( s.getRightOp() instanceof InvokeExpr )
            {

             assignflag = true;

             returnvariable = s.getLeftOp();

             if ( currInvokeExpr.equals ( ( InvokeExpr ) s.getRightOp() ) )
             looking = false;
 
            }

           }

          });

         } catch ( java.lang.RuntimeException e ) {}

        } // WHILE LOOKING

        if ( looking == false )
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

        CallSite cs = getCorrectCallSite ( currInvokeExpr , mn );    

        if ( ( cs.getMethods().size() == 1 ) && ( invokeflag || assignflag ) ) 
        {

         inlinemono++;

         syncflag = false;

         MethodNode men = (MethodNode) cs.getMethods().iterator().next();
      
         me = men.getMethod();

         if ( ( Modifier.isSynchronized ( me.getModifiers() ) ) && ( ! staticInvoked ) )
         syncflag = true;
     
         if ( ! ( /* incorrectlyjimplified.contains ( me.getDeclaringClass().getName() ) || */ me.getName().equals("<init>") ) ) 
         {

          if ( satisfiesCriteria ( me, currInvokeExpr ) )
          {

           changedclasses.add ( scm.getClass ( meth.getDeclaringClass().getName() ) );
         
           melistBody = Jimplifier.getJimpleBody( me );

           stmtsAtSite = stmtsAtSite + melistBody.getStmtList().size();

           List melocals = melistBody.getLocals();

           Iterator melocalsit = melocals.iterator();

           while ( melocalsit.hasNext() )
           {

            Local newlocal = ( Local ) melocalsit.next();

            Local clonedlocal = ( Local ) newlocal.clone();

            clonedlocal.setName( new String( "dummy"+j ) );

            localsHT.put ( newlocal.getName(), clonedlocal );

            listBody.addLocal( clonedlocal );

            j++;

           }


           //     System.out.println ( "REACHED 1");

           if ( assignflag )
           {

            returnvartype = me.getReturnType();

            me.getReturnType().apply ( new TypeSwitch() {

             public void caseBooleanType ( BooleanType t ) {
 
              returnvartype = IntType.v ();
 
             }
    
             public void caseByteType ( ByteType t ) {

              returnvartype = IntType.v ();

             }

             public void caseCharType ( CharType t ) {

              returnvartype = IntType.v ();

             } 
  
             public void caseShortType ( ShortType t ) {

              returnvartype = IntType.v ();

             }

            });

           dummyreturn = jimple.newLocal ( new String ( "dummyreturn"+returnj ), returnvartype );

           localsHT.put ( dummyreturn.getName(), dummyreturn );

           listBody.addLocal ( dummyreturn );

           returnj++;

          } // IF ASSIGNFLAG
         
           // System.out.println ( "REACHED 2");


          if ( ! staticInvoked )
          {

           Local dummynullcheck = jimple.newLocal ( new String ( "dummynull"+nullcheckj ), RefType.v ( "java.lang.NullPointerException" ) );

           localsHT.put ( dummynullcheck.getName(), dummynullcheck );

           listBody.addLocal ( dummynullcheck );

           nullcheckj++;

           Value baseval = ( ( NonStaticInvokeExpr ) currInvokeExpr ).getBase();
    
           insertNullCheck ( baseval, dummynullcheck, listBody.getStmtList(), stmtIter );
       
          }


          // System.out.println ( "REACHED 3");

           if ( syncflag )
           {

/*
           syncaddress = jimple.newLocal ( new String ( "dummyaddress"+addressj ), StmtAddressType.v() );

           localsHT.put ( syncaddress.getName(), syncaddress );

           listBody.addLocal ( syncaddress );

           addressj++;
*/

            syncthrow = jimple.newLocal ( new String ( "dummythrow"+throwj ), RefType.v ( "java.lang.Throwable" ) );

            localsHT.put ( syncthrow.getName(), syncthrow );

            listBody.addLocal ( syncthrow );

            throwj++;

           }

           // System.out.println( "TRYING TO INLINE "+me.getSignature()+" INTO "+meth.getSignature() +" FOR "+currInvokeExpr ); 

           men.alreadyInlined = true;

           actuallyinlined++;

           InlineMethod ( melistBody.getStmtList() , listBody.getStmtList(), stmtIter );

           // System.out.println ("INLINED SUCCESS");
             
           // PrintWriter out = new PrintWriter(System.out, true);

           // listBody.printTo ( out );

          }

         } // ADDED NOW
         else initnum++;

        }    

       } 

      }
      else criteria0++;

     } catch ( java.lang.RuntimeException e ){}

    }

   }

  }

 }

}

 








 private HashMap invokeExprsHT = new HashMap();





 public CallSite getCorrectCallSite ( InvokeExpr inve, MethodNode methodnode ) {

  return ( CallSite ) invokeExprsHT.get ( inve ); 
 
 }






 private ArrayList ImportantQ = new ArrayList();

 private ArrayList ImportantCS = new ArrayList();

 private ArrayList UnimportantQ = new ArrayList();

 private ArrayList ImprovedCallSites = new ArrayList(); 

 private HashMap origSizeHT = new HashMap();





 public void setImprovedCallSites ( List improved ) {

  ImprovedCallSites = ( ArrayList ) improved;

  //  System.out.println ( "IMPROVEDD SIZE = "+improved.size() );

 }







 public Set examineMethodsToFixCallSites ( Collection callgraph, Resolver res ) {

  resolver = res;

  scm = resolver.getManager();

  clgb = resolver.getClassGraphBuilder();

  incorrectlyjimplified = clgb.getIncorrectlyJimplifiedClasses();

  //   System.out.println ( "INCORRECTLY JIMPLIFIED" );

  Iterator incorrectlyjimplifiedit = incorrectlyjimplified.iterator();

  while ( incorrectlyjimplifiedit.hasNext() )
  {

    System.out.println ( ( String ) incorrectlyjimplifiedit.next() );

  }

  System.out.println();
  System.out.print("Identifying important call sites for inlining.....");

  resolverclassesHT = resolver.getClassesHT();

  resolvermethodsHT = resolver.getMethodsHT();

  resolverfieldsHT = resolver.getFieldsHT();

  List sortedbydepths = sortMethods ( callgraph );

  // List reachedcallgraph = setMethodDepths ( sortedcallgraph );

  // List sortedbydepths = sortByMethodDepths ( reachedcallgraph );

  // Iterator iter = callgraph.iterator();

  // Iterator iter = sortedcallgraph.iterator();

  //  System.out.println ( "LOOPS SIZE = "+sortedbydepths.size() );

  LoopDetector loopd = new LoopDetector();

  Iterator numloopsit = sortedbydepths.iterator();

  while ( numloopsit.hasNext() )
  {

   MethodNode loopsMN = ( MethodNode ) numloopsit.next();

   //   System.out.println ( "LOOPING "+loopsMN.getMethod().getSignature() );

   loopd.setLoopCountFor ( loopsMN );

  }


  // System.out.println ( "LOOPS SIZE = "+sortedbydepths.size() );

 Iterator finalit = sortedbydepths.iterator();

 while ( finalit.hasNext() )
 {

  MethodNode nextmn = ( MethodNode ) finalit.next();

  //  System.out.println ( "FINAL "+nextmn.getMethod().getSignature()+" NUM "+nextmn.ImportantInvokeExprs.size() );

  int origSize = Jimplifier.getJimpleBody( nextmn.getMethod() ).getStmtList().size();

  origSizeHT.put ( nextmn.getMethod().getSignature(), new Integer ( origSize ) );

  boolean recursiveflag = false;

  if ( cagb.recursiveMethods.contains ( nextmn.getMethod().getSignature() ) )
  recursiveflag = true;

  Iterator callsitesiter = nextmn.getCallSites().iterator();
 
  while ( callsitesiter.hasNext() )
  {

   CallSite nextcs = ( CallSite ) callsitesiter.next();

   invokeExprsToMethods.put ( nextcs.getInvokeExpr(), nextmn );

   invokeExprsHT.put ( nextcs.getInvokeExpr(), nextcs );

   if ( recursiveflag )
   {

     //    System.out.println ( "IMPORTANT CALLSITE "+nextcs.getCallerID() );

    ImportantQ.add ( nextcs.getInvokeExpr() );
    ImportantCS.add ( nextcs );
   }
   else 
   {
    if ( nextmn.ImportantInvokeExprs.contains ( nextcs.getInvokeExpr() ) )
    {

      // System.out.println ( "IMPORTANT CALLSITE "+nextcs.getCallerID() );

     ImportantQ.add ( nextcs.getInvokeExpr() );
     ImportantCS.add ( nextcs );
    }
    else
    UnimportantQ.add ( nextcs.getInvokeExpr() );
   }

  }

 }




 ArrayList ImportantMethods = new ArrayList();

 Iterator importit = ImportantCS.iterator();

 while ( importit.hasNext() )
 {

  CallSite nextimportant = ( CallSite ) importit.next();

  Set attachedmethods = nextimportant.getMethods();

  ArrayList attachedQ = new ArrayList();

  attachedQ.addAll ( attachedmethods );

  while ( ! attachedQ.isEmpty() )
  {

   MethodNode nextimpmethod = ( MethodNode ) attachedQ.remove(0); 

   if ( ! ImportantMethods.contains ( nextimpmethod ) )
   {

    ImportantMethods.add ( 0, nextimpmethod );

    Iterator allpossmethodsit = nextimpmethod.getAllPossibleMethods().iterator();

    while ( allpossmethodsit.hasNext() )
    attachedQ.add ( ( MethodNode ) allpossmethodsit.next() );
    
   }

  }

 }




 Iterator importantmthdsit = ImportantMethods.iterator();

 while ( importantmthdsit.hasNext() )
 {

  MethodNode nextimpmn = ( MethodNode ) importantmthdsit.next();

  //  System.out.println ( "IMPORTANT METHOD "+nextimpmn.getMethod().getSignature() );

  Iterator callsitesit = nextimpmn.getCallSites().iterator();

  while ( callsitesit.hasNext() )
  {

   CallSite nextcs = ( CallSite ) callsitesit.next();

   if ( ! ImportantCS.contains ( nextcs ) )
   {

    ImportantCS.add ( nextcs );
    ImportantQ.add ( 0, nextcs.getInvokeExpr() );

   }

  }  

 }


 System.out.println("Done");

 // ImportantQ = ( ArrayList ) getCallSitesFromProfile();

 System.out.println(); 

 System.out.print("Attempting to inline at important call sites");

 Iterator importantit = ImportantQ.iterator();

 int impcs = 0;

 while ( importantit.hasNext() )
 {
   impcs++;

   if ( ( impcs % 10 ) == 0 )
   System.out.print(".");

  InvokeExpr importantinvoke = ( InvokeExpr ) importantit.next();

  //  System.out.println ( "EXAMINING IMPORTANT INVOKE "+importantinvoke );

  examineCallSite ( importantinvoke );


 }


 System.out.println("Done");




 /*

 System.out.println ( "NUMBER OF SITES IN INITIAL LIST = "+numpotentiallyinlined);
 System.out.println ( "NUMBER OF SITES ACTUALLY CONSIDERED = "+numactuallyseen);
 System.out.println ( "NUMBER OF SITES WERE ALLOWED TO BE CHANGED = "+allowedtochange);
 System.out.println ( "NUMBER OF SITES CALLING <init>  = "+initnum);
 System.out.println ( "NUMBER OF SITES REJECTED BY CRITERIA 0 = "+criteria0); 
 System.out.println ( "NUMBER OF MONOMORPHIC SITES = "+inlinemono);
 System.out.println ( "NUMBER OF SITES REJECTED BY CRITERIA 1 = "+criteria1);
 System.out.println ( "NUMBER OF SITES REJECTED BY CRITERIA 2 = "+criteria2);
 System.out.println ( "NUMBER OF SITES REJECTED BY CRITERIA 3 = "+criteria3); 
 System.out.println ( "NUMBER OF SITES REJECTED BY CRITERIA 4 = "+criteria4); 
 System.out.println ( "NUMBER OF SITES REJECTED BY CRITERIA 5 = "+criteria5); 
 System.out.println ( "NUMBER OF SITES REJECTED BY CRITERIA 6 = "+criteria6); 
 System.out.println ( "NUMBER OF SITES REJECTED BY CRITERIA 7 = "+criteria7);
 System.out.println ( "NUMBER OF SITES REJECTED BY CRITERIA 8 = "+criteria8);
 System.out.println ( "NUMBER OF SITES ACTUALLY INLINED = "+actuallyinlined);

 */

 
/*

 Iterator improvedit = ImprovedCallSites.iterator();

 while ( improvedit.hasNext() )
 {

  InvokeExpr improvedinvoke = ( InvokeExpr ) ( ( CallSite ) improvedit.next()).getInvokeExpr();

  examineCallSite ( improvedinvoke );

 }

 inliningImportant = false; 

*/



/*

 Iterator unimportantit = UnimportantQ.iterator();

 while ( unimportantit.hasNext() )
 {

  InvokeExpr unimportantinvoke = ( InvokeExpr ) unimportantit.next();

  examineCallSite ( unimportantinvoke );

 }


*/

/*
 inliningImportant = false;

 Iterator finit = sortedbydepths.iterator();

 while ( finit.hasNext() )
 {

  MethodNode nextmn = ( MethodNode ) finit.next();

  examineMethod ( nextmn );

 }
*/

 /*

  PrintWriter out = new PrintWriter(System.out, true);

  Iterator changedit = changedclasses.iterator();

  //  System.out.println ( "+++++++++ NO. OF CHANGED CLASSES "+changedclasses.size() );

  while ( changedit.hasNext () )
  {      
   
   System.out.println();
   //  System.out.println ( "Generating optimized class : "+ changedclass.getName() );

   try {

    ArrayList usefulmethods = new ArrayList();

    SootClass changedclass = ( SootClass ) changedit.next();

    // System.out.println ( "Generating optimized class : "+ changedclass.getName() );
 
    // System.out.println ( "CHANGGED CLASS "+changedclass );

    Iterator methit = changedclass.getMethods().iterator();

    // System.out.println ( "NO OF MTHDS = "+changedclass.getMethods().size() );
 
    while ( methit.hasNext() ) 
    {

     SootMethod changedmethod = ( SootMethod ) methit.next();

     try {

      MethodNode changednode  = ( MethodNode ) cagb.getNode ( changedmethod );

      //      JimpleBody changedjb = ( JimpleBody ) ( new StoredBody( Jimple.v() ) ).resolveFor ( changedmethod );

      JimpleBody changedjb = null;

      if ( changedmethod.hasActiveBody() )
      {

        // System.out.println ( changedmethod.getSignature()+"ACTIVE ? "+changedmethod.hasActiveBody() ); 

       changedjb = ( JimpleBody ) changedmethod.getActiveBody();  

      }
      else
      throw new java.lang.RuntimeException();

      // Transformations.packLocals ( changedjb );

      // System.out.println ("CLEANING UP CODE" );

      gotoEliminate ( changedjb );


      Transformations.cleanupCode ( changedjb ); 



//    Transformations.removeUnusedLocals ( changedjb ); 

//    ChaitinAllocator.packLocals ( changedjb );  
//    Transformations.removeUnusedLocals ( changedjb ); 

      changedmethod.setActiveBody ( new GrimpBody ( (JimpleBody) changedmethod.getActiveBody() ) );



      usefulmethods.add ( changedmethod );
     
     } catch ( java.lang.RuntimeException e ) {

       // System.out.println ("BUILDING NEW BODY NOW FOR "+changedmethod.getSignature() );

     // BuildAndStoreBody changedbasb = new BuildAndStoreBody ( Jimple.v(), new StoredBody ( ClassFile.v() ) );

     JimpleBody changedjb = new JimpleBody( new ClassFileBody( changedmethod ), BuildJimpleBodyOption.USE_PACKING );

     changedmethod.setActiveBody ( new GrimpBody ( changedjb ) );


     //     JimpleBody changedjb = ( JimpleBody ) changedbasb.resolveFor ( changedmethod );

    } 

   }

    // System.out.println ( " TRYING TO GENERATE "+ changedclass.getName() +" SIZE = "+changedclass.getMethods().size() );

   // changedclass.printTo( new StoredBody( Jimple.v() ), out );
   
   changedclass.printTo(out); 

   //  changedclass.write( new BuildAndStoreBody ( Grimp.v(), new StoredBody ( Jimple.v() ) ) ); // new StoredBody(Jimple.v())));

   changedclass.write();

     System.out.println ( "Generating optimized class : "+ changedclass.getName() );

  } catch ( java.lang.RuntimeException e ) { System.out.println ("FAILURE"); e.printStackTrace ( System.out ); }

 }

 */

 return changedclasses;


}








 public static BufferedReader getBufReader(InputStream i)
 throws IOException {
   
      return( new BufferedReader
            ( new InputStreamReader(i)));
   
 }

 public static BufferedReader getBufReader(FileInputStream f)
 throws IOException {
               
      return getBufReader( (InputStream) f );
 }


 public static BufferedReader getBufReader(File file)
 throws IOException {
        
      FileInputStream in = new FileInputStream( file);
      return getBufReader( (InputStream) in );
 }


 public static BufferedReader getBufReader(String file)
 throws IOException {
        
      FileInputStream in = new FileInputStream(file);
      return getBufReader( (InputStream) in );
 }






 public List getCallSitesFromProfile() {

  ArrayList allCallSites = new ArrayList(); 

  ArrayList halfCallSites = new ArrayList();
   
  try { 

     BufferedReader b = getBufReader ( "frequency.out" );

     for ( ;; )
     {

       String currentline = b.readLine();
     
       if ( currentline == null )
       break;

       allCallSites.add ( 0, currentline );

     }

  } catch ( java.io.IOException e ) {}

  int allCallSitesSize = allCallSites.size();

  int halfCallSitesSize = allCallSitesSize / 2;

  for ( int i = 0; i < halfCallSitesSize; i++ )
  {

   String currentline = ( String ) allCallSites.get ( i );

   int separatorindex = currentline.indexOf ( ' ' );
      
   int callsitenumindex = currentline.lastIndexOf ( '$' );
 
   String callsitenumAsString = currentline.substring ( callsitenumindex + 1, separatorindex );
      
   Integer callsitenum = Integer.valueOf ( callsitenumAsString );
  
   String methodsig = currentline.substring ( 0, callsitenumindex );

   MethodNode mnode = cagb.getNode ( methodsig );

   CallSite cs = mnode.getCallSite ( callsitenum );

   halfCallSites.add ( cs.getInvokeExpr() );

  }

  return halfCallSites;

 }















  private int examined = 0;



  


 public void examineMethod ( MethodNode mn ) {
    
   localsHT.clear(); 

   SootMethod meth = mn.getMethod();

   inliningInsideMethod = mn;

   if ( ( ! meth.getName().equals ( "<clinit>" ) ) && ( ! Modifier.isNative( meth.getModifiers() ) ) )
   {

    if ( examined < ( numBenchmarkNodes )/2 )
    {

     if ( allowedToChange ( meth.getDeclaringClass().getName() ) )
     {

      int origSize = Jimplifier.getJimpleBody( meth ).getStmtList().size();

      //      System.out.println ( "TRYING TO INLINE INSIDE TARGET "+meth.getSignature()+" "+( ( Integer ) methodsToMinDepth.get ( meth.getSignature() ) ) );

      examined++;

      try {

       currmethod = meth;

       listBody = Jimplifier.getJimpleBody( meth );
    
       List locals = listBody.getLocals();

       Iterator localsit = locals.iterator();

       while ( localsit.hasNext() )
       {

        Local newlocal = ( Local ) localsit.next();

        localsHT.put ( newlocal.getName(), newlocal );

       }
    
       Iterator stmtIter = listBody.getStmtList().iterator();

       numstmts = 0; 

       while ( stmtIter.hasNext() )
       {

        try {

         Stmt stmt = (Stmt)stmtIter.next();

         if ( listBody.getStmtList().size() < ( 2*( origSize ) ) ) 
         {

          numstmts++;

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

          if ( ( cs.getMethods().size() == 1 ) && ( invokeflag || assignflag ) ) 
          {

           syncflag = false;

           MethodNode men = (MethodNode) cs.getMethods().iterator().next();
      
           me = men.getMethod();

           if ( ( Modifier.isSynchronized ( me.getModifiers() ) ) && ( ! staticInvoked ) )
           syncflag = true;

           if ( ! ( /* incorrectlyjimplified.contains ( me.getDeclaringClass().getName() ) || */ me.getName().equals("<init>") ) ) 
           {

            if ( satisfiesCriteria ( me, currInvokeExpr ) )
            {

             changedclasses.add ( scm.getClass ( meth.getDeclaringClass().getName() ) );

             melistBody = Jimplifier.getJimpleBody( me );

             List melocals = melistBody.getLocals();

             Iterator melocalsit = melocals.iterator();

             while ( melocalsit.hasNext() )
             {

              Local newlocal = ( Local ) melocalsit.next();

              Local clonedlocal = ( Local ) newlocal.clone();

              clonedlocal.setName( new String( "dummy"+j ) );

              localsHT.put ( newlocal.getName(), clonedlocal );
 
              listBody.addLocal( clonedlocal );

              j++;

             }

             if ( assignflag )
             {

              returnvartype = me.getReturnType();

              me.getReturnType().apply ( new TypeSwitch() {

              public void caseBooleanType ( BooleanType t ) {
 
               returnvartype = IntType.v ();
 
              }
    
              public void caseByteType ( ByteType t ) {

               returnvartype = IntType.v ();

              }

              public void caseCharType ( CharType t ) {

               returnvartype = IntType.v ();

              } 
  
              public void caseShortType ( ShortType t ) {

               returnvartype = IntType.v ();

              }

             });

             dummyreturn = jimple.newLocal ( new String ( "dummyreturn"+returnj ), returnvartype );

             localsHT.put ( dummyreturn.getName(), dummyreturn );

             listBody.addLocal ( dummyreturn );

             returnj++;

            }


            if ( ! staticInvoked )
            {

             Local dummynullcheck = jimple.newLocal ( new String ( "dummynull"+nullcheckj ), RefType.v ( "java.lang.NullPointerException" ) );

             localsHT.put ( dummynullcheck.getName(), dummynullcheck );

             listBody.addLocal ( dummynullcheck );

             nullcheckj++;

             Value baseval = ( ( NonStaticInvokeExpr ) currInvokeExpr ).getBase();
    
             insertNullCheck ( baseval, dummynullcheck, listBody.getStmtList(), stmtIter );
       
            }

            if ( syncflag )
            {

/*
              syncaddress = jimple.newLocal ( new String ( "dummyaddress"+addressj ), StmtAddressType.v() );

              localsHT.put ( syncaddress.getName(), syncaddress );

              listBody.addLocal ( syncaddress );

              addressj++;

*/

              syncthrow = jimple.newLocal ( new String ( "dummythrow"+throwj ), RefType.v ( "java.lang.Throwable" ) );

              localsHT.put ( syncthrow.getName(), syncthrow );

              listBody.addLocal ( syncthrow );

              throwj++;

             }

            //             System.out.println( "TRYING TO INLINE "+me.getSignature()+" INTO "+meth.getSignature() +" FOR "+currInvokeExpr ); 

             men.alreadyInlined = true;

             InlineMethod ( melistBody.getStmtList() , listBody.getStmtList(), stmtIter );

             if ( ( men.incomingedges == 1 ) /* && ( ! mn.alreadyInlined ) */ )
             {

                removedmethods.add ( men.getMethod().getSignature() );

             }

            }

           } // ADDED NOW

          }    

         } //IF SEARCH

        } // < 1.2*   

       } catch ( java.lang.RuntimeException e ){ e.printStackTrace(System.out); }

      }

     } catch ( java.lang.RuntimeException e ){}  

     //     System.out.println ( " NEW JIMPLE CODE FOR "+meth.getSignature() );

     PrintWriter out = new PrintWriter(System.out, true);

     listBody.printTo ( out );

    } // IF

   } // IF

  } // IF
   
 }  
   









 private Local syncaddress;

 private Local syncthrow;








 public void delay () {

   for ( int ii = 0; ii < 100000; ii++ )
   {

           for ( int iii = 0; iii < 10000; iii++ )
           {
             int iiii = ii/100;

            }
   }

 }





 private List emptylist = new ArrayList();






 public void insertNullCheck ( Value baseval, Local nulllocal, StmtList targetbody, Iterator target )
 {

   //  System.out.println ("ENTERED NULL CHECKS");

  AssignStmt nullassignstmt = jimple.newAssignStmt ( nulllocal, jimple.newNewExpr ( ( RefType ) nulllocal.getType() ) );

  SpecialInvokeExpr nullspinvexpr = jimple.newSpecialInvokeExpr ( nulllocal, scm.getClass ( "java.lang.NullPointerException" ).getMethod ( "<init>", emptylist ), emptylist );

  InvokeStmt nullinvstmt = jimple.newInvokeStmt ( nullspinvexpr );

  ThrowStmt nullthrowstmt = jimple.newThrowStmt ( nulllocal );

  NeExpr neexpr = jimple.newNeExpr ( baseval, NullConstant.v() );

  IfStmt nullifstmt = jimple.newIfStmt ( neexpr, ( Stmt ) targetbody.get ( numstmts - 1 ) );

//  targetbody.add ( numstmts - 1, nullifstmt );

  targetbody.add ( numstmts, nullifstmt );

  //  System.out.println ("REACHED NULL 1" ); 

  Stmt removedstmt = ( Stmt ) targetbody.remove ( numstmts - 1 );

  //  System.out.println ("REACHED NULL 2" );

  targetbody.add ( numstmts, removedstmt );

  nullifstmt.setTarget ( removedstmt );

  //  target.next();

  numstmts++;

  targetbody.add ( numstmts - 1, nullassignstmt );

  // target.next();

  numstmts++;

  targetbody.add ( numstmts - 1, nullinvstmt );

  // target.next();

  numstmts++;

  targetbody.add ( numstmts - 1, nullthrowstmt );
  
  // target.next();

  numstmts++;

  //  System.out.println ("EXITED NULL CHECKS" );

 }
















  public boolean satisfiesCriteria ( SootMethod m, InvokeExpr ie ) {

   try {

    if ( syncflag && staticInvoked )
    {

     criteria1++;
     return false;

    }

    if ( ( Jimplifier.getJimpleBody ( m ).getStmtList().size() > 60 )   /* && unimportantmethod */  )  
    {

     criteria2++;
     return false;

    }


   if ( Modifier.isNative ( m.getModifiers() ) )
   {

    criteria3++;
    return false;

   }

   // System.out.println ( " CLEARED 1 " );

   if ( ( resolver.getErrorInvokeExprs().contains ( ie ) ) )
   {

    criteria4++;
    return false;

   }

   // System.out.println ( " CLEARED 2 " );

   if ( ( resolver.getErrorMethods().contains ( m.getSignature() ) ) ) 
   {
 
    criteria5++;
    return false;   

   }

   // System.out.println ( " CLEARED 3 " );

   if ( ! satisfiesInvokeSpecialSafety ( m ) )
   {

    // System.out.println ( " PROBLEM WITH A SPECIAL INVOKE IN "+m.getSignature() );
   
    criteria6++;
    return false;

   }

   // System.out.println ( " CLEARED 4 " ); 

   if ( ! satisfiesExceptionsAccess ( m ) )
   {
 
    criteria7++;
    return false;

   }

   // System.out.println ( " CLEARED 5 " );


   if ( ! satisfiesResolverCriteria ( m ) )
   {

    criteria7++;
    return false;

    }

   // System.out.println ( " CLEARED 6 " );

   if ( ( currmethod.getSignature().equals ( m.getSignature() ) ) )
   {

    criteria8++;
    return false;

   }

   // System.out.println ( " CLEARED 7 " );

   } catch ( java.lang.RuntimeException e ) { return false; }


   return true;


  }











  public boolean satisfiesExceptionsAccess ( SootMethod m ) {

   Iterator excit = m.getExceptions().iterator();

   String currpackname = getPackageName ( currmethod.getDeclaringClass().getName() );

   while ( excit.hasNext() )
   {

    boolean samepack = false;

    SootClass nextException = ( SootClass ) excit.next();

    nextException = scm.getClass ( nextException.getName() );
     
    samepack = isSamePackage ( getPackageName ( nextException.getName() ), currpackname );
       
    if ( ( ( Integer ) classesHT.get ( nextException.getName() ) ) == null )  
    classesHT.put ( nextException.getName(), getCorrectModifier ( nextException.getModifiers() ) );
       
       if ( ! ( samepack ) )
       {  

        classesHT.put ( nextException.getName(), pub );

        if ( ( ( Integer ) resolverclassesHT.get ( nextException.getName() ) ).intValue() < 3 )
        return false;

       }   

   }

   return true;

  }













  public Type getCorrectType ( Type type ) {

   type.apply ( new TypeSwitch() {

    public void caseArrayType ( ArrayType t ) {

     correcttype = ArrayType.v ( t.baseType, t.numDimensions );

    }

    public void caseBooleanType ( BooleanType t ) {

     correcttype = BooleanType.v ();

    }
    
    public void caseByteType ( ByteType t ) {

     correcttype = ByteType.v ();

    }

    public void caseCharType ( CharType t ) {

     correcttype = CharType.v ();

    }

    public void caseDoubleType ( DoubleType t ) {

     correcttype = DoubleType.v ();

    }

    public void caseErroneousType ( ErroneousType t ) {

     correcttype = ErroneousType.v ();

    }

    public void caseFloatType ( FloatType t ) {

     correcttype = FloatType.v ();

    }
    
    public void caseLongType ( LongType t ) {

     correcttype = LongType.v ();

    }

    public void caseIntType ( IntType t ) {

     correcttype = IntType.v ();

    }

    public void caseNullType ( NullType t ) {

     correcttype = NullType.v ();

    }
    
    public void caseShortType ( ShortType t ) {

     correcttype = ShortType.v ();

    }

    public void caseStmtAddressType ( StmtAddressType t ) {

     correcttype = StmtAddressType.v ();

    }

    public void caseRefType ( RefType t ) {

     correcttype = RefType.v ( t.className );

    }
    
    public void caseUnknownType ( UnknownType t ) {

     correcttype = UnknownType.v();

    }

    public void caseVoidType ( VoidType t ) {

     correcttype = VoidType.v();

    }

   });

   return correcttype;

  }













  public Constant getCorrectConstant ( Constant constant ) {

   constant.apply ( new AbstractJimpleValueSwitch() {

    public void caseIntConstant ( IntConstant c ) {

     correctconstant = IntConstant.v ( c.value );

    }

    public void caseDoubleConstant ( DoubleConstant c ) {
   
     correctconstant = DoubleConstant.v ( c.value );

    }

    public void caseFloatConstant ( FloatConstant c ) {
   
     correctconstant = FloatConstant.v ( c.value );

    }
   
    public void caseLongConstant ( LongConstant c ) {
   
     correctconstant = LongConstant.v ( c.value );

    }

    public void caseNullConstant ( NullConstant c ) {
   
     correctconstant = NullConstant.v ();

    }

    public void caseStringConstant ( StringConstant c ) {

     correctconstant = StringConstant.v ( c.value );

    }

   });

   return correctconstant;

  }







  public Local getCorrectLocal ( Local local ) {

    //String correctlocalname = ( String ) localsHT.get ( local.getName() );

   // Local correctlocal = jimple.newLocal ( correctlocalname, getCorrectType (local.getType() ) );

   Local correctlocal = ( Local ) localsHT.get ( local.getName() );

   return correctlocal;

  }  








  public ArrayRef getCorrectArrayRef ( ArrayRef arrayref ) {

   Value base = arrayref.getBase();

   Value index = arrayref.getIndex();

   // ArrayRef correctarrayref = arrayref.clone();
   
   ArrayRef correctarrayref = jimple.newArrayRef ( base, index );

   correctarrayref.setBase ( ( Local ) getCorrectValue ( base ) );

   correctarrayref.setIndex ( getCorrectValue ( index ) );

   return correctarrayref;

  }


 
   
  



  public SootField getCorrectField ( SootField field ) {

   SootField correctfield = new SootField ( field.getName(), getCorrectType (
field.getType() ), field.getModifiers() );
 
   return correctfield;

  }











  public InstanceFieldRef getCorrectInstanceFieldRef ( InstanceFieldRef
instancefieldref ) {

    //   System.out.println("REACHED 1");

   Value base = instancefieldref.getBase();

   // InstanceFieldRef correctinstancefieldref = instancefieldref.clone();


   InstanceFieldRef correctinstancefieldref = jimple.newInstanceFieldRef ( base, instancefieldref.getField() );

   Value b = getCorrectValue ( base );
    
   // System.out.println("REACHED 2 "+b.toString());

   correctinstancefieldref.setBase ( b );

   // System.out.println("REACHED 2 "+b.toString());

   //   SootField f = getCorrectField ( instancefieldref.getField() );

   // System.out.println("REACHED 3 ");

   // correctinstancefieldref.setField ( f );

   // System.out.println("REACHED 3 "+f.toString() );

   return correctinstancefieldref;   

  }







  public StaticFieldRef getCorrectStaticFieldRef ( StaticFieldRef staticfieldref
) {

   SootField field = staticfieldref.getField();

   //   StaticFieldRef correctstaticfieldref = jimple.newStaticFieldRef (getCorrectField ( field ) );

     StaticFieldRef correctstaticfieldref = jimple.newStaticFieldRef ( field );


   return correctstaticfieldref;

  }

  
  








  /*

  public Immediate getCorrectImmediate ( Immediate im ) {

   Immediate correctimmediate = null;

   if ( im instanceof Local )
   {

    Local l = ( Local ) im;

    correctimmediate = getCorrectLocal ( l );

   }
   else    
   {

    Constant c = ( Constant ) im;

    correctimmediate = getCorrectConstant ( c );

   }

   return correctimmediate;

  }    

  */


  



  /*

  public Variable getCorrectVariable ( Variable variable ) {

   Variable correctvariable = null;

   if ( variable instanceof Local )
   {

    Local l = ( Local ) variable;

    correctvariable = getCorrectLocal ( l );

   }
   else if ( variable instanceof ArrayRef )
   { 
 
    ArrayRef arrayref = ( ArrayRef ) variable;

    correctvariable = getCorrectArrayRef ( arrayref );

   }
   else if ( variable instanceof InstanceFieldRef )
   {
 
    InstanceFieldRef instancefieldref = ( InstanceFieldRef ) variable;
   
    // System.out.println(" INSTANCE FIELD ========== "+instancefieldref);

    correctvariable = getCorrectInstanceFieldRef ( instancefieldref );

   }
   else if ( variable instanceof StaticFieldRef )
   {

    StaticFieldRef staticfieldref = ( StaticFieldRef ) variable;

    correctvariable = getCorrectStaticFieldRef ( staticfieldref );

   }

   return correctvariable;

  }  

  */






  

  public Value getCorrectValue ( Value rvalue ) {

   Value correctrvalue = null;

   if ( rvalue instanceof Local )
   {

    Local l = ( Local ) rvalue;

    correctrvalue = getCorrectLocal ( l );

   }
   else if ( rvalue instanceof ArrayRef )
   { 
 
    ArrayRef arrayref = ( ArrayRef ) rvalue;

    correctrvalue = getCorrectArrayRef ( arrayref );

   }
   else if ( rvalue instanceof InstanceFieldRef )
   {
 
    InstanceFieldRef instancefieldref = ( InstanceFieldRef ) rvalue;

    // System.out.println(" INSTANCE FIELD ========== "+instancefieldref);
     
    correctrvalue = getCorrectInstanceFieldRef ( instancefieldref );

   }
   else if ( rvalue instanceof StaticFieldRef )
   {      

    StaticFieldRef staticfieldref = ( StaticFieldRef ) rvalue;

    correctrvalue = getCorrectStaticFieldRef ( staticfieldref );

   }
   else if ( rvalue instanceof Constant )
   {

    Constant c = ( Constant ) rvalue;

    correctrvalue = getCorrectConstant ( c );

   }
   else if ( rvalue instanceof Expr )
   {
 
    Expr expr = ( Expr ) rvalue;  
 
    correctrvalue = getCorrectExpr ( expr );

   }
   // else if ( rvalue instanceof NextNextStmtRef )
   // {

    // correctrvalue = jimple.newNextNextStmtRef();

   // }


   return correctrvalue;

  }  


  
  






  public ConditionExpr getCorrectConditionExpr ( ConditionExpr conditionexpr ) {

   BinopExpr binopexpr = ( BinopExpr ) conditionexpr;

   ConditionExpr correctcondition = ( ConditionExpr ) getCorrectBinopExpr ( binopexpr );     

   return correctcondition;

  }









 


  public Expr getCorrectBinopExpr ( BinopExpr binopexpr ) {

   // BinopExpr correctbinopexpr = binopexpr.clone();

   op1 = binopexpr.getOp1();

   op2 = binopexpr.getOp2();

   binopexpr.apply ( new AbstractJimpleValueSwitch() {

    public void caseAddExpr ( AddExpr e ) {

     correctbinopexpr = jimple.newAddExpr ( op1, op2 );   

    }
    
    public void caseAndExpr ( AndExpr e ) {

     correctbinopexpr = jimple.newAndExpr ( op1, op2 );   

    }

    public void caseCmpExpr ( CmpExpr e ) {

     correctbinopexpr = jimple.newCmpExpr ( op1, op2 );   

    }

    public void caseCmpgExpr ( CmpgExpr e ) {

     correctbinopexpr = jimple.newCmpgExpr ( op1, op2 );   

    }

    public void caseCmplExpr ( CmplExpr e ) {

     correctbinopexpr = jimple.newCmplExpr ( op1, op2 );   

    }
    
    public void caseDivExpr ( DivExpr e ) {

     correctbinopexpr = jimple.newDivExpr ( op1, op2 );   

    }

    public void caseEqExpr ( EqExpr e ) {

     correctbinopexpr = jimple.newEqExpr ( op1, op2 );   

    }
    
    public void caseGeExpr ( GeExpr e ) {

     correctbinopexpr = jimple.newGeExpr ( op1, op2 );   

    }

    public void caseGtExpr ( GtExpr e ) {

     correctbinopexpr = jimple.newGtExpr ( op1, op2 );   

    }

    public void caseLtExpr ( LtExpr e ) {

     correctbinopexpr = jimple.newLtExpr ( op1, op2 );   

    }
    
    public void caseLeExpr ( LeExpr e ) {

     correctbinopexpr = jimple.newLeExpr ( op1, op2 );   

    }

    public void caseMulExpr ( MulExpr e ) {

     correctbinopexpr = jimple.newMulExpr ( op1, op2 );   

    }

    public void caseNeExpr ( NeExpr e ) {

     correctbinopexpr = jimple.newNeExpr ( op1, op2 );   

    }
    
    public void caseOrExpr ( OrExpr e ) {

     correctbinopexpr = jimple.newOrExpr ( op1, op2 );   

    }

    public void caseRemExpr ( RemExpr e ) {

     correctbinopexpr = jimple.newRemExpr ( op1, op2 );   

    }

    public void caseShlExpr ( ShlExpr e ) {

     correctbinopexpr = jimple.newShlExpr ( op1, op2 );   

    }

    public void caseShrExpr ( ShrExpr e ) {

     correctbinopexpr = jimple.newShrExpr ( op1, op2 );   

    }

    public void caseSubExpr ( SubExpr e ) {

     correctbinopexpr = jimple.newSubExpr ( op1, op2 );   

    }

    public void caseUshrExpr ( UshrExpr e ) {

     correctbinopexpr = jimple.newUshrExpr ( op1, op2 );   

    }

    public void caseXorExpr ( XorExpr e ) {

     correctbinopexpr = jimple.newXorExpr ( op1, op2 );   

    }

   });

   correctbinopexpr.setOp1 ( getCorrectValue ( op1 ) );
 
   correctbinopexpr.setOp2 ( getCorrectValue ( op2 ) );

   return correctbinopexpr;

  }









  public Expr getCorrectCastExpr ( CastExpr castexpr ) {

   //   CastExpr correctcastexpr = castexpr.clone();

   Value op = castexpr.getOp();

   CastExpr correctcastexpr = jimple.newCastExpr ( op, getCorrectType ( castexpr.getType() ) );

   correctcastexpr.setOp ( getCorrectValue ( op ) );
 
   return correctcastexpr;

  }









  public Expr getCorrectInstanceOfExpr ( InstanceOfExpr instanceofexpr ) {

   // InstanceOfExpr correctinstanceofexpr = instanceofexpr.clone();

   Value op = instanceofexpr.getOp();

   InstanceOfExpr correctinstanceofexpr = jimple.newInstanceOfExpr ( op, getCorrectType ( instanceofexpr.getCheckType() ) );

   correctinstanceofexpr.setOp ( getCorrectValue ( op ) );
 
   return correctinstanceofexpr;

  }










  public Expr getCorrectInvokeExpr ( InvokeExpr invokeexpr ) {

   // correctinvokeexpr = invokeexpr.clone();

   // System.out.println ( " REACHED CORRECTINVOKE"+invokeexpr );

   int numargs = invokeexpr.getArgCount();

   args = new ArrayList();

   for ( int i = 0; i < numargs; i++ )
   {

    Value arg = invokeexpr.getArg(i);

    args.add ( arg );

   }
   
   // System.out.println ( " RCI 1 "+invokeexpr );
   

   invokeexpr.apply( new AbstractJimpleValueSwitch() {

    public void caseInterfaceInvokeExpr(InterfaceInvokeExpr v) {

     Value base = v.getBase();

     correctinvokeexpr = jimple.newInterfaceInvokeExpr ( ( Local ) v.getBase(), v.getMethod(),
args ); 

     ( ( InterfaceInvokeExpr ) correctinvokeexpr ).setBase ( getCorrectValue( base ) );
  
    }

    
    public void caseSpecialInvokeExpr(SpecialInvokeExpr v) {

     Value base = v.getBase();

     // System.out.println ( " REACHED SPECINVOKE "+v );

     correctinvokeexpr = jimple.newSpecialInvokeExpr ( ( Local ) v.getBase(), v.getMethod(), args ); 

     // System.out.println ( " REACHED CLONEDSPEINV "+correctinvokeexpr );

     ( ( SpecialInvokeExpr ) correctinvokeexpr ).setBase ( getCorrectValue ( base ) );
  
     // System.out.println ( " REACHED CLONEDSPEINV "+correctinvokeexpr );

    }


    public void caseStaticInvokeExpr( StaticInvokeExpr v ) {

     correctinvokeexpr = jimple.newStaticInvokeExpr ( v.getMethod(), args );

    }

    
    public void caseVirtualInvokeExpr(VirtualInvokeExpr v) {

     Value base = v.getBase();

     correctinvokeexpr = jimple.newVirtualInvokeExpr ( ( Local ) v.getBase(), v.getMethod(), args ); 

     ( ( VirtualInvokeExpr ) correctinvokeexpr ).setBase ( getCorrectValue ( base ) );
  
    }

   });

   numargs = invokeexpr.getArgCount();

   for ( int i = 0; i < numargs; i++ )
   {

    Value arg = invokeexpr.getArg(i);

    correctinvokeexpr.setArg ( i, getCorrectValue ( arg ) );
 
   }

     
//   invokeExprsHT.put ( correctinvokeexpr, ( CallSite ) invokeExprsHT.get ( invokeexpr ) );

   invokeExprsToMethods.put ( correctinvokeexpr, inliningInsideMethod );

   if ( inliningImportant )
   {

    if ( invokeExprsHT.get ( invokeexpr ) != null )
    {

     invokeExprsHT.put ( correctinvokeexpr, ( CallSite ) invokeExprsHT.get ( invokeexpr ) );

      workQ.add ( correctinvokeexpr );

    }

   }

   return correctinvokeexpr;

  }




  public boolean inliningImportant = true;




  public Expr getCorrectUnopExpr ( UnopExpr unopexpr ) {

    // UnopExpr correctunopexpr = unopexpr.clone();

   op = unopexpr.getOp();
   
   unopexpr.apply ( new AbstractJimpleValueSwitch() {

    public void caseLengthExpr ( LengthExpr e ) {

     correctunopexpr = jimple.newLengthExpr ( op );   

    }

    public void caseNegExpr ( NegExpr e ) {

     correctunopexpr = jimple.newNegExpr ( op );   

    }

   });

   correctunopexpr.setOp ( getCorrectValue ( op ) );
 
   return correctunopexpr;

  }








 
  public NewArrayExpr getCorrectNewArrayExpr ( NewArrayExpr newarrayexpr ) {

   NewArrayExpr correctnewarrayexpr = jimple.newNewArrayExpr ( getCorrectType (
newarrayexpr.getBaseType() ), getCorrectValue ( newarrayexpr.getSize() ) );
   
   return correctnewarrayexpr;

 }








 
  public NewExpr getCorrectNewExpr ( NewExpr newexpr ) {

   NewExpr correctnewexpr = jimple.newNewExpr ( ( RefType ) getCorrectType (
newexpr.getBaseType() ) );
   
   return correctnewexpr;

  }












  public NewMultiArrayExpr getCorrectNewMultiArrayExpr ( NewMultiArrayExpr
newmultiarrayexpr ) {

   // List correctlist = getCorrectList ( newmultiarrayexpr.getSizes() );

   List correctlist = new ArrayList();

   for ( int i = 0; i < newmultiarrayexpr.getSizeCount(); i++ )
   {

    Value imm = getCorrectValue ( newmultiarrayexpr.getSize ( i ) ); 

    correctlist.add ( i, imm );

   }



   NewMultiArrayExpr correctnewmultiarrayexpr = jimple.newNewMultiArrayExpr ( ( ( ArrayType ) getCorrectType ( newmultiarrayexpr.getBaseType() ) ), correctlist );
   
   return correctnewmultiarrayexpr;

 }












  
  public Expr getCorrectExpr ( Expr expr ) {

   Expr correctexpr = null;
  
   if ( expr instanceof BinopExpr )
   {

    BinopExpr binopexpr = ( BinopExpr ) expr;

    correctexpr = getCorrectBinopExpr ( binopexpr );

   }
   else if ( expr instanceof CastExpr )
   {

    CastExpr castexpr = ( CastExpr ) expr;

    correctexpr = getCorrectCastExpr ( castexpr );

   }
   else if ( expr instanceof InstanceOfExpr )
   {

    InstanceOfExpr instanceofexpr = ( InstanceOfExpr ) expr;

    correctexpr = getCorrectInstanceOfExpr ( instanceofexpr );
  
   }
   else if ( expr instanceof InvokeExpr )
   {

    InvokeExpr invokeexpr = ( InvokeExpr ) expr;

    correctexpr = getCorrectInvokeExpr ( invokeexpr );
  
   }
   else if ( expr instanceof NewArrayExpr )
   {
  
    NewArrayExpr newarrayexpr = ( NewArrayExpr ) expr;

    correctexpr = getCorrectNewArrayExpr ( newarrayexpr );

   }
   else if ( expr instanceof NewExpr )
   {
  
    NewExpr newexpr = ( NewExpr ) expr;

    correctexpr = getCorrectNewExpr ( newexpr );

   }
   else if ( expr instanceof NewMultiArrayExpr )
   {
  
    NewMultiArrayExpr newmultiarrayexpr = ( NewMultiArrayExpr ) expr;

    correctexpr = getCorrectNewMultiArrayExpr ( newmultiarrayexpr );

   }
   else if ( expr instanceof UnopExpr )
   {

    UnopExpr unopexpr = ( UnopExpr ) expr;

    correctexpr = getCorrectUnopExpr ( unopexpr );
  
   }

   return correctexpr;

  }





  public Local cloneForsync;

  public Local thisvariable;






  public Stmt getCorrectStmt ( Stmt stmt ) {


   stmt.apply( new AbstractStmtSwitch(){


    public void caseIdentityStmt ( IdentityStmt s ) {

     Value leftop = ( ( Value ) s.getLeftOp() );

     Value rightop = null;

     if ( s.getRightOp() instanceof ThisRef )
     {

  //    System.out.println ( "REACHED HERE" );

      if ( interfaceInvoked ) 
      rightop = currInterfaceInvokeExpr.getBase();     
      else if ( specialInvoked )
      rightop = currSpecialInvokeExpr.getBase();
      else if ( virtualInvoked )
      rightop = currVirtualInvokeExpr.getBase();

     }

     if ( s.getRightOp() instanceof ParameterRef )
     {

       ParameterRef pref = ( ParameterRef ) s.getRightOp();

       int index = pref.getIndex(); 

//       System.out.println ( "REACHED PARAMETER" );

//       System.out.println ( currInvokeExpr );

       rightop = currInvokeExpr.getArg( index );
     
//       System.out.println ( " ACTUAL PARAMETER "+ rightop );

     }

     if ( ! ( s.getRightOp() instanceof CaughtExceptionRef ) )
     {

      Value correctleftop = getCorrectValue ( leftop );

      if ( syncflag )
      {

       if ( s.getRightOp() instanceof ThisRef )
       {

        cloneForsync = jimple.newLocal ( new String ( "dummy"+j ), ( ( Local ) correctleftop ).getType() );

        thisvariable = ( Local ) correctleftop;

        j++;

        localsHT.put( cloneForsync.getName(), cloneForsync );

        listBody.addLocal ( cloneForsync );

       }

      }


//      System.out.println ( "REACHED PARAMETER 2 ");

      // System.out.println ( ( ( Local ) rightop ).getName() );

      //      Value correctrightop = getCorrectValue ( rightop );

      Value correctrightop = rightop;

      // System.out.println ( ( ( Local ) correctrightop ).getName() );

      correctstmt = jimple.newAssignStmt ( correctleftop, correctrightop );
   
     }
     else
     correctstmt = jimple.newIdentityStmt ( getCorrectValue ( leftop ), jimple.newCaughtExceptionRef( listBody ) );

    }

    public void caseBreakpointStmt ( BreakpointStmt s ) {

     // correctstmt = s.clone();     
  
     correctstmt = jimple.newBreakpointStmt();

    }

    public void caseAssignStmt ( AssignStmt s ) {
    
     Value var = s.getLeftOp();

     Value rval = s.getRightOp();

     Value correctvar = getCorrectValue ( var );

     // System.out.println (" DEBUG 1 ");

     // System.out.println (" DEBUG 1 "+correctvar.toString() );

     Value correctrval = getCorrectValue ( rval ); 

     // System.out.println (" DEBUG 2 ");

     // System.out.println (" DEBUG 2 "+correctrval.toString() );
   
     correctstmt = jimple.newAssignStmt ( correctvar , correctrval );

     // System.out.println(" DEBUG "+correctstmt.toString() );

    }

    public void caseEnterMonitorStmt ( EnterMonitorStmt s ) {

     Value im = ( Value ) s.getOp();

     correctstmt = jimple.newEnterMonitorStmt ( getCorrectValue ( im ) ); 

    }

    public void caseExitMonitorStmt ( ExitMonitorStmt s ) {

     Value im = ( Value ) s.getOp();

     correctstmt = jimple.newExitMonitorStmt ( getCorrectValue ( im ) ); 

    }

    public void caseGotoStmt ( GotoStmt s ) {

     Stmt target = ( Stmt ) s.getTarget();

     // correctstmt = new GotoStmt ( target.clone() );

     correctstmt = jimple.newGotoStmt ( target );

    }

    public void caseIfStmt ( IfStmt s ) {
   
     Value con = s.getCondition();

     Stmt target = s.getTarget();

     // correctstmt = new IfStmt ( getCorrectCondition ( con ), target.clone() );

     correctstmt = jimple.newIfStmt ( getCorrectValue ( con ), target );

    }

    public void caseInvokeStmt ( InvokeStmt s ) {

     InvokeExpr invokeexpr = ( InvokeExpr ) s.getInvokeExpr();

     correctstmt = jimple.newInvokeStmt ( ( InvokeExpr ) getCorrectInvokeExpr (
invokeexpr ) );

    }

    public void caseLookupSwitchStmt ( LookupSwitchStmt s ) {

     Value key = s.getKey();

     List lookupvalues = getCorrectList ( s.getLookupValues() );

     List targets = getCorrectList ( s.getTargets() );

     Stmt defaulttarget = ( Stmt ) s.getDefaultTarget();

     // correctstmt = jimple.newLookupSwitchStmt ( getCorrectImmediate ( key ), lookupvalues.clone(), targets.clone(), defaulttarget.clone() );

     correctstmt = jimple.newLookupSwitchStmt ( getCorrectValue ( key ), lookupvalues, targets, defaulttarget );

    }

    public void caseNopStmt ( NopStmt s ) {

     // correctstmt = s.clone();

     correctstmt = jimple.newNopStmt();

    }

    public void caseRetStmt ( RetStmt s ) {

     Value l = s.getStmtAddress();

     correctstmt = jimple.newRetStmt ( getCorrectValue ( l ) );

    }

    public void caseReturnStmt ( ReturnStmt s ) {

     Value returnvalue = s.getReturnValue();

     Value correctreturnvalue = getCorrectValue ( returnvalue );

/*
     RValue r = null;

     if ( correctreturnvalue instanceof Local )
     r = ( Local ) correctreturnvalue;
     else if ( correctreturnvalue instanceof Constant )
     r = ( Constant ) correctreturnvalue;

     if ( assignflag )
     correctstmt = new AssignStmt ( returnvariable, r );
     else
     correctstmt = new NopStmt();
*/

     correctstmt = jimple.newReturnStmt ( correctreturnvalue );

    }

    public void caseReturnVoidStmt ( ReturnVoidStmt s ) {

     // correctstmt = s.clone();

     correctstmt = jimple.newReturnVoidStmt();

    }

      
    public void caseTableSwitchStmt ( TableSwitchStmt s ) {

     Value key = s.getKey();

     int highindex = s.getHighIndex();

     int lowindex = s.getLowIndex(); 

     List targets = getCorrectList ( s.getTargets() );

     Stmt defaulttarget = ( Stmt ) s.getDefaultTarget();

     // correctstmt = new TableSwitchStmt ( getCorrectImmediate ( key ), lowindex, highindex, targets.clone(), defaulttarget.clone() );


     correctstmt = jimple.newTableSwitchStmt ( getCorrectValue ( key ), lowindex,highindex, targets, defaulttarget );

    }

    public void caseThrowStmt ( ThrowStmt s ) {

     Value im = s.getOp();

     correctstmt = jimple.newThrowStmt ( getCorrectValue ( im ) );

    }

   });

   return correctstmt;
 
  }


 



 IdentityStmt syncid;

 ExitMonitorStmt syncexit;

 List syncexits = new ArrayList();

 List syncexittargets = new ArrayList();






  public void InlineMethod ( StmtList inlinablemethod, StmtList targetmethod, Iterator target ) {

  // int start = startstmtnum; 

   syncexits = new ArrayList();

   syncexittargets = new ArrayList();

   String inmethname = inlinablemethod.getBody().getMethod().getSignature();

   String tamethname = targetmethod.getBody().getMethod().getSignature();

   Iterator fixupiterator = targetmethod.iterator();
 
   int fixupnumstmts = 0;

   while ( fixupnumstmts < ( numstmts - 1 ) )
   {

    fixupiterator.next();

    fixupnumstmts++;

   }

   // System.out.println ( " IN INLINE METHOD, FIXUPNUMSTMTS = "+fixupnumstmts );

   // System.out.println ( " IN INLINE METHOD, NUMSTMTS = "+numstmts );

   targetnumstmts = fixupnumstmts + 1;

   int numidentity = 0;

   if ( syncflag )
   numidentity = CountIdentityStmts ( inlinablemethod );

   Iterator stmtiter = inlinablemethod.iterator(); 

   int numSofar = 0;

   boolean firstinsertion = true;

   while ( stmtiter.hasNext() )
   {

    Stmt nextstmt = ( Stmt ) stmtiter.next();

    numSofar++;

    if ( (syncflag ) && ( firstinsertion ) )
    {

     if ( numSofar > numidentity )
     {

      AssignStmt syncassign = jimple.newAssignStmt ( cloneForsync, thisvariable );
      targetmethod.add ( numstmts, syncassign );

      numstmts++;

      //      target.next();

      EnterMonitorStmt syncenter = jimple.newEnterMonitorStmt ( thisvariable );

      targetmethod.add ( numstmts, syncenter );

      numstmts++;
       
      //    target.next();

      firstinsertion = false;
    
      syncid = jimple.newIdentityStmt ( syncthrow, jimple.newCaughtExceptionRef ( listBody ) );

      syncexit = jimple.newExitMonitorStmt ( thisvariable );

     }

    }

/*
    if ( syncflag )
    {

     if ( ( nextstmt instanceof ReturnStmt ) || ( nextstmt instanceof ReturnVoidStmt ) )
     {


      AssignStmt syncassgn = jimple.newAssignStmt ( syncaddress, jimple.newNextNextStmtRef() );

      targetmethod.add ( numstmts, syncassgn );

      numstmts++;
       
      target.next();



      GotoStmt syncgoto = jimple.newGotoStmt ( syncexit );

      targetmethod.add ( numstmts, syncgoto );

      numstmts++;
       
      target.next();

      syncexits.add ( syncexit );

      syncexittargets.add ( nextstmt );

      syncexit = jimple.newExitMonitorStmt( thisvariable );

     }

    }


*/

    //    System.out.println ( "ORIG STMT "+nextstmt.toString() ); 

    try {

     Stmt newstmt = getCorrectStmt ( nextstmt );

     
    
     // System.out.println ( "REPLACED BY : "+newstmt.toString()+" FROM "+inmethname+" INTO "+tamethname );
 
/*
     if ( nextstmt instanceof IdentityStmt )
     System.out.println ( "REACHED GETCORRECTSTMT");

     if ( nextstmt instanceof IdentityStmt )
     System.out.println ( "NUMSTMTS "+numstmts );
*/

     targetmethod.add ( numstmts, newstmt ); 

/*
     if ( nextstmt instanceof IdentityStmt )
     System.out.println ( "REACHED GETCORRECTSTMT 1 ");
*/


     numstmts++;

     // targetmethod.add ( start, newstmt );

     // start++;

     // target.next();

/*
     if ( nextstmt instanceof IdentityStmt )
     System.out.println ( "REACHED GETCORRECTSTMT 2");
*/


    } catch ( java.lang.RuntimeException e ) { 

      // System.out.println(" PROBLEM STMT IGNORED IN INLINER : "+nextstmt.toString() ); 
        
       e.printStackTrace ( System.out );
     }

   }

   targetmeth = targetmethod;

   inlinablemeth = inlinablemethod;

   FixupTargets ();

   FixupTraps();

   if ( syncflag )
   {

    targetmethod.add ( numstmts, syncid );

    numstmts++;

    // target.next();

    ExitMonitorStmt syncex = jimple.newExitMonitorStmt ( cloneForsync );
    
    targetmethod.add ( numstmts, syncex );

    numstmts++;

    // target.next();

    ThrowStmt syncthrw = jimple.newThrowStmt ( syncthrow );
    
    targetmethod.add ( numstmts, syncthrw );

    numstmts++;

    // target.next();

/*

    Iterator syncexitit = syncexits.iterator();

    Iterator syncexittargetit = syncexittargets.iterator();

    while ( syncexitit.hasNext() )
    {

     ExitMonitorStmt monitorexit = ( ExitMonitorStmt ) syncexitit.next();

     targetmethod.add ( numstmts, monitorexit );

     numstmts++;

     // target.next();

     // RetStmt syncret = jimple.newRetStmt ( syncaddress );

     GotoStmt syncret = jimple.newGotoStmt ( ( Stmt ) syncexittargetit.next() );

     targetmethod.add ( numstmts, syncret );

     numstmts++;

     // target.next();

    }

*/

   }


   if ( assignflag )
   returnstmt = jimple.newAssignStmt ( returnvariable, ( Value ) dummyreturn );
   else
   returnstmt = jimple.newNopStmt();

   targetmethod.add ( numstmts, returnstmt );

   numstmts++;

   // target.next();

   // System.out.println("JUST BEFORE ENTERING FIXUP NUMSTMTS = "+numstmts);

   // System.out.println(" AND FIXUPNUMSTMTS = "+fixupnumstmts );

   if ( syncflag )
   {

    int trapindex = indexOf ( firstnonidstmt, inlinablemeth ) + 2;

    Stmt starttrap = ( Stmt ) targetmeth.get ( targetnumstmts+trapindex ); 

    Trap synctrap = jimple.newTrap ( scm.getClass( "java.lang.Throwable" ), starttrap, syncid, syncid );

    listBody.addTrap ( synctrap );

   } 


   FixupMethod ( targetmethod, fixupiterator, target, fixupnumstmts );
   
   Iterator syncexitit = syncexits.iterator();
     
   Iterator syncexittargetit = syncexittargets.iterator();
     
   while ( syncexitit.hasNext() )
   {
   
     ExitMonitorStmt monitorexit = ( ExitMonitorStmt ) syncexitit.next();
  
     targetmethod.add ( numstmts - 1, monitorexit );
   
     numstmts++;
    
     // target.next();
   
     // RetStmt syncret = jimple.newRetStmt ( syncaddress );
   
     GotoStmt syncret = jimple.newGotoStmt ( ( Stmt ) syncexittargetit.next() );
   
     targetmethod.add ( numstmts - 1, syncret );
   
     numstmts++;
  
     // target.next();
    
   }

/*
   System.out.println ( "REACHED FIXUP 1" );

   Iterator tgtit = targetmethod.iterator();

   while ( tgtit.hasNext() )
   {

    Stmt nextst = ( Stmt ) tgtit.next();

    System.out.println ("DEBUG "+nextst );

    if ( nextst instanceof GotoStmt )
    System.out.println ("DEBUG TGT "+( ( GotoStmt ) nextst).getTarget() );

   }

*/
   // System.out.println("SUCCESSFULLY INLINED");

  }



  public int CountIdentityStmts ( StmtList list ) {

   Iterator listit = list.iterator();

   int numid = 0;

   boolean idleft = true;

   Stmt idstmt = null;

   while ( ( listit.hasNext() ) && ( idleft ) )
   {

    idstmt = ( Stmt ) listit.next();

    if ( idstmt instanceof IdentityStmt )
    {

     IdentityStmt ids = ( IdentityStmt ) idstmt;

     if ( ids.getRightOp() instanceof CaughtExceptionRef )
     idleft = false;
     else
     numid++;

    }
    else
    idleft = false;

   }

   firstnonidstmt = idstmt;

   return numid;

  }



  Stmt firstnonidstmt;



  public void FixupTraps () {

   Iterator excit = me.getExceptions().iterator();

   while ( excit.hasNext() )
   {

    SootClass nextException = ( SootClass ) excit.next();

    if ( ! ( currmethod.throwsException ( nextException ) ) )
    currmethod.addException ( nextException );

   }
   
   Iterator trapiterator = melistBody.getTraps().iterator();
   
   while ( trapiterator.hasNext() )
   {

    Trap t = ( Trap ) trapiterator.next();

    Stmt beginstmt = ( Stmt ) t.getBeginUnit();

    Stmt endstmt = ( Stmt ) t.getEndUnit();

    Stmt handlerstmt = ( Stmt ) t.getHandlerUnit();
 
    SootClass exclass = t.getException();
  
    int indexOftarget = indexOf ( beginstmt , inlinablemeth );

    if ( syncflag )
    indexOftarget = indexOftarget + 2;

    Stmt inlinedbeginstmt = ( Stmt ) targetmeth.get ( targetnumstmts+indexOftarget );      

   indexOftarget = indexOf ( endstmt , inlinablemeth );

   if ( syncflag )
   indexOftarget = indexOftarget + 2;

   Stmt inlinedendstmt = ( Stmt ) targetmeth.get ( targetnumstmts+indexOftarget );      

   indexOftarget = indexOf ( handlerstmt , inlinablemeth );

  if ( syncflag )
  indexOftarget = indexOftarget + 2;



   Stmt inlinedhandlerstmt = ( Stmt ) targetmeth.get ( targetnumstmts+indexOftarget );      

   Trap newtrap = jimple.newTrap ( exclass, inlinedbeginstmt, inlinedendstmt, inlinedhandlerstmt );
   
   listBody.addTrap ( newtrap );
     
   }  


  }


















  public void FixupTargets () {

   // System.out.println ( "REACHED FIXUP TARGETS" );

   Iterator inlineiterator = inlinablemeth.iterator();

/*

   Iterator targetiterator = targetmeth.iterator();
 
   targetnumstmts = 0;

   while ( targetnumstmts < numstmts )
   {

    targetiterator.next();

    targetnumstmts++;

   }

*/

   inlinenumstmts = 0;

   while ( inlineiterator.hasNext() )  
   {

    Stmt s = ( Stmt ) inlineiterator.next();

    //    System.out.println("CHECK IN ORIGINAL MTHD : "+s.toString() );

    s.apply ( new AbstractStmtSwitch () {    

     public void caseGotoStmt ( GotoStmt s ) {

      Stmt target = ( Stmt ) s.getTarget();

      // correctstmt = new GotoStmt ( target.clone() );

      int indexOftarget = indexOf ( target, inlinablemeth );

       if ( syncflag )
   indexOftarget = indexOftarget + 2;

      GotoStmt inlinedStmt = null;

      if ( syncflag )    
inlinedStmt = ( GotoStmt ) targetmeth.get( targetnumstmts+inlinenumstmts + 2);
      else
      inlinedStmt = ( GotoStmt ) targetmeth.get( targetnumstmts+inlinenumstmts );

      // System.out.println("CHECK IN TARGET METHOD : "+inlinedStmt.toString() );

      Stmt inlinedtarget = ( Stmt ) targetmeth.get ( targetnumstmts+indexOftarget );      

      inlinedStmt.setTarget ( inlinedtarget ); 

     }


     public void caseIfStmt ( IfStmt s ) {
   
      Stmt target = ( Stmt ) s.getTarget();

      int indexOftarget = indexOf ( target, inlinablemeth );

 if ( syncflag )
   indexOftarget = indexOftarget + 2;


      //System.out.println( "REACHED HERE" );

      //System.out.println( "CHECK IN TARGET METHOD 1 : "+ targetmeth.get (targetnumstmts+inlinenumstmts ) );

      IfStmt inlinedStmt = null;

      if ( syncflag )
      inlinedStmt = ( IfStmt ) targetmeth.get( targetnumstmts+inlinenumstmts+2 );
      else
      inlinedStmt = ( IfStmt ) targetmeth.get( targetnumstmts+inlinenumstmts );

      //System.out.println("CHECK IN TARGET METHOD : "+inlinedStmt.toString() );

      Stmt inlinedtarget = ( Stmt ) targetmeth.get ( targetnumstmts+indexOftarget );      

      inlinedStmt.setTarget ( inlinedtarget ); 

     }



     public void caseLookupSwitchStmt ( LookupSwitchStmt s ) {

      List targets = s.getTargets();

      LookupSwitchStmt inlinedStmt = null;

      if ( syncflag )
      inlinedStmt = ( LookupSwitchStmt ) targetmeth.get ( targetnumstmts+inlinenumstmts+2 );
      else
      inlinedStmt = ( LookupSwitchStmt ) targetmeth.get ( targetnumstmts+inlinenumstmts );

      //System.out.println("CHECK IN TARGET METHOD : "+inlinedStmt.toString() );

      Iterator targetsit = targets.iterator();

      int targetsnum = 0;

      while ( targetsit.hasNext() )
      {

       Stmt target = ( Stmt ) targetsit.next();

       int indexOftarget = indexOf ( target, inlinablemeth );

       if ( syncflag )
       indexOftarget = indexOftarget + 2;

       Stmt inlinedtarget = ( Stmt ) targetmeth.get ( targetnumstmts+indexOftarget );
       
       inlinedStmt.setTarget ( targetsnum, inlinedtarget );

       targetsnum++;
       
      }

      Stmt defaulttarget = ( Stmt ) s.getDefaultTarget();

      int indexOftarget = indexOf ( defaulttarget, inlinablemeth );

       if ( syncflag )
       indexOftarget = indexOftarget + 2;

      Stmt inlineddtarget = ( Stmt ) targetmeth.get ( targetnumstmts+indexOftarget );

      inlinedStmt.setDefaultTarget ( inlineddtarget );

     }


    public void caseTableSwitchStmt ( TableSwitchStmt s ) {

     List targets = s.getTargets();

     TableSwitchStmt inlinedStmt = null;

     if ( syncflag )
     inlinedStmt = ( TableSwitchStmt ) targetmeth.get ( targetnumstmts+inlinenumstmts+2 );
     else
     inlinedStmt = ( TableSwitchStmt ) targetmeth.get ( targetnumstmts+inlinenumstmts );
	  
     //System.out.println("CHECK IN TARGET METHOD : "+inlinedStmt.toString() );

     Iterator targetsit = targets.iterator();

     int targetsnum = 0;

     while ( targetsit.hasNext() )
     {

      Stmt target = ( Stmt ) targetsit.next();

      int indexOftarget = indexOf ( target, inlinablemeth );

       if ( syncflag )
       indexOftarget = indexOftarget + 2;

      Stmt inlinedtarget = ( Stmt ) targetmeth.get ( targetnumstmts+indexOftarget );
       
      inlinedStmt.setTarget ( targetsnum, inlinedtarget );

      targetsnum++;
       
     }

     Stmt defaulttarget = ( Stmt ) s.getDefaultTarget();

     int indexOftarget = indexOf ( defaulttarget, inlinablemeth );

       if ( syncflag )
       indexOftarget = indexOftarget + 2;

     Stmt inlineddtarget = ( Stmt ) targetmeth.get ( targetnumstmts+indexOftarget );

     inlinedStmt.setDefaultTarget ( inlineddtarget );

    }

   });


   inlinenumstmts++;   

  }
 
 }






















  public void FixupMethod ( StmtList targetMethod, Iterator fixupIterator,
Iterator target, int fixupNumStmts ) {

    // System.out.println ( "REACHED FIXUP ");

   targetMethod.remove ( fixupNumStmts );      

   targetMethod.add ( fixupNumStmts, jimple.newNopStmt() );

   int numiter = 0; 


   // ADDED DEC 18
   // COMMENTED OUT targetMethod.remove ( fixupNumStmts + 1 );

   fixupNumStmts++;

   // try {

//        fixupIterator.next();

        // } catch ( java.lang.RuntimeException e ) { e.printStackTrace ( System.out ); }

   // numstmts--;


//   Stmt s = ( Stmt ) fixupIterator.next();
     Stmt s = ( Stmt ) targetMethod.get ( fixupNumStmts );
   

// while ( !( s.equals ( returnstmt ) ) )

   // System.out.println ( "INFIXUP NUMSTMTS = "+numstmts );

   // System.out.println ( "INFIXUP FIXUPNUMSTMTS = "+fixupNumStmts );

   while ( fixupNumStmts < numstmts )
   {

     // System.out.println ( s );

     // System.out.println("NUMSTMTS = "+numstmts);

     // System.out.println("FIXUPNUMSTMTS = "+fixupNumStmts);

    if ( s instanceof ReturnVoidStmt )
    {



    if ( syncflag )
    {
/*

      AssignStmt syncassgn = jimple.newAssignStmt ( syncaddress, jimple.newNextNextStmtRef() );

      targetMethod.add ( fixupNumStmts + 1, syncassgn );

      fixupIterator.next();

      numstmts++;
       
      target.next();

      fixupNumStmts++;

*/

      GotoStmt syncgoto = jimple.newGotoStmt ( syncexit );

      targetMethod.add ( fixupNumStmts + 1, syncgoto );

//      fixupIterator.next();

      numstmts++;
       
      //      target.next();
       
      fixupNumStmts++;

      syncexits.add ( syncexit );
   
      // syncexittargets.add ( s );
      
      syncexit = jimple.newExitMonitorStmt( thisvariable );

    }


     GotoStmt gs = jimple.newGotoStmt ( returnstmt );

     if ( syncflag ) 
     syncexittargets.add ( gs );


     targetMethod.add ( fixupNumStmts+1 , gs );

   //  System.out.println ( "ADDED "+targetMethod.size() );

     //System.out.println(" STMT : "+s.toString());

     
    
     if ( syncflag )
     targetMethod.remove ( fixupNumStmts - 1);
     else
     targetMethod.remove ( fixupNumStmts );



  //   System.out.println (" REMOVED "+targetMethod.size() );

     //System.out.println(" REMOVED STMT : "+rs.toString());

//     targetMethod.add ( fixupNumStmts, jimple.newGotoStmt ( returnstmt ) );

    }
    else if ( s instanceof ReturnStmt )
    {

     Value im = ( ( ReturnStmt ) s ).getReturnValue();

     // targetMethod.remove ( fixupNumStmts );
     
     Value r = null;

     if ( im instanceof Local )
     r = ( Local ) im;
     else if ( im instanceof Constant )
     r = ( Constant ) im;





    if ( syncflag )
    {

/*
      AssignStmt syncassgn = jimple.newAssignStmt ( syncaddress, jimple.newNextNextStmtRef() );

      targetMethod.add ( fixupNumStmts + 1, syncassgn );

      fixupIterator.next();

      numstmts++;
       
      target.next();

      fixupNumStmts++;

*/

      GotoStmt syncgoto = jimple.newGotoStmt ( syncexit );

      targetMethod.add ( fixupNumStmts + 1, syncgoto );


//      fixupIterator.next();

      numstmts++;
       
      // target.next();

      fixupNumStmts++;

      syncexits.add ( syncexit );
     
      // syncexittargets.add ( s );
     
      syncexit = jimple.newExitMonitorStmt( thisvariable );

    }


    // System.out.println ( "FIXUP "+targetMethod.get(fixupNumStmts) );

    // System.out.println ( "FIXUP - 1 "+targetMethod.get(fixupNumStmts - 1) );

     targetMethod.add ( fixupNumStmts+1 , jimple.newAssignStmt ( dummyreturn , r ) );

     fixupNumStmts++;


     GotoStmt gs = jimple.newGotoStmt ( returnstmt );

     if ( syncflag )
     syncexittargets.add ( gs );

     targetMethod.add ( fixupNumStmts+1 , gs );

     if ( syncflag )
     targetMethod.remove ( fixupNumStmts - 2 );
     else
     {
      targetMethod.remove ( fixupNumStmts - 1 );
     } 


     // System.out.println ( "REACHED FIXUP METHOD 2" );

     // ADDED DEC 24 
//     fixupIterator.next();

     numiter++;
     numstmts++;
     
     // target.next();

    }     

    // s = ( Stmt ) fixupIterator.next();

    s = (Stmt) targetMethod.get ( fixupNumStmts+ /* numiter */ +1); 

    fixupNumStmts++;

   }

   //     System.out.println ( "LAST "+s); 
   //   System.out.println ( " FIXED UP METHOD " );


  }



  



  public int indexOf ( Stmt s, StmtList l ) {

   Iterator it = l.iterator();

   int index = 0;

   while ( it.hasNext() )
   {
     
    try {

    Stmt next = ( Stmt ) it.next();

    if ( next.equals ( s ) ) 
    return index;
    else
    index++;

    } catch ( java.lang.RuntimeException e ) {}

   }

   return -1;

  }








  public List getCorrectList ( List l ) {

   List correctlist = new ArrayList();

   correctlist.addAll ( l );

   return correctlist;

  }







 public void removeMethods() {

 }























  public void examineMethods ( Collection callgraph, Resolver res ) {

  resolver = res;

  scm = resolver.getManager();

  clgb = resolver.getClassGraphBuilder();

  incorrectlyjimplified = clgb.getIncorrectlyJimplifiedClasses();

  //  System.out.println ( "INCORRECTLY JIMPLIFIED" );

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


 inliningImportant = false;

 Iterator iter = sortedbydepths.iterator();

 // Iterator iter = importantmethods.iterator();

  while ( iter.hasNext() )
  {

   MethodNode tempMN = (MethodNode) iter.next();

   examineMethod( tempMN );
  
  }

 
  removeMethods();

  PrintWriter out = new PrintWriter(System.out, true);

  Iterator changedit = changedclasses.iterator();

  // System.out.println ( "+++++++++ NO. OF CHANGED CLASSES "+changedclasses.size() );

   while ( changedit.hasNext () )
   {      

     try {


    ArrayList usefulmethods = new ArrayList();

    SootClass changedclass = ( SootClass ) changedit.next();

    // System.out.println ( "CHANGGED CLASS "+changedclass );

    Iterator methit = changedclass.getMethods().iterator();

    // System.out.println ( "NO OF MTHDS = "+changedclass.getMethods().size() );

/*
    Jimple jimple = Jimple.v();
   
    BodyExpr storedclass = new StoredBody ( ClassFile.v() );
  
*/
 
    while ( methit.hasNext() ) 
    {

     SootMethod changedmethod = ( SootMethod ) methit.next();

     try {

     MethodNode changednode  = ( MethodNode ) cagb.getNode ( changedmethod );

     // JimpleBody changedjb = ( JimpleBody ) ( new StoredBody( Jimple.v() ) ).resolveFor ( changedmethod );

     JimpleBody changedjb = null;

     if ( changedmethod.hasActiveBody() ) 
     changedjb = ( JimpleBody ) changedmethod.getActiveBody();
     else
     throw new java.lang.RuntimeException();

     // System.out.println ("PACKING LOCALS" );

    // Transformations.packLocals ( changedjb );

     // System.out.println ("CLEANING UP CODE" );

    gotoEliminate ( changedjb );
    Transformations.cleanupCode ( changedjb );
//    Transformations.removeUnusedLocals ( changedjb ); 

//    ChaitinAllocator.packLocals ( changedjb ); 
//    Transformations.removeUnusedLocals ( changedjb ); 

    changedmethod.setActiveBody ( new GrimpBody ( changedjb ) );  

    usefulmethods.add ( changedmethod );
     
     } catch ( java.lang.RuntimeException e ) {

//      System.out.println ( " REACHED EX 1" );

       //      System.out.println ("BUILDING NEW BODY NOW FOR "+changedmethod.getSignature() );

      //      BuildAndStoreBody changedbasb = new BuildAndStoreBody ( Jimple.v(), new StoredBody ( ClassFile.v() ) );

     JimpleBody changedjb = new JimpleBody( new ClassFileBody( changedmethod ), BuildJimpleBodyOption.USE_PACKING );

     changedmethod.setActiveBody ( new GrimpBody ( changedjb ) );

//      System.out.println ( " REACHED EX 2" );

     //      JimpleBody changedjb = ( JimpleBody ) changedbasb.resolveFor ( changedmethod );

//      System.out.println ( " REACHED EX 3" );

     } 

    }


  //  changedclass.setMethods ( usefulmethods );


    //    System.out.println ( " TRYING TO GENERATE "+ changedclass.getName() +" SIZE = "+changedclass.getMethods().size() );

    changedclass.printTo( out );
   
    changedclass.write();

    // changedclass = null;

/*

      Jimple jimple = Jimple.v();
            
      BodyExpr storedclass = new StoredBody ( ClassFile.v() );
            
      ClassFileBody clbd = ( ClassFileBody ) storedclass.resolveFor ( method );

*/


    // delay();

    //    System.out.println ( " GENERATED "+ changedclass.getName() );

        } catch ( java.lang.RuntimeException e ) { e.printStackTrace ( System.out ); }

   }





 }



 public void gotoEliminate ( JimpleBody jb ) {

  StmtList stmtlist = jb.getStmtList();

  // Iterator stmtit = stmtlist.iterator();

  int size = stmtlist.size();

  int index = 0;

  while ( index < size )
  {

   Stmt s = ( Stmt) stmtlist.get(index);

   if ( s instanceof GotoStmt )
   {

    GotoStmt gotostmt = ( GotoStmt ) s;

    Stmt target = ( Stmt ) gotostmt.getTarget(); 

    if ( ( index + 1 ) == stmtlist.indexOf ( target ) )
    {  

     stmtlist.remove ( index );
    
     stmtlist.add ( index, jimple.newNopStmt() );

    }

   }

   index++;

  }

 }











 

  public boolean satisfiesInvokeSpecialSafety ( SootMethod method ) { 

   SootClass inlinableclass = method.getDeclaringClass();

   SootClass targetclass = currmethod.getDeclaringClass();

   try {

    JimpleBody stmtListBody = Jimplifier.getJimpleBody ( method );   

    StmtList l = stmtListBody.getStmtList();

    Iterator it = l.iterator();

    while ( it.hasNext() ) 
    {

     Stmt s = ( Stmt ) it.next();

     List boxes = s.getUseAndDefBoxes();

     Iterator boxit = boxes.iterator();

     while ( boxit.hasNext() )
     {

      ValueBox vb = ( ValueBox ) boxit.next();

      Value v = vb.getValue();
      
      if ( v instanceof SpecialInvokeExpr )
      {

       SpecialInvokeExpr speinvexpr = ( SpecialInvokeExpr ) v;

       SootMethod meth = speinvexpr.getMethod();

       SootClass dec = scm.getClass ( meth.getDeclaringClass().getName() ); 

       if ( ! ( meth.getName().equals("<init>") ) )
       {

        if ( ( isStrictSuperClass ( inlinableclass, dec ) ) || ( isStrictSuperClass ( targetclass, dec ) ) )
        return false;

       }

      }

     }
     
    }

   } catch ( java.lang.RuntimeException e ) {}

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














  public Integer getCorrectModifier ( int modifiers ) {

   if ( Modifier.isPublic ( modifiers ) )
   return pub;
   else if ( Modifier.isProtected ( modifiers ) )
   return prot;
   else if ( Modifier.isPrivate ( modifiers ) )
   return priv;
   else
   return def;

  }









  public boolean changeModifiersOfAccessesFrom ( SootMethod method ) {

   boolean ref = false;

   SootClass sc = null;

   try {

   JimpleBody stmtListBody = Jimplifier.getJimpleBody ( method );   

   List localslist = stmtListBody.getLocals();

   Iterator localsit = localslist.iterator();

   while ( localsit.hasNext() )
   {
     
    Local loc = ( Local ) localsit.next();

    if ( loc.getType() instanceof RefType )
    {
     
     ref = true;

     String locName = loc.getType().toString();

     sc = scm.getClass ( locName );
     
    }  
    else if ( loc.getType() instanceof ArrayType )
    {

     Type t = ( ( ArrayType ) loc.getType() ).baseType;

     if ( t instanceof RefType )
     {
      
      ref = true;

      String locName = t.toString(); 

      sc = scm.getClass( locName );

     }

    }

    if ( ref )
    {

      if ( ( ( Integer ) classesHT.get ( sc.getName() ) ).intValue() > ( getCorrectModifier ( sc.getModifiers() ) ).intValue() )
      {

      // System.out.println ( " TRYING TO CHANGE LOCALS " );

      if ( ! allowedToChange ( sc.getName() ) )
      return false;

      sc.setModifiers ( getChangedClassModifiers ( sc.getName(), sc.getModifiers() ) );

      changedclasses.add ( sc );

      }


    }

   }


  StmtList l = stmtListBody.getStmtList();

  Iterator it = l.iterator();

   while ( it.hasNext() ) 
   {

    Stmt s = ( Stmt ) it.next();

    List boxes = s.getUseAndDefBoxes();

    Iterator boxit = boxes.iterator();

    while ( boxit.hasNext() )
    {

      ValueBox vb = ( ValueBox ) boxit.next();

      Value v = vb.getValue();

      // System.out.println ( " NEXTVALUE "+v );

      SootClass dec = null;

      SootField field = null;

      Value im = null;
      
      if ( v instanceof InstanceFieldRef )
      {

   //    System.out.println ( "INSTANCE FIELD 1 "+v );

       InstanceFieldRef ifr = ( InstanceFieldRef ) v;

       field = ifr.getField();

       dec = scm.getClass ( field.getDeclaringClass().getName() ); 

       field = dec.getField ( field.getName() );


       if ( ( ( Integer ) classesHT.get ( dec.getName() ) ).intValue() > ( getCorrectModifier ( dec.getModifiers() ) ).intValue() )
      {

//       if ( incorrectlyjimplified.contains ( dec.getName() ) )

       System.out.println ( " TRYING TO CHANGE CLASS MOD FOR "+dec.getName() );

       if ( ! allowedToChange ( dec.getName() ) )
       return false;

      dec.setModifiers ( getChangedClassModifiers ( dec.getName(), dec.getModifiers() ) );
       changedclasses.add ( dec );

      }


//      System.out.println ( " MIDWAY " );
  
       if ( ( ( Integer ) fieldsHT.get ( field.getSignature() ) ).intValue() > ( getCorrectModifier ( field.getModifiers() ) ).intValue() )
       {


//        if ( incorrectlyjimplified.contains ( dec.getName() ) )


         //      System.out.println ( " TRYING TO CHANGE MOD OF FIELD "+field.getSignature() ); 

        if ( ! allowedToChange ( dec.getName() ) )
        return false;

 //     System.out.println ( "EARLIER "+Modifier.toString ( field.getModifiers() ) );

 //     System.out.println ( ( ( Integer ) fieldsHT.get ( field.getSignature() ) ).intValue() );

      field.setModifiers ( getChangedFieldModifiers ( field.getSignature(), field.getModifiers() ) );

       changedclasses.add ( dec );

//       System.out.println( " NOTE : CHANGED MODIFIER FOR "+field.getSignature() );  

//       System.out.println ( "LATER "+Modifier.toString ( field.getModifiers() ) );

       }


      }
      else if ( v instanceof StaticFieldRef ) 
      {

  //     System.out.println ( " STATIC FIELD 1 "+v );

       StaticFieldRef sfr = ( StaticFieldRef ) v;

       field = sfr.getField();
      
       dec = scm.getClass ( field.getDeclaringClass().getName() ); 

       field = dec.getField ( field.getName() );

      if ( ( ( Integer ) classesHT.get ( dec.getName() ) ).intValue() > ( getCorrectModifier ( dec.getModifiers() ) ).intValue() )
      {

       // if ( incorrectlyjimplified.contains ( dec.getName() ) )

       if ( ! allowedToChange ( dec.getName() ) )
       return false;

      dec.setModifiers ( getChangedClassModifiers ( dec.getName(), dec.getModifiers() ) );

      changedclasses.add ( dec );
      
      } 
  
       if ( ( ( Integer ) fieldsHT.get ( field.getSignature() ) ).intValue() > ( getCorrectModifier ( field.getModifiers() ) ).intValue() )
      {

     //  if ( incorrectlyjimplified.contains ( dec.getName() ) )

       if ( ! allowedToChange ( dec.getName() ) )
       return false;

      field.setModifiers ( getChangedFieldModifiers ( field.getSignature(), field.getModifiers() ) );

      changedclasses.add( dec );

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
       
       }  
       else if ( t instanceof ArrayType )
       {

        Type ty = ( ( ArrayType ) t ).baseType;

        if ( ty instanceof RefType )
        {
     
         ref = true;

         String locName = ty.toString(); 

         sc = scm.getClass ( locName );

        }

       }

       if ( ref )
       {

        if ( ( ( Integer ) classesHT.get ( sc.getName() ) ).intValue() > ( getCorrectModifier ( sc.getModifiers() ) ).intValue() )
	{

 //        if ( incorrectlyjimplified.contains ( sc.getName() ) )

         if ( ! allowedToChange ( sc.getName() ) )
         return false;

         sc.setModifiers ( getChangedClassModifiers ( sc.getName(), sc.getModifiers() ) );
        
         changedclasses.add ( sc );

        }

       }

      }
      else if ( v instanceof InstanceOfExpr )
      {

       ref = false;

       InstanceOfExpr ce = ( InstanceOfExpr ) v; 
       
       Type t = ce.getCheckType();   

       if ( t instanceof RefType )
       {
     
        ref = true;

        String locName = t.toString();

        sc = scm.getClass ( locName );
       
       }  
       else if ( t instanceof ArrayType )
       {

        Type ty = ( ( ArrayType ) t ).baseType;

        if ( ty instanceof RefType )
        {
     
         ref = true;

         String locName = ty.toString(); 

         sc = scm.getClass ( locName );

        }

       }

       if ( ref )
       {

        if ( ( ( Integer ) classesHT.get ( sc.getName() ) ).intValue() > ( getCorrectModifier ( sc.getModifiers() ) ).intValue() )
        {

      //   if ( incorrectlyjimplified.contains ( sc.getName() ) )


         if ( ! allowedToChange ( sc.getName() ) )
         return false;
       

        sc.setModifiers ( getChangedClassModifiers ( sc.getName(), sc.getModifiers() ) );

        changedclasses.add ( sc );

        }

       }

      }
      else if ( v instanceof NewExpr )
      {

       NewExpr newexpr = ( NewExpr ) v; 
       
       RefType t = newexpr.getBaseType();   

       String locName = t.toString();

       sc = scm.getClass ( locName );
     
       if ( ( ( Integer ) classesHT.get ( sc.getName() ) ).intValue() > ( getCorrectModifier ( sc.getModifiers() ) ).intValue() )
       {

        // if ( incorrectlyjimplified.contains ( sc.getName() ) )

         if ( ! allowedToChange ( sc.getName() ) )
         return false;

       sc.setModifiers ( getChangedClassModifiers ( sc.getName(), sc.getModifiers() ) );

       changedclasses.add ( sc );
        
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
     
       }  
       else if ( t instanceof ArrayType )
       {

        Type ty = ( ( ArrayType ) t ).baseType;

        if ( ty instanceof RefType )
        {

         ref = true;
      
         String locName = ty.toString(); 

         sc = scm.getClass( locName );

        }

       }

       if ( ref )
       {

        if ( ( ( Integer ) classesHT.get ( sc.getName() ) ).intValue() > ( getCorrectModifier ( sc.getModifiers() ) ).intValue() )
        {

   //     if ( incorrectlyjimplified.contains ( sc.getName() ) )

        if ( ! allowedToChange ( sc.getName() ) )
        return false;

        sc.setModifiers ( getChangedClassModifiers ( sc.getName(), sc.getModifiers() ) );

        changedclasses.add ( sc );

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

       }  
       else if ( t instanceof ArrayType )
       {

        Type ty = ( ( ArrayType ) t ).baseType;

        if ( ty instanceof RefType )
        {
      
         ref = true;

         String locName = ty.toString(); 

         sc = scm.getClass ( locName );

        }

       }

       if ( ref )
       {

        if ( ( ( Integer ) classesHT.get ( sc.getName() ) ).intValue() > ( getCorrectModifier ( sc.getModifiers() ) ).intValue() )
	{

        // if ( incorrectlyjimplified.contains ( sc.getName() ) )

        if ( ! allowedToChange ( sc.getName() ) )
        return false;

         sc.setModifiers ( getChangedClassModifiers ( sc.getName(), sc.getModifiers() ) );

         changedclasses.add ( sc );

        }

       }

      }
      else if ( v instanceof InvokeExpr )
      {

       InvokeExpr stinvexpr = ( InvokeExpr ) v;
   
       int argcount = stinvexpr.getArgCount();
    
       int counter = 0;

       while ( counter < argcount )
       {

        ref = false;

        if ( stinvexpr.getMethod().getParameterType( counter ) instanceof RefType )
        {
       
         ref = true;

         String argtype = stinvexpr.getMethod().getParameterType( counter ).toString();
       
         sc = scm.getClass ( argtype );

        }
        else if ( stinvexpr.getMethod().getParameterType( counter ) instanceof ArrayType )
        {
       
         Type t = ( ( ArrayType ) stinvexpr.getMethod().getParameterType( counter ) ).baseType;
       
         if ( t instanceof RefType )
         { 

          ref = true;

          String argtype = t.toString();

          sc = scm.getClass ( argtype );
   
         }

       }

       if ( ref )
       {

        if ( ( ( Integer ) classesHT.get ( sc.getName() ) ).intValue() > ( getCorrectModifier ( sc.getModifiers() ) ).intValue() )
	{

 //       if ( incorrectlyjimplified.contains ( sc.getName() ) )

         if ( ! allowedToChange ( sc.getName() ) )
         return false;      

        sc.setModifiers ( getChangedClassModifiers ( sc.getName(), sc.getModifiers() ) );

        changedclasses.add ( sc );

        }

        }         

       counter++;

      }

       SootMethod meth = stinvexpr.getMethod();

       dec = scm.getClass ( meth.getDeclaringClass().getName() ); 

       meth = dec.getMethod ( meth.getName(), meth.getParameterTypes() );

       if ( ( ( Integer ) classesHT.get ( dec.getName() ) ).intValue() > ( getCorrectModifier ( dec.getModifiers() ) ).intValue() )
       {

     //   if ( incorrectlyjimplified.contains ( dec.getName() ) )

        if ( ! allowedToChange ( dec.getName() ) )
        return false;

       dec.setModifiers ( getChangedClassModifiers ( dec.getName(), dec.getModifiers() ) );

       changedclasses.add ( dec );

       }

       if ( ( ( Integer ) methodsHT.get ( meth.getSignature() ) ).intValue() > ( getCorrectModifier ( meth.getModifiers() ) ).intValue() )
       {

    //    if ( incorrectlyjimplified.contains ( dec.getName() ) )

        if ( ! allowedToChange ( dec.getName() ) )
        return false;

       int newmodifiers = getChangedMethodModifiers ( meth.getSignature(), meth.getModifiers() );

       meth.setModifiers ( newmodifiers );


       try {

       adjustSubMethods ( meth, newmodifiers );

       } catch ( java.lang.RuntimeException e ) {

        return false;

       }

       changedclasses.add ( dec );

       }

     }

   }

  }

   } catch ( java.lang.RuntimeException e ) { /* System.out.println ( " IN INLINER Jimple cannot handle : "+method.getSignature() ); */ } 
 
 return true;

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

  // SootClass cn = scm.getClass ( cname );

  // result = result && ( ! Modifier.isAbstract( cn.getModifiers() ) );

  // System.out.println ( "ALLOWED 2 "+ cname+ " "+result );


 
  if ( ! includeLibraries )
  result = result && ( ! isLibraryNode ( cname ) );

  if ( ! ( classesToProcess == null ) )
  result = result && ( classesToProcess.contains ( cname ) ); 

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











  public void adjustSubMethods ( SootMethod m, int newmodifiers ) {

   ClassNode cn = clgb.getNode ( m.getDeclaringClass().getName() );

   Set subclassnodes = clgb.getAllSubClassesOf ( cn );

   Iterator subclassnodesit = subclassnodes.iterator();
     
   while ( subclassnodesit.hasNext() )
   {
   
    ClassNode subcn = ( ClassNode ) subclassnodesit.next();

    SootClass subclass = scm.getClass ( subcn.getSootClass().getName() );

    if ( subclass.declaresMethod ( m.getName(), m.getParameterTypes() ) )
    {

     SootMethod submeth = subclass.getMethod ( m.getName(), m.getParameterTypes() );     

     if ( getCorrectModifier ( submeth.getModifiers() ).intValue() < getCorrectModifier ( newmodifiers ).intValue()  )
     {

      if ( ! allowedToChange ( subclass.getName() ) )
      throw new ca.mcgill.sable.soot.jimple.toolkit.invoke.NotAllowedToChangeException();

      submeth.setModifiers ( newmodifiers );

      changedclasses.add ( subclass );

     }

    }

   }

  }

















  public int getChangedClassModifiers ( String s, int modifiers ) {

   int changedmodifiers = modifiers;

   changedmodifiers = changedmodifiers & 0xFFFB;
   
   changedmodifiers = changedmodifiers & 0xFFFD;
   
   changedmodifiers = changedmodifiers & 0xFFFE;
   
   if ( ( ( Integer ) classesHT.get ( s ) ).intValue() == 3 )
   changedmodifiers = changedmodifiers | 0x0001;

   return changedmodifiers;

  }









 
 public int getChangedMethodModifiers ( String s, int modifiers ) {

   int changedmodifiers = modifiers;

   changedmodifiers = changedmodifiers & 0xFFFB;
   
   changedmodifiers = changedmodifiers & 0xFFFD;
   
   changedmodifiers = changedmodifiers & 0xFFFE;
   
   if ( ( ( Integer ) methodsHT.get ( s ) ).intValue() == 3 )
   changedmodifiers = changedmodifiers | 0x0001;
   else if ( ( ( Integer ) methodsHT.get ( s ) ).intValue() == 2 )
   changedmodifiers = changedmodifiers | 0x0004;
   else if ( ( ( Integer ) methodsHT.get ( s ) ).intValue() == 0 )
   changedmodifiers = changedmodifiers | 0x0002;

   return changedmodifiers;

 }









  public int getChangedFieldModifiers ( String s, int modifiers ) {

   int changedmodifiers = modifiers;

   changedmodifiers = changedmodifiers & 0xFFFB;
   
   changedmodifiers = changedmodifiers & 0xFFFD;
   
   changedmodifiers = changedmodifiers & 0xFFFE;
   
   if ( ( ( Integer ) fieldsHT.get ( s ) ).intValue() == 3 )
   changedmodifiers = changedmodifiers | 0x0001;
   else if ( ( ( Integer ) fieldsHT.get ( s ) ).intValue() == 2 )
   changedmodifiers = changedmodifiers | 0x0004;
   else if ( ( ( Integer ) fieldsHT.get ( s ) ).intValue() == 0 )
   changedmodifiers = changedmodifiers | 0x0002;

   return changedmodifiers;

 }











 public boolean satisfiesResolverCriteria ( SootMethod method ) {

   // System.out.println ( "RESOLVING METHOD "+method.getSignature() );

   boolean ref = false;

   boolean samepackage = false;

   boolean sameclass = false;

   boolean sameprotected = false;

   SootClass sc = null;

   classesHT.clear();

   methodsHT.clear();

   fieldsHT.clear();

   try {


//   Iterator iter = scm.getClasses().iterator();

//   while ( iter.hasNext() )
//   scm.removeClass( ((SootClass) iter.next()) );

   SootClass currclass = currmethod.getDeclaringClass(); 

   String currname = currclass.getName();

   String currpackagename = getPackageName ( currname );

   /*      

   BuildAndStoreBody buildAndStoreBody = new BuildAndStoreBody(Jimple.v() , new StoredBody(ClassFile.v()));

   JimpleBody stmtListBody = (JimpleBody) buildAndStoreBody.resolveFor(method);

   */


 //  JimpleBody stmtListBody = ( JimpleBody) new StoredBody(Jimple.v()).resolveFor(method);

   JimpleBody stmtListBody = Jimplifier.getJimpleBody ( method );   

   List localslist = stmtListBody.getLocals();

   Iterator localsit = localslist.iterator();

   int localnum = 0;

   while ( localsit.hasNext() )
   {
     
    // System.out.println ( "EXAMINING LOCAL NO : "+localnum++ );

    ref = false;

    samepackage = false;

    Local loc = ( Local ) localsit.next();

    if ( loc.getType() instanceof RefType )
    {
     
     ref = true;

     String locName = loc.getType().toString();

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

      sc = scm.getClass( locName );

      samepackage = isSamePackage ( getPackageName ( locName ), currpackagename );

     }

    }

    if ( ref )
    {

     if ( ( ( Integer ) classesHT.get ( sc.getName() ) ) == null )  
     classesHT.put ( sc.getName(), getCorrectModifier ( sc.getModifiers() ) );

     if ( ! ( samepackage ) ) 
     {  

      classesHT.put ( sc.getName(), pub ); 

      if ( ( ( Integer ) resolverclassesHT.get ( sc.getName() ) ).intValue() < 3 )
      return false;

     }   

    }


   }
    

   StmtList l = stmtListBody.getStmtList();

   


  Iterator it = l.iterator();

   int boxnum = 0;

   // System.out.println ( "SIZED = "+l.size() );

   while ( it.hasNext() ) 
   {

    // System.out.println ( "EXAMINING STMT : "+s );

    Stmt s = ( Stmt ) it.next();

    // System.out.println ( "EXAMINING STMT : "+s );

    List boxes = s.getUseAndDefBoxes();

    Iterator boxit = boxes.iterator();

    while ( boxit.hasNext() )
    {

      sameclass = false;

      samepackage = false;

      sameprotected = false;

      ValueBox vb = ( ValueBox ) boxit.next();

      Value v = vb.getValue();

      // System.out.println ( "SUITABLE "+v );

      SootClass dec = null;

      SootField field = null;

      Value im = null;
      
      if ( v instanceof InstanceFieldRef )
      {

       // System.out.println ( "INSTANCE FIELD" );

       InstanceFieldRef ifr = ( InstanceFieldRef ) v;

       im = ifr.getBase();

       String basetype = im.getType().toString();

       field = ifr.getField();

       dec = field.getDeclaringClass(); 

       if ( currclass.getName().equals ( dec.getName() ) )
       sameclass = true;

       samepackage = isSamePackage ( getPackageName ( dec.getName() ), currpackagename );

       if ( ( ( Integer ) classesHT.get ( dec.getName() ) ) == null )  
       classesHT.put ( dec.getName(), getCorrectModifier ( dec.getModifiers() ) );


       if ( ! ( samepackage ) ) 
       {  

        classesHT.put ( dec.getName(), pub );

        if ( ( ( Integer ) resolverclassesHT.get ( dec.getName() ) ).intValue() < 3 )
        return false;

       } 


       if ( im.getType() instanceof ArrayType )
       sameprotected = currclass.getName().equals ( "java.lang.Object" );
       else
       sameprotected = isSameProtected ( dec, currclass, basetype, currpackagename ); 

       if ( ( ( ( Integer ) fieldsHT.get ( field.getSignature() ) ) == null ) )
       fieldsHT.put ( field.getSignature(), getCorrectModifier ( field.getModifiers() ) );


       if ( ! ( sameprotected ) ) 
       {

        fieldsHT.put ( field.getSignature(), pub );

        if ( ( ( Integer ) resolverfieldsHT.get ( field.getSignature() ) ).intValue() < 3 )
        return false;

       } 
       else if ( ( ! ( samepackage ) ) && sameprotected )
       {

        if ( ( ( Integer ) fieldsHT.get ( field.getSignature() ) ).intValue() < 2 )
        fieldsHT.put ( field.getSignature(), prot );

        if ( ( ( Integer ) resolverfieldsHT.get ( field.getSignature() ) ).intValue() < 2 )
        return false;

       }
       else if ( ( ! ( sameclass ) ) && samepackage ) 
       {

        if ( ( ( Integer ) fieldsHT.get ( field.getSignature() ) ).intValue() < 1 )
        fieldsHT.put ( field.getSignature(), def );

        if ( ( ( Integer ) resolverfieldsHT.get ( field.getSignature() ) ).intValue() < 1 )
        return false;

       }

      }
      else if ( v instanceof StaticFieldRef ) 
      {

       // System.out.println ( "STATIC FIELD" );

       StaticFieldRef sfr = ( StaticFieldRef ) v;

       field = sfr.getField();
      
       dec = field.getDeclaringClass();

      
       if ( currclass.getName().equals ( dec.getName() ) )
       sameclass = true;
       
       samepackage = isSamePackage ( getPackageName( dec.getName() ), currpackagename );

       if ( ( ( Integer ) classesHT.get ( dec.getName() ) ) == null )  
       classesHT.put ( dec.getName(), getCorrectModifier ( dec.getModifiers() ) );
     
       if ( ! ( samepackage ) )
       {
      
        classesHT.put( dec.getName(), pub );

        if ( ( ( Integer ) resolverclassesHT.get ( dec.getName() ) ).intValue() < 3 )
        return false;

       }


       sameprotected = isSameStaticProtected ( dec, currclass, currpackagename );

       if ( ( ( ( Integer ) fieldsHT.get ( field.getSignature() ) ) == null ) )
       fieldsHT.put ( field.getSignature(), getCorrectModifier ( field.getModifiers() ) );

       if ( ! ( sameprotected ) ) 
       {

        fieldsHT.put ( field.getSignature(), pub );

        if ( ( ( Integer ) resolverfieldsHT.get ( field.getSignature() ) ).intValue() < 3 )
        return false;

       }
       else if ( ( ! ( samepackage ) ) && sameprotected )
       {

        if ( ( ( Integer ) fieldsHT.get ( field.getSignature() ) ).intValue() < 2 )
        fieldsHT.put ( field.getSignature(), prot );
        
        if ( ( ( Integer ) resolverfieldsHT.get ( field.getSignature() ) ).intValue() < 2 )
        return false;

       }
       else if ( ( ! ( sameclass ) ) && samepackage ) 
       {

        if ( ( ( Integer ) fieldsHT.get ( field.getSignature() ) ).intValue() < 1 )
        fieldsHT.put ( field.getSignature(), def );

        if ( ( ( Integer ) resolverfieldsHT.get ( field.getSignature() ) ).intValue() < 1 )
        return false;

       }   

      }
      else if ( v instanceof CastExpr )
      {

       // System.out.println ( " CAST " );

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
        classesHT.put ( sc.getName(), getCorrectModifier ( sc.getModifiers() ) );

        if ( ! ( samepackage ) ) 
        {  

         classesHT.put ( sc.getName(), pub );

         if ( ( ( Integer ) resolverclassesHT.get ( sc.getName() ) ).intValue() < 3 )
         return false;

        }   

       }

      }
      else if ( v instanceof InstanceOfExpr )
      {

       // System.out.println ( " INSTANCEOF " );

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
        classesHT.put ( sc.getName(), getCorrectModifier ( sc.getModifiers() ) );

        if ( ! ( samepackage ) ) 
        {  
 
         classesHT.put ( sc.getName(), pub );

         if ( ( ( Integer ) resolverclassesHT.get ( sc.getName() ) ).intValue() < 3 )
         return false;

        }   

       }

      }
      else if ( v instanceof NewExpr )
      {

       // System.out.println ( "NEW" );

       NewExpr newexpr = ( NewExpr ) v; 
       
       RefType t = newexpr.getBaseType();   

       String locName = t.toString();

       sc = scm.getClass ( locName );
     
       samepackage = isSamePackage ( getPackageName ( locName ), currpackagename );
       
       if ( ( ( Integer ) classesHT.get ( sc.getName() ) ) == null )  
       classesHT.put ( sc.getName(), getCorrectModifier ( sc.getModifiers() ) );
       
       if ( ! ( samepackage ) )
       {  

        classesHT.put ( sc.getName(), pub );

        if ( ( ( Integer ) resolverclassesHT.get ( sc.getName() ) ).intValue() < 3 )
        return false;

       }   

      }
      else if ( v instanceof NewArrayExpr )
      {

//       System.out.println ( " NEWARRAY " );

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
        classesHT.put ( sc.getName(), getCorrectModifier ( sc.getModifiers() ) );

        if ( ! ( samepackage ) )
        {  
 
         classesHT.put ( sc.getName(), pub );

         if ( ( ( Integer ) resolverclassesHT.get ( sc.getName() ) ).intValue() < 3 )
         return false;

        }   

       }

      }
      else if ( v instanceof NewMultiArrayExpr )
      {

  //      System.out.println ( "NEWMULTI" );

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
        classesHT.put ( sc.getName(), getCorrectModifier ( sc.getModifiers() ) );

        if ( ! ( samepackage ) )
        {  
 
         classesHT.put ( sc.getName(), pub );

         if ( ( ( Integer ) resolverclassesHT.get ( sc.getName() ) ).intValue() < 3 )
         return false;

        }   

       }

      }
      else if ( v instanceof StaticInvokeExpr )
      {

     //  System.out.println ( "STATICINVOKE" );

       StaticInvokeExpr stinvexpr = ( StaticInvokeExpr ) v;
   
       int argcount = stinvexpr.getArgCount();
    
       int counter = 0;

       // System.out.println (" REACHED COUNTER "+argcount );

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
        classesHT.put ( sc.getName(), getCorrectModifier ( sc.getModifiers() ) );

        if ( ! ( samepackage ) )
        {  
 
         classesHT.put ( sc.getName(), pub );

         if ( ( ( Integer ) resolverclassesHT.get ( sc.getName() ) ).intValue() < 3 )
         return false;

        }         

       }

       counter++;

      }

       // System.out.println ( "EXITED COUNTER" );

       samepackage = false;

       SootMethod meth = stinvexpr.getMethod();

       dec = meth.getDeclaringClass(); 

       if ( currclass.getName().equals ( dec.getName() ) )
       sameclass = true;

       samepackage = isSamePackage ( getPackageName ( dec.getName() ), currpackagename );

       if ( ( ( Integer ) methodsHT.get ( meth.getSignature() ) ) == null )  
       methodsHT.put ( meth.getSignature(), getCorrectModifier ( meth.getModifiers() ) );

       if ( ( ( Integer ) classesHT.get ( dec.getName() ) ) == null )  
       classesHT.put ( dec.getName(), getCorrectModifier ( dec.getModifiers() ) );


       if ( ! ( samepackage ) )
       {
      
        classesHT.put ( dec.getName(), pub );

        if ( ( ( Integer ) resolverclassesHT.get ( dec.getName() ) ).intValue() < 3 )
        return false;

       }


       sameprotected = isSameStaticProtected ( dec, currclass, currpackagename ); 


       if ( ! ( sameprotected ) ) 
       {

        methodsHT.put ( meth.getSignature(), pub );

        if ( ( ( Integer ) resolvermethodsHT.get ( meth.getSignature() ) ).intValue() < 3 )
        return false;

       }
       else if ( ( ! ( samepackage ) ) && sameprotected )
       {

        if ( ( ( Integer ) methodsHT.get ( meth.getSignature() ) ).intValue() < 2 )
        methodsHT.put ( meth.getSignature(), prot );

        if ( ( ( Integer ) resolvermethodsHT.get ( meth.getSignature() ) ).intValue() < 2 )
        return false;

       }
       else if ( ( ! ( sameclass ) ) && samepackage ) 
       {

        if ( ( ( Integer ) methodsHT.get ( meth.getSignature() ) ).intValue() < 1 )
        methodsHT.put ( meth.getSignature(), def );

        if ( ( ( Integer ) resolvermethodsHT.get ( meth.getSignature() ) ).intValue() < 1 )
        return false;

       }

      }
      else if ( v instanceof InvokeExpr )
      {

       // System.out.println ( " NORMAL INVOKE " );

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
        classesHT.put ( sc.getName(), getCorrectModifier( sc.getModifiers() ) );

        if ( ! ( samepackage ) ) 
        {  
 
         classesHT.put ( sc.getName(), pub );

         if ( ( ( Integer ) resolverclassesHT.get ( sc.getName() ) ).intValue() < 3 )
         return false;

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

       dec = meth.getDeclaringClass(); 

       if ( currclass.getName().equals ( dec.getName() ) )
       sameclass = true;

       samepackage = isSamePackage ( getPackageName ( dec.getName() ), currpackagename );

       if ( ( ( Integer ) methodsHT.get ( meth.getSignature() ) ) == null )  
       methodsHT.put ( meth.getSignature(), getCorrectModifier ( meth.getModifiers() ) );


       if ( ( ( Integer ) classesHT.get ( dec.getName() ) ) == null )  
       classesHT.put ( dec.getName(), getCorrectModifier ( dec.getModifiers() ) );

       if ( ! ( samepackage ) )
       {
      
        classesHT.put ( dec.getName(), pub );

        if ( ( ( Integer ) resolverclassesHT.get ( dec.getName() ) ).intValue() < 3 )
        return false;

       }


       if ( im.getType() instanceof ArrayType )
       sameprotected = currclass.getName().equals ( "java.lang.Object" );
       else
       sameprotected = isSameProtected ( dec, currclass, basetype, currpackagename ); 


       if ( ! ( sameprotected ) ) 
       {

        methodsHT.put ( meth.getSignature(), pub );

        if ( ( ( Integer ) resolvermethodsHT.get ( meth.getSignature() ) ).intValue() < 3 )
        return false;

       }
       else if ( ( ! ( samepackage ) ) && sameprotected )
       {

        if ( ( ( Integer ) methodsHT.get ( meth.getSignature() ) ).intValue() < 2 )
        methodsHT.put ( meth.getSignature(), prot );
        
        if ( ( ( Integer ) resolvermethodsHT.get ( meth.getSignature() ) ).intValue() < 2 )
        return false;

       }
       else if ( ( ! ( sameclass ) ) && samepackage ) 
       {

        if ( ( ( Integer ) methodsHT.get ( meth.getSignature() ) ).intValue() < 1 )
        methodsHT.put ( meth.getSignature(), def );

        if ( ( ( Integer ) resolvermethodsHT.get ( meth.getSignature() ) ).intValue() < 1 )
        return false;

       }
 
     }

    }

   }

   } catch ( java.lang.RuntimeException e ) { /* System.out.println ( " IN INLINER "+method.getSignature() ); */ }

 //  System.out.println ( " CHANGE MODIFIERS ? " );

   return changeModifiersOfAccessesFrom ( method ); 

   // return true;

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















}
































































































































