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

/**
 * Class to compute the DominanceFrontier using Cytron's celebrated efficient
 * algorithm.
 *
 * @author Navindra Umanee
 * @see <a
 * href="http://citeseer.nj.nec.com/cytron91efficiently.html">Efficiently
 * Computing Static Single Assignment Form and the Control Dependence
 * Graph</a>
 **/
public class CytronDominanceFrontier<N> implements DominanceFrontier<N>
{
    protected DominatorTree<N> dt;
    protected Map<DominatorNode<N>, List<DominatorNode<N>>> nodeToFrontier;
    
    public CytronDominanceFrontier(DominatorTree<N> dt)
    {
        this.dt = dt;
        nodeToFrontier = new HashMap<DominatorNode<N>, List<DominatorNode<N>>>();
        for (DominatorNode<N> head : dt.getHeads()) {
            bottomUpDispatch(head);
        }
        for(N gode : dt.graph) {
            DominatorNode<N> dode = dt.fetchDode(gode);
            if (dode == null) {
                throw new RuntimeException("dode == null");
            } else if (!isFrontierKnown(dode)) {
                System.out.print("'");
                System.out.print(dode);
                System.out.println("'");
                throw new RuntimeException("frontier did not have dode> ");
            }
        }
    }

    public List<DominatorNode<N>> getDominanceFrontierOf(DominatorNode<N> node)
    {
        List<DominatorNode<N>> frontier = nodeToFrontier.get(node);
        if(frontier == null)
            throw new RuntimeException("Frontier not defined for node: " + node);
        return new ArrayList<DominatorNode<N>>(frontier);
    }

    protected boolean isFrontierKnown(DominatorNode<N> node)
    {
        return nodeToFrontier.containsKey(node);
    }
    
    /**
     * Make sure we visit children first.  This is reverse topological
     * order.
     **/
    protected void bottomUpDispatch(DominatorNode<N> node)
    {
        // *** FIXME: It's annoying that this algorithm is so
        // *** inefficient in that in traverses the tree from the head
        // *** to the tail before it does anything.
        
        if(isFrontierKnown(node))
            return;

        for (DominatorNode<N> child : dt.getChildrenOf(node)) {
            if(!isFrontierKnown(child))
                bottomUpDispatch(child);
        }

        processNode(node);
    }
    
    /**
     * Calculate dominance frontier for a set of basic blocks.
     *
     * <p> Uses the algorithm of Cytron et al., TOPLAS Oct. 91:
     *
     * <pre>
     * for each X in a bottom-up traversal of the dominator tree do
     *
     *      DF(X) < - null
     *      for each Y in Succ(X) do
     *        if (idom(Y)!=X) then DF(X) <- DF(X) U Y
     *      end
     *      for each Z in {idom(z) = X} do
     *        for each Y in DF(Z) do
     *              if (idom(Y)!=X) then DF(X) <- DF(X) U Y
     *        end
     *      end
     * </pre>
     **/
    protected void processNode(DominatorNode<N> node)
    {
        List<DominatorNode<N>> dominanceFrontier = new ArrayList<DominatorNode<N>>();
        
        // local
        {
            Iterator<DominatorNode<N>> succsIt = dt.getSuccsOf(node).iterator();
            
            while(succsIt.hasNext()){
                DominatorNode<N> succ = succsIt.next();
                
                if(!dt.isImmediateDominatorOf(node, succ))
                    dominanceFrontier.add(succ);
            }
        }

        // up
        {
            for (DominatorNode<N> child : dt.getChildrenOf(node)) {
                for (DominatorNode<N> childFront : getDominanceFrontierOf(child)) {
                    if(!dt.isImmediateDominatorOf(node, childFront)){
                        dominanceFrontier.add(childFront);
                    }
                }
            }
        }
        
        nodeToFrontier.put(node, dominanceFrontier);
    }
}

