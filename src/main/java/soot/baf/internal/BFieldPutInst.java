package soot.baf.internal;

import soot.SootField;
import soot.SootFieldRef;
import soot.UnitPrinter;
import soot.baf.FieldPutInst;
import soot.baf.InstSwitch;
import soot.util.Switch;
import soot.util.backend.ASMBackendUtils;

public class BFieldPutInst extends AbstractInst implements FieldPutInst {

  SootFieldRef fieldRef;

  public BFieldPutInst(SootFieldRef fieldRef) {
    if (fieldRef.isStatic()) {
      throw new RuntimeException("wrong static-ness");
    }
    this.fieldRef = fieldRef;
  }

  @Override
  public Object clone() {
    return new BFieldPutInst(fieldRef);
  }

  @Override
  public int getInCount() {
    return 2;
  }

  @Override
  public int getOutCount() {
    return 0;
  }

  @Override
  public int getInMachineCount() {
    return ASMBackendUtils.sizeOfType(fieldRef.type()) + 1;
  }

  @Override
  public int getOutMachineCount() {
    return 0;
  }

  @Override
  final public String getName() {
    return "fieldput";
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
    ((InstSwitch) sw).caseFieldPutInst(this);
  }

  @Override
  public boolean containsFieldRef() {
    return true;
  }
}
