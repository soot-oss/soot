package soot.asm;

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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.jspecify.annotations.Nullable;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.TryCatchBlockNode;

// FIXME: [AD] is it reasonable to get rid of it?
class BranchedInsnInfo {
  /* edge endpoint */

  final AbstractInsnNode insn;
  /* previous stacks at edge */

  private final LinkedList<Operand[]> prevStacks;
  /* current stack at edge */
  @Nullable
  private final List<List<Operand>> operandStacks = new ArrayList<>();
  private final Set<TryCatchBlockNode> activeTrapHandlers;

  BranchedInsnInfo(AbstractInsnNode insn, List<Operand> operands, Set<TryCatchBlockNode> activeTrapHandlers) {
    this.insn = insn;
    this.prevStacks = new LinkedList<>();
    this.operandStacks.add(operands);
    this.activeTrapHandlers = new HashSet<>(activeTrapHandlers);
  }

  public AbstractInsnNode getInsn() {
    return insn;
  }

  public List<List<Operand>> getOperandStacks() {
    return operandStacks;
  }

  public void addOperandStack(@Nullable List<Operand> operandStack) {
    operandStacks.add(operandStack);
  }

  public LinkedList<Operand[]> getPrevStacks() {
    return prevStacks;
  }

  public void addToPrevStack(Operand[] stacksOperands) {
    prevStacks.add(stacksOperands);
  }

  public Set<TryCatchBlockNode> getActiveTrapHandlers() {
    return this.activeTrapHandlers;
  }
}
