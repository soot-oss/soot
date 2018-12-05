package soot.grimp.internal;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1999 Patrick Lam
 * Copyright (C) 2004 Ondrej Lhotak
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

import java.util.ArrayList;
import java.util.List;

import soot.SootMethodRef;
import soot.UnitPrinter;
import soot.Value;
import soot.ValueBox;
import soot.grimp.Grimp;
import soot.grimp.Precedence;
import soot.grimp.PrecedenceTest;
import soot.jimple.internal.AbstractInterfaceInvokeExpr;

public class GInterfaceInvokeExpr extends AbstractInterfaceInvokeExpr implements Precedence {
  public GInterfaceInvokeExpr(Value base, SootMethodRef methodRef, List args) {
    super(Grimp.v().newObjExprBox(base), methodRef, new ValueBox[args.size()]);

    for (int i = 0; i < args.size(); i++) {
      this.argBoxes[i] = Grimp.v().newExprBox((Value) args.get(i));
    }
  }

  public int getPrecedence() {
    return 950;
  }

  private String toString(Value op, String opString, String rightString) {
    String leftOp = opString;

    if (getBase() instanceof Precedence && ((Precedence) getBase()).getPrecedence() < getPrecedence()) {
      leftOp = "(" + leftOp + ")";
    }
    return leftOp + rightString;
  }

  public String toString() {
    StringBuffer buffer = new StringBuffer();

    buffer.append("." + methodRef.getSignature() + "(");

    if (argBoxes != null) {
      for (int i = 0; i < argBoxes.length; i++) {
        if (i != 0) {
          buffer.append(", ");
        }

        buffer.append(argBoxes[i].getValue().toString());
      }
    }

    buffer.append(")");

    return toString(getBase(), getBase().toString(), buffer.toString());
  }

  public void toString(UnitPrinter up) {
    if (PrecedenceTest.needsBrackets(baseBox, this)) {
      up.literal("(");
    }
    baseBox.toString(up);
    if (PrecedenceTest.needsBrackets(baseBox, this)) {
      up.literal(")");
    }
    up.literal(".");
    up.methodRef(methodRef);
    up.literal("(");

    if (argBoxes != null) {
      for (int i = 0; i < argBoxes.length; i++) {
        if (i != 0) {
          up.literal(", ");
        }

        argBoxes[i].toString(up);
      }
    }

    up.literal(")");
  }

  public Object clone() {
    List argList = new ArrayList(getArgCount());

    for (int i = 0; i < getArgCount(); i++) {
      argList.add(i, Grimp.cloneIfNecessary(getArg(i)));
    }

    return new GInterfaceInvokeExpr(Grimp.cloneIfNecessary(getBase()), methodRef, argList);
  }

}
