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

 - Modified on 15-Jun-1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   First internal release (Version 0.1).
*/
 
package ca.mcgill.sable.soot.jimple;

import ca.mcgill.sable.soot.baf.*;
import ca.mcgill.sable.util.*;

public abstract class ForwardFlowAnalysis extends FlowAnalysis 
{   
    public ForwardFlowAnalysis(StmtGraph graph)
    {
        super(graph);
    }
    
    protected boolean isForward()
    {
        return true;
    }
    
    protected void doAnalysis()
    {
        LinkedList changedStmts = new LinkedList();
        // HashSet changedStmtsSet = new HashSet();
             
        // Set initial values and nodes to visit.
        {
            Iterator it = graph.iterator();
            Flow initialFlow = getInitialFlow();
            
            while(it.hasNext())
            {
                Stmt s = (Stmt) it.next();

                changedStmts.addLast(s);
                // changedStmtsSet.add(s);
                        
                stmtToBeforeFlow.put(s, initialFlow.clone());
                stmtToAfterFlow.put(s, initialFlow.clone());
            }
        }
        
        // Perform fixed point flow analysis    
        {
            Flow previousAfterFlow = (Flow) getInitialFlow().clone();
            
            while(!changedStmts.isEmpty())
            {
                Flow beforeFlow;
                Flow afterFlow;
                
                Stmt s = (Stmt) changedStmts.removeFirst();
                
                // changedStmtsSet.remove(s);

                ((Flow) stmtToAfterFlow.get(s)).copy(previousAfterFlow);
                
                // Compute and store beforeFlow
                {
                    List preds = graph.getPredsOf(s);
                    
                    beforeFlow = (Flow) stmtToBeforeFlow.get(s);
                    
                    if(preds.size() == 1)
                        ((Flow) stmtToAfterFlow.get(preds.get(0))).copy(beforeFlow);
                    else if(preds.size() != 0)
                    {
                        Iterator predIt = preds.iterator();
                        
                        ((Flow) stmtToAfterFlow.get(predIt.next())).copy(beforeFlow);
                            
                        while(predIt.hasNext())
                        {
                            Flow otherBranch = (Flow) stmtToAfterFlow.get(predIt.next());  
                            merge(beforeFlow, otherBranch, beforeFlow);
                        } 
                    }
                }
                
                // Compute afterFlow and store it.
                {
                    afterFlow = (Flow) stmtToAfterFlow.get(s);
                    flowThrough(beforeFlow, s, afterFlow);
                }
                
                // Update queue appropriately
                    if(!afterFlow.equals(previousAfterFlow))
                    {
                        Iterator succIt = graph.getSuccsOf(s).iterator();
                        
                        while(succIt.hasNext())
                        {
                            // if(!changedStmts.contains(succs[i]))
                            changedStmts.addLast(succIt.next());
                        }
                    }
            }
        }
    }    
}


