
package ca.mcgill.sable.soot.jimple.toolkit.scalar;

import ca.mcgill.sable.util.*;
import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.soot.toolkit.scalar.*;
import ca.mcgill.sable.soot.jimple.*;
import java.io.*;
import java.util.*;


public class ConstantOptimizer {

    static boolean debug = ca.mcgill.sable.soot.Main.isInDebugMode;
    static boolean verbose = ca.mcgill.sable.soot.Main.isVerbose;

    public static void optimizeConstants(StmtBody stmtBody)
    {
	int numFolded = 0;
	int numPropagated = 0;

        if (verbose)
            System.out.println("[" + stmtBody.getMethod().getName() +
			       "] Propagating and folding constants...");

	Chain units = stmtBody.getUnits();
	CompleteUnitGraph stmtGraph = new CompleteUnitGraph(stmtBody);
        LocalDefs localDefs;
        
        localDefs = new SimpleLocalDefs(stmtGraph);

        // Perform a constant/local propagation pass.
	Iterator stmtIt = stmtGraph.pseudoTopologicalOrderIterator();

	// go through each use box in each statement
	while (stmtIt.hasNext()) {
	    Stmt stmt = (Stmt) stmtIt.next();

	    // propagation pass
	    Iterator useBoxIt = stmt.getUseBoxes().iterator();
	    ValueBox useBox;

	    while (useBoxIt.hasNext()) {
		useBox = (ValueBox) useBoxIt.next();
		if (useBox.getValue() instanceof Local) {
		    Local local = (Local) useBox.getValue();
		    List defsOfUse = localDefs.getDefsOfAt(local, stmt);
		    if (defsOfUse.size() == 1) {
			DefinitionStmt defStmt =
			    (DefinitionStmt) defsOfUse.get(0);
			if (defStmt.getRightOp() instanceof NumericConstant) {
			    if (useBox.canContainValue(defStmt.getRightOp())) {
				useBox.setValue(defStmt.getRightOp());
				numPropagated++;
			    }
			}
		    }
		}
	    }
		
	    // folding pass
	    useBoxIt = stmt.getUseBoxes().iterator();

	    while (useBoxIt.hasNext()) {
		useBox = (ValueBox) useBoxIt.next();
		Value value = useBox.getValue();
		if (!(value instanceof Constant)) {
		    if (Evaluator.isValueConstantValued(value)) {
			Value constValue =
			    Evaluator.getConstantValueOf(value);
			if (useBox.canContainValue(constValue)) {
			    useBox.setValue(constValue);
			    numFolded++;
			}
		    }
		}
	    }
	}

       if (verbose)
            System.out.println("[" + stmtBody.getMethod().getName() +
                "] Propagated: " + numPropagated + ", Folded:  " + numFolded);

    } // optimizeConstants

} // ConstantOptimizer
    




