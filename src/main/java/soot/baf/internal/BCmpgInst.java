package soot.baf.internal;

import soot.Type;
import soot.baf.CmpgInst;
import soot.baf.InstSwitch;
import soot.util.Switch;
import soot.util.backend.ASMBackendUtils;

public class BCmpgInst extends AbstractOpTypeInst implements CmpgInst {

  public BCmpgInst(Type opType) {
    super(opType);
  }

  @Override
  public Object clone() {
    return new BCmpgInst(getOpType());
  }

  @Override
  public int getInCount() {
    return 2;
  }

  @Override
  public int getInMachineCount() {
    return 2 * ASMBackendUtils.sizeOfType(getOpType());
  }

  @Override
  public int getOutCount() {
    return 1;
  }

  @Override
  public int getOutMachineCount() {
    return 1;
  }

  @Override
  public final String getName() {
    return "cmpg";
  }

  @Override
  public void apply(Switch sw) {
    ((InstSwitch) sw).caseCmpgInst(this);
  }
}
