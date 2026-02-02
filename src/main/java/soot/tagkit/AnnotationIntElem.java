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
 * Represents the int annotation element each annotation can have several elements for Java 1.5.
 */
public class AnnotationIntElem extends AnnotationElem {

  private final int value;

  /**
   * Annotation int element
   * 
   * @param v
   *          value of type int
   * @param kind
   *          I: int; B: byte; Z: boolean; C: char; S: short;
   * @param name
   */
  public AnnotationIntElem(int v, char kind, String name) {
    super(kind, name);
    this.value = v;
  }

  public AnnotationIntElem(int v, String name) {
    this(v, 'I', name);
  }

  public AnnotationIntElem(Byte v, String name) {
    this(v, 'B', name);
  }

  public AnnotationIntElem(Character v, String name) {
    this(v, 'C', name);
  }

  public AnnotationIntElem(Short v, String name) {
    this(v, 'S', name);
  }

  @Override
  public String toString() {
    return super.toString() + " value: " + value;
  }

  public int getValue() {
    return value;
  }

  @Override
  public void apply(Switch sw) {
    ((IAnnotationElemTypeSwitch) sw).caseAnnotationIntElem(this);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + value;
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
    AnnotationIntElem other = (AnnotationIntElem) obj;
    return this.value == other.value;
  }
}
