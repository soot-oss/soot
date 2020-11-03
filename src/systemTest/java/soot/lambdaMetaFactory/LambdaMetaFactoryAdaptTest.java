package soot.lambdaMetaFactory;

import org.junit.Test;
import org.powermock.core.classloader.annotations.PowerMockIgnore;

import soot.SootMethod;
import soot.testing.framework.AbstractTestingFramework;

/**
 * @author Manuel Benz created on 2018-12-18
 */
@PowerMockIgnore({ "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*" })
public class LambdaMetaFactoryAdaptTest extends AbstractTestingFramework {

  @Test
  public void parameterBoxing() {
    String testClass = "soot.lambdaMetaFactory.Adapt";

    final SootMethod target = prepareTarget(methodSigFromComponents(testClass, "void", "parameterBoxingTarget"), testClass);

    // TODO more fine-grained testing

    validateAllBodies(target.getDeclaringClass());
  }

  @Test
  public void parameterWidening() {
    String testClass = "soot.lambdaMetaFactory.Adapt";

    final SootMethod target = prepareTarget(methodSigFromComponents(testClass, "void", "parameterWidening"), testClass);

    // TODO more fine-grained testing

    validateAllBodies(target.getDeclaringClass());
  }

  @Test
  public void returnBoxing() {
    String testClass = "soot.lambdaMetaFactory.Adapt";

    final SootMethod target = prepareTarget(methodSigFromComponents(testClass, "void", "returnBoxing"), testClass);

    // TODO more fine-grained testing

    validateAllBodies(target.getDeclaringClass());
  }

  @Test
  public void returnWidening() {
    String testClass = "soot.lambdaMetaFactory.Adapt";

    final SootMethod target = prepareTarget(methodSigFromComponents(testClass, "void", "returnWidening"), testClass);

    // TODO more fine-grained testing

    validateAllBodies(target.getDeclaringClass());
  }
}
