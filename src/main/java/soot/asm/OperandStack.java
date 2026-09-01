/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2018 - 2020 Andreas Dann, Markus Schmidt and others
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

package soot.asm;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.tree.AbstractInsnNode;

import soot.Type;

/**
 * This class resembles the stack which the bytecode fills. It is used to convert to jimple with Locals. (stack-machine -&gt;
 * "register" machine model)
 */
public class OperandStack {

  private final AsmMethodSource methodSource;
  private List<Operand> stack;

  public Map<AbstractInsnNode, OperandMerging> mergings;

  public OperandStack(AsmMethodSource methodSource, int nrInsn) {
    this.methodSource = methodSource;
    mergings = new LinkedHashMap<>(nrInsn);
  }

  public OperandMerging getOrCreateMerging(AbstractInsnNode insn) {
    OperandMerging merging = this.mergings.get(insn);
    if (merging == null) {
      merging = new OperandMerging(insn, methodSource);
      this.mergings.put(insn, merging);
    }
    return merging;
  }

  public void push(Operand opr) {
    stack.add(opr);
  }

  public void pushDual(Operand opr) {
    stack.add(Operand.DWORD_DUMMY);
    stack.add(opr);
  }

  public Operand peek() {
    if (stack.isEmpty()) {
      throw new RuntimeException("Stack underrun");
    }
    return stack.get(stack.size() - 1);
  }

  public void push(Type t, Operand opr) {
    if (AsmUtil.isDWord(t)) {
      pushDual(opr);
    } else {
      push(opr);
    }
  }

  public Operand pop() {
    if (stack.isEmpty()) {
      throw new RuntimeException("Stack underrun");
    }
    return stack.remove(stack.size() - 1);
  }

  public Operand popDual() {
    Operand o = pop();
    Operand o2 = pop();
    if (o2 != Operand.DWORD_DUMMY && o2 != o) {
      throw new AssertionError("Not dummy operand, " + o2.value + " -- " + o.value);
    }
    return o;
  }

  public Operand pop(Type t) {
    return AsmUtil.isDWord(t) ? popDual() : pop();
  }

  public Operand popStackConst() {
    return pop();
  }

  public Operand popStackConstDual() {
    return popDual();
  }
  
  public boolean isEmpty() {
    return stack.isEmpty();
  }

  public List<Operand> getStack() {
    return stack;
  }

  public void setOperandStack(List<Operand> stack) {
    this.stack = stack;
  }
}
