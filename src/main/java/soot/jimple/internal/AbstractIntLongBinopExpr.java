package soot.jimple.internal;

import soot.Type;
import soot.ValueBox;

@SuppressWarnings("serial")
public abstract class AbstractIntLongBinopExpr extends AbstractBinopExpr {

  protected AbstractIntLongBinopExpr(ValueBox op1Box, ValueBox op2Box) {
    super(op1Box, op2Box);
  }

  public static boolean isIntLikeType(Type t) {
    return t instanceof IIntLikeType;
  }

  @Override
  public Type getType() {
    return getType(AbstractBinopExpr.BinopExprEnum.ABASTRACT_INT_LONG_BINOP_EXPR);
  }
}
