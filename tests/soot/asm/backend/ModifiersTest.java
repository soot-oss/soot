package soot.asm.backend;

import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.util.TraceClassVisitor;

/**
 * Test for various combinations of accessibility modifiers
 *
 * @author Tobias Hamann, Florian Kuebler, Dominik Helm, Lukas Sommer
 *
 */
public class ModifiersTest extends AbstractASMBackendTest {

	@Override
	protected void generate(TraceClassVisitor cw) {
		MethodVisitor mv;
		FieldVisitor fv;
		

		cw.visit(V1_3, ACC_PUBLIC + ACC_SUPER + ACC_ABSTRACT, "soot/asm/backend/targets/Modifiers", null, "java/lang/Object", null);
		cw.visitSource("Modifiers.java", null);

		{
		fv = cw.visitField(ACC_PRIVATE + ACC_VOLATILE, "i", "I", null, null);
		fv.visitEnd();
		}
		{
		fv = cw.visitField(ACC_PRIVATE + ACC_FINAL, "j", "I", null, new Integer(213));
		fv.visitEnd();
		}
		{
		fv = cw.visitField(ACC_PRIVATE + ACC_TRANSIENT, "k", "I", null, null);
		fv.visitEnd();
		}
		{
		mv = cw.visitMethod(ACC_PUBLIC + ACC_STRICT, "<init>", "()V", null, null);
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitIntInsn(SIPUSH, 213);
		mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/Modifiers", "j", "I");
		mv.visitInsn(RETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
		}
		{
		mv = cw.visitMethod(ACC_PUBLIC + ACC_FINAL + ACC_STRICT, "a", "()V", null, null);
		mv.visitCode();
		mv.visitInsn(RETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
		}
		{
		mv = cw.visitMethod(ACC_PUBLIC + ACC_SYNCHRONIZED + ACC_STRICT, "b", "()V", null, null);
		mv.visitCode();
		mv.visitInsn(RETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
		}
		{
		mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC + ACC_STRICT, "c", "()V", null, null);
		mv.visitCode();
		mv.visitInsn(RETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
		}
		{
		mv = cw.visitMethod(ACC_STRICT, "d", "()V", null, null);
		mv.visitCode();
		mv.visitInsn(RETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
		}
		{
		mv = cw.visitMethod(ACC_PROTECTED + ACC_STRICT, "e", "()V", null, null);
		mv.visitCode();
		mv.visitInsn(RETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
		}
		{
		mv = cw.visitMethod(ACC_ABSTRACT, "f", "()V", null, null);
		mv.visitEnd();
		}
		{
			if (targetCompiler == TargetCompiler.eclipse)
				mv = cw.visitMethod(ACC_PRIVATE + ACC_NATIVE, "g", "()V", null, null);
			else
				mv = cw.visitMethod(ACC_PRIVATE + ACC_NATIVE + ACC_STRICT, "g", "()V", null, null);
		mv.visitEnd();
		}
		cw.visitEnd();


	}

	@Override
	protected String getTargetClass() {
		return "soot.asm.backend.targets.Modifiers";
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
		return "1.3";
	}

}
