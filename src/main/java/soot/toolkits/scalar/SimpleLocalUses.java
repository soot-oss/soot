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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Body;
import soot.Local;
import soot.Timers;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.options.Options;
import soot.toolkits.graph.UnitGraph;

/**
 * Analysis that implements the LocalUses interface. Uses for a Local defined at a given Unit are returned as a list of
 * UnitValueBoxPairs each containing a Unit that use the local and the Local itself wrapped in a ValueBox.
 */
public class SimpleLocalUses implements LocalUses {
  private static final Logger logger = LoggerFactory.getLogger(SimpleLocalUses.class);
  final Body body;
  private Map<Unit, List<UnitValueBoxPair>> unitToUses;

  /**
   * Construct the analysis from a UnitGraph representation of a method body and a LocalDefs interface. This supposes that a
   * LocalDefs analysis must have been computed prior.
   *
   * <p>
   * Note: If you do not already have a UnitGraph, it may be cheaper to use the constructor which only requires a Body.
   */
  public SimpleLocalUses(UnitGraph graph, LocalDefs localDefs) {
    this(graph.getBody(), localDefs);
  }

  /**
   * Construct the analysis from a method body and a LocalDefs interface. This supposes that a LocalDefs analysis must have
   * been computed prior.
   */
  public SimpleLocalUses(Body body, LocalDefs localDefs) {
    this.body = body;
    final Options options = Options.v();
    if (options.time()) {
      Timers.v().usesTimer.start();
      Timers.v().usePhase1Timer.start();
    }

    if (options.verbose()) {
      logger.debug("[" + body.getMethod().getName() + "]     Constructing SimpleLocalUses...");
    }

    unitToUses = new HashMap<Unit, List<UnitValueBoxPair>>(body.getUnits().size() * 2 + 1, 0.7f);

    // Initialize this map to empty sets

    if (options.time()) {
      Timers.v().usePhase1Timer.end();
      Timers.v().usePhase2Timer.start();
    }

    // Traverse units and associate uses with definitions
    for (Unit unit : body.getUnits()) {
      for (ValueBox useBox : unit.getUseBoxes()) {
        Value v = useBox.getValue();
        if (v instanceof Local) {
          // Add this statement to the uses of the definition of the local
          Local l = (Local) v;

          UnitValueBoxPair newPair = new UnitValueBoxPair(unit, useBox);

          List<Unit> defs = localDefs.getDefsOfAt(l, unit);
          if (defs != null) {
            for (Unit def : defs) {
              List<UnitValueBoxPair> lst = unitToUses.get(def);
              if (lst == null) {
                unitToUses.put(def, lst = new ArrayList<UnitValueBoxPair>());
              }
              lst.add(newPair);
            }
          }
        }
      }
    }

    if (options.time()) {
      Timers.v().usePhase2Timer.end();
      Timers.v().usesTimer.end();
    }

    if (options.verbose()) {
      logger.debug("[" + body.getMethod().getName() + "]     finished SimpleLocalUses...");
    }
  }

  /**
   * Uses for a Local defined at a given Unit are returned as a list of UnitValueBoxPairs each containing a Unit that use the
   * local and the Local itself wrapped in a ValueBox.
   *
   * @param s
   *          a unit that we want to query for the uses of the Local it (may) define.
   * @return a UnitValueBoxPair of the Units that use the Local.
   */
  @Override
  public List<UnitValueBoxPair> getUsesOf(Unit s) {
    List<UnitValueBoxPair> l = unitToUses.get(s);
    if (l == null) {
      return Collections.emptyList();
    }

    return Collections.unmodifiableList(l);
  }

  /**
   * Gets all variables that are used in this body
   *
   * @return The list of variables used in this body
   */
  public Set<Local> getUsedVariables() {
    Set<Local> res = new HashSet<Local>();
    for (List<UnitValueBoxPair> vals : unitToUses.values()) {
      for (UnitValueBoxPair val : vals) {
        res.add((Local) val.valueBox.getValue());
      }
    }
    return res;
  }

  /**
   * Gets all variables that are not used in this body
   *
   * @return The list of variables declared, but not used in this body
   */
  public Set<Local> getUnusedVariables() {
    Set<Local> res = new HashSet<Local>(body.getLocals());
    res.retainAll(getUsedVariables());
    return res;
  }

}
