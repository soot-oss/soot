/* Soot - a J*va Optimization Framework
 * Copyright (C) 2000 Patrick Lam
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

package soot.jimple.toolkits.scalar.pre;

import soot.*;
import soot.jimple.*;
import soot.toolkits.scalar.*;
import soot.toolkits.graph.*;
import java.util.*;

/** An expression's value is <i>locally anticipatable</i> in a basic block <code>b</code> 
 * if it is computed in that block and if moving that computation to the beginning of
 * the block would leave the effect of the block unchanged. 
 * That is, there can be no def's of the operands. */
class LocallyAnticipatableExprs
{
    /* universe is all expressions in the program. */
    public static BoundedFlowSet getAntLocExprsOf(Block b, FlowUniverse uni)
    {
        BoundedFlowSet retVal = new ArrayPackedSet(uni);

        Iterator it = b.iterator();
    
        HashSet defdLocals = new HashSet();

        while (it.hasNext())
        {
            Unit u = (Unit)it.next();

            if (u instanceof AssignStmt)
            {
                boolean fail = false;
                AssignStmt as = (AssignStmt)u;

                Value rhs = as.getRightOp();

                if (!(rhs instanceof Expr))
                    fail = true;

                // screen out for the case where the rhs is previously-defined
                if (!fail)
                {
                    Iterator loIt = rhs.getUseBoxes().iterator();
                    while (loIt.hasNext())
                        if (defdLocals.contains(loIt.next()))
                            fail = true;
                }

                if (!fail)
                    retVal.add(rhs, retVal);
            }

            defdLocals.addAll(u.getDefBoxes());
        }
        return retVal;
    }
}
