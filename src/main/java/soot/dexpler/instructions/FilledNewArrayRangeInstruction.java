package soot.dexpler.instructions;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2012 Michael Markert, Frank Hartmann
 *
 * (c) 2012 University of Luxembourg - Interdisciplinary Centre for
 * Security Reliability and Trust (SnT) - All rights reserved
 * Alexandre Bartel
 *
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

import static soot.dexpler.Util.isFloatLike;

import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction3rc;
import org.jf.dexlib2.iface.reference.TypeReference;

import soot.ArrayType;
import soot.Local;
import soot.Type;
import soot.dexpler.DexBody;
import soot.dexpler.DexType;
import soot.dexpler.IDalvikTyper;
import soot.dexpler.typing.DalvikTyper;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;
import soot.jimple.NewArrayExpr;

public class FilledNewArrayRangeInstruction extends FilledArrayInstruction {

  public FilledNewArrayRangeInstruction(Instruction instruction, int codeAdress) {
    super(instruction, codeAdress);
  }

  @Override
  public void jimplify(DexBody body) {
    if (!(instruction instanceof Instruction3rc)) {
      throw new IllegalArgumentException("Expected Instruction3rc but got: " + instruction.getClass());
    }

    Instruction3rc filledNewArrayInstr = (Instruction3rc) instruction;

    // NopStmt nopStmtBeginning = Jimple.v().newNopStmt();
    // body.add(nopStmtBeginning);

    int usedRegister = filledNewArrayInstr.getRegisterCount();
    Type t = DexType.toSoot((TypeReference) filledNewArrayInstr.getReference());
    // NewArrayExpr needs the ElementType as it increases the array dimension by 1
    Type arrayType = ((ArrayType) t).getElementType();
    NewArrayExpr arrayExpr = Jimple.v().newNewArrayExpr(arrayType, IntConstant.v(usedRegister));
    Local arrayLocal = body.getStoreResultLocal();
    AssignStmt assignStmt = Jimple.v().newAssignStmt(arrayLocal, arrayExpr);
    body.add(assignStmt);

    for (int i = 0; i < usedRegister; i++) {
      ArrayRef arrayRef = Jimple.v().newArrayRef(arrayLocal, IntConstant.v(i));

      AssignStmt assign
          = Jimple.v().newAssignStmt(arrayRef, body.getRegisterLocal(i + filledNewArrayInstr.getStartRegister()));
      addTags(assign);
      body.add(assign);
    }
    // NopStmt nopStmtEnd = Jimple.v().newNopStmt();
    // body.add(nopStmtEnd);

    // defineBlock(nopStmtBeginning,nopStmtEnd);
    setUnit(assignStmt);

    // body.setDanglingInstruction(this);

    if (IDalvikTyper.ENABLE_DVKTYPER) {
      // Debug.printDbg(IDalvikTyper.DEBUG, "constraint: "+ assignStmt);
      DalvikTyper.v().setType(assignStmt.getLeftOpBox(), arrayExpr.getType(), false);
      // DalvikTyper.v().addConstraint(assignStmt.getLeftOpBox(), assignStmt.getRightOpBox());
    }

  }

  @Override
  boolean isUsedAsFloatingPoint(DexBody body, int register) {
    Instruction3rc i = (Instruction3rc) instruction;
    Type arrayType = DexType.toSoot((TypeReference) i.getReference());
    int startRegister = i.getStartRegister();
    int endRegister = startRegister + i.getRegisterCount();

    return register >= startRegister && register <= endRegister && isFloatLike(arrayType);
  }

}
