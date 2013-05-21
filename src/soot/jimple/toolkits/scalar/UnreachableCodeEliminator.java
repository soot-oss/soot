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
 * Modified by the Sable Research Group and others 1997-2003.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */



package soot.jimple.toolkits.scalar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import soot.Body;
import soot.BodyTransformer;
import soot.G;
import soot.PhaseOptions;
import soot.Scene;
import soot.Singletons;
import soot.Trap;
import soot.Unit;
import soot.jimple.Stmt;
import soot.jimple.StmtBody;
import soot.options.Options;
import soot.toolkits.exceptions.PedanticThrowAnalysis;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.util.ArraySet;

public class UnreachableCodeEliminator extends BodyTransformer
{
    public UnreachableCodeEliminator( Singletons.Global g ) {}
    public static UnreachableCodeEliminator v() { return G.v().soot_jimple_toolkits_scalar_UnreachableCodeEliminator(); }

    protected void internalTransform(Body b, String phaseName, Map options) 
    {
        new Instance().internalTransform(b, phaseName, options);
    }

    class Instance {
        ExceptionalUnitGraph stmtGraph;
        HashSet<Object> visited;
        int numPruned;

        protected void internalTransform(Body b, String phaseName, Map options) 
        {
            StmtBody body = (StmtBody)b;
            
            if (Options.v().verbose()) 
                G.v().out.println("[" + body.getMethod().getName() + "] Eliminating unreachable code...");

            numPruned = 0;

            if (PhaseOptions.getBoolean(options, "remove-unreachable-traps")) {
                stmtGraph = new ExceptionalUnitGraph(body, Scene.v().getDefaultThrowAnalysis(),
                        true);
            } else {
                // Force a conservative ExceptionalUnitGraph() which
                // necessarily includes an edge from every trapped Unit to
                // its handler, so that we retain Traps in the case where
                // trapped units remain, but the default ThrowAnalysis
                // says that none of them can throw the caught exception.
                stmtGraph = new ExceptionalUnitGraph(body, PedanticThrowAnalysis.v(),
                                                    false);
            }
            visited = new HashSet<Object>();

            // We need a map from Units that handle Traps, to a Set of their
            // Traps, so we can remove the Traps should we remove the handler.
            Map<Unit, Set> handlerToTraps = new HashMap<Unit, Set>();
            for( Iterator trapIt = body.getTraps().iterator(); trapIt.hasNext(); ) {
                final Trap trap = (Trap) trapIt.next();
                Unit handler = trap.getHandlerUnit();
                Set<Trap> handlersTraps = handlerToTraps.get(handler);
                if (handlersTraps == null) {
                    handlersTraps = new ArraySet(3);
                    handlerToTraps.put(handler, handlersTraps);
                }
                handlersTraps.add(trap);
            }

            // Used to be: "mark first statement and all its successors, recursively"
            // Bad idea! Some methods are extremely long. It broke because the recursion reached the
            // 3799th level.

            if (!body.getUnits().isEmpty()) {
                LinkedList<Unit> startPoints = new LinkedList<Unit>();
                startPoints.addLast(body.getUnits().getFirst());

                visitStmts(startPoints);
            }

            Iterator stmtIt = body.getUnits().snapshotIterator();
            while (stmtIt.hasNext()) 
            {
                // find unmarked nodes
                Stmt stmt = (Stmt)stmtIt.next();
                
                if (!visited.contains(stmt)) 
                {
                    body.getUnits().remove(stmt);
                    Set traps = handlerToTraps.get(stmt);
                    if (traps != null) {
                        for( Iterator trapIt = traps.iterator(); trapIt.hasNext(); ) {
                            final Trap trap = (Trap) trapIt.next();
                            body.getTraps().remove(trap);
                        }
                    }
                    numPruned++;
                }
            }
            if (Options.v().verbose())
                G.v().out.println("[" + body.getMethod().getName() + "]     Removed " + numPruned + " statements...");
                
            // Now eliminate empty traps. 
            //
            // For the most part, this is an atavism, an an artifact of
            // pre-ExceptionalUnitGraph code, when the only way for a trap to 
            // become unreachable was if all its trapped units were removed, and
            // the stmtIt loop did not remove Traps as it removed handler units.
            // We've left this separate test for empty traps here, even though 
            // most such traps would already have been eliminated by the preceding
            // loop, because in arbitrary bytecode you could have
            // handler unit that was still reachable by normal control flow, even
            // though it no longer trapped any units (though such code is unlikely
            // to occur in practice, and certainly no in code generated from Java
            // source.
            {
                Iterator trapIt = b.getTraps().iterator();
                
                while(trapIt.hasNext())
                {
                    Trap t = (Trap) trapIt.next();
                    
                    if(t.getBeginUnit() == t.getEndUnit())
                        trapIt.remove();
                }
            }
            
    } // pruneUnreachables

        private void visitStmts(LinkedList<Unit> st) {

            // Do DFS of the unit graph, starting from the passed nodes.

            while (!st.isEmpty()) {
                Unit stmt = st.removeLast();
                if (!visited.contains(stmt)) {
                    visited.add(stmt);
                    Iterator<Unit> succIt = stmtGraph.getSuccsOf(stmt).iterator();
                    while (succIt.hasNext()) {
                        Unit o = succIt.next();
                        if (!visited.contains(o))
                            st.addLast(o);
                    }
                }
            }
        } // visitStmts

    }
} // UnreachablePruner
