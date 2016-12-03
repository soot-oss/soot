package soot.jimple;

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
