package soot;

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

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import soot.jimple.GotoStmt;
import soot.jimple.Jimple;
import soot.util.Chain;

/**
 * Although the Patching Chain is meant to only work for units, it can also work with certain subclasses of units. However,
 * for insertOnEdge and similar operations, new Jimple statements have to be generated. As such, it might be the case that a
 * {@code PatchingChain<X extends Unit>} is not allowed to contain such new statements, since they are not a subclass of X.
 * Therefore, we decided to create a chain specifically for units, where we can be certain that they are allowed to contain
 * all kind of units. Feel free to go and grab a beer.
 */
@SuppressWarnings("serial")
public class UnitPatchingChain extends PatchingChain<Unit> {

  public UnitPatchingChain(Chain<Unit> aChain) {
    super(aChain);
  }

  /**
   * Inserts instrumentation in a manner such that the resulting control flow graph (CFG) of the program will contain
   * <code>toInsert</code> on an edge that is defined by <code>point_source</code> and <code>point_target</code>.
   *
   * @param toInsert
   *          instrumentation to be added in the Chain
   * @param point_src
   *          the source point of an edge in CFG
   * @param point_tgt
   *          the target point of an edge
   */
  public void insertOnEdge(Collection<? extends Unit> toInsert, Unit point_src, Unit point_tgt) {
    if (toInsert == null) {
      throw new RuntimeException("Tried to insert a null Collection into the Chain!");
    }
    // Throw an exception if both source and target are null
    if (point_src == null && point_tgt == null) {
      throw new RuntimeException("insertOnEdge failed! Both source unit and target points are null.");
    }
    // Nothing to do if it's empty
    if (toInsert.isEmpty()) {
      return;
    }

    // Insert 'toInsert' before 'target' point in chain if the source point is null
    if (point_src == null) {
      assert (point_tgt != null);
      Unit firstInserted = toInsert.iterator().next();
      point_tgt.redirectJumpsToThisTo(firstInserted);
      innerChain.insertBefore(toInsert, point_tgt);
      return;
    }

    // Insert 'toInsert' after 'source' point in chain if the target point is null
    if (point_tgt == null) {
      assert (point_src != null);
      innerChain.insertAfter(toInsert, point_src);
      return;
    }

    // If target is right after the source in the Chain
    // 1- Redirect all jumps (if any) from 'source' to 'target', to 'toInsert[0]'
    // (source->target) ==> (source->toInsert[0])
    // 2- Insert 'toInsert' after 'source' in Chain
    if (getSuccOf(point_src) == point_tgt) {
      Unit firstInserted = toInsert.iterator().next();
      for (UnitBox box : point_src.getUnitBoxes()) {
        if (box.getUnit() == point_tgt) {
          box.setUnit(firstInserted);
        }
      }
      innerChain.insertAfter(toInsert, point_src);
      return;
    }

    // If the target is not right after the source in chain then,
    // 1- Redirect all jumps (if any) from 'source' to 'target', to 'toInsert[0]'
    // (source->target) ==> (source->toInsert[0])
    // 1.1- if there are no jumps from source to target, then such an edge
    // does not exist. Throw an exception.
    // 2- Insert 'toInsert' before 'target' in Chain
    // 3- If required, add a 'goto target' statement so that no other edge
    // executes 'toInsert'
    final Unit firstInserted = toInsert.iterator().next();
    boolean validEdgeFound = false;
    Unit originalPred = this.getPredOf(point_tgt);
    for (UnitBox box : point_src.getUnitBoxes()) {
      if (box.getUnit() == point_tgt) {
        if (point_src instanceof GotoStmt) {
          box.setUnit(firstInserted);
          innerChain.insertAfter(toInsert, point_src);

          Unit goto_unit = Jimple.v().newGotoStmt(point_tgt);
          if (toInsert instanceof List) {
            List<? extends Unit> l = (List<? extends Unit>) toInsert;
            innerChain.insertAfter(goto_unit, l.get(l.size() - 1));
          } else {
            innerChain.insertAfter(goto_unit, (Unit) toInsert.toArray()[toInsert.size() - 1]);
          }
          return;
        }

        box.setUnit(firstInserted);
        validEdgeFound = true;
      }
    }
    if (validEdgeFound) {
      innerChain.insertBefore(toInsert, point_tgt);

      if (originalPred != point_src) {
        if (originalPred instanceof GotoStmt) {
          return;
        }

        Unit goto_unit = Jimple.v().newGotoStmt(point_tgt);
        innerChain.insertBefore(Collections.singletonList(goto_unit), firstInserted);
      }
      return;
    }

    // In certain scenarios, the above code can add extra 'goto' units on a
    // different edge
    // So, an edge [src --> tgt] becomes [src -> goto tgt -> tgt].
    // When this happens, the original edge [src -> tgt] ceases to exist.
    // The following code handles such scenarios.
    final Unit succ = getSuccOf(point_src);
    if (succ instanceof GotoStmt) {
      if (succ.getUnitBoxes().get(0).getUnit() == point_tgt) {
        succ.redirectJumpsToThisTo(firstInserted);
        innerChain.insertBefore(toInsert, succ);
        return;
      }
    }
    // If the control reaches this point, it means that an edge [src -> tgt]
    // as specified by user does not exist so throw an exception.
    throw new RuntimeException(
        "insertOnEdge failed! No such edge found. The edge on which you want to insert an instrumentation is invalid.");

  }

  /**
   * Inserts instrumentation in a manner such that the resulting control flow graph (CFG) of the program will contain
   * <code>toInsert</code> on an edge that is defined by <code>point_source</code> and <code>point_target</code>.
   *
   * @param toInsert
   *          instrumentation to be added in the Chain
   * @param point_src
   *          the source point of an edge in CFG
   * @param point_tgt
   *          the target point of an edge
   */
  public void insertOnEdge(List<Unit> toInsert, Unit point_src, Unit point_tgt) {
    insertOnEdge((Collection<Unit>) toInsert, point_src, point_tgt);
  }

  /**
   * Inserts instrumentation in a manner such that the resulting control flow graph (CFG) of the program will contain
   * <code>toInsert</code> on an edge that is defined by <code>point_source</code> and <code>point_target</code>.
   *
   * @param toInsert
   *          instrumentation to be added in the Chain
   * @param point_src
   *          the source point of an edge in CFG
   * @param point_tgt
   *          the target point of an edge
   */
  public void insertOnEdge(Chain<Unit> toInsert, Unit point_src, Unit point_tgt) {
    insertOnEdge((Collection<Unit>) toInsert, point_src, point_tgt);
  }

  /**
   * Inserts instrumentation in a manner such that the resulting control flow graph (CFG) of the program will contain
   * <code>toInsert</code> on an edge that is defined by <code>point_source</code> and <code>point_target</code>.
   *
   * @param toInsert
   *          the instrumentation to be added in the Chain
   * @param point_src
   *          the source point of an edge in CFG
   * @param point_tgt
   *          the target point of an edge
   */
  public void insertOnEdge(Unit toInsert, Unit point_src, Unit point_tgt) {
    insertOnEdge(Collections.singleton(toInsert), point_src, point_tgt);
  }
}
