package soot.toolkits.graph;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2005 Navindra Umanee <navindra@cs.mcgill.ca>
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

/**
 * DirectedGraph which can be reversed and re-reversed.
 *
 * @author Navindra Umanee
 **/
public interface ReversibleGraph<N> extends MutableDirectedGraph<N> {
  /**
   * Returns true if the graph is now reversed from its original state at creation.
   **/
  public boolean isReversed();

  /**
   * Reverse the edges of the current graph and swap head and tail nodes. Returns self.
   **/
  public ReversibleGraph<N> reverse();
}
