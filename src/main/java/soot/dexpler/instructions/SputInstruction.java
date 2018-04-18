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

import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction;
import org.jf.dexlib2.iface.instruction.ReferenceInstruction;
import org.jf.dexlib2.iface.reference.FieldReference;

import soot.Local;
import soot.Type;
import soot.dexpler.DexBody;
import soot.dexpler.DexType;
import soot.dexpler.IDalvikTyper;
import soot.dexpler.typing.DalvikTyper;
import soot.jimple.AssignStmt;
import soot.jimple.Jimple;
import soot.jimple.StaticFieldRef;

public class SputInstruction extends FieldInstruction {

    public SputInstruction (Instruction instruction, int codeAdress) {
        super(instruction, codeAdress);
    }

    @Override
	public void jimplify (DexBody body) {
        int source = ((OneRegisterInstruction)instruction).getRegisterA();
        FieldReference f = (FieldReference)((ReferenceInstruction)instruction).getReference();
        StaticFieldRef instanceField = Jimple.v().newStaticFieldRef(getStaticSootFieldRef(f));
        Local sourceValue = body.getRegisterLocal(source);
        AssignStmt assign = getAssignStmt(body, sourceValue, instanceField);
        setUnit(assign);
        addTags(assign);
        body.add(assign);

		if (IDalvikTyper.ENABLE_DVKTYPER) {
          DalvikTyper.v().setType(assign.getRightOpBox(), instanceField.getType(), true);
        }
    }

    @Override
    protected Type getTargetType(DexBody body) {
        FieldReference f = (FieldReference)((ReferenceInstruction) instruction).getReference();
        return DexType.toSoot(f.getType());
    }
}
