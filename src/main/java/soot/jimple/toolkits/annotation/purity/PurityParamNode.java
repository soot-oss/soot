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
 * A node representing a method parameter. Each method parameter has a number, starting from 0.
 * 
 */
public class PurityParamNode implements PurityNode {
  private int id;

  PurityParamNode(int id) {
    this.id = id;
  }

  public String toString() {
    return "P_" + id;
  }

  public int hashCode() {
    return id;
  }

  public boolean equals(Object o) {
    if (o instanceof PurityParamNode) {
      return ((PurityParamNode) o).id == id;
    } else {
      return false;
    }
  }

  public boolean isInside() {
    return false;
  }

  public boolean isLoad() {
    return false;
  }

  public boolean isParam() {
    return true;
  }
}
