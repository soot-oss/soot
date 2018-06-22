package soot.testing.framework;

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

import soot.SootMethod;

/**
 * @author Manuel Benz created on 22.06.18
 */
public class HelloTestingFrameworkTest extends AbstractTestingFramework {

  private static final String TEST_TARGET_CLASS = "soot.testing.framework.HelloTestingFrameworkTarget";

  @Test
  public void findsTarget() {
    final SootMethod sootMethod = prepareTarget("<" + TEST_TARGET_CLASS + ": void helloWorld()>", TEST_TARGET_CLASS);
    Assert.assertNotNull("Could not find target method. System test setup seems to be incorrect.", sootMethod);
    Assert.assertTrue(sootMethod.isConcrete());
    Assert.assertNotNull(sootMethod.retrieveActiveBody());
  }
}
