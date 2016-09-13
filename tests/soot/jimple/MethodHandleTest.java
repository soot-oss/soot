package soot.jimple;

import com.google.common.io.Files;
import org.junit.Test;
import org.objectweb.asm.*;
import soot.G;
import soot.Main;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class MethodHandleTest {
  
  @Test
  public void test() throws IOException {
    
    // First generate a classfile with a MethodHnadle
    ClassWriter cv = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
    cv.visit(Opcodes.V1_7, Opcodes.ACC_PUBLIC, "HelloMethodHandles", null, Type.getInternalName(Object.class), null);
    MethodVisitor mv = cv.visitMethod(Opcodes.ACC_STATIC | Opcodes.ACC_PUBLIC, "getSquareRoot",
        Type.getMethodDescriptor(Type.getType(java.lang.invoke.MethodHandle.class)), null, null);
    
    mv.visitCode();
    mv.visitLdcInsn(new Handle(Opcodes.H_INVOKESTATIC, Type.getInternalName(Math.class), "sqrt", 
        Type.getMethodDescriptor(Type.DOUBLE_TYPE, Type.DOUBLE_TYPE), false));
//    mv.visitInsn(Opcodes.ACONST_NULL);
    mv.visitInsn(Opcodes.ARETURN);
    mv.visitEnd();
    
    cv.visitEnd();

    File tempDir = Files.createTempDir();
    File classFile = new File(tempDir, "HelloMethodHandles.class");
    Files.write(cv.toByteArray(), classFile);

    G.reset();
    
    String[] commandLine = {"-pp", "-cp", tempDir.getAbsolutePath(), "-O", "HelloMethodHandles", };
    
    System.out.println("Command Line: " + Arrays.toString(commandLine));
    
    Main.main(commandLine);
    
  }
}
