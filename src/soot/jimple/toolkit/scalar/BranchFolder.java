
package soot.jimple.toolkit.scalar;

import ca.mcgill.sable.util.*;
import soot.*;
import soot.jimple.*;
import java.io.*;
import java.util.*;


public class BranchFolder {

    static boolean debug = soot.Main.isInDebugMode;
    static boolean verbose = soot.Main.isVerbose;

    public static void foldBranches(StmtBody stmtBody)
    {
        int numTrue = 0, numFalse = 0;

        if (verbose)
            System.out.println("[" + stmtBody.getMethod().getName() +
                               "] Folding branches...");

        Chain units = stmtBody.getUnits();
        ArrayList unitList = new ArrayList(); unitList.addAll(units);

        Iterator stmtIt = unitList.iterator();
        while (stmtIt.hasNext()) {
            Stmt stmt = (Stmt)stmtIt.next();
            if (stmt instanceof IfStmt) {
                // check for constant-valued conditions
                Value cond = ((IfStmt) stmt).getCondition();
                if (Evaluator.isValueConstantValued(cond)) {
                    cond = Evaluator.getConstantValueOf(cond);

                    if (((IntConstant) cond).value == 1) {
                        // if condition always true, convert if to goto
                        Stmt newStmt =
                            Jimple.v().newGotoStmt(((IfStmt)stmt).getTarget());
                        
                        units.insertAfter(newStmt, stmt);
                    }
                    // remove if
                    units.remove(stmt);
                }
            }
        }

       if (verbose)
            System.out.println("[" + stmtBody.getMethod().getName() +
                "] Folded " + numTrue + " true, " + numFalse +
                               " false branches");

    } // foldBranches

} // BranchFolder
    
