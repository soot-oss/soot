package soot.shimple.internal;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2004 Navindra Umanee <navindra@cs.mcgill.ca>
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

import soot.Type;
import soot.Unit;
import soot.UnitBox;
import soot.UnitPrinter;
import soot.Value;
import soot.shimple.PiExpr;
import soot.shimple.Shimple;
import soot.toolkits.scalar.ValueUnitPair;
import soot.util.Switch;

/**
 * @author Navindra Umanee
 **/
public class SPiExpr implements PiExpr {
  protected ValueUnitPair argBox;
  protected Object targetKey;

  public SPiExpr(Value v, Unit u, Object o) {
    argBox = new SValueUnitPair(v, u);
    this.targetKey = o;
  }

  public ValueUnitPair getArgBox() {
    return argBox;
  }

  public Value getValue() {
    return argBox.getValue();
  }

  public Unit getCondStmt() {
    return argBox.getUnit();
  }

  public Object getTargetKey() {
    return targetKey;
  }

  public void setValue(Value value) {
    argBox.setValue(value);
  }

  public void setCondStmt(Unit pred) {
    argBox.setUnit(pred);
  }

  public void setTargetKey(Object targetKey) {
    this.targetKey = targetKey;
  }

  public List<UnitBox> getUnitBoxes() {
    return Collections.<UnitBox>singletonList(argBox);
  }

  public void clearUnitBoxes() {
    System.out.println("clear unit boxes");
    argBox.setUnit(null);
  }

  public boolean equivTo(Object o) {
    if (!(o instanceof SPiExpr)) {
      return false;
    }

    return getArgBox().equivTo(((SPiExpr) o).getArgBox());
  }

  public int equivHashCode() {
    return getArgBox().equivHashCode() * 17;
  }

  public void apply(Switch sw) {
    // *** FIXME:
    throw new RuntimeException("Not Yet Implemented.");
  }

  public Object clone() {
    return new SPiExpr(getValue(), getCondStmt(), getTargetKey());
  }

  public String toString() {
    String s = Shimple.PI + "(" + getValue() + ")";
    return s;
  }

  public void toString(UnitPrinter up) {
    up.literal(Shimple.PI);
    up.literal("(");
    argBox.toString(up);
    up.literal(" [");
    up.literal(targetKey.toString());
    up.literal("])");
  }

  public Type getType() {
    return getValue().getType();
  }

  public List getUseBoxes() {
    return Collections.singletonList(argBox);
  }
}
