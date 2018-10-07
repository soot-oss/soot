package soot.jimple.toolkits.callgraph;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Ondrej Lhotak
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

import java.util.Iterator;

import soot.MethodOrMethodContext;

/**
 * Adapts an iterator over a collection of Edge's to be an iterator over the source methods of the edges.
 * 
 * @author Ondrej Lhotak
 */
public final class Sources implements Iterator<MethodOrMethodContext> {
  Iterator<Edge> edges;

  public Sources(Iterator<Edge> edges) {
    this.edges = edges;
  }

  public boolean hasNext() {
    return edges.hasNext();
  }

  public MethodOrMethodContext next() {
    Edge e = edges.next();
    return e.getSrc();
  }

  public void remove() {
    throw new UnsupportedOperationException();
  }
}
