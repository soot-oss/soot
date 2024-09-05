package soot.jimple.internal;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1999 Patrick Lam
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-2.1.html>.
 * #L%
 */

import java.util.ArrayList;
import java.util.List;

import soot.BooleanType;
import soot.ByteType;
import soot.CharType;
import soot.DecimalType;
import soot.DoubleType;
import soot.FloatType;
import soot.IntType;
import soot.LongType;
import soot.RefType;
import soot.Scene;
import soot.ShortType;
import soot.SootClass;
import soot.Type;
import soot.UByteType;
import soot.ULongType;
import soot.UShortType;
import soot.UnitPrinter;
import soot.UnknownType;
import soot.Value;
import soot.ValueBox;
import soot.dotnet.types.DotNetBasicTypes;
import soot.dotnet.types.DotNetINumber;
import soot.grimp.PrecedenceTest;
import soot.jimple.Expr;
import soot.options.Options;

@SuppressWarnings("serial")
public abstract class AbstractBinopExpr implements Expr {

  protected final ValueBox op1Box;
  protected final ValueBox op2Box;

  protected AbstractBinopExpr(ValueBox op1Box, ValueBox op2Box) {
    this.op1Box = op1Box;
    this.op2Box = op2Box;
  }

  /** Returns the unique symbol for an operator. */
  protected abstract String getSymbol();

  @Override
  public abstract Object clone();

  public Value getOp1() {
    return op1Box.getValue();
  }

  public Value getOp2() {
    return op2Box.getValue();
  }

  public ValueBox getOp1Box() {
    return op1Box;
  }

  public ValueBox getOp2Box() {
    return op2Box;
  }

  public void setOp1(Value op1) {
    op1Box.setValue(op1);
  }

  public void setOp2(Value op2) {
    op2Box.setValue(op2);
  }

  @Override
  public final List<ValueBox> getUseBoxes() {
    List<ValueBox> list = new ArrayList<ValueBox>();

    list.addAll(op1Box.getValue().getUseBoxes());
    list.add(op1Box);

    list.addAll(op2Box.getValue().getUseBoxes());
    list.add(op2Box);

    return list;
  }

  @Override
  public boolean equivTo(Object o) {
    if (o instanceof AbstractBinopExpr) {
      AbstractBinopExpr abe = (AbstractBinopExpr) o;
      return this.op1Box.getValue().equivTo(abe.op1Box.getValue()) && this.op2Box.getValue().equivTo(abe.op2Box.getValue())
          && this.getSymbol().equals(abe.getSymbol());
    }
    return false;
  }

  /** Returns a hash code for this object, consistent with structural equality. */
  @Override
  public int equivHashCode() {
    return op1Box.getValue().equivHashCode() * 101 + op2Box.getValue().equivHashCode() + 17 ^ getSymbol().hashCode();
  }

  @Override
  public String toString() {
    return op1Box.getValue().toString() + getSymbol() + op2Box.getValue().toString();
  }

  @Override
  public void toString(UnitPrinter up) {
    {
      final boolean needsBrackets = PrecedenceTest.needsBrackets(op1Box, this);
      if (needsBrackets) {
        up.literal("(");
      }
      op1Box.toString(up);
      if (needsBrackets) {
        up.literal(")");
      }
    }

    up.literal(getSymbol());

    {
      final boolean needsBrackets = PrecedenceTest.needsBracketsRight(op2Box, this);
      if (needsBrackets) {
        up.literal("(");
      }
      op2Box.toString(up);
      if (needsBrackets) {
        up.literal(")");
      }
    }
  }

  protected Type getType(BinopExprEnum exprTypes) {
    final Type t1 = this.op1Box.getValue().getType();
    final Type t2 = this.op2Box.getValue().getType();

    final IntType tyInt = IntType.v();
    final ByteType tyByte = ByteType.v();
    final ShortType tyShort = ShortType.v();
    final CharType tyChar = CharType.v();
    final BooleanType tyBool = BooleanType.v();
    final UByteType tyUByte = UByteType.v();
    final UShortType tyUShort = UShortType.v();

    boolean isDotNet = Options.v().src_prec() == Options.src_prec_dotnet;
    if (isDotNet && t1.equals(t2)) {
      return t1;
    }
    if ((tyInt.equals(t1) || tyByte.equals(t1) || tyShort.equals(t1) || tyChar.equals(t1) || tyBool.equals(t1)
        || tyUByte.equals(t1) || tyUShort.equals(t1))
        && (tyInt.equals(t2) || tyByte.equals(t2) || tyShort.equals(t2) || tyChar.equals(t2) || tyBool.equals(t2)
            || tyUByte.equals(t2) || tyUShort.equals(t2))) {
      return tyInt;
    }
    final LongType tyLong = LongType.v();
    if (tyLong.equals(t1) || tyLong.equals(t2)) {
      return tyLong;
    }
    if (exprTypes.equals(BinopExprEnum.ABSTRACT_FLOAT_BINOP_EXPR)) {
      final DecimalType tyDecimal = DecimalType.v();
      if (tyDecimal.equals(t1) || tyDecimal.equals(t2)) {
        return tyDecimal;
      }
      final DoubleType tyDouble = DoubleType.v();
      if (tyDouble.equals(t1) || tyDouble.equals(t2)) {
        return tyDouble;
      }
      final FloatType tyFloat = FloatType.v();
      if (tyFloat.equals(t1) || tyFloat.equals(t2)) {
        return tyFloat;
      }
    }

    // in dotnet enums are value types, such as myBool = 1 is allowed in CIL
    if (isDotNet) {
      if (isSuperclassSystemEnum(t1) || isSuperclassSystemEnum(t2)) {
        return tyInt;
      }
      if (t2 instanceof IntType && t1 instanceof DotNetINumber) {
        return t1;
      }
      if (t1 instanceof IntType && t2 instanceof DotNetINumber) {
        return t2;
      }
      if (t1 instanceof ULongType && t2 instanceof IIntLikeType) {
        return t1;
      }
      if (t2 instanceof ULongType && t1 instanceof IIntLikeType) {
        return t2;
      }
    }
    return UnknownType.v();

  }

  /**
   * Returns true if the superclass of the given Type is a System.Enum (.Net)
   * 
   * @param t
   * @return
   */
  public boolean isSuperclassSystemEnum(Type t) {
    if ((Options.v().src_prec() != Options.src_prec_dotnet) || !(t instanceof RefType)) {
      return false;
    }
    SootClass sootClass = ((RefType) t).getSootClass();
    if (sootClass == null) {
      return false;
    }
    SootClass superclass = sootClass.getSuperclassUnsafe();
    if (superclass == null) {
      return false;
    }
    if (Scene.v().getOrMakeFastHierarchy().canStoreType(superclass.getType(), RefType.v(DotNetBasicTypes.SYSTEM_ENUM))) {
      return true;
    }
    return false;
  }

  public enum BinopExprEnum {
    ABASTRACT_INT_LONG_BINOP_EXPR, ABSTRACT_FLOAT_BINOP_EXPR
  }
}
