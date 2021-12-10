package soot.lambdaMetaFactory;
/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2019 Manuel Benz
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

import org.junit.Test;
import org.powermock.core.classloader.annotations.PowerMockIgnore;

import soot.testing.framework.AbstractTestingFramework;

/**
 * Reproduces issue 1292: https://github.com/soot-oss/soot/issues/1292
 *
 * @author raintung.li
 */
@PowerMockIgnore({ "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*" })
public class Issue1292Test extends AbstractTestingFramework {

  @Test
  public void testNewTest() {
    String testClass = "soot.lambdaMetaFactory.Issue1292";
    prepareTarget(
        methodSigFromComponents(testClass, "void", "testNew", "java.util.List"),
        testClass,
        "java.util.function.Function");
    // if no exception is thrown, everything is working as intended
  }
}
