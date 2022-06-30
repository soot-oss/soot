package soot.toolkits.graph;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2005 Navindra Umanee <navindra@cs.mcgill.ca>
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

import java.util.List;

/**
 * A reversible version of HashMutableDirectedGraph
 *
 * @author Navindra Umanee
 *
 * @param <N>
 */
public class HashReversibleGraph<N> extends HashMutableDirectedGraph<N> implements ReversibleGraph<N> {
  protected boolean reversed;

  public HashReversibleGraph(DirectedGraph<N> dg) {
    this();

    for (N s : dg) {
      addNode(s);
    }

    for (N s : dg) {
      for (N t : dg.getSuccsOf(s)) {
        addEdge(s, t);
      }
    }

    /* use the same heads and tails as the original graph */
    heads.clear();
    heads.addAll(dg.getHeads());
    tails.clear();
    tails.addAll(dg.getTails());
  }

  public HashReversibleGraph() {
    super();
    reversed = false;
  }

  @Override
  public boolean isReversed() {
    return reversed;
  }

  @Override
  public ReversibleGraph<N> reverse() {
    reversed = !reversed;
    return this;
  }

  @Override
  public void addEdge(N from, N to) {
    if (reversed) {
      super.addEdge(to, from);
    } else {
      super.addEdge(from, to);
    }
  }

  @Override
  public void removeEdge(N from, N to) {
    if (reversed) {
      super.removeEdge(to, from);
    } else {
      super.removeEdge(from, to);
    }
  }

  @Override
  public boolean containsEdge(N from, N to) {
    return reversed ? super.containsEdge(to, from) : super.containsEdge(from, to);
  }

  @Override
  public List<N> getHeads() {
    return reversed ? super.getTails() : super.getHeads();
  }

  @Override
  public List<N> getTails() {
    return reversed ? super.getHeads() : super.getTails();
  }

  @Override
  public List<N> getPredsOf(N s) {
    return reversed ? super.getSuccsOf(s) : super.getPredsOf(s);
  }

  @Override
  public List<N> getSuccsOf(N s) {
    return reversed ? super.getPredsOf(s) : super.getSuccsOf(s);
  }
}
