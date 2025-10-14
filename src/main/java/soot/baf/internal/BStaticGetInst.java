package soot.baf.internal;

import soot.SootField;
import soot.SootFieldRef;
import soot.UnitPrinter;
import soot.baf.InstSwitch;
import soot.baf.StaticGetInst;
import soot.util.Switch;
import soot.util.backend.ASMBackendUtils;

public class BStaticGetInst extends AbstractInst implements StaticGetInst {

  SootFieldRef fieldRef;

  public BStaticGetInst(SootFieldRef fieldRef) {
    if (!fieldRef.isStatic()) {
      throw new RuntimeException("wrong static-ness");
    }
    this.fieldRef = fieldRef;
  }

  @Override
  public Object clone() {
    return new BStaticGetInst(fieldRef);
  }

  @Override
  public int getInCount() {
    return 0;
  }

  @Override
  public int getInMachineCount() {
    return 0;
  }

  @Override
  public int getOutCount() {
    return 1;
  }

  @Override
  public int getOutMachineCount() {
    return ASMBackendUtils.sizeOfType(fieldRef.type());
  }

  @Override
  final public String getName() {
    return "staticget";
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
    ((InstSwitch) sw).caseStaticGetInst(this);
  }

  @Override
  public boolean containsFieldRef() {
    return true;
  }
}
