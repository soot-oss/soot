/* Soot - a Java Optimization Framework
 * Copyright (C) 2012 Michael Markert, Frank Hartmann
 *
 * (c) 2012 University of Luxembourg - Interdisciplinary Centre for
 * Security Reliability and Trust (SnT) - All rights reserved
 * Alexandre Bartel
 *
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package soot.dexpler.instructions;

import java.util.HashSet;
import java.util.Set;

import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction;
import org.jf.dexlib2.iface.instruction.ReferenceInstruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction21c;
import org.jf.dexlib2.iface.reference.TypeReference;

import soot.Type;
import soot.dexpler.DexBody;
import soot.dexpler.DexType;
import soot.dexpler.IDalvikTyper;
import soot.jimple.AssignStmt;
import soot.jimple.ClassConstant;
import soot.jimple.Jimple;

public class ConstClassInstruction extends DexlibAbstractInstruction {

    AssignStmt assign = null;

    public ConstClassInstruction (Instruction instruction, int codeAdress) {
        super(instruction, codeAdress);
    }

    public void jimplify (DexBody body) {
        if(!(instruction instanceof Instruction21c))
            throw new IllegalArgumentException("Expected Instruction21c but got: "+instruction.getClass());

        ReferenceInstruction constClass = (ReferenceInstruction) this.instruction;

        TypeReference tidi = (TypeReference)(constClass.getReference());
        String type = tidi.getType();
        if (type.startsWith("L") && type.endsWith(";"))
          type = type.replaceAll("^L", "").replaceAll(";$", "");

        int dest = ((OneRegisterInstruction) instruction).getRegisterA();
        assign = Jimple.v().newAssignStmt(body.getRegisterLocal(dest), ClassConstant.v(type));
        setUnit(assign);
        tagWithLineNumber(assign);
        body.add(assign);

		}
		public void getConstraint(IDalvikTyper dalvikTyper) {
				if (IDalvikTyper.ENABLE_DVKTYPER) {
          int op = (int)instruction.getOpcode().value;
          //dalvikTyper.captureAssign((JAssignStmt)assign, op); //TODO: classtype could be null!
          dalvikTyper.setObjectType(assign.getLeftOpBox());
        }
    }

    @Override
    boolean overridesRegister(int register) {
        OneRegisterInstruction i = (OneRegisterInstruction) instruction;
        int dest = i.getRegisterA();
        return register == dest;
    }

    @Override
    public Set<Type> introducedTypes() {
        ReferenceInstruction i = (ReferenceInstruction) instruction;

        Set<Type> types = new HashSet<Type>();
        types.add(DexType.toSoot((TypeReference) i.getReference()));
        return types;
    }
}
