package soot.asm.backend;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vallée-Rai and others
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.util.TraceClassVisitor;

/**
 * Test for several store bytecode instructions
 *
 * @author Tobias Hamann, Florian Kuebler, Dominik Helm, Lukas Sommer
 *
 */
public class StoresTest extends AbstractASMBackendTest {

	@Override
	protected void generate(TraceClassVisitor visitor) {
		MethodVisitor mv;

		visitor.visit(V1_1, ACC_PUBLIC + ACC_SUPER, "soot/asm/backend/targets/Stores", null, "java/lang/Object", null);
		visitor.visitSource("Stores.java", null);
		{
		mv = visitor.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
		mv.visitInsn(RETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
		}
		if (targetCompiler == TargetCompiler.eclipse) {
		mv = visitor.visitMethod(ACC_PUBLIC, "doSth", "()I", null, null);
		mv.visitCode();
		mv.visitLdcInsn(new Integer(2343249));
		mv.visitVarInsn(ISTORE, 0);
		mv.visitLdcInsn(new Double("3.14324"));
		mv.visitVarInsn(DSTORE, 1);
		mv.visitLdcInsn(new Float("3.143"));
		mv.visitVarInsn(FSTORE, 3);
		mv.visitIntInsn(SIPUSH, 4636);
		mv.visitVarInsn(ISTORE, 4);
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
		mv.visitInsn(LCONST_0);
		mv.visitInsn(LCMP);
		Label l0 = new Label();
		mv.visitJumpInsn(IFLE, l0);
		mv.visitInsn(ICONST_1);
        mv.visitVarInsn(ISTORE, 6);
		Label l1 = new Label();
		mv.visitJumpInsn(GOTO, l1);
		mv.visitLabel(l0);
		//mv.visitFrame(F_FULL, 5, new Object[] {"soot/asm/backend/targets/Stores", INTEGER, DOUBLE, FLOAT, INTEGER}, 0, new Object[] {});
		mv.visitInsn(ICONST_0);
        mv.visitVarInsn(ISTORE, 6);
        mv.visitLabel(l1);
        //mv.visitFrame(F_SAME1, 0, null, 1, new Object[] {INTEGER});
		mv.visitTypeInsn(NEW, "java/lang/Object");
		mv.visitInsn(DUP);
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
		mv.visitVarInsn(ASTORE, 5);
		mv.visitInsn(ICONST_3);
		mv.visitIntInsn(NEWARRAY, T_INT);
		mv.visitInsn(ICONST_1);
		mv.visitLdcInsn(new Integer(24355764));
		mv.visitInsn(IASTORE);
		mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
		mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
		mv.visitInsn(DUP);
        
		mv.visitVarInsn(ILOAD, 0);
		mv.visitInsn(I2D);
		mv.visitVarInsn(DLOAD, 1);
		mv.visitInsn(DADD);
		mv.visitVarInsn(FLOAD, 3);
		mv.visitInsn(F2D);
		mv.visitInsn(DADD);
		mv.visitVarInsn(ILOAD, 4);
		mv.visitInsn(I2D);
		mv.visitInsn(DADD);
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/String", "valueOf", "(D)Ljava/lang/String;", false);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>",  "(Ljava/lang/String;)V", false);
		mv.visitVarInsn(ILOAD, 6);
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Z)Ljava/lang/StringBuilder;", false);
		mv.visitVarInsn(ILOAD, 0);
        mv.visitInsn(I2B);
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;", false);
		mv.visitLdcInsn(314435665);
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(J)Ljava/lang/StringBuilder;", false);
		mv.visitIntInsn(BIPUSH, 123);
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;", false);
		mv.visitLdcInsn(" ");
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
		mv.visitVarInsn(ALOAD, 5);
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/Object;)Ljava/lang/StringBuilder;", false);
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
		mv.visitVarInsn(ILOAD, 0);
		mv.visitInsn(IRETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
		}
		else {
			mv = visitor.visitMethod(ACC_PUBLIC, "doSth", "()I", null, null);
			mv.visitCode();
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
			mv.visitInsn(LCONST_0);
			mv.visitInsn(LCMP);
			Label l0 = new Label();
			mv.visitJumpInsn(IFLE, l0);
			mv.visitInsn(ICONST_1);
			mv.visitVarInsn(ISTORE, 1);			
			Label l1 = new Label();
			mv.visitJumpInsn(GOTO, l1);
			
			mv.visitLabel(l0);
			mv.visitInsn(ICONST_0);
			mv.visitVarInsn(ISTORE, 1);

			mv.visitLabel(l1);
			mv.visitTypeInsn(NEW, "java/lang/Object");
			mv.visitVarInsn(ASTORE, 0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
			mv.visitInsn(ICONST_3);
			mv.visitIntInsn(NEWARRAY, T_INT);
			mv.visitInsn(ICONST_1);
			mv.visitLdcInsn(new Integer(24355764));
			mv.visitInsn(IASTORE);
			mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
			mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
			mv.visitInsn(DUP);
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
			mv.visitLdcInsn(new Integer(2343249));
			mv.visitInsn(I2D);
			mv.visitLdcInsn(new Double("3.14324"));
			mv.visitInsn(DADD);
			mv.visitLdcInsn(new Float("3.143"));
			mv.visitInsn(F2D);
			mv.visitInsn(DADD);
			mv.visitIntInsn(SIPUSH, 4636);
			mv.visitInsn(I2D);
			mv.visitInsn(DADD);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(D)Ljava/lang/StringBuilder;", false);
			mv.visitLdcInsn("");
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
			mv.visitVarInsn(ILOAD, 1);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Z)Ljava/lang/StringBuilder;", false);
			mv.visitLdcInsn(new Integer(2343249));
			mv.visitInsn(I2B);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(I)Ljava/lang/StringBuilder;", false);
			mv.visitLdcInsn(new Long(314435665L));
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(J)Ljava/lang/StringBuilder;", false);
			mv.visitIntInsn(BIPUSH, 123);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(C)Ljava/lang/StringBuilder;", false);
			mv.visitLdcInsn(" ");
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/Object;)Ljava/lang/StringBuilder;", false);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
			mv.visitLdcInsn(new Integer(2343249));
			mv.visitInsn(IRETURN);
			mv.visitMaxs(0, 0);
			mv.visitEnd();
		}
		visitor.visitEnd();


	}

	@Override
	protected String getTargetClass() {
		return "soot.asm.backend.targets.Stores";
	}

}
