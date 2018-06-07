package soot.jimple.internal;

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

import soot.SootField;
import soot.SootFieldRef;
import soot.Type;
import soot.Unit;
import soot.UnitPrinter;
import soot.Value;
import soot.ValueBox;
import soot.baf.Baf;
import soot.grimp.PrecedenceTest;
import soot.jimple.ConvertToBaf;
import soot.jimple.InstanceFieldRef;
import soot.jimple.JimpleToBafContext;
import soot.jimple.RefSwitch;
import soot.util.Switch;

@SuppressWarnings("serial")
public abstract class AbstractInstanceFieldRef implements InstanceFieldRef, ConvertToBaf {
  protected SootFieldRef fieldRef;
  final ValueBox baseBox;

  protected AbstractInstanceFieldRef(ValueBox baseBox, SootFieldRef fieldRef) {
    if (fieldRef.isStatic()) {
      throw new RuntimeException("wrong static-ness");
    }
    this.baseBox = baseBox;
    this.fieldRef = fieldRef;
  }

  public abstract Object clone();

  public String toString() {
    return baseBox.getValue().toString() + "." + fieldRef.getSignature();
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
    up.fieldRef(fieldRef);
  }

  public Value getBase() {
    return baseBox.getValue();
  }

  public ValueBox getBaseBox() {
    return baseBox;
  }

  public void setBase(Value base) {
    baseBox.setValue(base);
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
  public final List<ValueBox> getUseBoxes() {
    List<ValueBox> useBoxes = new ArrayList<ValueBox>();

    useBoxes.addAll(baseBox.getValue().getUseBoxes());
    useBoxes.add(baseBox);

    return useBoxes;
  }

  public Type getType() {
    return fieldRef.type();
  }

  public void apply(Switch sw) {
    ((RefSwitch) sw).caseInstanceFieldRef(this);
  }

  public boolean equivTo(Object o) {
    if (o instanceof AbstractInstanceFieldRef) {
      AbstractInstanceFieldRef fr = (AbstractInstanceFieldRef) o;
      return fr.getField().equals(getField()) && fr.baseBox.getValue().equivTo(baseBox.getValue());
    }
    return false;
  }

  /** Returns a hash code for this object, consistent with structural equality. */
  public int equivHashCode() {
    return getField().equivHashCode() * 101 + baseBox.getValue().equivHashCode() + 17;
  }

  public void convertToBaf(JimpleToBafContext context, List<Unit> out) {
    ((ConvertToBaf) getBase()).convertToBaf(context, out);
    Unit u;
    out.add(u = Baf.v().newFieldGetInst(fieldRef));

    u.addAllTagsOf(context.getCurrentUnit());
  }
}
