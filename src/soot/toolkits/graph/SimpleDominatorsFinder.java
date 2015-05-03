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

import java.util.*;
import soot.toolkits.scalar.*;

/**
 * Wrapper class for a simple dominators analysis based on a simple
 * flow analysis algorithm.  Works with any DirectedGraph with a
 * single head.
 *
 * @author Navindra Umanee
 **/
public class SimpleDominatorsFinder<N> implements DominatorsFinder<N>
{
    protected DirectedGraph<N> graph;
    protected Map<N, FlowSet<N>> nodeToDominators;

    /**
     * Compute dominators for provided singled-headed directed graph.
     **/
    public SimpleDominatorsFinder(DirectedGraph<N> graph)
    {
        //if(Options.v().verbose())
        //G.v().out.println("[" + graph.getBody().getMethod().getName() +
        //"]     Finding Dominators...");

        this.graph = graph;
        SimpleDominatorsAnalysis<N> analysis = new SimpleDominatorsAnalysis<N>(graph);

        // build node to dominators map
        {
            nodeToDominators = new HashMap<N, FlowSet<N>>(graph.size() * 2 + 1, 0.7f);
            
            for(Iterator<N> nodeIt = graph.iterator(); nodeIt.hasNext();) {
                N node = nodeIt.next();
                FlowSet<N> set = analysis.getFlowAfter(node);
                nodeToDominators.put(node, set);
            }
        }
    }

    public DirectedGraph<N> getGraph()
    {
        return graph;
    }
    
    public List<N> getDominators(N node)
    {
        // non-backed list since FlowSet is an ArrayPackedFlowSet
        return nodeToDominators.get(node).toList();
    }

    public N getImmediateDominator(N node)
    {
        // root node
        if(getGraph().getHeads().contains(node))
            return null;

	// could be memoised, I guess

        List<N> dominatorsList = getDominators(node);
        dominatorsList.remove(node);

        Iterator<N> dominatorsIt = dominatorsList.iterator();
        N immediateDominator = null;

        while((immediateDominator == null) && dominatorsIt.hasNext()){
            N dominator = dominatorsIt.next();

            if(isDominatedByAll(dominator, dominatorsList))
                immediateDominator = dominator;
        }

        if(immediateDominator == null)
            throw new RuntimeException("Assertion failed.");
        
        return immediateDominator;
    }

    public boolean isDominatedBy(N node, N dominator)
    {
        return nodeToDominators.get(node).contains(dominator);
    }

    public boolean isDominatedByAll(N node, Collection<N> dominators)
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
class SimpleDominatorsAnalysis<N> extends ForwardFlowAnalysis<N, FlowSet<N>>
{
    FlowSet<N> emptySet;
    Map<N, FlowSet<N>> nodeToGenerateSet;
    
    SimpleDominatorsAnalysis(DirectedGraph<N> graph)
    {
        super(graph);

        // define empty set, with proper universe for complementation
        {
            List<N> nodes = new ArrayList();

            for(Iterator<N> nodesIt = graph.iterator(); nodesIt.hasNext();)
                nodes.add(nodesIt.next());
            
            FlowUniverse<N> nodeUniverse = new CollectionFlowUniverse<N>(nodes);
            emptySet = new ArrayPackedSet<N>(nodeUniverse);
        }

        // pre-compute generate sets
        {
            nodeToGenerateSet = new HashMap<N, FlowSet<N>>(graph.size() * 2 + 1, 0.7f);

            for(Iterator<N> nodeIt = graph.iterator(); nodeIt.hasNext();){
                N s = nodeIt.next();
                FlowSet<N> genSet = emptySet.clone();
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
    protected FlowSet<N> newInitialFlow()
    {
        BoundedFlowSet<N> initSet = (BoundedFlowSet<N>) emptySet.clone();
        initSet.complement();
        return initSet;
    }

    /**
     * OUT(Start) contains only Start at initialization time.
     **/
    protected FlowSet<N> entryInitialFlow()
    {
        List<N> heads = graph.getHeads();

        if(heads.size() != 1)
            throw new RuntimeException("Assertion failed:  Only one head expected.");

        BoundedFlowSet<N> initSet = (BoundedFlowSet<N>) emptySet.clone();
        initSet.add(heads.get(0));
        return initSet;
    }

    /**
     * We compute out straightforwardly.
     **/
    protected void flowThrough(FlowSet<N> in, N block, FlowSet<N> out)
    {
        // Perform generation
        in.union(nodeToGenerateSet.get(block), out);
    }

    /**
     * All paths == Intersection.
     **/
    protected void merge(FlowSet<N> in1, FlowSet<N> in2, FlowSet<N> out)
    {
        in1.intersection(in2, out);
    }

    protected void copy(FlowSet<N> source, FlowSet<N> dest)
    {
        source.copy(dest);
    }
}
