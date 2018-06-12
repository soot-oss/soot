package soot.toolkits.graph;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Navindra Umanee <navindra@cs.mcgill.ca>
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

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a dominator node in DominatorTree. Usually you should use DominatorTree or DominanceFrontier to obtain
 * information on how a node relates to other nodes instead of directly using any methods provided here.
 *
 * @author Navindra Umanee
 **/
public class DominatorNode<N> {
  protected N gode;
  protected DominatorNode<N> parent;
  protected List<DominatorNode<N>> children;

  protected DominatorNode(N gode) {
    this.gode = gode;
    children = new ArrayList<DominatorNode<N>>();
  }

  /**
   * Sets the parent of this node in the DominatorTree. Usually called internally.
   **/
  public void setParent(DominatorNode<N> parent) {
    this.parent = parent;
  }

  /**
   * Adds a child to the internal list of children of this node in tree. Usually called internally.
   **/
  public boolean addChild(DominatorNode<N> child) {
    if (children.contains(child)) {
      return false;
    } else {
      children.add(child);
      return true;
    }
  }

  /**
   * Returns the node (from the original DirectedGraph) encapsulated by this DominatorNode.
   **/
  public N getGode() {
    return gode;
  }

  /**
   * Returns the parent of the node in the DominatorTree.
   **/
  public DominatorNode<N> getParent() {
    return parent;
  }

  /**
   * Returns a backed list of the children of this node in the DominatorTree.
   **/
  public List<DominatorNode<N>> getChildren() {
    return children;
  }

  /**
   * Returns true if this node is the head of its DominatorTree.
   **/
  public boolean isHead() {
    return parent == null;
  }

  /**
   * Returns true if this node is a tail of its DominatorTree.
   **/
  public boolean isTail() {
    return children.isEmpty();
  }

  public String toString() {
    // *** FIXME: Print info about parent and children
    return gode.toString();
  }
}
