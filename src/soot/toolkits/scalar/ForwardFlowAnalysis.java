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

/**
 *   Abstract class that provides the fixed point iteration functionality
 *   required by all ForwardFlowAnalyses.
 *  
 */
public abstract class ForwardFlowAnalysis extends FlowAnalysis
{
    /** Construct the analysis from a DirectedGraph representation of a Body. */
    public ForwardFlowAnalysis(DirectedGraph graph)
    {
        super(graph);
    }

    protected boolean isForward()
    {
        return true;
    }

    protected void doAnalysis()
    {
        LinkedList changedUnits = new LinkedList();
        HashSet changedUnitsSet = new HashSet();

        int numNodes = graph.size();
        int numComputations = 0;
        
        // Set initial values and nodes to visit.
        {
            Iterator it = graph.iterator();
            while(it.hasNext())
            {
                Object s = it.next();

                unitToBeforeFlow.put(s, newInitialFlow());
                unitToAfterFlow.put(s, newInitialFlow());
            }

            it = graph.getHeads().iterator();
            while (it.hasNext())
            {
                Object s = it.next();

                changedUnits.addLast(s);
                changedUnitsSet.add(s);
            }
        }

	// Feng Qian: March 07, 2002
	// Set initial values for entry points
	{
	  Iterator it = graph.getHeads().iterator();
	  
	  while (it.hasNext()) {
	    Object s = it.next();
	    // this is a forward flow analysis
	    unitToBeforeFlow.put(s, entryInitialFlow());
	  }
	}

	// optional 
        customizeInitialFlowGraph();

        // Perform fixed point flow analysis
        {
            Object previousAfterFlow = newInitialFlow();

            while(!changedUnits.isEmpty())
            {
                Object beforeFlow;
                Object afterFlow;

                Object s = changedUnits.removeFirst();

                changedUnitsSet.remove(s);

                copy(unitToAfterFlow.get(s), previousAfterFlow);

                // Compute and store beforeFlow
                {
                    List preds = graph.getPredsOf(s);

                    beforeFlow = unitToBeforeFlow.get(s);

                    if(preds.size() == 1)
                        copy(unitToAfterFlow.get(preds.get(0)), beforeFlow);
                    else if(preds.size() != 0)
                    {
                        Iterator predIt = preds.iterator();

                        copy(unitToAfterFlow.get(predIt.next()), beforeFlow);

                        while(predIt.hasNext())
                        {
                            Object otherBranchFlow = unitToAfterFlow.get(predIt.
next());
                            merge(beforeFlow, otherBranchFlow, beforeFlow);
                        }
                    }
                }

                // Compute afterFlow and store it.
                {
                    afterFlow = unitToAfterFlow.get(s);
                    flowThrough(beforeFlow, s, afterFlow);
                    numComputations++;
                }

                // Update queue appropriately
                    if(!afterFlow.equals(previousAfterFlow))
                    {
                        Iterator succIt = graph.getSuccsOf(s).iterator();

                        while(succIt.hasNext())
                        {
                            Object succ = succIt.next();
                            
                            if(!changedUnitsSet.contains(succ))
                            {
                                changedUnits.addLast(succ);
                                changedUnitsSet.add(succ);
                            }
                        }
                    }
            }
        }
        
        // System.out.println(graph.getBody().getMethod().getSignature() + " numNodes: " + numNodes + 
        //    " numComputations: " + numComputations + " avg: " + Main.truncatedOf((double) numComputations / numNodes, 2));
        
        Main.totalFlowNodes += numNodes;
        Main.totalFlowComputations += numComputations;
    }

}


