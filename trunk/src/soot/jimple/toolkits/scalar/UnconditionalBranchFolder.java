/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Phong Co
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */



package soot.jimple.toolkits.scalar;
import soot.options.*;

import soot.util.*;
import soot.*;
import soot.jimple.*;

import java.util.*;


public class UnconditionalBranchFolder extends BodyTransformer
{
    public UnconditionalBranchFolder( Singletons.Global g ) {}
    public static UnconditionalBranchFolder v() { return G.v().soot_jimple_toolkits_scalar_UnconditionalBranchFolder(); }

    static final int JUMPOPT_TYPES = 6;
    int numFound[], numFixed[];

    HashMap<Stmt, Stmt> stmtMap;
    
    protected void internalTransform(Body b, String phaseName, Map options) 
    {
        StmtBody body = (StmtBody)b;

        if (Options.v().verbose()) 
            G.v().out.println("[" + body.getMethod().getName() + "] Folding unconditional branches...");


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
        stmtMap = new HashMap<Stmt, Stmt>();

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
                target = ((IfStmt)stmt).getTarget();

                if (target instanceof GotoStmt) {
                    newTarget = getFinalTarget(target);
                    if (newTarget == null)
                        newTarget = stmt;
                    ((IfStmt)stmt).setTarget(newTarget);
                    updateCounters(2, true);
                }
                else if (target instanceof IfStmt) {
                    updateCounters(4, false);
                }
            }
        }
        if (Options.v().verbose()) 
            G.v().out.println("[" + body.getMethod().getName() + "]     " + numFixed[0] + " of " + 
                                numFound[0] + " branches folded.");
             
                               
    } // optimizeJumps

    private void updateCounters(int type, boolean fixed) {

        if ((type < 0) || (type > JUMPOPT_TYPES))
            return;

        numFound[0]++;
        numFound[type]++;
        if (fixed) {
            numFixed[0]++;
            numFixed[type]++;
        }
    }
        
    private Stmt getFinalTarget(Stmt stmt) {
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
            finalTarget = stmtMap.get(target);
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
    




