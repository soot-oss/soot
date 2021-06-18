package soot;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2000 Patrick Lam
 *   extended 2002 Florian Loitsch
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

import soot.util.Switch;

/**
 * Encapsulates the Value class, but uses EquivTo for equality comparisons. Also uses equivHashCode as its hash code.
 */
@SuppressWarnings("serial")
public class EquivalentValue implements Value {

  private final Value e;

  public EquivalentValue(Value v) {
    this.e = (v instanceof EquivalentValue) ? ((EquivalentValue) v).e : v;
  }

  @Override
  public boolean equals(Object o) {
    return e.equivTo((o instanceof EquivalentValue) ? ((EquivalentValue) o).e : o);
  }

  /**
   * compares the encapsulated value with <code>v</code>, using <code>equivTo</code>
   */
  public boolean equivToValue(Value v) {
    return e.equivTo(v);
  }

  /**
   * compares the encapsulated value with <code>v</code>, using <code>equals</code>
   */
  public boolean equalsToValue(Value v) {
    return e.equals(v);
  }

  /**
   * @deprecated
   * @see #getValue()
   */
  @Deprecated
  public Value getDeepestValue() {
    return getValue();
  }

  @Override
  public int hashCode() {
    return e.equivHashCode();
  }

  @Override
  public String toString() {
    return e.toString();
  }

  public Value getValue() {
    return e;
  }

  /*********************************/
  /* implement the Value-interface */
  /*********************************/
  @Override
  public List<ValueBox> getUseBoxes() {
    return e.getUseBoxes();
  }

  @Override
  public Type getType() {
    return e.getType();
  }

  @Override
  public Object clone() {
    return new EquivalentValue((Value) e.clone());
  }

  @Override
  public boolean equivTo(Object o) {
    return e.equivTo(o);
  }

  @Override
  public int equivHashCode() {
    return e.equivHashCode();
  }

  @Override
  public void apply(Switch sw) {
    e.apply(sw);
  }

  @Override
  public void toString(UnitPrinter up) {
    e.toString(up);
  }
}
