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
import soot.jimple.ExprSwitch;
import soot.jimple.Jimple;
import soot.jimple.NewExpr;
import soot.util.Switch;

@SuppressWarnings("serial")
public abstract class AbstractNewExpr implements NewExpr {
  RefType type;

  public boolean equivTo(Object o) {
    if (o instanceof AbstractNewExpr) {
      AbstractNewExpr ae = (AbstractNewExpr) o;
      return type.equals(ae.type);
    }
    return false;
  }

  /** Returns a hash code for this object, consistent with structural equality. */
  public int equivHashCode() {
    return type.hashCode();
  }

  public abstract Object clone();

  public String toString() {
    return Jimple.NEW + " " + type.toString();
  }

  public void toString(UnitPrinter up) {
    up.literal(Jimple.NEW);
    up.literal(" ");
    up.type(type);
  }

  public RefType getBaseType() {
    return type;
  }

  public void setBaseType(RefType type) {
    this.type = type;
  }

  public Type getType() {
    return type;
  }

  public List<ValueBox> getUseBoxes() {
    return Collections.emptyList();
  }

  public void apply(Switch sw) {
    ((ExprSwitch) sw).caseNewExpr(this);
  }
}
