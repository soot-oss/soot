package soot.baf.internal;

import soot.Type;
import soot.baf.CmplInst;
import soot.baf.InstSwitch;
import soot.util.Switch;
import soot.util.backend.ASMBackendUtils;

public class BCmplInst extends AbstractOpTypeInst implements CmplInst {

  public BCmplInst(Type opType) {
    super(opType);
  }

  @Override
  public Object clone() {
    return new BCmplInst(getOpType());
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
    return "cmpl";
  }

  @Override
  public void apply(Switch sw) {
    ((InstSwitch) sw).caseCmplInst(this);
  }
}
