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

public class DeclaredTypeAnalysis {

 public static InvokeGraph pruneInvokeGraph(InvokeGraph g)
 {
    
    CHA cha = g.getCHA();

    if ( g.getRTA() == null )
    RapidTypeAnalysis.pruneInvokeGraph(g);

    RTA rta = g.getRTA();

    DTA dta = null;    

    if ( dta == null )
    {
	 dta = new DTA();
     g.setDTA ( dta );
    }
    else
    dta = g.getDTA();

    Collection DTAcallgraph = null;

    Collection DTAfinalcallgraph = null;

    // for ( int i =0; i < 2; i++ )
    // {

      dta.constructBitMap(cha.getAllClassFinder().getAllClassesHT());

	  dta.initializeConstraintGraph(rta);
	
	  dta.analyseStatements();

	  dta.solveConstraints(cha.getAllClassFinder().getAllClassesHT());

	  // dta.setRTAred(rta);

	  DTAcallgraph = dta.getCallGraph();

	  DTAfinalcallgraph = dta.getFinalCallGraph();

      // }

    return g;

 }

}
