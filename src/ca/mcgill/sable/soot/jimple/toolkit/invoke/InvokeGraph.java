// package ca.mcgill.sable.soot.virtualCalls;


package ca.mcgill.sable.soot.jimple.toolkit.invoke;

// import java.util.*;
import java.io.*;
import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.soot.jimple.*;
import ca.mcgill.sable.util.*;
import java.util.*;
import ca.mcgill.sable.soot.baf.*; 
import ca.mcgill.sable.soot.coffi.*;
// import ca.mcgill.sable.soot.sideEffect.*;

public class InvokeGraph {
	
  private SootClassManager manager; 

  private List entrymethods;

  private CHA cha;

  private RTA rta;
  
  private DTA dta;

  private VTA vta;



  // public InvokeGraph ( SootClassManager cm ) { manager = cm; }



  /*
  public InvokeGraph ( SootClassManager cm, String MainClass ) {

    manager = cm;

	SootClassFinder bclassfinder = new SootClassFinder();
	
	ArrayList bclasses = bclassfinder.getSootClasses(MainClass);

	AllClassFinder allclassfinder = new AllClassFinder();

    allclassfinder.includeAllClasses(cm, bclasses);

	Map allclassesHT = allclassfinder.getAllClassesHT();

    cha = new CHA();

    cha.buildConservativeCallGraph ( MainClass, allclassfinder );

    entrymethods = cha.getEntryMethods();

  }
  */





  public InvokeGraph ( SootClass MainClass ) {

    manager = MainClass.getManager();

	// SootClassFinder bclassfinder = new SootClassFinder();
	
	// ArrayList bclasses = bclassfinder.getSootClasses(MainClass);

    ArrayList bclasses = new ArrayList();

    bclasses.add ( MainClass ); 

	AllClassFinder allclassfinder = new AllClassFinder(false);

    allclassfinder.includeAllClasses(MainClass.getManager(), bclasses);

	Map allclassesHT = allclassfinder.getAllClassesHT();

    cha = new CHA(allclassfinder);

    // cha.buildConservativeCallGraph ( MainClass.getName(), allclassfinder );

    entrymethods = cha.getEntryMethods();

  }






  
  InvokeGraph ( SootClass MainClass, boolean isCHA ) {

    manager = MainClass.getManager();

	// SootClassFinder bclassfinder = new SootClassFinder();
	
	// ArrayList bclasses = bclassfinder.getSootClasses(MainClass);

    ArrayList bclasses = new ArrayList();

    bclasses.add ( MainClass ); 

	AllClassFinder allclassfinder = new AllClassFinder();

    allclassfinder.includeAllClasses(MainClass.getManager(), bclasses);

	Map allclassesHT = allclassfinder.getAllClassesHT();

    cha = new CHA();

    if ( isCHA )
    cha.buildConservativeCallGraph ( MainClass.getName(), allclassfinder );

    entrymethods = cha.getEntryMethods();

  }











  

  public SootClassManager getManager() { return manager; }










  
  public List getEntryMethods() { return entrymethods; }




  public void setEntryMethods(List entrymethods) { this.entrymethods = entrymethods;}





  public void addEntryMethod(SootMethod method) { 

   if ( ! entrymethods.contains(method) ) entrymethods.add(method); 

  }






  public void removeEntryMethod(SootMethod method) {  

   if ( entrymethods.contains(method) ) 
   entrymethods.remove(method);

  }





  public boolean isEntryMethod(SootMethod method) {

   return ( entrymethods.contains(method) ); 

  }









  
  public List getReachableMethods() { 

   List reachablemethods = new ArrayList();

   Iterator callgraphit = cha.getCallGraphBuilder().getCallGraph().iterator();
    
   while ( callgraphit.hasNext() )
   {

     MethodNode mn = (MethodNode) callgraphit.next();

     SootMethod method = mn.getMethod(); 

     if ( mn.incomingedges > 0 )
     reachablemethods.add ( method );
     else
     {

      if ( ( ( ( method.getName().equals("<clinit>") || method.getName().equals("finalize") ) || ( getEntryMethods().contains(method) ) ) || method.getName().equals("initializeSystemClass") ) || method.getName().equals("main") )
      {
       reachablemethods.add ( method );
      }

     }

    }

    return reachablemethods;

   }













  public List getReachableClasses() {

   HashMap reachableclassesHT = new HashMap();
  
   List reachableclasses = new ArrayList();

   {

      Iterator methodsit = getReachableMethods().iterator();
      while ( methodsit.hasNext() )
      {

       SootMethod method = (SootMethod) methodsit.next();

       if ( reachableclassesHT.get(method.getDeclaringClass().getName()) == null )              
       {

        reachableclassesHT.put( method.getDeclaringClass().getName(), method.getDeclaringClass() );
        reachableclasses.add( method.getDeclaringClass() );

       }

      }

   }
   
   return reachableclasses;

  }











  public SootMethod getDeclaringMethod(InvokeExpr ie) {

   CallGraphBuilder cagb = cha.getCallGraphBuilder();
   return ( cagb.getContainerMethod(ie).getMethod() );

  }









  public List getInvokeExprsIn(SootMethod m) {
          
   CallGraphBuilder cagb = cha.getCallGraphBuilder();   
   MethodNode mn = cagb.getNode(m);

   // List invokeexprs = new ArrayList();

   // Iterator callsitesit = mn.getCallSitesAsList().iterator();
    
   // while ( callsitesit.hasNext() )
   // invokeexprs.add ( (( CallSite ) callsitesit.next()).getInvokeExpr() ); 

   if ( mn.getInvokeExprs() == null )
   return new ArrayList();

   return mn.getInvokeExprs(); 

  }











  public List getMethodsInvokedFrom(SootMethod m) {

   List methodsinvoked = new ArrayList();
  
   CallGraphBuilder cagb = cha.getCallGraphBuilder();   
   MethodNode mn = cagb.getNode(m);
   
   Iterator allpossmethodsit = mn.getAllPossibleMethods().iterator();

   while (allpossmethodsit.hasNext())
   methodsinvoked.add( ((MethodNode) allpossmethodsit.next()).getMethod() );

   return methodsinvoked;

  }





  





  
  public List getReachableMethodsFrom(SootMethod m) {

   return ( getReachableMethodsFrom(m, new ArrayList()) );

  }








  

  List getReachableMethodsFrom(SootMethod m, List listSoFar) {

   CallGraphBuilder cagb = cha.getCallGraphBuilder();   
   MethodNode mn = cagb.getNode(m);
   
   Iterator allpossmethodsit = mn.getAllPossibleMethods().iterator();

   while (allpossmethodsit.hasNext())
   {
     MethodNode nextmn = (MethodNode) allpossmethodsit.next();

     if ( ! ( listSoFar.contains (nextmn.getMethod()) ) )  
     {
      listSoFar.add ( nextmn.getMethod() );
      getReachableMethodsFrom (nextmn.getMethod(), listSoFar);
     }

   }

   return listSoFar;

  }

  






  public boolean isMethodReachable ( SootMethod m ) 
  {

   return ( getReachableMethods().contains(m) );

  }






  

  public boolean isClassReachable ( SootClass c ) 
  {

   return ( getReachableClasses().contains(c) );

  }








  public boolean isMethodReachableFrom ( SootMethod from, SootMethod to )
  {

   return ( getReachableMethodsFrom(from).contains(to) );

  }








  
  public boolean isClassReachableFrom ( SootMethod from, SootClass to )
  {

   return ( getReachableClassesFrom(from).contains(to) );

  }







  

  public List getReachableClassesFrom(SootMethod m) {

   HashMap reachableclassesHT = new HashMap();
  
   List reachableclasses = new ArrayList();

   {

      Iterator methodsit = getReachableMethodsFrom(m).iterator();
      while ( methodsit.hasNext() )
      {

       SootMethod method = (SootMethod) methodsit.next();

       if ( reachableclassesHT.get(method.getDeclaringClass().getName()) == null )              
       {

        reachableclassesHT.put( method.getDeclaringClass().getName(), method.getDeclaringClass() );
        reachableclasses.add( method.getDeclaringClass() );

       }

      }

   }
   
   return reachableclasses;

  }
   







   







  public List getInvokerMethodsOf(SootMethod m) {

   List invokermethods = new ArrayList();

   Iterator invokinginvokesit = getInvokersOf(m).iterator();

   while ( invokinginvokesit.hasNext() )
   {

    InvokeExpr ie =  (InvokeExpr) invokinginvokesit.next();
    SootMethod meth = getDeclaringMethod(ie);
 
    if ( ! invokermethods.contains(meth) )
    invokermethods.add(meth); 

   }

   return invokermethods;

  }








  public int getInvokerMethodCountOf(SootMethod m) {

   return ( getInvokerMethodsOf(m).size() );

  }








  public List getTargetsOf(InvokeExpr ie) {

   CallGraphBuilder cagb = cha.getCallGraphBuilder();   
   MethodNode mn = cagb.getContainerMethod(ie);
   CallSite cs = mn.getCallSite(ie);

   List targets = new ArrayList();

   Iterator targetit = cs.getMethods().iterator();
   while ( targetit.hasNext() )
   {
     MethodNode target = (MethodNode) targetit.next(); 
     targets.add (target.getMethod());
   }

   return targets;

  }






  public boolean removeTarget(InvokeExpr ie, SootMethod m) {

   SootMethod enclosingmethod = getDeclaringMethod(ie);
   CallGraphBuilder cagb = cha.getCallGraphBuilder(); 
   MethodNode enclosingmn = cagb.getNode(enclosingmethod);
   CallSite cs = enclosingmn.getCallSite(ie);
   MethodNode targetmn = cagb.getNode(m); 

   if ( cs.getMethods().contains(targetmn) )
   {
    targetmn.incomingedges--;
    targetmn.removeInvokingSite(cs);
    cs.removeMethod(targetmn);
    return true;
   }
   else
   return false;

  }





  
  public boolean addTarget(InvokeExpr ie, SootMethod m) {

   SootMethod enclosingmethod = getDeclaringMethod(ie);
   CallGraphBuilder cagb = cha.getCallGraphBuilder(); 
   MethodNode enclosingmn = cagb.getNode(enclosingmethod);
   CallSite cs = enclosingmn.getCallSite(ie);
   MethodNode targetmn = cagb.getNode(m); 

   if ( ! cs.getMethods().contains(targetmn) )
   {
    targetmn.incomingedges++;
    targetmn.addInvokingSite(cs);
    cs.addMethod(targetmn);
   }
 
   return true;

  }













  public void addInvokeExpr(InvokeExpr ie, SootMethod m) {

    MethodNode enclosingmn = null;

    CallGraphBuilder cagb = cha.getCallGraphBuilder(); 
     
    try {

     enclosingmn = cagb.getNode(m);

    } catch ( java.lang.RuntimeException e ) {

     cagb.CreateNodeAndAddToHT(m);
     enclosingmn = cagb.getNode(m);

    }   
    
   
    Iterator callsitesit = enclosingmn.getCallSites().iterator();

    while ( callsitesit.hasNext() )
    {

     CallSite cs = (CallSite) callsitesit.next();

     if ( cs.getInvokeExpr().equals(ie) )
     return;

    }

   cagb.invokeToContainerMethod.put ( ie, enclosingmn );

   String invokeExprId = Helper.getInvokeExprId( enclosingmn.getCallSites().size() + 1, ie.getMethod() );

   CallSite callSite = new CallSite( invokeExprId , ie );
   callSite.setCallerID ( enclosingmn.getMethod().getSignature()+"$"+( enclosingmn.getCallSites().size() + 1 ) );

   enclosingmn.addCallSite( callSite, new Integer ( enclosingmn.getCallSites().size() + 1 ) );

   if ( ! enclosingmn.getInvokeExprs().contains(ie) )
   enclosingmn.addInvokeExpr( ie );

  }












  public void removeInvokeExpr(InvokeExpr ie, SootMethod m) {

    MethodNode enclosingmn = null;

    CallGraphBuilder cagb = cha.getCallGraphBuilder(); 
     
    try {

     enclosingmn = cagb.getNode(m);

    } catch ( java.lang.RuntimeException e ) {

     cagb.CreateNodeAndAddToHT(m);
     enclosingmn = cagb.getNode(m);

    }   
    
   
    Iterator callsitesit = enclosingmn.getCallSites().iterator();

    while ( callsitesit.hasNext() )
    {

     CallSite cs = (CallSite) callsitesit.next();

     if ( cs.getInvokeExpr().equals(ie) )
     {

      Iterator methit = cs.getMethods().iterator(); 
      while ( methit.hasNext() )
      {

       MethodNode targetmn = (MethodNode) methit.next();
       targetmn.incomingedges--;
       targetmn.removeInvokingSite(cs);
      }

      cs.setMethods(null);

      enclosingmn.removeCallSite(cs);
      enclosingmn.removeInvokeExpr( ie );
      return;
     } 

    }

  }





















  public void addMethod(SootMethod method) {
 
   CallGraphBuilder cagb = cha.getCallGraphBuilder(); 
  
   MethodNode mn = cagb.CreateNodeAndAddToHT(method);

  }









  public void removeMethod(SootMethod method) {
 
   CallGraphBuilder cagb = cha.getCallGraphBuilder(); 
   MethodNode mn = cagb.getNode(method);

   List invokers = new ArrayList();

   Iterator csiter = mn.getInvokingSites().iterator();

   while ( csiter.hasNext() )
   {
     CallSite caller = ( CallSite ) csiter.next();
     caller.removeMethod ( mn );
   }

   cagb.removeNode(method);

  }









  
  /*

  public void addInvokeExpr(InvokeExpr ie, SootMethod m) {

    MethodNode enclosingmn = null;

    CallGraphBuilder cagb = cha.getCallGraphBuilder(); 
     
    try {

     enclosingmn = cagb.getNode(m);

    } catch ( java.lang.RuntimeException e ) {

     cagb.CreateNodeAndAddToHT(m);
     enclosingmn = cagb.getNode(m);

    }   
    
   
    Iterator callsitesit = enclosingmn.getCallSites().iterator();

    while ( callsitesit.hasNext() )
    {

     CallSite cs = (CallSite) callsitesit.next();

     if ( cs.getInvokeExpr().equals(ie) )
     return;

    }

   cagb.invokeToContainerMethod.put ( ie, enclosingmn );

   String invokeExprId = Helper.getInvokeExprId( enclosingmn.getCallSites().size() + 1, ie.getMethod() );

   CallSite callSite = new CallSite( invokeExprId , ie );
   callSite.setCallerID ( enclosingmn.getMethod().getSignature()+"$"+( enclosingmn.getCallSites().size() + 1 ) );

   enclosingmn.addCallSite( callSite, new Integer ( enclosingmn.getCallSites().size() + 1 ) );

   Set possibleMethods = cagb.getAllPossibleMethodsOf( ie, enclosingmn.getMethod());

   Iterator methodIter = possibleMethods.iterator();

   while( methodIter.hasNext() )
   {

	 try {
	  
	  SootMethod possibleMethod = (SootMethod)methodIter.next();

      possibleMethod = cha.getClassGraphBuilder().getNode ( possibleMethod.getDeclaringClass().getName() ).getSootClass().getMethod ( possibleMethod.getName(), possibleMethod.getParameterTypes() );

	  MethodNode possibleMNode;
	  
	  // if possibleMethod has not been yet inserted into the HT then ...
	  if ( ( possibleMNode = (MethodNode) cagb.MethodNodeHT.get(Helper.getFullMethodName(possibleMethod)) ) == null )
	  possibleMNode = cagb.CreateNodeAndAddToHT( possibleMethod );
	  
	  callSite.addMethod( possibleMNode );
      possibleMNode.addInvokingSite( callSite );
	  possibleMNode.addCaller( enclosingmn );
	  possibleMNode.incomingedges++;

      if ( possibleMNode.getMethod().getSignature().equals ( enclosingmn.getMethod().getSignature() ) )
      cagb.recursiveMethods.add ( possibleMNode.getMethod().getSignature() );                                 

	 } catch( RuntimeException e ){}

   } // WHILE

  }

  

  */



  

  


  List invokeExprs;




  /*

  public void addMethod(SootMethod method) {
 
   CallGraphBuilder cagb = cha.getCallGraphBuilder(); 
  
   MethodNode mn = cagb.CreateNodeAndAddToHT(method);

   invokeExprs = new ArrayList();

   try {

    JimpleBody jimpleBody = new JimpleBody( new ClassFileBody ( method ), 0 );

    method.setActiveBody(jimpleBody);

    Iterator stmtIter = jimpleBody.getStmtList().iterator();

    while ( stmtIter.hasNext() ){

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
	
	  });

	 } catch (java.lang.RuntimeException e ){}

    } // WHILE 

   } catch(java.lang.RuntimeException e ){}  

   Iterator invokeIt = invokeExprs.iterator();
 
   while (invokeIt.hasNext())
   {

    InvokeExpr ie = (InvokeExpr) invokeIt.next();

    addInvokeExpr(ie,method);

   }

  }

  */










  public int getTargetCountOf(InvokeExpr ie) {
   
   CallGraphBuilder cagb = cha.getCallGraphBuilder();   
   MethodNode mn = cagb.getContainerMethod(ie);
   CallSite cs = mn.getCallSite(ie);

   return ( cs.getMethods().size() );

  }








  public List getInvokersOf(SootMethod m) {
      
   CallGraphBuilder cagb = cha.getCallGraphBuilder();   
   MethodNode mn = cagb.getNode(m);

   List invokers = new ArrayList();

   Iterator csiter = mn.getInvokingSites().iterator();

   while ( csiter.hasNext() )
   invokers.add ( (( CallSite ) csiter.next() ).getInvokeExpr() );

   return invokers;

  }








  public int getInvokerCountOf(SootMethod m) {
      
   CallGraphBuilder cagb = cha.getCallGraphBuilder();   
   MethodNode mn = cagb.getNode(m);

   return mn.getInvokingSites().size();

  }














  /*
  public void addTarget(InvokeExpr ie, SootMethod m) {

   CallGraphBuilder cagb = cha.getCallGraphBuilder();   
   MethodNode mn = cagb.getContainerMethod(ie);
   CallSite cs = mn.getCallSite(ie);

  }  
  */


  CHA getCHA() { return cha; }  

  void setCHA( CHA cha ) { this.cha = cha; }  
  
  RTA getRTA() { return rta; }

  void setRTA( RTA rta ) { this.rta = rta; }  

  DTA getDTA() { return dta; }

  void setDTA( DTA dta ) { this.dta = dta; }  

  VTA getVTA() { return vta; }

  void setVTA( VTA vta ) { this.vta = vta; }

}








