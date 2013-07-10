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
import org.jf.dexlib2.iface.instruction.ReferenceInstruction;
import org.jf.dexlib2.iface.instruction.TwoRegisterInstruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction22c;
import org.jf.dexlib2.iface.reference.TypeReference;

import soot.ArrayType;
import soot.IntType;
import soot.Local;
import soot.Type;
import soot.Value;
import soot.dexpler.Debug;
import soot.dexpler.DexBody;
import soot.dexpler.DexType;
import soot.dexpler.IDalvikTyper;
import soot.jimple.AssignStmt;
import soot.jimple.Jimple;
import soot.jimple.NewArrayExpr;
import soot.jimple.internal.JAssignStmt;

public class NewArrayInstruction extends DexlibAbstractInstruction {

    AssignStmt assign = null;

    public NewArrayInstruction (Instruction instruction, int codeAdress) {
        super(instruction, codeAdress);
    }

    public void jimplify (DexBody body) {

        if(!(instruction instanceof Instruction22c))
            throw new IllegalArgumentException("Expected Instruction22c but got: "+instruction.getClass());

        Instruction22c newArray = (Instruction22c)instruction;
        int dest = newArray.getRegisterA();

        Value size = body.getRegisterLocal(newArray.getRegisterB());

        Type t = DexType.toSoot((TypeReference) newArray.getReference());
        // NewArrayExpr needs the ElementType as it increases the array dimension by 1
        Type arrayType = ((ArrayType) t).getElementType();
        Debug.printDbg("new array element type: ", arrayType);

        NewArrayExpr newArrayExpr = Jimple.v().newNewArrayExpr(arrayType, size);

        Local l = body.getRegisterLocal(dest);
        assign = Jimple.v().newAssignStmt(l, newArrayExpr);

        setUnit(assign);
        tagWithLineNumber(assign);
        body.add(assign);

		}
		public void getConstraint(IDalvikTyper dalvikTyper) {
				if (IDalvikTyper.ENABLE_DVKTYPER) {
          int op = (int)instruction.getOpcode().value;
          dalvikTyper.captureAssign((JAssignStmt)assign, op); // TODO: ref. type may be null!
          NewArrayExpr newArrayExpr = (NewArrayExpr)assign.getRightOp();
          dalvikTyper.setType(newArrayExpr.getSizeBox(), IntType.v());
          dalvikTyper.setObjectType(assign.getLeftOpBox());
        }
    }

    @Override
    boolean overridesRegister(int register) {
        TwoRegisterInstruction i = (TwoRegisterInstruction) instruction;
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
