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

import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.NarrowLiteralInstruction;
import org.jf.dexlib2.iface.instruction.TwoRegisterInstruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction22b;
import org.jf.dexlib2.iface.instruction.formats.Instruction22s;

import soot.Local;
import soot.Value;
import soot.dexpler.DexBody;
import soot.dexpler.tags.IntOpTag;
import soot.jimple.AssignStmt;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;

public class BinopLitInstruction extends TaggedInstruction {

  public BinopLitInstruction(Instruction instruction, int codeAdress) {
    super(instruction, codeAdress);
  }

  @Override
  public void jimplify(DexBody body) {
    if (!(instruction instanceof Instruction22s) && !(instruction instanceof Instruction22b)) {
      throw new IllegalArgumentException("Expected Instruction22s or Instruction22b but got: " + instruction.getClass());
    }

    NarrowLiteralInstruction binOpLitInstr = (NarrowLiteralInstruction) this.instruction;
    int dest = ((TwoRegisterInstruction) instruction).getRegisterA();
    int source = ((TwoRegisterInstruction) instruction).getRegisterB();

    Local source1 = body.getRegisterLocal(source);

    IntConstant constant = IntConstant.v(binOpLitInstr.getNarrowLiteral());

    Value expr = getExpression(source1, constant);

    AssignStmt assign = Jimple.v().newAssignStmt(body.getRegisterLocal(dest), expr);
    assign.addTag(getTag());

    setUnit(assign);
    addTags(assign);
    body.add(assign);

    /*
     * if (IDalvikTyper.ENABLE_DVKTYPER) { Debug.printDbg(IDalvikTyper.DEBUG, "constraint: "+ assign);
     *
     * int op = (int)instruction.getOpcode().value; if (op >= 0xd8) { op -= 0xd8; } else { op -= 0xd0; } BinopExpr bexpr =
     * (BinopExpr)expr; //body.dvkTyper.setType((op == 1) ? bexpr.getOp2Box() : bexpr.getOp1Box(), op1BinType[op]);
     * DalvikTyper.v().setType(((JAssignStmt)assign).leftBox, op1BinType[op], false);
     *
     * }
     */
  }

  @SuppressWarnings("fallthrough")
  private Value getExpression(Local source1, Value source2) {
    Opcode opcode = instruction.getOpcode();
    switch (opcode) {
      case ADD_INT_LIT16:
        setTag(new IntOpTag());
      case ADD_INT_LIT8:
        setTag(new IntOpTag());
        return Jimple.v().newAddExpr(source1, source2);

      case RSUB_INT:
        setTag(new IntOpTag());
      case RSUB_INT_LIT8:
        setTag(new IntOpTag());
        return Jimple.v().newSubExpr(source2, source1);

      case MUL_INT_LIT16:
        setTag(new IntOpTag());
      case MUL_INT_LIT8:
        setTag(new IntOpTag());
        return Jimple.v().newMulExpr(source1, source2);

      case DIV_INT_LIT16:
        setTag(new IntOpTag());
      case DIV_INT_LIT8:
        setTag(new IntOpTag());
        return Jimple.v().newDivExpr(source1, source2);

      case REM_INT_LIT16:
        setTag(new IntOpTag());
      case REM_INT_LIT8:
        setTag(new IntOpTag());
        return Jimple.v().newRemExpr(source1, source2);

      case AND_INT_LIT8:
        setTag(new IntOpTag());
      case AND_INT_LIT16:
        setTag(new IntOpTag());
        return Jimple.v().newAndExpr(source1, source2);

      case OR_INT_LIT16:
        setTag(new IntOpTag());
      case OR_INT_LIT8:
        setTag(new IntOpTag());
        return Jimple.v().newOrExpr(source1, source2);

      case XOR_INT_LIT16:
        setTag(new IntOpTag());
      case XOR_INT_LIT8:
        setTag(new IntOpTag());
        return Jimple.v().newXorExpr(source1, source2);

      case SHL_INT_LIT8:
        setTag(new IntOpTag());
        return Jimple.v().newShlExpr(source1, source2);

      case SHR_INT_LIT8:
        setTag(new IntOpTag());
        return Jimple.v().newShrExpr(source1, source2);

      case USHR_INT_LIT8:
        setTag(new IntOpTag());
        return Jimple.v().newUshrExpr(source1, source2);

      default:
        throw new RuntimeException("Invalid Opcode: " + opcode);
    }
  }

  @Override
  boolean overridesRegister(int register) {
    TwoRegisterInstruction i = (TwoRegisterInstruction) instruction;
    int dest = i.getRegisterA();
    return register == dest;
  }
}
