package soot.dava.internal.javaRep;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Jerome Miecznikowski
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

import soot.ArrayType;
import soot.Type;
import soot.UnitPrinter;
import soot.Value;
import soot.grimp.Grimp;
import soot.grimp.Precedence;
import soot.jimple.internal.AbstractNewArrayExpr;

public class DNewArrayExpr extends AbstractNewArrayExpr implements Precedence {
  public DNewArrayExpr(Type type, Value size) {
    super(type, Grimp.v().newExprBox(size));
  }

  public int getPrecedence() {
    return 850;
  }

  public Object clone() {
    return new DNewArrayExpr(getBaseType(), Grimp.cloneIfNecessary(getSize()));
  }

  public void toString(UnitPrinter up) {
    up.literal("new");
    up.literal(" ");
    Type type = getBaseType();
    if (type instanceof ArrayType) {
      ArrayType arrayType = (ArrayType) type;
      up.type(arrayType.baseType);
      up.literal("[");
      getSizeBox().toString(up);
      up.literal("]");
      for (int i = 0; i < arrayType.numDimensions; i++) {
        up.literal("[]");
      }
    } else {
      up.type(getBaseType());
      up.literal("[");
      getSizeBox().toString(up);
      up.literal("]");
    }
  }

  public String toString() {
    return "new " + getBaseType() + "[" + getSize() + "]";
  }

}
