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
    this.approx = type;
    this.inv_approx = type;
  }

  @Override
  public int hashCode() {
    if (rep != this) {
      return ecr().hashCode();
    }

    return id;
  }

  @Override
  public boolean equals(Object obj) {
    if (rep != this) {
      return ecr().equals(obj);
    }
    if ((obj == null) || !obj.getClass().equals(this.getClass())) {
      return false;
    }
    return ((TypeVariable) obj).ecr() == this;
  }

  @Override
  public int compareTo(Object o) {
    if (rep != this) {
      return ecr().compareTo(o);
    } else {
      return id - ((TypeVariable) o).ecr().id;
    }
  }

  private TypeVariable ecr() {
    if (rep != this) {
      rep = rep.ecr();
    }
    return rep;
  }

  public TypeVariable union(TypeVariable var) throws TypeException {
    if (this.rep != this) {
      return ecr().union(var);
    }

    TypeVariable y = var.ecr();
    if (this == y) {
      return this;
    }

    if (this.rank > y.rank) {
      y.rep = this;

      merge(y);
      y.clear();

      return this;
    }

    this.rep = y;
    if (this.rank == y.rank) {
      y.rank++;
    }

    y.merge(this);
    clear();

    return y;
  }

  private void clear() {
    this.inv_approx = null;
    this.approx = null;
    this.type = null;
    this.parents = null;
    this.children = null;
  }

  private void merge(TypeVariable var) throws TypeException {
    // Merge types
    if (this.type == null) {
      this.type = var.type;
    } else if (var.type != null) {
      error("Type Error(22): Attempt to merge two types.");
    }

    // Merge parents
    {
      Set<TypeVariable> set = new TreeSet<TypeVariable>(this.parents);
      set.addAll(var.parents);
      set.remove(this);
      this.parents = Collections.unmodifiableList(new LinkedList<TypeVariable>(set));
    }

    // Merge children
    {
      Set<TypeVariable> set = new TreeSet<TypeVariable>(this.children);
      set.addAll(var.children);
      set.remove(this);
      this.children = Collections.unmodifiableList(new LinkedList<TypeVariable>(set));
    }
  }

  public int id() {
    return (rep != this) ? ecr().id() : id;
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
      Set<TypeVariable> set = new TreeSet<TypeVariable>(this.parents);
      set.add(var);
      this.parents = Collections.unmodifiableList(new LinkedList<TypeVariable>(set));
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
      Set<TypeVariable> set = new TreeSet<TypeVariable>(this.parents);
      set.remove(var);
      this.parents = Collections.unmodifiableList(new LinkedList<TypeVariable>(set));
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
      Set<TypeVariable> set = new TreeSet<TypeVariable>(this.children);
      set.add(var);
      this.children = Collections.unmodifiableList(new LinkedList<TypeVariable>(set));
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
      Set<TypeVariable> set = new TreeSet<TypeVariable>(this.children);
      set.remove(var);
      this.children = Collections.unmodifiableList(new LinkedList<TypeVariable>(set));
    }
    {
      Set<TypeVariable> set = new TreeSet<TypeVariable>(var.parents);
      set.remove(this);
      var.parents = Collections.unmodifiableList(new LinkedList<TypeVariable>(set));
    }
  }

  public List<TypeVariable> parents() {
    return (rep != this) ? ecr().parents() : parents;
  }

  public List<TypeVariable> children() {
    return (rep != this) ? ecr().children() : children;
  }

  public TypeNode approx() {
    return (rep != this) ? ecr().approx() : approx;
  }

  public TypeNode inv_approx() {
    return (rep != this) ? ecr().inv_approx() : inv_approx;
  }

  public TypeNode type() {
    return (rep != this) ? ecr().type() : type;
  }

  static void error(String message) throws TypeException {
    TypeException e = new TypeException(message);
    if (DEBUG) {
      logger.error(e.getMessage(), e);
    }
    throw e;
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

    for (TypeVariable typeVariable : this.parents) {
      TypeVariable parent = typeVariable.ecr();

      if (parent.approx == null) {
        parent.approx = this.approx;
        workList.add(parent);
      } else {
        TypeNode type = parent.approx.lca_2(this.approx);
        if (type != parent.approx) {
          parent.approx = type;
          workList.add(parent);
        }
      }
    }

    if (this.type != null) {
      this.approx = this.type;
    }
  }

  private void fixInvApprox(TreeSet<TypeVariable> workList) throws TypeException {
    if (rep != this) {
      ecr().fixInvApprox(workList);
      return;
    }

    for (TypeVariable typeVariable : this.children) {
      TypeVariable child = typeVariable.ecr();

      if (child.inv_approx == null) {
        child.inv_approx = this.inv_approx;
        workList.add(child);
      } else {
        TypeNode type = child.inv_approx.gcd_2(this.inv_approx);

        if (type != child.inv_approx) {
          child.inv_approx = type;
          workList.add(child);
        }
      }
    }

    if (this.type != null) {
      this.inv_approx = this.type;
    }
  }

  @Override
  public String toString() {
    if (rep != this) {
      return ecr().toString();
    }

    StringBuilder s = new StringBuilder();
    s.append("[id:").append(id);
    if (type != null) {
      s.append(",type:").append(type);
    }
    s.append(",approx:").append(approx);
    s.append(",inv_approx:").append(inv_approx);

    s.append(",[parents:");
    {
      boolean comma = false;
      for (TypeVariable typeVariable : parents) {
        if (comma) {
          s.append(',');
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
          s.append(',');
        } else {
          comma = true;
        }
        s.append(typeVariable.id());
      }
    }
    s.append("]]");

    return s.toString();
  }

  public void fixParents() {
    if (rep != this) {
      ecr().fixParents();
      return;
    }
    this.parents = Collections.unmodifiableList(new LinkedList<TypeVariable>(new TreeSet<TypeVariable>(parents)));
  }

  public void fixChildren() {
    if (rep != this) {
      ecr().fixChildren();
      return;
    }
    this.children = Collections.unmodifiableList(new LinkedList<TypeVariable>(new TreeSet<TypeVariable>(children)));
  }
}
