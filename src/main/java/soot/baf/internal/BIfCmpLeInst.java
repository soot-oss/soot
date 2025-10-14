package soot.baf.internal;

import soot.Type;
import soot.Unit;
import soot.baf.Baf;
import soot.baf.IfCmpLeInst;
import soot.baf.InstSwitch;
import soot.util.Switch;
import soot.util.backend.ASMBackendUtils;

public class BIfCmpLeInst extends AbstractOpTypeBranchInst implements IfCmpLeInst {

  public BIfCmpLeInst(Type opType, Unit target) {
    super(opType, Baf.v().newInstBox(target));
  }

  @Override
  public Object clone() {
    return new BIfCmpLeInst(getOpType(), getTarget());
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
    return 0;
  }

  @Override
  public int getOutMachineCount() {
    return 0;
  }

  @Override
  public String getName() {
    return "ifcmple";
  }

  @Override
  public void apply(Switch sw) {
    ((InstSwitch) sw).caseIfCmpLeInst(this);
  }
}
