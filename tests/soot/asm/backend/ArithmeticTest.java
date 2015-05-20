package soot.asm.backend;

import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.util.TraceClassVisitor;

/**
 * Test for several arithmetic bytecode instructions
 *
 * @author Tobias Hamann, Florian Kuebler, Dominik Helm, Lukas Sommer
 *
 */
public class ArithmeticTest extends AbstractASMBackendTest {

	@Override
	protected void generate(TraceClassVisitor cw) {

		FieldVisitor fv;
		MethodVisitor mv;

		cw.visit(V1_2, ACC_PUBLIC + ACC_SUPER, "soot/asm/backend/targets/ArithmeticLib", null,
				"java/lang/Object", null);

		cw.visitSource("ArithmeticLib.java", null);

		{
		fv = cw.visitField(ACC_PRIVATE, "rInt", "I", null, null);
		fv.visitEnd();
		}
		{
		fv = cw.visitField(ACC_PRIVATE, "rFloat", "F", null, null);
		fv.visitEnd();
		}
		{
		fv = cw.visitField(ACC_PRIVATE, "rLong", "J", null, null);
		fv.visitEnd();
		}
		{
		fv = cw.visitField(ACC_PRIVATE, "rDouble", "D", null, null);
		fv.visitEnd();
		}
		{
		fv = cw.visitField(ACC_PRIVATE, "rShort", "S", null, null);
		fv.visitEnd();
		}
		{
		fv = cw.visitField(ACC_PRIVATE, "rChar", "C", null, null);
		fv.visitEnd();
		}
		{
		fv = cw.visitField(ACC_PRIVATE, "rByte", "B", null, null);
		fv.visitEnd();
		}
		{
		fv = cw.visitField(ACC_FINAL, "cInt", "I", null, new Integer(1));
		fv.visitEnd();
		}
		{
		fv = cw.visitField(ACC_FINAL, "cFloat", "F", null, new Float("1.0"));
		fv.visitEnd();
		}
		{
		fv = cw.visitField(ACC_FINAL, "cLong", "J", null, new Long(1L));
		fv.visitEnd();
		}
		{
		fv = cw.visitField(ACC_FINAL, "cDouble", "D", null, new Double("1.0"));
		fv.visitEnd();
		}
		{
		mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitInsn(ICONST_1);
		mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/ArithmeticLib", "cInt", "I");
		mv.visitVarInsn(ALOAD, 0);
		mv.visitInsn(FCONST_1);
		mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/ArithmeticLib", "cFloat", "F");
		mv.visitVarInsn(ALOAD, 0);
		mv.visitInsn(LCONST_1);
		mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/ArithmeticLib", "cLong", "J");
		mv.visitVarInsn(ALOAD, 0);
		mv.visitInsn(DCONST_1);
		mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/ArithmeticLib", "cDouble", "D");
		mv.visitInsn(RETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
		}
		
		{
		mv = cw.visitMethod(ACC_PUBLIC, "castInt2Byte", "()B", null, null);
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/ArithmeticLib", "rInt", "I");
		mv.visitInsn(I2B);
		mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/ArithmeticLib", "rByte", "B");
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/ArithmeticLib", "rByte", "B");
		mv.visitInsn(IRETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
		}
		{
		mv = cw.visitMethod(ACC_PUBLIC, "castInt2Char", "()C", null, null);
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/ArithmeticLib", "rInt", "I");
		mv.visitInsn(I2C);
		mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/ArithmeticLib", "rChar", "C");
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/ArithmeticLib", "rChar", "C");
		mv.visitInsn(IRETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
		}
		{
		mv = cw.visitMethod(ACC_PUBLIC, "castInt2Short", "()S", null, null);
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/ArithmeticLib", "rInt", "I");
		mv.visitInsn(I2S);
		mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/ArithmeticLib", "rShort", "S");
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/ArithmeticLib", "rShort", "S");
		mv.visitInsn(IRETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
		}

		{
		mv = cw.visitMethod(ACC_PUBLIC, "doCompDouble", "(D)D", null, null);
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(DLOAD, 1);
		mv.visitInsn(DCONST_1);
		mv.visitInsn(DDIV);
		mv.visitInsn(D2I);
		mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/ArithmeticLib", "rInt", "I");
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(DLOAD, 1);
		mv.visitLdcInsn(new Double("6.0"));
		mv.visitInsn(DMUL);
		mv.visitInsn(D2L);
		mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/ArithmeticLib", "rLong", "J");
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(DLOAD, 1);
		mv.visitInsn(DCONST_0);
		mv.visitInsn(DADD);
		mv.visitInsn(D2F);
		mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/ArithmeticLib", "rFloat", "F");
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(DLOAD, 1);
		mv.visitLdcInsn(new Double("4.0"));
		mv.visitInsn(DSUB);
		mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/ArithmeticLib", "rDouble", "D");
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/ArithmeticLib", "rDouble", "D");
		mv.visitInsn(DRETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
		}
		{
		mv = cw.visitMethod(ACC_PUBLIC, "doCompFloat", "(F)F", null, null);
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(FLOAD, 1);
		mv.visitLdcInsn(new Float("13.0"));
		mv.visitInsn(FDIV);
		mv.visitInsn(F2I);
		mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/ArithmeticLib", "rInt", "I");
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(FLOAD, 1);
		mv.visitLdcInsn(new Float("3.0"));
		mv.visitInsn(FMUL);
		mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/ArithmeticLib", "rFloat", "F");
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(FLOAD, 1);
		mv.visitInsn(FCONST_2);
		mv.visitInsn(FSUB);
		mv.visitInsn(F2L);
		mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/ArithmeticLib", "rLong", "J");
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(FLOAD, 1);
		mv.visitInsn(FCONST_1);
		mv.visitInsn(FADD);
		mv.visitInsn(F2D);
		mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/ArithmeticLib", "rDouble", "D");
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/ArithmeticLib", "rFloat", "F");
		mv.visitInsn(FRETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
		}
		{
		mv = cw.visitMethod(ACC_PUBLIC, "doCompInt", "(I)I", null, null);
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ILOAD, 1);
		mv.visitInsn(ICONST_M1);
		mv.visitInsn(IDIV);
		mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/ArithmeticLib", "rInt", "I");
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ILOAD, 1);
		mv.visitIntInsn(BIPUSH, 17);
		mv.visitInsn(IMUL);
		mv.visitInsn(I2F);
		mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/ArithmeticLib", "rFloat", "F");
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ILOAD, 1);
		mv.visitInsn(ICONST_5);
		mv.visitInsn(IADD);
		mv.visitInsn(I2L);
		mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/ArithmeticLib", "rLong", "J");
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ILOAD, 1);
		mv.visitInsn(ICONST_2);
		mv.visitInsn(ISUB);
		mv.visitInsn(I2D);
		mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/ArithmeticLib", "rDouble", "D");
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/ArithmeticLib", "rInt", "I");
		mv.visitInsn(IRETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
		}
		{
		mv = cw.visitMethod(ACC_PUBLIC, "doCompLong", "(J)J", null, null);
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(LLOAD, 1);
		mv.visitLdcInsn(new Long(5L));
		mv.visitInsn(LMUL);
		mv.visitInsn(L2I);
		mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/ArithmeticLib", "rInt", "I");
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(LLOAD, 1);
		mv.visitLdcInsn(new Long(2L));
		mv.visitInsn(LADD);
		mv.visitInsn(L2F);
		mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/ArithmeticLib", "rFloat", "F");
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(LLOAD, 1);
		mv.visitLdcInsn(new Long(6L));
		mv.visitInsn(LMUL);
		mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/ArithmeticLib", "rLong", "J");
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(LLOAD, 1);
		mv.visitLdcInsn(new Long(6L));
		mv.visitInsn(LDIV);
		mv.visitInsn(L2D);
		mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/ArithmeticLib", "rDouble", "D");
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/ArithmeticLib", "rLong", "J");
		mv.visitInsn(LRETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
		}
		cw.visitEnd();
	}

	@Override
	protected String getTargetClass() {
		return "soot.asm.backend.targets.ArithmeticLib";
	}

	@Override
	protected String getTargetFolder() {
		return "./testclasses";
	}

	@Override
	protected String getClassPathFolder() {
		return "./testclasses";
	}
	
	@Override
	protected String getRequiredJavaVersion(){
		return "1.2";
	}
}
