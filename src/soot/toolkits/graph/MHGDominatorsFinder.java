/* Soot - a J*va Optimization Framework
 * Copyright (C) 2005 Navindra Umanee <navindra@cs.mcgill.ca>
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
 * Dominators finder for multi-headed graph.
 *
 * @author Navindra Umanee
 **/
public class MHGDominatorsFinder implements DominatorsFinder
{
    protected DirectedGraph graph;
    protected Map nodeToDominators;

    public MHGDominatorsFinder(DirectedGraph graph)
    {
        //if(Options.v().verbose())
        //G.v().out.println("[" + graph.getBody().getMethod().getName() +
        //"]     Finding Dominators...");

        this.graph = graph;
        MHGDominatorsAnalysis analysis = new MHGDominatorsAnalysis(graph);
        analysis.doAnalysis();
        nodeToDominators = analysis.nodeToFlowSet;
        /*
        for(Iterator i = nodeToDominators.keySet().iterator(); i.hasNext();){
            Object key = i.next();
            System.out.println(key + " is dominated by: ");
            for(Iterator j = ((FlowSet)nodeToDominators.get(key)).iterator(); j.hasNext();)
                System.out.println(j.next());
            System.out.println();
        }
        */
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
class MHGDominatorsAnalysis
{
    DirectedGraph graph;
    List heads;
    ArraySparseSet fullSet;
    Map nodeToFlowSet;
    
    public MHGDominatorsAnalysis(DirectedGraph graph)
    {
        this.graph = graph;
        heads = graph.getHeads();
        nodeToFlowSet = new HashMap();

        fullSet = new ArraySparseSet();
        for(Iterator i = graph.iterator(); i.hasNext();)
            fullSet.add(i.next());
        
        for(Iterator i = graph.iterator(); i.hasNext();){
            Object o = i.next();
            if(heads.contains(o)){
                ArraySparseSet self = new ArraySparseSet();
                self.add(o);
                nodeToFlowSet.put(o, self);
            }
            else{
                nodeToFlowSet.put(o, fullSet.clone());
            }
        }
    }

    public void doAnalysis()
    {
        boolean change = true;
        while(change){
            change = false;
            for(Iterator i = graph.iterator(); i.hasNext();){
                Object o = i.next();

                ArraySparseSet predsIntersect = new ArraySparseSet();
                if(heads.contains(o))
                    predsIntersect.add(o);
                else
                    predsIntersect.union(fullSet, predsIntersect);

                for(Iterator j = graph.getPredsOf(o).iterator(); j.hasNext();){
                    ArraySparseSet predSet = (ArraySparseSet) nodeToFlowSet.get(j.next());
                    predsIntersect.intersection(predSet, predsIntersect);
                }

                ArraySparseSet oldSet = (ArraySparseSet) nodeToFlowSet.get(o);
                ArraySparseSet newSet = new ArraySparseSet();
                newSet.add(o);
                newSet.union(predsIntersect, newSet);
                if(!newSet.equals(oldSet)){
                    nodeToFlowSet.put(o, newSet);
                    change = true;
                }
            }
        }
    }
}
