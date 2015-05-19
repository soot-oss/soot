package soot.asm.backend;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.util.TraceClassVisitor;

/**
 * Test for try catch bytecode instructions
 *
 * @author Tobias Hamann, Florian Kuebler, Dominik Helm, Lukas Sommer
 *
 */
public class TryCatchTest extends AbstractASMBackendTest {

	@Override
	protected void generate(TraceClassVisitor cw) {
		MethodVisitor mv;

		cw.visit(V1_1, ACC_PUBLIC + ACC_SUPER, "pkg/TryCatch", null, "java/lang/Object", null);
		cw.visitSource("TryCatch.java", null);

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
		mv = cw.visitMethod(0, "doSth", "(Ljava/lang/Object;)I", null, null);
		mv.visitCode();
		Label l0 = new Label();
		Label l1 = new Label();
		Label l2 = new Label();
		mv.visitTryCatchBlock(l0, l1, l2, "java/lang/NullPointerException");
		Label l3 = new Label();
		Label l4 = new Label();
		mv.visitTryCatchBlock(l0, l3, l4, "java/lang/Throwable");
		mv.visitInsn(ICONST_0);
		mv.visitVarInsn(ISTORE, 0);
		mv.visitLabel(l0);
		mv.visitVarInsn(ALOAD, 1);
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "notify", "()V", false);
		mv.visitInsn(ICONST_1);
		mv.visitVarInsn(ISTORE, 0);
		mv.visitLabel(l1);
		Label l5 = new Label();
		mv.visitJumpInsn(GOTO, l5);
		mv.visitLabel(l2);
		//mv.visitFrame(F_FULL, 3, new Object[] {"pkg/TryCatch", "java/lang/Object", INTEGER}, 1, new Object[] {"java/lang/NullPointerException"});
		mv.visitVarInsn(ASTORE, 1);
		mv.visitInsn(ICONST_M1);
		mv.visitVarInsn(ISTORE, 0);
		mv.visitLabel(l3);
		mv.visitJumpInsn(GOTO, l5);
		mv.visitLabel(l4);
		//mv.visitFrame(F_SAME1, 0, null, 1, new Object[] {"java/lang/Throwable"});
		mv.visitVarInsn(ASTORE, 1);
		mv.visitLabel(l5);
		//mv.visitFrame(F_SAME, 0, null, 0, null);
		mv.visitVarInsn(ILOAD, 0);
		mv.visitInsn(IRETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
		}
		cw.visitEnd();

		
	}

	@Override
	protected String getTargetClass() {
		return "soot.asm.backend.targets.TryCatch";
	}

	@Override
	protected String getTargetFolder() {
		return "./testclasses/soot/asm/backend/targets";
	}

	@Override
	protected String getClassPathFolder() {
		return "./testclasses/soot/asm/backend/targets";
	}

}
