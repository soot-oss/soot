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
package soot.java10;

import static org.junit.Assert.assertTrue;

import com.google.common.base.Optional;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import soot.G;
import soot.Main;
import soot.Scene;
import soot.SootClass;
import soot.SootModuleResolver;
import soot.options.Options;

import categories.Java11Test;

/**
 * Tests the loading of Java 9 to 11 Modules.
 *
 * @author Andreas Dann
 */
@Category(Java11Test.class)
public class LoadingTest {
  /**
   * load a JDK9to11 class by naming its module
   */
  @Test
  public void testLoadingJava9to11Class() {
    G.reset();
    Options.v().set_soot_modulepath("VIRTUAL_FS_FOR_JDK");
    Scene.v().loadBasicClasses();

    SootClass klass1
        = SootModuleResolver.v().resolveClass("java.lang.invoke.VarHandle", SootClass.BODIES, Optional.of("java.base"));

    assertTrue(klass1.getName().equals("java.lang.invoke.VarHandle"));
    assertTrue(klass1.moduleName.equals("java.base"));

    SootClass klass2 = SootModuleResolver.v().resolveClass("java.lang.invoke.ConstantBootstraps", SootClass.BODIES,
        Optional.of("java.base"));

    assertTrue(klass2.getName().equals("java.lang.invoke.ConstantBootstraps"));
    assertTrue(klass2.moduleName.equals("java.base"));

    Scene.v().loadNecessaryClasses();
  }

  /**
   * load a JDK9 class using the CI
   */
  @Test
  public void testLoadingJava9ClassFromCI() {
    G.reset();
    Main.main(new String[] { "-soot-modulepath", "VIRTUAL_FS_FOR_JDK", "-pp", "-src-prec", "only-class",
        "java.lang.invoke.VarHandle" });

    SootClass klass = Scene.v().getSootClass("java.lang.invoke.VarHandle");
    assertTrue(klass.getName().equals("java.lang.invoke.VarHandle"));
    assertTrue(klass.moduleName.equals("java.base"));

  }

  /**
   * load a JDK11 class using the CI
   */
  @Test
  public void testLoadingJava11ClassFromCI() {
    G.reset();
    Main.main(new String[] { "-soot-modulepath", "VIRTUAL_FS_FOR_JDK", "-pp", "-src-prec", "only-class",
        "java.lang.invoke.ConstantBootstraps" });

    SootClass klass = Scene.v().getSootClass("java.lang.invoke.ConstantBootstraps");
    assertTrue(klass.getName().equals("java.lang.invoke.ConstantBootstraps"));
    assertTrue(klass.moduleName.equals("java.base"));

  }
}
