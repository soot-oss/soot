package soot.toolkits.exceptions;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 John Jorgensen
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

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Body;
import soot.BodyTransformer;
import soot.Scene;
import soot.Singletons;
import soot.Trap;
import soot.Unit;
import soot.jimple.toolkits.scalar.UnreachableCodeEliminator;
import soot.options.Options;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.ExceptionalUnitGraph.ExceptionDest;
import soot.util.Chain;

/**
 * A {@link BodyTransformer} that shrinks the protected area covered by each {@link Trap} in the {@link Body} so that it
 * begins at the first of the {@link Body}'s {@link Unit}s which might throw an exception caught by the {@link Trap} and ends
 * just after the last {@link Unit} which might throw an exception caught by the {@link Trap}. In the case where none of the
 * {@link Unit}s protected by a {@link Trap} can throw the exception it catches, the {@link Trap}'s protected area is left
 * completely empty, which will likely cause the {@link UnreachableCodeEliminator} to remove the {@link Trap} completely.
 *
 * The {@link TrapTightener} is used to reduce the risk of unverifiable code which can result from the use of
 * {@link ExceptionalUnitGraph}s from which unrealizable exceptional control flow edges have been removed.
 */

public final class TrapTightener extends TrapTransformer {
  private static final Logger logger = LoggerFactory.getLogger(TrapTightener.class);

  protected ThrowAnalysis throwAnalysis = null;

  public TrapTightener(Singletons.Global g) {
  }

  public static TrapTightener v() {
    return soot.G.v().soot_toolkits_exceptions_TrapTightener();
  }

  public TrapTightener(ThrowAnalysis ta) {
    this.throwAnalysis = ta;
  }

  protected void internalTransform(Body body, String phaseName, Map<String, String> options) {
    if (this.throwAnalysis == null) {
      this.throwAnalysis = Scene.v().getDefaultThrowAnalysis();
    }

    if (Options.v().verbose()) {
      logger.debug("[" + body.getMethod().getName() + "] Tightening trap boundaries...");
    }

    Chain<Trap> trapChain = body.getTraps();
    Chain<Unit> unitChain = body.getUnits();
    if (trapChain.size() > 0) {
      ExceptionalUnitGraph graph = new ExceptionalUnitGraph(body, throwAnalysis);
      Set<Unit> unitsWithMonitor = getUnitsWithMonitor(graph);

      for (Iterator<Trap> trapIt = trapChain.iterator(); trapIt.hasNext();) {
        Trap trap = trapIt.next();
        boolean isCatchAll = trap.getException().getName().equals("java.lang.Throwable");
        Unit firstTrappedUnit = trap.getBeginUnit();
        Unit firstTrappedThrower = null;
        Unit firstUntrappedUnit = trap.getEndUnit();
        Unit lastTrappedUnit = unitChain.getPredOf(firstUntrappedUnit);
        Unit lastTrappedThrower = null;
        for (Unit u = firstTrappedUnit; u != null && u != firstUntrappedUnit; u = unitChain.getSuccOf(u)) {
          if (mightThrowTo(graph, u, trap)) {
            firstTrappedThrower = u;
            break;
          }

          // If this is the catch-all block and the current unit has
          // an,
          // active monitor, we need to keep the block
          if (isCatchAll && unitsWithMonitor.contains(u)) {
            if (firstTrappedThrower == null) {
              firstTrappedThrower = u;
            }
            break;
          }
        }
        if (firstTrappedThrower != null) {
          for (Unit u = lastTrappedUnit; u != null; u = unitChain.getPredOf(u)) {
            if (mightThrowTo(graph, u, trap)) {
              lastTrappedThrower = u;
              break;
            }

            // If this is the catch-all block and the current unit
            // has an, active monitor, we need to keep the block
            if (isCatchAll && unitsWithMonitor.contains(u)) {
              lastTrappedThrower = u;
              break;
            }
          }
        }
        // If no statement inside the trap can throw an exception, we
        // remove the complete trap.
        if (firstTrappedThrower == null) {
          trapIt.remove();
        } else {
          if (firstTrappedThrower != null && firstTrappedUnit != firstTrappedThrower) {
            trap.setBeginUnit(firstTrappedThrower);
          }
          if (lastTrappedThrower == null) {
            lastTrappedThrower = firstTrappedUnit;
          }
          if (lastTrappedUnit != lastTrappedThrower) {
            trap.setEndUnit(unitChain.getSuccOf(lastTrappedThrower));
          }
        }
      }
    }
  }

  /**
   * A utility routine which determines if a particular {@link Unit} might throw an exception to a particular {@link Trap},
   * according to the information supplied by a particular control flow graph.
   *
   * @param g
   *          The control flow graph providing information about exceptions.
   * @param u
   *          The unit being inquired about.
   * @param t
   *          The trap being inquired about.
   * @return <tt>true</tt> if <tt>u</tt> might throw an exception caught by <tt>t</tt>, according to <tt>g</tt.
   */
  protected boolean mightThrowTo(ExceptionalUnitGraph g, Unit u, Trap t) {
    for (ExceptionDest dest : g.getExceptionDests(u)) {
      if (dest.getTrap() == t) {
        return true;
      }
    }
    return false;
  }
}
