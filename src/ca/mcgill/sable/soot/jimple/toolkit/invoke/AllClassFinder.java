package ca.mcgill.sable.soot.jimple.toolkit.invoke;
// import java.util.*;

import java.io.*;
import ca.mcgill.sable.soot.jimple.*;
import ca.mcgill.sable.util.*;
import java.util.*;
import ca.mcgill.sable.soot.baf.*;
import ca.mcgill.sable.soot.*;
// import ca.mcgill.sable.soot.sideEffect.*;

class AllClassFinder {
   private Map allclassesHT;
   private ArrayList allclasses = new ArrayList();
   private ClassGraphBuilder classgraphbuilder = new ClassGraphBuilder();
   static Timer classgbTimer = new Timer();
   static long classgbMem;
   private boolean isVerbose;
   AllClassFinder() { isVerbose = true;}


   AllClassFinder(boolean isVerbose) { isVerbose = isVerbose; }


   ClassGraphBuilder getClassGraphBuilder() {
      return classgraphbuilder;
   }


   Map getAllClassesHT() {
      return allclassesHT;
   }


   /* ArrayList */ void includeAllClasses (ArrayList bclasses) {
      if (isVerbose)
      System.out.print ( "Building the inheritance hierarchy....." );
      Iterator iter = bclasses.iterator();
      classgbTimer.start();
      while ( iter.hasNext() )
      {
         SootClass bclass = (SootClass) iter.next();
         classgraphbuilder.buildClassAndInterfaceGraph( bclass.getName() );
      }

      classgbTimer.end();
      classgbMem = Runtime.getRuntime().totalMemory() -
      Runtime.getRuntime().freeMemory();
      //       System.out.println("TIME FOR BUILDING THE CLASS GRAPH : "+classgbTimer.getTime());

      // System.out.println("SPACE FOR BUILDING THE CLASS GRAPH : "+classgbMem);

      if (isVerbose)
      {
         System.out.println ( "Done" );
         System.out.println();
      }

      classgraphbuilder.getStartAndRunMethods();
      classgraphbuilder.buildVirtualTables();
      if (isVerbose)
      classgraphbuilder.setClassGraphNumbers();
      allclassesHT = classgraphbuilder.getClassGraph();
      /*
             ArrayList allclassnodes = Helper.CNHT2VL(allclassesHT);

             allclasses = Helper.cnode2bclass(allclassnodes);


             System.out.println("");

             System.out.println("TOTAL NUMBER OF CLASSES : "+allclasses.size());

             return allclasses;

             */
   }


   /* ArrayList */ void includeAllClasses (SootClassManager manager, ArrayList bclasses) {
      if ( isVerbose )
      System.out.print ( "Building the inheritance hierarchy....." );
      Iterator iter = bclasses.iterator();
      classgbTimer.start();
      while ( iter.hasNext() )
      {
         SootClass bclass = (SootClass) iter.next();
         classgraphbuilder.buildClassAndInterfaceGraph( bclass.getName(), manager );
      }

      classgbTimer.end();
      classgbMem = Runtime.getRuntime().totalMemory() -
      Runtime.getRuntime().freeMemory();
      // System.out.println("TIME FOR BUILDING THE CLASS GRAPH : "+classgbTimer.getTime());

      // System.out.println("SPACE FOR BUILDING THE CLASS GRAPH : "+classgbMem);

      if ( isVerbose )
      {
         System.out.println ( "Done" );
         System.out.println();
      }

      classgraphbuilder.getStartAndRunMethods();
      classgraphbuilder.buildVirtualTables();
      if (isVerbose)
      classgraphbuilder.setClassGraphNumbers();
      allclassesHT = classgraphbuilder.getClassGraph();
      /*
             ArrayList allclassnodes = Helper.CNHT2VL(allclassesHT);

             allclasses = Helper.cnode2bclass(allclassnodes);


             System.out.println("");

             System.out.println("TOTAL NUMBER OF CLASSES : "+allclasses.size());

             return allclasses;

             */
   }


}




