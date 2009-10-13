/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-2000 Raja Vallee-Rai
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

import soot.toolkits.graph.interaction.*;
import soot.options.*;

/** Abstract class providing an engine for branched forward flow analysis.
 *  WARNING: This does not handle exceptional flow as branches! 
 * */
public abstract class ForwardBranchedFlowAnalysis<A> extends BranchedFlowAnalysis<Unit, A>
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
    private void accumulateAfterFlowSets(Unit s, A[] flowRepositories, List<Object> previousAfterFlows)
    {
        int repCount = 0;
        
        previousAfterFlows.clear();
        if (s.fallsThrough())
        {
            copy(unitToAfterFallFlow.get(s).get(0), flowRepositories[repCount]);
            previousAfterFlows.add(flowRepositories[repCount++]);
        }
        
        if (s.branches())
        {
            List<A> l = (unitToAfterBranchFlow.get(s));
            Iterator<A> it = l.iterator();

            while (it.hasNext())
            {
                A fs = (it.next());
                copy(fs, flowRepositories[repCount]);
                previousAfterFlows.add(flowRepositories[repCount++]);
            }
        }
    } // end accumulateAfterFlowSets


    protected void doAnalysis()
    {
        final Map<Unit, Integer> numbers = new HashMap<Unit, Integer>();
        List orderedUnits = new PseudoTopologicalOrderer().newList(graph,false);
        {
            int i = 1;
            for( Iterator uIt = orderedUnits.iterator(); uIt.hasNext(); ) {
                final Unit u = (Unit) uIt.next();
                numbers.put(u, new Integer(i));
                i++;
            }
        }

        TreeSet<Unit> changedUnits = new TreeSet<Unit>( new Comparator() {
            public int compare(Object o1, Object o2) {
                Integer i1 = numbers.get(o1);
                Integer i2 = numbers.get(o2);
                return (i1.intValue() - i2.intValue());
            }
        } );

        Map<Unit, ArrayList> unitToIncomingFlowSets = new HashMap<Unit, ArrayList>(graph.size() * 2 + 1, 0.7f);
        List heads = graph.getHeads();
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
            Chain sl = ((UnitGraph)graph).getBody().getUnits();
            Iterator it = graph.iterator();

            while(it.hasNext())
            {
                Unit s = (Unit) it.next();

                changedUnits.add(s);

                unitToBeforeFlow.put(s, newInitialFlow());

                if (s.fallsThrough())
                {
                    ArrayList<A> fl = new ArrayList<A>();

                    fl.add((newInitialFlow()));
                    unitToAfterFallFlow.put(s, fl);

		    Unit succ=(Unit) sl.getSuccOf(s);
		    // it's possible for someone to insert some (dead) 
		    // fall through code at the very end of a method body
		    if(succ!=null) {
			List<Object> l = (unitToIncomingFlowSets.get(sl.getSuccOf(s)));
			l.addAll(fl);
		    }
                }
                else
                    unitToAfterFallFlow.put(s, new ArrayList<A>());

                if (s.branches())
                {
                    ArrayList<A> l = new ArrayList<A>();
                    List<A> incList;
                    Iterator boxIt = s.getUnitBoxes().iterator();

                    while (boxIt.hasNext())
                    {
                        A f = (newInitialFlow());

                        l.add(f);
                        Unit ss = ((UnitBox) (boxIt.next())).getUnit();
                        incList = (unitToIncomingFlowSets.get(ss));
                                          
                        incList.add(f);
                    }
                    unitToAfterBranchFlow.put(s, l);
                }
                else
                    unitToAfterBranchFlow.put(s, new ArrayList<A>());

                if (s.getUnitBoxes().size() > maxBranchSize)
                    maxBranchSize = s.getUnitBoxes().size();
            }
        }

        // Feng Qian: March 07, 2002
        // init entry points
        {
            Iterator<Unit> it = heads.iterator();

            while (it.hasNext()) {
                Unit s = it.next();
                // this is a forward flow analysis
                unitToBeforeFlow.put(s, entryInitialFlow());
            }
        }

        if (treatTrapHandlersAsEntries())
        {
            Iterator trapIt = ((UnitGraph)graph).getBody().
                                   getTraps().iterator();
            while(trapIt.hasNext()) {
                Trap trap = (Trap) trapIt.next();
                Unit handler = trap.getHandlerUnit();
                unitToBeforeFlow.put(handler, entryInitialFlow());
            }
        }

        // Perform fixed point flow analysis
        {
            List<Object> previousAfterFlows = new ArrayList<Object>(); 
            List<Object> afterFlows = new ArrayList<Object>();
            A[] flowRepositories = (A[]) new Object[maxBranchSize+1];
            for (int i = 0; i < maxBranchSize+1; i++)
                flowRepositories[i] = newInitialFlow();
            A[] previousFlowRepositories = (A[])new Object[maxBranchSize+1];
            for (int i = 0; i < maxBranchSize+1; i++)
                previousFlowRepositories[i] = newInitialFlow();

            while(!changedUnits.isEmpty())
            {
                A beforeFlow;

                Unit s = changedUnits.first();
                changedUnits.remove(s);
                boolean isHead = heads.contains(s);

                accumulateAfterFlowSets(s, previousFlowRepositories, previousAfterFlows);

                // Compute and store beforeFlow
                {
                    List<A> preds = unitToIncomingFlowSets.get(s);

                    beforeFlow = unitToBeforeFlow.get(s);

                    if(preds.size() == 1)
                        copy(preds.get(0), beforeFlow);
                    else if(preds.size() != 0)
                    {
                        Iterator<A> predIt = preds.iterator();

                        copy(predIt.next(), beforeFlow);

                        while(predIt.hasNext())
                        {
                            A otherBranchFlow = predIt.next();
                            A newBeforeFlow = newInitialFlow();
                            merge(s, beforeFlow, otherBranchFlow, newBeforeFlow);
                            copy(newBeforeFlow, beforeFlow);
                        }
                    }

                    if(isHead && preds.size() != 0)
                        mergeInto(s, beforeFlow, entryInitialFlow());
                }

                // Compute afterFlow and store it.
                {
                    ArrayList<A> afterFallFlow = unitToAfterFallFlow.get(s);
                    ArrayList<A> afterBranchFlow = unitToAfterBranchFlow.get(s);
                    if (Options.v().interactive_mode()){
                        A savedFlow = newInitialFlow();
                        copy(beforeFlow, savedFlow);
                        FlowInfo fi = new FlowInfo(savedFlow, s, true);
                        if (InteractionHandler.v().getStopUnitList() != null && InteractionHandler.v().getStopUnitList().contains(s)){
                            InteractionHandler.v().handleStopAtNodeEvent(s);
                        }
                        InteractionHandler.v().handleBeforeAnalysisEvent(fi);
                    }
                    flowThrough(beforeFlow, s, (List) afterFallFlow, (List) afterBranchFlow);
                    if (Options.v().interactive_mode()){
                        ArrayList l = new ArrayList();
                        if (!((List)afterFallFlow).isEmpty()){
                            l.addAll((List)afterFallFlow);
                        }
                        if (!((List)afterBranchFlow).isEmpty()){
                            l.addAll((List)afterBranchFlow);
                        }
                        
                        /*if (s instanceof soot.jimple.IfStmt){
                            l.addAll((List)afterFallFlow);
                            l.addAll((List)afterBranchFlow);
                        }
                        else {
                            l.addAll((List)afterFallFlow);
                        }*/
                        FlowInfo fi = new FlowInfo(l, s, false);
                        InteractionHandler.v().handleAfterAnalysisEvent(fi);
                    }
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
                            
                        changedUnits.add(succ);
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
