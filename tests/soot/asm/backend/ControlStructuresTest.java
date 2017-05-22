package soot.asm.backend;

import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.util.TraceClassVisitor;

/**
 * Test for table and lookup switch bytecode instructions
 *
 * @author Tobias Hamann, Florian Kuebler, Dominik Helm, Lukas Sommer
 *
 */
public class ControlStructuresTest extends AbstractASMBackendTest {

	@Override
	protected void generate(TraceClassVisitor cw) {
		MethodVisitor mv;
		FieldVisitor fv;

		cw.visit(V1_6, ACC_PUBLIC + ACC_SUPER, "soot/asm/backend/targets/ControlStructures",
				null, "java/lang/Object", null);
		
		cw.visitSource("ControlStructures.java", null);

		{
		fv = cw.visitField(0, "result", "Ljava/util/List;", "Ljava/util/List<Ljava/lang/Integer;>;", null);
		fv.visitEnd();
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
		{
		mv = cw.visitMethod(ACC_PROTECTED, "get", "(I)Ljava/util/List;", "(I)Ljava/util/List<Ljava/lang/Integer;>;", null);
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitTypeInsn(NEW, "java/util/ArrayList");
		mv.visitInsn(DUP);
		mv.visitMethodInsn(INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V", false);
		mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/ControlStructures", "result", "Ljava/util/List;");
		mv.visitVarInsn(ILOAD, 1);
		Label l0 = new Label();
		Label l1 = new Label();
		Label l2 = new Label();
		Label l3 = new Label();
		mv.visitTableSwitchInsn(1, 3, l3, new Label[] { l0, l1, l2 });
		mv.visitLabel(l0);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/ControlStructures", "result", "Ljava/util/List;");
		mv.visitInsn(ICONST_1);
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
		mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z", true);
		mv.visitInsn(POP);
		Label l4 = new Label();
		mv.visitJumpInsn(GOTO, l4);
		mv.visitLabel(l1);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/ControlStructures", "result", "Ljava/util/List;");
		mv.visitInsn(ICONST_2);
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
		mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z", true);
		mv.visitInsn(POP);
		mv.visitLabel(l2);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/ControlStructures", "result", "Ljava/util/List;");
		mv.visitInsn(ICONST_3);
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
		mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z", true);
		mv.visitInsn(POP);
		mv.visitJumpInsn(GOTO, l4);
		mv.visitLabel(l3);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/ControlStructures", "result", "Ljava/util/List;");
		mv.visitInsn(ACONST_NULL);
		mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z", true);
		mv.visitInsn(POP);
		mv.visitLabel(l4);
		mv.visitVarInsn(ILOAD, 1);
		Label l5 = new Label();
		Label l6 = new Label();
		Label l7 = new Label();
		Label l8 = new Label();
		Label l9 = new Label();
		mv.visitLookupSwitchInsn(l9, new int[] { 1, 10, 100, 1000 }, new Label[] { l5, l6, l7, l8 });
		mv.visitLabel(l5);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/ControlStructures", "result", "Ljava/util/List;");
		mv.visitInsn(ICONST_1);
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
		mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z", true);
		mv.visitInsn(POP);
		mv.visitLabel(l6);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/ControlStructures", "result", "Ljava/util/List;");
		mv.visitIntInsn(BIPUSH, 10);
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
		mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z", true);
		mv.visitInsn(POP);
		mv.visitLabel(l7);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/ControlStructures", "result", "Ljava/util/List;");
		mv.visitIntInsn(BIPUSH, 100);
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
		mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z", true);
		mv.visitInsn(POP);
		mv.visitLabel(l8);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/ControlStructures", "result", "Ljava/util/List;");
		mv.visitIntInsn(SIPUSH, 1000);
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
		mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z", true);
		mv.visitInsn(POP);
		mv.visitLabel(l9);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/ControlStructures", "result", "Ljava/util/List;");
		mv.visitInsn(ACONST_NULL);
		mv.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "add", "(Ljava/lang/Object;)Z", true);
		mv.visitInsn(POP);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/ControlStructures", "result", "Ljava/util/List;");
		mv.visitInsn(ARETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
		}
		cw.visitEnd();

	}

	@Override
	protected String getTargetClass() {
		return "soot.asm.backend.targets.ControlStructures";
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
		return "1.6";
	}
}
