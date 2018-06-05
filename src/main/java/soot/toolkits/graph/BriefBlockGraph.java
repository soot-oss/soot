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
import soot.Unit;

/**
 * <p>
 * Represents a CFG for a {@link Body} where the nodes are {@link Block}s and edges are derived from control flow. Control
 * flow associated with exceptions is ignored, so the graph will be a forest where each exception handler constitutes a
 * disjoint subgraph.
 * </p>
 */
public class BriefBlockGraph extends BlockGraph {
  /**
   * Constructs a {@link BriefBlockGraph} from a given {@link Body}.
   *
   * <p>
   * Note that this constructor builds a {@link BriefUnitGraph} internally when splitting <tt>body</tt>'s {@link Unit}s into
   * {@link Block}s. Callers who already have a {@link BriefUnitGraph} to hand can use the constructor taking a
   * <tt>CompleteUnitGraph</tt> as a parameter, as a minor optimization.
   *
   * @param body
   *          the {@link Body} for which to build a graph.
   */
  public BriefBlockGraph(Body body) {
    this(new BriefUnitGraph(body));
  }

  /**
   * Constructs a {@link BriefBlockGraph} representing the <tt>Unit</tt>-level control flow represented by the passed
   * {@link BriefUnitGraph}.
   *
   * @param unitGraph
   *          the {@link Body} for which to build a graph.
   */
  public BriefBlockGraph(BriefUnitGraph unitGraph) {
    super(unitGraph);

    soot.util.PhaseDumper.v().dumpGraph(this, mBody);
  }
}
