package soot.jimple.internal;

import soot.Value;
import soot.jimple.ExprSwitch;
import soot.jimple.Jimple;
import soot.util.Switch;

/**
 * This subtract expression checks for an overflow and throws an exception 
 * in case the subtraction cannot be performed without an overflow.
 * In .NET CIL code, this corresponds to a Sub_Ovf instruction. 
 * {@link https://learn.microsoft.com/de-de/dotnet/api/system.reflection.emit.opcodes.sub_ovf}
 * 
 * Note that since this class inherits from {@link JSubExpr}, most analysis (e.g. data flow) can treat this like a 
 * normal subtract expression without further changes.
 */
public class JCheckedSubExpr extends JSubExpr implements ICheckedExpr {

  private static final long serialVersionUID = 1L;

  public JCheckedSubExpr(Value op1, Value op2) {
    super(op1, op2);
  }

  @Override
  public void apply(Switch sw) {
    ((ExprSwitch) sw).caseCheckedSubExpr(this);
  }

  @Override
  public Object clone() {
    return new JCheckedSubExpr(Jimple.cloneIfNecessary(getOp1()), Jimple.cloneIfNecessary(getOp2()));
  }
}
