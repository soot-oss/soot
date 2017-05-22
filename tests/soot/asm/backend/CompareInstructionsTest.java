package soot.asm.backend;

import org.objectweb.asm.*;
import org.objectweb.asm.util.TraceClassVisitor;

/**
 * Test for equals and not-equals bytecode instructions
 *
 * @author Tobias Hamann, Florian Kuebler, Dominik Helm, Lukas Sommer
 *
 */
public class CompareInstructionsTest extends AbstractASMBackendTest {

	@Override
	protected void generate(TraceClassVisitor cw) {

		FieldVisitor fv;
		MethodVisitor mv;

		cw.visit(V1_1, ACC_PUBLIC + ACC_SUPER, "soot/asm/backend/targets/CompareInstructions", null,
				"java/lang/Object", null);
		cw.visitSource("CompareInstructions.java", null);

		{
			fv = cw.visitField(0, "i", "I", null, null);
			fv.visitEnd();
		}
		{
			fv = cw.visitField(0, "f", "F", null, null);
			fv.visitEnd();
		}
		{
			fv = cw.visitField(0, "d", "D", null, null);
			fv.visitEnd();
		}
		{
			fv = cw.visitField(0, "l", "J", null, null);
			fv.visitEnd();
		}
		{
			fv = cw.visitField(0, "b", "B", null, null);
			fv.visitEnd();
		}
		{
			fv = cw.visitField(0, "bool", "Z", null, null);
			fv.visitEnd();
		}
		{
			fv = cw.visitField(0, "c", "C", null, null);
			fv.visitEnd();
		}
		{
			fv = cw.visitField(0, "s", "S", null, null);
			fv.visitEnd();
		}
		{
			fv = cw.visitField(0, "o", "Ljava/lang/Object;", null, null);
			fv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>",
					"()V", false);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitInsn(ICONST_2);
			mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/CompareInstructions", "i", "I");
			mv.visitVarInsn(ALOAD, 0);
			mv.visitLdcInsn(new Float("221349.02"));
			mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/CompareInstructions", "f", "F");
			mv.visitVarInsn(ALOAD, 0);
			mv.visitLdcInsn(new Double("2123996.1231231233"));
			mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/CompareInstructions", "d", "D");
			mv.visitVarInsn(ALOAD, 0);
			mv.visitLdcInsn(new Long(2L));
			mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/CompareInstructions", "l", "J");
			mv.visitVarInsn(ALOAD, 0);
			mv.visitInsn(ICONST_2);
			mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/CompareInstructions", "b", "B");
			mv.visitVarInsn(ALOAD, 0);
			mv.visitInsn(ICONST_1);
			mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/CompareInstructions", "bool", "Z");
			mv.visitVarInsn(ALOAD, 0);
			mv.visitInsn(ICONST_4);
			mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/CompareInstructions", "c", "C");
			mv.visitVarInsn(ALOAD, 0);
			mv.visitInsn(ICONST_3);
			mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/CompareInstructions", "s", "S");
			mv.visitInsn(RETURN);
			mv.visitMaxs(0, 0);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(0, "compareBool", "(Z)V", null, null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/CompareInstructions", "bool", "Z");
			mv.visitVarInsn(ILOAD, 1);
			Label l0 = new Label();
			mv.visitJumpInsn(IF_ICMPNE, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitInsn(ICONST_1);
			mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/CompareInstructions", "bool", "Z");
			mv.visitLabel(l0);
			// mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/CompareInstructions", "bool", "Z");
			mv.visitVarInsn(ILOAD, 1);
			Label l1 = new Label();
			mv.visitJumpInsn(IF_ICMPEQ, l1);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitInsn(ICONST_0);
			mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/CompareInstructions", "bool", "Z");
			mv.visitLabel(l1);
			// mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
			mv.visitInsn(RETURN);
			mv.visitMaxs(0, 0);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(0, "compareb", "(B)V", null, null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/CompareInstructions", "b", "B");
			mv.visitVarInsn(ILOAD, 1);
			Label l0 = new Label();
			mv.visitJumpInsn(IF_ICMPNE, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitInsn(ICONST_2);
			mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/CompareInstructions", "b", "B");
			mv.visitLabel(l0);
			// mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/CompareInstructions", "b", "B");
			mv.visitVarInsn(ILOAD, 1);
			Label l1 = new Label();
			mv.visitJumpInsn(IF_ICMPEQ, l1);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitInsn(ICONST_1);
			mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/CompareInstructions", "b", "B");
			mv.visitLabel(l1);
			// mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
			mv.visitInsn(RETURN);
			mv.visitMaxs(0, 0);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(0, "comparec", "(C)V", null, null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/CompareInstructions", "c", "C");
			mv.visitVarInsn(ILOAD, 1);
			Label l0 = new Label();
			mv.visitJumpInsn(IF_ICMPNE, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitInsn(ICONST_2);
			mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/CompareInstructions", "c", "C");
			mv.visitLabel(l0);
			// mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/CompareInstructions", "c", "C");
			mv.visitVarInsn(ILOAD, 1);
			Label l1 = new Label();
			mv.visitJumpInsn(IF_ICMPEQ, l1);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitInsn(ICONST_3);
			mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/CompareInstructions", "c", "C");
			mv.visitLabel(l1);
			// mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
			mv.visitInsn(RETURN);
			mv.visitMaxs(0, 0);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(0, "compared", "(D)V", null, null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/CompareInstructions", "d", "D");
			mv.visitVarInsn(DLOAD, 1);
			mv.visitInsn(DCMPL);
			Label l0 = new Label();
			mv.visitJumpInsn(IFNE, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitLdcInsn(new Double("2.0"));
			mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/CompareInstructions", "d", "D");
			mv.visitLabel(l0);
			// mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/CompareInstructions", "d", "D");
			mv.visitVarInsn(DLOAD, 1);
			mv.visitInsn(DCMPL);
			Label l1 = new Label();
			mv.visitJumpInsn(IFEQ, l1);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitInsn(DCONST_1);
			mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/CompareInstructions", "d", "D");
			mv.visitLabel(l1);
			// mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
			mv.visitInsn(RETURN);
			mv.visitMaxs(0, 0);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(0, "comparef", "(F)V", null, null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/CompareInstructions", "f", "F");
			mv.visitVarInsn(FLOAD, 1);
			mv.visitInsn(FCMPL);
			Label l0 = new Label();
			mv.visitJumpInsn(IFNE, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitInsn(FCONST_0);
			mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/CompareInstructions", "f", "F");
			mv.visitLabel(l0);
			// mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/CompareInstructions", "f", "F");
			mv.visitVarInsn(FLOAD, 1);
			mv.visitInsn(FCMPL);
			Label l1 = new Label();
			mv.visitJumpInsn(IFEQ, l1);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitInsn(FCONST_1);
			mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/CompareInstructions", "f", "F");
			mv.visitLabel(l1);
			// mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
			mv.visitInsn(RETURN);
			mv.visitMaxs(0, 0);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(0, "comparei", "(I)V", null, null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/CompareInstructions", "i", "I");
			mv.visitVarInsn(ILOAD, 1);
			Label l0 = new Label();
			mv.visitJumpInsn(IF_ICMPNE, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitInsn(ICONST_2);
			mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/CompareInstructions", "i", "I");
			mv.visitLabel(l0);
			// mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/CompareInstructions", "i", "I");
			mv.visitVarInsn(ILOAD, 1);
			Label l1 = new Label();
			mv.visitJumpInsn(IF_ICMPEQ, l1);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitInsn(ICONST_1);
			mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/CompareInstructions", "i", "I");
			mv.visitLabel(l1);
			// mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
			mv.visitInsn(RETURN);
			mv.visitMaxs(0, 0);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(0, "comparel", "(J)V", null, null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/CompareInstructions", "l", "J");
			mv.visitVarInsn(LLOAD, 1);
			mv.visitInsn(LCMP);
			Label l0 = new Label();
			mv.visitJumpInsn(IFNE, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitLdcInsn(new Long(2L));
			mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/CompareInstructions", "l", "J");
			mv.visitLabel(l0);
			// mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/CompareInstructions", "l", "J");
			mv.visitVarInsn(LLOAD, 1);
			mv.visitInsn(LCMP);
			Label l1 = new Label();
			mv.visitJumpInsn(IFEQ, l1);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitInsn(LCONST_1);
			mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/CompareInstructions", "l", "J");
			mv.visitLabel(l1);
			// mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
			mv.visitInsn(RETURN);
			mv.visitMaxs(0, 0);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(0, "comparenull", "(Ljava/lang/Object;)V", null, null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/CompareInstructions", "o", "Ljava/lang/Object;");
			Label l0 = new Label();
			mv.visitJumpInsn(IFNONNULL, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitTypeInsn(NEW, "java/lang/Object");
			mv.visitInsn(DUP);
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
			mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/CompareInstructions", "o", "Ljava/lang/Object;");
			mv.visitLabel(l0);
			mv.visitVarInsn(ALOAD, 1);
			Label l1 = new Label();
			mv.visitJumpInsn(IFNULL, l1);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitInsn(ACONST_NULL);
			mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/CompareInstructions", "o", "Ljava/lang/Object;");
			mv.visitLabel(l1);
			mv.visitInsn(RETURN);
			mv.visitMaxs(0, 0);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(0, "compares", "(S)V", null, null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/CompareInstructions", "s", "S");
			mv.visitVarInsn(ILOAD, 1);
			Label l0 = new Label();
			mv.visitJumpInsn(IF_ICMPNE, l0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitInsn(ICONST_1);
			mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/CompareInstructions", "s", "S");
			mv.visitLabel(l0);
			// mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/CompareInstructions", "s", "S");
			mv.visitVarInsn(ILOAD, 1);
			Label l1 = new Label();
			mv.visitJumpInsn(IF_ICMPEQ, l1);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitInsn(ICONST_3);
			mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/CompareInstructions", "s", "S");
			mv.visitLabel(l1);
			// mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
			mv.visitInsn(RETURN);
			mv.visitMaxs(0, 0);
			mv.visitEnd();
		}
			cw.visitEnd();

	}

	@Override
	protected String getTargetClass() {
		return "soot.asm.backend.targets.CompareInstructions";
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
