package soot.toolkits.graph;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2004 Navindra Umanee <navindra@cs.mcgill.ca>
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
import java.util.Iterator;
import java.util.List;

/**
 * This adapter provides a DirectedGraph interface to DominatorTree.
 *
 * <p>
 *
 * This might be useful if e.g. you want to apply a DirectedGraph analysis such as the PseudoTopologicalOrderer to a
 * DominatorTree.
 *
 * @author Navindra Umanee
 **/
public class DominatorTreeAdapter<N> implements DirectedGraph<DominatorNode<N>> {
  DominatorTree<N> dt;

  public DominatorTreeAdapter(DominatorTree<N> dt) {
    this.dt = dt;
  }

  public List<DominatorNode<N>> getHeads() {
    return dt.getHeads();
  }

  public List<DominatorNode<N>> getTails() {
    return dt.getTails();
  }

  public List<DominatorNode<N>> getPredsOf(DominatorNode<N> node) {
    return Collections.singletonList(dt.getParentOf(node));
  }

  public List<DominatorNode<N>> getSuccsOf(DominatorNode<N> node) {
    return dt.getChildrenOf(node);
  }

  public Iterator<DominatorNode<N>> iterator() {
    return dt.iterator();
  }

  public int size() {
    return dt.size();
  }
}
