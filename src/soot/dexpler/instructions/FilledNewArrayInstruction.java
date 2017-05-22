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

import static soot.dexpler.Util.isFloatLike;

import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction35c;
import org.jf.dexlib2.iface.reference.TypeReference;

import soot.ArrayType;
import soot.Local;
import soot.Type;
import soot.dexpler.Debug;
import soot.dexpler.DexBody;
import soot.dexpler.DexType;
import soot.dexpler.IDalvikTyper;
import soot.dexpler.typing.DalvikTyper;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;
import soot.jimple.NewArrayExpr;

public class FilledNewArrayInstruction extends FilledArrayInstruction {

    public FilledNewArrayInstruction (Instruction instruction, int codeAdress) {
        super(instruction, codeAdress);
    }

    public void jimplify (DexBody body) {
        if(!(instruction instanceof Instruction35c))
            throw new IllegalArgumentException("Expected Instruction35c but got: "+instruction.getClass());

        Instruction35c filledNewArrayInstr = (Instruction35c)instruction;
        int[] regs = {filledNewArrayInstr.getRegisterC(),
        			  filledNewArrayInstr.getRegisterD(),
                      filledNewArrayInstr.getRegisterE(),
                      filledNewArrayInstr.getRegisterF(),
                      filledNewArrayInstr.getRegisterG(),
                     };
//        NopStmt nopStmtBeginning = Jimple.v().newNopStmt();
//        body.add(nopStmtBeginning);

        int usedRegister = filledNewArrayInstr.getRegisterCount();

        Type t = DexType.toSoot((TypeReference) filledNewArrayInstr.getReference());
        // NewArrayExpr needs the ElementType as it increases the array dimension by 1
        Type arrayType = ((ArrayType) t).getElementType();
        NewArrayExpr arrayExpr = Jimple.v().newNewArrayExpr(arrayType, IntConstant.v(usedRegister));
        // new local generated intentional, will be moved to real register by MoveResult
        Local arrayLocal = body.getStoreResultLocal();
        AssignStmt assign = Jimple.v().newAssignStmt(arrayLocal, arrayExpr);
        body.add (assign);
        for (int i = 0; i < usedRegister; i++) {
          ArrayRef arrayRef = Jimple.v().newArrayRef(arrayLocal, IntConstant.v(i));

          AssignStmt assign2 = Jimple.v().newAssignStmt(arrayRef, body.getRegisterLocal(regs[i]));
          addTags(assign2);
          body.add(assign2);
        }
//      NopStmt nopStmtEnd = Jimple.v().newNopStmt();
//      body.add(nopStmtEnd);
//      defineBlock(nopStmtBeginning, nopStmtEnd);
        setUnit (assign);

//      body.setDanglingInstruction(this);

		if (IDalvikTyper.ENABLE_DVKTYPER) {
			Debug.printDbg(IDalvikTyper.DEBUG, "constraint: "+ assign);
          DalvikTyper.v().setType(assign.getLeftOpBox(), arrayExpr.getType(), false);
          //DalvikTyper.v().setType(array, arrayType, isUse)
          //DalvikTyper.v().addConstraint(assign.getLeftOpBox(), assign.getRightOpBox());
        }


    }

    @Override
    boolean isUsedAsFloatingPoint(DexBody body, int register) {
        Instruction35c i = (Instruction35c) instruction;
        Type arrayType = DexType.toSoot((TypeReference) i.getReference());
        return isRegisterUsed(register) && isFloatLike(arrayType);
    }

    /**
     * Check if register is referenced by this instruction.
     *
     */
    private boolean isRegisterUsed(int register) {
        Instruction35c i = (Instruction35c) instruction;
        return register == i.getRegisterD() ||
            register == i.getRegisterE() ||
            register == i.getRegisterF() ||
            register == i.getRegisterG() ||
            register == i.getRegisterC();
    }


}
