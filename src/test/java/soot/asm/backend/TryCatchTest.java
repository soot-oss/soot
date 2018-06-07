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
 * Test for try catch bytecode instructions
 *
 * @author Tobias Hamann, Florian Kuebler, Dominik Helm, Lukas Sommer
 *
 */
public class TryCatchTest extends AbstractASMBackendTest {

	@Override
	protected void generate(TraceClassVisitor cw) {
		MethodVisitor mv;

		cw.visit(V1_1, ACC_PUBLIC + ACC_SUPER, "soot/asm/backend/targets/TryCatch", null, "java/lang/Object", null);
		cw.visitSource("TryCatch.java", null);

		{
		mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
		mv.visitInsn(RETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
		}
		if (targetCompiler == TargetCompiler.eclipse) {
		mv = cw.visitMethod(0, "doSth", "(Ljava/lang/Object;)I", null, null);
		mv.visitCode();
		Label l0 = new Label();
		Label l1 = new Label();
		Label l2 = new Label();
		mv.visitTryCatchBlock(l0, l1, l2, "java/lang/NullPointerException");
		Label l3 = new Label();
		Label l4 = new Label();
		mv.visitTryCatchBlock(l0, l3, l4, "java/lang/Throwable");
		mv.visitInsn(ICONST_0);
		mv.visitVarInsn(ISTORE, 0);
		mv.visitLabel(l0);
		mv.visitVarInsn(ALOAD, 1);
		mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "notify", "()V", false);
		mv.visitInsn(ICONST_1);
		mv.visitVarInsn(ISTORE, 0);
		mv.visitLabel(l1);
		Label l5 = new Label();
		mv.visitJumpInsn(GOTO, l5);
		mv.visitLabel(l2);
		//mv.visitFrame(F_FULL, 3, new Object[] {"soot/asm/backend/targets/TryCatch", "java/lang/Object", INTEGER}, 1, new Object[] {"java/lang/NullPointerException"});
		mv.visitVarInsn(ASTORE, 1);
		mv.visitInsn(ICONST_M1);
		mv.visitVarInsn(ISTORE, 0);
		mv.visitLabel(l3);
		mv.visitJumpInsn(GOTO, l5);
		mv.visitLabel(l4);
		//mv.visitFrame(F_SAME1, 0, null, 1, new Object[] {"java/lang/Throwable"});
		mv.visitVarInsn(ASTORE, 1);
		mv.visitLabel(l5);
		//mv.visitFrame(F_SAME, 0, null, 0, null);
		mv.visitVarInsn(ILOAD, 0);
		mv.visitInsn(IRETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
		}
		else {
			mv = cw.visitMethod(0, "doSth", "(Ljava/lang/Object;)I", null, null);
			mv.visitCode();
			Label l0 = new Label();
			Label l1 = new Label();
			Label l2 = new Label();
			mv.visitTryCatchBlock(l0, l1, l2, "java/lang/NullPointerException");
			Label l3 = new Label();
			mv.visitTryCatchBlock(l0, l1, l3, "java/lang/Throwable");
			Label l4 = new Label();
			mv.visitTryCatchBlock(l2, l4, l3, "java/lang/Throwable");
			Label l5 = new Label();
			mv.visitTryCatchBlock(l3, l5, l3, "java/lang/Throwable");
			mv.visitLabel(l0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "notify", "()V", false);
			mv.visitLabel(l1);
			mv.visitInsn(ICONST_1);
			mv.visitInsn(IRETURN);
			mv.visitLabel(l2);
			mv.visitVarInsn(ASTORE, 0);
			mv.visitLabel(l4);
			mv.visitInsn(ICONST_M1);
			mv.visitInsn(IRETURN);
			mv.visitLabel(l3);
			mv.visitVarInsn(ASTORE, 0);
			mv.visitLabel(l5);
			mv.visitInsn(ICONST_0);
			mv.visitInsn(IRETURN);
			mv.visitMaxs(0, 0);
			mv.visitEnd();
		}
		cw.visitEnd();

		
	}

	@Override
	protected String getTargetClass() {
		return "soot.asm.backend.targets.TryCatch";
	}

}
