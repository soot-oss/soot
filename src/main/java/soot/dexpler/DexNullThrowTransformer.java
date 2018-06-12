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
import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.Local;
import soot.RefType;
import soot.Scene;
import soot.SootMethodRef;
import soot.Type;
import soot.Unit;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;
import soot.jimple.LongConstant;
import soot.jimple.NullConstant;
import soot.jimple.Stmt;
import soot.jimple.StringConstant;
import soot.jimple.ThrowStmt;
import soot.jimple.toolkits.scalar.LocalCreation;

/**
 * Some Android applications throw null references, e.g.,
 *
 * a = null; throw a;
 *
 * This will make unit graph construction fail as no targets of the throw statement can be found. We therefore replace such
 * statements with direct NullPointerExceptions which would happen at runtime anyway.
 *
 * @author Steven Arzt
 *
 */
public class DexNullThrowTransformer extends BodyTransformer {

  public static DexNullThrowTransformer v() {
    return new DexNullThrowTransformer();
  }

  @Override
  protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
    LocalCreation lc = new LocalCreation(b.getLocals(), "ex");

    for (Iterator<Unit> unitIt = b.getUnits().snapshotIterator(); unitIt.hasNext();) {
      Unit u = unitIt.next();

      // Check for a null exception
      if (u instanceof ThrowStmt) {
        ThrowStmt throwStmt = (ThrowStmt) u;
        if (throwStmt.getOp() == NullConstant.v() || throwStmt.getOp().equals(IntConstant.v(0))
            || throwStmt.getOp().equals(LongConstant.v(0))) {
          createThrowStmt(b, throwStmt, lc);
        }
      }
    }
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
        Collections.singletonList(StringConstant.v("Null throw statement replaced by Soot"))));
    body.getUnits().insertBefore(invConsStmt, oldStmt);

    // Throw the exception
    body.getUnits().swapWith(oldStmt, Jimple.v().newThrowStmt(lcEx));
  }

}
