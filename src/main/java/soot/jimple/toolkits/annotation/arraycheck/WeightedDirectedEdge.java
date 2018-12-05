package soot.jimple.toolkits.annotation.arraycheck;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2000 Feng Qian
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

class WeightedDirectedEdge {
  Object from, to;
  int weight;

  public WeightedDirectedEdge(Object from, Object to, int weight) {
    this.from = from;
    this.to = to;
    this.weight = weight;
  }

  public int hashCode() {
    return from.hashCode() + to.hashCode() + weight;
  }

  public boolean equals(Object other) {
    if (other instanceof WeightedDirectedEdge) {
      WeightedDirectedEdge another = (WeightedDirectedEdge) other;
      return ((this.from == another.from) && (this.to == another.to) && (this.weight == another.weight));
    }
    return false;
  }

  public String toString() {
    return from + "->" + to + "=" + weight;
  }
}
