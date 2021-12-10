package soot.jimple.toolkits.thread.mhp;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vallée-Rai and others
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
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import soot.toolkits.graph.DirectedGraph;
import soot.util.FastStack;

// *** USE AT YOUR OWN RISK ***
// May Happen in Parallel (MHP) analysis by Lin Li.
// This code should be treated as beta-quality code.
// It was written in 2003, but not incorporated into Soot until 2006.
// As such, it may contain incorrect assumptions about the usage
// of certain Soot classes.
// Some portions of this MHP analysis have been quality-checked, and are
// now used by the Transactions toolkit.
//
// -Richard L. Halpert, 2006-11-30

public class LoopBodyFinder {

  private final FastStack<Object> stack = new FastStack<Object>();
  private final Set<Set<Object>> loops = new HashSet<Set<Object>>();

  LoopBodyFinder(Map<Object, Object> backEdges, DirectedGraph g) {
    findLoopBody(backEdges, g);
  }

  private void findLoopBody(Map<Object, Object> backEdges, DirectedGraph g) {
    Set maps = backEdges.entrySet();
    for (Iterator iter = maps.iterator(); iter.hasNext();) {
      Map.Entry entry = (Map.Entry) iter.next();
      Object tail = entry.getKey();
      // Tag tag = (Tag)key.getTags().get(0);
      // System.out.println("---key= "+tag+" "+key);
      Object head = entry.getValue();
      Set<Object> loopBody = finder(tail, head, g);
      loops.add(loopBody);
    }

  }

  private Set<Object> finder(Object tail, Object head, DirectedGraph g) {
    Set<Object> loop = new HashSet<Object>();
    loop.add(head);
    insert(tail, loop);
    while (!stack.empty()) {
      Object p = stack.pop();
      Iterator predsListIt = g.getPredsOf(p).iterator();
      while (predsListIt.hasNext()) {
        Object pred = predsListIt.next();
        insert(pred, loop);
      }
    }
    return loop;
  }

  private void insert(Object m, Set<Object> loop) {
    if (!loop.contains(m)) {
      loop.add(m);
      stack.push(m);
    }
  }

  public Set<Set<Object>> getLoopBody() {
    return loops;
  }
}
