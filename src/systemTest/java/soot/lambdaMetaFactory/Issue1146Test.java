package soot.lambdaMetaFactory;

import org.junit.Test;

import soot.SootMethod;
import soot.options.Options;
import soot.testing.framework.AbstractTestingFramework;

/**
 * Reproduces issue 1146: https://github.com/Sable/soot/issues/1146
 *
 * @author Manuel Benz at 2019-05-14
 */
public class Issue1146Test extends AbstractTestingFramework {

  @Test
  public void getVertragTest() {
    String testClass = "soot.lambdaMetaFactory.Issue1146";

    final SootMethod target = prepareTarget(
        methodSigFromComponents(testClass, "soot.lambdaMetaFactory.Issue1146$Vertrag", "getVertrag", "java.lang.String"),
        testClass, "java.util.function.Function");
    // if no exception is thrown, everything is working as intended
  }

  @Test
  public void getVertrag2Test() {
    String testClass = "soot.lambdaMetaFactory.Issue1146";

    final SootMethod target = prepareTarget(
        methodSigFromComponents(testClass, "soot.lambdaMetaFactory.Issue1146$Vertrag", "getVertrag2", "java.lang.String"),
        testClass, "java.util.function.Function");
    // if no exception is thrown, everything is working as intended
  }

  @Override
  protected void setupSoot() {
    super.setupSoot();
    // set classpath to something so that the rt.jar is not loaded as it would be when using the default cp
    Options.v().set_soot_classpath(SYSTEMTEST_TARGET_CLASSES_DIR);
  }
}
