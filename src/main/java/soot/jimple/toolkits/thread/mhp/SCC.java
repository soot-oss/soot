
package soot.jimple.toolkits.thread.mhp;

import heros.solver.Pair;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vallée-Rai and others
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import soot.toolkits.graph.DirectedGraph;
import soot.util.FastStack;

// *** USE AT YOUR OWN RISK ***
// May Happen in Parallel (MHP) analysis by Lin Li.
// This code should be treated as beta-quality code.
// It was written in 2003, but not incorporated into Soot until 2006.
// As such, it may contain incorrect assumptions about the usage
// of certain Soot classes.
// Some portions of this MHP analysis have been quality-checked, and are
// now used by the Transactions toolkit.
//
// -Richard L. Halpert, 2006-11-30

public class SCC<T> {

  private Set<T> gray;
  private final LinkedList<T> finishedOrder;
  private final List<List<T>> sccList;

  public SCC(Iterator<T> it, DirectedGraph<T> g) {

    gray = new HashSet<T>();
    finishedOrder = new LinkedList<T>();
    sccList = new ArrayList<List<T>>();

    // Visit each node
    {

      while (it.hasNext()) {
        T s = it.next();
        if (!gray.contains(s)) {

          visitNode(g, s);
        }
      }

    }

    // Re-color all nodes white
    gray = new HashSet<T>();

    // visit nodes via tranpose edges according decreasing order of finish time of nodes

    {

      Iterator<T> revNodeIt = finishedOrder.iterator();
      while (revNodeIt.hasNext()) {
        T s = revNodeIt.next();
        if (!gray.contains(s)) {

          List<T> scc = new ArrayList<T>();

          visitRevNode(g, s, scc);
          sccList.add(scc);
        }

      }
    }
  }

  private void visitNode(DirectedGraph<T> g, T s) {
    gray.add(s);
    FastStack<Pair<T, Iterator<T>>> stack = new FastStack<>();
    stack.push(new Pair<>(s, g.getSuccsOf(s).iterator()));
    next: while (!stack.isEmpty()) {

      Pair<T, Iterator<T>> p = stack.peek();
      Iterator<T> it = p.getO2();
      while (it.hasNext()) {
        T succ = it.next();
        if (!gray.contains(succ)) {
          gray.add(succ);
          stack.push(new Pair<T, Iterator<T>>(succ, g.getSuccsOf(succ).iterator()));
          continue next;
        }
      }
      stack.pop();
      finishedOrder.addFirst(p.getO1());
    }
  }

  private void visitRevNode(DirectedGraph<T> g, T s, List<T> scc) {

    scc.add(s);
    gray.add(s);

    FastStack<Iterator<T>> stack = new FastStack<>();
    stack.push(g.getPredsOf(s).iterator());

    next: while (!stack.isEmpty()) {

      Iterator<T> predsIt = stack.peek();
      while (predsIt.hasNext()) {
        T pred = predsIt.next();
        if (!gray.contains(pred)) {
          scc.add(pred);
          gray.add(pred);
          stack.push(g.getPredsOf(pred).iterator());
          continue next;
        }
      }
      stack.pop();
    }
  }

  public List<List<T>> getSccList() {
    return sccList;
  }

  public LinkedList<T> getFinishedOrder() {
    return finishedOrder;
  }
}