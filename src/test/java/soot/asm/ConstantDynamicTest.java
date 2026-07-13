package soot.asm;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vallée-Rai and others
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.google.common.io.Files;

import java.io.File;
import java.util.Collections;
import java.util.Iterator;

import org.junit.Test;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.ConstantDynamic;
import org.objectweb.asm.Handle;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import soot.Body;
import soot.G;
import soot.IntType;
import soot.RefType;
import soot.Scene;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.IntConstant;
import soot.jimple.DynamicInvokeExpr;
import soot.options.Options;

/**
 * Tests that Soot gracefully handles JEP 309 dynamic class-file constants (CONSTANT_Dynamic). Since {@code javac} does not
 * emit dynamic constants, the test classes are generated directly with ASM.
 *
 * @see <a href="https://github.com/soot-oss/soot/issues/1002">soot-oss/soot#1002</a>
 */
public class ConstantDynamicTest {

  private static final String CLASS_NAME = "DynamicConstantHolder";
  private static final String INTERNAL_NAME = "DynamicConstantHolder";

  /**
   * Bootstrap method descriptor for a {@code CONSTANT_Dynamic} entry: {@code (Lookup, String, Class) -> Object}.
   */
  private static final String BSM_DESC
      = "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/Class;)Ljava/lang/Object;";

  /**
   * Generates a class that references two dynamic constants (one of reference type without bootstrap arguments and one of
   * primitive type with a single static bootstrap argument) and returns the directory the class was written to.
   */
  private File generateClassWithDynamicConstants() throws Exception {
    ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
    cw.visit(Opcodes.V11, Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER, INTERNAL_NAME, null,
        "java/lang/Object", null);

    // Bootstrap method: static Object bsm(Lookup, String, Class). Soot never resolves/executes it,
    // it only needs to be referenceable so the body simply returns null.
    MethodVisitor bsm = cw.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "bsm", BSM_DESC, null, null);
    bsm.visitCode();
    bsm.visitInsn(Opcodes.ACONST_NULL);
    bsm.visitInsn(Opcodes.ARETURN);
    bsm.visitMaxs(0, 0);
    bsm.visitEnd();

    Handle bsmHandle = new Handle(Opcodes.H_INVOKESTATIC, INTERNAL_NAME, "bsm", BSM_DESC, false);

    // String getStringConstant() { return <dynamic String constant>; }
    ConstantDynamic stringConstant = new ConstantDynamic("stringConstant", "Ljava/lang/String;", bsmHandle);
    MethodVisitor m1
        = cw.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "getStringConstant", "()Ljava/lang/String;", null, null);
    m1.visitCode();
    m1.visitLdcInsn(stringConstant);
    m1.visitInsn(Opcodes.ARETURN);
    m1.visitMaxs(0, 0);
    m1.visitEnd();

    // int getIntConstant() { return <dynamic int constant with static arg 42>; }
    ConstantDynamic intConstant
        = new ConstantDynamic("intConstant", "I", bsmHandle, Integer.valueOf(42));
    MethodVisitor m2 = cw.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "getIntConstant", "()I", null, null);
    m2.visitCode();
    m2.visitLdcInsn(intConstant);
    m2.visitInsn(Opcodes.IRETURN);
    m2.visitMaxs(0, 0);
    m2.visitEnd();

    cw.visitEnd();

    File tempDir = Files.createTempDir();
    Files.write(cw.toByteArray(), new File(tempDir, "DynamicConstantHolder.class"));
    return tempDir;
  }

  private void setupSoot(File classDir) {
    G.reset();
    Options.v().set_prepend_classpath(true);
    Options.v().set_process_dir(Collections.singletonList(classDir.getAbsolutePath()));
    Options.v().set_src_prec(Options.src_prec_class);
    Options.v().set_output_format(Options.output_format_none);
    Options.v().set_allow_phantom_refs(true);
    Options.v().setPhaseOption("cg", "enabled:false");
    Scene.v().loadNecessaryClasses();
  }

  private static DynamicInvokeExpr findDynamicInvoke(Body body) {
    for (Unit u : body.getUnits()) {
      for (ValueBox vb : u.getUseBoxes()) {
        Value v = vb.getValue();
        if (v instanceof DynamicInvokeExpr) {
          return (DynamicInvokeExpr) v;
        }
      }
    }
    return null;
  }

  @Test
  public void referenceTypedDynamicConstantIsModelledGracefully() throws Exception {
    File classDir = generateClassWithDynamicConstants();
    setupSoot(classDir);

    SootMethod method = Scene.v().getSootClass(CLASS_NAME).getMethodByName("getStringConstant");
    // Reading the body must not throw: the dynamic constant is handled gracefully.
    Body body = method.retrieveActiveBody();

    DynamicInvokeExpr indy = findDynamicInvoke(body);
    assertNotNull("Dynamic constant should be modelled as a dynamic invocation", indy);
    // The declared type of the constant must be preserved.
    assertEquals(RefType.v("java.lang.String"), indy.getType());
    // The bootstrap method of the constant must be retained.
    assertEquals("bsm", indy.getBootstrapMethodRef().getName());
    // A dynamic constant carries no dynamic arguments.
    assertEquals(0, indy.getArgCount());
  }

  @Test
  public void primitiveTypedDynamicConstantRetainsBootstrapArguments() throws Exception {
    File classDir = generateClassWithDynamicConstants();
    setupSoot(classDir);

    SootMethod method = Scene.v().getSootClass(CLASS_NAME).getMethodByName("getIntConstant");
    Body body = method.retrieveActiveBody();

    DynamicInvokeExpr indy = findDynamicInvoke(body);
    assertNotNull("Dynamic constant should be modelled as a dynamic invocation", indy);
    // The declared primitive type of the constant must be preserved.
    assertEquals(IntType.v(), indy.getType());
    // The static bootstrap argument of the constant must be retained.
    assertEquals(1, indy.getBootstrapArgCount());
    assertEquals(IntConstant.v(42), indy.getBootstrapArg(0));
  }

  @Test
  public void allDynamicConstantsAreConvertibleWithoutError() throws Exception {
    File classDir = generateClassWithDynamicConstants();
    setupSoot(classDir);

    boolean sawDynamicInvoke = false;
    for (Iterator<SootMethod> it = Scene.v().getSootClass(CLASS_NAME).getMethods().iterator(); it.hasNext();) {
      SootMethod m = it.next();
      Body body = m.retrieveActiveBody();
      if (findDynamicInvoke(body) != null) {
        sawDynamicInvoke = true;
      }
    }
    assertTrue("At least one method should contain a modelled dynamic constant", sawDynamicInvoke);
  }
}
