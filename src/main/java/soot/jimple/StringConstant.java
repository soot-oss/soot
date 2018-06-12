package soot.jimple;

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

import soot.RefType;
import soot.Type;
import soot.util.StringTools;
import soot.util.Switch;

public class StringConstant extends Constant {
  public final String value;

  private StringConstant(String s) {
    if (s == null) {
      throw new IllegalArgumentException("String constant cannot be null");
    }
    this.value = s;
  }

  public static StringConstant v(String value) {
    return new StringConstant(value);
  }

  // In this case, equals should be structural equality.
  public boolean equals(Object c) {
    return (c instanceof StringConstant && ((StringConstant) c).value.equals(this.value));
  }

  /** Returns a hash code for this StringConstant object. */
  public int hashCode() {
    return value.hashCode();
  }

  public String toString() {
    return StringTools.getQuotedStringOf(value);
  }

  public Type getType() {
    return RefType.v("java.lang.String");
  }

  public void apply(Switch sw) {
    ((ConstantSwitch) sw).caseStringConstant(this);
  }
}
