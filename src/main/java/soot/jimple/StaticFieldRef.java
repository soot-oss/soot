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

  // NOTE: both fields here are private to force subclasses to use the get/set methods thus ensuring that the cached
  // SootField remains consistent with the SootFieldRef
  private SootFieldRef fieldRef;
  private SootField fieldCache = null;

  protected StaticFieldRef(SootFieldRef fieldRef) {
    if (!fieldRef.isStatic()) {
      throw new RuntimeException("wrong static-ness");
    }
    this.fieldRef = fieldRef;
  }

  @Override
  public Object clone() {
    return new StaticFieldRef(fieldRef);
  }

  @Override
  public String toString() {
    return fieldRef.getSignature();
  }

  @Override
  public void toString(UnitPrinter up) {
    up.fieldRef(fieldRef);
  }

  @Override
  public SootFieldRef getFieldRef() {
    return fieldRef;
  }

  @Override
  public void setFieldRef(SootFieldRef fieldRef) {
    this.fieldCache = null;// reset cache
    this.fieldRef = fieldRef;
  }

  @Override
  public SootField getField() {
    SootField fieldCache = this.fieldCache;
    if (fieldCache == null) { // Use the cached SootField if available
      fieldCache = fieldRef.resolve();
      this.fieldCache = fieldCache;
    }
    return fieldCache;
  }

  @Override
  public List<ValueBox> getUseBoxes() {
    return Collections.emptyList();
  }

  @Override
  public Type getType() {
    return fieldRef.type();
  }

  @Override
  public void apply(Switch sw) {
    ((RefSwitch) sw).caseStaticFieldRef(this);
  }

  @Override
  public boolean equivTo(Object o) {
    if (o instanceof StaticFieldRef) {
      return ((StaticFieldRef) o).getField().equals(getField());
    } else {
      return false;
    }
  }

  @Override
  public int equivHashCode() {
    return getField().equivHashCode();
  }

  @Override
  public void convertToBaf(JimpleToBafContext context, List<Unit> out) {
    Unit u = Baf.v().newStaticGetInst(fieldRef);
    u.addAllTagsOf(context.getCurrentUnit());
    out.add(u);
  }
}
