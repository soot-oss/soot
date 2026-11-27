package soot.dotnet;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2026 Marc Miltenberger
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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Assume;
import org.junit.Test;

import soot.SootClass;
import soot.SootField;
import soot.SourceLocator;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.JimpleBody;

/**
 * Static data with an initial value in the PE file (RVA fields).
 */
public class DotnetInitialValueTest extends AbstractDotnetTestingFramework {

  private static final String RVA_TYPE = "<PrivateImplementationDetails>";

  @Test
  public void rvaFieldCarriesInitialValue() {
    DotnetClassProvider.ensureAssemblyIndex();
    File source = SourceLocator.v().dexClassIndex().get(RVA_TYPE);
    Assume.assumeTrue("The classpath must consist of the test targets only",
        source != null && source.getName().equals(TARGET_ASSEMBLY_NAME));

    SootClass rva = loadClass(RVA_TYPE);
    SootField field = null;
    for (SootField candidate : rva.getFields()) {
      if (candidate.getTag(soot.dotnet.members.InitialFieldTagValue.NAME) != null) {
        field = candidate;
      }
    }
    assertNotNull("The RVA field must carry its initial value", field);

    byte[] value = ((soot.dotnet.members.InitialFieldTagValue) field
        .getTag(soot.dotnet.members.InitialFieldTagValue.NAME)).getValue();
    assertArrayEquals("Initial value of the static array (little endian 1, 2, 3, 4)",
        new byte[] { 1, 0, 0, 0, 2, 0, 0, 0, 3, 0, 0, 0, 4, 0, 0, 0 }, value);
  }

  @Test
  public void staticArrayInitializerBecomesArrayStores() {
    JimpleBody b = body(testClass("ArrayInitializer"), "<clinit>", 0);

    int stores = 0;
    for (soot.Unit u : b.getUnits()) {
      if (u instanceof AssignStmt && ((AssignStmt) u).getLeftOp() instanceof ArrayRef) {
        stores++;
      }
    }
    assertEquals("Each array element must be initialized by a store:\n" + b.toString(), 4, stores);
    assertJimple(b, "Numbers>");
  }

  @Test
  public void staticArrayIsReadableInOtherMethods() {
    JimpleBody b = body(testClass("ArrayInitializer"), "Third", 0);

    assertJimple(b, "<" + TEST_NAMESPACE + "ArrayInitializer: int[] Numbers>");
    assertTrue("Index access expected", b.getUnits().stream()
        .anyMatch(u -> u instanceof AssignStmt && ((AssignStmt) u).getRightOp() instanceof ArrayRef));
    assertJimple(b, "[2]");
    assertJimple(b, "return");
  }
}
