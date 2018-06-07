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

import soot.util.Switch;

@SuppressWarnings("serial")
public class AnySubType extends RefLikeType {
  private AnySubType(RefType base) {
    this.base = base;
  }

  public static AnySubType v(RefType base) {
    if (base.getAnySubType() == null) {
      synchronized (base) {
        if (base.getAnySubType() == null) {
          base.setAnySubType(new AnySubType(base));
        }
      }
    }
    return base.getAnySubType();
  }

  public String toString() {
    return "Any_subtype_of_" + base;
  }

  public void apply(Switch sw) {
    ((TypeSwitch) sw).caseAnySubType(this);
  }

  public Type getArrayElementType() {
    throw new RuntimeException("Attempt to get array base type of a non-array");
  }

  public RefType getBase() {
    return base;
  }

  public void setBase(RefType base) {
    this.base = base;
  }

  private RefType base;
}
