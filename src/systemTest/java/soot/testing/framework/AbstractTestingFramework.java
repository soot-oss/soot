package soot.testing.framework;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2018 Manuel Benz
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import soot.ArrayType;
import soot.G;
import soot.Local;
import soot.Modifier;
import soot.PackManager;
import soot.PhaseOptions;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.VoidType;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.NullConstant;
import soot.options.Options;

/**
 * @author Manuel Benz created on 22.06.18
 */
@RunWith(PowerMockRunner.class)
public abstract class AbstractTestingFramework {

  private static final String SYSTEMTEST_TARGET_CLASSES_DIR = "target/systemTest-target-classes";

  /**
   * Sets up the Scene by analyzing all included classes and generating a call graph for the given target. This is done by
   * generating an entry point that calls the method with the given signature.
   * <p>
   * <p>
   * Important: Every test case has to call this method with an appropriate target before testing something.
   *
   * @param targetMethodSignature
   *          Signature of the method to be analyzed
   * @param classesOrPackagesToAnalyze
   *          Defines the list of classes/packages that are included when building the Scene. State package with wildcards,
   *          e.g., "soot.*" to include all classes in the soot package. Note that it is good practice to include all classes
   *          that are tested (or the complete package) explicitly, to ensure they are not excluded when building the Scene.
   */
  protected SootMethod prepareTarget(String targetMethodSignature, String... classesOrPackagesToAnalyze) {
    return prepareTarget(targetMethodSignature, Arrays.asList(classesOrPackagesToAnalyze));
  }

  /**
   * Sets up the Scene by analyzing all included classes and generating a call graph for the given target. This is done by
   * generating an entry point that calls the method with the given signature.
   * <p>
   * <p>
   * Important: Every test case has to call this method with an appropriate target before testing something.
   *
   * @param targetMethodSignature
   *          Signature of the method to be analyzed
   * @param classesOrPackagesToAnalyze
   *          Defines the list of classes/packages that are included when building the Scene. State package with wildcards,
   *          e.g., "soot.*" to include all classes in the soot package. Note that it is good practice to include all classes
   *          that are tested (or the complete package) explicitly, to ensure they are not excluded when building the Scene.
   */
  protected SootMethod prepareTarget(String targetMethodSignature, Collection<String> classesOrPackagesToAnalyze) {
    setupSoot(classesOrPackagesToAnalyze);
    mockStatics();
    SootMethod sootTestMethod = createTestTarget(targetMethodSignature);
    runSoot();
    return sootTestMethod;
  }

  /**
   * Can be used to mock static members with PowerMock if needed. Is run right after Soot was initialized and before the
   * testing target is acquired
   */
  protected void mockStatics() {

  }

  protected void runSoot() {
    PackManager.v().getPack("wjpp").apply();
    PackManager.v().getPack("cg").apply();
    PackManager.v().getPack("wjpp").apply();
  }

  /**
   * Sets common options for all test cases
   * 
   * @param classesOrPackagesToAnalyze
   */
  private void setupSoot(Collection<String> classesOrPackagesToAnalyze) {
    G.reset();
    Options.v().set_whole_program(true);
    Options.v().set_output_format(Options.output_format_none);
    Options.v().set_allow_phantom_refs(true);
    Options.v().set_no_bodies_for_excluded(true);
    Options.v().set_exclude(getExcludes());
    Options.v().set_include(new ArrayList<>(classesOrPackagesToAnalyze));
    Options.v().set_process_dir(Collections.singletonList(SYSTEMTEST_TARGET_CLASSES_DIR));
    setupSoot();
  }

  /**
   * Can be used to set Soot options as needed
   */
  protected void setupSoot() {
    PhaseOptions.v().setPhaseOption("cg.spark", "on");
  }

  /**
   * Defines the list of classes/packages that are excluded when building the Scene. State package with wildcards, e.g.,
   * "soot.*" to exclude all classes in the soot package.
   * <p>
   * <p>
   * Note that it is good practice to exclude everything and include only the needed classes for the specific test case when
   * calling {@link AbstractTestingFramework#prepareTarget(String, String...)}
   * 
   * @return A list of excluded packages and classes
   */
  protected List<String> getExcludes() {
    List<String> excludeList = new ArrayList<>();
    excludeList.add("java.*");
    excludeList.add("sun.*");
    excludeList.add("android.*");
    excludeList.add("org.apache.*");
    excludeList.add("org.eclipse.*");
    excludeList.add("soot.*");
    excludeList.add("javax.*");
    return excludeList;
  }

  private SootMethod createTestTarget(String targetMethod) {
    SootMethod sootTestMethod = getMethodForSig(targetMethod);
    if (sootTestMethod == null) {
      throw new RuntimeException("The method with name " + targetMethod + " was not found in the Soot Scene.");
    }
    String targetClass = makeDummyClass(sootTestMethod);
    Scene.v().addBasicClass(targetClass, SootClass.BODIES);
    Scene.v().loadNecessaryClasses();
    SootClass c = Scene.v().forceResolve(targetClass, SootClass.BODIES);
    c.setApplicationClass();
    Scene.v().setEntryPoints(Collections.singletonList(c.getMethodByName("main")));
    return sootTestMethod;
  }

  private String makeDummyClass(SootMethod sootTestMethod) {
    SootClass sootClass = new SootClass("dummyClass");
    SootMethod mainMethod
        = new SootMethod("main", Arrays.asList(new Type[] { ArrayType.v(RefType.v("java.lang.String"), 1) }), VoidType.v(),
            Modifier.PUBLIC | Modifier.STATIC);
    sootClass.addMethod(mainMethod);

    JimpleBody body = Jimple.v().newBody(mainMethod);
    mainMethod.setActiveBody(body);
    RefType testCaseType = RefType.v(sootTestMethod.getDeclaringClass());
    Local allocatedTestObj = Jimple.v().newLocal("dummyObj", testCaseType);
    body.getLocals().add(allocatedTestObj);
    body.getUnits().add(Jimple.v().newAssignStmt(allocatedTestObj, Jimple.v().newNewExpr(testCaseType)));
    ArrayList args = new ArrayList(sootTestMethod.getParameterCount());
    for (int i = 0; i < sootTestMethod.getParameterCount(); i++) {
      args.add(NullConstant.v());
    }
    body.getUnits()
        .add(Jimple.v().newInvokeStmt(Jimple.v().newVirtualInvokeExpr(allocatedTestObj, sootTestMethod.makeRef(), args)));

    Scene.v().addClass(sootClass);
    return sootClass.toString();
  }

  private String classFromSignature(String targetMethod) {
    return targetMethod.substring(1, targetMethod.indexOf(":"));
  }

  private SootMethod getMethodForSig(String sig) {
    Scene.v().forceResolve(classFromSignature(sig), SootClass.BODIES);
    return Scene.v().getMethod(sig);
  }
}
