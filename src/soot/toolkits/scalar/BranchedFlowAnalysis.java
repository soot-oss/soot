/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-2000 Patrick Lam
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

/*
 * Modified by the Sable Research Group and others 1997-2000.
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */



/*
    2000, March 20 - Updated code provided by
                                    Patrick Lam <plam@sable.mcgill.ca>
                     from 1.beta.4.dev.60
                     to 1.beta.6.dev.34
                     -- Janus (Richard Godard)
*/

package soot.toolkits.scalar;

import soot.*;
import soot.toolkits.graph.*;
import soot.util.*;
import java.util.*;

/** Abstract class providing functionality for branched flow analysis.
 *
 * A branched flow analysis is one which can propagate different
 * information to the successors of a node.  This is useful for
 * propagating information past a statement like <code>if(x &gt;
 * 0)</code>: one successor has <code>x &gt; 0</code> while the other
 * successor has <code>x &le; 0</code>. */
public abstract class BranchedFlowAnalysis extends AbstractFlowAnalysis
{
    /** Maps graph nodes to OUT sets. */
    protected Map unitToAfterFallFlow;
    protected Map unitToAfterBranchFlow;

    public BranchedFlowAnalysis(UnitGraph graph)
    {
        super(graph);

        unitToAfterFallFlow = new HashMap(graph.size() * 2 + 1, 0.7f);
        unitToAfterBranchFlow = new HashMap(graph.size() * 2 + 1, 0.7f);
    }

    /** Given the merge of the <code>in</code> sets, 
     * compute the <code>fallOut</code> and <code>branchOuts</code>
     * set for <code>s</code>. */
    protected abstract void flowThrough(Object in, Unit s, 
                                        List fallOut, List branchOuts);

    public Object getFallFlowAfter(Unit s)
    {
        List fl = (List) unitToAfterFallFlow.get(s);

        if (fl.isEmpty())
            return newInitialFlow();
        else
            return fl.get(0);
    }


    public List getBranchFlowAfter(Unit s)
    {
        return (List) (unitToAfterBranchFlow.get(s));
    }

    public Object getFlowBefore(Unit s)
    {
        return unitToBeforeFlow.get(s);
    }
} // end class BranchedFlowAnalysis

