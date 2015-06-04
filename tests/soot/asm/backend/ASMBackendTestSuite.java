package soot.asm.backend;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import soot.baf.ASMBackendMockingTest;
import soot.util.backend.SootASMClassWriterTest;

/**
 * Suite for testing all of the ASM backend
 *
 * @author Tobias Hamann, Florian Kuebler, Dominik Helm, Lukas Sommer
 *
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
	AnnotatedAnnotatedClassTest.class,
	AnnotatedAnnotationTest.class,
	AnnotatedClassTest.class,
	AnnotatedFieldTest.class,
	AnnotatedMethodTest.class,
	AnnotatedParameterTest.class,
	AnnotationTest.class,
	ArithmeticTest.class,
	ArraysTest.class,
	CompareArithmeticInstructions2Test.class,
	CompareArithmeticInstructionsTest.class,
	CompareInstructionsTest.class,
	ConstantPoolTest.class,
	ControlStructuresTest.class,
	DupsTest.class,
	EnumTest.class,
	ExceptionTest.class,
	ExtendedArithmeticLibTest.class,
	InnerClass2Test.class,
	InnerClassTest.class,
	InstanceOfCastsTest.class,
	InterfaceTest.class,
//	LambdaTest.class,
	LineNumbersTest.class,
	LogicalOperationsTest.class,
	MethodExampleTest.class,
	ModifiersTest.class,
	MonitorTest.class,
	NullTypesTest.class,
	OuterClassTest.class,
	ReturnsTest.class,
	StoresTest.class,
	TryCatchTest.class,
	ASMBackendMockingTest.class,
	SootASMClassWriterTest.class,
	MinimalJavaVersionTest.class
})
public class ASMBackendTestSuite {
	
}
