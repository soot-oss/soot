package soot.defaultInterfaceMethods;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2019 Raja Vallée-Rai and others
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PowerMockIgnore;

import soot.Body;
import soot.FastHierarchy;
import soot.MethodOrMethodContext;
import soot.PhaseOptions;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.UnitPatchingChain;
import soot.jimple.Stmt;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.jimple.toolkits.callgraph.ReachableMethods;
import soot.jimple.toolkits.callgraph.VirtualCalls;
import soot.testing.framework.AbstractTestingFramework;

/** */
@PowerMockIgnore({ "com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*", "org.w3c.*" })
public class DefaultInterfaceTest extends AbstractTestingFramework {
	
	private static String voidType = "void";
	private static String mainClass = "main";

  @Override
  protected void setupSoot() {
    super.setupSoot();
    PhaseOptions.v().setPhaseOption("cg.cha", "on");
  }
  
  @Test
  public void SubClassTest() throws FileNotFoundException, UnsupportedEncodingException {		
	  
	  String testClass = "soot.defaultInterfaceMethods.JavaNCSSCheck";
	  String abstractClass = "soot.defaultInterfaceDifferentPackage.AbstractCheck";
	  String classToAnalyze = "soot.defaultInterfaceDifferentPackage.AbstractCheck";
	  final SootMethod target =
		        prepareTarget(
		            methodSigFromComponents(testClass, voidType, mainClass),
		            testClass,
		            classToAnalyze);
		
		ArrayList<Edge> edges = GetCallGraph();
		
		assertEquals(edges.get(0).getTgt(), Scene.v().getMethod("<soot.defaultInterfaceDifferentPackage.AbstractCheck: void log(java.lang.String,java.lang.String)>"));		
		
	}

  @Test
  public void simpleDefaultInterfaceTest() {

    String testClass = "soot.defaultInterfaceMethods.SimpleDefaultInterface";
    String defaultClass = "soot.defaultInterfaceMethods.Default";
    String classToAnalyze = "soot.defaultInterfaceMethods.Default";

    final SootMethod target =
        prepareTarget(
            methodSigFromComponents(testClass, voidType, mainClass),
            testClass,
            classToAnalyze);

    SootMethod defaultMethod =
        Scene.v().getMethod("<soot.defaultInterfaceMethods.Default: void target()>");
    Body body = target.retrieveActiveBody();
    SootMethod targetMethod = resolveMethodRefInBody(body.getUnits(), "void target()");
    SootMethod resolvedMethod =
        VirtualCalls.v()
            .resolveNonSpecial(Scene.v().getRefType(testClass), defaultMethod.makeRef(), false);
    SootMethod concreteImpl =
        Scene.v()
            .getFastHierarchy()
            .resolveConcreteDispatch(Scene.v().getSootClass(testClass), defaultMethod);
    SootMethod concreteImplViaResolveMethod =
        Scene.v()
            .getFastHierarchy()
            .resolveMethod(Scene.v().getSootClass(testClass), defaultMethod, false);
    Set<SootMethod> abstractImpl =
        Scene.v()
            .getFastHierarchy()
            .resolveAbstractDispatch(Scene.v().getSootClass(defaultClass), defaultMethod);

    boolean edgePresent = checkInEdges(defaultMethod, target);
    final ReachableMethods reachableMethods = Scene.v().getReachableMethods();
    /* Arguments for assert function */

    assertEquals(defaultMethod, resolvedMethod);
    assertEquals(defaultMethod, targetMethod);
    assertEquals(defaultMethod.getName(), "target");
    assertNotNull(defaultMethod);
    assertTrue(reachableMethods.contains(defaultMethod));
    assertTrue(edgePresent);
    assertEquals(defaultMethod, concreteImpl);
    assertEquals(concreteImpl, concreteImplViaResolveMethod);
    assertTrue(
        abstractImpl.contains(
            Scene.v().getMethod("<soot.defaultInterfaceMethods.Default: void target()>")));
  }

  @Test
  public void interfaceSameSignatureTest() {
    String testClassSig = "soot.defaultInterfaceMethods.InterfaceSameSignature";
    String interfaceReadSig = "soot.defaultInterfaceMethods.Read";
    String interfaceWriteSig = "soot.defaultInterfaceMethods.Write";    

    final SootMethod target =
        prepareTarget(
            methodSigFromComponents(testClassSig, voidType, mainClass),
            testClassSig,
            interfaceReadSig,
            interfaceWriteSig);

    SootClass testClass = Scene.v().getSootClass(testClassSig);
    SootClass interfaceRead = Scene.v().getSootClass(interfaceReadSig);
    SootClass interfaceWrite = Scene.v().getSootClass(interfaceWriteSig);

    SootMethod mainPrintMethod =
        Scene.v().getMethod("<soot.defaultInterfaceMethods.InterfaceSameSignature: void print()>");
    SootMethod readInterfacePrint =
        Scene.v().getMethod("<soot.defaultInterfaceMethods.Read: void print()>");
    SootMethod writeInterfacePrint =
        Scene.v().getMethod("<soot.defaultInterfaceMethods.Write: void print()>");
    SootMethod readInterfaceRead =
        Scene.v().getMethod("<soot.defaultInterfaceMethods.Read: void read()>");
    SootMethod writeInterfaceWrite =
        Scene.v().getMethod("<soot.defaultInterfaceMethods.Write: void write()>");

    Body mainBody = target.retrieveActiveBody();
    Body mainPrintBody = mainPrintMethod.retrieveActiveBody();

    SootMethod refMainMethod = resolveMethodRefInBody(mainBody.getUnits(), "void print()");
    SootMethod refWritePrintMethod =
        resolveMethodRefInBody(
            mainPrintBody.getUnits(), "soot.defaultInterfaceMethods.Write: void print()");
    SootMethod refReadPrintMethod =
        resolveMethodRefInBody(
            mainPrintBody.getUnits(), "soot.defaultInterfaceMethods.Read: void print()");
    SootMethod refDefaultRead = resolveMethodRefInBody(mainBody.getUnits(), "void read()");
    SootMethod refDefaultWrite = resolveMethodRefInBody(mainBody.getUnits(), "void write()");

    SootMethod resolvedMainMethod =
        VirtualCalls.v()
            .resolveNonSpecial(
                Scene.v().getRefType(testClassSig), mainPrintMethod.makeRef(), false);
    SootMethod resolvedWritePrintMethod =
        VirtualCalls.v()
            .resolveNonSpecial(
                Scene.v().getRefType(testClassSig), writeInterfacePrint.makeRef(), false);
    SootMethod resolvedReadPrintMethod =
        VirtualCalls.v()
            .resolveNonSpecial(
                Scene.v().getRefType(testClassSig), readInterfacePrint.makeRef(), false);
    SootMethod resolvedDefaultReadMethod =
        VirtualCalls.v()
            .resolveNonSpecial(
                Scene.v().getRefType(testClassSig), readInterfaceRead.makeRef(), false);
    SootMethod resolvedDefaultWriteMethod =
        VirtualCalls.v()
            .resolveNonSpecial(
                Scene.v().getRefType(testClassSig), writeInterfaceWrite.makeRef(), false);

    FastHierarchy fh = Scene.v().getFastHierarchy();
    SootMethod concreteImplMainPrint = fh.resolveConcreteDispatch(testClass, mainPrintMethod);
    SootMethod concreteImplWritePrint = fh.resolveConcreteDispatch(testClass, refWritePrintMethod);
    SootMethod concreteImplReadPrint = fh.resolveConcreteDispatch(testClass, refReadPrintMethod);
    SootMethod concreteImplDefaultRead = fh.resolveConcreteDispatch(testClass, refDefaultRead);
    SootMethod concreteImplDefaultWrite = fh.resolveConcreteDispatch(testClass, refDefaultWrite);

    assertEquals(
        Sets.newHashSet(readInterfaceRead),
        fh.resolveAbstractDispatch(interfaceRead, refDefaultRead));
    assertEquals(
        Sets.newHashSet(writeInterfaceWrite),
        fh.resolveAbstractDispatch(interfaceWrite, refDefaultWrite));
    assertEquals(
        Sets.newHashSet(mainPrintMethod),
        fh.resolveAbstractDispatch(interfaceRead, refReadPrintMethod));
    assertEquals(
        Sets.newHashSet(mainPrintMethod),
        fh.resolveAbstractDispatch(interfaceWrite, refWritePrintMethod));

    /* Edges should be present */
    boolean edgeMainPrintToReadPrint = checkInEdges(readInterfacePrint, mainPrintMethod);
    boolean edgeMainPrintToWritePrint = checkInEdges(writeInterfacePrint, mainPrintMethod);
    boolean edgeMainMethodToPrint = checkInEdges(mainPrintMethod, target);
    boolean edgeMainMethodToReadMethod = checkInEdges(readInterfaceRead, target);
    boolean edgeMainMethodToWriteMethod = checkInEdges(writeInterfaceWrite, target);

    /* Edges should not be present */
    boolean edgeMainMethodToReadPrint = checkInEdges(readInterfacePrint, target);
    boolean edgeMainMethodToWritePrint = checkInEdges(writeInterfacePrint, target);

    final ReachableMethods reachableMethods = Scene.v().getReachableMethods();

    /* Arguments for assert function */
    Map<SootMethod, String> targetMethods =
        new HashMap<SootMethod, String>() {
          {
            put(mainPrintMethod, "print");
            put(readInterfacePrint, "print");
            put(writeInterfacePrint, "print");
            put(readInterfaceRead, "read");
            put(writeInterfaceWrite, "write");
          }
        };

    Map<SootMethod, SootMethod> resolvedMethods =
        new HashMap<SootMethod, SootMethod>() {
          {
            put(mainPrintMethod, resolvedMainMethod);
            put(mainPrintMethod, resolvedWritePrintMethod);
            put(mainPrintMethod, resolvedReadPrintMethod);
            put(readInterfaceRead, resolvedDefaultReadMethod);
            put(writeInterfaceWrite, resolvedDefaultWriteMethod);
          }
        };

    Map<SootMethod, SootMethod> methodRef =
        new HashMap<SootMethod, SootMethod>() {
          {
            put(mainPrintMethod, refMainMethod);
            put(writeInterfacePrint, refWritePrintMethod);
            put(readInterfacePrint, refReadPrintMethod);
            put(readInterfaceRead, refDefaultRead);
            put(writeInterfaceWrite, refDefaultWrite);
          }
        };

    Map<SootMethod, SootMethod> concreteImpl =
        new HashMap<SootMethod, SootMethod>() {
          {
            put(mainPrintMethod, concreteImplMainPrint);
            put(mainPrintMethod, concreteImplWritePrint);
            put(mainPrintMethod, concreteImplReadPrint);
            put(readInterfaceRead, concreteImplDefaultRead);
            put(writeInterfaceWrite, concreteImplDefaultWrite);
          }
        };

    ArrayList<Boolean> edgePresent =
        new ArrayList<Boolean>() {
          {
            add(edgeMainPrintToReadPrint);
            add(edgeMainPrintToWritePrint);
            add(edgeMainMethodToPrint);
            add(edgeMainMethodToReadMethod);
            add(edgeMainMethodToWriteMethod);
          }
        };

    ArrayList<Boolean> edgeNotPresent =
        new ArrayList<Boolean>() {
          {
            add(edgeMainMethodToReadPrint);
            add(edgeMainMethodToWritePrint);
          }
        };

    for (Map.Entry<SootMethod, String> targetMethod : targetMethods.entrySet()) {
      assertNotNull(targetMethod.getKey());
    }
    for (Map.Entry<SootMethod, SootMethod> virtualResolvedMethod : resolvedMethods.entrySet()) {
      assertEquals(virtualResolvedMethod.getKey(), virtualResolvedMethod.getValue());
    }
    for (Map.Entry<SootMethod, SootMethod> methodRef1 : methodRef.entrySet()) {
      assertEquals(methodRef1.getKey(), methodRef1.getValue());
    }
    for (Map.Entry<SootMethod, String> targetMethod : targetMethods.entrySet()) {
      assertEquals(targetMethod.getKey().getName(), targetMethod.getValue());
    }
    for (Map.Entry<SootMethod, String> targetMethod : targetMethods.entrySet()) {
      assertTrue(reachableMethods.contains(targetMethod.getKey()));
    }
    for (boolean isPresent : edgePresent) {
      assertTrue(isPresent);
    }
    for (boolean notPresent : edgeNotPresent) {
      assertFalse(notPresent);
    }
    for (Map.Entry<SootMethod, SootMethod> concreteImpl1 : concreteImpl.entrySet()) {
      assertEquals(concreteImpl1.getKey(), concreteImpl1.getValue());
    }
  }

  @Test
  public void classInterfaceWithSameSignatureTest() {
    String testClass = "soot.defaultInterfaceMethods.ClassInterfaceSameSignature";
    String defaultClass = "soot.defaultInterfaceMethods.HelloWorld";

    final SootMethod target =
        prepareTarget(
            methodSigFromComponents(testClass, voidType, mainClass),
            testClass,
            defaultClass);

    SootMethod mainPrintMethod =
        Scene.v()
            .getMethod("<soot.defaultInterfaceMethods.ClassInterfaceSameSignature: void print()>");
    SootMethod defaultPrintMethod =
        Scene.v().getMethod("<soot.defaultInterfaceMethods.HelloWorld: void print()>");

    Body mainBody = target.retrieveActiveBody();
    SootMethod refMainMethod = resolveMethodRefInBody(mainBody.getUnits(), "void print()");
    SootMethod resolvedMethod =
        VirtualCalls.v()
            .resolveNonSpecial(
                Scene.v().getRefType(testClass), defaultPrintMethod.makeRef(), false);
    SootMethod concreteImpl =
        Scene.v()
            .getFastHierarchy()
            .resolveConcreteDispatch(Scene.v().getSootClass(testClass), defaultPrintMethod);
    Set<SootMethod> abstractImpl =
        Scene.v()
            .getFastHierarchy()
            .resolveAbstractDispatch(Scene.v().getSootClass(defaultClass), defaultPrintMethod);
    boolean edgeMainMethodToMainPrint = checkInEdges(mainPrintMethod, target);
    boolean edgeMainPrintToDefaultPrint = checkInEdges(defaultPrintMethod, target);
    final ReachableMethods reachableMethods = Scene.v().getReachableMethods();

    Map<SootMethod, String> targetMethods =
        new HashMap<SootMethod, String>() {
          {
            put(mainPrintMethod, "print");
            put(defaultPrintMethod, "print");
          }
        };

    ArrayList<Boolean> edgePresent =
        new ArrayList<Boolean>() {
          {
            add(edgeMainMethodToMainPrint);
          }
        };

    for (Map.Entry<SootMethod, String> targetMethod : targetMethods.entrySet()) {
      assertNotNull(targetMethod.getKey());
    }
    assertEquals(mainPrintMethod, resolvedMethod);
    assertEquals(mainPrintMethod, refMainMethod);
    for (Map.Entry<SootMethod, String> targetMethod : targetMethods.entrySet()) {
      assertEquals(targetMethod.getKey().getName(), targetMethod.getValue());
    }

    assertTrue(reachableMethods.contains(mainPrintMethod));

    for (boolean isPresent : edgePresent) {
      assertTrue(isPresent);
    }
    assertEquals(mainPrintMethod, concreteImpl);
    assertTrue(
        abstractImpl.contains(
            Scene.v()
                .getMethod(
                    "<soot.defaultInterfaceMethods.ClassInterfaceSameSignature: void print()>")));
  }

  @Test
  public void superClassInterfaceWithSameSignatureTest() {
    String testClass = "soot.defaultInterfaceMethods.SuperClassInterfaceSameSignature";
    String defaultClass = "soot.defaultInterfaceMethods.PrintInterface";
    String defaultSuperClass = "soot.defaultInterfaceMethods.DefaultPrint";
    String superClassImplementsInterface = "soot.defaultInterfaceMethods.SuperClassImplementsInterface";

    final SootMethod target =
        prepareTarget(
            methodSigFromComponents(testClass, voidType, mainClass),
            testClass,
            defaultClass,
            superClassImplementsInterface);

    SootMethod defaultSuperMainMethod =
        Scene.v()
            .getMethod("<soot.defaultInterfaceMethods.SuperClassImplementsInterface: void main()>");
    SootMethod mainMethod =
        Scene.v()
            .getMethod(
                "<soot.defaultInterfaceMethods.SuperClassImplementsInterface: void print()>");
    SootMethod defaultMethod =
        Scene.v().getMethod("<soot.defaultInterfaceMethods.PrintInterface: void print()>");
    SootMethod defaultSuperClassMethod =
        Scene.v().getMethod("<soot.defaultInterfaceMethods.DefaultPrint: void print()>");

    Body mainBody = target.retrieveActiveBody();
    SootMethod refMainMethod = resolveMethodRefInBody(mainBody.getUnits(), "void print()");

    SootMethod resolvedMethod =
        VirtualCalls.v()
            .resolveNonSpecial(Scene.v().getRefType(testClass), defaultMethod.makeRef(), false);
    SootMethod resolvedSuperClassDefaultMethod =
        VirtualCalls.v()
            .resolveNonSpecial(
                Scene.v().getRefType(testClass), defaultSuperClassMethod.makeRef(), false);

    SootMethod concreteImpl =
        Scene.v()
            .getFastHierarchy()
            .resolveConcreteDispatch(Scene.v().getSootClass(testClass), defaultMethod);

    Set<SootMethod> abstractImpl =
        Scene.v()
            .getFastHierarchy()
            .resolveAbstractDispatch(Scene.v().getSootClass(defaultClass), defaultMethod);
    Set<SootMethod> abstractImplSuperClass =
        Scene.v()
            .getFastHierarchy()
            .resolveAbstractDispatch(
                Scene.v().getSootClass(defaultSuperClass), defaultSuperClassMethod);

    boolean edgeMainToSuperClassPrint = checkInEdges(mainMethod, target);
    boolean edgeMainToDefaultPrint = checkInEdges(defaultMethod, target);
    boolean edgeMainToSuperDefaultPrint = checkInEdges(defaultSuperClassMethod, target);
    boolean edgeSuperMainToSuperPrint =
        checkInEdges(defaultSuperClassMethod, defaultSuperMainMethod);

    final ReachableMethods reachableMethods = Scene.v().getReachableMethods();

    List<SootMethod> targetMethods =
        new ArrayList<SootMethod>() {
          {
            add(mainMethod);
            add(defaultMethod);
            add(defaultSuperClassMethod);
          }
        };

    ArrayList<Boolean> edgeNotPresent =
        new ArrayList<Boolean>() {
          {
            add(edgeMainToDefaultPrint);
            add(edgeMainToSuperDefaultPrint);
            add(edgeSuperMainToSuperPrint);
          }
        };

    Map<SootMethod, SootMethod> resolvedMethods =
        new HashMap<SootMethod, SootMethod>() {
          {
            put(mainMethod, resolvedMethod);
            put(resolvedSuperClassDefaultMethod, resolvedMethod);
          }
        };

    for (SootMethod targetMethod : targetMethods) {
      assertNotNull(targetMethod);
    }
    assertEquals(targetMethods.get(0), refMainMethod);
    assertEquals(targetMethods.get(0).getName(), "print");
    assertTrue(edgeMainToSuperClassPrint);
    for (boolean notPresent : edgeNotPresent) {
      assertFalse(notPresent);
    }
    assertEquals(targetMethods.get(0), concreteImpl);
    assertNotEquals(targetMethods.get(1), concreteImpl);
    assertTrue(
        abstractImpl.contains(
            Scene.v()
                .getMethod(
                    "<soot.defaultInterfaceMethods.SuperClassImplementsInterface: void print()>")));
    assertTrue(
        abstractImplSuperClass.contains(
            Scene.v()
                .getMethod(
                    "<soot.defaultInterfaceMethods.SuperClassImplementsInterface: void print()>")));
  }

  @Test
  public void derivedInterfacesTest() {
    String testClassSig = "soot.defaultInterfaceMethods.DerivedInterfaces";
    String defaultInterfaceOneSig = "soot.defaultInterfaceMethods.InterfaceTestOne";
    String defaultInterfaceTwoSig = "soot.defaultInterfaceMethods.InterfaceTestTwo";

    final SootMethod target =
        prepareTarget(
            methodSigFromComponents(testClassSig, voidType, mainClass),
            testClassSig,
            defaultInterfaceOneSig,
            defaultInterfaceTwoSig);

    FastHierarchy fh = Scene.v().getFastHierarchy();
    SootClass testClass = Scene.v().getSootClass(testClassSig);
    SootClass defaultInterfaceOne = Scene.v().getSootClass(defaultInterfaceOneSig);
    SootClass defaultInterfaceTwo = Scene.v().getSootClass(defaultInterfaceTwoSig);

    SootMethod interfaceOnePrint =
        Scene.v().getMethod(methodSigFromComponents(defaultInterfaceOneSig, "void print()"));
    SootMethod interfaceTwoPrint =
        Scene.v().getMethod(methodSigFromComponents(defaultInterfaceTwoSig, "void print()"));

    SootMethod refMainMethod =
        resolveMethodRefInBody(target.retrieveActiveBody().getUnits(), "void print()");

    SootMethod interfaceOneResolvedMethod =
        VirtualCalls.v().resolveNonSpecial(testClass.getType(), interfaceOnePrint.makeRef(), false);
    SootMethod interfaceTwoResolvedMethod =
        VirtualCalls.v().resolveNonSpecial(testClass.getType(), interfaceTwoPrint.makeRef(), false);

    SootMethod concreteImplInterfaceOne = fh.resolveConcreteDispatch(testClass, interfaceOnePrint);
    SootMethod concreteImplInterfaceTwo = fh.resolveConcreteDispatch(testClass, interfaceTwoPrint);

    Set<SootMethod> abstractImplInterfaceOne =
        fh.resolveAbstractDispatch(defaultInterfaceOne, interfaceOnePrint);

    boolean edgeMainToInterfaceTwoPrint = checkInEdges(interfaceTwoPrint, target);
    boolean edgeMainToInterfaceOnePrint = checkInEdges(interfaceOnePrint, target);

    final ReachableMethods reachableMethods = Scene.v().getReachableMethods();

    List<SootMethod> targetMethods =
        new ArrayList<SootMethod>() {
          {
            add(interfaceOnePrint);
            add(interfaceTwoPrint);
          }
        };

    Map<SootMethod, SootMethod> resolvedMethods =
        new HashMap<SootMethod, SootMethod>() {
          {
            put(interfaceTwoPrint, interfaceOneResolvedMethod);
            put(interfaceTwoPrint, interfaceTwoResolvedMethod);
          }
        };

    Map<SootMethod, SootMethod> concreteImplTrue =
        new HashMap<SootMethod, SootMethod>() {
          {
            put(interfaceTwoPrint, concreteImplInterfaceOne);
            put(interfaceTwoPrint, concreteImplInterfaceTwo);
          }
        };

    Map<SootMethod, SootMethod> concreteImplNotTrue =
        new HashMap<SootMethod, SootMethod>() {
          {
            put(interfaceOnePrint, concreteImplInterfaceOne);
            put(interfaceOnePrint, concreteImplInterfaceTwo);
          }
        };

    for (SootMethod targetMethod : targetMethods) {
      Assert.assertNotNull(targetMethod);
    }
    assertEquals(targetMethods.get(1), refMainMethod);
    assertEquals(targetMethods.get(1).getName(), "print");
    assertFalse(edgeMainToInterfaceOnePrint);
    assertTrue(edgeMainToInterfaceTwoPrint);
    assertTrue(reachableMethods.contains(targetMethods.get(1)));
    assertFalse(reachableMethods.contains(targetMethods.get(0)));
    for (Map.Entry<SootMethod, SootMethod> virtualResolvedMethod : resolvedMethods.entrySet()) {
      assertEquals(virtualResolvedMethod.getKey(), virtualResolvedMethod.getValue());
    }
    for (Map.Entry<SootMethod, SootMethod> concreteImpl : concreteImplTrue.entrySet()) {
      assertEquals(concreteImpl.getKey(), concreteImpl.getValue());
    }
    for (Map.Entry<SootMethod, SootMethod> concreteImpl : concreteImplNotTrue.entrySet()) {
      assertNotEquals(concreteImpl.getKey(), concreteImpl.getValue());
    }
    assertEquals(Sets.newHashSet(targetMethods.get(1)), abstractImplInterfaceOne);

    assertEquals(
        Sets.newHashSet(targetMethods.get(1)),
        fh.resolveAbstractDispatch(defaultInterfaceTwo, interfaceTwoPrint));
  }

  @Test
  public void interfaceInheritanceTest() {
    String testClass = "soot.defaultInterfaceMethods.InterfaceInheritance";
    String defaultClass = "soot.defaultInterfaceMethods.InterfaceTestA";
    String defaultInterface = "soot.defaultInterfaceMethods.InterfaceTestB";

    final SootMethod target =
        prepareTarget(
            methodSigFromComponents(testClass, voidType, mainClass),
            testClass,
            defaultClass,
            defaultInterface);

    SootMethod interfaceTestAPrint =
        Scene.v().getMethod("<soot.defaultInterfaceMethods.InterfaceTestA: void print()>");
    SootMethod mainPrintMessageMethod =
        Scene.v()
            .getMethod("<soot.defaultInterfaceMethods.InterfaceInheritance: void printMessage()>");
    Body mainBody = target.retrieveActiveBody();
    SootMethod refMainMethod = resolveMethodRefInBody(mainBody.getUnits(), "void print()");
    SootMethod resolvedMethod =
        VirtualCalls.v()
            .resolveNonSpecial(
                Scene.v().getRefType(testClass), interfaceTestAPrint.makeRef(), false);
    SootMethod concreteImpl =
        Scene.v()
            .getFastHierarchy()
            .resolveConcreteDispatch(Scene.v().getSootClass(testClass), interfaceTestAPrint);
    Set<SootMethod> abstractImpl =
        Scene.v()
            .getFastHierarchy()
            .resolveAbstractDispatch(Scene.v().getSootClass(defaultClass), interfaceTestAPrint);

    boolean edgeMainToInterfaceTestAPrint = checkInEdges(interfaceTestAPrint, target);
    boolean edgeMainToMainPrintMessage = checkInEdges(mainPrintMessageMethod, target);
    final ReachableMethods reachableMethods = Scene.v().getReachableMethods();

    List<SootMethod> targetMethods =
        new ArrayList<SootMethod>() {
          {
            add(interfaceTestAPrint);
            add(mainPrintMessageMethod);
          }
        };

    for (SootMethod targetMethod : targetMethods) {
      Assert.assertNotNull(targetMethod);
    }
    assertEquals(targetMethods.get(0), refMainMethod);
    assertEquals(targetMethods.get(0).getName(), "print");
    assertTrue(edgeMainToInterfaceTestAPrint);
    assertFalse(edgeMainToMainPrintMessage);
    assertTrue(reachableMethods.contains(targetMethods.get(0)));
    assertFalse(reachableMethods.contains(targetMethods.get(1)));
    assertEquals(targetMethods.get(0), resolvedMethod);
    assertEquals(targetMethods.get(0), concreteImpl);
    assertTrue(abstractImpl.contains(targetMethods.get(0)));
  }

  @Test
  public void interfaceReAbstractionTest() {
    String testClass = "soot.defaultInterfaceMethods.InterfaceReAbstracting";
    String defaultClass = "soot.defaultInterfaceMethods.InterfaceA";
    String defaultInterface = "soot.defaultInterfaceMethods.InterfaceB";

    final SootMethod target =
        prepareTarget(
            methodSigFromComponents(testClass, "void", "main"),
            testClass,
            defaultClass,
            defaultInterface);

    SootMethod interfaceAPrint =
        Scene.v().getMethod("<soot.defaultInterfaceMethods.InterfaceA: void print()>");
    SootMethod mainMethodPrint =
        Scene.v().getMethod("<soot.defaultInterfaceMethods.InterfaceReAbstracting: void print()>");

    Body mainBody = target.retrieveActiveBody();
    SootMethod refMainMethod = resolveMethodRefInBody(mainBody.getUnits(), "void print()");
    SootMethod resolvedMethod =
        VirtualCalls.v()
            .resolveNonSpecial(Scene.v().getRefType(testClass), interfaceAPrint.makeRef(), false);
    SootMethod concreteImpl =
        Scene.v()
            .getFastHierarchy()
            .resolveConcreteDispatch(Scene.v().getSootClass(testClass), interfaceAPrint);
    Set<SootMethod> abstractImpl =
        Scene.v()
            .getFastHierarchy()
            .resolveAbstractDispatch(Scene.v().getSootClass(defaultClass), interfaceAPrint);

    boolean edgeMainMethodToMainPrint = checkInEdges(mainMethodPrint, target);
    boolean edgeMainMethodToInterfaceAPrint = checkInEdges(interfaceAPrint, target);
    final ReachableMethods reachableMethods = Scene.v().getReachableMethods();

    List<SootMethod> targetMethods =
        new ArrayList<SootMethod>() {
          {
            add(mainMethodPrint);
            add(interfaceAPrint);
          }
        };

    for (SootMethod targetMethod : targetMethods) {
      Assert.assertNotNull(targetMethod);
    }
    assertEquals(targetMethods.get(0), refMainMethod);
    assertEquals(targetMethods.get(0).getName(), "print");
    assertTrue(edgeMainMethodToMainPrint);
    assertFalse(edgeMainMethodToInterfaceAPrint);
    assertTrue(reachableMethods.contains(targetMethods.get(0)));
    assertFalse(reachableMethods.contains(targetMethods.get(1)));
    assertEquals(targetMethods.get(0), resolvedMethod);
    assertEquals(targetMethods.get(0), concreteImpl);
    assertNotEquals(targetMethods.get(1), concreteImpl);
    assertEquals(
        Sets.newHashSet(
            Scene.v()
                .getMethod("<soot.defaultInterfaceMethods.InterfaceReAbstracting: void print()>")),
        abstractImpl);
  }

  @Test
  public void superClassPreferenceOverDefaultMethodTest() {
    String testClass = "soot.defaultInterfaceMethods.SuperClassPreferenceOverDefaultMethod";
    String defaultInterfaceOne = "soot.defaultInterfaceMethods.InterfaceOne";
    String defaultInterfaceTwo = "soot.defaultInterfaceMethods.InterfaceTwo";
    String defaultSuperClass = "soot.defaultInterfaceMethods.SuperClass";

    final SootMethod target =
        prepareTarget(
            methodSigFromComponents(testClass, voidType, mainClass),
            testClass,
            defaultInterfaceOne,
            defaultInterfaceTwo,
            defaultSuperClass);

    SootMethod interfaceOnePrint =
        Scene.v().getMethod("<soot.defaultInterfaceMethods.InterfaceOne: void print()>");
    SootMethod interfaceTwoPrint =
        Scene.v().getMethod("<soot.defaultInterfaceMethods.InterfaceTwo: void print()>");
    SootMethod superClassPrint =
        Scene.v().getMethod("<soot.defaultInterfaceMethods.SuperClass: void print()>");

    Body mainBody = target.retrieveActiveBody();
    SootMethod refMainMethod = resolveMethodRefInBody(mainBody.getUnits(), "void print()");

    SootMethod resolvedInterfaceOneDefaultMethod =
        VirtualCalls.v()
            .resolveNonSpecial(Scene.v().getRefType(testClass), interfaceOnePrint.makeRef(), false);
    SootMethod resolvedInterfaceTwoDefaultMethod =
        VirtualCalls.v()
            .resolveNonSpecial(Scene.v().getRefType(testClass), interfaceTwoPrint.makeRef(), false);

    SootMethod concreteImplInterfaceOne =
        Scene.v()
            .getFastHierarchy()
            .resolveConcreteDispatch(Scene.v().getSootClass(testClass), interfaceOnePrint);
    SootMethod concreteImplInterfaceTwo =
        Scene.v()
            .getFastHierarchy()
            .resolveConcreteDispatch(Scene.v().getSootClass(testClass), interfaceTwoPrint);

    Set<SootMethod> abstractImplInterfaceOne =
        Scene.v()
            .getFastHierarchy()
            .resolveAbstractDispatch(
                Scene.v().getSootClass(defaultInterfaceOne), interfaceOnePrint);
    Set<SootMethod> abstractImplInterfaceTwo =
        Scene.v()
            .getFastHierarchy()
            .resolveAbstractDispatch(
                Scene.v().getSootClass(defaultInterfaceTwo), interfaceTwoPrint);

    boolean edgeMainToInterfaceOnePrint = checkInEdges(interfaceOnePrint, target);
    boolean edgeMainToInterfaceTwoPrint = checkInEdges(interfaceTwoPrint, target);
    boolean edgeMainToSuperClassPrint = checkInEdges(superClassPrint, target);

    final ReachableMethods reachableMethods = Scene.v().getReachableMethods();

    List<SootMethod> targetMethods =
        new ArrayList<SootMethod>() {
          {
            add(superClassPrint);
            add(interfaceOnePrint);
            add(interfaceTwoPrint);
          }
        };

    ArrayList<Boolean> edgeNotPresent =
        new ArrayList<Boolean>() {
          {
            add(edgeMainToInterfaceOnePrint);
            add(edgeMainToInterfaceTwoPrint);
          }
        };

    Map<SootMethod, SootMethod> resolvedMethods =
        new HashMap<SootMethod, SootMethod>() {
          {
            put(superClassPrint, resolvedInterfaceOneDefaultMethod);
            put(superClassPrint, resolvedInterfaceTwoDefaultMethod);
          }
        };

    Map<SootMethod, SootMethod> concreteImplTrue =
        new HashMap<SootMethod, SootMethod>() {
          {
            put(superClassPrint, concreteImplInterfaceOne);
            put(superClassPrint, concreteImplInterfaceTwo);
          }
        };

    Map<SootMethod, SootMethod> concreteImplNotTrue =
        new HashMap<SootMethod, SootMethod>() {
          {
            put(interfaceOnePrint, concreteImplInterfaceOne);
            put(interfaceOnePrint, concreteImplInterfaceTwo);
          }
        };

    for (SootMethod targetMethod : targetMethods) {
      assertNotNull(targetMethod);
    }
    assertEquals(targetMethods.get(0), refMainMethod);
    assertEquals(targetMethods.get(0).getName(), "print");
    assertTrue(edgeMainToSuperClassPrint);
    for (boolean notPresent : edgeNotPresent) {
      assertFalse(notPresent);
    }
    assertTrue(reachableMethods.contains(targetMethods.get(0)));
    assertFalse(reachableMethods.contains(targetMethods.get(1)));
    assertFalse(reachableMethods.contains(targetMethods.get(2)));
    for (Map.Entry<SootMethod, SootMethod> virtualResolvedMethod : resolvedMethods.entrySet()) {
      assertEquals(virtualResolvedMethod.getKey(), virtualResolvedMethod.getValue());
    }
    for (Map.Entry<SootMethod, SootMethod> concreteImpl : concreteImplTrue.entrySet()) {
      assertEquals(concreteImpl.getKey(), concreteImpl.getValue());
    }
    for (Map.Entry<SootMethod, SootMethod> concreteImpl : concreteImplNotTrue.entrySet()) {
      assertNotEquals(concreteImpl.getKey(), concreteImpl.getValue());
    }
    assertTrue(
        abstractImplInterfaceOne.contains(
            Scene.v().getMethod("<soot.defaultInterfaceMethods.SuperClass: void print()>")));
    assertTrue(
        abstractImplInterfaceTwo.contains(
            Scene.v().getMethod("<soot.defaultInterfaceMethods.SuperClass: void print()>")));
  }

  private boolean checkInEdges(SootMethod defaultMethod, SootMethod targetMethod) {

    boolean isPresent = false;
    Iterator<Edge> inEdges = Scene.v().getCallGraph().edgesInto(defaultMethod);
    while (inEdges.hasNext()) {
      MethodOrMethodContext sourceMethod = inEdges.next().getSrc();
      if (sourceMethod.equals(targetMethod)) {
        isPresent = true;
      }
    }
    return isPresent;
  }

  /**
   * Searches the given unit chain for the first call to a method with the given subsignature and
   * resolves it's MethodRef
   *
   * @param units
   * @param methodSubSig
   * @return
   */
  private SootMethod resolveMethodRefInBody(UnitPatchingChain units, String methodSubSig) {

    SootMethod method = null;
    for (Unit unit : units) {
      Stmt s = (Stmt) unit;
      if (s.containsInvokeExpr()
          && s.getInvokeExpr().getMethodRef().getSignature().contains(methodSubSig)) {
        return s.getInvokeExpr().getMethodRef().tryResolve();
      }
    }
    return null;
  }

  @Test
  public void maximallySpecificSuperInterface() {
    String targetClassName = "soot.defaultInterfaceMethods.MaximallySpecificSuperInterface";
    String superClass = "soot.defaultInterfaceMethods.B";
    String subInterface = "soot.defaultInterfaceMethods.C";
    String superInterface = "soot.defaultInterfaceMethods.D";

    final SootMethod mainMethod =
        prepareTarget(
            methodSigFromComponents(targetClassName, voidType, mainClass),
            targetClassName,
            superClass,
            subInterface,
            superInterface);

    SootClass testClass = mainMethod.getDeclaringClass();

    SootMethod subInterfacePrint =
        Scene.v().getMethod(methodSigFromComponents(subInterface, "void print()"));
    SootMethod superInterfacePrint =
        Scene.v().getMethod(methodSigFromComponents(superInterface, "void print()"));

    SootMethod methodRefResolved =
        resolveMethodRefInBody(mainMethod.retrieveActiveBody().getUnits(), "void print()");
    assertEquals(subInterfacePrint, methodRefResolved);

    SootMethod virtualCallsResolved =
        VirtualCalls.v()
            .resolveNonSpecial(testClass.getType(), superInterfacePrint.makeRef(), false);
    assertEquals(subInterfacePrint, virtualCallsResolved);

    SootMethod concreteImplI1 =
        Scene.v().getFastHierarchy().resolveConcreteDispatch(testClass, superInterfacePrint);
    assertEquals(subInterfacePrint, concreteImplI1);

    Set<SootMethod> abstractImpl =
        Scene.v()
            .getFastHierarchy()
            .resolveAbstractDispatch(superInterfacePrint.getDeclaringClass(), superInterfacePrint);
    assertEquals(Sets.newHashSet(subInterfacePrint), abstractImpl);

    assertTrue(checkInEdges(subInterfacePrint, mainMethod));
    assertTrue(Scene.v().getReachableMethods().contains(subInterfacePrint));
  }
  
  private static ArrayList<Edge> GetCallGraph() {			
		CallGraph cg = Scene.v().getCallGraph();
		Iterator<Edge> mainMethodEdges = cg.edgesOutOf(Scene.v().getMethod("<soot.defaultInterfaceMethods.JavaNCSSCheck: void finishTree()>"));
		ArrayList<Edge> edgeList = Lists.newArrayList(mainMethodEdges);
		return edgeList;		
	}
}
