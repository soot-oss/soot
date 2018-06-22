package soot.testing.framework;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import soot.SootMethod;

/**
 * @author Manuel Benz created on 22.06.18
 */
public class HelloTestingFrameworkTest extends AbstractTestingFramework {

  private static final String TEST_TARGET_CLASS = "soot.testing.framework.HelloTestingFrameworkTarget";

  @Test
  public void findsTarget() {
    final SootMethod sootMethod = prepareTarget("<" + TEST_TARGET_CLASS + ": void helloWorld()>");
    Assert.assertNotNull("Could not find target method. System test setup seems to be incorrect.", sootMethod);
  }

  @Override
  protected List<String> getIncludes() {
    return Collections.singletonList(TEST_TARGET_CLASS);
  }
}
