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
import soot.util.*;
import java.util.*;
import soot.jimple.*;


public class LocalPacker extends BodyTransformer
{
    private static LocalPacker instance = new LocalPacker();
    private LocalPacker() {}

    public static LocalPacker v() { return instance; }

    public String getDeclaredOptions() { return super.getDeclaredOptions() + " unsplit-original-locals"; }

    protected void internalTransform(Body body, String phaseName, Map options)
    {
        boolean isUnsplit = Options.getBoolean(options, "unsplit-original-locals");
        
        if(soot.Main.isVerbose)
            System.out.println("[" + body.getMethod().getName() + "] Packing locals...");
    
        Map localToGroup = new DeterministicHashMap(body.getLocalCount() * 2 + 1, 0.7f);
            // A group represents a bunch of locals which may potentially intefere with each other
            // 2 separate groups can not possibly interfere with each other 
            // (coloring say ints and doubles)
            
        Map groupToColorCount = new HashMap(body.getLocalCount() * 2 + 1, 0.7f);
        Map localToColor = new HashMap(body.getLocalCount() * 2 + 1, 0.7f);
        Map localToNewLocal;
        
        // Assign each local to a group, and set that group's color count to 0.
        {
            Iterator localIt = body.getLocals().iterator();

            while(localIt.hasNext())
            {
                Local l = (Local) localIt.next();
                Object g = l.getType();
                
                localToGroup.put(l, g);
                
                if(!groupToColorCount.containsKey(g))
                {
                    groupToColorCount.put(g, new Integer(0));
                }
            }
        }

        // Assign colors to the parameter locals.
        {
            Iterator codeIt = body.getUnits().iterator();

            while(codeIt.hasNext())
            {
                Unit s = (Unit) codeIt.next();

                if(s instanceof IdentityUnit &&
                    ((IdentityUnit) s).getLeftOp() instanceof Local)
                {
                    Local l = (Local) ((IdentityUnit) s).getLeftOp();
                    
                    Object group = localToGroup.get(l);
                    int count = ((Integer) groupToColorCount.get(group)).intValue();
                    
                    localToColor.put(l, new Integer(count));
                    
                    count++;
                    
                    groupToColorCount.put(group, new Integer(count));
                }
            }
        }
        
        // Call the graph colorer.
            if(isUnsplit)
                FastColorer.unsplitAssignColorsToLocals(body, localToGroup,
                    localToColor, groupToColorCount);
            else
                FastColorer.assignColorsToLocals(body, localToGroup,
                    localToColor, groupToColorCount);

                                    
        // Map each local to a new local.
        {
            List originalLocals = new ArrayList();
            localToNewLocal = new HashMap(body.getLocalCount() * 2 + 1, 0.7f);
            Map groupIntToLocal = new HashMap(body.getLocalCount() * 2 + 1, 0.7f);
            
            originalLocals.addAll(body.getLocals());
            body.getLocals().clear();

            Iterator localIt = originalLocals.iterator();

            while(localIt.hasNext())
            {
                Local original = (Local) localIt.next();
                
                Object group = localToGroup.get(original);
                int color = ((Integer) localToColor.get(original)).intValue();
                GroupIntPair pair = new GroupIntPair(group, color);
                
                Local newLocal;
                
                if(groupIntToLocal.containsKey(pair))
                    newLocal = (Local) groupIntToLocal.get(pair);
                else {
                    newLocal = (Local) original.clone();
                    newLocal.setType((Type) group);
                    
                    groupIntToLocal.put(pair, newLocal);
                    body.getLocals().add(newLocal);
                }
                
                localToNewLocal.put(original, newLocal);
            }
        }

        
        // Go through all valueBoxes of this method and perform changes
        {
            Iterator codeIt = body.getUnits().iterator();

            while(codeIt.hasNext())
            {
                Unit s = (Unit) codeIt.next();

                Iterator boxIt = s.getUseAndDefBoxes().iterator();

                while(boxIt.hasNext())
                {
                    ValueBox box = (ValueBox) boxIt.next();

                    if(box.getValue() instanceof Local)
                    {
                        Local l = (Local) box.getValue();
                        box.setValue((Local) localToNewLocal.get(l));
                    }
                }
            }
        }
    }
}

