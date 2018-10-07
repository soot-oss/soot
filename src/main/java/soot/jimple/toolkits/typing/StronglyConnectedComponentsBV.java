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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.util.BitSetIterator;
import soot.util.BitVector;

/**
 * @deprecated use {@link soot.jimple.toolkits.typing.fast.TypeResolver} instead
 */
@Deprecated
class StronglyConnectedComponentsBV {
  private static final Logger logger = LoggerFactory.getLogger(StronglyConnectedComponentsBV.class);
  BitVector variables;
  Set<TypeVariableBV> black;
  LinkedList<TypeVariableBV> finished;

  TypeResolverBV resolver;

  LinkedList<LinkedList<TypeVariableBV>> forest = new LinkedList<LinkedList<TypeVariableBV>>();
  LinkedList<TypeVariableBV> current_tree;

  private static final boolean DEBUG = false;

  public StronglyConnectedComponentsBV(BitVector typeVariableList, TypeResolverBV resolver) throws TypeException {
    this.resolver = resolver;
    variables = typeVariableList;

    black = new TreeSet<TypeVariableBV>();
    finished = new LinkedList<TypeVariableBV>();

    for (BitSetIterator i = variables.iterator(); i.hasNext();) {
      TypeVariableBV var = resolver.typeVariableForId(i.next());

      if (!black.contains(var)) {
        black.add(var);
        dfsg_visit(var);
      }
    }

    black = new TreeSet<TypeVariableBV>();

    for (TypeVariableBV var : finished) {
      if (!black.contains(var)) {
        current_tree = new LinkedList<TypeVariableBV>();
        forest.add(current_tree);
        black.add(var);
        dfsgt_visit(var);
      }
    }

    for (Iterator<LinkedList<TypeVariableBV>> i = forest.iterator(); i.hasNext();) {
      LinkedList<TypeVariableBV> list = i.next();
      TypeVariableBV previous = null;
      StringBuffer s = null;
      if (DEBUG) {
        s = new StringBuffer("scc:\n");
      }

      for (Iterator<TypeVariableBV> j = list.iterator(); j.hasNext();) {
        TypeVariableBV current = j.next();

        if (DEBUG) {
          s.append(" " + current + "\n");
        }

        if (previous == null) {
          previous = current;
        } else {
          try {
            previous = previous.union(current);
          } catch (TypeException e) {
            if (DEBUG) {
              logger.debug("" + s);
            }
            throw e;
          }
        }
      }
    }
  }

  private void dfsg_visit(TypeVariableBV var) {
    BitVector parents = var.parents();

    for (BitSetIterator i = parents.iterator(); i.hasNext();) {
      TypeVariableBV parent = resolver.typeVariableForId(i.next());

      if (!black.contains(parent)) {
        black.add(parent);
        dfsg_visit(parent);
      }
    }

    finished.add(0, var);
  }

  private void dfsgt_visit(TypeVariableBV var) {
    current_tree.add(var);

    BitVector children = var.children();

    for (BitSetIterator i = children.iterator(); i.hasNext();) {
      TypeVariableBV child = resolver.typeVariableForId(i.next());

      if (!black.contains(child)) {
        black.add(child);
        dfsgt_visit(child);
      }
    }
  }
}
