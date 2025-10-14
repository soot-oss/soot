package soot.baf.internal;

import soot.Type;
import soot.UnitPrinter;
import soot.baf.Baf;
import soot.util.backend.ASMBackendUtils;

public abstract class AbstractOpTypeInst extends AbstractInst {

  protected Type opType;

  protected AbstractOpTypeInst(Type opType) {
    setOpType(opType);
  }

  public Type getOpType() {
    return opType;
  }

  public void setOpType(Type t) {
    this.opType = Baf.getDescriptorTypeOf(t);
  }

  /* override AbstractInst's toString with our own, including types */
  @Override
  public String toString() {
    return getName() + "." + Baf.bafDescriptorOf(opType) + getParameters();
  }

  @Override
  public void toString(UnitPrinter up) {
    up.literal(getName());
    up.literal(".");
    up.literal(Baf.bafDescriptorOf(opType));
    getParameters(up);
  }

  @Override
  public int getOutMachineCount() {
    return ASMBackendUtils.sizeOfType(getOpType());
  }
}
