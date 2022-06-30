package soot.dava.internal.javaRep;

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

import java.util.ArrayList;
import java.util.List;

import soot.Type;
import soot.UnitPrinter;
import soot.Value;
import soot.ValueBox;
import soot.util.Switch;

/*
 * TODO: Starting with a 1D array in mind will try to refactor for multi D arrays
 * later
 */
public class DArrayInitExpr implements Value {
  // an array of elements for the initialization
  ValueBox[] elements;

  // store the type of the array
  Type type;

  public DArrayInitExpr(ValueBox[] elements, Type type) {
    this.elements = elements;
    this.type = type;
  }

  /*
   * go through the elements array return useBoxes of each value plus the valuebox itself
   */
  public List<ValueBox> getUseBoxes() {
    List<ValueBox> list = new ArrayList<ValueBox>();

    for (ValueBox element : elements) {
      list.addAll(element.getValue().getUseBoxes());
      list.add(element);
    }
    return list;
  }

  /*
   * TODO: Does not work
   */
  public Object clone() {
    return this;
  }

  public Type getType() {
    return type;
  }

  public void toString(UnitPrinter up) {
    up.literal("{");
    for (int i = 0; i < elements.length; i++) {
      elements[i].toString(up);
      if (i + 1 < elements.length) {
        up.literal(" , ");
      }
    }
    up.literal("}");
  }

  public String toString() {
    StringBuffer b = new StringBuffer();
    b.append("{");
    for (int i = 0; i < elements.length; i++) {
      b.append(elements[i].toString());
      if (i + 1 < elements.length) {
        b.append(" , ");
      }
    }
    b.append("}");
    return b.toString();
  }

  public void apply(Switch sw) {
    // TODO Auto-generated method stub

  }

  public boolean equivTo(Object o) {
    // TODO Auto-generated method stub
    return false;
  }

  public int equivHashCode() {
    int toReturn = 0;
    for (ValueBox element : elements) {
      toReturn += element.getValue().equivHashCode();
    }

    return toReturn;
  }

  public ValueBox[] getElements() {
    return elements;
  }

}
