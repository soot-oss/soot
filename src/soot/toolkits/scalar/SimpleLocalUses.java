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
import soot.toolkits.graph.*;
import soot.util.*;
import java.util.*;

public class SimpleLocalUses implements LocalUses
{
    Map unitToUses;

    public SimpleLocalUses(CompleteUnitGraph graph, LocalDefs localDefs)
    {
        if(Main.isProfilingOptimization)
           Main.usesTimer.start();
    
        if(Main.isProfilingOptimization)
           Main.usePhase1Timer.start();
        
        if(Main.isVerbose)
            System.out.println("[" + graph.getBody().getMethod().getName() +
                "]     Constructing SimpleLocalUses...");
    
        unitToUses = new HashMap(graph.size() * 2 + 1, 0.7f);
    
        // Initialize this map to empty sets
        {
            Iterator it = graph.iterator();

            while(it.hasNext())
            {
                Unit s = (Unit) it.next();
                unitToUses.put(s, new ArrayList());
            }
        }

        if(Main.isProfilingOptimization)
           Main.usePhase1Timer.end();
    
        if(Main.isProfilingOptimization)
           Main.usePhase2Timer.start();
    
        // Traverse units and associate uses with definitions
        {
            Iterator it = graph.iterator();

            while(it.hasNext())
            {
                Unit s = (Unit) it.next();

                Iterator boxIt = s.getUseBoxes().iterator();

                while(boxIt.hasNext())
                {
                    ValueBox useBox = (ValueBox) boxIt.next();

                    if(useBox.getValue() instanceof Local)
                    {
                        // Add this statement to the uses of the definition of the local

                        Local l = (Local) useBox.getValue();

                        List possibleDefs = localDefs.getDefsOfAt(l, s);
                        Iterator defIt = possibleDefs.iterator();

                        while(defIt.hasNext())
                        {
                            List useList = (List) unitToUses.get(defIt.next());
                            useList.add(new UnitValueBoxPair(s, useBox));
                        }
                    }
                }
            }
        }

        if(Main.isProfilingOptimization)
           Main.usePhase2Timer.end();
    
        if(Main.isProfilingOptimization)
           Main.usePhase3Timer.start();
    
        // Store the map as a bunch of unmodifiable lists.
        {
            Iterator it = graph.iterator();
            
            while(it.hasNext())
            {
                Unit s = (Unit) it.next();

                unitToUses.put(s, Collections.unmodifiableList(((List) unitToUses.get(s))));
            }
            
        }
        
        if(Main.isProfilingOptimization)
           Main.usePhase3Timer.end();
    
        if(Main.isProfilingOptimization)
            Main.usesTimer.end();
    }

    public List getUsesOf(Unit s)
    {
        return (List) unitToUses.get(s);
    }
}
