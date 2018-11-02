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

import com.google.common.collect.Lists;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import soot.Scene;
import soot.SootMethod;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.testing.framework.AbstractTestingFramework;

/**
 * @author Manuel Benz created on 31.10.18
 */
public class LambdaMetaFactorySPARKTest extends AbstractTestingFramework {

  private static final String TEST_METHOD_SUBSIG = "void main()";

  @Test
  public void lambda1() {
    String testClass = "soot.lambdaMetaFactory.jcg.Lambda1";

    final SootMethod target
        = prepareTarget(methodSigFromComponents(testClass, TEST_METHOD_SUBSIG), testClass, "java.util.function.Function");

    final CallGraph cg = Scene.v().getCallGraph();

    final SootMethod bootstrap
        = Scene.v().getMethod("<soot.dummy.lambda$main$0$1: java.util.function.Function bootstrap$()>");
    final SootMethod metaFactoryConstructor = Scene.v().getMethod("<soot.dummy.lambda$main$0$1: void <init>()>");
    final SootMethod genericApply
        = Scene.v().getMethod("<soot.dummy.lambda$main$0$1: java.lang.Object apply(java.lang.Object)>");
    final SootMethod concreteApply
        = Scene.v().getMethod("<soot.dummy.lambda$main$0$1: java.lang.Boolean apply(java.lang.Integer)>");
    final SootMethod lambdaBody
        = Scene.v().getMethod(methodSigFromComponents(testClass, "java.lang.Boolean lambda$main$0(java.lang.Integer)"));
    final SootMethod doSomething = Scene.v().getMethod("<soot.lambdaMetaFactory.jcg.Lambda1: void doSomething()>");

    final List<Edge> edgesFromTarget = Lists.newArrayList(cg.edgesOutOf(target));

    Assert.assertTrue("There should be an edge from main to the bootstrap method of the synthetic LambdaMetaFactory",
        edgesFromTarget.stream().anyMatch(e -> e.tgt().equals(bootstrap) && e.isStatic()));
    Assert.assertTrue("There should be an edge to the constructor of the LambdaMetaFactory in the bootstrap method",
        Lists.newArrayList(cg.edgesOutOf(bootstrap)).stream()
            .anyMatch(e -> e.tgt().equals(metaFactoryConstructor) && e.isSpecial()));
    Assert.assertTrue(
        "There should be an interface invocation on the synthetic LambdaMetaFactory's implementation of the functional interface (bridge) in the main method",
        edgesFromTarget.stream().anyMatch(e -> e.getTgt().equals(genericApply) && e.isInstance()));
    Assert.assertTrue(
        "There should be a virtual invocation of the synthetic LambdaMetaFactory's implementation of the functional interface in the bridge method",
        Lists.newArrayList(cg.edgesOutOf(genericApply)).stream()
            .anyMatch(e -> e.tgt().equals(concreteApply) && e.isVirtual()));

    Assert.assertTrue(
        "There should be a static call to the lambda body implementation in the generated functional interface implementation of the synthetic LambdaMetaFactory",
        Lists.newArrayList(cg.edgesOutOf(concreteApply)).stream()
            .anyMatch(e -> e.getTgt().equals(lambdaBody) && e.isStatic()));

    Assert.assertTrue("There should be a static call to the doSomething method in actual lambda body implementation", Lists
        .newArrayList(cg.edgesOutOf(lambdaBody)).stream().anyMatch(e -> e.getTgt().equals(doSomething) && e.isStatic()));
  }
}