package soot.asm.backend;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.util.TraceClassVisitor;

/**
 * Test for annotation class that contains an annotation
 *
 * @author Tobias Hamann, Florian Kuebler, Dominik Helm, Lukas Sommer
 *
 */
public class AnnotatedAnnotationTest extends AbstractASMBackendTest {

	@Override
	protected void generate(TraceClassVisitor cw) {

		MethodVisitor mv;

		cw.visit(V1_5, ACC_PUBLIC + ACC_ANNOTATION + ACC_ABSTRACT + ACC_INTERFACE,
				"soot/asm/backend/targets/MyAnnotatedAnnotation", null,
				"java/lang/Object", new String[] { "java/lang/annotation/Annotation" }); //TODO V1_1 seems wrong here
		cw.visitSource("MyAnnotatedAnnotation.java", null);
		
		{
		mv = cw.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "value",
				"()Lsoot/asm/backend/targets/MyTestAnnotation;", null, null);
		mv.visitEnd();
		}
		cw.visitEnd();

	}

	@Override
	protected String getTargetClass() {
		return "soot.asm.backend.targets.MyAnnotatedAnnotation";
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
