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

 - Modified on March 14, 1999 by Raja Vallee-Rai (rvalleerai@sable.mcgill.ca) (*)
   First release.
*/

package ca.mcgill.sable.soot.jimple;

import ca.mcgill.sable.soot.*;
import ca.mcgill.sable.util.*;

class SimpleEqualLocals implements EqualLocals
{
    Map localStmtPairToDefs;
    LiveLocals liveLocals;
    
    SimpleEqualLocalsAnalysis analysis;
    
    public SimpleEqualLocals(CompleteStmtGraph g)
    {
        analysis = new SimpleEqualLocalsAnalysis(g);
    }

    public boolean isLocalEqualToAt(Local l, Local m, Stmt s) 
    {
        if(l == m)
            return true;
        
        FlowSet set = (FlowSet) analysis.getFlowBeforeStmt(s);
        
        return set.contains(new LocalCopy(l, m));
    }
    
    public List getCopiesAt(Stmt s)
    {
        FlowSet set = (FlowSet) analysis.getFlowBeforeStmt(s);
        
        return set.toList();
    }
}
    
class SimpleEqualLocalsAnalysis extends ForwardFlowAnalysis
{
    FlowSet emptySet;
    
    public SimpleEqualLocalsAnalysis(StmtGraph g)
    {
        super(g);

        emptySet = new ArraySparseSet();

        doAnalysis();
    }

    protected Object newInitialFlow()
    {
        return emptySet.clone();
    }

    protected void flowThrough(Object inValue, Stmt stmt, Object outValue)
    {
        FlowSet in = (FlowSet) inValue, out = (FlowSet) outValue;

        in.copy(out);
        
        if(stmt instanceof DefinitionStmt)
        {
            DefinitionStmt d = (DefinitionStmt) stmt;

            if(d.getLeftOp() instanceof Local)
            {
                // Of the form x  = ...  so remove local from its 
                // current equiv class
                
                Local x  = (Local) d.getLeftOp();
    
                Iterator copyIt = in.toList().iterator();
                
                while(copyIt.hasNext())
                {
                    LocalCopy copy = (LocalCopy) copyIt.next();
                    
                    if(copy.leftLocal == x || copy.rightLocal == x)
                        out.remove(copy, out);
                }
                    
                if(d.getRightOp() instanceof Local)
                {
                    // Of the form x = y, so make x equivalent to everything
                    // that y is equivalent to.
                    
                    Local y = (Local) d.getRightOp();
                    
                    if(x != y)
                    {
                        out.add(new LocalCopy(x, y), out);
                        out.add(new LocalCopy(y, x), out);
                        
                        copyIt = in.toList().iterator();
                    
                        while(copyIt.hasNext())
                        {
                            LocalCopy copy = (LocalCopy) copyIt.next();
                            Local other;
                            
                            if(copy.leftLocal == y & copy.rightLocal != x)
                                out.add(new LocalCopy(x, copy.rightLocal), out);
                                
                            if(copy.rightLocal == y & copy.leftLocal != x)
                                out.add(new LocalCopy(copy.leftLocal, x), out);
                        }
                    }   
                }
            }
        }
    }

    protected void copy(Object source, Object dest)
    {
        FlowSet sourceSet = (FlowSet) source,
            destSet = (FlowSet) dest;
            
        sourceSet.copy(destSet);
    }

    protected void merge(Object in1, Object in2, Object out)
    {
        FlowSet inSet1 = (FlowSet) in1,
            inSet2 = (FlowSet) in2;

        FlowSet outSet = (FlowSet) out;

        inSet1.intersection(inSet2, outSet);
    }
}
