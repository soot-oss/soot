package soot.jimple.toolkits.annotation.nullcheck;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2000 Janus
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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import soot.EquivalentValue;
import soot.Unit;
import soot.Value;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.scalar.FlowSet;

/**
 * @deprecated uses deprecated type {@link BranchedRefVarsAnalysis} and seems of no use for Soot so marked for future
 *             deletion, unless clients object
 */
@Deprecated
public class LocalRefVarsAnalysisWrapper {
  // compilation options
  private static final boolean computeChecks = true;
  private static final boolean discardKTop = true;

  private final BranchedRefVarsAnalysis analysis;
  private final Map<Unit, List<RefIntPair>> unitToVarsBefore;
  private final Map<Unit, List<RefIntPair>> unitToVarsAfterFall;
  private final Map<Unit, List<List<RefIntPair>>> unitToListsOfVarsAfterBranches;
  private final Map<Unit, List<Object>> unitToVarsNeedCheck;
  private final Map<Unit, List<RefIntPair>> unitToVarsDontNeedCheck;

  // constructor, where we do all our computations
  public LocalRefVarsAnalysisWrapper(ExceptionalUnitGraph graph) {
    this.analysis = new BranchedRefVarsAnalysis(graph);
    final int size = graph.size() * 2 + 1;
    this.unitToVarsBefore = new HashMap<Unit, List<RefIntPair>>(size, 0.7f);
    this.unitToVarsAfterFall = new HashMap<Unit, List<RefIntPair>>(size, 0.7f);
    this.unitToListsOfVarsAfterBranches = new HashMap<Unit, List<List<RefIntPair>>>(size, 0.7f);
    this.unitToVarsNeedCheck = new HashMap<Unit, List<Object>>(size, 0.7f);
    this.unitToVarsDontNeedCheck = new HashMap<Unit, List<RefIntPair>>(size, 0.7f);
    // end while

    for (Unit s : graph) {
      unitToVarsAfterFall.put(s, Collections.unmodifiableList(buildList(analysis.getFallFlowAfter(s))));

      // we get a list of flow sets for branches, iterate over them
      {
        List<FlowSet<RefIntPair>> branchesFlowsets = analysis.getBranchFlowAfter(s);
        List<List<RefIntPair>> lst = new ArrayList<List<RefIntPair>>(branchesFlowsets.size());
        for (FlowSet<RefIntPair> set : branchesFlowsets) {
          lst.add(Collections.unmodifiableList(buildList(set)));
        }
        unitToListsOfVarsAfterBranches.put(s, lst);
      } // done with branches

      final FlowSet<RefIntPair> set = analysis.getFlowBefore(s);
      unitToVarsBefore.put(s, Collections.unmodifiableList(buildList(set)));
      // NOTE: that set is used in the compute check bellow too

      if (computeChecks) {
        ArrayList<RefIntPair> dontNeedCheckVars = new ArrayList<RefIntPair>();
        ArrayList<Object> needCheckVars = new ArrayList<Object>();

        HashSet<Value> allChecksSet = new HashSet<Value>(5, 0.7f);
        allChecksSet.addAll(analysis.unitToArrayRefChecksSet.get(s));
        allChecksSet.addAll(analysis.unitToInstanceFieldRefChecksSet.get(s));
        allChecksSet.addAll(analysis.unitToInstanceInvokeExprChecksSet.get(s));
        allChecksSet.addAll(analysis.unitToLengthExprChecksSet.get(s));
        // set of all references that are subject to a null pointer check at this statement
        for (Value v : allChecksSet) {
          int vInfo = analysis.anyRefInfo(v, set);
          switch (vInfo) {
            case BranchedRefVarsAnalysis.kTop:
              // since it's a check, just print the name of the variable, we know it's top
              needCheckVars.add(v);
              break;
            case BranchedRefVarsAnalysis.kBottom:
              // this could happen in some rare cases; cf known limitations
              // in the BranchedRefVarsAnalysis implementation notes
              needCheckVars.add(analysis.getKRefIntPair(new EquivalentValue(v), vInfo));
              break;
            default:
              // no check, print the pair (ref, value), so we know why we are not doing the check
              dontNeedCheckVars.add(analysis.getKRefIntPair(new EquivalentValue(v), vInfo));
              break;
          }
        }

        unitToVarsNeedCheck.put(s, Collections.unmodifiableList(needCheckVars));
        unitToVarsDontNeedCheck.put(s, Collections.unmodifiableList(dontNeedCheckVars));
      } // end if computeChecks
    }

  } // end constructor & computations

  // utility method to build lists of (ref, value) pairs for a given flow set
  // optionally discard (ref, kTop) pairs.
  private List<RefIntPair> buildList(FlowSet<RefIntPair> set) {
    List<RefIntPair> lst = new ArrayList<RefIntPair>();
    for (EquivalentValue r : analysis.refTypeValues) {
      int refInfo = analysis.refInfo(r, set);
      if (!discardKTop || (refInfo != BranchedRefVarsAnalysis.kTop)) {
        lst.add(analysis.getKRefIntPair(r, refInfo));
        // remove tops from the list that will be printed for readability
      }
    }
    return lst;
  } // buildList

  /*
   *
   * Accesor methods.
   *
   * Public accessor methods to the various class fields containing the results of the computations.
   *
   */

  public List<RefIntPair> getVarsBefore(Unit s) {
    return unitToVarsBefore.get(s);
  } // end getVarsBefore

  public List<RefIntPair> getVarsAfterFall(Unit s) {
    return unitToVarsAfterFall.get(s);
  } // end getVarsAfterFall

  public List<List<RefIntPair>> getListsOfVarsAfterBranch(Unit s) {
    return unitToListsOfVarsAfterBranches.get(s);
  } // end getListsOfVarsAfterBranch

  public List<Object> getVarsNeedCheck(Unit s) {
    if (computeChecks) {
      return unitToVarsNeedCheck.get(s);
    } else {
      return new ArrayList<Object>();
    }
  } // end getVarsNeedCheck

  public List<RefIntPair> getVarsDontNeedCheck(Unit s) {
    if (computeChecks) {
      return unitToVarsDontNeedCheck.get(s);
    } else {
      return new ArrayList<RefIntPair>();
    }
  } // end getVarsNeedCheck

} // end class LocalRefVarsAnalysisWrapper
