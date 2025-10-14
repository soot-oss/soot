package soot.baf.internal;

import soot.SootField;
import soot.SootFieldRef;
import soot.UnitPrinter;
import soot.baf.InstSwitch;
import soot.baf.StaticPutInst;
import soot.util.Switch;
import soot.util.backend.ASMBackendUtils;

public class BStaticPutInst extends AbstractInst implements StaticPutInst {

  SootFieldRef fieldRef;

  public BStaticPutInst(SootFieldRef fieldRef) {
    if (!fieldRef.isStatic()) {
      throw new RuntimeException("wrong static-ness");
    }
    this.fieldRef = fieldRef;
  }

  @Override
  public Object clone() {
    return new BStaticPutInst(fieldRef);
  }

  @Override
  public int getInCount() {
    return 1;
  }

  @Override
  public int getInMachineCount() {
    return ASMBackendUtils.sizeOfType(fieldRef.type());
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
    return "staticput";
  }

  @Override
  final String getParameters() {
    return " " + fieldRef.getSignature();
  }

  @Override
  protected void getParameters(UnitPrinter up) {
    up.literal(" ");
    up.fieldRef(fieldRef);
  }

  @Override
  public SootFieldRef getFieldRef() {
    return fieldRef;
  }

  @Override
  public SootField getField() {
    return fieldRef.resolve();
  }

  @Override
  public void apply(Switch sw) {
    ((InstSwitch) sw).caseStaticPutInst(this);
  }

  @Override
  public boolean containsFieldRef() {
    return true;
  }
}
