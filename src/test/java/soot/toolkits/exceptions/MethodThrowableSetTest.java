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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import soot.AnySubType;
import soot.G;
import soot.PackManager;
import soot.RefLikeType;
import soot.Scene;
import soot.SootMethod;
import soot.options.Options;

public class MethodThrowableSetTest {

	private static String TARGET_CLASS = "soot.toolkits.exceptions.targets.MethodThrowableSetClass";
	private static String EXCEPTION_CLASS = "soot.toolkits.exceptions.targets.MyException";

	private static ExceptionTestUtility testUtility;

	@BeforeClass
	public static void setUp() throws IOException {
		// Initialize Soot
		G.reset();

		List<String> processDir = new ArrayList<>();
        File f = new File("./target/test-classes");
		if (f.exists())
			processDir.add(f.getCanonicalPath());
		Options.v().set_process_dir(processDir);

		Options.v().set_src_prec(Options.src_prec_only_class);
		Options.v().set_allow_phantom_refs(true);
		Options.v().set_output_format(Options.output_format_none);

		Scene.v().addBasicClass(TARGET_CLASS);
		Scene.v().loadNecessaryClasses();

		testUtility = new ExceptionTestUtility();

		PackManager.v().runPacks();
	}

	/**
	 * Derived class to allow access to some protected members
	 */
	@Ignore
	private static class ThrowAnalysisForTest extends UnitThrowAnalysis {

		public ThrowAnalysisForTest() {
			super(true);
		}

		@Override
		public ThrowableSet mightThrow(SootMethod sm) {
			return super.mightThrow(sm);
		}

	}

	/**
	 * Retrieves all exceptions that can potentially be thrown by the method
	 * with the given signature
	 * 
	 * @param methodSig
	 *            The signature of the method for which to retrieve the
	 *            exceptions
	 * @return The exceptions that the method with the given signature can
	 *         possibly throw
	 */
	private ThrowableSet getExceptionsForMethod(String methodSig) {
		SootMethod sm = Scene.v().getMethod(methodSig);
		ThrowAnalysisForTest ta = new ThrowAnalysisForTest();
		return ta.mightThrow(sm);
	}

	@Test
	public void simpleExceptionTest1() {
		ThrowableSet ts = getExceptionsForMethod(
				"<soot.toolkits.exceptions.targets.MethodThrowableSetClass: void foo()>");

		Set<RefLikeType> expected = new HashSet<RefLikeType>();
		expected.addAll(testUtility.VM_ERRORS);
		expected.add(testUtility.ARITHMETIC_EXCEPTION);
		expected.add(testUtility.NULL_POINTER_EXCEPTION);

		Assert.assertTrue(ExceptionTestUtility.sameMembers(expected, Collections.<AnySubType>emptySet(), ts));
	}

	@Test
	public void simpleExceptionTest2() {
		ThrowableSet ts = getExceptionsForMethod(
				"<soot.toolkits.exceptions.targets.MethodThrowableSetClass: void bar()>");

		Set<RefLikeType> expected = new HashSet<RefLikeType>();
		expected.addAll(testUtility.VM_ERRORS);
		expected.add(testUtility.ARITHMETIC_EXCEPTION);
		expected.add(testUtility.NULL_POINTER_EXCEPTION);

		Assert.assertTrue(ExceptionTestUtility.sameMembers(expected, Collections.<AnySubType>emptySet(), ts));
	}

	@Test
	public void simpleExceptionTest3() {
		ThrowableSet ts = getExceptionsForMethod(
				"<soot.toolkits.exceptions.targets.MethodThrowableSetClass: void tool()>");

		Set<RefLikeType> expected = new HashSet<RefLikeType>();
		expected.addAll(testUtility.VM_ERRORS);
		expected.add(testUtility.ARITHMETIC_EXCEPTION);
		expected.add(testUtility.NULL_POINTER_EXCEPTION);
		expected.add(testUtility.ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION);

		Assert.assertTrue(ExceptionTestUtility.sameMembers(expected, Collections.<AnySubType>emptySet(), ts));
	}

	@Test
	public void getAllExceptionTest1() {
		ThrowableSet ts = getExceptionsForMethod(
				"<soot.toolkits.exceptions.targets.MethodThrowableSetClass: void getAllException()>");

		Set<RefLikeType> expected = new HashSet<RefLikeType>();
		expected.addAll(testUtility.VM_ERRORS);

		Assert.assertTrue(ExceptionTestUtility.sameMembers(expected, Collections.<AnySubType>emptySet(), ts));
	}

	@Test
	public void getMyExceptionTest1() {
		ThrowableSet ts = getExceptionsForMethod(
				"<soot.toolkits.exceptions.targets.MethodThrowableSetClass: void getMyException()>");

		Set<RefLikeType> expected = new HashSet<RefLikeType>();
		expected.add(AnySubType.v(testUtility.ERROR)); // for NewExpr
		expected.add(testUtility.ILLEGAL_MONITOR_STATE_EXCEPTION);
		expected.add(testUtility.NULL_POINTER_EXCEPTION);
		expected.add(AnySubType.v(Scene.v().getSootClass(EXCEPTION_CLASS).getType()));

		Assert.assertTrue(ExceptionTestUtility.sameMembers(expected, Collections.<AnySubType>emptySet(), ts));
	}

	@Test
	public void nestedTryCatchTest1() {
		ThrowableSet ts = getExceptionsForMethod(
				"<soot.toolkits.exceptions.targets.MethodThrowableSetClass: void nestedTry()>");

		Set<RefLikeType> expected = new HashSet<RefLikeType>();
		expected.addAll(testUtility.VM_ERRORS);
		expected.add(testUtility.ARITHMETIC_EXCEPTION);
		expected.add(testUtility.NULL_POINTER_EXCEPTION);
		expected.add(testUtility.ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION);

		Assert.assertTrue(ExceptionTestUtility.sameMembers(expected, Collections.<AnySubType>emptySet(), ts));
	}

	@Test
	public void recursionTest1() {
		ThrowableSet ts = getExceptionsForMethod(
				"<soot.toolkits.exceptions.targets.MethodThrowableSetClass: void recursion()>");

		Set<RefLikeType> expected = new HashSet<RefLikeType>();
		expected.addAll(testUtility.VM_ERRORS);

		Assert.assertTrue(ExceptionTestUtility.sameMembers(expected, Collections.<AnySubType>emptySet(), ts));
	}

	@Test
	public void unitInCatchBlockTest1() {
		ThrowableSet ts = getExceptionsForMethod(
				"<soot.toolkits.exceptions.targets.MethodThrowableSetClass: void unitInCatchBlock()>");

		Set<RefLikeType> expected = new HashSet<RefLikeType>();
		expected.addAll(testUtility.VM_ERRORS);
		expected.add(testUtility.ARITHMETIC_EXCEPTION);

		Assert.assertTrue(ExceptionTestUtility.sameMembers(expected, Collections.<AnySubType>emptySet(), ts));
	}

}
