/* Soot - a J*va Optimization Framework
 * Copyright (C) 1999 Patrick Lam
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

package soot;

import soot.*;
import soot.jimple.*;
import soot.util.*;
import java.util.*;

/** Utility methods for dealing with traps. */
public class TrapManager
{
    /** If exception e is caught at unit u in body b, return true;
     * otherwise, return false. */
    public static boolean isExceptionCaughtAt(SootClass e, Unit u, Body b)
    {
        /* Look through the traps t of b, checking to see if:
         *  - caught exception is e;
         *  - and, unit lies between t.beginUnit and t.endUnit */

        Hierarchy h = Scene.v().getActiveHierarchy();
        Chain units = b.getUnits();
        Iterator trapsIt = b.getTraps().iterator();

        while (trapsIt.hasNext())
        {
            Trap t = (Trap)trapsIt.next();

            /* Ah ha, we might win. */
            if (h.isClassSubclassOfIncluding(e, t.getException()))
            {
                Iterator it = units.iterator(t.getBeginUnit(),
                                             units.getPredOf(t.getEndUnit()));
                while (it.hasNext())
                    if (u.equals(it.next()))
                        return true;
            }
        }

        return false;
    }

    /** Returns the list of traps caught at Unit u in Body b. */
    public static List getTrapsAt(Unit unit, Body b)
    {
        List trapsList = new ArrayList();
        Chain units = b.getUnits();

        Iterator trapsIt = b.getTraps().iterator();

        while (trapsIt.hasNext())
        {
            Trap t = (Trap)trapsIt.next();

            Iterator it = units.iterator(t.getBeginUnit(),
                                         units.getPredOf(t.getEndUnit()));
            while (it.hasNext())
                if (unit.equals(it.next()))
                    trapsList.add(t);
        }

        return trapsList;
    }

    /** Returns a set of units which lie inside the range of any trap. */
    public static Set getTrappedUnitsOf(Body b)
    {
        Set trapsSet = new HashSet();
        Chain units = b.getUnits();

        Iterator trapsIt = b.getTraps().iterator();

        while (trapsIt.hasNext())
        {
                Trap t = (Trap)trapsIt.next();

            Iterator it = units.iterator(t.getBeginUnit(),
                                         units.getPredOf(t.getEndUnit()));
            while (it.hasNext())
                trapsSet.add(it.next());
        }
        return trapsSet;
    }

    /** Splits all traps so that they do not cross the range rangeStart - rangeEnd. 
     * Note that rangeStart is inclusive, rangeEnd is exclusive. */
    public static void splitTrapsAgainst(Body b, Unit rangeStart, Unit rangeEnd) 
    {
        Chain traps = b.getTraps(), units = b.getUnits();
        Iterator trapsIt = traps.snapshotIterator();
        
        while (trapsIt.hasNext())
        {
            Trap t = (Trap)trapsIt.next();
            
            Iterator unitIt = units.iterator(t.getBeginUnit(),
                                             t.getEndUnit());

            boolean insideRange = false;

            while (unitIt.hasNext())
            {
                Unit u = (Unit)unitIt.next();
                if (u.equals(rangeStart))
                    insideRange = true;
                if (!unitIt.hasNext()) // i.e. u.equals(t.getEndUnit())
                {
                    if (insideRange)
                    {
                        Trap newTrap = (Trap)t.clone();
                        t.setBeginUnit(rangeStart);
                        newTrap.setEndUnit(rangeStart);
                        traps.insertAfter(newTrap, t);
                    }
                    else
                        break;
                }
                if (u.equals(rangeEnd))
                {
                    // insideRange had better be true now.
                    if (!insideRange)
                        throw new RuntimeException("inversed range?");
                    Trap firstTrap = (Trap)t.clone();
                    Trap secondTrap = (Trap)t.clone();
                    firstTrap.setEndUnit(rangeStart);
                    secondTrap.setBeginUnit(rangeStart);
                    secondTrap.setEndUnit(rangeEnd);
                    t.setBeginUnit(rangeEnd);

                    traps.insertAfter(firstTrap, t);
                    traps.insertAfter(secondTrap, t);
                }
            }
        }
    }

    /** Given a body and a unit handling an exception,
     * returns the list of exception types possibly caught 
     * by the handler. */
    public static List getExceptionTypesOf(Unit u, Body body)
    {
         List possibleTypes = new ArrayList();
        
         Iterator trapIt = body.getTraps().iterator();
        
         while(trapIt.hasNext())
         {
            Trap trap = (Trap) trapIt.next();
            
            Unit handler = trap.getHandlerUnit();
             
            if(handler == u)
            {
                possibleTypes.add(RefType.v(trap.getException().
                    getName()));
            }
        }
        
        return possibleTypes;
    }
}
