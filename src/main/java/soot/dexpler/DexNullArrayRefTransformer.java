//
// (c) 2012 University of Luxembourg - Interdisciplinary Centre for
// Security Reliability and Trust (SnT) - All rights reserved
//
// Author: Alexandre Bartel
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU Lesser General Public License as published by
// the Free Software Foundation, either version 2.1 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//

package soot.dexpler;

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

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.Local;
import soot.RefType;
import soot.Scene;
import soot.SootMethodRef;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.DefinitionStmt;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;
import soot.jimple.LengthExpr;
import soot.jimple.NullConstant;
import soot.jimple.Stmt;
import soot.jimple.StringConstant;
import soot.jimple.toolkits.scalar.LocalCreation;
import soot.jimple.toolkits.scalar.UnreachableCodeEliminator;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.scalar.LocalDefs;

/**
 * If Dalvik bytecode contains statements using a base array which is always null, Soot's fast type resolver will fail with
 * the following exception: "Exception in thread " main" java.lang.RuntimeException: Base of array reference is not an
 * array!"
 *
 * Those statements are replaced by a throw statement (this is what will happen in practice if the code is executed).
 *
 * @author alex
 * @author Steven Arzt
 *
 */
public class DexNullArrayRefTransformer extends BodyTransformer {

  public static DexNullArrayRefTransformer v() {
    return new DexNullArrayRefTransformer();
  }

  protected void internalTransform(final Body body, String phaseName, Map<String, String> options) {
    final ExceptionalUnitGraph g = new ExceptionalUnitGraph(body, DalvikThrowAnalysis.v());
    final LocalDefs defs = LocalDefs.Factory.newLocalDefs(g);
    final LocalCreation lc = new LocalCreation(body.getLocals(), "ex");

    boolean changed = false;
    for (Iterator<Unit> unitIt = body.getUnits().snapshotIterator(); unitIt.hasNext();) {
      Stmt s = (Stmt) unitIt.next();

      if (s.containsArrayRef()) {
        // Check array reference
        Value base = s.getArrayRef().getBase();
        if (isAlwaysNullBefore(s, (Local) base, defs)) {
          createThrowStmt(body, s, lc);
          changed = true;
        }
      } else if (s instanceof AssignStmt) {
        AssignStmt ass = (AssignStmt) s;
        Value rightOp = ass.getRightOp();
        if (rightOp instanceof LengthExpr) {
          // Check lengthof expression
          LengthExpr l = (LengthExpr) ass.getRightOp();
          Value base = l.getOp();
          if (base instanceof IntConstant) {
            IntConstant ic = (IntConstant) base;
            if (ic.value == 0) {
              createThrowStmt(body, s, lc);
              changed = true;
            }
          } else if (base == NullConstant.v() || isAlwaysNullBefore(s, (Local) base, defs)) {
            createThrowStmt(body, s, lc);
            changed = true;
          }
        }
      }
    }

    if (changed) {
      UnreachableCodeEliminator.v().transform(body);
    }
  }

  /**
   * Checks whether the given local is guaranteed to be always null at the given statement
   *
   * @param s
   *          The statement at which to check the local
   * @param base
   *          The local to check
   * @param defs
   *          The definition analysis object to use for the check
   * @return True if the given local is guaranteed to always be null at the given statement, otherwise false
   */
  private boolean isAlwaysNullBefore(Stmt s, Local base, LocalDefs defs) {
    List<Unit> baseDefs = defs.getDefsOfAt(base, s);
    if (baseDefs.isEmpty()) {
      return true;
    }

    for (Unit u : baseDefs) {
      if (!(u instanceof DefinitionStmt)) {
        return false;
      }
      DefinitionStmt defStmt = (DefinitionStmt) u;
      if (defStmt.getRightOp() != NullConstant.v()) {
        return false;
      }
    }
    return true;
  }

  /**
   * Creates a new statement that throws a NullPointerException
   *
   * @param body
   *          The body in which to create the statement
   * @param oldStmt
   *          The old faulty statement that shall be replaced with the exception
   * @param lc
   *          The object for creating new locals
   */
  private void createThrowStmt(Body body, Unit oldStmt, LocalCreation lc) {
    RefType tp = RefType.v("java.lang.NullPointerException");
    Local lcEx = lc.newLocal(tp);

    SootMethodRef constructorRef
        = Scene.v().makeConstructorRef(tp.getSootClass(), Collections.singletonList((Type) RefType.v("java.lang.String")));

    // Create the exception instance
    Stmt newExStmt = Jimple.v().newAssignStmt(lcEx, Jimple.v().newNewExpr(tp));
    body.getUnits().insertBefore(newExStmt, oldStmt);
    Stmt invConsStmt = Jimple.v().newInvokeStmt(Jimple.v().newSpecialInvokeExpr(lcEx, constructorRef,
        Collections.singletonList(StringConstant.v("Invalid array reference replaced by Soot"))));
    body.getUnits().insertBefore(invConsStmt, oldStmt);

    // Throw the exception
    body.getUnits().swapWith(oldStmt, Jimple.v().newThrowStmt(lcEx));
  }
}
