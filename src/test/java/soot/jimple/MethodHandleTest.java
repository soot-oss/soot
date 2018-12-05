package soot.jimple;

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

import com.google.common.io.Files;

import org.junit.Ignore;
import org.junit.Test;
import org.objectweb.asm.*;
import soot.G;
import soot.Main;

import java.io.File;
import java.io.IOException;
import java.lang.invoke.*;
import java.util.Arrays;

public class MethodHandleTest {
  
  @Test
  public void testConstant() throws IOException {

    // First generate a classfile with a MethodHnadle
    ClassWriter cv = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
    cv.visit(Opcodes.V1_7, Opcodes.ACC_PUBLIC, "HelloMethodHandles", null, Type.getInternalName(Object.class), null);
    MethodVisitor mv = cv.visitMethod(Opcodes.ACC_STATIC | Opcodes.ACC_PUBLIC, "getSquareRoot",
        Type.getMethodDescriptor(Type.getType(java.lang.invoke.MethodHandle.class)), null, null);

    mv.visitCode();

    mv.visitLdcInsn(new Handle(Opcodes.H_INVOKESTATIC, Type.getInternalName(Math.class), "sqrt",
        Type.getMethodDescriptor(Type.DOUBLE_TYPE, Type.DOUBLE_TYPE), false));


    mv.visitInsn(Opcodes.ARETURN);
    mv.visitEnd();

    cv.visitEnd();

    File tempDir = Files.createTempDir();
    File classFile = new File(tempDir, "HelloMethodHandles.class");
    Files.write(cv.toByteArray(), classFile);

    G.reset();

    String[] commandLine = {"-asm-backend", "-pp", "-cp", tempDir.getAbsolutePath(), "-O", "HelloMethodHandles", };

    System.out.println("Command Line: " + Arrays.toString(commandLine));

    Main.main(commandLine);
  }


  @Test
  @Ignore
  public void testInvoke() throws IOException {

    // First generate a classfile with a MethodHnadle
    ClassWriter cv = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
    cv.visit(Opcodes.V1_7, Opcodes.ACC_PUBLIC, "UniformDistribution", null, Type.getInternalName(Object.class), null);
    
    MethodVisitor mv = cv.visitMethod(Opcodes.ACC_STATIC | Opcodes.ACC_PUBLIC, "sample",
        Type.getMethodDescriptor(
            Type.DOUBLE_TYPE, 
            Type.getType(java.lang.invoke.MethodHandle.class) /* rng method */, 
            Type.DOUBLE_TYPE  /* max */), null, null);
 
    mv.visitCode();

    mv.visitVarInsn(Opcodes.ALOAD, 0); // load MethodHandle
    
    // Call MethodHandle.invoke() with polymorphic signature: ()D
    mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(java.lang.invoke.MethodHandle.class), 
          "invoke", Type.getMethodDescriptor(Type.DOUBLE_TYPE), false);
    
    mv.visitVarInsn(Opcodes.DLOAD, 1);
    mv.visitInsn(Opcodes.DMUL);
    mv.visitInsn(Opcodes.DRETURN);
    mv.visitEnd();
    cv.visitEnd();

    File tempDir = Files.createTempDir();
    File classFile = new File(tempDir, "UniformDistribution.class");
    Files.write(cv.toByteArray(), classFile);

    G.reset();

    String[] commandLine = {"-asm-backend", "-pp", "-cp", tempDir.getAbsolutePath(), "-O", "UniformDistribution", };

    System.out.println("Command Line: " + Arrays.toString(commandLine));

    Main.main(commandLine);
  }
}
