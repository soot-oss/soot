package soot.dava.toolkits.base.finders;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Jerome Miecznikowski
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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import soot.toolkits.graph.DirectedGraph;

class SwitchNodeGraph implements DirectedGraph {
  private LinkedList body;
  private final LinkedList heads, tails;
  private final HashMap binding;

  public SwitchNodeGraph(List body) {
    this.body = new LinkedList();
    this.body.addAll(body);

    binding = new HashMap();

    heads = new LinkedList();
    tails = new LinkedList();

    Iterator it = body.iterator();
    while (it.hasNext()) {
      SwitchNode sn = (SwitchNode) it.next();

      binding.put(sn.get_AugStmt().bsuccs.get(0), sn);
      sn.reset();
    }

    it = body.iterator();
    while (it.hasNext()) {
      ((SwitchNode) it.next()).setup_Graph(binding);
    }

    it = body.iterator();
    while (it.hasNext()) {
      SwitchNode sn = (SwitchNode) it.next();

      if (sn.get_Preds().isEmpty()) {
        heads.add(sn);
      }

      if (sn.get_Succs().isEmpty()) {
        tails.add(sn);
      }
    }
  }

  public int size() {
    return body.size();
  }

  public List getHeads() {
    return heads;
  }

  public List getTails() {
    return tails;
  }

  public List getPredsOf(Object o) {
    return ((SwitchNode) o).get_Preds();
  }

  public List getSuccsOf(Object o) {
    return ((SwitchNode) o).get_Succs();
  }

  public Iterator iterator() {
    return body.iterator();
  }

  public List getBody() {
    return body;
  }
}
