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
 The reference version is: $JimpleVersion: 0.5 $

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

 - Modified on September 29, 1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   Fixed a bug with the building of the InterferenceGraph.  It used to
   improperly assign locals to definitions that were never used.
   

 - Modified on 23-Jul-1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   Renamed the uses of Hashtable to HashMap.

 - Modified on 15-Jun-1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   First internal release (Version 0.1).
*/
 
package ca.mcgill.sable.soot.jimple;

import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.util.*;

public class InterferenceGraph
{
    Map localToLocals;

    private InterferenceGraph()
    {
    }
        
    public InterferenceGraph(JimpleBody body, Type type, LiveLocals liveLocals)
    {
        StmtList stmtList = body.getStmtList();
        
        // Initialize localToLocals
        {
            localToLocals = new HashMap(body.getLocalCount() * 2 + 1, 0.7f);
            
            Iterator localIt = body.getLocals().iterator();
            
            while(localIt.hasNext())
            {
                Local local = (Local) localIt.next();
                
                if(local.getType().equals(type))
                    localToLocals.put(local, new ArraySet());
            }
        }   
        
        // Go through code, noting interferences
        {
            Iterator codeIt = stmtList.iterator();
            
            while(codeIt.hasNext())
            {
                Stmt stmt = (Stmt) codeIt.next();
                
                List liveLocalsAtStmt = liveLocals.getLiveLocalsAfter(stmt);
                List locals = new ArrayList();
                
                locals.addAll(liveLocalsAtStmt);
                
                // Augment live locals with the variable just defined
                {
                    if(stmt instanceof DefinitionStmt)
                    {
                        DefinitionStmt def = (DefinitionStmt) stmt;
                        
                        if(def.getLeftOp() instanceof Local)
                        {
                            if(!locals.contains(def.getLeftOp()))
                                locals.add(def.getLeftOp());                
                        }
                    }
                }
                
                int localCount = locals.size();
                
                for(int j = 0; j < localCount; j++)
                {
                    Local l1 = (Local) locals.get(j);
                       
                    for(int k = j + 1; k < localCount; k++)
                    {
                         Local l2 = (Local) locals.get(k);
                              
                        if(l1.getType().equals(type) &&
                            l2.getType().equals(type))
                        {
                            // Record this interference

                            setInterference(l1, l2);
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
    
    public void removeLocal(Local local)
    {
        Object[] locals = ((Set) localToLocals.get(local)).toArray();
            
        // Handle all inverse edges
            for(int i = 0; i < locals.length; i++)
                ((Set) localToLocals.get(locals[i])).remove(local);
                
        // Handle all outgoing edges
            localToLocals.remove(local);              
    }
        
    public Local removeMostInterferingLocal()
    {
        if(isEmpty())
            throw new RuntimeException("graph is empty");
            
        
        Iterator it = localToLocals.entries().iterator();
        Local top = (Local) ((Map.Entry) it.next()).getKey(); 
        
        while(it.hasNext())
        {
            Local other = (Local) ((Map.Entry) it.next()).getKey();
            
            if(((Set) localToLocals.get(other)).size() > ((Set) localToLocals.get(top)).size())
                top = other;
        }
        
        
        removeLocal(top);
        
        return top;
    }
    
    Local[] getInterferencesOf(Local l)
    {
        Object[] objects = ((Set) localToLocals.get(l)).toArray();
        Local[] locals = new Local[objects.length];
        
        for(int i = 0; i < objects.length; i++)
            locals[i] = (Local) objects[i];
            
        return locals;
    }

    /*    
    protected Object clone()
    {
        InterferenceGraph newGraph = InterferenceGraph();
        
        // Clone all the elements
        {
            Iterator it = localToLocals.entries().iterator();
            
    
            while(it.hasNext())
            {
                Local local = (Local) ((Map.Entry) it.next()).getValue();
                
                newGraph.put(local, localToLocals.get(local).clone());
            }
        }
    } */  
}
