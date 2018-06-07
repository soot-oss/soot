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
import org.objectweb.asm.Type;
import org.objectweb.asm.util.TraceClassVisitor;

/**
 * Test for enum classes
 *
 * @author Tobias Hamann, Florian Kuebler, Dominik Helm, Lukas Sommer
 *
 */
public class EnumTest extends AbstractASMBackendTest {
	
	

	@Override
	protected void generate(TraceClassVisitor cw) {
		FieldVisitor fv;
		MethodVisitor mv;

		cw.visit(V1_5, ACC_PUBLIC + ACC_FINAL + ACC_SUPER + ACC_ENUM,
				"soot/asm/backend/targets/MyEnum",
				"Ljava/lang/Enum<Lsoot/asm/backend/targets/MyEnum;>;",
				"java/lang/Enum", null);
		cw.visitSource("MyEnum.java", null);
		{
		fv = cw.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC + ACC_ENUM, "JA", "Lsoot/asm/backend/targets/MyEnum;", null, null);
		fv.visitEnd();
		}
		{
		fv = cw.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC + ACC_ENUM, "NEIN", "Lsoot/asm/backend/targets/MyEnum;", null, null);
		fv.visitEnd();
		}
		{
			if (targetCompiler == TargetCompiler.eclipse)
				fv = cw.visitField(ACC_PRIVATE + ACC_FINAL + ACC_STATIC + ACC_SYNTHETIC, "ENUM$VALUES", "[Lsoot/asm/backend/targets/MyEnum;", null, null);
			else
				fv = cw.visitField(ACC_PRIVATE + ACC_FINAL + ACC_STATIC + ACC_SYNTHETIC, "$VALUES", "[Lsoot/asm/backend/targets/MyEnum;", null, null);
		fv.visitEnd();
		}
		{
		mv = cw.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
		mv.visitCode();
		mv.visitTypeInsn(NEW, "soot/asm/backend/targets/MyEnum");
		mv.visitInsn(DUP);
		mv.visitLdcInsn("JA");
		mv.visitInsn(ICONST_0);
		mv.visitMethodInsn(INVOKESPECIAL, "soot/asm/backend/targets/MyEnum", "<init>", "(Ljava/lang/String;I)V", false);
		mv.visitFieldInsn(PUTSTATIC, "soot/asm/backend/targets/MyEnum", "JA", "Lsoot/asm/backend/targets/MyEnum;");
		mv.visitTypeInsn(NEW, "soot/asm/backend/targets/MyEnum");
		mv.visitInsn(DUP);
		mv.visitLdcInsn("NEIN");
		mv.visitInsn(ICONST_1);
		mv.visitMethodInsn(INVOKESPECIAL, "soot/asm/backend/targets/MyEnum", "<init>", "(Ljava/lang/String;I)V", false);
		mv.visitFieldInsn(PUTSTATIC, "soot/asm/backend/targets/MyEnum", "NEIN", "Lsoot/asm/backend/targets/MyEnum;");
		mv.visitInsn(ICONST_2);
		mv.visitTypeInsn(ANEWARRAY, "soot/asm/backend/targets/MyEnum");
		mv.visitVarInsn(ASTORE, 0);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitInsn(ICONST_0);
		mv.visitFieldInsn(GETSTATIC, "soot/asm/backend/targets/MyEnum", "JA", "Lsoot/asm/backend/targets/MyEnum;");
		mv.visitInsn(AASTORE);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitInsn(ICONST_1);
		mv.visitFieldInsn(GETSTATIC, "soot/asm/backend/targets/MyEnum", "NEIN", "Lsoot/asm/backend/targets/MyEnum;");
		mv.visitInsn(AASTORE);
		mv.visitVarInsn(ALOAD, 0);
		if (targetCompiler == TargetCompiler.eclipse)
			mv.visitFieldInsn(PUTSTATIC, "soot/asm/backend/targets/MyEnum", "ENUM$VALUES", "[Lsoot/asm/backend/targets/MyEnum;");
		else
			mv.visitFieldInsn(PUTSTATIC, "soot/asm/backend/targets/MyEnum", "$VALUES", "[Lsoot/asm/backend/targets/MyEnum;");
		mv.visitInsn(RETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
		}
		{
			if (targetCompiler == TargetCompiler.eclipse)
				mv = cw.visitMethod(ACC_PRIVATE, "<init>", "(Ljava/lang/String;I)V", null, null);
			else
				mv = cw.visitMethod(ACC_PRIVATE, "<init>", "(Ljava/lang/String;I)V", "()V", null);
		mv.visitVarInsn(ALOAD, 0);
		mv.visitVarInsn(ALOAD, 1);
		mv.visitVarInsn(ILOAD, 2);
		mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Enum", "<init>", "(Ljava/lang/String;I)V", false);
		mv.visitInsn(RETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
		}
		{
		mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "valueOf", "(Ljava/lang/String;)Lsoot/asm/backend/targets/MyEnum;", null, null);
		mv.visitCode();
		mv.visitLdcInsn(Type.getType("Lsoot/asm/backend/targets/MyEnum;"));
		mv.visitVarInsn(ALOAD, 0);
		mv.visitMethodInsn(INVOKESTATIC, "java/lang/Enum", "valueOf", "(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/Enum;", false);
		mv.visitTypeInsn(CHECKCAST, "soot/asm/backend/targets/MyEnum");
		mv.visitInsn(ARETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
		}
		if (targetCompiler == TargetCompiler.eclipse){
			mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "values", "()[Lsoot/asm/backend/targets/MyEnum;", null, null);
			mv.visitCode();
			mv.visitFieldInsn(GETSTATIC, "soot/asm/backend/targets/MyEnum", "ENUM$VALUES", "[Lsoot/asm/backend/targets/MyEnum;");
			mv.visitInsn(DUP);
			mv.visitInsn(ARRAYLENGTH);
			mv.visitInsn(DUP);
			mv.visitVarInsn(ISTORE, 0);
			mv.visitTypeInsn(ANEWARRAY, "soot/asm/backend/targets/MyEnum");
			mv.visitVarInsn(ASTORE, 1);
			mv.visitInsn(ICONST_0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitInsn(ICONST_0);
			mv.visitVarInsn(ILOAD, 0);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/System", "arraycopy", "(Ljava/lang/Object;ILjava/lang/Object;II)V", false);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitInsn(ARETURN);
			mv.visitMaxs(0, 0);
			mv.visitEnd();
		}
		else {
			mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "values", "()[Lsoot/asm/backend/targets/MyEnum;", null, null);
			mv.visitCode();
			mv.visitFieldInsn(GETSTATIC, "soot/asm/backend/targets/MyEnum", "$VALUES", "[Lsoot/asm/backend/targets/MyEnum;");
//			mv.visitMethodInsn(INVOKEVIRTUAL, "[Lsoot/asm/backend/targets/MyEnum;", "clone", "()Ljava/lang/Object;", false);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "clone", "()Ljava/lang/Object;", false);
			mv.visitTypeInsn(CHECKCAST, "[Lsoot/asm/backend/targets/MyEnum;");
			mv.visitInsn(ARETURN);
			mv.visitMaxs(0, 0);
			mv.visitEnd();
		}
		cw.visitEnd();


	}

	@Override
	protected String getTargetClass() {
		return "soot.asm.backend.targets.MyEnum";
	}

}
