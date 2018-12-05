package soot.jimple.toolkits.annotation.logic;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2004 Jennifer Lhotak
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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.Body;
import soot.BodyTransformer;
import soot.Unit;
import soot.jimple.Stmt;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.MHGDominatorsFinder;
import soot.toolkits.graph.UnitGraph;

public class LoopFinder extends BodyTransformer {

  private Set<Loop> loops;

  public LoopFinder() {
    loops = null;
  }

  protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
    getLoops(b);
  }

  public Set<Loop> getLoops(Body b) {
    if (loops != null) {
      return loops;
    }
    return getLoops(new ExceptionalUnitGraph(b));
  }

  public Set<Loop> getLoops(UnitGraph g) {
    if (loops != null) {
      return loops;
    }

    MHGDominatorsFinder<Unit> a = new MHGDominatorsFinder<Unit>(g);
    Map<Stmt, List<Stmt>> loops = new HashMap<Stmt, List<Stmt>>();

    for (Unit u : g.getBody().getUnits()) {
      List<Unit> succs = g.getSuccsOf(u);
      List<Unit> dominaters = a.getDominators(u);
      List<Stmt> headers = new ArrayList<Stmt>();

      for (Unit succ : succs) {
        if (dominaters.contains(succ)) {
          // header succeeds and dominates s, we have a loop
          headers.add((Stmt) succ);
        }
      }

      for (Unit header : headers) {
        List<Stmt> loopBody = getLoopBodyFor(header, u, g);
        if (loops.containsKey(header)) {
          // merge bodies
          List<Stmt> lb1 = loops.get(header);
          loops.put((Stmt) header, union(lb1, loopBody));
        } else {
          loops.put((Stmt) header, loopBody);
        }
      }
    }

    Set<Loop> ret = new HashSet<Loop>();
    for (Map.Entry<Stmt, List<Stmt>> entry : loops.entrySet()) {
      ret.add(new Loop(entry.getKey(), entry.getValue(), g));
    }

    this.loops = ret;
    return ret;
  }

  private List<Stmt> getLoopBodyFor(Unit header, Unit node, UnitGraph g) {
    List<Stmt> loopBody = new ArrayList<Stmt>();
    Deque<Unit> stack = new ArrayDeque<Unit>();

    loopBody.add((Stmt) header);
    stack.push(node);

    while (!stack.isEmpty()) {
      Stmt next = (Stmt) stack.pop();
      if (!loopBody.contains(next)) {
        // add next to loop body
        loopBody.add(0, next);
        // put all preds of next on stack
        for (Unit u : g.getPredsOf(next)) {
          stack.push(u);
        }
      }
    }

    assert (node == header && loopBody.size() == 1) || loopBody.get(loopBody.size() - 2) == node;
    assert loopBody.get(loopBody.size() - 1) == header;

    return loopBody;
  }

  private List<Stmt> union(List<Stmt> l1, List<Stmt> l2) {
    for (Stmt next : l2) {
      if (!l1.contains(next)) {
        l1.add(next);
      }
    }
    return l1;
  }
}
