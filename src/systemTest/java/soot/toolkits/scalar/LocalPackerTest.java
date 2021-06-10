package soot.toolkits.scalar;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2020 Timothy Hoffman
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
import org.powermock.core.classloader.annotations.PowerMockIgnore;

import soot.Body;
import soot.Local;
import soot.SootMethod;
import soot.jimple.JimpleBody;
import soot.options.Options;
import soot.testing.framework.AbstractTestingFramework;

/**
 * @author Timothy Hoffman
 */
@PowerMockIgnore({ "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*" })
public class LocalPackerTest extends AbstractTestingFramework {

  private static final String TEST_TARGET_CLASS = "soot.toolkits.scalar.LocalPackerTestInput";

  @Override
  protected void setupSoot() {
    Options.v().setPhaseOption("jb", "use-original-names:true");
    Options.v().setPhaseOption("jb.sils", "enabled:false");
  }

  @Test
  public void nullAssignment() {
    SootMethod target =
        prepareTarget(methodSigFromComponents(TEST_TARGET_CLASS, "void", "prefixVariableNames"), TEST_TARGET_CLASS);

    Body body = target.retrieveActiveBody();
    Assert.assertTrue(body instanceof JimpleBody);

    // Assert all local names are distinct
    Assert.assertTrue(body.getLocals().stream().map(Local::getName).distinct().count() == body.getLocalCount());

    LocalPacker.v().transform(body);

    // Assert all local names are distinct
    Assert.assertTrue(body.getLocals().stream().map(Local::getName).distinct().count() == body.getLocalCount());
  }
}
