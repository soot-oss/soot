package soot.jimple.toolkits.pointer;

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

import java.util.HashSet;

import soot.tagkit.Attribute;

public class DependenceGraph implements Attribute {
  private final static String NAME = "DependenceGraph";
  HashSet<Edge> edges = new HashSet<Edge>();

  protected class Edge {
    short from;
    short to;

    Edge(short from, short to) {
      this.from = from;
      this.to = to;
    }

    public int hashCode() {
      return (((from) << 16) + to);
    }

    public boolean equals(Object other) {
      Edge o = (Edge) other;
      return from == o.from && to == o.to;
    }
  }

  public boolean areAdjacent(short from, short to) {
    if (from > to) {
      return areAdjacent(to, from);
    }
    if (from < 0 || to < 0) {
      return false;
    }
    if (from == to) {
      return true;
    }
    return edges.contains(new Edge(from, to));
  }

  public void addEdge(short from, short to) {
    if (from < 0) {
      throw new RuntimeException("from < 0");
    }
    if (to < 0) {
      throw new RuntimeException("to < 0");
    }
    if (from > to) {
      addEdge(to, from);
      return;
    }
    edges.add(new Edge(from, to));
  }

  public String getName() {
    return NAME;
  }

  public void setValue(byte[] v) {
    throw new RuntimeException("Not Supported");
  }

  public byte[] getValue() {
    byte[] ret = new byte[4 * edges.size()];
    int i = 0;
    for (Edge e : edges) {
      ret[i + 0] = (byte) ((e.from >> 8) & 0xff);
      ret[i + 1] = (byte) (e.from & 0xff);
      ret[i + 2] = (byte) ((e.to >> 8) & 0xff);
      ret[i + 3] = (byte) (e.to & 0xff);
      i += 4;
    }
    return ret;
  }

  public String toString() {
    StringBuffer buf = new StringBuffer("Dependences");
    for (Edge e : edges) {
      buf.append("( " + e.from + ", " + e.to + " ) ");
    }
    return buf.toString();
  }
}
