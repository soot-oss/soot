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
package soot;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BasicTest {

  @Test
  public void sootClassNamePackageTests() {
    G.reset();

    Scene.v().loadNecessaryClasses();

    SootClass sc1 = new SootClass("Class1");
    SootClass sc2 = new SootClass("package1.Class2");
    SootClass sc3 = new SootClass("package2.Class3");

    Scene.v().addClass(sc1);
    Scene.v().addClass(sc2);
    Scene.v().addClass(sc3);

    assertEquals("Class1", sc1.getName());
    assertEquals("Class1", sc1.getShortName());
    assertEquals("Class1", sc1.getJavaStyleName());
    assertEquals("Class1", sc1.getShortJavaStyleName());
    assertEquals("", sc1.getPackageName());
    assertEquals("", sc1.getJavaPackageName());

    assertEquals("package1.Class2", sc2.getName());
    assertEquals("Class2", sc2.getShortName());
    assertEquals("package1.Class2", sc2.getJavaStyleName());
    assertEquals("Class2", sc2.getShortJavaStyleName());
    assertEquals("package1", sc2.getPackageName());
    assertEquals("package1", sc2.getJavaPackageName());

    assertEquals("package2.Class3", sc3.getName());
    assertEquals("Class3", sc3.getShortName());
    assertEquals("package2.Class3", sc3.getJavaStyleName());
    assertEquals("Class3", sc3.getShortJavaStyleName());
    assertEquals("package2", sc3.getPackageName());
    assertEquals("package2", sc3.getJavaPackageName());
  }

}
