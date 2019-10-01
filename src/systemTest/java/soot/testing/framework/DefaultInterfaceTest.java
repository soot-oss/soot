package soot.testing.framework;

import org.junit.Test;
import soot.Body;
import soot.Scene;
import soot.SootMethod;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.ReachableMethods;

public class DefaultInterfaceTest extends AbstractTestingFramework {

  @Test
  public void interfaceTest() {
    String testClass = "soot.interfaceTesting.TestMain";

    final SootMethod target =
        prepareTarget(
            methodSigFromComponents(testClass, "void", "main"),
            testClass,
            "soot.interfaceTesting.Default");

		SootMethod defaultMethod = Scene.v().getMethod("<soot.interfaceTesting.Default: void target()>");

		Body body = defaultMethod.retrieveActiveBody();

		final CallGraph cg = Scene.v().getCallGraph();

    final ReachableMethods methods = Scene.v().getReachableMethods();
  }
}
