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
import java.util.ListIterator;

import soot.SootMethodRef;
import soot.UnitPrinter;
import soot.Value;
import soot.ValueBox;
import soot.grimp.Grimp;
import soot.grimp.Precedence;
import soot.grimp.PrecedenceTest;
import soot.jimple.internal.AbstractInterfaceInvokeExpr;

public class GInterfaceInvokeExpr extends AbstractInterfaceInvokeExpr implements Precedence {

  public GInterfaceInvokeExpr(Value base, SootMethodRef methodRef, List<? extends Value> args) {
    super(Grimp.v().newObjExprBox(base), methodRef, new ValueBox[args.size()]);

    final Grimp grmp = Grimp.v();
    for (ListIterator<? extends Value> it = args.listIterator(); it.hasNext();) {
      Value v = it.next();
      this.argBoxes[it.previousIndex()] = grmp.newExprBox(v);
    }
  }

  @Override
  public int getPrecedence() {
    return 950;
  }

  @Override
  public String toString() {
    final Value base = getBase();
    String baseString = base.toString();
    if (base instanceof Precedence && ((Precedence) base).getPrecedence() < getPrecedence()) {
      baseString = "(" + baseString + ")";
    }

    StringBuilder buf = new StringBuilder(baseString);

    buf.append('.').append(methodRef.getSignature()).append('(');
    if (argBoxes != null) {
      for (int i = 0, e = argBoxes.length; i < e; i++) {
        if (i != 0) {
          buf.append(", ");
        }
        buf.append(argBoxes[i].getValue().toString());
      }
    }
    buf.append(')');

    return buf.toString();
  }

  @Override
  public void toString(UnitPrinter up) {
    final boolean needsBrackets = PrecedenceTest.needsBrackets(baseBox, this);
    if (needsBrackets) {
      up.literal("(");
    }
    baseBox.toString(up);
    if (needsBrackets) {
      up.literal(")");
    }
    up.literal(".");
    up.methodRef(methodRef);
    up.literal("(");
    if (argBoxes != null) {
      for (int i = 0, e = argBoxes.length; i < e; i++) {
        if (i != 0) {
          up.literal(", ");
        }
        argBoxes[i].toString(up);
      }
    }
    up.literal(")");
  }

  @Override
  public Object clone() {
    final int count = getArgCount();
    List<Value> clonedArgs = new ArrayList<Value>(count);
    for (int i = 0; i < count; i++) {
      clonedArgs.add(Grimp.cloneIfNecessary(getArg(i)));
    }
    return new GInterfaceInvokeExpr(Grimp.cloneIfNecessary(getBase()), methodRef, clonedArgs);
  }
}
