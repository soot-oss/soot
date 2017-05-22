package soot.asm.backend;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.util.TraceClassVisitor;

/**
 * Test for annotations of annotations
 *
 * @author Tobias Hamann, Florian Kuebler, Dominik Helm, Lukas Sommer
 *
 */
public class AnnotatedAnnotatedClassTest extends AbstractASMBackendTest {

	@Override
	protected void generate(TraceClassVisitor cw) {

		MethodVisitor mv;
		AnnotationVisitor av0;

		cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER, "soot/asm/backend/targets/AnnotatedAnnotatedClass",
				null, "java/lang/Object", null);
		cw.visitSource("AnnotatedAnnotatedClass.java", null);
		{
		av0 = cw.visitAnnotation("Lsoot/asm/backend/targets/MyAnnotatedAnnotation;", false);
		{
		AnnotationVisitor av1 = av0.visitAnnotation("value", "Lsoot/asm/backend/targets/MyTestAnnotation;");
		av1.visit("iVal", new Integer(1));
		av1.visit("fVal", new Float("1.0"));
		av1.visit("lVal", new Long(1L));
		av1.visit("dVal", new Double("1.0"));
		av1.visit("zVal", Boolean.TRUE);
		av1.visit("bVal", new Byte((byte)1));
		av1.visit("sVal", new Short((short)1));
		av1.visit("strVal", "1");
		av1.visit("rVal", Type.getType("Lsoot/asm/backend/targets/AnnotatedClass;"));
		av1.visit("iAVal", new int[] {1,2,3,4});
		{
		AnnotationVisitor av2 = av1.visitArray("sAVal");
		av2.visit(null, "A");
		av2.visit(null, "B");
		av2.visit(null, "C");
		av2.visitEnd();
		}
		av1.visitEnd();
		}
		av0.visitEnd();
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
		cw.visitEnd();


	}

	@Override
	protected String getTargetClass() {
		return "soot.asm.backend.targets.AnnotatedAnnotatedClass";
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
