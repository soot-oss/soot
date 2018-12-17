package soot.lambdaMetaFactory;

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
        = Scene.v().getMethod(methodSigFromComponents(testClass, "java.lang.Boolean lambda$main$0(java.lang.Integer)"));
    final SootMethod doSomething = Scene.v().getMethod(methodSigFromComponents(testClass, "void doSomething()"));

    final List<Edge> edgesFromTarget = Lists.newArrayList(cg.edgesOutOf(target));

    Assert.assertTrue("There should be an edge from main to the bootstrap method of the synthetic LambdaMetaFactory",
        edgesFromTarget.stream().anyMatch(e -> e.tgt().equals(bootstrap) && e.isStatic()));
    Assert.assertTrue("There should be an edge to the constructor of the LambdaMetaFactory in the bootstrap method",
        Lists.newArrayList(cg.edgesOutOf(bootstrap)).stream()
            .anyMatch(e -> e.tgt().equals(metaFactoryConstructor) && e.isSpecial()));
    Assert.assertTrue(
        "There should be an interface invocation on the synthetic LambdaMetaFactory's implementation of the functional interface (bridge) in the main method",
        edgesFromTarget.stream().anyMatch(e -> e.getTgt().equals(apply) && e.isInstance()));
    Assert.assertTrue(
        "There should be a static call to the lambda body implementation in the generated functional interface implementation of the synthetic LambdaMetaFactory",
        Lists.newArrayList(cg.edgesOutOf(apply)).stream()
            .anyMatch(e -> e.getTgt().equals(lambdaBody) && e.isStatic()));

    Assert.assertTrue("There should be a static call to the doSomething method in actual lambda body implementation", Lists
        .newArrayList(cg.edgesOutOf(lambdaBody)).stream().anyMatch(e -> e.getTgt().equals(doSomething) && e.isStatic()));
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
