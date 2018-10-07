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

import soot.BooleanType;
import soot.ByteType;
import soot.CharType;
import soot.DoubleType;
import soot.FloatType;
import soot.IntType;
import soot.LongType;
import soot.ShortType;
import soot.Type;
import soot.UnknownType;
import soot.Value;

@SuppressWarnings("serial")
public abstract class AbstractFloatBinopExpr extends AbstractBinopExpr {
  public Type getType() {
    Value op1 = op1Box.getValue();
    Value op2 = op2Box.getValue();
    Type op1t = op1.getType();
    Type op2t = op2.getType();
    if ((op1t.equals(IntType.v()) || op1t.equals(ByteType.v()) || op1t.equals(ShortType.v()) || op1t.equals(CharType.v())
        || op1t.equals(BooleanType.v()))
        && (op2t.equals(IntType.v()) || op2t.equals(ByteType.v()) || op2t.equals(ShortType.v()) || op2t.equals(CharType.v())
            || op2t.equals(BooleanType.v()))) {
      return IntType.v();
    } else if (op1t.equals(LongType.v()) || op2t.equals(LongType.v())) {
      return LongType.v();
    } else if (op1t.equals(DoubleType.v()) || op2t.equals(DoubleType.v())) {
      return DoubleType.v();
    } else if (op1t.equals(FloatType.v()) || op2t.equals(FloatType.v())) {
      return FloatType.v();
    } else {
      return UnknownType.v();
    }
  }
}
