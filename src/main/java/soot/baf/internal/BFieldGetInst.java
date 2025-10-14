package soot.baf.internal;

import soot.SootField;
import soot.SootFieldRef;
import soot.UnitPrinter;
import soot.baf.FieldGetInst;
import soot.baf.InstSwitch;
import soot.util.Switch;
import soot.util.backend.ASMBackendUtils;

public class BFieldGetInst extends AbstractInst implements FieldGetInst {

  SootFieldRef fieldRef;

  public BFieldGetInst(SootFieldRef fieldRef) {
    if (fieldRef.isStatic()) {
      throw new RuntimeException("wrong static-ness");
    }
    this.fieldRef = fieldRef;
  }

  @Override
  public Object clone() {
    return new BFieldGetInst(fieldRef);
  }

  @Override
  public int getInCount() {
    return 1;
  }

  @Override
  public int getInMachineCount() {
    return 1;
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
    return "fieldget";
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
    ((InstSwitch) sw).caseFieldGetInst(this);
  }

  @Override
  public boolean containsFieldRef() {
    return true;
  }
}
