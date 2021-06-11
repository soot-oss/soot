package soot.baf.internal;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1999 Patrick Lam, Patrick Pominville and Raja Vallee-Rai
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

import soot.AbstractJasminClass;
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
    return AbstractJasminClass.sizeOfType(fromType);
  }

  @Override
  public int getOutCount() {
    return 1;
  }

  @Override
  public int getOutMachineCount() {
    return AbstractJasminClass.sizeOfType(toType);
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
