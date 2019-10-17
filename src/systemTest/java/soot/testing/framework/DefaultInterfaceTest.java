package soot.testing.framework;

import org.junit.Test;

import java.util.Iterator;
import java.util.Set;

import org.junit.Assert;
import soot.Body;
import soot.G;
import soot.G.Global;
import soot.MethodOrMethodContext;
import soot.RefType;
import soot.SootClass;
import soot.Scene;
import soot.SootMethod;
import soot.SootMethodRefImpl;
import soot.Unit;
import soot.UnitPatchingChain;
import soot.jimple.internal.JInvokeStmt;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.VirtualCalls;
import soot.jimple.toolkits.callgraph.Edge;
import soot.jimple.toolkits.callgraph.ReachableMethods;

public class DefaultInterfaceTest extends AbstractTestingFramework {

  @Test
  public void interfaceTest() {
	  
	  String testClass = "soot.interfaceTesting.TestSimpleDefault";
	  String defaultClass = "soot.interfaceTesting.Default";

	  final SootMethod target =
			  prepareTarget(
					  methodSigFromComponents(testClass, "void", "main"),
					  testClass,
					  "soot.interfaceTesting.Default");

	  SootMethod defaultMethod = Scene.v().getMethod("<soot.interfaceTesting.Default: void target()>"); 	  

	  Body body = target.retrieveActiveBody();
	  
	  SootMethod targetMethod = getSootMethodRef(body.getUnits(), "void target()");
	  
	  SootMethod resolvedMethod = G.v().soot_jimple_toolkits_callgraph_VirtualCalls().resolveNonSpecial(Scene.v().getRefType(testClass), defaultMethod.getNumberedSubSignature(), false);
	  
	  Scene.v().getFastHierarchy().resolveConcreteDispatch(Scene.v().getSootClass(testClass), defaultMethod);
	  
	  Scene.v().getFastHierarchy().resolveAbstractDispatch(Scene.v().getSootClass(defaultClass), defaultMethod);  

	  final CallGraph cg = Scene.v().getCallGraph(); 
	  
	  boolean edgePresent = checkInEdges(cg, defaultMethod, target);
	  	  
	  final ReachableMethods reachableMethods = Scene.v().getReachableMethods(); 
	  
	  Assert.assertEquals(defaultMethod, resolvedMethod);
	  Assert.assertEquals(defaultMethod, targetMethod);
	  Assert.assertEquals(defaultMethod.getName(), "target");
	  Assert.assertNotNull(defaultMethod);
	  Assert.assertTrue(reachableMethods.contains(defaultMethod));
	  Assert.assertTrue(edgePresent);
    
  }
  
  @Test
  public void interfaceWithSameSignatureTest() {
	  String testClass = "soot.interfaceTesting.TestInterfaceSameSignature";

	  final SootMethod target =
			  prepareTarget(
					  methodSigFromComponents(testClass, "void", "main"),
					  testClass,
					  "soot.interfaceTesting.Read", "soot.interfaceTesting.Write");

	  SootMethod mainPrintMethod = Scene.v().getMethod("<soot.interfaceTesting.TestInterfaceSameSignature: void print()>");
	  SootMethod readInterfacePrint = Scene.v().getMethod("<soot.interfaceTesting.Read: void print()>");
	  SootMethod writeInterfacePrint = Scene.v().getMethod("<soot.interfaceTesting.Write: void print()>");
	  SootMethod defaultRead = Scene.v().getMethod("<soot.interfaceTesting.Read: void read()>");
	  SootMethod defaultWrite = Scene.v().getMethod("<soot.interfaceTesting.Write: void write()>");
	  
	  Body mainBody = target.retrieveActiveBody();
	  Body mainPrintBody = mainPrintMethod.retrieveActiveBody();
	  
	  SootMethod refMainMethod = getSootMethodRef(mainBody.getUnits(), "void print()");
	  SootMethod refWritePrintMethod = getSootMethodRef(mainPrintBody.getUnits(), "soot.interfaceTesting.Write: void print()");
	  SootMethod refReadPrintMethod = getSootMethodRef(mainPrintBody.getUnits(), "soot.interfaceTesting.Read: void print()");
	  SootMethod refDefaultRead = getSootMethodRef(mainBody.getUnits(), "void read()");
	  SootMethod refDefaultWrite = getSootMethodRef(mainBody.getUnits(), "void write()");
	  
	  SootMethod resolvedMainMethod = G.v().soot_jimple_toolkits_callgraph_VirtualCalls().resolveNonSpecial(Scene.v().getRefType(testClass), mainPrintMethod.getNumberedSubSignature(), false);
	  SootMethod resolvedWritePrintMethod = G.v().soot_jimple_toolkits_callgraph_VirtualCalls().resolveNonSpecial(Scene.v().getRefType(testClass), writeInterfacePrint.getNumberedSubSignature(), false);
	  SootMethod resolvedReadPrintMethod = G.v().soot_jimple_toolkits_callgraph_VirtualCalls().resolveNonSpecial(Scene.v().getRefType(testClass), readInterfacePrint.getNumberedSubSignature(), false);
	  SootMethod resolvedDefaultReadMethod = G.v().soot_jimple_toolkits_callgraph_VirtualCalls().resolveNonSpecial(Scene.v().getRefType(testClass), defaultRead.getNumberedSubSignature(), false);
	  SootMethod resolvedDefaultWriteMethod = G.v().soot_jimple_toolkits_callgraph_VirtualCalls().resolveNonSpecial(Scene.v().getRefType(testClass), defaultWrite.getNumberedSubSignature(), false);  
	  
	  
	  final CallGraph cg = Scene.v().getCallGraph();
	  
	  /* Edges should be present */
	  boolean edgeMainPrintToReadPrint = checkInEdges(cg, readInterfacePrint, mainPrintMethod);
	  boolean edgeMainPrintToWritePrint = checkInEdges(cg, writeInterfacePrint, mainPrintMethod);
	  boolean edgeMainMethodToPrint = checkInEdges(cg, mainPrintMethod, target);
	  
	  /* Edges should not be present */
	  boolean edgeMainMethodToReadPrint = checkInEdges(cg, readInterfacePrint, target);
	  boolean edgeMainMethodToWritePrint = checkInEdges(cg, writeInterfacePrint, target);
	  
	  /* Edges should be present */
	  boolean edgeMainMethodToReadMethod = checkInEdges(cg, defaultRead, target);
	  boolean edgeMainMethodToWriteMethod = checkInEdges(cg, defaultWrite, target);

	  final ReachableMethods reachableMethods = Scene.v().getReachableMethods();
	  
	  Assert.assertNotNull(mainPrintMethod);
	  Assert.assertNotNull(readInterfacePrint);
	  Assert.assertNotNull(writeInterfacePrint);
	  Assert.assertNotNull(defaultRead);
	  Assert.assertNotNull(defaultWrite);
	  
	  Assert.assertEquals(mainPrintMethod.getName(), "print");
	  Assert.assertEquals(readInterfacePrint.getName(), "print");
	  Assert.assertEquals(writeInterfacePrint.getName(), "print");
	  Assert.assertEquals(defaultRead.getName(), "read");
	  Assert.assertEquals(defaultWrite.getName(), "write");
	  
	  Assert.assertTrue(reachableMethods.contains(mainPrintMethod));
	  Assert.assertTrue(reachableMethods.contains(readInterfacePrint));
	  Assert.assertTrue(reachableMethods.contains(writeInterfacePrint));
	  Assert.assertTrue(reachableMethods.contains(defaultRead));
	  Assert.assertTrue(reachableMethods.contains(defaultWrite));
	  
	  Assert.assertTrue(edgeMainPrintToReadPrint);
	  Assert.assertTrue(edgeMainPrintToWritePrint);
	  Assert.assertTrue(edgeMainMethodToPrint);
	  Assert.assertFalse(edgeMainMethodToReadPrint);
	  Assert.assertFalse(edgeMainMethodToWritePrint);
	  Assert.assertTrue(edgeMainMethodToReadMethod);
	  Assert.assertTrue(edgeMainMethodToWriteMethod);
	  
	  Assert.assertEquals(mainPrintMethod, resolvedMainMethod);
	  Assert.assertEquals(readInterfacePrint, resolvedReadPrintMethod);
	  Assert.assertEquals(writeInterfacePrint, resolvedWritePrintMethod);
	  Assert.assertEquals(defaultRead, resolvedDefaultReadMethod);
	  Assert.assertEquals(defaultWrite, resolvedDefaultWriteMethod);
	  
	  Assert.assertEquals(mainPrintMethod, refMainMethod);
	  Assert.assertEquals(readInterfacePrint, refReadPrintMethod);
	  Assert.assertEquals(writeInterfacePrint, refWritePrintMethod);
	  Assert.assertEquals(defaultRead, refDefaultRead);
	  Assert.assertEquals(defaultWrite, refDefaultWrite);
	  
  }
  
  @Test
  public void classInterfaceWithSameSignatureTest() {
	  String testClass = "soot.interfaceTesting.TestClassPreferenceOverInterface";

	  final SootMethod target =
			  prepareTarget(
					  methodSigFromComponents(testClass, "void", "main"),
					  testClass,
					  "soot.interfaceTesting.HelloWorld");

	  SootMethod mainPrintMethod = Scene.v().getMethod("<soot.interfaceTesting.TestClassPreferenceOverInterface: void print()>");
	  SootMethod defaultPrintMethod = Scene.v().getMethod("<soot.interfaceTesting.InterfaceOne: void print()>");

	  final CallGraph cg = Scene.v().getCallGraph();
	  
	  Body mainBody = target.retrieveActiveBody();
	  
	  SootMethod refMainMethod = getSootMethodRef(mainBody.getUnits(), "void print()");
	  
	  SootMethod resolvedMethod = G.v().soot_jimple_toolkits_callgraph_VirtualCalls().resolveNonSpecial(Scene.v().getRefType(testClass), defaultPrintMethod.getNumberedSubSignature(), false);
	  
	  boolean edgeMainMethodToMainPrint = checkInEdges(cg, mainPrintMethod, target);
	  boolean edgeMainPrintToDefaultPrint = checkInEdges(cg, defaultPrintMethod, target);

	  final ReachableMethods reachableMethods = Scene.v().getReachableMethods();
	  
	  Assert.assertNotNull(mainPrintMethod);
	  Assert.assertNotNull(defaultPrintMethod);
	  
	  Assert.assertEquals(mainPrintMethod.getName(), "print");
	  
	  Assert.assertTrue(edgeMainMethodToMainPrint);
	  Assert.assertFalse(edgeMainPrintToDefaultPrint);
	  
	  Assert.assertTrue(reachableMethods.contains(mainPrintMethod));
	  Assert.assertFalse(reachableMethods.contains(defaultPrintMethod));
	  
	  Assert.assertEquals(mainPrintMethod, refMainMethod);
	  Assert.assertEquals(mainPrintMethod, resolvedMethod);
	  
  }
  
  @Test
  public void superClassInterfaceWithSameSignatureTest() {
	  String testClass = "soot.interfaceTesting.TestSuperClassInterfaceSameSignature";

	  final SootMethod target =
			  prepareTarget(
					  methodSigFromComponents(testClass, "void", "main"),
					  testClass,
					  "soot.interfaceTesting.PrintInterface", "soot.interfaceTesting.TestSuperClassImplementsInterface");

	  SootMethod defaultSuperMainMethod = Scene.v().getMethod("<soot.interfaceTesting.TestSuperClassImplementsInterface: void main()>");
	  SootMethod mainMethod = Scene.v().getMethod("<soot.interfaceTesting.TestSuperClassImplementsInterface: void print()>");
	  SootMethod defaultMethod = Scene.v().getMethod("<soot.interfaceTesting.PrintInterface: void print()>");
	  SootMethod defaultSuperClassMethod = Scene.v().getMethod("<soot.interfaceTesting.DefaultPrint: void print()>");
	  
	  Body mainBody = target.retrieveActiveBody();
	  SootMethod refMainMethod = getSootMethodRef(mainBody.getUnits(), "void print()");
	  
	  SootMethod resolvedMethod = G.v().soot_jimple_toolkits_callgraph_VirtualCalls().resolveNonSpecial(Scene.v().getRefType(testClass), defaultMethod.getNumberedSubSignature(), false);
	  SootMethod resolvedSuperClassDefaultMethod = G.v().soot_jimple_toolkits_callgraph_VirtualCalls().resolveNonSpecial(Scene.v().getRefType(testClass), defaultSuperClassMethod.getNumberedSubSignature(), false);

	  final CallGraph cg = Scene.v().getCallGraph();
	  
	  boolean edgeMainToSuperClassPrint = checkInEdges(cg, mainMethod, target);
	  boolean edgeMainToDefaultPrint = checkInEdges(cg, defaultMethod, target);
	  boolean edgeMainToSuperDefaultPrint = checkInEdges(cg, defaultSuperClassMethod, target);
	  boolean edgeSuperMainToSuperPrint = checkInEdges(cg, defaultSuperClassMethod, defaultSuperMainMethod);

	  final ReachableMethods reachableMethods = Scene.v().getReachableMethods();
	  
	  Assert.assertNotNull(mainMethod);
	  Assert.assertNotNull(defaultMethod);
	  Assert.assertNotNull(defaultSuperClassMethod);
	  
	  Assert.assertEquals(mainMethod.getName(), "print");  
	  
	  Assert.assertTrue(edgeMainToSuperClassPrint);
	  Assert.assertFalse(edgeMainToDefaultPrint);
	  Assert.assertFalse(edgeMainToSuperDefaultPrint);
	  Assert.assertFalse(edgeSuperMainToSuperPrint);
	  
	  Assert.assertTrue(reachableMethods.contains(mainMethod));
	  Assert.assertFalse(reachableMethods.contains(defaultSuperClassMethod));
	  Assert.assertFalse(reachableMethods.contains(defaultMethod));	 
	  
	  Assert.assertEquals(mainMethod, refMainMethod);
	  Assert.assertEquals(mainMethod, resolvedMethod);
	  Assert.assertEquals(resolvedSuperClassDefaultMethod, resolvedMethod);
  }  

  @Test
  public void derivedInterfacesTest() {
	  String testClass = "soot.interfaceTesting.TestDerivedInterfaces";
	  final SootMethod target =
			  prepareTarget(
					  methodSigFromComponents(testClass, "void", "main"),
					  testClass,
					  "soot.interfaceTesting.InterfaceTestOne", "soot.interfaceTesting.InterfaceTestTwo");	  
	  
	  SootMethod interfaceOnePrint = Scene.v().getMethod("<soot.interfaceTesting.InterfaceTestOne: void print()>");
	  SootMethod interfaceTwoPrint = Scene.v().getMethod("<soot.interfaceTesting.InterfaceTestTwo: void print()>");
	  
	  Body mainBody = target.retrieveActiveBody();
	  SootMethod refMainMethod = getSootMethodRef(mainBody.getUnits(), "void print()");
	  
	  SootMethod interfaceOneResolvedMethod = G.v().soot_jimple_toolkits_callgraph_VirtualCalls().resolveNonSpecial(Scene.v().getRefType(testClass), interfaceOnePrint.getNumberedSubSignature(), false);
	  SootMethod interfaceTwoResolvedMethod = G.v().soot_jimple_toolkits_callgraph_VirtualCalls().resolveNonSpecial(Scene.v().getRefType(testClass), interfaceTwoPrint.getNumberedSubSignature(), false);
	  
	  final CallGraph cg = Scene.v().getCallGraph();
	  
	  boolean edgeMainToInterfaceTwoPrint = checkInEdges(cg, interfaceTwoPrint, target);
	  boolean edgeMainToInterfaceOnePrint = checkInEdges(cg, interfaceOnePrint, target);
	  
	  final ReachableMethods reachableMethods = Scene.v().getReachableMethods();
	  
	  Assert.assertEquals(interfaceTwoPrint.getName(), "print");  
	  Assert.assertNotNull(interfaceTwoPrint);
	  Assert.assertNotNull(interfaceOnePrint);
	  
	  Assert.assertFalse(edgeMainToInterfaceOnePrint);
	  Assert.assertTrue(edgeMainToInterfaceTwoPrint);
	  
	  Assert.assertTrue(reachableMethods.contains(interfaceTwoPrint));
	  Assert.assertFalse(reachableMethods.contains(interfaceOnePrint));
	  
	  Assert.assertEquals(interfaceTwoPrint, refMainMethod);
	  
	  Assert.assertEquals(interfaceTwoPrint, interfaceOneResolvedMethod);
	  Assert.assertEquals(interfaceTwoPrint, interfaceTwoResolvedMethod);
	  
  }
  
  @Test
  public void interfaceInheritanceTest() {
	  String testClass = "soot.interfaceTesting.TestInterfaceInheritance";
	  final SootMethod target =
			  prepareTarget(
					  methodSigFromComponents(testClass, "void", "main"),
					  testClass,
					  "soot.interfaceTesting.InterfaceTestA", "soot.interfaceTesting.InterfaceTestB");	  
	  
	  SootMethod interfaceTestAPrint = Scene.v().getMethod("<soot.interfaceTesting.InterfaceTestA: void print()>");
	  SootMethod mainPrintMessageMethod = Scene.v().getMethod("<soot.interfaceTesting.TestInterfaceInheritance: void printMessage()>");
	  
	  Body mainBody = target.retrieveActiveBody();
	  SootMethod refMainMethod = getSootMethodRef(mainBody.getUnits(), "void print()");
	  
	  SootMethod resolvedMethod = G.v().soot_jimple_toolkits_callgraph_VirtualCalls().resolveNonSpecial(Scene.v().getRefType(testClass), interfaceTestAPrint.getNumberedSubSignature(), false);
	  
	  final CallGraph cg = Scene.v().getCallGraph();
	  
	  boolean edgeMainToInterfaceTestAPrint = checkInEdges(cg, interfaceTestAPrint, target);
	  boolean edgeMainToMainPrintMessage = checkInEdges(cg, mainPrintMessageMethod, target);
	  
	  final ReachableMethods reachableMethods = Scene.v().getReachableMethods();
	  
	  Assert.assertEquals(interfaceTestAPrint.getName(), "print");
	  Assert.assertNotNull(interfaceTestAPrint);
	  Assert.assertNotNull(mainPrintMessageMethod);
	  
	  Assert.assertTrue(edgeMainToInterfaceTestAPrint);
	  Assert.assertFalse(edgeMainToMainPrintMessage);
	  
	  Assert.assertTrue(reachableMethods.contains(interfaceTestAPrint));
	  Assert.assertFalse(reachableMethods.contains(mainPrintMessageMethod));
	  
	  Assert.assertEquals(interfaceTestAPrint, refMainMethod);
	  Assert.assertEquals(interfaceTestAPrint, resolvedMethod);
	  
  }
  
  @Test
  public void interfaceReAbstractionTest() {
	  String testClass = "soot.interfaceTesting.TestInterfaceReAbstracting";
	  final SootMethod target =
			  prepareTarget(
					  methodSigFromComponents(testClass, "void", "main"),
					  testClass,
					  "soot.interfaceTesting.InterfaceA", "soot.interfaceTesting.InterfaceB");
	  
	  SootMethod interfaceAPrint = Scene.v().getMethod("<soot.interfaceTesting.InterfaceA: void print()>");
	  SootMethod mainMethodPrint = Scene.v().getMethod("<soot.interfaceTesting.TestInterfaceReAbstracting: void print()>");
	  
	  Body mainBody = target.retrieveActiveBody();
	  SootMethod refMainMethod = getSootMethodRef(mainBody.getUnits(), "void print()");
	  
	  SootMethod resolvedMethod = G.v().soot_jimple_toolkits_callgraph_VirtualCalls().resolveNonSpecial(Scene.v().getRefType(testClass), interfaceAPrint.getNumberedSubSignature(), false);
	  
	  final CallGraph cg = Scene.v().getCallGraph();
	  
	  boolean edgeMainMethodToMainPrint = checkInEdges(cg, mainMethodPrint, target);
	  boolean edgeMainMethodToInterfaceAPrint = checkInEdges(cg, interfaceAPrint, target);
	  
	  final ReachableMethods reachableMethods = Scene.v().getReachableMethods();
	  
	  Assert.assertEquals(mainMethodPrint.getName(), "print");
	  Assert.assertNotNull(mainMethodPrint);
	  Assert.assertNotNull(interfaceAPrint);
	  
	  Assert.assertTrue(edgeMainMethodToMainPrint);
	  Assert.assertFalse(edgeMainMethodToInterfaceAPrint);
	  
	  Assert.assertTrue(reachableMethods.contains(mainMethodPrint));
	  Assert.assertFalse(reachableMethods.contains(interfaceAPrint));
	  
	  Assert.assertEquals(mainMethodPrint, refMainMethod);
	  Assert.assertEquals(mainMethodPrint, resolvedMethod);
  }
  
  @Test
  public void SuperClassPreferenceOverDefaultMethodTest() {
	  String testClass = "soot.interfaceTesting.TestSuperClassPreferenceOverInterface";
	  final SootMethod target =
			  prepareTarget(
					  methodSigFromComponents(testClass, "void", "main"),
					  testClass,
					  "soot.interfaceTesting.InterfaceOne", "soot.interfaceTesting.InterfaceTwo", "soot.interfaceTesting.TestSuperClass");
	  
	  SootMethod interfaceOnePrint = Scene.v().getMethod("<soot.interfaceTesting.InterfaceOne: void print()>");
	  SootMethod interfaceTwoPrint = Scene.v().getMethod("<soot.interfaceTesting.InterfaceTwo: void print()>");
	  SootMethod superClassPrint = Scene.v().getMethod("<soot.interfaceTesting.TestSuperClass: void print()>");
	  
	  Body mainBody = target.retrieveActiveBody();
	  SootMethod refMainMethod = getSootMethodRef(mainBody.getUnits(), "void print()");
	  
	  SootMethod resolvedInterfaceOneDefaultMethod = G.v().soot_jimple_toolkits_callgraph_VirtualCalls().resolveNonSpecial(Scene.v().getRefType(testClass), interfaceOnePrint.getNumberedSubSignature(), false);
	  SootMethod resolvedInterfaceTwoDefaultMethod = G.v().soot_jimple_toolkits_callgraph_VirtualCalls().resolveNonSpecial(Scene.v().getRefType(testClass), interfaceTwoPrint.getNumberedSubSignature(), false);
	  
	  final CallGraph cg = Scene.v().getCallGraph();
	  
	  boolean edgeMainToInterfaceOnePrint = checkInEdges(cg, interfaceOnePrint, target);
	  boolean edgeMainToInterfaceTwoPrint = checkInEdges(cg, interfaceTwoPrint, target);
	  boolean edgeMainToSuperClassPrint = checkInEdges(cg, superClassPrint, target);
	  
	  final ReachableMethods reachableMethods = Scene.v().getReachableMethods();
	  
	  Assert.assertNotNull(superClassPrint);
	  Assert.assertNotNull(interfaceOnePrint);
	  Assert.assertNotNull(interfaceTwoPrint);
	  
	  Assert.assertEquals(superClassPrint.getName(), "print");
	  
	  Assert.assertTrue(edgeMainToSuperClassPrint);
	  Assert.assertFalse(edgeMainToInterfaceOnePrint);
	  Assert.assertFalse(edgeMainToInterfaceTwoPrint);
	  
	  Assert.assertTrue(reachableMethods.contains(superClassPrint));
	  Assert.assertFalse(reachableMethods.contains(interfaceOnePrint));
	  Assert.assertFalse(reachableMethods.contains(interfaceTwoPrint));
	  
	  Assert.assertEquals(superClassPrint, refMainMethod);
	  Assert.assertEquals(superClassPrint, resolvedInterfaceOneDefaultMethod);
	  Assert.assertEquals(superClassPrint, resolvedInterfaceTwoDefaultMethod);
  }
  
  private boolean checkInEdges(CallGraph callGraph, SootMethod defaultMethod, SootMethod targetMethod) {

	  boolean isPresent = false;
	  Iterator<Edge> inEdges = callGraph.edgesInto(defaultMethod);
	  while(inEdges.hasNext()) {
		  MethodOrMethodContext sourceMethod = inEdges.next().getSrc();
		  if(sourceMethod.equals(targetMethod)) {
			  isPresent = true;
		  }
	  }
	  return isPresent;
  }
  
  private SootMethod getSootMethodRef(UnitPatchingChain units, String targetMethod) {
	  
	  SootMethod method = null;
	  for(Unit unit: units) {
		  if(unit instanceof JInvokeStmt && unit.toString().contains(targetMethod) ) {
			  method = ((JInvokeStmt) unit).getInvokeExpr().getMethodRef().tryResolve();			 
		  }
	  }
	  return method;
  }
}
  
