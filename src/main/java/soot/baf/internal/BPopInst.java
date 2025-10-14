package soot.baf.internal;

import soot.Type;
import soot.baf.InstSwitch;
import soot.baf.PopInst;
import soot.util.Switch;
import soot.util.backend.ASMBackendUtils;

public class BPopInst extends AbstractInst implements PopInst {

  protected Type mType;

  public BPopInst(Type aType) {
    this.mType = aType;
  }

  @Override
  public Object clone() {
    return new BPopInst(mType);
  }

  @Override
  public int getWordCount() {
    return getInMachineCount();
  }

  @Override
  public void setWordCount(int count) {
    throw new RuntimeException("not implemented");
  }

  @Override
  final public String getName() {
    return "pop";
  }

  @Override
  final String getParameters() {
    return "";
  }

  @Override
  public int getInCount() {
    return 1;
  }

  @Override
  public int getOutMachineCount() {
    return 0;
  }

  @Override
  public int getOutCount() {
    return 0;
  }

  @Override
  public int getInMachineCount() {
    return ASMBackendUtils.sizeOfType(mType);
  }

  @Override
  public void apply(Switch sw) {
    ((InstSwitch) sw).casePopInst(this);
  }

  public Type getType() {
    return mType;
  }
}
