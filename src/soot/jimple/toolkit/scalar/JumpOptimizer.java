
package soot.jimple.toolkit.scalar;

import ca.mcgill.sable.util.*;
import soot.*;
import soot.jimple.*;
import java.io.*;
import java.util.*;


public class JumpOptimizer {

    static boolean debug = soot.Main.isInDebugMode;
    static boolean verbose = soot.Main.isVerbose;

    static final int JUMPOPT_TYPES = 6;
    static int numFound[], numFixed[];

    static HashMap stmtMap;
    
    public static void optimizeJumps(StmtBody body) 
    {
        if (verbose) 
        {
            System.out.println("[" + body.getMethod().getName() + "] Starting jump optimizer...");
        }

        // allocate counters once only
        if (numFound == null) {
            numFound = new int[JUMPOPT_TYPES+1];
            numFixed = new int[JUMPOPT_TYPES+1];
        }

        for (int i = 0; i <= JUMPOPT_TYPES; i++) {
            numFound[i] = 0;
            numFixed[i] = 0;
        }

        Chain units = body.getUnits();
        stmtMap = new HashMap();

        // find goto and if-goto statements
        Iterator stmtIt = units.iterator();
        Stmt stmt, target, newTarget;
        while (stmtIt.hasNext()) {
            stmt = (Stmt)stmtIt.next();
            if (stmt instanceof GotoStmt) {

                target = (Stmt)((GotoStmt)stmt).getTarget();

                if (stmtIt.hasNext()) {
                    // check for goto -> next statement
                    if (units.getSuccOf(stmt) == target)
                    {
                        stmtIt.remove();
                        updateCounters(6, true);
                    }
                }

                if (target instanceof GotoStmt) {
                    newTarget = getFinalTarget(target);
                    if (newTarget == null)
                        newTarget = stmt;
                    ((GotoStmt)stmt).setTarget(newTarget);
                    updateCounters(1, true);
                }
                else if (target instanceof IfStmt) {
                    updateCounters(3, false);
                }
            }
            else if (stmt instanceof IfStmt) {
                target = (Stmt)((IfStmt)stmt).getTarget();

                if (target instanceof GotoStmt) {
                    newTarget = getFinalTarget(target);
                    if (newTarget == null)
                        newTarget = stmt;
                    ((IfStmt)stmt).setTarget((Unit)newTarget);
                    updateCounters(2, true);
                }
                else if (target instanceof IfStmt) {
                    updateCounters(4, false);
                }
            }
        }
        if (verbose) {
            System.out.println("[" + body.getMethod().getName() + "] Found " + numFound[0] +
                               " branch optimization opportunities, " +
                               numFixed[0] + " fixed.");
        }
    } // optimizeJumps

    private static void updateCounters(int type, boolean fixed) {

        if ((type < 0) || (type > JUMPOPT_TYPES))
            return;

        numFound[0]++;
        numFound[type]++;
        if (fixed) {
            numFixed[0]++;
            numFixed[type]++;
        }
    }
        
    private static Stmt getFinalTarget(Stmt stmt) {
        Stmt finalTarget=null, target;
        
        // if not a goto, this is the final target
        if (!(stmt instanceof GotoStmt))
            return stmt;

        // first map this statement to itself, so we can detect cycles
        stmtMap.put(stmt, stmt);

        target = (Stmt)((GotoStmt)stmt).getTarget();

        // check if target is in statement map
        if (stmtMap.containsKey(target)) {
            // see if it maps to itself
            finalTarget = (Stmt)stmtMap.get(target);
            if (finalTarget == target)
                // this is part of a cycle
                finalTarget = null;
        }
        else
            finalTarget = getFinalTarget(target);
            
        stmtMap.put(stmt, finalTarget);
        return finalTarget;
    } // getFinalTarget

} // JumpOptimizer
    




