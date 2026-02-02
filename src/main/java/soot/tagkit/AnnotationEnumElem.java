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
 * Represents the Enum annotation element each annotation can have several elements for Java 1.5.
 */
public class AnnotationEnumElem extends AnnotationElem {

  private String typeName;
  private String constantName;

  /**
   *
   * @param typeName
   *          type of given enum (Foo.MyEnum)
   * @param constantName
   *          name of given enum (BarEnum)
   * @param elementName
   *          given element/parameter name
   */
  public AnnotationEnumElem(String typeName, String constantName, String elementName) {
    this(typeName, constantName, 'e', elementName);
  }

  public AnnotationEnumElem(String t, String c, char kind, String name) {
    super(kind, name);
    this.typeName = t;
    this.constantName = c;
  }

  @Override
  public String toString() {
    return super.toString() + " type name: " + typeName + " constant name: " + constantName;
  }

  public String getTypeName() {
    return typeName;
  }

  public void setTypeName(String newValue) {
    typeName = newValue;
  }

  public String getConstantName() {
    return constantName;
  }

  public void setConstantName(String newValue) {
    constantName = newValue;
  }

  @Override
  public void apply(Switch sw) {
    ((IAnnotationElemTypeSwitch) sw).caseAnnotationEnumElem(this);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((constantName == null) ? 0 : constantName.hashCode());
    result = prime * result + ((typeName == null) ? 0 : typeName.hashCode());
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
    AnnotationEnumElem other = (AnnotationEnumElem) obj;
    if (this.constantName == null) {
      if (other.constantName != null) {
        return false;
      }
    } else if (!this.constantName.equals(other.constantName)) {
      return false;
    }
    if (this.typeName == null) {
      if (other.typeName != null) {
        return false;
      }
    } else if (!this.typeName.equals(other.typeName)) {
      return false;
    }
    return true;
  }
}
