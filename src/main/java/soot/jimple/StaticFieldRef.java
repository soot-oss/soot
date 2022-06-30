package soot.jimple;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 1999 Raja Vallee-Rai
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

import java.util.Collections;
import java.util.List;

import soot.SootField;
import soot.SootFieldRef;
import soot.Type;
import soot.Unit;
import soot.UnitPrinter;
import soot.ValueBox;
import soot.baf.Baf;
import soot.util.Switch;

public class StaticFieldRef implements FieldRef, ConvertToBaf {

  protected SootFieldRef fieldRef;

  protected StaticFieldRef(SootFieldRef fieldRef) {
    if (!fieldRef.isStatic()) {
      throw new RuntimeException("wrong static-ness");
    }
    this.fieldRef = fieldRef;
  }

  public Object clone() {
    return new StaticFieldRef(fieldRef);
  }

  public String toString() {
    return fieldRef.getSignature();
  }

  public void toString(UnitPrinter up) {
    up.fieldRef(fieldRef);
  }

  public SootFieldRef getFieldRef() {
    return fieldRef;
  }

  public void setFieldRef(SootFieldRef fieldRef) {
    this.fieldRef = fieldRef;
  }

  public SootField getField() {
    return fieldRef.resolve();
  }

  @Override
  public List<ValueBox> getUseBoxes() {
    return Collections.emptyList();
  }

  public Type getType() {
    return fieldRef.type();
  }

  public void apply(Switch sw) {
    ((RefSwitch) sw).caseStaticFieldRef(this);
  }

  public boolean equivTo(Object o) {
    if (o instanceof StaticFieldRef) {
      return ((StaticFieldRef) o).getField().equals(getField());
    }

    return false;
  }

  public int equivHashCode() {
    return getField().equivHashCode();
  }

  public void convertToBaf(JimpleToBafContext context, List<Unit> out) {
    Unit u = Baf.v().newStaticGetInst(fieldRef);
    u.addAllTagsOf(context.getCurrentUnit());
    out.add(u);
  }
}
