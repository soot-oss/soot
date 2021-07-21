package soot.asm;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2014 Raja Vallee-Rai and others
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

import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PowerMockIgnore;

import soot.Body;
import soot.Local;
import soot.SootMethod;
import soot.options.Options;
import soot.testing.framework.AbstractTestingFramework;

/**
 * Test for the phase "jb.sils"
 * 
 * @author Linghui Luo
 */
@PowerMockIgnore({ "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*" })
public class SilsTest extends AbstractTestingFramework {

  @Override
  protected void setupSoot() {
    final Options opts = Options.v();
    opts.setPhaseOption("jb", "use-original-names:true");
    opts.setPhaseOption("jb.sils", "enabled:true");
    opts.setPhaseOption("jb.tr", "ignore-nullpointer-dereferences:true");
    opts.setPhaseOption("cg", "enabled:false");
  }

  /**
   * This is the case phase jb.sils is enabled (default). see the case phase jb.sils is disabled in
   * {@link AsmMethodSourceTest#testSilsDisabled()}
   */
  @Test
  @Ignore
  public void testSilsEnabled() {
    final String className = "soot.asm.LocalNaming";
    final String[] params = {};
    SootMethod target = prepareTarget(methodSigFromComponents(className, "void", "test", params), className);
    Body body = target.retrieveActiveBody();
    Set<String> localNames = body.getLocals().stream().map(Local::getName).collect(Collectors.toSet());
    // test if all expected Local names are present
    // currently d, f are not preserved.
    Assert.assertTrue(localNames.contains("d"));
    Assert.assertTrue(localNames.contains("f"));
    Assert.assertTrue(localNames.contains("arr"));
  }
}
