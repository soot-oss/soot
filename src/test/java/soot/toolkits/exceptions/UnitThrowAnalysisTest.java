package soot.toolkits.exceptions;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import soot.AnySubType;
import soot.ArrayType;
import soot.DoubleType;
import soot.FloatType;
import soot.IntType;
import soot.Local;
import soot.LongType;
import soot.Modifier;
import soot.RefLikeType;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootFieldRef;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.VoidType;
import soot.grimp.Grimp;
import soot.jimple.ArrayRef;
import soot.jimple.DivExpr;
import soot.jimple.DoubleConstant;
import soot.jimple.FloatConstant;
import soot.jimple.IfStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;
import soot.jimple.LongConstant;
import soot.jimple.RemExpr;
import soot.jimple.StaticFieldRef;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.StringConstant;
import soot.jimple.ThrowStmt;
import soot.jimple.VirtualInvokeExpr;
import soot.toolkits.exceptions.ExceptionTestUtility.ExceptionHashSet;

public class UnitThrowAnalysisTest {

	static {
		Scene.v().loadBasicClasses();
	}

	class ImmaculateInvokeUnitThrowAnalysis extends UnitThrowAnalysis {
		// A variant of UnitThrowAnalysis which assumes that invoked
		// methods will never throw any exceptions, rather than that
		// they might throw anything Throwable. This allows us to
		// test that individual arguments to invocations are being
		// examined.

		protected ThrowableSet mightThrow(SootMethod m) {
			return ThrowableSet.Manager.v().EMPTY;
		}
	}

	UnitThrowAnalysis unitAnalysis;
	UnitThrowAnalysis immaculateAnalysis;

	// A collection of Grimp values and expressions used in various tests:
	protected StaticFieldRef floatStaticFieldRef;
	protected Local floatLocal;
	protected FloatConstant floatConstant;
	protected Local floatConstantLocal;
	protected InstanceFieldRef floatInstanceFieldRef;
	protected ArrayRef floatArrayRef;
	protected VirtualInvokeExpr floatVirtualInvoke;
	protected StaticInvokeExpr floatStaticInvoke;

	private ExceptionTestUtility utility;

	@Before
	public void setUp() {
		unitAnalysis = new UnitThrowAnalysis();
		immaculateAnalysis = new ImmaculateInvokeUnitThrowAnalysis();

		// Ensure the Exception classes we need are represented in Soot:
		utility = new ExceptionTestUtility();

		List voidList = new ArrayList();
		SootClass bogusClass = new SootClass("BogusClass");
		bogusClass.addMethod(Scene.v().makeSootMethod("floatFunction", voidList, FloatType.v()));
		bogusClass.addMethod(Scene.v().makeSootMethod("floatFunction",
				Arrays.asList(new Type[] { FloatType.v(), FloatType.v(), }), FloatType.v(), Modifier.STATIC));
		SootFieldRef nanFieldRef = Scene.v().makeFieldRef(Scene.v().getSootClass("java.lang.Float"), "NaN",
				FloatType.v(), true);
		floatStaticFieldRef = Grimp.v().newStaticFieldRef(nanFieldRef);
		floatLocal = Grimp.v().newLocal("local", FloatType.v());
		floatConstant = FloatConstant.v(33.42f);
		floatConstantLocal = Grimp.v().newLocal("local", RefType.v("soot.jimple.FloatConstant"));
		SootFieldRef valueFieldRef = Scene.v().makeFieldRef(bogusClass, "value", FloatType.v(), false);
		floatInstanceFieldRef = Grimp.v().newInstanceFieldRef(floatConstantLocal, valueFieldRef);
		floatArrayRef = Grimp.v().newArrayRef(Jimple.v().newLocal("local1", FloatType.v()), IntConstant.v(0));
		floatVirtualInvoke = Grimp.v().newVirtualInvokeExpr(floatConstantLocal,
				Scene.v().makeMethodRef(bogusClass, "floatFunction", voidList, FloatType.v(), false), voidList);
		floatStaticInvoke = Grimp.v().newStaticInvokeExpr(
				Scene.v().makeMethodRef(bogusClass, "floatFunction",
						Arrays.asList(new Type[] { FloatType.v(), FloatType.v(), }), FloatType.v(), true),
				Arrays.asList(new Value[] { floatStaticFieldRef, floatArrayRef, }));
	}

	@Test
	public void testJBreakpointStmt() {
		Stmt s = Grimp.v().newBreakpointStmt();
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(s)));
	}

	@Test
	public void testGBreakpointStmt() {
		Stmt s = Grimp.v().newBreakpointStmt();
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(s)));
	}

	@Ignore("Fails")
	@Test
	public void testJInvokeStmt() {
		List voidList = new ArrayList();
		Stmt s = Jimple.v().newInvokeStmt(
				Jimple.v().newVirtualInvokeExpr(Jimple.v().newLocal("local1", RefType.v("java.lang.Object")),
						Scene.v().makeMethodRef(Scene.v().getSootClass("java.lang.Object"), "wait", voidList,
								VoidType.v(), false),
						voidList));
		ExceptionHashSet expectedRep = new ExceptionHashSet(utility.VM_AND_RESOLVE_METHOD_ERRORS_REP);
		expectedRep.add(utility.NULL_POINTER_EXCEPTION);
		ExceptionHashSet expectedCatch = new ExceptionHashSet(utility.VM_AND_RESOLVE_METHOD_ERRORS_PLUS_SUPERTYPES);
		expectedCatch.add(utility.NULL_POINTER_EXCEPTION);
		expectedCatch.add(utility.RUNTIME_EXCEPTION);
		expectedCatch.add(utility.EXCEPTION);
		assertTrue(
				ExceptionTestUtility.sameMembers(expectedRep, Collections.EMPTY_SET, immaculateAnalysis.mightThrow(s)));
		assertEquals(expectedCatch, utility.catchableSubset(immaculateAnalysis.mightThrow(s)));
		assertTrue(ExceptionTestUtility.sameMembers(utility.ALL_THROWABLES_REP, Collections.EMPTY_SET,
				unitAnalysis.mightThrow(s)));
		assertEquals(utility.ALL_TEST_THROWABLES, utility.catchableSubset(unitAnalysis.mightThrow(s)));

		SootClass bogusClass = new SootClass("BogusClass");
		bogusClass.addMethod(Scene.v().makeSootMethod("emptyMethod", voidList, VoidType.v(), Modifier.STATIC));
		s = Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(
				Scene.v().makeMethodRef(bogusClass, "emptyMethod", voidList, VoidType.v(), true), voidList));
		assertTrue(ExceptionTestUtility.sameMembers(utility.ALL_ERRORS_REP, Collections.EMPTY_SET,
				immaculateAnalysis.mightThrow(s)));
		assertEquals(utility.ALL_TEST_ERRORS_PLUS_SUPERTYPES,
				utility.catchableSubset(immaculateAnalysis.mightThrow(s)));
		assertTrue(ExceptionTestUtility.sameMembers(utility.ALL_THROWABLES_REP, Collections.EMPTY_SET,
				unitAnalysis.mightThrow(s)));
		assertEquals(utility.ALL_TEST_THROWABLES, utility.catchableSubset(unitAnalysis.mightThrow(s)));
	}

	@Ignore("Fails")
	@Test
	public void testGInvokeStmt() {
		List voidList = new ArrayList();
		Stmt s = Grimp.v().newInvokeStmt(
				Grimp.v().newVirtualInvokeExpr(Grimp.v().newLocal("local1", RefType.v("java.lang.Object")),
						Scene.v().makeMethodRef(Scene.v().getSootClass("java.lang.Object"), "wait", voidList,
								VoidType.v(), false),
						voidList));
		ExceptionHashSet expectedRep = new ExceptionHashSet(utility.VM_AND_RESOLVE_METHOD_ERRORS_REP);
		expectedRep.add(utility.NULL_POINTER_EXCEPTION);
		ExceptionHashSet expectedCatch = new ExceptionHashSet(utility.VM_AND_RESOLVE_METHOD_ERRORS_PLUS_SUPERTYPES);
		expectedCatch.add(utility.NULL_POINTER_EXCEPTION);
		expectedCatch.add(utility.RUNTIME_EXCEPTION);
		expectedCatch.add(utility.EXCEPTION);
		assertTrue(
				ExceptionTestUtility.sameMembers(expectedRep, Collections.EMPTY_SET, immaculateAnalysis.mightThrow(s)));
		assertEquals(expectedCatch, utility.catchableSubset(immaculateAnalysis.mightThrow(s)));
		assertTrue(ExceptionTestUtility.sameMembers(utility.ALL_THROWABLES_REP, Collections.EMPTY_SET,
				unitAnalysis.mightThrow(s)));
		assertEquals(utility.ALL_TEST_THROWABLES, utility.catchableSubset(unitAnalysis.mightThrow(s)));

		SootClass bogusClass = new SootClass("BogusClass");
		bogusClass.addMethod(Scene.v().makeSootMethod("emptyMethod", voidList, VoidType.v(), Modifier.STATIC));
		s = Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(
				Scene.v().makeMethodRef(bogusClass, "emptyMethod", voidList, VoidType.v(), true), voidList));
		s = Grimp.v().newInvokeStmt(Grimp.v().newStaticInvokeExpr(
				Scene.v().makeMethodRef(bogusClass, "emptyMethod", voidList, VoidType.v(), true), voidList));
		assertTrue(ExceptionTestUtility.sameMembers(utility.ALL_ERRORS_REP, Collections.EMPTY_SET,
				immaculateAnalysis.mightThrow(s)));
		assertEquals(utility.ALL_TEST_ERRORS_PLUS_SUPERTYPES,
				utility.catchableSubset(immaculateAnalysis.mightThrow(s)));
		assertTrue(ExceptionTestUtility.sameMembers(utility.ALL_THROWABLES_REP, Collections.EMPTY_SET,
				unitAnalysis.mightThrow(s)));
		assertEquals(utility.ALL_TEST_THROWABLES, utility.catchableSubset(unitAnalysis.mightThrow(s)));
	}

	@Test
	public void testJAssignStmt() {

		// local0 = 0
		Stmt s = Jimple.v().newAssignStmt(Jimple.v().newLocal("local0", IntType.v()), IntConstant.v(0));
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(s)));

		ArrayRef arrayRef = Jimple.v().newArrayRef(
				Jimple.v().newLocal("local1", ArrayType.v(RefType.v("java.lang.Object"), 1)), IntConstant.v(0));
		Local scalarRef = Jimple.v().newLocal("local2", RefType.v("java.lang.Object"));

		// local2 = local1[0]
		s = Jimple.v().newAssignStmt(scalarRef, arrayRef);

		Set<RefLikeType> expectedRep = new ExceptionHashSet<RefLikeType>(utility.VM_ERRORS);
		expectedRep.add(utility.NULL_POINTER_EXCEPTION);
		expectedRep.add(utility.ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION);
		assertTrue(ExceptionTestUtility.sameMembers(expectedRep, Collections.<AnySubType>emptySet(),
				unitAnalysis.mightThrow(s)));

		Set expectedCatch = new ExceptionHashSet(utility.VM_ERRORS_PLUS_SUPERTYPES);
		expectedCatch.add(utility.NULL_POINTER_EXCEPTION);
		expectedCatch.add(utility.ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION);
		expectedCatch.add(utility.INDEX_OUT_OF_BOUNDS_EXCEPTION);
		expectedCatch.add(utility.RUNTIME_EXCEPTION);
		expectedCatch.add(utility.EXCEPTION);
		assertEquals(expectedCatch, utility.catchableSubset(unitAnalysis.mightThrow(s)));

		// local1[0] = local2
		s = Jimple.v().newAssignStmt(arrayRef, scalarRef);
		expectedRep.add(utility.ARRAY_STORE_EXCEPTION);
		assertTrue(ExceptionTestUtility.sameMembers(expectedRep, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));
		expectedCatch.add(utility.ARRAY_STORE_EXCEPTION);
		assertEquals(expectedCatch, utility.catchableSubset(unitAnalysis.mightThrow(s)));
	}

	@Test
	public void testGAssignStmt() {

		// local0 = 0
		Stmt s = Grimp.v().newAssignStmt(Grimp.v().newLocal("local0", IntType.v()), IntConstant.v(0));
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(s)));

		ArrayRef arrayRef = Grimp.v().newArrayRef(
				Grimp.v().newLocal("local1", ArrayType.v(RefType.v("java.lang.Object"), 1)), IntConstant.v(0));
		Local scalarRef = Grimp.v().newLocal("local2", RefType.v("java.lang.Object"));

		// local2 = local1[0]
		s = Grimp.v().newAssignStmt(scalarRef, arrayRef);

		Set expectedRep = new ExceptionHashSet(utility.VM_ERRORS);
		expectedRep.add(utility.NULL_POINTER_EXCEPTION);
		expectedRep.add(utility.ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION);
		assertTrue(ExceptionTestUtility.sameMembers(expectedRep, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));
		Set expectedCatch = new ExceptionHashSet(utility.VM_ERRORS_PLUS_SUPERTYPES);
		expectedCatch.add(utility.NULL_POINTER_EXCEPTION);
		expectedCatch.add(utility.ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION);
		expectedCatch.add(utility.INDEX_OUT_OF_BOUNDS_EXCEPTION);
		expectedCatch.add(utility.RUNTIME_EXCEPTION);
		expectedCatch.add(utility.EXCEPTION);
		assertEquals(expectedCatch, utility.catchableSubset(unitAnalysis.mightThrow(s)));

		// local1[0] = local2
		s = Grimp.v().newAssignStmt(arrayRef, scalarRef);
		expectedRep.add(utility.ARRAY_STORE_EXCEPTION);
		assertTrue(ExceptionTestUtility.sameMembers(expectedRep, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));
		expectedCatch.add(utility.ARRAY_STORE_EXCEPTION);
		assertEquals(expectedCatch, utility.catchableSubset(unitAnalysis.mightThrow(s)));
	}

	@Test
	public void testJIdentityStmt() {

		Stmt s = Jimple.v().newIdentityStmt(Grimp.v().newLocal("local0", IntType.v()),
				Jimple.v().newCaughtExceptionRef());
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(s)));

		s = Jimple.v().newIdentityStmt(Grimp.v().newLocal("local0", RefType.v("java.lang.NullPointerException")),
				Jimple.v().newThisRef(RefType.v("java.lang.NullPointerException")));
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(s)));

		s = Jimple.v().newIdentityStmt(Grimp.v().newLocal("local0", RefType.v("java.lang.NullPointerException")),
				Jimple.v().newParameterRef(RefType.v("java.lang.NullPointerException"), 0));
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(s)));
	}

	@Test
	public void testGIdentityStmt() {

		Stmt s = Grimp.v().newIdentityStmt(Grimp.v().newLocal("local0", IntType.v()),
				Grimp.v().newCaughtExceptionRef());
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(s)));

		s = Grimp.v().newIdentityStmt(Grimp.v().newLocal("local0", RefType.v("java.lang.NullPointerException")),
				Grimp.v().newThisRef(RefType.v("java.lang.NullPointerException")));
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(s)));

		s = Grimp.v().newIdentityStmt(Grimp.v().newLocal("local0", RefType.v("java.lang.NullPointerException")),
				Grimp.v().newParameterRef(RefType.v("java.lang.NullPointerException"), 0));
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(s)));
	}

	@Test
	public void testJEnterMonitorStmt() {
		Stmt s = Jimple.v().newEnterMonitorStmt(StringConstant.v("test"));

		Set expectedRep = new ExceptionHashSet(utility.VM_ERRORS);
		Set expectedCatch = new ExceptionHashSet(utility.VM_ERRORS_PLUS_SUPERTYPES);
		expectedRep.add(utility.NULL_POINTER_EXCEPTION);
		assertTrue(ExceptionTestUtility.sameMembers(expectedRep, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));

		expectedCatch.add(utility.NULL_POINTER_EXCEPTION);
		expectedCatch.add(utility.RUNTIME_EXCEPTION);
		expectedCatch.add(utility.EXCEPTION);
		assertEquals(expectedCatch, utility.catchableSubset(unitAnalysis.mightThrow(s)));
	}

	@Test
	public void testGEnterMonitorStmt() {
		Stmt s = Grimp.v().newEnterMonitorStmt(StringConstant.v("test"));

		Set expectedRep = new ExceptionHashSet(utility.VM_ERRORS);
		Set expectedCatch = new ExceptionHashSet(utility.VM_ERRORS_PLUS_SUPERTYPES);

		expectedRep.add(utility.NULL_POINTER_EXCEPTION);
		assertTrue(ExceptionTestUtility.sameMembers(expectedRep, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));

		expectedCatch.add(utility.NULL_POINTER_EXCEPTION);
		expectedCatch.add(utility.RUNTIME_EXCEPTION);
		expectedCatch.add(utility.EXCEPTION);
		assertEquals(expectedCatch, utility.catchableSubset(unitAnalysis.mightThrow(s)));
	}

	@Test
	public void testJExitMonitorStmt() {
		Stmt s = Jimple.v().newExitMonitorStmt(StringConstant.v("test"));

		Set expectedRep = new ExceptionHashSet(utility.VM_ERRORS);
		expectedRep.add(utility.ILLEGAL_MONITOR_STATE_EXCEPTION);
		expectedRep.add(utility.NULL_POINTER_EXCEPTION);
		assertTrue(ExceptionTestUtility.sameMembers(expectedRep, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));

		Set expectedCatch = new ExceptionHashSet(utility.VM_ERRORS_PLUS_SUPERTYPES);
		expectedCatch.add(utility.ILLEGAL_MONITOR_STATE_EXCEPTION);
		expectedCatch.add(utility.NULL_POINTER_EXCEPTION);
		expectedCatch.add(utility.RUNTIME_EXCEPTION);
		expectedCatch.add(utility.EXCEPTION);
		assertEquals(expectedCatch, utility.catchableSubset(unitAnalysis.mightThrow(s)));
	}

	@Test
	public void testGExitMonitorStmt() {
		Stmt s = Grimp.v().newExitMonitorStmt(StringConstant.v("test"));

		Set expectedRep = new ExceptionHashSet(utility.VM_ERRORS);
		expectedRep.add(utility.ILLEGAL_MONITOR_STATE_EXCEPTION);
		expectedRep.add(utility.NULL_POINTER_EXCEPTION);
		assertTrue(ExceptionTestUtility.sameMembers(expectedRep, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));

		Set expectedCatch = new ExceptionHashSet(utility.VM_ERRORS_PLUS_SUPERTYPES);
		expectedCatch.add(utility.ILLEGAL_MONITOR_STATE_EXCEPTION);
		expectedCatch.add(utility.NULL_POINTER_EXCEPTION);
		expectedCatch.add(utility.RUNTIME_EXCEPTION);
		expectedCatch.add(utility.EXCEPTION);

		assertEquals(expectedCatch, utility.catchableSubset(unitAnalysis.mightThrow(s)));
	}

	@Test
	public void testJGotoStmt() {
		Stmt nop = Jimple.v().newNopStmt();
		Stmt s = Jimple.v().newGotoStmt(nop);
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(s)));
	}

	@Test
	public void testGGotoStmt() {
		Stmt nop = Grimp.v().newNopStmt();
		Stmt s = Grimp.v().newGotoStmt(nop);
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(s)));
	}

	@Test
	public void testJIfStmt() {
		IfStmt s = Jimple.v().newIfStmt(Jimple.v().newEqExpr(IntConstant.v(1), IntConstant.v(1)), (Unit) null);
		s.setTarget(s); // A very tight infinite loop.
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(s)));
	}

	@Test
	public void testGIfStmt() {
		IfStmt s = Grimp.v().newIfStmt(Grimp.v().newEqExpr(IntConstant.v(1), IntConstant.v(1)), (Unit) null);
		s.setTarget(s); // A very tight infinite loop.
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(s)));
	}

	@Test
	public void testJLookupSwitchStmt() {
		Stmt target = Jimple.v().newAssignStmt(Jimple.v().newLocal("local0", IntType.v()), IntConstant.v(0));
		Stmt s = Jimple.v().newLookupSwitchStmt(IntConstant.v(1), Collections.singletonList(IntConstant.v(1)),
				Collections.singletonList(target), target);
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(s)));
	}

	@Test
	public void testGLookupSwitchStmt() {
		Stmt target = Grimp.v().newAssignStmt(Grimp.v().newLocal("local0", IntType.v()), IntConstant.v(0));
		Stmt s = Grimp.v().newLookupSwitchStmt(IntConstant.v(1), Arrays.asList(new Value[] { IntConstant.v(1) }),
				Arrays.asList(new Unit[] { target }), target);
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(s)));
	}

	@Test
	public void testJNopStmt() {
		Stmt s = Jimple.v().newNopStmt();
		Set expectedRep = new ExceptionHashSet(utility.VM_ERRORS);
		assertTrue(ExceptionTestUtility.sameMembers(expectedRep, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));
	}

	@Test
	public void testGNopStmt() {
		Stmt s = Grimp.v().newNopStmt();
		Set expectedRep = new ExceptionHashSet(utility.VM_ERRORS);
		assertTrue(ExceptionTestUtility.sameMembers(expectedRep, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));
	}

	@Ignore("Fails")
	@Test
	public void testJReturnStmt() {
		Stmt s = Jimple.v().newReturnStmt(IntConstant.v(1));

		Set expectedRep = new ExceptionHashSet(utility.VM_ERRORS);
		expectedRep.add(utility.ILLEGAL_MONITOR_STATE_EXCEPTION);
		assertTrue(ExceptionTestUtility.sameMembers(expectedRep, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));

		Set expectedCatch = new ExceptionHashSet(utility.VM_ERRORS_PLUS_SUPERTYPES);
		expectedCatch.add(utility.ILLEGAL_MONITOR_STATE_EXCEPTION);
		expectedCatch.add(utility.RUNTIME_EXCEPTION);
		expectedCatch.add(utility.EXCEPTION);
		assertEquals(expectedCatch, utility.catchableSubset(unitAnalysis.mightThrow(s)));
	}

	@Ignore("Fails")
	@Test
	public void testGReturnStmt() {
		Stmt s = Grimp.v().newReturnStmt(IntConstant.v(1));

		Set expectedRep = new ExceptionHashSet(utility.VM_ERRORS);
		expectedRep.add(utility.ILLEGAL_MONITOR_STATE_EXCEPTION);
		assertTrue(ExceptionTestUtility.sameMembers(expectedRep, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));

		Set expectedCatch = new ExceptionHashSet(utility.VM_ERRORS_PLUS_SUPERTYPES);
		expectedCatch.add(utility.ILLEGAL_MONITOR_STATE_EXCEPTION);
		expectedCatch.add(utility.RUNTIME_EXCEPTION);
		expectedCatch.add(utility.EXCEPTION);
		assertEquals(expectedCatch, utility.catchableSubset(unitAnalysis.mightThrow(s)));
	}

	@Ignore("Fails")
	@Test
	public void testJReturnVoidStmt() {
		Stmt s = Jimple.v().newReturnVoidStmt();

		Set expectedRep = new ExceptionHashSet(utility.VM_ERRORS);
		expectedRep.add(utility.ILLEGAL_MONITOR_STATE_EXCEPTION);
		assertTrue(ExceptionTestUtility.sameMembers(expectedRep, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));

		Set expectedCatch = new ExceptionHashSet(utility.VM_ERRORS_PLUS_SUPERTYPES);
		expectedCatch.add(utility.ILLEGAL_MONITOR_STATE_EXCEPTION);
		expectedCatch.add(utility.RUNTIME_EXCEPTION);
		expectedCatch.add(utility.EXCEPTION);
		assertEquals(expectedCatch, utility.catchableSubset(unitAnalysis.mightThrow(s)));
	}

	@Ignore("Fails")
	@Test
	public void testGReturnVoidStmt() {
		Stmt s = Grimp.v().newReturnVoidStmt();

		Set expectedRep = new ExceptionHashSet(utility.VM_ERRORS);
		expectedRep.add(utility.ILLEGAL_MONITOR_STATE_EXCEPTION);
		assertTrue(ExceptionTestUtility.sameMembers(expectedRep, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));

		Set expectedCatch = new ExceptionHashSet(utility.VM_ERRORS_PLUS_SUPERTYPES);
		expectedCatch.add(utility.ILLEGAL_MONITOR_STATE_EXCEPTION);
		expectedCatch.add(utility.RUNTIME_EXCEPTION);
		expectedCatch.add(utility.EXCEPTION);
		assertEquals(expectedCatch, utility.catchableSubset(unitAnalysis.mightThrow(s)));
	}

	@Test
	public void testJTableSwitchStmt() {
		Stmt target = Jimple.v().newAssignStmt(Jimple.v().newLocal("local0", IntType.v()), IntConstant.v(0));
		Stmt s = Jimple.v().newTableSwitchStmt(IntConstant.v(1), 0, 1, Arrays.asList(new Unit[] { target }), target);
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(s)));
	}

	@Test
	public void testGTableSwitchStmt() {
		Stmt target = Grimp.v().newAssignStmt(Grimp.v().newLocal("local0", IntType.v()), IntConstant.v(0));
		Stmt s = Grimp.v().newTableSwitchStmt(IntConstant.v(1), 0, 1, Arrays.asList(new Unit[] { target }), target);
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(s)));
	}

	@Test
	public void testJThrowStmt() {

		// First test with an argument that is included in
		// PERENNIAL_THROW_EXCEPTIONS.
		ThrowStmt s = Jimple.v()
				.newThrowStmt(Jimple.v().newLocal("local0", RefType.v("java.lang.NullPointerException")));
		Set expectedRep = new ExceptionHashSet(utility.PERENNIAL_THROW_EXCEPTIONS);
		expectedRep.remove(utility.NULL_POINTER_EXCEPTION);
		expectedRep.add(AnySubType.v(utility.NULL_POINTER_EXCEPTION));
		assertTrue(ExceptionTestUtility.sameMembers(expectedRep, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));
		assertEquals(utility.PERENNIAL_THROW_EXCEPTIONS_PLUS_SUPERTYPES,
				utility.catchableSubset(unitAnalysis.mightThrow(s)));

		// Throw a local of type IncompatibleClassChangeError.
		Local local = Jimple.v().newLocal("local1", utility.INCOMPATIBLE_CLASS_CHANGE_ERROR);
		s.setOp(local);
		expectedRep = new ExceptionHashSet(utility.THROW_PLUS_INCOMPATIBLE_CLASS_CHANGE);
		expectedRep.remove(utility.INCOMPATIBLE_CLASS_CHANGE_ERROR);
		expectedRep.add(AnySubType.v(utility.INCOMPATIBLE_CLASS_CHANGE_ERROR));
		assertTrue(ExceptionTestUtility.sameMembers(expectedRep, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));
		assertEquals(utility.THROW_PLUS_INCOMPATIBLE_CLASS_CHANGE_PLUS_SUBTYPES_PLUS_SUPERTYPES,
				utility.catchableSubset(unitAnalysis.mightThrow(s)));

		// Throw a local of unknown type.
		local = Jimple.v().newLocal("local1", soot.UnknownType.v());
		s.setOp(local);
		assertTrue(ExceptionTestUtility.sameMembers(utility.ALL_THROWABLES_REP, Collections.EMPTY_SET,
				unitAnalysis.mightThrow(s)));
		assertEquals(utility.ALL_TEST_THROWABLES, utility.catchableSubset(unitAnalysis.mightThrow(s)));
	}

	@Test
	public void testGThrowStmt() {
		ThrowStmt s = Grimp.v().newThrowStmt(Grimp.v().newLocal("local0", RefType.v("java.util.zip.ZipException")));

		Set expectedRep = new ExceptionHashSet(utility.PERENNIAL_THROW_EXCEPTIONS);
		expectedRep.add(AnySubType.v(Scene.v().getRefType("java.util.zip.ZipException")));
		assertTrue(ExceptionTestUtility.sameMembers(expectedRep, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));

		Set expectedCatch = new ExceptionHashSet(utility.PERENNIAL_THROW_EXCEPTIONS_PLUS_SUPERTYPES);
		// We don't need to add java.util.zip.ZipException, since it is not
		// in the universe of test Throwables.
		assertEquals(expectedCatch, utility.catchableSubset(unitAnalysis.mightThrow(s)));

		// Now throw a new IncompatibleClassChangeError.
		s = Grimp.v().newThrowStmt(Grimp.v().newNewInvokeExpr(utility.INCOMPATIBLE_CLASS_CHANGE_ERROR,
				Scene.v().makeMethodRef(utility.INCOMPATIBLE_CLASS_CHANGE_ERROR.getSootClass(), "void <init>",
						Collections.EMPTY_LIST, VoidType.v(), false),
				new ArrayList()));
		assertTrue(ExceptionTestUtility.sameMembers(utility.THROW_PLUS_INCOMPATIBLE_CLASS_CHANGE, Collections.EMPTY_SET,
				unitAnalysis.mightThrow(s)));
		assertEquals(utility.THROW_PLUS_INCOMPATIBLE_CLASS_CHANGE_PLUS_SUPERTYPES,
				utility.catchableSubset(unitAnalysis.mightThrow(s)));

		// Throw a local of type IncompatibleClassChangeError.
		Local local = Grimp.v().newLocal("local1", utility.INCOMPATIBLE_CLASS_CHANGE_ERROR);
		s.setOp(local);
		expectedRep = new ExceptionHashSet(utility.PERENNIAL_THROW_EXCEPTIONS);
		expectedRep.remove(utility.INCOMPATIBLE_CLASS_CHANGE_ERROR);
		expectedRep.add(AnySubType.v(utility.INCOMPATIBLE_CLASS_CHANGE_ERROR));
		assertTrue(ExceptionTestUtility.sameMembers(expectedRep, Collections.EMPTY_SET, unitAnalysis.mightThrow(s)));
		assertEquals(utility.THROW_PLUS_INCOMPATIBLE_CLASS_CHANGE_PLUS_SUBTYPES_PLUS_SUPERTYPES,
				utility.catchableSubset(unitAnalysis.mightThrow(s)));

		// Throw a local of unknown type.
		local = Jimple.v().newLocal("local1", soot.UnknownType.v());
		s.setOp(local);
		assertTrue(ExceptionTestUtility.sameMembers(utility.ALL_THROWABLES_REP, Collections.EMPTY_SET,
				unitAnalysis.mightThrow(s)));
		assertEquals(utility.ALL_TEST_THROWABLES, utility.catchableSubset(unitAnalysis.mightThrow(s)));
	}

	@Test
	public void testJArrayRef() {
		ArrayRef arrayRef = Jimple.v().newArrayRef(
				Jimple.v().newLocal("local1", ArrayType.v(RefType.v("java.lang.Object"), 1)), IntConstant.v(0));

		Set expectedRep = new ExceptionHashSet(utility.VM_ERRORS);
		expectedRep.add(utility.NULL_POINTER_EXCEPTION);
		expectedRep.add(utility.ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION);
		assertTrue(ExceptionTestUtility.sameMembers(expectedRep, Collections.EMPTY_SET,
				unitAnalysis.mightThrow(arrayRef)));

		Set expectedCatch = new ExceptionHashSet(utility.VM_ERRORS_PLUS_SUPERTYPES);
		expectedCatch.add(utility.NULL_POINTER_EXCEPTION);
		expectedCatch.add(utility.ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION);
		expectedCatch.add(utility.INDEX_OUT_OF_BOUNDS_EXCEPTION);
		expectedCatch.add(utility.RUNTIME_EXCEPTION);
		expectedCatch.add(utility.EXCEPTION);
		assertEquals(expectedCatch, utility.catchableSubset(unitAnalysis.mightThrow(arrayRef)));
	}

	@Test
	public void testGArrayRef() {
		ArrayRef arrayRef = Grimp.v().newArrayRef(
				Grimp.v().newLocal("local1", ArrayType.v(RefType.v("java.lang.Object"), 1)), IntConstant.v(0));

		Set expectedRep = new ExceptionHashSet(utility.VM_ERRORS);
		expectedRep.add(utility.NULL_POINTER_EXCEPTION);
		expectedRep.add(utility.ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION);
		assertTrue(ExceptionTestUtility.sameMembers(expectedRep, Collections.EMPTY_SET,
				unitAnalysis.mightThrow(arrayRef)));

		Set expectedCatch = new ExceptionHashSet(utility.VM_ERRORS_PLUS_SUPERTYPES);
		expectedCatch.add(utility.NULL_POINTER_EXCEPTION);
		expectedCatch.add(utility.ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION);
		expectedCatch.add(utility.INDEX_OUT_OF_BOUNDS_EXCEPTION);
		expectedCatch.add(utility.RUNTIME_EXCEPTION);
		expectedCatch.add(utility.EXCEPTION);
		assertEquals(expectedCatch, utility.catchableSubset(unitAnalysis.mightThrow(arrayRef)));
	}

	@Test
	public void testJDivExpr() {
		Set vmAndArithmetic = new ExceptionHashSet(utility.VM_ERRORS);
		vmAndArithmetic.add(utility.ARITHMETIC_EXCEPTION);
		Set vmAndArithmeticAndSupertypes = new ExceptionHashSet(utility.VM_ERRORS_PLUS_SUPERTYPES);
		vmAndArithmeticAndSupertypes.add(utility.ARITHMETIC_EXCEPTION);
		vmAndArithmeticAndSupertypes.add(utility.RUNTIME_EXCEPTION);
		vmAndArithmeticAndSupertypes.add(utility.EXCEPTION);

		Local intLocal = Jimple.v().newLocal("intLocal", IntType.v());
		Local longLocal = Jimple.v().newLocal("longLocal", LongType.v());
		Local floatLocal = Jimple.v().newLocal("floatLocal", FloatType.v());
		Local doubleLocal = Jimple.v().newLocal("doubleLocal", DoubleType.v());

		DivExpr v = Jimple.v().newDivExpr(intLocal, IntConstant.v(0));
		assertTrue(
				ExceptionTestUtility.sameMembers(vmAndArithmetic, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(vmAndArithmeticAndSupertypes, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Jimple.v().newDivExpr(intLocal, IntConstant.v(2));
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Jimple.v().newDivExpr(IntConstant.v(0), IntConstant.v(2));
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Jimple.v().newDivExpr(intLocal, intLocal);
		assertTrue(
				ExceptionTestUtility.sameMembers(vmAndArithmetic, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(vmAndArithmeticAndSupertypes, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Jimple.v().newDivExpr(longLocal, LongConstant.v(0));
		assertTrue(
				ExceptionTestUtility.sameMembers(vmAndArithmetic, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(vmAndArithmeticAndSupertypes, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Jimple.v().newDivExpr(longLocal, LongConstant.v(2));
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Jimple.v().newDivExpr(LongConstant.v(0), LongConstant.v(2));
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Jimple.v().newDivExpr(longLocal, longLocal);
		assertTrue(
				ExceptionTestUtility.sameMembers(vmAndArithmetic, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(vmAndArithmeticAndSupertypes, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Jimple.v().newDivExpr(floatLocal, FloatConstant.v(0.0f));
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Jimple.v().newDivExpr(floatLocal, FloatConstant.v(2.0f));
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Jimple.v().newDivExpr(FloatConstant.v(0), FloatConstant.v(2.0f));
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Jimple.v().newDivExpr(floatLocal, floatLocal);
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Jimple.v().newDivExpr(doubleLocal, DoubleConstant.v(0.0));
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Jimple.v().newDivExpr(doubleLocal, DoubleConstant.v(2.0));
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Jimple.v().newDivExpr(DoubleConstant.v(0), DoubleConstant.v(2.0));
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Jimple.v().newDivExpr(doubleLocal, doubleLocal);
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));
	}

	@Test
	public void testGDivExpr() {
		Set vmAndArithmetic = new ExceptionHashSet(utility.VM_ERRORS);
		vmAndArithmetic.add(utility.ARITHMETIC_EXCEPTION);
		Set vmAndArithmeticAndSupertypes = new ExceptionHashSet(utility.VM_ERRORS_PLUS_SUPERTYPES);
		vmAndArithmeticAndSupertypes.add(utility.ARITHMETIC_EXCEPTION);
		vmAndArithmeticAndSupertypes.add(utility.RUNTIME_EXCEPTION);
		vmAndArithmeticAndSupertypes.add(utility.EXCEPTION);

		Local intLocal = Grimp.v().newLocal("intLocal", IntType.v());
		Local longLocal = Grimp.v().newLocal("longLocal", LongType.v());
		Local floatLocal = Grimp.v().newLocal("floatLocal", FloatType.v());
		Local doubleLocal = Grimp.v().newLocal("doubleLocal", DoubleType.v());

		DivExpr v = Grimp.v().newDivExpr(intLocal, IntConstant.v(0));
		assertTrue(
				ExceptionTestUtility.sameMembers(vmAndArithmetic, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(vmAndArithmeticAndSupertypes, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Grimp.v().newDivExpr(intLocal, IntConstant.v(2));
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Grimp.v().newDivExpr(IntConstant.v(0), IntConstant.v(2));
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Grimp.v().newDivExpr(intLocal, intLocal);
		assertTrue(
				ExceptionTestUtility.sameMembers(vmAndArithmetic, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(vmAndArithmeticAndSupertypes, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Grimp.v().newDivExpr(Grimp.v().newAddExpr(intLocal, intLocal), Grimp.v().newMulExpr(intLocal, intLocal));
		assertTrue(
				ExceptionTestUtility.sameMembers(vmAndArithmetic, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(vmAndArithmeticAndSupertypes, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Grimp.v().newDivExpr(longLocal, LongConstant.v(0));
		assertTrue(
				ExceptionTestUtility.sameMembers(vmAndArithmetic, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(vmAndArithmeticAndSupertypes, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Grimp.v().newDivExpr(longLocal, LongConstant.v(2));
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Grimp.v().newDivExpr(LongConstant.v(0), LongConstant.v(2));
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Grimp.v().newDivExpr(longLocal, longLocal);
		assertTrue(
				ExceptionTestUtility.sameMembers(vmAndArithmetic, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(vmAndArithmeticAndSupertypes, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Grimp.v().newDivExpr(Grimp.v().newAddExpr(longLocal, longLocal),
				Grimp.v().newMulExpr(longLocal, longLocal));
		assertTrue(
				ExceptionTestUtility.sameMembers(vmAndArithmetic, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(vmAndArithmeticAndSupertypes, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Grimp.v().newDivExpr(floatLocal, FloatConstant.v(0.0f));
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Grimp.v().newDivExpr(floatLocal, FloatConstant.v(2.0f));
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Grimp.v().newDivExpr(FloatConstant.v(0), FloatConstant.v(2.0f));
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Grimp.v().newDivExpr(floatLocal, floatLocal);
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Grimp.v().newDivExpr(doubleLocal, DoubleConstant.v(0.0));
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Grimp.v().newDivExpr(doubleLocal, DoubleConstant.v(2.0));
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Grimp.v().newDivExpr(DoubleConstant.v(0), DoubleConstant.v(2.0));
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Grimp.v().newDivExpr(doubleLocal, doubleLocal);
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));
	}

	@Test
	public void testJRemExpr() {
		Set vmAndArithmetic = new ExceptionHashSet(utility.VM_ERRORS);
		vmAndArithmetic.add(utility.ARITHMETIC_EXCEPTION);
		Set vmAndArithmeticAndSupertypes = new ExceptionHashSet(utility.VM_ERRORS_PLUS_SUPERTYPES);
		vmAndArithmeticAndSupertypes.add(utility.ARITHMETIC_EXCEPTION);
		vmAndArithmeticAndSupertypes.add(utility.RUNTIME_EXCEPTION);
		vmAndArithmeticAndSupertypes.add(utility.EXCEPTION);

		Local intLocal = Jimple.v().newLocal("intLocal", IntType.v());
		Local longLocal = Jimple.v().newLocal("longLocal", LongType.v());
		Local floatLocal = Jimple.v().newLocal("floatLocal", FloatType.v());
		Local doubleLocal = Jimple.v().newLocal("doubleLocal", DoubleType.v());

		RemExpr v = Jimple.v().newRemExpr(intLocal, IntConstant.v(0));
		assertTrue(
				ExceptionTestUtility.sameMembers(vmAndArithmetic, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(vmAndArithmeticAndSupertypes, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Jimple.v().newRemExpr(intLocal, IntConstant.v(2));
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Jimple.v().newRemExpr(IntConstant.v(0), IntConstant.v(2));
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Jimple.v().newRemExpr(intLocal, intLocal);
		assertTrue(
				ExceptionTestUtility.sameMembers(vmAndArithmetic, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(vmAndArithmeticAndSupertypes, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Jimple.v().newRemExpr(longLocal, LongConstant.v(0));
		assertTrue(
				ExceptionTestUtility.sameMembers(vmAndArithmetic, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(vmAndArithmeticAndSupertypes, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Jimple.v().newRemExpr(longLocal, LongConstant.v(2));
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Jimple.v().newRemExpr(LongConstant.v(0), LongConstant.v(2));
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Jimple.v().newRemExpr(longLocal, longLocal);
		assertTrue(
				ExceptionTestUtility.sameMembers(vmAndArithmetic, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(vmAndArithmeticAndSupertypes, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Jimple.v().newRemExpr(floatLocal, FloatConstant.v(0.0f));
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Jimple.v().newRemExpr(floatLocal, FloatConstant.v(2.0f));
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Jimple.v().newRemExpr(FloatConstant.v(0), FloatConstant.v(2.0f));
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Jimple.v().newRemExpr(floatLocal, floatLocal);
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Jimple.v().newRemExpr(doubleLocal, DoubleConstant.v(0.0));
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Jimple.v().newRemExpr(doubleLocal, DoubleConstant.v(2.0));
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Jimple.v().newRemExpr(DoubleConstant.v(0), DoubleConstant.v(2.0));
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Jimple.v().newRemExpr(doubleLocal, doubleLocal);
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));
	}

	@Test
	public void testGRemExpr() {
		Set vmAndArithmetic = new ExceptionHashSet(utility.VM_ERRORS);
		vmAndArithmetic.add(utility.ARITHMETIC_EXCEPTION);
		Set vmAndArithmeticAndSupertypes = new ExceptionHashSet(utility.VM_ERRORS_PLUS_SUPERTYPES);
		vmAndArithmeticAndSupertypes.add(utility.ARITHMETIC_EXCEPTION);
		vmAndArithmeticAndSupertypes.add(utility.RUNTIME_EXCEPTION);
		vmAndArithmeticAndSupertypes.add(utility.EXCEPTION);

		Local intLocal = Grimp.v().newLocal("intLocal", IntType.v());
		Local longLocal = Grimp.v().newLocal("longLocal", LongType.v());
		Local floatLocal = Grimp.v().newLocal("floatLocal", FloatType.v());
		Local doubleLocal = Grimp.v().newLocal("doubleLocal", DoubleType.v());

		RemExpr v = Grimp.v().newRemExpr(intLocal, IntConstant.v(0));
		assertTrue(
				ExceptionTestUtility.sameMembers(vmAndArithmetic, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(vmAndArithmeticAndSupertypes, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Grimp.v().newRemExpr(intLocal, IntConstant.v(2));
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Grimp.v().newRemExpr(IntConstant.v(0), IntConstant.v(2));
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Grimp.v().newRemExpr(intLocal, intLocal);
		assertTrue(
				ExceptionTestUtility.sameMembers(vmAndArithmetic, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(vmAndArithmeticAndSupertypes, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Grimp.v().newRemExpr(Grimp.v().newAddExpr(intLocal, intLocal), Grimp.v().newMulExpr(intLocal, intLocal));
		assertTrue(
				ExceptionTestUtility.sameMembers(vmAndArithmetic, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(vmAndArithmeticAndSupertypes, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Grimp.v().newRemExpr(longLocal, LongConstant.v(0));
		assertTrue(
				ExceptionTestUtility.sameMembers(vmAndArithmetic, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(vmAndArithmeticAndSupertypes, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Grimp.v().newRemExpr(longLocal, LongConstant.v(2));
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Grimp.v().newRemExpr(LongConstant.v(0), LongConstant.v(2));
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Grimp.v().newRemExpr(longLocal, longLocal);
		assertTrue(
				ExceptionTestUtility.sameMembers(vmAndArithmetic, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(vmAndArithmeticAndSupertypes, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Grimp.v().newRemExpr(Grimp.v().newAddExpr(longLocal, longLocal),
				Grimp.v().newMulExpr(longLocal, longLocal));
		assertTrue(
				ExceptionTestUtility.sameMembers(vmAndArithmetic, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(vmAndArithmeticAndSupertypes, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Grimp.v().newRemExpr(floatLocal, FloatConstant.v(0.0f));
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Grimp.v().newRemExpr(floatLocal, FloatConstant.v(2.0f));
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Grimp.v().newRemExpr(FloatConstant.v(0), FloatConstant.v(2.0f));
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Grimp.v().newRemExpr(floatLocal, floatLocal);
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Grimp.v().newRemExpr(doubleLocal, DoubleConstant.v(0.0));
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Grimp.v().newRemExpr(doubleLocal, DoubleConstant.v(2.0));
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Grimp.v().newRemExpr(DoubleConstant.v(0), DoubleConstant.v(2.0));
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Grimp.v().newRemExpr(doubleLocal, doubleLocal);
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));
	}

	@Test
	public void testJBinOpExp() {
		Value v = Jimple.v().newAddExpr(IntConstant.v(456), Jimple.v().newLocal("local", IntType.v()));
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Jimple.v().newOrExpr(Jimple.v().newLocal("local", LongType.v()), LongConstant.v(33));
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Jimple.v().newLeExpr(Jimple.v().newLocal("local", FloatType.v()), FloatConstant.v(33.42f));
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Jimple.v().newEqExpr(DoubleConstant.v(-33.45e-3), Jimple.v().newLocal("local", DoubleType.v()));
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));
	}

	@Ignore("Fails")
	@Test
	public void testGBinOpExp() {
		Value v = Grimp.v().newAddExpr(floatStaticFieldRef, floatConstant);
		assertTrue(ExceptionTestUtility.sameMembers(utility.ALL_ERRORS_REP, Collections.EMPTY_SET,
				unitAnalysis.mightThrow(v)));
		assertEquals(utility.ALL_TEST_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Grimp.v().newOrExpr(v, floatConstant);
		assertTrue(ExceptionTestUtility.sameMembers(utility.ALL_ERRORS_REP, Collections.EMPTY_SET,
				unitAnalysis.mightThrow(v)));
		assertEquals(utility.ALL_TEST_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		Set expectedRep = new ExceptionHashSet(utility.ALL_ERRORS_REP);
		expectedRep.add(utility.NULL_POINTER_EXCEPTION);

		Set expectedCatch = new ExceptionHashSet(utility.ALL_TEST_ERRORS_PLUS_SUPERTYPES);
		expectedCatch.add(utility.NULL_POINTER_EXCEPTION);
		expectedCatch.add(utility.RUNTIME_EXCEPTION);
		expectedCatch.add(utility.EXCEPTION);

		v = Grimp.v().newLeExpr(floatInstanceFieldRef, v);
		assertTrue(ExceptionTestUtility.sameMembers(expectedRep, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(expectedCatch, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		v = Grimp.v().newEqExpr(v, floatVirtualInvoke);
		assertTrue(
				ExceptionTestUtility.sameMembers(expectedRep, Collections.EMPTY_SET, immaculateAnalysis.mightThrow(v)));
		assertEquals(expectedCatch, utility.catchableSubset(immaculateAnalysis.mightThrow(v)));
		assertTrue(ExceptionTestUtility.sameMembers(utility.ALL_THROWABLES_REP, Collections.EMPTY_SET,
				unitAnalysis.mightThrow(v)));
		assertEquals(utility.ALL_TEST_THROWABLES, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		expectedRep.add(utility.ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION);
		expectedCatch.add(utility.ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION);
		expectedCatch.add(utility.INDEX_OUT_OF_BOUNDS_EXCEPTION);

		v = Grimp.v().newNeExpr(v, floatStaticInvoke);
		assertEquals(expectedCatch, utility.catchableSubset(immaculateAnalysis.mightThrow(v)));
		assertEquals(expectedCatch, utility.catchableSubset(immaculateAnalysis.mightThrow(v)));
		assertTrue(ExceptionTestUtility.sameMembers(utility.ALL_THROWABLES_REP, Collections.EMPTY_SET,
				unitAnalysis.mightThrow(v)));
		assertEquals(utility.ALL_TEST_THROWABLES, utility.catchableSubset(unitAnalysis.mightThrow(v)));
	}

	@Test
	public void testJCastExpr() {
		// First an upcast that can be statically proved safe.
		Value v = Jimple.v().newCastExpr(Jimple.v().newLocal("local", utility.INCOMPATIBLE_CLASS_CHANGE_ERROR),
				utility.LINKAGE_ERROR);
		Set expectedRep = new ExceptionHashSet(utility.VM_AND_RESOLVE_CLASS_ERRORS_REP);
		Set expectedCatch = new ExceptionHashSet(utility.VM_AND_RESOLVE_CLASS_ERRORS_PLUS_SUPERTYPES);
		assertTrue(ExceptionTestUtility.sameMembers(expectedRep, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(expectedCatch, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		// Then a vacuous cast which can be statically proved safe.
		v = Jimple.v().newCastExpr(Jimple.v().newLocal("local", utility.LINKAGE_ERROR), utility.LINKAGE_ERROR);
		assertTrue(ExceptionTestUtility.sameMembers(expectedRep, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(expectedCatch, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		// Finally a downcast which is not necessarily safe:
		v = Jimple.v().newCastExpr(Jimple.v().newLocal("local", utility.LINKAGE_ERROR),
				utility.INCOMPATIBLE_CLASS_CHANGE_ERROR);

		expectedRep.add(utility.CLASS_CAST_EXCEPTION);
		assertTrue(ExceptionTestUtility.sameMembers(expectedRep, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));

		expectedCatch.add(utility.CLASS_CAST_EXCEPTION);
		expectedCatch.add(utility.RUNTIME_EXCEPTION);
		expectedCatch.add(utility.EXCEPTION);

		assertEquals(expectedCatch, utility.catchableSubset(unitAnalysis.mightThrow(v)));
	}

	@Test
	public void testGCastExpr() {
		// First an upcast that can be statically proved safe.
		Value v = Grimp.v().newCastExpr(Jimple.v().newLocal("local", utility.INCOMPATIBLE_CLASS_CHANGE_ERROR),
				utility.LINKAGE_ERROR);
		Set expectedRep = new ExceptionHashSet(utility.VM_AND_RESOLVE_CLASS_ERRORS_REP);
		Set expectedCatch = new ExceptionHashSet(utility.VM_AND_RESOLVE_CLASS_ERRORS_PLUS_SUPERTYPES);
		assertTrue(ExceptionTestUtility.sameMembers(expectedRep, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(expectedCatch, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		// Then a vacuous cast which can be statically proved safe.
		v = Jimple.v().newCastExpr(Jimple.v().newLocal("local", utility.LINKAGE_ERROR), utility.LINKAGE_ERROR);
		assertTrue(ExceptionTestUtility.sameMembers(expectedRep, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(expectedCatch, utility.catchableSubset(unitAnalysis.mightThrow(v)));

		// Finally a downcast which is not necessarily safe:
		v = Jimple.v().newCastExpr(Jimple.v().newLocal("local", utility.LINKAGE_ERROR),
				utility.INCOMPATIBLE_CLASS_CHANGE_ERROR);

		expectedRep.add(utility.CLASS_CAST_EXCEPTION);
		assertTrue(ExceptionTestUtility.sameMembers(expectedRep, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));

		expectedCatch.add(utility.CLASS_CAST_EXCEPTION);
		expectedCatch.add(utility.RUNTIME_EXCEPTION);
		expectedCatch.add(utility.EXCEPTION);

		assertEquals(expectedCatch, utility.catchableSubset(unitAnalysis.mightThrow(v)));
	}

	@Test
	public void testGInstanceFieldRef() {
		Local local = Grimp.v().newLocal("local", utility.INCOMPATIBLE_CLASS_CHANGE_ERROR);

		Set expectedRep = new ExceptionHashSet(utility.VM_AND_RESOLVE_FIELD_ERRORS_REP);
		expectedRep.add(utility.NULL_POINTER_EXCEPTION);

		Set expectedCatch = new ExceptionHashSet(utility.VM_AND_RESOLVE_FIELD_ERRORS_PLUS_SUPERTYPES);
		expectedCatch.add(utility.NULL_POINTER_EXCEPTION);
		expectedCatch.add(utility.RUNTIME_EXCEPTION);
		expectedCatch.add(utility.EXCEPTION);

		Value v = Grimp.v().newInstanceFieldRef(local, Scene.v().makeFieldRef(utility.THROWABLE.getSootClass(),
				"detailMessage", RefType.v("java.lang.String"), false));
		assertTrue(ExceptionTestUtility.sameMembers(expectedRep, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(expectedCatch, utility.catchableSubset(unitAnalysis.mightThrow(v)));
	}

	@Test
	public void testStringConstant() {
		Value v = StringConstant.v("test");
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(v)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(v)));
	}

	@Test
	public void testJLocal() {
		Local local = Jimple.v().newLocal("local1", utility.INCOMPATIBLE_CLASS_CHANGE_ERROR);
		assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET,
				unitAnalysis.mightThrow(local)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(local)));
	}

	@Test
	public void testGLocal() {
		Local local = Grimp.v().newLocal("local1", utility.INCOMPATIBLE_CLASS_CHANGE_ERROR);
		assertTrue(ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET,
				unitAnalysis.mightThrow(local)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(local)));
	}

	@Test
	public void testBAddInst() {
		soot.baf.AddInst i = soot.baf.Baf.v().newAddInst(IntType.v());
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(i)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(i)));
	}

	@Test
	public void testBAndInst() {
		soot.baf.AndInst i = soot.baf.Baf.v().newAndInst(IntType.v());
		assertTrue(
				ExceptionTestUtility.sameMembers(utility.VM_ERRORS, Collections.EMPTY_SET, unitAnalysis.mightThrow(i)));
		assertEquals(utility.VM_ERRORS_PLUS_SUPERTYPES, utility.catchableSubset(unitAnalysis.mightThrow(i)));
	}

	@Test
	public void testBArrayLengthInst() {
		soot.baf.ArrayLengthInst i = soot.baf.Baf.v().newArrayLengthInst();
		Set expectedRep = new ExceptionHashSet(utility.VM_ERRORS);
		expectedRep.add(utility.NULL_POINTER_EXCEPTION);
		assertTrue(ExceptionTestUtility.sameMembers(expectedRep, Collections.EMPTY_SET, unitAnalysis.mightThrow(i)));
		Set expectedCatch = new ExceptionHashSet(utility.VM_ERRORS_PLUS_SUPERTYPES);
		expectedCatch.add(utility.NULL_POINTER_EXCEPTION);
		expectedCatch.add(utility.RUNTIME_EXCEPTION);
		expectedCatch.add(utility.EXCEPTION);
		assertEquals(expectedCatch, utility.catchableSubset(unitAnalysis.mightThrow(i)));
	}
}
