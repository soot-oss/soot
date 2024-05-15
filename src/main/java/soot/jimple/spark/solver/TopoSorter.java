package soot.jimple.spark.solver;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2002 Ondrej Lhotak
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

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import soot.jimple.spark.pag.Node;
import soot.jimple.spark.pag.PAG;
import soot.jimple.spark.pag.VarNode;

/**
 * Performs a pseudo-topological sort on the VarNodes in a PAG.
 * 
 * @author Ondrej Lhotak
 */

public class TopoSorter {
  /** Actually perform the topological sort on the PAG. */
  public void sort() {
    for (VarNode v : pag.getVarNodeNumberer()) {
      dfsVisit(v);
    }
    visited = null;
  }

  public TopoSorter(PAG pag, boolean ignoreTypes) {
    this.pag = pag;
    this.ignoreTypes = ignoreTypes;
    // this.visited = new NumberedSet( pag.getVarNodeNumberer() );
    this.visited = new HashSet<VarNode>();
  }

  /* End of public methods. */
  /* End of package methods. */

  protected boolean ignoreTypes;
  protected PAG pag;
  protected int nextFinishNumber = 1;
  protected HashSet<VarNode> visited;

  protected void dfsVisit(VarNode n) {
    if (!visited.add(n)) {
      return;
    }

    Stack<VarNode> stack = new Stack<>();
    Set<VarNode> visitedSuccessors = new HashSet<>();
    stack.add(n);

    while (!stack.isEmpty()) {
      VarNode s = stack.peek();

      if (visitedSuccessors.add(s)) {
        Node[] succs = pag.simpleLookup(s);
        for (Node element : succs) {
          if (ignoreTypes || pag.getTypeManager().castNeverFails(n.getType(), element.getType())) {
            if (visited.add((VarNode) element)) {
              stack.push((VarNode) element);
            }
          }
        }

      } else {
        stack.pop();
        s.setFinishingNumber(nextFinishNumber++);
      }
    }
  }
}
