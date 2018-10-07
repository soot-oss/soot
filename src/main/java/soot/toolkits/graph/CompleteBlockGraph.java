package soot.toolkits.graph;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1999 Patrice Pominville, Raja Vallee-Rai
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

import soot.Body;
import soot.Trap;

/**
 * <p>
 * Represents a CFG for a {@link Body} instance where the nodes are {@link Block} instances, and where control flow
 * associated with exceptions is taken into account. When dividing the {@link Body} into basic blocks,
 * <code>CompleteBlockGraph</code> assumes that every {@link Unit} covered by a {@link Trap} has the potential to throw an
 * exception caught by the {@link Trap}. This generally has the effect of separating every covered {@link Unit} into a
 * separate block.
 *
 * <p>
 * This implementation of <code>CompleteBlockGraph</code> is included for backwards compatibility, but the graphs it produces
 * are not necessarily identical to the graphs produced by the implementation of <code>CompleteBlockGraph</code> See the
 * documentation for {@link CompleteUnitGraph} for details of the incompatibilities.
 * </p>
 */
public class CompleteBlockGraph extends ExceptionalBlockGraph {
  public CompleteBlockGraph(Body b) {
    super(new CompleteUnitGraph(b));
  }
}
