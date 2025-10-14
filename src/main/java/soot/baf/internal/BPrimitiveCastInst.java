package soot.baf.internal;

import soot.BooleanType;
import soot.ByteType;
import soot.CharType;
import soot.DoubleType;
import soot.FloatType;
import soot.IntType;
import soot.LongType;
import soot.NullType;
import soot.ShortType;
import soot.Type;
import soot.TypeSwitch;
import soot.baf.InstSwitch;
import soot.baf.PrimitiveCastInst;
import soot.util.Switch;
import soot.util.backend.ASMBackendUtils;

public class BPrimitiveCastInst extends AbstractInst implements PrimitiveCastInst {

  Type fromType;
  protected Type toType;

  public BPrimitiveCastInst(Type fromType, Type toType) {
    if (fromType instanceof NullType) {
      throw new RuntimeException("invalid fromType " + fromType);
    }
    this.fromType = fromType;
    this.toType = toType;
  }

  @Override
  public Object clone() {
    return new BPrimitiveCastInst(getFromType(), toType);
  }

  @Override
  public int getInCount() {
    return 1;
  }

  @Override
  public int getInMachineCount() {
    return ASMBackendUtils.sizeOfType(fromType);
  }

  @Override
  public int getOutCount() {
    return 1;
  }

  @Override
  public int getOutMachineCount() {
    return ASMBackendUtils.sizeOfType(toType);
  }

  // after changing the types, use getName to check validity
  @Override
  public Type getFromType() {
    return fromType;
  }

  @Override
  public void setFromType(Type t) {
    this.fromType = t;
  }

  @Override
  public Type getToType() {
    return toType;
  }

  @Override
  public void setToType(Type t) {
    this.toType = t;
  }

  @Override
  final public String getName() {
    TypeSwitch<String> sw = new TypeSwitch<String>() {

      @Override
      public void defaultCase(Type ty) {
        throw new RuntimeException("invalid fromType " + fromType);
      }

      @Override
      public void caseDoubleType(DoubleType ty) {
        if (IntType.v().equals(toType)) {
          setResult("d2i");
        } else if (LongType.v().equals(toType)) {
          setResult("d2l");
        } else if (FloatType.v().equals(toType)) {
          setResult("d2f");
        } else {
          throw new RuntimeException("invalid toType from double: " + toType);
        }
      }

      @Override
      public void caseFloatType(FloatType ty) {
        if (IntType.v().equals(toType)) {
          setResult("f2i");
        } else if (LongType.v().equals(toType)) {
          setResult("f2l");
        } else if (DoubleType.v().equals(toType)) {
          setResult("f2d");
        } else {
          throw new RuntimeException("invalid toType from float: " + toType);
        }
      }

      @Override
      public void caseIntType(IntType ty) {
        emitIntToTypeCast();
      }

      @Override
      public void caseBooleanType(BooleanType ty) {
        emitIntToTypeCast();
      }

      @Override
      public void caseByteType(ByteType ty) {
        emitIntToTypeCast();
      }

      @Override
      public void caseCharType(CharType ty) {
        emitIntToTypeCast();
      }

      @Override
      public void caseShortType(ShortType ty) {
        emitIntToTypeCast();
      }

      private void emitIntToTypeCast() {
        if (ByteType.v().equals(toType)) {
          setResult("i2b");
        } else if (CharType.v().equals(toType)) {
          setResult("i2c");
        } else if (ShortType.v().equals(toType)) {
          setResult("i2s");
        } else if (FloatType.v().equals(toType)) {
          setResult("i2f");
        } else if (LongType.v().equals(toType)) {
          setResult("i2l");
        } else if (DoubleType.v().equals(toType)) {
          setResult("i2d");
        } else if (IntType.v().equals(toType)) {
          setResult(""); // this shouldn't happen?
        } else if (BooleanType.v().equals(toType)) {
          setResult("");
        } else {
          throw new RuntimeException("invalid toType from int: " + toType);
        }
      }

      @Override
      public void caseLongType(LongType ty) {
        if (IntType.v().equals(toType)) {
          setResult("l2i");
        } else if (FloatType.v().equals(toType)) {
          setResult("l2f");
        } else if (DoubleType.v().equals(toType)) {
          setResult("l2d");
        } else {
          throw new RuntimeException("invalid toType from long: " + toType);
        }
      }
    };

    fromType.apply(sw);

    return sw.getResult();
  }

  /* override toString with our own, *not* including types */
  @Override
  public String toString() {
    return getName() + getParameters();
  }

  @Override
  public void apply(Switch sw) {
    ((InstSwitch) sw).casePrimitiveCastInst(this);
  }
}
