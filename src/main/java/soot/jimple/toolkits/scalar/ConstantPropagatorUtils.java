package soot.jimple.toolkits.scalar;

import soot.Body;
import soot.FastHierarchy;
import soot.RefType;
import soot.Scene;
import soot.Trap;
import soot.Unit;
import soot.UnitPatchingChain;
import soot.Value;
import soot.ValueBox;
import soot.jimple.ClassConstant;
import soot.jimple.DefinitionStmt;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.ExceptionalUnitGraph.ExceptionDest;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2000 Patrick Lam
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

/**
 * Contains utility methods for constant propagation
 */
public class ConstantPropagatorUtils {

  /**
   * Checks whether propagating <i>propagatedValue</i> from <i>defFrom</i> to <i>defTo</i> is safe.
   * @param graph an exceptional unit graph
   * @param propagatedValue the propagated value
   * @param defFrom definition source
   * @param defTo target
   * @param targetBox the target box
   * @return true if and only if propagation is safe w.r.t. trap handling
   */
  public static boolean mayPropagate(ExceptionalUnitGraph graph, Value propagatedValue, DefinitionStmt defFrom, Unit defTo,
      ValueBox targetBox) {
    if (!targetBox.canContainValue(propagatedValue)) {
      return false;
    }
    if (propagatedValue instanceof ClassConstant) {
      //Class Constants can trigger a NoClassDefFoundError.
      //Therefore, we must not propagate them, since we might change the semantics of the original
      //program w.r.t. traps.
      RefType rt = RefType.v("java.lang.NoClassDefFoundError");
      Trap trap = null;
      for (ExceptionDest d : graph.getExceptionDests(defFrom)) {
        if (d.getThrowables().catchableAs(rt)) {
          trap = d.getTrap();
          break;
        }
      }
      Body body = graph.getBody();
      FastHierarchy fh = Scene.v().getOrMakeFastHierarchy();
      UnitPatchingChain chain = body.getUnits();

      //this is not super fast, but the exceptional unit graph is not helpful here, since
      //we would need to rebuild it after propagation to reflect the changes, which would be more expensive.
      for (Trap i : body.getTraps()) {
        if (trap != null && i.getHandlerUnit() != trap.getHandlerUnit()) {
          continue;
        }
        if (fh.canStoreType(rt, i.getException().getType())) {
          Unit u = i.getBeginUnit();
          while (u != i.getEndUnit()) {
            if (u == defTo) {
              //when the original code had the same handling unit, this is fine.
              //if there is none, this is not fine.
              return trap != null;
            }
            u = chain.getSuccOf(u);
          }
        }
      }
      //we have not found a trap, so the original code must not have one, either
      return trap == null;
    }

    return true;
  }

  private static Unit getTrapHandler(ExceptionalUnitGraph graph, Unit def, RefType rtException) {
    for (ExceptionDest d : graph.getExceptionDests(def)) {
      if (d.getThrowables().catchableAs(rtException)) {
        return d.getHandlerNode();
      }
    }
    return null;
  }

}
