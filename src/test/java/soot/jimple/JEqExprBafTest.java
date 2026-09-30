package soot.jimple;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2026 Mustafa Senoglu
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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import soot.BooleanType;
import soot.G;
import soot.IntType;
import soot.Local;
import soot.Modifier;
import soot.RefType;
import soot.Scene;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.VoidType;
import soot.baf.BafBody;
import soot.baf.GotoInst;
import soot.baf.IfCmpNeInst;
import soot.baf.IfNeInst;
import soot.baf.IfNonNullInst;
import soot.baf.PlaceholderInst;
import soot.baf.PushInst;
import soot.baf.StoreInst;
import soot.baf.TargetArgInst;
import soot.options.Options;

/**
 * Regression test for <a href="https://github.com/soot-oss/soot/issues/1841">issue #1841</a>: assigning a
 * {@code ==} comparison (e.g. {@code b = (r == null)}) to a local used to fail the Jimple to BAF conversion with
 * "unsupported conversion".
 */
public class JEqExprBafTest {

  @Before
  public void setUp() {
    G.reset();

    final Options opts = Options.v();
    opts.set_allow_phantom_refs(false);
    opts.set_no_bodies_for_excluded(true);

    // Disable "bb" phase for direct Jimple->Baf translation
    opts.setPhaseOption("bb", "enabled:false");

    Scene.v().loadNecessaryClasses();
  }

  private static BafBody convertAssignToBaf(Value lhs, Value rhs) {
    Jimple jimp = Jimple.v();
    SootMethod m = new SootMethod("m", Collections.emptyList(), VoidType.v(), Modifier.STATIC);
    JimpleBody body = jimp.newBody(m);
    m.setActiveBody(body);

    body.getLocals().add((Local) lhs);
    if (rhs instanceof EqExpr) {
      EqExpr eq = (EqExpr) rhs;
      addOperandLocal(body, eq.getOp1());
      addOperandLocal(body, eq.getOp2());
    }
    body.getUnits().add(jimp.newAssignStmt(lhs, rhs));
    body.getUnits().add(jimp.newReturnVoidStmt());

    return new BafBody(body, Collections.emptyMap());
  }

  private static void addOperandLocal(JimpleBody body, Value op) {
    if (op instanceof Local && !body.getLocals().contains(op)) {
      body.getLocals().add((Local) op);
    }
  }

  private static void assertWellFormed(BafBody bafBody) {
    for (Unit u : bafBody.getUnits()) {
      Assert.assertFalse("Unresolved placeholder in BAF body: " + u, u instanceof PlaceholderInst);
      if (u instanceof TargetArgInst) {
        Unit target = ((TargetArgInst) u).getTarget();
        Assert.assertTrue("Branch target outside BAF body: " + target, bafBody.getUnits().contains(target));
      }
    }
  }

  private static boolean contains(BafBody bafBody, Class<?> type) {
    for (Unit u : bafBody.getUnits()) {
      if (type.isInstance(u)) {
        return true;
      }
    }
    return false;
  }

  @Test
  public void nullComparisonAsValue() {
    Jimple jimp = Jimple.v();
    Local ref = jimp.newLocal("r", RefType.v("java.lang.Object"));
    Local result = jimp.newLocal("b", BooleanType.v());

    BafBody bafBody = convertAssignToBaf(result, jimp.newEqExpr(ref, NullConstant.v()));

    assertWellFormed(bafBody);
    Assert.assertTrue("Expected an IfNonNull branch", contains(bafBody, IfNonNullInst.class));
    Assert.assertTrue("Expected pushed boolean constants", contains(bafBody, PushInst.class));
    Assert.assertTrue("Expected a goto joining both outcomes", contains(bafBody, GotoInst.class));
    Assert.assertTrue("Expected the assigned value to be stored", contains(bafBody, StoreInst.class));
  }

  @Test
  public void intComparisonAsValue() {
    Jimple jimp = Jimple.v();
    Local left = jimp.newLocal("i", IntType.v());
    Local right = jimp.newLocal("j", IntType.v());
    Local result = jimp.newLocal("b", BooleanType.v());

    BafBody bafBody = convertAssignToBaf(result, jimp.newEqExpr(left, right));

    assertWellFormed(bafBody);
    Assert.assertTrue("Expected an IfCmpNe branch", contains(bafBody, IfCmpNeInst.class));
  }

  @Test
  public void intZeroComparisonAsValue() {
    Jimple jimp = Jimple.v();
    Local left = jimp.newLocal("i", IntType.v());
    Local result = jimp.newLocal("b", BooleanType.v());

    BafBody bafBody = convertAssignToBaf(result, jimp.newEqExpr(left, IntConstant.v(0)));

    assertWellFormed(bafBody);
    Assert.assertTrue("Expected an IfNe branch", contains(bafBody, IfNeInst.class));
  }
}
