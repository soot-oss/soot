package soot.toolkits.scalar;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 1999 Raja Vallee-Rai
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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * Reference implementation for a FlowSet. Items are stored in an Array.
 */
public class ArraySparseSet<T> extends AbstractFlowSet<T> {
  protected static final int DEFAULT_SIZE = 8;

  protected int numElements;
  protected int maxElements;
  protected T[] elements;

  @SuppressWarnings("unchecked")
  public ArraySparseSet() {
    maxElements = DEFAULT_SIZE;
    elements = (T[]) new Object[DEFAULT_SIZE];
    numElements = 0;
  }

  private ArraySparseSet(ArraySparseSet<T> other) {
    numElements = other.numElements;
    maxElements = other.maxElements;
    elements = other.elements.clone();
  }

  /** Returns true if flowSet is the same type of flow set as this. */
  private boolean sameType(Object flowSet) {
    return (flowSet instanceof ArraySparseSet);
  }

  public ArraySparseSet<T> clone() {
    return new ArraySparseSet<T>(this);
  }

  public FlowSet<T> emptySet() {
    return new ArraySparseSet<T>();
  }

  public void clear() {
    numElements = 0;
    Arrays.fill(elements, null);
  }

  public int size() {
    return numElements;
  }

  public boolean isEmpty() {
    return numElements == 0;
  }

  /** Returns a unbacked list of elements in this set. */
  public List<T> toList() {
    return Arrays.asList(Arrays.copyOf(elements, numElements));
  }

  /*
   * Expand array only when necessary, pointed out by Florian Loitsch March 08, 2002
   */
  public void add(T e) {
    /* Expand only if necessary! and removes one if too:) */
    // Add element
    if (!contains(e)) {
      // Expand array if necessary
      if (numElements == maxElements) {
        doubleCapacity();
      }
      elements[numElements++] = e;
    }
  }

  private void doubleCapacity() {
    int newSize = maxElements * 2;

    @SuppressWarnings("unchecked")
    T[] newElements = (T[]) new Object[newSize];

    System.arraycopy(elements, 0, newElements, 0, numElements);
    elements = newElements;
    maxElements = newSize;
  }

  public void remove(Object obj) {
    for (int i = 0; i < numElements; i++) {
      if (elements[i].equals(obj)) {
        remove(i);
        break;
      }
    }
  }

  public void remove(int idx) {
    numElements--;
    // copy last element to deleted position
    elements[idx] = elements[numElements];
    // delete reference in last cell so that
    // we only retain a single reference to the
    // "old last" element, for memory safety
    elements[numElements] = null;
    return;
  }

  public void union(FlowSet<T> otherFlow, FlowSet<T> destFlow) {
    if (sameType(otherFlow) && sameType(destFlow)) {
      ArraySparseSet<T> other = (ArraySparseSet<T>) otherFlow;
      ArraySparseSet<T> dest = (ArraySparseSet<T>) destFlow;

      // For the special case that dest == other
      if (dest == other) {
        for (int i = 0; i < this.numElements; i++) {
          dest.add(this.elements[i]);
        }
      }

      // Else, force that dest starts with contents of this
      else {
        if (this != dest) {
          copy(dest);
        }

        for (int i = 0; i < other.numElements; i++) {
          dest.add(other.elements[i]);
        }
      }
    } else {
      super.union(otherFlow, destFlow);
    }
  }

  public void intersection(FlowSet<T> otherFlow, FlowSet<T> destFlow) {
    if (sameType(otherFlow) && sameType(destFlow)) {
      ArraySparseSet<T> other = (ArraySparseSet<T>) otherFlow;
      ArraySparseSet<T> dest = (ArraySparseSet<T>) destFlow;
      ArraySparseSet<T> workingSet;

      if (dest == other || dest == this) {
        workingSet = new ArraySparseSet<T>();
      } else {
        workingSet = dest;
        workingSet.clear();
      }

      for (int i = 0; i < this.numElements; i++) {
        if (other.contains(this.elements[i])) {
          workingSet.add(this.elements[i]);
        }
      }

      if (workingSet != dest) {
        workingSet.copy(dest);
      }
    } else {
      super.intersection(otherFlow, destFlow);
    }
  }

  public void difference(FlowSet<T> otherFlow, FlowSet<T> destFlow) {
    if (sameType(otherFlow) && sameType(destFlow)) {
      ArraySparseSet<T> other = (ArraySparseSet<T>) otherFlow;
      ArraySparseSet<T> dest = (ArraySparseSet<T>) destFlow;
      ArraySparseSet<T> workingSet;

      if (dest == other || dest == this) {
        workingSet = new ArraySparseSet<T>();
      } else {
        workingSet = dest;
        workingSet.clear();
      }

      for (int i = 0; i < this.numElements; i++) {
        if (!other.contains(this.elements[i])) {
          workingSet.add(this.elements[i]);
        }
      }

      if (workingSet != dest) {
        workingSet.copy(dest);
      }
    } else {
      super.difference(otherFlow, destFlow);
    }
  }

  /**
   * @deprecated This method uses linear-time lookup. For better performance, consider using a {@link HashSet} instead, if
   *             you require this operation.
   */
  @Deprecated
  public boolean contains(Object obj) {
    for (int i = 0; i < numElements; i++) {
      if (elements[i].equals(obj)) {
        return true;
      }
    }

    return false;
  }

  @SuppressWarnings("unchecked")
  public boolean equals(Object otherFlow) {
    if (sameType(otherFlow)) {
      ArraySparseSet<T> other = (ArraySparseSet<T>) otherFlow;

      if (other.numElements != this.numElements) {
        return false;
      }

      int size = this.numElements;

      // Make sure that thisFlow is contained in otherFlow
      for (int i = 0; i < size; i++) {
        if (!other.contains(this.elements[i])) {
          return false;
        }
      }

      /*
       * both arrays have the same size, no element appears twice in one array, all elements of ThisFlow are in otherFlow ->
       * they are equal! we don't need to test again! // Make sure that otherFlow is contained in ThisFlow for(int i = 0; i <
       * size; i++) if(!this.contains(other.elements[i])) return false;
       */

      return true;
    } else {
      return super.equals(otherFlow);
    }
  }

  public void copy(FlowSet<T> destFlow) {
    if (sameType(destFlow)) {
      ArraySparseSet<T> dest = (ArraySparseSet<T>) destFlow;

      while (dest.maxElements < this.maxElements) {
        dest.doubleCapacity();
      }

      dest.numElements = this.numElements;

      System.arraycopy(this.elements, 0, dest.elements, 0, this.numElements);
    } else {
      super.copy(destFlow);
    }
  }

  @Override
  public Iterator<T> iterator() {
    return new Iterator<T>() {

      int nextIdx = 0;

      @Override
      public boolean hasNext() {
        return nextIdx < numElements;
      }

      @Override
      public T next() {
        return elements[nextIdx++];
      }

      @Override
      public void remove() {
        if (nextIdx == 0) {
          throw new IllegalStateException("'next' has not been called yet.");
        }
        ArraySparseSet.this.remove(nextIdx - 1);
        nextIdx--;
      }

    };
  }

}
