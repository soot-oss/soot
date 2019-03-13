/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 1999 Raja Vallee-Rai
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
package soot.jbco.jimpleTransformations;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.util.Map;

import org.junit.Test;


public class ClassRenamerTest {

  @Test
  public void getName() {
    assertThat(ClassRenamer.v().getName(), equalTo(ClassRenamer.name));
  }

  @Test
  public void getDependencies() {
    assertThat(ClassRenamer.v().getDependencies(), equalTo(new String[] { ClassRenamer.name }));
  }

  @Test
  public void getPackageName() {
    assertNull(ClassRenamer.getPackageName(""));
    assertNull(ClassRenamer.getPackageName(null));
    assertNull(ClassRenamer.getPackageName("."));
    assertNull(ClassRenamer.getPackageName("ClassName"));
    assertEquals("com.sable", ClassRenamer.getPackageName("com.sable.Soot"));
  }

  @Test
  public void getClassName() {
    assertNull(ClassRenamer.getClassName(""));
    assertNull(ClassRenamer.getClassName(null));
    assertNull(ClassRenamer.getClassName("."));
    assertEquals("ClassName", ClassRenamer.getClassName("ClassName"));
    assertEquals("Soot", ClassRenamer.getClassName("com.sable.Soot"));
    assertNull(ClassRenamer.getClassName("com.sable."));
  }

  @Test
  public void getOrAddNewName_cachingName() {
    ClassRenamer.v().setRemovePackages(false);
    ClassRenamer.v().setRenamePackages(false);

    final String newName = ClassRenamer.v().getOrAddNewName(null, "ClassName");
    assertThat(newName, not(containsString(".")));

    Map<String, String> mapping = ClassRenamer.v().getClassNameMapping((pOldName, pNewName) -> pOldName.equals("ClassName"));
    assertThat(mapping, hasEntry("ClassName", newName));
    assertThat(mapping.size(), equalTo(1));

    assertThat(ClassRenamer.v().getOrAddNewName(null, "ClassName"), equalTo(newName));

    mapping = ClassRenamer.v().getClassNameMapping((pOldName, pNewName) -> pOldName.equals("ClassName"));
    assertThat(mapping, hasEntry("ClassName", newName));
    assertThat(mapping.size(), equalTo(1));
  }

  @Test
  public void getOrAddNewName_cachingPackage() {
    ClassRenamer.v().setRemovePackages(false);
    ClassRenamer.v().setRenamePackages(false);

    final String newName = ClassRenamer.v().getOrAddNewName("pac.age", "ClassName");
    assertThat(newName, allOf(startsWith("pac.age."), not(endsWith("ClassName"))));
    assertThat(newName.split("\\.").length, equalTo(3));

    assertThat(ClassRenamer.v().getOrAddNewName("pac.age", "ClassName"), equalTo(newName));
  }

  @Test
  public void getOrAddNewName_nullClassName() {
    ClassRenamer.v().setRemovePackages(false);
    ClassRenamer.v().setRenamePackages(false);

    final String newName = ClassRenamer.v().getOrAddNewName("pac.age", null);
    assertThat(newName, startsWith("pac.age."));
    assertThat(newName.split("\\.").length, equalTo(3));

    assertThat(ClassRenamer.v().getOrAddNewName("pac.age", null), not(equalTo(newName)));
  }

  @Test
  public void getOrAddNewName_renamePackage() {
    ClassRenamer.v().setRemovePackages(false);
    ClassRenamer.v().setRenamePackages(true);

    final String newName = ClassRenamer.v().getOrAddNewName("pac.age.getOrAddNewName_renamePackage", "ClassName");
    assertThat(newName, allOf(not(startsWith("pac.age.getOrAddNewName_renamePackage.")), not(endsWith("ClassName"))));
    assertThat(newName.split("\\.").length, equalTo(4));

    assertThat(ClassRenamer.v().getOrAddNewName("pac.age.getOrAddNewName_renamePackage", "ClassName"), equalTo(newName));
  }

  @Test
  public void getOrAddNewName_renamePackage_nullPackage() {
    ClassRenamer.v().setRemovePackages(false);
    ClassRenamer.v().setRenamePackages(true);

    final String newName = ClassRenamer.v().getOrAddNewName(null, "ClassName");
    assertThat(newName, allOf(not(endsWith("ClassName")), not(containsString("."))));

    final String newName0 = ClassRenamer.v().getOrAddNewName(null, "ClassName");
    assertThat(newName0, equalTo(newName)); // package names and class names are equal

    final String newName1 = ClassRenamer.v().getOrAddNewName(null, "ClassName1");
    assertThat(newName1, not(equalTo(newName)));
    assertThat(newName1.split("\\.").length, equalTo(2));
    assertThat(newName.split("\\.")[0], equalTo(newName.split("\\.")[0])); // package names are equal
  }

  @Test
  public void getOrAddNewName_removePackage() {
    ClassRenamer.v().setRemovePackages(true);

    String newName = ClassRenamer.v().getOrAddNewName("a.b.c", "ClassName");
    assertThat(newName, allOf(not(endsWith("ClassName")), not(containsString("."))));

    String packageName = "a.b.c";
    for (int i = 0; i < 100; i++) {
      packageName = packageName + ".p" + i;
      newName = ClassRenamer.v().getOrAddNewName(packageName, "ClassName");
      assertThat(newName, allOf(not(endsWith("ClassName")), not(containsString("."))));
    }
  }

}
