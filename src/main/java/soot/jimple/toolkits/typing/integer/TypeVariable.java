package soot.jimple.toolkits.typing.integer;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2000 Etienne Gagnon.  All rights reserved.
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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Represents a type variable. **/
class TypeVariable implements Comparable<Object> {
  private static final Logger logger = LoggerFactory.getLogger(TypeVariable.class);
  private static final boolean DEBUG = false;

  private final int id;
  private TypeVariable rep = this;
  private int rank = 0;

  private TypeNode approx;
  private TypeNode inv_approx;

  private TypeNode type;

  private List<TypeVariable> parents = Collections.emptyList();
  private List<TypeVariable> children = Collections.emptyList();

  public TypeVariable(int id, TypeResolver resolver) {
    this.id = id;
  }

  public TypeVariable(int id, TypeResolver resolver, TypeNode type) {
    this.id = id;
    this.type = type;
    approx = type;
    inv_approx = type;
  }

  public int hashCode() {
    if (rep != this) {
      return ecr().hashCode();
    }

    return id;
  }

  public boolean equals(Object obj) {
    if (rep != this) {
      return ecr().equals(obj);
    }

    if (obj == null) {
      return false;
    }

    if (!obj.getClass().equals(getClass())) {
      return false;
    }

    TypeVariable ecr = ((TypeVariable) obj).ecr();

    if (ecr != this) {
      return false;
    }

    return true;
  }

  public int compareTo(Object o) {
    if (rep != this) {
      return ecr().compareTo(o);
    }

    return id - ((TypeVariable) o).ecr().id;
  }

  private TypeVariable ecr() {
    if (rep != this) {
      rep = rep.ecr();
    }

    return rep;
  }

  public TypeVariable union(TypeVariable var) throws TypeException {
    if (rep != this) {
      return ecr().union(var);
    }

    TypeVariable y = var.ecr();

    if (this == y) {
      return this;
    }

    if (rank > y.rank) {
      y.rep = this;

      merge(y);
      y.clear();

      return this;
    }

    rep = y;
    if (rank == y.rank) {
      y.rank++;
    }

    y.merge(this);
    clear();

    return y;
  }

  private void clear() {
    inv_approx = null;
    approx = null;
    type = null;
    parents = null;
    children = null;
  }

  private void merge(TypeVariable var) throws TypeException {
    // Merge types
    if (type == null) {
      type = var.type;
    } else if (var.type != null) {
      error("Type Error(22): Attempt to merge two types.");
    }

    // Merge parents
    {
      Set<TypeVariable> set = new TreeSet<TypeVariable>(parents);
      set.addAll(var.parents);
      set.remove(this);
      parents = Collections.unmodifiableList(new LinkedList<TypeVariable>(set));
    }

    // Merge children
    {
      Set<TypeVariable> set = new TreeSet<TypeVariable>(children);
      set.addAll(var.children);
      set.remove(this);
      children = Collections.unmodifiableList(new LinkedList<TypeVariable>(set));
    }
  }

  public int id() {
    if (rep != this) {
      return ecr().id();
    }

    return id;
  }

  public void addParent(TypeVariable variable) {
    if (rep != this) {
      ecr().addParent(variable);
      return;
    }

    TypeVariable var = variable.ecr();

    if (var == this) {
      return;
    }

    {
      Set<TypeVariable> set = new TreeSet<TypeVariable>(parents);
      set.add(var);
      parents = Collections.unmodifiableList(new LinkedList<TypeVariable>(set));
    }

    {
      Set<TypeVariable> set = new TreeSet<TypeVariable>(var.children);
      set.add(this);
      var.children = Collections.unmodifiableList(new LinkedList<TypeVariable>(set));
    }
  }

  public void removeParent(TypeVariable variable) {
    if (rep != this) {
      ecr().removeParent(variable);
      return;
    }

    TypeVariable var = variable.ecr();

    {
      Set<TypeVariable> set = new TreeSet<TypeVariable>(parents);
      set.remove(var);
      parents = Collections.unmodifiableList(new LinkedList<TypeVariable>(set));
    }

    {
      Set<TypeVariable> set = new TreeSet<TypeVariable>(var.children);
      set.remove(this);
      var.children = Collections.unmodifiableList(new LinkedList<TypeVariable>(set));
    }
  }

  public void addChild(TypeVariable variable) {
    if (rep != this) {
      ecr().addChild(variable);
      return;
    }

    TypeVariable var = variable.ecr();

    if (var == this) {
      return;
    }

    {
      Set<TypeVariable> set = new TreeSet<TypeVariable>(children);
      set.add(var);
      children = Collections.unmodifiableList(new LinkedList<TypeVariable>(set));
    }

    {
      Set<TypeVariable> set = new TreeSet<TypeVariable>(var.parents);
      set.add(this);
      var.parents = Collections.unmodifiableList(new LinkedList<TypeVariable>(set));
    }
  }

  public void removeChild(TypeVariable variable) {
    if (rep != this) {
      ecr().removeChild(variable);
      return;
    }

    TypeVariable var = variable.ecr();

    {
      Set<TypeVariable> set = new TreeSet<TypeVariable>(children);
      set.remove(var);
      children = Collections.unmodifiableList(new LinkedList<TypeVariable>(set));
    }

    {
      Set<TypeVariable> set = new TreeSet<TypeVariable>(var.parents);
      set.remove(this);
      var.parents = Collections.unmodifiableList(new LinkedList<TypeVariable>(set));
    }
  }

  public List<TypeVariable> parents() {
    if (rep != this) {
      return ecr().parents();
    }

    return parents;
  }

  public List<TypeVariable> children() {
    if (rep != this) {
      return ecr().children();
    }

    return children;
  }

  public TypeNode approx() {
    if (rep != this) {
      return ecr().approx();
    }

    return approx;
  }

  public TypeNode inv_approx() {
    if (rep != this) {
      return ecr().inv_approx();
    }

    return inv_approx;
  }

  public TypeNode type() {
    if (rep != this) {
      return ecr().type();
    }

    return type;
  }

  static void error(String message) throws TypeException {
    try {
      throw new TypeException(message);
    } catch (TypeException e) {
      if (DEBUG) {
        logger.error(e.getMessage(), e);
      }
      throw e;
    }
  }

  /**
   * Computes approximative types. The work list must be initialized with all constant type variables.
   */
  public static void computeApprox(TreeSet<TypeVariable> workList) throws TypeException {
    while (workList.size() > 0) {
      TypeVariable var = workList.first();
      workList.remove(var);

      var.fixApprox(workList);
    }
  }

  public static void computeInvApprox(TreeSet<TypeVariable> workList) throws TypeException {
    while (workList.size() > 0) {
      TypeVariable var = workList.first();
      workList.remove(var);

      var.fixInvApprox(workList);
    }
  }

  private void fixApprox(TreeSet<TypeVariable> workList) throws TypeException {
    if (rep != this) {
      ecr().fixApprox(workList);
      return;
    }

    for (TypeVariable typeVariable : parents) {
      TypeVariable parent = typeVariable.ecr();

      if (parent.approx == null) {
        parent.approx = approx;
        workList.add(parent);
      } else {
        TypeNode type = parent.approx.lca_2(approx);

        if (type != parent.approx) {
          parent.approx = type;
          workList.add(parent);
        }
      }
    }

    if (type != null) {
      approx = type;
    }
  }

  private void fixInvApprox(TreeSet<TypeVariable> workList) throws TypeException {
    if (rep != this) {
      ecr().fixInvApprox(workList);
      return;
    }

    for (TypeVariable typeVariable : children) {
      TypeVariable child = typeVariable.ecr();

      if (child.inv_approx == null) {
        child.inv_approx = inv_approx;
        workList.add(child);
      } else {
        TypeNode type = child.inv_approx.gcd_2(inv_approx);

        if (type != child.inv_approx) {
          child.inv_approx = type;
          workList.add(child);
        }
      }
    }

    if (type != null) {
      inv_approx = type;
    }
  }

  public String toString() {
    if (rep != this) {
      return ecr().toString();
    }

    StringBuffer s = new StringBuffer();
    s.append(",[parents:");

    {
      boolean comma = false;

      for (TypeVariable typeVariable : parents) {
        if (comma) {
          s.append(",");
        } else {
          comma = true;
        }
        s.append(typeVariable.id());
      }
    }

    s.append("],[children:");

    {
      boolean comma = false;

      for (TypeVariable typeVariable : children) {
        if (comma) {
          s.append(",");
        } else {
          comma = true;
        }
        s.append(typeVariable.id());
      }
    }

    s.append("]");
    return "[id:" + id + ((type != null) ? (",type:" + type) : "") + ",approx:" + approx + ",inv_approx:" + inv_approx + s
        + "]";
  }

  public void fixParents() {
    if (rep != this) {
      ecr().fixParents();
      return;
    }

    {
      Set<TypeVariable> set = new TreeSet<TypeVariable>(parents);
      parents = Collections.unmodifiableList(new LinkedList<TypeVariable>(set));
    }
  }

  public void fixChildren() {
    if (rep != this) {
      ecr().fixChildren();
      return;
    }

    {
      Set<TypeVariable> set = new TreeSet<TypeVariable>(children);
      children = Collections.unmodifiableList(new LinkedList<TypeVariable>(set));
    }
  }

}
