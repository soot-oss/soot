package soot.testing.framework;

import org.junit.Test;

import java.util.Iterator;

import org.junit.Assert;
import soot.Body;
import soot.Scene;
import soot.SootMethod;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
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

	  Assert.assertEquals(defaultMethod.getName(), "target");
	  Assert.assertNotNull(defaultMethod);
	  Assert.assertNotNull(methods.contains(defaultMethod));
    
  }
  
  @Test
  public void interfaceWithSameSignatureTest() {
	  String testClass = "soot.interfaceTesting.TestInterfaceSameSignature";

	  final SootMethod target =
			  prepareTarget(
					  methodSigFromComponents(testClass, "void", "main"),
					  testClass,
					  "soot.interfaceTesting.Read", "soot.interfaceTesting.Write");

	  SootMethod mainMethod = Scene.v().getMethod("<soot.interfaceTesting.TestInterfaceSameSignature: void print()>");
	  SootMethod readInterface = Scene.v().getMethod("<soot.interfaceTesting.Read: void print()>");
	  SootMethod writeInterface = Scene.v().getMethod("<soot.interfaceTesting.Write: void print()>");	  

	  final CallGraph cg = Scene.v().getCallGraph();

	  final ReachableMethods methods = Scene.v().getReachableMethods();
	  
	  Assert.assertNotNull(mainMethod);
	  Assert.assertNotNull(readInterface);
	  Assert.assertNotNull(writeInterface);
	  
	  Assert.assertEquals(mainMethod.getName(), "print");
	  Assert.assertEquals(readInterface.getName(), "print");
	  Assert.assertEquals(writeInterface.getName(), "print");
	  
	  Assert.assertNotNull(methods.contains(mainMethod));
	  Assert.assertNotNull(methods.contains(readInterface));
	  Assert.assertNotNull(methods.contains(writeInterface));
	  
  }
  
  @Test
  public void classInterfaceWithSameSignatureTest() {
	  String testClass = "soot.interfaceTesting.TestClassInterfaceSameSignature";

	  final SootMethod target =
			  prepareTarget(
					  methodSigFromComponents(testClass, "void", "main"),
					  testClass,
					  "soot.interfaceTesting.helloWorld");

	  SootMethod mainMethod = Scene.v().getMethod("<soot.interfaceTesting.TestClassInterfaceSameSignature: void print()>");	  	  

	  final CallGraph cg = Scene.v().getCallGraph();

	  final ReachableMethods methods = Scene.v().getReachableMethods();
	  
	  Assert.assertNotNull(mainMethod);
	  
	  Assert.assertEquals(mainMethod.getName(), "print");
	  
	  Assert.assertNotNull(methods.contains(mainMethod));
	  
	  
  }
  
  @Test
  public void superClassInterfaceWithSameSignatureTest() {
	  String testClass = "soot.interfaceTesting.TestSuperClassInterfaceSameSignature";

	  final SootMethod target =
			  prepareTarget(
					  methodSigFromComponents(testClass, "void", "main"),
					  testClass,
					  "soot.interfaceTesting.printInterface");

	  SootMethod mainMethod = Scene.v().getMethod("<soot.interfaceTesting.TestSuperClass: void print()>");	  	  

	  final CallGraph cg = Scene.v().getCallGraph();

	  final ReachableMethods methods = Scene.v().getReachableMethods();
	  
	  Assert.assertNotNull(mainMethod);
	  
	  Assert.assertEquals(mainMethod.getName(), "print");
	  
	  Assert.assertNotNull(methods.contains(mainMethod));
	  
	  
  }
}
