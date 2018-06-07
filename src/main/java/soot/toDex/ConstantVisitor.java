package soot.toDex;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vallée-Rai and others
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
import org.jf.dexlib2.iface.reference.StringReference;
import org.jf.dexlib2.iface.reference.TypeReference;
import org.jf.dexlib2.immutable.reference.ImmutableStringReference;
import org.jf.dexlib2.immutable.reference.ImmutableTypeReference;

import soot.jimple.AbstractConstantSwitch;
import soot.jimple.ClassConstant;
import soot.jimple.DoubleConstant;
import soot.jimple.FloatConstant;
import soot.jimple.IntConstant;
import soot.jimple.LongConstant;
import soot.jimple.NullConstant;
import soot.jimple.Stmt;
import soot.jimple.StringConstant;
import soot.toDex.instructions.Insn;
import soot.toDex.instructions.Insn11n;
import soot.toDex.instructions.Insn21c;
import soot.toDex.instructions.Insn21s;
import soot.toDex.instructions.Insn31i;
import soot.toDex.instructions.Insn51l;
import soot.util.Switchable;

/**
 * A visitor that builds a list of instructions from the Jimple constants it visits.<br>
 * <br>
 * Use {@link Switchable#apply(soot.util.Switch)} with this visitor to add statements. These are added to the instructions in
 * the {@link StmtVisitor}.<br>
 * Do not forget to use {@link #setDestination(Register)} to set the storage location for the constant.
 *
 * @see StmtVisitor
 */
class ConstantVisitor extends AbstractConstantSwitch {

  private StmtVisitor stmtV;

  private Register destinationReg;

  private Stmt origStmt;

  public ConstantVisitor(StmtVisitor stmtV) {
    this.stmtV = stmtV;
  }

  public void setDestination(Register destinationReg) {
    this.destinationReg = destinationReg;
  }

  public void setOrigStmt(Stmt stmt) {
    this.origStmt = stmt;
  }

  public void defaultCase(Object o) {
    // const* opcodes not used since there seems to be no point in doing so:
    // CONST_HIGH16, CONST_WIDE_HIGH16
    throw new Error("unknown Object (" + o.getClass() + ") as Constant: " + o);
  }

  public void caseStringConstant(StringConstant s) {
    StringReference ref = new ImmutableStringReference(s.value);
    stmtV.addInsn(new Insn21c(Opcode.CONST_STRING, destinationReg, ref), origStmt);
  }

  public void caseClassConstant(ClassConstant c) {
    TypeReference referencedClass = new ImmutableTypeReference(c.getValue());
    stmtV.addInsn(new Insn21c(Opcode.CONST_CLASS, destinationReg, referencedClass), origStmt);
  }

  public void caseLongConstant(LongConstant l) {
    long constant = l.value;
    stmtV.addInsn(buildConstWideInsn(constant), origStmt);
  }

  private Insn buildConstWideInsn(long literal) {
    if (SootToDexUtils.fitsSigned16(literal)) {
      return new Insn21s(Opcode.CONST_WIDE_16, destinationReg, (short) literal);
    } else if (SootToDexUtils.fitsSigned32(literal)) {
      return new Insn31i(Opcode.CONST_WIDE_32, destinationReg, (int) literal);
    } else {
      return new Insn51l(Opcode.CONST_WIDE, destinationReg, literal);
    }
  }

  public void caseDoubleConstant(DoubleConstant d) {
    long longBits = Double.doubleToLongBits(d.value);
    stmtV.addInsn(buildConstWideInsn(longBits), origStmt);
  }

  public void caseFloatConstant(FloatConstant f) {
    int intBits = Float.floatToIntBits(f.value);
    stmtV.addInsn(buildConstInsn(intBits), origStmt);
  }

  private Insn buildConstInsn(int literal) {
    if (SootToDexUtils.fitsSigned4(literal)) {
      return new Insn11n(Opcode.CONST_4, destinationReg, (byte) literal);
    } else if (SootToDexUtils.fitsSigned16(literal)) {
      return new Insn21s(Opcode.CONST_16, destinationReg, (short) literal);
    } else {
      return new Insn31i(Opcode.CONST, destinationReg, literal);
    }
  }

  public void caseIntConstant(IntConstant i) {
    stmtV.addInsn(buildConstInsn(i.value), origStmt);
  }

  public void caseNullConstant(NullConstant v) {
    // dex bytecode spec says: "In terms of bitwise representation, (Object)
    // null == (int) 0."
    stmtV.addInsn(buildConstInsn(0), origStmt);
  }

}
