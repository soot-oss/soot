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

/** An expression is <i>delayed</i> at the entrance to basic block <code>b</code> 
 * if it is anticipatable and earliest at that point and if all subsequent
 * computations of it are in block <code>b</code>. */
class DelayednessAnalysis extends ForwardFlowAnalysis
{
    BoundedFlowSet emptySet;
    FlowUniverse exprUniv;

    HashMap blockToGenerateSet;
    HashMap blockToPreserveSet;
    AnticipEarliestExprs anea;
    BlockGraph g;

    public DelayednessAnalysis(BlockGraph g, AnticipEarliestExprs anea, FlowUniverse exprUniv)
    {
        super(g);
        blockToGenerateSet = new HashMap(g.size() * 2 + 1, 0.7f);
        blockToPreserveSet = new HashMap(g.size() * 2 + 1, 0.7f);
        this.anea = anea;
        this.g = g;

        emptySet = new ArrayPackedSet(exprUniv);
        this.exprUniv = exprUniv;

        Iterator blockIt = g.iterator();
        while (blockIt.hasNext())
        {
            Block b = (Block)blockIt.next();

            BoundedFlowSet aneaSet = anea.getAnticipEarliestExprsBefore(b);
            blockToGenerateSet.put(b, aneaSet);

            BoundedFlowSet ant = LocallyAnticipatableExprs.getAntLocExprsOf(b, exprUniv);
            ant.complement(ant);
            blockToPreserveSet.put(b, ant);
        }

        doAnalysis();
    }

    protected void customizeInitialFlowGraph()
    {
        Iterator blockIt = g.getHeads().iterator();

        while (blockIt.hasNext())
        {
            Block b = (Block) blockIt.next();
            unitToAfterFlow.put(b, anea.getAnticipEarliestExprsBefore(b));
        }
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

        // Add generated sets to `out'.
        in.union((FlowSet) blockToGenerateSet.get(b), out);

        // Intersect with \neg AntLoc.
        out.intersection((FlowSet) blockToPreserveSet.get(b), out);
    }
}
