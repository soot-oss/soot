package soot.jimple.toolkits.ide.icfg;

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

import heros.InterproceduralCFG;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import soot.Value;
import soot.toolkits.graph.DirectedGraph;

/**
 * An {@link InterproceduralCFG} which supports the computation of predecessors.
 */
public interface BiDiInterproceduralCFG<N, M> extends InterproceduralCFG<N, M> {

  public List<N> getPredsOf(N u);

  public Collection<N> getEndPointsOf(M m);

  public List<N> getPredsOfCallAt(N u);

  public Set<N> allNonCallEndNodes();

  // also exposed to some clients who need it
  public DirectedGraph<N> getOrCreateUnitGraph(M body);

  public List<Value> getParameterRefs(M m);

  /**
   * Gets whether the given statement is a return site of at least one call
   * 
   * @param n
   *          The statement to check
   * @return True if the given statement is a return site, otherwise false
   */
  public boolean isReturnSite(N n);

  /**
   * Checks whether the given statement is rachable from the entry point
   * 
   * @param u
   *          The statement to check
   * @return True if there is a control flow path from the entry point of the program to the given statement, otherwise false
   */
  public boolean isReachable(N u);

}
