/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
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


package soot.toolkits.scalar;

import soot.*;
import soot.toolkits.graph.*;
import soot.util.*;
import java.util.*;

/** An abstract class providing a framework for carrying out dataflow analysis.
 * Subclassing either BackwardFlowAnalysis or ForwardFlowAnalysis and providing
 * implementations for the abstract methods will allow Soot to compute the
 * corresponding flow analysis. */
public abstract class FlowAnalysis
{
    /** Maps graph nodes to IN sets. */
    protected Map unitToBeforeFlow;

    /** Maps graph nodes to OUT sets. */
    protected Map unitToAfterFlow;

    /** The graph being analysed. */
    protected DirectedGraph graph;

    /** Constructs a flow analysis on the given <code>DirectedGraph</code>. */
    public FlowAnalysis(DirectedGraph graph)
    {
        unitToAfterFlow = new HashMap(graph.size() * 2 + 1, 0.7f);
        unitToBeforeFlow = new HashMap(graph.size() * 2 + 1, 0.7f);

        this.graph = graph;
    }

    /** Returns the flow object corresponding to the initial values for each graph node. */
    protected abstract Object newInitialFlow();

    /** Returns true if this analysis is forwards. */
    protected abstract boolean isForward();

    /** Given the merge of the <code>out</code> sets, compute the <code>in</code> set for <code>s</code>. */
    protected abstract void flowThrough(Object in, Object d, Object out);

    /** Compute the merge of the <code>in1</code> and <code>in2</code> sets, putting the result into <code>out</code>. 
     * The behavior of this function depends on the implementation ( it may be necessary to check whether
     * <code>in1</code> and <code>in2</code> are equal or aliased ). 
     * Used by the doAnalysis method. */
    protected abstract void merge(Object in1, Object in2, Object out);

    /** Creates a copy of the <code>source</code> flow object in <code>dest</code>. */
    protected abstract void copy(Object source, Object dest);

    /** Carries out the actual flow analysis.  
     * Typically called from a concrete FlowAnalysis's constructor.*/
    protected abstract void doAnalysis();

    /** Customize the initial flow graph.  May be called from a concrete
     * FlowAnalysis constructor to adjust, for instance, the value for the initial node. */
    protected void customizeInitialFlowGraph()
    {
    }

    /** Accessor function returning value of OUT set for s. */
    public Object getFlowAfter(Object s)
    {
        return unitToAfterFlow.get(s);
    }

    /** Accessor function returning value of IN set for s. */
    public Object getFlowBefore(Object s)
    {
        return unitToBeforeFlow.get(s);
    }
}
