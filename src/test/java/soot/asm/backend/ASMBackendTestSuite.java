package soot.asm.backend;

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
