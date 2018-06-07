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

import java.util.Collections;
import java.util.List;

import soot.RefType;
import soot.Type;
import soot.UnitPrinter;
import soot.ValueBox;
import soot.jimple.CaughtExceptionRef;
import soot.jimple.RefSwitch;
import soot.util.Switch;

public class JCaughtExceptionRef implements CaughtExceptionRef {
  public JCaughtExceptionRef() {
  }

  public boolean equivTo(Object c) {
    return c instanceof CaughtExceptionRef;
  }

  /** Returns a hash code for this object, consistent with structural equality. */
  public int equivHashCode() {
    return 1729;
  }

  public Object clone() {
    return new JCaughtExceptionRef();
  }

  public String toString() {
    return "@caughtexception";
  }

  public void toString(UnitPrinter up) {
    up.identityRef(this);
  }

  public final List<ValueBox> getUseBoxes() {
    return Collections.emptyList();
  }

  public Type getType() {
    return RefType.v("java.lang.Throwable");
  }

  public void apply(Switch sw) {
    ((RefSwitch) sw).caseCaughtExceptionRef(this);
  }
}
