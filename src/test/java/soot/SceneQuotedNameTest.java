package soot;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vallée-Rai and others
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

public class SceneQuotedNameTest {

  private Scene freshScene() {
    G.reset();
    return Scene.v();
  }

  @Test
  public void testPlainNamesPassThrough() {
    Scene scene = freshScene();
    Assert.assertEquals("foo", scene.quotedNameOf("foo"));
    Assert.assertEquals("java.lang.String", scene.quotedNameOf("java.lang.String"));
    Assert.assertEquals("java.lang.Annotation[]", scene.quotedNameOf("java.lang.Annotation[]"));
  }

  @Test
  public void testReservedNameQuoting() {
    Scene scene = freshScene();
    Assert.assertEquals("'with'", scene.quotedNameOf("with"));
    Assert.assertEquals("'annotation'", scene.quotedNameOf("annotation"));
    Assert.assertEquals("java.lang.'annotation'.Annotation", scene.quotedNameOf("java.lang.annotation.Annotation"));
    Assert.assertEquals("'-foo'", scene.quotedNameOf("-foo"));
  }

  @Test
  public void testSignatureQuoting() {
    Scene scene = freshScene();
    Assert.assertEquals(
        "com.fasterxml.jackson.core.util.JacksonFeatureSet 'with'(com.fasterxml.jackson.core.util.JacksonFeature)",
        scene.quotedNameOf(
            "com.fasterxml.jackson.core.util.JacksonFeatureSet with(com.fasterxml.jackson.core.util.JacksonFeature)"));
    Assert.assertEquals("void foo()", scene.quotedNameOf("void foo()"));
    Assert.assertEquals(
        "<com.Foo: void 'with'(int)>",
        scene.quotedNameOf("<com.Foo: void with(int)>"));
  }

  @Test
  public void testAlreadyQuotedPassThrough() {
    Scene scene = freshScene();
    Assert.assertEquals("'with'", scene.quotedNameOf("'with'"));
    Assert.assertEquals(
        "<com.Foo: void 'with'(int)>",
        scene.quotedNameOf("<com.Foo: void 'with'(int)>"));
  }
}
