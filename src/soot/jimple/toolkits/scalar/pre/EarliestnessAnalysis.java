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

/** An expression is <i>earliest</i> at the entrance to basic block <code>b</code> 
 * if no block from <code>entry</code> to <code>b</code> both includes a computation of
 * the expression and produces the same value as evaluating the expression at the
 * entrance to block <code>b</code>. */
class EarliestnessAnalysis extends ForwardFlowAnalysis
{
    BoundedFlowSet emptySet;
    FlowUniverse exprUniv;

    HashMap blockToGenerateSet;
    HashMap blockToPreserveSet;       

    public EarliestnessAnalysis(BlockGraph g, AnticipatableExprs a, FlowUniverse exprUniv)
    {
        super(g);
        blockToGenerateSet = new HashMap(g.size() * 2 + 1, 0.7f);
        blockToPreserveSet = new HashMap(g.size() * 2 + 1, 0.7f);

        emptySet = new ArrayPackedSet(exprUniv);
        this.exprUniv = exprUniv;

        Iterator blockIt = g.iterator();

        while (blockIt.hasNext())
        {
            Block b = (Block)blockIt.next();

            BoundedFlowSet nonTrans = LocallyTransparentExprs.getTransLocExprsOf(b, exprUniv);
            nonTrans.complement(nonTrans);
            blockToGenerateSet.put(b, nonTrans);

            BoundedFlowSet ant = a.getAnticipatableExprsBefore(b);
            blockToPreserveSet.put(b, ant);
        }

        doAnalysis();
    }

    protected Object newInitialFlow()
    {
        BoundedFlowSet allExprs = (BoundedFlowSet)emptySet.clone();
        allExprs.complement(allExprs);

        return allExprs;
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
        
        inSet1.union(inSet2, outSet);
    }

    protected void flowThrough(Object inValue, Object b, Object outValue)
    {
        BoundedFlowSet in = (BoundedFlowSet) inValue, out = (BoundedFlowSet) outValue;

        // Remove non-preserved sets from `in' and dump into `out'.
        in.intersection((FlowSet) blockToPreserveSet.get(b), out);

        // Add generated sets to `out'.
        out.union((FlowSet) blockToGenerateSet.get(b), out);
    }
}
