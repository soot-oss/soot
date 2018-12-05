package soot;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 1999 Raja Vallee-Rai
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

import soot.tagkit.AbstractHost;

/**
 * Reference implementation for ValueBox; just add a canContainValue method.
 */
@SuppressWarnings("serial")
public abstract class AbstractValueBox extends AbstractHost implements ValueBox {
  Value value;

  public void setValue(Value value) {
    if (value == null) {
      throw new IllegalArgumentException("value may not be null");
    }
    if (canContainValue(value)) {
      this.value = value;
    } else {
      throw new RuntimeException("Box " + this + " cannot contain value: " + value + "(" + value.getClass() + ")");
    }
  }

  public Value getValue() {
    return value;
  }

  public void toString(UnitPrinter up) {
    up.startValueBox(this);
    value.toString(up);
    up.endValueBox(this);
  }

  public String toString() {
    return getClass().getSimpleName() + "(" + value + ")";
  }
}
