package soot.baf.internal;

import soot.Type;
import soot.baf.InstSwitch;
import soot.baf.ReturnInst;
import soot.util.Switch;
import soot.util.backend.ASMBackendUtils;

public class BReturnInst extends AbstractOpTypeInst implements ReturnInst {

  public BReturnInst(Type opType) {
    super(opType);
  }

  @Override
  public Object clone() {
    return new BReturnInst(getOpType());
  }

  @Override
  public int getInCount() {
    return 1;
  }

  @Override
  public int getInMachineCount() {
    return ASMBackendUtils.sizeOfType((getOpType()));
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
    return "return";
  }

  @Override
  public void apply(Switch sw) {
    ((InstSwitch) sw).caseReturnInst(this);
  }

  @Override
  public boolean fallsThrough() {
    return false;
  }
}
