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

package soot.jimple.toolkits.scalar.obsoletepre;

import soot.*;
import soot.jimple.*;
import soot.toolkits.scalar.*;
import soot.toolkits.graph.*;
import java.util.*;

/** An expression's value is <i>locally transparent</i> in a basic block <code>b</code> 
 * if there are no assignments in <code>b</code> to variables of the expression. */
class LocallyTransparentExprs
{
    public static BoundedFlowSet getTransLocExprsOf(Block b, FlowUniverse uni)
    {
        Map localsToExprList = new HashMap();

        for (int i = 0; i < uni.getSize(); i++)
        {
            Expr e = (Expr)uni.getObjectOf(i);
            Iterator useBoxesIt = e.getUseBoxes().iterator();
            while (useBoxesIt.hasNext())
            {
                ValueBox vb = (ValueBox)useBoxesIt.next();
                if (vb.getValue() instanceof Local)
                {
                    Local l = (Local)vb.getValue();
                    if (localsToExprList.containsKey(l))
                    {
                        List exprList = (List)localsToExprList.get(l);
                        exprList.add(e);
                    }
                    else
                    {
                        List exprList = new ArrayList();
                        exprList.add(e);
                        localsToExprList.put(l, exprList);
                    }
                }
            }
        }

        HashSet nonTransExprs = new HashSet();

        Iterator it = b.iterator();
        while (it.hasNext())
        {
            Iterator defBoxesIt = ((Unit)it.next()).getDefBoxes().iterator();

            while (defBoxesIt.hasNext())
            {
                ValueBox vb = (ValueBox)defBoxesIt.next();
                Value v = vb.getValue();
                if (v instanceof Local && localsToExprList.get(v) != null)
                {
                    Iterator badExprsIt = ((List)localsToExprList.get
                                           (v)).iterator();
                    while (badExprsIt.hasNext())
                        nonTransExprs.add(badExprsIt.next());
                }
            }
        }

        BoundedFlowSet transExprs = new ArrayPackedSet(uni);

        for (int i = 0; i < uni.getSize(); i++)
        {
            Object e = uni.getObjectOf(i);
            if (!nonTransExprs.contains(e))
                transExprs.add(e, transExprs);
        }

        return transExprs;
    }
}
