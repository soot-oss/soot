
package soot.jimple.toolkits.scalar;

import soot.util.*;
import soot.*;
import soot.jimple.*;
import java.io.*;
import java.util.*;


public class ConditionalBranchFolder 
{
    private static ConditionalBranchFolder instance = new ConditionalBranchFolder();
    private ConditionalBranchFolder() {}

    public static ConditionalBranchFolder v() { return instance; }

    static boolean debug = soot.Main.isInDebugMode;
    static boolean verbose = soot.Main.isVerbose;

    protected void internalTransform(Body body, Map options)
    {
        StmtBody stmtBody = (StmtBody)body;

        int numTrue = 0, numFalse = 0;

        if (verbose)
            System.out.println("[" + stmtBody.getMethod().getName() +
                               "] Folding conditional branches...");

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
                               " conditional branches");

    } // foldBranches

} // BranchFolder
    
