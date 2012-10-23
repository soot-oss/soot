/* Soot - a Java Optimization Framework
 * Copyright (C) 2012 Michael Markert, Frank Hartmann
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

package soot.dex.instructions;

import org.jf.dexlib.FieldIdItem;
import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.InstructionWithReference;
import org.jf.dexlib.Code.SingleRegisterInstruction;

import soot.Local;
import soot.Type;
import soot.dex.DexBody;
import soot.dex.DexType;
import soot.dex.IDalvikTyper;
import soot.jimple.AssignStmt;
import soot.jimple.Jimple;
import soot.jimple.StaticFieldRef;
import soot.jimple.internal.JAssignStmt;

public class SputInstruction extends FieldInstruction {

    public SputInstruction (Instruction instruction, int codeAdress) {
        super(instruction, codeAdress);
    }

    public void jimplify (DexBody body) {
        int source = ((SingleRegisterInstruction)instruction).getRegisterA();
        FieldIdItem f = (FieldIdItem)((InstructionWithReference)instruction).getReferencedItem();
        StaticFieldRef instanceField = Jimple.v().newStaticFieldRef(getStaticSootFieldRef(f));
        Local sourceValue = body.getRegisterLocal(source);
        AssignStmt assign = getAssignStmt(body, sourceValue, instanceField);
        defineBlock(assign);
        tagWithLineNumber(assign);
        body.add(assign);
        if (IDalvikTyper.ENABLE_DVKTYPER) {
          int op = (int)instruction.opcode.value;
          body.dalvikTyper.captureAssign((JAssignStmt)assign, op);
        }
    }

    @Override
    protected Type getTargetType(DexBody body) {
        FieldIdItem f = (FieldIdItem)((InstructionWithReference) instruction).getReferencedItem();
        return DexType.toSoot(f.getFieldType());
    }
}
