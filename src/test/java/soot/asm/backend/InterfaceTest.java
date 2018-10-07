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

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.TraceClassVisitor;

/**
 * Test for an interface class and accessibility modifiers
 *
 * @author Tobias Hamann, Florian Kuebler, Dominik Helm, Lukas Sommer
 *
 */
public class InterfaceTest extends AbstractASMBackendTest {

	@Override
	protected void generate(TraceClassVisitor visitor) {
		visitor.visit(Opcodes.V1_1, Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT + Opcodes.ACC_INTERFACE,
				"soot/asm/backend/targets/Comparable", null, "java/lang/Object",
		new String[] { "soot/asm/backend/targets/Measurable" });
		visitor.visitSource("Comparable.java", null);
		visitor.visitField(Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL + Opcodes.ACC_STATIC, "LESS", "I",
		null, new Integer(-1)).visitEnd();
		visitor.visitField(Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL + Opcodes.ACC_STATIC, "EQUAL", "I",
		null, new Integer(0)).visitEnd();
		visitor.visitField(Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL + Opcodes.ACC_STATIC, "GREATER", "I",
		null, new Integer(1)).visitEnd();
		visitor.visitMethod(Opcodes.ACC_PUBLIC + Opcodes.ACC_ABSTRACT, "compareTo",
				"(Ljava/lang/Object;)I", null, null).visitEnd();
		visitor.visitEnd();
		
	}

	@Override
	protected String getTargetClass() {
		return "soot.asm.backend.targets.Comparable";
	}

}
