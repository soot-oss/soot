/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Jimple, a 3-address code Java(TM) bytecode representation.        *
 * Copyright (C) 1997, 1998 Raja Vallee-Rai (kor@sable.mcgill.ca)    *
 * All rights reserved.                                              *
 *                                                                   *
 * Modifications by Etienne Gagnon (gagnon@sable.mcgill.ca) are      *
 * Copyright (C) 1998 Etienne Gagnon (gagnon@sable.mcgill.ca).  All  *
 * rights reserved.                                                  *
 *                                                                   *
 * Modifications by Patrick Lam (plam@sable.mcgill.ca) are           *
 * Copyright (C) 1999 Patrick Lam.  All rights reserved.             *
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

 - Modified on April 20, 1999 by Raja Vallee-Rai (rvalleerai@sable.mcgill.ca) (*) 
   Added a unsplitOriginalLocals method which packs only those non-stack
   variables which have been split.
   Split off from Transformations.java
*/

package ca.mcgill.sable.soot.jimple;

import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.util.*;
import java.util.*;

public class LocalPacker
{
    public static void packLocals(StmtBody body)
    {
        packLocals_internal(body, false);        
    }
    
    public static void unsplitOriginalLocals(StmtBody body)
    {
        packLocals_internal(body, true);    
    }
    
    private static void packLocals_internal(StmtBody body, boolean isConservative)
    {
    
        Map localToGroup = new DeterministicHashMap(body.getLocalCount() * 2 + 1, 0.7f);
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
                Stmt s = (Stmt) codeIt.next();

                if(s instanceof IdentityStmt &&
                    ((IdentityStmt) s).getLeftOp() instanceof Local)
                {
                    Local l = (Local) ((IdentityStmt) s).getLeftOp();
                    
                    Object group = localToGroup.get(l);
                    int count = ((Integer) groupToColorCount.get(group)).intValue();
                    
                    localToColor.put(l, new Integer(count));
                    
                    count++;
                    
                    groupToColorCount.put(group, new Integer(count));
                }
            }
        }
        
        // Call the graph colorer.
            if(isConservative)
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
                Stmt s = (Stmt) codeIt.next();

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

