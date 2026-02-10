package soot.portedtest;

import static org.junit.Assert.assertNotNull;

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

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Ignore;
import org.junit.Test;

import soot.Body;
import soot.G;
import soot.PhaseOptions;
import soot.Scene;
import soot.SootMethod;
import soot.options.Options;

/**
 * Tests for the issue regarding the always-false condition documented in https://github.com/soot-oss/soot/pull/1834 The
 * subsequent test cases have been adapted from SootUp (https://github.com/soot-oss/SootUp/pull/472)
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
      // Use a custom error message which is nicely readable;
      // JUnits assertEquals mangles with the text, which makes it harder to retrieve the ground truth
      throw new AssertionError(String.format("Expected:\n%s\n\nWas:\n%s", exp, actualStmts));
    }
  }

  @Test
  public void test01() {
    loadClasses(resourcePath, "java8", "bin");
    SootMethod method = Scene.v().getMethod("<MethodAcceptingLamExpr: void lambdaAsParamMethod()>");
    List<String> expectedBodyStmts = Stream.of("r0 := @this: MethodAcceptingLamExpr\n"
        + "r1 = staticinvoke <MethodAcceptingLamExpr$lambda_lambdaAsParamMethod_0__1: Percentage bootstrap$()>()\n"
        + "$r2 = <java.lang.System: java.io.PrintStream out>\n" + "$r4 = new java.lang.StringBuilder\n"
        + "specialinvoke $r4.<java.lang.StringBuilder: void <init>()>()\n"
        + "$r4 = virtualinvoke $r4.<java.lang.StringBuilder: java.lang.StringBuilder append(java.lang.String)>(\"Percentage : \")\n"
        + "$d0 = interfaceinvoke r1.<Percentage: double calcPercentage(double)>(45.0)\n"
        + "$r4 = virtualinvoke $r4.<java.lang.StringBuilder: java.lang.StringBuilder append(double)>($d0)\n"
        + "$r3 = virtualinvoke $r4.<java.lang.StringBuilder: java.lang.String toString()>()\n"
        + "virtualinvoke $r2.<java.io.PrintStream: void println(java.lang.String)>($r3)\n" + "return")
        .collect(Collectors.toCollection(ArrayList::new));
    assertJimpleStmts(method, expectedBodyStmts);
  }

  @Test
  public void test02() {
    loadClasses(resourcePath, "java9", "bin");
    List<String> expectedBodyStmts = Stream.of(
        "r1 = dynamicinvoke \"makeConcatWithConstants\" <java.lang.String (java.lang.String)>(\"This test\") <java.lang.invoke.StringConcatFactory: java.lang.invoke.CallSite makeConcatWithConstants(java.lang.invoke.MethodHandles$Lookup,java.lang.String,java.lang.invoke.MethodType,java.lang.String,java.lang.Object[])>(\"\\u0001 is cool\")",
        "$r0 = <java.lang.System: java.io.PrintStream out>",
        "virtualinvoke $r0.<java.io.PrintStream: void println(java.lang.String)>(r1)", "return")
        .collect(Collectors.toCollection(ArrayList::new));
    SootMethod method = Scene.v().getMethod("<DynamicInvoke: void stringConcatenation()>");
    assertJimpleStmts(method, expectedBodyStmts);
  }

  @Test
  public void test03() {
    loadClasses(resourcePath, "java11", "bin");
    List<String> expectedBodyStmts = Stream.of("r0 := @this: TypeInferenceLambda\n"
        + "r5 = staticinvoke <TypeInferenceLambda$lambda_lambda_0__1: java.util.function.BinaryOperator bootstrap$()>()\n"
        + "$r1 = staticinvoke <java.lang.Integer: java.lang.Integer valueOf(int)>(2)\n"
        + "$r2 = staticinvoke <java.lang.Integer: java.lang.Integer valueOf(int)>(3)\n"
        + "$r4 = interfaceinvoke r5.<java.util.function.BinaryOperator: java.lang.Object apply(java.lang.Object,java.lang.Object)>($r1, $r2)\n"
        + "$r1 = (java.lang.Integer) $r4\n" + "virtualinvoke $r1.<java.lang.Integer: int intValue()>()\n" + "return")
        .collect(Collectors.toCollection(ArrayList::new));
    SootMethod method = Scene.v().getMethod("<TypeInferenceLambda: void lambda()>");
    assertJimpleStmts(method, expectedBodyStmts);
  }

  @Test
  public void test04() {
    loadClasses(resourcePath, "java6", "bin");

    List<String> expectedBodyStmts = Stream
        .of("r0 := @this: Autoboxing", "staticinvoke <java.lang.Integer: java.lang.Integer valueOf(int)>(6)", "return")
        .collect(Collectors.toCollection(ArrayList::new));
    SootMethod method = Scene.v().getMethod("<Autoboxing: void autoboxing()>");
    assertJimpleStmts(method, expectedBodyStmts);
  }

  @Test
  public void test05() {
    loadClasses(resourcePath, "java6", "bin");

    List<String> expectedBodyStmts = Stream.of("r00 := @this: GenTypeParam\n" + "$r11 = new java.util.ArrayList\n"
        + "specialinvoke $r11.<java.util.ArrayList: void <init>(int)>(3)\n" + "$r09 = newarray (java.lang.Integer)[3]\n"
        + "$r03 = staticinvoke <java.lang.Integer: java.lang.Integer valueOf(int)>(1)\n" + "$r09[0] = $r03\n"
        + "$r03 = staticinvoke <java.lang.Integer: java.lang.Integer valueOf(int)>(2)\n" + "$r09[1] = $r03\n"
        + "$r03 = staticinvoke <java.lang.Integer: java.lang.Integer valueOf(int)>(3)\n" + "$r09[2] = $r03\n"
        + "r12 = staticinvoke <java.util.Arrays: java.util.List asList(java.lang.Object[])>($r09)\n"
        + "r00 = new GenTypeParam\n" + "specialinvoke r00.<GenTypeParam: void <init>()>()\n"
        + "virtualinvoke r00.<GenTypeParam: void copy(java.util.List,java.util.List)>($r11, r12)\n"
        + "$r02 = <java.lang.System: java.io.PrintStream out>\n"
        + "$r03 = staticinvoke <java.lang.Integer: java.lang.Integer valueOf(int)>(2)\n"
        + "$r07 = staticinvoke <java.lang.Integer: java.lang.Integer valueOf(int)>(8)\n"
        + "$r08 = staticinvoke <java.lang.Integer: java.lang.Integer valueOf(int)>(3)\n"
        + "$r10 = virtualinvoke r00.<GenTypeParam: java.lang.Number largestNum(java.lang.Number,java.lang.Number,java.lang.Number)>($r03, $r07, $r08)\n"
        + "virtualinvoke $r02.<java.io.PrintStream: void println(java.lang.Object)>($r10)\n" + "return")
        .collect(Collectors.toCollection(ArrayList::new));
    SootMethod method = Scene.v().getMethod("<GenTypeParam: void geneTypeParamDisplay()>");
    assertJimpleStmts(method, expectedBodyStmts);
  }

  @Test
  @Ignore("The variable names r10 and r11 may be used in swapped order")
  public void test06() {
    loadClasses(resourcePath, "java6", "bin");
    List<String> expectedBodyStmts = Stream.of("r00 := @this: Reflection", "$r01 = new Reflection",
        "specialinvoke $r01.<Reflection: void <init>()>()", "r05 = class \"LReflection;\"", "r06 = class \"LReflection;\"",
        "r07 = class \"LReflection;\"", "$r02 = <java.lang.System: java.io.PrintStream out>",
        "virtualinvoke $r02.<java.io.PrintStream: void println(java.lang.Object)>(class \"LReflection;\")",
        "$r08 = newarray (java.lang.Class)[0]",
        "r10 = virtualinvoke r06.<java.lang.Class: java.lang.reflect.Constructor getConstructor(java.lang.Class[])>($r08)",
        "$r03 = <java.lang.System: java.io.PrintStream out>",
        "$r09 = virtualinvoke r10.<java.lang.reflect.Constructor: java.lang.String getName()>()",
        "virtualinvoke $r03.<java.io.PrintStream: void println(java.lang.String)>($r09)",
        "$r04 = <java.lang.System: java.io.PrintStream out>",
        "$r11 = virtualinvoke r07.<java.lang.Class: java.lang.reflect.Method[] getMethods()>()", "$i00 = lengthof $r11",
        "virtualinvoke $r04.<java.io.PrintStream: void println(int)>($i00)", "return")
        .collect(Collectors.toCollection(ArrayList::new));
    SootMethod method = Scene.v().getMethod("<Reflection: void checkReflection()>");
    assertJimpleStmts(method, expectedBodyStmts);
  }

  @Test
  public void test07() {
    loadClasses(resourcePath, "java6", "bin");
    List<String> expectedBodyStmts = Stream
        .of("r0 := @this: UncheckedCast\n" + "$r6 = newarray (java.lang.Integer)[4]\n"
            + "$r2 = staticinvoke <java.lang.Integer: java.lang.Integer valueOf(int)>(5)\n" + "$r6[0] = $r2\n"
            + "$r2 = staticinvoke <java.lang.Integer: java.lang.Integer valueOf(int)>(8)\n" + "$r6[1] = $r2\n"
            + "$r2 = staticinvoke <java.lang.Integer: java.lang.Integer valueOf(int)>(9)\n" + "$r6[2] = $r2\n"
            + "$r2 = staticinvoke <java.lang.Integer: java.lang.Integer valueOf(int)>(6)\n" + "$r6[3] = $r2\n"
            + "r7 = staticinvoke <java.util.Arrays: java.util.List asList(java.lang.Object[])>($r6)\n"
            + "$r1 = <java.lang.System: java.io.PrintStream out>\n"
            + "virtualinvoke $r1.<java.io.PrintStream: void println(java.lang.Object)>(r7)\n" + "return")
        .collect(Collectors.toCollection(ArrayList::new));
    SootMethod method = Scene.v().getMethod("<UncheckedCast: void uncheckedCastDisplay()>");
    assertJimpleStmts(method, expectedBodyStmts);
  }

  @Test
  public void test08() {
    loadClasses(resourcePath, "java11", "bin");
    List<String> expectedBodyStmts = Stream.of("r0 := @this: TypeInferenceLambda\n"
        + "r5 = staticinvoke <TypeInferenceLambda$lambda_lambda_0__1: java.util.function.BinaryOperator bootstrap$()>()\n"
        + "$r1 = staticinvoke <java.lang.Integer: java.lang.Integer valueOf(int)>(2)\n"
        + "$r2 = staticinvoke <java.lang.Integer: java.lang.Integer valueOf(int)>(3)\n"
        + "$r4 = interfaceinvoke r5.<java.util.function.BinaryOperator: java.lang.Object apply(java.lang.Object,java.lang.Object)>($r1, $r2)\n"
        + "$r1 = (java.lang.Integer) $r4\n" + "virtualinvoke $r1.<java.lang.Integer: int intValue()>()\n" + "return")
        .collect(Collectors.toCollection(ArrayList::new));

    SootMethod method = Scene.v().getMethod("<TypeInferenceLambda: void lambda()>");
    assertJimpleStmts(method, expectedBodyStmts);
  }

  @Test
  public void test09() {
    loadClasses(resourcePath, "java9", "bin");
    List<String> expectedBodyStmts
        = Stream.of("r0 := @this: AnonymousDiamondOperator\n" + "$r1 = new AnonymousDiamondOperator$1\n"
            + "specialinvoke $r1.<AnonymousDiamondOperator$1: void <init>(AnonymousDiamondOperator)>(r0)\n"
            + "$r3 = staticinvoke <java.lang.Integer: java.lang.Integer valueOf(int)>(22)\n"
            + "$r4 = staticinvoke <java.lang.Integer: java.lang.Integer valueOf(int)>(23)\n" + "$r2 = (MyClass) $r1\n"
            + "$r6 = virtualinvoke $r2.<MyClass: java.lang.Object add(java.lang.Object,java.lang.Object)>($r3, $r4)\n"
            + "$r3 = (java.lang.Integer) $r6\n" + "$i0 = virtualinvoke $r3.<java.lang.Integer: int intValue()>()\n"
            + "return $i0").collect(Collectors.toCollection(ArrayList::new));
    SootMethod method = Scene.v().getMethod("<AnonymousDiamondOperator: int innerClassDiamond()>");
    assertJimpleStmts(method, expectedBodyStmts);
  }

  @Test
  public void test10() {
    loadClasses(resourcePath, "java6", "bin");
    List<String> expectedBodyStmts = Stream.of("r0 := @this: DeclareEnum",
        "r2 = staticinvoke <DeclareEnum$Type: DeclareEnum$Type[] values()>()", "i0 = lengthof r2", "i1 = 0",
        "if i1 >= i0 goto return", "r1 = r2[i1]", "$r3 = <java.lang.System: java.io.PrintStream out>",
        "virtualinvoke $r3.<java.io.PrintStream: void println(java.lang.Object)>(r1)", "i1 = i1 + 1", "goto [?= (branch)]",
        "return").collect(Collectors.toCollection(ArrayList::new));
    SootMethod method = Scene.v().getMethod("<DeclareEnum: void declareEnum()>");
    assertJimpleStmts(method, expectedBodyStmts);
  }

  @Test
  public void test11() {
    loadClasses(resourcePath, "java6", "bin");
    List<String> expectedBodyStmts = Stream.of("r0 := @this: GenericTypeParamOnClass\n"
        + "$r1 = new GenericTypeParamOnClass$A\n"
        + "specialinvoke $r1.<GenericTypeParamOnClass$A: void <init>(GenericTypeParamOnClass)>(r0)\n"
        + "$r2 = staticinvoke <java.lang.Integer: java.lang.Integer valueOf(int)>(5)\n"
        + "staticinvoke <GenericTypeParamOnClass$A: void access$000(GenericTypeParamOnClass$A,java.lang.Object)>($r1, $r2)\n"
        + "$r4 = virtualinvoke $r1.<GenericTypeParamOnClass$A: java.lang.Object get()>()\n"
        + "$r2 = (java.lang.Integer) $r4\n" + "virtualinvoke $r2.<java.lang.Integer: int intValue()>()\n" + "return")
        .collect(Collectors.toCollection(ArrayList::new));
    SootMethod method = Scene.v().getMethod("<GenericTypeParamOnClass: void genericTypeParamOnClass()>");
    assertJimpleStmts(method, expectedBodyStmts);
  }
}
