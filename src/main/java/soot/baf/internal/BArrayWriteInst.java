package soot.baf.internal;

import soot.Type;
import soot.baf.ArrayWriteInst;
import soot.baf.InstSwitch;
import soot.util.Switch;
import soot.util.backend.ASMBackendUtils;

public class BArrayWriteInst extends AbstractOpTypeInst implements ArrayWriteInst {

  public BArrayWriteInst(Type opType) {
    super(opType);
  }

  @Override
  public Object clone() {
    return new BArrayWriteInst(getOpType());
  }

  @Override
  public int getInCount() {
    return 3;
  }

  @Override
  public int getInMachineCount() {
    return 2 + ASMBackendUtils.sizeOfType(getOpType());
  }

  @Override
  public int getOutCount() {
    return 0;
  }

  @Override
  public int getOutMachineCount() {
    return 0;
  }

  @Override
  final public String getName() {
    return "arraywrite";
  }

  @Override
  public void apply(Switch sw) {
    ((InstSwitch) sw).caseArrayWriteInst(this);
  }

  @Override
  public boolean containsArrayRef() {
    return true;
  }
}
