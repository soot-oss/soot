package soot.portedtest;

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

public class RedundantJimpleStatementsTest {
    /**
     * The subsequent test cases have been adapted from SootUp (https://github.com/soot-oss/SootUp/pull/472).
     * The issue regarding the always-false condition was initially documented in https://github.com/soot-oss/soot/pull/1834.
     */

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
        Scene.v().loadNecessaryClasses();
    }

    private List<String> bodyStmtsAsStrings(Body body) {
        List<String> contentList = new ArrayList<>();
        for (Unit stmt : body.getUnits()) {
            contentList.add(stmt.toString());
        }
        return contentList;
    }

    private void assertJimpleStmts(SootMethod method, List<String> expectedStmts) {
        Body body = method.retrieveActiveBody();
        assertNotNull(body);
        List<String> actualStmts = bodyStmtsAsStrings(body);

        assertEquals(expectedStmts, actualStmts);
    }

    @Test
    public void test01() {
        loadClasses(resourcePath, "java8", "bin");
        SootMethod method = Scene.v().getMethod("<MethodAcceptingLamExpr: void lambdaAsParamMethod()>");
        List<String> expectedBodyStmts = Stream.of(
                "r6 := @this: MethodAcceptingLamExpr",
                "r0 = staticinvoke <MethodAcceptingLamExpr$lambda_lambdaAsParamMethod_0__1: Percentage bootstrap$()>()",
                "$r2 = <java.lang.System: java.io.PrintStream out>",
                "$r1 = new java.lang.StringBuilder",
                "specialinvoke $r1.<java.lang.StringBuilder: void <init>()>()",
                "$r3 = virtualinvoke $r1.<java.lang.StringBuilder: java.lang.StringBuilder append(java.lang.String)>(\"Percentage : \")",
                "$d0 = interfaceinvoke r0.<Percentage: double calcPercentage(double)>(45.0)",
                "$r4 = virtualinvoke $r3.<java.lang.StringBuilder: java.lang.StringBuilder append(double)>($d0)",
                "$r5 = virtualinvoke $r4.<java.lang.StringBuilder: java.lang.String toString()>()",
                "virtualinvoke $r2.<java.io.PrintStream: void println(java.lang.String)>($r5)",
                "return").collect(Collectors.toCollection(ArrayList::new));
        assertJimpleStmts(method, expectedBodyStmts);
    }

    @Test
    public void test02() {
        loadClasses(resourcePath, "java9", "bin");
        List<String> expectedBodyStmts = Stream.of(
                "r1 = dynamicinvoke \"makeConcatWithConstants\" <java.lang.String (java.lang.String)>(\"This test\") <java.lang.invoke.StringConcatFactory: java.lang.invoke.CallSite makeConcatWithConstants(java.lang.invoke.MethodHandles$Lookup,java.lang.String,java.lang.invoke.MethodType,java.lang.String,java.lang.Object[])>(\"\\u0001 is cool\")",
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
                "r5 := @this: TypeInferenceLambda",
                "r0 = staticinvoke <TypeInferenceLambda$lambda_lambda_0__1: java.util.function.BinaryOperator bootstrap$()>()",
                "$r2 = staticinvoke <java.lang.Integer: java.lang.Integer valueOf(int)>(2)",
                "$r1 = staticinvoke <java.lang.Integer: java.lang.Integer valueOf(int)>(3)",
                "$r3 = interfaceinvoke r0.<java.util.function.BinaryOperator: java.lang.Object apply(java.lang.Object,java.lang.Object)>($r2, $r1)",
                "$r4 = (java.lang.Integer) $r3",
                "virtualinvoke $r4.<java.lang.Integer: int intValue()>()",
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
                "r12 := @this: GenTypeParam",
                "$r0 = new java.util.ArrayList",
                "specialinvoke $r0.<java.util.ArrayList: void <init>(int)>(3)",
                "$r1 = newarray (java.lang.Integer)[3]",
                "$r2 = staticinvoke <java.lang.Integer: java.lang.Integer valueOf(int)>(1)",
                "$r1[0] = $r2",
                "$r3 = staticinvoke <java.lang.Integer: java.lang.Integer valueOf(int)>(2)",
                "$r1[1] = $r3",
                "$r4 = staticinvoke <java.lang.Integer: java.lang.Integer valueOf(int)>(3)",
                "$r1[2] = $r4",
                "r5 = staticinvoke <java.util.Arrays: java.util.List asList(java.lang.Object[])>($r1)",
                "$r6 = new GenTypeParam",
                "specialinvoke $r6.<GenTypeParam: void <init>()>()",
                "virtualinvoke $r6.<GenTypeParam: void copy(java.util.List,java.util.List)>($r0, r5)",
                "$r7 = <java.lang.System: java.io.PrintStream out>",
                "$r10 = staticinvoke <java.lang.Integer: java.lang.Integer valueOf(int)>(2)",
                "$r9 = staticinvoke <java.lang.Integer: java.lang.Integer valueOf(int)>(8)",
                "$r8 = staticinvoke <java.lang.Integer: java.lang.Integer valueOf(int)>(3)",
                "$r11 = virtualinvoke $r6.<GenTypeParam: java.lang.Number largestNum(java.lang.Number,java.lang.Number,java.lang.Number)>($r10, $r9, $r8)",
                "virtualinvoke $r7.<java.io.PrintStream: void println(java.lang.Object)>($r11)",
                "return").collect(Collectors.toCollection(ArrayList::new));
        SootMethod method = Scene.v().getMethod("<GenTypeParam: void geneTypeParamDisplay()>");
        assertJimpleStmts(method, expectedBodyStmts);
    }

    @Test
    @Ignore("The variable names r10 and r11 may be used in swapped order")
    public void test06() {
        loadClasses(resourcePath, "java6", "bin");
        List<String> expectedBodyStmts = Stream.of(
                "r9 := @this: Reflection",
                "$r0 = new Reflection",
                "specialinvoke $r0.<Reflection: void <init>()>()",
                "r1 = class \"LReflection;\"",
                "r11 = class \"LReflection;\"",
                "r10 = class \"LReflection;\"",
                "$r2 = <java.lang.System: java.io.PrintStream out>",
                "virtualinvoke $r2.<java.io.PrintStream: void println(java.lang.Object)>(class \"LReflection;\")",
                "$r3 = newarray (java.lang.Class)[0]",
                "r4 = virtualinvoke r11.<java.lang.Class: java.lang.reflect.Constructor getConstructor(java.lang.Class[])>($r3)",
                "$r5 = <java.lang.System: java.io.PrintStream out>",
                "$r6 = virtualinvoke r4.<java.lang.reflect.Constructor: java.lang.String getName()>()",
                "virtualinvoke $r5.<java.io.PrintStream: void println(java.lang.String)>($r6)",
                "$r7 = <java.lang.System: java.io.PrintStream out>",
                "$r8 = virtualinvoke r10.<java.lang.Class: java.lang.reflect.Method[] getMethods()>()",
                "$i0 = lengthof $r8",
                "virtualinvoke $r7.<java.io.PrintStream: void println(int)>($i0)",
                "return").collect(Collectors.toCollection(ArrayList::new));
        SootMethod method = Scene.v().getMethod("<Reflection: void checkReflection()>");
        assertJimpleStmts(method, expectedBodyStmts);
    }

    @Test
    public void test07() {
        loadClasses(resourcePath, "java6", "bin");
        List<String> expectedBodyStmts = Stream.of(
                "r7 := @this: UncheckedCast",
                "$r0 = newarray (java.lang.Integer)[4]",
                "$r1 = staticinvoke <java.lang.Integer: java.lang.Integer valueOf(int)>(5)",
                "$r0[0] = $r1",
                "$r2 = staticinvoke <java.lang.Integer: java.lang.Integer valueOf(int)>(8)",
                "$r0[1] = $r2",
                "$r3 = staticinvoke <java.lang.Integer: java.lang.Integer valueOf(int)>(9)",
                "$r0[2] = $r3",
                "$r4 = staticinvoke <java.lang.Integer: java.lang.Integer valueOf(int)>(6)",
                "$r0[3] = $r4",
                "r5 = staticinvoke <java.util.Arrays: java.util.List asList(java.lang.Object[])>($r0)",
                "$r6 = <java.lang.System: java.io.PrintStream out>",
                "virtualinvoke $r6.<java.io.PrintStream: void println(java.lang.Object)>(r5)",
                "return").collect(Collectors.toCollection(ArrayList::new));
        SootMethod method = Scene.v().getMethod("<UncheckedCast: void uncheckedCastDisplay()>");
        assertJimpleStmts(method, expectedBodyStmts);
    }

    @Test
    public void test08() {
        loadClasses(resourcePath, "java11", "bin");
        List<String> expectedBodyStmts = Stream.of(
                "r5 := @this: TypeInferenceLambda",
                "r0 = staticinvoke <TypeInferenceLambda$lambda_lambda_0__1: java.util.function.BinaryOperator bootstrap$()>()",
                "$r2 = staticinvoke <java.lang.Integer: java.lang.Integer valueOf(int)>(2)",
                "$r1 = staticinvoke <java.lang.Integer: java.lang.Integer valueOf(int)>(3)",
                "$r3 = interfaceinvoke r0.<java.util.function.BinaryOperator: java.lang.Object apply(java.lang.Object,java.lang.Object)>($r2, $r1)",
                "$r4 = (java.lang.Integer) $r3",
                "virtualinvoke $r4.<java.lang.Integer: int intValue()>()",
                "return").collect(Collectors.toCollection(ArrayList::new));

        SootMethod method = Scene.v().getMethod("<TypeInferenceLambda: void lambda()>");
        assertJimpleStmts(method, expectedBodyStmts);
    }


    @Test
    public void test09() {
        loadClasses(resourcePath, "java9", "bin");
        List<String> expectedBodyStmts = Stream.of(
                "r1 := @this: AnonymousDiamondOperator",
                        "$r6 = new AnonymousDiamondOperator$1",
                        "specialinvoke $r6.<AnonymousDiamondOperator$1: void <init>(AnonymousDiamondOperator)>(r1)",
                        "$r3 = staticinvoke <java.lang.Integer: java.lang.Integer valueOf(int)>(22)",
                        "$r2 = staticinvoke <java.lang.Integer: java.lang.Integer valueOf(int)>(23)",
                        "$r7 = (MyClass) $r6",
                        "$r4 = virtualinvoke $r7.<MyClass: java.lang.Object add(java.lang.Object,java.lang.Object)>($r3, $r2)",
                        "r5 = (java.lang.Integer) $r4",
                        "$i0 = virtualinvoke r5.<java.lang.Integer: int intValue()>()",
                        "return $i0").collect(Collectors.toCollection(ArrayList::new));
        SootMethod method = Scene.v().getMethod("<AnonymousDiamondOperator: int innerClassDiamond()>");
        assertJimpleStmts(method, expectedBodyStmts);
    }

    @Test
    public void test10() {
        loadClasses(resourcePath, "java6", "bin");
        List<String> expectedBodyStmts = Stream.of(
                "r3 := @this: DeclareEnum",
                        "r0 = staticinvoke <DeclareEnum$Type: DeclareEnum$Type[] values()>()",
                        "i0 = lengthof r0",
                        "i1 = 0",
                        "if i1 >= i0 goto return",
                        "r1 = r0[i1]",
                        "$r2 = <java.lang.System: java.io.PrintStream out>",
                        "virtualinvoke $r2.<java.io.PrintStream: void println(java.lang.Object)>(r1)",
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
                "r1 := @this: GenericTypeParamOnClass",
                        "$r0 = new GenericTypeParamOnClass$A",
                        "specialinvoke $r0.<GenericTypeParamOnClass$A: void <init>(GenericTypeParamOnClass)>(r1)",
                        "$r2 = staticinvoke <java.lang.Integer: java.lang.Integer valueOf(int)>(5)",
                        "staticinvoke <GenericTypeParamOnClass$A: void access$000(GenericTypeParamOnClass$A,java.lang.Object)>($r0, $r2)",
                        "$r3 = virtualinvoke $r0.<GenericTypeParamOnClass$A: java.lang.Object get()>()",
                        "$r4 = (java.lang.Integer) $r3",
                        "virtualinvoke $r4.<java.lang.Integer: int intValue()>()",
                        "return").collect(Collectors.toCollection(ArrayList::new));
        SootMethod method = Scene.v().getMethod("<GenericTypeParamOnClass: void genericTypeParamOnClass()>");
        assertJimpleStmts(method, expectedBodyStmts);
    }
}
