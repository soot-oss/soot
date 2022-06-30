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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import soot.ArrayType;
import soot.Type;
import soot.Unit;
import soot.UnitPrinter;
import soot.Value;
import soot.ValueBox;
import soot.baf.Baf;
import soot.jimple.ConvertToBaf;
import soot.jimple.ExprSwitch;
import soot.jimple.Jimple;
import soot.jimple.JimpleToBafContext;
import soot.jimple.NewMultiArrayExpr;
import soot.util.Switch;

@SuppressWarnings("serial")
public abstract class AbstractNewMultiArrayExpr implements NewMultiArrayExpr, ConvertToBaf {

  protected ArrayType baseType;
  protected final ValueBox[] sizeBoxes;

  protected AbstractNewMultiArrayExpr(ArrayType type, ValueBox[] sizeBoxes) {
    this.baseType = type;
    this.sizeBoxes = sizeBoxes;
  }

  @Override
  public abstract Object clone();

  @Override
  public boolean equivTo(Object o) {
    if (o instanceof AbstractNewMultiArrayExpr) {
      AbstractNewMultiArrayExpr ae = (AbstractNewMultiArrayExpr) o;
      return baseType.equals(ae.baseType) && (this.sizeBoxes.length == ae.sizeBoxes.length);
    }
    return false;
  }

  /** Returns a hash code for this object, consistent with structural equality. */
  @Override
  public int equivHashCode() {
    return baseType.hashCode();
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder(Jimple.NEWMULTIARRAY + " (");

    buf.append(baseType.baseType.toString()).append(')');
    for (ValueBox element : sizeBoxes) {
      buf.append('[').append(element.getValue().toString()).append(']');
    }
    for (int i = 0, e = baseType.numDimensions - sizeBoxes.length; i < e; i++) {
      buf.append("[]");
    }

    return buf.toString();
  }

  @Override
  public void toString(UnitPrinter up) {
    up.literal(Jimple.NEWMULTIARRAY + " (");
    up.type(baseType.baseType);
    up.literal(")");
    for (ValueBox element : sizeBoxes) {
      up.literal("[");
      element.toString(up);
      up.literal("]");
    }
    for (int i = 0, e = baseType.numDimensions - sizeBoxes.length; i < e; i++) {
      up.literal("[]");
    }
  }

  @Override
  public ArrayType getBaseType() {
    return baseType;
  }

  @Override
  public void setBaseType(ArrayType baseType) {
    this.baseType = baseType;
  }

  @Override
  public ValueBox getSizeBox(int index) {
    return sizeBoxes[index];
  }

  @Override
  public int getSizeCount() {
    return sizeBoxes.length;
  }

  @Override
  public Value getSize(int index) {
    return sizeBoxes[index].getValue();
  }

  @Override
  public List<Value> getSizes() {
    final ValueBox[] boxes = sizeBoxes;
    List<Value> toReturn = new ArrayList<Value>(boxes.length);
    for (ValueBox element : boxes) {
      toReturn.add(element.getValue());
    }
    return toReturn;
  }

  @Override
  public void setSize(int index, Value size) {
    sizeBoxes[index].setValue(size);
  }

  @Override
  public final List<ValueBox> getUseBoxes() {
    List<ValueBox> list = new ArrayList<ValueBox>();
    Collections.addAll(list, sizeBoxes);
    for (ValueBox element : sizeBoxes) {
      list.addAll(element.getValue().getUseBoxes());
    }
    return list;
  }

  @Override
  public Type getType() {
    return baseType;
  }

  @Override
  public void apply(Switch sw) {
    ((ExprSwitch) sw).caseNewMultiArrayExpr(this);
  }

  @Override
  public void convertToBaf(JimpleToBafContext context, List<Unit> out) {
    final List<Value> sizes = getSizes();

    for (Value s : sizes) {
      ((ConvertToBaf) s).convertToBaf(context, out);
    }

    Unit u = Baf.v().newNewMultiArrayInst(getBaseType(), sizes.size());
    out.add(u);
    u.addAllTagsOf(context.getCurrentUnit());
  }
}
