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

import soot.*;
import soot.toolkits.scalar.*;
import soot.toolkits.graph.*;
import soot.jimple.*;
import java.util.*;
import soot.util.*;

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
public class DominanceFrontier
{
    public DominanceFrontier()
    {
        super();
    }

    public DominanceFrontier(List dominatorNodeHeads)
    {
        Iterator headsIt = dominatorNodeHeads.iterator();
        while(headsIt.hasNext())
            bottomUpDispatch((DominatorNode)headsIt.next());
    }

    // *** TODO: We should do something more efficient like
    // *** Topological Sorting.  Soot actually has a implementation
    // *** available.
    
    /**
     * Make sure we visit children first.
     **/
    public void bottomUpDispatch(DominatorNode node)
    {
        if(node.isFrontierKnown())
            return;

        Iterator children = node.getChildren().iterator();

        while(children.hasNext()){
            DominatorNode child = (DominatorNode) children.next();

            if(!child.isFrontierKnown())
                bottomUpDispatch(child);
            else
                processNode(child);
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
    public void processNode(DominatorNode node)
    {
        // local
        {
            Iterator succsIt = node.getSuccs().iterator();

            while(succsIt.hasNext()){
                DominatorNode succ = (DominatorNode) succsIt.next();

                if(!succ.isImmediateDominator(node)){
                    node.addToDominanceFrontier(succ);
                }
            }
        }

        // up
        {
            Iterator childIt = node.getChildren().iterator();
            while(childIt.hasNext()){
                DominatorNode child = (DominatorNode) childIt.next();

                Iterator childFrontIt = child.getDominanceFrontier().iterator();
                while(childFrontIt.hasNext()){
                    DominatorNode childFront = (DominatorNode) childFrontIt.next();

                    if(!childFront.isImmediateDominator(node)){
                        node.addToDominanceFrontier(childFront);
                    }
                }
            }
        }

        node.setFrontierKnown();
    }
}

