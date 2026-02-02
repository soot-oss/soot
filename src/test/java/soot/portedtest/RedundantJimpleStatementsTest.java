package soot.portedtest;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2014 Raja Vallee-Rai and others
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

import com.google.common.base.Joiner;
import org.junit.Ignore;
import org.junit.Test;
import soot.*;
import soot.options.Options;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests for the issue regarding the always-false condition documented in https://github.com/soot-oss/soot/pull/1834
 * The subsequent test cases have been adapted from SootUp (https://github.com/soot-oss/SootUp/pull/472)
 *
 * @author momo
 */

public class RedundantJimpleStatementsTest {

    final String resourcePath = "src/test/resources/ported/PR1834";

    public void loadClasses(String first, String... more) {
        Path cp = Paths.get(first, more);
        G.reset();
        Options.v().set_prepend_classpath(true);
        Options.v().set_process_dir(Collections.singletonList(cp.toFile().getAbsolutePath()));
        Options.v().set_src_prec(Options.src_prec_class);
        Options.v().set_output_format(Options.output_format_jimple);
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_ignore_resolving_levels(true);
        PhaseOptions.v().setPhaseOption("jb", "stabilize-local-names:true");

        Scene.v().loadNecessaryClasses();
    }

    private String bodyStmtsAsString(Body body) {
        return Joiner.on('\n').join(body.getUnits());
    }

    private void assertJimpleStmts(SootMethod method, List<String> expectedStmts) {
        Body body = method.retrieveActiveBody();
        assertNotNull(body);
        String actualStmts = bodyStmtsAsString(body);

        String exp = Joiner.on('\n').join(expectedStmts);
        if (!exp.equals(actualStmts)) {
            //Use a custom error message which is nicely readable;
            //JUnits assertEquals mangles with the text, which makes it harder to retrieve the ground truth
            throw new AssertionError(String.format("Expected:\n%s\n\nWas:\n%s", exp, actualStmts));
        }
    }

    @Test
    public void test01() {
        loadClasses(resourcePath, "java8", "bin");
        SootMethod method = Scene.v().getMethod("<MethodAcceptingLamExpr: void lambdaAsParamMethod()>");
        List<String> expectedBodyStmts = Stream.of(
        		"r0 := @this: MethodAcceptingLamExpr",
        		"r1 = staticinvoke <MethodAcceptingLamExpr$lambda_lambdaAsParamMethod_0__1: Percentage bootstrap$()>()",
        		"$r2 = <java.lang.System: java.io.PrintStream out>",
        		"$r4 = new java.lang.StringBuilder",
        		"specialinvoke $r4.<java.lang.StringBuilder: void <init>()>()",
        		"$r5 = virtualinvoke $r4.<java.lang.StringBuilder: java.lang.StringBuilder append(java.lang.String)>(\"Percentage : \")",
        		"$d0 = interfaceinvoke r1.<Percentage: double calcPercentage(double)>(45.0)",
        		"$r6 = virtualinvoke $r5.<java.lang.StringBuilder: java.lang.StringBuilder append(double)>($d0)",
        		"$r3 = virtualinvoke $r6.<java.lang.StringBuilder: java.lang.String toString()>()",
        		"virtualinvoke $r2.<java.io.PrintStream: void println(java.lang.String)>($r3)",
        		"return"
        		).collect(Collectors.toCollection(ArrayList::new));
        assertJimpleStmts(method, expectedBodyStmts);
    }

    @Test
    public void test02() {
        loadClasses(resourcePath, "java9", "bin");
        List<String> expectedBodyStmts = Stream.of("r1 = dynamicinvoke \"makeConcatWithConstants\" <java.lang.String (java.lang.String)>(\"This test\") <java.lang.invoke.StringConcatFactory: java.lang.invoke.CallSite makeConcatWithConstants(java.lang.invoke.MethodHandles$Lookup,java.lang.String,java.lang.invoke.MethodType,java.lang.String,java.lang.Object[])>(\"\\u0001 is cool\")",
        		"$r0 = <java.lang.System: java.io.PrintStream out>",
        		"virtualinvoke $r0.<java.io.PrintStream: void println(java.lang.String)>(r1)",
        		"return").collect(Collectors.toCollection(ArrayList::new));
        SootMethod method = Scene.v().getMethod("<DynamicInvoke: void stringConcatenation()>");
        assertJimpleStmts(method, expectedBodyStmts);
    }

    @Test
    public void test03() {
        loadClasses(resourcePath, "java11", "bin");
        List<String> expectedBodyStmts = Stream.of(
        		"r0 := @this: TypeInferenceLambda",
        		"r5 = staticinvoke <TypeInferenceLambda$lambda_lambda_0__1: java.util.function.BinaryOperator bootstrap$()>()",
        		"$r1 = staticinvoke <java.lang.Integer: java.lang.Integer valueOf(int)>(2)",
        		"$r2 = staticinvoke <java.lang.Integer: java.lang.Integer valueOf(int)>(3)",
        		"$r4 = interfaceinvoke r5.<java.util.function.BinaryOperator: java.lang.Object apply(java.lang.Object,java.lang.Object)>($r1, $r2)",
        		"$r3 = (java.lang.Integer) $r4",
        		"virtualinvoke $r3.<java.lang.Integer: int intValue()>()",
        		"return").collect(Collectors.toCollection(ArrayList::new));
        SootMethod method = Scene.v().getMethod("<TypeInferenceLambda: void lambda()>");
        assertJimpleStmts(method, expectedBodyStmts);
    }

    @Test
    public void test04() {
        loadClasses(resourcePath, "java6", "bin");

        List<String> expectedBodyStmts = Stream.of(
        		"r0 := @this: Autoboxing",
        		"staticinvoke <java.lang.Integer: java.lang.Integer valueOf(int)>(6)",
        		"return").collect(Collectors.toCollection(ArrayList::new));
        SootMethod method = Scene.v().getMethod("<Autoboxing: void autoboxing()>");
        assertJimpleStmts(method, expectedBodyStmts);
    }

    @Test
    public void test05() {
        loadClasses(resourcePath, "java6", "bin");

        List<String> expectedBodyStmts = Stream.of(
        		"r00 := @this: GenTypeParam",
        		"$r11 = new java.util.ArrayList",
        		"specialinvoke $r11.<java.util.ArrayList: void <init>(int)>(3)",
        		"$r09 = newarray (java.lang.Integer)[3]",
        		"$r03 = staticinvoke <java.lang.Integer: java.lang.Integer valueOf(int)>(1)",
        		"$r09[0] = $r03",
        		"$r04 = staticinvoke <java.lang.Integer: java.lang.Integer valueOf(int)>(2)",
        		"$r09[1] = $r04",
        		"$r05 = staticinvoke <java.lang.Integer: java.lang.Integer valueOf(int)>(3)",
        		"$r09[2] = $r05",
        		"r12 = staticinvoke <java.util.Arrays: java.util.List asList(java.lang.Object[])>($r09)",
        		"$r01 = new GenTypeParam",
        		"specialinvoke $r01.<GenTypeParam: void <init>()>()",
        		"virtualinvoke $r01.<GenTypeParam: void copy(java.util.List,java.util.List)>($r11, r12)",
        		"$r02 = <java.lang.System: java.io.PrintStream out>",
        		"$r06 = staticinvoke <java.lang.Integer: java.lang.Integer valueOf(int)>(2)",
        		"$r07 = staticinvoke <java.lang.Integer: java.lang.Integer valueOf(int)>(8)",
        		"$r08 = staticinvoke <java.lang.Integer: java.lang.Integer valueOf(int)>(3)",
        		"$r10 = virtualinvoke $r01.<GenTypeParam: java.lang.Number largestNum(java.lang.Number,java.lang.Number,java.lang.Number)>($r06, $r07, $r08)",
        		"virtualinvoke $r02.<java.io.PrintStream: void println(java.lang.Object)>($r10)",
        		"return").collect(Collectors.toCollection(ArrayList::new));
        SootMethod method = Scene.v().getMethod("<GenTypeParam: void geneTypeParamDisplay()>");
        assertJimpleStmts(method, expectedBodyStmts);
    }

    @Test
    @Ignore("The variable names r10 and r11 may be used in swapped order")
    public void test06() {
        loadClasses(resourcePath, "java6", "bin");
        List<String> expectedBodyStmts = Stream.of(
        		"r00 := @this: Reflection",
        		"$r01 = new Reflection",
        		"specialinvoke $r01.<Reflection: void <init>()>()",
        		"r05 = class \"LReflection;\"",
        		"r06 = class \"LReflection;\"",
        		"r07 = class \"LReflection;\"",
        		"$r02 = <java.lang.System: java.io.PrintStream out>",
        		"virtualinvoke $r02.<java.io.PrintStream: void println(java.lang.Object)>(class \"LReflection;\")",
        		"$r08 = newarray (java.lang.Class)[0]",
        		"r10 = virtualinvoke r06.<java.lang.Class: java.lang.reflect.Constructor getConstructor(java.lang.Class[])>($r08)",
        		"$r03 = <java.lang.System: java.io.PrintStream out>",
        		"$r09 = virtualinvoke r10.<java.lang.reflect.Constructor: java.lang.String getName()>()",
        		"virtualinvoke $r03.<java.io.PrintStream: void println(java.lang.String)>($r09)",
        		"$r04 = <java.lang.System: java.io.PrintStream out>",
        		"$r11 = virtualinvoke r07.<java.lang.Class: java.lang.reflect.Method[] getMethods()>()",
        		"$i00 = lengthof $r11",
        		"virtualinvoke $r04.<java.io.PrintStream: void println(int)>($i00)",
        		"return"
        		).collect(Collectors.toCollection(ArrayList::new));
        SootMethod method = Scene.v().getMethod("<Reflection: void checkReflection()>");
        assertJimpleStmts(method, expectedBodyStmts);
    }

    @Test
    public void test07() {
        loadClasses(resourcePath, "java6", "bin");
        List<String> expectedBodyStmts = Stream.of(
        		"r0 := @this: UncheckedCast",
        		"$r6 = newarray (java.lang.Integer)[4]",
        		"$r2 = staticinvoke <java.lang.Integer: java.lang.Integer valueOf(int)>(5)",
        		"$r6[0] = $r2",
        		"$r3 = staticinvoke <java.lang.Integer: java.lang.Integer valueOf(int)>(8)",
        		"$r6[1] = $r3",
        		"$r4 = staticinvoke <java.lang.Integer: java.lang.Integer valueOf(int)>(9)",
        		"$r6[2] = $r4",
        		"$r5 = staticinvoke <java.lang.Integer: java.lang.Integer valueOf(int)>(6)",
        		"$r6[3] = $r5",
        		"r7 = staticinvoke <java.util.Arrays: java.util.List asList(java.lang.Object[])>($r6)",
        		"$r1 = <java.lang.System: java.io.PrintStream out>",
        		"virtualinvoke $r1.<java.io.PrintStream: void println(java.lang.Object)>(r7)",
        		"return").collect(Collectors.toCollection(ArrayList::new));
        SootMethod method = Scene.v().getMethod("<UncheckedCast: void uncheckedCastDisplay()>");
        assertJimpleStmts(method, expectedBodyStmts);
    }

    @Test
    public void test08() {
        loadClasses(resourcePath, "java11", "bin");
        List<String> expectedBodyStmts = Stream.of(
        		"r0 := @this: TypeInferenceLambda",
        		"r5 = staticinvoke <TypeInferenceLambda$lambda_lambda_0__1: java.util.function.BinaryOperator bootstrap$()>()",
        		"$r1 = staticinvoke <java.lang.Integer: java.lang.Integer valueOf(int)>(2)",
        		"$r2 = staticinvoke <java.lang.Integer: java.lang.Integer valueOf(int)>(3)",
        		"$r4 = interfaceinvoke r5.<java.util.function.BinaryOperator: java.lang.Object apply(java.lang.Object,java.lang.Object)>($r1, $r2)",
        		"$r3 = (java.lang.Integer) $r4",
        		"virtualinvoke $r3.<java.lang.Integer: int intValue()>()",
        		"return").collect(Collectors.toCollection(ArrayList::new));

        SootMethod method = Scene.v().getMethod("<TypeInferenceLambda: void lambda()>");
        assertJimpleStmts(method, expectedBodyStmts);
    }


    @Test
    public void test09() {
        loadClasses(resourcePath, "java9", "bin");
        List<String> expectedBodyStmts = Stream.of(
        		"r0 := @this: AnonymousDiamondOperator",
        		"$r1 = new AnonymousDiamondOperator$1",
        		"specialinvoke $r1.<AnonymousDiamondOperator$1: void <init>(AnonymousDiamondOperator)>(r0)",
        		"$r3 = staticinvoke <java.lang.Integer: java.lang.Integer valueOf(int)>(22)",
        		"$r4 = staticinvoke <java.lang.Integer: java.lang.Integer valueOf(int)>(23)",
        		"$r2 = (MyClass) $r1",
        		"$r6 = virtualinvoke $r2.<MyClass: java.lang.Object add(java.lang.Object,java.lang.Object)>($r3, $r4)",
        		"r5 = (java.lang.Integer) $r6",
        		"$i0 = virtualinvoke r5.<java.lang.Integer: int intValue()>()",
        		"return $i0").collect(Collectors.toCollection(ArrayList::new));
        SootMethod method = Scene.v().getMethod("<AnonymousDiamondOperator: int innerClassDiamond()>");
        assertJimpleStmts(method, expectedBodyStmts);
    }

    @Test
    public void test10() {
        loadClasses(resourcePath, "java6", "bin");
        List<String> expectedBodyStmts = Stream.of(
        		"r0 := @this: DeclareEnum",
        		"r2 = staticinvoke <DeclareEnum$Type: DeclareEnum$Type[] values()>()",
        		"i0 = lengthof r2",
        		"i1 = 0",
        		"if i1 >= i0 goto return",
        		"r1 = r2[i1]",
        		"$r3 = <java.lang.System: java.io.PrintStream out>",
        		"virtualinvoke $r3.<java.io.PrintStream: void println(java.lang.Object)>(r1)",
        		"i1 = i1 + 1",
        		"goto [?= (branch)]",
        		"return").collect(Collectors.toCollection(ArrayList::new));
        SootMethod method = Scene.v().getMethod("<DeclareEnum: void declareEnum()>");
        assertJimpleStmts(method, expectedBodyStmts);
    }

    @Test
    public void test11() {
        loadClasses(resourcePath, "java6", "bin");
        List<String> expectedBodyStmts = Stream.of(
        		"r0 := @this: GenericTypeParamOnClass",
        		"$r1 = new GenericTypeParamOnClass$A",
        		"specialinvoke $r1.<GenericTypeParamOnClass$A: void <init>(GenericTypeParamOnClass)>(r0)",
        		"$r2 = staticinvoke <java.lang.Integer: java.lang.Integer valueOf(int)>(5)",
        		"staticinvoke <GenericTypeParamOnClass$A: void access$000(GenericTypeParamOnClass$A,java.lang.Object)>($r1, $r2)",
        		"$r4 = virtualinvoke $r1.<GenericTypeParamOnClass$A: java.lang.Object get()>()",
        		"$r3 = (java.lang.Integer) $r4",
        		"virtualinvoke $r3.<java.lang.Integer: int intValue()>()",
        		"return").collect(Collectors.toCollection(ArrayList::new));
        SootMethod method = Scene.v().getMethod("<GenericTypeParamOnClass: void genericTypeParamOnClass()>");
        assertJimpleStmts(method, expectedBodyStmts);
    }
}
