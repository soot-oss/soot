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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Body;
import soot.BodyTransformer;
import soot.G;
import soot.Local;
import soot.PhaseOptions;
import soot.Singletons;
import soot.Timers;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.DefinitionStmt;
import soot.jimple.FieldRef;
import soot.jimple.InvokeExpr;
import soot.jimple.MonitorStmt;
import soot.jimple.Stmt;
import soot.jimple.StmtBody;
import soot.options.Options;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.ExceptionalUnitGraphFactory;
import soot.toolkits.graph.PseudoTopologicalOrderer;
import soot.toolkits.scalar.LocalDefs;
import soot.toolkits.scalar.LocalUses;
import soot.toolkits.scalar.UnitValueBoxPair;
import soot.util.Chain;

public class Aggregator extends BodyTransformer {
  private static final Logger logger = LoggerFactory.getLogger(Aggregator.class);

  public Aggregator(Singletons.Global g) {
  }

  public static Aggregator v() {
    return G.v().soot_jimple_toolkits_base_Aggregator();
  }

  /**
   * Traverse the statements in the given body, looking for aggregation possibilities; that is, given a def d and a use u, d
   * has no other uses, u has no other defs, collapse d and u.
   *
   * option: only-stack-locals; if this is true, only aggregate variables starting with $
   */
  @Override
  protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
    StmtBody body = (StmtBody) b;

    final boolean time = Options.v().time();
    if (time) {
      Timers.v().aggregationTimer.start();
    }

    Map<ValueBox, Zone> boxToZone = new HashMap<ValueBox, Zone>(body.getUnits().size() * 2 + 1, 0.7f);
    // Determine the zone of every box
    {
      Zonation zonation = new Zonation(body);
      for (Unit u : body.getUnits()) {
        Zone zone = zonation.getZoneOf(u);
        for (ValueBox box : u.getUseAndDefBoxes()) {
          boxToZone.put(box, zone);
        }
      }
    }

    boolean onlyStackVars = PhaseOptions.getBoolean(options, "only-stack-locals");
    int aggregateCount = Options.v().verbose() ? 1 : 0;
    do {
      if (aggregateCount != 0) {
        logger.debug("[" + body.getMethod().getName() + "] Aggregating iteration " + aggregateCount + "...");
        aggregateCount++;
      }
    } while (internalAggregate(body, boxToZone, onlyStackVars));

    if (time) {
      Timers.v().aggregationTimer.end();
    }
  }

  private static boolean internalAggregate(StmtBody body, Map<ValueBox, Zone> boxToZone, boolean onlyStackVars) {
    boolean hadAggregation = false;

    final Chain<Unit> units = body.getUnits();
    final ExceptionalUnitGraph graph = ExceptionalUnitGraphFactory.createExceptionalUnitGraph(body);
    final LocalDefs localDefs = G.v().soot_toolkits_scalar_LocalDefsFactory().newLocalDefs(graph);
    final LocalUses localUses = LocalUses.Factory.newLocalUses(body, localDefs);

    NEXT_UNIT: for (Unit u : new PseudoTopologicalOrderer<Unit>().newList(graph, false)) {
      if (!(u instanceof AssignStmt)) {
        continue;
      }
      AssignStmt s = (AssignStmt) u;
      Value lhs = s.getLeftOp();
      if (!(lhs instanceof Local)) {
        continue;
      }
      final Local lhsLocal = (Local) lhs;
      if (onlyStackVars) {
        if (!lhsLocal.isStackLocal()) {
          continue;
        }
      }

      Unit usepairUnit;
      ValueBox usepairValueBox;
      {
        List<UnitValueBoxPair> lu = localUses.getUsesOf(s);
        if (lu.size() != 1) {
          continue;
        }
        UnitValueBoxPair usepair = lu.get(0);
        usepairUnit = usepair.unit;
        usepairValueBox = usepair.valueBox;
      }

      // Check to make sure aggregation pair in the same zone
      if ((localDefs.getDefsOfAt(lhsLocal, usepairUnit).size() != 1)
          || (boxToZone.get(s.getRightOpBox()) != boxToZone.get(usepairValueBox))) {
        continue;
      }

      /*
       * Need to check the path between def and use to see if there are any intervening re-defs of RHS in fact, we should
       * check that this path is unique. If the RHS uses only locals, then we know what to do; if RHS has a method invocation
       * f(a, b, c) or field access, we must ban field writes, other method calls and (as usual) writes to a, b, c.
       */
      // look for a path from s to use in graph.
      // only look in an extended basic block, though.
      List<Unit> path = graph.getExtendedBasicBlockPathBetween(s, usepairUnit);
      if (path == null) {
        continue;
      }

      {
        boolean propagatingInvokeExpr = false;
        boolean propagatingFieldRef = false;
        boolean propagatingArrayRef = false;
        ArrayList<FieldRef> fieldRefList = new ArrayList<FieldRef>();// iteration
        HashSet<Value> localsUsed = new HashSet<Value>();// fast contains check
        for (ValueBox vb : s.getUseBoxes()) {
          Value v = vb.getValue();
          if (v instanceof Local) {
            localsUsed.add(v);
          } else if (v instanceof InvokeExpr) {
            propagatingInvokeExpr = true;
          } else if (v instanceof ArrayRef) {
            propagatingArrayRef = true;
          } else if (v instanceof FieldRef) {
            propagatingFieldRef = true;
            fieldRefList.add((FieldRef) v);
          }
        }

        Iterator<Unit> pathIt = path.iterator();
        assert (pathIt.hasNext());
        pathIt.next(); // skip s.
        while (pathIt.hasNext()) {
          Stmt between = (Stmt) pathIt.next();

          if (between != usepairUnit) {
            // Make sure not propagating past a {enter,exit}Monitor
            if (propagatingInvokeExpr && between instanceof MonitorStmt) {
              continue NEXT_UNIT;// give up: can't aggregate.
            }

            // Check for killing definitions
            for (ValueBox vb : between.getDefBoxes()) {
              Value v = vb.getValue();
              if (localsUsed.contains(v)) {
                continue NEXT_UNIT;// give up: can't aggregate.
              } else if (v instanceof FieldRef) {
                if (propagatingInvokeExpr) {
                  continue NEXT_UNIT;// give up: can't aggregate.
                } else if (propagatingFieldRef) {
                  // Can't aggregate a field access if passing a definition of
                  // a field with the same name, because they might be aliased.
                  for (FieldRef fieldRef : fieldRefList) {
                    if (isSameField((FieldRef) v, fieldRef)) {
                      continue NEXT_UNIT;// give up: can't aggregate.
                    }
                  }
                }
              } else if (v instanceof ArrayRef) {
                if (propagatingInvokeExpr) {
                  // Cannot aggregate an invoke expr past an array write
                  continue NEXT_UNIT;// give up: can't aggregate.
                } else if (propagatingArrayRef) {
                  // Cannot aggregate an array read past a write. This is
                  // conservative (if types differ they may not be aliased).
                  continue NEXT_UNIT;// give up: can't aggregate.
                }
              }
            }
          }

          // Check for intervening side effects due to method calls
          if (propagatingInvokeExpr || propagatingFieldRef || propagatingArrayRef) {
            for (ValueBox box : between.getUseBoxes()) {
              if (between == usepairUnit && box == usepairValueBox) {
                // Reached use point, stop looking for side effects
                break;
              }

              Value v = box.getValue();
              if (v instanceof InvokeExpr || (propagatingInvokeExpr && (v instanceof FieldRef || v instanceof ArrayRef))) {
                continue NEXT_UNIT;// give up: can't aggregate.
              }
            }
          }
        } // end while
      }

      // assuming that the d-u chains are correct, we need not check the actual contents of ld
      Value aggregatee = s.getRightOp();
      if (usepairValueBox.canContainValue(aggregatee)) {
        boolean wasSimpleCopy = isSimpleCopy(usepairUnit);
        usepairValueBox.setValue(aggregatee);
        units.remove(s);
        // clean up the tags. If s was not a simple copy, the new
        // statement should get the tags of s.
        // OK, this fix was wrong. The condition should not be
        // "If s was not a simple copy", but rather "If usepairUnit
        // was a simple copy". This way, when there's a load of a
        // constant followed by an invoke, the invoke gets the tags.
        if (wasSimpleCopy) {
          // usepairUnit.removeAllTags();
          usepairUnit.addAllTagsOf(s);
        }
        hadAggregation = true;
      }
    } // end for(...)
    return hadAggregation;
  }

  /**
   * Checks whether two field references point to the same field
   *
   * @param ref1
   *          The first field reference
   * @param ref2
   *          The second reference
   * @return True if the two references point to the same field, otherwise false
   */
  private static boolean isSameField(FieldRef ref1, FieldRef ref2) {
    return (ref1 == ref2) || ref1.getFieldRef().equals(ref2.getFieldRef());
  }

  private static boolean isSimpleCopy(Unit u) {
    if (!(u instanceof DefinitionStmt)) {
      return false;
    }
    DefinitionStmt defstmt = (DefinitionStmt) u;
    return defstmt.getRightOp() instanceof Local && defstmt.getLeftOp() instanceof Local;
  }
}
