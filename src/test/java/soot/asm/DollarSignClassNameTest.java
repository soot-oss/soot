package soot.asm;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2024 Mustafa Şenoğlu and others
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
import static org.junit.Assert.assertTrue;

import com.google.common.io.Files;

import java.io.File;
import java.util.Collections;

import org.junit.Test;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import soot.G;
import soot.Scene;
import soot.SootClass;
import soot.options.Options;

/**
 * Tests that Soot correctly resolves outer class information for classes whose names contain '$'. This verifies the fix
 * for <a href="https://github.com/soot-oss/soot/issues/1956">soot-oss/soot#1956</a>, where non-nested classes with '$'
 * in their names (e.g., {@code $Gson$Types}, {@code A$B}) incorrectly triggered a {@code SootClassNotFoundException}
 * because Soot assumed the part before the last '$' was always the outer class.
 */
public class DollarSignClassNameTest {

  /**
   * Generates two classes: (1) a genuine nested class {@code Outer$Inner} with proper InnerClasses attribute, and (2) a
   * non-nested class {@code sample.A$B} that has '$' in its name but is NOT an inner class.
   */
  private File generateTestClasses() throws Exception {
    File tempDir = Files.createTempDir();

    // --- Class 1: Outer (contains InnerClasses attribute for Outer$Inner) ---
    ClassWriter cwOuter = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
    cwOuter.visit(Opcodes.V11, Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER, "Outer", null, "java/lang/Object", null);
    // Declare InnerClasses: Outer$Inner is a static inner class of Outer
    cwOuter.visitInnerClass("Outer$Inner", "Outer", "Inner", Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC);
    cwOuter.visitEnd();
    Files.write(cwOuter.toByteArray(), new File(tempDir, "Outer.class"));

    // --- Class 1b: Outer$Inner (the actual inner class file) ---
    ClassWriter cwInner = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
    cwInner.visit(Opcodes.V11, Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER, "Outer$Inner", null, "java/lang/Object", null);
    cwInner.visitInnerClass("Outer$Inner", "Outer", "Inner", Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC);
    cwInner.visitEnd();
    Files.write(cwInner.toByteArray(), new File(tempDir, "Outer$Inner.class"));

    // --- Class 2: sample.A$B (non-nested class with '$' in its name) ---
    // Its InnerClasses attribute does NOT list A$B itself as an inner class.
    ClassWriter cwDollar = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
    cwDollar.visit(Opcodes.V11, Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER, "sample/A$B", null, "java/lang/Object", null);
    // Intentionally NO visitInnerClass for "sample/A$B" — it is NOT an inner class
    cwDollar.visitEnd();
    Files.write(cwDollar.toByteArray(), new File(tempDir, "A$B.class"));

    // --- Class 3: sample.$Gson$Types (another non-nested class with multiple '$') ---
    ClassWriter cwGson = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
    cwGson.visit(Opcodes.V11, Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER, "sample/$Gson$Types", null, "java/lang/Object",
        null);
    cwGson.visitEnd();
    Files.write(cwGson.toByteArray(), new File(tempDir, "$Gson$Types.class"));

    return tempDir;
  }

  private void setupSoot(File classDir) {
    G.reset();
    Options.v().set_prepend_classpath(true);
    Options.v().set_process_dir(Collections.singletonList(classDir.getAbsolutePath()));
    Options.v().set_src_prec(Options.src_prec_class);
    Options.v().set_output_format(Options.output_format_none);
    Options.v().set_allow_phantom_refs(true);
    Options.v().setPhaseOption("cg", "enabled:false");
    Scene.v().loadNecessaryClasses();
  }

  @Test
  public void genuineNestedClassResolvesOuterClass() throws Exception {
    File classDir = generateTestClasses();
    setupSoot(classDir);

    SootClass innerClass = Scene.v().getSootClass("Outer$Inner");
    assertTrue("Outer$Inner should have outer class set", innerClass.hasOuterClass());
    assertEquals("Outer", innerClass.getOuterClass().getName());
  }

  @Test
  public void nonNestedClassWithDollarSignDoesNotResolveOuterClass() throws Exception {
    File classDir = generateTestClasses();
    setupSoot(classDir);

    SootClass abClass = Scene.v().getSootClass("sample.A$B");
    assertFalse("sample.A$B should NOT have outer class set", abClass.hasOuterClass());
  }

  @Test
  public void nonNestedClassWithMultipleDollarSignsDoesNotResolveOuterClass() throws Exception {
    File classDir = generateTestClasses();
    setupSoot(classDir);

    SootClass gsonClass = Scene.v().getSootClass("sample.$Gson$Types");
    assertFalse("sample.$Gson$Types should NOT have outer class set", gsonClass.hasOuterClass());
  }

  @Test
  public void nonNestedClassCanBeLoadedWithoutSootClassNotFoundException() throws Exception {
    File classDir = generateTestClasses();
    setupSoot(classDir);

    // Loading should succeed without throwing SootClassNotFoundException
    SootClass abClass = Scene.v().getSootClass("sample.A$B");
    assertTrue("sample.A$B should not be phantom", abClass.isConcrete() || abClass.isAbstract());

    SootClass gsonClass = Scene.v().getSootClass("sample.$Gson$Types");
    assertTrue("sample.$Gson$Types should not be phantom", gsonClass.isConcrete() || gsonClass.isAbstract());
  }
}
