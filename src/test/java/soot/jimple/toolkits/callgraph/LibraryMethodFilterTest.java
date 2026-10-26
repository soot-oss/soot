package soot.jimple.toolkits.callgraph;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2026 Soot contributors
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Before;
import org.junit.Test;

import soot.Body;
import soot.G;
import soot.Local;
import soot.Modifier;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.VoidType;
import soot.jimple.Jimple;
import soot.jimple.spark.SparkTransformer;
import soot.jimple.spark.builder.ContextInsensitiveBuilder;
import soot.jimple.spark.geom.geomPA.GeomPointsTo;
import soot.jimple.spark.geom.helper.GeomEvaluator;
import soot.jimple.spark.internal.SparkNativeHelper;
import soot.jimple.spark.pag.MethodPAG;
import soot.jimple.spark.pag.PAG;
import soot.jimple.spark.pag.VarNode;
import soot.jimple.toolkits.pointer.representations.ReferenceVariable;
import soot.jimple.toolkits.pointer.util.NativeMethodDriver;
import soot.options.Options;
import soot.options.SparkOptions;

/** Regression tests for keeping library ownership separate from body expansion. */
public class LibraryMethodFilterTest {
  private SootClass api;
  private SootMethod modeled;

  @Before
  public void setUp() {
    G.reset();
    Options.v().set_allow_phantom_refs(true);
    Options.v().set_prepend_classpath(true);
    Scene.v().loadBasicClasses();
    api = new SootClass("example.LibraryApi", Modifier.PUBLIC);
    api.setSuperclass(Scene.v().getSootClass("java.lang.Object"));
    Scene.v().addClass(api);
    api.setLibraryClass();
    modeled = new SootMethod("forward", Collections.emptyList(), VoidType.v(), Modifier.PUBLIC);
    api.addMethod(modeled);
  }

  @Test
  public void defaultAppOnlyResolutionStillExcludesLibraryTargets() {
    assertFalse(Scene.v().hasLibraryMethodFilter());
    assertNull(VirtualCalls.v().resolveNonSpecial(api.getType(), modeled.makeRef(), true));
  }

  @Test
  public void absentFilterDoesNotRestrictLibraryMethods() {
    assertTrue(Scene.v().isMethodAnalysisAllowed(modeled));
    assertSame(modeled, VirtualCalls.v().resolveNonSpecial(api.getType(), modeled.makeRef(), false));
    assertNull(VirtualCalls.v().resolveNonSpecial(api.getType(), modeled.makeRef(), true));
    assertTrue(api.isLibraryClass());
  }

  @Test
  public void opaqueLibraryTargetIsRetainedWithoutPromotingItsClass() {
    Scene.v().setLibraryMethodFilter(method -> false);
    assertSame(modeled, VirtualCalls.v().resolveNonSpecial(api.getType(), modeled.makeRef(), true));
    assertFalse(Scene.v().isMethodAnalysisAllowed(modeled));
    assertTrue(api.isLibraryClass());
    Scene.v().setLibraryMethodFilter(null);
    assertTrue(Scene.v().isMethodAnalysisAllowed(modeled));
    assertNull(VirtualCalls.v().resolveNonSpecial(api.getType(), modeled.makeRef(), true));
  }

  @Test
  public void onlySelectedMethodsOfTheSameLibraryClassAreAllowed() {
    SootMethod other = new SootMethod("other", Collections.emptyList(), VoidType.v(), Modifier.PUBLIC);
    api.addMethod(other);
    Scene.v().setLibraryMethodFilter(method -> method == modeled);
    assertTrue(Scene.v().isMethodAnalysisAllowed(modeled));
    assertFalse(Scene.v().isMethodAnalysisAllowed(other));
    assertTrue(api.isLibraryClass());
  }

  @Test
  public void forbiddenBodyIsNotLoadedAndMiscellaneousConstraintsRemain() {
    modeled.setSource((method, phase) -> {
      throw new AssertionError("An opaque library body must not be loaded");
    });
    Scene.v().setLibraryMethodFilter(method -> false);
    MethodPAG.v(new PAG(new SparkOptions(Collections.emptyMap())), modeled).build();
    assertFalse(modeled.hasActiveBody());

    SootClass loader = Scene.v().getSootClass("java.lang.ClassLoader");
    SootMethod constructor = loader.getMethod("void <init>()");
    constructor.releaseActiveBody();
    constructor.setSource((method, phase) -> {
      throw new AssertionError("An opaque ClassLoader constructor must not be loaded");
    });
    PAG pag = new PAG(new SparkOptions(Collections.emptyMap()));
    MethodPAG methodPag = MethodPAG.v(pag, constructor);
    methodPag.build();
    methodPag.addToPAG(null);
    assertTrue(Arrays.asList(pag.simpleLookup((VarNode) pag.nodeFactory().caseDefaultClassLoader()))
        .contains(methodPag.nodeFactory().caseThis()));
    assertFalse(constructor.hasActiveBody());
  }

  @Test
  public void nativeSimulationRemainsAvailableForOpaqueLibraries() {
    SootMethod nativeMethod = new SootMethod("nativeCall", Collections.emptyList(), VoidType.v(),
        Modifier.PUBLIC | Modifier.NATIVE);
    api.addMethod(nativeMethod);
    Scene.v().setLibraryMethodFilter(method -> false);
    PAG pag = new PAG(new SparkOptions(Collections.singletonMap("simulate-natives", "true")));
    AtomicBoolean simulated = new AtomicBoolean();
    pag.nativeMethodDriver = new NativeMethodDriver(new SparkNativeHelper(pag)) {
      @Override
      public boolean process(SootMethod method, ReferenceVariable thisVar, ReferenceVariable returnVar,
          ReferenceVariable[] params) {
        assertSame(nativeMethod, method);
        simulated.set(true);
        return true;
      }
    };
    MethodPAG.v(pag, nativeMethod).build();
    assertTrue(simulated.get());
    assertFalse(nativeMethod.hasActiveBody());
  }

  @Test
  public void selectedLibraryBodyParticipatesInRealSpark() {
    assertSelectedLibraryAnalysis(true, 0);
  }

  @Test
  public void selectedLibraryBodyParticipatesInWholeProgramSpark() {
    assertSelectedLibraryAnalysis(false, 0);
  }

  @Test
  public void geometricBasicEvaluationRespectsLibraryFilter() {
    assertSelectedLibraryAnalysis(true, 1);
  }

  @Test
  public void geometricClientEvaluationRespectsLibraryFilter() {
    assertSelectedLibraryAnalysis(true, 2);
  }

  private void assertSelectedLibraryAnalysis(boolean appOnly, int geomEvaluation) {
    SootClass app = new SootClass("example.Application", Modifier.PUBLIC);
    app.setSuperclass(Scene.v().getSootClass("java.lang.Object"));
    Scene.v().addClass(app);
    app.setApplicationClass();
    SootMethod callback = new SootMethod("callback", Collections.emptyList(), VoidType.v(),
        Modifier.PUBLIC | Modifier.STATIC);
    app.addMethod(callback);
    Body callbackBody = Jimple.v().newBody(callback);
    callback.setActiveBody(callbackBody);
    callbackBody.getUnits().add(Jimple.v().newReturnVoidStmt());

    modeled.setReturnType(api.getType());
    Body modelBody = Jimple.v().newBody(modeled);
    modeled.setSource((method, phase) -> modelBody);
    Local receiver = Jimple.v().newLocal("this", api.getType());
    Local created = Jimple.v().newLocal("created", api.getType());
    modelBody.getLocals().add(receiver);
    modelBody.getLocals().add(created);
    modelBody.getUnits().add(Jimple.v().newIdentityStmt(receiver, Jimple.v().newThisRef(api.getType())));
    modelBody.getUnits().add(Jimple.v().newAssignStmt(created, Jimple.v().newNewExpr(api.getType())));
    modelBody.getUnits().add(Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(callback.makeRef())));
    modelBody.getUnits().add(Jimple.v().newReturnStmt(created));

    SootMethod opaque = new SootMethod("opaque", Collections.emptyList(), VoidType.v(), Modifier.PUBLIC);
    api.addMethod(opaque);
    opaque.setSource((method, phase) -> {
      throw new AssertionError("SPARK must retain the target without inspecting the opaque body");
    });
    SootMethod entry = new SootMethod("entry", Collections.emptyList(), VoidType.v(), Modifier.PUBLIC | Modifier.STATIC);
    app.addMethod(entry);
    Body entryBody = Jimple.v().newBody(entry);
    entry.setActiveBody(entryBody);
    Local instance = Jimple.v().newLocal("api", api.getType());
    Local result = Jimple.v().newLocal("result", api.getType());
    entryBody.getLocals().add(instance);
    entryBody.getLocals().add(result);
    entryBody.getUnits().add(Jimple.v().newAssignStmt(instance, Jimple.v().newNewExpr(api.getType())));
    entryBody.getUnits().add(Jimple.v().newAssignStmt(result,
        Jimple.v().newVirtualInvokeExpr(instance, modeled.makeRef())));
    entryBody.getUnits().add(Jimple.v().newInvokeStmt(Jimple.v().newVirtualInvokeExpr(instance, opaque.makeRef())));
    entryBody.getUnits().add(Jimple.v().newReturnVoidStmt());
    Scene.v().setEntryPoints(Collections.singletonList(entry));
    Scene.v().setLibraryMethodFilter(method -> method == modeled);
    Map<String, String> options = new HashMap<>(soot.PhaseOptions.v().getPhaseOptions("cg.spark"));
    options.put("enabled", "true");
    options.put("on-fly-cg", "true");
    options.put("apponly", Boolean.toString(appOnly));
    options.put("simulate-natives", "false");
    if (geomEvaluation > 0) {
      options.put("geom-pta", "true");
      options.put("geom-eval", Integer.toString(geomEvaluation));
      options.put("simplify-offline", "false");
      options.put("simplify-sccs", "false");
    }
    SparkTransformer.v().transform("cg.spark", options);

    CallGraph graph = Scene.v().getCallGraph();
    assertTrue("The modeled library target must remain in the call graph", graph.edgesOutOf(entry).hasNext());
    assertTrue("SPARK must expand the selected model body", hasEdge(graph, entry, modeled));
    assertTrue("Opaque targets must remain available for downstream summaries", hasEdge(graph, entry, opaque));
    assertTrue("Calls inside the selected model must be discovered", hasEdge(graph, modeled, callback));
    assertFalse("The concrete receiver must participate in points-to analysis",
        Scene.v().getPointsToAnalysis().reachingObjects(instance).isEmpty());
    assertFalse("The selected library body must contribute allocation constraints",
        Scene.v().getPointsToAnalysis().reachingObjects(created).isEmpty());
    assertTrue("The allocation inside the library must flow to the caller's result",
        Scene.v().getPointsToAnalysis().reachingObjects(result)
            .hasNonEmptyIntersection(Scene.v().getPointsToAnalysis().reachingObjects(created)));
    assertFalse(opaque.hasActiveBody());
    assertTrue(api.isLibraryClass());
  }

  @Test
  public void preJimplifyOnlyLoadsAcceptedLibraryAndApplicationBodies() {
    G.reset();
    SootClass library = new SootClass("example.PreloadedLibrary", Modifier.PUBLIC);
    Scene.v().addClass(library);
    library.setLibraryClass();
    SootMethod selected = addLazyStaticMethod(library, "selected");
    SootMethod rejected = addLazyStaticMethod(library, "rejected");
    rejected.setSource((method, phase) -> {
      throw new AssertionError("Pre-jimplify must not load a rejected library method");
    });
    SootClass app = new SootClass("example.PreloadedApplication", Modifier.PUBLIC);
    Scene.v().addClass(app);
    app.setApplicationClass();
    SootMethod application = addLazyStaticMethod(app, "entry");
    Scene.v().setLibraryMethodFilter(method -> method == selected);

    new ContextInsensitiveBuilder().preJimplify();

    assertTrue(selected.hasActiveBody());
    assertTrue(application.hasActiveBody());
    assertFalse(rejected.hasActiveBody());
    assertTrue(library.isLibraryClass());
  }

  @Test
  public void preJimplifyRetainsDefaultBehaviorAfterClearingFilter() {
    G.reset();
    SootClass library = new SootClass("example.UnfilteredLibrary", Modifier.PUBLIC);
    Scene.v().addClass(library);
    library.setLibraryClass();
    SootMethod method = addLazyStaticMethod(library, "entry");
    Scene.v().setLibraryMethodFilter(target -> false);
    Scene.v().setLibraryMethodFilter(null);

    new ContextInsensitiveBuilder().preJimplify();

    assertTrue(method.hasActiveBody());
    assertTrue(library.isLibraryClass());
  }

  @Test
  public void aliasEvaluationDoesNotLoadRejectedLibraryBodies() {
    evaluatorForRejectedLibraryMethod().checkAliasAnalysis();
  }

  @Test
  public void castEvaluationDoesNotLoadRejectedLibraryBodies() {
    evaluatorForRejectedLibraryMethod().checkCastsSafety();
  }

  @Test
  public void heapEvaluationDoesNotLoadRejectedLibraryBodies() {
    evaluatorForRejectedLibraryMethod().estimateHeapDefuseGraph();
  }

  private GeomEvaluator evaluatorForRejectedLibraryMethod() {
    modeled.setSource((method, phase) -> {
      throw new AssertionError("Evaluation must not load a rejected library method");
    });
    Scene.v().setLibraryMethodFilter(method -> false);
    GeomPointsTo pointsTo = new GeomPointsTo(new SparkOptions(Collections.emptyMap())) {
      @Override
      public Set<SootMethod> getAllReachableMethods() {
        return Collections.singleton(modeled);
      }
    };
    pointsTo.ps = new PrintStream(new ByteArrayOutputStream());
    return new GeomEvaluator(pointsTo, pointsTo.ps);
  }

  private static SootMethod addLazyStaticMethod(SootClass owner, String name) {
    SootMethod method = new SootMethod(name, Collections.emptyList(), VoidType.v(), Modifier.PUBLIC | Modifier.STATIC);
    owner.addMethod(method);
    method.setSource((target, phase) -> {
      Body body = Jimple.v().newBody(target);
      body.getUnits().add(Jimple.v().newReturnVoidStmt());
      return body;
    });
    return method;
  }

  @Test
  public void applicationBodiesRemainAllowedAndResetClearsThePolicy() {
    SootClass app = new SootClass("example.App", Modifier.PUBLIC);
    Scene.v().addClass(app);
    app.setApplicationClass();
    SootMethod method = new SootMethod("entry", Collections.emptyList(), VoidType.v(), Modifier.PUBLIC | Modifier.STATIC);
    app.addMethod(method);
    Scene.v().setLibraryMethodFilter(libraryMethod -> false);
    assertTrue(Scene.v().isMethodAnalysisAllowed(method));
    G.reset();
    assertFalse(Scene.v().hasLibraryMethodFilter());
    assertTrue(Scene.v().isMethodAnalysisAllowed(modeled));
  }

  @Test
  public void defaultSparkExpandsLibraryBodiesWhenAppOnlyIsDisabled() {
    assertDefaultLibraryAnalysis(false);
  }

  @Test
  public void defaultSparkStillExcludesVirtualLibraryTargetsWhenAppOnlyIsEnabled() {
    assertDefaultLibraryAnalysis(true);
  }

  private void assertDefaultLibraryAnalysis(boolean appOnly) {
    SootClass app = new SootClass("example.DefaultApplication", Modifier.PUBLIC);
    app.setSuperclass(Scene.v().getSootClass("java.lang.Object"));
    Scene.v().addClass(app);
    app.setApplicationClass();
    SootMethod callback = new SootMethod("callback", Collections.emptyList(), VoidType.v(),
        Modifier.PUBLIC | Modifier.STATIC);
    app.addMethod(callback);
    Body callbackBody = Jimple.v().newBody(callback);
    callback.setActiveBody(callbackBody);
    callbackBody.getUnits().add(Jimple.v().newReturnVoidStmt());
    modeled.setSource((method, phase) -> {
      Body body = Jimple.v().newBody(method);
      Local receiver = Jimple.v().newLocal("this", api.getType());
      body.getLocals().add(receiver);
      body.getUnits().add(Jimple.v().newIdentityStmt(receiver, Jimple.v().newThisRef(api.getType())));
      body.getUnits().add(Jimple.v().newInvokeStmt(Jimple.v().newStaticInvokeExpr(callback.makeRef())));
      body.getUnits().add(Jimple.v().newReturnVoidStmt());
      return body;
    });

    SootMethod entry = new SootMethod("entry", Collections.emptyList(), VoidType.v(), Modifier.PUBLIC | Modifier.STATIC);
    app.addMethod(entry);
    Body entryBody = Jimple.v().newBody(entry);
    entry.setActiveBody(entryBody);
    Local instance = Jimple.v().newLocal("api", api.getType());
    entryBody.getLocals().add(instance);
    entryBody.getUnits().add(Jimple.v().newAssignStmt(instance, Jimple.v().newNewExpr(api.getType())));
    entryBody.getUnits().add(Jimple.v().newInvokeStmt(Jimple.v().newVirtualInvokeExpr(instance, modeled.makeRef())));
    entryBody.getUnits().add(Jimple.v().newReturnVoidStmt());
    Scene.v().setEntryPoints(Collections.singletonList(entry));
    Map<String, String> options = new HashMap<>(soot.PhaseOptions.v().getPhaseOptions("cg.spark"));
    options.put("enabled", "true");
    options.put("on-fly-cg", "true");
    options.put("apponly", Boolean.toString(appOnly));
    options.put("simulate-natives", "false");
    SparkTransformer.v().transform("cg.spark", options);

    CallGraph graph = Scene.v().getCallGraph();
    if (appOnly) {
      assertFalse(hasEdge(graph, entry, modeled));
      assertFalse(hasEdge(graph, modeled, callback));
      assertFalse(modeled.hasActiveBody());
    } else {
      assertTrue(hasEdge(graph, entry, modeled));
      assertTrue(hasEdge(graph, modeled, callback));
      assertTrue(modeled.hasActiveBody());
    }
    assertTrue(api.isLibraryClass());
  }

  private static boolean hasEdge(CallGraph graph, SootMethod from, SootMethod to) {
    Iterator<Edge> edges = graph.edgesOutOf(from);
    while (edges.hasNext()) {
      if (edges.next().tgt() == to) {
        return true;
      }
    }
    return false;
  }
}
