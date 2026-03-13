package soot.asm;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2014 Raja Vallee-Rai and others
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

import java.util.HashSet;
import java.util.Set;

import org.jspecify.annotations.NonNull;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.TryCatchBlockNode;

import soot.Immediate;
import soot.Local;
import soot.Value;
import soot.jimple.Jimple;
import soot.jimple.Stmt;
import soot.jimple.internal.AbstractInvokeExpr;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JCaughtExceptionRef;
import soot.tagkit.Tag;

/**
 * Stack operand.
 * 
 * @author Aaloan Miftah
 */
final class Operand {

  static final Operand DWORD_DUMMY = new Operand(null, null, null);

  public static enum OperandType {
    INT, LONG, FLOAT, DOUBLE
  }

  final AbstractInsnNode insn;
  final Value value;

  OperandType type;
  Tag tag;

  Local stackLocal;
  private AsmMethodSource methodSource;
  private Set<TryCatchBlockNode> activeTrapHandlers;

  /**
   * Constructs a new stack operand.
   * 
   * @param insn
   *          the instruction that produced this operand.
   * @param value
   *          the generated value.
   */
  Operand(AbstractInsnNode insn, Value value, AsmMethodSource methodSource) {
    this.insn = insn;
    this.value = value;
    this.type = null;
    this.methodSource = methodSource;
    this.activeTrapHandlers = methodSource == null ? new HashSet<>() : new HashSet<>(methodSource.activeTrapHandlers);
  }

  Local getOrAssignValueToStackLocal() {
    if (stackLocal == null) {
      changeStackLocal(methodSource.newStackLocal());
    }

    return stackLocal;
  }

  void emitStatement() {
    if (this == DWORD_DUMMY) {
      return;
    }

    if (methodSource.getStmt(insn) != null) {
      // the operand is already used, which means side effects already happen as well
      return;
    }

    if (value instanceof AbstractInvokeExpr) {
      methodSource.setUnit(insn, Jimple.v().newInvokeStmt(value));
    } else {
      // create an assignment that uses the value because it might have side effects
      getOrAssignValueToStackLocal();
    }
  }

  void changeStackLocal(Local newStackLocal) {
    Local oldStackLocal = this.stackLocal;

    if (oldStackLocal == newStackLocal) {
      // nothing to change
      return;
    }

    Stmt stmt = methodSource.getStmt(insn);
    if (!(stmt instanceof JAssignStmt)) {
      // emit `$newStackLocal = value`
      if (value instanceof JCaughtExceptionRef) {
        methodSource.updateInlineExceptionHandler(insn, newStackLocal);
      } else {
        methodSource.setUnit(insn, Jimple.v().newAssignStmt(newStackLocal, value));
      }
    } else {
      JAssignStmt assignStmt = (JAssignStmt) stmt;
      assert assignStmt.getLeftOp() == oldStackLocal || assignStmt.getLeftOp() == newStackLocal;
      // replace `$oldStackLocal = value` with `$newStackLocal = value`
      assignStmt.setLeftOp(newStackLocal);
    }

    // Replace all usages of `oldStackLocal` with `newStackLocal`
    if (oldStackLocal != null) {
      methodSource.replace(oldStackLocal, newStackLocal);
    }

    this.stackLocal = newStackLocal;
  }

  Local toLocal() {
    if (stackLocal == null && value instanceof Local) {
      return (Local) value;
    }

    return getOrAssignValueToStackLocal();
  }

  Immediate toImmediate() {
    // Don't inline when the trap handlers (catch blocks) change between the operand and the usage.
    // Even though immediates are just locals or constants,
    // the corresponding instructions could still throw a `VirtualMachineError`.
    boolean matchingTrapHandlers = this.activeTrapHandlers.equals(methodSource.activeTrapHandlers);

    if (stackLocal == null && value instanceof Immediate && matchingTrapHandlers) {
      return (Immediate) value;
    }

    return getOrAssignValueToStackLocal();
  }

  /**
   * Determines if this operand is equal to another operand.
   *
   * @param other
   *          the other operand.
   * @return {@code true} if this operand is equal to another operand, {@code false} otherwise.
   */
  boolean equivTo(@NonNull Operand other) {
    Value stackOrValue = stackLocal == null ? value : stackLocal;
    Value stackOrValueOther = other.stackLocal == null ? other.value : other.stackLocal;

    // care for DWORD comparison, as asValue is null, which would result in a
    // NullPointerException
    return (this == other)
        || ((this == Operand.DWORD_DUMMY) == (other == Operand.DWORD_DUMMY) && stackOrValue.equivTo(stackOrValueOther));
  }

  @Override
  public String toString() {
    return "Operand{" + "insn=" + insn + ", value=" + value + ", stack=" + stackLocal + '}';
  }

  @NonNull
  public AbstractInsnNode getInsn() {
    return insn;
  }

  @Override
  public boolean equals(Object other) {
    return other instanceof Operand && equivTo((Operand) other);
  }

}