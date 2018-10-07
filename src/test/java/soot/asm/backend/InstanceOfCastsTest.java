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
 * Test for instanceof and cast bytecode instructions
 *
 * @author Tobias Hamann, Florian Kuebler, Dominik Helm, Lukas Sommer
 *
 */
public class InstanceOfCastsTest extends AbstractASMBackendTest {

	@Override
	protected void generate(TraceClassVisitor cw) {
		MethodVisitor mv;

		cw.visit(V1_1, ACC_PUBLIC + ACC_SUPER, "soot/asm/backend/targets/InstanceOfCasts",
				null, "java/lang/Object", null);
		cw.visitSource("InstanceOfCasts.java", null);
		
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
		mv = cw.visitMethod(ACC_PUBLIC, "convertMeasurableArray", "([Ljava/lang/Object;)[Lsoot/asm/backend/targets/Measurable;", null, null);
		mv.visitCode();
		mv.visitVarInsn(ALOAD, 1);
		mv.visitTypeInsn(INSTANCEOF, "[Lsoot/asm/backend/targets/Measurable;");
		Label l0 = new Label();
		mv.visitJumpInsn(IFEQ, l0);
		mv.visitVarInsn(ALOAD, 1);
		mv.visitTypeInsn(CHECKCAST, "[Lsoot/asm/backend/targets/Measurable;");
		if (targetCompiler != TargetCompiler.eclipse)
			mv.visitTypeInsn(CHECKCAST, "[Lsoot/asm/backend/targets/Measurable;");
		mv.visitInsn(ARETURN);
		mv.visitLabel(l0);
		mv.visitInsn(ACONST_NULL);
		mv.visitInsn(ARETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
		}
			{
			mv = cw.visitMethod(ACC_PUBLIC, "isMeasurable", "(Ljava/lang/Object;)Z", null, null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 1);
			mv.visitTypeInsn(INSTANCEOF, "soot/asm/backend/targets/Measurable");
			mv.visitInsn(IRETURN);
			mv.visitMaxs(0, 0);
			mv.visitEnd();
			}
			cw.visitEnd();

		
	}

	@Override
	protected String getTargetClass() {
		return "soot.asm.backend.targets.InstanceOfCasts";
	}

}
