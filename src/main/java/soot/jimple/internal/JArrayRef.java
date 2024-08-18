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
import java.util.List;

import soot.ArrayType;
import soot.Local;
import soot.NullType;
import soot.Type;
import soot.Unit;
import soot.UnitPrinter;
import soot.UnknownType;
import soot.Value;
import soot.ValueBox;
import soot.baf.Baf;
import soot.jimple.ArrayRef;
import soot.jimple.ConvertToBaf;
import soot.jimple.Jimple;
import soot.jimple.JimpleToBafContext;
import soot.jimple.RefSwitch;
import soot.tagkit.Tag;
import soot.util.Switch;

public class JArrayRef implements ArrayRef, ConvertToBaf {

  protected final ValueBox baseBox;
  protected final ValueBox indexBox;

  public JArrayRef(Value base, Value index) {
    this(Jimple.v().newLocalBox(base), Jimple.v().newImmediateBox(index));
  }

  protected JArrayRef(ValueBox baseBox, ValueBox indexBox) {
    this.baseBox = baseBox;
    this.indexBox = indexBox;
  }

  @Override
  public Object clone() {
    return new JArrayRef(Jimple.cloneIfNecessary(getBase()), Jimple.cloneIfNecessary(getIndex()));
  }

  @Override
  public boolean equivTo(Object o) {
    if (o instanceof ArrayRef) {
      ArrayRef oArrayRef = (ArrayRef) o;
      return this.getBase().equivTo(oArrayRef.getBase()) && this.getIndex().equivTo(oArrayRef.getIndex());
    }
    return false;
  }

  /** Returns a hash code for this object, consistent with structural equality. */
  @Override
  public int equivHashCode() {
    return getBase().equivHashCode() * 101 + getIndex().equivHashCode() + 17;
  }

  @Override
  public String toString() {
    return baseBox.getValue().toString() + "[" + indexBox.getValue().toString() + "]";
  }

  @Override
  public void toString(UnitPrinter up) {
    baseBox.toString(up);
    up.literal("[");
    indexBox.toString(up);
    up.literal("]");
  }

  @Override
  public Value getBase() {
    return baseBox.getValue();
  }

  @Override
  public void setBase(Local base) {
    baseBox.setValue(base);
  }

  @Override
  public ValueBox getBaseBox() {
    return baseBox;
  }

  @Override
  public Value getIndex() {
    return indexBox.getValue();
  }

  @Override
  public void setIndex(Value index) {
    indexBox.setValue(index);
  }

  @Override
  public ValueBox getIndexBox() {
    return indexBox;
  }

  @Override
  public List<ValueBox> getUseBoxes() {
    List<ValueBox> useBoxes = new ArrayList<ValueBox>();

    useBoxes.addAll(baseBox.getValue().getUseBoxes());
    useBoxes.add(baseBox);

    useBoxes.addAll(indexBox.getValue().getUseBoxes());
    useBoxes.add(indexBox);

    return useBoxes;
  }

  @Override
  public Type getType() {
    Type type = baseBox.getValue().getType();

    return getElementType(type);
  }

  public static Type getElementType(Type type) {
    if (UnknownType.v().equals(type)) {
      return UnknownType.v();
    } else if (NullType.v().equals(type)) {
      return NullType.v();
    } else {
      // use makeArrayType on non-array type references when they propagate to this point.
      // kludge, most likely not correct.
      // may stop spark from complaining when it gets passed phantoms.
      // ideally I'd want to find out just how they manage to get this far.
      ArrayType arrayType = (type instanceof ArrayType) ? (ArrayType) type : (ArrayType) type.makeArrayType();
      if (arrayType.numDimensions == 1) {
        return arrayType.baseType;
      } else {
        return ArrayType.v(arrayType.baseType, arrayType.numDimensions - 1);
      }
    }
  }

  @Override
  public void apply(Switch sw) {
    ((RefSwitch) sw).caseArrayRef(this);
  }

  @Override
  public void convertToBaf(JimpleToBafContext context, List<Unit> out) {
    ((ConvertToBaf) getBase()).convertToBaf(context, out);
    ((ConvertToBaf) getIndex()).convertToBaf(context, out);

    Unit x = Baf.v().newArrayReadInst(getType());
    out.add(x);
    for (Tag next : context.getCurrentUnit().getTags()) {
      x.addTag(next);
    }
  }
}
