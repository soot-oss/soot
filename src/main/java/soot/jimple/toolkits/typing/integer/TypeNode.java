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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Type;

/**
 * Each instance of this class represents one basic type.
 **/
class TypeNode {
  private static final Logger logger = LoggerFactory.getLogger(TypeNode.class);
  public static final boolean DEBUG = false;

  private final int id;
  private final Type type;

  public TypeNode(int id, Type type) {
    this.id = id;
    this.type = type;

    if (DEBUG) {
      logger.debug("creating node " + this);
    }
  }

  /** Returns the unique id of this type node. **/
  public int id() {
    return id;
  }

  /** Returns the type represented by this type node. **/
  public Type type() {
    return type;
  }

  public boolean hasAncestor_1(TypeNode typeNode) {
    if (typeNode == this) {
      return true;
    }

    return ClassHierarchy.v().hasAncestor_1(id, typeNode.id);
  }

  public boolean hasAncestor_2(TypeNode typeNode) {
    if (typeNode == this) {
      return true;
    }

    return ClassHierarchy.v().hasAncestor_2(id, typeNode.id);
  }

  /*
   * public boolean hasDescendant_1(TypeNode typeNode) { return ClassHierarchy.v().hasDescendant_1(id, typeNode.id); }
   * 
   * public boolean hasDescendant_2(TypeNode typeNode) { return ClassHierarchy.v().hasDescendant_2(id, typeNode.id); }
   * 
   * public boolean hasDescendantOrSelf_1(TypeNode typeNode) { if(typeNode == this) return true;
   * 
   * return hasDescendant_1(typeNode); }
   * 
   * public boolean hasDescendantOrSelf_2(TypeNode typeNode) { if(typeNode == this) return true;
   * 
   * return hasDescendant_2(typeNode); }
   */

  public TypeNode lca_1(TypeNode typeNode) {
    return ClassHierarchy.v().lca_1(id, typeNode.id);
  }

  public TypeNode lca_2(TypeNode typeNode) {
    return ClassHierarchy.v().lca_2(id, typeNode.id);
  }

  public TypeNode gcd_1(TypeNode typeNode) {
    return ClassHierarchy.v().gcd_1(id, typeNode.id);
  }

  public TypeNode gcd_2(TypeNode typeNode) {
    return ClassHierarchy.v().gcd_2(id, typeNode.id);
  }

  public String toString() {
    if (type != null) {
      return type + "(" + id + ")";
    }

    if (this == ClassHierarchy.v().TOP) {
      return "TOP" + "(" + id + ")";
    }

    if (this == ClassHierarchy.v().R0_1) {
      return "R0_1" + "(" + id + ")";
    }

    if (this == ClassHierarchy.v().R0_127) {
      return "R0_127" + "(" + id + ")";
    }

    if (this == ClassHierarchy.v().R0_32767) {
      return "R0_32767" + "(" + id + ")";
    }

    return "ERROR!!!!";
  }
}
