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



  public InvokeGraph ( SootClassManager cm ) { manager = cm; }



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

	AllClassFinder allclassfinder = new AllClassFinder();

    allclassfinder.includeAllClasses(MainClass.getManager(), bclasses);

	Map allclassesHT = allclassfinder.getAllClassesHT();

    cha = new CHA();

    cha.buildConservativeCallGraph ( MainClass.getName(), allclassfinder );

    entrymethods = cha.getEntryMethods();

  }













  

  public SootClassManager getManager() { return manager; }
  
  public List getEntryMethods() { return entrymethods; }
  
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
      if ( ( method.getName().equals("<clinit>") || method.getName().equals("finalize") ) || ( getEntryMethods().contains(method) ) )
      reachablemethods.add ( method );
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




