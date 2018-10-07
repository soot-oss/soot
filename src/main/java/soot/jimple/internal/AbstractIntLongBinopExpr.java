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
import soot.IntType;
import soot.LongType;
import soot.ShortType;
import soot.Type;
import soot.UnknownType;
import soot.Value;

@SuppressWarnings("serial")
abstract public class AbstractIntLongBinopExpr extends AbstractBinopExpr {

  public static boolean isIntLikeType(Type t) {
    return t.equals(IntType.v()) || t.equals(ByteType.v()) || t.equals(ShortType.v()) || t.equals(CharType.v())
        || t.equals(BooleanType.v());
  }

  public Type getType() {
    Value op1 = op1Box.getValue();
    Value op2 = op2Box.getValue();

    if (isIntLikeType(op1.getType()) && isIntLikeType(op2.getType())) {
      return IntType.v();
    } else if (op1.getType().equals(LongType.v()) && op2.getType().equals(LongType.v())) {
      return LongType.v();
    } else {
      return UnknownType.v();
    }
  }
}
