/* Soot - a J*va Optimization Framework
 * Copyright (C) 2000 Patrick Lam
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
import soot.*;
import soot.toolkits.scalar.*;
import soot.toolkits.graph.*;
import soot.jimple.*;
import java.util.*;
import soot.util.*;

/** Provides an user-interface for the AvailableExpressionsAnalysis class.
 * Returns, for each statement, the list of expressions available before and after it. */
public class SlowAvailableExpressions implements AvailableExpressions
{
    Map unitToPairsAfter;
    Map unitToPairsBefore;

    Map unitToEquivsAfter;
    Map unitToEquivsBefore;

    /** Wrapper for SlowAvailableExpressionsAnalysis. */ 
    public SlowAvailableExpressions(Body b)
    {
        SlowAvailableExpressionsAnalysis analysis = 
            new SlowAvailableExpressionsAnalysis(new ExceptionalUnitGraph(b));

        // Build unitToExprs map
        {
            unitToPairsAfter = new HashMap(b.getUnits().size() * 2 + 1, 0.7f);
            unitToPairsBefore = new HashMap(b.getUnits().size() * 2 + 1, 0.7f);
            unitToEquivsAfter = new HashMap(b.getUnits().size() * 2 + 1, 0.7f);
            unitToEquivsBefore = new HashMap(b.getUnits().size() * 2 + 1, 0.7f);

            Iterator unitIt = b.getUnits().iterator();

            while(unitIt.hasNext())
            {
                Unit s = (Unit) unitIt.next();
 
                FlowSet set = (FlowSet) analysis.getFlowBefore(s);

                List pairsBefore = new ArrayList();
                List pairsAfter = new ArrayList();

                Chain equivsBefore = new HashChain();
                Chain equivsAfter = new HashChain();

                List setAsList = set.toList();
                Iterator si = setAsList.iterator();
                while (si.hasNext())
                {
                    Value v = (Value)si.next();
                    Stmt containingStmt = (Stmt)analysis.rhsToContainingStmt.get(v);
                    UnitValueBoxPair p = new UnitValueBoxPair
                        (containingStmt, ((AssignStmt)containingStmt).getRightOpBox());
                    EquivalentValue ev = new EquivalentValue(v);
                    pairsBefore.add(p);
                    if (!equivsBefore.contains(ev))
                        equivsBefore.add(ev);
                }

                unitToPairsBefore.put(s, pairsBefore);
                unitToEquivsBefore.put(s, equivsBefore);
                
                set = (FlowSet) analysis.getFlowAfter(s);
                setAsList = set.toList();
                si = setAsList.iterator();
                while (si.hasNext())
                {
                    Value v = (Value)si.next();
                    Stmt containingStmt = (Stmt)analysis.rhsToContainingStmt.get(v);
                    UnitValueBoxPair p = new UnitValueBoxPair
                        (containingStmt, ((AssignStmt)containingStmt).getRightOpBox());
                    EquivalentValue ev = new EquivalentValue(v);
                    pairsAfter.add(p);
                    if (!equivsAfter.contains(ev))
                        equivsAfter.add(ev);
                }

                unitToPairsAfter.put(s, pairsAfter);
                unitToEquivsAfter.put(s, equivsAfter);
            }  
        }
    }

    /** Returns a List containing the UnitValueBox pairs corresponding to expressions available before u. */
    public List getAvailablePairsBefore(Unit u)
    {
        return (List)unitToPairsBefore.get(u);
    }

    /** Returns a List containing the UnitValueBox pairs corresponding to expressions available after u. */
    public List getAvailablePairsAfter(Unit u)
    {
        return (List)unitToPairsAfter.get(u);
    }

    /** Returns a Chain containing the EquivalentValue objects corresponding to expressions available before u. */
    public Chain getAvailableEquivsBefore(Unit u)
    {
        return (Chain)unitToEquivsBefore.get(u);
    }

    /** Returns a Chain containing the EquivalentValue objects corresponding to expressions available after u. */
    public Chain getAvailableEquivsAfter(Unit u)
    {
        return (Chain)unitToEquivsAfter.get(u);
    }
}
