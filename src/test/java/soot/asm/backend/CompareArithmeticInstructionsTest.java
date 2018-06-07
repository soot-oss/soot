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

import org.objectweb.asm.*;
import org.objectweb.asm.util.TraceClassVisitor;

/**
 * Test for greater-than and less-equal bytecode instructions
 *
 * @author Tobias Hamann, Florian Kuebler, Dominik Helm, Lukas Sommer
 *
 */
public class CompareArithmeticInstructionsTest extends AbstractASMBackendTest {

    @Override
    protected void generate(TraceClassVisitor cw) {

        FieldVisitor fv;
        MethodVisitor mv;

        cw.visit(V1_1, ACC_PUBLIC + ACC_SUPER,
        		"soot/asm/backend/targets/CompareArithmeticInstuctions",
        		null, "java/lang/Object", null);
        cw.visitSource("CompareArithmeticInstuctions.java", null);

        {
            fv = cw.visitField(0, "i", "I", null, null);
            fv.visitEnd();
        }
        {
            fv = cw.visitField(0, "f", "F", null, null);
            fv.visitEnd();
        }
        {
            fv = cw.visitField(0, "d", "D", null, null);
            fv.visitEnd();
        }
        {
            fv = cw.visitField(0, "l", "J", null, null);
            fv.visitEnd();
        }
        {
            fv = cw.visitField(0, "b", "B", null, null);
            fv.visitEnd();
        }
        {
            fv = cw.visitField(0, "c", "C", null, null);
            fv.visitEnd();
        }
        {
            fv = cw.visitField(0, "s", "S", null, null);
            fv.visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitInsn(ICONST_2);
            mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/CompareArithmeticInstuctions", "i", "I");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitLdcInsn(new Float("221349.02"));
            mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/CompareArithmeticInstuctions", "f", "F");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitLdcInsn(new Double("2123996.1231231233"));
            mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/CompareArithmeticInstuctions", "d", "D");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitLdcInsn(new Long(2L));
            mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/CompareArithmeticInstuctions", "l", "J");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitInsn(ICONST_2);
            mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/CompareArithmeticInstuctions", "b", "B");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitInsn(ICONST_4);
            mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/CompareArithmeticInstuctions", "c", "C");
            mv.visitVarInsn(ALOAD, 0);
            mv.visitInsn(ICONST_3);
            mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/CompareArithmeticInstuctions", "s", "S");
            mv.visitInsn(RETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(0, "compareb", "(B)V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/CompareArithmeticInstuctions", "b", "B");
            mv.visitVarInsn(ILOAD, 1);
            Label l0 = new Label();
            mv.visitJumpInsn(IF_ICMPGT, l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitInsn(ICONST_2);
            mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/CompareArithmeticInstuctions", "b", "B");
            mv.visitLabel(l0);
            //mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/CompareArithmeticInstuctions", "b", "B");
            mv.visitVarInsn(ILOAD, 1);
            Label l1 = new Label();
            mv.visitJumpInsn(IF_ICMPLE, l1);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitInsn(ICONST_1);
            mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/CompareArithmeticInstuctions", "b", "B");
            mv.visitLabel(l1);
            //mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitInsn(RETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(0, "comparec", "(C)V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/CompareArithmeticInstuctions", "c", "C");
            mv.visitVarInsn(ILOAD, 1);
            Label l0 = new Label();
            mv.visitJumpInsn(IF_ICMPGT, l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitInsn(ICONST_2);
            mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/CompareArithmeticInstuctions", "c", "C");
            mv.visitLabel(l0);
            //mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/CompareArithmeticInstuctions", "c", "C");
            mv.visitVarInsn(ILOAD, 1);
            Label l1 = new Label();
            mv.visitJumpInsn(IF_ICMPLE, l1);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitInsn(ICONST_3);
            mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/CompareArithmeticInstuctions", "c", "C");
            mv.visitLabel(l1);
            //mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitInsn(RETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(0, "compared", "(D)V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/CompareArithmeticInstuctions", "d", "D");
            mv.visitVarInsn(DLOAD, 1);
            mv.visitInsn(DCMPG);
            Label l0 = new Label();
            mv.visitJumpInsn(IFGT, l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitLdcInsn(new Double("2.0"));
            mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/CompareArithmeticInstuctions", "d", "D");
            mv.visitLabel(l0);
            //mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/CompareArithmeticInstuctions", "d", "D");
            mv.visitVarInsn(DLOAD, 1);
            mv.visitInsn(DCMPL);
            Label l1 = new Label();
            mv.visitJumpInsn(IFLE, l1);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitInsn(DCONST_1);
            mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/CompareArithmeticInstuctions", "d", "D");
            mv.visitLabel(l1);
            //mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitInsn(RETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(0, "comparef", "(F)V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/CompareArithmeticInstuctions", "f", "F");
            mv.visitVarInsn(FLOAD, 1);
            mv.visitInsn(FCMPG);
            Label l0 = new Label();
            mv.visitJumpInsn(IFGT, l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitInsn(FCONST_2);
            mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/CompareArithmeticInstuctions", "f", "F");
            mv.visitLabel(l0);
            //mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/CompareArithmeticInstuctions", "f", "F");
            mv.visitVarInsn(FLOAD, 1);
            mv.visitInsn(FCMPL);
            Label l1 = new Label();
            mv.visitJumpInsn(IFLE, l1);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitInsn(FCONST_1);
            mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/CompareArithmeticInstuctions", "f", "F");
            mv.visitLabel(l1);
            //mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitInsn(RETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(0, "comparei", "(I)V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/CompareArithmeticInstuctions", "i", "I");
            mv.visitVarInsn(ILOAD, 1);
            Label l0 = new Label();
            mv.visitJumpInsn(IF_ICMPGT, l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitInsn(ICONST_2);
            mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/CompareArithmeticInstuctions", "i", "I");
            mv.visitLabel(l0);
            //mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/CompareArithmeticInstuctions", "i", "I");
            mv.visitVarInsn(ILOAD, 1);
            Label l1 = new Label();
            mv.visitJumpInsn(IF_ICMPLE, l1);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitInsn(ICONST_1);
            mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/CompareArithmeticInstuctions", "i", "I");
            mv.visitLabel(l1);
            //mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitInsn(RETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(0, "comparel", "(J)V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/CompareArithmeticInstuctions", "l", "J");
            mv.visitVarInsn(LLOAD, 1);
            mv.visitInsn(LCMP);
            Label l0 = new Label();
            mv.visitJumpInsn(IFGT, l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitLdcInsn(new Long(2L));
            mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/CompareArithmeticInstuctions", "l", "J");
            mv.visitLabel(l0);
            //mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/CompareArithmeticInstuctions", "l", "J");
            mv.visitVarInsn(LLOAD, 1);
            mv.visitInsn(LCMP);
            Label l1 = new Label();
            mv.visitJumpInsn(IFLE, l1);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitInsn(LCONST_1);
            mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/CompareArithmeticInstuctions", "l", "J");
            mv.visitLabel(l1);
            //mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitInsn(RETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }
        {
            mv = cw.visitMethod(0, "compares", "(S)V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/CompareArithmeticInstuctions", "s", "S");
            mv.visitVarInsn(ILOAD, 1);
            Label l0 = new Label();
            mv.visitJumpInsn(IF_ICMPGT, l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitInsn(ICONST_1);
            mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/CompareArithmeticInstuctions", "s", "S");
            mv.visitLabel(l0);
            //mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitFieldInsn(GETFIELD, "soot/asm/backend/targets/CompareArithmeticInstuctions", "s", "S");
            mv.visitVarInsn(ILOAD, 1);
            Label l1 = new Label();
            mv.visitJumpInsn(IF_ICMPLE, l1);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitInsn(ICONST_3);
            mv.visitFieldInsn(PUTFIELD, "soot/asm/backend/targets/CompareArithmeticInstuctions", "s", "S");
            mv.visitLabel(l1);
            //mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            mv.visitInsn(RETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }
        cw.visitEnd();


    }

    @Override
    protected String getTargetClass() {
        return "soot.asm.backend.targets.CompareArithmeticInstuctions";
    }

}
