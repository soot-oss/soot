/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
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




package soot.toolkits.scalar;

import soot.*;
import java.util.*;


public class UnusedLocalEliminator extends BodyTransformer
{ 
    private static UnusedLocalEliminator instance = new UnusedLocalEliminator();
    private UnusedLocalEliminator() {}

    public static UnusedLocalEliminator v() { return instance; }

    protected void internalTransform(Body body, String phaseName, Map options)
    {
        if(soot.Main.isVerbose)
            System.out.println("[" + body.getMethod().getName() + "] Eliminating unused locals...");

        Set usedLocals = new HashSet();

        // Traverse statements noting all the uses
        {
            Iterator unitIt = body.getUnits().iterator();

            while(unitIt.hasNext())
            {
                Unit s = (Unit) unitIt.next();

                // Remove all locals in defBoxes from unusedLocals
                {
                    Iterator boxIt = s.getUseAndDefBoxes().iterator();

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
