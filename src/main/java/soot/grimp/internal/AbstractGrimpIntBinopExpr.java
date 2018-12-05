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

import soot.Value;
import soot.ValueBox;
import soot.dava.internal.javaRep.DCmpExpr;
import soot.dava.internal.javaRep.DCmpgExpr;
import soot.dava.internal.javaRep.DCmplExpr;
import soot.grimp.Grimp;
import soot.grimp.Precedence;
import soot.jimple.DivExpr;
import soot.jimple.SubExpr;
import soot.jimple.internal.AbstractIntBinopExpr;

abstract public class AbstractGrimpIntBinopExpr extends AbstractIntBinopExpr implements Precedence {
  public AbstractGrimpIntBinopExpr(Value op1, Value op2) {
    this(Grimp.v().newArgBox(op1), Grimp.v().newArgBox(op2));
  }

  protected AbstractGrimpIntBinopExpr(ValueBox op1Box, ValueBox op2Box) {
    this.op1Box = op1Box;
    this.op2Box = op2Box;
  }

  abstract public int getPrecedence();

  private String toString(Value op1, Value op2, String leftOp, String rightOp) {
    if (op1 instanceof Precedence && ((Precedence) op1).getPrecedence() < getPrecedence()) {
      leftOp = "(" + leftOp + ")";
    }

    if (op2 instanceof Precedence) {
      int opPrec = ((Precedence) op2).getPrecedence(), myPrec = getPrecedence();

      if ((opPrec < myPrec) || ((opPrec == myPrec) && ((this instanceof SubExpr) || (this instanceof DivExpr)
          || (this instanceof DCmpExpr) || (this instanceof DCmpgExpr) || (this instanceof DCmplExpr)))) {
        rightOp = "(" + rightOp + ")";
      }
    }

    return leftOp + getSymbol() + rightOp;
  }

  public String toString() {
    Value op1 = op1Box.getValue(), op2 = op2Box.getValue();
    String leftOp = op1.toString(), rightOp = op2.toString();

    return toString(op1, op2, leftOp, rightOp);
  }
}
