package soot.asm.backend;

import org.objectweb.asm.Label;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.util.TraceClassVisitor;

/**
 * Test for monitor bytecode instructions
 *
 * @author Tobias Hamann, Florian Kuebler, Dominik Helm, Lukas Sommer
 *
 */
public class MonitorTest extends AbstractASMBackendTest {

	@Override
	protected void generate(TraceClassVisitor cw) {
		FieldVisitor fv;
		MethodVisitor mv;

		cw.visit(V1_1, ACC_PUBLIC + ACC_SUPER, "soot/asm/backend/targets/Monitor", null, "java/lang/Object", null);
		cw.visitSource("Monitor.java", null);

		{
		fv = cw.visitField(0, "o", "Ljava/lang/Object;", null, null);
		fv.visitEnd();
		}
		{
		mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
		mv.visitInsn(RETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
		}
		if (targetCompiler == TargetCompiler.eclipse) {
		mv = cw.visitMethod(ACC_PUBLIC, "doSth", "()V", null, null);
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/Monitor", "o", "Ljava/lang/Object;");
		mv.visitInsn(DUP);
		mv.visitInsn(MONITORENTER);
		mv.visitInsn(MONITOREXIT);
		mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "()V", false);
		mv.visitInsn(RETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
		}
		else {
			mv = cw.visitMethod(ACC_PUBLIC, "doSth", "()V", null, null);
			mv.visitCode();
			Label l0 = new Label();
			Label l1 = new Label();
			Label l2 = new Label();
			mv.visitTryCatchBlock(l0, l1, l2, "java/lang/Throwable");
			Label l3 = new Label();
			mv.visitTryCatchBlock(l2, l3, l2, "java/lang/Throwable");
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/Monitor", "o", "Ljava/lang/Object;");
			mv.visitVarInsn(ASTORE, 0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitInsn(MONITORENTER);
			mv.visitLabel(l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitInsn(MONITOREXIT);
			mv.visitLabel(l1);
			Label l4 = new Label();
			mv.visitJumpInsn(GOTO, l4);
			mv.visitLabel(l2);
			mv.visitVarInsn(ASTORE, 1);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitInsn(MONITOREXIT);
			mv.visitLabel(l3);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitInsn(ATHROW);
			mv.visitLabel(l4);
			mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "()V", false);
			mv.visitInsn(RETURN);
			mv.visitMaxs(0, 0);
			mv.visitEnd();
		}
		cw.visitEnd();
		
	}

	@Override
	protected String getTargetClass() {
		return "soot.asm.backend.targets.Monitor";
	}

}
