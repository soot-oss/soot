package soot.dava.internal.asg;

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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.Unit;
import soot.dava.Dava;
import soot.jimple.IfStmt;
import soot.jimple.LookupSwitchStmt;
import soot.jimple.Stmt;
import soot.jimple.TableSwitchStmt;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.graph.PseudoTopologicalOrderer;
import soot.toolkits.graph.TrapUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.util.IterableSet;

public class AugmentedStmtGraph implements DirectedGraph<AugmentedStmt> {
  private HashMap<Stmt, AugmentedStmt> binding;
  private HashMap<AugmentedStmt, AugmentedStmt> original2clone;
  private IterableSet<AugmentedStmt> aug_list;
  private IterableSet<Stmt> stmt_list;
  private List<AugmentedStmt> bheads, btails, cheads, ctails;

  public AugmentedStmtGraph(AugmentedStmtGraph other) {
    this();

    HashMap<AugmentedStmt, AugmentedStmt> old2new = new HashMap<AugmentedStmt, AugmentedStmt>();

    for (AugmentedStmt oas : other.aug_list) {
      Stmt s = oas.get_Stmt();

      AugmentedStmt nas = new AugmentedStmt(s);
      aug_list.add(nas);
      stmt_list.add(s);
      binding.put(s, nas);

      old2new.put(oas, nas);
    }

    for (AugmentedStmt oas : other.aug_list) {
      AugmentedStmt nas = (AugmentedStmt) old2new.get(oas);

      for (AugmentedStmt aug : oas.bpreds) {
        nas.bpreds.add(old2new.get(aug));
      }
      if (nas.bpreds.isEmpty()) {
        bheads.add(nas);
      }

      for (AugmentedStmt aug : oas.cpreds) {
        nas.cpreds.add(old2new.get(aug));
      }
      if (nas.cpreds.isEmpty()) {
        cheads.add(nas);
      }

      for (AugmentedStmt aug : oas.bsuccs) {
        nas.bsuccs.add(old2new.get(aug));
      }
      if (nas.bsuccs.isEmpty()) {
        btails.add(nas);
      }

      for (AugmentedStmt aug : oas.csuccs) {
        nas.csuccs.add(old2new.get(aug));
      }
      if (nas.csuccs.isEmpty()) {
        ctails.add(nas);
      }
    }

    find_Dominators();
  }

  public AugmentedStmtGraph(BriefUnitGraph bug, TrapUnitGraph cug) {
    this();

    Dava.v().log("AugmentedStmtGraph::AugmentedStmtGraph() - cug.size() = " + cug.size());

    // make the augmented statements
    for (Unit u : cug) {
      Stmt s = (Stmt) u;
      add_StmtBinding(s, new AugmentedStmt(s));
    }

    // make the list of augmented statements in pseudo topological order!
    List<Unit> cugList = (new PseudoTopologicalOrderer<Unit>()).newList(cug, false);
    for (Unit u : cugList) {
      Stmt s = (Stmt) u;
      aug_list.add(get_AugStmt(s));
      stmt_list.add(s);
    }

    // now that we've got all the augmented statements, mirror the statement
    // graph
    for (AugmentedStmt as : aug_list) {
      mirror_PredsSuccs(as, bug);
      mirror_PredsSuccs(as, cug);
    }

    find_Dominators();
  }

  public AugmentedStmtGraph() {
    binding = new HashMap<Stmt, AugmentedStmt>();
    original2clone = new HashMap<AugmentedStmt, AugmentedStmt>();
    aug_list = new IterableSet<AugmentedStmt>();
    stmt_list = new IterableSet<Stmt>();

    bheads = new LinkedList<AugmentedStmt>();
    btails = new LinkedList<AugmentedStmt>();
    cheads = new LinkedList<AugmentedStmt>();
    ctails = new LinkedList<AugmentedStmt>();
  }

  public void add_AugmentedStmt(AugmentedStmt as) {
    Stmt s = as.get_Stmt();

    aug_list.add(as);
    stmt_list.add(s);

    add_StmtBinding(s, as);

    if (as.bpreds.isEmpty()) {
      bheads.add(as);
    }

    if (as.cpreds.isEmpty()) {
      cheads.add(as);
    }

    if (as.bsuccs.isEmpty()) {
      btails.add(as);
    }

    if (as.csuccs.isEmpty()) {
      ctails.add(as);
    }

    check_List(as.bpreds, btails);
    check_List(as.bsuccs, bheads);
    check_List(as.cpreds, ctails);
    check_List(as.csuccs, cheads);
  }

  public boolean contains(Object o) {
    return aug_list.contains(o);
  }

  public AugmentedStmt get_CloneOf(AugmentedStmt as) {
    return original2clone.get(as);
  }

  @Override
  public int size() {
    return aug_list.size();
  }

  private <T> void check_List(List<T> psList, List<T> htList) {
    for (T t : psList) {
      if (htList.contains(t)) {
        htList.remove(t);
      }
    }
  }

  public void calculate_Reachability(AugmentedStmt source, Set<AugmentedStmt> blockers, AugmentedStmt dominator) {
    if (blockers == null) {
      throw new RuntimeException("Tried to call AugmentedStmtGraph:calculate_Reachability() with null blockers.");
    }

    if (source == null) {
      return;
    }

    LinkedList<AugmentedStmt> worklist = new LinkedList<AugmentedStmt>();
    HashSet<AugmentedStmt> touchSet = new HashSet<AugmentedStmt>();

    worklist.addLast(source);
    touchSet.add(source);

    while (!worklist.isEmpty()) {
      AugmentedStmt as = worklist.removeFirst();

      for (AugmentedStmt sas : as.csuccs) {
        if (touchSet.contains(sas) || !sas.get_Dominators().contains(dominator)) {
          continue;
        }

        touchSet.add(sas);

        IterableSet<AugmentedStmt> reachers = sas.get_Reachers();

        if (!reachers.contains(source)) {
          reachers.add(source);
        }

        if (!blockers.contains(sas)) {
          worklist.addLast(sas);
        }
      }
    }
  }

  public void calculate_Reachability(Collection<AugmentedStmt> sources, Set<AugmentedStmt> blockers,
      AugmentedStmt dominator) {
    for (AugmentedStmt next : sources) {
      calculate_Reachability(next, blockers, dominator);
    }
  }

  public void calculate_Reachability(AugmentedStmt source, AugmentedStmt blocker, AugmentedStmt dominator) {
    HashSet<AugmentedStmt> h = new HashSet<AugmentedStmt>();

    h.add(blocker);

    calculate_Reachability(source, h, dominator);
  }

  public void calculate_Reachability(Collection<AugmentedStmt> sources, AugmentedStmt blocker, AugmentedStmt dominator) {
    HashSet<AugmentedStmt> h = new HashSet<AugmentedStmt>();

    h.add(blocker);

    calculate_Reachability(sources, h, dominator);
  }

  public void calculate_Reachability(AugmentedStmt source, AugmentedStmt dominator) {
    calculate_Reachability(source, Collections.<AugmentedStmt>emptySet(), dominator);
  }

  public void calculate_Reachability(Collection<AugmentedStmt> sources, AugmentedStmt dominator) {
    calculate_Reachability(sources, Collections.<AugmentedStmt>emptySet(), dominator);
  }

  public void calculate_Reachability(AugmentedStmt source) {
    calculate_Reachability(source, null);
  }

  public void calculate_Reachability(Collection<AugmentedStmt> sources) {
    calculate_Reachability(sources, null);
  }

  public void add_StmtBinding(Stmt s, AugmentedStmt as) {
    binding.put(s, as);
  }

  public AugmentedStmt get_AugStmt(Stmt s) {
    AugmentedStmt as = binding.get(s);
    if (as == null) {
      throw new RuntimeException("Could not find augmented statement for: " + s.toString());
    }
    return as;
  }

  // now put in the methods to satisfy the DirectedGraph interface

  @Override
  public List<AugmentedStmt> getHeads() {
    return cheads;
  }

  @Override
  public List<AugmentedStmt> getTails() {
    return ctails;
  }

  @Override
  public Iterator<AugmentedStmt> iterator() {
    return aug_list.iterator();
  }

  @Override
  public List<AugmentedStmt> getPredsOf(AugmentedStmt s) {
    return s.cpreds;
  }

  public List<AugmentedStmt> getPredsOf(Stmt s) {
    return get_AugStmt(s).cpreds;
  }

  @Override
  public List<AugmentedStmt> getSuccsOf(AugmentedStmt s) {
    return s.csuccs;
  }

  public List<AugmentedStmt> getSuccsOf(Stmt s) {
    return get_AugStmt(s).csuccs;
  }

  // end of methods satisfying DirectedGraph

  public List<AugmentedStmt> get_BriefHeads() {
    return bheads;
  }

  public List<AugmentedStmt> get_BriefTails() {
    return btails;
  }

  public IterableSet<AugmentedStmt> get_ChainView() {
    return new IterableSet<AugmentedStmt>(aug_list);
  }

  @Override
  public Object clone() {
    return new AugmentedStmtGraph(this);
  }

  public boolean remove_AugmentedStmt(AugmentedStmt toRemove) {
    if (!aug_list.contains(toRemove)) {
      return false;
    }

    for (AugmentedStmt pas : toRemove.bpreds) {
      if (pas.bsuccs.contains(toRemove)) {
        pas.bsuccs.remove(toRemove);
      }
    }

    for (AugmentedStmt pas : toRemove.cpreds) {
      if (pas.csuccs.contains(toRemove)) {
        pas.csuccs.remove(toRemove);
      }
    }

    for (AugmentedStmt sas : toRemove.bsuccs) {
      if (sas.bpreds.contains(toRemove)) {
        sas.bpreds.remove(toRemove);
      }
    }

    for (AugmentedStmt sas : toRemove.csuccs) {
      if (sas.cpreds.contains(toRemove)) {
        sas.cpreds.remove(toRemove);
      }
    }

    aug_list.remove(toRemove);
    stmt_list.remove(toRemove.get_Stmt());

    bheads.remove(toRemove);
    btails.remove(toRemove);
    cheads.remove(toRemove);
    ctails.remove(toRemove);

    binding.remove(toRemove.get_Stmt());

    return true;

    // NOTE: we do *NOT* touch the underlying unit graphs.
  }

  @Override
  public String toString() {
    StringBuilder b = new StringBuilder();
    String cr = "\n";

    b.append("AugmentedStmtGraph (size: ").append(size()).append(" stmts)").append(cr);

    for (AugmentedStmt as : aug_list) {
      b.append("| .---").append(cr).append("| | AugmentedStmt ").append(as.toString()).append(cr).append("| |").append(cr)
          .append("| |  preds:");

      for (AugmentedStmt pas : as.cpreds) {
        b.append(" ").append(pas.toString());
      }

      b.append(cr).append("| |").append(cr).append("| |  succs:");
      for (AugmentedStmt sas : as.csuccs) {
        b.append(" ").append(sas.toString());
      }

      b.append(cr).append("| |").append(cr).append("| |  doms:");
      for (AugmentedStmt das : as.get_Dominators()) {
        b.append(" ").append(das.toString());
      }

      b.append(cr).append("| `---").append(cr);
    }

    b.append("-").append(cr);
    return b.toString();
  }

  private void mirror_PredsSuccs(AugmentedStmt as, UnitGraph ug) {
    Stmt s = as.get_Stmt();

    LinkedList<AugmentedStmt> preds = new LinkedList<AugmentedStmt>(), succs = new LinkedList<AugmentedStmt>();

    // mirror the predecessors
    for (Unit u : ug.getPredsOf(s)) {
      AugmentedStmt po = get_AugStmt((Stmt) u);
      if (!preds.contains(po)) {
        preds.add(po);
      }
    }

    // mirror the successors
    for (Unit u : ug.getSuccsOf(s)) {
      AugmentedStmt so = get_AugStmt((Stmt) u);
      if (!succs.contains(so)) {
        succs.add(so);
      }
    }

    // attach the mirrors properly to the AugmentedStmt
    if (ug instanceof BriefUnitGraph) {
      as.bpreds = preds;
      as.bsuccs = succs;

      if (preds.isEmpty()) {
        bheads.add(as);
      }
      if (succs.isEmpty()) {
        btails.add(as);
      }
    } else if (ug instanceof TrapUnitGraph) {
      as.cpreds = preds;
      as.csuccs = succs;

      if (preds.isEmpty()) {
        cheads.add(as);
      }
      if (succs.isEmpty()) {
        ctails.add(as);
      }
    } else {
      throw new RuntimeException("Unknown UnitGraph type: " + ug.getClass());
    }
  }

  public IterableSet<AugmentedStmt> clone_Body(IterableSet<AugmentedStmt> oldBody) {
    HashMap<AugmentedStmt, AugmentedStmt> old2new = new HashMap<AugmentedStmt, AugmentedStmt>();
    HashMap<AugmentedStmt, AugmentedStmt> new2old = new HashMap<AugmentedStmt, AugmentedStmt>();

    IterableSet<AugmentedStmt> newBody = new IterableSet<AugmentedStmt>();

    for (AugmentedStmt as : oldBody) {
      AugmentedStmt clone = (AugmentedStmt) as.clone();

      original2clone.put(as, clone);

      old2new.put(as, clone);
      new2old.put(clone, as);
      newBody.add(clone);
    }

    for (AugmentedStmt newAs : newBody) {
      AugmentedStmt oldAs = new2old.get(newAs);

      mirror_PredsSuccs(oldAs, oldAs.bpreds, newAs.bpreds, old2new);
      mirror_PredsSuccs(oldAs, oldAs.cpreds, newAs.cpreds, old2new);
      mirror_PredsSuccs(oldAs, oldAs.bsuccs, newAs.bsuccs, old2new);
      mirror_PredsSuccs(oldAs, oldAs.csuccs, newAs.csuccs, old2new);
    }

    for (AugmentedStmt au : newBody) {
      add_AugmentedStmt(au);
    }

    HashMap<Unit, Stmt> so2n = new HashMap<Unit, Stmt>();

    for (AugmentedStmt as : oldBody) {
      Stmt os = as.get_Stmt();
      Stmt ns = old2new.get(as).get_Stmt();

      so2n.put(os, ns);
    }

    for (AugmentedStmt nas : newBody) {
      AugmentedStmt oas = new2old.get(nas);

      Stmt ns = nas.get_Stmt(), os = oas.get_Stmt();

      if (os instanceof IfStmt) {
        Unit target = ((IfStmt) os).getTarget();
        Unit newTgt = so2n.get(target);
        ((IfStmt) ns).setTarget(newTgt == null ? target : newTgt);
      } else if (os instanceof TableSwitchStmt) {
        TableSwitchStmt otss = (TableSwitchStmt) os, ntss = (TableSwitchStmt) ns;

        Unit target = otss.getDefaultTarget();
        Unit newTgt = so2n.get(target);
        ntss.setDefaultTarget(newTgt == null ? target : newTgt);

        LinkedList<Unit> new_target_list = new LinkedList<Unit>();
        int target_count = otss.getHighIndex() - otss.getLowIndex() + 1;
        for (int i = 0; i < target_count; i++) {
          target = otss.getTarget(i);
          newTgt = so2n.get(target);
          new_target_list.add(newTgt == null ? target : newTgt);
        }
        ntss.setTargets(new_target_list);
      } else if (os instanceof LookupSwitchStmt) {
        LookupSwitchStmt olss = (LookupSwitchStmt) os, nlss = (LookupSwitchStmt) ns;

        Unit target = olss.getDefaultTarget();
        Unit newTgt = so2n.get(target);
        nlss.setDefaultTarget(newTgt == null ? target : newTgt);

        Unit[] new_target_list = new Unit[olss.getTargetCount()];
        for (int i = 0; i < new_target_list.length; i++) {
          target = olss.getTarget(i);
          newTgt = so2n.get(target);
          new_target_list[i] = newTgt == null ? target : newTgt;
        }
        nlss.setTargets(new_target_list);
        nlss.setLookupValues(olss.getLookupValues());
      }
    }

    return newBody;
  }

  private void mirror_PredsSuccs(AugmentedStmt originalAs, List<AugmentedStmt> oldList, List<AugmentedStmt> newList,
      Map<AugmentedStmt, AugmentedStmt> old2new) {
    for (AugmentedStmt oldAs : oldList) {
      AugmentedStmt newAs = old2new.get(oldAs);

      if (newAs != null) {
        newList.add(newAs);
      } else {
        newList.add(oldAs);

        AugmentedStmt clonedAs = old2new.get(originalAs);

        if (oldList == originalAs.bpreds) {
          oldAs.bsuccs.add(clonedAs);
        } else if (oldList == originalAs.cpreds) {
          oldAs.csuccs.add(clonedAs);
        } else if (oldList == originalAs.bsuccs) {
          oldAs.bpreds.add(clonedAs);
        } else if (oldList == originalAs.csuccs) {
          oldAs.cpreds.add(clonedAs);
        } else {
          throw new RuntimeException("Error mirroring preds and succs in Try block splitting.");
        }
      }
    }
  }

  public void find_Dominators() {
    // set up the dominator sets for all the nodes in the graph
    for (AugmentedStmt as : aug_list) {
      // Dominators:
      // safe starting approximation for S(0) ... empty set
      // unsafe starting approximation for S(i) .. full set
      IterableSet<AugmentedStmt> doms = as.get_Dominators();
      doms.clear();
      if (!as.cpreds.isEmpty()) {
        doms.addAll(aug_list);
      }
    }

    // build the worklist
    IterableSet<AugmentedStmt> worklist = new IterableSet<AugmentedStmt>();
    worklist.addAll(aug_list);

    // keep going until the worklist is empty
    while (!worklist.isEmpty()) {
      AugmentedStmt as = worklist.getFirst();
      worklist.removeFirst();

      IterableSet<AugmentedStmt> pred_intersection = new IterableSet<AugmentedStmt>();
      boolean first_pred = true;

      // run through all the predecessors and get their dominance
      // intersection
      for (AugmentedStmt pas : as.cpreds) {
        if (first_pred) {
          // for the first predecessor just take all its dominators
          pred_intersection.addAll(pas.get_Dominators());
          if (!pred_intersection.contains(pas)) {
            pred_intersection.add(pas);
          }
          first_pred = false;
        } else {
          // for the subsequent predecessors remove the ones they do not
          // have from the intersection
          for (Iterator<AugmentedStmt> piit = pred_intersection.snapshotIterator(); piit.hasNext();) {
            AugmentedStmt pid = piit.next();

            if (!pas.get_Dominators().contains(pid) && (pas != pid)) {
              pred_intersection.remove(pid);
            }
          }
        }
      }

      // update dominance if we have a change
      if (!as.get_Dominators().equals(pred_intersection)) {
        for (AugmentedStmt o : as.csuccs) {
          if (!worklist.contains(o)) {
            worklist.add(o);
          }
        }

        as.get_Dominators().clear();
        as.get_Dominators().addAll(pred_intersection);
      }
    }
  }
}
