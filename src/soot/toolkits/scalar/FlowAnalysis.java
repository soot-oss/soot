/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
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
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */


package soot.toolkits.scalar;

import soot.*;
import soot.toolkits.graph.*;
import soot.util.*;
import java.util.*;

/** An abstract class providing a framework for carrying out dataflow analysis.
 * Subclassing either BackwardFlowAnalysis or ForwardFlowAnalysis and providing
 * implementations for the abstract methods will allow Soot to compute the
 * corresponding flow analysis. */
public abstract class FlowAnalysis extends AbstractFlowAnalysis
{
    /** Maps graph nodes to OUT sets. */
    protected Map unitToAfterFlow;

    /** Filtered: Maps graph nodes to OUT sets. */
    protected Map filterUnitToAfterFlow;

    /** Constructs a flow analysis on the given <code>DirectedGraph</code>. */
    public FlowAnalysis(DirectedGraph graph)
    {
        super(graph);
        unitToAfterFlow = new HashMap(graph.size() * 2 + 1, 0.7f);
    }

    /** Given the merge of the <code>out</code> sets, compute the <code>in</code> set for <code>s</code> (or in to out, depending on direction). */
    protected abstract void flowThrough(Object in, Object d, Object out);

    /** Accessor function returning value of OUT set for s. */
    public Object getFlowAfter(Object s)
    {
        return unitToAfterFlow.get(s);
    }
}
