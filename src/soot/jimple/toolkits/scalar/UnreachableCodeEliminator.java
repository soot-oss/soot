/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Phong Co
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
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

import soot.util.*;
import soot.*;
import soot.jimple.*;
import java.io.*;
import java.util.*;
import soot.toolkits.graph.*;

public class UnreachableCodeEliminator extends BodyTransformer
{
    public UnreachableCodeEliminator( Singletons.Global g ) {}
    public static UnreachableCodeEliminator v() { return G.v().UnreachableCodeEliminator(); }

    CompleteUnitGraph stmtGraph;
    HashSet visited;
    int numPruned;

    protected void internalTransform(Body b, String phaseName, Map options) 
    {
        StmtBody body = (StmtBody)b;
        
        if (soot.Main.v().opts.verbose()) 
            G.v().out.println("[" + body.getMethod().getName() + "] Eliminating unreachable code...");

        numPruned = 0;
        stmtGraph = new CompleteUnitGraph(body);
        visited = new HashSet();

        // Used to be: "mark first statement and all its successors, recursively"
        // Bad idea! Some methods are extremely long. It broke because the recursion reached the
        // 3799th level.

        if (!body.getUnits().isEmpty())
            visitStmts((Stmt)body.getUnits().getFirst());

        Iterator stmtIt = body.getUnits().snapshotIterator();
        while (stmtIt.hasNext()) 
        {
            // find unmarked nodes
            Stmt stmt = (Stmt)stmtIt.next();
            
            if (!visited.contains(stmt)) 
            {
                body.getUnits().remove(stmt);
                numPruned++;
            }
        }
        if (soot.Main.v().opts.verbose())
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

    private void visitStmts(Stmt head) {

        // Do DFS of the unit graph, starting at the head node.

        LinkedList st = new LinkedList();
        st.addLast(head);

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
