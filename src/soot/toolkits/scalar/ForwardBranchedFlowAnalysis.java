/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-2000 Raja Vallee-Rai
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
 * Modified by the Sable Research Group and others 1997-2000.
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */


/*
    2000, March 20 - Updated code provided by Patrick Lam
                            <plam@sable.mcgill.ca>
                     from 1.beta.4.dev.60
                     to 1.beta.6.dev.34
                     Plus some bug fixes.
                     -- Janus <janus@place.org>


     KNOWN LIMITATION: the analysis doesn't handle traps since traps
               handler statements have predecessors, but they
               don't have the trap handler as successor.  This
               might be a limitation of the CompleteUnitGraph
               tho.
*/


package soot.toolkits.scalar;

import soot.*;
import soot.toolkits.graph.*;
import soot.util.*;
import java.util.*;

/** Abstract class providing an engine for branched forward flow analysis. */
public abstract class ForwardBranchedFlowAnalysis extends BranchedFlowAnalysis
{
    public ForwardBranchedFlowAnalysis(UnitGraph graph)
    {
        super(graph);
    }

    protected boolean isForward()
    {
        return true;
    }

    // Accumulate the previous afterFlow sets.
    private void accumulateAfterFlowSets(Unit s, Object[] flowRepositories, List previousAfterFlows)
    {
        int repCount = 0;
        
        previousAfterFlows.clear();
        if (s.fallsThrough())
        {
            copy(((List) unitToAfterFallFlow.get(s)).get(0), flowRepositories[repCount]);
            previousAfterFlows.add(flowRepositories[repCount++]);
        }
        
        if (s.branches())
        {
            List l = (List)(unitToAfterBranchFlow.get(s));
            Iterator it = l.iterator();

            while (it.hasNext())
            {
                Object fs = (Object) (it.next());
                copy(fs, flowRepositories[repCount]);
                previousAfterFlows.add(flowRepositories[repCount++]);
            }
        }
    } // end accumulateAfterFlowSets


    protected void doAnalysis()
    {
        LinkedList changedUnits = new LinkedList();
        HashSet changedUnitsSet = new HashSet();
        Map unitToIncomingFlowSets = new HashMap(graph.size() * 2 + 1, 0.7f);

        int numNodes = graph.size();
        int numComputations = 0;
        int maxBranchSize = 0;
        
        // initialize unitToIncomingFlowSets
        {
            Iterator it = graph.iterator();

            while (it.hasNext())
            {
                Unit s = (Unit) it.next();

                unitToIncomingFlowSets.put(s, new ArrayList());
            }
        }

        // Set initial values and nodes to visit.
        // WARNING: DO NOT HANDLE THE CASE OF THE TRAPS
        {
            Chain sl = graph.getBody().getUnits();
            Iterator it = graph.iterator();

            while(it.hasNext())
            {
                Unit s = (Unit) it.next();

                changedUnits.addLast(s);
                changedUnitsSet.add(s);

                unitToBeforeFlow.put(s, newInitialFlow());

                if (s.fallsThrough())
                {
                    ArrayList fl = new ArrayList();

                    fl.add((Object) (newInitialFlow()));
                    unitToAfterFallFlow.put(s, fl);

                    List l = (List) (unitToIncomingFlowSets.get(sl.getSuccOf(s)));
                    l.addAll(fl);
                }
                else
                    unitToAfterFallFlow.put(s, new ArrayList());

                if (s.branches())
                {
                    ArrayList l = new ArrayList();
                    List incList;
                    Iterator boxIt = s.getUnitBoxes().iterator();

                    while (boxIt.hasNext())
                    {
                        Object f = (Object) (newInitialFlow());

                        l.add(f);
                        Unit ss = ((UnitBox) (boxIt.next())).getUnit();
                        incList = (List) (unitToIncomingFlowSets.get(ss));
                                          
                        incList.add(f);
                    }
                    unitToAfterBranchFlow.put(s, l);
                }
                else
                    unitToAfterBranchFlow.put(s, new ArrayList());

                if (s.getUnitBoxes().size() > maxBranchSize)
                    maxBranchSize = s.getUnitBoxes().size();
            }
        }

        // Feng Qian: March 07, 2002
        // init entry points
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
            List previousAfterFlows = new ArrayList(); 
            List afterFlows = new ArrayList();
            Object[] flowRepositories = new Object[maxBranchSize+1];
            for (int i = 0; i < maxBranchSize+1; i++)
                flowRepositories[i] = (Object) newInitialFlow();
            Object[] previousFlowRepositories = new Object[maxBranchSize+1];
            for (int i = 0; i < maxBranchSize+1; i++)
                previousFlowRepositories[i] = (Object) newInitialFlow();

            while(!changedUnits.isEmpty())
            {
                Object beforeFlow;

                Unit s = (Unit) changedUnits.removeFirst();

                changedUnitsSet.remove(s);                

                accumulateAfterFlowSets(s, previousFlowRepositories, previousAfterFlows);

                // Compute and store beforeFlow
                {
                    List preds = (List) (unitToIncomingFlowSets.get(s));

                    beforeFlow = unitToBeforeFlow.get(s);

                    if(preds.size() == 1)
                        copy(preds.get(0), beforeFlow);
                    else if(preds.size() != 0)
                    {
                        Iterator predIt = preds.iterator();

                        copy(predIt.next(), beforeFlow);

                        while(predIt.hasNext())
                        {
                            Object otherBranchFlow = predIt.next();
                            merge(beforeFlow, otherBranchFlow, beforeFlow);
                        }
                    }
                }

                // Compute afterFlow and store it.
                {
                    Object afterFallFlow = unitToAfterFallFlow.get(s);
                    Object afterBranchFlow = unitToAfterBranchFlow.get(s);
                    flowThrough(beforeFlow, s, (List) afterFallFlow, (List) afterBranchFlow);
                    numComputations++;
                }

                accumulateAfterFlowSets(s, flowRepositories, afterFlows);

                // Update queue appropriately
                if(!afterFlows.equals(previousAfterFlows))
                {
                    Iterator succIt = graph.getSuccsOf(s).iterator();

                    while(succIt.hasNext())
                    {
                        Unit succ = (Unit) succIt.next();
                            
                        if(!changedUnitsSet.contains(succ))
                        {
                            changedUnits.addLast(succ);
                            changedUnitsSet.add(succ);
                        }
                    }
                }
            }
        }
        
        // G.v().out.println(graph.getBody().getMethod().getSignature() + " numNodes: " + numNodes + 
        //    " numComputations: " + numComputations + " avg: " + Main.truncatedOf((double) numComputations / numNodes, 2));
        
        Timers.v().totalFlowNodes += numNodes;
        Timers.v().totalFlowComputations += numComputations;

    } // end doAnalysis

} // end class ForwardBranchedFlowAnalysis
