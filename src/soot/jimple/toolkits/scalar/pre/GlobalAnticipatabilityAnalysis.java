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

/** An expression's value is <i>globally anticipatable</i> in a basic block <code>b</code> 
 * if every path from the entry point of <code>b</code> includes a computation of
 * the expression and if placing that computation at any point on these paths
 * would leave the expression's value unchanged. */
class GlobalAnticipatabilityAnalysis extends BackwardFlowAnalysis
{
    BoundedFlowSet emptySet;
    FlowUniverse exprUniv;

    HashMap blockToGenerateSet;
    HashMap blockToPreserveSet;       

    public GlobalAnticipatabilityAnalysis(BlockGraph g, FlowUniverse exprUniv)
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
            BoundedFlowSet genSet = (BoundedFlowSet)emptySet.clone();

            genSet.union(LocallyAnticipatableExprs.getAntLocExprsOf(b, exprUniv), genSet);
            blockToGenerateSet.put(b, genSet);

            BoundedFlowSet killSet = (BoundedFlowSet)emptySet.clone();
            killSet.union(LocallyTransparentExprs.getTransLocExprsOf(b, exprUniv), killSet);
            killSet.complement(killSet);
            blockToPreserveSet.put(b, killSet);
        }

        doAnalysis();
    }

    protected Object newInitialFlow()
    {
        return emptySet.clone();
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

    protected void flowThrough(Object inValue, Directed b, Object outValue)
    {
        BoundedFlowSet in = (BoundedFlowSet) inValue, out = (BoundedFlowSet) outValue;

        // Remove non-preserved sets from `out' and dump into `in'.
        out.intersection((FlowSet) blockToPreserveSet.get(b), in);

        // Add generated sets to `in'.
        in.union((FlowSet) blockToGenerateSet.get(b), in);
    }
}
