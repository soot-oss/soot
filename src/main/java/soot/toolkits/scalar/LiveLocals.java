package soot.toolkits.scalar;

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

import java.util.List;

import soot.Local;
import soot.Unit;
import soot.toolkits.graph.UnitGraph;

/**
 * Provides an interface for querying for the list of Locals that are live before an after a given unit in a method.
 */
public interface LiveLocals {
  static final public class Factory {
    private Factory() {
    }

    public static LiveLocals newLiveLocals(UnitGraph graph) {
      return new SimpleLiveLocals(graph);
    }
  }

  /**
   * Returns the list of Locals that are live before the specified Unit.
   * 
   * @param s
   *          the Unit that defines this query.
   * @return a list of Locals that are live before the specified unit in the method.
   */
  public List<Local> getLiveLocalsBefore(Unit s);

  /**
   * Returns the list of Locals that are live after the specified Unit.
   * 
   * @param s
   *          the Unit that defines this query.
   * @return a list of Locals that are live after the specified unit in the method.
   */
  public List<Local> getLiveLocalsAfter(Unit s);
}
