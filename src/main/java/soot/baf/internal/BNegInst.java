package soot.baf.internal;

import soot.Type;
import soot.baf.InstSwitch;
import soot.baf.NegInst;
import soot.util.Switch;
import soot.util.backend.ASMBackendUtils;

public class BNegInst extends AbstractOpTypeInst implements NegInst {

  public BNegInst(Type opType) {
    super(opType);
  }

  @Override
  public Object clone() {
    return new BNegInst(getOpType());
  }

  @Override
  public int getInCount() {
    return 1;
  }

  @Override
  public int getOutCount() {
    return 1;
  }

  @Override
  public int getInMachineCount() {
    return ASMBackendUtils.sizeOfType(getOpType());
  }

  @Override
  public int getOutMachineCount() {
    return ASMBackendUtils.sizeOfType(getOpType());
  }

  @Override
  public final String getName() {
    return "neg";
  }

  @Override
  public void apply(Switch sw) {
    ((InstSwitch) sw).caseNegInst(this);
  }
}
