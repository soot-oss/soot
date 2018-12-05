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

import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.Local;
import soot.Trap;
import soot.Unit;
import soot.javaToJimple.LocalGenerator;
import soot.jimple.CaughtExceptionRef;
import soot.jimple.IdentityStmt;
import soot.jimple.Jimple;
import soot.jimple.Stmt;

/**
 * Transformer to ensure that all exception handlers pull the exception object. In other words, if an exception handler must
 * always have a unit like
 *
 * $r10 = @caughtexception
 *
 * This is especially important if the dex code is later to be translated into Java bytecode. If no one ever accesses the
 * exception object, it will reside on the stack forever, potentially leading to mismatching stack heights.
 *
 * @author Steven Arzt
 *
 */
public class DexTrapStackFixer extends BodyTransformer {

  public static DexTrapStackFixer v() {
    return new DexTrapStackFixer();
  }

  @Override
  protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
    for (Trap t : b.getTraps()) {
      // If the first statement already catches the exception, we're fine
      if (isCaughtExceptionRef(t.getHandlerUnit())) {
        continue;
      }

      // Add the exception reference
      Local l = new LocalGenerator(b).generateLocal(t.getException().getType());
      Stmt caughtStmt = Jimple.v().newIdentityStmt(l, Jimple.v().newCaughtExceptionRef());
      b.getUnits().add(caughtStmt);
      b.getUnits().add(Jimple.v().newGotoStmt(t.getHandlerUnit()));
      t.setHandlerUnit(caughtStmt);
    }
  }

  /**
   * Checks whether the given statement stores an exception reference
   *
   * @param handlerUnit
   *          The statement to check
   * @return True if the given statement stores an exception reference, otherwise false
   */
  private boolean isCaughtExceptionRef(Unit handlerUnit) {
    if (!(handlerUnit instanceof IdentityStmt)) {
      return false;
    }
    IdentityStmt stmt = (IdentityStmt) handlerUnit;
    return stmt.getRightOp() instanceof CaughtExceptionRef;
  }

}
