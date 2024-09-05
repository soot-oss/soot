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

import soot.Type;
import soot.Value;
import soot.ValueBox;
import soot.jimple.ExprSwitch;
import soot.jimple.Jimple;
import soot.util.Switch;

/**
 * This cast expression checks for an overflow and throws an exception 
 * in case the cast cannot be performed without an overflow.
 * In .NET CIL code, this corresponds to a Conv_Ovf_* instruction. 
 * {@link https://learn.microsoft.com/de-de/dotnet/api/system.reflection.emit.opcodes.conv_ovf_i8?view=net-8.0}
 * 
 * Note that since this class inherits from {@link AbstractCastExpr}, most analysis (e.g. data flow) can treat this like a 
 * normal cast expression without further changes.
 */
public class JCheckedCastExpr extends AbstractCastExpr implements ICheckedExpr {

  public JCheckedCastExpr(Value op, Type type) {
    super(Jimple.v().newImmediateBox(op), type);
  }

  public JCheckedCastExpr(ValueBox op, Type type) {
    super(op, type);
  }

  @Override
  public Object clone() {
    return new JCheckedCastExpr(Jimple.cloneIfNecessary(getOp()), type);
  }

  @Override
  public void apply(Switch sw) {
    ((ExprSwitch) sw).caseCheckedCastExpr(this);
  }

}
