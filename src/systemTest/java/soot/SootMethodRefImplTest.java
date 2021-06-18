package soot;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2021 Timothy Hoffman
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

import java.util.Arrays;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PowerMockIgnore;

import soot.jimple.Stmt;
import soot.testing.framework.AbstractTestingFramework;

/**
 * @author Timothy Hoffman
 */
@PowerMockIgnore({ "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*" })
public class SootMethodRefImplTest extends AbstractTestingFramework {

  private static final String TEST_TARGET_CLASS = "soot.SootMethodRefImplTestInput";

  @Override
  protected void setupSoot() {
  }

  @Test
  public void testCachingInvalidation() throws Exception {
    SootMethod m1 = prepareTarget(methodSigFromComponents(TEST_TARGET_CLASS, "void", "m1"), TEST_TARGET_CLASS);
    final SootClass clas = m1.getDeclaringClass();

    // There are only 3 methods in the class originally.
    Assert.assertEquals(Arrays.asList("<init>", "m1", "m2"),
        clas.getMethods().stream().map(SootMethod::getName).sorted().collect(Collectors.toList()));

    // Ensure the previous value of SootMethodRefImpl#resolveCache
    // is not used if the referenced method itself is modified.
    final Body b = m1.retrieveActiveBody();
    final SootMethodRef mRef = getMethodRef(b);
    Assert.assertEquals("m2", mRef.getName());

    // Get the original referenced method appearing in the test source (i.e. "m2")
    final SootMethod origM = mRef.resolve();
    Assert.assertTrue(!origM.isPhantom());
    Assert.assertEquals("m2", origM.getName());

    // Change the name of the method so the method reference no
    // longer refers to that method.
    origM.setName("newMethodName");
    Assert.assertEquals("newMethodName", origM.getName());

    // Changing the method itself does not change the reference
    Assert.assertEquals("m2", mRef.getName());

    // There are still just 3 methods in the class (but "m2" was renamed).
    Assert.assertEquals(Arrays.asList("<init>", "m1", "newMethodName"),
        clas.getMethods().stream().map(SootMethod::getName).sorted().collect(Collectors.toList()));

    // When resolving the reference, the cached value is not used since the
    // original method was renamed. It now gives a different method (that was
    // created automatically since a method with the name "m2" no longer exists).
    final SootMethod newM = mRef.resolve();
    Assert.assertNotSame(origM, newM);
    Assert.assertEquals("m2", newM.getName());

    // There are now 4 methods since resolving "m2" created it again.
    Assert.assertEquals(Arrays.asList("<init>", "m1", "m2", "newMethodName"),
        clas.getMethods().stream().map(SootMethod::getName).sorted().collect(Collectors.toList()));
  }

  private static SootMethodRef getMethodRef(Body b) {
    SootMethodRef retVal = null;
    for (Unit u : b.getUnits()) {
      Assert.assertTrue(u instanceof Stmt);
      Stmt s = (Stmt) u;
      if (s.containsInvokeExpr()) {
        Assert.assertNull(retVal);// the body has exactly 1 InvokeExpr
        retVal = s.getInvokeExpr().getMethodRef();
      }
    }
    Assert.assertNotNull(retVal);// the body has exactly 1 InvokeExpr
    return retVal;
  }
}
