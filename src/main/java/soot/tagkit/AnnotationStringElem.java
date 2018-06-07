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
 * Represents the base class of annotation elements each annotation can have several elements for Java 1.5.
 */

public class AnnotationStringElem extends AnnotationElem {

  String value;

  public AnnotationStringElem(String s, char kind, String name) {
    super(kind, name);
    this.value = s;
  }

  public String toString() {
    return super.toString() + " value: " + value;
  }

  public String getValue() {
    return value;
  }

  @Override
  public void apply(Switch sw) {
    ((IAnnotationElemTypeSwitch) sw).caseAnnotationStringElem(this);
  }
}
