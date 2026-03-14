package soot.asm;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2020 Raja Vallée-Rai, Andreas Dann, Markus Schmidt and others
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
import java.util.Iterator;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.objectweb.asm.tree.AbstractInsnNode;

import soot.Local;
import soot.Value;
import soot.ValueBox;
import soot.jimple.Stmt;

/**
 * This class tracks the inputs and outputs for an instruction.
 *
 * <p>
 * When an instruction is reached from different branches, the state of the operand stack might be different. In that case,
 * the different operands from the different branches need to be merged. This class does that merging.
 *
 * @author Aaloan Miftah
 */
final class OperandMerging {
  @NonNull
  private final AbstractInsnNode insn;

  /**
   * Keep track of the result of the instruction. The output might get a stack local assigned when it is used as a local or
   * immediate. When another branch produces the output and calls `mergeOutput`, it should be assigned to the same stack
   * local.
   *
   * <p>
   * Interestingly, none of the operations that need to have any inputs/outputs tracked produce more than a single output, so
   * this doesn't need to be a list.
   */
  @Nullable
  private Operand output;

  @NonNull
  final ArrayList<Operand[]> inputOperands = new ArrayList<>(1);
  @NonNull
  private final AsmMethodSource src;

  /**
   * Constructs a new operand merging information holder.
   *
   * @param src
   *          source the merging belongs to.
   */
  OperandMerging(@NonNull AbstractInsnNode insn, @NonNull AsmMethodSource src) {
    this.insn = insn;
    this.src = src;
  }

  /**
   * Merges the output operand produced by the instruction.
   *
   * <p>
   * The output from a previous branch might have been assigned to a (stack) local in which case any newly created outputs
   * for different branches should be assigned to the same stack local.
   *
   * @param outputOperand
   *          the newly produced operand that will get pushed onto the operand stack
   */
  void mergeOutput(@NonNull Operand outputOperand) {
    if (output == null) {
      output = outputOperand;
    } else if (output.stackLocal != null) {
      if (outputOperand.stackLocal == null) {
        outputOperand.changeStackLocal(output.stackLocal);
      } else {
        if (output.stackLocal != outputOperand.stackLocal) {
          throw new IllegalStateException(
              "Incompatible stacklocal mismatch. There exist multiple, different possible output Locals ("
                  + outputOperand.stackLocal + ", " + output.stackLocal + ").");
        }
      }
      outputOperand.changeStackLocal(output.stackLocal);
    }
  }

  /**
   * Merges the specified operands with the operands that were previously used with this instruction.
   *
   * <p>
   * This should be called after the operands have been popped of the stack.
   *
   * <p>
   * To convert from the stack-based instructions to register-based instructions, all possible combinations of branches need
   * to be walked, because the contents of the operand stack might be different when coming from different branches.
   *
   * <p>
   * Take the following code as an example:
   *
   * <pre>
   * System.out.println(n == 1 ? a : "two");
   * </pre>
   *
   * If the first branch is taken `a` will be on the stack when the `println` gets invoked, if the second branch is taken
   * `"two"` will be on the stack when the `println` gets invoked. This method will merge the two (or more) diverging
   * operands by creating a local variable that the value of both operands will be assigned to in their respective branches.
   * That local will be used when invoking the `println` method.
   *
   * @param oprs
   *          the new operands.
   * @throws IllegalArgumentException
   *           if the number of new operands is not equal to the number of old operands.
   */
  void mergeInputs(@NonNull Operand... oprs) {
    if (inputOperands.isEmpty()) {
      inputOperands.add(oprs);
      // There are no other operands to merge with
      return;
    }

    if (inputOperands.get(0).length != oprs.length) {
      throw new IllegalArgumentException("Invalid in operands length!");
    }

    if (oprs.length == 0) {
      // No operands to merge
      return;
    }

    for (int i = 0; i < oprs.length; i++) {
      Operand newOp = oprs[i];
      Local stack = null;

      // Search for a stack local that was already allocated for an operand in a different branch
      for (int j = 0; j != inputOperands.size(); j++) {
        stack = inputOperands.get(j)[i].stackLocal;
        if (stack != null) {
          break;
        }
      }

      // The incoming operand may already have a stack local allocated that can be re-used
      if (stack == null && newOp.stackLocal != null) {
        stack = newOp.stackLocal;
      }

      if (stack == null && inputOperands.get(0)[i].value.equivTo(newOp.value)) {
        // all branches have the same value,
        // and no stack local was allocated yet,
        // so no stack local is needed to converge the values
        continue;
      }

      // Didn't find any pre-allocated stack local from any operand.
      // So create a new stack local.
      // TODO use a special case when the statement is an assignment to a local since in that case
      // we can use the local directly instead of creating a new stack local
      if (stack == null) {
        stack = src.newStackLocal();
      }

      /* add assign statement for prevOp */
      for (int j = 0; j != inputOperands.size(); j++) {
        Operand prevOp = inputOperands.get(j)[i];
        prevOp.changeStackLocal(stack);
      }
      newOp.changeStackLocal(stack);

      Stmt oldStatement = this.src.getStmt(this.insn);

      if (oldStatement != null) {
        Value old = inputOperands.get(0)[i].value;
        Iterator<ValueBox> it = oldStatement.getUseAndDefBoxesIterator();
        while (it.hasNext()) {
          ValueBox vb = it.next();
          if (vb.getValue() == old) {
            vb.setValue(stack);
          }
        }
      }
    }

    inputOperands.add(oprs);
  }
}
