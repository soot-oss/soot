package soot.toolkits.graph;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 1999 Raja Vallee-Rai
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
 * Convenience class which returns a PseudoTopologicalOrderer with the mReversed flag set by default.
 * 
 * @deprecated use {@link PseudoTopologicalOrderer#newList(DirectedGraph, boolean)} instead
 */
@Deprecated
public class ReversePseudoTopologicalOrderer<N> extends PseudoTopologicalOrderer<N> {
  /**
   * Constructs a PseudoTopologicalOrderer with the mReversed flag set.
   * 
   * @deprecated use {@link PseudoTopologicalOrderer#newList(DirectedGraph, boolean)} instead
   */
  public ReversePseudoTopologicalOrderer() {
    super();
    setReverseOrder(true);
  }
}
