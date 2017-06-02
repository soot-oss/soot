package soot.asm.backend;

import org.objectweb.asm.*;
import org.objectweb.asm.util.TraceClassVisitor;

/**
 * Test for null types
 *
 * @author Tobias Hamann, Florian Kuebler, Dominik Helm, Lukas Sommer
 *
 */
public class NullTypesTest extends AbstractASMBackendTest{
    @Override
    protected void generate(TraceClassVisitor cw) {

        MethodVisitor mv;

        cw.visit(V1_1, ACC_PUBLIC + ACC_SUPER, "soot/asm/backend/targets/nullTypes", null, "java/lang/Object", null);
        cw.visitSource("nullTypes.java", null);

        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(0, "doStuff", "(Ljava/lang/Integer;)Ljava/lang/Integer;", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 1);
            Label l0 = new Label();
            mv.visitJumpInsn(IFNONNULL, l0);
            mv.visitInsn(ACONST_NULL);
            mv.visitInsn(ARETURN);
            mv.visitLabel(l0);
            //mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitInsn(ICONST_1);
            mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            mv.visitInsn(ARETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }
        cw.visitEnd();
    }

    @Override
    protected String getTargetClass() {
        return "soot.asm.backend.targets.nullTypes";
    }

}

