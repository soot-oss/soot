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

/**
 * Represents the visibility of an annotation attribute attatched to a class, field, method or method param (only one of
 * these each) has one or more annotations for Java 1.5.
 */

public class VisibilityAnnotationTag implements Tag {

  private int visibility;
  private ArrayList<AnnotationTag> annotations = null;

  public VisibilityAnnotationTag(int vis) {
    this.visibility = vis;
  }

  // should also print here number of annotations and perhaps the annotations themselves
  public String toString() {
    StringBuffer sb = new StringBuffer("Visibility Annotation: level: ");
    switch (visibility) {
      case AnnotationConstants.RUNTIME_INVISIBLE:
        sb.append("CLASS (runtime-invisible)");
        break;
      case AnnotationConstants.RUNTIME_VISIBLE:
        sb.append("RUNTIME (runtime-visible)");
        break;
      case AnnotationConstants.SOURCE_VISIBLE:
        sb.append("SOURCE");
        break;
    }
    sb.append("\n Annotations:");
    if (annotations != null) {
      for (AnnotationTag tag : annotations) {
        sb.append("\n");
        sb.append(tag.toString());
      }
    }
    sb.append("\n");
    return sb.toString();
  }

  /** Returns the tag name. */
  public String getName() {
    return "VisibilityAnnotationTag";
  }

  public String getInfo() {
    return "VisibilityAnnotation";
  }

  public int getVisibility() {
    return visibility;
  }

  /** Returns the tag raw data. */
  public byte[] getValue() {
    throw new RuntimeException("VisibilityAnnotationTag has no value for bytecode");
  }

  public void addAnnotation(AnnotationTag a) {
    if (annotations == null) {
      annotations = new ArrayList<AnnotationTag>();
    }
    annotations.add(a);
  }

  public ArrayList<AnnotationTag> getAnnotations() {
    return annotations;
  }

  public boolean hasAnnotations() {
    return annotations == null ? false : true;
  }
}
