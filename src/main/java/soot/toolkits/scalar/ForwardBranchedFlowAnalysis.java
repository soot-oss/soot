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

  protected boolean isForward() {
    return true;
  }

  // Accumulate the previous afterFlow sets.
  private void accumulateAfterFlowSets(Unit s, A[] flowRepositories, List<Object> previousAfterFlows) {
    int repCount = 0;

    previousAfterFlows.clear();
    if (s.fallsThrough()) {
      copy(unitToAfterFallFlow.get(s).get(0), flowRepositories[repCount]);
      previousAfterFlows.add(flowRepositories[repCount++]);
    }

    if (s.branches()) {
      List<A> l = (getBranchFlowAfter(s));
      Iterator<A> it = l.iterator();

      while (it.hasNext()) {
        A fs = (it.next());
        copy(fs, flowRepositories[repCount]);
        previousAfterFlows.add(flowRepositories[repCount++]);
      }
    }
  } // end accumulateAfterFlowSets

  @Override
  protected void doAnalysis() {
    final Map<Unit, Integer> numbers = new HashMap<Unit, Integer>();
    List<Unit> orderedUnits = new PseudoTopologicalOrderer<Unit>().newList(graph, false);
    {
      int i = 1;
      for (Unit u : orderedUnits) {
        numbers.put(u, new Integer(i));
        i++;
      }
    }

    TreeSet<Unit> changedUnits = new TreeSet<Unit>(new Comparator<Unit>() {
      public int compare(Unit o1, Unit o2) {
        Integer i1 = numbers.get(o1);
        Integer i2 = numbers.get(o2);
        return (i1.intValue() - i2.intValue());
      }
    });

    Map<Unit, ArrayList<A>> unitToIncomingFlowSets = new HashMap<Unit, ArrayList<A>>(graph.size() * 2 + 1, 0.7f);
    List<Unit> heads = graph.getHeads();
    int numNodes = graph.size();
    int numComputations = 0;
    int maxBranchSize = 0;

    // initialize unitToIncomingFlowSets
    {
      for (Unit s : graph) {
        unitToIncomingFlowSets.put(s, new ArrayList<A>());
      }
    }

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
            List<A> l = (unitToIncomingFlowSets.get(sl.getSuccOf(s)));
            l.addAll(fl);
          }
        } else {
          unitToAfterFallFlow.put(s, new ArrayList<A>());
        }

        List<A> l = new ArrayList<A>();
        if (s.branches()) {
          List<A> incList;
          for (UnitBox ub : s.getUnitBoxes()) {
            A f = (newInitialFlow());

            l.add(f);
            Unit ss = ub.getUnit();
            incList = (unitToIncomingFlowSets.get(ss));

            incList.add(f);
          }

        }
        unitToAfterBranchFlow.put(s, l);

        if (s.getUnitBoxes().size() > maxBranchSize) {
          maxBranchSize = s.getUnitBoxes().size();
        }
      }
    }

    // Feng Qian: March 07, 2002
    // init entry points
    {
      for (Unit s : heads) {
        // this is a forward flow analysis
        unitToBeforeFlow.put(s, entryInitialFlow());
      }
    }

    if (treatTrapHandlersAsEntries()) {
      for (Trap trap : ((UnitGraph) graph).getBody().getTraps()) {
        Unit handler = trap.getHandlerUnit();
        unitToBeforeFlow.put(handler, entryInitialFlow());
      }
    }

    // Perform fixed point flow analysis
    {
      List<Object> previousAfterFlows = new ArrayList<Object>();
      List<Object> afterFlows = new ArrayList<Object>();
      A[] flowRepositories = (A[]) new Object[maxBranchSize + 1];
      for (int i = 0; i < maxBranchSize + 1; i++) {
        flowRepositories[i] = newInitialFlow();
      }
      A[] previousFlowRepositories = (A[]) new Object[maxBranchSize + 1];
      for (int i = 0; i < maxBranchSize + 1; i++) {
        previousFlowRepositories[i] = newInitialFlow();
      }

      while (!changedUnits.isEmpty()) {
        A beforeFlow;

        Unit s = changedUnits.first();
        changedUnits.remove(s);
        boolean isHead = heads.contains(s);

        accumulateAfterFlowSets(s, previousFlowRepositories, previousAfterFlows);

        // Compute and store beforeFlow
        {
          List<A> preds = unitToIncomingFlowSets.get(s);

          beforeFlow = getFlowBefore(s);

          if (preds.size() == 1) {
            copy(preds.get(0), beforeFlow);
          } else if (preds.size() != 0) {
            Iterator<A> predIt = preds.iterator();

            copy(predIt.next(), beforeFlow);

            while (predIt.hasNext()) {
              A otherBranchFlow = predIt.next();
              A newBeforeFlow = newInitialFlow();
              merge(s, beforeFlow, otherBranchFlow, newBeforeFlow);
              copy(newBeforeFlow, beforeFlow);
            }
          }

          if (isHead && preds.size() != 0) {
            mergeInto(s, beforeFlow, entryInitialFlow());
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
