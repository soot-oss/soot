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
class CHA {
   private SootClassManager cm;
   private ClassGraphBuilder clgb;
   private CallGraphBuilder cagb;
   private AllClassFinder allclassfinder;
   private List entrypoints = new ArrayList();
   private String className;
   CHA(){}


   CHA( AllClassFinder allclassfinder ) {
      this.allclassfinder = allclassfinder;
      List paramList = new ArrayList();
      clgb = allclassfinder.getClassGraphBuilder();
      cm = clgb.getManager();
      cagb = new CallGraphBuilder( clgb );
   }


   ClassGraphBuilder getClassGraphBuilder() {
      return clgb;
   }


   CallGraphBuilder getCallGraphBuilder() {
      return cagb;
   }


   List getEntryMethods() {
      return entrypoints;
   }


   String getMainClassName() {
      return className;
   }


   AllClassFinder getAllClassFinder() {
      return allclassfinder;
   }


   void buildConservativeCallGraph ( String className, AllClassFinder allclassfinder ) {
      this.className = className;
      this.allclassfinder = allclassfinder;
      List paramList = new ArrayList();
      Collection callGraph = null;
      clgb = allclassfinder.getClassGraphBuilder();
      cm = clgb.getManager();
      System.out.print ("Jimplifying methods and building the call graph");
      cagb = new CallGraphBuilder( clgb );
      // BUILDS CALL GRAPH STARTING FROM MAIN() METHOD 

      paramList.add("java.lang.String[]");
      cagb.buildCallGraph( className , "main" , paramList );
      // System.out.println ( "MAIN CLASS "+className );   

      SootMethod mainmethod = ca.mcgill.sable.soot.jimple.toolkit.invoke.Helper.getMethod(cm.getClass(className), "main", paramList);
      entrypoints.add ( mainmethod );
      Iterator startit = clgb.startmethods.iterator();
      // BUILDS CALL GRAPH STARTING FROM START METHODS 

      while (startit.hasNext())
      {
         try {
            SootMethod sootmethod = (SootMethod) startit.next();
            entrypoints.add ( sootmethod );
            cagb.buildCallGraph( sootmethod );
         }
         catch ( java.lang.RuntimeException e1 ) {}

      }

      Iterator runit = clgb.runmethods.iterator();
      // BUILDS CALL GRAPH STARTING FROM RUN METHODS

      while (runit.hasNext())
      {
         try {
            SootMethod sootmethod = (SootMethod) runit.next();
            entrypoints.add ( sootmethod );
            cagb.buildCallGraph( sootmethod );
         }
         catch ( java.lang.RuntimeException e1 ) {}

      }

      Collection methods = cagb.getCallGraph();
      Iterator methodsit = methods.iterator();
      while ( methodsit.hasNext() )
      {
         MethodNode mn = (MethodNode) methodsit.next();
         if ( ( mn.getMethod().getName().equals("finalize") || mn.getMethod().getName().equals("<clinit>") ) || mn.getMethod().getName().equals("initializeSystemClass") )
         entrypoints.add ( mn.getMethod() );
      }

      System.out.println("Done");
      System.out.println();
      System.out.println ("Call graph characterestics : ");
      System.out.println ("--------------------------------------- ");
      System.out.println();
      System.out.println("TOTAL NUMBER OF JIMPLE STATEMENTS              : "+Jimplifier.cgnumstmts);
      System.out.println("TOTAL NUMBER OF JIMPLE STATEMENTS IN BENCHMARK : "+Jimplifier.cgbenchnumstmts);
      System.out.println();
      System.out.println("TOTAL NUMBER OF METHODS                        : "+Jimplifier.totnummethods);
      System.out.println("TOTAL NUMBER OF NATIVE METHODS                 : "+Jimplifier.totnativenummethods);
      System.out.println("TOTAL NUMBER OF ABSTRACT METHODS               : "+Jimplifier.totabstractnummethods);
      System.out.println();
      System.out.println("TOTAL NUMBER OF METHODS IN BENCHMARK           : "+Jimplifier.totnumbenchmethods);
      System.out.println("TOTAL NUMBER OF NATIVE METHODS IN BENCHMARK    : "+Jimplifier.totnativebenchnummethods);
      System.out.println("TOTAL NUMBER OF ABSTRACT METHODS IN BENCHMARK  : "+Jimplifier.totabstractbenchnummethods);
      System.out.println();
      System.out.println("NUMBER OF CALLSITES                            : "+cagb.getSitesNum());
      System.out.println("NUMBER OF CALLGRAPH EDGES                      : "+cagb.getEdgesNum());
      System.out.println();
   }


}




