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

public class RapidTypeAnalysis {

 public static InvokeGraph pruneInvokeGraph(InvokeGraph g)
 {
 
   RTA rta = new RTA();

   CHA cha = g.getCHA();

   rta.constructBitMap ( cha.getAllClassFinder().getAllClassesHT() );

   Map instancetypesHT = rta.getInstanceTypes(cha); 

   Collection RTAcallgraph = rta.getCallGraph();

   Collection RTAfinalcallgraph = rta.getFinalCallGraph();

   g.setRTA ( rta );

   return g; 
 }

}
