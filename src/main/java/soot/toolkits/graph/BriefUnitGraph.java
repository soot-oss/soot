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

import java.util.HashMap;
import java.util.List;

import soot.Body;
import soot.Timers;
import soot.Unit;
import soot.options.Options;

/**
 * Represents a CFG where the nodes are Unit instances, and where no edges are included to account for control flow
 * associated with exceptions.
 *
 * @see Unit
 * @see UnitGraph
 */
public class BriefUnitGraph extends UnitGraph {

  /**
   * Constructs a BriefUnitGraph given a Body instance.
   *
   * @param body
   *          The underlying body we want to make a graph for.
   */
  public BriefUnitGraph(Body body) {
    super(body);
    int size = unitChain.size();

    if (Options.v().time()) {
      Timers.v().graphTimer.start();
    }

    unitToSuccs = new HashMap<Unit, List<Unit>>(size * 2 + 1, 0.7f);
    unitToPreds = new HashMap<Unit, List<Unit>>(size * 2 + 1, 0.7f);
    buildUnexceptionalEdges(unitToSuccs, unitToPreds);

    buildHeadsAndTails();

    if (Options.v().time()) {
      Timers.v().graphTimer.end();
    }

    soot.util.PhaseDumper.v().dumpGraph(this, body);
  }

}
