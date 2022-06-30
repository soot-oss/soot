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

import soot.jimple.Stmt;

/**
 * A node created dynamically and attached to a statement Stmt. Can be either an inside or a load node. Two such nodes are
 * equal if and only if they have the same inside / load flag and are attached to the same statement (we use Stmt.equal
 * here).
 */
public class PurityStmtNode implements PurityNode {

  /** gives a unique id, for pretty-printing purposes */
  private static final Map<Stmt, Integer> nMap = new HashMap<Stmt, Integer>();
  private static int n = 0;

  /** Statement that created the node */
  private final Stmt id;

  /** true if an inside node, false if an load node */
  private final boolean inside;

  PurityStmtNode(Stmt id, boolean inside) {
    this.id = id;
    this.inside = inside;
    if (!nMap.containsKey(id)) {
      nMap.put(id, n);
      n++;
    }
  }

  @Override
  public String toString() {
    return inside ? ("I_" + nMap.get(id)) : ("L_" + nMap.get(id));
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof PurityStmtNode) {
      PurityStmtNode oo = (PurityStmtNode) o;
      return this.id.equals(oo.id) && this.inside == oo.inside;
    } else {
      return false;
    }
  }

  @Override
  public boolean isInside() {
    return inside;
  }

  @Override
  public boolean isLoad() {
    return !inside;
  }

  @Override
  public boolean isParam() {
    return false;
  }
}
