package soot.lambdaMetaFactory;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2018 Manuel Benz
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

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import soot.Kind;
import soot.Scene;
import soot.SootMethod;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.testing.framework.AbstractTestingFramework;

/**
 * @author Manuel Benz created on 2018-12-17
 */
@Ignore

public abstract class AbstractLambdaMetaFactoryCGTest extends AbstractTestingFramework {

  private static final String TEST_METHOD_NAME = "main";
  private static final String TEST_METHOD_RET = "void";

  @Override
  protected void setupSoot() {

  }

  @Test
  public void lambdaNoCaptures() {
    String testClass = "soot.lambdaMetaFactory.LambdaNoCaptures";

    final SootMethod target = prepareTarget(methodSigFromComponents(testClass, TEST_METHOD_RET, TEST_METHOD_NAME), testClass,
        "java.util.function.Function");

    final CallGraph cg = Scene.v().getCallGraph();

    final String metaFactoryClass = getMetaFactoryNameLambda(testClass, TEST_METHOD_NAME);

    final SootMethod bootstrap
        = Scene.v().getMethod(methodSigFromComponents(metaFactoryClass, "java.util.function.Function", "bootstrap$"));
    final SootMethod metaFactoryConstructor
        = Scene.v().getMethod(methodSigFromComponents(metaFactoryClass, "void", "<init>"));
    final SootMethod apply
        = Scene.v().getMethod(methodSigFromComponents(metaFactoryClass, "java.lang.Object", "apply", "java.lang.Object"));
    final SootMethod lambdaBody
        = Scene.v().getMethod(methodSigFromComponents(testClass, "java.lang.String", "lambda$main$0", "java.lang.Integer"));
    final SootMethod staticCallee
        = Scene.v().getMethod(methodSigFromComponents(testClass, "void", "staticCallee", "java.lang.Integer"));

    final List<Edge> edgesFromTarget = newArrayList(cg.edgesOutOf(target));

    assertTrue("There should be an edge from main to the bootstrap method of the synthetic LambdaMetaFactory",
        edgesFromTarget.stream().anyMatch(e -> e.tgt().equals(bootstrap) && e.isStatic()));
    assertTrue("There should be an edge to the constructor of the LambdaMetaFactory in the bootstrap method",
        newArrayList(cg.edgesOutOf(bootstrap)).stream()
            .anyMatch(e -> e.tgt().equals(metaFactoryConstructor) && e.isSpecial()));
    assertTrue(
        "There should be an instance invocation on the synthetic LambdaMetaFactory's implementation of the functional interface in the main method",
        edgesFromTarget.stream().anyMatch(e -> e.getTgt().equals(apply) && e.kind() == Kind.INTERFACE));
    assertTrue(
        "There should be a static call to the lambda body implementation in the generated functional interface implementation of the synthetic LambdaMetaFactory",
        newArrayList(cg.edgesOutOf(apply)).stream().anyMatch(e -> e.getTgt().equals(lambdaBody) && e.isStatic()));

    assertTrue("There should be a static call to the staticCallee method in actual lambda body implementation",
        newArrayList(cg.edgesOutOf(lambdaBody)).stream().anyMatch(e -> e.getTgt().equals(staticCallee) && e.isStatic()));

    validateAllBodies(target.getDeclaringClass(), bootstrap.getDeclaringClass());
  }

  @Test
  public void lambdaWithCaptures() {
    String testClass = "soot.lambdaMetaFactory.LambdaWithCaptures";

    final SootMethod target
        = prepareTarget(methodSigFromComponents(testClass, TEST_METHOD_RET, TEST_METHOD_NAME), testClass);

    final CallGraph cg = Scene.v().getCallGraph();

    final String metaFactoryClass = getMetaFactoryNameLambda(testClass, TEST_METHOD_NAME);

    final SootMethod bootstrap = Scene.v().getMethod(methodSigFromComponents(metaFactoryClass, "java.util.function.Supplier",
        "bootstrap$", testClass, "java.lang.String"));
    final SootMethod metaFactoryConstructor
        = Scene.v().getMethod(methodSigFromComponents(metaFactoryClass, "void", "<init>", testClass, "java.lang.String"));
    final SootMethod get = Scene.v().getMethod(methodSigFromComponents(metaFactoryClass, "java.lang.Object", "get"));
    final SootMethod lambdaBody
        = Scene.v().getMethod(methodSigFromComponents(testClass, "java.lang.String", "lambda$main$0", "java.lang.String"));
    final SootMethod getString = Scene.v().getMethod(methodSigFromComponents(testClass, "java.lang.String", "getString"));

    final List<Edge> edgesFromTarget = newArrayList(cg.edgesOutOf(target));

    assertTrue("There should be an edge from main to the bootstrap method of the synthetic LambdaMetaFactory",
        edgesFromTarget.stream().anyMatch(e -> e.tgt().equals(bootstrap) && e.isStatic()));
    assertTrue("There should be an edge to the constructor of the LambdaMetaFactory in the bootstrap method",
        newArrayList(cg.edgesOutOf(bootstrap)).stream()
            .anyMatch(e -> e.tgt().equals(metaFactoryConstructor) && e.isSpecial()));
    assertTrue(
        "There should be an interface invocation on the synthetic LambdaMetaFactory's implementation of the functional interface in the main method",
        edgesFromTarget.stream().anyMatch(e -> e.getTgt().equals(get) && e.kind() == Kind.INTERFACE));
    assertTrue(
        "There should be a virtual call to the lambda body implementation in the generated functional interface implementation of the synthetic LambdaMetaFactory",
        newArrayList(cg.edgesOutOf(get)).stream().anyMatch(e -> e.getTgt().equals(lambdaBody) && e.isVirtual()));
    assertTrue("There should be a call to the getString method in actual lambda body implementation",
        newArrayList(cg.edgesOutOf(lambdaBody)).stream().anyMatch(e -> e.getTgt().equals(getString)));

    validateAllBodies(target.getDeclaringClass(), bootstrap.getDeclaringClass());
  }

  @Test
  public void markerInterfaces() {
    String testClass = "soot.lambdaMetaFactory.MarkerInterfaces";

    final SootMethod target
        = prepareTarget(methodSigFromComponents(testClass, TEST_METHOD_RET, TEST_METHOD_NAME), testClass);

    final CallGraph cg = Scene.v().getCallGraph();

    final String metaFactoryClass = getMetaFactoryNameLambda(testClass, TEST_METHOD_NAME);

    final SootMethod bootstrap = Scene.v()
        .getMethod(methodSigFromComponents(metaFactoryClass, "java.util.function.Supplier", "bootstrap$", testClass));
    final SootMethod metaFactoryConstructor
        = Scene.v().getMethod(methodSigFromComponents(metaFactoryClass, "void", "<init>", testClass));
    final SootMethod get = Scene.v().getMethod(methodSigFromComponents(metaFactoryClass, "java.lang.Object", "get"));
    final SootMethod lambdaBody
        = Scene.v().getMethod(methodSigFromComponents(testClass, "java.lang.Object", "lambda$main$0"));
    final SootMethod getString = Scene.v().getMethod(methodSigFromComponents(testClass, "java.lang.String", "getString"));

    final List<Edge> edgesFromTarget = newArrayList(cg.edgesOutOf(target));

    assertTrue("There should be an edge from main to the bootstrap method of the synthetic LambdaMetaFactory",
        edgesFromTarget.stream().anyMatch(e -> e.tgt().equals(bootstrap) && e.isStatic()));
    assertTrue("There should be an edge to the constructor of the LambdaMetaFactory in the bootstrap method",
        newArrayList(cg.edgesOutOf(bootstrap)).stream()
            .anyMatch(e -> e.tgt().equals(metaFactoryConstructor) && e.isSpecial()));
    assertTrue(
        "There should be an interface invocation on the synthetic LambdaMetaFactory's implementation of the functional interface in the main method",
        edgesFromTarget.stream().anyMatch(e -> e.getTgt().equals(get) && e.kind() == Kind.INTERFACE));
    assertTrue(
        "There should be a virtual call to the lambda body implementation in the generated functional interface implementation of the synthetic LambdaMetaFactory",
        newArrayList(cg.edgesOutOf(get)).stream().anyMatch(e -> e.getTgt().equals(lambdaBody) && e.isVirtual()));

    assertTrue("There should be a virtual call to the getString method in actual lambda body implementation",
        newArrayList(cg.edgesOutOf(lambdaBody)).stream().anyMatch(e -> e.getTgt().equals(getString) && e.isVirtual()));

    validateAllBodies(target.getDeclaringClass(), bootstrap.getDeclaringClass());
  }

  @Test
  public void staticMethodRef() {
    String testClass = "soot.lambdaMetaFactory.StaticMethodRef";

    final SootMethod target
        = prepareTarget(methodSigFromComponents(testClass, TEST_METHOD_RET, TEST_METHOD_NAME), testClass);

    final CallGraph cg = Scene.v().getCallGraph();

    final String referencedMethodName = "staticMethod";

    final String metaFactoryClass = getMetaFactoryNameMethodRef(testClass, referencedMethodName);

    final SootMethod bootstrap
        = Scene.v().getMethod(methodSigFromComponents(metaFactoryClass, "java.util.function.Supplier", "bootstrap$"));
    final SootMethod metaFactoryConstructor
        = Scene.v().getMethod(methodSigFromComponents(metaFactoryClass, "void", "<init>"));
    final SootMethod get = Scene.v().getMethod(methodSigFromComponents(metaFactoryClass, "java.lang.Object", "get"));
    final SootMethod referencedMethod = Scene.v().getMethod(methodSigFromComponents(testClass, "int", referencedMethodName));

    final List<Edge> edgesFromTarget = newArrayList(cg.edgesOutOf(target));

    assertTrue("There should be an edge from main to the bootstrap method of the synthetic LambdaMetaFactory",
        edgesFromTarget.stream().anyMatch(e -> e.tgt().equals(bootstrap) && e.isStatic()));
    assertTrue("There should be an edge to the constructor of the LambdaMetaFactory in the bootstrap method",
        newArrayList(cg.edgesOutOf(bootstrap)).stream()
            .anyMatch(e -> e.tgt().equals(metaFactoryConstructor) && e.isSpecial()));
    assertTrue(
        "There should be an interface invocation on the synthetic LambdaMetaFactory's implementation of the functional interface in the main method",
        edgesFromTarget.stream().anyMatch(e -> e.getTgt().equals(get) && e.kind() == Kind.INTERFACE));
    assertTrue("There should be a static call to the referenced method",
        newArrayList(cg.edgesOutOf(get)).stream().anyMatch(e -> e.getTgt().equals(referencedMethod) && e.isStatic()));

    validateAllBodies(target.getDeclaringClass(), bootstrap.getDeclaringClass());
  }

  @Test
  public void privateMethodRef() {
    String testClass = "soot.lambdaMetaFactory.PrivateMethodRef";

    final SootMethod target
        = prepareTarget(methodSigFromComponents(testClass, TEST_METHOD_RET, TEST_METHOD_NAME), testClass);

    final CallGraph cg = Scene.v().getCallGraph();

    final String referencedMethodName = "privateMethod";

    final String metaFactoryClass = getMetaFactoryNameMethodRef(testClass, referencedMethodName);

    final SootMethod bootstrap = Scene.v()
        .getMethod(methodSigFromComponents(metaFactoryClass, "java.util.function.Supplier", "bootstrap$", testClass));
    final SootMethod metaFactoryConstructor
        = Scene.v().getMethod(methodSigFromComponents(metaFactoryClass, "void", "<init>", testClass));
    final SootMethod get = Scene.v().getMethod(methodSigFromComponents(metaFactoryClass, "java.lang.Object", "get"));
    final SootMethod referencedMethod = Scene.v().getMethod(methodSigFromComponents(testClass, "int", referencedMethodName));

    final List<Edge> edgesFromTarget = newArrayList(cg.edgesOutOf(target));

    assertTrue("There should be an edge from main to the bootstrap method of the synthetic LambdaMetaFactory",
        edgesFromTarget.stream().anyMatch(e -> e.tgt().equals(bootstrap) && e.isStatic()));
    assertTrue("There should be an edge to the constructor of the LambdaMetaFactory in the bootstrap method",
        newArrayList(cg.edgesOutOf(bootstrap)).stream()
            .anyMatch(e -> e.tgt().equals(metaFactoryConstructor) && e.isSpecial()));
    assertTrue(
        "There should be an interface invocation on the synthetic LambdaMetaFactory's implementation of the functional interface in the main method",
        edgesFromTarget.stream().anyMatch(e -> e.getTgt().equals(get) && e.kind() == Kind.INTERFACE));
    assertTrue("There should be a virtual call to the referenced method",
        newArrayList(cg.edgesOutOf(get)).stream().anyMatch(e -> e.getTgt().equals(referencedMethod) && e.isVirtual()));

    validateAllBodies(target.getDeclaringClass(), bootstrap.getDeclaringClass());
  }

  @Test
  public void publicMethodRef() {
    String testClass = "soot.lambdaMetaFactory.PublicMethodRef";

    final SootMethod target
        = prepareTarget(methodSigFromComponents(testClass, TEST_METHOD_RET, TEST_METHOD_NAME), testClass);

    final CallGraph cg = Scene.v().getCallGraph();

    final String referencedMethodName = "publicMethod";

    final String metaFactoryClass = getMetaFactoryNameMethodRef(testClass, referencedMethodName);

    final SootMethod bootstrap = Scene.v()
        .getMethod(methodSigFromComponents(metaFactoryClass, "java.util.function.Supplier", "bootstrap$", testClass));
    final SootMethod metaFactoryConstructor
        = Scene.v().getMethod(methodSigFromComponents(metaFactoryClass, "void", "<init>", testClass));
    final SootMethod get = Scene.v().getMethod(methodSigFromComponents(metaFactoryClass, "java.lang.Object", "get"));
    final SootMethod referencedMethod = Scene.v().getMethod(methodSigFromComponents(testClass, "int", referencedMethodName));

    final List<Edge> edgesFromTarget = newArrayList(cg.edgesOutOf(target));

    assertTrue("There should be an edge from main to the bootstrap method of the synthetic LambdaMetaFactory",
        edgesFromTarget.stream().anyMatch(e -> e.tgt().equals(bootstrap) && e.isStatic()));
    assertTrue("There should be an edge to the constructor of the LambdaMetaFactory in the bootstrap method",
        newArrayList(cg.edgesOutOf(bootstrap)).stream()
            .anyMatch(e -> e.tgt().equals(metaFactoryConstructor) && e.isSpecial()));
    assertTrue(
        "There should be an interface invocation on the synthetic LambdaMetaFactory's implementation of the functional interface in the main method",
        edgesFromTarget.stream().anyMatch(e -> e.getTgt().equals(get) && e.kind() == Kind.INTERFACE));
    assertTrue("There should be a virtual call to the referenced method",
        newArrayList(cg.edgesOutOf(get)).stream().anyMatch(e -> e.getTgt().equals(referencedMethod) && e.isVirtual()));

    validateAllBodies(target.getDeclaringClass(), bootstrap.getDeclaringClass());
  }

  @Test
  public void constructorMethodRef() {
    String testClass = "soot.lambdaMetaFactory.ConstructorMethodRef";

    final SootMethod target
        = prepareTarget(methodSigFromComponents(testClass, TEST_METHOD_RET, TEST_METHOD_NAME), testClass);

    final CallGraph cg = Scene.v().getCallGraph();

    final String referencedMethodName = "<init>";

    final String metaFactoryClass = getMetaFactoryNameMethodRef(testClass, referencedMethodName);

    final SootMethod bootstrap
        = Scene.v().getMethod(methodSigFromComponents(metaFactoryClass, "java.util.function.Supplier", "bootstrap$"));
    final SootMethod metaFactoryConstructor
        = Scene.v().getMethod(methodSigFromComponents(metaFactoryClass, "void", "<init>"));
    final SootMethod get = Scene.v().getMethod(methodSigFromComponents(metaFactoryClass, "java.lang.Object", "get"));
    final SootMethod referencedMethod
        = Scene.v().getMethod(methodSigFromComponents(testClass, "void", referencedMethodName));

    final List<Edge> edgesFromTarget = newArrayList(cg.edgesOutOf(target));

    assertTrue("There should be an edge from main to the bootstrap method of the synthetic LambdaMetaFactory",
        edgesFromTarget.stream().anyMatch(e -> e.tgt().equals(bootstrap) && e.isStatic()));
    assertTrue("There should be an edge to the constructor of the LambdaMetaFactory in the bootstrap method",
        newArrayList(cg.edgesOutOf(bootstrap)).stream()
            .anyMatch(e -> e.tgt().equals(metaFactoryConstructor) && e.isSpecial()));
    assertTrue(
        "There should be an interface invocation on the synthetic LambdaMetaFactory's implementation of the functional interface  in the main method",
        edgesFromTarget.stream().anyMatch(e -> e.getTgt().equals(get) && e.kind() == Kind.INTERFACE));
    assertTrue("There should be a special call to the referenced method",
        newArrayList(cg.edgesOutOf(get)).stream().anyMatch(e -> e.getTgt().equals(referencedMethod) && e.isSpecial()));

    validateAllBodies(target.getDeclaringClass(), bootstrap.getDeclaringClass());
  }

  @Test
  public void inheritedMethodRef() {
    String testClass = "soot.lambdaMetaFactory.InheritedMethodRef";

    final SootMethod target
        = prepareTarget(methodSigFromComponents(testClass, TEST_METHOD_RET, TEST_METHOD_NAME), testClass);

    final CallGraph cg = Scene.v().getCallGraph();

    final String referencedMethodName = "superMethod";

    final String metaFactoryClass = getMetaFactoryNameLambda(testClass, TEST_METHOD_NAME);

    final SootMethod bootstrap = Scene.v()
        .getMethod(methodSigFromComponents(metaFactoryClass, "java.util.function.Supplier", "bootstrap$", testClass));
    final SootMethod metaFactoryConstructor
        = Scene.v().getMethod(methodSigFromComponents(metaFactoryClass, "void", "<init>", testClass));
    final SootMethod get = Scene.v().getMethod(methodSigFromComponents(metaFactoryClass, "java.lang.Object", "get"));
    final SootMethod referencedMethod
        = Scene.v().getMethod(methodSigFromComponents("soot.lambdaMetaFactory.Super", "int", referencedMethodName));
    final SootMethod lambdaBody
        = Scene.v().getMethod(methodSigFromComponents(testClass, "java.lang.Integer", "lambda$main$0"));

    final List<Edge> edgesFromTarget = newArrayList(cg.edgesOutOf(target));

    assertTrue("There should be an edge from main to the bootstrap method of the synthetic LambdaMetaFactory",
        edgesFromTarget.stream().anyMatch(e -> e.tgt().equals(bootstrap) && e.isStatic()));
    assertTrue("There should be an edge to the constructor of the LambdaMetaFactory in the bootstrap method",
        newArrayList(cg.edgesOutOf(bootstrap)).stream()
            .anyMatch(e -> e.tgt().equals(metaFactoryConstructor) && e.isSpecial()));
    assertTrue(
        "There should be an interface invocation on the synthetic LambdaMetaFactory's implementation of the functional interface in the main method",
        edgesFromTarget.stream().anyMatch(e -> e.getTgt().equals(get) && e.kind() == Kind.INTERFACE));
    // Call is from <soot.lambdaMetaFactory.InheritedMethodRef$lambda_main_0__1
    // to <soot.lambdaMetaFactory.InheritedMethodRef: java.lang.Integer lambda$main$0()>
    // As such, it needs to be a virtual call.
    assertTrue(
        "There should be a virtual call to the lambda body implementation in the generated functional interface implementation of the synthetic LambdaMetaFactory",
        newArrayList(cg.edgesOutOf(get)).stream().anyMatch(e -> e.getTgt().equals(lambdaBody) && e.isVirtual()));
    assertTrue("There should be a special call to the referenced method", newArrayList(cg.edgesOutOf(lambdaBody)).stream()
        .anyMatch(e -> e.getTgt().equals(referencedMethod) && e.isSpecial()));

    validateAllBodies(target.getDeclaringClass(), bootstrap.getDeclaringClass());
  }

  @Test
  public void methodRefWithParameters() {
    String testClass = "soot.lambdaMetaFactory.MethodRefWithParameters";

    final SootMethod target
        = prepareTarget(methodSigFromComponents(testClass, TEST_METHOD_RET, TEST_METHOD_NAME), testClass);

    final CallGraph cg = Scene.v().getCallGraph();

    final String referencedMethodName = "staticWithCaptures";

    final String metaFactoryClass = getMetaFactoryNameMethodRef(testClass, referencedMethodName);

    final SootMethod bootstrap
        = Scene.v().getMethod(methodSigFromComponents(metaFactoryClass, "java.util.function.BiFunction", "bootstrap$"));
    final SootMethod metaFactoryConstructor
        = Scene.v().getMethod(methodSigFromComponents(metaFactoryClass, "void", "<init>"));
    final SootMethod apply = Scene.v().getMethod(
        methodSigFromComponents(metaFactoryClass, "java.lang.Object", "apply", "java.lang.Object", "java.lang.Object"));
    final SootMethod referencedMethod
        = Scene.v().getMethod(methodSigFromComponents(testClass, "int", referencedMethodName, "int", "java.lang.Integer"));

    final List<Edge> edgesFromTarget = newArrayList(cg.edgesOutOf(target));

    assertTrue("There should be an edge from main to the bootstrap method of the synthetic LambdaMetaFactory",
        edgesFromTarget.stream().anyMatch(e -> e.tgt().equals(bootstrap) && e.isStatic()));
    assertTrue("There should be an edge to the constructor of the LambdaMetaFactory in the bootstrap method",
        newArrayList(cg.edgesOutOf(bootstrap)).stream()
            .anyMatch(e -> e.tgt().equals(metaFactoryConstructor) && e.isSpecial()));
    assertTrue("There should be an interface invocation on the referenced method",
        edgesFromTarget.stream().anyMatch(e -> e.getTgt().equals(apply) && e.kind() == Kind.INTERFACE));
    assertTrue("There should be a static call to the referenced method",
        newArrayList(cg.edgesOutOf(apply)).stream().anyMatch(e -> e.getTgt().equals(referencedMethod) && e.isStatic()));

    validateAllBodies(target.getDeclaringClass(), bootstrap.getDeclaringClass());
  }

  private String getMetaFactoryNameMethodRef(String testClass, String referencedMethod) {
    return testClass + "$" + referencedMethod.replace("<", "").replace(">", "") + "__1";
  }

  private String getMetaFactoryNameLambda(String testClass, String testMethodName) {
    return testClass + "$lambda_" + testMethodName + "_0__1";
  }

}
