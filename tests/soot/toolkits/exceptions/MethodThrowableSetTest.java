package soot.toolkits.exceptions;

import java.util.Collections;
import java.util.HashSet;
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
    public static void setUp() {
		// Initialize Soot
		G.reset();
		Options.v().set_process_dir(Collections.singletonList("./testclasses"));
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
	 * @param methodSig The signature of the method for which to retrieve the
	 * exceptions
	 * @return The exceptions that the method with the given signature can
	 * possibly throw
	 */
	private ThrowableSet getExceptionsForMethod(String methodSig) {
		SootMethod sm = Scene.v().getMethod(methodSig);
		ThrowAnalysisForTest ta = new ThrowAnalysisForTest();
		return ta.mightThrow(sm); 
	}

	@Test
	public void simpleExceptionTest1(){
		ThrowableSet ts = getExceptionsForMethod(
				"<soot.toolkits.exceptions.targets.MethodThrowableSetClass: void foo()>");
		
		Set<RefLikeType> expected = new HashSet<RefLikeType>();
		expected.addAll(testUtility.VM_ERRORS);
		expected.add(testUtility.ARITHMETIC_EXCEPTION);
		expected.add(testUtility.NULL_POINTER_EXCEPTION);
		
		Assert.assertTrue(ExceptionTestUtility.sameMembers(expected,
				Collections.<AnySubType>emptySet(), ts));
	}
	
	@Test
	public void simpleExceptionTest2(){
		ThrowableSet ts = getExceptionsForMethod(
				"<soot.toolkits.exceptions.targets.MethodThrowableSetClass: void bar()>");
		
		Set<RefLikeType> expected = new HashSet<RefLikeType>();
		expected.addAll(testUtility.VM_ERRORS);
		expected.add(testUtility.ARITHMETIC_EXCEPTION);
		expected.add(testUtility.NULL_POINTER_EXCEPTION);
		
		Assert.assertTrue(ExceptionTestUtility.sameMembers(expected,
				Collections.<AnySubType>emptySet(), ts));
	}

	@Test
	public void simpleExceptionTest3(){
		ThrowableSet ts = getExceptionsForMethod(
				"<soot.toolkits.exceptions.targets.MethodThrowableSetClass: void tool()>");
		
		Set<RefLikeType> expected = new HashSet<RefLikeType>();
		expected.addAll(testUtility.VM_ERRORS);
		expected.add(testUtility.ARITHMETIC_EXCEPTION);
		expected.add(testUtility.NULL_POINTER_EXCEPTION);
		expected.add(testUtility.ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION);
		
		Assert.assertTrue(ExceptionTestUtility.sameMembers(expected,
				Collections.<AnySubType>emptySet(), ts));
	}

	@Test
	public void getAllExceptionTest1(){
		ThrowableSet ts = getExceptionsForMethod(
				"<soot.toolkits.exceptions.targets.MethodThrowableSetClass: void getAllException()>");
		
		Set<RefLikeType> expected = new HashSet<RefLikeType>();
		expected.addAll(testUtility.VM_ERRORS);
		
		Assert.assertTrue(ExceptionTestUtility.sameMembers(expected,
				Collections.<AnySubType>emptySet(), ts));
	}

	@Test
	public void getMyExceptionTest1(){
		ThrowableSet ts = getExceptionsForMethod(
				"<soot.toolkits.exceptions.targets.MethodThrowableSetClass: void getMyException()>");
		
		Set<RefLikeType> expected = new HashSet<RefLikeType>();
		expected.add(AnySubType.v(testUtility.ERROR)); // for NewExpr
		expected.add(testUtility.ILLEGAL_MONITOR_STATE_EXCEPTION);
		expected.add(testUtility.NULL_POINTER_EXCEPTION);
		expected.add(AnySubType.v(Scene.v().getSootClass(EXCEPTION_CLASS).getType()));
		
		Assert.assertTrue(ExceptionTestUtility.sameMembers(expected,
				Collections.<AnySubType>emptySet(), ts));
	}

	@Test
	public void nestedTryCatchTest1(){
		ThrowableSet ts = getExceptionsForMethod(
				"<soot.toolkits.exceptions.targets.MethodThrowableSetClass: void nestedTry()>");
		
		Set<RefLikeType> expected = new HashSet<RefLikeType>();
		expected.addAll(testUtility.VM_ERRORS);
		expected.add(testUtility.ARITHMETIC_EXCEPTION);
		expected.add(testUtility.NULL_POINTER_EXCEPTION);
		expected.add(testUtility.ARRAY_INDEX_OUT_OF_BOUNDS_EXCEPTION);
		
		Assert.assertTrue(ExceptionTestUtility.sameMembers(expected,
				Collections.<AnySubType>emptySet(), ts));
	}

	@Test
	public void recursionTest1(){
		ThrowableSet ts = getExceptionsForMethod(
				"<soot.toolkits.exceptions.targets.MethodThrowableSetClass: void recursion()>");
		
		Set<RefLikeType> expected = new HashSet<RefLikeType>();
		expected.addAll(testUtility.VM_ERRORS);
		
		Assert.assertTrue(ExceptionTestUtility.sameMembers(expected,
				Collections.<AnySubType>emptySet(), ts));
	}

	@Test
	public void unitInCatchBlockTest1(){
		ThrowableSet ts = getExceptionsForMethod(
				"<soot.toolkits.exceptions.targets.MethodThrowableSetClass: void unitInCatchBlock()>");
		
		Set<RefLikeType> expected = new HashSet<RefLikeType>();
		expected.addAll(testUtility.VM_ERRORS);
		expected.add(testUtility.ARITHMETIC_EXCEPTION);
		
		Assert.assertTrue(ExceptionTestUtility.sameMembers(expected,
				Collections.<AnySubType>emptySet(), ts));
	}

}
