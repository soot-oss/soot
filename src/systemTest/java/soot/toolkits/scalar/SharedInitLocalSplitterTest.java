package soot.toolkits.scalar;

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

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PowerMockIgnore;

import soot.Body;
import soot.SootMethod;
import soot.jimple.JimpleBody;
import soot.options.Options;
import soot.testing.framework.AbstractTestingFramework;

/**
 * @author Timothy Hoffman
 */
@PowerMockIgnore({ "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*" })
public class SharedInitLocalSplitterTest extends AbstractTestingFramework {

  private static final String TEST_TARGET_CLASS = "soot.toolkits.scalar.SharedInitLocalSplitterTestInput";

  @Override
  protected void setupSoot() {
    Options.v().setPhaseOption("jb.sils", "enabled:true");
  }

  @Ignore
  @Test
  public void testCaseA() {
    SootMethod target =
        prepareTarget(methodSigFromComponents(TEST_TARGET_CLASS, "int", "test", "java.lang.Object"), TEST_TARGET_CLASS);

    Body body = target.retrieveActiveBody();
    Assert.assertTrue(body instanceof JimpleBody);

    // Ensure unused locals are removed before asserting expected count.
    UnusedLocalEliminator.v().transform(body);

    // System.out.println("[SharedInitLocalSplitterTest#testCaseA] body = " + body);

    // SharedInitializationLocalSplitter should not have introduced any
    // additional locals in this example because all of the integer constant
    // values are used only as integers.
    Assert.assertEquals(3, body.getLocalCount());
  }
}
