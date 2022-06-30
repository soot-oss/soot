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
 * Represents the Array annotation element each annotation can have several elements for Java 1.5.
 */
public class AnnotationArrayElem extends AnnotationElem {

  private final ArrayList<AnnotationElem> values;

  public AnnotationArrayElem(ArrayList<AnnotationElem> types, String elemName) {
    this(types, '[', elemName);
  }

  public AnnotationArrayElem(ArrayList<AnnotationElem> t, char kind, String name) {
    super(kind, name);
    this.values = t;
  }

  @Override
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

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((values == null) ? 0 : values.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj) || (this.getClass() != obj.getClass())) {
      return false;
    }
    AnnotationArrayElem other = (AnnotationArrayElem) obj;
    if (this.values == null) {
      if (other.values != null) {
        return false;
      }
    } else if (!this.values.equals(other.values)) {
      return false;
    }
    return true;
  }
}
