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

import static soot.dexpler.Util.dottedClassName;

import java.util.HashSet;
import java.util.Set;

import org.jf.dexlib.TypeIdItem;
import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.InstructionWithReference;
import org.jf.dexlib.Code.SingleRegisterInstruction;
import org.jf.dexlib.Code.Format.Instruction21c;

import soot.RefType;
import soot.dexpler.DexBody;
import soot.dexpler.DexType;
import soot.dexpler.IDalvikTyper;
import soot.jimple.AssignStmt;
import soot.jimple.Jimple;
import soot.jimple.NewExpr;

public class NewInstanceInstruction extends DexlibAbstractInstruction {

    AssignStmt assign = null;
  
    public NewInstanceInstruction (Instruction instruction, int codeAdress) {
        super(instruction, codeAdress);
    }

    public void jimplify (DexBody body) {
        Instruction21c i = (Instruction21c)instruction;
        int dest = i.getRegisterA();
        String className = dottedClassName(((TypeIdItem)(i.getReferencedItem())).getTypeDescriptor());
        RefType type = RefType.v(className);
        NewExpr n = Jimple.v().newNewExpr(type);
        assign = Jimple.v().newAssignStmt(body.getRegisterLocal(dest), n);
        setUnit(assign);
        tagWithLineNumber(assign);
        body.add(assign);
        
		}
		public void getConstraint(IDalvikTyper dalvikTyper) {
				if (IDalvikTyper.ENABLE_DVKTYPER) {
          int op = (int)instruction.opcode.value;
          //dalvikTyper.captureAssign((JAssignStmt)assign, op); // TODO: ref. type may be null!
          dalvikTyper.setObjectType(assign.getLeftOpBox());
        }
    }

    @Override
    boolean overridesRegister(int register) {
        SingleRegisterInstruction i = (SingleRegisterInstruction) instruction;
        int dest = i.getRegisterA();
        return register == dest;
    }
    
    @Override
    public Set<DexType> introducedTypes() {
        InstructionWithReference i = (InstructionWithReference) instruction;

        Set<DexType> types = new HashSet<DexType>();
        types.add(new DexType((TypeIdItem) i.getReferencedItem()));
        return types;
    }
}
