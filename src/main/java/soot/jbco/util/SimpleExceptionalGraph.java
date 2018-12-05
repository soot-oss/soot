package soot.jbco.util;

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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import soot.Body;
import soot.Trap;
import soot.Unit;
import soot.toolkits.graph.TrapUnitGraph;

/**
 * @author Michael Batchelder
 * 
 *         Created on 15-Jun-2006
 */
public class SimpleExceptionalGraph extends TrapUnitGraph {

  /**
   * @param body
   */
  public SimpleExceptionalGraph(Body body) {
    super(body);
    int size = unitChain.size();

    unitToSuccs = new HashMap<Unit, List<Unit>>(size * 2 + 1, 0.7f);
    unitToPreds = new HashMap<Unit, List<Unit>>(size * 2 + 1, 0.7f);
    buildUnexceptionalEdges(unitToSuccs, unitToPreds);
    buildSimpleExceptionalEdges(unitToSuccs, unitToPreds);

    buildHeadsAndTails();
  }

  protected void buildSimpleExceptionalEdges(Map unitToSuccs, Map unitToPreds) {
    for (Iterator<Trap> trapIt = body.getTraps().iterator(); trapIt.hasNext();) {
      Trap trap = trapIt.next();

      Unit handler = trap.getHandlerUnit();
      for (Iterator predIt = ((List) unitToPreds.get(trap.getBeginUnit())).iterator(); predIt.hasNext();) {
        Unit pred = (Unit) predIt.next();
        addEdge(unitToSuccs, unitToPreds, pred, handler);
      }
    }
  }
}
