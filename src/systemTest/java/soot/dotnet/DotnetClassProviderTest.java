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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import soot.ClassSource;
import soot.Scene;
import soot.SootClass;
import soot.SourceLocator;
import soot.jimple.JimpleBody;

/**
 * Discovery of assemblies, types and class sources by the .NET frontend.
 */
public class DotnetClassProviderTest extends AbstractDotnetTestingFramework {

  @Test
  public void allTypesOfAssemblyAreDiscoverable() {
    List<String> classes = SourceLocator.v().getClassesUnder(TARGETS_DIR);
    Set<String> names = new HashSet<>(classes);

    assertTrue("Plain type must be found", names.contains(TEST_NAMESPACE + "Statements"));
    assertTrue("Nested type must be found", names.contains(TEST_NAMESPACE + "Container`1$Nested"));
    assertTrue("Generic type must be found", names.contains(TEST_NAMESPACE + "Container`1"));
    assertTrue("Type of the referenced assembly must be found",
        names.contains("Soot.SystemTest.Dotnet.Referenced.Registry"));
    assertFalse("Source files must not be reported as classes", names.contains("Statements.cs"));
  }

  @Test
  public void classSourceIsProvidedForKnownTypes() {
    ClassSource source = SourceLocator.v().getClassSource(TEST_NAMESPACE + "Statements");

    assertNotNull("A class source is expected for an existing type", source);
    assertTrue("A DotnetClassSource is expected", source instanceof DotnetClassSource);
    assertEquals(DotnetClassSource.class, source.getClass());
  }

  @Test
  public void classSourceIsNullForUnknownTypes() {
    assertNull("No class source expected for an unknown type",
        SourceLocator.v().getClassSource("Soot.SystemTest.Dotnet.DoesNotExist"));
  }

  @Test
  public void assemblyIndexMapsTypeToAssembly() {
    DotnetClassProvider.ensureAssemblyIndex();
    File assembly = SourceLocator.v().dexClassIndex().get(TEST_NAMESPACE + "Statements");

    assertNotNull("Type must be indexed", assembly);
    assertEquals(TARGET_ASSEMBLY_NAME, assembly.getName());
  }

  @Test
  public void onlyRealAssembliesAreAccepted() throws java.io.IOException {
    AssemblyFile assembly = new AssemblyFile(TARGET_ASSEMBLY);
    assertTrue("The target assembly must be recognized", assembly.isAssembly());

    File textFile = java.nio.file.Files.createTempFile("soot-systemtest", ".txt").toFile();
    textFile.deleteOnExit();
    AssemblyFile notAnAssembly = new AssemblyFile(textFile.getAbsolutePath());
    assertFalse("A text file must not be recognized", notAnAssembly.isAssembly());
  }

  @Test
  public void allTypesOfAnAssemblyAreExposed() {
    AssemblyFile assembly = new AssemblyFile(TARGET_ASSEMBLY);

    Set<String> typeNames = new HashSet<>(assembly.getAllTypeNames());
    assertTrue("Statements must be listed", typeNames.contains(TEST_NAMESPACE + "Statements"));
    assertTrue("Enum must be listed", typeNames.contains(TEST_NAMESPACE + "Color"));
    assertNotNull("Type definitions must be readable", assembly.getAllTypes());
  }

  @Test
  public void crossAssemblyReferencesResolve() {
    SootClass registry = loadClass("Soot.SystemTest.Dotnet.Referenced.Registry");

    assertTrue("Factory method must exist", registry.declaresMethodByName("CreateMembers"));
    assertEquals(TEST_NAMESPACE + "Members", method(registry, "CreateMembers", 0).getReturnType().toString());

    JimpleBody b = body(registry, "CreateMembers", 0);
    assertJimple(b, "new " + TEST_NAMESPACE + "Members");
    assertJimple(b, "<" + TEST_NAMESPACE + "Members: void <init>()>");

    // The referenced assembly knows the classes of the assembly it references.
    assertTrue("Referenced types must resolve", Scene.v().containsClass(TEST_NAMESPACE + "Square"));
  }

  @Test
  public void nestedTypeOfReferencedAssemblyIsIndexed() {
    DotnetClassProvider.ensureAssemblyIndex();
    File assembly = SourceLocator.v().dexClassIndex().get("Soot.SystemTest.Dotnet.Referenced.Registry$Entry");

    assertNotNull("Nested type of referenced assembly must be indexed", assembly);
    assertEquals("Soot.SystemTest.Dotnet.Referenced.dll", assembly.getName());
  }
}
