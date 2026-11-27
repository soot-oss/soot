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
import soot.SootField;
import soot.SootMethod;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.JimpleBody;
import soot.jimple.StaticFieldRef;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.VirtualInvokeExpr;
import soot.tagkit.ConstantValueTag;

/**
 * Fields, properties, indexers, events and inheritance of C# types.
 */
public class DotnetMembersTest extends AbstractDotnetTestingFramework {

  @Test
  public void fieldsKeepNameAndModifiers() {
    SootClass clazz = testClass("Members");

    SootField constant = clazz.getFieldByName("Constant");
    assertTrue("const must be static", constant.isStatic());
    ConstantValueTag valueTag = (ConstantValueTag) constant.getTags().stream()
        .filter(t -> t instanceof ConstantValueTag).findFirst().orElse(null);
    assertNotNull("Constant value expected", valueTag);
    assertEquals(soot.jimple.IntConstant.v(42), valueTag.getConstant());

    assertTrue("public static field must stay static", clazz.getFieldByName("StaticField").isStatic());
    assertFalse("instance field must not be static", clazz.getFieldByName("instanceField").isStatic());
    assertTrue("private field must stay private",
        Modifier.isPrivate(clazz.getFieldByName("instanceField").getModifiers()));
    assertTrue("protected field must stay protected", Modifier.isProtected(clazz.getFieldByName("name").getModifiers()));
  }

  @Test
  public void propertyAccessorsReadAndWriteFields() {
    SootClass clazz = testClass("Members");
    JimpleBody getter = body(clazz, "get_InstanceField", 0);
    JimpleBody setter = body(clazz, "set_InstanceField", 1);

    assertTrue("Getter must read the backing field", fieldRefs(getter).stream()
        .anyMatch(r -> r instanceof InstanceFieldRef && r.getField().getName().equals("instanceField")));
    assertTrue("Setter must write the backing field", fieldRefs(setter).stream()
        .anyMatch(r -> r instanceof InstanceFieldRef && r.getField().getName().equals("instanceField")));
  }

  @Test
  public void autoPropertyUsesCompilerGeneratedBackingField() {
    SootClass clazz = testClass("Members");
    SootMethod getter = method(clazz, "get_StaticProperty", 0);
    assertTrue("Auto property accessor must be static", getter.isStatic());

    JimpleBody b = body(getter);
    assertJimple(b, "<StaticProperty>k__BackingField");
    assertTrue("Backing field read expected",
        fieldRefs(b).stream().anyMatch(r -> r instanceof StaticFieldRef));
  }

  @Test
  public void constructorInitializesFieldsAndCallsSuper() {
    JimpleBody b = body(testClass("Members"), "<init>", 0);

    assertJimple(b, "specialinvoke this.<System.Object: void <init>()>");
    assertJimple(b, "instanceField> = 1");
  }

  @Test
  public void indexerAccessorsTakeIndexAndValue() {
    SootClass clazz = testClass("Members");

    assertEquals("Getter parameter count", 1, method(clazz, "get_Item", 1).getParameterCount());
    assertEquals("Setter parameter count", 2, method(clazz, "set_Item", 2).getParameterCount());
    assertEquals("Setter last parameter type", "int",
        method(clazz, "set_Item", 2).getParameterType(1).toString());

    JimpleBody getter = body(clazz, "get_Item", 1);
    assertJimple(getter, "instanceField>");
    assertJimple(getter, " + ");
  }

  @Test
  public void eventAccessorsAreGenerated() {
    SootClass clazz = testClass("Members");

    SootMethod add = method(clazz, "add_Changed", 1);
    SootMethod remove = method(clazz, "remove_Changed", 1);
    assertEquals("System.EventHandler", add.getParameterType(0).toString());
    assertEquals("System.EventHandler", remove.getParameterType(0).toString());
    assertTrue("Event accessors must have a body", add.isConcrete() && remove.isConcrete());
    assertFalse("Event accessor bodies must not be empty", body(add).getUnits().isEmpty());
    assertFalse("Event accessor bodies must not be empty", body(remove).getUnits().isEmpty());
  }

  @Test
  public void eventInvocationUsesNullCheckAndDelegateInvoke() {
    JimpleBody b = body(testClass("Members"), "RaiseChanged", 0);

    assertJimple(b, "Changed>");
    assertUnit(b, soot.jimple.IfStmt.class);
    assertJimple(b, "doInvoke(System.Object,System.EventArgs)");
  }

  @Test
  public void delegateInvocationUsesDoInvoke() {
    JimpleBody b = body(testClass("Members"), "SetNameViaDelegate", 1);

    assertJimple(b, "<System.Action`1: void doInvoke(System.Object)>");
  }

  @Test
  public void derivedClassExtendsBaseAndKeepsOwnMembers() {
    SootClass derived = testClass("DerivedMembers");

    assertEquals(TEST_NAMESPACE + "Members", derived.getSuperclass().getName());
    assertTrue("Additional field must exist", derived.declaresFieldByName("Additional"));
    assertFalse("Additional field must not be static", derived.getFieldByName("Additional").isStatic());

    SootMethod toString = method(derived, "ToString", 0);
    JimpleBody b = body(toString);
    assertJimple(b, "specialinvoke this.<System.Object: System.String ToString()>");
    assertTrue("Base call must be a special invoke",
        invokeExprs(b).stream().anyMatch(e -> e instanceof soot.jimple.SpecialInvokeExpr));
  }

  @Test
  public void virtualDispatchIsPreservedInBaseClass() {
    JimpleBody b = body(testClass("Shape"), "ToString", 0);

    assertEquals("Both abstract members must be called virtually", 2,
        invokeExprs(b).stream().filter(e -> e instanceof VirtualInvokeExpr).count());
    assertTrue("Name getter must be invoked", invokeExprs(b).stream()
        .anyMatch(e -> e instanceof InstanceInvokeExpr && e.getMethodRef().getName().equals("get_Name")));
    assertTrue("Concat must be a static call",
        invokeExprs(b).stream().anyMatch(e -> e instanceof StaticInvokeExpr));
  }
}
