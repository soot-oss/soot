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





package soot.jimple.toolkits.base;

import soot.*;
import soot.jimple.*;
import soot.util.*;
import java.util.*;

public class Zonation
{
    private int zoneCount;
    private Map unitToZone;
    
    public Zonation(StmtBody body)
    {
        Chain units = body.getUnits();
        Map unitToTrapBoundaries = new HashMap();
        
        // Initialize each unit to an empty set
        {
            Iterator unitIt = units.iterator();
            
            while(unitIt.hasNext())
            {
                Unit u = (Unit) unitIt.next();
                
                unitToTrapBoundaries.put(u, new ArrayList());
            }
        }
            
        // Build trap boundaries
        {
            Iterator trapIt = body.getTraps().iterator();
            
            while(trapIt.hasNext())
            {
                Trap t = (Trap) trapIt.next();
                
                List boundary = (List) unitToTrapBoundaries.get(t.getBeginUnit());
                boundary.add(t);
                
                boundary = (List) unitToTrapBoundaries.get(t.getEndUnit());
                boundary.add(t);
            }
        }
        
        // Traverse units, assigning each to a zone
        {
            Map trapListToZone = new HashMap(10, 0.7f);
            List currentTraps = new ArrayList();
            Zone currentZone;
            
            zoneCount = 0;
            unitToZone = new HashMap(units.size() * 2 + 1, 0.7f);
                        
            // Initialize first empty zone
                currentZone = new Zone("0");
                trapListToZone.put(new ArrayList(), currentZone);
                
            Iterator unitIt = units.iterator();
            
            while(unitIt.hasNext())
            {
                Unit u = (Unit) unitIt.next();
                
                // Process trap boundaries
                {
                    List trapBoundaries = (List) unitToTrapBoundaries.get(u);

                    if(trapBoundaries.size() != 0)
                    {                        
                        Iterator trapIt = trapBoundaries.iterator();
                        
                        while(trapIt.hasNext())
                        {
                            Trap trap = (Trap) trapIt.next();
                            
                            if(currentTraps.contains(trap))
                                currentTraps.remove(trap);
                            else
                                currentTraps.add(trap);
                        }
                                          
                        if(trapListToZone.containsKey(currentTraps))
                            currentZone = (Zone) trapListToZone.get(currentTraps);
                        else
                        {   
                            // Create a new zone
                            
                            zoneCount++;
                            currentZone = new Zone(new Integer(zoneCount).toString());
                            
                            trapListToZone.put(currentTraps, currentZone);
                        }
                                                    
                    }
                }
                    
                unitToZone.put(u, currentZone);
            }
        }
        
    }
    
    public Zone getZoneOf(Unit u)
    {
        Zone z = (Zone) unitToZone.get(u);
        
        if(z == null)
            throw new RuntimeException("null zone!");

        return z; 
    }
    
    public int getZoneCount()
    {
        return zoneCount;
    }
}
