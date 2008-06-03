/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
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




package soot.toolkits.scalar;
import soot.options.*;

import soot.*;

import java.util.*;





/**
 *    A BodyTransformer that removes all unused local variables from a given Body.
 *    Implemented as a singleton.
 *    @see BodyTransformer
 *    @see Body 
 */
public class UnusedLocalEliminator extends BodyTransformer
{ 
    public UnusedLocalEliminator( Singletons.Global g ) {}
    public static UnusedLocalEliminator v() { return G.v().soot_toolkits_scalar_UnusedLocalEliminator(); }

    protected void internalTransform(Body body, String phaseName, Map options)
    {
        if(Options.v().verbose())
            G.v().out.println("[" + body.getMethod().getName() + "] Eliminating unused locals...");

        Set<Value> usedLocals = new HashSet<Value>();

        // Traverse statements noting all the uses and defs
        {
            Iterator unitIt = body.getUnits().iterator();

            while(unitIt.hasNext())
            {
                Unit s = (Unit) unitIt.next();

                {
                    Iterator boxIt;
                    boxIt = s.getUseBoxes().iterator();
                    while(boxIt.hasNext())
                    {
                        Value value = ((ValueBox) boxIt.next()).getValue();

                        if(value instanceof Local && !usedLocals.contains(value))
                            usedLocals.add(value);
                    }
                    boxIt = s.getDefBoxes().iterator();
                    while(boxIt.hasNext())
                    {
                        Value value = ((ValueBox) boxIt.next()).getValue();

                        if(value instanceof Local && !usedLocals.contains(value))
                            usedLocals.add(value);
                    }
                }
            }

        }

        // Remove all locals that are unused.
        {
            Iterator it = body.getLocals().iterator();
            
            while(it.hasNext())
            {
                Local local = (Local) it.next();

                if(!usedLocals.contains(local))
                    it.remove();
            }
        }
    }
}
