package soot.testing.framework;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
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
 * @author Manuel Benz created on 01.09.17
 */
@RunWith(PowerMockRunner.class)
public abstract class AbstractTestingFramework {

  private static final String SYSTEMTEST_TARGET_CLASSES_DIR = "target/systemTest-target-classes";

  /**
   * Sets up the Scene by analyzing all included classes and generating a call graph for the given target. This is done by
   * generating an entry point that calls the method with the given signature.
   * <p>
   * <p>
   * * Important: Every test case has to call this method with an appropriate target before testing something.
   *
   * @param targetMethodSignature
   *          Signature of the method to be analyzed
   */
  protected SootMethod prepareTarget(String targetMethodSignature) {
    setupSoot();
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

  protected void setupSoot() {
    G.reset();
    Options.v().set_whole_program(true);
    Options.v().set_output_format(Options.output_format_none);
    Options.v().set_allow_phantom_refs(true);
    Options.v().set_no_bodies_for_excluded(true);
    Options.v().set_exclude(getExcludes());
    Options.v().set_include(getIncludes());
    PhaseOptions.v().setPhaseOption("cg.spark", "on");
    Options.v().set_process_dir(Collections.singletonList(SYSTEMTEST_TARGET_CLASSES_DIR));
  }

  /**
   * Defines the list of classes/packages that are excluded when building the Scene. State package with wildcards, e.g.,
   * "soot.*" to exclude all classes in the soot package.
   * 
   * @return A list of excluded packages and classes
   */
  protected List<String> getExcludes() {
    List<String> excludeList = new LinkedList<>();
    excludeList.add("java.*");
    excludeList.add("sun.*");
    excludeList.add("android.*");
    excludeList.add("org.apache.*");
    excludeList.add("org.eclipse.*");
    excludeList.add("soot.*");
    excludeList.add("javax.*");
    return excludeList;
  }

  /**
   * Defines the list of classes/packages that are included when building the Scene. State package with wildcards, e.g.,
   * "soot.*" to exclude all classes in the soot package.
   *
   * <p>
   * <p>
   * Note that it is good practice to include the solo class that is tested or the complete package if multiple classes are
   * tested, to ensure they are not excluded when building the Scene
   *
   * @return A list of included packages and classes
   */
  protected abstract List<String> getIncludes();

  private SootMethod createTestTarget(String targetMethod) {
    SootMethod sootTestMethod = getMethodForSig(targetMethod);
    if (sootTestMethod == null) {
      throw new RuntimeException("The method with name " + targetMethod + " was not found in the Soot Scene.");
    }
    String targetClass = makeDummyClass(sootTestMethod);
    Scene.v().addBasicClass(targetClass, SootClass.BODIES);
    Scene.v().loadNecessaryClasses();
    SootClass c = Scene.v().forceResolve(targetClass, SootClass.BODIES);
    if (c != null) {
      c.setApplicationClass();
    }
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

  protected SootMethod getMethodForSig(String sig) {
    Scene.v().forceResolve(classFromSignature(sig), SootClass.BODIES);
    return Scene.v().getMethod(sig);
  }
}
