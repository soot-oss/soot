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
import soot.options.*;

import soot.util.*;
import soot.*;
import soot.jimple.*;
import java.io.*;
import java.util.*;
import soot.toolkits.graph.*;

public class UnreachableCodeEliminator extends BodyTransformer
{
    public UnreachableCodeEliminator( Singletons.Global g ) {}
    public static UnreachableCodeEliminator v() { return G.v().soot_jimple_toolkits_scalar_UnreachableCodeEliminator(); }

    ExceptionalUnitGraph stmtGraph;
    HashSet visited;
    int numPruned;

    protected void internalTransform(Body b, String phaseName, Map options) 
    {
        StmtBody body = (StmtBody)b;
        
        if (Options.v().verbose()) 
            G.v().out.println("[" + body.getMethod().getName() + "] Eliminating unreachable code...");

        numPruned = 0;
        stmtGraph = new ExceptionalUnitGraph(body);
        visited = new HashSet();

        // Used to be: "mark first statement and all its successors, recursively"
        // Bad idea! Some methods are extremely long. It broke because the recursion reached the
        // 3799th level.

	// We need a map from Units that handle Traps, to a Set of their
	// Traps, so we can remove the Traps should we remove the handler.
	Map handlerToTraps = new HashMap();

        if (!body.getUnits().isEmpty()) {
	    LinkedList startPoints = new LinkedList();
	    startPoints.addLast(body.getUnits().getFirst());

	    // Add trap handlers to startPoints unless we are removing
	    // unreachable traps.
	    boolean addHandlersToStart = 
		(! PhaseOptions.getBoolean(options, "remove-unreachable-traps"));

	    for (Iterator it = body.getTraps().iterator(); it.hasNext(); ) {
		Trap trap = (Trap) it.next();
		Unit handler = trap.getHandlerUnit();
		if (addHandlersToStart) {
		    // Don't add handlers for empty traps to the starting
		    // points, since we're about to remove those traps anyway.
		    if (trap.getBeginUnit() != trap.getEndUnit()) {
			startPoints.addLast(handler);
		    }
		}
		Set handlersTraps = (Set) handlerToTraps.get(handler);
		if (handlersTraps == null) {
		    handlersTraps = new ArraySet(3);
		    handlerToTraps.put(handler, handlersTraps);
		}
		handlersTraps.add(trap);
	    }

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
		Set traps = (Set) handlerToTraps.get(stmt);
		if (traps != null) {
		    for (Iterator it = traps.iterator(); it.hasNext(); ) {
			Trap trap = (Trap) it.next();
			body.getTraps().remove(trap);
		    }
		}
                numPruned++;
            }
        }
        if (Options.v().verbose())
            G.v().out.println("[" + body.getMethod().getName() + "]     Removed " + numPruned + " statements...");
            
        // Now eliminate empty traps.
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

    private void visitStmts(LinkedList st) {

        // Do DFS of the unit graph, starting from the passed nodes.

        while (!st.isEmpty()) {
            Object stmt = st.removeLast();
            if (!visited.contains(stmt)) {
                visited.add(stmt);
                Iterator succIt = stmtGraph.getSuccsOf(stmt).iterator();
                while (succIt.hasNext()) {
                    Object o = succIt.next();
                    if (!visited.contains(o))
                        st.addLast(o);
                }
            }
        }
    } // visitStmts

} // UnreachablePruner
