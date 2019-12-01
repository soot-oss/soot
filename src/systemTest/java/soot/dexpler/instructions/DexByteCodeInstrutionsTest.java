package soot.dexpler.instructions;

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

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

import soot.ModulePathSourceLocator;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.UnitPatchingChain;
import soot.jimple.DynamicInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.VirtualInvokeExpr;
import soot.options.Options;
import soot.testing.framework.AbstractTestingFramework;

/**
 * @author Manuel Benz created on 22.10.18
 */

public class DexByteCodeInstrutionsTest extends AbstractTestingFramework {

  private static final String METHOD_HANDLE_CLASS = "java.lang.invoke.MethodHandle";
  private static final String TARGET_CLASS = "soot.dexpler.instructions.DexBytecodeTarget";
  private static final String METHOD_HANDLE_INVOKE_SUBSIG = "java.lang.Object invoke(java.lang.Object[])";
  private static final String SUPPLIER_GET_SUBSIG = "java.util.function.Supplier get()";

  @Override
  protected void setupSoot() {
    super.setupSoot();
    Options.v().set_src_prec(Options.src_prec_apk);
    // to get the basic classes; java.lang.Object, java.lang.Throwable, ... we add the rt.jar to the classpath
    String rtJar = "";
    if (Scene.isJavaGEQ9(System.getProperty("java.version"))) {
      rtJar = ModulePathSourceLocator.DUMMY_CLASSPATH_JDK9_FS;
    } else {
      rtJar = System.getProperty("java.home") + File.separator + "lib" + File.separator + "rt.jar";

    }

    Options.v().set_process_dir(Arrays.asList(targetDexPath(), rtJar));
    Options.v().set_force_android_jar(androidJarPath());
    Options.v().set_android_api_version(26);
  }

  @Override
  protected void runSoot() {
    // we do not want to have a call graph for this test
  }

  private String androidJarPath() {
    // this is not the nicest thing. Make sure to keep the version in sync with the pom
    // also .m2 repository could fail
    return System.getProperty("user.home") + "/.m2/repository/" + "com/google/android/android/4.1.1.4/android-4.1.1.4.jar";
  }

  private String targetDexPath() {
    final URL targetDex = getClass().getResource("dexBytecodeTarget.dex");
    try {
      return targetDex.toURI().getPath();
    } catch (URISyntaxException e) {
      throw new RuntimeException("Exception loading test resources", e);
    }
  }

  @Test
  public void InvokePolymorphic1() {
    final SootMethod testTarget = prepareTarget(
        methodSigFromComponents(TARGET_CLASS, "void invokePolymorphicTarget(java.lang.invoke.MethodHandle)"), TARGET_CLASS);

    // We model invokePolymorphic as invokeVirtual
    final List<InvokeExpr> invokes = invokesFromMethod(testTarget);
    Assert.assertEquals(1, invokes.size());
    final InvokeExpr invokePoly = invokes.get(0);
    Assert.assertTrue(invokePoly instanceof VirtualInvokeExpr);
    final SootMethodRef targetMethodRef = invokePoly.getMethodRef();
    Assert.assertEquals(methodSigFromComponents(METHOD_HANDLE_CLASS, METHOD_HANDLE_INVOKE_SUBSIG),
        targetMethodRef.getSignature());
  }

  @Test
  public void InvokeCustom1() {
    final SootMethod testTarget
        = prepareTarget(methodSigFromComponents(TARGET_CLASS, "void invokeCustomTarget()"), TARGET_CLASS);

    // We model invokeCustom as invokeDynamic
    final List<InvokeExpr> invokes = invokesFromMethod(testTarget);
    Assert.assertEquals(1, invokes.size());
    final InvokeExpr invokeCustom = invokes.get(0);
    Assert.assertTrue(invokeCustom instanceof DynamicInvokeExpr);
    final SootMethodRef targetMethodRef = invokeCustom.getMethodRef();
    Assert.assertEquals(methodSigFromComponents(SootClass.INVOKEDYNAMIC_DUMMY_CLASS_NAME, SUPPLIER_GET_SUBSIG),
        targetMethodRef.getSignature());
    final String callToLambdaMethaFactory
        = "dynamicinvoke \"get\" <java.util.function.Supplier ()>() <java.lang.invoke.LambdaMetafactory: java.lang.invoke.CallSite metafactory(java.lang.invoke.MethodHandles$Lookup,java.lang.String,java.lang.invoke.MethodType,java.lang.invoke.MethodType,java.lang.invoke.MethodHandle,java.lang.invoke.MethodType)>(methodtype: java.lang.Object __METHODTYPE__(), methodhandle: \"REF_INVOKE_STATIC\" <soot.dexpler.instructions.DexBytecodeTarget: java.lang.String lambda$invokeCustomTarget$0()>, methodtype: java.lang.String __METHODTYPE__())";
    Assert.assertEquals(callToLambdaMethaFactory, invokeCustom.toString());
  }

  private List<InvokeExpr> invokesFromMethod(SootMethod testTarget) {
    final UnitPatchingChain units = testTarget.retrieveActiveBody().getUnits();
    return units.stream().filter(u -> ((Stmt) u).containsInvokeExpr()).map(u -> ((Stmt) u).getInvokeExpr())
        .collect(Collectors.toList());
  }

}
