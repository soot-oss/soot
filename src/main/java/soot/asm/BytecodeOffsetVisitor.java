package soot.asm;

import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class BytecodeOffsetVisitor extends MethodVisitor {
  private int currentOffset = 0;

  public BytecodeOffsetVisitor(MethodVisitor mv) {
    super(Opcodes.ASM9, mv);
  }

  @Override
  public void visitCode() {
    currentOffset = 0;
    super.visitCode();
  }

  @Override
  public void visitInsn(int opcode) {
    currentOffset++;
    super.visitInsn(opcode);
  }

  @Override
  public void visitIntInsn(int opcode, int operand) {
    switch (opcode) {
      case Opcodes.BIPUSH:
        currentOffset += 2; // 1 byte opcode + 1 byte operand
        break;
      case Opcodes.SIPUSH:
        currentOffset += 3; // 1 byte opcode + 2 byte operand
        break;
      case Opcodes.NEWARRAY:
        currentOffset += 2; // 1 byte opcode + 1 byte type
        break;
      default:
        throw new IllegalArgumentException(String.format("Unsupported instruction: %d", opcode));
    }
    super.visitIntInsn(opcode, operand);
  }

  @Override
  public void visitVarInsn(int opcode, int varIndex) {
    switch (opcode) {
      case Opcodes.ILOAD:
      case Opcodes.ISTORE:
      case Opcodes.FLOAD:
      case Opcodes.FSTORE:
      case Opcodes.ALOAD:
      case Opcodes.ASTORE:
      case Opcodes.RET:
        currentOffset += 2;
        break;
      case Opcodes.LLOAD:
      case Opcodes.LSTORE:
      case Opcodes.DLOAD:
      case Opcodes.DSTORE:
        currentOffset += 3;
        break;
      default:
        throw new IllegalArgumentException(String.format("Unsupported instruction: %d", opcode));
    }
    super.visitVarInsn(opcode, varIndex);
  }

  @Override
  public void visitTypeInsn(int opcode, String type) {
    currentOffset += 2;
    super.visitTypeInsn(opcode, type);
  }

  @Override
  public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
    currentOffset += 3;
    super.visitFieldInsn(opcode, owner, name, descriptor);
  }

  @Override
  public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
    if (opcode == Opcodes.INVOKEINTERFACE) {
      currentOffset += 3;
    } else {
      currentOffset += 2;
    }
    if (mv != null) {
      mv.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
    }
  }

  @Override
  public void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle,
      Object... bootstrapMethodArguments) {
    currentOffset += 3;
    super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments);
  }

  @Override
  public void visitJumpInsn(int opcode, Label label) {
    currentOffset += 5;
    super.visitJumpInsn(opcode, label);
  }

  @Override
  public void visitLdcInsn(Object value) {
    currentOffset += (value instanceof Long || value instanceof Double) ? 3 : 2;
    super.visitLdcInsn(value);
  }

  @Override
  public void visitIincInsn(int varIndex, int increment) {
    currentOffset += (increment >= -128 && increment <= 127) ? 2 : 3;
    super.visitIincInsn(varIndex, increment);
  }

  @Override
  public void visitTableSwitchInsn(int min, int max, Label dflt, Label... labels) {
    int padding = (4 - (currentOffset % 4)) % 4;
    currentOffset += padding;
    currentOffset += 1 + 3 + 4 + 4 + 4 + (labels.length * 4);
    super.visitTableSwitchInsn(min, max, dflt, labels);
  }

  @Override
  public void visitLookupSwitchInsn(Label dflt, int[] keys, Label[] labels) {
    int padding = (4 - (currentOffset % 4)) % 4;
    currentOffset += padding;
    int numKeys = keys.length;
    currentOffset += 1 + 3 + 4 + 4 + (numKeys * 4) + (labels.length * 4);
    super.visitLookupSwitchInsn(dflt, keys, labels);
  }

  @Override
  public void visitMultiANewArrayInsn(String descriptor, int dims) {
    currentOffset += 2;
    super.visitMultiANewArrayInsn(descriptor, dims);
  }

  public int getCurrentOffset() {
    return currentOffset;
  }

}
