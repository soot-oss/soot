
package soot.jimple.toolkits.scalar;

import soot.util.*;
import soot.*;
import soot.jimple.*;
import java.io.*;
import java.util.*;
import soot.toolkits.graph.*;

public class UnreachableCodeEliminator extends BodyTransformer
{
    private static UnreachableCodeEliminator instance = new UnreachableCodeEliminator();
    private UnreachableCodeEliminator() {}

    public static UnreachableCodeEliminator v() { return instance; }

    static boolean debug = soot.Main.isInDebugMode;
    static boolean verbose = soot.Main.isVerbose;

    static CompleteUnitGraph stmtGraph;
    static HashSet visited;
    static int numPruned;

    protected void internalTransform(Body b, Map options) 
    {
        StmtBody body = (StmtBody)b;

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
        if (verbose)
        {
            if (numPruned == 0) {
                System.out.println("    --- no unreachable blocks ---");
            }
            else {
                System.out.println("    --- removed " + numPruned +
                                   " unreachable blocks");
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
    




