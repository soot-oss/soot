package soot.asm;

import java.util.Map;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LabelNode;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2026 Marc Miltenberger
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
/**
 * Stores the bytecode offset while reading in as a fake instruction.
 */
public class BytecodeOffsetNode extends AbstractInsnNode {
  public static final int BYTECODE_OFFSET_TYPE = -1;
  public int bytecodeOffset;

  /**
   * Creates a new BytecodeOffsetNode.
   *
   * @param line a line number. This number refers to the source file from which the class was
   *     compiled.
   * @param start the first instruction corresponding to this line number.
   */
  public BytecodeOffsetNode(final int byteCodeOffset) {
    super(-1);
    this.bytecodeOffset = byteCodeOffset;
  }

  @Override
  public int getType() {
    return BYTECODE_OFFSET_TYPE;
  }

  @Override
  public void accept(final MethodVisitor methodVisitor) {
    if (methodVisitor instanceof BytecodeOffsetReceiver) {
      BytecodeOffsetReceiver rec = (BytecodeOffsetReceiver) methodVisitor;
      rec.bytecodeOffsetChanged(bytecodeOffset);
    }
  }

  @Override
  public AbstractInsnNode clone(final Map<LabelNode, LabelNode> clonedLabels) {
    return new BytecodeOffsetNode(bytecodeOffset);
  }
}
