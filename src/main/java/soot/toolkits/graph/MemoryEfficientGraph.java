package soot.toolkits.graph;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2001 Felix Kwok
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

/**
 * A memory efficient version of HashMutableDirectedGraph, in the sense that throw-away objects passed as arguments will not
 * be kept in the process of adding edges.
 */

public class MemoryEfficientGraph<N> extends HashMutableDirectedGraph<N> {

  HashMap<N, N> self = new HashMap<N, N>();

  public void addNode(N o) {
    super.addNode(o);
    self.put(o, o);
  }

  public void removeNode(N o) {
    super.removeNode(o);
    self.remove(o);
  }

  public void addEdge(N from, N to) {
    if (containsNode(from) && containsNode(to)) {
      super.addEdge(self.get(from), self.get(to));
    } else if (!containsNode(from)) {
      throw new RuntimeException(from.toString() + " not in graph!");
    } else {
      throw new RuntimeException(to.toString() + " not in graph!");
    }
  }

  public void removeEdge(N from, N to) {
    if (containsNode(from) && containsNode(to)) {
      super.removeEdge(self.get(from), self.get(to));
    } else if (!containsNode(from)) {
      throw new RuntimeException(from.toString() + " not in graph!");
    } else {
      throw new RuntimeException(to.toString() + " not in graph!");
    }
  }

}
