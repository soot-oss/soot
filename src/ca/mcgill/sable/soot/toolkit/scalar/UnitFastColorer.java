/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Jimple, a 3-address code Java(TM) bytecode representation.        *
 * Copyright (C) 1997, 1998 Raja Vallee-Rai (kor@sable.mcgill.ca)    *
 * All rights reserved.                                              *
 *                                                                   *
 * This work was done as a project of the Sable Research Group,      *
 * School of Computer Science, McGill University, Canada             *
 * (http://www.sable.mcgill.ca/).  It is understood that any         *
 * modification not identified as such is not covered by the         *
 * preceding statement.                                              *
 *                                                                   *
 * This work is free software; you can redistribute it and/or        *
 * modify it under the terms of the GNU Library General Public       *
 * License as published by the Free Software Foundation; either      *
 * version 2 of the License, or (at your option) any later version.  *
 *                                                                   *
 * This work is distributed in the hope that it will be useful,      *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of    *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU *
 * Library General Public License for more details.                  *
 *                                                                   *
 * You should have received a copy of the GNU Library General Public *
 * License along with this library; if not, write to the             *
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,      *
 * Boston, MA  02111-1307, USA.                                      *
 *                                                                   *
 * Java is a trademark of Sun Microsystems, Inc.                     *
 *                                                                   *
 * To submit a bug report, send a comment, or get the latest news on *
 * this project and other Sable Research Group projects, please      *
 * visit the web site: http://www.sable.mcgill.ca/                   *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

/*
 Reference Version
 -----------------
 This is the latest official version on which this file is based.

 Change History
 --------------
 A) Notes:

 Please use the following template.  Most recent changes should
 appear at the top of the list.

 - Modified on [date (March 1, 1900)] by [name]. [(*) if appropriate]
   [description of modification].

 Any Modification flagged with "(*)" was done as a project of the
 Sable Research Group, School of Computer Science,
 McGill University, Canada (http://www.sable.mcgill.ca/).

 You should add your copyright, using the following template, at
 the top of this file, along with other copyrights.

 *                                                                   *
 * Modifications by [name] are                                       *
 * Copyright (C) [year(s)] [your name (or company)].  All rights     *
 * reserved.                                                         *
 *                                                                   *

 B) Changes:

 - Modified on March 23, 1999 by Raja Vallee-Rai (rvalleerai@sable.mcgill.ca) (*)
   Performed some tweaks to make this class deterministic.
   
 - Modified on March 13, 1999 by Raja Vallee-Rai (rvalleerai@sable.mcgill.ca) (*)
   Re-organized the timers.

 - Modified on February 28, 1999 by Raja Vallee-Rai (rvalleerai@sable.mcgill.ca). (*)
   Initial release.
*/

package ca.mcgill.sable.soot.toolkit.scalar;

import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.util.*;
import java.util.*;

public class UnitFastColorer
{   
    public static void unsplitAssignColorsToLocals(Body unitBody, Map localToGroup, 
        Map localToColor, Map groupToColorCount)
    {
	
	CompleteUnitGraph unitGraph = new CompleteUnitGraph(unitBody);

        UnitLiveLocals liveLocals;

        
	liveLocals = new SimpleUnitLiveLocals(unitGraph);
        
        
        UnitInterferenceGraph intGraph = new UnitInterferenceGraph(unitBody, localToGroup, liveLocals);

	Iterator it = intGraph.getLocals().iterator();
	
	while(it.hasNext()) {
	    Local local = (Local) it.next();
	    Local[] locals = intGraph.getInterferencesOf(local);
	    System.out.println(local);
	    System.out.println(locals); 
	}

	    





        Map localToOriginalName = new HashMap();
        
        // Map each local variable to its original name
        {
            Iterator localIt = intGraph.getLocals().iterator();
            
            while(localIt.hasNext())
            {
                Local local = (Local) localIt.next();
                
                int signIndex;
                
                signIndex = local.getName().indexOf("#");
                
                if(signIndex != -1)
                {
                    localToOriginalName.put(local, local.getName().substring(0, signIndex));
                }
                else
                    localToOriginalName.put(local, local.getName());
            }
        }
        
        Map originalNameToColors = new HashMap();
            // maps an original name to the colors being used for it
                    
        // Assign a color for each local.
        {
            int[] freeColors = new int[10];
            Iterator localIt = intGraph.getLocals().iterator();
            
            while(localIt.hasNext())
            {
                Local local = (Local) localIt.next();
                
                if(localToColor.containsKey(local))
                {
                    // Already assigned, probably a parameter
                    continue;
                }
                
                Object group = localToGroup.get(local);
                int colorCount = ((Integer) groupToColorCount.get(group)).intValue();
                
                if(freeColors.length < colorCount)
                    freeColors = new int[Math.max(freeColors.length * 2, colorCount)];
                
                // Set all colors to free.
                {
                    for(int i= 0; i < colorCount; i++)
                        freeColors[i] = 1;
                }
                
                // Remove unavailable colors for this local
                {
                    Local[] interferences = intGraph.getInterferencesOf(local);

                    for(int i = 0; i < interferences.length; i++)
                    {
                        if(localToColor.containsKey(interferences[i]))
                        {
                            int usedColor = ((Integer) localToColor.get(interferences[i])).intValue();
                
                            freeColors[usedColor] = 0;
                        }
                    }
                }
                
                // Assign a color to this local.
                {
                    String originalName = (String) localToOriginalName.get(local);
                    List originalNameColors = (List) originalNameToColors.get(originalName);
                    
                    if(originalNameColors == null)
                    {
                        originalNameColors = new ArrayList();
                        originalNameToColors.put(originalName, originalNameColors);
                        
                    }
                    
                    boolean found = false;
                    int assignedColor = 0;
                    
                    // Check if the colors assigned to this original name is already free
                    {
                        Iterator colorIt = originalNameColors.iterator();
                        
                        while(colorIt.hasNext())
                        {
                            Integer color = (Integer) colorIt.next();
                            
                            if(freeColors[color.intValue()] == 1)
                            {
                                found = true;
                                assignedColor = color.intValue();
                            }
                        }
                    }
                    
                    if(!found)
                    {
                        assignedColor = colorCount++;
                        groupToColorCount.put(group, new Integer(colorCount));
                        originalNameColors.add(new Integer(assignedColor));
                    }   
                    
                    localToColor.put(local, new Integer(assignedColor));
                }
            }
        }
                            
    }    
    
    public static void assignColorsToLocals(Body unitBody, Map localToGroup, 
        Map localToColor, Map groupToColorCount)
    {
       
        CompleteUnitGraph unitGraph = new CompleteUnitGraph(unitBody);

        UnitLiveLocals liveLocals;

       
	liveLocals = new SimpleUnitLiveLocals(unitGraph);
       

        UnitInterferenceGraph intGraph = new UnitInterferenceGraph(unitBody, localToGroup, liveLocals);
	Iterator it = intGraph.getLocals().iterator();
	
	while(it.hasNext()) {
	    Local local = (Local) it.next();
	    Local[] locals = intGraph.getInterferencesOf(local);
	    System.out.print(local + ":");
	    for(int i = 0; i < locals.length; i++) 
		System.out.print(locals[i] + " ");
	    System.out.println("");
	}

        // Assign a color for each local.
        {
            int[] freeColors = new int[10];
            Iterator localIt = intGraph.getLocals().iterator();
            
            while(localIt.hasNext())
            {
                Local local = (Local) localIt.next();
                
                if(localToColor.containsKey(local))
                {
                    // Already assigned, probably a parameter
                    continue;
                }
                
                Object group = localToGroup.get(local);
                int colorCount = ((Integer) groupToColorCount.get(group)).intValue();
                
                if(freeColors.length < colorCount)
                    freeColors = new int[Math.max(freeColors.length * 2, colorCount)];
                
                // Set all colors to free.
                {
                    for(int i= 0; i < colorCount; i++)
                        freeColors[i] = 1;
                }
                
                // Remove unavailable colors for this local
                {
                    Local[] interferences = intGraph.getInterferencesOf(local);

                    for(int i = 0; i < interferences.length; i++)
                    {
                        if(localToColor.containsKey(interferences[i]))
                        {
                            int usedColor = ((Integer) localToColor.get(interferences[i])).intValue();
                
                            freeColors[usedColor] = 0;
                        }
                    }
                }
                
                // Assign a color to this local.
                {
                    boolean found = false;
                    int assignedColor = 0;
                    
                    for(int i = 0; i < colorCount; i++)
                        if(freeColors[i] == 1)
                        {
                            found = true;
                            assignedColor = i;
                        }
                    
                    if(!found)
                    {
                        assignedColor = colorCount++;
                        groupToColorCount.put(group, new Integer(colorCount));
                    }   
                    
                    localToColor.put(local, new Integer(assignedColor));
                }
            }
        }
                            
    }

    public static class UnitInterferenceGraph
    {
        Map localToLocals;// Maps a local to its interfering locals.
        List locals;
        
        private UnitInterferenceGraph()
        {
        }
    
        public List getLocals()
        {
            return locals;
        }
        
        public UnitInterferenceGraph(Body body, Map localToGroup, UnitLiveLocals liveLocals)
        {

            locals = new ArrayList();
            locals.addAll(body.getLocals());
            
            // Initialize localToLocals
            {
                localToLocals = new HashMap(body.getLocalCount() * 2 + 1, 0.7f);
    
                Iterator localIt = body.getLocals().iterator();
    
                while(localIt.hasNext())
                {
                    Local local = (Local) localIt.next();
    
                    localToLocals.put(local, new ArraySet());
                }
            }
    
            // Go through code, noting interferences
            {
                Iterator codeIt = body.getUnits().iterator();
    
                while(codeIt.hasNext())
		    {
			Unit unit = (Unit) codeIt.next();
    
			List liveLocalsAtUnit = liveLocals.getLiveLocalsAfter(unit);
                    
			// Note interferences if this statement is a definition
			{
			    List defBoxes = unit.getDefBoxes();
		
			    if(!defBoxes.isEmpty()) {
		
				if(!(defBoxes.size() ==1)) 
				    throw new RuntimeException("UnitFastColorer: invalid number of def boxes");
			    
				if(((ValueBox)defBoxes.get(0)).getValue() instanceof Local) {
		
				    Local defLocal = (Local) ((ValueBox)defBoxes.get(0)).getValue();
                  
				    Iterator localIt = liveLocalsAtUnit.iterator();
				
				    while(localIt.hasNext())
					{
					    Local otherLocal = (Local) localIt.next();
                
					    if(localToGroup.get(otherLocal).equals(
										   localToGroup.get(defLocal)))
					    {
		
						setInterference(defLocal, otherLocal);
					    }
					}
				}   
			    } 
		    
			}                    
		    }
            }
        }
    
        public boolean localsInterfere(Local l1, Local l2)
        {
            return ((Set) localToLocals.get(l1)).contains(l2);
        }
    
        public void setInterference(Local l1, Local l2)
        {
            ((Set) localToLocals.get(l1)).add(l2);
            ((Set) localToLocals.get(l2)).add(l1);
        }
    
        public boolean isEmpty()
        {
            return localToLocals.isEmpty();
        }
        
        Local[] getInterferencesOf(Local l)
        {
            Object[] objects = ((Set) localToLocals.get(l)).toArray();
            Local[] locals = new Local[objects.length];
    
            for(int i = 0; i < objects.length; i++)
                locals[i] = (Local) objects[i];
    
            return locals; 
        }
    }
}
