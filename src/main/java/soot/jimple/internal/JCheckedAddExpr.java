package soot.jimple.internal;

import soot.Value;
import soot.jimple.ExprSwitch;
import soot.jimple.Jimple;
import soot.util.Switch;

/**
 * This add expression checks for an overflow and throws an exception 
 * in case the addition cannot be performed without an overflow.
 * In .NET CIL code, this corresponds to a Add_Ovf instruction. 
 * {@link https://learn.microsoft.com/de-de/dotnet/api/system.reflection.emit.opcodes.add_ovf}
 * 
 * Note that since this class inherits from {@link JAddExpr}, most analysis (e.g. data flow) can treat this like a 
 * normal add expression without further changes.
 */
public class JCheckedAddExpr extends JAddExpr implements ICheckedExpr {

  private static final long serialVersionUID = 1L;

  public JCheckedAddExpr(Value op1, Value op2) {
    super(op1, op2);
  }

  @Override
  public void apply(Switch sw) {
    ((ExprSwitch) sw).caseCheckedAddExpr(this);
  }

  @Override
  public Object clone() {
    return new JCheckedAddExpr(Jimple.cloneIfNecessary(getOp1()), Jimple.cloneIfNecessary(getOp2()));
  }
}
