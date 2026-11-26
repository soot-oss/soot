package soot.dotnet;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2026 Marc Miltenberger
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import soot.Trap;
import soot.Unit;
import soot.Value;
import soot.jimple.CaughtExceptionRef;
import soot.jimple.IdentityStmt;
import soot.jimple.IfStmt;
import soot.jimple.IntConstant;
import soot.jimple.JimpleBody;
import soot.jimple.ReturnStmt;
import soot.jimple.SwitchStmt;
import soot.jimple.ThrowStmt;

/**
 * Translating of C# branching and exception handling to Jimple.
 */
public class DotnetStatementsTest extends AbstractDotnetTestingFramework {

  @Test
  public void ifStatementBecomesConditionalBranch() {
    JimpleBody b = body(testClass("Statements"), "Branch", 1);

    assertEquals("Both branches must produce a conditional jump", 2, countUnitsOfType(b, IfStmt.class));
    assertEquals("Both branches must return a constant", 2, countUnitsOfType(b, ReturnStmt.class));

    boolean returnsOne = false;
    boolean returnsMinusOne = false;
    for (Unit u : b.getUnits()) {
      if (!(u instanceof ReturnStmt)) {
        continue;
      }
      Value op = ((ReturnStmt) u).getOp();
      returnsOne |= IntConstant.v(1).equals(op);
      returnsMinusOne |= IntConstant.v(-1).equals(op);
    }
    Assert.assertTrue("Missing return of 1", returnsOne);
    Assert.assertTrue("Missing return of -1", returnsMinusOne);
  }

  @Test
  public void switchBecomesSwitchStmt() {
    JimpleBody b = body(testClass("Statements"), "Switch", 1);

    SwitchStmt s = assertUnit(b, SwitchStmt.class);
    assertEquals("Two case targets expected", 2, s.getTargets().size());
    Assert.assertNotNull("Default target expected", s.getDefaultTarget());
    if (s instanceof soot.jimple.TableSwitchStmt) {
      soot.jimple.TableSwitchStmt table = (soot.jimple.TableSwitchStmt) s;
      assertEquals("Lowest case value", 1, table.getLowIndex());
      assertEquals("Highest case value", 2, table.getHighIndex());
    } else {
      assertEquals("First case value", IntConstant.v(1),
          ((soot.jimple.LookupSwitchStmt) s).getLookupValues().get(0));
      assertEquals("Second case value", IntConstant.v(2),
          ((soot.jimple.LookupSwitchStmt) s).getLookupValues().get(1));
    }
  }

  @Test
  public void loopsBecomeBackJumps() {
    for (String method : new String[] { "Loop", "DoWhile" }) {
      JimpleBody b = body(testClass("Statements"), method, 1);
      assertTrue(method + " must branch", countUnitsOfType(b, IfStmt.class) > 0);
      assertTrue(method + " must have a back jump", countUnitsOfType(b, soot.jimple.GotoStmt.class) > 0);
    }
  }

  @Test
  public void tryCatchFinallyProducesTraps() {
    JimpleBody b = body(testClass("Statements"), "TryCatchFinally", 1);

    assertTrue("A trap must guard the try block", b.getTraps().size() >= 1);
    for (Trap trap : b.getTraps()) {
      assertNotNull(trap.getException());
      assertTrue("Handler must start with a caughtexception definition",
          trap.getHandlerUnit() instanceof IdentityStmt
              && ((IdentityStmt) trap.getHandlerUnit()).getRightOp() instanceof CaughtExceptionRef);
    }

    // The finally block increments the static field once on the normal and once on the exceptional path.
    assertEquals("Finally block must run on both paths", 2,
        b.getUnits().stream().filter(u -> u.toString().contains("SideEffect> =")).count());
    assertUnit(b, ThrowStmt.class);
  }

  @Test
  public void multipleCatchClausesKeepTheirTypes() {
    JimpleBody b = body(testClass("Statements"), "TryCatchMultiple", 1);

    List<String> caught = new java.util.ArrayList<>();
    for (Trap trap : b.getTraps()) {
      caught.add(trap.getException().getName());
    }
    assertTrue("FormatException must be caught, got " + caught, caught.contains("System.FormatException"));
    assertTrue("OverflowException must be caught, got " + caught, caught.contains("System.OverflowException"));
    assertTrue("The catch handlers must return their constants", b.getUnits().stream()
        .filter(u -> u instanceof ReturnStmt).map(u -> ((ReturnStmt) u).getOp().toString())
        .anyMatch(v -> v.equals("1") || v.equals("2")));
  }

  @Test
  public void throwStatementKeepsExceptionType() {
    JimpleBody b = body(testClass("Statements"), "Throw", 0);

    assertUnit(b, ThrowStmt.class);
    assertJimple(b, "new System.InvalidOperationException");
    assertJimple(b, "<System.InvalidOperationException: void <init>(System.String)>");
    assertJimple(b, "\"boom\"");
  }

}
