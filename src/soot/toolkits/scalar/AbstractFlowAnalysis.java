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
import soot.options.*;
import soot.toolkits.graph.interaction.*;

/** An abstract class providing a metaframework for carrying out
 * dataflow analysis.  This class provides common methods and fields
 * required by the BranchedFlowAnalysis and FlowAnalysis abstract classes.
 */
public abstract class AbstractFlowAnalysis
{
    /** Maps graph nodes to IN sets. */
    protected Map unitToBeforeFlow;

    /** Filtered: Maps graph nodes to IN sets. */
    protected Map filterUnitToBeforeFlow;

    /** The graph being analysed. */
    protected DirectedGraph graph;

    /** Constructs a flow analysis on the given <code>DirectedGraph</code>. */
    public AbstractFlowAnalysis(DirectedGraph graph)
    {
        unitToBeforeFlow = new HashMap(graph.size() * 2 + 1, 0.7f);
        this.graph = graph;
        if (Options.v().interactive_mode()){
            InteractionHandler.v().handleCfgEvent(graph);
        }
    }

    /** 
     * Returns the flow object corresponding to the initial values for
     * each graph node. 
     */
    protected abstract Object newInitialFlow();

    /**
     * Returns the initial flow value for entry/exit graph nodes.
     */
    protected abstract Object entryInitialFlow();

    /**
     * We hereby retract the API for customizeInitialFlowGraph().
     */
    protected final void customizeInitialFlowGraph() {}

    /**
     * Determines whether <code>entryInitialFlow()</code>
     * is applied to trap handlers.
     */
    protected boolean treatTrapHandlersAsEntries() { return false; }

    /** Returns true if this analysis is forwards. */
    protected abstract boolean isForward();

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

    /** Accessor function returning value of IN set for s. */
    public Object getFlowBefore(Object s)
    {
        return unitToBeforeFlow.get(s);
    }

    protected void merge(Object inout, Object in) {
        Object tmp = newInitialFlow();
        merge(inout, in, tmp);
        copy(tmp, inout);
    }
}
