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

import soot.PackManager;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.options.Options;
import soot.testing.framework.AbstractTestingFramework;

/**
 * @author Andreas Dann created on 06.02.19
 */
public class PolymorphicDispatchTest extends AbstractTestingFramework {

  private static final String TEST_TARGET_CLASS = "soot.jimple.PolymorphicDispatch";

  @Override
  protected void setupSoot() {
    Options.v().set_allow_phantom_refs(false);
    Options.v().set_no_bodies_for_excluded(false);
    Options.v().set_prepend_classpath(true);
  }

  @Override
  protected void runSoot() {
    PackManager.v().runBodyPacks();
  }

  @Test
  public void findsTarget() {
    final SootMethod sootMethod = prepareTarget("<" + TEST_TARGET_CLASS + ": void test()>", TEST_TARGET_CLASS);
    Assert.assertNotNull("Could not find target method. System test setup seems to be incorrect.", sootMethod);
    Assert.assertTrue(sootMethod.isConcrete());
    Assert.assertNotNull(sootMethod.retrieveActiveBody());
    for (Unit u : sootMethod.getActiveBody().getUnits()) {
      if (u instanceof AssignStmt) {
        Value right = ((AssignStmt) u).getRightOp();
        if (right instanceof InvokeExpr) {
          // SootMethod m = ((InvokeExpr) right).getMethodRef().resolve();
          // System.out.println(m);
        }
      }
    }
  }
}
