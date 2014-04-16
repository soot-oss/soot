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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import soot.Body;
import soot.G;
import soot.Local;
import soot.Timers;
import soot.Unit;
import soot.ValueBox;
import soot.options.Options;
import soot.toolkits.graph.UnitGraph;


/**
 *  Analysis that implements the LocalUses interface.
 *  Uses for a Local defined at a given Unit are returned as 
 *  a list of UnitValueBoxPairs each containing a Unit that use the
 *  local and the Local itself wrapped in a ValueBox.
 */
public class SimpleLocalUses implements LocalUses
{
    Map<Unit, List<UnitValueBoxPair>> unitToUses;

    /**
     * Construct the analysis from a UnitGraph representation
     * of a method body and a LocalDefs interface. This supposes that
     * a LocalDefs analysis must have been computed prior.
     *
     * <p> Note: If you do not already have a UnitGraph, it may be
     * cheaper to use the constructor which only requires a Body.
     */
    public SimpleLocalUses(UnitGraph graph, LocalDefs localDefs)
    {
        this(graph.getBody(), localDefs);
    }

    /**
     * Construct the analysis from a method body and a LocalDefs
     * interface. This supposes that a LocalDefs analysis must have
     * been computed prior.
     */
    public SimpleLocalUses(Body body, LocalDefs localDefs)
    {
        if(Options.v().time())
           Timers.v().usesTimer.start();
    
        if(Options.v().time())
           Timers.v().usePhase1Timer.start();
        
        if(Options.v().verbose())
            G.v().out.println("[" + body.getMethod().getName() +
                "]     Constructing SimpleLocalUses...");
        
        unitToUses = new HashMap<Unit, List<UnitValueBoxPair>>(body.getUnits().size() * 2 + 1, 0.7f);
    
        // Initialize this map to empty sets
        for (Unit s : body.getUnits())
        	unitToUses.put(s, new ArrayList<UnitValueBoxPair>());

        if(Options.v().time())
           Timers.v().usePhase1Timer.end();
    
        if(Options.v().time())
           Timers.v().usePhase2Timer.start();
    
        // Traverse units and associate uses with definitions
        {
            for (Unit s : body.getUnits())
            {
                for (ValueBox useBox : s.getUseBoxes())
                {
                	if(useBox.getValue() instanceof Local)
                    {
                        // Add this statement to the uses of the definition of the local

                        Local l = (Local) useBox.getValue();

                        List<Unit> possibleDefs = localDefs.getDefsOfAt(l, s);
                        for (Unit def : possibleDefs) {
                            List<UnitValueBoxPair> useList = unitToUses.get(def);
                            useList.add(new UnitValueBoxPair(s, useBox));
                        }
                    }
                }
            }
        }

        if(Options.v().time())
           Timers.v().usePhase2Timer.end();
    
        if(Options.v().time())
           Timers.v().usePhase3Timer.start();
    
        // Store the map as a bunch of unmodifiable lists.
        for (Unit s : body.getUnits())
        	unitToUses.put(s, Collections.unmodifiableList(unitToUses.get(s)));
        
        if(Options.v().time())
           Timers.v().usePhase3Timer.end();
    
        if(Options.v().time())
            Timers.v().usesTimer.end();

        if(Options.v().verbose())
            G.v().out.println("[" + body.getMethod().getName() +
                "]     finished SimpleLocalUses...");
    }

    /**
     *  Uses for a Local defined at a given Unit are returned as 
     *  a list of UnitValueBoxPairs each containing a Unit that use the
     *  local and the Local itself wrapped in a ValueBox.
     *  @param s a unit that we want to query for the uses of the Local it (may) define.
     *  @return a UnitValueBoxPair of the Units that use the Local.
     */
    public List<UnitValueBoxPair> getUsesOf(Unit s)
    {
        return unitToUses.get(s);
    }
}
