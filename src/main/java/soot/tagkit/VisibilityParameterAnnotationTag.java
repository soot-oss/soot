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
 * Represents the visibility of an annotation attribute attached to a class, field, method or method param (only one of these
 * each) has one or more annotations for Java 1.5.
 */
public class VisibilityParameterAnnotationTag implements Tag {

  public static final String NAME = "VisibilityParameterAnnotationTag";

  private final int num_params;
  private final int kind;
  private ArrayList<VisibilityAnnotationTag> visibilityAnnotations;

  /**
   * NOTE: any parameter without annotations has a {@code null} entry in the list
   * 
   * @param num
   *          total number of parameters
   * @param kind
   *          one of {@link soot.tagkit.AnnotationConstants}
   */
  public VisibilityParameterAnnotationTag(int num, int kind) {
    this.num_params = num;
    this.kind = kind;
  }

  @Override
  public String toString() {
    // should also print here number of annotations and perhaps the annotations themselves
    StringBuilder sb = new StringBuilder("Visibility Param Annotation: num params: ");
    sb.append(num_params).append(" kind: ").append(kind);
    if (visibilityAnnotations != null) {
      for (VisibilityAnnotationTag tag : visibilityAnnotations) {
        sb.append('\n');
        if (tag != null) {
          sb.append(tag.toString());
        }
      }
    }
    sb.append('\n');
    return sb.toString();
  }

  @Override
  public String getName() {
    return NAME;
  }

  public String getInfo() {
    return "VisibilityParameterAnnotation";
  }

  @Override
  public byte[] getValue() {
    throw new RuntimeException("VisibilityParameterAnnotationTag has no value for bytecode");
  }

  public void addVisibilityAnnotation(VisibilityAnnotationTag a) {
    if (visibilityAnnotations == null) {
      visibilityAnnotations = new ArrayList<VisibilityAnnotationTag>();
    }
    visibilityAnnotations.add(a);
  }

  public ArrayList<VisibilityAnnotationTag> getVisibilityAnnotations() {
    return visibilityAnnotations;
  }

  public int getKind() {
    return kind;
  }

  public int getNumParams() {
    return num_params;
  }
}
