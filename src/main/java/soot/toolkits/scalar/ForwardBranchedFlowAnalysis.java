package soot.toolkits.scalar;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2000 Raja Vallee-Rai
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Timers;
import soot.Trap;
import soot.Unit;
import soot.UnitBox;
import soot.options.Options;
import soot.toolkits.graph.PseudoTopologicalOrderer;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.graph.interaction.FlowInfo;
import soot.toolkits.graph.interaction.InteractionHandler;
import soot.util.Chain;

/**
 * Abstract class providing an engine for branched forward flow analysis. WARNING: This does not handle exceptional flow as
 * branches!
 */
public abstract class ForwardBranchedFlowAnalysis<A> extends BranchedFlowAnalysis<Unit, A> {
  private static final Logger logger = LoggerFactory.getLogger(ForwardBranchedFlowAnalysis.class);

  public ForwardBranchedFlowAnalysis(UnitGraph graph) {
    super(graph);
  }

  @Override
  protected boolean isForward() {
    return true;
  }

  // Accumulate the previous afterFlow sets.
  private void accumulateAfterFlowSets(Unit s, A[] flowRepositories, List<A> previousAfterFlows) {
    int repCount = 0;

    previousAfterFlows.clear();
    if (s.fallsThrough()) {
      copy(unitToAfterFallFlow.get(s).get(0), flowRepositories[repCount]);
      previousAfterFlows.add(flowRepositories[repCount++]);
    }

    if (s.branches()) {
      for (A fs : getBranchFlowAfter(s)) {
        copy(fs, flowRepositories[repCount]);
        previousAfterFlows.add(flowRepositories[repCount++]);
      }
    }
  } // end accumulateAfterFlowSets

  @Override
  protected void doAnalysis() {
    TreeSet<Unit> changedUnits = new TreeSet<Unit>(new Comparator<Unit>() {
      // map each unit to a distinct integer for a total ordering
      final Map<Unit, Integer> numbers = new HashMap<Unit, Integer>();
      {
        int i = 1;
        for (Unit u : new PseudoTopologicalOrderer<Unit>().newList(graph, false)) {
          numbers.put(u, i);
          i++;
        }
      }

      @Override
      public int compare(Unit o1, Unit o2) {
        return numbers.get(o1) - numbers.get(o2);
      }
    });

    // initialize unitToIncomingFlowSets
    final int numNodes = graph.size();
    Map<Unit, ArrayList<A>> unitToIncomingFlowSets = new HashMap<Unit, ArrayList<A>>(numNodes * 2 + 1, 0.7f);
    for (Unit s : graph) {
      unitToIncomingFlowSets.put(s, new ArrayList<A>());
    }

    int numComputations = 0;
    int maxBranchSize = 0;

    // Set initial values and nodes to visit.
    // WARNING: DO NOT HANDLE THE CASE OF THE TRAPS
    {
      Chain<Unit> sl = ((UnitGraph) graph).getBody().getUnits();
      for (Unit s : graph) {
        changedUnits.add(s);

        unitToBeforeFlow.put(s, newInitialFlow());

        if (s.fallsThrough()) {
          List<A> fl = new ArrayList<A>();
          fl.add((newInitialFlow()));
          unitToAfterFallFlow.put(s, fl);

          Unit succ = sl.getSuccOf(s);
          // it's possible for someone to insert some (dead)
          // fall through code at the very end of a method body
          if (succ != null) {
            unitToIncomingFlowSets.get(succ).addAll(fl);
          }
        } else {
          unitToAfterFallFlow.put(s, new ArrayList<A>());
        }

        final List<UnitBox> unitBoxes = s.getUnitBoxes();
        List<A> l = new ArrayList<A>();
        if (s.branches()) {
          for (UnitBox ub : unitBoxes) {
            A f = newInitialFlow();
            l.add(f);
            unitToIncomingFlowSets.get(ub.getUnit()).add(f);
          }
        }
        unitToAfterBranchFlow.put(s, l);

        if (unitBoxes.size() > maxBranchSize) {
          maxBranchSize = unitBoxes.size();
        }
      }
    }

    // Feng Qian: March 07, 2002
    // init entry points
    final List<Unit> heads = graph.getHeads();
    for (Unit s : heads) {
      // this is a forward flow analysis
      unitToBeforeFlow.put(s, entryInitialFlow());
    }

    if (treatTrapHandlersAsEntries()) {
      for (Trap trap : ((UnitGraph) graph).getBody().getTraps()) {
        unitToBeforeFlow.put(trap.getHandlerUnit(), entryInitialFlow());
      }
    }

    // Perform fixed point flow analysis
    {
      @SuppressWarnings("unchecked")
      A[] flowRepositories = (A[]) new Object[maxBranchSize + 1];
      @SuppressWarnings("unchecked")
      A[] previousFlowRepositories = (A[]) new Object[maxBranchSize + 1];
      for (int i = 0; i < maxBranchSize + 1; i++) {
        flowRepositories[i] = newInitialFlow();
        previousFlowRepositories[i] = newInitialFlow();
      }

      List<A> previousAfterFlows = new ArrayList<A>();
      List<A> afterFlows = new ArrayList<A>();
      while (!changedUnits.isEmpty()) {
        Unit s = changedUnits.first();
        changedUnits.remove(s);

        accumulateAfterFlowSets(s, previousFlowRepositories, previousAfterFlows);

        // Compute and store beforeFlow
        A beforeFlow = getFlowBefore(s);
        {
          Iterator<A> preds = unitToIncomingFlowSets.get(s).iterator();
          if (preds.hasNext()) {
            // Handle the first pred
            copy(preds.next(), beforeFlow);
            // Handle remaining preds
            while (preds.hasNext()) {
              A otherBranchFlow = preds.next();
              A newBeforeFlow = newInitialFlow();
              merge(s, beforeFlow, otherBranchFlow, newBeforeFlow);
              copy(newBeforeFlow, beforeFlow);
            }

            if (heads.contains(s)) {
              mergeInto(s, beforeFlow, entryInitialFlow());
            }
          }
        }

        // Compute afterFlow and store it.
        {
          List<A> afterFallFlow = unitToAfterFallFlow.get(s);
          List<A> afterBranchFlow = getBranchFlowAfter(s);
          if (Options.v().interactive_mode()) {
            InteractionHandler ih = InteractionHandler.v();
            A savedFlow = newInitialFlow();
            copy(beforeFlow, savedFlow);
            FlowInfo<A, Unit> fi = new FlowInfo<A, Unit>(savedFlow, s, true);
            if (ih.getStopUnitList() != null && ih.getStopUnitList().contains(s)) {
              ih.handleStopAtNodeEvent(s);
            }
            ih.handleBeforeAnalysisEvent(fi);
          }
          flowThrough(beforeFlow, s, afterFallFlow, afterBranchFlow);
          if (Options.v().interactive_mode()) {
            List<A> l = new ArrayList<A>();
            if (!afterFallFlow.isEmpty()) {
              l.addAll(afterFallFlow);
            }
            if (!afterBranchFlow.isEmpty()) {
              l.addAll(afterBranchFlow);
            }

            /*
             * if (s instanceof soot.jimple.IfStmt){ l.addAll((List)afterFallFlow); l.addAll((List)afterBranchFlow); } else {
             * l.addAll((List)afterFallFlow); }
             */
            FlowInfo<List<A>, Unit> fi = new FlowInfo<List<A>, Unit>(l, s, false);
            InteractionHandler.v().handleAfterAnalysisEvent(fi);
          }
          numComputations++;
        }

        accumulateAfterFlowSets(s, flowRepositories, afterFlows);

        // Update queue appropriately
        if (!afterFlows.equals(previousAfterFlows)) {
          for (Unit succ : graph.getSuccsOf(s)) {
            changedUnits.add(succ);
          }
        }
      }
    }

    // logger.debug(""+graph.getBody().getMethod().getSignature() +
    // " numNodes: " + numNodes +
    // " numComputations: " + numComputations + " avg: " +
    // Main.truncatedOf((double) numComputations / numNodes, 2));

    Timers.v().totalFlowNodes += numNodes;
    Timers.v().totalFlowComputations += numComputations;

  } // end doAnalysis

} // end class ForwardBranchedFlowAnalysis
