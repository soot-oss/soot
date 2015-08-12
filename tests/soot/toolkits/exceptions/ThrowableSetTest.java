package soot.toolkits.exceptions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.AssertionFailedError;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import soot.AnySubType;
import soot.RefLikeType;
import soot.RefType;
import soot.Scene;
import soot.toolkits.exceptions.ExceptionTestUtility.ExceptionHashSet;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ThrowableSetTest {

	static {
		Scene.v().loadBasicClasses();
	}

	final static boolean DUMP_INTERNALS = false;
	final ThrowableSet.Manager mgr = ThrowableSet.Manager.v();


	// A class for verifying that the sizeToSetsMap
	// follows our expectations.
	static class ExpectedSizeToSets {
		private Map<Integer, Set<SetPair>> expectedMap = new HashMap<Integer, Set<SetPair>>(); // from Integer to Set.

		private static class SetPair {
			Set<RefLikeType> included;
			Set<AnySubType> excluded;

			SetPair(Set<RefLikeType> included, Set<AnySubType> excluded) {
				this.included = included;
				this.excluded = excluded;
			}

			@Override
			public boolean equals(Object o) {
				if (o == this) {
					return true;
				}
				if (! (o instanceof SetPair)) {
					return false;
				}
				SetPair sp = (SetPair) o;
				return (   this.included.equals(sp.included)
						&& this.excluded.equals(sp.excluded));
			}

			@Override
			public int hashCode() {
				int result = 31;
				result = (37 * result) + included.hashCode();
				result = (37 * result) + excluded.hashCode();
				return result;
			}

			@Override
			public String toString() {
				return (  super.toString()
						+ System.getProperty("line.separator")
						+ "+[" + included.toString() + ']'
						+ "-[" + excluded.toString() + ']');
			}
		}

		ExpectedSizeToSets() {
			// The empty set.
			this.add(Collections.<RefLikeType>emptySet(), Collections.<AnySubType>emptySet());

			// All Throwables set.
			Set<RefLikeType> temp = new ExceptionHashSet<RefLikeType>();
			temp.add(AnySubType.v(Scene.v().getRefType("java.lang.Throwable")));
			this.add(temp, Collections.<AnySubType>emptySet());

			// VM errors set.
			temp = new ExceptionHashSet<RefLikeType>();
			temp.add(Scene.v().getRefType("java.lang.InternalError"));
			temp.add(Scene.v().getRefType("java.lang.OutOfMemoryError"));
			temp.add(Scene.v().getRefType("java.lang.StackOverflowError"));
			temp.add(Scene.v().getRefType("java.lang.UnknownError"));
			temp.add(Scene.v().getRefType("java.lang.ThreadDeath"));
			this.add(temp, Collections.<AnySubType>emptySet());

			// Resolve Class errors set.
			Set<RefLikeType> classErrors = new ExceptionHashSet<RefLikeType>();
			classErrors.add(Scene.v().getRefType("java.lang.ClassCircularityError"));
			classErrors.add(AnySubType.v(Scene.v().getRefType("java.lang.ClassFormatError")));
			classErrors.add(Scene.v().getRefType("java.lang.IllegalAccessError"));
			classErrors.add(Scene.v().getRefType("java.lang.IncompatibleClassChangeError"));
			classErrors.add(Scene.v().getRefType("java.lang.LinkageError"));
			classErrors.add(Scene.v().getRefType("java.lang.NoClassDefFoundError"));
			classErrors.add(Scene.v().getRefType("java.lang.VerifyError"));
			this.add(classErrors, Collections.<AnySubType>emptySet());

			// Resolve Field errors set.
			temp = new ExceptionHashSet<RefLikeType>(classErrors);
			temp.add(Scene.v().getRefType("java.lang.NoSuchFieldError"));
			this.add(temp, Collections.<AnySubType>emptySet());

			// Resolve method errors set.
			temp = new ExceptionHashSet<RefLikeType>(classErrors);
			temp.add(Scene.v().getRefType("java.lang.AbstractMethodError"));
			temp.add(Scene.v().getRefType("java.lang.NoSuchMethodError"));
			temp.add(Scene.v().getRefType("java.lang.UnsatisfiedLinkError"));
			this.add(temp, Collections.<AnySubType>emptySet());

			// Initialization errors set.
			temp = new ExceptionHashSet<RefLikeType>();
			temp.add(AnySubType.v(Scene.v().getRefType("java.lang.Error")));
			this.add(temp, Collections.<AnySubType>emptySet());
		}

		void add(Set<RefLikeType> inclusions, Set<AnySubType> exclusions) {
			int key = inclusions.size() + exclusions.size();
			Set<SetPair> values = expectedMap.get(key);
			if (values == null) {
				values = new HashSet<SetPair>();
				expectedMap.put(key, values);
			}
			// Make sure we have our own copies of the sets.
			values.add(newSetPair(inclusions,exclusions));
		}

		void addAndCheck(Set<RefLikeType> inclusions, Set<AnySubType> exclusions) {
			add(inclusions, exclusions);
			assertTrue(match());
		}

		SetPair newSetPair(Collection<RefLikeType> inclusions, Collection<AnySubType> exclusions) {
			return new SetPair(new ExceptionHashSet<RefLikeType>(inclusions),
					new ExceptionHashSet<AnySubType>(exclusions));
		}

		boolean match() {
			final Collection<ThrowableSet> toCompare = ThrowableSet.Manager.v().getThrowableSets();

			int sum = 0;
			for (Collection<SetPair> expectedValues : expectedMap.values()) {
				sum += expectedValues.size();
			}
			assertEquals(sum, toCompare.size());

			for (ThrowableSet actual : toCompare) {
				Collection<RefLikeType> included = actual.typesIncluded();
				Collection<AnySubType> excluded = actual.typesExcluded();
				
				SetPair actualPair = newSetPair(included, excluded);
				int key = included.size() + excluded.size();
				assertTrue("Undefined SetPair found", expectedMap.get(key).contains(actualPair));
			}
			boolean result = true;

			if (DUMP_INTERNALS) {
				if (! result) System.err.println("!!!ExpectedSizeToSets.match() FAILED!!!");
				System.err.println("expectedMap:");
				System.err.println(expectedMap);
				System.err.println("actualMap:");
				System.err.println(toCompare);
				System.err.flush();
			}
			return result;
		}
	}
	private static ExpectedSizeToSets expectedSizeToSets;

	// A class to check that memoized results match what we expect.
	// Admittedly, this amounts to a reimplementation of the memoized
	// structures within ThrowableSet -- I'm hoping that the two
	// implementations will have different bugs!
	static class ExpectedMemoizations  {
		Map<ThrowableSet, Map<Object, ThrowableSet>> throwableSetToMemoized =
				new HashMap<ThrowableSet, Map<Object, ThrowableSet>>();

		void checkAdd(ThrowableSet lhs, Object rhs, ThrowableSet result) {
			// rhs should be either a ThrowableSet or a RefType.
			Map<Object, ThrowableSet> actualMemoized = lhs.getMemoizedAdds();
			assertTrue(actualMemoized.get(rhs) == result);

			Map<Object, ThrowableSet> expectedMemoized = throwableSetToMemoized.get(lhs);
			if (expectedMemoized == null) {
				expectedMemoized = new HashMap<Object, ThrowableSet>();
				throwableSetToMemoized.put(lhs, expectedMemoized);
			}
			expectedMemoized.put(rhs, result);
			assertEquals(expectedMemoized, actualMemoized);
		}
	}
	private static ExpectedMemoizations expectedMemoizations;

	private static ExceptionTestUtility util;

	@BeforeClass
	public static void setUp() {
		expectedSizeToSets = new ExpectedSizeToSets();
		expectedMemoizations = new ExpectedMemoizations();
		util = new ExceptionTestUtility(System.getProperty("sun.boot.class.path"));
	}

	/**
	 * Asserts that the membership in the component sets of a
	 * ThrowableSet correspond to expectations.
	 *
	 * @param s The set to be checked.
	 *
	 * @param included the {@link Set} of RefLikeTypes
	 * expected to be in included in <code>s</code>.
	 *
	 * @param excluded an {@link Set} of RefLikeTypes
	 * expected to be excluded from <code>s</code>.
	 *
	 * @throws  AssertionFailedError if <code>s</code> does not
	 * contain the types in <code>included</code> except for those
	 * in <code>excluded</code>.
	 */
	public static void assertSameMembers(ThrowableSet s,
			Set<? extends RefLikeType> included,
			Set<AnySubType> excluded) {
		assertTrue(ExceptionTestUtility.sameMembers(included, excluded, s));
	}


	/**
	 * Asserts that the membership in the component sets of a
	 * ThrowableSet correspond to expectations.
	 *
	 * @param s The set to be checked.
	 *
	 * @param included an array containing the RefLikeTypes
	 * expected to be in included in <code>s</code>.
	 *
	 * @param excluded an array containing the RefLikeTypes
	 * expected to be excluded from <code>s</code>.
	 *
	 * @throws  AssertionFailedError if <code>s</code> does not
	 * contain the types in <code>included</code> except for those
	 * in <code>excluded</code>.
	 */
	public static void assertSameMembers(ThrowableSet s,
			RefLikeType[] included,
			AnySubType[] excluded) {
		assertTrue(ExceptionTestUtility.sameMembers(
				new ExceptionHashSet<RefLikeType>(Arrays.asList(included)),
				new ExceptionHashSet<AnySubType>(Arrays.asList(excluded)),
				s));
	}


	/**
	 * Asserts that the membership in the component sets of a
	 * ThrowableSet.Pair correspond to expectations.
	 *
	 * @param p The pair to be checked.
	 *
	 * @param caughtIncluded the set of {@link RefLikeType}s
	 * expected to be in included in <code>p.getCaught()</code>.
	 *
	 * @param caughtExcluded the set of <code>RefLikeType</code>s
	 * expected to be excluded from <code>p.getCaught()</code>.
	 *
	 * @param uncaughtIncluded the set of <code>RefLikeType</code>s
	 * expected to be in included in <code>p.getUncaught()</code>.
	 *
	 * @param uncaughtExcluded the set of <code>RefLikeType</code>s
	 * expected to be excluded from <code>p.getUncaught()</code>.
	 *
	 * @throws  AssertionFailedError if <code>s</code> does not
	 * contain the types in <code>included</code> except for those
	 * in <code>excluded</code>.
	 */
	public static void assertSameMembers(ThrowableSet.Pair p,
			Set<? extends RefLikeType> caughtIncluded,
			Set<AnySubType> caughtExcluded,
			Set<? extends RefLikeType> uncaughtIncluded,
			Set<AnySubType> uncaughtExcluded) {
		assertSameMembers(p.getCaught(), caughtIncluded, caughtExcluded);
		assertSameMembers(p.getUncaught(), uncaughtIncluded, uncaughtExcluded);
	}


	/**
	 * Asserts that the membership in the component sets of a
	 * ThrowableSet.Pair correspond to expectations.
	 *
	 * @param p The pair to be checked.
	 *
	 * @param caughtIncluded an array containing the {@link RefLikeType}s
	 * expected to be in included in <code>p.getCaught()</code>.
	 *
	 * @param caughtExcluded an array containing the <code>RefLikeType</code>s
	 * expected to be excluded from <code>p.getCaught()</code>.
	 *
	 * @param uncaughtIncluded an array containing the <code>RefLikeType</code>s
	 * expected to be in included in <code>p.getUncaught()</code>.
	 *
	 * @param uncaughtExcluded an array containing the <code>RefLikeType</code>s
	 * expected to be excluded from <code>p.getUncaught()</code>.
	 *
	 * @throws  AssertionFailedError if <code>s</code> does not
	 * contain the types in <code>included</code> except for those
	 * in <code>excluded</code>.
	 */
	public static void assertSameMembers(ThrowableSet.Pair p,
			RefLikeType[] caughtIncluded,
			AnySubType[] caughtExcluded,
			RefLikeType[] uncaughtIncluded,
			AnySubType[] uncaughtExcluded) {
		assertSameMembers(p.getCaught(), caughtIncluded, caughtExcluded);
		assertSameMembers(p.getUncaught(), uncaughtIncluded, uncaughtExcluded);
	}


	private ThrowableSet checkAdd(ThrowableSet lhs, Object rhs,
			Set<RefLikeType> expectedIncluded, Set<AnySubType> expectedExcluded,
			ThrowableSet actualResult) {
		// Utility routine used by the next three add()s.

		assertSameMembers(actualResult, expectedIncluded, expectedExcluded);
		expectedSizeToSets.addAndCheck(expectedIncluded, expectedExcluded);
		expectedMemoizations.checkAdd(lhs, rhs, actualResult);
		return actualResult;
	}

	private ThrowableSet checkAdd(ThrowableSet lhs, Object rhs,
			Set<RefLikeType> expectedResult, ThrowableSet actualResult) {
		// Utility routine used by the next three add()s.
		return checkAdd(lhs, rhs, expectedResult, Collections.<AnySubType>emptySet(),
				actualResult);
	}


	private ThrowableSet add(ThrowableSet lhs, ThrowableSet rhs,
			Set<RefLikeType> expectedIncluded, Set<AnySubType> expectedExcluded) {
		// Add rhs to lhs, checking the results.

		ThrowableSet actualResult = lhs.add(rhs);
		return checkAdd(lhs, rhs, expectedIncluded, expectedExcluded,
				actualResult);
	}

	private ThrowableSet add(ThrowableSet lhs, ThrowableSet rhs,
			Set<RefLikeType> expectedResult) {
		// Add rhs to lhs, checking the results.
		return add(lhs, rhs, expectedResult, Collections.<AnySubType>emptySet());
	}

	private ThrowableSet add(ThrowableSet lhs, RefType rhs,
			Set<RefLikeType> expectedResult) {
		// Add rhs to lhs, checking the results.

		ThrowableSet actualResult = lhs.add(rhs);
		return checkAdd(lhs, rhs, expectedResult, actualResult);
	}

	private ThrowableSet add(ThrowableSet lhs, AnySubType rhs,
			Set<RefLikeType> expectedResult) {
		// Add rhs to lhs, checking the results.

		ThrowableSet actualResult = lhs.add(rhs);
		return checkAdd(lhs, rhs, expectedResult, actualResult);
	}


	@Test
	public void test_01_InitialState() {
		if (DUMP_INTERNALS) {
			System.err.println("\n\ntestInitialState()");
		}
		assertTrue(expectedSizeToSets.match());
		if (DUMP_INTERNALS) {
			printAllSets();
		}
	}

	@Test
	public void test_02_SingleInstance0() {
		if (DUMP_INTERNALS) {
			System.err.println("\n\ntestSingleInstance0()");
		}
		Set<RefLikeType> expected = new ExceptionHashSet<RefLikeType>(Arrays.asList(new RefLikeType[] {
				util.UNDECLARED_THROWABLE_EXCEPTION,
		}));

		ThrowableSet set0 = add(mgr.EMPTY, util.UNDECLARED_THROWABLE_EXCEPTION,
				expected);
		ThrowableSet set1 = add(mgr.EMPTY, util.UNDECLARED_THROWABLE_EXCEPTION,
				expected);
		assertTrue("The same ThrowableSet object should represent two sets containing the same single class.",
				set0 == set1);

		Set<RefType> catchable = new ExceptionHashSet<RefType>(Arrays.asList(new RefType[] {
				util.UNDECLARED_THROWABLE_EXCEPTION,
				util.RUNTIME_EXCEPTION,
				util.EXCEPTION,
				util.THROWABLE,
		}));
		assertEquals("Should be catchable only as UndeclaredThrowableException and its superclasses",
				catchable, util.catchableSubset(set0));

		ThrowableSet.Pair catchableAs = set0.whichCatchableAs(util.LINKAGE_ERROR);
		assertEquals(mgr.EMPTY, catchableAs.getCaught());
		assertEquals(set0, catchableAs.getUncaught());
		catchableAs = set0.whichCatchableAs(util.UNDECLARED_THROWABLE_EXCEPTION);
		assertEquals(catchableAs.getCaught(), set0);
		assertEquals(catchableAs.getUncaught(), mgr.EMPTY);
		catchableAs = set0.whichCatchableAs(util.RUNTIME_EXCEPTION);
		assertEquals(catchableAs.getCaught(), set0);
		assertEquals(catchableAs.getUncaught(), mgr.EMPTY);
		if (DUMP_INTERNALS) {
			printAllSets();
		}
	}

	@Test
	public void test_03_SingleInstance1() {
		if (DUMP_INTERNALS) {
			System.err.println("\n\ntestSingleInstance1()");
		}
		Set<RefLikeType> expected0 = new ExceptionHashSet<RefLikeType>(Arrays.asList(new RefLikeType[] {
				util.UNDECLARED_THROWABLE_EXCEPTION,
		}));
		Set<RefLikeType> expected1 = new ExceptionHashSet<RefLikeType>(Arrays.asList(new RefLikeType[] {
				util.UNSUPPORTED_LOOK_AND_FEEL_EXCEPTION,
		}));
		Set<RefLikeType> expectedResult = new ExceptionHashSet<RefLikeType>(Arrays.asList(new RefLikeType[] {
				util.UNDECLARED_THROWABLE_EXCEPTION,
				util.UNSUPPORTED_LOOK_AND_FEEL_EXCEPTION,
		}));

		ThrowableSet set0 = add(mgr.EMPTY, util.UNDECLARED_THROWABLE_EXCEPTION,
				expected0);
		ThrowableSet set0a = add(set0, util.UNSUPPORTED_LOOK_AND_FEEL_EXCEPTION,
				expectedResult);
		ThrowableSet set1 = add(mgr.EMPTY,
				util.UNSUPPORTED_LOOK_AND_FEEL_EXCEPTION,
				expected1);
		ThrowableSet set1a = add(set1, util.UNDECLARED_THROWABLE_EXCEPTION,
				expectedResult);

		assertTrue("The same ThrowableSet object should represent two sets containing the same two exceptions, even if added in different orders.",
				set0a == set1a);

		Set<RefLikeType> catchable = new ExceptionHashSet<RefLikeType>(expectedResult);
		catchable.add(util.RUNTIME_EXCEPTION);
		catchable.add(util.EXCEPTION);
		catchable.add(util.THROWABLE);
		assertEquals("Should be catchable only as UndeclaredThrowableException "
				+ "UnsupportedLookAndFeelException and superclasses",
				catchable, util.catchableSubset(set0a));

		if (DUMP_INTERNALS) {
			printAllSets();
		}
	}


	@Test
	public void test_04_AddingSubclasses() {
		if (DUMP_INTERNALS) {
			System.err.println("\n\ntestAddingSubclasses()");
		}
		Set<RefLikeType> expected = new ExceptionHashSet<RefLikeType>();
		expected.add(util.INDEX_OUT_OF_BOUNDS_EXCEPTION);
		ThrowableSet set0 = add(mgr.EMPTY, util.INDEX_OUT_OF_BOUNDS_EXCEPTION,
				expected);

		expected.clear();
		expected.add(AnySubType.v(util.INDEX_OUT_OF_BOUNDS_EXCEPTION));
		ThrowableSet set1 = add(mgr.EMPTY,
				AnySubType.v(util.INDEX_OUT_OF_BOUNDS_EXCEPTION),
				expected);
		assertTrue("ThrowableSet should distinguish the case where a single exception includes subclasses from that where it does not.",
				set0 != set1);

		Set<RefLikeType> catchable = new ExceptionHashSet<RefLikeType>(Arrays.asList(new RefLikeType[] {
				util.INDEX_OUT_OF_BOUNDS_EXCEPTION,
				util.RUNTIME_EXCEPTION,
				util.EXCEPTION,
				util.THROWABLE,
		}));
		assertEquals(catchable, util.catchableSubset(set0));

		catchable.add(util.ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION);
		catchable.add(util.STRING_INDEX_OUT_OF_BOUNDS_EXCEPTION);
		assertEquals(catchable, util.catchableSubset(set1));

		if (DUMP_INTERNALS) {
			printAllSets();
		}
	}

	@Test
	public void test_05_AddingSets0() {
		if (DUMP_INTERNALS) {
			System.err.println("\n\ntestAddingSets0()");
		}
		Set<RefLikeType> expected = new ExceptionHashSet<RefLikeType>(Arrays.asList(new RefLikeType[] {
				util.INDEX_OUT_OF_BOUNDS_EXCEPTION,
		}));
		ThrowableSet set0 = add(mgr.EMPTY, util.INDEX_OUT_OF_BOUNDS_EXCEPTION,
				expected);

		expected.clear();
		expected.add(AnySubType.v(util.INDEX_OUT_OF_BOUNDS_EXCEPTION));
		ThrowableSet set1 = add(mgr.EMPTY,
				AnySubType.v(util.INDEX_OUT_OF_BOUNDS_EXCEPTION),
				expected);

		ThrowableSet result = add(set1, set0, expected);
		assertTrue("{AnySubType(E)} union {E} should equal {AnySubType(E)}",
				result == set1);

		result = add(set1, set0, expected);
		assertTrue("{E} union {AnySubType(E)} should equal {AnySubType(E)}",
				result == set1);

		if (DUMP_INTERNALS) {
			System.err.println("testAddingSets0()");
			printAllSets();
		}
	}


	@Test
	public void test_06_AddingSets1() {
		Set<RefLikeType> expected = new ExceptionHashSet<RefLikeType>(util.VM_ERRORS);
		expected.add(util.UNDECLARED_THROWABLE_EXCEPTION);
		ThrowableSet set0 = add(mgr.VM_ERRORS,
				util.UNDECLARED_THROWABLE_EXCEPTION, expected);
		expected.add(util.UNSUPPORTED_LOOK_AND_FEEL_EXCEPTION);
		set0 = add(set0, util.UNSUPPORTED_LOOK_AND_FEEL_EXCEPTION, expected);

		ThrowableSet set1 = mgr.INITIALIZATION_ERRORS;
		expected = new ExceptionHashSet<RefLikeType>();
		expected.add(AnySubType.v(util.ERROR));
		assertSameMembers(set1, expected, Collections.<AnySubType>emptySet());

		expected.add(util.UNDECLARED_THROWABLE_EXCEPTION);
		expected.add(util.UNSUPPORTED_LOOK_AND_FEEL_EXCEPTION);
		ThrowableSet result0 = add(set0, set1, expected);
		ThrowableSet result1 = add(set1, set0, expected);
		assertTrue("Adding sets should be commutative.", result0 == result1);

		Set<RefLikeType> catchable = new ExceptionHashSet<RefLikeType>(util.ALL_TEST_ERRORS_PLUS_SUPERTYPES);
		catchable.add(util.UNDECLARED_THROWABLE_EXCEPTION);
		catchable.add(util.UNSUPPORTED_LOOK_AND_FEEL_EXCEPTION);
		catchable.add(util.RUNTIME_EXCEPTION);// Superclasses of
		catchable.add(util.EXCEPTION);        // others.
		catchable.add(util.ERROR);
		catchable.add(util.THROWABLE);
		assertEquals(catchable, util.catchableSubset(result0));

		if (DUMP_INTERNALS) {
			printAllSets();
		}
	}


	@Test
	public void test_07_AddingSets2() {
		Set<RefLikeType> expected = new ExceptionHashSet<RefLikeType>(util.VM_ERRORS);
		expected.add(util.UNDECLARED_THROWABLE_EXCEPTION);
		ThrowableSet set0 = add(mgr.VM_ERRORS,
				util.UNDECLARED_THROWABLE_EXCEPTION, expected);
		expected.add(util.UNSUPPORTED_LOOK_AND_FEEL_EXCEPTION);
		set0 = add(set0, util.UNSUPPORTED_LOOK_AND_FEEL_EXCEPTION, expected);

		ThrowableSet set1 = mgr.INITIALIZATION_ERRORS;
		expected = new ExceptionHashSet<RefLikeType>();
		expected.add(AnySubType.v(util.ERROR));
		assertSameMembers(set1, expected, Collections.<AnySubType>emptySet());

		expected.add(util.UNDECLARED_THROWABLE_EXCEPTION);
		expected.add(util.UNSUPPORTED_LOOK_AND_FEEL_EXCEPTION);
		ThrowableSet result0 = add(set0, set1, expected);
		ThrowableSet result1 = add(set1, set0, expected);
		assertTrue("Adding sets should be commutative.", result0 == result1);

		Set<RefLikeType> catchable = new ExceptionHashSet<RefLikeType>(util.ALL_TEST_ERRORS_PLUS_SUPERTYPES);
		catchable.add(util.UNDECLARED_THROWABLE_EXCEPTION);
		catchable.add(util.UNSUPPORTED_LOOK_AND_FEEL_EXCEPTION);
		catchable.add(util.RUNTIME_EXCEPTION);// Superclasses of
		catchable.add(util.EXCEPTION);        // others.
		catchable.add(util.ERROR);
		catchable.add(util.THROWABLE);
		assertEquals(catchable, util.catchableSubset(result0));

		if (DUMP_INTERNALS) {
			printAllSets();
		}
	}


	@Test
	public void test_08_WhichCatchable0() {
		if (DUMP_INTERNALS) {
			System.err.println("\n\ntestWhichCatchable0()");
		}
		Set<RefLikeType> expected = new ExceptionHashSet<RefLikeType>(Arrays.asList(new RefLikeType[] {
				util.UNDECLARED_THROWABLE_EXCEPTION,
		}));

		ThrowableSet set0 = add(mgr.EMPTY, util.UNDECLARED_THROWABLE_EXCEPTION,
				expected);
		Set<RefLikeType> catchable = new ExceptionHashSet<RefLikeType>(Arrays.asList(new RefLikeType[] {
				util.UNDECLARED_THROWABLE_EXCEPTION,
				util.RUNTIME_EXCEPTION,
				util.EXCEPTION,
				util.THROWABLE,
		}));

		ThrowableSet.Pair catchableAs = set0.whichCatchableAs(util.LINKAGE_ERROR);
		assertEquals(mgr.EMPTY, catchableAs.getCaught());
		assertEquals(set0, catchableAs.getUncaught());
		assertEquals(Collections.EMPTY_SET,
				util.catchableSubset(catchableAs.getCaught()));
		assertEquals(catchable, util.catchableSubset(catchableAs.getUncaught()));

		assertTrue(set0.catchableAs(util.UNDECLARED_THROWABLE_EXCEPTION));
		catchableAs = set0.whichCatchableAs(util.UNDECLARED_THROWABLE_EXCEPTION);
		assertEquals(catchableAs.getCaught(), set0);
		assertEquals(catchableAs.getUncaught(), mgr.EMPTY);
		assertEquals(catchable, util.catchableSubset(catchableAs.getCaught()));
		assertEquals(Collections.EMPTY_SET,
				util.catchableSubset(catchableAs.getUncaught()));

		assertTrue(set0.catchableAs(util.RUNTIME_EXCEPTION));
		catchableAs = set0.whichCatchableAs(util.RUNTIME_EXCEPTION);
		assertEquals(catchableAs.getCaught(), set0);
		assertEquals(catchableAs.getUncaught(), mgr.EMPTY);
		assertEquals(catchable, util.catchableSubset(catchableAs.getCaught()));
		assertEquals(Collections.EMPTY_SET,
				util.catchableSubset(catchableAs.getUncaught()));

		assertTrue(set0.catchableAs(util.EXCEPTION));
		catchableAs = set0.whichCatchableAs(util.EXCEPTION);
		assertEquals(catchableAs.getCaught(), set0);
		assertEquals(catchableAs.getUncaught(), mgr.EMPTY);
		assertEquals(catchable, util.catchableSubset(catchableAs.getCaught()));
		assertEquals(Collections.EMPTY_SET,
				util.catchableSubset(catchableAs.getUncaught()));

		assertTrue(set0.catchableAs(util.THROWABLE));
		catchableAs = set0.whichCatchableAs(util.THROWABLE);
		assertEquals(catchableAs.getCaught(), set0);
		assertEquals(catchableAs.getUncaught(), mgr.EMPTY);
		assertEquals(catchable, util.catchableSubset(catchableAs.getCaught()));
		assertEquals(Collections.EMPTY_SET,
				util.catchableSubset(catchableAs.getUncaught()));

		assertTrue(! set0.catchableAs(util.ERROR));
		catchableAs = set0.whichCatchableAs(util.ERROR);
		assertEquals(catchableAs.getCaught(), mgr.EMPTY);
		assertEquals(catchableAs.getUncaught(), set0);
		assertEquals(Collections.EMPTY_SET,
				util.catchableSubset(catchableAs.getCaught()));
		assertEquals(catchable, util.catchableSubset(catchableAs.getUncaught()));

		if (DUMP_INTERNALS) {
			printAllSets();
		}
	}


	@Test
	public void test_09_WhichCatchable1() {
		if (DUMP_INTERNALS) {
			System.err.println("\n\ntestWhichCatchable1()");
		}
		ThrowableSet set0 = mgr.EMPTY.add(util.LINKAGE_ERROR);
		Set<RefType> catcherTypes = new ExceptionHashSet<RefType>(Arrays.asList(new RefType[] {
				util.LINKAGE_ERROR,
				util.ERROR,
				util.THROWABLE,
		}));

		assertTrue(set0.catchableAs(util.ERROR));
		ThrowableSet.Pair catchableAs = set0.whichCatchableAs(util.ERROR);
		assertEquals(set0, catchableAs.getCaught());
		assertEquals(mgr.EMPTY, catchableAs.getUncaught());
		assertEquals(catcherTypes,
				util.catchableSubset(catchableAs.getCaught()));
		assertEquals(Collections.EMPTY_SET,
				util.catchableSubset(catchableAs.getUncaught()));

		assertTrue(set0.catchableAs(util.LINKAGE_ERROR));
		catchableAs = set0.whichCatchableAs(util.LINKAGE_ERROR);
		assertEquals(set0, catchableAs.getCaught());
		assertEquals(mgr.EMPTY, catchableAs.getUncaught());
		assertEquals(catcherTypes,
				util.catchableSubset(catchableAs.getCaught()));
		assertEquals(Collections.EMPTY_SET,
				util.catchableSubset(catchableAs.getUncaught()));

		assertTrue(! set0.catchableAs(util.INCOMPATIBLE_CLASS_CHANGE_ERROR));
		catchableAs = set0.whichCatchableAs(util.INCOMPATIBLE_CLASS_CHANGE_ERROR);
		assertEquals(mgr.EMPTY, catchableAs.getCaught());
		assertEquals(set0, catchableAs.getUncaught());
		assertEquals(Collections.EMPTY_SET,
				util.catchableSubset(catchableAs.getCaught()));
		assertEquals(catcherTypes,
				util.catchableSubset(catchableAs.getUncaught()));

		assertTrue(! set0.catchableAs(util.INSTANTIATION_ERROR));
		catchableAs = set0.whichCatchableAs(util.INSTANTIATION_ERROR);
		assertEquals(mgr.EMPTY, catchableAs.getCaught());
		assertEquals(set0, catchableAs.getUncaught());
		assertEquals(Collections.EMPTY_SET,
				util.catchableSubset(catchableAs.getCaught()));
		assertEquals(catcherTypes,
				util.catchableSubset(catchableAs.getUncaught()));

		assertTrue(! set0.catchableAs(util.INTERNAL_ERROR));
		catchableAs = set0.whichCatchableAs(util.INTERNAL_ERROR);
		assertEquals(mgr.EMPTY, catchableAs.getCaught());
		assertEquals(set0, catchableAs.getUncaught());
		assertEquals(Collections.EMPTY_SET,
				util.catchableSubset(catchableAs.getCaught()));
		assertEquals(catcherTypes,
				util.catchableSubset(catchableAs.getUncaught()));

		if (DUMP_INTERNALS) {
			printAllSets();
		}
	}


	@Test
	public void test_10_WhichCatchable2() {
		if (DUMP_INTERNALS) {
			System.err.println("\n\ntestWhichCatchable2()");
		}

		ThrowableSet set0 = mgr.EMPTY.add(AnySubType.v(util.LINKAGE_ERROR));
		Set<RefType> catcherTypes = new ExceptionHashSet<RefType>(Arrays.asList(new RefType[] {
				util.CLASS_CIRCULARITY_ERROR,
				util.CLASS_FORMAT_ERROR,
				util.UNSUPPORTED_CLASS_VERSION_ERROR,
				util.EXCEPTION_IN_INITIALIZER_ERROR,
				util.INCOMPATIBLE_CLASS_CHANGE_ERROR,
				util.ABSTRACT_METHOD_ERROR,
				util.ILLEGAL_ACCESS_ERROR,
				util.INSTANTIATION_ERROR,
				util.NO_SUCH_FIELD_ERROR,
				util.NO_SUCH_METHOD_ERROR,
				util.NO_CLASS_DEF_FOUND_ERROR,
				util.UNSATISFIED_LINK_ERROR,
				util.VERIFY_ERROR,
				util.LINKAGE_ERROR,
				util.ERROR,
				util.THROWABLE,
		}));

		assertTrue(set0.catchableAs(util.ERROR));
		ThrowableSet.Pair catchableAs = set0.whichCatchableAs(util.ERROR);
		assertEquals(set0, catchableAs.getCaught());
		assertEquals(mgr.EMPTY, catchableAs.getUncaught());
		assertEquals(catcherTypes,
				util.catchableSubset(catchableAs.getCaught()));
		assertEquals(Collections.EMPTY_SET,
				util.catchableSubset(catchableAs.getUncaught()));

		assertTrue(set0.catchableAs(util.LINKAGE_ERROR));
		catchableAs = set0.whichCatchableAs(util.LINKAGE_ERROR);
		assertEquals(set0, catchableAs.getCaught());
		assertEquals(mgr.EMPTY, catchableAs.getUncaught());
		assertEquals(catcherTypes,
				util.catchableSubset(catchableAs.getCaught()));
		assertEquals(Collections.EMPTY_SET,
				util.catchableSubset(catchableAs.getUncaught()));

		assertTrue(set0.catchableAs(util.INCOMPATIBLE_CLASS_CHANGE_ERROR));
		catchableAs = set0.whichCatchableAs(util.INCOMPATIBLE_CLASS_CHANGE_ERROR);
		Set<AnySubType> expectedCaughtIncluded = new ExceptionHashSet<AnySubType>(
				Arrays.asList(new AnySubType[]
						{AnySubType.v(util.INCOMPATIBLE_CLASS_CHANGE_ERROR)}));
		Set<AnySubType> expectedCaughtExcluded = Collections.emptySet();
		Set<AnySubType> expectedUncaughtIncluded = new ExceptionHashSet<AnySubType>(
				Arrays.asList(new AnySubType[]
						{AnySubType.v(util.LINKAGE_ERROR)}));
		Set<AnySubType> expectedUncaughtExcluded = expectedCaughtIncluded;
		assertSameMembers(catchableAs,
				expectedCaughtIncluded,
				expectedCaughtExcluded,
				expectedUncaughtIncluded,
				expectedUncaughtExcluded);
		catcherTypes = new ExceptionHashSet<RefType>(Arrays.asList(new RefType[] {
				util.INCOMPATIBLE_CLASS_CHANGE_ERROR,
				util.ABSTRACT_METHOD_ERROR,
				util.ILLEGAL_ACCESS_ERROR,
				util.INSTANTIATION_ERROR,
				util.NO_SUCH_FIELD_ERROR,
				util.NO_SUCH_METHOD_ERROR,
				util.LINKAGE_ERROR,
				util.ERROR,
				util.THROWABLE,
		}));
		Set<RefType> noncatcherTypes = new ExceptionHashSet<RefType>(Arrays.asList(new RefType[] {
				util.CLASS_CIRCULARITY_ERROR,
				util.CLASS_FORMAT_ERROR,
				util.UNSUPPORTED_CLASS_VERSION_ERROR,
				util.EXCEPTION_IN_INITIALIZER_ERROR,
				util.NO_CLASS_DEF_FOUND_ERROR,
				util.UNSATISFIED_LINK_ERROR,
				util.VERIFY_ERROR,
				util.LINKAGE_ERROR,
				util.ERROR,
				util.THROWABLE,
		}));
		assertEquals(catcherTypes,
				util.catchableSubset(catchableAs.getCaught()));
		assertEquals(noncatcherTypes,
				util.catchableSubset(catchableAs.getUncaught()));

		assertTrue(set0.catchableAs(util.INSTANTIATION_ERROR));
		catchableAs = set0.whichCatchableAs(util.INSTANTIATION_ERROR);
		expectedCaughtIncluded = new ExceptionHashSet<AnySubType>(
				Arrays.asList(new AnySubType[]
						{AnySubType.v(util.INSTANTIATION_ERROR)}));
		expectedCaughtExcluded = Collections.emptySet();
		expectedUncaughtExcluded = expectedCaughtIncluded;
		assertSameMembers(catchableAs,
				expectedCaughtIncluded,
				expectedCaughtExcluded,
				expectedUncaughtIncluded,
				expectedUncaughtExcluded);
		catcherTypes = new ExceptionHashSet<RefType>(Arrays.asList(new RefType[] {
				util.INSTANTIATION_ERROR,
				util.INCOMPATIBLE_CLASS_CHANGE_ERROR,
				util.LINKAGE_ERROR,
				util.ERROR,
				util.THROWABLE,
		}));
		noncatcherTypes = new ExceptionHashSet<RefType>(Arrays.asList(new RefType[] {
				util.CLASS_CIRCULARITY_ERROR,
				util.CLASS_FORMAT_ERROR,
				util.UNSUPPORTED_CLASS_VERSION_ERROR,
				util.EXCEPTION_IN_INITIALIZER_ERROR,
				util.ABSTRACT_METHOD_ERROR,
				util.ILLEGAL_ACCESS_ERROR,
				util.NO_SUCH_FIELD_ERROR,
				util.NO_SUCH_METHOD_ERROR,
				util.NO_CLASS_DEF_FOUND_ERROR,
				util.UNSATISFIED_LINK_ERROR,
				util.VERIFY_ERROR,
				util.INCOMPATIBLE_CLASS_CHANGE_ERROR,
				util.LINKAGE_ERROR,
				util.ERROR,
				util.THROWABLE,
		}));
		assertEquals(catcherTypes,
				util.catchableSubset(catchableAs.getCaught()));
		assertEquals(noncatcherTypes,
				util.catchableSubset(catchableAs.getUncaught()));

		assertTrue(! set0.catchableAs(util.INTERNAL_ERROR));
		catchableAs = set0.whichCatchableAs(util.INTERNAL_ERROR);
		assertEquals(mgr.EMPTY, catchableAs.getCaught());
		assertEquals(set0, catchableAs.getUncaught());
		noncatcherTypes = new ExceptionHashSet<RefType>(Arrays.asList(new RefType[] {
				util.CLASS_CIRCULARITY_ERROR,
				util.CLASS_FORMAT_ERROR,
				util.UNSUPPORTED_CLASS_VERSION_ERROR,
				util.EXCEPTION_IN_INITIALIZER_ERROR,
				util.ABSTRACT_METHOD_ERROR,
				util.ILLEGAL_ACCESS_ERROR,
				util.INCOMPATIBLE_CLASS_CHANGE_ERROR,
				util.NO_SUCH_FIELD_ERROR,
				util.NO_SUCH_METHOD_ERROR,
				util.INSTANTIATION_ERROR,
				util.NO_CLASS_DEF_FOUND_ERROR,
				util.UNSATISFIED_LINK_ERROR,
				util.VERIFY_ERROR,
				util.INCOMPATIBLE_CLASS_CHANGE_ERROR,
				util.LINKAGE_ERROR,
				util.ERROR,
				util.THROWABLE,
		}));
		assertEquals(Collections.EMPTY_SET,
				util.catchableSubset(catchableAs.getCaught()));
		assertEquals(noncatcherTypes,
				util.catchableSubset(catchableAs.getUncaught()));

		if (DUMP_INTERNALS) {
			printAllSets();
		}
	}


	@Test
	public void test_11_WhichCatchable3() {
		if (DUMP_INTERNALS) {
			System.err.println("\n\ntestWhichCatchable3()");
		}

		ThrowableSet set0 = mgr.EMPTY;
		set0 = set0.add(AnySubType.v(util.ERROR));

		assertTrue(set0.catchableAs(util.INCOMPATIBLE_CLASS_CHANGE_ERROR));
		ThrowableSet.Pair catchableAs = set0.whichCatchableAs(util.INCOMPATIBLE_CLASS_CHANGE_ERROR);
		Set<AnySubType> expectedCaughtIncluded = new ExceptionHashSet<AnySubType>(
				Arrays.asList(new AnySubType[]
						{AnySubType.v(util.INCOMPATIBLE_CLASS_CHANGE_ERROR)}));
		Set<AnySubType> expectedCaughtExcluded = Collections.emptySet();
		Set<AnySubType> expectedUncaughtIncluded = new ExceptionHashSet<AnySubType>(
				Arrays.asList(new AnySubType[]
						{AnySubType.v(util.ERROR)}));
		Set<AnySubType> expectedUncaughtExcluded = expectedCaughtIncluded;
		assertTrue(ExceptionTestUtility.sameMembers(expectedCaughtIncluded,
				expectedCaughtExcluded,
				catchableAs.getCaught()));
		assertTrue(ExceptionTestUtility.sameMembers(expectedUncaughtIncluded,
				expectedUncaughtExcluded,
				catchableAs.getUncaught()));
		assertEquals(new ExceptionHashSet<RefType>(Arrays.asList(new RefType[] {
				util.THROWABLE,
				util.ERROR,
				util.LINKAGE_ERROR,
				util.INCOMPATIBLE_CLASS_CHANGE_ERROR,
				util.ABSTRACT_METHOD_ERROR,
				util.INSTANTIATION_ERROR,
				util.ILLEGAL_ACCESS_ERROR,
				util.NO_SUCH_FIELD_ERROR,
				util.NO_SUCH_METHOD_ERROR,})),
				util.catchableSubset(catchableAs.getCaught()));
		assertEquals(new ExceptionHashSet<RefLikeType>(Arrays.asList(new RefLikeType[] {
				util.THROWABLE,
				util.ERROR,
				util.AWT_ERROR,
				util.LINKAGE_ERROR,
				util.CLASS_CIRCULARITY_ERROR,
				util.CLASS_FORMAT_ERROR,
				util.UNSUPPORTED_CLASS_VERSION_ERROR,
				util.EXCEPTION_IN_INITIALIZER_ERROR,
				util.NO_CLASS_DEF_FOUND_ERROR,
				util.UNSATISFIED_LINK_ERROR,
				util.VERIFY_ERROR,
				util.THREAD_DEATH,
				util.VIRTUAL_MACHINE_ERROR,
				util.INTERNAL_ERROR,
				util.OUT_OF_MEMORY_ERROR,
				util.STACK_OVERFLOW_ERROR,
				util.UNKNOWN_ERROR,})),
				util.catchableSubset(catchableAs.getUncaught()));

		set0 = catchableAs.getUncaught();

		assertTrue(set0.catchableAs(util.THROWABLE));
		catchableAs = set0.whichCatchableAs(util.THROWABLE);
		assertEquals(set0, catchableAs.getCaught());
		assertEquals(mgr.EMPTY, catchableAs.getUncaught());
		assertEquals(new ExceptionHashSet<RefType>(Arrays.asList(new RefType[] {
				util.THROWABLE,
				util.ERROR,
				util.AWT_ERROR,
				util.LINKAGE_ERROR,
				util.CLASS_CIRCULARITY_ERROR,
				util.CLASS_FORMAT_ERROR,
				util.UNSUPPORTED_CLASS_VERSION_ERROR,
				util.EXCEPTION_IN_INITIALIZER_ERROR,
				util.NO_CLASS_DEF_FOUND_ERROR,
				util.UNSATISFIED_LINK_ERROR,
				util.VERIFY_ERROR,
				util.THREAD_DEATH,
				util.VIRTUAL_MACHINE_ERROR,
				util.INTERNAL_ERROR,
				util.OUT_OF_MEMORY_ERROR,
				util.STACK_OVERFLOW_ERROR,
				util.UNKNOWN_ERROR,})),
				util.catchableSubset(catchableAs.getCaught()));
		assertEquals(Collections.EMPTY_SET,
				util.catchableSubset(catchableAs.getUncaught()));

		assertTrue(set0.catchableAs(util.ERROR));
		catchableAs = set0.whichCatchableAs(util.ERROR);
		assertEquals(set0, catchableAs.getCaught());
		assertEquals(mgr.EMPTY, catchableAs.getUncaught());
		assertEquals(new ExceptionHashSet<RefType>(Arrays.asList(new RefType[] {
				util.THROWABLE,
				util.ERROR,
				util.AWT_ERROR,
				util.LINKAGE_ERROR,
				util.CLASS_CIRCULARITY_ERROR,
				util.CLASS_FORMAT_ERROR,
				util.UNSUPPORTED_CLASS_VERSION_ERROR,
				util.EXCEPTION_IN_INITIALIZER_ERROR,
				util.NO_CLASS_DEF_FOUND_ERROR,
				util.UNSATISFIED_LINK_ERROR,
				util.VERIFY_ERROR,
				util.THREAD_DEATH,
				util.VIRTUAL_MACHINE_ERROR,
				util.INTERNAL_ERROR,
				util.OUT_OF_MEMORY_ERROR,
				util.STACK_OVERFLOW_ERROR,
				util.UNKNOWN_ERROR,})),
				util.catchableSubset(catchableAs.getCaught()));
		assertEquals(Collections.EMPTY_SET,
				util.catchableSubset(catchableAs.getUncaught()));

		assertTrue(set0.catchableAs(util.LINKAGE_ERROR));
		catchableAs = set0.whichCatchableAs(util.LINKAGE_ERROR);
		expectedCaughtIncluded = new ExceptionHashSet<AnySubType>(
				Arrays.asList(new AnySubType[]
						{AnySubType.v(util.LINKAGE_ERROR)}));
		expectedCaughtExcluded = new ExceptionHashSet<AnySubType>(
				Arrays.asList(new AnySubType[]
						{AnySubType.v(util.INCOMPATIBLE_CLASS_CHANGE_ERROR)}));
		expectedUncaughtIncluded = new ExceptionHashSet<AnySubType>(
				Arrays.asList(new AnySubType[]
						{AnySubType.v(util.ERROR)}));
		expectedUncaughtExcluded = expectedCaughtIncluded;
		assertTrue(ExceptionTestUtility.sameMembers(expectedCaughtIncluded,
				expectedCaughtExcluded,
				catchableAs.getCaught()));
		assertTrue(ExceptionTestUtility.sameMembers(expectedUncaughtIncluded,
				expectedUncaughtExcluded,
				catchableAs.getUncaught()));
		assertEquals(new ExceptionHashSet<RefType>(Arrays.asList(new RefType[] {
				util.THROWABLE,
				util.ERROR,
				util.LINKAGE_ERROR,
				util.CLASS_CIRCULARITY_ERROR,
				util.CLASS_FORMAT_ERROR,
				util.UNSUPPORTED_CLASS_VERSION_ERROR,
				util.EXCEPTION_IN_INITIALIZER_ERROR,
				util.NO_CLASS_DEF_FOUND_ERROR,
				util.UNSATISFIED_LINK_ERROR,
				util.VERIFY_ERROR,})),
				util.catchableSubset(catchableAs.getCaught()));
		assertEquals(new ExceptionHashSet<RefType>(Arrays.asList(new RefType[] {
				util.THROWABLE,
				util.ERROR,
				util.AWT_ERROR,
				util.THREAD_DEATH,
				util.VIRTUAL_MACHINE_ERROR,
				util.INTERNAL_ERROR,
				util.OUT_OF_MEMORY_ERROR,
				util.STACK_OVERFLOW_ERROR,
				util.UNKNOWN_ERROR,})),
				util.catchableSubset(catchableAs.getUncaught()));

		assertTrue(! set0.catchableAs(util.INCOMPATIBLE_CLASS_CHANGE_ERROR));
		catchableAs = set0.whichCatchableAs(util.INCOMPATIBLE_CLASS_CHANGE_ERROR);
		assertEquals(mgr.EMPTY, catchableAs.getCaught());
		assertEquals(set0, catchableAs.getUncaught());
		assertEquals(Collections.EMPTY_SET,
				util.catchableSubset(catchableAs.getCaught()));
		assertEquals(new ExceptionHashSet<RefType>(Arrays.asList(new RefType[] {
				util.THROWABLE,
				util.ERROR,
				util.LINKAGE_ERROR,
				util.AWT_ERROR,
				util.THREAD_DEATH,
				util.VIRTUAL_MACHINE_ERROR,
				util.INTERNAL_ERROR,
				util.OUT_OF_MEMORY_ERROR,
				util.STACK_OVERFLOW_ERROR,
				util.CLASS_CIRCULARITY_ERROR,
				util.CLASS_FORMAT_ERROR,
				util.UNSUPPORTED_CLASS_VERSION_ERROR,
				util.EXCEPTION_IN_INITIALIZER_ERROR,
				util.NO_CLASS_DEF_FOUND_ERROR,
				util.UNSATISFIED_LINK_ERROR,
				util.VERIFY_ERROR,
				util.UNKNOWN_ERROR,})),
				util.catchableSubset(catchableAs.getUncaught()));

		catchableAs = set0.whichCatchableAs(util.ILLEGAL_ACCESS_ERROR);
		assertEquals(mgr.EMPTY, catchableAs.getCaught());
		assertEquals(set0, catchableAs.getUncaught());
		assertEquals(Collections.EMPTY_SET,
				util.catchableSubset(catchableAs.getCaught()));
		assertEquals(new ExceptionHashSet<RefType>(Arrays.asList(new RefType[] {
				util.THROWABLE,
				util.ERROR,
				util.LINKAGE_ERROR,
				util.AWT_ERROR,
				util.THREAD_DEATH,
				util.VIRTUAL_MACHINE_ERROR,
				util.INTERNAL_ERROR,
				util.OUT_OF_MEMORY_ERROR,
				util.STACK_OVERFLOW_ERROR,
				util.CLASS_CIRCULARITY_ERROR,
				util.CLASS_FORMAT_ERROR,
				util.UNSUPPORTED_CLASS_VERSION_ERROR,
				util.EXCEPTION_IN_INITIALIZER_ERROR,
				util.NO_CLASS_DEF_FOUND_ERROR,
				util.UNSATISFIED_LINK_ERROR,
				util.VERIFY_ERROR,
				util.UNKNOWN_ERROR,})),
				util.catchableSubset(catchableAs.getUncaught()));

		if (DUMP_INTERNALS) {
			printAllSets();
		}
	}


	@Test
	public void test_12_WhichCatchable10() {
		if (DUMP_INTERNALS) {
			System.err.println("\n\ntestWhichCatchable3()");
		}

		ThrowableSet set0 = mgr.EMPTY;
		set0 = set0.add(AnySubType.v(util.THROWABLE));

		assertTrue(set0.catchableAs(util.ARITHMETIC_EXCEPTION));
		ThrowableSet.Pair catchableAs = set0.whichCatchableAs(util.ARITHMETIC_EXCEPTION);
		assertSameMembers(catchableAs,
				new RefLikeType[] {
				AnySubType.v(util.ARITHMETIC_EXCEPTION),
		},
		new AnySubType[] {
		},
		new RefLikeType[] {
				AnySubType.v(util.THROWABLE),
		},
		new AnySubType[] {
				AnySubType.v(util.ARITHMETIC_EXCEPTION),
		});
		assertEquals(new ExceptionHashSet<RefLikeType>(Arrays.asList(new RefLikeType[] {
				util.THROWABLE,
				util.EXCEPTION,
				util.RUNTIME_EXCEPTION,
				util.ARITHMETIC_EXCEPTION,})),
				util.catchableSubset(catchableAs.getCaught()));
		HashSet<RefLikeType> expectedUncaught = new HashSet<RefLikeType>(util.ALL_TEST_THROWABLES);
		expectedUncaught.remove(util.ARITHMETIC_EXCEPTION);
		assertEquals(expectedUncaught,
				util.catchableSubset(catchableAs.getUncaught()));

		set0 = catchableAs.getUncaught();
		assertTrue(set0.catchableAs(util.ABSTRACT_METHOD_ERROR));
		catchableAs = set0.whichCatchableAs(util.ABSTRACT_METHOD_ERROR);
		assertSameMembers(catchableAs,
				new RefLikeType[] {
				AnySubType.v(util.ABSTRACT_METHOD_ERROR),
		},
		new AnySubType[] {
		},
		new RefLikeType[] {
				AnySubType.v(util.THROWABLE),
		},
		new AnySubType[] {
				AnySubType.v(util.ARITHMETIC_EXCEPTION),
				AnySubType.v(util.ABSTRACT_METHOD_ERROR),
		});
		assertEquals(new ExceptionHashSet<RefLikeType>(Arrays.asList(new RefLikeType[] {
				util.THROWABLE,
				util.ERROR,
				util.LINKAGE_ERROR,
				util.INCOMPATIBLE_CLASS_CHANGE_ERROR,
				util.ABSTRACT_METHOD_ERROR,})),
				util.catchableSubset(catchableAs.getCaught()));
		expectedUncaught.remove(util.ABSTRACT_METHOD_ERROR);
		assertEquals(expectedUncaught,
				util.catchableSubset(catchableAs.getUncaught()));

		set0 = catchableAs.getUncaught();
		assertTrue(set0.catchableAs(util.RUNTIME_EXCEPTION));
		catchableAs = set0.whichCatchableAs(util.RUNTIME_EXCEPTION);
		assertSameMembers(catchableAs,
				new RefLikeType[] {
				AnySubType.v(util.RUNTIME_EXCEPTION),
		},
		new AnySubType[] {
				AnySubType.v(util.ARITHMETIC_EXCEPTION),
		},
		new RefLikeType[] {
				AnySubType.v(util.THROWABLE),
		},
		new AnySubType[] {
				AnySubType.v(util.RUNTIME_EXCEPTION),
				AnySubType.v(util.ABSTRACT_METHOD_ERROR),
		});
		assertEquals(new ExceptionHashSet<RefLikeType>(Arrays.asList(new RefLikeType[] {
				util.THROWABLE,
				util.EXCEPTION,
				util.RUNTIME_EXCEPTION,
				util.ARRAY_STORE_EXCEPTION,
				util.CLASS_CAST_EXCEPTION,
				util.ILLEGAL_MONITOR_STATE_EXCEPTION,
				util.INDEX_OUT_OF_BOUNDS_EXCEPTION,
				util.ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION,
				util.STRING_INDEX_OUT_OF_BOUNDS_EXCEPTION,
				util.NEGATIVE_ARRAY_SIZE_EXCEPTION,
				util.NULL_POINTER_EXCEPTION,
				util.UNDECLARED_THROWABLE_EXCEPTION})),
				util.catchableSubset(catchableAs.getCaught()));
		expectedUncaught.remove(util.RUNTIME_EXCEPTION);
		expectedUncaught.remove(util.ARRAY_STORE_EXCEPTION);
		expectedUncaught.remove(util.CLASS_CAST_EXCEPTION);
		expectedUncaught.remove(util.ILLEGAL_MONITOR_STATE_EXCEPTION);
		expectedUncaught.remove(util.INDEX_OUT_OF_BOUNDS_EXCEPTION);
		expectedUncaught.remove(util.ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION);
		expectedUncaught.remove(util.STRING_INDEX_OUT_OF_BOUNDS_EXCEPTION);
		expectedUncaught.remove(util.NEGATIVE_ARRAY_SIZE_EXCEPTION);
		expectedUncaught.remove(util.NULL_POINTER_EXCEPTION);
		expectedUncaught.remove(util.UNDECLARED_THROWABLE_EXCEPTION);
		assertEquals(expectedUncaught,
				util.catchableSubset(catchableAs.getUncaught()));
	}


	@Test
	public void test_13_AddAfterWhichCatchableAs0() {
		if (DUMP_INTERNALS) {
			System.err.println("\n\ntestAddAfterWhichCatchable0()");
		}

		ThrowableSet anyError = mgr.EMPTY.add(AnySubType.v(util.ERROR));

		assertTrue(anyError.catchableAs(util.LINKAGE_ERROR));
		ThrowableSet.Pair catchableAs = anyError.whichCatchableAs(util.LINKAGE_ERROR);
		assertSameMembers(catchableAs,
				new RefLikeType[] {
				AnySubType.v(util.LINKAGE_ERROR),
		},
		new AnySubType[] {
		},
		new RefLikeType[] {
				AnySubType.v(util.ERROR),
		},
		new AnySubType[] {
				AnySubType.v(util.LINKAGE_ERROR),
		});

		ThrowableSet anyErrorMinusLinkage = catchableAs.getUncaught();
		try {
			ThrowableSet anyErrorMinusLinkagePlusIncompatibleClassChange
			= anyErrorMinusLinkage.add(util.INCOMPATIBLE_CLASS_CHANGE_ERROR);
			fail("add(IncompatiableClassChangeError) after removing LinkageError should currently generate an exception");

			// Following documents what we would like to be able to implement:
			assertSameMembers(anyErrorMinusLinkagePlusIncompatibleClassChange,
					new RefLikeType[] {
					AnySubType.v(util.ERROR),
					util.INCOMPATIBLE_CLASS_CHANGE_ERROR,
			},
			new AnySubType[] {
					AnySubType.v(util.LINKAGE_ERROR),
			});
		} catch (ThrowableSet.AlreadyHasExclusionsException e) {
			// this is what should happen.
		}

		try {
			ThrowableSet anyErrorMinusLinkagePlusAnyIncompatibleClassChange
			= anyErrorMinusLinkage.add(AnySubType.v(util.INCOMPATIBLE_CLASS_CHANGE_ERROR));
			fail("add(AnySubType.v(IncompatiableClassChangeError)) after removing LinkageError should currently generate an exception");

			// Following documents what we would like to be able to implement:
			assertSameMembers(anyErrorMinusLinkagePlusAnyIncompatibleClassChange,
					new RefLikeType[] {
					AnySubType.v(util.ERROR),
					AnySubType.v(util.INCOMPATIBLE_CLASS_CHANGE_ERROR),
			},
			new AnySubType[] {
					AnySubType.v(util.LINKAGE_ERROR),
			});
		} catch (ThrowableSet.AlreadyHasExclusionsException e) {
			// this is what should happen.
		}

		// Add types that should not change the set.
		ThrowableSet sameSet
		= anyErrorMinusLinkage.add(util.VIRTUAL_MACHINE_ERROR);
		assertTrue(sameSet == anyErrorMinusLinkage);
		assertSameMembers(sameSet,
				new RefLikeType[] {
				AnySubType.v(util.ERROR),
		},
		new AnySubType[] {
				AnySubType.v(util.LINKAGE_ERROR),
		});
		sameSet
		= anyErrorMinusLinkage.add(AnySubType.v(util.VIRTUAL_MACHINE_ERROR));
		assertTrue(sameSet == anyErrorMinusLinkage);
		assertSameMembers(sameSet,
				new RefLikeType[] {
				AnySubType.v(util.ERROR),
		},
		new AnySubType[] {
				AnySubType.v(util.LINKAGE_ERROR),
		});

		ThrowableSet anyErrorMinusLinkagePlusArrayIndex
		= anyErrorMinusLinkage.add(util.ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION);
		assertSameMembers(anyErrorMinusLinkagePlusArrayIndex,
				new RefLikeType[] {
				AnySubType.v(util.ERROR),
				util.ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION,
		},
		new AnySubType[] {
				AnySubType.v(util.LINKAGE_ERROR),
		});

		ThrowableSet anyErrorMinusLinkagePlusAnyIndex
		= anyErrorMinusLinkagePlusArrayIndex.add(AnySubType.v(util.INDEX_OUT_OF_BOUNDS_EXCEPTION));
		assertSameMembers(anyErrorMinusLinkagePlusAnyIndex,
				new RefLikeType[] {
				AnySubType.v(util.ERROR),
				AnySubType.v(util.INDEX_OUT_OF_BOUNDS_EXCEPTION),
		},
		new AnySubType[] {
				AnySubType.v(util.LINKAGE_ERROR),
		});

		ThrowableSet anyErrorMinusLinkagePlusAnyRuntime
		= anyErrorMinusLinkagePlusAnyIndex.add(AnySubType.v(util.RUNTIME_EXCEPTION));
		assertSameMembers(anyErrorMinusLinkagePlusAnyRuntime,
				new RefLikeType[] {
				AnySubType.v(util.ERROR),
				AnySubType.v(util.RUNTIME_EXCEPTION),
		},
		new AnySubType[] {
				AnySubType.v(util.LINKAGE_ERROR),
		});

		try {
			ThrowableSet anyErrorMinusLinkagePlusAnyRuntimePlusError
			= anyErrorMinusLinkagePlusAnyRuntime.add(AnySubType.v(util.ERROR));
			fail("add(AnySubType(Error)) after removing LinkageError should currently generate an exception.");

			// This documents what we would like to implement:
			assertSameMembers(anyErrorMinusLinkagePlusAnyRuntimePlusError,
					new RefLikeType[] {
					AnySubType.v(util.ERROR),
					AnySubType.v(util.RUNTIME_EXCEPTION),
			},
			new AnySubType[] {
			});
		} catch (ThrowableSet.AlreadyHasExclusionsException e) {
			// This is what should happen.
		}

		try {
			ThrowableSet anyErrorMinusLinkagePlusAnyRuntimePlusLinkageError
			= anyErrorMinusLinkagePlusAnyRuntime.add(AnySubType.v(util.LINKAGE_ERROR));
			fail("add(AnySubType(LinkageError)) after removing LinkageError should currently generate an exception.");

			// This documents what we would like to implement:
			assertSameMembers(anyErrorMinusLinkagePlusAnyRuntimePlusLinkageError,
					new RefLikeType[] {
					AnySubType.v(util.ERROR),
					AnySubType.v(util.RUNTIME_EXCEPTION),
			},
			new AnySubType[] {
			});
		} catch (ThrowableSet.AlreadyHasExclusionsException e) {
			// This is what should happen.
		}

	}

	@Test
	public void test_14_WhichCatchablePhantom0() {
		if (DUMP_INTERNALS) {
			System.err.println("\n\ntestWhichCatchablePhantom0()");
		}

		ThrowableSet anyError = mgr.EMPTY.add(AnySubType.v(util.ERROR));

		assertSameMembers(anyError.whichCatchableAs(util.LINKAGE_ERROR),
				new RefLikeType[] { AnySubType.v(util.LINKAGE_ERROR) },
				new AnySubType[] {},
				new RefLikeType[] { AnySubType.v(util.ERROR) },
				new AnySubType[] { AnySubType.v(util.LINKAGE_ERROR) });
		assertSameMembers(anyError.whichCatchableAs(util.PHANTOM_EXCEPTION1),
				new RefLikeType[] {},
				new AnySubType[] {},
				new RefLikeType[] { AnySubType.v(util.ERROR) },
				new AnySubType[] {});

		assertTrue(anyError.catchableAs(util.LINKAGE_ERROR));
		assertFalse(anyError.catchableAs(util.PHANTOM_EXCEPTION1));

		ThrowableSet phantomOnly = mgr.EMPTY.add(util.PHANTOM_EXCEPTION1);

		assertSameMembers(phantomOnly.whichCatchableAs(util.LINKAGE_ERROR),
				new RefLikeType[] {},
				new AnySubType[] {},
				new RefLikeType[] { util.PHANTOM_EXCEPTION1 },
				new AnySubType[] {});
		assertSameMembers(phantomOnly.whichCatchableAs(util.PHANTOM_EXCEPTION2),
				new RefLikeType[] {},
				new AnySubType[] {},
				new RefLikeType[] { util.PHANTOM_EXCEPTION1 },
				new AnySubType[] {});
		assertSameMembers(phantomOnly.whichCatchableAs(util.PHANTOM_EXCEPTION1),
				new RefLikeType[] { util.PHANTOM_EXCEPTION1 },
				new AnySubType[] {},
				new RefLikeType[] {},
				new AnySubType[] {});

		assertFalse(phantomOnly.catchableAs(util.LINKAGE_ERROR));
		assertTrue(phantomOnly.catchableAs(util.PHANTOM_EXCEPTION1));
		assertFalse(phantomOnly.catchableAs(util.PHANTOM_EXCEPTION2));

		ThrowableSet bothPhantoms = phantomOnly.add(util.PHANTOM_EXCEPTION2);

		assertSameMembers(bothPhantoms.whichCatchableAs(util.PHANTOM_EXCEPTION1),
				new RefLikeType[] { util.PHANTOM_EXCEPTION1 },
				new AnySubType[] {},
				new RefLikeType[] { util.PHANTOM_EXCEPTION2 },
				new AnySubType[] {});
		assertSameMembers(bothPhantoms.whichCatchableAs(util.PHANTOM_EXCEPTION2),
				new RefLikeType[] { util.PHANTOM_EXCEPTION2 },
				new AnySubType[] {},
				new RefLikeType[] { util.PHANTOM_EXCEPTION1 },
				new AnySubType[] {});

		assertFalse(bothPhantoms.catchableAs(util.LINKAGE_ERROR));
		assertTrue(bothPhantoms.catchableAs(util.PHANTOM_EXCEPTION1));
		assertTrue(bothPhantoms.catchableAs(util.PHANTOM_EXCEPTION2));

		ThrowableSet bothPhantoms2 = phantomOnly.add(util.PHANTOM_EXCEPTION2);
		assertTrue(bothPhantoms == bothPhantoms2);

		ThrowableSet throwableOnly = mgr.EMPTY.add(util.THROWABLE);
		assertFalse(throwableOnly.catchableAs(util.PHANTOM_EXCEPTION1));

		ThrowableSet allThrowables = mgr.EMPTY.add(AnySubType.v(util.THROWABLE));
		assertTrue(allThrowables.catchableAs(util.PHANTOM_EXCEPTION1));
	}

	@Test
	public void test_14_WhichCatchablePhantom1() {
		if (DUMP_INTERNALS) {
			System.err.println("\n\ntestWhichCatchablePhantom1()");
		}

		ThrowableSet phantomOnly = mgr.EMPTY.add(AnySubType.v(util.PHANTOM_EXCEPTION1));

		assertSameMembers(phantomOnly.whichCatchableAs(util.LINKAGE_ERROR),
				new RefLikeType[] {},
				new AnySubType[] {},
				new RefLikeType[] { AnySubType.v(util.PHANTOM_EXCEPTION1) },
				new AnySubType[] {});
		assertSameMembers(phantomOnly.whichCatchableAs(util.PHANTOM_EXCEPTION2),
				new RefLikeType[] {},
				new AnySubType[] {},
				new RefLikeType[] { AnySubType.v(util.PHANTOM_EXCEPTION1) },
				new AnySubType[] {});
		assertSameMembers(phantomOnly.whichCatchableAs(util.PHANTOM_EXCEPTION1),
				new RefLikeType[] { AnySubType.v(util.PHANTOM_EXCEPTION1) },
				new AnySubType[] {},
				new RefLikeType[] {},
				new AnySubType[] {});

		assertFalse(phantomOnly.catchableAs(util.LINKAGE_ERROR));
		assertTrue(phantomOnly.catchableAs(util.PHANTOM_EXCEPTION1));
		assertFalse(phantomOnly.catchableAs(util.PHANTOM_EXCEPTION2));

		ThrowableSet bothPhantoms = phantomOnly.add(AnySubType.v(util.PHANTOM_EXCEPTION2));

		assertSameMembers(bothPhantoms.whichCatchableAs(util.PHANTOM_EXCEPTION1),
				new RefLikeType[] { AnySubType.v(util.PHANTOM_EXCEPTION1) },
				new AnySubType[] {},
				new RefLikeType[] { AnySubType.v(util.PHANTOM_EXCEPTION2) },
				new AnySubType[] {});
		assertSameMembers(bothPhantoms.whichCatchableAs(util.PHANTOM_EXCEPTION2),
				new RefLikeType[] { AnySubType.v(util.PHANTOM_EXCEPTION2) },
				new AnySubType[] {},
				new RefLikeType[] { AnySubType.v(util.PHANTOM_EXCEPTION1) },
				new AnySubType[] {});

		assertFalse(bothPhantoms.catchableAs(util.LINKAGE_ERROR));
		assertTrue(bothPhantoms.catchableAs(util.PHANTOM_EXCEPTION1));
		assertTrue(bothPhantoms.catchableAs(util.PHANTOM_EXCEPTION2));

		ThrowableSet bothPhantoms2 = phantomOnly.add(AnySubType.v(util.PHANTOM_EXCEPTION2));
		assertTrue(bothPhantoms == bothPhantoms2);
	}

	void printAllSets() {
		for (ThrowableSet s : mgr.getThrowableSets()) {
			System.err.println(s.toString());
			System.err.println("\n\tMemoized Adds:");
			for (Map.Entry<Object, ThrowableSet> entry : s.getMemoizedAdds().entrySet()) {
				System.err.print(' ');
				if (entry.getKey() instanceof ThrowableSet) {
					System.err.print(((ThrowableSet) entry.getKey()).toBriefString());
				} else {
					System.err.print(entry.getKey().toString());
				}
				System.err.print('=');
				System.err.print(entry.getValue().toBriefString());
				System.err.print('\n');
			}
		}
	}


	/*
    // Suite that uses a prescribed order, rather than whatever
    // order reflection produces.
    public static Test cannedSuite() {
        TestSuite suite = new TestSuite();
        suite.addTest(new ThrowableSetTest("testInitialState"));
        suite.addTest(new ThrowableSetTest("testSingleInstance0"));
        suite.addTest(new ThrowableSetTest("testSingleInstance1"));
        suite.addTest(new ThrowableSetTest("testAddingSubclasses"));
        suite.addTest(new ThrowableSetTest("testAddingSets0"));
        suite.addTest(new ThrowableSetTest("testAddingSets1"));
        TestSetup setup = new ThrowableSetTestSetup(suite);
        return setup;
    }


    public static Test reflectionSuite() {
        TestSuite suite = new TestSuite(ThrowableSetTest.class);
        TestSetup setup = new ThrowableSetTestSetup(suite);
        return setup;
    }

    public static Test suite() {
        Scene.v().loadBasicClasses();
        return reflectionSuite();
    }

    public static void main(String arg[]) {
        if (arg.length > 0) {
            jdkLocation = arg[0];
        }
        Scene.v().loadBasicClasses();
        junit.textui.TestRunner.run(reflectionSuite());
        System.out.println(ThrowableSet.Manager.v().reportInstrumentation());
    }
	 */
}

