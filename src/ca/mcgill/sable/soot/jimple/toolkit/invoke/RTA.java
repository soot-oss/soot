// package ca.mcgill.sable.soot.virtualCalls;

package ca.mcgill.sable.soot.jimple.toolkit.invoke;

import java.io.*;
import ca.mcgill.sable.soot.jimple.*;
import ca.mcgill.sable.util.*;
import java.util.*;
import ca.mcgill.sable.soot.baf.*; 
import ca.mcgill.sable.soot.*;
// import ca.mcgill.sable.soot.sideEffect.*;
import java.util.BitSet;

public class RTA {
	
	Scene cm; /*  = Scene.v(); */

	private ArrayList newExprs = new ArrayList();

  //      private ArrayList instancetypes = new ArrayList();

	private Map instancetypesHT = new HashMap();

	private Map instancelocHT = new HashMap();

	private Map openmethodsHT = new HashMap();

  //      private Map clinitHT = new HashMap();

	private SootClass currclass;

	private SootMethod currmethod;

	private ClassGraphBuilder clgb;

	private CallGraphBuilder cagb;

	private String className;

	private boolean isStatic;

	private boolean isSpecial;

	static Timer callgbTimer = new Timer();

	static long callgbMem;

	static Timer RTATimer = new Timer();
       
	static long RTAMem;

	static Timer RTAedgeRemovedTimer = new Timer();

	static long RTAedgeRemovedMem;

	private boolean intflag;

	private boolean virflag;

//        private Iterator iteR;

	private int totedges = 0;

	private int totadd = 0;

	private int totactadd = 0;

	private int intactedges = 0;

	private Map csHT = new HashMap();

	private int reduced[] = new int[20000];

	public int[] getReduced() { return reduced; }

	private int alive = 0;

	private int totalin = 0;

        private BitSet instanceSet = new BitSet();

        private HashMap bitMap = new HashMap();

        private HashMap inversebitMap = new HashMap();




  public CallGraphBuilder getCallGraphBuilder(){
  return cagb;
  }




 
  public ClassGraphBuilder getClassGraphBuilder(){
  return clgb;
  }





  public void constructBitMap( Map allclassHT ) {

   Iterator classesIt = allclassHT.values().iterator();
 
   int i = 0;

   while ( classesIt.hasNext() )
   {

    String classname = ( ( ClassNode ) classesIt.next() ).getName();

    Integer ii = new Integer( i );

    bitMap.put ( classname, ii );

    inversebitMap.put ( ii, classname );

    i++;

   }

  }










  public void RTAgc1( Map callgraph ) { 

   /* THIS METHOD PERFORMS GARBAGE COLLECTION OF METHODNODES THAT HAVE 
      BEEN JIMPLIFIED AND STORED BUT NOT BEING USED IN THE CALL GRAPH
    */

   Map allmethods = Jimplifier.getMethodNameToJimpleBody();

   List unusedmethods = new ArrayList(); 

   Iterator methodsit = allmethods.keySet().iterator();

   // COLLECTS KEYS FOR ALL UNUSED METHODS
   
   while ( methodsit.hasNext() )
   {
   
    String methodkey = ( String ) methodsit.next();
  
    if ( ( (MethodNode) callgraph.get(methodkey) ) == null )
    unusedmethods.add(methodkey);

   }

   Iterator unusedit = unusedmethods.iterator();

   int i = 0; 

   // REMOVE UNUSED METHODS FROM THE JIMPLIFIED METHODS HT AND GARBAGE COLLECT

   while ( unusedit.hasNext() )
   {

    String unusedname = ( String ) unusedit.next();

    Jimplifier.getMethodNameToJimpleBody().remove( unusedname );

    if ( ( i % 50 ) == 0 )    
    {

     System.out.println("i = "+i);

     System.gc();

    }

    i++;

   }
    
  }


  public ArrayList getNewExprs ( CHA cha ) {

  /* CALLED BY GETINSTANCETYPES(), THIS METHOD DOES THE FOLLOWING :

     1. BUILDS THE CALL GRAPH ( INCLUDING START() AND RUN() METHODS AS 
	POTENTIAL ENTRY POINTS TO ACCOUNT FOR MULTITHREADING ) 

     2. GARBAGE COLLECTS BY CALLING RTAGC1()

     3. RETURNS A VECTORLIST CONTAINING ALL THE NEW, NEWARRAY, NEWMULTIARRAY 
	EXPRESSIONS IN THE METHODS IN THE CALL GRAPH

  */

  List paramList = new ArrayList();

  Collection callGraph = null;

  clgb = cha.getClassGraphBuilder();
  
  cm = clgb.getManager();

  cagb = cha.getCallGraphBuilder();

  callGraph = cagb.getCallGraph();

    //new CallGraphBuilder( clgb );

  /*

  callgbTimer.start();  
  
  callgbTimer.end();
  callgbMem = Runtime.getRuntime().totalMemory() -
Runtime.getRuntime().freeMemory();

  System.out.println("TIME FOR CALL GRAPH : "+callgbTimer.getTime());
  System.out.println("SPACE FOR CALL GRAPH : "+callgbMem);

  
  
  System.out.println("TOTAL NUMBER OF JIMPLE STATEMENTS : "+Jimplifier.numstmts);
  System.out.println("TOTAL NUMBER OF JIMPLE STATEMENTS IN BENCHMARK : "+Jimplifier.benchnumstmts);

  System.out.println("TOTAL NUMBER OF JIMPLE STATEMENTS ANALYSED : "+Jimplifier.cgnumstmts);
  System.out.println("TOTAL NUMBER OF JIMPLE STATEMENTS ANALYSED IN BENCHMARK : "+Jimplifier.cgbenchnumstmts);

  callGraph = cagb.getCallGraph();

  System.out.println("TOTAL NUMBER OF METHODS : "+Jimplifier.totnummethods);
  System.out.println("TOTAL NUMBER OF NATIVE METHODS : "+Jimplifier.totnativenummethods);
  System.out.println("TOTAL NUMBER OF ABSTRACT METHODS : "+Jimplifier.totabstractnummethods);
  
  System.out.println("TOTAL NUMBER OF METHODS IN BENCHMARK : "+Jimplifier.totnumbenchmethods);
  System.out.println("TOTAL NUMBER OF NATIVE METHODS IN BENCHMARK : "+Jimplifier.totnativebenchnummethods);
  System.out.println("TOTAL NUMBER OF ABSTRACT METHODS IN BENCHMARK : "+Jimplifier.totabstractbenchnummethods);

  */

  // GARBAGE COLLECTION 

  // RTAgc1( callGraph );

  RTATimer.start();

  // openmethodsHT = cagb.getOpenMethodsHT();

  Iterator iter = callGraph.iterator();

  while ( iter.hasNext() )
  {

    try {

     MethodNode tempMN = (MethodNode) iter.next();

     currmethod = tempMN.getMethod();

     currclass = currmethod.getDeclaringClass();

     try {

      JimpleBody listBody = Jimplifier.getJimpleBody( currmethod );

      Iterator stmtIter = listBody.getStmtList().iterator();

      while ( stmtIter.hasNext() )
      {   

       try {

	Stmt stmt = (Stmt) stmtIter.next();

	stmt.apply( new AbstractStmtSwitch() {

	 public void caseAssignStmt(AssignStmt s){

	  

	  if ( s.getRightOp() instanceof NewExpr )
	  {

	   // NEW EXPRESSIONS ADDED HERE

	   newExprs.add( s.getRightOp() );
       
	   instancelocHT.put(s.getRightOp().toString(),currclass.getName()+" "+currmethod.getName());
       
	  }
	  else if ( s.getRightOp() instanceof NewArrayExpr )
	  {

	   // NEW ARRAY EXPRESSIONS ADDED HERE

	   newExprs.add( s.getRightOp() );

	   instancelocHT.put(s.getRightOp().toString(),currclass.getName()+" "+currmethod.getName());

	   }
	  else if( s.getRightOp() instanceof NewMultiArrayExpr )
	  {

	   // NEW MULTI ARRAY EXPRESSIONS ADDED HERE

	   newExprs.add( s.getRightOp() );
 
	   instancelocHT.put(s.getRightOp().toString(),currclass.getName()+" "+currmethod.getName());

	  }

	 } // CASE ASSIGN STMT
      
	} );

       } catch ( java.lang.RuntimeException e ){

	 System.err.println("\t------- NullPtr ERROR IN RTA : PROBLEM STMT IGNORED IN "+currclass.getName()+" "+currmethod.getName());

       }

      } // WHILE STMTITER

     } catch ( java.lang.NullPointerException e ) {

       System.err.println("\t------- NullPtr ERROR IN RTA : Jimple can't handle " + currclass.getName() );
	   
     } catch ( java.lang.RuntimeException e ) {

       System.err.println("\t ------ Runtime ERROR IN RTA : Jimple can't handle " + currclass.getName() +" :  " + 
e.getMessage());
	
     } 

    } catch ( java.lang.RuntimeException e ) {} 

   } // WHILE ITER
	    
   return newExprs;
       
  }







  /* MEMORY : CAN REMOVE instancelocHT 

	    : CAN REMOVE instancetypes */








 
  public Map getInstanceTypes (/* String classname,    ArrayList bclasses, */ CHA cha
                               /*, AllClassFinder allclassfinder */ ) {

  /* THIS METHOD RETURNS A HASHTABLE CONTAINING THE TYPE NAMES OF ALL 
     THE CLASSES THAT HAVE BEEN INSTANTIATED */ 

  System.out.println();
  System.out.print("Performing Rapid Type Analysis.....");
  
  className = cha.getMainClassName();

  // OBTAIN ALL THE NEW, NEWARRAY, NEWMULTIARRAY EXPRESSIONS 

  ArrayList newexprs = getNewExprs(cha);

  //  System.out.println("");
  // System.out.println("INSTANCE TYPES : ");

  Iterator iter = newexprs.iterator();

  while ( iter.hasNext() )
  {

   Object nextexpr = iter.next();
       
   try {

    NewExpr newexpr = (NewExpr) nextexpr;  

    try {

     RefType reftype = newexpr.getBaseType();

     if ( ((SootClass) instancetypesHT.get(reftype.className)) == null ) 
     {

      // EXTRACT THE INSTANCE TYPE FROM THE NEW EXPRESSION

      instancetypesHT.put(reftype.className,new SootClass(reftype.className));

      instanceSet.set ( ( ( Integer ) bitMap.get ( reftype.className ) ).intValue() );
    
      // System.out.print(reftype.className);

      // System.out.println(" IN "+((String)instancelocHT.get(newexpr.toString())));

     }

    } catch (java.lang.RuntimeException e1 ) {}

   } catch (java.lang.RuntimeException e ) {

    try {

     // EXTRACT THE INSTANCE TYPE FROM THE NEWARRAY EXPRESSION

     NewArrayExpr newarrayexpr = (NewArrayExpr) nextexpr;
	  
     try {

      RefType reftype = (RefType) (newarrayexpr.getBaseType());
	  
      if ( ((SootClass) instancetypesHT.get(reftype.className)) == null )
      {

       instancetypesHT.put(reftype.className,new SootClass(reftype.className));
 
       instanceSet.set ( ( ( Integer ) bitMap.get ( reftype.className )).intValue() );

       // System.out.print(reftype.className);
	
       // System.out.println(" IN "+((String)instancelocHT.get(newarrayexpr.toString())));

      }
	
     } catch (java.lang.RuntimeException e2 ) {}

    } catch (java.lang.RuntimeException e1 ) {

      // EXTRACT THE INSTANCE TYPE FROM THE NEWMULTIARRAYEXPRESSION

      NewMultiArrayExpr newmultiarrayexpr = (NewMultiArrayExpr) nextexpr;   

      try {

       RefType reftype = (RefType) ((ArrayType) (newmultiarrayexpr.getBaseType())).baseType;

       if ( ((SootClass) instancetypesHT.get(reftype.className)) == null )
       {   

	instancetypesHT.put(reftype.className,new SootClass(reftype.className));

        instanceSet.set ( ( ( Integer ) bitMap.get ( reftype.className )).intValue() );

	    // System.out.print(reftype.className);
 
	    // System.out.println(" IN "+((String)instancelocHT.get(newmultiarrayexpr.toString())));
	  
       }

      } catch (java.lang.RuntimeException e3 ) {}

     } // TRY 
	  
    } // TRY 

   } // WHILE 

   adjustForNativeMethods();

   System.out.println("Done");

   return instancetypesHT;

  }      






  public Collection getFinalCallGraph() {

  /* THIS METHOD ACTUALLY REMOVES THE CALL GRAPH EDGES THAT CAN BE REMOVED 
     ACCORDING TO RAPID TYPE ANALYSIS */

  String reachedclass = new String();
   
  Collection callGraph = null;

  // System.out.println("BEFORE PRUNING THE CALL GRAPH HAS : "+totedges+" EDGES ");
  // System.out.println("AFTER PRUNING THE CALL GRAPH HAS : "+intactedges+" INTACT EDGES ");

  int totunchanged = 0, methodcnt = 0, cscnt = 0;

  try {

   PrintWriter pw = null;

   try {

    File tempFile = new File("analysis.CHA");
 
    FileOutputStream streamOut = new FileOutputStream(tempFile);

    pw = new PrintWriter(streamOut);

   } catch ( java.io.IOException e ) {}

   callGraph = cagb.getCallGraph();

   Iterator iter = callGraph.iterator(); 

   while ( iter.hasNext() )
   {

    methodcnt++;
   
    try {
   
     MethodNode tempMN = (MethodNode) iter.next();


     currmethod = tempMN.getMethod(); 

     String currname = currmethod.getDeclaringClass().getName();
   
     Iterator CSiter = tempMN.getCallSites().iterator();

     while ( CSiter.hasNext() )
     {


      cscnt++;

      try {

       isStatic = false; 

       isSpecial = false;

       CallSite cs = (CallSite) CSiter.next();
       
       InvokeExpr invokeExpr = cs.getInvokeExpr();

       invokeExpr.apply( new AbstractJimpleValueSwitch(){
	
	public void caseStaticInvokeExpr(StaticInvokeExpr v) {

	isStatic = true;

	}

	public void caseSpecialInvokeExpr(SpecialInvokeExpr v) {

	isSpecial = true;

	}

       });


       Map actualinstanceHT = new HashMap();

       HashMap reachedclassHT = new HashMap();

       Set vs = new HashSet();

       try {

	// OBTAIN THE SET OF METHODNODES THAT CAN BE CALLED FROM THIS CALLSITE
	// AS DETERMINED BY RAPID TYPE ANALYSIS


       if ( ! ( ( ClassGraphBuilder.isLibraryNode("java.", currname) || ClassGraphBuilder.isLibraryNode("sun.", currname) ) || ClassGraphBuilder.isLibraryNode("sunw.", currname) ) ) 
       {

        if ( ! ( ( invokeExpr instanceof StaticInvokeExpr ) || /* ( inexpr.getMethod().getName().equals ( "<init>" ) ) */ ( invokeExpr instanceof SpecialInvokeExpr ) ) )
        {     

         String currentID = cs.getCallerID();

         Iterator possibleit = cs.getMethods().iterator();

         while ( possibleit.hasNext() )
         {

          SootMethod printmethod = ( ( MethodNode ) possibleit.next() ).getMethod();

      //  try {

          pw.println ( currentID+" "+printmethod.getSignature() );

      //  } catch ( java.io.IOException e ) {}

         }

        }

        }

	vs = ( HashSet ) csHT.get( currmethod.getSignature()+invokeExpr.toString() ); 

	totadd = totadd + vs.size();

       } catch ( java.lang.RuntimeException e ) {}

       if ( ! ( isStatic || isSpecial ) )
       {

	// REPLACE THE SET OF METHODNODES THAT CAN BE CALLED FROM THIS 
	// CALLSITE BY THE NEW SET OBTAINED USING RAPID TYPE ANALYSIS        

	cs.setMethods(vs);
	totactadd = totactadd + vs.size();
       }
       else 
       totunchanged++;

      } catch ( java.lang.RuntimeException e ) {}

     } // WHILE CSITER

    } catch ( java.lang.RuntimeException e ) {}

   } // WHILE ITER

   pw.close();

  } catch ( java.lang.RuntimeException e ) {}

  /* CHECK ON NUMBER OF NODES AFTER PRUNING , CAN BE REMOVED */


  int totalnum = 0;

  try {   
       
    //   int totalnum = 0;
   
   Iterator iter = callGraph.iterator();

   PrintWriter pw = null;

   PrintWriter pw1 = null;

   HashSet seenclasses = new HashSet();

   try {


   File tempFile = new File("analysis.RTA");
 
   FileOutputStream streamOut = new FileOutputStream(tempFile);
            
   pw = new PrintWriter(streamOut);

   tempFile = new File("profiled.RTA");

   streamOut = new FileOutputStream(tempFile);
   
   pw1 = new PrintWriter(streamOut);


   } catch ( java.io.IOException e ) {}


   while ( iter.hasNext() )
   {
 
    try {
 
    MethodNode tempMN = (MethodNode) iter.next();
 
    currmethod = tempMN.getMethod();

    String currname = currmethod.getDeclaringClass().getName();
       
    if ( ! ( ( ClassGraphBuilder.isLibraryNode("java.", currname) || ClassGraphBuilder.isLibraryNode("sun.", currname) ) || ClassGraphBuilder.isLibraryNode("sunw.", currname) ) )
    {

    //  try {

    //   System.out.println ( "SEEN BENCHMARK" );
    //   System.out.println ( currname );       

       if ( ! seenclasses.contains ( currname ) )
       {

      //  System.out.println ( "PRINTING....");

        pw1.println ( currname );
     
        seenclasses.add ( currname );

       }

    //  } catch ( java.io.IOException e ) {}

    }


    Iterator CSiter = tempMN.getCallSites().iterator();

    while ( CSiter.hasNext() )
    {
	
     try {

      CallSite cs = (CallSite) CSiter.next();

      Set possMethodNodes = cs.getMethods();

      totalnum = totalnum + possMethodNodes.size();

      InvokeExpr inexpr = cs.getInvokeExpr();

//      String currname = currmethod.getDeclaringClass().getName();

      if ( ! ( ( ClassGraphBuilder.isLibraryNode("java.", currname) || ClassGraphBuilder.isLibraryNode("sun.", currname) ) || ClassGraphBuilder.isLibraryNode("sunw.", currname) ) ) 
      {

      if ( ! ( ( inexpr instanceof StaticInvokeExpr ) || /* ( inexpr.getMethod().getName().equals ( "<init>" ) ) */ ( inexpr instanceof SpecialInvokeExpr ) ) )
      {     

      String currentID = cs.getCallerID();

//      Set possMethodNodes = cs.getMethods();

//      totalnum = totalnum + possMethodNodes.size();

      Iterator possibleit = possMethodNodes.iterator();

      while ( possibleit.hasNext() )
      {

       SootMethod printmethod = ( ( MethodNode ) possibleit.next() ).getMethod();

      //  try {

       pw.println ( currentID+" "+printmethod.getSignature() );

      //  } catch ( java.io.IOException e ) {}

      }

      }

     }

     } catch ( java.lang.RuntimeException e ) {}

    }

   } catch ( java.lang.RuntimeException e ) {}
 
  } // WHILE ITER

  pw.close();

  pw1.close();

  // System.out.println("TOTAL METHODS : "+methodcnt);

  // System.out.println("TOTAL CALLSITES : "+cscnt);

  // System.out.println("TOTAL UNCHANGED : "+totunchanged+" EDGES ");

  // System.out.println("TOTAL ACTUALLY ADDED : "+totactadd+" EDGES ");

  // System.out.println("PRUNED CALL GRAPH HAS : "+totalnum+" EDGES");

 } catch ( java.lang.RuntimeException e ) {}

 System.out.println("PRUNED CALL GRAPH HAS : "+totalnum+" EDGES");
 
 return callGraph;

 }





 public static BufferedWriter getBufWriter(String file)
 throws IOException {
      
      FileOutputStream out = new FileOutputStream(file);
      return getBufWriter( (OutputStream) out );
 }

 public static BufferedWriter getBufWriter(OutputStream i)
 throws IOException {

      return( new BufferedWriter
            ( new OutputStreamWriter(i)));

 }






















  public Collection getCallGraph() {

  /* THIS METHOD PERFORMS ALL THE STATISTICS GATHERING ON THE CALL GRAPH 
     IMPROVEMENT POSSIBLE USING RAPID TYPE ANALYSIS

     THE NEW SET OF METHODNODES ( OBTAINED USING RTA ) THAT CAN BE INVOKED 
     FROM EACH CALL SITE IS ALSO COMPUTED IN THIS METHOD

     ALSO PERFORMS SOME GARBAGE COLLECTION WITH A CALL TO RTAGC2()  
  */


  int edges = 0,removededges = 0,polymorphicsites = 0, polytotal = 0,polyred =
0, 
  intpolyred = 0, virpolyred = 0,sites = 0,zerored = 0,siteno = 0,twoedges =
0,inttwoedges = 0, virtwoedges = 0,threeedges =0, intthreeedges = 0,
virthreeedges = 0, moreedges =0, intmoreedges = 0, virmoreedges = 0, twored = 0,
inttwored = 0, virtwored = 0,
  threered =0, intthreered = 0, virthreered = 0, morered =0, intmorered = 0,
virmorered = 0, 
  zonered = 0,ztwored =0,zthreered =0,zmorered = 0,bired = 0, intbired = 0,
virbired = 0, bthreered = 0, intbthreered = 0, virbthreered = 0, bmorered = 0, intbmorered =0, virbmorered = 0;

  int benchmethodnodes = 0, benchsites = 0, benchpolymorphicsites = 0, benchpolytotal = 0, benchtwoedges = 0, benchinttwoedges = 0, benchvirttwoedges = 0, benchthreeedges = 0, benchintthreeedges = 0, benchvirtthreeedges = 0, benchmoreedges = 0, benchintmoreedges = 0, benchvirtmoreedges = 0, benchedges = 0, benchtotedges = 0, benchremovededges = 0, benchreduction = 0, benchintactedges = 0, benchpolyred = 0, benchintpolyred = 0, benchvirpolyred = 0, benchtwored = 0, benchinttwored = 0, benchvirtwored = 0, benchthreered = 0, benchintthreered = 0, benchvirthreered = 0, benchmorered = 0, benchintmorered = 0, benchvirmorered = 0;

 
  List paramList = new ArrayList();

  String reachedclass = new String();  

  Collection callGraph = cagb.getCallGraph();

  // Map OpenMethodsHT = cagb.getOpenMethodsHT();

  // cagb.removeOpenMethods();

  // System.out.println("");
  // System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++");
  // System.out.println("NUMBER OF METHODNODES = "+callGraph.size());
  // System.out.println("NUMBER OF BENCHMARK METHODNODES = "+Jimplifier.benchjimplifiedNum );

  // System.out.println("NUMBER OF CALLSITES = "+cagb.getSitesNum());
  // System.out.println("NUMBER OF CALLGRAPH EDGES = "+cagb.getEdgesNum());
  // System.out.println("");

  System.out.println();
  System.out.print ("Pruning call graph based on Rapid Type Analysis");
  
  Iterator iter = callGraph.iterator();

  RTATimer.end();

  RTAMem = Runtime.getRuntime().totalMemory() -
Runtime.getRuntime().freeMemory();
   
  // System.out.println("TIME FOR RTA (ANALYSIS ONLY) : "+RTATimer.getTime());
  // System.out.println("SPACE FOR RTA (ANALYSIS ONLY) : "+RTAMem);

  RTAedgeRemovedTimer.start();

  int mthdcount = 10;

  while ( iter.hasNext() )
  {

    try {

    if ( mthdcount == 10 )
    {
     System.out.print(".");
     mthdcount = 1;
    }
    else
    mthdcount++;

    MethodNode tempMN = (MethodNode) iter.next();

    currmethod = tempMN.getMethod();

    currclass = currmethod.getDeclaringClass();

    String currclassname = currclass.getName();

    sites = sites + tempMN.getCallSites().size();

    if ( ! ( ( ClassGraphBuilder.isLibraryNode("java.", currclassname) || ClassGraphBuilder.isLibraryNode("sun.", currclassname) ) || ClassGraphBuilder.isLibraryNode("sunw.", currclassname) ) )
    {

     benchsites = benchsites + tempMN.getCallSites().size();

     benchmethodnodes++;

    }

//    Iterator CSiter = tempMN.getCallSites().iterator();

//    ArrayList callsitesAL = (ArrayList) (tempMN).getCallSites();

//    ArrayList callsites = Helper.CSAL2VL(callsitesAL);

    Iterator CSiter = tempMN.getCallSites().iterator();

    while ( CSiter.hasNext() )
    {    

     siteno++;

     // System.out.print("<"+siteno+">");    

     CallSite cs = (CallSite) CSiter.next();

     HashSet filteredMethodNodes = new HashSet();

     try {

      Set possMethodNodes = cs.getMethods();

      int size = possMethodNodes.size();

      int reduction = 0;

      isStatic = false;

      isSpecial = false;
 
      intflag = false;

      virflag = false;

      cs.getInvokeExpr().apply( new AbstractJimpleValueSwitch() {
     
       public void caseInterfaceInvokeExpr(InterfaceInvokeExpr v) {

	intflag = true;
	 
	Local l = (Local) v.getBase();

       }
	
       public void caseSpecialInvokeExpr(SpecialInvokeExpr v) {

	isSpecial = true;
   
	Local l = (Local) v.getBase();

       }

       public void caseStaticInvokeExpr(StaticInvokeExpr v) {

	isStatic = true;

       }

       public void caseVirtualInvokeExpr(VirtualInvokeExpr v) {
      
	virflag = true;

	Local l = (Local) v.getBase();

       }
       
      });


      HashMap reachedclassHT = new HashMap();

      try {

       // GET THE HASHTABLE CONTAINING THE NAMES OF THE CLASSES THAT CAN BE 
       // REACHED DUE TO THIS METHOD CALL
       // THIS ANSWER IS ARRIVED AT BY CHECKVIRTUALTABLES(..) BY USING THE
       // RESULT FROM RAPID TYPE ANALYSIS 
       
       reachedclassHT = checkVirtualTables(cs.getInvokeExpr().getMethod());

      } catch ( java.lang.RuntimeException e ) {}



      if ( size > 1 ) 
      { 

        polymorphicsites++; polytotal = polytotal + size;

       if ( ! ( ( ClassGraphBuilder.isLibraryNode("java.", currclassname) || ClassGraphBuilder.isLibraryNode("sun.", currclassname) ) || ClassGraphBuilder.isLibraryNode("sunw.", currclassname) ) )
       {

        benchpolymorphicsites++;

        benchpolytotal = benchpolytotal + size;

       }


       if ( size == 2 ) 
       { 
	
	twoedges++; 
     
	if ( intflag ) inttwoedges++; 
	else virtwoedges++;

        if ( ! ( ( ClassGraphBuilder.isLibraryNode("java.", currclassname) || ClassGraphBuilder.isLibraryNode("sun.", currclassname) ) || ClassGraphBuilder.isLibraryNode("sunw.", currclassname) ) )
        {

         benchtwoedges++;

         if ( intflag ) benchinttwoedges++;
         else benchvirttwoedges++;

        }

       }
       else if ( size == 3 ) 
       { 

	threeedges++; 

	if ( intflag ) intthreeedges++; 
	else virthreeedges++;

        if ( ! ( ( ClassGraphBuilder.isLibraryNode("java.", currclassname) || ClassGraphBuilder.isLibraryNode("sun.", currclassname) ) || ClassGraphBuilder.isLibraryNode("sunw.", currclassname) ) )
        {

         benchthreeedges++;

         if ( intflag ) benchintthreeedges++;
         else benchvirtthreeedges++;

        }

       }
       else 
       { 
 
	moreedges++; 

	if ( intflag ) intmoreedges++; 
	else virmoreedges++; 

        if ( ! ( ( ClassGraphBuilder.isLibraryNode("java.", currclassname) || ClassGraphBuilder.isLibraryNode("sun.", currclassname) ) || ClassGraphBuilder.isLibraryNode("sunw.", currclassname) ) )
        {

         benchmoreedges++;

         if ( intflag ) benchintmoreedges++;
         else benchvirtmoreedges++;

        }

       }
      
      } // IF SIZE

       edges = edges + size;

       if ( ! ( ( ClassGraphBuilder.isLibraryNode("java.", currclassname) || ClassGraphBuilder.isLibraryNode("sun.", currclassname) ) || ClassGraphBuilder.isLibraryNode("sunw.", currclassname) ) )
       {

        benchedges = benchedges + size;

       }

       Iterator MNiter = possMethodNodes.iterator();

       while ( MNiter.hasNext() )
       {

	try {

	 MethodNode mn = (MethodNode) MNiter.next();

	 SootMethod method = mn.getMethod();

	 reachedclass = (method.getDeclaringClass()).getName();

	 isStatic = (isStatic) || (Modifier.isStatic( method.getDeclaringClass().getModifiers() ));

	 if ( !(isStatic || isSpecial) )
	 {

//          System.out.print("CALLED METHOD "+reachedclass+method.getName());

/*
	 if (( ( ((SootClass) instancetypesHT.get(reachedclass)) == null )&& 
( !((Boolean) OpenMethodsHT.get(reachedclass+method.getName())).booleanValue())
) 
&& (! method.getName().equals(new String("<init>"))))
*/

/*
	 if (( ( ((SootClass) instancetypesHT.get(reachedclass)) == null )&& ( !
CallGraphBuilder.isOpen(reachedclass+method.getName() )) ) && (!
method.getName().equals(new String("<init>"))))
	 {
*/

	  // CHECK TO SEE IF CALL GRAPH EDGE CAN BE REMOVED


	  if ( ( (( (String) reachedclassHT.get(reachedclass) ) == null ) 
) && ( ! ( ( NonStaticInvokeExpr)cs.getInvokeExpr()).getBase().getType().toString().endsWith("[]") ) )
	  {

           if ( ! ( ( size==1) && ( ( ( NonStaticInvokeExpr ) cs.getInvokeExpr()).getBase().getType().toString().equals("java.lang.Object") ) ) )
           {

	   totedges++;

	   removededges++;

	   reduction++;

	   mn.incomingedges--;

       mn.removeInvokingSite(cs);

           if ( ! ( ( ClassGraphBuilder.isLibraryNode("java.", currclassname) || ClassGraphBuilder.isLibraryNode("sun.", currclassname) ) || ClassGraphBuilder.isLibraryNode("sunw.", currclassname) ) )
           {

            benchtotedges++;

            benchremovededges++;

            benchreduction++;

           } 

           // if ( mn.incomingedges < 1 )
           // mn.isRedundant = true;


	   /*        try {
		      System.out.println("CALL SITE : "+cs.getInvokeExpr().toString());
		     } catch ( NoInvokeExprException e ){}
	   */

	   //     System.out.println(" CURRENT CLASS AND METHOD : "+currclass.getName()+" "+currmethod.getName());

	   // System.out.print("CALLED METHOD "+reachedclass+" "+method.getName());
	   // System.out.println(" REMOVED");

	  } // IF REACHEDCLASS
	  else 
	  {

	   filteredMethodNodes.add(mn);

	   totedges++;

	   intactedges++;

           if ( ! ( ( ClassGraphBuilder.isLibraryNode("java.", currclassname) || ClassGraphBuilder.isLibraryNode("sun.", currclassname) ) || ClassGraphBuilder.isLibraryNode("sunw.", currclassname) ) )
           {

            benchtotedges++;

            benchintactedges++;

           }

	  }

	 } // IF ISSTATIC
	 else 
	 {

	  filteredMethodNodes.add(mn);

	  intactedges++;

	  totedges++;

          if ( ! ( ( ClassGraphBuilder.isLibraryNode("java.", currclassname) || ClassGraphBuilder.isLibraryNode("sun.", currclassname) ) || ClassGraphBuilder.isLibraryNode("sunw.", currclassname) ) )
          {

           benchtotedges++;

           benchintactedges++;

          }

          } 

	 }

	} catch ( java.lang.NullPointerException e ) {}
  
       } // WHILE MNITER

       reduced[siteno-1] = size - reduction;

       if ( reduction > 0 )
       {

//        System.out.println("REDUCTION FROM "+size+" TO "+(size - reduction));
    
	if ( ( size - reduction ) == 1 )
	{

	 polyred++;

	 if ( intflag )
	 intpolyred++;
	 else if ( virflag )
	 virpolyred++;       

         if ( ! ( ( ClassGraphBuilder.isLibraryNode("java.", currclassname) || ClassGraphBuilder.isLibraryNode("sun.", currclassname) ) || ClassGraphBuilder.isLibraryNode("sunw.", currclassname) ) )
         {
          benchpolyred++;

          if ( intflag )
          benchintpolyred++;
          else if ( virflag )
          benchvirpolyred++;

         }

	 if ( size == 2 ) 
	 {

	  twored++;

	  if ( intflag )
	  inttwored++;
	  else if ( virflag )
	  virtwored++;

          if ( ! ( ( ClassGraphBuilder.isLibraryNode("java.", currclassname) || ClassGraphBuilder.isLibraryNode("sun.", currclassname) ) || ClassGraphBuilder.isLibraryNode("sunw.", currclassname) ) )
          {
           benchtwored++;

           if ( intflag )
           benchinttwored++;
           else if ( virflag )
           benchvirtwored++;

          }

	 }
	 else if ( size == 3 )
	 {
 
	  threered++;
 
	  if ( intflag )
	  intthreered++;
	  else if ( virflag )
	  virthreered++;

          if ( ! ( ( ClassGraphBuilder.isLibraryNode("java.", currclassname) || ClassGraphBuilder.isLibraryNode("sun.", currclassname) ) || ClassGraphBuilder.isLibraryNode("sunw.", currclassname) ) )
          {
           benchthreered++;

           if ( intflag )
           benchintthreered++;
           else if ( virflag )
           benchvirthreered++;

          }

	 }
	 else
	 {

	  morered++;

	  if ( intflag )
	  intmorered++;
	  else if ( virflag )
	  virmorered++;

          if ( ! ( ( ClassGraphBuilder.isLibraryNode("java.", currclassname) || ClassGraphBuilder.isLibraryNode("sun.", currclassname) ) || ClassGraphBuilder.isLibraryNode("sunw.", currclassname) ) )
          {

           benchmorered++;

           if ( intflag )
           benchintmorered++;
           else if ( virflag )
           benchvirmorered++;

          }

	 }

	}
	else if ( ( size - reduction ) == 0 )
	{

	 zerored++;

	 if ( size == 1 )
         {

	 zonered++;

     //         System.out.println ( "REDUCED TO ZERO INV EXPR "+ cs.getInvokeExpr() + " IN "+currmethod.getSignature() );
                                 
         }
	 else if ( size == 2 )
	 ztwored++;
	 else if ( size == 3 )
	 zthreered++;
	 else
	 zmorered++;

	}
	else if ( ( size - reduction ) == 2 )
	{

	 bired++;

	 if ( intflag )
	 intbired++;
	 else if ( virflag )
	 virbired++;

	 if ( size == 3 )
	 {

	  bthreered++;

	  if ( intflag )
	  intbthreered++;
	  else if ( virflag )
	  virbthreered++;

	 }
	 else
	 {
     
	  bmorered++;

	  if ( intflag )
	  intbmorered++;
	  else if ( virflag )
	  virbmorered++;

	 }

	}

       } // IF REDUCTION 

      } catch ( java.lang.RuntimeException e ) {}


     try {

     // CSHT CONTAINS THE LIST OF METHODS THAT CAN BE CALLED 
     // FROM THIS CALL SITE AS A RESULT OF PERFORMING RTA  

     
csHT.put(currmethod.getSignature()+cs.getInvokeExpr().toString(),filteredMethodNodes);
	
      if ( filteredMethodNodes.size() > 0 ) 
      {

       // System.out.println( "SIZE = "+filteredMethodNodes.size());

       totalin = totalin + filteredMethodNodes.size();

      }

     } catch ( java.lang.RuntimeException e ){}
 

    
    } // WHILE CSITER

   } catch ( java.lang.NullPointerException e ){}

  } // WHILE ITER

      System.out.println("Done");
      System.out.println("");
      /*    

      System.out.println("TOTAL NUMBER OF EDGES IN CALL GRAPH = "+edges);
      System.out.println("NUMBER OF BENCHMARK EDGES = "+benchedges);

      System.out.println("TOTAL NUMBER OF POLYMORPHIC EDGES = "+polytotal);
      System.out.println("NUMBER OF BENCHMARK POLYMORPHIC EDGES = "+benchpolytotal ); 
      System.out.println("TOTAL NUMBER OF SITES = "+sites);
      System.out.println("NUMBER OF BENCHMARK SITES = "+benchsites );

      System.out.println("TOTAL NUMBER OF POLYMORPHIC SITES = "+polymorphicsites);
      System.out.println("NUMBER OF BENCHMARK POLYMORPHIC SITES = "+benchpolymorphicsites);

      System.out.println("TOTAL NUMBER OF SITES ( TO 2 MTHDS ) = "+twoedges);
      System.out.println("NUMBER OF BENCHMARK SITES ( TO 2 MTHDS ) = "+benchtwoedges);

      System.out.println("TOTAL NUMBER OF INVOKEINTERFACE SITES ( TO 2 MTHDS ) = "+inttwoedges);
      System.out.println("NUMBER OF BENCHMARK INVOKEINTERFACE SITES ( TO 2 MTHDS ) = "+benchinttwoedges );

      System.out.println("TOTAL NUMBER OF INVOKEVIRTUAL SITES ( TO 2 MTHDS ) = "+virtwoedges);
      System.out.println("NUMBER OF BENCHMARK INVOKEVIRTUAL SITES ( TO 2 MTHDS ) = "+benchvirttwoedges );

      System.out.println("TOTAL NUMBER OF SITES ( TO 3 MTHDS ) = "+threeedges);
      System.out.println("NUMBER OF BENCHMARK SITES ( TO 3 MTHDS ) = "+benchthreeedges);

      System.out.println("TOTAL NUMBER OF INVOKEINTERFACE SITES ( TO 3 MTHDS ) = "+intthreeedges);
      System.out.println("NUMBER OF BENCHMARK INVOKEINTERFACE SITES ( TO 3 MTHDS ) = "+benchintthreeedges);

      System.out.println("TOTAL NUMBER OF INVOKEVIRTUAL SITES ( TO 3 MTHDS ) = "+virthreeedges);
      System.out.println("NUMBER OF BENCHMARK INVOKEVIRTUAL SITES ( TO 3 MTHDS ) = "+benchvirtthreeedges);


      System.out.println("TOTAL NUMBER OF SITES ( TO >3 MTHDS ) = "+moreedges);
      System.out.println("NUMBER OF BENCHMARK SITES ( TO >3 MTHDS ) = "+benchmoreedges);

      System.out.println("TOTAL NUMBER OF INVOKEINTERFACE SITES ( TO >3 MTHDS ) = "+intmoreedges);

      System.out.println("NUMBER OF BENCHMARK INVOKEINTERFACE SITES ( TO >3 MTHDS ) = "+benchintmoreedges);

      System.out.println("TOTAL NUMBER OF INVOKEVIRTUAL SITES ( TO >3 MTHDS ) = "+virmoreedges);
      System.out.println("NUMBER OF BENCHMARK INVOKEVIRTUAL SITES ( TO >3 MTHDS ) = "+benchvirtmoreedges);

      System.out.println("TOTAL NUMBER OF EDGES REMOVED = "+removededges);

      System.out.println("NUMBER OF BENCHMARK EDGES REMOVED = "+benchremovededges);

      System.out.println("NUMBER OF POLYMORPHIC SITES REDUCED TO 2 = "+bired);
      System.out.println("NUMBER OF INVOKEINTERFACE SITES REDUCED TO 2 = "+intbired);
      System.out.println("NUMBER OF INVOKEVIRTUAL SITES REDUCED TO 2 = "+virbired);
      System.out.println("TOTAL NUMBER OF SITES ( TO 3 MTHDS ) REDUCED TO 2 = "+bthreered);
      System.out.println("TOTAL NUMBER OF INVOKEINTERFACE SITES ( TO 3 MTHDS ) REDUCED TO 2 = "+intbthreered);
      System.out.println("TOTAL NUMBER OF INVOKEVIRTUAL ( TO 3 MTHDS ) REDUCED TO 2 = "+virbthreered);
      System.out.println("TOTAL NUMBER OF SITES ( TO >3 MTHDS ) REDUCED TO 2 = "+bmorered);
      System.out.println("TOTAL NUMBER OF INVOKEINTERFACE SITES ( TO >3 MTHDS ) REDUCED TO 2 = "+intbmorered);
      System.out.println("TOTAL NUMBER OF INVOKEVIRTUAL SITES ( TO >3 MTHDS ) REDUCED TO 2 = "+virbmorered);
      System.out.println("NUMBER OF POLYMORPHIC SITES REDUCED TO 1 = "+polyred);

      System.out.println("NUMBER OF BENCHMARK POLYMORPHIC SITES REDUCED TO 1 = "+benchpolyred);
      System.out.println("NUMBER OF INVOKEINTERFACE SITES REDUCED TO 1 = "+intpolyred);
      System.out.println("NUMBER OF BENCHMARK INVOKEINTERFACE SITES REDUCED TO 1 = "+benchintpolyred);

     System.out.println("NUMBER OF INVOKEVIRTUAL SITES REDUCED TO 1 = "+virpolyred);
     System.out.println("NUMBER OF BENCHMARK INVOKEVIRTUAL SITES REDUCED TO 1 = "+benchvirpolyred);

      System.out.println("TOTAL NUMBER OF SITES ( TO 2 MTHDS ) REDUCED TO 1 = "+twored);  
      System.out.println("NUMBER OF BENCHMARK SITES ( TO 2 MTHDS ) REDUCED TO 1 = "+benchtwored);  
   
      System.out.println("TOTAL NUMBER OF INVOKEINTERFACE SITES ( TO 2 MTHDS ) REDUCED TO 1 = "+inttwored); 

      System.out.println("NUMBER OF BENCHMARK INVOKEINTERFACE SITES ( TO 2 MTHDS ) REDUCED TO 1 = "+benchinttwored); 

      System.out.println("TOTAL NUMBER OF INVOKEVIRTUAL SITES ( TO 2 MTHDS ) REDUCED TO 1 = "+virtwored); 

      System.out.println("NUMBER OF BENCHMARK INVOKEVIRTUAL SITES ( TO 2 MTHDS ) REDUCED TO 1 = "+benchvirtwored); 

      System.out.println("TOTAL NUMBER OF SITES ( TO 3 MTHDS ) REDUCED TO 1 = "+threered);
      System.out.println("NUMBER OF BENCHMARK SITES ( TO 3 MTHDS ) REDUCED TO 1 = "+benchthreered);

      System.out.println("TOTAL NUMBER OF INVOKEINTERFACE SITES ( TO 3 MTHDS ) REDUCED TO 1 = "+intthreered);

      System.out.println("NUMBER OF BENCHMARK INVOKEINTERFACE SITES ( TO 3 MTHDS ) REDUCED TO 1 = "+benchintthreered);

      System.out.println("TOTAL NUMBER OF INVOKEVIRTUAL SITES ( TO 3 MTHDS ) REDUCED TO 1 = "+virthreered);

      System.out.println("NUMBER OF BENCHMARK INVOKEVIRTUAL SITES ( TO 3 MTHDS ) REDUCED TO 1 = "+benchvirthreered);

      System.out.println("TOTAL NUMBER OF SITES ( TO >3 MTHDS ) REDUCED TO 1 = "+morered);

      System.out.println("NUMBER OF BENCHMARK SITES ( TO >3 MTHDS ) REDUCED TO 1 = "+benchmorered);

      System.out.println("TOTAL NUMBER OF INVOKEINTERFACE SITES ( TO >3 MTHDS ) REDUCED TO 1 = "+intmorered);

     System.out.println("NUMBER OF BENCHMARK INVOKEINTERFACE SITES ( TO >3 MTHDS ) REDUCED TO 1 = "+benchintmorered);

      System.out.println("TOTAL NUMBER OF INVOKEVIRTUAL SITES ( TO >3 MTHDS ) REDUCED TO 1 = "+virmorered);

      System.out.println("NUMBER OF BENCHMARK INVOKEVIRTUAL SITES ( TO >3 MTHDS ) REDUCED TO 1 = "+benchvirmorered);

      System.out.println("NUMBER OF SITES REDUCED TO 0 = "+zerored);
      System.out.println("TOTAL NUMBER OF SITES ( TO 1 MTHD ) REDUCED TO 0 = "+zonered);
      System.out.println("TOTAL NUMBER OF SITES ( TO 2 MTHDS ) REDUCED TO 0 = "+ztwored);    
      System.out.println("TOTAL NUMBER OF SITES ( TO 3 MTHDS ) REDUCED TO 0 = "+zthreered);
      System.out.println("TOTAL NUMBER OF SITES ( TO >3 MTHDS ) REDUCED TO 0 = "+zmorered);

      */

      RTAedgeRemovedTimer.end();

      RTAedgeRemovedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
      /*
      System.out.println("TIME FOR RTA (EDGE REMOVAL ONLY) : "+RTAedgeRemovedTimer.getTime());
      System.out.println("SPACE FOR RTA (EDGE REMOVAL ONLY) : "+RTAedgeRemovedMem);
      */
      // PRINT NUMBER OF ALIVE METHODS 
      
      printRemainingMethods();

      // GARBAGE COLLECTION

      // RTAgc2();

      return callGraph;

   }









  public void RTAgc2()
  {
   /* GARBAGE COLLECTION OF INSTANCELOCHT WHICH IS
      USED ONLY TO PRINT OUT INFORMATION ABOUT WHERE NEW, NEWARRAY, 
      OR NEWMULTIARRAY EXPRESSIONS ARE OBSERVED */ 
   
   instancelocHT = null;

   int i = 0;
    
   for ( i = 0; i < 10; i++ )
   System.gc();

  }











  public HashMap checkHierarchy( Map instanceHT, SootMethod meth, SootMethod currmeth ) 
{

   // NOT USED ANY MORE 
 
   Object[] htkeys = instanceHT.keySet().toArray();

   HashMap answerHT = new HashMap();

   for ( int i = 0 ; i < htkeys.length ; i++ )
   {
 
    try {

     String className = ((SootClass) instanceHT.get(htkeys[i])).getName();

     ClassNode cnode = clgb.getNode(className);

     SootMethod method = cagb.getSuperMethod( cnode, meth, currmeth ); 
    
     answerHT.put(method.getDeclaringClass().getName(),method.getDeclaringClass().getName());
       
    } catch (java.lang.RuntimeException e ) {}
   }

   return answerHT;

  }












  public HashMap checkVirtualTables( SootMethod meth ) {

  /* THIS METHOD PERFORMS THE LOOKUP IN THE VIRTUALHT CREATED IN 
     CLASSGRAPHBUILDER

     RETURNS A HASHTABLE CONTAINING CLASSES WHICH DECLARE A METHOD 
     NAMED METH AND ARE DETERMINED TO BE POTENTIAL TARGETS BY RAPID 
     TYPE ANALYSIS     
  */


  // CONSTRUCT THE METHOD SIGNATURE EXCEPT FOR THE CLASSNAME
	
  StringBuffer buffer = new StringBuffer();
	
  buffer.append(meth.getName());
      
  buffer.append("(");
    
  Iterator typeIt = meth.getParameterTypes().iterator();
       
  if(typeIt.hasNext())
  {

   buffer.append(typeIt.next());
      
   while(typeIt.hasNext())
   {

    buffer.append(",");

    buffer.append(typeIt.next());

   }

  }
     
  buffer.append(")");
     
  buffer.append(":" + meth.getReturnType().toString());
	      
  HashMap answerHT = new HashMap();

  // USE THE RESULT FROM RAPID TYPE ANALYSIS 
		
  // Iterator it = instancetypesHT.values().iterator();

  // while ( it.hasNext() )
  for ( int i = 0 ; i < /* htkeys.length */ instanceSet.size(); i++ )
  {

   try {
	 
    // String className = (String) ( ( SootClass ) it.next() ).getName();

      String className = null;

//    System.out.println(" ORIGINAL CLASS " + className );
   
    if ( instanceSet.get(i) )
    {

     className = ( String ) inversebitMap.get ( new Integer ( i ) );

     HashMap stm = ( HashMap ) clgb.virtualHT.get(className);
	     
     String reachedclass = ( String ) stm.get(buffer.toString() );
   
//    System.out.println(" REACHED CLASS "+ reachedclass ); 

     answerHT.put(reachedclass, reachedclass);

    }

   } catch (java.lang.RuntimeException e ) {}

  }     
     
  return answerHT;

  }








  void printRemainingMethods() {

  /* CALCULATES THE NUMBER OF METHODS THAT MIGHT BE CALLED ( ALIVE )
     AS A RESULT OF PERFORMING RTA AND PRINTS OUT THE NUMBER */
     
  alive = 0;

  int benchalive = 0;

  int clinitnum = 0;

  int benchclinitnum = 0;

  Iterator iter = cagb.getCallGraph().iterator();

  while ( iter.hasNext() ) 
  {
   
   MethodNode mn = ( MethodNode ) iter.next(); 

   String currclassname = mn.getMethod().getDeclaringClass().getName();

   if ( mn.incomingedges > 0 ) 
   {
    alive++;

    if ( ! ( ( ClassGraphBuilder.isLibraryNode("java.", currclassname) || ClassGraphBuilder.isLibraryNode("sun.", currclassname) ) || ClassGraphBuilder.isLibraryNode("sunw.", currclassname) ) )
    benchalive++;

   }
   else 
   {

     // System.out.println ( "DEAD "+mn.getMethod().getSignature() );

     if ( ( mn.getMethod().getName().equals("<clinit>") ) || ( mn.getMethod().getName().equals("finalize") ) )
     {

      clinitnum++;  

      if ( ! ( ( ClassGraphBuilder.isLibraryNode("java.", currclassname) || ClassGraphBuilder.isLibraryNode("sun.", currclassname) ) || ClassGraphBuilder.isLibraryNode("sunw.", currclassname) ) )
      benchclinitnum++;
     }



   for( Iterator runMethodIter = clgb.runmethods.iterator() ;
	 runMethodIter.hasNext() ; )
   {

      SootMethod method = (SootMethod)runMethodIter.next();

      if ( method.getSignature().equals ( mn.getMethod().getSignature() ) )
      {


       clinitnum++;  

      if ( ! ( ( ClassGraphBuilder.isLibraryNode("java.", currclassname) || ClassGraphBuilder.isLibraryNode("sun.", currclassname) ) || ClassGraphBuilder.isLibraryNode("sunw.", currclassname) ) )
      benchclinitnum++;
     }

   }



   for( Iterator startMethodIter = clgb.startmethods.iterator() ;
	 startMethodIter.hasNext() ; )
   {

      SootMethod method = (SootMethod) startMethodIter.next();

      if ( method.getSignature().equals ( mn.getMethod().getSignature() ) )
      {


       clinitnum++;  

      if ( ! ( ( ClassGraphBuilder.isLibraryNode("java.", currclassname) || ClassGraphBuilder.isLibraryNode("sun.", currclassname) ) || ClassGraphBuilder.isLibraryNode("sunw.", currclassname) ) )
      benchclinitnum++;
     }

   }


   }

  }

  /*
  System.out.println("NUMBER OF ALIVE METHODS = "+alive);

  System.out.println("NUMBER OF ALIVE BENCHMARK METHODS = "+benchalive);

  System.out.println("NUMBER OF CLINIT METHODS = "+clinitnum);

  System.out.println("NUMBER OF BENCHMARK CLINIT METHODS = "+benchclinitnum );

  */

 }





 public Set newinstances = new HashSet();





 private void adjustForNativeMethods () {

  Collection callGraph = cagb.getCallGraph();

  Iterator iter = callGraph.iterator();
   
  while ( iter.hasNext() )
  {
    
    try {

     MethodNode tempMN = (MethodNode) iter.next();
    
     currmethod = tempMN.getMethod();
   
     if ( currmethod.getSignature().equals ( "<java.awt.Toolkit: java.awt.Toolkit getDefaultToolkit()>") )
     {

      if ( clgb.getNode ( "java.awt.Toolkit" ) != null )
      {
    
       instancetypesHT.put( "java.awt.Toolkit" , new SootClass( "java.awt.Toolkit" ) );

       instanceSet.set ( ( ( Integer ) bitMap.get ( "java.awt.Toolkit" ) ).intValue() );
     
       newinstances.add ( "java.awt.Toolkit" );

       adjustSubClasses ( "java.awt.Toolkit" );

      }

     }
     else if ( currmethod.getSignature().equals ( "<java.beans.Beans: java.lang.Object instantiate(java.lang.ClassLoader,java.lang.String)>" ) )
     {

      if ( clgb.getNode ( "java.applet.Applet" ) != null )
      {

      instancetypesHT.put ( "java.applet.Applet" , new SootClass ( "java.applet.Applet" ) );

      instanceSet.set ( ( ( Integer ) bitMap.get ( "java.applet.Applet" ) ).intValue() );

      newinstances.add ( "java.applet.Applet" );

      adjustSubClasses ( "java.applet.Applet" );
    
      }

     }
     else if ( currmethod.getSignature().equals ( "<java.beans.Introspector: java.beans.BeanInfo findInformant(java.lang.Class)>" ) )
     {

      if ( clgb.getNode ( "java.beans.BeanInfo" ) != null )
      {

      instancetypesHT.put ( "java.beans.BeanInfo" , new SootClass ( "java.beans.BeanInfo" ) );
    
      instanceSet.set ( ( ( Integer ) bitMap.get ( "java.beans.BeanInfo" ) ).intValue() );

      newinstances.add ( "java.beans.BeanInfo" );

      adjustSubClasses ( "java.beans.BeanInfo" );

      }

     }
     else if ( currmethod.getSignature().equals ( "<java.beans.PropertyEditorManager: java.beans.PropertyEditor findEditor(java.lang.Class)>" ) )
     {

      if ( clgb.getNode ( "java.beans.PropertyEditor" ) != null )
      {

      instancetypesHT.put ( "java.beans.PropertyEditor" , new SootClass ( "java.beans.PropertyEditor" ) );
  
      instanceSet.set ( ( ( Integer ) bitMap.get ( "java.beans.PropertyEditor" ) ).intValue() );

      newinstances.add ( "java.beans.PropertyEditor" );

      adjustSubClasses ( "java.beans.PropertyEditor" ); 

      }

     }
     else if ( currmethod.getSignature().equals ( "<java.beans.PropertyEditorManager: java.beans.PropertyEditor instantiate(java.lang.Class,java.lang.String)>" ) )
     {

      if ( clgb.getNode ( "java.beans.PropertyEditor" ) != null )
      {


      instancetypesHT.put ( "java.beans.PropertyEditor" , new SootClass ( "java.beans.PropertyEditor" ) );

      instanceSet.set ( ( ( Integer ) bitMap.get ( "java.beans.PropertyEditor" ) ).intValue() );

      newinstances.add ( "java.beans.PropertyEditor" );

      adjustSubClasses ( "java.beans.PropertyEditor" );

      }

     }
     else if ( currmethod.getSignature().equals ( "<java.net.DatagramSocket: void create(int,java.net.InetAddress)>" ) )
     {

      if ( clgb.getNode ( "java.net.DatagramSocketImpl" ) != null )
      {

      instancetypesHT.put ( "java.net.DatagramSocketImpl" , new SootClass ( "java.net.DatagramSocketImpl" ) );

      instanceSet.set ( ( ( Integer ) bitMap.get ( "java.net.DatagramSocketImpl" ) ).intValue() );

      newinstances.add ( "java.net.DatagramSocketImpl" );

      adjustSubClasses ( "java.net.DatagramSocketImpl" );

      }

     }
     else if ( currmethod.getSignature().equals ( "<java.net.InetAddress: <clinit>':():void>" ) )
     {

      if ( clgb.getNode ( "java.net.InetAddressImpl" ) != null )
      {

      instancetypesHT.put ( "java.net.InetAddressImpl" , new SootClass ( "java.net.InetAddressImpl" ) );

      instanceSet.set ( ( ( Integer ) bitMap.get ( "java.net.InetAddressImpl" ) ).intValue() );

      newinstances.add ( "java.net.InetAddressImpl" );

      adjustSubClasses ( "java.net.InetAddressImpl" );

      }

     }
     else if ( currmethod.getSignature().equals ( "<java.net.MulticastSocket: void create(int,java.net.InetAddress)>" ) )
     {

      if ( clgb.getNode ( "java.net.DatagramSocketImpl" ) != null )
      {


      instancetypesHT.put ( "java.net.DatagramSocketImpl" , new SootClass ( "java.net.DatagramSocketImpl" ) );

      instanceSet.set ( ( ( Integer ) bitMap.get ( "java.net.DatagramSocketImpl" ) ).intValue() );

      newinstances.add ( "java.net.DatagramSocketImpl" );

      adjustSubClasses ( "java.net.DatagramSocketImpl" );

     }

     }
     else if ( currmethod.getSignature().equals ( "<java.net.URL: java.net.URLStreamHandler getURLStreamHandler(java.lang.String)>" ) )
     {

      if ( clgb.getNode ( "java.net.URLStreamHandler" ) != null )
      {

      instancetypesHT.put ( "java.net.URLStreamHandler" , new SootClass ( "java.net.URLStreamHandler" ) );

      instanceSet.set ( ( ( Integer ) bitMap.get ( "java.net.URLStreamHandler" ) ).intValue() );

      newinstances.add ( "java.net.URLStreamHandler" );

      adjustSubClasses ( "java.net.URLStreamHandler" );

      }

     }
     else if ( currmethod.getSignature().equals ( "<java.net.URLConnection: java.net.ContentHandler lookupContentHandlerClassFor(java.lang.String)>" ) )
     {

      if ( clgb.getNode ( "java.net.ContentHandler" ) != null )
      {

      instancetypesHT.put ( "java.net.ContentHandler" , new SootClass ( "java.net.ContentHandler" ) ); 

      instanceSet.set ( ( ( Integer ) bitMap.get ( "java.net.ContentHandler" ) ).intValue() );

      newinstances.add ( "java.net.ContentHandler" );

      adjustSubClasses ( "java.net.ContentHandler" );

      }

     }
     else if ( currmethod.getSignature().equals ( "<java.security.Provider: java.security.Provider loadProvider(java.lang.String)>" ) )
     {

      if ( clgb.getNode ( "java.security.Provider" ) != null )
      {

      instancetypesHT.put ( "java.security.Provider" , new SootClass ( "java.security.Provider" ) );

      instanceSet.set ( ( ( Integer ) bitMap.get ( "java.security.Provider" ) ).intValue() );

      newinstances.add ( "java.security.Provider" );
      
      adjustSubClasses ( "java.security.Provider" );

      }

     }
     else if ( currmethod.getSignature().equals ( "<java.security.Security: java.lang.Object getImpl(java.lang.String,java.lang.String,java.lang.String)>" ) )
     {

      if ( clgb.getNode ( "java.security.KeyPairGenerator" ) != null )
      {

      instancetypesHT.put ( "java.security.KeyPairGenerator" , new SootClass ( "java.security.KeyPairGenerator" ) );

      instanceSet.set ( ( ( Integer ) bitMap.get ( "java.security.KeyPairGenerator" ) ).intValue() );

      newinstances.add ( "java.security.KeyPairGenerator" );

      adjustSubClasses ( "java.security.KeyPairGenerator" );

      }

      if ( clgb.getNode ( "java.security.MessageDigest" ) != null )
      {

      instancetypesHT.put ( "java.security.MessageDigest" , new SootClass ( "java.security.MessageDigest" ) );

      instanceSet.set ( ( ( Integer ) bitMap.get ( "java.security.MessageDigest" ) ).intValue() );

      newinstances.add ( "java.security.MessageDigest" );

      adjustSubClasses ( "java.security.MessageDigest" );

      }

      if ( clgb.getNode ( "java.security.Signature" ) != null )
      {

      instancetypesHT.put ( "java.security.Signature" , new SootClass ( "java.security.Signature" ) );

      instanceSet.set ( ( ( Integer ) bitMap.get ( "java.security.Signature" ) ).intValue() );

      newinstances.add ( "java.security.Signature" );

      adjustSubClasses ( "java.security.Signature" );

      }

     }
     else if ( currmethod.getSignature().equals ( "<java.util.ResourceBundle: java.util.ResourceBundle findBundle(java.lang.String,java.lang.StringBuffer,java.lang.ClassLoader,boolean)>" ) )
     {

      if ( clgb.getNode ( "java.util.ResourceBundle" ) != null )
      {

      instancetypesHT.put ( "java.util.ResourceBundle" , new SootClass ( "java.util.ResourceBundle" ) );
      
      instanceSet.set ( ( ( Integer ) bitMap.get ( "java.util.ResourceBundle" ) ).intValue() );

      newinstances.add ( "java.util.ResourceBundle" );

      adjustSubClasses ( "java.util.ResourceBundle" );

     }

     }
     else if ( currmethod.getSignature().equals ( "<java.rmi.registry.LocateRegistry: java.rmi.registry.Registry createRegistry(int)>" ) )
     {

      if ( clgb.getNode ( "java.rmi.registry.RegistryHandler" ) != null )
      {
 
      instancetypesHT.put ( "java.rmi.registry.RegistryHandler" , new SootClass ( "java.rmi.registry.RegistryHandler" ) );

      instanceSet.set ( ( ( Integer ) bitMap.get ( "java.rmi.registry.RegistryHandler" ) ).intValue() );

      newinstances.add ( "java.rmi.registry.RegistryHandler" );

      adjustSubClasses ( "java.rmi.registry.RegistryHandler" );

      }

     }
     else if ( currmethod.getSignature().equals ( "<java.rmi.server.RMIClassLoader: java.rmi.server.LoaderHandler getHandler()>" ) )
     {

      if ( clgb.getNode ( "java.rmi.server.LoaderHandler" ) != null )
      {

      instancetypesHT.put ( "java.rmi.server.LoaderHandler" , new SootClass ( "java.rmi.server.LoaderHandler" ) );
      
      instanceSet.set ( ( ( Integer ) bitMap.get ( "java.rmi.server.LoaderHandler" ) ).intValue() );

      newinstances.add ( "java.rmi.server.LoaderHandler" );

      adjustSubClasses ( "java.rmi.server.LoaderHandler" );

      }

     }
     else if ( currmethod.getSignature().equals ("<java.rmi.server.RemoteObject: void readObject(java.io.ObjectInputStream)>" ) )
     {

      if ( clgb.getNode ( "java.rmi.server.RemoteRef" ) != null )
      {

      instancetypesHT.put ( "java.rmi.server.RemoteRef" , new SootClass ( "java.rmi.server.RemoteRef" ) );

      instanceSet.set ( ( ( Integer ) bitMap.get ( "java.rmi.server.RemoteRef" ) ).intValue() );

      newinstances.add ( "java.rmi.server.RemoteRef" );

      adjustSubClasses ( "java.rmi.server.RemoteRef" );

      }

     }
     else if ( currmethod.getSignature().equals ( "<java.rmi.server.RemoteServer: java.lang.String getClientHost()>" ) )
     {

      if ( clgb.getNode ( "java.rmi.server.ServerRef" ) != null )
      {

      instancetypesHT.put ( "java.rmi.server.ServerRef" , new SootClass ( "java.rmi.server.ServerRef" ) );

      instanceSet.set ( ( ( Integer ) bitMap.get ( "java.rmi.server.ServerRef" ) ).intValue() );

      newinstances.add ( "java.rmi.server.ServerRef" );
      
      adjustSubClasses ( "java.rmi.server.ServerRef" );

      }
      
     }
     else if ( currmethod.getSignature().equals ( "<java.rmi.server.UnicastRemoteObject: java.rmi.server.RemoteStub exportObject(java.rmi.Remote)>" ) )
     {

      if ( clgb.getNode ( "java.rmi.server.ServerRef" ) != null )
      {

      instancetypesHT.put ( "java.rmi.server.ServerRef" , new SootClass ( "java.rmi.server.ServerRef" ) );

      instanceSet.set ( ( ( Integer ) bitMap.get ( "java.rmi.server.ServerRef" ) ).intValue() );
      
      newinstances.add ( "java.rmi.server.ServerRef" );

      adjustSubClasses ( "java.rmi.server.ServerRef" );

      }

     }
     
/* NEXT CATEGORY */

     else if ( currmethod.getSignature().equals ( "<java.lang.Object: java.lang.Class getClass()>" ) )
     {

      if ( clgb.getNode ( "java.lang.Class" ) != null )
      {
      
      instancetypesHT.put ( "java.lang.Class" , new SootClass ( "java.lang.Class" ) );

      instanceSet.set ( ( ( Integer ) bitMap.get ( "java.lang.Class" ) ).intValue() );
      
      adjustSubClasses ( "java.lang.Class" );
       
      }
      
     }

    else if ( currmethod.getSignature().equals ( "<java.lang.Class: java.lang.Class forName(java.lang.String)>" ) )
    {
 
      if ( clgb.getNode ( "java.lang.Class" ) != null )
      {
    
       instancetypesHT.put ( "java.lang.Class" , new SootClass ( "java.lang.Class" ) );

       instanceSet.set ( ( ( Integer ) bitMap.get ( "java.lang.Class" ) ).intValue() );

       adjustSubClasses ( "java.lang.Class" );
    
      }
       
     }

    else if ( currmethod.getSignature().equals ( "<java.lang.Class: java.lang.ClassLoader getClassLoader()>" ) )   
    {

      if ( clgb.getNode ( "java.lang.ClassLoader" ) != null ) 
      {
       
       instancetypesHT.put ( "java.lang.ClassLoader" , new SootClass ( "java.lang.ClassLoader" ) );

       instanceSet.set ( ( ( Integer ) bitMap.get ( "java.lang.ClassLoader" ) ).intValue() );
      
       adjustSubClasses ( "java.lang.ClassLoader" );

      }
    
     }
     else if ( currmethod.getSignature().equals ( "<java.lang.Class: java.lang.Class getSuperclass()>" ) )
     {

         
      if ( clgb.getNode ( "java.lang.Class" ) != null )
      {
 
       instancetypesHT.put ( "java.lang.Class" , new SootClass ( "java.lang.Class" ) );

       instanceSet.set ( ( ( Integer ) bitMap.get ( "java.lang.Class" ) ).intValue() );
       
       adjustSubClasses ( "java.lang.Class" );
       
      }
      
     }
     else if ( currmethod.getSignature().equals ( "<java.lang.Class: java.lang.Class[] getInterfaces()>" ) )
     {
          
      if ( clgb.getNode ( "java.lang.Class" ) != null )
      {

       instancetypesHT.put ( "java.lang.Class" , new SootClass ( "java.lang.Class" ) );

       instanceSet.set ( ( ( Integer ) bitMap.get ( "java.lang.Class" ) ).intValue() );
    
       adjustSubClasses ( "java.lang.Class" );
  
      }
     
     }
     else if ( currmethod.getSignature().equals ( "<java.lang.Class: java.lang.Class getComponentType()>" ) )
     {

      if ( clgb.getNode ( "java.lang.Class" ) != null )
      {
       
       instancetypesHT.put ( "java.lang.Class" , new SootClass ( "java.lang.Class" ) );

       instanceSet.set ( ( ( Integer ) bitMap.get ( "java.lang.Class" ) ).intValue() );
      
       adjustSubClasses ( "java.lang.Class" );

      }

     }
     else if ( currmethod.getSignature().equals ( "<java.lang.Class: java.lang.Class getPrimitiveClass(java.lang.String)>" ) )
     {

      if ( clgb.getNode ( "java.lang.Class" ) != null )
      {
       
       instancetypesHT.put ( "java.lang.Class" , new SootClass ( "java.lang.Class" ) );

       instanceSet.set ( ( ( Integer ) bitMap.get ( "java.lang.Class" ) ).intValue() );
      
       adjustSubClasses ( "java.lang.Class" );

      }
    
     }
     else if ( currmethod.getSignature().equals ( "<java.lang.Class: java.lang.reflect.Field[] getFields0(int)>" ) )
     {

      if ( clgb.getNode ( "java.lang.reflect.Field" ) != null )
      {
       
       instancetypesHT.put ( "java.lang.reflect.Field" , new SootClass ( "java.lang.reflect.Field" ) );

       instanceSet.set ( ( ( Integer ) bitMap.get ( "java.lang.reflect.Field" ) ).intValue() );
       
       adjustSubClasses ( "java.lang.reflect.Field" );
       
      }
      
     }
     else if ( currmethod.getSignature().equals ( "<java.lang.Class: java.lang.reflect.Method getMethod0(java.lang.String,java.lang.Class[],int)>" ) )
     {

      if ( clgb.getNode ( "java.lang.reflect.Method" ) != null )
      {
       
       instancetypesHT.put ( "java.lang.reflect.Method" , new SootClass ( "java.lang.reflect.Method" ) );

       instanceSet.set ( ( ( Integer ) bitMap.get ( "java.lang.reflect.Method" ) ).intValue() );

       adjustSubClasses ( "java.lang.reflect.Method" );

      }
       
     }
     else if ( currmethod.getSignature().equals ( "<java.lang.Class: java.lang.reflect.Constructor getConstructor0(java.lang.Class[],int)>" ) )
     {

      if ( clgb.getNode ( "java.lang.reflect.Constructor" ) != null )
      {
       
       instancetypesHT.put ( "java.lang.reflect.Constructor" , new SootClass ( "java.lang.reflect.Constructor" ) );

       instanceSet.set ( ( ( Integer ) bitMap.get ( "java.lang.reflect.Constructor" ) ).intValue() );

       adjustSubClasses ( "java.lang.reflect.Constructor" );

      }
       
     }
     else if ( currmethod.getSignature().equals ( "<java.lang.System: java.util.Properties initProperties(java.util.Properties)>" ) )
     {

      if ( clgb.getNode ( "java.util.Properties" ) != null )
      {
       
       instancetypesHT.put ( "java.util.Properties" , new SootClass ( "java.util.Properties" ) );

       instanceSet.set ( ( ( Integer ) bitMap.get ( "java.util.Properties" ) ).intValue() );

       adjustSubClasses ( "java.util.Properties" );

      }

     }
     else if ( currmethod.getSignature().equals ( "<java.lang.Thread: java.lang.Thread currentThread()>" ) )
     {

      if ( clgb.getNode ( "java.lang.Thread" ) != null )
      {
       
       instancetypesHT.put ( "java.lang.Thread" , new SootClass ( "java.lang.Thread" ) );

       instanceSet.set ( ( ( Integer ) bitMap.get ( "java.lang.Thread" ) ).intValue() );

       adjustSubClasses ( "java.lang.Thread" );
    
      }

     }
     else if ( currmethod.getSignature().equals ( "<java.lang.ClassLoader: java.lang.Class defineClass0(java.lang.String,byte[],int,int)>" ) )
     {

      if ( clgb.getNode ( "java.lang.Class" ) != null )
      {
       
       instancetypesHT.put ( "java.lang.Class" , new SootClass ( "java.lang.Class" ) );

       instanceSet.set ( ( ( Integer ) bitMap.get ( "java.lang.Class" ) ).intValue() );

       adjustSubClasses ( "java.lang.Class" );
    
      }
       
     } 
     else if ( currmethod.getSignature().equals ( "<java.lang.ClassLoader: java.lang.Class findSystemClass0(java.lang.String)>" ) )
     {

      if ( clgb.getNode ( "java.lang.Class" ) != null )
      {
    
       instancetypesHT.put ( "java.lang.Class" , new SootClass ( "java.lang.Class" ) );

       instanceSet.set ( ( ( Integer ) bitMap.get ( "java.lang.Class" ) ).intValue() );
       
       adjustSubClasses ( "java.lang.Class" );
       
      }
      
     }
     else if ( currmethod.getSignature().equals ( "<java.lang.ClassLoader: java.io.InputStream getSystemResourceAsStream0(java.lang.String)>" ) )
     {

      if ( clgb.getNode ( "java.io.InputStream" ) != null )
      {
    
       instancetypesHT.put ( "java.io.InputStream" , new SootClass ( "java.io.InputStream" ) );

       instanceSet.set ( ( ( Integer ) bitMap.get ( "java.io.InputStream" ) ).intValue() );
    
       adjustSubClasses ( "java.lang.InputStream" );
    
      }
    
     }
     else if ( currmethod.getSignature().equals ( "<java.lang.SecurityManager: java.lang.Class[] getClassContext()>" ) )
     {

      if ( clgb.getNode ( "java.lang.Class" ) != null )
      {
       
       instancetypesHT.put ( "java.lang.Class" , new SootClass ( "java.lang.Class" ) );

       instanceSet.set ( ( ( Integer ) bitMap.get ( "java.lang.Class" ) ).intValue() );
       
       adjustSubClasses ( "java.lang.Class" );
       
      }
      
     }

     else if ( currmethod.getSignature().equals ( "<java.lang.SecurityManager: java.lang.ClassLoader currentClassLoader()>" ) )
     {

      if ( clgb.getNode ( "java.lang.ClassLoader" ) != null )
      {
    
       instancetypesHT.put ( "java.lang.ClassLoader" , new SootClass ( "java.lang.ClassLoader" ) );

       instanceSet.set ( ( ( Integer ) bitMap.get ( "java.lang.ClassLoader" ) ).intValue() );
    
       adjustSubClasses ( "java.lang.ClassLoader" );
    
      }
    
     }
     else if ( currmethod.getSignature().equals ( "<java.lang.SecurityManager: java.lang.Class currentLoadedClass0()>" ) )
     {

      if ( clgb.getNode ( "java.lang.Class" ) != null )
      {
       
       instancetypesHT.put ( "java.lang.Class" , new SootClass ( "java.lang.Class" ) );

       instanceSet.set ( ( ( Integer ) bitMap.get ( "java.lang.Class" ) ).intValue() );
       
       adjustSubClasses ( "java.lang.Class" );
       
      }
      
     }
     else if ( currmethod.getSignature().equals ( "<java.io.ObjectInputStream: java.lang.Class loadClass0(java.lang.Class,java.lang.String)>" ) )
     {

      if ( clgb.getNode ( "java.lang.Class" ) != null )
      {
    
       instancetypesHT.put ( "java.lang.Class" , new SootClass ( "java.lang.Class" ) );
    
       instanceSet.set ( ( ( Integer ) bitMap.get ( "java.lang.Class" ) ).intValue() );

       adjustSubClasses ( "java.lang.Class" );
    
      }
    
     }
     else if ( currmethod.getSignature().equals ( "<java.lang.Runtime: java.lang.Processjava.lang.Runtime.execInternal(java.lang.String[],java.lang.String[]) execInternal(java.lang.String[],java.lang.String[])>" ) )
     {

      if ( clgb.getNode ( "java.lang.Process" ) != null )
      {
       
       instancetypesHT.put ( "java.lang.Process" , new SootClass ( "java.lang.Process" ) );

       instanceSet.set ( ( ( Integer ) bitMap.get ( "java.lang.Process" ) ).intValue() );
       
       adjustSubClasses ( "java.lang.Process" );
       
      }
      
     }
     else if ( currmethod.getSignature().equals ( "<java.io.FileDescriptor: java.io.FileDescriptor initSystemFD(java.io.FileDescriptor,int)>" ) )
     {

      if ( clgb.getNode ( "java.io.FileDescriptor" ) != null )
      {
       
       instancetypesHT.put ( "java.io.FileDescriptor" , new SootClass ( "java.io.FileDescriptor" ) );

       instanceSet.set ( ( ( Integer ) bitMap.get ( "java.io.FileDescriptor" ) ).intValue() );
       
       adjustSubClasses ( "java.io.FileDescriptor" );
       
      }
      
     }
     else if ( currmethod.getSignature().equals ( "<java.util.ResourceBundle: java.lang.Class[] getClassContext()>" ) )
     {

      if ( clgb.getNode ( "java.lang.Class" ) != null )
      {
       
       instancetypesHT.put ( "java.lang.Class" , new SootClass ( "java.lang.Class" ) );

       instanceSet.set ( ( ( Integer ) bitMap.get ( "java.lang.Class" ) ).intValue() );
       
       adjustSubClasses ( "java.lang.Class" );
    
      }
       
     } 
     else if ( currmethod.getSignature().equals ( "<java.io.ObjectStreamClass: java.io.ObjectStreamField[] getFields0(java.lang.Class)>" ) )
     {

      if ( clgb.getNode ( "java.io.ObjectStreamField" ) != null )
      {
       
       instancetypesHT.put ( "java.io.ObjectStreamField" , new SootClass ( "java.io.ObjectStreamField" ) );

       instanceSet.set ( ( ( Integer ) bitMap.get ( "java.io.ObjectStreamField" ) ).intValue() );
      
       adjustSubClasses ( "java.io.ObjectStreamField" );
       
      }
      
     }
 








    } catch ( java.lang.RuntimeException e ) {}

   }

 }


 private void adjustSubClasses ( String s ) { 

   ClassNode cn = clgb.getNode ( s );

   Set subclassnodes = clgb.getAllSubClassesOf ( cn );

   Iterator subclassnodesit = subclassnodes.iterator();
     
   while ( subclassnodesit.hasNext() )
   {
 
    try {
  
    ClassNode subcn = ( ClassNode ) subclassnodesit.next();

    String name = subcn.getSootClass().getName();

    instancetypesHT.put ( name , subcn.getSootClass() );

    instanceSet.set ( ( ( Integer ) bitMap.get ( name ) ).intValue() );

    } catch ( java.lang.RuntimeException e ) {}

   }

 }








}



















