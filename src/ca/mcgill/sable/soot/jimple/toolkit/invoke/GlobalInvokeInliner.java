
package ca.mcgill.sable.soot.jimple.toolkit.invoke;

// package ca.mcgill.sable.soot.virtualCalls;

// import java.util.*;
import java.io.*;
import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.soot.jimple.*;
import ca.mcgill.sable.util.*;
import java.util.*;
import ca.mcgill.sable.soot.baf.*; 
import ca.mcgill.sable.soot.coffi.*;
// import ca.mcgill.sable.soot.sideEffect.*;

public class GlobalInvokeInliner {



 public static Set inlineInvokes(InvokeGraph g)
 {

        Resolver resolver = new Resolver( g.getCHA().getClassGraphBuilder() );

        resolver.resolveMethods( g.getCHA().getCallGraphBuilder().getCallGraph() );

        Inliner inliner = new Inliner( g.getCHA().getCallGraphBuilder() );

        inliner.setClassesToProcess( null, false );

        // inliner.setImprovedCallSites ( vta.ImprovedCallSites ); 

        return inliner.examineMethodsToFixCallSites( g.getCHA().getCallGraphBuilder().getCallGraph(), resolver );

 }



 public static Set inlineInvokes(InvokeGraph g, List classesToProcess)
 {

        List classesbyName = new ArrayList();

        Iterator processit = classesToProcess.iterator();

        while ( processit.hasNext() )
        {
         SootClass sc = (SootClass) processit.next();
         classesbyName.add(sc.getName());  
        }   

        Resolver resolver = new Resolver( g.getCHA().getClassGraphBuilder() );

        resolver.resolveMethods( g.getCHA().getCallGraphBuilder().getCallGraph() );

        Inliner inliner = new Inliner( g.getCHA().getCallGraphBuilder() );

        inliner.setClassesToProcess( classesbyName, false );

        // inliner.setImprovedCallSites ( vta.ImprovedCallSites ); 

        return inliner.examineMethodsToFixCallSites( g.getCHA().getCallGraphBuilder().getCallGraph(), resolver );

 }

 

 public static Set inlineInvokes(InvokeGraph g, List classesToProcess, boolean changeLibraries)
 {

        List classesbyName = new ArrayList();

        Iterator processit = classesToProcess.iterator();

        while ( processit.hasNext() )
        {
         SootClass sc = (SootClass) processit.next();
         classesbyName.add(sc.getName());  
        }   

        Resolver resolver = new Resolver( g.getCHA().getClassGraphBuilder() );

        resolver.resolveMethods( g.getCHA().getCallGraphBuilder().getCallGraph() );

        Inliner inliner = new Inliner( g.getCHA().getCallGraphBuilder() );

        inliner.setClassesToProcess( classesbyName, changeLibraries );

        // inliner.setImprovedCallSites ( vta.ImprovedCallSites ); 

        return inliner.examineMethodsToFixCallSites( g.getCHA().getCallGraphBuilder().getCallGraph(), resolver );

 }



}










