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

/**
 * Represents the annotation default attribute attatched method - could have at most one annotation default each for Java
 * 1.5.
 */

public class AnnotationDefaultTag implements Tag {
  private AnnotationElem defaultVal;

  public AnnotationDefaultTag(AnnotationElem def) {
    this.defaultVal = def;
  }

  // should also print here number of annotations and perhaps the annotations themselves
  public String toString() {
    return "Annotation Default: " + defaultVal;
  }

  /** Returns the tag name. */
  public String getName() {
    return "AnnotationDefaultTag";
  }

  public String getInfo() {
    return "AnnotationDefault";
  }

  public AnnotationElem getDefaultVal() {
    return defaultVal;
  }

  /** Returns the tag raw data. */
  public byte[] getValue() {
    throw new RuntimeException("AnnotationDefaultTag has no value for bytecode");
  }

}
