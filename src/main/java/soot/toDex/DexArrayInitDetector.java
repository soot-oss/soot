package soot.toDex;

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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.Body;
import soot.Trap;
import soot.Unit;
import soot.Value;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.IntConstant;
import soot.jimple.NewArrayExpr;

/**
 * Detector class that identifies array initializations and packs them into a single instruction:
 *
 * a = new char[2]; a[0] = 42; a[1] = 3;
 *
 * In dex, this can be expressed in a more concise way:
 *
 * a = new char[2]; fill(a, ...)
 *
 * @author Steven Arzt
 *
 */
public class DexArrayInitDetector {

  private Map<Unit, List<Value>> arrayInitToFillValues = new HashMap<Unit, List<Value>>();
  private Set<Unit> ignoreUnits = new HashSet<Unit>();

  private int minimumArrayLength = -1;

  /**
   * Constructs packed array initializations from the individual element assignments in the given body
   *
   * @param body
   *          The body in which to look for element assignments
   */
  public void constructArrayInitializations(Body body) {
    // Find an array construction followed by consecutive element
    // assignments
    Unit arrayInitStmt = null;
    List<Value> arrayValues = null;
    Set<Unit> curIgnoreUnits = null;
    int arraySize = 0;
    Value concernedArray = null;
    for (Unit u : body.getUnits()) {
      if (!(u instanceof AssignStmt)) {
        arrayValues = null;
        continue;
      }

      AssignStmt assignStmt = (AssignStmt) u;
      if (assignStmt.getRightOp() instanceof NewArrayExpr) {
        NewArrayExpr newArrayExp = (NewArrayExpr) assignStmt.getRightOp();
        if (newArrayExp.getSize() instanceof IntConstant) {
          IntConstant intConst = (IntConstant) newArrayExp.getSize();
          if (minimumArrayLength == -1 || intConst.value >= minimumArrayLength) {
            arrayValues = new ArrayList<Value>();
            arraySize = intConst.value;
            curIgnoreUnits = new HashSet<Unit>();
            concernedArray = assignStmt.getLeftOp();
          }
        } else {
          arrayValues = null;
        }
      } else if (assignStmt.getLeftOp() instanceof ArrayRef && assignStmt.getRightOp() instanceof IntConstant
      /*
       * NumericConstant
       */
          && arrayValues != null) {
        ArrayRef aref = (ArrayRef) assignStmt.getLeftOp();
        if (aref.getBase() != concernedArray) {
          arrayValues = null;
          continue;
        }
        if (aref.getIndex() instanceof IntConstant) {
          IntConstant intConst = (IntConstant) aref.getIndex();
          if (intConst.value == arrayValues.size()) {
            arrayValues.add(assignStmt.getRightOp());
            if (intConst.value == 0) {
              arrayInitStmt = u;
            } else if (intConst.value == arraySize - 1) {
              curIgnoreUnits.add(u);
              checkAndSave(arrayInitStmt, arrayValues, arraySize, curIgnoreUnits);
              arrayValues = null;
            } else {
              curIgnoreUnits.add(u);
            }
          } else {
            arrayValues = null;
          }
        } else {
          arrayValues = null;
        }
      } else {
        arrayValues = null;
      }
    }
  }

  /**
   * Sets the minimum array length to consider
   * 
   * Only arrays with more than this number of elements will be identified, because we only need to handle those explicitly.
   * For small arrays, we can have individual assignments for each element in the code, but for larger methods, this would
   * exceed the allowed method size according to the JVM / Dalvik Spec.
   * 
   * @param l
   *          the minimum length of arrays
   */
  public void setMinimumArrayLength(int l) {
    minimumArrayLength = l;
  }

  private void checkAndSave(Unit arrayInitStmt, List<Value> arrayValues, int arraySize, Set<Unit> curIgnoreUnits) {
    // If we already have all assignments, construct the filler
    if (arrayValues != null && arrayValues.size() == arraySize && arrayInitStmt != null) {
      arrayInitToFillValues.put(arrayInitStmt, arrayValues);
      ignoreUnits.addAll(curIgnoreUnits);
    }
  }

  public List<Value> getValuesForArrayInit(Unit arrayInit) {
    return arrayInitToFillValues.get(arrayInit);
  }

  public Set<Unit> getIgnoreUnits() {
    return ignoreUnits;
  }

  /**
   * Fixes the traps in the given body to account for assignments to individual array elements being replaced by a single
   * fill instruction. If a trap starts or ends in the middle of the replaced instructions, we have to move the trap.
   *
   * @param activeBody
   *          The body in which to fix the traps
   */
  public void fixTraps(Body activeBody) {
    traps: for (Iterator<Trap> trapIt = activeBody.getTraps().iterator(); trapIt.hasNext();) {
      Trap t = trapIt.next();
      Unit beginUnit = t.getBeginUnit();
      Unit endUnit = t.getEndUnit();

      while (ignoreUnits.contains(beginUnit)) {
        beginUnit = activeBody.getUnits().getPredOf(beginUnit);
        if (beginUnit == endUnit) {
          trapIt.remove();
          continue traps;
        }

        // The trap must start no earlier than the initial array filling
        if (arrayInitToFillValues.containsKey(beginUnit)) {
          break;
        }
      }
      while (ignoreUnits.contains(endUnit)) {
        endUnit = activeBody.getUnits().getSuccOf(endUnit);
        if (beginUnit == endUnit) {
          trapIt.remove();
          continue traps;
        }
      }

      t.setBeginUnit(beginUnit);
      t.setEndUnit(endUnit);
    }
  }

}
