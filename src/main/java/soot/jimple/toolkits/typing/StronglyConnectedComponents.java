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
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class StronglyConnectedComponents {
  private static final Logger logger = LoggerFactory.getLogger(StronglyConnectedComponents.class);
  List<TypeVariable> variables;
  Set<TypeVariable> black;
  List<TypeVariable> finished;

  List<List<TypeVariable>> forest = new LinkedList<List<TypeVariable>>();
  List<TypeVariable> current_tree;

  private static final boolean DEBUG = false;

  public static void merge(List<TypeVariable> typeVariableList) throws TypeException {
    new StronglyConnectedComponents(typeVariableList);
  }

  private StronglyConnectedComponents(List<TypeVariable> typeVariableList) throws TypeException {
    variables = typeVariableList;

    black = new TreeSet<TypeVariable>();
    finished = new LinkedList<TypeVariable>();

    for (TypeVariable var : variables) {
      if (!black.add(var)) {
        dfsg_visit(var);
      }
    }

    black = new TreeSet<TypeVariable>();

    for (TypeVariable var : finished) {
      if (!black.add(var)) {
        current_tree = new LinkedList<TypeVariable>();
        forest.add(current_tree);
        dfsgt_visit(var);
      }
    }

    for (Iterator<List<TypeVariable>> i = forest.iterator(); i.hasNext();) {
      List<TypeVariable> list = i.next();
      TypeVariable previous = null;
      StringBuffer s = null;
      if (DEBUG) {
        s = new StringBuffer("scc:\n");
      }

      for (Iterator<TypeVariable> j = list.iterator(); j.hasNext();) {
        TypeVariable current = j.next();

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

  private void dfsg_visit(TypeVariable var) {
    List<TypeVariable> parents = var.parents();

    for (TypeVariable parent : parents) {
      if (!black.add(parent)) {
        dfsg_visit(parent);
      }
    }

    finished.add(0, var);
  }

  private void dfsgt_visit(TypeVariable var) {
    current_tree.add(var);

    List<TypeVariable> children = var.children();

    for (TypeVariable child : children) {
      if (!black.add(child)) {
        dfsgt_visit(child);
      }
    }
  }
}
