package soot.jimple.internal;

import soot.Type;
import soot.ValueBox;

@SuppressWarnings("serial")
public abstract class AbstractFloatBinopExpr extends AbstractBinopExpr {

  protected AbstractFloatBinopExpr(ValueBox op1Box, ValueBox op2Box) {
    super(op1Box, op2Box);
  }

  @Override
  public Type getType() {
    return getType(AbstractBinopExpr.BinopExprEnum.ABSTRACT_FLOAT_BINOP_EXPR);
  }
}