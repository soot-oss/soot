package soot.jimple;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2018 Manuel Benz
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

import org.junit.Assert;
import org.junit.Test;

import soot.Body;
import soot.PackManager;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.options.Options;
import soot.testing.framework.AbstractTestingFramework;

/**
 * @author Andreas Dann created on 06.02.19
 * @author Manuel Benz 27.2.19
 */


public class PolymorphicDispatchTest extends AbstractTestingFramework {

  private static final String TEST_TARGET_CLASS = "soot.jimple.PolymorphicDispatch";

  @Override
  protected void setupSoot() {
    Options.v().set_allow_phantom_refs(true);
    Options.v().set_no_bodies_for_excluded(false);
    Options.v().set_prepend_classpath(true);
    // if we use validate globally, every test will fail due to validation of target methods of other tests. Even if the test
    // would actually pass...
    Options.v().set_validate(false);
  }

  @Override
  protected void runSoot() {
    PackManager.v().runBodyPacks();
  }

  @Test
  public void findsTarget() {
    String methodSignature = methodSigFromComponents(TEST_TARGET_CLASS, "void", "unambiguousMethod", "");
    final SootMethod sootMethod = prepareTarget(methodSignature, TEST_TARGET_CLASS);
    Assert.assertTrue(sootMethod.isConcrete());

    Body body = sootMethod.retrieveActiveBody();
    Assert.assertNotNull(body);
    // validate individual method
    body.validate();

    for (Unit u : body.getUnits()) {
      if (u instanceof AssignStmt) {
        Value right = ((AssignStmt) u).getRightOp();
        if (right instanceof InvokeExpr) {
          SootMethod m = ((InvokeExpr) right).getMethodRef().resolve();
          Assert.assertFalse(m.isPhantom());
          Assert.assertTrue(m.isDeclared());
          if (m.getName().equals("invoke")) {
            Assert.assertTrue(m.isNative());
          }
        }
      }
    }
  }

  @Test
  public void handlesAmbiguousMethod() {
    String methodSignature = methodSigFromComponents(TEST_TARGET_CLASS, "void", "ambiguousMethod", "");
    final SootMethod sootMethod = prepareTarget(methodSignature, TEST_TARGET_CLASS);
    Assert.assertTrue(sootMethod.isConcrete());

    Body body = sootMethod.retrieveActiveBody();
    Assert.assertNotNull(body);
    // validate individual method
    body.validate();

    for (Unit u : body.getUnits()) {
      if (u instanceof AssignStmt) {
        Value right = ((AssignStmt) u).getRightOp();
        if (right instanceof InvokeExpr) {
          SootMethod m = ((InvokeExpr) right).getMethodRef().resolve();
          Assert.assertFalse(m.isPhantom());
          Assert.assertTrue(m.isDeclared());
          if (m.getName().equals("invoke")) {
            Assert.assertTrue(m.isNative());
          }
        }
      }
    }
  }
}
