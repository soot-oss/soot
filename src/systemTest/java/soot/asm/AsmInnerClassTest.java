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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.powermock.core.classloader.annotations.PowerMockIgnore;

import soot.Modifier;
import soot.Scene;
import soot.SootMethod;
import soot.tagkit.InnerClassTag;
import soot.tagkit.Tag;
import soot.testing.framework.AbstractTestingFramework;

@PowerMockIgnore({ "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*" })
public class AsmInnerClassTest extends AbstractTestingFramework {

  private static final String TEST_TARGET_CLASS = "soot.asm.ScopeFinderTarget";

  @Override
  protected void setupSoot() {
    // no need for call graph
  }

  @Test
  public void nonInner() {
    // statements at the beginning of a for loop should have the line number as for the branching
    // statement and not the last line number after the branch that leads outside the loop
    SootMethod target =
        prepareTarget(
            methodSigFromComponents(TEST_TARGET_CLASS, "void", "method"), TEST_TARGET_CLASS);
    assertEquals(2, Scene.v().getApplicationClasses().size());
    assertFalse(target.getDeclaringClass().hasOuterClass());
    assertFalse(target.getDeclaringClass().isInnerClass());
    InnerClassTag tag = (InnerClassTag) target.getDeclaringClass().getTag(InnerClassTag.NAME);
    // the class has inner classes
    assertNotNull(tag);
  }

  @Test
  public void InnerStatic() {
    SootMethod target2 =
        prepareTarget(
            methodSigFromComponents(TEST_TARGET_CLASS + "$Inner", "void", "<init>"),
            TEST_TARGET_CLASS + "$Inner");
    assertEquals(2, Scene.v().getApplicationClasses().size());
    assertTrue(target2.getDeclaringClass().hasOuterClass());
    assertTrue(target2.getDeclaringClass().isInnerClass());
    InnerClassTag tag2 = (InnerClassTag) target2.getDeclaringClass().getTag(InnerClassTag.NAME);
    assertNotNull(tag2);
    assertEquals("soot/asm/ScopeFinderTarget$Inner", tag2.getInnerClass());
    assertEquals("soot/asm/ScopeFinderTarget", tag2.getOuterClass());
    assertTrue(Modifier.isStatic(tag2.getAccessFlags()));
  }

  @Test
  public void InnerStaticInner() {
    SootMethod target3 =
        prepareTarget(
            methodSigFromComponents(TEST_TARGET_CLASS + "$Inner$InnerInner", "void", "method"),
            TEST_TARGET_CLASS + "$Inner$InnerInner");
    // one dummy
    assertEquals(2, Scene.v().getApplicationClasses().size());
    assertTrue(target3.getDeclaringClass().hasOuterClass());
    assertTrue(target3.getDeclaringClass().isInnerClass());
    InnerClassTag innerClassTag = null;
    for (Tag tag : target3.getDeclaringClass().getTags()) {
      // FIXME: we have multiple innerclasstags? for a parent it makes sense but for a child class?
      if (tag instanceof InnerClassTag) {
        boolean inner =
            ((InnerClassTag) tag)
                .getInnerClass()
                .equals("soot/asm/ScopeFinderTarget$Inner$InnerInner");
        if (inner) {
          innerClassTag = (InnerClassTag) tag;
          break;
        }
      }
    }
    assertNotNull(innerClassTag);
    assertEquals("soot/asm/ScopeFinderTarget$Inner$InnerInner", innerClassTag.getInnerClass());
    assertEquals("soot/asm/ScopeFinderTarget$Inner", innerClassTag.getOuterClass());
    assertFalse(Modifier.isStatic(innerClassTag.getAccessFlags()));
  }
}
