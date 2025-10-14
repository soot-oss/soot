package soot.baf.internal;

import soot.Type;
import soot.baf.InstSwitch;
import soot.baf.OrInst;
import soot.util.Switch;
import soot.util.backend.ASMBackendUtils;

public class BOrInst extends AbstractOpTypeInst implements OrInst {

  public BOrInst(Type opType) {
    super(opType);
  }

  @Override
  public Object clone() {
    return new BOrInst(getOpType());
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
    return 1 * ASMBackendUtils.sizeOfType(getOpType());
  }

  @Override
  public final String getName() {
    return "or";
  }

  @Override
  public void apply(Switch sw) {
    ((InstSwitch) sw).caseOrInst(this);
  }
}
