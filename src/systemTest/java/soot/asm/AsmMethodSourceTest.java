package soot.asm;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2020 Manuel Benz
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

import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PowerMockIgnore;

import soot.Body;
import soot.Local;
import soot.SootMethod;
import soot.Unit;
import soot.UnitPatchingChain;
import soot.jimple.Jimple;
import soot.jimple.NopStmt;
import soot.options.Options;
import soot.testing.framework.AbstractTestingFramework;
import soot.util.HashChain;

/** @author Manuel Benz at 13.02.20 */
@PowerMockIgnore({ "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*" })
public class AsmMethodSourceTest extends AbstractTestingFramework {

  private static final String TEST_TARGET_CLASS = "soot.asm.LineNumberExtraction";

  @Override
  protected void setupSoot() {
    final Options opts = Options.v();
    opts.setPhaseOption("jb", "use-original-names:true");
    opts.setPhaseOption("jb.sils", "enabled:false");
    opts.setPhaseOption("jb.tr", "ignore-nullpointer-dereferences:true");
    opts.setPhaseOption("cg", "enabled:false");
    opts.set_keep_line_number(true);
  }

  @Test
  public void iterator() {
    // statements at the beginning of a for loop should have the line number as for the branching
    // statement and not the last line number after the branch that leads outside the loop
    SootMethod target = prepareTarget(methodSigFromComponents(TEST_TARGET_CLASS, "void", "iterator"), TEST_TARGET_CLASS);

    Body body = target.retrieveActiveBody();

    Optional<Unit> unit = body.getUnits().stream()
        .filter(u -> u.toString().contains("<java.util.Iterator: java.lang.Object next()>()")).findFirst();

    Assert.assertTrue(unit.isPresent());

    Assert.assertEquals(31, unit.get().getJavaSourceStartLineNumber());
  }

  @Test
  public void localNaming() {
    // This test ensures that local names are preserved in the Jimple code.
    final String className = "soot.asm.LocalNaming";
    final String[] params = { "java.lang.String", "java.lang.Integer", "byte[]", "java.lang.StringBuilder" };
    SootMethod target = prepareTarget(methodSigFromComponents(className, "void", "localNaming", params), className);
    Body body = target.retrieveActiveBody();
    Set<String> localNames = body.getLocals().stream().map(Local::getName).collect(Collectors.toSet());

    // All expected Local names are present
    Assert.assertTrue(localNames.contains("alpha"));
    Assert.assertTrue(localNames.contains("beta"));
    Assert.assertTrue(localNames.contains("gamma"));
    Assert.assertTrue(localNames.contains("delta"));
    Assert.assertTrue(localNames.contains("epsilon"));
    Assert.assertTrue(localNames.contains("zeta"));
    Assert.assertTrue(localNames.contains("eta"));
    Assert.assertTrue(localNames.contains("theta"));
    Assert.assertTrue(localNames.contains("iota"));
    Assert.assertTrue(localNames.contains("omega"));

    // No Local name contains "$stack"
    Assert.assertTrue(localNames.stream().allMatch(n -> !n.contains("$stack")));
  }

  /**
   * This is the case phase jb.sils is disabled. see the case phase jb.sils is enabled in {@link SilsTest#testSilsEnabled()}
   */
  @Test
  public void testSilsDisabled() {
    final String className = "soot.asm.LocalNaming";
    final String[] params = {};
    SootMethod target = prepareTarget(methodSigFromComponents(className, "void", "test", params), className);
    Body body = target.retrieveActiveBody();
    Set<String> localNames = body.getLocals().stream().map(Local::getName).collect(Collectors.toSet());
    // test if all expected Local names are present
    Assert.assertTrue(localNames.contains("d"));
    Assert.assertTrue(localNames.contains("f"));
    Assert.assertTrue(localNames.contains("arr"));
  }

  @Test
  public void testInner() {
    NopStmt[] nops = new NopStmt[6];
    for (int i = 0; i < nops.length; i++) {
      nops[i] = Jimple.v().newNopStmt();
    }
    UnitPatchingChain chainNew = new UnitPatchingChain(new HashChain<Unit>());
    UnitContainer container = new UnitContainer(nops[0], new UnitContainer(nops[1], new UnitContainer(nops[2]), nops[3]),
        nops[4], new UnitContainer(nops[5]));
    AsmMethodSource.emitUnits(container, chainNew);
    UnitPatchingChain chainOld = new UnitPatchingChain(new HashChain<Unit>());
    oldEmitImplementation(container, chainOld);

    Assert.assertEquals(chainOld.size(), chainNew.size());
    Iterator<Unit> itO = chainOld.iterator();
    Iterator<Unit> itN = chainNew.iterator();
    while (itO.hasNext()) {
      Unit oo = itO.next();
      Unit nn = itN.next();
      if (oo != nn) {
        Assert.fail();
      }
    }
  }

  private void oldEmitImplementation(Unit u, UnitPatchingChain c) {
    if (u instanceof UnitContainer) {
      for (Unit uu : ((UnitContainer) u).units) {
        oldEmitImplementation(uu, c);
      }
    } else {
      c.add(u);
    }
  }
}
