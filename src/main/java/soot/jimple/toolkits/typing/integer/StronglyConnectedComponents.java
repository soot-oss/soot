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

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class StronglyConnectedComponents {
  private static final Logger logger = LoggerFactory.getLogger(StronglyConnectedComponents.class);
  private static final boolean DEBUG = false;

  private final Set<TypeVariable> black = new TreeSet<TypeVariable>();
  private final List<TypeVariable> finished = new LinkedList<TypeVariable>();
  private List<TypeVariable> current_tree = new LinkedList<TypeVariable>();

  public static void merge(List<TypeVariable> typeVariableList) throws TypeException {
    new StronglyConnectedComponents(typeVariableList);
  }

  private StronglyConnectedComponents(List<TypeVariable> typeVariableList) throws TypeException {
    for (TypeVariable var : typeVariableList) {
      if (!black.contains(var)) {
        black.add(var);
        dfsg_visit(var);
      }
    }

    black.clear();
    final List<List<TypeVariable>> forest = new LinkedList<List<TypeVariable>>();

    for (TypeVariable var : finished) {
      if (!black.contains(var)) {
        current_tree = new LinkedList<TypeVariable>();
        forest.add(current_tree);
        black.add(var);
        dfsgt_visit(var);
      }
    }

    for (List<TypeVariable> list : forest) {
      StringBuilder s = DEBUG ? new StringBuilder("scc:\n") : null;
      TypeVariable previous = null;
      for (TypeVariable current : list) {
        if (DEBUG) {
          s.append(' ').append(current).append('\n');
        }

        if (previous == null) {
          previous = current;
        } else {
          try {
            previous = previous.union(current);
          } catch (TypeException e) {
            if (DEBUG) {
              logger.debug(s.toString());
            }
            throw e;
          }
        }
      }
    }
  }

  private void dfsg_visit(TypeVariable var) {
    for (TypeVariable parent : var.parents()) {
      if (!black.contains(parent)) {
        black.add(parent);
        dfsg_visit(parent);
      }
    }
    finished.add(0, var);
  }

  private void dfsgt_visit(TypeVariable var) {
    current_tree.add(var);
    for (TypeVariable child : var.children()) {
      if (!black.contains(child)) {
        black.add(child);
        dfsgt_visit(child);
      }
    }
  }
}
