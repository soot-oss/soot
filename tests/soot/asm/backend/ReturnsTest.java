package soot.asm.backend;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.util.TraceClassVisitor;

/**
 * Test for several return bytecode instructions
 *
 * @author Tobias Hamann, Florian Kuebler, Dominik Helm, Lukas Sommer
 *
 */
public class ReturnsTest extends AbstractASMBackendTest {

	@Override
	protected void generate(TraceClassVisitor visitor) {
		MethodVisitor mv;

		visitor.visit(V1_1, ACC_PUBLIC + ACC_SUPER, "soot/asm/backend/targets/Returns", null,
				"java/lang/Object", null);
		
		visitor.visitSource("Returns.java", null);

		{
			mv = visitor.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>",
					"()V", false);
			mv.visitInsn(RETURN);
			mv.visitMaxs(0, 0);
			mv.visitEnd();
		}
		{
			mv = visitor.visitMethod(ACC_PUBLIC, "getIntArray", "()[I", null,
					null);
			mv.visitCode();
			mv.visitInsn(ICONST_4);
			mv.visitIntInsn(NEWARRAY, T_INT);
			mv.visitInsn(ARETURN);
			mv.visitMaxs(0, 0);
			mv.visitEnd();
		}
		{
			mv = visitor.visitMethod(ACC_PUBLIC, "getNull",
					"()Ljava/lang/Object;", null, null);
			mv.visitCode();
			mv.visitInsn(ACONST_NULL);
			mv.visitInsn(ARETURN);
			mv.visitMaxs(0, 0);
			mv.visitEnd();
		}
		{
			mv = visitor.visitMethod(ACC_PUBLIC, "getObjectArray",
					"()[Ljava/lang/Object;", null, null);
			mv.visitCode();
			mv.visitInsn(ICONST_4);
			mv.visitTypeInsn(ANEWARRAY, "java/lang/Object");
			mv.visitInsn(ARETURN);
			mv.visitMaxs(0, 0);
			mv.visitEnd();
		}
		visitor.visitEnd();

	}

	@Override
	protected String getTargetClass() {
		return "soot.asm.backend.targets.Returns";
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
