/* Soot - a J*va Optimization Framework
 * Copyright (C) 2003 John Jorgensen
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package soot.toolkits.exceptions;

import soot.*;
import soot.jimple.*;
import soot.util.*;
import java.util.*;

/**
 * <p>A class for representing the set of exceptions that an
 * instruction may throw.</p>
 *
 * <p> <code>ThrowableSet</code> does not implement the 
 * {@link java.util.Set} interface, so perhaps it is misnamed.
 * Instead, it provides only the operations that we require for
 * determining whether a given statement might throw an exception that
 * would be caught by a given handler.</p>
 *
 * <p>There is a limitation on the combinations of operations
 * permitted on a <code>ThrowableSet</code>.  The
 * <code>ThrowableSet</code>s returned by {@link
 * #whichCatchableAs(RefType)} cannot be involved in subsequent
 * <code>add()</code> or <code>whichCatchableAs()</code> operations.
 * That is, given
 *
 * <blockquote>
 * <code>p = s.whichCatchableAs(r)</code>
 * </blockquote>
 *
 * for any <code>ThrowableSet</code> <code>s</code> and 
 * {@link soot.RefType RefType} <code>r</code>, and 
 *
 * <blockquote>
 * <code>t == p.getUncaught()</code> or 
 * <code>t == p.getCaught()</code>
 * </blockquote>
 *
 * then calls to 
 * <code>t.add(r)</code>, <code>t.add(a)</code>, and <code>s.add(t)</code>,
 * will throw an {@link ThrowableSet.AlreadyHasExclusionsException}, for any 
 * <code>RefType</code> <code>r</code>, {@link AnySubType} <code>a</code>, 
 * and <code>ThrowableSet</code> <code>t</code>.</p>
 *
 * <p> Actually the restrictions implemented are not quite so strict
 * (there are some combinations of <code>whichCatchableAs()</code>
 * followed by <code>add()</code> which will not raise an exception),
 * but a more accurate description would require reference to the
 * internals of the current implementation. The restrictions should
 * not be too onerous for <code>ThrowableSet</code>'s anticipated
 * uses: we expect <code>ThrowableSet</code>s to grow by accumulating
 * all the exception types that a given {@link Unit} may throw, then,
 * shrink as the types caught by different exception handlers are
 * removed to yield the sets representing exceptions which escape
 * those handlers.</p>
 * 
 * <p> The <code>ThrowableSet</code> class is intended to be immutable
 * (hence the <code>final</code> modifier on its declaration).  It
 * does not take the step of guaranteeing immutability by cloning the
 * <code>RefLikeType</code> objects it contains, though, because we trust
 * {@link Scene} to enforce the existence of only one 
 * <code>RefLikeType</code> instance with a given name.</p>
 */

public final class ThrowableSet {

    private static final boolean INSTRUMENTING = true;

    /**
     * Singleton class for fields and initializers common to all
     * ThrowableSet objects (i.e., these would be static fields and
     * initializers, in the absence of soot's {@link G} and {@link
     * Singletons} classes).
     */
    public static class Manager {

	/**
	 * Map from {@link Integer}s representing set size to all
	 * <code>ThrowableSet</code>s of that size.
	 */
	private final Map sizeToSets = new HashMap();

	/**
	 * <code>ThrowableSet</code> containing no exception classes.
	 */
	public final ThrowableSet EMPTY;

	/**
	 * <code>ThrowableSet</code> representing all possible
	 * Throwables.
	 */
	final ThrowableSet ALL_THROWABLES;

	/**
	 * <code>ThrowableSet</code> containing all the asynchronous
	 * and virtual machine errors, which may be thrown by any
	 * bytecode instruction at any point in the computation.
	 */
	final ThrowableSet VM_ERRORS;

	/**
	 * <code>ThrowableSet</code> containing all the exceptions
	 * that may be thrown in the course of resolving a reference
	 * to another class, including the process of loading, preparing,
	 * and verifying the referenced class.
	 */
	final ThrowableSet RESOLVE_CLASS_ERRORS;

	/**
	 * <code>ThrowableSet</code> containing all the exceptions
	 * that may be thrown in the course of resolving a reference
	 * to a field.
	 */
	final ThrowableSet RESOLVE_FIELD_ERRORS;

	/**
	 * <code>ThrowableSet</code> containing all the exceptions
	 * that may be thrown in the course of resolving a reference
	 * to a non-static method.
	 */
	final ThrowableSet RESOLVE_METHOD_ERRORS;

	/**
	 * <code>ThrowableSet</code> containing all the exceptions
	 * which may be thrown by instructions that have the potential
	 * to cause a new class to be loaded and initialized (including
	 * UnsatisfiedLinkError, which is raised at runtime rather than
	 * linking type).
	 */
	final ThrowableSet INITIALIZATION_ERRORS;

	final RefType RUNTIME_EXCEPTION;
	final RefType ARITHMETIC_EXCEPTION;
	final RefType ARRAY_STORE_EXCEPTION;
	final RefType CLASS_CAST_EXCEPTION;
	final RefType ILLEGAL_MONITOR_STATE_EXCEPTION;
	final RefType INDEX_OUT_OF_BOUNDS_EXCEPTION;
	final RefType ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION;
	final RefType NEGATIVE_ARRAY_SIZE_EXCEPTION;
	final RefType NULL_POINTER_EXCEPTION;
	final RefType INSTANTIATION_ERROR;

	// counts for instrumenting:
	private int registeredSets = 0;
	private int addsOfRefType = 0;
	private int addsOfAnySubType = 0;
	private int addsOfSet = 0;
	private int addsInclusionFromMap = 0;
	private int addsInclusionFromMemo = 0;
	private int addsInclusionFromSearch = 0;
	private int addsInclusionInterrupted = 0;
	private int addsExclusionWithSearch = 0;
	private int addsExclusionWithoutSearch = 0;
	private int removesOfAnySubType = 0;
	private int removesFromMap = 0;
	private int removesFromMemo = 0;
	private int removesFromSearch = 0;
	private int registrationCalls = 0;
	private int catchableAsQueries = 0;
	private int catchableAsFromMap = 0;
	private int catchableAsFromSearch = 0;
	
	/**
	 * Constructs a <code>ThrowableSet.Manager</code> for inclusion in 
	 * Soot's global variable manager, {@link G}.
	 *
	 * @param g guarantees that the constructor may only be called 
	 * from {@link Singletons}.
	 */
	public Manager( Singletons.Global g ) {
	    // First ensure the Exception classes are represented in Soot.

	    // Runtime errors:
	    RUNTIME_EXCEPTION =
		Scene.v().getRefType("java.lang.RuntimeException");
	    ARITHMETIC_EXCEPTION =
		Scene.v().getRefType("java.lang.ArithmeticException");
	    ARRAY_STORE_EXCEPTION = 
		Scene.v().getRefType("java.lang.ArrayStoreException");
	    CLASS_CAST_EXCEPTION =
		Scene.v().getRefType("java.lang.ClassCastException");
            ILLEGAL_MONITOR_STATE_EXCEPTION = 
                Scene.v().getRefType("java.lang.IllegalMonitorStateException");
	    INDEX_OUT_OF_BOUNDS_EXCEPTION =
		Scene.v().getRefType("java.lang.IndexOutOfBoundsException");
	    ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION =
		Scene.v().getRefType("java.lang.ArrayIndexOutOfBoundsException");
	    NEGATIVE_ARRAY_SIZE_EXCEPTION =
		Scene.v().getRefType("java.lang.NegativeArraySizeException");	    
	    NULL_POINTER_EXCEPTION = 
		Scene.v().getRefType("java.lang.NullPointerException");

	    INSTANTIATION_ERROR =
		Scene.v().getRefType("java.lang.InstantiationError");

	    EMPTY = registerSetIfNew(null, null);

	    Set allThrowablesSet = new HashSet();
	    allThrowablesSet.add(AnySubType.v(Scene.v().getRefType("java.lang.Throwable")));
	    ALL_THROWABLES = registerSetIfNew(allThrowablesSet, null);

	    Set vmErrorSet = new HashSet();
	    vmErrorSet.add(Scene.v().getRefType("java.lang.InternalError"));
	    vmErrorSet.add(Scene.v().getRefType("java.lang.OutOfMemoryError"));
	    vmErrorSet.add(Scene.v().getRefType("java.lang.StackOverflowError"));
	    vmErrorSet.add(Scene.v().getRefType("java.lang.UnknownError"));

	    // The Java library's deprecated Thread.stop(Throwable) method
	    // would actually allow _any_ Throwable to be delivered
	    // asynchronously, not just java.lang.ThreadDeath.
	    vmErrorSet.add(Scene.v().getRefType("java.lang.ThreadDeath"));

	    VM_ERRORS = registerSetIfNew(vmErrorSet, null);

	    Set resolveClassErrorSet = new HashSet();
	    resolveClassErrorSet.add(Scene.v().getRefType("java.lang.ClassCircularityError"));
	    // We add AnySubType(ClassFormatError) so that we can
	    // avoid adding its subclass,
	    // UnsupportedClassVersionError, explicitly.  This is a
	    // hack to allow Soot to analyze older class libraries 
	    // (UnsupportedClassVersionError was added in JDK 1.2).
	    resolveClassErrorSet.add(AnySubType.v(Scene.v().getRefType("java.lang.ClassFormatError")));
	    resolveClassErrorSet.add(Scene.v().getRefType("java.lang.IllegalAccessError"));
	    resolveClassErrorSet.add(Scene.v().getRefType("java.lang.IncompatibleClassChangeError"));
	    resolveClassErrorSet.add(Scene.v().getRefType("java.lang.LinkageError"));
	    resolveClassErrorSet.add(Scene.v().getRefType("java.lang.NoClassDefFoundError"));
	    resolveClassErrorSet.add(Scene.v().getRefType("java.lang.VerifyError"));
	    RESOLVE_CLASS_ERRORS = registerSetIfNew(resolveClassErrorSet, null);

	    Set resolveFieldErrorSet = new HashSet(resolveClassErrorSet);
	    resolveFieldErrorSet.add(Scene.v().getRefType("java.lang.NoSuchFieldError"));
	    RESOLVE_FIELD_ERRORS = registerSetIfNew(resolveFieldErrorSet, null);

	    Set resolveMethodErrorSet = new HashSet(resolveClassErrorSet);
	    resolveMethodErrorSet.add(Scene.v().getRefType("java.lang.AbstractMethodError"));
	    resolveMethodErrorSet.add(Scene.v().getRefType("java.lang.NoSuchMethodError"));
	    resolveMethodErrorSet.add(Scene.v().getRefType("java.lang.UnsatisfiedLinkError"));
	    RESOLVE_METHOD_ERRORS = registerSetIfNew(resolveMethodErrorSet, null);

	    // The static initializers of a newly loaded class might
	    // throw any Error (if they threw an Exception---even a
	    // RuntimeException---it would be replaced by an
	    // ExceptionInInitializerError):
	    //
	    Set initializationErrorSet = new HashSet();
	    initializationErrorSet.add(AnySubType.v(Scene.v().getRefType("java.lang.Error")));
	    INITIALIZATION_ERRORS = registerSetIfNew(initializationErrorSet, null);
	}


	/**
	 * Returns the single instance of <code>ThrowableSet.Manager</code>.
	 *
	 * @return Soot's <code>ThrowableSet.Manager</code>.
	 */
	public static Manager v() { 
	    return G.v().soot_toolkits_exceptions_ThrowableSet_Manager(); 
	}


	/**
	 * <p>Returns a <code>ThrowableSet</code> representing the set of
	 * exceptions included in <code>include</code> minus the set
	 * of exceptions included in <code>exclude</code>. Creates a
	 * new <code>ThrowableSet</code> only if there was not already
	 * one whose contents correspond to <code>include</code> -
	 * <code>exclude</code>.</p>
	 *
	 * @param include  A set of {@link RefLikeType}
	 * objects representing exception types included in the result; may
	 * be <code>null</code> if there are no included types.
	 *
	 * @param exclude  A set of {@link AnySubType}
	 * objects representing exception types excluded from the result; may
	 * be <code>null</code> if there are no excluded types.
	 *
	 * @return a <code>ThrowableSet</code> representing the set of
	 * exceptions corresponding to <code>include</code> -
	 * <code>exclude</code>.
	 */
	private ThrowableSet registerSetIfNew(Set include, Set exclude) {
	    if (INSTRUMENTING) {
		registrationCalls++;
	    }
	    if (include == null) {
		include = Collections.EMPTY_SET;
	    }
	    if (exclude == null) {
		exclude = Collections.EMPTY_SET;
	    }
	    int size = include.size() + exclude.size();
	    Integer sizeKey = new Integer(size);

	    List sizeList = (List) sizeToSets.get(sizeKey);
	    if (sizeList == null) {
		sizeList = new LinkedList();
		sizeToSets.put(sizeKey, sizeList);
	    }
	    for (Iterator i = sizeList.iterator(); i.hasNext() ;) {
		ThrowableSet set = (ThrowableSet) i.next();
		if (set.exceptionsIncluded.equals(include) 
		    && set.exceptionsExcluded.equals(exclude)) {
		    return set;
		}
	    }
	    if (INSTRUMENTING) {
		registeredSets++;
	    }
	    ThrowableSet result = new ThrowableSet(include, exclude);
	    sizeList.add(result);
	    return result;
	}


	/**
	 * Report the counts collected by instrumentation (for now, at
	 * least, there is no need to provide access to the individual
	 * values as numbers).
	 *
	 * @return a string listing the counts.
	 */
	public String reportInstrumentation() {
	    int setCount = 0;
	    for (Iterator it = sizeToSets.values().iterator(); it.hasNext(); ) {
		List sizeList = (List) it.next();
		setCount += sizeList.size();
	    }
	    if (setCount != registeredSets) {
		throw new IllegalStateException("ThrowableSet.reportInstrumentation() assertion failure: registeredSets != list count");
	    }
	    StringBuffer buf = new StringBuffer("registeredSets: ")
		.append(setCount)
		.append("\naddsOfRefType: ")
		.append(addsOfRefType)
		.append("\naddsOfAnySubType: ")
		.append(addsOfAnySubType)
		.append("\naddsOfSet: ")
		.append(addsOfSet)
		.append("\naddsInclusionFromMap: ")
		.append(addsInclusionFromMap)
		.append("\naddsInclusionFromMemo: ")
		.append(addsInclusionFromMemo)
		.append("\naddsInclusionFromSearch: ")
		.append(addsInclusionFromSearch)
		.append("\naddsInclusionInterrupted: ")
		.append(addsInclusionInterrupted)
		.append("\naddsExclusionWithoutSearch: ")
		.append(addsExclusionWithoutSearch)
		.append("\naddsExclusionWithSearch: ")
		.append(addsExclusionWithSearch)
		.append("\nremovesOfAnySubType: ")
		.append(removesOfAnySubType)
		.append("\nremovesFromMap: ")
		.append(removesFromMap)
		.append("\nremovesFromMemo: ")
		.append(removesFromMemo)
		.append("\nremovesFromSearch: ")
		.append(removesFromSearch)
		.append("\nregistrationCalls: ")
		.append(registrationCalls)
		.append("\ncatchableAsQueries: ")
		.append(catchableAsQueries)
		.append("\ncatchableAsFromMap: ")
		.append(catchableAsFromMap)
		.append("\ncatchableAsFromSearch: ")
		.append(catchableAsFromSearch)
		.append('\n');
	    return buf.toString();
	}

	/**
	 * A package-private method to provide unit tests with access
	 * to the collection of ThrowableSets.   
	 */
	Map getSizeToSets() {
	    return Manager.v().sizeToSets;
	}
    }


    public static class AlreadyHasExclusionsException extends IllegalStateException {
	public AlreadyHasExclusionsException(String s) {
	    super(s);
	}
    }


    /**
     * Set of exception types included within the set.
     */
    private final Set exceptionsIncluded;

    /**
     * Set of exception types which, though members of
     * exceptionsIncluded, are to be excluded from the types
     * represented by this <code>ThrowableSet</code>.  To simplify
     * the implementation, once a <code>ThrowableSet</code> has
     * any excluded types, the various <code>add()</code> methods of
     * this class must bar additions of subtypes of those
     * excluded types.
     */
    private final Set exceptionsExcluded;

    /**
     * A map from 
     * ({@link RefLikeType} \\union <code>ThrowableSet</code>) 
     * to <code>ThrowableSet</code>.  If the mapping (k,v) is in
     * <code>memoizedAdds</code> and k is a
     * <code>ThrowableSet</code>, then v is the set that
     * results from adding all elements in k to <code>this</code>.  If
     * (k,v) is in <code>memoizedAdds</code> and k is a
     * {@link RefLikeType}, then v is the set that results from adding
     * k to <code>this</code>.
     */
    private Map memoizedAdds;

    private ThrowableSet getMemoizedAdds(Object key) {
	if (memoizedAdds == null) {
	    memoizedAdds = new HashMap();
	}
	return (ThrowableSet) memoizedAdds.get(key);
    }


    /**
     * Constructs a <code>ThrowableSet</code> which contains the
     * exception types represented in <code>include</code>, except for
     * those which are also in <code>exclude</code>. The constructor
     * is private to ensure that the only way to get a new
     * <code>ThrowableSet</code> is by adding elements to or removing
     * them from an existing set.
     *
     * @param include The set of {@link RefType} and {@link AnySubType} 
     *                objects representing the types to be included in the set.
     * @param exclude The set of {@link AnySubType} 
     *                objects representing the types to be excluded 
     *                from the set.  
     */
    private ThrowableSet(Set include, Set exclude) {
	exceptionsIncluded = Collections.unmodifiableSet(include);
	exceptionsExcluded = Collections.unmodifiableSet(exclude);
	// We don't need to clone include and exclude to guarantee
	// immutability since ThrowableSet(Set,Set) is private to this
	// class, where it is only called (via
	// Manager.v().registerSetIfNew()) with arguments which the
	// callers do not subsequently modify.
    }


    /**
     * Returns a <code>ThrowableSet</code> which contains
     * <code>e</code> in addition to the exceptions in
     * this <code>ThrowableSet</code>. 
     *
     * <p>Add <code>e</code> as a {@link RefType} when
     * you know that the run-time class of the exception you are representing is
     * necessarily <code>e</code> and cannot be a subclass of
     * <code>e</code>.  
     *
     * <p>For example, if you were 
     * recording the type of the exception thrown by
     *
     * <pre>
     * throw new IOException("Permission denied");
     * </pre>
     *
     * you would call
     *
     * <pre>
     * <code>add(Scene.v().getRefType("java.lang.Exception.IOException"))</code>
     * </pre>
     *
     * since the class of the exception is necessarily
     * <code>IOException</code>.
     *
     * @param e	the exception class
     *
     * @return a set containing <code>e</code> as well as the
     * exceptions in this set.
     *
     * @throws {@link ThrowableSet.IllegalStateException} if this
     * <code>ThrowableSet</code> is the result of a {@link
     * #whichCatchableAs(RefType)} operation and, thus, unable to
     * represent the addition of <code>e</code>.
     */
    public ThrowableSet add(RefType e) 
      throws ThrowableSet.AlreadyHasExclusionsException {
	if (INSTRUMENTING) {
	    Manager.v().addsOfRefType++;
	}
	if (this.exceptionsIncluded.contains(e)) {
	    if (INSTRUMENTING) {
		Manager.v().addsInclusionFromMap++;
		Manager.v().addsExclusionWithoutSearch++;
	    }
	    return this; 
	} else {
	    ThrowableSet result = getMemoizedAdds(e);
	    if (result != null) {
		if (INSTRUMENTING) {
		    Manager.v().addsInclusionFromMemo++;
		    Manager.v().addsExclusionWithoutSearch++;
		}
		return result;
	    } else {
		if (INSTRUMENTING) {
		    Manager.v().addsInclusionFromSearch++;
		    if (exceptionsExcluded.size() != 0) {
			Manager.v().addsExclusionWithSearch++;
		    } else {
			Manager.v().addsExclusionWithoutSearch++;
		    }
		}
		FastHierarchy hierarchy = Scene.v().getOrMakeFastHierarchy();

		for (Iterator i = exceptionsExcluded.iterator(); i.hasNext(); ) {
		    RefType exclusionBase = ((AnySubType) i.next()).getBase();
		    if (hierarchy.canStoreType(e, exclusionBase)) {
			throw new AlreadyHasExclusionsException(
			    "ThrowableSet.add(RefType): adding" + e.toString() 
			    + " to the set [ " + this.toString()
			    + "] where " + exclusionBase.toString() 
			    + " is excluded.");
		    }
		}

		for (Iterator i = exceptionsIncluded.iterator(); i.hasNext() ; ) {
		    RefLikeType incumbent = (RefLikeType) i.next();
		    if (incumbent instanceof AnySubType) {
			// Need to use incumbent.getBase() because
			// hierarchy.canStoreType() assumes that parent
			// is not an AnySubType.
			RefType incumbentBase = ((AnySubType) incumbent).getBase();
			if (hierarchy.canStoreType(e, incumbentBase)) {
			    memoizedAdds.put(e, this);
			    return this;
			}
		    } else if (! (incumbent instanceof RefType)) {
			// assertion failure.
			throw new IllegalStateException("ThrowableSet.add(RefType): Set element " +
							incumbent.toString() +
							" is neither a RefType nor an AnySubType.");
		    }
		}
		Set  resultSet = new HashSet(this.exceptionsIncluded);
		resultSet.add(e);
		result = Manager.v().registerSetIfNew(resultSet, 
						      this.exceptionsExcluded);
		memoizedAdds.put(e, result);
		return result;
	    }
	}
    }


    /**
     * Returns a <code>ThrowableSet</code> which contains
     * <code>e</code> and all of its subclasses as well as the
     * exceptions in this set.
     *
     * <p><code>e</code> should be an instance of {@link AnySubType} 
     * if you know that the
     * compile-time type of the exception you are representing is
     * <code>e</code>, but the exception may be instantiated at run-time
     * by a subclass of
     * <code>e</code>.  
     *
     * <p>For example, if you were recording the type of
     * the exception thrown by
     *
     * <pre>
     * catch (IOException e) {
     *    throw e;
     * }
     * </pre>
     *
     * you would call 
     *
     * <pre>
     * <code>add(AnySubtype.v(Scene.v().getRefType("java.lang.Exception.IOException")))</code>
     * </pre>
     *
     * since the handler might rethrow any subclass of
     * <code>IOException</code>.  
     *
     * @param e represents a subtree of the exception class hierarchy
     * to add to this set.
     *
     * @return a set containing <code>e</code> and all its subclasses,
     * as well as the exceptions represented by this set.
     *
     * @throws ThrowableSet.AlreadyHasExclusionsException if this
     * <code>ThrowableSet</code> is the result of a {@link
     * #whichCatchableAs(RefType)} operation and, thus, unable to
     * represent the addition of <code>e</code>.
     */
    public ThrowableSet add(AnySubType e) 
      throws ThrowableSet.AlreadyHasExclusionsException {
	if (INSTRUMENTING) {
	    Manager.v().addsOfAnySubType++;
	}

	ThrowableSet result = getMemoizedAdds(e);
	if (result != null) {
	    if (INSTRUMENTING) {
		Manager.v().addsInclusionFromMemo++;
		Manager.v().addsExclusionWithoutSearch++;
	    }
	    return result;
	} else {
	    FastHierarchy hierarchy = Scene.v().getOrMakeFastHierarchy();
	    RefType newBase = e.getBase(); 

	    if (INSTRUMENTING) {
		if (exceptionsExcluded.size() != 0) {
		    Manager.v().addsExclusionWithSearch++;
		} else {
		    Manager.v().addsExclusionWithoutSearch++;
		}
	    }
	    for (Iterator i = exceptionsExcluded.iterator(); i.hasNext(); ) {
		RefType exclusionBase = ((AnySubType) i.next()).getBase();
		if (hierarchy.canStoreType(newBase, exclusionBase)
		    || hierarchy.canStoreType(exclusionBase, newBase)) {
		    if (INSTRUMENTING) {
			// To ensure that the subcategories total properly:
			Manager.v().addsInclusionInterrupted++;
		    }
		    throw new AlreadyHasExclusionsException(
			"ThrowableSet.add(" + e.toString() 
			+ ") to the set [ " + this.toString()
			+ "] where " + exclusionBase.toString() 
			+ " is excluded.");
		}
	    }

	    if (this.exceptionsIncluded.contains(e)) {
		if (INSTRUMENTING) {
		    Manager.v().addsInclusionFromMap++;
		}
		return this; 

	    } else {
		if (INSTRUMENTING) {
		    Manager.v().addsInclusionFromSearch++;
		}

		int changes = 0;
		boolean addNewException = true;
		Set  resultSet = new HashSet();

		for (Iterator i = this.exceptionsIncluded.iterator(); i.hasNext() ; ) {
		    RefLikeType incumbent = (RefLikeType) i.next();
		    if (incumbent instanceof RefType) {
			if (hierarchy.canStoreType(incumbent, newBase)) {
			    // Omit incumbent from result.
			    changes++;
			} else {
			    resultSet.add(incumbent);
			}
		    } else if (incumbent instanceof AnySubType) {
			RefType incumbentBase = ((AnySubType) incumbent).getBase();
			// We have to use the base types in these hierarchy calls
			// because we want to know if _all_ possible
			// types represented by e can be represented by
			// the incumbent, or vice versa.
			if (hierarchy.canStoreType(newBase, incumbentBase)) {
			    addNewException = false;
			    resultSet.add(incumbent);
			} else if (hierarchy.canStoreType(incumbentBase, newBase)) {
			    // Omit incumbent from result;
			    changes++;
			} else {
			    resultSet.add(incumbent);
			}
		    } else { // assertion failure.
			throw new IllegalStateException("ThrowableSet.add(AnySubType): Set element " +
							incumbent.toString() + 
							" is neither a RefType nor an AnySubType.");
		    }
		}
		if (addNewException) {
		    resultSet.add(e);
		    changes++;
		}
		if (changes > 0) {
		    result = Manager.v().registerSetIfNew(resultSet, 
							  this.exceptionsExcluded);
		} else {
		    result = this;
		}
		memoizedAdds.put(e, result);
		return result;
	    }
	}
    }


    /**
     * Returns a <code>ThrowableSet</code> which contains
     * all the exceptions in <code>s</code> in addition to those in
     * this <code>ThrowableSet</code>.
     *
     * @param s	set of exceptions to add to this set.
     *
     * @return the union of this set with <code>s</code>
     *
     * @throws ThrowableSet.AlreadyHasExclusionsException if this 
     * <code>ThrowableSet</code> or <code>s</code> is the
     * result of a {@link #whichCatchableAs(RefType)} operation, so that
     * it is not possible to represent the addition of <code>s</code> to
     * this <code>ThrowableSet</code>.
     */
    public ThrowableSet add(ThrowableSet s)
      throws ThrowableSet.AlreadyHasExclusionsException {
	if (INSTRUMENTING) {
	    Manager.v().addsOfSet++;
	}
	if (exceptionsExcluded.size() > 0 || s.exceptionsExcluded.size() > 0) {
	    throw new AlreadyHasExclusionsException("ThrowableSet.Add(ThrowableSet): attempt to add to [" + this.toString() + "] after removals recorded.");
	}
	ThrowableSet result = getMemoizedAdds(s);
	if (result == null) {
	    if (INSTRUMENTING) {
		Manager.v().addsInclusionFromSearch++;
		Manager.v().addsExclusionWithoutSearch++;
	    }
	    result = this.add(s.exceptionsIncluded);
	    memoizedAdds.put(s, result);
	} else if (INSTRUMENTING) {
	    Manager.v().addsInclusionFromMemo++;
	    Manager.v().addsExclusionWithoutSearch++;
	}
	return result;
    }


    /**
     * Returns a <code>ThrowableSet</code> which contains all
     * the exceptions in <code>addedExceptions</code> in addition to those
     * in this <code>ThrowableSet</code>. 
     *
     * @param addedExceptions a set of {@link RefLikeType} and 
     * {@link AnySubType} objects to be added to the types included in this
     * <code>ThrowableSet</code>.
     *
     * @return a set containing all the <code>addedExceptions</code> as well
     * as the exceptions in this set.
     */
    private ThrowableSet add(Set addedExceptions) {
	Set resultSet = new HashSet(this.exceptionsIncluded);
	int changes = 0;
	FastHierarchy hierarchy = Scene.v().getOrMakeFastHierarchy();

	// This algorithm is O(n m), where n and m are the sizes of the
	// two sets, so hope that the sets are small.

	for (Iterator i = addedExceptions.iterator(); i.hasNext(); ) {
	    RefLikeType newType = (RefLikeType) i.next();
	    if (! resultSet.contains(newType)) {
		boolean addNewType = true;
		if (newType instanceof RefType) {
		    for (Iterator j = resultSet.iterator(); j.hasNext(); ) {
			RefLikeType incumbentType = (RefLikeType) j.next();
			if (incumbentType instanceof RefType) {
			    if (newType == incumbentType) {
				// assertion failure.
				throw new IllegalStateException("ThrowableSet.add(Set): resultSet.contains() failed to screen duplicate RefType "
								+ newType);
			    }
			} else if (incumbentType instanceof AnySubType) {
			    RefType incumbentBase = ((AnySubType) incumbentType).getBase();
			    if (hierarchy.canStoreType(newType, incumbentBase)) {
				// No need to add this class.
				addNewType = false;
			    }
			} else { // assertion failure.
			    throw new IllegalStateException("ThrowableSet.add(Set): incumbent Set element " 
							    + incumbentType 
							    + " is neither a RefType nor an AnySubType.");
			}
		    }
		} else if (newType instanceof AnySubType) {
		    RefType newBase = ((AnySubType) newType).getBase();
		    for (Iterator j = resultSet.iterator(); j.hasNext(); ) {
			RefLikeType incumbentType = (RefLikeType) j.next();
			if (incumbentType instanceof RefType) {
			    RefType incumbentBase = (RefType) incumbentType;
			    if (hierarchy.canStoreType(incumbentBase, newBase)) {
				j.remove();
				changes++;
			    }
			} else if (incumbentType instanceof AnySubType) {
			    RefType incumbentBase = ((AnySubType) incumbentType).getBase();
			    if (newBase == incumbentBase) {
				// assertion failure.
				throw new IllegalStateException("ThrowableSet.add(Set): resultSet.contains() failed to screen duplicate AnySubType "
							   + newBase);
			    } else if (hierarchy.canStoreType(incumbentBase, newBase)) {
				j.remove();
				changes++;
			    } else if (hierarchy.canStoreType(newBase, incumbentBase)) {
				// No need to add this class.
				addNewType = false;
			    }
			} else { // assertion failure.
			    throw new IllegalStateException("ThrowableSet.add(Set): old Set element "
						       + incumbentType
						       + " is neither a RefType nor an AnySubType.");
			}
		    }
		} else { // assertion failure.
		    throw new IllegalArgumentException("ThrowableSet.add(Set): new Set element " 
						       + newType
						       + " is neither a RefType nor an AnySubType.");
		}
		if (addNewType) {
		    changes++;
		    resultSet.add(newType);
		}
	    }
	}
			    
	ThrowableSet result = null;
	if (changes > 0) {
	    result = Manager.v().registerSetIfNew(resultSet, 
						  this.exceptionsExcluded);
	} else {
	    result = this;
	}
	return result;
    }


    /**
     * Indicates whether this ThrowableSet includes some 
     * exception that might be caught by a handler argument of the
     * type <code>catcher</code>.  
     *
     * @param catcher type of the handler parameter to be tested.
     *
     * @return <code>true</code> if this set contains an exception type
     *                           that might be caught by <code>catcher</code>, 
     *                           false if it does not.
     */
    public boolean catchableAs(RefType catcher) {
	if (INSTRUMENTING) {
	    Manager.v().catchableAsQueries++;
	}

	FastHierarchy h = Scene.v().getOrMakeFastHierarchy();

	if (exceptionsExcluded.size() > 0) {
	    if (INSTRUMENTING) {
		Manager.v().catchableAsFromSearch++;
	    }
	    for (Iterator i = exceptionsExcluded.iterator(); i.hasNext(); ) {
		AnySubType exclusion = (AnySubType) i.next();
		if (h.canStoreType(catcher, exclusion.getBase())) {
		    return false;
		}
	    }
	}

	if (exceptionsIncluded.contains(catcher)) {
	    if (INSTRUMENTING) {
		if (exceptionsExcluded.size() == 0) {
		    Manager.v().catchableAsFromMap++;
		} else {
		    Manager.v().catchableAsFromSearch++;
		}
	    }
	    return true;
	} else {
	    if (INSTRUMENTING) {
		if (exceptionsExcluded.size() == 0) {
		    Manager.v().catchableAsFromSearch++;
		}
	    }
	    for (Iterator i = exceptionsIncluded.iterator(); i.hasNext(); ) {
		RefLikeType thrownType = (RefLikeType) i.next();
		if (thrownType instanceof RefType) {
		    if (thrownType == catcher) {
			// assertion failure.
			throw new IllegalStateException("ThrowableSet.catchableAs(RefType): exceptions.contains() failed to match contained RefType "
							   + catcher);
		    } else if (h.canStoreType(thrownType, catcher)) {
			return true;
		    }
		} else {
		    RefType thrownBase = ((AnySubType) thrownType).getBase();
		    // At runtime, thrownType might be instantiated by any
		    // of thrownBase's subtypes, so:
		    if (h.canStoreType(thrownBase, catcher)
			|| h.canStoreType(catcher, thrownBase)) {
			return true;
		    }
		}
	    }
	    return false;
	}
    }


    /**
     * Partitions the exceptions in this <code>ThrowableSet</code>
     * into those which would be caught by a handler with the passed
     * <code>catch</code> parameter type and those which would not.
     *
     * @param catcher type of the handler parameter to be tested.
     *
     * @return a pair of <code>ThrowableSet</code>s, one containing the
     *         types in this <code>ThrowableSet</code> which would be
     *         be caught as <code>catcher</code> and the other containing
     *         the types in this <code>ThrowableSet</code> which would
     *         not be caught as <code>catcher</code>.
     */
    public Pair whichCatchableAs(RefType catcher) {
	if (INSTRUMENTING) {
	    Manager.v().removesOfAnySubType++;
	}

	FastHierarchy h = Scene.v().getOrMakeFastHierarchy();
	Set caughtIncluded = null;
	Set caughtExcluded = null;
	Set uncaughtIncluded = null;
	Set uncaughtExcluded = null;

	if (INSTRUMENTING) {
	    Manager.v().removesFromSearch++;
	}

	for (Iterator i = exceptionsExcluded.iterator(); i.hasNext(); ) {
	    AnySubType exclusion = (AnySubType) i.next();
	    RefType exclusionBase = exclusion.getBase();
	    if (h.canStoreType(catcher, exclusionBase)) {
		// Because the add() operations ban additions to sets
		// with exclusions, we can be sure no types in this are
		// caught by catcher.
		return new Pair(ThrowableSet.Manager.v().EMPTY, this);
	    } else if (h.canStoreType(exclusionBase, catcher)) {
		// exclusion wouldn't be in exceptionsExcluded if one
		// of its supertypes were not in exceptionsIncluded,
		// so we know the next loop will add either that supertype 
		// or catcher to caughtIncluded.  Thus:
		caughtExcluded = addExceptionToSet(exclusion, caughtExcluded);
	    } else {
		uncaughtExcluded = addExceptionToSet(exclusion, uncaughtExcluded);
	    }
	}

	for (Iterator i = exceptionsIncluded.iterator(); i.hasNext(); ) {
	    RefLikeType inclusion = (RefLikeType) i.next();
	    if (inclusion instanceof RefType) {
		if (h.canStoreType(inclusion, catcher)) {
		    caughtIncluded = addExceptionToSet(inclusion, caughtIncluded);
		} else {
		    uncaughtIncluded = addExceptionToSet(inclusion, uncaughtIncluded);
		}
	    } else {
		RefType base = ((AnySubType) inclusion).getBase();
		if (h.canStoreType(base, catcher)) {
		    // All subtypes of base will be caught.  Any exclusions
		    // will already have been copied to caughtExcluded by
		    // the preceding loop.
		    caughtIncluded = 
			addExceptionToSet(inclusion, caughtIncluded);
		} else if (h.canStoreType(catcher, base)) {
		    // Some subtypes of base will be caught, and
		    // we know that not all of those catchable subtypes
		    // are among exceptionsExcluded, since in that case we
		    // would already have returned from within the 
		    // preceding loop.  So, remove AnySubType(catcher)
		    // from the uncaught types.
		    uncaughtIncluded 
			= addExceptionToSet(inclusion, uncaughtIncluded);
		    uncaughtExcluded 
			= addExceptionToSet(AnySubType.v(catcher), uncaughtExcluded);
		    caughtIncluded 
			= addExceptionToSet(AnySubType.v(catcher), caughtIncluded);
		    // Any already excluded subtypes of inclusion
		    // which are subtypes of catcher will have been
		    // added to caughtExcluded by the previous loop.
		} else {
		    uncaughtIncluded 
			= addExceptionToSet(inclusion, uncaughtIncluded);
		}
	    }
	}
	ThrowableSet caughtSet 
	    = Manager.v().registerSetIfNew(caughtIncluded, caughtExcluded);
	ThrowableSet uncaughtSet 
	    = Manager.v().registerSetIfNew(uncaughtIncluded, uncaughtExcluded);
	return new Pair(caughtSet, uncaughtSet);
    }


    /**
     * The return type for {@link ThrowableSet#whichCatchableAs(RefType)},
     * consisting of a pair of ThrowableSets.
     */
    public static class Pair {
	private ThrowableSet caught;
	private ThrowableSet uncaught;
	/** 
	 * Constructs a <code>ThrowableSet.Pair</code>.
	 *
	 * @param caught The set of exceptions to be returned when 
	 *               {@link #getCaught()} is called on the constructed
	 *               <code>ThrowableSet.Pair</code>.
	 *
	 * @param uncaught The set of exceptions to be returned when
	 *                 {@link #getUncaught()} is called on the 
	 *                 constructed <code>ThrowableSet.Pair</code>.
	 */
	protected Pair(ThrowableSet caught, ThrowableSet uncaught) {
	    this.caught = caught;
	    this.uncaught = uncaught;
	}

	/**
	 * @return the set of caught exceptions.
	 */
	public ThrowableSet getCaught() {
	    return caught;
	}

	/**
	 * @return the set of uncaught exceptions.
	 */
	public ThrowableSet getUncaught() {
	    return uncaught;
	}

	/**
	 * Indicates whether two {@link Object}s are
	 * <code>ThrowableSet.Pair</code>s representing the same set of
	 * caught and uncaught exception types.
	 *
	 * @param o the <code>Object</code> to compare to this
	 * <code>ThrowableSet.Pair</code>.
	 *
	 * @return <code>true</code> if <code>o</code> is a
	 * <code>ThrowableSet.Pair</code> representing the same set of
	 * caught and uncaught types as this
	 * <code>ThrowableSet.Pair</code>.
	 */
	public boolean equals(Object o) {
	    if (o == this) {
		return true;
	    }
	    if (! (o instanceof Pair)) {
		return false;
	    }
	    Pair tsp = (Pair) o;
	    if (   this.caught.equals(tsp.caught)
		&& this.uncaught.equals(tsp.uncaught)) {
		return true;
	    }
	    return false;
	}


	public int hashCode() {
	    int result = 31;
	    result = 37 * result + caught.hashCode();
	    result = 37 * result + uncaught.hashCode();
	    return result;
	}
    }


    /**
     * Utility method for building sets of exceptional types for a
     * {@link Pair}.
     *
     * @param e The exceptional type to add to the set.
     *
     * @param set The <code>Set</code> to which to add the types, or 
     *            <code>null</code> if no <code>Set</code> has yet been
     *            allocated.
     *
     * @return A <code>Set</code> containing the elements in <code>set</code>
     *         plus <code>e</code>.
     */
    private Set addExceptionToSet(RefLikeType e, Set set) {
	if (set == null) {
	    set = new HashSet();
	}
	set.add(e);
	return set;
    }


    /**
     * Returns a string representation of this <code>ThrowableSet</code>.
     */
    public String toString() {
	StringBuffer buffer = new StringBuffer(this.toBriefString());
	buffer.append(":\n  ");
	for (Iterator i = exceptionsIncluded.iterator(); i.hasNext(); ) {
	    buffer.append('+');
	    Object o = i.next();
	    buffer.append(o == null ? "null" : o.toString());
	    // buffer.append(i.next().toString());
	}
	for (Iterator i = exceptionsExcluded.iterator(); i.hasNext(); ) {
	    buffer.append('-');
	    buffer.append(i.next().toString());
	}
	return buffer.toString();
    }


    /**
     * Returns a cryptic identifier for this <code>ThrowableSet</code>,
     * used to identify a set when it appears in a collection.
     */
    public String toBriefString() {
	return super.toString();
    }


    /**
     * Returns an {@link Iterator} over a {@link Collection} of
     * Throwable types which iterates over its elements in a
     * consistent order (maintaining an ordering that is consistent
     * across different runs makes it easier to compare sets generated
     * by different implementations of the CFG classes).
     *
     * @param coll The collection to iterate over.
     *
     * @return An iterator which presents the elements of <code>coll</code> 
     * in order.
     */
    private static Iterator sortedThrowableIterator(Collection coll) {
	if (coll.size() <= 1) {
	    return coll.iterator();
	} else {
	    Object array[] = coll.toArray();
	    Arrays.sort(array, new ThrowableComparator());
	    return Arrays.asList(array).iterator();
	}
    }


    /**
     * Comparator used to implement sortedThrowableIterator().
     * 
     */
    private static class ThrowableComparator implements java.util.Comparator {

	private static RefType baseType(Object o) {
	    if (o instanceof AnySubType) {
		return ((AnySubType) o).getBase();
	    } else {
		return (RefType) o; // ClassCastException if o is not a RefType.
	    }
	}

	public int compare(Object o1, Object o2) {
	    RefType t1 = baseType(o1);
	    RefType t2 = baseType(o2);
	    if (t1.equals(t2)) {
		// There should never be both AnySubType(t) and
		// t in a ThrowableSet, but if it happens, put 
		// AnySubType(t) first:
		if (o1 instanceof AnySubType) {
		    if (o2 instanceof AnySubType) {
			return 0;
		    } else {
			return -1;
		    }
		} else if (o2 instanceof AnySubType) {
		    return 1;
		} else {
		    return 0;
		}
	    } else {
		return t1.toString().compareTo(t2.toString());
	    }
	}

	public boolean equal(Object o1, Object o2) {
	    return (o1.equals(o2));
	}
    }


    /**
     * <p>Produce an abbreviated representation of this
     * <code>ThrowableSet</code>, suitable for human consumption.  The
     * abbreviations include:</p>
     * 
     * <ul> 
     *
     * <li>The strings &ldquo;<code>java.lang.</code>&rdquo; is
     * stripped from the beginning of exception names.</li>
     *
     * <li>The string &ldquo;<code>Exception</code>&rdquo; is stripped from
     * the ends of exception names.</li>
     *
     * <li>Instances of <code>AnySubType</code> are indicated by surrounding
     * the base type name with parentheses, rather than with the string 
     * &ldquo;<code>Any_subtype_of_</code>&rdquo;</li>
     *
     * <li>If this <code>ThrowableSet</code> includes all the elements
     * of {@link ThrowableSet.Manager#VM_ERRORS VM_ERRORS}, they are
     * abbreviated as &ldquo;<code>vmErrors</code>&rdquo; rather than
     * listed individually.</li>
     * 
     * @return An abbreviated representation of the contents of this set.
     */
    public String toAbbreviatedString() {
	return toAbbreviatedString(exceptionsIncluded, '+') 
	    + toAbbreviatedString(exceptionsExcluded, '-');
    }


    /**
     * <p>Utility method which prints the abbreviations of the
     * elements in a passed {@link Set} of exception types.</p>
     * 
     * @param s The exceptions to print.
     *
     * @param connector The character to insert between exceptions.
     *
     * @return An abbreviated representation of the exceptions.
     */
    private String toAbbreviatedString(Set s, char connector) {
	final String JAVA_LANG = "java.lang.";
	final int JAVA_LANG_LENGTH = JAVA_LANG.length();
	final String EXCEPTION = "Exception";
	final  int EXCEPTION_LENGTH = EXCEPTION.length();

	Collection vmErrorThrowables = ThrowableSet.Manager.v().VM_ERRORS.exceptionsIncluded;
	boolean containsAllVmErrors = s.containsAll(vmErrorThrowables);
	StringBuffer buf = new StringBuffer();

	if (containsAllVmErrors) {
	    buf.append(connector);
	    buf.append("vmErrors");
	}

	for (Iterator it = sortedThrowableIterator(s); it.hasNext(); ) {
	    RefLikeType reflikeType = (RefLikeType) it.next();
	    RefType baseType = null;
	    if (reflikeType instanceof RefType) {
		baseType = (RefType)reflikeType;
		if (vmErrorThrowables.contains(baseType) && containsAllVmErrors) {
		    continue;		// Already accounted for vmErrors.
		} else {
		    buf.append(connector);
		}
	    } else if (reflikeType instanceof AnySubType) {
		buf.append(connector);
		buf.append('(');
		baseType = ((AnySubType)reflikeType).getBase();
	    }
	    String typeName = baseType.toString();
	    if (typeName.startsWith(JAVA_LANG)) {
		typeName = typeName.substring(JAVA_LANG_LENGTH);
	    }
	    if (typeName.length() > EXCEPTION_LENGTH && 
		typeName.endsWith(EXCEPTION)) {
		typeName = typeName.substring(0, typeName.length()-EXCEPTION_LENGTH);
	    }
	    buf.append(typeName);
	    if (reflikeType instanceof AnySubType) {
		buf.append(')');
	    }
	}
	return buf.toString();
    }


    /** 
     * A package-private method to provide unit tests with access to
     * the {@link RefLikeType} objects which represent the
     * <code>Throwable</code> types included in this set.
     *
     * @return an unmodifiable collection view of the
     * <code>Throwable</code> types in this set.
     */
    Collection typesIncluded() {
        return new AbstractCollection() {

	    public Iterator iterator() {
		return new Iterator() {
		    private Iterator i = exceptionsIncluded.iterator();

		    public boolean hasNext() {
			return i.hasNext();
		    }

		    public Object next() {
			return i.next();
		    }

		    public void remove() {
			throw new UnsupportedOperationException();
		    }
		};
	    }

	    public int size() {
		return exceptionsIncluded.size();
	    }
	};
    }


    /** 
     * A package-private method to provide unit tests with access to
     * the {@link RefLikeType} objects which represent the
     * <code>Throwable</code> types excluded from this set.
     *
     * @return an unmodifiable collection view of the
     * <code>Throwable</code> types excluded from this set.
     */
    Collection typesExcluded() {
        return new AbstractCollection() {

	    public Iterator iterator() {
		return new Iterator() {
		    private Iterator i = exceptionsExcluded.iterator();

		    public boolean hasNext() {
			return i.hasNext();
		    }

		    public Object next() {
			return i.next();
		    }

		    public void remove() {
			throw new UnsupportedOperationException();
		    }
		};
	    }

	    public int size() {
		return exceptionsExcluded.size();
	    }
	};
    }


    /**
     * A package-private method to provide unit tests with access to
     * ThrowableSet's internals.
     */
    Map getMemoizedAdds() {
	if (memoizedAdds == null) {
	    return Collections.EMPTY_MAP;
	} else {
	    return Collections.unmodifiableMap(memoizedAdds);
	}
    }
}
