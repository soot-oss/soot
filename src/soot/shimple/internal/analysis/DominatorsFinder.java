/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Navindra Umanee
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

package soot.shimple.internal.analysis;

/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 Navindra Umanee
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

import soot.*;
import soot.util.*;
import java.util.*;
import soot.jimple.*;
import soot.toolkits.graph.*;
import soot.toolkits.scalar.*;

/**
 * A wrapper class for our Dominators analysis.  We finally chose to
 * implement the easy flow analysis algorithm instead of a more
 * efficient iterative one.
 *
 * @author Navindra Umanee
 **/
public class DominatorsFinder
{
    Map blockToDominators;

    public DominatorsFinder(BlockGraph graph)
    {
        if(soot.Main.isVerbose)
            System.out.println("[" + graph.getBody().getMethod().getName() +
                               "]     Constructing Dominators...");

        DominatorsAnalysis analysis = new DominatorsAnalysis(graph);

        // build block to dominators map
        {
            blockToDominators = new HashMap(graph.size() * 2 + 1, 0.7f);

            Iterator blockIt = graph.getBlocks().iterator();

            while(blockIt.hasNext()){
                Block s = (Block) blockIt.next();

                FlowSet set = (FlowSet) analysis.getFlowAfter(s);
                blockToDominators.put(s, set);
            }
        }
    }

    public List getDominators(Block s)
    {
        return ((FlowSet) blockToDominators.get(s)).toList();
    }

    public boolean isDominatedBy(Block s, Block dominator)
    {
        return ((FlowSet) blockToDominators.get(s)).contains(dominator);
    }

    public boolean isDominatedByAll(Block s, Collection dominators)
    {
        return (((FlowSet) blockToDominators.get(s)).toList()).containsAll(dominators);
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
class DominatorsAnalysis extends ForwardFlowAnalysis
{
    FlowSet emptySet;
    Map blockToGenerateSet;
    
    DominatorsAnalysis(BlockGraph graph)
    {
        super(graph);

        // define empty set, with proper universe for complementation
        {
            List blocks = graph.getBlocks();
            FlowUniverse blockUniverse = new CollectionFlowUniverse(blocks);

            emptySet = new ArrayPackedSet(blockUniverse);
        }

        // pre-compute generate sets
        {
            blockToGenerateSet = new HashMap(graph.size() * 2 + 1, 0.7f);

            Iterator blockIt = graph.getBlocks().iterator();

            while(blockIt.hasNext()){
                Block s = (Block) blockIt.next();

                FlowSet genSet = (FlowSet) emptySet.clone();

                genSet.add(s, genSet);

                blockToGenerateSet.put(s, genSet);
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

        initSet.complement(initSet);
                           
        return initSet;
    }

    /**
     * OUT(Start) contains only Start at initialization time.
     **/
    protected void customizeInitialFlowGraph()
    {
        Iterator headsIt = graph.getHeads().iterator();

        while(headsIt.hasNext()){
            Object s = headsIt.next();
            BoundedFlowSet initSet = (BoundedFlowSet) emptySet.clone();
            initSet.add(s, initSet);
            unitToBeforeFlow.put(s, initSet);
        }
    }

    /**
     * We compute out straightforwardly.
     **/
    protected void flowThrough(Object inValue, Object block, Object outValue)
    {
        FlowSet in = (FlowSet) inValue, out = (FlowSet) outValue;

        // Perform generation
        in.union((FlowSet) blockToGenerateSet.get(block), out);
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
