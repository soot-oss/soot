
package soot.jimple.toolkit.scalar;

import ca.mcgill.sable.util.*;
import soot.*;
import soot.jimple.*;
import java.io.*;
import java.util.*;
import soot.toolkit.graph.*;

public class UnreachablePruner {

    static boolean debug = soot.Main.isInDebugMode;
    static boolean verbose = soot.Main.isVerbose;

    static CompleteUnitGraph stmtGraph;
    static HashSet visited;
    static int numPruned;

    public static void pruneUnreachables(StmtBody body) {
        numPruned = 0;
        stmtGraph = new CompleteUnitGraph(body);
        visited = new HashSet();

        if (verbose) 
            System.out.println("[" + body.getMethod().getName() + "] Starting unreachable pruner...");

        // mark first statement and all its successors, recursively
        if (!body.getUnits().isEmpty())
            visitStmt((Stmt)body.getUnits().getFirst());

        Iterator stmtIt = body.getUnits().iterator();
        while (stmtIt.hasNext()) {
            // find unmarked nodes
            Stmt stmt = (Stmt)stmtIt.next();
            if (!visited.contains(stmt)) {
                stmtIt.remove();
                numPruned++;
            }
        }
        if (numPruned == 0) {
            if (verbose)
                System.out.println("    --- no unreachable blocks ---");
        }
        else {
            if (verbose) {
                System.out.println("    --- removed " + numPruned +
                                   " unreachable blocks, " +
                                   " optimizing jumps again ---");
                JumpOptimizer.optimizeJumps(body);
            }
        }
  } // pruneUnreachables

    private static void visitStmt(Stmt stmt) {
        //ignore if already seen
        if (visited.contains(stmt)) {
            return;
        }

        // add to list of visited nodes
        visited.add(stmt);

        // visit all successors recursively
        Iterator succIt = stmtGraph.getSuccsOf(stmt).iterator();

        while (succIt.hasNext())
            visitStmt((Stmt)succIt.next());
    } // visitStmt
} // UnreachablePruner
    




