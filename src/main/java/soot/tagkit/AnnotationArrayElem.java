package soot.tagkit;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2005 Jennifer Lhotak
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

import soot.util.Switch;

/**
 * Represents the base class of annotation elements each annotation can have several elements for Java 1.5.
 */

public class AnnotationArrayElem extends AnnotationElem {

  ArrayList<AnnotationElem> values;

  public AnnotationArrayElem(ArrayList<AnnotationElem> t, char kind, String name) {
    super(kind, name);
    this.values = t;
  }

  public String toString() {
    return super.toString() + " values: " + values.toString();
  }

  public ArrayList<AnnotationElem> getValues() {
    return values;
  }

  public int getNumValues() {
    if (values == null) {
      return 0;
    } else {
      return values.size();
    }
  }

  public AnnotationElem getValueAt(int i) {
    return values.get(i);
  }

  @Override
  public void apply(Switch sw) {
    ((IAnnotationElemTypeSwitch) sw).caseAnnotationArrayElem(this);
  }
}
