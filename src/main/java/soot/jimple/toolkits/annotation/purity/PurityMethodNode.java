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

import java.util.HashMap;
import java.util.Map;

import soot.SootMethod;

/**
 * Kind of Stmt inside node, but global to the method. Used for synthetic summary of analysed methods returning a fresh
 * object.
 */
public class PurityMethodNode implements PurityNode {

  /** gives a unique id, for pretty-printing purposes */
  private static final Map<SootMethod, Integer> nMap = new HashMap<SootMethod, Integer>();
  private static int n = 0;

  /** Method that created the node */
  private SootMethod id;

  PurityMethodNode(SootMethod id) {
    this.id = id;
    if (!nMap.containsKey(id)) {
      nMap.put(id, n);
      n++;
    }
  }

  @Override
  public String toString() {
    return "M_" + nMap.get(id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof PurityMethodNode) {
      PurityMethodNode oo = (PurityMethodNode) o;
      return this.id.equals(oo.id);
    } else {
      return false;
    }
  }

  @Override
  public boolean isInside() {
    return true;
  }

  @Override
  public boolean isLoad() {
    return false;
  }

  @Override
  public boolean isParam() {
    return false;
  }
}
