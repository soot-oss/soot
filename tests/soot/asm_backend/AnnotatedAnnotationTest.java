package soot.asm_backend;

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

		cw.visit(V1_5, ACC_PUBLIC + ACC_ANNOTATION + ACC_ABSTRACT + ACC_INTERFACE, "pkg/MyAnnotatedAnnotation", null, "java/lang/Object", new String[] { "java/lang/annotation/Annotation" }); //TODO V1_1 seems wrong here
		cw.visitSource("MyAnnotatedAnnotation.java", null);
		
		{
		mv = cw.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "value", "()Lpkg/MyTestAnnotation;", null, null);
		mv.visitEnd();
		}
		cw.visitEnd();

	}

	@Override
	protected String getTargetClass() {
		return "pkg.MyAnnotatedAnnotation";
	}

	@Override
	protected String getTargetFolder() {
		return "./testcode_asm_backend";
	}

	@Override
	protected String getClassPathFolder() {
		return "./testcode_asm_backend";
	}

}
