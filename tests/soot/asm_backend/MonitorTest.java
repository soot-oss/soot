package soot.asm_backend;

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

		cw.visit(V1_1, ACC_PUBLIC + ACC_SUPER, "pkg/Monitor", null, "java/lang/Object", null);
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
		{
		mv = cw.visitMethod(ACC_PUBLIC, "doSth", "()V", null, null);
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, "pkg/Monitor", "o", "Ljava/lang/Object;");
		mv.visitInsn(DUP);
		mv.visitInsn(MONITORENTER);
		mv.visitInsn(MONITOREXIT);
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
		return "pkg.Monitor";
	}

	@Override
	protected String getTargetFolder() {
		return "./testcode_asm_backend";
	}

	@Override
	protected String getClassPathFolder() {
		return "./testcode_asm_backend";
	}

}
