
// package ca.mcgill.sable.soot.virtualCalls; 

package ca.mcgill.sable.soot.jimple.toolkit.invoke;
import java.io.*;
// import java.util.*;
import ca.mcgill.sable.util.*;
import java.util.*;
class Main {
   Main(String s){
      controller(s);
   }


   Main() {}


   static boolean NOPRINT = true;
   private void controller(String s){
      ca.mcgill.sable.soot.Main.sootClassPath = "/home/profs/hendren/JavaBench/EiffelSuite/benchmarks/illness/classes:/home/acaps/u2/vijay/PIZZA/pizza/classes:/home/profs/hendren/JavaBench/AdaSuite/kalman/classes:/home/profs/hendren/JavaBench/MLSuite/benchmarks/nucleic/classes:/home/profs/hendren/JavaBench/AdaSuite/rudstone/classes:/home/profs/hendren/JavaBench/MLSuite/benchmarks/nucleic/classes:home/profs/hendren/JavaBench/SchemeSuite:/tmp/sablecc-2.9:/home/profs/hendren/JavaBench/FromML/boyer/Classes:/home/acaps/u2/vijay/local/ADABENCH/Dhrystone:/home/acaps/u2/vijay/local/ADALIB:/home/profs/hendren/JavaBench/EiffelSuite/benchmarks/compile_to_c/classes:/home/acaps/u2/vijay/JDKCLASSES:/home/acaps/u2/vijay/local/PUZZLE/puzzle/classes:/home/acaps/u2/vijay/BENCH/BENCH/jvm98:/home/acaps/u2/vijay:/home/acaps/u2/vijay/LATESTUTIL/sableUtil-1.11/classes:/home/acaps/u2/vijay/SOOT2/src:/home/acaps/u2/vijay/local";
      try {
         //	ca.mcgill.sable.soot.Main.isVerbose = true;

         // ca.mcgill.sable.soot.jimple.Main.usePackedLive = true;

         SootClassFinder bclassfinder = new SootClassFinder();
         ArrayList bclasses = bclassfinder.getSootClasses(s);
         AllClassFinder allclassfinder = new AllClassFinder();
         allclassfinder.includeAllClasses(bclasses);
         Map allclassesHT = allclassfinder.getAllClassesHT();
         CHA cha = new CHA();
         cha.buildConservativeCallGraph ( s, allclassfinder );
         RTA rta = new RTA();
         rta.constructBitMap( allclassesHT );
         Map instancetypesHT = rta.getInstanceTypes(cha);
         // Collection RTAcallgraph = rta.getCallGraph();

         Collection RTAcallgraph = rta.getCallGraphBuilder().getCallGraph();
         // Collection RTAfinalcallgraph = rta.getFinalCallGraph();

         /*

                 for ( int i = 0; i < 30; i++ )
                 {

                  System.out.println ( " GARBAGE COLLECTION ... " );

                  System.gc();

                 }

         */
         /*

                 Resolver resolver = new Resolver( rta.getClassGraphBuilder() ); 

                 resolver.resolveMethods( RTAfinalcallgraph );

         	Inliner inliner = new Inliner( rta.getCallGraphBuilder() );

         //	inliner.examineMethods( RTAfinalcallgraph, resolver );
            
                 inliner.examineMethodsToFixCallSites( RTAfinalcallgraph, resolver );

                 */
         /*

         	DTA dta = new DTA();

                 dta.constructBitMap(allclassesHT);

         	dta.initializeConstraintGraph(rta);

         	dta.analyseStatements();

         	dta.solveConstraints(allclassesHT);

         	dta.setRTAred(rta);

         	Collection DTAcallgraph = dta.getCallGraph();

                 Collection DTAfinalcallgraph = dta.getFinalCallGraph();

         */
         VTA vta = new VTA();
         Collection VTAcallgraph = null;
         Collection VTAfinalcallgraph = null;
         /*
                 vta.constructBitMap(allclassesHT);

         	vta.initializeConstraintGraph(rta);
         	
         	vta.analyseStatements();

                 vta.solveConstraintsForArrayFlags();

                 vta.prepass = false;
         */
         for ( int i =0; i < 2; i++ )
         {
            vta.constructBitMap(allclassesHT);
            vta.initializeConstraintGraph(rta);
            vta.analyseStatements();
            vta.solveConstraints(allclassesHT);
            vta.setRTAred(rta);
            VTAcallgraph = vta.getCallGraph();
            VTAfinalcallgraph = vta.getFinalCallGraph();
         }

         /*

                 Resolver resolver = new Resolver( rta.getClassGraphBuilder() );

                 resolver.resolveMethods( VTAfinalcallgraph );




                 Inliner inliner = new Inliner( rta.getCallGraphBuilder() );

                 inliner.setImprovedCallSites ( vta.ImprovedCallSites ); 

                 inliner.examineMethodsToFixCallSites( VTAfinalcallgraph, resolver );

         //        inliner.examineMethods( VTAfinalcallgraph, resolver );


         */
         /*

                 Optimizer optimizer = new Optimizer( rta.getCallGraphBuilder() );
           
                 optimizer.examineMethods( VTAfinalcallgraph, resolver );
         */
      }
      catch ( java.lang.RuntimeException e ) { e.printStackTrace( System.out ); }

   }


   static void main(String[] args){
      Main p;
      p = new Main(args[0]);
   }


}




