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
 * A class for representing the set of exceptions that an
 * instruction may throw.  
 *
 * <p> <code>ThrowableSet</code> does not implement the 
 * {@link java.util.Set} interface, so perhaps it is misnamed.
 * Instead, it provides only the operations that we require for
 * determining whether a given statement might throw an exception that
 * would be caught by a given handler.
 *
 * <p> The class is intended to be immutable (hence the
 * <code>final</code> modifier on its declaration).  It does not take
 * the step of guaranteeing immutability by cloning the {@link
 * RefLikeType} objects it contains, though, because we trust {@link
 * Scene} to enforce the existence of only one {@link RefLikeType}
 * instance with a given name.
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
	private int addsFromMap = 0;
	private int addsFromMemo = 0;
	private int addsNeedingSearch = 0;
	private int registrationCalls = 0;
	private int catchableAsQueries = 0;
	private int catchableAsFromMap = 0;
	private int catchableAsNeedingSearch = 0;
	
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
	    Scene.v().loadClassAndSupport("java.lang.RuntimeException");
	    RUNTIME_EXCEPTION =
		Scene.v().getRefType("java.lang.RuntimeException");
	    Scene.v().loadClassAndSupport("java.lang.ArithmeticException");
	    ARITHMETIC_EXCEPTION =
		Scene.v().getRefType("java.lang.ArithmeticException");
	    Scene.v().loadClassAndSupport("java.lang.ArrayStoreException");
	    ARRAY_STORE_EXCEPTION = 
		Scene.v().getRefType("java.lang.ArrayStoreException");
	    Scene.v().loadClassAndSupport("java.lang.ClassCastException");
	    CLASS_CAST_EXCEPTION =
		Scene.v().getRefType("java.lang.ClassCastException");
	    Scene.v().loadClassAndSupport("java.lang.IllegalMonitorStateException");
            ILLEGAL_MONITOR_STATE_EXCEPTION = 
                Scene.v().getRefType("java.lang.IllegalMonitorStateException");
	    Scene.v().loadClassAndSupport("java.lang.IndexOutOfBoundsException");
	    INDEX_OUT_OF_BOUNDS_EXCEPTION =
		Scene.v().getRefType("java.lang.IndexOutOfBoundsException");
	    Scene.v().loadClassAndSupport("java.lang.ArrayIndexOutOfBoundsException");
	    ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION =
		Scene.v().getRefType("java.lang.ArrayIndexOutOfBoundsException");
	    Scene.v().loadClassAndSupport("java.lang.NegativeArraySizeException");
	    NEGATIVE_ARRAY_SIZE_EXCEPTION =
		Scene.v().getRefType("java.lang.NegativeArraySizeException");	    
	    Scene.v().loadClassAndSupport("java.lang.NullPointerException");
	    NULL_POINTER_EXCEPTION = 
		Scene.v().getRefType("java.lang.NullPointerException");

	    Scene.v().loadClassAndSupport("java.lang.InstantiationError");
	    INSTANTIATION_ERROR =
		Scene.v().getRefType("java.lang.InstantiationError");

	    EMPTY = registerSetIfNew(new HashSet());

	    Set allThrowablesSet = new HashSet();
	    Scene.v().loadClassAndSupport("java.lang.Throwable");
	    allThrowablesSet.add(AnySubType.v(Scene.v().getRefType("java.lang.Throwable")));
	    ALL_THROWABLES = registerSetIfNew(allThrowablesSet);

	    Set vmErrorSet = new HashSet();
	    Scene.v().loadClassAndSupport("java.lang.InternalError");
	    vmErrorSet.add(Scene.v().getRefType("java.lang.InternalError"));
	    Scene.v().loadClassAndSupport("java.lang.OutOfMemoryError");
	    vmErrorSet.add(Scene.v().getRefType("java.lang.OutOfMemoryError"));
	    Scene.v().loadClassAndSupport("java.lang.StackOverflowError");
	    vmErrorSet.add(Scene.v().getRefType("java.lang.StackOverflowError"));
	    Scene.v().loadClassAndSupport("java.lang.UnknownError");
	    vmErrorSet.add(Scene.v().getRefType("java.lang.UnknownError"));

	    // The Java library's deprecated Thread.stop(Throwable) method
	    // would actually allow _any_ Throwable to be delivered
	    // asynchronously, not just java.lang.ThreadDeath.
	    Scene.v().loadClassAndSupport("java.lang.ThreadDeath");
	    vmErrorSet.add(Scene.v().getRefType("java.lang.ThreadDeath"));

	    VM_ERRORS = registerSetIfNew(vmErrorSet);

	    Set resolveClassErrorSet = new HashSet();
	    Scene.v().loadClassAndSupport("java.lang.ClassCircularityError");
	    resolveClassErrorSet.add(Scene.v().getRefType("java.lang.ClassCircularityError"));
	    Scene.v().loadClassAndSupport("java.lang.ClassFormatError");
	    // We add AnySubType(ClassFormatError) so that we can
	    // avoid adding its subclass,
	    // UnsupportedClassVersionError, explicitly.  This is a
	    // hack to allow Soot to analyze older class libraries 
	    // (UnsupportedClassVersionError was added in JDK 1.2).
	    allThrowablesSet.add(AnySubType.v(Scene.v().getRefType("java.lang.ClassFormatError")));
	    Scene.v().loadClassAndSupport("java.lang.IllegalAccessError");
	    resolveClassErrorSet.add(Scene.v().getRefType("java.lang.IllegalAccessError"));
	    Scene.v().loadClassAndSupport("java.lang.IncompatibleClassChangeError");
	    resolveClassErrorSet.add(Scene.v().getRefType("java.lang.IncompatibleClassChangeError"));
	    Scene.v().loadClassAndSupport("java.lang.LinkageError");
	    resolveClassErrorSet.add(Scene.v().getRefType("java.lang.LinkageError"));
	    Scene.v().loadClassAndSupport("java.lang.NoClassDefFoundError");
	    resolveClassErrorSet.add(Scene.v().getRefType("java.lang.NoClassDefFoundError"));
	    Scene.v().loadClassAndSupport("java.lang.VerifyError");
	    resolveClassErrorSet.add(Scene.v().getRefType("java.lang.VerifyError"));
	    RESOLVE_CLASS_ERRORS = registerSetIfNew(resolveClassErrorSet);

	    Set resolveFieldErrorSet = new HashSet(resolveClassErrorSet);
	    Scene.v().loadClassAndSupport("java.lang.NoSuchFieldError");
	    resolveFieldErrorSet.add(Scene.v().getRefType("java.lang.NoSuchFieldError"));
	    RESOLVE_FIELD_ERRORS = registerSetIfNew(resolveFieldErrorSet);

	    Set resolveMethodErrorSet = new HashSet(resolveClassErrorSet);
	    Scene.v().loadClassAndSupport("java.lang.AbstractMethodError");
	    resolveMethodErrorSet.add(Scene.v().getRefType("java.lang.AbstractMethodError"));
	    Scene.v().loadClassAndSupport("java.lang.NoSuchMethodError");
	    resolveMethodErrorSet.add(Scene.v().getRefType("java.lang.NoSuchMethodError"));
	    Scene.v().loadClassAndSupport("java.lang.UnsatisfiedLinkError");
	    resolveMethodErrorSet.add(Scene.v().getRefType("java.lang.UnsatisfiedLinkError"));
	    RESOLVE_METHOD_ERRORS = registerSetIfNew(resolveMethodErrorSet);

	    // The static initializers of a newly loaded class might
	    // throw any Error (if they threw an Exception---even a
	    // RuntimeException---it would be replaced by an
	    // ExceptionInInitializerError):
	    //
	    Set initializationErrorSet = new HashSet();
	    Scene.v().loadClassAndSupport("java.lang.Error");
	    initializationErrorSet.add(AnySubType.v(Scene.v().getRefType("java.lang.Error")));
	    INITIALIZATION_ERRORS = registerSetIfNew(initializationErrorSet);
	}


	/**
	 * Returns the single instance of <code>ThrowableSet.Manager</code>.
	 *
	 * @return Soot's <code>ThrowableSet.Manager</code>.
	 */
	public static Manager v() { 
	    return G.v().ThrowableSetManager(); 
	}


	/**
	 * Returns a <code>ThrowableSet</code> representing the set of
	 * exceptions included in <code>s</code>. Creates a new
	 * <code>ThrowableSet</code> only if there was not already one
	 * whose contents correspond to <code>s</code>.
	 *
	 * @param s  A set of {@link RefLikeType}
	 * objects representing exception types.
	 *
	 * @return a <code>ThrowableSet</code> representing the
	 * set of exceptions corresponding to <code>s</code>.
	 */
	private ThrowableSet registerSetIfNew(Set s) {
	    if (INSTRUMENTING) {
		registrationCalls++;
	    }
	    Integer sizeKey = new Integer(s.size());

	    // In principle, we are actually mapping from sizes to
	    // sets of ThrowableSets of that size, so we could use a
	    // Set in the next line instead of a List. I'm assuming
	    // that the number of ThrowableSets per size is small
	    // enough that it's cheaper to use the simpler data
	    // structure.  Changing the implementation to use Sets
	    // instead should require modifying only this method and
	    // any test routines that use getSizeToSets().
	    //
	    List sizeList = (List) sizeToSets.get(sizeKey);

	    if (sizeList == null) {
		sizeList = new LinkedList();
		sizeToSets.put(sizeKey, sizeList);
	    }
	    for (Iterator i = sizeList.iterator(); i.hasNext() ;) {
		ThrowableSet set = (ThrowableSet) i.next();
		if (set.exceptions.equals(s)) {
		    return set;
		}
	    }
	    if (INSTRUMENTING) {
		registeredSets++;
	    }
	    ThrowableSet result = new ThrowableSet(s);
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
		.append("\naddsFromMap: ")
		.append(addsFromMap)
		.append("\naddsFromMemo: ")
		.append(addsFromMemo)
		.append("\naddsNeedingSearch: ")
		.append(addsNeedingSearch)
		.append("\nregistrationCalls: ")
		.append(registrationCalls)
		.append("\ncatchableAsQueries: ")
		.append(catchableAsQueries)
		.append("\ncatchableAsFromMap: ")
		.append(catchableAsFromMap)
		.append("\ncatchableAsNeedingSearch: ")
		.append(catchableAsNeedingSearch)
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


    /**
     * Set of exception types represented by the set.
     */
    private final Set exceptions;

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
    private final Map memoizedAdds = new HashMap(); 


    /**
     * Constructs a <code>ThrowableSet</code> which contains
     * the exceptions in <code>s</code>. The constructor is private to
     * ensure that the only way to get a new
     * <code>ThrowableSet</code> is by adding to an existing
     * one.
     *
     * @param s  The set of {@link RefLikeType} objects representing the 
     *           types thrown.
     */
    private ThrowableSet(Set s) {
	exceptions = Collections.unmodifiableSet(s);
	// We don't need to clone s to guarantee immutability since
	// ThrowableSet(Set) is private to this class, where it is
	// only called (via Manager.v().registerSetIfNew())
	// with arguments which the callers do not subsequently modify.
    }


    /**
     * Returns a <code>ThrowableSet</code> which contains
     * <code>e</code> in addition to the exceptions in
     * this <code>ThrowableSet</code>. 
     *
     * <p>Add <code>e</code> as a {@link RefType} when
     * you know that the run-time type of the exception you are representing is
     * necessarily <code>e</code> and not a subclass of
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
     * since the run-time type of the exception is necessarily
     * <code>IOException</code>.
     *
     * @param e	the exception class
     *
     * @return a set containing <code>e</code> as well as the
     * exceptions in this set.
     */
    public ThrowableSet add(RefType e) {
	if (INSTRUMENTING) {
	    Manager.v().addsOfRefType++;
	}
	if (this.exceptions.contains(e)) {
	    if (INSTRUMENTING) {
		Manager.v().addsFromMap++;
	    }
	    return this; 
	} else {
	    ThrowableSet result = (ThrowableSet) memoizedAdds.get(e);
	    if (result != null) {
		if (INSTRUMENTING) {
		    Manager.v().addsFromMemo++;
		}
		return result;
	    } else {
		if (INSTRUMENTING) {
		    Manager.v().addsNeedingSearch++;
		}
		FastHierarchy hierarchy = Scene.v().getOrMakeFastHierarchy();
		for (Iterator i = this.exceptions.iterator(); i.hasNext() ; ) {
		    RefLikeType incumbent = (RefLikeType) i.next();
		    if (incumbent instanceof AnySubType) {
			// Need to use incumbent.getBase() below because
			// hierarchy.canStoreType() assumes that parent
			// is not an AnySubType.
			if (hierarchy.canStoreType(e, ((AnySubType) incumbent).getBase())) {
			    memoizedAdds.put(e, this);
			    return this;
			}
		    } else if (! (incumbent instanceof RefType)) {
			// assertion failure.
			throw new RuntimeException("ThrowableSet.add(RefType): Set element " +
						   incumbent.toString() +
						   " is neither a RefType nor an AnySubType.");
		    }
		}
		Set  resultSet = new HashSet(this.exceptions);
		resultSet.add(e);
		result = Manager.v().registerSetIfNew(resultSet);
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
     * <code>e</code>, but it is possible that some instantiations of
     * the exception will have a run-time type which is a subclass of
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
     */
    public ThrowableSet add(AnySubType e) {
	if (INSTRUMENTING) {
	    Manager.v().addsOfAnySubType++;
	}
	if (this.exceptions.contains(e)) {
	    if (INSTRUMENTING) {
		Manager.v().addsFromMap++;
	    }
	    return this; 
	} else {
	    ThrowableSet result = (ThrowableSet) memoizedAdds.get(e);
	    if (result != null) {
		if (INSTRUMENTING) {
		    Manager.v().addsFromMemo++;
		}
		return result;
	    } else {
		if (INSTRUMENTING) {
		    Manager.v().addsNeedingSearch++;
		}
		int changes = 0;
		boolean addNewException = true;
		Set  resultSet = new HashSet();
		FastHierarchy hierarchy = Scene.v().getOrMakeFastHierarchy();
		RefType newBase = e.getBase(); 
		for (Iterator i = this.exceptions.iterator(); i.hasNext() ; ) {
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
			// We have to use the base types in these calls to
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
			throw new RuntimeException("ThrowableSet.add(AnySubType): Set element " +
						   incumbent.toString() + 
						   " is neither a RefType nor an AnySubType.");
		    }
		}
		if (addNewException) {
		    resultSet.add(e);
		    changes++;
		}
		if (changes > 0) {
		    result = Manager.v().registerSetIfNew(resultSet);
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
     * @param s	set of exceptions to add to this.
     *
     * @return the union of this set with <code>s</code>
     */
    public ThrowableSet add(ThrowableSet s) {
	if (INSTRUMENTING) {
	    Manager.v().addsOfSet++;
	}
	ThrowableSet result = (ThrowableSet) memoizedAdds.get(s);
	if (result == null) {
	    if (INSTRUMENTING) {
		Manager.v().addsNeedingSearch++;
	    }
	    result = this.add(s.exceptions);
	    memoizedAdds.put(s, result);
	} else if (INSTRUMENTING) {
	    Manager.v().addsFromMemo++;
	}
	return result;
    }


    /**
     * Returns a <code>ThrowableSet</code> which contains all
     * the exceptions in <code>addedExceptions</code> in addition to those
     * in this <code>ThrowableSet</code>. 
     *
     * @param addedExceptions A set of {@link RefLikeType} and 
     * {@link AnySubType} objects
     *
     * @return a set containing all the <code>addedExceptions</code> as well
     * as the exceptions in this set.
     */
    private ThrowableSet add(Set addedExceptions) {
	Set resultSet = new HashSet(this.exceptions);
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
				throw new RuntimeException("ThrowableSet.add(Set): resultSet.contains() failed to screen duplicate RefType "
							   + newType);
			    }
			} else if (incumbentType instanceof AnySubType) {
			    RefType incumbentBase = ((AnySubType) incumbentType).getBase();
			    if (hierarchy.canStoreType(newType, incumbentBase)) {
				// No need to add this class.
				addNewType = false;
			    }
			} else { // assertion failure.
			    throw new RuntimeException("ThrowableSet.add(Set): incumbent Set element " 
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
				throw new RuntimeException("ThrowableSet.add(Set): resultSet.contains() failed to screen duplicate AnySubType "
							   + newBase);
			    } else if (hierarchy.canStoreType(incumbentBase, newBase)) {
				j.remove();
				changes++;
			    } else if (hierarchy.canStoreType(newBase, incumbentBase)) {
				// No need to add this class.
				addNewType = false;
			    }
			} else { // assertion failure.
			    throw new RuntimeException("ThrowableSet.add(Set): old Set element "
						       + incumbentType
						       + " is neither a RefType nor an AnySubType.");
			}
		    }
		} else { // assertion failure.
		    throw new RuntimeException("ThrowableSet.add(Set): new Set element " 
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
	    result = Manager.v().registerSetIfNew(resultSet);
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
     *                           that might be caught by <code>catcher</code>.
     */
    public boolean catchableAs(RefType catcher) {
	if (INSTRUMENTING) {
	    Manager.v().catchableAsQueries++;
	}
	if (exceptions.contains(catcher)) {
	    if (INSTRUMENTING) {
		Manager.v().catchableAsFromMap++;
	    }
	    return true;
	} else {
	    if (INSTRUMENTING) {
		Manager.v().catchableAsNeedingSearch++;
	    }
	    FastHierarchy h = Scene.v().getOrMakeFastHierarchy();
	    for (Iterator i = exceptions.iterator(); i.hasNext(); ) {
		RefLikeType thrownType = (RefLikeType) i.next();
		if (thrownType instanceof RefType) {
		    if (thrownType == catcher) {
			// assertion failure.
			throw new RuntimeException("ThrowableSet.catchableAs(RefType): exceptions.contains() failed to match contained RefType "
							   + catcher);
		    } else if (h.canStoreType(thrownType, catcher)) {
			return true;
		    }
		} else if (thrownType instanceof AnySubType) {
		    RefType thrownBase = ((AnySubType) thrownType).getBase();
		    // At runtime, thrownType might be instantiated by any
		    // of thrownBase's subtypes, so:
		    if (h.canStoreType(thrownBase, catcher)
			|| h.canStoreType(catcher, thrownBase)) {
			return true;
		    }
		} else { // assertion failure.
		    throw new RuntimeException("ThrowableSet.catchableAs(RefType): Set element " 
					       + thrownType
					       + " is neither a RefType nor an AnySubType."
					       );
		}
	    }
	    return false;
	}
    }


    /** 
     * Returns an unmodifiable collection view of the {@link
     * RefLikeType} objects which represent the <code>Throwable</code>
     * types included in this set.
     *
     * <p>Effective use of the collection really requires knowledge of
     * the internals of <code>ThrowableSet</code>.  It is provided for
     * analyses which can be implemented most efficiently by iterating
     * through individual exceptions in the set.
     *
     * @return an unmodifiable collection view of the
     * <code>Throwable</code> types in this set.
     */
    public Collection types() {
        return new AbstractCollection() {

	    public Iterator iterator() {
		return new Iterator() {
		    private Iterator i = exceptions.iterator();

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
		return exceptions.size();
	    }
	};
    }


    /**
     * Returns a string representation of this <code>ThrowableSet</code>.
     */
    public String toString() {
	StringBuffer buffer = new StringBuffer(this.toBriefString());
	buffer.append(":\n");
	for (Iterator i = exceptions.iterator(); i.hasNext(); ) {
	    buffer.append(' ');
	    buffer.append(i.next().toString());
	}
	return buffer.toString();
    }


    /**
     * Returns a cryptic identifier for a <code>ThrowableSet</code>,
     * used to identify a set when it appears in a collection.
     */
    public String toBriefString() {
	return super.toString();
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
     * <li>If all the elements of VM_ERRORS are present, they are
     * abbreviated as &ldquo;<code>vmErrors</code>&rdquo; rather than
     * listed individually.</li>
     * 
     * @return An abbreviated representation of the contents of this set.
     */
    public String toAbbreviatedString() {
	final String JAVA_LANG = "java.lang.";
	final int JAVA_LANG_LENGTH = JAVA_LANG.length();
	final String EXCEPTION = "Exception";
	final  int EXCEPTION_LENGTH = EXCEPTION.length();

	Collection setsThrowables = this.types();
	Collection vmErrorThrowables = ThrowableSet.Manager.v().VM_ERRORS.types();
	boolean containsAllVmErrors = setsThrowables.containsAll(vmErrorThrowables);
	StringBuffer buf = new StringBuffer();

	if (containsAllVmErrors) {
	    buf.append("+vmErrors");
	}

	for (Iterator it = this.types().iterator(); it.hasNext(); ) {
	    RefLikeType reflikeType = (RefLikeType) it.next();
	    RefType baseType = null;
	    if (reflikeType instanceof RefType) {
		baseType = (RefType)reflikeType;
		if (vmErrorThrowables.contains(baseType) && containsAllVmErrors) {
		    continue;		// This is already accounted for in "+vmError".
		} else {
		    buf.append('+');
		}
	    } else if (reflikeType instanceof AnySubType) {
		buf.append("+(");
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
     * ThrowableSet's internals.
     */
    Map getMemoizedAdds() {
        return memoizedAdds;
    }
}
