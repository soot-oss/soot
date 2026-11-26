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
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import soot.Modifier;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.JimpleBody;
import soot.Type;
import soot.VoidType;

/**
 * Type kinds, hierarchy and generic/nested type naming of the .NET frontend.
 */
public class DotnetTypesTest extends AbstractDotnetTestingFramework {

  @Test
  public void interfaceKeepsAbstractMembers() {
    SootClass iface = testClass("IShape");

    assertTrue("Type must be an interface", iface.isInterface());
    assertTrue("Interface methods must be abstract", method(iface, "Area", 0).isAbstract());
    assertTrue("Properties of interfaces must be abstract", method(iface, "get_Name", 0).isAbstract());
  }

  @Test
  public void abstractClassKeepsAbstractMethods() {
    SootClass shape = testClass("Shape");

    assertTrue("Type must be abstract", Modifier.isAbstract(shape.getModifiers()));
    assertFalse("Type must not be an interface", shape.isInterface());
    assertTrue("Area must be abstract", method(shape, "Area", 0).isAbstract());
    assertTrue("Area must have return type double", method(shape, "Area", 0).getReturnType() instanceof soot.DoubleType);
    assertTrue("Constructor of the abstract class must be concrete", method(shape, "<init>", 0).isConcrete());
  }

  @Test
  public void subclassesImplementAbstractMembers() {
    assertTrue("IShape must stay implemented by the base class", testClass("Shape").getInterfaces().stream()
        .anyMatch(i -> i.getName().equals(TEST_NAMESPACE + "IShape")));
    for (String name : new String[] { "Square", "Circle" }) {
      SootClass sub = testClass(name);
      assertEquals(TEST_NAMESPACE + "Shape", sub.getSuperclass().getName());
      assertFalse("Area must be concrete", method(sub, "Area", 0).isAbstract());
      assertTrue("Area must return double", method(sub, "Area", 0).getReturnType() instanceof soot.DoubleType);
      assertTrue("Constructor must be concrete", method(sub, "<init>", 1).isConcrete());
    }
  }

  @Test
  public void structDerivesFromValueType() {
    SootClass point = testClass("Point");

    assertFalse("Struct must not be an interface", point.isInterface());
    assertFalse("Struct must not be abstract", point.isAbstract());
    assertEquals("System.ValueType", point.getSuperclass().getName());
    assertTrue("Struct field must exist", point.declaresFieldByName("X"));
    assertTrue("Struct method must exist", method(point, "Manhattan", 0).isConcrete());

    JimpleBody body = body(point, "Manhattan", 0);
    assertJimple(body, "Point: int X>");
    assertJimple(body, "Point: int Y>");
  }

  @Test
  public void enumKeepsConstantsAndUnderlyingType() {
    SootClass color = testClass("Color");

    assertTrue("Type must be an enum", color.isEnum());
    assertEquals("System.Enum", color.getSuperclass().getName());
    for (String constant : new String[] { "Red", "Green", "Blue" }) {
      assertTrue("Enum constant " + constant + " must exist", color.declaresFieldByName(constant));
      assertTrue("Enum constant " + constant + " must be static", color.getFieldByName(constant).isStatic());
    }
    assertTrue("Underlying value field must exist", color.declaresFieldByName("value__"));
  }

  @Test
  public void genericTypeKeepsArityInName() {
    SootClass container = testClass("Container`1");

    assertNotNull("Generic type must be loadable", container);
    SootMethod map = method(container, "Map", 1);
    assertEquals("Mapper must accept a function", "System.Func`2", map.getParameterType(0).toString());
  }

  @Test
  public void nestedTypeUsesDollarSeparator() {
    SootClass nested = loadClass(TEST_NAMESPACE + "Container`1$Nested");

    assertNotNull("Nested type must be loadable", nested);
    assertTrue("Nested type must declare Depth", nested.declaresMethodByName("Depth"));
    assertEquals("Depth must return int", soot.IntType.v(), method(nested, "Depth", 0).getReturnType());
    assertTrue("Depth must be concrete", method(nested, "Depth", 0).isConcrete());
  }

  @Test
  public void methodSignaturesUseDotnetTypes() {
    SootClass instructions = testClass("Instructions");

    SootMethod sum = method(instructions, "Sum", 1);
    Type param = sum.getParameterType(0);
    assertTrue("Params array parameter must be an array type", param instanceof soot.ArrayType);
    assertEquals("int[]", param.toString());
    assertEquals("Sum must return int", soot.IntType.v(), sum.getReturnType());

    SootMethod store = method(instructions, "StoreArrayElement", 3);
    assertEquals("void", VoidType.v().toString());
    assertTrue("Store must be void", store.getReturnType() instanceof VoidType);
    assertEquals("long[]", store.getParameterType(0).toString());
  }
}
