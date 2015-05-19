package soot.asm.backend;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.util.TraceClassVisitor;

/**
 * Test for method declaring exception
 *
 * @author Tobias Hamann, Florian Kuebler, Dominik Helm, Lukas Sommer
 *
 */
public class ExceptionTest extends AbstractASMBackendTest {

	@Override
	protected void generate(TraceClassVisitor cw) {
		MethodVisitor mv;

		cw.visit(V1_4, ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE,
				"soot/asm/backend/targets/ExceptionMethods", null,
				"java/lang/Object", null);
		
		cw.visitSource("ExceptionMethods.java", null);

		{
		mv = cw.visitMethod(ACC_PUBLIC + ACC_ABSTRACT, "foo", "()V", null, new String[] { "java/lang/NullPointerException" });
		mv.visitEnd();
		}

		cw.visitEnd();

	}

	@Override
	protected String getTargetClass() {
		return "soot.asm.backend.targets.ExceptionMethods";
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
	protected String getRequiredJavaVersion() {
		return "1.4";
	}

}
