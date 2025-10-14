package soot.baf.internal;

import soot.Type;
import soot.baf.InstSwitch;
import soot.baf.XorInst;
import soot.util.Switch;
import soot.util.backend.ASMBackendUtils;

public class BXorInst extends AbstractOpTypeInst implements XorInst {

  public BXorInst(Type opType) {
    super(opType);
  }

  @Override
  public Object clone() {
    return new BXorInst(getOpType());
  }

  @Override
  public int getInCount() {
    return 2;
  }

  @Override
  public int getOutCount() {
    return 1;
  }

  @Override
  public int getInMachineCount() {
    return 2 * ASMBackendUtils.sizeOfType(getOpType());
  }

  @Override
  public int getOutMachineCount() {
    return 1 * ASMBackendUtils.sizeOfType(getOpType());
  }

  @Override
  public final String getName() {
    return "xor";
  }

  @Override
  public void apply(Switch sw) {
    ((InstSwitch) sw).caseXorInst(this);
  }
}
