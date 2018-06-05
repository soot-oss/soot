package soot.jimple.toolkits.base;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import soot.Trap;
import soot.Unit;
import soot.jimple.StmtBody;
import soot.util.Chain;

public class Zonation {
  private int zoneCount;
  private Map<Unit, Zone> unitToZone;

  public Zonation(StmtBody body) {
    Chain<Unit> units = body.getUnits();
    Map<Unit, List<Trap>> unitToTrapBoundaries = new HashMap<Unit, List<Trap>>();

    // Build trap boundaries
    for (Trap t : body.getTraps()) {
      addTrapBoundary(t.getBeginUnit(), t, unitToTrapBoundaries);
      addTrapBoundary(t.getEndUnit(), t, unitToTrapBoundaries);
    }

    // Traverse units, assigning each to a zone
    {
      Map<List<Trap>, Zone> trapListToZone = new HashMap<List<Trap>, Zone>(10, 0.7f);
      List<Trap> currentTraps = new ArrayList<Trap>();
      Zone currentZone;

      zoneCount = 0;
      unitToZone = new HashMap<Unit, Zone>(units.size() * 2 + 1, 0.7f);

      // Initialize first empty zone
      currentZone = new Zone("0");
      trapListToZone.put(new ArrayList<Trap>(), currentZone);

      for (Unit u : units) {
        // Process trap boundaries
        {
          List<Trap> trapBoundaries = unitToTrapBoundaries.get(u);
          if (trapBoundaries != null && !trapBoundaries.isEmpty()) {
            for (Trap trap : trapBoundaries) {
              if (currentTraps.contains(trap)) {
                currentTraps.remove(trap);
              } else {
                currentTraps.add(trap);
              }
            }

            if (trapListToZone.containsKey(currentTraps)) {
              currentZone = trapListToZone.get(currentTraps);
            } else {
              // Create a new zone
              zoneCount++;
              currentZone = new Zone(new Integer(zoneCount).toString());

              trapListToZone.put(currentTraps, currentZone);
            }

          }
        }

        unitToZone.put(u, currentZone);
      }
    }

  }

  private void addTrapBoundary(Unit unit, Trap t, Map<Unit, List<Trap>> unitToTrapBoundaries) {
    List<Trap> boundary = unitToTrapBoundaries.get(unit);
    if (boundary == null) {
      boundary = new ArrayList<Trap>();
      unitToTrapBoundaries.put(unit, boundary);
    }
    boundary.add(t);
  }

  public Zone getZoneOf(Unit u) {
    Zone z = unitToZone.get(u);

    if (z == null) {
      throw new RuntimeException("null zone!");
    }

    return z;
  }

  public int getZoneCount() {
    return zoneCount;
  }
}
