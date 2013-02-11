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

import org.jf.dexlib.TypeIdItem;
import org.jf.dexlib.Code.Instruction;
import org.jf.dexlib.Code.Format.Instruction35c;

import soot.ArrayType;
import soot.Type;
import soot.dexpler.DexBody;
import soot.dexpler.DexType;
import soot.dexpler.IDalvikTyper;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;
import soot.jimple.NewArrayExpr;
import soot.jimple.internal.JAssignStmt;

public class FilledNewArrayInstruction extends FilledArrayInstruction {

    AssignStmt assign = null;
  
    public FilledNewArrayInstruction (Instruction instruction, int codeAdress) {
        super(instruction, codeAdress);
    }

    public void jimplify (DexBody body) {
        if(!(instruction instanceof Instruction35c))
            throw new IllegalArgumentException("Expected Instruction35c but got: "+instruction.getClass());

        Instruction35c filledNewArrayInstr = (Instruction35c)instruction;

        int[] regs = {filledNewArrayInstr.getRegisterD(),
                      filledNewArrayInstr.getRegisterE(),
                      filledNewArrayInstr.getRegisterF(),
                      filledNewArrayInstr.getRegisterG(),
                      filledNewArrayInstr.getRegisterA()
                     };

//        NopStmt nopStmtBeginning = Jimple.v().newNopStmt();
//        body.add(nopStmtBeginning);

        int usedRegister = filledNewArrayInstr.getRegCount();

        Type t = DexType.toSoot((TypeIdItem) filledNewArrayInstr.getReferencedItem());
        // NewArrayExpr needs the ElementType as it increases the array dimension by 1
        Type arrayType = ((ArrayType) t).getElementType();

        NewArrayExpr arrayExpr = Jimple.v().newNewArrayExpr(arrayType, IntConstant.v(usedRegister));
        // new local generated intentional, will be moved to real register by MoveResult
        arrayLocal = body.getStoreResultLocal();
        assign = Jimple.v().newAssignStmt(arrayLocal, arrayExpr);
        body.add (assign);
        for (int i = 0; i < usedRegister; i++) {
          ArrayRef arrayRef = Jimple.v().newArrayRef(arrayLocal, IntConstant.v(i));

          AssignStmt assign = Jimple.v().newAssignStmt(arrayRef, body.getRegisterLocal(regs[i]));
          tagWithLineNumber(assign);
          body.add(assign);
      }
//      NopStmt nopStmtEnd = Jimple.v().newNopStmt();
//      body.add(nopStmtEnd);
//      defineBlock(nopStmtBeginning, nopStmtEnd);
      setUnit (assign);
      
//      body.setDanglingInstruction(this);

		}
    
		public void getConstraint(IDalvikTyper dalvikTyper) {
				if (IDalvikTyper.ENABLE_DVKTYPER) {
          int op = (int)instruction.opcode.value;
          dalvikTyper.captureAssign((JAssignStmt)assign, op); // TODO: ref. type may be null!
        }


    }

    @Override
    boolean isUsedAsFloatingPoint(DexBody body, int register) {
        Instruction35c i = (Instruction35c) instruction;
        Type arrayType = DexType.toSoot((TypeIdItem) i.getReferencedItem());
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
            register == i.getRegisterA();
    }


}
