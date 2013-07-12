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

import org.jf.dexlib2.iface.reference.FieldReference;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.ReferenceInstruction;
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction;

import soot.dexpler.DexBody;
import soot.dexpler.IDalvikTyper;
import soot.jimple.AssignStmt;
import soot.jimple.Jimple;
import soot.jimple.StaticFieldRef;
import soot.jimple.internal.JAssignStmt;

public class SgetInstruction extends FieldInstruction {

    AssignStmt assign = null;
  
    public SgetInstruction (Instruction instruction, int codeAdress) {
        super(instruction, codeAdress);
    }

    public void jimplify (DexBody body) {
        int dest = ((OneRegisterInstruction)instruction).getRegisterA();
        FieldReference f = (FieldReference)((ReferenceInstruction)instruction).getReference();
        StaticFieldRef r = Jimple.v().newStaticFieldRef(getStaticSootFieldRef(f));
        assign = Jimple.v().newAssignStmt(body.getRegisterLocal(dest), r);
        setUnit(assign);
        tagWithLineNumber(assign);
        body.add(assign);
        
		}
		public void getConstraint(IDalvikTyper dalvikTyper) {
				if (IDalvikTyper.ENABLE_DVKTYPER) {
          int op = (int)instruction.getOpcode().value;
          dalvikTyper.captureAssign((JAssignStmt)assign, op);
        }
    }

    @Override
    boolean overridesRegister(int register) {
        OneRegisterInstruction i = (OneRegisterInstruction) instruction;
        int dest = i.getRegisterA();
        return register == dest;
    }
}
