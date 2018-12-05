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
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.util.TraceClassVisitor;

/**
 * Test for bitwise logical operation bytecode instructions
 *
 * @author Tobias Hamann, Florian Kuebler, Dominik Helm, Lukas Sommer
 *
 */
public class LogicalOperationsTest extends AbstractASMBackendTest {

	@Override
	protected void generate(TraceClassVisitor cw) {
		MethodVisitor mv;
		FieldVisitor fv;

		cw.visit(V1_1, ACC_PUBLIC + ACC_SUPER, "soot/asm/backend/targets/LogicalOperations", null,
				"java/lang/Object", null);
		cw.visitSource("LogicalOperations.java", null);
		{
			fv = cw.visitField(ACC_PRIVATE, "i1", "I", null, null);
			fv.visitEnd();
		}
		{
			fv = cw.visitField(ACC_PRIVATE, "b1", "Z", null, null);
			fv.visitEnd();
		}
		{
			fv = cw.visitField(ACC_PRIVATE, "l1", "J", null, null);
			fv.visitEnd();
		}
		{
			fv = cw.visitField(ACC_PRIVATE, "i2", "I", null, null);
			fv.visitEnd();
		}
		{
			fv = cw.visitField(ACC_PRIVATE, "b2", "Z", null, null);
			fv.visitEnd();
		}
		{
			fv = cw.visitField(ACC_PRIVATE, "l2", "J", null, null);
			fv.visitEnd();
		}
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
			mv = cw.visitMethod(ACC_PUBLIC, "doAnd", "()V", null, null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/LogicalOperations", "i2", "I");
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/LogicalOperations", "i1", "I");
			mv.visitInsn(IAND);
			mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/LogicalOperations", "i1", "I");
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/LogicalOperations", "l2", "J");
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/LogicalOperations", "l1", "J");
			mv.visitInsn(LAND);
			mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/LogicalOperations", "l1", "J");
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/LogicalOperations", "b2", "Z");
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/LogicalOperations", "b1", "Z");
			mv.visitInsn(IAND);
			mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/LogicalOperations", "b1", "Z");
			mv.visitInsn(RETURN);
			mv.visitMaxs(0, 0);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "doInv", "()V", null, null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/LogicalOperations", "i2", "I");
			mv.visitInsn(ICONST_M1);
			mv.visitInsn(IXOR);
			mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/LogicalOperations", "i1", "I");
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/LogicalOperations", "i2", "I");
			mv.visitInsn(ICONST_M1);
			mv.visitInsn(IXOR);
			mv.visitInsn(I2L);
			mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/LogicalOperations", "l1", "J");
			mv.visitInsn(RETURN);
			mv.visitMaxs(0, 0);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "doOr", "()V", null, null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/LogicalOperations", "i2", "I");
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/LogicalOperations", "i1", "I");
			mv.visitInsn(IOR);
			mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/LogicalOperations", "i1", "I");
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/LogicalOperations", "l2", "J");
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/LogicalOperations", "l1", "J");
			mv.visitInsn(LOR);
			mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/LogicalOperations", "l1", "J");
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/LogicalOperations", "b2", "Z");
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/LogicalOperations", "b1", "Z");
			mv.visitInsn(IOR);
			mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/LogicalOperations", "b1", "Z");
			mv.visitInsn(RETURN);
			mv.visitMaxs(0, 0);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "doShl", "()V", null, null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/LogicalOperations", "i1", "I");
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/LogicalOperations", "i2", "I");
			mv.visitInsn(ISHL);
			mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/LogicalOperations", "i1", "I");
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/LogicalOperations", "l1", "J");
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/LogicalOperations", "l2", "J");
			mv.visitInsn(L2I);
			mv.visitInsn(LSHL);
			mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/LogicalOperations", "l1", "J");
			mv.visitInsn(RETURN);
			mv.visitMaxs(0, 0);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "doShr", "()V", null, null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/LogicalOperations", "i1", "I");
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/LogicalOperations", "i2", "I");
			mv.visitInsn(ISHR);
			mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/LogicalOperations", "i1", "I");
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/LogicalOperations", "l1", "J");
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/LogicalOperations", "l2", "J");
			mv.visitInsn(L2I);
			mv.visitInsn(LSHR);
			mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/LogicalOperations", "l1", "J");
			mv.visitInsn(RETURN);
			mv.visitMaxs(0, 0);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "doUShr", "()V", null, null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/LogicalOperations", "i1", "I");
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/LogicalOperations", "i2", "I");
			mv.visitInsn(IUSHR);
			mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/LogicalOperations", "i1", "I");
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/LogicalOperations", "l1", "J");
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/LogicalOperations", "l2", "J");
			mv.visitInsn(L2I);
			mv.visitInsn(LUSHR);
			mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/LogicalOperations", "l1", "J");
			mv.visitInsn(RETURN);
			mv.visitMaxs(0, 0);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "doXOr", "()V", null, null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/LogicalOperations", "i2", "I");
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/LogicalOperations", "i1", "I");
			mv.visitInsn(IXOR);
			mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/LogicalOperations", "i1", "I");
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/LogicalOperations", "l2", "J");
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/LogicalOperations", "l1", "J");
			mv.visitInsn(LXOR);
			mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/LogicalOperations", "l1", "J");
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/LogicalOperations", "b2", "Z");
			mv.visitVarInsn(ALOAD, 0);
			mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/LogicalOperations", "b1", "Z");
			mv.visitInsn(IXOR);
			mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/LogicalOperations", "b1", "Z");
			mv.visitInsn(RETURN);
			mv.visitMaxs(0, 0);
			mv.visitEnd();
		}
		cw.visitEnd();

	}

	@Override
	protected String getTargetClass() {
		return "soot.asm.backend.targets.LogicalOperations";
	}

}
