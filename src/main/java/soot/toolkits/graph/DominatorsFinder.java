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

import java.util.Collection;
import java.util.List;

/**
 * General interface for a dominators analysis.
 *
 * @author Navindra Umanee
 **/
public interface DominatorsFinder<N> {
  /**
   * Returns the graph to which the analysis pertains.
   **/
  public DirectedGraph<N> getGraph();

  /**
   * Returns a list of dominators for the given node in the graph.
   **/
  public List<N> getDominators(N node);

  /**
   * Returns the immediate dominator of node or null if the node has no immediate dominator.
   **/
  public N getImmediateDominator(N node);

  /**
   * True if "node" is dominated by "dominator" in the graph.
   **/
  public boolean isDominatedBy(N node, N dominator);

  /**
   * True if "node" is dominated by all nodes in "dominators" in the graph.
   **/
  public boolean isDominatedByAll(N node, Collection<N> dominators);
}
