package soot.jimple.toolkits.infoflow;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vallée-Rai and others
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

import soot.NullType;
import soot.Type;
import soot.UnitPrinter;
import soot.Value;
import soot.ValueBox;
import soot.util.Switch;

// Wraps any object as a Value

public class AbstractDataSource implements Value {
  Object sourcename;

  public AbstractDataSource(Object sourcename) {
    this.sourcename = sourcename;
  }

  @Override
  public List<ValueBox> getUseBoxes() {
    return Collections.emptyList();
  }

  /** Clones the object. Not implemented here. */
  public Object clone() {
    return new AbstractDataSource(sourcename);
  }

  /**
   * Returns true if this object is structurally equivalent to c. AbstractDataSources are equal and equivalent if their
   * sourcename is the same
   */
  public boolean equivTo(Object c) {
    if (sourcename instanceof Value) {
      return (c instanceof AbstractDataSource && ((Value) sourcename).equivTo(((AbstractDataSource) c).sourcename));
    }
    return (c instanceof AbstractDataSource && ((AbstractDataSource) c).sourcename.equals(sourcename));
  }

  public boolean equals(Object c) {
    return (c instanceof AbstractDataSource && ((AbstractDataSource) c).sourcename.equals(sourcename));
  }

  /** Returns a hash code consistent with structural equality for this object. */
  public int equivHashCode() {
    if (sourcename instanceof Value) {
      return ((Value) sourcename).equivHashCode();
    }
    return sourcename.hashCode();
  }

  public void toString(UnitPrinter up) {
  }

  public Type getType() {
    return NullType.v();
  }

  public void apply(Switch sw) {
    throw new RuntimeException("Not Implemented");
  }

  public String toString() {
    return "sourceof<" + sourcename.toString() + ">";
  }
}
