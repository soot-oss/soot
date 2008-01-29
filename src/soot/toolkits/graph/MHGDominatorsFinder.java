/* Soot - a J*va Optimization Framework
 * Copyright (C) 2005 Navindra Umanee <navindra@cs.mcgill.ca>
 * Copyright (C) 2007 Eric Bodden
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

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
 * 2007/07/03 - updated to use {@link BitSet}s instead of {@link HashSet}s, as the most expensive
 * operation in this algorithm used to be cloning of the fullSet, which is very cheap
 * for {@link BitSet}s.
 *
 * @author Navindra Umanee
 * @author Eric Bodden
 **/
public class MHGDominatorsFinder implements DominatorsFinder
{
    protected DirectedGraph graph;
    protected BitSet fullSet;
    protected List heads;
    protected Map<Object,BitSet> nodeToFlowSet;
    protected Map<Object,Integer> nodeToIndex;
    protected Map<Integer,Object> indexToNode;
    protected int lastIndex = 0;

    public MHGDominatorsFinder(DirectedGraph graph)
    {
        this.graph = graph;
        doAnalysis();
    }

    protected void doAnalysis()
    {
        heads = graph.getHeads();
        nodeToFlowSet = new HashMap<Object, BitSet>();
        nodeToIndex = new HashMap<Object, Integer>();
        indexToNode = new HashMap<Integer, Object>();
    
        //build full set
        fullSet = new BitSet(graph.size());
        fullSet.flip(0, graph.size()-1);//set all to true
        
        //set up domain for intersection: head nodes are only dominated by themselves,
        //other nodes are dominated by everything else
        for(Iterator i = graph.iterator(); i.hasNext();){
            Object o = i.next();
            if(heads.contains(o)){
                BitSet self = new BitSet();
                self.set(indexOf(o));
                nodeToFlowSet.put(o, self);
            }
            else{
                nodeToFlowSet.put(o, fullSet);
            }
        }
    
        boolean changed = true;
        do{
            changed = false;
            for(Iterator i = graph.iterator(); i.hasNext();){
                Object o = i.next();
    
                //set up domain for intersection: head nodes are only dominated by themselves,
                //other nodes are dominated by everything else
                BitSet predsIntersect;
                if(heads.contains(o)) {
                    predsIntersect = new BitSet();
                    predsIntersect.set(indexOf(o));
                }
                else
                    //this clone() is fast on BitSets (opposed to on HashSets)
                    predsIntersect = (BitSet) fullSet.clone();
    
                //intersect over all predecessors
                for(Iterator j = graph.getPredsOf(o).iterator(); j.hasNext();){
                    BitSet predSet = nodeToFlowSet.get(j.next());
                    predsIntersect.and(predSet);
                }
    
                BitSet oldSet = nodeToFlowSet.get(o);
                //each node dominates itself
                predsIntersect.set(indexOf(o));
                if(!predsIntersect.equals(oldSet)){
                    nodeToFlowSet.put(o, predsIntersect);
                    changed = true;
                }
            }
        } while(changed);
    }
    
    protected int indexOf(Object o) {
        Integer index = nodeToIndex.get(o);
        if(index==null) {
            index = lastIndex;
            nodeToIndex.put(o,index);
            indexToNode.put(index,o);
            lastIndex++;
        }
        return index;
    }
    
    public DirectedGraph getGraph()
    {
        return graph;
    }
    
    public List getDominators(Object node)
    {
        //reconstruct list of dominators from bitset
        List result = new ArrayList();
        BitSet bitSet = nodeToFlowSet.get(node);
        for(int i=0;i<bitSet.length();i++) {
            if(bitSet.get(i)) {
                result.add(indexToNode.get(i));
            }
        }
        return result;
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

        assert immediateDominator!=null;
        
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
