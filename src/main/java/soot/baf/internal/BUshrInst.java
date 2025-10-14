package soot.baf.internal;

import soot.Type;
import soot.baf.InstSwitch;
import soot.baf.UshrInst;
import soot.util.Switch;
import soot.util.backend.ASMBackendUtils;

public class BUshrInst extends AbstractOpTypeInst implements UshrInst {

  public BUshrInst(Type opType) {
    super(opType);
  }

  @Override
  public Object clone() {
    return new BUshrInst(getOpType());
  }

  @Override
  public int getInCount() {
    return 2;
  }

  @Override
  public int getInMachineCount() {
    return ASMBackendUtils.sizeOfType(getOpType()) + 1;
  }

  @Override
  public int getOutCount() {
    return 1;
  }

  @Override
  public int getOutMachineCount() {
    return 1 * ASMBackendUtils.sizeOfType(getOpType());
  }

  @Override
  public final String getName() {
    return "ushr";
  }

  @Override
  public void apply(Switch sw) {
    ((InstSwitch) sw).caseUshrInst(this);
  }
}
