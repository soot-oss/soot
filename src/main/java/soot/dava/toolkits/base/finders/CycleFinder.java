package soot.dava.toolkits.base.finders;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Jerome Miecznikowski
 * Copyright (C) 2006 Nomair A. Naeem
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.G;
import soot.Local;
import soot.Singletons;
import soot.Unit;
import soot.dava.Dava;
import soot.dava.DavaBody;
import soot.dava.RetriggerAnalysisException;
import soot.dava.internal.SET.SETCycleNode;
import soot.dava.internal.SET.SETDoWhileNode;
import soot.dava.internal.SET.SETNode;
import soot.dava.internal.SET.SETUnconditionalWhileNode;
import soot.dava.internal.SET.SETWhileNode;
import soot.dava.internal.asg.AugmentedStmt;
import soot.dava.internal.asg.AugmentedStmtGraph;
import soot.dava.internal.javaRep.DIntConstant;
import soot.dava.toolkits.base.misc.ConditionFlipper;
import soot.grimp.internal.GAssignStmt;
import soot.grimp.internal.GTableSwitchStmt;
import soot.jimple.AssignStmt;
import soot.jimple.ConditionExpr;
import soot.jimple.GotoStmt;
import soot.jimple.IfStmt;
import soot.jimple.LookupSwitchStmt;
import soot.jimple.Stmt;
import soot.jimple.TableSwitchStmt;
import soot.jimple.internal.JGotoStmt;
import soot.toolkits.graph.StronglyConnectedComponentsFast;
import soot.util.IterableSet;
import soot.util.StationaryArrayList;

public class CycleFinder implements FactFinder {
  private static final Logger logger = LoggerFactory.getLogger(CycleFinder.class);

  public CycleFinder(Singletons.Global g) {
  }

  public static CycleFinder v() {
    return G.v().soot_dava_toolkits_base_finders_CycleFinder();
  }

  public void find(DavaBody body, AugmentedStmtGraph asg, SETNode SET) throws RetriggerAnalysisException {
    Dava.v().log("CycleFinder::find()");

    AugmentedStmtGraph wasg = (AugmentedStmtGraph) asg.clone();
    List<List<AugmentedStmt>> component_list = build_component_list(wasg);

    // loop through all nestings
    while (component_list.isEmpty() == false) {

      IterableSet<AugmentedStmt> node_list = new IterableSet<AugmentedStmt>();

      // loop through all the strongly connected components
      for (List<AugmentedStmt> cal : component_list) {
        node_list.clear();
        node_list.addAll(cal);
        // node_list contains all the nodes belonging to this SCC

        IterableSet<AugmentedStmt> entry_points = get_EntryPoint(node_list);

        // if more than one entry points found
        if (entry_points.size() > 1) {

          LinkedList<AugmentedStmt> asgEntryPoints = new LinkedList<AugmentedStmt>();
          for (AugmentedStmt au : entry_points) {
            asgEntryPoints.addLast(asg.get_AugStmt(au.get_Stmt()));
          }

          IterableSet<AugmentedStmt> asgScc = new IterableSet<AugmentedStmt>();
          for (AugmentedStmt au : node_list) {
            asgScc.addLast(asg.get_AugStmt(au.get_Stmt()));
          }

          fix_MultiEntryPoint(body, asg, asgEntryPoints, asgScc);
          throw new RetriggerAnalysisException();
        }

        // gets to this code only if each SCC has one entry point?
        AugmentedStmt entry_point = (AugmentedStmt) entry_points.getFirst();
        AugmentedStmt characterizing_stmt = find_CharacterizingStmt(entry_point, node_list, wasg), succ_stmt = null;

        if (characterizing_stmt != null) {
          for (AugmentedStmt au : characterizing_stmt.bsuccs) {
            succ_stmt = au;
            if (node_list.contains(succ_stmt) == false) {
              break;
            }
          }
        }

        wasg.calculate_Reachability(succ_stmt, new HashSet<AugmentedStmt>(), entry_point);
        IterableSet<AugmentedStmt> cycle_body = get_CycleBody(entry_point, succ_stmt, asg, wasg);

        SETCycleNode newNode = null;

        if (characterizing_stmt != null) {
          checkExceptionLoop: for (ExceptionNode en : body.get_ExceptionFacts()) {
            IterableSet<AugmentedStmt> tryBody = en.get_TryBody();

            if (tryBody.contains(asg.get_AugStmt(characterizing_stmt.get_Stmt()))) {
              for (AugmentedStmt cbas : cycle_body) {
                if (tryBody.contains(cbas) == false) {
                  characterizing_stmt = null;
                  break checkExceptionLoop;
                }
              }
            }
          }
        }

        /*
         * if (tryBody.contains( asg.get_AugStmt( characterizing_stmt.get_Stmt()))) {
         *
         * if (checkExceptionNodes.contains( en) == false) checkExceptionNodes.add( en);
         *
         * Iterator cbit = cycle_body.snapshotIterator(); while (cbit.hasNext()) { AugmentedStmt cbas = (AugmentedStmt)
         * cbit.next();
         *
         * if (tryBody.contains( cbas) == false) cycle_body.remove( cbas); } } }
         *
         * enlit = checkExceptionNodes.iterator(); exceptionNestingLoop: while (enlit.hasNext()) { ExceptionNode en =
         * (ExceptionNode) enlit.next();
         *
         * Iterator cbit = cycle_body.iterator(); while (cbit.hasNext()) { AugmentedStmt cbas = (AugmentedStmt) cbit.next();
         *
         * if (en.get_TryBody().contains( cbas) == false) { characterizing_stmt = null; break exceptionNestingLoop; } } } }
         */

        // unconditional loop
        if (characterizing_stmt == null) {
          wasg.remove_AugmentedStmt(entry_point);
          newNode = new SETUnconditionalWhileNode(cycle_body);
        }

        else {
          body.consume_Condition(asg.get_AugStmt(characterizing_stmt.get_Stmt()));
          wasg.remove_AugmentedStmt(characterizing_stmt);

          IfStmt condition = (IfStmt) characterizing_stmt.get_Stmt();
          if (cycle_body.contains(asg.get_AugStmt(condition.getTarget())) == false) {
            condition.setCondition(ConditionFlipper.flip((ConditionExpr) condition.getCondition()));
          }

          if (characterizing_stmt == entry_point) {
            newNode = new SETWhileNode(asg.get_AugStmt(characterizing_stmt.get_Stmt()), cycle_body);
          } else {
            newNode = new SETDoWhileNode(asg.get_AugStmt(characterizing_stmt.get_Stmt()),
                asg.get_AugStmt(entry_point.get_Stmt()), cycle_body);
          }
        }

        if (newNode != null) {
          SET.nest(newNode);
        }
      }

      component_list = build_component_list(wasg);
    }
  }

  /*
   * Nomair A. Naeem Entry point to a SCC ARE those stmts whose predecessor does not belong to the SCC
   */
  private IterableSet<AugmentedStmt> get_EntryPoint(IterableSet<AugmentedStmt> nodeList) {
    IterableSet<AugmentedStmt> entryPoints = new IterableSet<AugmentedStmt>();

    for (AugmentedStmt as : nodeList) {
      for (AugmentedStmt po : as.cpreds) {
        if (nodeList.contains(po) == false) {
          entryPoints.add(as);
          break;
        }
      }
    }

    return entryPoints;
  }

  private List<List<AugmentedStmt>> build_component_list(AugmentedStmtGraph asg) {
    List<List<AugmentedStmt>> c_list = new LinkedList<List<AugmentedStmt>>();

    StronglyConnectedComponentsFast<AugmentedStmt> scc = new StronglyConnectedComponentsFast<AugmentedStmt>(asg);

    // makes sure that all scc's with only one statement in them are removed
    /*
     * 26th Jan 2006 Nomair A. Naeem This could be potentially bad since self loops will also get removed Adding code to
     * check for self loop (a stmt is a self loop if its pred and succ contain the stmt itself
     */
    for (List<AugmentedStmt> wcomp : scc.getComponents()) {
      if (wcomp.size() > 1) {
        c_list.add(wcomp);
      } else if (wcomp.size() == 1) {
        // this is a scc of one augmented stmt
        // We should add those which are self loops
        AugmentedStmt as = wcomp.get(0);

        if (as.cpreds.contains(as) && (as.csuccs.contains(as))) {
          // "as" has a predecssor and successor which is as i.e. it
          // is a self loop

          List<AugmentedStmt> currentComponent = null;
          currentComponent = new StationaryArrayList<AugmentedStmt>();
          currentComponent.add(as);
          // System.out.println("Special add of"+as);
          c_list.add(currentComponent);
        }
      }
    }
    return c_list;
  }

  private AugmentedStmt find_CharacterizingStmt(AugmentedStmt entry_point, IterableSet<AugmentedStmt> sc_component,
      AugmentedStmtGraph asg) {
    /*
     * Check whether we are a while loop.
     */

    if (entry_point.get_Stmt() instanceof IfStmt) {

      // see if there's a successor who's not in the strict loop set
      for (AugmentedStmt au : entry_point.bsuccs) {
        if (sc_component.contains(au) == false) {
          return entry_point;
        }
      }
    }

    /*
     * We're not a while loop. Get the candidates for condition on a do-while loop.
     */

    IterableSet<AugmentedStmt> candidates = new IterableSet<AugmentedStmt>();
    HashMap<AugmentedStmt, AugmentedStmt> candSuccMap = new HashMap<AugmentedStmt, AugmentedStmt>();
    HashSet<AugmentedStmt> blockers = new HashSet<AugmentedStmt>();

    // Get the set of all candidates.
    for (AugmentedStmt pas : entry_point.bpreds) {
      if ((pas.get_Stmt() instanceof GotoStmt) && (pas.bpreds.size() == 1)) {
        pas = (AugmentedStmt) pas.bpreds.get(0);
      }

      if ((sc_component.contains(pas)) && (pas.get_Stmt() instanceof IfStmt)) {

        for (AugmentedStmt spas : pas.bsuccs) {
          if (sc_component.contains(spas) == false) {

            candidates.add(pas);
            candSuccMap.put(pas, spas);
            blockers.add(spas);

            break;
          }
        }
      }
    }

    /*
     * If there was no candidate, we are an unconditional loop.
     */

    if (candidates.isEmpty()) {
      return null;
    }

    /*
     * Get the best candidate for the do-while condition.
     */

    if (candidates.size() == 1) {
      return (AugmentedStmt) candidates.getFirst();
    }

    // Take the candidate(s) whose successor has maximal reachability from
    // all candidates.

    asg.calculate_Reachability(candidates, blockers, entry_point);

    IterableSet<AugmentedStmt> max_Reach_Set = null;
    int reachSize = 0;

    for (AugmentedStmt as : candidates) {
      int current_reach_size = ((AugmentedStmt) candSuccMap.get(as)).get_Reachers().intersection(candidates).size();

      if (current_reach_size > reachSize) {
        max_Reach_Set = new IterableSet<AugmentedStmt>();
        reachSize = current_reach_size;
      }

      if (current_reach_size == reachSize) {
        max_Reach_Set.add(as);
      }
    }

    candidates = max_Reach_Set;

    if (candidates == null) {
      throw new RuntimeException("Did not find a suitable candidate");
    }
    if (candidates.size() == 1) {
      return (AugmentedStmt) candidates.getFirst();
    }

    // Find a single source shortest path from the entry point to any of the
    // remaining candidates.

    HashSet<Object> touchSet = new HashSet<Object>();
    LinkedList<AugmentedStmt> worklist = new LinkedList<AugmentedStmt>();
    worklist.addLast(entry_point);
    touchSet.add(entry_point);

    while (worklist.isEmpty() == false) {

      for (AugmentedStmt so : worklist.removeFirst().csuccs) {
        if (candidates.contains(so)) {
          return so;
        }

        if ((sc_component.contains(so)) && (touchSet.contains(so) == false)) {
          worklist.addLast(so);
          touchSet.add(so);
        }
      }
    }

    throw new RuntimeException("Somehow didn't find a condition for a do-while loop!");
  }

  private IterableSet<AugmentedStmt> get_CycleBody(AugmentedStmt entry_point, AugmentedStmt boundary_stmt,
      AugmentedStmtGraph asg, AugmentedStmtGraph wasg) {
    IterableSet<AugmentedStmt> cycle_body = new IterableSet<AugmentedStmt>();
    LinkedList<AugmentedStmt> worklist = new LinkedList<AugmentedStmt>();
    AugmentedStmt asg_ep = asg.get_AugStmt(entry_point.get_Stmt());

    worklist.add(entry_point);
    cycle_body.add(asg_ep);

    while (worklist.isEmpty() == false) {
      AugmentedStmt as = worklist.removeFirst();

      for (AugmentedStmt wsas : as.csuccs) {
        AugmentedStmt sas = asg.get_AugStmt(wsas.get_Stmt());

        if (cycle_body.contains(sas)) {
          continue;
        }

        /*
         * if (sas.get_Dominators().contains( asg_ep) == false) { logger.debug(""+ wsas + " not dominated by " + asg_ep);
         * logger.debug(""+ "doms"); Iterator dit = sas.get_Dominators().iterator(); while (dit.hasNext()) logger.debug(""+
         * "    " + dit.next()); logger.debug("preds"); dit = sas.cpreds.iterator(); while (dit.hasNext()) logger.debug(""+
         * "    " + dit.next()); }
         */

        if ((cycle_body.contains(sas) == false) && (sas.get_Dominators().contains(asg_ep))) {

          if ((boundary_stmt != null) && ((wsas.get_Reachers().contains(boundary_stmt)) || (wsas == boundary_stmt))) {
            continue;
          }

          // logger.debug(""+ sas);

          worklist.add(wsas);
          cycle_body.add(sas);
        }
      }
    }

    return cycle_body;
  }

  private void fix_MultiEntryPoint(DavaBody body, AugmentedStmtGraph asg, LinkedList<AugmentedStmt> entry_points,
      IterableSet<AugmentedStmt> scc) {
    AugmentedStmt naturalEntryPoint = get_NaturalEntryPoint(entry_points, scc);
    Local controlLocal = body.get_ControlLocal();

    Unit defaultTarget = naturalEntryPoint.get_Stmt();
    LinkedList<AugmentedStmt> targets = new LinkedList<AugmentedStmt>();

    /*
     * Nomair A Naeem, Micheal Batchelder 5 th April 2005 shouldnt send empty targets list to constructor of GTableSwitch
     * since then it just creates an empty array to hold the targets.. we intend to fill these in later using the setTarget
     * method
     *
     * hence the hack is to just send in null fully aware that they are going to be changed to the target we want within the
     * following while loop
     */
    for (int i = 0; i < entry_points.size(); i++) {
      targets.add(null);
      /* 5th April End code change */
    }

    TableSwitchStmt tss = new GTableSwitchStmt(controlLocal, 0, entry_points.size() - 2, targets, defaultTarget);
    AugmentedStmt dispatchStmt = new AugmentedStmt(tss);

    IterableSet<AugmentedStmt> predecessorSet = new IterableSet<AugmentedStmt>(),
        indirectionStmtSet = new IterableSet<AugmentedStmt>(), directionStmtSet = new IterableSet<AugmentedStmt>();

    int count = 0;
    Iterator<AugmentedStmt> epit = entry_points.iterator();
    while (epit.hasNext()) {
      AugmentedStmt entryPoint = epit.next();

      GotoStmt gotoStmt = new JGotoStmt(entryPoint.get_Stmt());
      AugmentedStmt indirectionStmt = new AugmentedStmt(gotoStmt);

      indirectionStmtSet.add(indirectionStmt);

      tss.setTarget(count++, gotoStmt);

      dispatchStmt.add_BSucc(indirectionStmt);
      indirectionStmt.add_BPred(dispatchStmt);
      indirectionStmt.add_BSucc(entryPoint);
      entryPoint.add_BPred(indirectionStmt);

      asg.add_AugmentedStmt(indirectionStmt);

      LinkedList<AugmentedStmt> toRemove = new LinkedList<AugmentedStmt>();

      for (AugmentedStmt pas : entryPoint.cpreds) {
        if ((pas == indirectionStmt) || ((entryPoint != naturalEntryPoint) && (scc.contains(pas)))) {
          continue;
        }

        if (scc.contains(pas) == false) {
          predecessorSet.add(pas);
        }

        AssignStmt asnStmt = new GAssignStmt(controlLocal, DIntConstant.v(count, null));
        AugmentedStmt directionStmt = new AugmentedStmt(asnStmt);

        directionStmtSet.add(directionStmt);

        patch_Stmt(pas.get_Stmt(), entryPoint.get_Stmt(), asnStmt);

        // Mark the original predecessor to be removed.
        toRemove.addLast(pas);

        pas.csuccs.remove(entryPoint);
        pas.csuccs.add(directionStmt);
        if (pas.bsuccs.contains(entryPoint)) {
          pas.bsuccs.remove(entryPoint);
          pas.bsuccs.add(directionStmt);
        }

        directionStmt.cpreds.add(pas);
        if (pas.bsuccs.contains(directionStmt)) {
          directionStmt.bpreds.add(pas);
        }

        directionStmt.add_BSucc(dispatchStmt);
        dispatchStmt.add_BPred(directionStmt);

        asg.add_AugmentedStmt(directionStmt);
      }

      for (AugmentedStmt ras : toRemove) {
        entryPoint.cpreds.remove(ras);
        if (entryPoint.bpreds.contains(ras)) {
          entryPoint.bpreds.remove(ras);
        }
      }
    }

    asg.add_AugmentedStmt(dispatchStmt);

    exceptionFactLoop: for (ExceptionNode en : body.get_ExceptionFacts()) {
      IterableSet<AugmentedStmt> tryBody = en.get_TryBody();

      for (AugmentedStmt au : entry_points) {
        if (tryBody.contains(au) == false) {
          continue exceptionFactLoop;
        }
      }

      en.add_TryStmts(indirectionStmtSet);
      en.add_TryStmt(dispatchStmt);

      for (AugmentedStmt au : predecessorSet) {
        if (tryBody.contains(au) == false) {
          continue exceptionFactLoop;
        }
      }

      en.add_TryStmts(directionStmtSet);
    }
  }

  private AugmentedStmt get_NaturalEntryPoint(LinkedList<AugmentedStmt> entry_points, IterableSet<AugmentedStmt> scc) {
    AugmentedStmt best_candidate = null;
    int minScore = 0;

    Iterator<AugmentedStmt> epit = entry_points.iterator();
    while (epit.hasNext()) {
      AugmentedStmt entryPoint = epit.next();
      HashSet<AugmentedStmt> touchSet = new HashSet<AugmentedStmt>(), backTargets = new HashSet<AugmentedStmt>();

      touchSet.add(entryPoint);
      DFS(entryPoint, touchSet, backTargets, scc);

      if ((best_candidate == null) || (backTargets.size() < minScore)) {
        minScore = touchSet.size();
        best_candidate = entryPoint;
      }
    }

    return best_candidate;
  }

  private void DFS(AugmentedStmt as, HashSet<AugmentedStmt> touchSet, HashSet<AugmentedStmt> backTargets,
      IterableSet<AugmentedStmt> scc) {
    for (AugmentedStmt sas : as.csuccs) {
      if (scc.contains(sas) == false) {
        continue;
      }

      if (touchSet.contains(sas)) {
        if (backTargets.contains(sas) == false) {
          backTargets.add(sas);
        }
      } else {
        touchSet.add(sas);
        DFS(sas, touchSet, backTargets, scc);
        touchSet.remove(sas);
      }
    }
  }

  private void patch_Stmt(Stmt src, Stmt oldDst, Stmt newDst) {
    if (src instanceof GotoStmt) {
      ((GotoStmt) src).setTarget(newDst);
      return;
    }

    if (src instanceof IfStmt) {
      IfStmt ifs = (IfStmt) src;

      if (ifs.getTarget() == oldDst) {
        ifs.setTarget(newDst);
      }

      return;
    }

    if (src instanceof TableSwitchStmt) {
      TableSwitchStmt tss = (TableSwitchStmt) src;

      if (tss.getDefaultTarget() == oldDst) {
        tss.setDefaultTarget(newDst);
        return;
      }

      for (int i = tss.getLowIndex(); i <= tss.getHighIndex(); i++) {
        if (tss.getTarget(i) == oldDst) {
          tss.setTarget(i, newDst);
          return;
        }
      }
    }

    if (src instanceof LookupSwitchStmt) {
      LookupSwitchStmt lss = (LookupSwitchStmt) src;

      if (lss.getDefaultTarget() == oldDst) {
        lss.setDefaultTarget(newDst);
        return;
      }

      for (int i = 0; i < lss.getTargetCount(); i++) {
        if (lss.getTarget(i) == oldDst) {
          lss.setTarget(i, newDst);
          return;
        }
      }
    }
  }
}
