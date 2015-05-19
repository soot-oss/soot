package soot.asm.backend;

import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.util.TraceClassVisitor;

/**
 * Test for inner class in method
 *
 * @author Tobias Hamann, Florian Kuebler, Dominik Helm, Lukas Sommer
 *
 */
public class InnerClass2Test extends AbstractASMBackendTest {

	@Override
	protected void generate(TraceClassVisitor cw) {
		MethodVisitor mv;
		FieldVisitor fv;

		cw.visit(V1_1, ACC_SUPER, "pkg/InnerClass$1", null, "java/lang/Object",
				new String[] { "pkg/Measurable" });
		
		cw.visitSource("InnerClass.java", null);

		cw.visitOuterClass("pkg/InnerClass", "doInner", "()V");

		cw.visitInnerClass("pkg/InnerClass$1", null, null, 0);

		{
			fv = cw.visitField(ACC_FINAL + ACC_SYNTHETIC, "this$0",
					"Lpkg/InnerClass;", null, null);
			fv.visitEnd();
		}
		{
			mv = cw.visitMethod(0, "<init>", "(Lpkg/InnerClass;)V", null, null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitFieldInsn(PUTFIELD, "pkg/InnerClass$1", "this$0",
					"Lpkg/InnerClass;");
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>",
					"()V", false);
			mv.visitInsn(RETURN);
			mv.visitMaxs(0, 0);
			mv.visitEnd();
		}
		cw.visitEnd();

	}

	@Override
	protected String getTargetClass() {
		return "soot.asm.backend.targets.InnerClass$1";
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
