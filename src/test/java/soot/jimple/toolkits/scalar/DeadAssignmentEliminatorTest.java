package soot.jimple.toolkits.scalar;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2020 Raja Vallee-Rai and others
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

import org.junit.Before;
import org.junit.Test;

import soot.ArrayType;
import soot.DoubleType;
import soot.G;
import soot.IntType;
import soot.Local;
import soot.Modifier;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.UnitPatchingChain;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.NullConstant;
import soot.options.Options;
import soot.util.Chain;

/**
 * Tests for the dead assignment eliminator.
 */
public class DeadAssignmentEliminatorTest {

  /**
   * Initializes Soot.
   */
  @Before
  public void initialize() {
    // load necessary classes
    G.reset();
    Options o = Options.v();
    o.set_whole_program(true);
    Scene.v().loadNecessaryClasses();
    Scene.v().loadClassAndSupport("java.lang.Object");
    Scene.v().loadClassAndSupport("java.lang.String");
    Scene.v().loadClassAndSupport("java.lang.Number");
  }

  /**
   * Tests if the eliminator keeps array length expression which might cause NPEs.
   */
  @Test
  public void keepArrayLength() {
    // create test method and body
    SootClass cl = new SootClass("TestClass", Modifier.PUBLIC);
    SootMethod method = new SootMethod("testMethod", Collections.singletonList(RefType.v("java.lang.Object")),
        ArrayType.v(IntType.v(), 1), Modifier.PUBLIC);
    cl.addMethod(method);
    JimpleBody body = Jimple.v().newBody(method);
    method.setActiveBody(body);

    // create locals
    Chain<Local> locals = body.getLocals();
    Local a = Jimple.v().newLocal("a", ArrayType.v(IntType.v(), 1));
    locals.add(a);
    Local b = Jimple.v().newLocal("b", IntType.v());
    locals.add(b);

    // create code
    UnitPatchingChain units = body.getUnits();
    Unit identity0 = Jimple.v().newIdentityStmt(a, Jimple.v().newParameterRef(RefType.v("java.lang.Object"), 0));
    units.add(identity0);
    Unit cast0 = Jimple.v().newAssignStmt(b, Jimple.v().newLengthExpr(a));
    units.add(cast0);
    Unit ret = Jimple.v().newReturnStmt(b);
    units.add(ret);

    // execute transform
    DeadAssignmentEliminator.v().internalTransform(body, "testPhase", Collections.emptyMap());

    // check resulting code (length statement should be preserved)
    Iterator<Unit> it = units.iterator();
    assertEquals(identity0, it.next());
    assertEquals(cast0, it.next());
    assertEquals(ret, it.next());
    assertEquals(3, units.size());
  }

  /**
   * Tests if the eliminator keeps casts which can throw ClassCastExceptions.
   */
  @Test
  public void keepEssentialCast() {
    // create test method and body
    SootClass cl = new SootClass("TestClass", Modifier.PUBLIC);
    SootMethod method = new SootMethod("testMethod", Collections.singletonList(RefType.v("java.lang.Object")),
        ArrayType.v(IntType.v(), 1), Modifier.PUBLIC);
    cl.addMethod(method);
    JimpleBody body = Jimple.v().newBody(method);
    method.setActiveBody(body);

    // create locals
    Chain<Local> locals = body.getLocals();
    Local a = Jimple.v().newLocal("a", IntType.v());
    locals.add(a);
    Local b = Jimple.v().newLocal("b", IntType.v());
    locals.add(b);
    Local c = Jimple.v().newLocal("c", IntType.v());
    locals.add(c);
    Local d = Jimple.v().newLocal("d", IntType.v());
    locals.add(d);

    // create code
    UnitPatchingChain units = body.getUnits();
    Unit identity0 = Jimple.v().newIdentityStmt(a, Jimple.v().newParameterRef(RefType.v("java.lang.Object"), 0));
    units.add(identity0);
    Unit cast0 = Jimple.v().newAssignStmt(b, Jimple.v().newCastExpr(a, ArrayType.v(IntType.v(), 1)));
    units.add(cast0);
    Unit cast1 = Jimple.v().newAssignStmt(c, Jimple.v().newCastExpr(a, RefType.v("java.lang.Number")));
    units.add(cast1);
    Unit cast2 = Jimple.v().newAssignStmt(d, Jimple.v().newCastExpr(NullConstant.v(), RefType.v("java.lang.Number")));
    units.add(cast2);
    Unit ret = Jimple.v().newReturnStmt(b);
    units.add(ret);

    // execute transform
    DeadAssignmentEliminator.v().internalTransform(body, "testPhase", Collections.emptyMap());

    // check resulting code (cast should be removed)
    Iterator<Unit> it = units.iterator();
    assertEquals(identity0, it.next());
    assertEquals(cast0, it.next());
    assertEquals(cast1, it.next());
    assertEquals(ret, it.next());
    assertEquals(4, units.size());
  }

  /**
   * Tests if the eliminator removes primitive casts which cannot throw ClassCastExceptions.
   */
  @Test
  public void removePrimitiveCast() {
    // create test method and body
    SootClass cl = new SootClass("TestClass", Modifier.PUBLIC);
    SootMethod method = new SootMethod("testMethod", Arrays.asList(IntType.v(), IntType.v()), IntType.v(), Modifier.PUBLIC);
    cl.addMethod(method);
    JimpleBody body = Jimple.v().newBody(method);
    method.setActiveBody(body);

    // create locals
    Chain<Local> locals = body.getLocals();
    Local a = Jimple.v().newLocal("a", IntType.v());
    locals.add(a);
    Local b = Jimple.v().newLocal("b", IntType.v());
    locals.add(b);
    Local c = Jimple.v().newLocal("c", IntType.v());
    locals.add(c);
    Local d = Jimple.v().newLocal("d", DoubleType.v());
    locals.add(d);

    // create code
    UnitPatchingChain units = body.getUnits();
    Unit identity0 = Jimple.v().newIdentityStmt(a, Jimple.v().newParameterRef(IntType.v(), 0));
    units.add(identity0);
    Unit identity1 = Jimple.v().newIdentityStmt(b, Jimple.v().newParameterRef(IntType.v(), 1));
    units.add(identity1);
    Unit addition = Jimple.v().newAssignStmt(c, Jimple.v().newAddExpr(a, b));
    units.add(addition);
    Unit cast = Jimple.v().newAssignStmt(d, Jimple.v().newCastExpr(a, DoubleType.v()));
    units.add(cast);
    Unit ret = Jimple.v().newReturnStmt(c);
    units.add(ret);

    // execute transform
    DeadAssignmentEliminator.v().internalTransform(body, "testPhase", Collections.emptyMap());

    // check resulting code (cast should be removed)
    Iterator<Unit> it = units.iterator();
    assertEquals(identity0, it.next());
    assertEquals(identity1, it.next());
    assertEquals(addition, it.next());
    assertEquals(ret, it.next());
    assertEquals(4, units.size());
  }
}
