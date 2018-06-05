package soot.toolkits.exceptions;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2004 John Jorgensen
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

import soot.AnySubType;
import soot.NullType;
import soot.RefType;
import soot.Type;
import soot.Unit;
import soot.UnknownType;
import soot.Value;
import soot.baf.ThrowInst;
import soot.grimp.NewInvokeExpr;
import soot.jimple.ThrowStmt;

/**
 * Abstract class implementing parts of the {@link ThrowAnalysis} interface which may be common to multiple concrete
 * <code>ThrowAnalysis</code> classes. <code>AbstractThrowAnalysis</code> provides straightforward implementations of
 * {@link mightThrowExplicitly(ThrowInst)} and {@link mightThrowExplicitly(ThrowStmt)}, since concrete implementations of
 * <code>ThrowAnalysis</code> seem likely to differ mainly in their treatment of implicit exceptions.
 */
public abstract class AbstractThrowAnalysis implements ThrowAnalysis {

  abstract public ThrowableSet mightThrow(Unit u);

  public ThrowableSet mightThrowExplicitly(ThrowInst t) {
    // Deducing the type at the top of the Baf stack is beyond me, so...
    return ThrowableSet.Manager.v().ALL_THROWABLES;
  }

  public ThrowableSet mightThrowExplicitly(ThrowStmt t) {
    Value thrownExpression = t.getOp();
    Type thrownType = thrownExpression.getType();
    if (thrownType == null || thrownType instanceof UnknownType) {
      // We can't identify the type of thrownExpression, so...
      return ThrowableSet.Manager.v().ALL_THROWABLES;
    } else if (thrownType instanceof NullType) {
      ThrowableSet result = ThrowableSet.Manager.v().EMPTY;
      result = result.add(ThrowableSet.Manager.v().NULL_POINTER_EXCEPTION);
      return result;
    } else if (!(thrownType instanceof RefType)) {
      throw new IllegalStateException("UnitThrowAnalysis StmtSwitch: type of throw argument is not a RefType!");
    } else {
      ThrowableSet result = ThrowableSet.Manager.v().EMPTY;
      if (thrownExpression instanceof NewInvokeExpr) {
        // In this case, we know the exact type of the
        // argument exception.
        result = result.add((RefType) thrownType);
      } else {
        result = result.add(AnySubType.v((RefType) thrownType));
      }
      return result;
    }
  }

  abstract public ThrowableSet mightThrowImplicitly(ThrowInst t);

  abstract public ThrowableSet mightThrowImplicitly(ThrowStmt t);
}
