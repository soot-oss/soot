package soot.asm.backend;

import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.util.TraceClassVisitor;

/**
 * Test for inner class in class
 *
 * @author Tobias Hamann, Florian Kuebler, Dominik Helm, Lukas Sommer
 *
 */
public class InnerClassTest extends AbstractASMBackendTest {

	@Override
	protected void generate(TraceClassVisitor visitor) {
		MethodVisitor mv;
		FieldVisitor fv;

		visitor.visit(V1_1, ACC_SUPER, "soot/asm/backend/targets/InnerClass$Inner", null,
				"java/lang/Object", null);
		
		visitor.visitSource("InnerClass.java", null);

		visitor.visitInnerClass("soot/asm/backend/targets/InnerClass$Inner", "soot/asm/backend/targets/InnerClass",
				"Inner", ACC_PRIVATE);

		{
			fv = visitor.visitField(ACC_FINAL + ACC_STATIC, "a", "I", null,
					new Integer(3));
			fv.visitEnd();
		}
		{
			fv = visitor.visitField(ACC_FINAL + ACC_SYNTHETIC, "this$0",
					"Lsoot/asm/backend/targets/InnerClass;", null, null);
			fv.visitEnd();
		}
		{
			mv = visitor.visitMethod(ACC_PRIVATE, "<init>",
					"(Lsoot/asm/backend/targets/InnerClass;)V", null, null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/InnerClass$Inner", "this$0",
					"Lsoot/asm/backend/targets/InnerClass;");
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>",
					"()V", false);
			mv.visitInsn(RETURN);
			mv.visitMaxs(0, 0);
			mv.visitEnd();
		}
		visitor.visitEnd();

	}

	@Override
	protected String getTargetClass() {
		return "soot.asm.backend.targets.InnerClass$Inner";
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
