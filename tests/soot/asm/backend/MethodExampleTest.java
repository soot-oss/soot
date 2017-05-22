package soot.asm.backend;

import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.TraceClassVisitor;

/**
 * Test for methods taken from the ASM 4.0 guide
 *
 * @author Tobias Hamann, Florian Kuebler, Dominik Helm, Lukas Sommer
 *
 */
public class MethodExampleTest extends AbstractASMBackendTest {

	@Override
	protected void generate(TraceClassVisitor visitor) {
		FieldVisitor fv;
		MethodVisitor mv;

		visitor.visit(Opcodes.V1_1, Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER,
				"soot/asm/backend/targets/Bean", null, "java/lang/Object", null);
		visitor.visitSource("Bean.java", null);
		{
		fv = visitor.visitField(Opcodes.ACC_PRIVATE, "f", "I", null, null);
		fv.visitEnd();
		}
		{
		mv = visitor.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
		mv.visitCode();
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
		mv.visitInsn(Opcodes.RETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
		}
		{
		mv = visitor.visitMethod(Opcodes.ACC_PUBLIC, "checkAndSetF", "(I)V", null, null);
		mv.visitCode();
		mv.visitVarInsn(Opcodes.ILOAD, 1);
		Label l0 = new Label();
		mv.visitJumpInsn(Opcodes.IFLT, l0);
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitVarInsn(Opcodes.ILOAD, 1);
		mv.visitFieldInsn(Opcodes.PUTFIELD, "soot/asm/backend/targets/Bean", "f", "I");
		Label l1 = new Label();
		mv.visitJumpInsn(Opcodes.GOTO, l1);
		mv.visitLabel(l0);
		mv.visitTypeInsn(Opcodes.NEW, "java/lang/IllegalArgumentException");
		mv.visitInsn(Opcodes.DUP);
		mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "()V", false);
		mv.visitInsn(Opcodes.ATHROW);
		mv.visitLabel(l1);
		mv.visitInsn(Opcodes.RETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
		}
		{
		mv = visitor.visitMethod(Opcodes.ACC_PUBLIC, "getF", "()I", null, null);
		mv.visitCode();
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitFieldInsn(Opcodes.GETFIELD, "soot/asm/backend/targets/Bean", "f", "I");
		mv.visitInsn(Opcodes.IRETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
		}
		{
		mv = visitor.visitMethod(Opcodes.ACC_PUBLIC, "setF", "(I)V", null, null);
		mv.visitCode();
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitVarInsn(Opcodes.ILOAD, 1);
		mv.visitFieldInsn(Opcodes.PUTFIELD, "soot/asm/backend/targets/Bean", "f", "I");
		mv.visitInsn(Opcodes.RETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
		}
		visitor.visitEnd();

	}

	@Override
	protected String getTargetClass() {
		return "soot.asm.backend.targets.Bean";
	}

	@Override
	protected String getTargetFolder() {
		return "./testclasses";
	}

	@Override
	protected String getClassPathFolder() {
		return "./testclasses";
	}

}
