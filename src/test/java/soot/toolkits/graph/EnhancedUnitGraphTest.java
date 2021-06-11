package soot.toolkits.graph;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2019 Shawn Meier
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import soot.G;
import soot.PackManager;
import soot.Scene;
import soot.SootClass;
import soot.Transform;
import soot.Unit;
import soot.UnitPatchingChain;
import soot.options.Options;
import soot.toolkits.graph.pdg.EnhancedUnitGraph;

public class EnhancedUnitGraphTest {
  private static EnhancedUnitGraphTestUtility testUtility;

  private static String TARGET_CLASS = "soot.toolkits.graph.targets.TestException";

  @BeforeClass
  public static void setUp() throws IOException {
    G.reset();
    List<String> processDir = new ArrayList<>();
    File f = new File("./target/test-classes");
    if (f.exists()) {
      processDir.add(f.getCanonicalPath());
    }
    Options.v().set_process_dir(processDir);

    Options.v().set_src_prec(Options.src_prec_only_class);
    Options.v().set_allow_phantom_refs(true);
    Options.v().set_output_format(Options.output_format_none);
    Options.v().set_drop_bodies_after_load(false);
    Scene.v().addBasicClass(TARGET_CLASS);
    Scene.v().loadNecessaryClasses();
    Options.v().set_prepend_classpath(true);
    Scene.v().forceResolve("soot.toolkits.graph.targets.TestException", SootClass.BODIES);
    testUtility = new EnhancedUnitGraphTestUtility();
    PackManager.v().getPack("jtp").add(new Transform("jtp.TestEnhancedGraphUtility", testUtility));
    PackManager.v().runPacks();
  }

  @Test
  public void exceptionIsReachable() {

    EnhancedUnitGraph unitGraph = testUtility.getUnitGraph();
    UnitPatchingChain allUnits = unitGraph.body.getUnits();

    int targetUnitsFound = 0;

    for (Unit u : allUnits) {
      if (u.toString().contains("@caughtexception")) {
        assert (unitGraph.unitToPreds.get(u).size() > 0);
        ++targetUnitsFound;
      }
    }

    assert (targetUnitsFound == 2);
  }
}
