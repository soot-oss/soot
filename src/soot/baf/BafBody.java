/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Baf, a Java(TM) bytecode analyzer framework.                      *
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

 - Modified on November 2, 1998 by Raja Vallee-Rai (kor@sable.mcgill.ca) (*)
   Repackaged all source files and performed extensive modifications.
   First initial release of Soot.

 - Modified on 15-Jun-1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   First internal release (Version 0.1).
*/

package soot.baf;

import soot.*;
import soot.jimple.*;
import soot.baf.toolkit.scalar.*;
import soot.toolkit.scalar.*;

import ca.mcgill.sable.util.*;
import java.util.*;
import java.io.*;

public class BafBody extends Body
{
    public Object clone()
    {
        Body b = new JimpleBody(getMethod());
        b.importBodyContentsFrom(this);
        return b;
    }

    public BafBody(Body body)
    {
        super(body.getMethod());

        JimpleBody jimpleBody;

        if(body instanceof JimpleBody)
            jimpleBody = (JimpleBody) body;
        else
            throw new RuntimeException("Can only construct BafBody's directly"
              + " from JimpleBody's.");
       
        JimpleToBafContext context = new JimpleToBafContext(jimpleBody.getLocalCount());
           
        // Convert all locals
        {
            Iterator localIt = jimpleBody.getLocals().iterator();
            
            while(localIt.hasNext())
            {
                Local l = (Local) localIt.next();
                Type t = l.getType();
                Local newLocal;
                
                newLocal = Baf.v().newLocal(l.getName(), UnknownType.v());
                
                if(t.equals(DoubleType.v()) || t.equals(LongType.v()))
                    newLocal.setType(DoubleWordType.v());
                else
                    newLocal.setType(WordType.v());
        
                context.setBafLocalOfJimpleLocal(l, newLocal);            
                getLocals().add(newLocal);
            }
        }
    
        Map stmtToFirstInstruction = new HashMap();
            
        // Convert all jimple instructions
        {
            Iterator stmtIt = jimpleBody.getUnits().iterator();
            
            while(stmtIt.hasNext())
            {
                Stmt s = (Stmt) stmtIt.next();
                List conversionList = new ArrayList();

                ((ConvertToBaf) s).convertToBaf(context, conversionList);
                
                stmtToFirstInstruction.put(s, conversionList.get(0));
                getUnits().addAll(conversionList);
            }
        }
        
        // Change all place holders
        {
            Iterator boxIt = getUnitBoxes().iterator();
            
            while(boxIt.hasNext())
            {
                UnitBox box = (UnitBox) boxIt.next();
                
                if(box.getUnit() instanceof PlaceholderInst)
                {
                    Unit source = ((PlaceholderInst) box.getUnit()).getSource();
                    box.setUnit((Unit) stmtToFirstInstruction.get(source));
                }
            }
        }

        // Convert all traps
        {
            Iterator trapIt = jimpleBody.getTraps().iterator();
            while (trapIt.hasNext())
            {
                Trap trap = (Trap) trapIt.next();

                getTraps().add(Baf.v().newTrap(trap.getException(),
                     (Unit)stmtToFirstInstruction.get(trap.getBeginUnit()),
                     (Unit)stmtToFirstInstruction.get(trap.getEndUnit()),
                     (Unit)stmtToFirstInstruction.get(trap.getHandlerUnit())));
            }
        }
        
        // Perform some optimizations on the naive baf code
        {
             LoadStoreOptimizer.v().optimize(this);
             UnusedLocalRemover.removeUnusedLocals(this);
             LocalPacker.packLocals(this);
        }
    }
}
