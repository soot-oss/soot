package soot.baf.internal;

import soot.SootMethod;
import soot.SootMethodRef;
import soot.Type;
import soot.UnitPrinter;
import soot.VoidType;
import soot.util.backend.ASMBackendUtils;

public abstract class AbstractInvokeInst extends AbstractInst {

  SootMethodRef methodRef;

  public SootMethodRef getMethodRef() {
    return methodRef;
  }

  public SootMethod getMethod() {
    return methodRef.resolve();
  }

  public Type getType() {
    return methodRef.getReturnType();
  }

  @Override
  public String toString() {
    return getName() + getParameters();
  }

  @Override
  abstract public String getName();

  @Override
  String getParameters() {
    return " " + methodRef.getSignature();
  }

  @Override
  protected void getParameters(UnitPrinter up) {
    up.literal(" ");
    up.methodRef(methodRef);
  }

  @Override
  public int getInCount() {
    return getMethodRef().getParameterTypes().size();
  }

  @Override
  public int getOutCount() {
    return (getMethodRef().getReturnType() instanceof VoidType) ? 0 : 1;
  }

  @Override
  public int getInMachineCount() {
    int count = 0;
    for (Type t : getMethodRef().getParameterTypes()) {
      count += ASMBackendUtils.sizeOfType(t);
    }
    return count;
  }

  @Override
  public int getOutMachineCount() {
    final Type returnType = getMethodRef().getReturnType();
    return (returnType instanceof VoidType) ? 0 : ASMBackendUtils.sizeOfType(returnType);
  }

  @Override
  public boolean containsInvokeExpr() {
    return true;
  }
}
