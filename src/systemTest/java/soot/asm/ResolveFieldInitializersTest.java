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

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.powermock.core.classloader.annotations.PowerMockIgnore;

import soot.Scene;
import soot.SootClass;
import soot.testing.framework.AbstractTestingFramework;

/** @author Manuel Benz at 20.02.20 */
@PowerMockIgnore({ "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*" })
public class ResolveFieldInitializersTest extends AbstractTestingFramework {

  private static final String TEST_TARGET_CLASS = "soot.asm.ResolveFieldInitializers";

  @Test
  public void initializedInMethodRef() {
    prepareTarget(methodSigFromComponents(TEST_TARGET_CLASS, "void", "<init>"), TEST_TARGET_CLASS);
    SootClass sootClass = Scene.v().getSootClass("java.util.ArrayDeque");
    assertEquals(SootClass.SIGNATURES, sootClass.resolvingLevel());
  }

  @Test
  public void initializedInConstructor() {
    prepareTarget(methodSigFromComponents(TEST_TARGET_CLASS, "void", "<init>"), TEST_TARGET_CLASS);
    SootClass sootClass = Scene.v().getSootClass("java.util.LinkedList");
    assertEquals(SootClass.SIGNATURES, sootClass.resolvingLevel());
  }
}
