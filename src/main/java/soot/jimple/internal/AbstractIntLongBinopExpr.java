package soot.jimple.internal;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2015 Steven Arzt
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
