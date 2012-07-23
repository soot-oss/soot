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

import static soot.dex.Util.dottedClassName;

import java.util.HashSet;
import java.util.Set;

import org.jf.dexlib.TypeIdItem;
import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.InstructionWithReference;
import org.jf.dexlib.Code.SingleRegisterInstruction;
import org.jf.dexlib.Code.Format.Instruction21c;

import soot.dex.DexBody;
import soot.dex.DexType;
import soot.dex.DvkTyper;
import soot.jimple.AssignStmt;
import soot.jimple.ClassConstant;
import soot.jimple.Jimple;
import soot.jimple.internal.JAssignStmt;

public class ConstClassInstruction extends DexlibAbstractInstruction {

    public ConstClassInstruction (Instruction instruction, int codeAdress) {
        super(instruction, codeAdress);
    }

    public void jimplify (DexBody body) {
        if(!(instruction instanceof Instruction21c))
            throw new IllegalArgumentException("Expected Instruction21c but got: "+instruction.getClass());

        InstructionWithReference constClass = (InstructionWithReference) this.instruction;
        String referencedClass = dottedClassName(((TypeIdItem)(constClass.getReferencedItem())).getTypeDescriptor());

        // yes ClassConstant really does want neither byte code names nor dotted names, but dots replaced with slashes
        ClassConstant cc = ClassConstant.v(referencedClass.replace('.', '/'));

        int dest = ((SingleRegisterInstruction) instruction).getRegisterA();

        AssignStmt assign = Jimple.v().newAssignStmt(body.getRegisterLocal(dest), cc);
        defineBlock(assign);
        tagWithLineNumber(assign);
        body.add(assign);
        if (DvkTyper.ENABLE_DVKTYPER) {
          int op = (int)instruction.opcode.value; 
          body.captureAssign((JAssignStmt)assign, op); //TODO: classtype could be null!
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
