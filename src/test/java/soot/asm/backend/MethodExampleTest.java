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

import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.TraceClassVisitor;

/**
 * Test for methods taken from the ASM 4.0 guide
 *
 * @author Tobias Hamann, Florian Kuebler, Dominik Helm, Lukas Sommer
 *
 */
public class MethodExampleTest extends AbstractASMBackendTest {

	@Override
	protected void generate(TraceClassVisitor visitor) {
		FieldVisitor fv;
		MethodVisitor mv;

		visitor.visit(Opcodes.V1_1, Opcodes.ACC_PUBLIC + Opcodes.ACC_SUPER,
				"soot/asm/backend/targets/Bean", null, "java/lang/Object", null);
		visitor.visitSource("Bean.java", null);
		{
		fv = visitor.visitField(Opcodes.ACC_PRIVATE, "f", "I", null, null);
		fv.visitEnd();
		}
		{
		mv = visitor.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
		mv.visitCode();
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
		mv.visitInsn(Opcodes.RETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
		}
		{
		mv = visitor.visitMethod(Opcodes.ACC_PUBLIC, "checkAndSetF", "(I)V", null, null);
		mv.visitCode();
		mv.visitVarInsn(Opcodes.ILOAD, 1);
		Label l0 = new Label();
		mv.visitJumpInsn(Opcodes.IFLT, l0);
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitVarInsn(Opcodes.ILOAD, 1);
		mv.visitFieldInsn(Opcodes.PUTFIELD, "soot/asm/backend/targets/Bean", "f", "I");
		Label l1 = new Label();
		mv.visitJumpInsn(Opcodes.GOTO, l1);
		mv.visitLabel(l0);
		mv.visitTypeInsn(Opcodes.NEW, "java/lang/IllegalArgumentException");
		mv.visitInsn(Opcodes.DUP);
		mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/IllegalArgumentException", "<init>", "()V", false);
		mv.visitInsn(Opcodes.ATHROW);
		mv.visitLabel(l1);
		mv.visitInsn(Opcodes.RETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
		}
		{
		mv = visitor.visitMethod(Opcodes.ACC_PUBLIC, "getF", "()I", null, null);
		mv.visitCode();
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitFieldInsn(Opcodes.GETFIELD, "soot/asm/backend/targets/Bean", "f", "I");
		mv.visitInsn(Opcodes.IRETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
		}
		{
		mv = visitor.visitMethod(Opcodes.ACC_PUBLIC, "setF", "(I)V", null, null);
		mv.visitCode();
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitVarInsn(Opcodes.ILOAD, 1);
		mv.visitFieldInsn(Opcodes.PUTFIELD, "soot/asm/backend/targets/Bean", "f", "I");
		mv.visitInsn(Opcodes.RETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
		}
		visitor.visitEnd();

	}

	@Override
	protected String getTargetClass() {
		return "soot.asm.backend.targets.Bean";
	}

}
