
package ca.mcgill.sable.soot.jimple.toolkit.scalar;

import ca.mcgill.sable.util.*;
import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.soot.jimple.*;
import java.io.*;
import java.util.*;


public class UnreachablePruner {

    static boolean debug = false;
    static boolean verbose = false;

    static CompleteUnitGraph stmtGraph;
    static HashSet visited;
    static int numPruned;

    public static void pruneUnreachables(StmtBody body) {
	numPruned = 0;
	stmtGraph = new CompleteUnitGraph(body);
	visited = new HashSet();

	verbose = ca.mcgill.sable.soot.Main.isVerbose;
	debug = ca.mcgill.sable.soot.Main.isInDebugMode;

	if (verbose)
	    System.out.println("    ... starting unreachable pruner ...");

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
	    if (debug)
		System.out.println("    ignoring " + stmt);
	    return;
	}

	// add to list of visited nodes
	visited.add(stmt);
	if (debug)
	    System.out.println("    marking " + stmt);

	// visit all successors recursively
	Iterator succIt = stmtGraph.getSuccsOf(stmt).iterator();

	while (succIt.hasNext())
	    visitStmt((Stmt)succIt.next());
    } // visitStmt
} // UnreachablePruner
    




