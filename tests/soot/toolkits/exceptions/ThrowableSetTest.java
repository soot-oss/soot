package soot.toolkits.exceptions;

import soot.*;
import java.util.*;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import soot.toolkits.exceptions.ExceptionTestUtility.*;

public class ThrowableSetTest extends TestCase {

    final static boolean DUMP_INTERNALS = false;
    final static ThrowableSet.Manager mgr = ThrowableSet.Manager.v();

    private static String jdkLocation       // Can be changed by main().
        = "/usr/localcc/pkgs/jdk1.4/jre/lib/rt.jar";

    // We need to keep expectedSizeToSets and expectedMemoizations
    // static so that JUnit won't clear it out between the execution
    // of individual tests.
    private static ExpectedSizeToSets expectedSizeToSets = new ExpectedSizeToSets();
    static {
	// The empty set.
	Set temp = new ExceptionHashSet();
	expectedSizeToSets.add(temp);

	// All Throwables set.
	temp = new ExceptionHashSet();
	temp.add(AnySubType.v(Scene.v().getRefType("java.lang.Throwable")));
	expectedSizeToSets.add(temp);
	
	// VM errors set.
	temp = new ExceptionHashSet();
	temp.add(Scene.v().getRefType("java.lang.InternalError"));
	temp.add(Scene.v().getRefType("java.lang.OutOfMemoryError"));
	temp.add(Scene.v().getRefType("java.lang.StackOverflowError"));
	temp.add(Scene.v().getRefType("java.lang.UnknownError"));
	temp.add(Scene.v().getRefType("java.lang.ThreadDeath"));
	expectedSizeToSets.add(temp);

	// Resolve Class errors set.
	Set classErrors = new ExceptionHashSet();
	classErrors.add(Scene.v().getRefType("java.lang.ClassCircularityError"));
	classErrors.add(AnySubType.v(Scene.v().getRefType("java.lang.ClassFormatError")));
	classErrors.add(Scene.v().getRefType("java.lang.IllegalAccessError"));
	classErrors.add(Scene.v().getRefType("java.lang.IncompatibleClassChangeError"));
	classErrors.add(Scene.v().getRefType("java.lang.LinkageError"));
	classErrors.add(Scene.v().getRefType("java.lang.NoClassDefFoundError"));
	classErrors.add(Scene.v().getRefType("java.lang.VerifyError"));
	expectedSizeToSets.add(classErrors);

	// Resolve Field errors set.
	temp = new ExceptionHashSet(classErrors);
	temp.add(Scene.v().getRefType("java.lang.NoSuchFieldError"));
	expectedSizeToSets.add(temp);

	// Resolve method errors set.
	temp = new ExceptionHashSet(classErrors);
	temp.add(Scene.v().getRefType("java.lang.AbstractMethodError"));
	temp.add(Scene.v().getRefType("java.lang.NoSuchMethodError"));
	temp.add(Scene.v().getRefType("java.lang.UnsatisfiedLinkError"));
	expectedSizeToSets.add(temp);

	// Initialization errors set.
	temp = new ExceptionHashSet();
	temp.add(AnySubType.v(Scene.v().getRefType("java.lang.Error")));
	expectedSizeToSets.add(temp);
    }

    private static ExpectedMemoizations expectedMemoizations = 
	new ExpectedMemoizations();


    // A class for verifying that the sizeToSetsMap
    // follows our expectations. 
    static class ExpectedSizeToSets {
	private Map expectedMap = new HashMap(); // from Integer to Set.
	
	ExpectedSizeToSets() {
	}

	void add(Set set) {
	    Integer sz = new Integer(set.size());
	    Set values = (Set) expectedMap.get(sz);
	    if (values == null) {
		values = new HashSet();
		expectedMap.put(sz, values);
	    }
	    // Make sure we have our own copy.
	    values.add(new ExceptionHashSet(set));
	}

	void addAndCheck(Set set) {
	    this.add(set);
	    assertTrue(this.match());
	}

	boolean match() {
	    boolean result = true;
	    Map actualMap = mgr.getSizeToSets();
	    if (expectedMap.size() != actualMap.size()) {
		result = false;
	    } else {
		setloop: 
		for (Iterator i = expectedMap.keySet().iterator(); 
		     i.hasNext(); ) {
		    Integer key = (Integer) i.next();
		    Set expectedValues = (Set) expectedMap.get(key);

		    // To minimize restrictions on the contents of 
		    // sizeToSets, use only the Collection interface
		    // to access its values:
		    Collection actualValues = (Collection) actualMap.get(key);
		
		    if (expectedValues.size() != actualValues.size()) {
			result = false;
			break setloop;
		    }
		    for (Iterator j = actualValues.iterator(); j.hasNext(); ) {
			ThrowableSet actual = (ThrowableSet) j.next();
			Set actualTypes = 
			    new ExceptionHashSet(actual.types());
			if (! expectedValues.contains(actualTypes)) {
			    result = false;
			    break setloop;
			}
		    }
		}
	    }
	    if (DUMP_INTERNALS) {
		if (! result) System.err.println("!!!ExpectedSizeToSets.match() FAILED!!!");
		System.err.println("expectedMap:");
		System.err.println(expectedMap.toString());
		System.err.println("actualMap:");
		System.err.println(actualMap.toString());
		System.err.flush();
	    } 
	    return result;
	}
    }
    
    // A class to check that memoized results match what we expect.
    // Admittedly, this amounts to a reimplementation of the memoized
    // structures within ThrowableSet -- I'm hoping that the two 
    // implementations will have different bugs!
    static class ExpectedMemoizations {
	Map throwableSetToMemoized = new HashMap();

	void checkAdd(ThrowableSet lhs, Object rhs, ThrowableSet result) {
	    // rhs should be either a ThrowableSet or a RefType.

	    Map actualMemoized = lhs.getMemoizedAdds();
	    assertTrue(actualMemoized.get(rhs) == result);

	    Map expectedMemoized = (Map) throwableSetToMemoized.get(lhs);
	    if (expectedMemoized == null) {
		expectedMemoized = new HashMap();
		throwableSetToMemoized.put(lhs, expectedMemoized);
	    }
	    expectedMemoized.put(rhs, result);
	    assertEquals(expectedMemoized, actualMemoized);
	}
    }

    private ThrowableSet checkAdd(ThrowableSet lhs, Object rhs,
			  Set expectedResult, ThrowableSet actualResult) {
	// Utility routine used by the next three add()s.

	assertTrue(util.sameMembers(expectedResult, actualResult));
	expectedSizeToSets.addAndCheck(expectedResult);
	expectedMemoizations.checkAdd(lhs, rhs, actualResult);
	return actualResult;
    }


    private ExceptionTestUtility util = new ExceptionTestUtility(jdkLocation);

    private ThrowableSet add(ThrowableSet lhs, ThrowableSet rhs, 
			     Set expectedResult) {
	// Add rhs to lhs, checking the results.

	ThrowableSet actualResult = lhs.add(rhs);
	return checkAdd(lhs, rhs, expectedResult, actualResult);
    }

    private ThrowableSet add(ThrowableSet lhs, RefType rhs,
			     Set expectedResult) {
	// Add rhs to lhs, checking the results.

	ThrowableSet actualResult = lhs.add(rhs);
	return checkAdd(lhs, rhs, expectedResult, actualResult);
    }

    private ThrowableSet add(ThrowableSet lhs, AnySubType rhs,
			     Set expectedResult) {
	// Add rhs to lhs, checking the results.

	ThrowableSet actualResult = lhs.add(rhs);
	return checkAdd(lhs, rhs, expectedResult, actualResult);
    }

    public ThrowableSetTest(String name) {
	super(name);
    }


    public void testInitialState() {
	if (DUMP_INTERNALS) {
	    System.err.println("\n\ntestInitialState()");
	}
	assertTrue(expectedSizeToSets.match());
	if (DUMP_INTERNALS) {
	    printAllSets();
	}
    }

    public void testSingleInstance0() {
	if (DUMP_INTERNALS) {
	    System.err.println("\n\ntestSingleInstance0()");
	}
	Set expected = new ExceptionHashSet(Arrays.asList(new RefType[] {
	    util.UNDECLARED_THROWABLE_EXCEPTION,
	}));

	ThrowableSet set0 = add(mgr.EMPTY, util.UNDECLARED_THROWABLE_EXCEPTION,
				expected);
	ThrowableSet set1 = add(mgr.EMPTY, util.UNDECLARED_THROWABLE_EXCEPTION,
				expected);
	assertTrue("The same ThrowableSet object should represent two sets containing the same single class.",
		   set0 == set1);

	Set catchable = new ExceptionHashSet(Arrays.asList(new RefType[] {
	    util.UNDECLARED_THROWABLE_EXCEPTION,
	    util.RUNTIME_EXCEPTION,
	    util.EXCEPTION,
	    util.THROWABLE,
	}));
	assertEquals("Should be catchable only as UndeclaredThrowableException and its superclasses", 
		    util.catchableSubset(set0), catchable);

	if (DUMP_INTERNALS) {
	    printAllSets();
	}
    }

    public void testSingleInstance1() {
	if (DUMP_INTERNALS) {
	    System.err.println("\n\ntestSingleInstance1()");
	}
	Set expected0 = new ExceptionHashSet(Arrays.asList(new RefType[] {
			       util.UNDECLARED_THROWABLE_EXCEPTION,
	}));
	Set expected1 = new ExceptionHashSet(Arrays.asList(new RefType[] {
			       util.UNSUPPORTED_LOOK_AND_FEEL_EXCEPTION,
	}));
	Set expectedResult = new ExceptionHashSet(Arrays.asList(new RefType[] {
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

	Set catchable = new ExceptionHashSet(expectedResult);
	catchable.add(util.RUNTIME_EXCEPTION);
	catchable.add(util.EXCEPTION);
	catchable.add(util.THROWABLE);
	assertEquals("Should be catchable only as UndeclaredThrowableException "
		     + "UnsupportedLookAndFeelException and superclasses", 
		    util.catchableSubset(set0a), catchable);

	if (DUMP_INTERNALS) {
	    printAllSets();
	}
    }


    public void testAddingSubclasses() {
	if (DUMP_INTERNALS) {
	    System.err.println("\n\ntestAddingSubclasses()");
	}
	Set expected = new ExceptionHashSet();
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

	Set catchable = new ExceptionHashSet(Arrays.asList(new RefType[] {
	    util.INDEX_OUT_OF_BOUNDS_EXCEPTION,
	    util.RUNTIME_EXCEPTION,
	    util.EXCEPTION,
	    util.THROWABLE,
	}));
	assertEquals(util.catchableSubset(set0), catchable);

	catchable.add(util.ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION);
	catchable.add(util.STRING_INDEX_OUT_OF_BOUNDS_EXCEPTION);
	assertEquals(util.catchableSubset(set1), catchable);

	if (DUMP_INTERNALS) {
	    printAllSets();
	}
    }

    public void testAddingSets0() {
	if (DUMP_INTERNALS) {
	    System.err.println("\n\ntestAddingSets0()");
	}
	Set expected = new ExceptionHashSet(Arrays.asList(new RefType[] {
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

    public void testAddingSets1() {
	if (DUMP_INTERNALS) {
	    System.err.println("\n\ntestAddingSets1()");
	}
	Set expected = new ExceptionHashSet(util.VM_ERRORS);
	expected.add(util.UNDECLARED_THROWABLE_EXCEPTION);
	ThrowableSet set0 = add(mgr.VM_ERRORS,
				util.UNDECLARED_THROWABLE_EXCEPTION, expected);
	expected.add(util.UNSUPPORTED_LOOK_AND_FEEL_EXCEPTION);
	set0 = add(set0, util.UNSUPPORTED_LOOK_AND_FEEL_EXCEPTION, expected);
    
	ThrowableSet set1 = mgr.INITIALIZATION_ERRORS;
	expected = new ExceptionHashSet();
	expected.add(AnySubType.v(util.ERROR));
	assertTrue(util.sameMembers(expected, set1));

	expected.add(util.UNDECLARED_THROWABLE_EXCEPTION);
	expected.add(util.UNSUPPORTED_LOOK_AND_FEEL_EXCEPTION);
	ThrowableSet result0 = add(set0, set1, expected);
	ThrowableSet result1 = add(set1, set0, expected);
	assertTrue("Adding sets should be commutative.", result0 == result1);

	Set catchable = new ExceptionHashSet(util.ALL_TEST_ERRORS);
	catchable.add(util.UNDECLARED_THROWABLE_EXCEPTION);
	catchable.add(util.UNSUPPORTED_LOOK_AND_FEEL_EXCEPTION);
	catchable.add(util.RUNTIME_EXCEPTION);// Superclasses of 
	catchable.add(util.EXCEPTION);        // others.
	catchable.add(util.ERROR);
	catchable.add(util.THROWABLE);
	assertEquals(util.catchableSubset(result0), catchable);
	
	if (DUMP_INTERNALS) {
	    printAllSets();
	}
    }

    void printAllSets() {
        for (Iterator i = mgr.getSizeToSets().values().iterator(); i.hasNext(); ) {
            List sizeList = (List) i.next();
            for (Iterator j = sizeList.iterator(); j.hasNext(); ) {
                ThrowableSet s = (ThrowableSet) j.next();
                System.err.println(s.toString());
		System.err.println("\n\tMemoized Adds:");
		for (Iterator k = s.getMemoizedAdds().entrySet().iterator(); 
		     k.hasNext(); ) {
		    Map.Entry entry = (Map.Entry) k.next();
		    System.err.print(' ');
		    if (entry.getKey() instanceof ThrowableSet) {
			System.err.print(((ThrowableSet) entry.getKey()).toBriefString());
		    } else {
			System.err.print(entry.getKey().toString());
		    }
		    System.err.print('=');
		    System.err.print(((ThrowableSet) entry.getValue()).toBriefString());
		    System.err.print('\n');
		}
            }
        }
    }


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
	return suite;
    }
    

    public static Test reflectionSuite() {
	TestSuite suite = new TestSuite(ThrowableSetTest.class);
	return suite;
    }


    public static void main(String arg[]) {
        if (arg.length > 0) {
            jdkLocation = arg[0];
        }
    	junit.textui.TestRunner.run(reflectionSuite());
	System.out.println(ThrowableSet.Manager.v().reportInstrumentation());
    }
}
