package soot.asm.backend;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.util.TraceClassVisitor;

/**
 * Test for an annotation class
 *
 * @author Tobias Hamann, Florian Kuebler, Dominik Helm, Lukas Sommer
 *
 */
public class AnnotationTest extends AbstractASMBackendTest {

	@Override
	protected void generate(TraceClassVisitor cw) {
		MethodVisitor mv;
		AnnotationVisitor av0;

		cw.visit(V1_5, ACC_PUBLIC + ACC_ANNOTATION + ACC_ABSTRACT
				+ ACC_INTERFACE, "soot/asm/backend/targets/MyTestAnnotation", null,
				"java/lang/Object",
				new String[] { "java/lang/annotation/Annotation" });
		
		cw.visitSource("MyTestAnnotation.java", null);

		{
		av0 = cw.visitAnnotation("Ljava/lang/annotation/Retention;", true);
		av0.visitEnum("value", "Ljava/lang/annotation/RetentionPolicy;", "RUNTIME");
		av0.visitEnd();
		}
		{
		mv = cw.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "bVal", "()B", null, null);
		mv.visitEnd();
		}
		{
		mv = cw.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "dVal", "()D", null, null);
		mv.visitEnd();
		}
		{
		mv = cw.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "fVal", "()F", null, null);
		mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "iAVal", "()[I", null, null);
			mv.visitEnd();
		}
		{
		mv = cw.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "iVal", "()I", null, null);
		mv.visitEnd();
		}
		{
		mv = cw.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "lVal", "()J", null, null);
		mv.visitEnd();
		}
		{
		mv = cw.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "rVal", "()Ljava/lang/Class;",
				"()Ljava/lang/Class<Lsoot/asm/backend/targets/AnnotatedClass;>;", null);
		mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "sAVal", "()[Ljava/lang/String;", null, null);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "sVal", "()S", null, null);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "strVal", "()Ljava/lang/String;", null, null);
			mv.visitEnd();
		}
		{
		mv = cw.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "zVal", "()Z", null, null);
		mv.visitEnd();
		}


		cw.visitEnd();

	}

	@Override
	protected String getTargetClass() {
		return "soot.asm.backend.targets.MyTestAnnotation";
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
