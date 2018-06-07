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

import soot.Immediate;
import soot.Local;
import soot.dexpler.DexBody;
import soot.jimple.ConditionExpr;
import soot.jimple.IfStmt;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;

public abstract class ConditionalJumpInstruction extends JumpInstruction implements DeferableInstruction {

  public ConditionalJumpInstruction(Instruction instruction, int codeAdress) {
    super(instruction, codeAdress);
  }

  /**
   * Return an if statement depending on the instruction.
   */
  protected abstract IfStmt ifStatement(DexBody body);

  public void jimplify(DexBody body) {
    // check if target instruction has been jimplified
    if (getTargetInstruction(body).getUnit() != null) {
      IfStmt s = ifStatement(body);
      body.add(s);
      setUnit(s);
    } else {
      // set marker unit to swap real gotostmt with otherwise
      body.addDeferredJimplification(this);
      markerUnit = Jimple.v().newNopStmt();
      unit = markerUnit;
      // beginUnit = markerUnit;
      // endUnit = markerUnit;
      // beginUnit = markerUnit;
      body.add(markerUnit);
      // Unit end = Jimple.v().newNopStmt();
      // body.add(end);
      // endUnit = end;
    }
  }

  // DalvikTyper.v() here?

  public void deferredJimplify(DexBody body) {
    IfStmt s = ifStatement(body);
    body.getBody().getUnits().swapWith(markerUnit, s); // insertAfter(s, markerUnit);
    setUnit(s);
  }

  /**
   * Get comparison expression depending on opcode against zero or null.
   *
   * If the register is used as an object this will create a comparison with null, not zero.
   *
   * @param body
   *          the containing DexBody
   * @param reg
   *          the register to compare against zero.
   */
  protected ConditionExpr getComparisonExpr(DexBody body, int reg) {
    Local one = body.getRegisterLocal(reg);
    return getComparisonExpr(one, IntConstant.v(0));
  }

  /**
   * Get comparison expression depending on opcode between two immediates
   *
   * @param one
   *          first immediate
   * @param other
   *          second immediate
   * @throws RuntimeException
   *           if this is not a IfTest or IfTestz instruction.
   */
  protected ConditionExpr getComparisonExpr(Immediate one, Immediate other) {
    Opcode opcode = instruction.getOpcode();

    switch (opcode) {
      case IF_EQ:
      case IF_EQZ:
        return Jimple.v().newEqExpr(one, other);
      case IF_NE:
      case IF_NEZ:
        return Jimple.v().newNeExpr(one, other);
      case IF_LT:
      case IF_LTZ:
        return Jimple.v().newLtExpr(one, other);
      case IF_GE:
      case IF_GEZ:
        return Jimple.v().newGeExpr(one, other);
      case IF_GT:
      case IF_GTZ:
        return Jimple.v().newGtExpr(one, other);
      case IF_LE:
      case IF_LEZ:
        return Jimple.v().newLeExpr(one, other);
      default:
        throw new RuntimeException("Instruction is not an IfTest(z) instruction.");
    }
  }
}
