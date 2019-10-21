package soot.testing.framework;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.Iterator;
import java.util.Set;

import org.junit.Assert;
import soot.Body;
import soot.G;
import soot.G.Global;
import soot.MethodOrMethodContext;
import soot.PhaseOptions;
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
	@Override
	protected void setupSoot() {
		super.setupSoot();
		PhaseOptions.v().setPhaseOption("cg.cha", "on");
	}


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
	  
	  SootMethod resolvedMethod = G.v().soot_jimple_toolkits_callgraph_VirtualCalls().resolveNonSpecial(Scene.v().getRefType(testClass), defaultMethod, false);
	  
	  SootMethod concreteImpl = Scene.v().getFastHierarchy().resolveConcreteDispatch(Scene.v().getSootClass(testClass), defaultMethod);
	  
	  Set<SootMethod> abstractImpl = Scene.v().getFastHierarchy().resolveAbstractDispatch(Scene.v().getSootClass(defaultClass), defaultMethod);  

	  final CallGraph cg = Scene.v().getCallGraph(); 
	  
	  boolean edgePresent = checkInEdges(cg, defaultMethod, target);
	  	  
	  final ReachableMethods reachableMethods = Scene.v().getReachableMethods(); 
	  
	  assertEquals(defaultMethod, resolvedMethod);
	  assertEquals(defaultMethod, targetMethod);
	  assertEquals(defaultMethod.getName(), "target");
	  assertNotNull(defaultMethod);
	  assertTrue(reachableMethods.contains(defaultMethod));
	  assertTrue(edgePresent);
	  assertEquals(defaultMethod, concreteImpl);
  }
  
  @Test
  public void interfaceWithSameSignatureTest() {
	  String testClass = "soot.interfaceTesting.TestInterfaceSameSignature";
	  String interfaceReadClass = "soot.interfaceTesting.Read";
	  String interfaceWriteClass = "soot.interfaceTesting.Write";

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
	  
	  SootMethod resolvedMainMethod = G.v().soot_jimple_toolkits_callgraph_VirtualCalls().resolveNonSpecial(Scene.v().getRefType(testClass), mainPrintMethod, false);
	  SootMethod resolvedWritePrintMethod = G.v().soot_jimple_toolkits_callgraph_VirtualCalls().resolveNonSpecial(Scene.v().getRefType(testClass), writeInterfacePrint, false);
	  SootMethod resolvedReadPrintMethod = G.v().soot_jimple_toolkits_callgraph_VirtualCalls().resolveNonSpecial(Scene.v().getRefType(testClass), readInterfacePrint, false);
	  SootMethod resolvedDefaultReadMethod = G.v().soot_jimple_toolkits_callgraph_VirtualCalls().resolveNonSpecial(Scene.v().getRefType(testClass), defaultRead, false);
	  SootMethod resolvedDefaultWriteMethod = G.v().soot_jimple_toolkits_callgraph_VirtualCalls().resolveNonSpecial(Scene.v().getRefType(testClass), defaultWrite, false);
	  
	  SootMethod concreteImplMainPrint = Scene.v().getFastHierarchy().resolveConcreteDispatch(Scene.v().getSootClass(testClass), mainPrintMethod);
	  SootMethod concreteImplReadPrint = Scene.v().getFastHierarchy().resolveConcreteDispatch(Scene.v().getSootClass(testClass), refReadPrintMethod);
	  SootMethod concreteImplDefaultRead = Scene.v().getFastHierarchy().resolveConcreteDispatch(Scene.v().getSootClass(testClass), refDefaultRead);
	  SootMethod concreteImplDefaultWrite = Scene.v().getFastHierarchy().resolveConcreteDispatch(Scene.v().getSootClass(testClass), refDefaultWrite);
	  
	  Set<SootMethod> abstractImplDefaultRead = Scene.v().getFastHierarchy().resolveAbstractDispatch(Scene.v().getSootClass(interfaceReadClass), refDefaultRead); 
	  Set<SootMethod> abstractImplDefaultWrite = Scene.v().getFastHierarchy().resolveAbstractDispatch(Scene.v().getSootClass(interfaceWriteClass), refDefaultWrite); 
	  Set<SootMethod> abstractImplReadDefaultPrint = Scene.v().getFastHierarchy().resolveAbstractDispatch(Scene.v().getSootClass(interfaceReadClass), refReadPrintMethod);
	  Set<SootMethod> abstractImplWriteDefaultPrint = Scene.v().getFastHierarchy().resolveAbstractDispatch(Scene.v().getSootClass(interfaceWriteClass), refWritePrintMethod);  
	  
	  
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
	  
	  assertNotNull(mainPrintMethod);
	  assertNotNull(readInterfacePrint);
	  assertNotNull(writeInterfacePrint);
	  assertNotNull(defaultRead);
	  assertNotNull(defaultWrite);
	  
	  assertEquals(mainPrintMethod.getName(), "print");
	  assertEquals(readInterfacePrint.getName(), "print");
	  assertEquals(writeInterfacePrint.getName(), "print");
	  assertEquals(defaultRead.getName(), "read");
	  assertEquals(defaultWrite.getName(), "write");
	  
	  assertTrue(reachableMethods.contains(mainPrintMethod));
	  assertTrue(reachableMethods.contains(readInterfacePrint));
	  assertTrue(reachableMethods.contains(writeInterfacePrint));
	  assertTrue(reachableMethods.contains(defaultRead));
	  assertTrue(reachableMethods.contains(defaultWrite));
	  
	  assertTrue(edgeMainPrintToReadPrint);
	  assertTrue(edgeMainPrintToWritePrint);
	  assertTrue(edgeMainMethodToPrint);
	  assertFalse(edgeMainMethodToReadPrint);
	  assertFalse(edgeMainMethodToWritePrint);
	  assertTrue(edgeMainMethodToReadMethod);
	  assertTrue(edgeMainMethodToWriteMethod);
	  
	  assertEquals(mainPrintMethod, resolvedMainMethod);
	  assertEquals(mainPrintMethod, resolvedReadPrintMethod);
	  assertEquals(mainPrintMethod, resolvedWritePrintMethod);
	  assertEquals(defaultRead, resolvedDefaultReadMethod);
	  assertEquals(defaultWrite, resolvedDefaultWriteMethod);
	  
	  assertEquals(mainPrintMethod, refMainMethod);
	  assertEquals(readInterfacePrint, refReadPrintMethod);
	  assertEquals(writeInterfacePrint, refWritePrintMethod);
	  assertEquals(defaultRead, refDefaultRead);
	  assertEquals(defaultWrite, refDefaultWrite);
	  
	  assertEquals(mainPrintMethod, concreteImplMainPrint);
	  assertEquals(mainPrintMethod, Scene.v().getFastHierarchy().resolveConcreteDispatch(Scene.v().getSootClass(testClass), refWritePrintMethod));
	  assertEquals(mainPrintMethod, concreteImplReadPrint);
	  assertEquals(refDefaultRead, concreteImplDefaultRead);
	  assertEquals(refDefaultWrite, concreteImplDefaultWrite);
	  
  }
  
  @Test
  public void classInterfaceWithSameSignatureTest() {
	  String testClass = "soot.interfaceTesting.TestClassPreferenceOverInterface";
	  String defaultClass = "soot.interfaceTesting.HelloWorld";

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
	  
	  SootMethod resolvedMethod = G.v().soot_jimple_toolkits_callgraph_VirtualCalls().resolveNonSpecial(Scene.v().getRefType(testClass), defaultPrintMethod, false);
	  
	  SootMethod concreteImpl = Scene.v().getFastHierarchy().resolveConcreteDispatch(Scene.v().getSootClass(testClass), defaultPrintMethod);
	  
	  Set<SootMethod> abstractImpl = Scene.v().getFastHierarchy().resolveAbstractDispatch(Scene.v().getSootClass(defaultClass), defaultPrintMethod);
	  
	  boolean edgeMainMethodToMainPrint = checkInEdges(cg, mainPrintMethod, target);
	  boolean edgeMainPrintToDefaultPrint = checkInEdges(cg, defaultPrintMethod, target);

	  final ReachableMethods reachableMethods = Scene.v().getReachableMethods();
	  
	  assertNotNull(mainPrintMethod);
	  assertNotNull(defaultPrintMethod);
	  
	  assertEquals(mainPrintMethod.getName(), "print");
	  
	  assertTrue(edgeMainMethodToMainPrint);
	  assertFalse(edgeMainPrintToDefaultPrint);
	  
	  assertTrue(reachableMethods.contains(mainPrintMethod));
	  assertFalse(reachableMethods.contains(defaultPrintMethod));
	  
	  assertEquals(mainPrintMethod, refMainMethod);
	  assertEquals(mainPrintMethod, resolvedMethod);
	  
	  assertEquals(mainPrintMethod, concreteImpl);
	  
  }
  
  @Test
  public void superClassInterfaceWithSameSignatureTest() {
	  String testClass = "soot.interfaceTesting.TestSuperClassInterfaceSameSignature";
	  String defaultClass = "soot.interfaceTesting.PrintInterface";
	  String defaultSuperClass = "soot.interfaceTesting.DefaultPrint";

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
	  
	  SootMethod resolvedMethod = G.v().soot_jimple_toolkits_callgraph_VirtualCalls().resolveNonSpecial(Scene.v().getRefType(testClass), defaultMethod, false);
	  SootMethod resolvedSuperClassDefaultMethod = G.v().soot_jimple_toolkits_callgraph_VirtualCalls().resolveNonSpecial(Scene.v().getRefType(testClass), defaultSuperClassMethod, false);
	  
	  SootMethod concreteImpl = Scene.v().getFastHierarchy().resolveConcreteDispatch(Scene.v().getSootClass(testClass), defaultMethod);
	  
	  Set<SootMethod> abstractImpl = Scene.v().getFastHierarchy().resolveAbstractDispatch(Scene.v().getSootClass(defaultClass), defaultMethod);
	  Set<SootMethod> abstractImplSuperClass = Scene.v().getFastHierarchy().resolveAbstractDispatch(Scene.v().getSootClass(defaultSuperClass), defaultSuperClassMethod);

	  final CallGraph cg = Scene.v().getCallGraph();
	  
	  boolean edgeMainToSuperClassPrint = checkInEdges(cg, mainMethod, target);
	  boolean edgeMainToDefaultPrint = checkInEdges(cg, defaultMethod, target);
	  boolean edgeMainToSuperDefaultPrint = checkInEdges(cg, defaultSuperClassMethod, target);
	  boolean edgeSuperMainToSuperPrint = checkInEdges(cg, defaultSuperClassMethod, defaultSuperMainMethod);

	  final ReachableMethods reachableMethods = Scene.v().getReachableMethods();
	  
	  assertNotNull(mainMethod);
	  assertNotNull(defaultMethod);
	  assertNotNull(defaultSuperClassMethod);
	  
	  assertEquals(mainMethod.getName(), "print");
	  
	  assertTrue(edgeMainToSuperClassPrint);
	  assertFalse(edgeMainToDefaultPrint);
	  assertFalse(edgeMainToSuperDefaultPrint);
	  assertFalse(edgeSuperMainToSuperPrint);
	  
	  assertTrue(reachableMethods.contains(mainMethod));
	  assertFalse(reachableMethods.contains(defaultSuperClassMethod));
	  assertFalse(reachableMethods.contains(defaultMethod));
	  
	  assertEquals(mainMethod, refMainMethod);
	  assertEquals(mainMethod, resolvedMethod);
	  assertEquals(resolvedSuperClassDefaultMethod, resolvedMethod);
	  
	  assertEquals(mainMethod, concreteImpl);
	  assertNotEquals(defaultMethod, concreteImpl);
  }  

  @Test
  public void derivedInterfacesTest() {
	  String testClass = "soot.interfaceTesting.TestDerivedInterfaces";
	  String defaultInterfaceOne = "soot.interfaceTesting.InterfaceTestOne";
	  String defaultInterfaceTwo = "soot.interfaceTesting.InterfaceTestTwo";
	  
	  final SootMethod target =
			  prepareTarget(
					  methodSigFromComponents(testClass, "void", "main"),
					  testClass,
					  "soot.interfaceTesting.InterfaceTestOne", "soot.interfaceTesting.InterfaceTestTwo");	  
	  
	  SootMethod interfaceOnePrint = Scene.v().getMethod("<soot.interfaceTesting.InterfaceTestOne: void print()>");
	  SootMethod interfaceTwoPrint = Scene.v().getMethod("<soot.interfaceTesting.InterfaceTestTwo: void print()>");
	  
	  Body mainBody = target.retrieveActiveBody();
	  SootMethod refMainMethod = getSootMethodRef(mainBody.getUnits(), "void print()");
	  
	  SootMethod interfaceOneResolvedMethod = G.v().soot_jimple_toolkits_callgraph_VirtualCalls().resolveNonSpecial(Scene.v().getRefType(testClass), interfaceOnePrint, false);
	  SootMethod interfaceTwoResolvedMethod = G.v().soot_jimple_toolkits_callgraph_VirtualCalls().resolveNonSpecial(Scene.v().getRefType(testClass), interfaceTwoPrint, false);
	  
	  SootMethod concreteImplInterfaceOne = Scene.v().getFastHierarchy().resolveConcreteDispatch(Scene.v().getSootClass(testClass), interfaceOnePrint);
	  SootMethod concreteImplInterfaceTwo = Scene.v().getFastHierarchy().resolveConcreteDispatch(Scene.v().getSootClass(testClass), interfaceTwoPrint);
	  
	  Set<SootMethod> abstractImplInterfaceOne = Scene.v().getFastHierarchy().resolveAbstractDispatch(Scene.v().getSootClass(defaultInterfaceOne), interfaceOnePrint);
	  Set<SootMethod> abstractImplInterfaceTwo = Scene.v().getFastHierarchy().resolveAbstractDispatch(Scene.v().getSootClass(defaultInterfaceTwo), interfaceTwoPrint);
	  
	  final CallGraph cg = Scene.v().getCallGraph();
	  
	  boolean edgeMainToInterfaceTwoPrint = checkInEdges(cg, interfaceTwoPrint, target);
	  boolean edgeMainToInterfaceOnePrint = checkInEdges(cg, interfaceOnePrint, target);
	  
	  final ReachableMethods reachableMethods = Scene.v().getReachableMethods();
	  
	  assertEquals(interfaceTwoPrint.getName(), "print");
	  assertNotNull(interfaceTwoPrint);
	  assertNotNull(interfaceOnePrint);
	  
	  assertFalse(edgeMainToInterfaceOnePrint);
	  assertTrue(edgeMainToInterfaceTwoPrint);
	  
	  assertTrue(reachableMethods.contains(interfaceTwoPrint));
	  assertFalse(reachableMethods.contains(interfaceOnePrint));
	  
	  assertEquals(interfaceTwoPrint, refMainMethod);
	  
	  assertEquals(interfaceTwoPrint, interfaceOneResolvedMethod);
	  assertEquals(interfaceTwoPrint, interfaceTwoResolvedMethod);
	  
	  assertEquals(interfaceTwoPrint, concreteImplInterfaceOne);
	  assertNotEquals(interfaceOnePrint, concreteImplInterfaceOne);
	  assertEquals(interfaceTwoPrint, concreteImplInterfaceTwo);
	  assertNotEquals(interfaceOnePrint, concreteImplInterfaceTwo);
	  
  }
  
  @Test
  public void interfaceInheritanceTest() {
	  String testClass = "soot.interfaceTesting.TestInterfaceInheritance";
	  String defaultClass = "soot.interfaceTesting.InterfaceTestA";
	  
	  final SootMethod target =
			  prepareTarget(
					  methodSigFromComponents(testClass, "void", "main"),
					  testClass,
					  "soot.interfaceTesting.InterfaceTestA", "soot.interfaceTesting.InterfaceTestB");	  
	  
	  SootMethod interfaceTestAPrint = Scene.v().getMethod("<soot.interfaceTesting.InterfaceTestA: void print()>");
	  SootMethod mainPrintMessageMethod = Scene.v().getMethod("<soot.interfaceTesting.TestInterfaceInheritance: void printMessage()>");
	  
	  Body mainBody = target.retrieveActiveBody();
	  SootMethod refMainMethod = getSootMethodRef(mainBody.getUnits(), "void print()");
	  
	  SootMethod resolvedMethod = G.v().soot_jimple_toolkits_callgraph_VirtualCalls().resolveNonSpecial(Scene.v().getRefType(testClass), interfaceTestAPrint, false);
	  
	  SootMethod concreteImpl = Scene.v().getFastHierarchy().resolveConcreteDispatch(Scene.v().getSootClass(testClass), interfaceTestAPrint);
	  
	  Set<SootMethod> abstractImpl = Scene.v().getFastHierarchy().resolveAbstractDispatch(Scene.v().getSootClass(defaultClass), interfaceTestAPrint);
	  
	  final CallGraph cg = Scene.v().getCallGraph();
	  
	  boolean edgeMainToInterfaceTestAPrint = checkInEdges(cg, interfaceTestAPrint, target);
	  boolean edgeMainToMainPrintMessage = checkInEdges(cg, mainPrintMessageMethod, target);
	  
	  final ReachableMethods reachableMethods = Scene.v().getReachableMethods();
	  
	  assertEquals(interfaceTestAPrint.getName(), "print");
	  assertNotNull(interfaceTestAPrint);
	  assertNotNull(mainPrintMessageMethod);
	  
	  assertTrue(edgeMainToInterfaceTestAPrint);
	  assertFalse(edgeMainToMainPrintMessage);
	  
	  assertTrue(reachableMethods.contains(interfaceTestAPrint));
	  assertFalse(reachableMethods.contains(mainPrintMessageMethod));
	  
	  assertEquals(interfaceTestAPrint, refMainMethod);
	  assertEquals(interfaceTestAPrint, resolvedMethod);
	  
	  assertEquals(interfaceTestAPrint, concreteImpl);
	  
  }
  
  @Test
  public void interfaceReAbstractionTest() {
	  String testClass = "soot.interfaceTesting.TestInterfaceReAbstracting";
	  String defaultClass = "soot.interfaceTesting.InterfaceA";
	  
	  final SootMethod target =
			  prepareTarget(
					  methodSigFromComponents(testClass, "void", "main"),
					  testClass,
					  "soot.interfaceTesting.InterfaceA", "soot.interfaceTesting.InterfaceB");
	  
	  SootMethod interfaceAPrint = Scene.v().getMethod("<soot.interfaceTesting.InterfaceA: void print()>");
	  SootMethod mainMethodPrint = Scene.v().getMethod("<soot.interfaceTesting.TestInterfaceReAbstracting: void print()>");
	  
	  Body mainBody = target.retrieveActiveBody();
	  SootMethod refMainMethod = getSootMethodRef(mainBody.getUnits(), "void print()");
	  
	  SootMethod resolvedMethod = G.v().soot_jimple_toolkits_callgraph_VirtualCalls().resolveNonSpecial(Scene.v().getRefType(testClass), interfaceAPrint, false);
	  
	  SootMethod concreteImpl = Scene.v().getFastHierarchy().resolveConcreteDispatch(Scene.v().getSootClass(testClass), interfaceAPrint);
	  
	  Set<SootMethod> abstractImpl = Scene.v().getFastHierarchy().resolveAbstractDispatch(Scene.v().getSootClass(defaultClass), interfaceAPrint);
	  
	  final CallGraph cg = Scene.v().getCallGraph();
	  
	  boolean edgeMainMethodToMainPrint = checkInEdges(cg, mainMethodPrint, target);
	  boolean edgeMainMethodToInterfaceAPrint = checkInEdges(cg, interfaceAPrint, target);
	  
	  final ReachableMethods reachableMethods = Scene.v().getReachableMethods();
	  
	  assertEquals(mainMethodPrint.getName(), "print");
	  assertNotNull(mainMethodPrint);
	  assertNotNull(interfaceAPrint);
	  
	  assertTrue(edgeMainMethodToMainPrint);
	  assertFalse(edgeMainMethodToInterfaceAPrint);
	  
	  assertTrue(reachableMethods.contains(mainMethodPrint));
	  assertFalse(reachableMethods.contains(interfaceAPrint));
	  
	  assertEquals(mainMethodPrint, refMainMethod);
	  assertEquals(mainMethodPrint, resolvedMethod);
	  
	  assertEquals(mainMethodPrint, concreteImpl);
	  assertNotEquals(interfaceAPrint, concreteImpl);
  }
  
  @Test
  public void SuperClassPreferenceOverDefaultMethodTest() {
	  String testClass = "soot.interfaceTesting.TestSuperClassPreferenceOverInterface";
	  String defaultInterfaceOne = "soot.interfaceTesting.InterfaceOne";
	  String defaultInterfaceTwo = "soot.interfaceTesting.InterfaceTwo";
	  
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
	  
	  SootMethod resolvedInterfaceOneDefaultMethod = G.v().soot_jimple_toolkits_callgraph_VirtualCalls().resolveNonSpecial(Scene.v().getRefType(testClass), interfaceOnePrint, false);
	  SootMethod resolvedInterfaceTwoDefaultMethod = G.v().soot_jimple_toolkits_callgraph_VirtualCalls().resolveNonSpecial(Scene.v().getRefType(testClass), interfaceTwoPrint, false);
	  
	  SootMethod concreteImplInterfaceOne = Scene.v().getFastHierarchy().resolveConcreteDispatch(Scene.v().getSootClass(testClass), interfaceOnePrint);
	  SootMethod concreteImplInterfaceTwo = Scene.v().getFastHierarchy().resolveConcreteDispatch(Scene.v().getSootClass(testClass), interfaceTwoPrint);
	  
	  Set<SootMethod> abstractImplInterfaceOne = Scene.v().getFastHierarchy().resolveAbstractDispatch(Scene.v().getSootClass(defaultInterfaceOne), interfaceOnePrint);
	  Set<SootMethod> abstractImplInterfaceTwo = Scene.v().getFastHierarchy().resolveAbstractDispatch(Scene.v().getSootClass(defaultInterfaceTwo), interfaceTwoPrint);
	  
	  final CallGraph cg = Scene.v().getCallGraph();
	  
	  boolean edgeMainToInterfaceOnePrint = checkInEdges(cg, interfaceOnePrint, target);
	  boolean edgeMainToInterfaceTwoPrint = checkInEdges(cg, interfaceTwoPrint, target);
	  boolean edgeMainToSuperClassPrint = checkInEdges(cg, superClassPrint, target);
	  
	  final ReachableMethods reachableMethods = Scene.v().getReachableMethods();
	  
	  assertNotNull(superClassPrint);
	  assertNotNull(interfaceOnePrint);
	  assertNotNull(interfaceTwoPrint);
	  
	  assertEquals(superClassPrint.getName(), "print");
	  
	  assertTrue(edgeMainToSuperClassPrint);
	  assertFalse(edgeMainToInterfaceOnePrint);
	  assertFalse(edgeMainToInterfaceTwoPrint);
	  
	  assertTrue(reachableMethods.contains(superClassPrint));
	  assertFalse(reachableMethods.contains(interfaceOnePrint));
	  assertFalse(reachableMethods.contains(interfaceTwoPrint));
	  
	  assertEquals(superClassPrint, refMainMethod);
	  assertEquals(superClassPrint, resolvedInterfaceOneDefaultMethod);
	  assertEquals(superClassPrint, resolvedInterfaceTwoDefaultMethod);
	  
	  assertEquals(superClassPrint, concreteImplInterfaceOne);
	  assertNotEquals(interfaceOnePrint, concreteImplInterfaceOne);
	  assertEquals(superClassPrint, concreteImplInterfaceTwo);
	  assertNotEquals(interfaceTwoPrint, concreteImplInterfaceTwo);
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
  
