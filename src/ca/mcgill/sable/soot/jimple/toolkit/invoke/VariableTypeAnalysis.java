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

public class VariableTypeAnalysis {

 public static InvokeGraph pruneInvokeGraph(InvokeGraph g)
 {
    
    CHA cha = g.getCHA();

    if ( g.getRTA() == null )
    RapidTypeAnalysis.pruneInvokeGraph(g);

    RTA rta = g.getRTA();

    VTA vta = null;    

    if ( vta == null )
    {
	 vta = new VTA();
     g.setVTA ( vta );
    }
    else
    vta = g.getVTA();

    Collection VTAcallgraph = null;

    Collection VTAfinalcallgraph = null;

    // for ( int i =0; i < 2; i++ )
    // {

      vta.constructBitMap(cha.getAllClassFinder().getAllClassesHT());

	  vta.initializeConstraintGraph(rta);
	
	  vta.analyseStatements();

	  vta.solveConstraints(cha.getAllClassFinder().getAllClassesHT());

      //  vta.setRTAred(rta);

	  VTAcallgraph = vta.getCallGraph();

	  VTAfinalcallgraph = vta.getFinalCallGraph();

      // }

    return g;

 }

}




















