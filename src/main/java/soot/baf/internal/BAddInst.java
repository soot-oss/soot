package soot.baf.internal;

import soot.Type;
import soot.baf.AddInst;
import soot.baf.InstSwitch;
import soot.util.Switch;
import soot.util.backend.ASMBackendUtils;

public class BAddInst extends AbstractOpTypeInst implements AddInst {

  public BAddInst(Type opType) {
    super(opType);
  }

  @Override
  public Object clone() {
    return new BAddInst(getOpType());
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
    return "add";
  }

  @Override
  public void apply(Switch sw) {
    ((InstSwitch) sw).caseAddInst(this);
  }
}
