package soot.toolkits.exceptions;

import soot.*;
import java.util.*;

/**
 * Class which packages together some objects useful in
 * unit tests of exception handling.
 */
public class ExceptionTestUtility {

    // Individual Throwable types for our tests:
    final RefType THROWABLE;
    final RefType EXCEPTION;
    final RefType RUNTIME_EXCEPTION;
    final RefType ARITHMETIC_EXCEPTION;
    final RefType ARRAY_STORE_EXCEPTION;
    final RefType CLASS_CAST_EXCEPTION ;
    final RefType ILLEGAL_MONITOR_STATE_EXCEPTION;
    final RefType INDEX_OUT_OF_BOUNDS_EXCEPTION;
    final RefType ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION;
    final RefType STRING_INDEX_OUT_OF_BOUNDS_EXCEPTION;
    final RefType NEGATIVE_ARRAY_SIZE_EXCEPTION;
    final RefType NULL_POINTER_EXCEPTION;
    final RefType ERROR;
    final RefType LINKAGE_ERROR;
    final RefType CLASS_CIRCULARITY_ERROR;
    final RefType CLASS_FORMAT_ERROR;
    final RefType UNSUPPORTED_CLASS_VERSION_ERROR;
    final RefType EXCEPTION_IN_INITIALIZER_ERROR;
    final RefType INCOMPATIBLE_CLASS_CHANGE_ERROR;
    final RefType ABSTRACT_METHOD_ERROR;
    final RefType ILLEGAL_ACCESS_ERROR;
    final RefType INSTANTIATION_ERROR;
    final RefType NO_SUCH_FIELD_ERROR;
    final RefType NO_SUCH_METHOD_ERROR;
    final RefType NO_CLASS_DEF_FOUND_ERROR;
    final RefType UNSATISFIED_LINK_ERROR;
    final RefType VERIFY_ERROR;
    final RefType THREAD_DEATH;
    final RefType VIRTUAL_MACHINE_ERROR;
    final RefType INTERNAL_ERROR;
    final RefType OUT_OF_MEMORY_ERROR;
    final RefType STACK_OVERFLOW_ERROR;
    final RefType UNKNOWN_ERROR;
    final RefType AWT_ERROR; 
    final RefType UNDECLARED_THROWABLE_EXCEPTION;
    final RefType UNSUPPORTED_LOOK_AND_FEEL_EXCEPTION;

    // The universe of all Throwable types for our tests:
    final Set ALL_TEST_THROWABLES;

    // Set that matches the representation of all Throwables used
    // internally by ThrowableSet:
    final Set ALL_THROWABLES_REP;

    // Some useful subsets of our Throwable universe:
    final Set VM_ERRORS;
    final Set VM_ERRORS_PLUS_SUPERTYPES;
    final Set VM_AND_RESOLVE_CLASS_ERRORS;
    final Set VM_AND_RESOLVE_CLASS_ERRORS_PLUS_SUPERTYPES;
    final Set VM_AND_RESOLVE_FIELD_ERRORS;
    final Set VM_AND_RESOLVE_FIELD_ERRORS_PLUS_SUPERTYPES;
    final Set VM_AND_RESOLVE_METHOD_ERRORS;
    final Set VM_AND_RESOLVE_METHOD_ERRORS_PLUS_SUPERTYPES;
    final Set ALL_TEST_ERRORS;
    final Set ALL_TEST_ERRORS_PLUS_SUPERTYPES;
    final Set PERENNIAL_THROW_EXCEPTIONS;
    final Set PERENNIAL_THROW_EXCEPTIONS_PLUS_SUPERTYPES;
    final Set THROW_PLUS_INCOMPATIBLE_CLASS_CHANGE;
    final Set THROW_PLUS_INCOMPATIBLE_CLASS_CHANGE_PLUS_SUPERTYPES;
    final Set THROW_PLUS_INCOMPATIBLE_CLASS_CHANGE_PLUS_SUBTYPES;
    final Set THROW_PLUS_INCOMPATIBLE_CLASS_CHANGE_PLUS_SUBTYPES_PLUS_SUPERTYPES;

    // Sets that match the representations of subsets of Errors used
    // internally by ThrowableSet:
    final Set VM_AND_RESOLVE_CLASS_ERRORS_REP;
    final Set VM_AND_RESOLVE_FIELD_ERRORS_REP;
    final Set VM_AND_RESOLVE_METHOD_ERRORS_REP;
    final Set ALL_ERRORS_REP;

    ExceptionTestUtility(String pathToJavaLib) {
	Scene.v().setSootClassPath(pathToJavaLib);

	THROWABLE = Scene.v().getRefType("java.lang.Throwable");

	ERROR = Scene.v().getRefType("java.lang.Error");

	Scene.v().loadClassAndSupport("java.lang.Exception");
	EXCEPTION = Scene.v().getRefType("java.lang.Exception");

	// runtime exceptions.
	RUNTIME_EXCEPTION 
	    = Scene.v().getRefType("java.lang.RuntimeException");

	ARITHMETIC_EXCEPTION
	    = Scene.v().getRefType("java.lang.ArithmeticException");

	ARRAY_STORE_EXCEPTION
	    = Scene.v().getRefType("java.lang.ArrayStoreException");

	CLASS_CAST_EXCEPTION 
	    = Scene.v().getRefType("java.lang.ClassCastException");

	ILLEGAL_MONITOR_STATE_EXCEPTION
	    = Scene.v().getRefType("java.lang.IllegalMonitorStateException");

	INDEX_OUT_OF_BOUNDS_EXCEPTION
	    = Scene.v().getRefType("java.lang.IndexOutOfBoundsException");

	ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION
	    = Scene.v().getRefType("java.lang.ArrayIndexOutOfBoundsException");

	Scene.v().loadClassAndSupport("java.lang.StringIndexOutOfBoundsException");
	STRING_INDEX_OUT_OF_BOUNDS_EXCEPTION
	    = Scene.v().getRefType("java.lang.StringIndexOutOfBoundsException");

	NEGATIVE_ARRAY_SIZE_EXCEPTION
	    = Scene.v().getRefType("java.lang.NegativeArraySizeException");

	NULL_POINTER_EXCEPTION
	    = Scene.v().getRefType("java.lang.NullPointerException");

	// linkage errors.
	LINKAGE_ERROR = Scene.v().getRefType("java.lang.LinkageError");

	CLASS_CIRCULARITY_ERROR
	    = Scene.v().getRefType("java.lang.ClassCircularityError");

	CLASS_FORMAT_ERROR
	    = Scene.v().getRefType("java.lang.ClassFormatError");

	Scene.v().loadClassAndSupport("java.lang.UnsupportedClassVersionError");
	UNSUPPORTED_CLASS_VERSION_ERROR
    	    = Scene.v().getRefType("java.lang.UnsupportedClassVersionError");

	EXCEPTION_IN_INITIALIZER_ERROR
	    = Scene.v().getRefType("java.lang.ExceptionInInitializerError");

	INCOMPATIBLE_CLASS_CHANGE_ERROR
	    = Scene.v().getRefType("java.lang.IncompatibleClassChangeError");

	ABSTRACT_METHOD_ERROR 
	    = Scene.v().getRefType("java.lang.AbstractMethodError");

	ILLEGAL_ACCESS_ERROR
	    = Scene.v().getRefType("java.lang.IllegalAccessError");

	INSTANTIATION_ERROR
	    = Scene.v().getRefType("java.lang.InstantiationError");

	NO_SUCH_FIELD_ERROR
	    = Scene.v().getRefType("java.lang.NoSuchFieldError");

	NO_SUCH_METHOD_ERROR
	    = Scene.v().getRefType("java.lang.NoSuchMethodError");

	NO_CLASS_DEF_FOUND_ERROR
	    = Scene.v().getRefType("java.lang.NoClassDefFoundError");

	UNSATISFIED_LINK_ERROR
	    = Scene.v().getRefType("java.lang.UnsatisfiedLinkError");

	VERIFY_ERROR
	    = Scene.v().getRefType("java.lang.VerifyError");

	// Token non-linkage Error (in the sense that it is not among
	// Errors that the VM might throw itself during linkage---any
	// error could be generated during linking by a static
	// initializer).
	Scene.v().loadClassAndSupport("java.awt.AWTError");
	AWT_ERROR = Scene.v().getRefType("java.awt.AWTError");

	// VM errors:
	INTERNAL_ERROR
	    = Scene.v().getRefType("java.lang.InternalError");

	OUT_OF_MEMORY_ERROR
	    = Scene.v().getRefType("java.lang.OutOfMemoryError");

	STACK_OVERFLOW_ERROR
	    = Scene.v().getRefType("java.lang.StackOverflowError");

	UNKNOWN_ERROR
	    = Scene.v().getRefType("java.lang.UnknownError");

	THREAD_DEATH
	    = Scene.v().getRefType("java.lang.ThreadDeath");

	Scene.v().loadClassAndSupport("java.lang.VirtualMachineError");
	VIRTUAL_MACHINE_ERROR
	    = Scene.v().getRefType("java.lang.VirtualMachineError");

	// Two Throwables that our test statements will never throw (except
	// for invoke statements--in the absence of interprocedural analysis,
	// we have to assume they can throw anything).
	Scene.v().loadClassAndSupport("java.lang.reflect.UndeclaredThrowableException");
	UNDECLARED_THROWABLE_EXCEPTION
	    = Scene.v().getRefType("java.lang.reflect.UndeclaredThrowableException");

	Scene.v().loadClassAndSupport("javax.swing.UnsupportedLookAndFeelException");
	UNSUPPORTED_LOOK_AND_FEEL_EXCEPTION
	    = Scene.v().getRefType("javax.swing.UnsupportedLookAndFeelException");
	
	VM_ERRORS = Collections.unmodifiableSet(
	    new ExceptionHashSet(Arrays.asList(new RefType[] {
		THREAD_DEATH,
		INTERNAL_ERROR,
		OUT_OF_MEMORY_ERROR,
		STACK_OVERFLOW_ERROR,
		UNKNOWN_ERROR,
	    })));

	Set temp = new ExceptionHashSet(VM_ERRORS);
	temp.add(VIRTUAL_MACHINE_ERROR);
	temp.add(ERROR);
	temp.add(THROWABLE);
	VM_ERRORS_PLUS_SUPERTYPES = Collections.unmodifiableSet(temp);

	temp = new ExceptionHashSet(VM_ERRORS);
	temp.add(CLASS_CIRCULARITY_ERROR);
	temp.add(ILLEGAL_ACCESS_ERROR);
	temp.add(INCOMPATIBLE_CLASS_CHANGE_ERROR);
	temp.add(LINKAGE_ERROR);
	temp.add(NO_CLASS_DEF_FOUND_ERROR);
	temp.add(VERIFY_ERROR);
	Set tempForRep = new ExceptionHashSet(temp);
	tempForRep.add(AnySubType.v(CLASS_FORMAT_ERROR));
	VM_AND_RESOLVE_CLASS_ERRORS_REP = Collections.unmodifiableSet(tempForRep);

	temp.add(CLASS_FORMAT_ERROR);
	temp.add(UNSUPPORTED_CLASS_VERSION_ERROR);
	VM_AND_RESOLVE_CLASS_ERRORS = Collections.unmodifiableSet(temp);
	temp.add(VIRTUAL_MACHINE_ERROR);
	temp.add(ERROR);
	temp.add(THROWABLE);
	VM_AND_RESOLVE_CLASS_ERRORS_PLUS_SUPERTYPES = Collections.unmodifiableSet(temp);

	temp = new ExceptionHashSet(VM_AND_RESOLVE_CLASS_ERRORS_REP);
	temp.add(NO_SUCH_FIELD_ERROR);
	VM_AND_RESOLVE_FIELD_ERRORS_REP = Collections.unmodifiableSet(temp);

	temp = new ExceptionHashSet(VM_AND_RESOLVE_CLASS_ERRORS);
	temp.add(NO_SUCH_FIELD_ERROR);
	VM_AND_RESOLVE_FIELD_ERRORS = Collections.unmodifiableSet(temp);
	temp.add(VIRTUAL_MACHINE_ERROR);
	temp.add(ERROR);
	temp.add(THROWABLE);
	VM_AND_RESOLVE_FIELD_ERRORS_PLUS_SUPERTYPES = Collections.unmodifiableSet(temp);
	
	temp = new ExceptionHashSet(VM_AND_RESOLVE_CLASS_ERRORS_REP);
	temp.add(ABSTRACT_METHOD_ERROR);
	temp.add(NO_SUCH_METHOD_ERROR);
	temp.add(UNSATISFIED_LINK_ERROR);
	VM_AND_RESOLVE_METHOD_ERRORS_REP = Collections.unmodifiableSet(temp);

	temp = new ExceptionHashSet(VM_AND_RESOLVE_CLASS_ERRORS);
	temp.add(ABSTRACT_METHOD_ERROR);
	temp.add(NO_SUCH_METHOD_ERROR);
	temp.add(UNSATISFIED_LINK_ERROR);
	VM_AND_RESOLVE_METHOD_ERRORS = Collections.unmodifiableSet(temp);
	temp.add(VIRTUAL_MACHINE_ERROR);
	temp.add(ERROR);
	temp.add(THROWABLE);
	VM_AND_RESOLVE_METHOD_ERRORS_PLUS_SUPERTYPES = Collections.unmodifiableSet(temp);

	temp = new ExceptionHashSet();
	temp.add(AnySubType.v(Scene.v().getRefType("java.lang.Error")));
	ALL_ERRORS_REP = Collections.unmodifiableSet(temp);

	temp = new ExceptionHashSet(VM_AND_RESOLVE_METHOD_ERRORS);
	temp.add(NO_SUCH_FIELD_ERROR);
	temp.add(EXCEPTION_IN_INITIALIZER_ERROR);
	temp.add(INSTANTIATION_ERROR);
	temp.add(AWT_ERROR);
	ALL_TEST_ERRORS = Collections.unmodifiableSet(temp);
	
	temp = new ExceptionHashSet(ALL_TEST_ERRORS);
	temp.add(VIRTUAL_MACHINE_ERROR);
	temp.add(ERROR);
	temp.add(THROWABLE);
	ALL_TEST_ERRORS_PLUS_SUPERTYPES = Collections.unmodifiableSet(temp);

	temp = new ExceptionHashSet(VM_ERRORS);
	temp.add(ILLEGAL_MONITOR_STATE_EXCEPTION);
	temp.add(NULL_POINTER_EXCEPTION);
	PERENNIAL_THROW_EXCEPTIONS = Collections.unmodifiableSet(temp);

	temp = new ExceptionHashSet(VM_ERRORS_PLUS_SUPERTYPES);
	temp.add(ILLEGAL_MONITOR_STATE_EXCEPTION);
	temp.add(NULL_POINTER_EXCEPTION);
	temp.add(RUNTIME_EXCEPTION);
	temp.add(EXCEPTION);
	PERENNIAL_THROW_EXCEPTIONS_PLUS_SUPERTYPES = Collections.unmodifiableSet(temp);

	temp = new ExceptionHashSet(PERENNIAL_THROW_EXCEPTIONS);
	temp.add(INCOMPATIBLE_CLASS_CHANGE_ERROR);
	THROW_PLUS_INCOMPATIBLE_CLASS_CHANGE = Collections.unmodifiableSet(temp);

	temp = new ExceptionHashSet(PERENNIAL_THROW_EXCEPTIONS_PLUS_SUPERTYPES);
	temp.add(INCOMPATIBLE_CLASS_CHANGE_ERROR);
	temp.add(LINKAGE_ERROR);
	THROW_PLUS_INCOMPATIBLE_CLASS_CHANGE_PLUS_SUPERTYPES = Collections.unmodifiableSet(temp);

	temp = new ExceptionHashSet(THROW_PLUS_INCOMPATIBLE_CLASS_CHANGE); 
	temp.add(ABSTRACT_METHOD_ERROR);
	temp.add(ILLEGAL_ACCESS_ERROR);
	temp.add(INSTANTIATION_ERROR);;
	temp.add(NO_SUCH_FIELD_ERROR);
	temp.add(NO_SUCH_METHOD_ERROR);
	THROW_PLUS_INCOMPATIBLE_CLASS_CHANGE_PLUS_SUBTYPES
	    = Collections.unmodifiableSet(temp);

	temp = new ExceptionHashSet(THROW_PLUS_INCOMPATIBLE_CLASS_CHANGE_PLUS_SUPERTYPES);
	temp.add(ABSTRACT_METHOD_ERROR);
	temp.add(ILLEGAL_ACCESS_ERROR);
	temp.add(INSTANTIATION_ERROR);;
	temp.add(NO_SUCH_FIELD_ERROR);
	temp.add(NO_SUCH_METHOD_ERROR);
	THROW_PLUS_INCOMPATIBLE_CLASS_CHANGE_PLUS_SUBTYPES_PLUS_SUPERTYPES
	    = Collections.unmodifiableSet(temp);
	
	temp = new ExceptionHashSet(Arrays.asList(new RefType[] {
	    THROWABLE,
	    EXCEPTION,
	    RUNTIME_EXCEPTION,
	    ARITHMETIC_EXCEPTION,
	    ARRAY_STORE_EXCEPTION,
	    CLASS_CAST_EXCEPTION ,
	    ILLEGAL_MONITOR_STATE_EXCEPTION,
	    INDEX_OUT_OF_BOUNDS_EXCEPTION,
	    ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION,
	    STRING_INDEX_OUT_OF_BOUNDS_EXCEPTION,
	    NEGATIVE_ARRAY_SIZE_EXCEPTION,
	    NULL_POINTER_EXCEPTION,
	    ERROR,
	    LINKAGE_ERROR,
	    CLASS_CIRCULARITY_ERROR,
	    CLASS_FORMAT_ERROR,
	    UNSUPPORTED_CLASS_VERSION_ERROR,
	    EXCEPTION_IN_INITIALIZER_ERROR,
	    INCOMPATIBLE_CLASS_CHANGE_ERROR,
	    ABSTRACT_METHOD_ERROR,
	    ILLEGAL_ACCESS_ERROR,
	    INSTANTIATION_ERROR,
	    NO_SUCH_FIELD_ERROR,
	    NO_SUCH_METHOD_ERROR,
	    NO_CLASS_DEF_FOUND_ERROR,
	    UNSATISFIED_LINK_ERROR,
	    VERIFY_ERROR,
	    THREAD_DEATH,
	    VIRTUAL_MACHINE_ERROR,
	    INTERNAL_ERROR,
	    OUT_OF_MEMORY_ERROR,
	    STACK_OVERFLOW_ERROR,
	    UNKNOWN_ERROR,
	    AWT_ERROR, 
	    UNDECLARED_THROWABLE_EXCEPTION,
	    UNSUPPORTED_LOOK_AND_FEEL_EXCEPTION,
	}));
	ALL_TEST_THROWABLES = Collections.unmodifiableSet(temp);

	temp = new ExceptionHashSet();
	temp.add(AnySubType.v(Scene.v().getRefType("java.lang.Throwable")));
	ALL_THROWABLES_REP = Collections.unmodifiableSet(temp);
    }


    /**
     * Verifies that the argument <code>set</code> is catchable as
     * any of the exceptions in <code>members</code>.
     *
     * @param set <code>ThrowableSet</code> whose membership is
     * being checked.
     *
     * @param members A {@link List} of {@link RefType} objects representing
     * Throwable classes. 
     * 
     */
    public boolean catchableAsAllOf(ThrowableSet set, List members) {
	boolean result = true;
	for (Iterator i = members.iterator(); i.hasNext(); ) {
	    RefType member = (RefType) i.next();
	    result = result && set.catchableAs(member);
	}
	return result;
    }
	     
	
    /**
     * Verifies that the argument <code>set</code> is not catchable as
     * any of the exceptions in <code>members</code>.
     *
     * @param set <code>ThrowableSet</code> whose membership is
     * being checked.
     *
     * @param members A {@link List} of {@link RefType} objects representing
     * Throwable classes. 
     * 
     */
    public boolean catchableAsNoneOf(ThrowableSet set, List members) {
	boolean result = true;
	for (Iterator i = members.iterator(); i.hasNext(); ) {
	    RefType member = (RefType) i.next();
	    result = result && (! set.catchableAs(member));
	}
	return result;
    }
	     
	
    /**
     * Verifies that the argument <code>set</code> is catchable as
     * any of the exceptions in <code>members</code>, but not
     * as any other of the exceptions ALL_TEST_THROWABLES.
     *
     * @param set <code>ThrowableSet</code> whose membership is
     * being checked.
     *
     * @param members A {@link List} of {@link RefType} objects representing
     * Throwable classes. 
     * 
     */
    public boolean catchableOnlyAs(ThrowableSet set, List members) {
	boolean result = true;
	for (Iterator i = members.iterator(); i.hasNext(); ) {
	    RefType member = (RefType) i.next();
	    result = result && (set.catchableAs(member));
	}
	for (Iterator i = ALL_TEST_THROWABLES.iterator(); i.hasNext(); ) {
	    RefType e = (RefType) i.next();
	    if (! members.contains(e)) {
		result = result && (! set.catchableAs(e));
	    }
	}
	return result;
    }
	     

    /**
     * Returns a Set representation of the subset of ALL_TEST_THROWABLES
     * which are catchable by the argument <code>ThrowableSet</code>
     * (for use in assertions about the catchable exceptions.
     *
     * @param thrownSet <code>ThrowableSet</code> representing some
     * set of possible exceptions.
     */
    public Set catchableSubset(ThrowableSet thrownSet)
    {
	Set result = new ExceptionHashSet(ALL_TEST_THROWABLES.size());
	for (Iterator i = ALL_TEST_THROWABLES.iterator(); i.hasNext(); ) {
	    RefType e = (RefType) i.next();
	    if (thrownSet.catchableAs(e)) {
		result.add(e);
	    }
	}
	return result;
    }


    /**
     * Checks that the internal representation of the exceptions
     * in an argument {@link ThrowableSet} matches expectations.
     *
     * @param thrownSet {@link ThrowableSet} whose contents are being
     * checked.
     *
     * @param expectedIncludes contains the collection of {@link
     * RefType} and {@link AnySubType} objects that are expected to be
     * included in <code>thrownSet</code>.
     *
     * @param expectedExcludes contains the collection of {@link
     * RefType} and {@link AnySubType} objects that are expected to be
     * excluded from <code>thrownSet</code>.
     *
     * @return <code>true</code> if <code>thrownSet</code> and
     * <code>expected</code> have the same members.
     */
    public static boolean sameMembers(Set expectedIncluded, Set expectedExcluded, 
				      ThrowableSet thrownSet) {
	if (   (expectedIncluded.size() != thrownSet.typesIncluded().size())
	    || (expectedExcluded.size() != thrownSet.typesExcluded().size())
	    || (! expectedIncluded.containsAll(thrownSet.typesIncluded()))
	    || (! expectedExcluded.containsAll(thrownSet.typesExcluded()))) {
	    System.out.println("\nExpected included:" + expectedIncluded.toString() 
			       + "\nExpected excluded:" + expectedExcluded.toString()
			       + "\nActual:\n" + thrownSet.toString()); 
	    return false;
	} else {
	    return true;
	}
    }


    public static class ExceptionHashSet extends HashSet {
	// The only difference between this and a standard HashSet is that
	// we override the toString() method to make ExceptionHashSets
	// easier to compare when they appear in JUnit assertion failure
	// messages.

	ExceptionHashSet() {
	    super();
	}

	ExceptionHashSet(Collection c) {
	    super(c);
	}

	ExceptionHashSet(int initialCapacity) {
	    super(initialCapacity);
	}

	public String toString() {
	    StringBuffer result = new StringBuffer();
	    Object[] contents = (Object[]) this.toArray();
	    Comparator comparator = new Comparator() {
		public int compare(Object o1, Object o2) {
		    // The order doesn't matter, so long as it is consistent.
		    return o1.toString().compareTo(o2.toString());
		}
	    };
	    Arrays.sort(contents, comparator);
	    result.append("\nExceptionHashSet<");
	    for (int i = 0; i < contents.length; i++) {
		result.append("\n\t");
		result.append(contents[i].toString());
		result.append("<");
		result.append(Integer.toHexString(contents[i].hashCode()));
		result.append(">");
	    }
	    result.append("\n>ExceptionHashSet");
	    return result.toString();
	}
    }
}
