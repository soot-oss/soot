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

 - Modified on 23-Jul-1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   Renamed the uses of Hashtable to HashMap.

 - Modified on 15-Jun-1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   First internal release (Version 0.1).
*/
 
package ca.mcgill.sable.soot.jimple;

import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.util.*;

public class LocalCopies
{
    Map stmtToCopies;
    
    public LocalCopies(StmtGraphBody graphBody)
    {
        CopiesFlowAnalysis analysis = new CopiesFlowAnalysis(graphBody);
        StmtGraph g = graphBody.getStmtGraph();
        
        // Build up stmtToCopies map
        {
            stmtToCopies = new HashMap(g.size() * 2 + 1, 0.7f);
            
            Iterator stmtIt = g.iterator();
            
            while(stmtIt.hasNext())
            {
                Stmt s = (Stmt) stmtIt.next();
                
                FlowSet copies = (FlowSet) analysis.getFlowBeforeStmt(s);
                
                stmtToCopies.put(s, Collections.unmodifiableList(copies.toList()));
            }
        }
    }
    
    public boolean isLocalCopyOfBefore(Local x, Local y, Stmt s)
    {
        List copies = (List) stmtToCopies.get(s);
        
        return copies.contains(new LocalCopy(x, y));
    }
    
    public List getCopiesBefore(Stmt s)
    {
        return (List) stmtToCopies.get(s);
    }
}

class CopiesFlowAnalysis extends ForwardFlowAnalysis
{
    FlowSet emptySet;
    Map localToPreserveSet;
        
    CopiesFlowAnalysis(StmtGraphBody graphBody)
    {
        super(graphBody.getStmtGraph());
        StmtGraph g = graphBody.getStmtGraph();
        
        List copiesList;
        
        // Create list of possible copies
        {
            copiesList = new ArrayList();
               
            Iterator stmtIt = g.iterator();
            
            while(stmtIt.hasNext())
            {
                Stmt s = (Stmt) stmtIt.next();
                
                if(s instanceof DefinitionStmt)
                {
                    DefinitionStmt def = (DefinitionStmt) s;
                    
                    if(def.getLeftOp() instanceof Local &&
                        def.getRightOp() instanceof Local)
                    {
                        copiesList.add(new LocalCopy((Local) def.getLeftOp(), 
                            (Local) def.getRightOp()));
                    }
                }        
            }
            
            FlowUniverse copiesUniverse = new FlowUniverse(copiesList.toArray());
            emptySet = PackSet.v(copiesUniverse);
        }

        // Create preserve sets for each local.
        {
            localToPreserveSet = new HashMap(graphBody.getLocalCount() * 2 + 1, 0.7f);
            
            // Initialize each set to empty
            {
                Iterator localIt = graphBody.getLocals().iterator();
                
                while(localIt.hasNext())
                    localToPreserveSet.put(localIt.next(), emptySet.clone());
            }
            
            // Go through all the copies, add the copy to the killSet of the involved locals
            {
                Iterator copyIt = copiesList.iterator();
                
                while(copyIt.hasNext())
                {
                    LocalCopy copy = (LocalCopy) copyIt.next();
                    
                    FlowSet fset = (FlowSet) localToPreserveSet.get(copy.leftLocal);
                    
                    fset.add(copy, fset);
                    
                    fset = (FlowSet) localToPreserveSet.get(copy.rightLocal);
                    fset.add(copy, fset);
                }
            }
            
            // Flip all the kill sets to really become preserve sets
            {
                Iterator localIt = graphBody.getLocals().iterator();
                
                while(localIt.hasNext())
                {
                    FlowSet preserveSet = (FlowSet) localToPreserveSet.get(localIt.next());
                    
                    preserveSet.complement(preserveSet);
                }
            }
        }
            
                
        doAnalysis();
    }
    
    protected Flow getInitialFlow()
    {
        return emptySet;
    }
    
    protected void flowThrough(Flow inValue, Stmt stmt, Flow outValue)
    {
        FlowSet in = (FlowSet) inValue, out = (FlowSet) outValue;
        
        if(stmt instanceof DefinitionStmt)
        {
            // Perform Kill
            {
                DefinitionStmt def = (DefinitionStmt) stmt;
                
                if(def.getLeftOp() instanceof Local)
                    in.intersection((FlowSet) localToPreserveSet.get(def.getLeftOp()), out);
                else
                    in.copy(out);
            }
            
            // Perform generation
            {
                DefinitionStmt def = (DefinitionStmt) stmt;
                
                if(def.getLeftOp() instanceof Local && def.getRightOp() instanceof Local)
                {
                    out.add(new LocalCopy((Local) def.getLeftOp(), (Local) def.getRightOp()), 
                        out);
                    
                }
            }
        }
        else
            in.copy(out);
    }
    
    protected void merge(Flow in1, Flow in2, Flow out)
    {
        FlowSet inSet1 = (FlowSet) in1,
            inSet2 = (FlowSet) in2;
            
        FlowSet outSet = (FlowSet) out;
        
        inSet1.intersection(inSet2, outSet);
    }
}





