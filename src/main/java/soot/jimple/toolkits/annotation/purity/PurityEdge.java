package soot.jimple.toolkits.annotation.purity;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2005 Antoine Mine
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

/**
 * An edge in a purity graph. Each edge has a source PurityNode, a target PurityNode, and a field label (we use a String
 * here). To represent an array element, the convention is to use the [] field label. Edges are mmuable and hashable. They
 * compare equal only if they link equal nodes and have equal labels.
 *
 */
public class PurityEdge {
  private String field;
  private PurityNode source, target;
  private boolean inside;

  PurityEdge(PurityNode source, String field, PurityNode target, boolean inside) {
    this.source = source;
    this.field = field;
    this.target = target;
    this.inside = inside;
  }

  public String getField() {
    return field;
  }

  public PurityNode getTarget() {
    return target;
  }

  public PurityNode getSource() {
    return source;
  }

  public boolean isInside() {
    return inside;
  }

  public int hashCode() {
    return field.hashCode() + target.hashCode() + source.hashCode() + (inside ? 69 : 0);
  }

  public boolean equals(Object o) {
    if (!(o instanceof PurityEdge)) {
      return false;
    }
    PurityEdge e = (PurityEdge) o;
    return source.equals(e.source) && field.equals(e.field) && target.equals(e.target) && inside == e.inside;
  }

  public String toString() {
    if (inside) {
      return source.toString() + " = " + field + " => " + target.toString();
    } else {
      return source.toString() + " - " + field + " -> " + target.toString();
    }

  }
}
