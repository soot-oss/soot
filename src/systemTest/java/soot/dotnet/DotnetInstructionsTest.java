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
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import soot.ArrayType;
import soot.IntType;
import soot.LongType;
import soot.SootClass;
import soot.jimple.AddExpr;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.CastExpr;
import soot.jimple.DivExpr;
import soot.jimple.InstanceOfExpr;
import soot.jimple.JimpleBody;
import soot.jimple.LengthExpr;
import soot.jimple.MulExpr;
import soot.jimple.NewArrayExpr;
import soot.jimple.NullConstant;
import soot.jimple.RemExpr;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.StringConstant;
import soot.jimple.SubExpr;

/**
 * Translation of individual CIL instructions into Jimple expressions.
 */
public class DotnetInstructionsTest extends AbstractDotnetTestingFramework {

  private SootClass instructions;

  @org.junit.Before
  public void resolveTargets() {
    instructions = testClass("Instructions");
  }

  @Test
  public void boxingKeepsValue() {
    JimpleBody b = body(instructions, "Box", 1);

    assertJimple(b, "@parameter0: int");
    assertUnit(b, soot.jimple.ReturnStmt.class);
  }

  @Test
  public void unboxingBecomesCast() {
    JimpleBody b = body(instructions, "Unbox", 1);

    CastExpr cast = assertUnit(b, AssignStmt.class).getRightOp() instanceof CastExpr
        ? (CastExpr) assertUnit(b, AssignStmt.class).getRightOp()
        : null;
    assertTrue("Unbox must be a cast to int", cast != null && IntType.v().equals(cast.getCastType()));
  }

  @Test
  public void referenceCastBecomesCast() {
    JimpleBody b = body(instructions, "Cast", 1);

    boolean castToString = b.getUnits().stream().anyMatch(u -> u instanceof AssignStmt
        && ((AssignStmt) u).getRightOp() instanceof CastExpr
        && ((CastExpr) ((AssignStmt) u).getRightOp()).getCastType().toString().equals("System.String"));
    assertTrue("Cast to System.String expected", castToString);
  }

  @Test
  public void isExpressionBecomesInstanceOf() {
    JimpleBody b = body(instructions, "IsString", 1);

    boolean instanceOf = b.getUnits().stream().anyMatch(u -> u instanceof AssignStmt
        && ((AssignStmt) u).getRightOp() instanceof InstanceOfExpr
        && ((InstanceOfExpr) ((AssignStmt) u).getRightOp()).getCheckType().toString()
            .equals("System.String"));
    assertTrue("instanceof System.String expected", instanceOf);
  }

  @Test
  public void asExpressionKeepsNullFallback() {
    JimpleBody b = body(instructions, "AsString", 1);

    assertTrue("as conversion must test the type", b.getUnits().stream()
        .anyMatch(u -> u instanceof AssignStmt
            && ((AssignStmt) u).getRightOp() instanceof InstanceOfExpr));
    assertTrue("as conversion must have a null fallback", b.getUnits().stream()
        .anyMatch(u -> u instanceof AssignStmt
            && ((AssignStmt) u).getRightOp() instanceof NullConstant));
  }

  @Test
  public void nullLiteralBecomesNullConstant() {
    JimpleBody b = body(instructions, "Null", 0);

    boolean returnsNull = b.getUnits().stream().anyMatch(u -> u instanceof soot.jimple.ReturnStmt
        && ((soot.jimple.ReturnStmt) u).getOp() instanceof NullConstant);
    assertTrue("Return of null expected", returnsNull);
  }

  @Test
  public void stringLiteralBecomesStringConstant() {
    JimpleBody b = body(instructions, "Literal", 0);

    boolean returnsLiteral = b.getUnits().stream().anyMatch(u -> u instanceof soot.jimple.ReturnStmt
        && StringConstant.v("soot").equals(((soot.jimple.ReturnStmt) u).getOp()));
    assertTrue("Return of the string constant expected", returnsLiteral);
  }

  @Test
  public void arrayCreationAndLengthAreTranslated() {
    JimpleBody newArray = body(instructions, "NewArray", 1);
    NewArrayExpr newExpr = assertUnit(newArray, AssignStmt.class).getRightOp() instanceof NewArrayExpr
        ? (NewArrayExpr) assertUnit(newArray, AssignStmt.class).getRightOp()
        : null;
    assertTrue("new int[] expected", newExpr != null && ArrayType.v(IntType.v(), 1).equals(newExpr.getType()));

    JimpleBody length = body(instructions, "ArrayLength", 1);
    boolean hasLength = length.getUnits().stream().anyMatch(u -> u instanceof AssignStmt
        && ((AssignStmt) u).getRightOp() instanceof LengthExpr);
    assertTrue("arraylength expected", hasLength);
  }

  @Test
  public void arrayLoadAndStoreUseArrayRef() {
    JimpleBody load = body(instructions, "ArrayElement", 2);
    boolean hasLoad = load.getUnits().stream().anyMatch(u -> u instanceof AssignStmt
        && ((AssignStmt) u).getRightOp() instanceof ArrayRef);
    assertTrue("Array load expected", hasLoad);

    JimpleBody store = body(instructions, "StoreArrayElement", 3);
    boolean hasStore = store.getUnits().stream().anyMatch(u -> u instanceof AssignStmt
        && ((AssignStmt) u).getLeftOp() instanceof ArrayRef);
    assertTrue("Array store expected", hasStore);
    assertTrue("long array expected", store.getUnits().stream()
        .anyMatch(u -> u.toString().contains("long[]")));
  }

  @Test
  public void foreachOverParamsBecomesLoop() {
    JimpleBody b = body(instructions, "Sum", 1);

    assertJimple(b, "@parameter0: int[]");
    assertTrue("Loop jump expected", countUnitsOfType(b, soot.jimple.IfStmt.class) > 0);
    assertTrue("Accumulation expected", b.getUnits().stream()
        .anyMatch(u -> u instanceof AssignStmt
            && ((AssignStmt) u).getRightOp() instanceof AddExpr));
  }

  @Test
  public void stringConcatBecomesStaticCall() {
    JimpleBody b = body(instructions, "Concat", 2);

    assertTrue("String.Concat must be invoked statically", invokeExprs(b).stream()
        .anyMatch(e -> e instanceof StaticInvokeExpr
            && e.getMethodRef().getDeclaringClass().getName().equals("System.String")));
  }

  @Test
  public void longArithmeticKeepsLongType() {
    JimpleBody b = body(instructions, "LongArithmetic", 2);

    for (Class<? extends soot.Value> op : java.util.List.of(MulExpr.class, DivExpr.class, SubExpr.class,
        RemExpr.class, AddExpr.class)) {
      assertTrue(op.getSimpleName() + " expected",
          b.getUnits().stream().anyMatch(u -> u instanceof AssignStmt
              && op.isInstance(((AssignStmt) u).getRightOp())));
    }
    assertTrue("Long-typed locals expected", b.getUnits().stream()
        .anyMatch(u -> u instanceof AssignStmt
            && LongType.v().equals(((AssignStmt) u).getLeftOp().getType())));
  }

  @Test
  public void booleanLogicBecomesBranches() {
    JimpleBody b = body(instructions, "Logic", 2);

    assertTrue("Branch for && expected", countUnitsOfType(b, soot.jimple.IfStmt.class) > 0);
    assertJimple(b, "b == 0");
  }

  @Test
  public void ternaryBecomesBranchWithTwoResults() {
    JimpleBody b = body(instructions, "Ternary", 1);

    assertTrue("Branch expected", countUnitsOfType(b, soot.jimple.IfStmt.class) > 0);
    assertEquals("Two assignments expected", 2, b.getUnits().stream()
        .filter(u -> u instanceof AssignStmt
            && ((AssignStmt) u).getLeftOp().toString().equals("result"))
        .count());
  }

  @Test
  public void instanceCallOnValueTypeIsResolved() {
    JimpleBody b = body(instructions, "Unbox", 1);
    assertTrue(b.getUnits().size() > 0);

    JimpleBody describe = body(testClass("Members"), "Describe", 1);
    assertTrue("Int32.ToString must be invoked", invokeExprs(describe).stream()
        .anyMatch(e -> e.getMethodRef().getName().equals("ToString")));
  }
}
