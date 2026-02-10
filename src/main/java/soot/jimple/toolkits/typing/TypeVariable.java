package soot.jimple.toolkits.typing;

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

import soot.ArrayType;
import soot.RefType;
import soot.options.Options;
import soot.util.BitVector;

/** Represents a type variable. **/
class TypeVariable implements Comparable<Object> {
  private static final Logger logger = LoggerFactory.getLogger(TypeVariable.class);
  private static final boolean DEBUG = false;

  private final int id;
  private final TypeResolver resolver;

  private TypeVariable rep = this;
  private int rank = 0;

  private TypeNode approx;

  private TypeNode type;
  private TypeVariable array;
  private TypeVariable element;
  private int depth;

  private List<TypeVariable> parents = Collections.emptyList();
  private List<TypeVariable> children = Collections.emptyList();
  private BitVector ancestors;
  private BitVector indirectAncestors;

  public TypeVariable(int id, TypeResolver resolver) {
    this.id = id;
    this.resolver = resolver;
  }

  public TypeVariable(int id, TypeResolver resolver, TypeNode type) {
    this.id = id;
    this.resolver = resolver;
    this.type = type;
    this.approx = type;

    for (TypeNode parent : type.parents()) {
      addParent(resolver.typeVariable(parent));
    }

    if (type.hasElement()) {
      this.element = resolver.typeVariable(type.element());
      this.element.array = this;
    }
  }

  @Override
  public int hashCode() {
    return (rep != this) ? ecr().hashCode() : id;
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
    } else {
      this.rep = y;
      if (this.rank == y.rank) {
        y.rank++;
      }

      y.merge(this);
      clear();

      return y;
    }
  }

  private void clear() {
    this.approx = null;
    this.type = null;
    this.element = null;
    this.array = null;
    this.parents = null;
    this.children = null;
    this.ancestors = null;
    this.indirectAncestors = null;
  }

  private void merge(TypeVariable var) throws TypeException {
    if (this.depth != 0 || var.depth != 0) {
      throw new InternalTypingException();
    }

    // Merge types
    if (this.type == null) {
      this.type = var.type;
    } else if (var.type != null) {
      error("Type Error(1): Attempt to merge two types.");
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

  void validate() throws TypeException {
    if (this.rep != this) {
      ecr().validate();
      return;
    }

    // Validate relations.
    final TypeNode thisType = this.type;
    if (thisType != null) {
      for (TypeVariable typeVariable : this.parents) {
        TypeNode parentType = typeVariable.ecr().type;
        if (parentType != null) {
          if (!thisType.hasAncestor(parentType)) {
            if (DEBUG) {
              logger.debug(parentType + " is not a parent of " + thisType);
            }
            error("Type Error(2): Parent type is not a valid ancestor.");
          }
        }
      }

      for (TypeVariable typeVariable : this.children) {
        TypeVariable child = typeVariable.ecr();
        TypeNode childType = child.type;
        if (childType != null) {
          if (!thisType.hasDescendant(childType)) {
            if (DEBUG) {
              logger.debug(childType + "(" + child + ") is not a child of " + thisType + "(" + this + ")");
            }
            error("Type Error(3): Child type is not a valid descendant.");
          }
        }
      }
    }
  }

  public void removeIndirectRelations() {
    if (this.rep != this) {
      ecr().removeIndirectRelations();
      return;
    }

    if (this.indirectAncestors == null) {
      fixAncestors();
    }

    List<TypeVariable> parentsToRemove = new LinkedList<TypeVariable>();
    for (TypeVariable parent : this.parents) {
      if (this.indirectAncestors.get(parent.id())) {
        parentsToRemove.add(parent);
      }
    }
    for (TypeVariable parent : parentsToRemove) {
      removeParent(parent);
    }
  }

  private void fixAncestors() {
    BitVector ancestors = new BitVector(0);
    BitVector indirectAncestors = new BitVector(0);
    for (TypeVariable typeVariable : this.parents) {
      TypeVariable parent = typeVariable.ecr();

      if (parent.ancestors == null) {
        parent.fixAncestors();
      }

      ancestors.set(parent.id);
      ancestors.or(parent.ancestors);
      indirectAncestors.or(parent.ancestors);
    }

    this.ancestors = ancestors;
    this.indirectAncestors = indirectAncestors;
  }

  public int id() {
    return (rep != this) ? ecr().id() : id;
  }

  public void addParent(TypeVariable variable) {
    if (this.rep != this) {
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
    if (this.rep != this) {
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
    if (this.rep != this) {
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
    if (this.rep != this) {
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

  public int depth() {
    return (rep != this) ? ecr().depth() : depth;
  }

  public void makeElement() {
    if (rep != this) {
      ecr().makeElement();
      return;
    }

    if (element == null) {
      element = resolver.typeVariable();
      element.array = this;
    }
  }

  public TypeVariable element() {
    if (rep != this) {
      return ecr().element();
    } else {
      return (element == null) ? null : element.ecr();
    }
  }

  public TypeVariable array() {
    if (rep != this) {
      return ecr().array();
    } else {
      return (array == null) ? null : array.ecr();
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

  private void fixApprox(TreeSet<TypeVariable> workList) throws TypeException {
    if (rep != this) {
      ecr().fixApprox(workList);
      return;
    }

    if (type == null && approx != resolver.hierarchy().NULL) {
      TypeVariable element = element();

      if (element != null) {
        if (!approx.hasElement()) {
          logger.debug("*** " + this + " ***");
          error("Type Error(4)");
        }

        TypeNode temp = approx.element();

        if (element.approx == null) {
          element.approx = temp;
          workList.add(element);
        } else {
          TypeNode type = element.approx.lca(temp);

          if (type != element.approx) {
            element.approx = type;
            workList.add(element);
          } else if (element.approx != resolver.hierarchy().INT) {
            type = approx.lca(element.approx.array());

            if (type != approx) {
              approx = type;
              workList.add(this);
            }
          }
        }
      }

      TypeVariable array = array();

      if (array != null && approx != resolver.hierarchy().NULL && approx != resolver.hierarchy().INT) {
        TypeNode temp = approx.array();

        if (array.approx == null) {
          array.approx = temp;
          workList.add(array);
        } else {
          TypeNode type = array.approx.lca(temp);

          if (type != array.approx) {
            array.approx = type;
            workList.add(array);
          } else {
            type = approx.lca(array.approx.element());

            if (type != approx) {
              approx = type;
              workList.add(this);
            }
          }
        }
      }
    }

    for (TypeVariable typeVariable : parents) {
      TypeVariable parent = typeVariable.ecr();

      if (parent.approx == null) {
        parent.approx = approx;
        workList.add(parent);
      } else {
        TypeNode type = parent.approx.lca(approx);

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

  public void fixDepth() throws TypeException {
    if (rep != this) {
      ecr().fixDepth();
      return;
    }

    if (type != null) {
      if (type.type() instanceof ArrayType) {
        ArrayType at = (ArrayType) type.type();
        depth = at.numDimensions;
      } else {
        depth = 0;
      }
    } else {
      if (approx.type() instanceof ArrayType) {
        ArrayType at = (ArrayType) approx.type();
        depth = at.numDimensions;
      } else {
        depth = 0;
      }
    }

    // make sure array types have element type
    if (depth == 0 && element() != null) {
      error("Type Error(11)");
    } else if (depth > 0 && element() == null) {
      makeElement();
      TypeVariable element = element();
      element.depth = depth - 1;

      while (element.depth != 0) {
        element.makeElement();
        element.element().depth = element.depth - 1;
        element = element.element();
      }
    }
  }

  public void propagate() {
    if (rep != this) {
      ecr().propagate();
    }

    if (depth == 0) {
      return;
    }

    for (TypeVariable typeVariable : parents) {
      TypeVariable var = typeVariable.ecr();
      int varDepth = var.depth();
      if (varDepth == depth) {
        element().addParent(var.element());
      } else if (varDepth == 0) {
        if (var.type() == null) {
          // hack for J2ME library, reported by Stephen Cheng
          if (!Options.v().j2me()) {
            var.addChild(resolver.typeVariable(resolver.hierarchy().CLONEABLE));
            var.addChild(resolver.typeVariable(resolver.hierarchy().SERIALIZABLE));
          }
        }
      } else {
        if (var.type() == null) {
          // hack for J2ME library, reported by Stephen Cheng
          if (!Options.v().j2me()) {
            var.addChild(resolver.typeVariable(ArrayType.v(RefType.v("java.lang.Cloneable"), varDepth)));
            var.addChild(resolver.typeVariable(ArrayType.v(RefType.v("java.io.Serializable"), varDepth)));
          }
        }
      }
    }

    for (TypeVariable var : parents) {
      removeParent(var);
    }
  }

  @Override
  public String toString() {
    if (rep != this) {
      return ecr().toString();
    }

    StringBuilder s = new StringBuilder();

    s.append("[id:").append(id).append(",depth:").append(depth);
    if (type != null) {
      s.append(",type:").append(type);
    }
    s.append(",approx:").append(approx);

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
    s.append(']');

    if (element != null) {
      s.append(",arrayof:").append(element.id());
    }
    s.append(']');

    return s.toString();
  }

  public void fixParents() {
    if (rep != this) {
      ecr().fixParents();
      return;
    }
    parents = Collections.unmodifiableList(new LinkedList<TypeVariable>(new TreeSet<TypeVariable>(parents)));
  }

  public void fixChildren() {
    if (rep != this) {
      ecr().fixChildren();
      return;
    }
    children = Collections.unmodifiableList(new LinkedList<TypeVariable>(new TreeSet<TypeVariable>(children)));
  }
}
