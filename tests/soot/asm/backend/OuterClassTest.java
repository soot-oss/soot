package soot.asm.backend;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.util.TraceClassVisitor;

/**
 * Test for class containing inner classes
 *
 * @author Tobias Hamann, Florian Kuebler, Dominik Helm, Lukas Sommer
 *
 */
public class OuterClassTest extends AbstractASMBackendTest {

	@Override
	protected void generate(TraceClassVisitor cw) {
		MethodVisitor mv;

		cw.visit(V1_1, ACC_PUBLIC + ACC_SUPER, "soot/asm/backend/targets/InnerClass", null,
				"java/lang/Object", null);

		cw.visitSource("InnerClass.java", null);

		cw.visitInnerClass("soot/asm/backend/targets/InnerClass$1", null, null, 0);

		cw.visitInnerClass("soot/asm/backend/targets/InnerClass$Inner", "soot/asm/backend/targets/InnerClass", "Inner",
				ACC_PRIVATE);

		{
			mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>",
					"()V", false);
			mv.visitInsn(RETURN);
			mv.visitMaxs(0, 0);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "doInner", "()V", null, null);
			mv.visitCode();
			mv.visitTypeInsn(NEW, "soot/asm/backend/targets/InnerClass$1");
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKESPECIAL, "soot/asm/backend/targets/InnerClass$1", "<init>",
					"(Lsoot/asm/backend/targets/InnerClass;)V", false);
			mv.visitInsn(RETURN);
			mv.visitMaxs(0, 0);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "getA", "()I", null, null);
			mv.visitCode();
			mv.visitInsn(ICONST_3);
			mv.visitInsn(IRETURN);
			mv.visitMaxs(0, 0);
			mv.visitEnd();
		}
		cw.visitEnd();

	}

	@Override
	protected String getTargetClass() {
		return "soot.asm.backend.targets.InnerClass";
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
