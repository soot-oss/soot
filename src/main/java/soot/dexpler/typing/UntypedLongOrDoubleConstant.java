package soot.dexpler.typing;

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

import soot.DoubleType;
import soot.LongType;
import soot.Type;
import soot.Value;
import soot.jimple.DoubleConstant;
import soot.jimple.LongConstant;

public class UntypedLongOrDoubleConstant extends UntypedConstant {

  /**
  * 
  */
  private static final long serialVersionUID = -3970057807907204253L;
  public final long value;

  private UntypedLongOrDoubleConstant(long value) {
    this.value = value;
  }

  public static UntypedLongOrDoubleConstant v(long value) {
    return new UntypedLongOrDoubleConstant(value);
  }

  public boolean equals(Object c) {
    return c instanceof UntypedLongOrDoubleConstant && ((UntypedLongOrDoubleConstant) c).value == this.value;
  }

  /** Returns a hash code for this DoubleConstant object. */
  public int hashCode() {
    return (int) (value ^ (value >>> 32));
  }

  public DoubleConstant toDoubleConstant() {
    return DoubleConstant.v(Double.longBitsToDouble(value));
  }

  public LongConstant toLongConstant() {
    return LongConstant.v(value);
  }

  @Override
  public Value defineType(Type t) {
    if (t instanceof DoubleType) {
      return this.toDoubleConstant();
    } else if (t instanceof LongType) {
      return this.toLongConstant();
    } else {
      throw new RuntimeException("error: expected Double type or Long type. Got " + t);
    }
  }

}
