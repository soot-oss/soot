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

import java.util.List;

import soot.AbstractUnit;
import soot.Unit;
import soot.ValueBox;
import soot.baf.Baf;
import soot.jimple.ArrayRef;
import soot.jimple.ConvertToBaf;
import soot.jimple.FieldRef;
import soot.jimple.InvokeExpr;
import soot.jimple.JimpleToBafContext;
import soot.jimple.Stmt;

@SuppressWarnings("serial")
public abstract class AbstractStmt extends AbstractUnit implements Stmt, ConvertToBaf {
  public void convertToBaf(JimpleToBafContext context, List<Unit> out) {
    Unit u = Baf.v().newNopInst();
    out.add(u);
    u.addAllTagsOf(this);
  }

  public boolean containsInvokeExpr() {
    return false;
  }

  public InvokeExpr getInvokeExpr() {
    throw new RuntimeException("getInvokeExpr() called with no invokeExpr present!");
  }

  public ValueBox getInvokeExprBox() {
    throw new RuntimeException("getInvokeExprBox() called with no invokeExpr present!");
  }

  public boolean containsArrayRef() {
    return false;
  }

  public ArrayRef getArrayRef() {
    throw new RuntimeException("getArrayRef() called with no ArrayRef present!");
  }

  public ValueBox getArrayRefBox() {
    throw new RuntimeException("getArrayRefBox() called with no ArrayRef present!");
  }

  public boolean containsFieldRef() {
    return false;
  }

  public FieldRef getFieldRef() {
    throw new RuntimeException("getFieldRef() called with no FieldRef present!");
  }

  public ValueBox getFieldRefBox() {
    throw new RuntimeException("getFieldRefBox() called with no FieldRef present!");
  }

}
