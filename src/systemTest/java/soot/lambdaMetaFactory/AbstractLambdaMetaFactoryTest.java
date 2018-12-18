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

import org.junit.Test;

import soot.Scene;
import soot.SootMethod;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.testing.framework.AbstractTestingFramework;

/**
 * @author Manuel Benz created on 2018-12-17
 */
public abstract class AbstractLambdaMetaFactoryTest extends AbstractTestingFramework {

  private static final String TEST_METHOD_NAME = "main";
  private static final String TEST_METHOD_SUBSIG = String.format("void %s()", TEST_METHOD_NAME);

  @Override
  protected void setupSoot() {

  }

  @Test
  public void lambda1() {
    String testClass = "soot.lambdaMetaFactory.Lambda1";

    final SootMethod target
        = prepareTarget(methodSigFromComponents(testClass, TEST_METHOD_SUBSIG), testClass, "java.util.function.Function");

    final CallGraph cg = Scene.v().getCallGraph();

    final String metaFactoryClass = getMetaFactoryName(testClass, TEST_METHOD_NAME);

    final SootMethod bootstrap
        = Scene.v().getMethod(methodSigFromComponents(metaFactoryClass, "java.util.function.Function bootstrap$()"));
    final SootMethod metaFactoryConstructor
        = Scene.v().getMethod(methodSigFromComponents(metaFactoryClass, "void <init>()"));
    final SootMethod apply
        = Scene.v().getMethod(methodSigFromComponents(metaFactoryClass, "java.lang.Object apply(java.lang.Object)"));
    final SootMethod lambdaBody
        = Scene.v().getMethod(methodSigFromComponents(testClass, "java.lang.String lambda$main$0(java.lang.Integer)"));
    final SootMethod doSomething
        = Scene.v().getMethod(methodSigFromComponents(testClass, "void staticCallee(java.lang.Integer)"));

    final List<Edge> edgesFromTarget = newArrayList(cg.edgesOutOf(target));

    assertTrue("There should be an edge from main to the bootstrap method of the synthetic LambdaMetaFactory",
        edgesFromTarget.stream().anyMatch(e -> e.tgt().equals(bootstrap) && e.isStatic()));
    assertTrue("There should be an edge to the constructor of the LambdaMetaFactory in the bootstrap method",
        newArrayList(cg.edgesOutOf(bootstrap)).stream()
            .anyMatch(e -> e.tgt().equals(metaFactoryConstructor) && e.isSpecial()));
    assertTrue(
        "There should be an interface invocation on the synthetic LambdaMetaFactory's implementation of the functional interface (bridge) in the main method",
        edgesFromTarget.stream().anyMatch(e -> e.getTgt().equals(apply) && e.isInstance()));
    assertTrue(
        "There should be a static call to the lambda body implementation in the generated functional interface implementation of the synthetic LambdaMetaFactory",
        newArrayList(cg.edgesOutOf(apply)).stream().anyMatch(e -> e.getTgt().equals(lambdaBody) && e.isStatic()));

    assertTrue("There should be a static call to the doSomething method in actual lambda body implementation",
        newArrayList(cg.edgesOutOf(lambdaBody)).stream().anyMatch(e -> e.getTgt().equals(doSomething) && e.isStatic()));
  }

  private String getMetaFactoryName(String testClass, String testMethodName) {
    return testClass + "$lambda_" + testMethodName + "_0__1";
  }

  @Test
  public void parameterBoxing() {
    String testClass = "soot.lambdaMetaFactory.Adapt";

    final SootMethod target = prepareTarget(methodSigFromComponents(testClass, "void parameterBoxingTarget()"));

    final CallGraph cg = Scene.v().getCallGraph();

  }
}
