
package soot.jimple.toolkits.scalar;

import soot.util.*;
import soot.*;
import soot.toolkits.scalar.*;
import soot.jimple.*;
import java.io.*;
import java.util.*;
import soot.toolkits.graph.*;

public class ConstantPropagatorAndFolder extends BodyTransformer
{
    private static ConstantPropagatorAndFolder instance = new ConstantPropagatorAndFolder();
    private ConstantPropagatorAndFolder() {}

    public static ConstantPropagatorAndFolder v() { return instance; }

    static boolean debug = soot.Main.isInDebugMode;
    static boolean verbose = soot.Main.isVerbose;

    protected void internalTransform(Body b, Map options)
    {
        StmtBody stmtBody = (StmtBody)b;
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

}
    




