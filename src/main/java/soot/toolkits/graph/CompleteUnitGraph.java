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
import soot.toolkits.exceptions.PedanticThrowAnalysis;

/**
 * <p>
 * Represents a CFG for a {@link Body} instance where the nodes are {@link soot.Unit} instances, and where control flow
 * associated with exceptions is taken into account. In a <code>CompleteUnitGraph</code>, every <code>Unit</code> covered by
 * a {@link soot.Trap} is considered to have the potential to throw an exception caught by the <code>Trap</code>, so there
 * are edges to the <code>Trap</code>'s handler from every trapped <code>Unit</code> , as well as from all the predecessors
 * of the trapped <code>Unit</code>s.
 *
 * <p>
 * This implementation of <code>CompleteUnitGraph</code> is included for backwards compatibility (new code should use
 * {@link ExceptionalUnitGraph}), but the graphs it produces are not necessarily identical to the graphs produced by the
 * implementation of <code>CompleteUnitGraph</code> provided by versions of Soot up to and including release 2.1.0. The known
 * differences include:
 *
 * <ul>
 *
 * <li>If a <code>Body</code> includes <code>Unit</code>s which branch into the middle of the region protected by a
 * <code>Trap</code> this implementation of <code>CompleteUnitGraph</code> will include edges from those branching
 * <code>Unit</code>s to the <code>Trap</code>'s handler (since the branches are predecessors of an instruction which may
 * throw an exception caught by the <code>Trap</code>). The 2.1.0 implementation of <code>CompleteUnitGraph</code> mistakenly
 * omitted these edges.</li>
 *
 * <li>If the initial <code>Unit</code> in the <code>Body</code> might throw an exception caught by a <code>Trap</code>
 * within the body, this implementation will include the initial handler <code>Unit</code> in the list returned by
 * <code>getHeads()</code> (since the handler unit might be the first Unit in the method to execute to completion). The 2.1.0
 * implementation of <code>CompleteUnitGraph</code> mistakenly omitted the handler from the set of heads.</li>
 *
 * </ul>
 * </p>
 */
public class CompleteUnitGraph extends ExceptionalUnitGraph {
  public CompleteUnitGraph(Body b) {
    super(b, PedanticThrowAnalysis.v(), false);
  }
}
