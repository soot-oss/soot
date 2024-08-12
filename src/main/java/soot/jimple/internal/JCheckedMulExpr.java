package soot.jimple.internal;

import soot.Value;
import soot.jimple.ExprSwitch;
import soot.jimple.Jimple;
import soot.util.Switch;

/**
 * This multiply expression checks for an overflow and throws an exception 
 * in case the multiplication cannot be performed without an overflow.
 * In .NET CIL code, this corresponds to a Mul_Ovf instruction. 
 * {@link https://learn.microsoft.com/de-de/dotnet/api/system.reflection.emit.opcodes.mul_ovf}
 * 
 * Note that since this class inherits from {@link JMulExpr}, most analysis (e.g. data flow) can treat this like a 
 * normal multiply expression without further changes.
 */
public class JCheckedMulExpr extends JMulExpr implements ICheckedExpr {

  private static final long serialVersionUID = 1L;

  public JCheckedMulExpr(Value op1, Value op2) {
    super(op1, op2);
  }

  @Override
  public void apply(Switch sw) {
    ((ExprSwitch) sw).caseCheckedMulExpr(this);
  }

  @Override
  public Object clone() {
    return new JCheckedMulExpr(Jimple.cloneIfNecessary(getOp1()), Jimple.cloneIfNecessary(getOp2()));
  }
}
