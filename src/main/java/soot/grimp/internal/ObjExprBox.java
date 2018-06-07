package soot.grimp.internal;

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

import soot.Local;
import soot.Value;
import soot.jimple.CastExpr;
import soot.jimple.ClassConstant;
import soot.jimple.ConcreteRef;
import soot.jimple.InvokeExpr;
import soot.jimple.NewArrayExpr;
import soot.jimple.NewMultiArrayExpr;
import soot.jimple.NullConstant;
import soot.jimple.StringConstant;

public class ObjExprBox extends ExprBox {
  /* an ExprBox which can only contain object-looking references */
  public ObjExprBox(Value value) {
    super(value);
  }

  public boolean canContainValue(Value value) {
    return value instanceof ConcreteRef || value instanceof InvokeExpr || value instanceof NewArrayExpr
        || value instanceof NewMultiArrayExpr || value instanceof Local || value instanceof NullConstant
        || value instanceof StringConstant || value instanceof ClassConstant
        || (value instanceof CastExpr && canContainValue(((CastExpr) value).getOp()));
  }
}
