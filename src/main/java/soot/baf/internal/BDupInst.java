package soot.baf.internal;

import soot.Type;
import soot.baf.DupInst;
import soot.util.Switch;
import soot.util.backend.ASMBackendUtils;

public abstract class BDupInst extends AbstractInst implements DupInst {

  @Override
  public int getInCount() {
    return getUnderTypes().size() + getOpTypes().size();
  }

  @Override
  public int getInMachineCount() {
    int count = 0;
    for (Type t : getUnderTypes()) {
      count += ASMBackendUtils.sizeOfType(t);
    }
    for (Type t : getOpTypes()) {
      count += ASMBackendUtils.sizeOfType(t);
    }
    return count;
  }

  @Override
  public int getOutCount() {
    return getUnderTypes().size() + 2 * getOpTypes().size();
  }

  @Override
  public int getOutMachineCount() {
    int count = 0;
    for (Type t : getUnderTypes()) {
      count += ASMBackendUtils.sizeOfType(t);
    }
    for (Type t : getOpTypes()) {
      count += 2 * ASMBackendUtils.sizeOfType(t);
    }
    return count;
  }

  @Override
  public void apply(Switch sw) {
    throw new RuntimeException();
  }
}
