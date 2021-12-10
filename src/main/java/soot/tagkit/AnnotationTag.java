package soot.tagkit;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2005 Jennifer Lhotak
 * Copyright (C) 2013 Tata Consultancy Services & Ecole Polytechnique de Montreal
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
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Represents the annotation attribute attached to a class, method, field, method param - they could have many annotations
 * each for Java 1.5.
 */
public class AnnotationTag implements Tag {

  public static final String NAME = "AnnotationTag";

  // type - the question here is the class of the type is potentially
  // not loaded -- Does it need to be ??? - If it does then this may
  // be something difficult (if just passing the attributes through
  // then it maybe doesn't need to - but if they are runtime visible
  // attributes then won't the annotation class need to be created
  // in the set of output classes for use by tools when using
  // reflection ???

  // number of elem value pairs
  // a bunch of element value pairs
  // a type B C D F I J S Z s e c @ [
  // for B C D F I J S Z s elem is a constant value (entry to cp)
  // in Soot rep as
  // for e elem is a type and the simple name of the enum class
  // rep in Soot as a type and SootClass or as two strings
  // for c elem is a descriptor of the class represented
  // rep in Soot as a SootClass ?? or a string ??
  // for @ (nested annotation)
  // for [ elem is num values and array of values
  // should probably make a bunch of subclasses for all the
  // different kinds - with second level for the constant kinds

  /**
   * The type
   */
  private final String type;

  /**
   * The annotations
   */
  private List<AnnotationElem> elems;

  public AnnotationTag(String type) {
    this.type = type;
    this.elems = null;
  }

  public AnnotationTag(String type, Collection<AnnotationElem> elements) {
    this.type = type;

    if (elements == null || elements.isEmpty()) {
      this.elems = null;
    } else if (elements instanceof List<?>) {
      this.elems = (List<AnnotationElem>) elements;
    } else {
      this.elems = new ArrayList<AnnotationElem>(elements);
    }
  }

  @Deprecated
  public AnnotationTag(String type, int numElem) {
    this.type = type;
    this.elems = new ArrayList<AnnotationElem>(numElem);
  }

  // should also print here number of annotations and perhaps the annotations themselves
  @Override
  public String toString() {
    if (elems != null) {
      StringBuilder sb = new StringBuilder("Annotation: type: ");
      sb.append(type).append(" num elems: ").append(elems.size()).append(" elems: ");
      for (AnnotationElem next : elems) {
        sb.append('\n').append(next);
      }
      sb.append('\n');
      return sb.toString();
    } else {
      return "Annotation type: " + type + " without elements";
    }
  }

  @Override
  public String getName() {
    return NAME;
  }

  public String getInfo() {
    return "Annotation";
  }

  public String getType() {
    return type;
  }

  @Override
  public byte[] getValue() {
    throw new RuntimeException("AnnotationTag has no value for bytecode");
  }

  /**
   * Adds one element to the list
   *
   * @param elem
   *          the element
   */
  public void addElem(AnnotationElem elem) {
    if (elems == null) {
      elems = new ArrayList<AnnotationElem>();
    }
    elems.add(elem);
  }

  /**
   * Overwrites the elements stored previously
   *
   * @param list
   *          the new list of elements
   */
  public void setElems(List<AnnotationElem> list) {
    this.elems = list;
  }

  /**
   * @return an immutable collection of the elements
   */
  public Collection<AnnotationElem> getElems() {
    return elems == null ? Collections.<AnnotationElem>emptyList() : Collections.unmodifiableCollection(elems);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((elems == null) ? 0 : elems.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || this.getClass() != obj.getClass()) {
      return false;
    }
    AnnotationTag other = (AnnotationTag) obj;
    if (this.elems == null) {
      if (other.elems != null) {
        return false;
      }
    } else if (!this.elems.equals(other.elems)) {
      return false;
    }
    if (this.type == null) {
      if (other.type != null) {
        return false;
      }
    } else if (!this.type.equals(other.type)) {
      return false;
    }
    return true;
  }
}
