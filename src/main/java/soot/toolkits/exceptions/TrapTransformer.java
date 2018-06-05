package soot.toolkits.exceptions;

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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import soot.BodyTransformer;
import soot.Unit;
import soot.Value;
import soot.jimple.EnterMonitorStmt;
import soot.jimple.ExitMonitorStmt;
import soot.toolkits.graph.UnitGraph;
import soot.util.HashMultiMap;
import soot.util.MultiMap;

/**
 * Common abstract base class for all body transformers that change the trap list to, e.g., minimize the trap list
 * 
 * @author Steven Arzt
 *
 */
public abstract class TrapTransformer extends BodyTransformer {

  public Set<Unit> getUnitsWithMonitor(UnitGraph ug) {
    // Idea: Associate each unit with a set of monitors held at that
    // statement
    MultiMap<Unit, Value> unitMonitors = new HashMultiMap<>();

    // Start at the heads of the unit graph
    List<Unit> workList = new ArrayList<>();
    Set<Unit> doneSet = new HashSet<>();
    for (Unit head : ug.getHeads()) {
      workList.add(head);
    }

    while (!workList.isEmpty()) {
      Unit curUnit = workList.remove(0);

      boolean hasChanged = false;
      Value exitValue = null;
      if (curUnit instanceof EnterMonitorStmt) {
        // We enter a new monitor
        EnterMonitorStmt ems = (EnterMonitorStmt) curUnit;
        hasChanged = unitMonitors.put(curUnit, ems.getOp());
      } else if (curUnit instanceof ExitMonitorStmt) {
        // We leave a monitor
        ExitMonitorStmt ems = (ExitMonitorStmt) curUnit;
        exitValue = ems.getOp();
      }

      // Copy over the monitors from the predecessors
      for (Unit pred : ug.getPredsOf(curUnit)) {
        for (Value v : unitMonitors.get(pred)) {
          if (v != exitValue) {
            if (unitMonitors.put(curUnit, v)) {
              hasChanged = true;
            }
          }
        }
      }

      // Work on the successors
      if (doneSet.add(curUnit) || hasChanged) {
        workList.addAll(ug.getSuccsOf(curUnit));
      }
    }

    return unitMonitors.keySet();
  }

}
