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
 * Represents the visibility of an annotation attribute attached to method local variable. Only mark the local variable tag
 * different with{@link soot.tagkit.VisibilityParameterAnnotationTag}
 * 
 * @author raintung.li
 */
public class VisibilityLocalVariableAnnotationTag extends VisibilityParameterAnnotationTag {

  public static final String NAME = "VisibilityLocalVariableAnnotationTag";

  /**
   * @param num
   *          number of local variable annotations
   * @param kind
   *          one of {@link soot.tagkit.AnnotationConstants}
   */
  public VisibilityLocalVariableAnnotationTag(int num, int kind) {
    super(num, kind);
  }

  @Override
  public String toString() {
    // should also print here number of annotations and perhaps the annotations themselves
    int num_var = getVisibilityAnnotations() != null ? getVisibilityAnnotations().size() : 0;
    StringBuilder sb = new StringBuilder("Visibility LocalVariable Annotation: num Annotation: ");
    sb.append(num_var).append(" kind: ").append(getKind());
    if (num_var > 0) {
      for (VisibilityAnnotationTag tag : getVisibilityAnnotations()) {
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

  /**
   * Returns Local Variable tag info
   * 
   * @return string
   */
  @Override
  public String getInfo() {
    return "VisibilityLocalVariableAnnotation";
  }

  /**
   * VisibilityLocalVariableAnnotationTag not support
   * 
   * @return
   */
  @Override
  public byte[] getValue() {
    throw new RuntimeException("VisibilityLocalVariableAnnotationTag has no value for bytecode");
  }
}
