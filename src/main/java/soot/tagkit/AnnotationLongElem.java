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

import soot.util.Switch;

/**
 * Represents the long annotation element each annotation can have several elements for Java 1.5.
 */
public class AnnotationLongElem extends AnnotationElem {

  private final long value;

  public AnnotationLongElem(long v, String name) {
    this(v, 'J', name);
  }

  public AnnotationLongElem(long v, char kind, String name) {
    super(kind, name);
    this.value = v;
  }

  @Override
  public String toString() {
    return super.toString() + " value: " + value;
  }

  public long getValue() {
    return value;
  }

  @Override
  public void apply(Switch sw) {
    ((IAnnotationElemTypeSwitch) sw).caseAnnotationLongElem(this);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (int) (value ^ (value >>> 32));
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
    AnnotationLongElem other = (AnnotationLongElem) obj;
    return this.value == other.value;
  }
}
