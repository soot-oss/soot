package soot.dava.toolkits.base.finders;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Jerome Miecznikowski
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
import java.util.TreeSet;

import soot.G;
import soot.Singletons;
import soot.Value;
import soot.dava.Dava;
import soot.dava.DavaBody;
import soot.dava.RetriggerAnalysisException;
import soot.dava.internal.SET.SETNode;
import soot.dava.internal.SET.SETSwitchNode;
import soot.dava.internal.asg.AugmentedStmt;
import soot.dava.internal.asg.AugmentedStmtGraph;
import soot.jimple.LookupSwitchStmt;
import soot.jimple.Stmt;
import soot.jimple.TableSwitchStmt;
import soot.util.IterableSet;

public class SwitchFinder implements FactFinder {
  public SwitchFinder(Singletons.Global g) {
  }

  public static SwitchFinder v() {
    return G.v().soot_dava_toolkits_base_finders_SwitchFinder();
  }

  private IterableSet junkBody;
  private HashSet targetSet;
  private LinkedList targetList, snTargetList, tSuccList;
  private HashMap index2target, tSucc2indexSet, tSucc2target, tSucc2Body;

  public void find(DavaBody davaBody, AugmentedStmtGraph asg, SETNode SET) throws RetriggerAnalysisException {
    Dava.v().log("SwitchFinder::find()");

    final String defaultStr = "default";

    Iterator asgit = asg.iterator();
    while (asgit.hasNext()) {
      AugmentedStmt as = (AugmentedStmt) asgit.next();

      Stmt s = as.get_Stmt();

      if (((s instanceof TableSwitchStmt) == false) && ((s instanceof LookupSwitchStmt) == false)) {
        continue;
      }

      Value key = null;

      junkBody = new IterableSet();
      targetSet = new HashSet();

      targetList = new LinkedList();
      snTargetList = new LinkedList();
      tSuccList = new LinkedList();

      index2target = new HashMap();
      tSucc2indexSet = new HashMap();
      tSucc2target = new HashMap();
      tSucc2Body = new HashMap();

      if (s instanceof TableSwitchStmt) {
        TableSwitchStmt tss = (TableSwitchStmt) s;

        int target_count = tss.getHighIndex() - tss.getLowIndex() + 1;
        for (int i = 0; i < target_count; i++) {
          build_Bindings(as, new Integer(i + tss.getLowIndex()), asg.get_AugStmt((Stmt) tss.getTarget(i)));
        }

        build_Bindings(as, defaultStr, asg.get_AugStmt((Stmt) tss.getDefaultTarget()));
        key = tss.getKey();
      }

      else if (s instanceof LookupSwitchStmt) {
        LookupSwitchStmt lss = (LookupSwitchStmt) s;

        int target_count = lss.getTargetCount();
        for (int i = 0; i < target_count; i++) {
          build_Bindings(as, new Integer(lss.getLookupValue(i)), asg.get_AugStmt((Stmt) lss.getTarget(i)));
        }

        build_Bindings(as, defaultStr, asg.get_AugStmt((Stmt) lss.getDefaultTarget()));
        key = lss.getKey();
      }

      Iterator tsit = tSuccList.iterator();
      while (tsit.hasNext()) {
        AugmentedStmt tSucc = (AugmentedStmt) tsit.next();
        AugmentedStmt target = (AugmentedStmt) tSucc2target.get(tSucc);

        snTargetList.addLast(
            new SwitchNode(target, (TreeSet<Object>) tSucc2indexSet.get(tSucc), (IterableSet) tSucc2Body.get(tSucc)));
      }

      TreeSet targetHeads = new TreeSet(), killBodies = new TreeSet();

      // Get the set of head cases and clear those bodies that should not be included in the switch. Clear as mud, huh? :-)
      {
        asg.calculate_Reachability(targetList, targetSet, as);

        SwitchNodeGraph sng = new SwitchNodeGraph(snTargetList);

        killBodies.addAll(snTargetList);
        snTargetList = new LinkedList();

        LinkedList worklist = new LinkedList();
        worklist.addAll(sng.getHeads());

        while (worklist.isEmpty() == false) {
          SwitchNode sn = (SwitchNode) worklist.removeFirst();

          snTargetList.addLast(sn);
          killBodies.remove(sn);

          SwitchNode champ = null;
          Iterator sit = sn.get_Succs().iterator();

          while (sit.hasNext()) {
            SwitchNode ssn = (SwitchNode) sit.next();

            if ((champ == null) || (champ.get_Score() < ssn.get_Score())) {
              champ = ssn;
            }
          }

          if ((champ != null) && (champ.get_Score() > 0)) {
            worklist.addLast(champ);
          }

        }

        Iterator kit = killBodies.iterator();
        while (kit.hasNext()) {
          SwitchNode sn = (SwitchNode) kit.next();

          IterableSet snBody = sn.get_Body();
          snBody.clear();
          snBody.add(sn.get_AugStmt());
        }

        sng = new SwitchNodeGraph(snTargetList);
        targetHeads.addAll(sng.getHeads());
      }

      LinkedList<SwitchNode> switchNodeList = new LinkedList<SwitchNode>();

      // Now, merge the targetHeads list and the killBodies list, keeping bundles of case fall throughs from the node graph.
      {
        while ((targetHeads.isEmpty() == false) || (killBodies.isEmpty() == false)) {

          if ((targetHeads.isEmpty()) || ((targetHeads.isEmpty() == false) && (killBodies.isEmpty() == false)
              && (((SwitchNode) targetHeads.first()).compareTo(killBodies.first()) > 0))) {

            SwitchNode nextNode = (SwitchNode) killBodies.first();
            killBodies.remove(nextNode);

            switchNodeList.addLast(nextNode);
          } else {

            SwitchNode nextNode = (SwitchNode) targetHeads.first();
            targetHeads.remove(nextNode);

            while (true) {
              switchNodeList.addLast(nextNode);

              if (nextNode.get_Succs().isEmpty()) {
                break;
              }

              nextNode = (SwitchNode) nextNode.get_Succs().get(0);
            }
          }
        }
      }

      IterableSet body = new IterableSet();
      body.add(as);
      for (SwitchNode sn : switchNodeList) {
        body.addAll(sn.get_Body());
        if (sn.get_IndexSet().contains(defaultStr)) {
          sn.get_IndexSet().clear();
          sn.get_IndexSet().add(defaultStr);
        }
      }

      body.addAll(junkBody);

      for (ExceptionNode en : davaBody.get_ExceptionFacts()) {
        IterableSet tryBody = en.get_TryBody();

        if (tryBody.contains(as)) {
          Iterator fbit = body.snapshotIterator();

          while (fbit.hasNext()) {
            AugmentedStmt fbas = (AugmentedStmt) fbit.next();

            if (tryBody.contains(fbas) == false) {
              body.remove(fbas);

              for (SwitchNode sn : switchNodeList) {
                IterableSet switchBody = sn.get_Body();

                if (switchBody.contains(fbas)) {
                  switchBody.remove(fbas);
                  break;
                }
              }
            }
          }
        }
      }

      SET.nest(new SETSwitchNode(as, key, body, switchNodeList, junkBody));
    }
  }

  private IterableSet find_SubBody(AugmentedStmt switchAS, AugmentedStmt branchS) {
    IterableSet subBody = new IterableSet();
    LinkedList<AugmentedStmt> worklist = new LinkedList<AugmentedStmt>();

    subBody.add(branchS);
    branchS = (AugmentedStmt) branchS.bsuccs.get(0);

    if (branchS.get_Dominators().contains(switchAS)) {
      worklist.addLast(branchS);
      subBody.add(branchS);
    }

    while (worklist.isEmpty() == false) {
      AugmentedStmt as = worklist.removeFirst();

      Iterator sit = as.csuccs.iterator();
      while (sit.hasNext()) {
        AugmentedStmt sas = (AugmentedStmt) sit.next();

        if ((subBody.contains(sas) == false) && (sas.get_Dominators().contains(branchS))) {
          worklist.addLast(sas);
          subBody.add(sas);
        }
      }
    }

    return subBody;
  }

  private void build_Bindings(AugmentedStmt swAs, Object index, AugmentedStmt target) {
    AugmentedStmt tSucc = (AugmentedStmt) target.bsuccs.get(0);

    if (targetSet.add(tSucc)) {
      targetList.addLast(tSucc);
    }

    index2target.put(index, target);

    TreeSet indices = null;
    if ((indices = (TreeSet) tSucc2indexSet.get(tSucc)) == null) {
      indices = new TreeSet(new IndexComparator());
      tSucc2indexSet.put(tSucc, indices);
      tSucc2target.put(tSucc, target);
      tSucc2Body.put(tSucc, find_SubBody(swAs, target));
      tSuccList.add(tSucc);
    } else {
      junkBody.add(target);

      // break all edges between the junk body and any of it's successors

      Iterator sit = target.bsuccs.iterator();
      while (sit.hasNext()) {
        ((AugmentedStmt) sit.next()).bpreds.remove(target);
      }

      sit = target.csuccs.iterator();
      while (sit.hasNext()) {
        ((AugmentedStmt) sit.next()).cpreds.remove(target);
      }

      target.bsuccs.clear();
      target.csuccs.clear();
    }

    indices.add(index);
  }
}
