/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Navindra Umanee <navindra@cs.mcgill.ca>
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

package soot.toolkits.graph;

import soot.*;
import soot.util.*;
import java.util.*;
import soot.jimple.*;
import soot.options.*;
import soot.toolkits.graph.*;
import soot.toolkits.scalar.*;

/**
 * Wrapper class for a simple dominators analysis based on a simple
 * flow analysis algorithm.  Works with any DirectedGraph with a
 * single head.
 *
 * @author Navindra Umanee
 **/
public class SimpleDominatorsFinder implements DominatorsFinder
{
    protected DirectedGraph graph;
    protected Map nodeToDominators;

    /**
     * Compute dominators for provided singled-headed directed graph.
     **/
    public SimpleDominatorsFinder(DirectedGraph graph)
    {
        //if(Options.v().verbose())
        //G.v().out.println("[" + graph.getBody().getMethod().getName() +
        //"]     Finding Dominators...");

        this.graph = graph;
        SimpleDominatorsAnalysis analysis = new SimpleDominatorsAnalysis(graph);

        // build node to dominators map
        {
            nodeToDominators = new HashMap(graph.size() * 2 + 1, 0.7f);
            
            for(Iterator nodeIt = graph.iterator(); nodeIt.hasNext();) {
                Object node = nodeIt.next();
                FlowSet set = (FlowSet) analysis.getFlowAfter(node);
                nodeToDominators.put(node, set);
            }
        }
    }

    public DirectedGraph getGraph()
    {
        return graph;
    }
    
    public List getDominators(Object node)
    {
        // non-backed list since FlowSet is an ArrayPackedFlowSet
        return ((FlowSet) nodeToDominators.get(node)).toList();
    }

    public Object getImmediateDominator(Object node)
    {
        // root node
        if(getGraph().getHeads().contains(node))
            return null;

	// could be memoised, I guess

        List dominatorsList = getDominators(node);
        dominatorsList.remove(node);

        Iterator dominatorsIt = dominatorsList.iterator();
        Object immediateDominator = null;

        while((immediateDominator == null) && dominatorsIt.hasNext()){
            Object dominator = dominatorsIt.next();

            if(isDominatedByAll(dominator, dominatorsList))
                immediateDominator = dominator;
        }

        if(immediateDominator == null)
            throw new RuntimeException("Assertion failed.");
        
        return immediateDominator;
    }

    public boolean isDominatedBy(Object node, Object dominator)
    {
        return getDominators(node).contains(dominator);
    }

    public boolean isDominatedByAll(Object node, Collection dominators)
    {
        return getDominators(node).containsAll(dominators);
    }
}

/**
 * Calculate dominators for basic blocks.
 * <p> Uses the algorithm contained in Dragon book, pg. 670-1.
 * <pre>
 *       D(n0) := { n0 }
 *       for n in N - { n0 } do D(n) := N;
 *       while changes to any D(n) occur do
 *         for n in N - {n0} do
 *             D(n) := {n} U (intersect of D(p) over all predecessors p of n)
 * </pre>
 **/
class SimpleDominatorsAnalysis extends ForwardFlowAnalysis
{
    FlowSet emptySet;
    Map nodeToGenerateSet;
    
    SimpleDominatorsAnalysis(DirectedGraph graph)
    {
        super(graph);

        // define empty set, with proper universe for complementation
        {
            List nodes = new ArrayList();

            for(Iterator nodesIt = graph.iterator(); nodesIt.hasNext();)
                nodes.add(nodesIt.next());
            
            FlowUniverse nodeUniverse = new CollectionFlowUniverse(nodes);
            emptySet = new ArrayPackedSet(nodeUniverse);
        }

        // pre-compute generate sets
        {
            nodeToGenerateSet = new HashMap(graph.size() * 2 + 1, 0.7f);

            for(Iterator nodeIt = graph.iterator(); nodeIt.hasNext();){
                Object s = nodeIt.next();
                FlowSet genSet = (FlowSet) emptySet.clone();
                genSet.add(s, genSet);
                nodeToGenerateSet.put(s, genSet);
            }
        }
        
        doAnalysis();
    }

    /**
     * All OUTs are initialized to the full set of definitions
     * OUT(Start) is tweaked in customizeInitialFlowGraph.
     **/
    protected Object newInitialFlow()
    {
        BoundedFlowSet initSet = (BoundedFlowSet) emptySet.clone();
        initSet.complement();
        return initSet;
    }

    /**
     * OUT(Start) contains only Start at initialization time.
     **/
    protected Object entryInitialFlow()
    {
        List heads = graph.getHeads();

        if(heads.size() != 1)
            throw new RuntimeException("Assertion failed:  Only one head expected.");

        BoundedFlowSet initSet = (BoundedFlowSet) emptySet.clone();
        initSet.add(heads.get(0));
        return initSet;
    }

    /**
     * We compute out straightforwardly.
     **/
    protected void flowThrough(Object inValue, Object block, Object outValue)
    {
        FlowSet in = (FlowSet) inValue, out = (FlowSet) outValue;

        // Perform generation
        in.union((FlowSet) nodeToGenerateSet.get(block), out);
    }

    /**
     * All paths == Intersection.
     **/
    protected void merge(Object in1, Object in2, Object out)
    {
        FlowSet inSet1 = (FlowSet) in1,
            inSet2 = (FlowSet) in2;

        FlowSet outSet = (FlowSet) out;

        inSet1.intersection(inSet2, outSet);
    }

    protected void copy(Object source, Object dest)
    {
        FlowSet sourceSet = (FlowSet) source,
            destSet = (FlowSet) dest;

        sourceSet.copy(destSet);
    }
}
