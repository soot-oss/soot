package soot.asm_backend;

import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.util.TraceClassVisitor;

/**
 * Test for lambdas (invokeDynamic bytecode instruction)
 *
 * @author Tobias Hamann, Florian Kuebler, Dominik Helm, Lukas Sommer
 *
 */
public class LambdaTest extends AbstractASMBackendTest {

	@Override
	protected void generate(TraceClassVisitor cw) {
		MethodVisitor mv;

		cw.visit(V1_7, ACC_PUBLIC + ACC_SUPER, "pkg/LambdaExpression", null,
				"java/lang/Object", null);
		
		cw.visitSource("LambdaExpression.java", null);

		cw.visitInnerClass("java/lang/invoke/MethodHandles$Lookup",
				"java/lang/invoke/MethodHandles", "Lookup", ACC_PUBLIC
						+ ACC_FINAL + ACC_STATIC);

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
			mv = cw.visitMethod(
					ACC_PUBLIC + ACC_STATIC,
					"compare",
					"(Ljava/util/function/BiFunction;II)Z",
					"(Ljava/util/function/BiFunction<Ljava/lang/Integer;Ljava/lang/Integer;Ljava/lang/Boolean;>;II)Z",
					null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ILOAD, 1);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf",
					"(I)Ljava/lang/Integer;", false);
			mv.visitVarInsn(ILOAD, 2);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf",
					"(I)Ljava/lang/Integer;", false);
			mv.visitMethodInsn(INVOKEINTERFACE,
					"java/util/function/BiFunction", "apply",
					"(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
					true);
			mv.visitTypeInsn(CHECKCAST, "java/lang/Boolean");
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean",
					"booleanValue", "()Z", false);
			mv.visitInsn(IRETURN);
			mv.visitMaxs(0, 0);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main",
					"([Ljava/lang/String;)V", null, null);
			mv.visitCode();
			mv.visitInvokeDynamicInsn(
					"apply",
					"()Ljava/util/function/BiFunction;",
					new Handle(
							Opcodes.H_INVOKESTATIC,
							"java/lang/invoke/LambdaMetafactory",
							"metafactory",
							"(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;"),
					new Object[] {
							Type.getType("(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"),
							new Handle(Opcodes.H_INVOKESTATIC,
									"pkg/LambdaExpression", "lambda$0",
									"(Ljava/lang/Integer;Ljava/lang/Integer;)Ljava/lang/Boolean;"),
							Type.getType("(Ljava/lang/Integer;Ljava/lang/Integer;)Ljava/lang/Boolean;") });
			mv.visitInsn(ICONST_1);
			mv.visitInsn(ICONST_0);
			mv.visitMethodInsn(INVOKESTATIC, "pkg/LambdaExpression", "compare",
					"(Ljava/util/function/BiFunction;II)Z", false);
			mv.visitInsn(POP);
			mv.visitInsn(RETURN);
			mv.visitMaxs(0, 0);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(
					ACC_PRIVATE + ACC_STATIC + ACC_SYNTHETIC,
					"lambda$0",
					"(Ljava/lang/Integer;Ljava/lang/Integer;)Ljava/lang/Boolean;",
					null, null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue",
					"()I", false);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue",
					"()I", false);
			Label l0 = new Label();
			mv.visitJumpInsn(IF_ICMPLE, l0);
			mv.visitInsn(ICONST_1);
			mv.visitVarInsn(ISTORE, 1);
			Label l1 = new Label();
			mv.visitJumpInsn(GOTO, l1);
			mv.visitLabel(l0);
			mv.visitInsn(ICONST_0);
			mv.visitVarInsn(ISTORE, 1);
			mv.visitLabel(l1);
			mv.visitVarInsn(ILOAD, 1);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf",
					"(Z)Ljava/lang/Boolean;", false);
			mv.visitInsn(ARETURN);
			mv.visitMaxs(0, 0);
			mv.visitEnd();
		}
		cw.visitEnd();

	}

	@Override
	protected String getTargetClass() {
		return "pkg.LambdaExpression";
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
