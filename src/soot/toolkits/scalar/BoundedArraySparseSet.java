/* Soot - a J*va Optimization Framework
 * Copyright (C) 2002 Florian Loitsch
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */


package soot.toolkits.scalar;

import soot.util.*;
import java.util.*;

/**
 * provides an efficient implementation for flowsets, that are usually sparse,
 * but sometimes completely full. The full set is marked by a boolean to avoid
 * using memory, but removing an element from these full sets make them grow
 * very big.<br>
 * Even without the use of "complement()" or "topSet()" this flowSet is well
 * suited for sparse sets.
 */
public class BoundedArraySparseSet extends AbstractBoundedFlowSet {
  private static final int DEFAULT_SIZE = 8;

  private static final int UNION = 0;
  private static final int INTERSECTION = 1;
  private static final int DIFFERENCE = 2;
  private static final int INVERSE_DIFFERENCE = 3;

  private static final int SEARCH_LINEAR_LIMIT = 20;
  private static final int INSERTION_SORT_LIMIT = 20;

  private int numElements;
  private int maxElements;

  /* if isComplemented is false, elements contains the elements of the set,
   * otherwise it contains the elements that should be removed. */
  private int[] elements;
  private ObjectIntMapper elementIntMap;
  private boolean isSorted;

  private boolean isComplemented;

  private FlowUniverse flowUniverse;

  /* to avoid unnecessary allocations, we keep a tmp-array here */
  private TmpArrayHolder tmpArrayHolder;

  /**
   * creates a BoundedArraySparseSet without an explicite
   * flow-universe. although complement(), and topSet() can be called, toList()
   * isEmpty(), size() and equals(o) may yield an exception, if the whole
   * universe was necessary.
   */
  public BoundedArraySparseSet() {
    this(new ObjectIntMapper(), null);
  }

  /**
   * a BoundedArraySparseSet with flowUniverse. While the size of the
   * flowUniverse will be asked in several methods the elements itself are only
   * used in toList().<br>
   * Thi flowUniverse must be exhaustive. All possible elements must be in it.
   *
   * @param flowUniverse all possible elements.
   */
  public BoundedArraySparseSet(FlowUniverse flowUniverse) {
    this(new ObjectIntMapper(flowUniverse), flowUniverse);
  }

  private BoundedArraySparseSet(ObjectIntMapper elementIntMap, FlowUniverse
                                flowUniverse) {
    this(elementIntMap, flowUniverse, new TmpArrayHolder());
  }    

  private BoundedArraySparseSet(ObjectIntMapper elementIntMap, FlowUniverse
                                flowUniverse, TmpArrayHolder tmpArrayHolder) {
    numElements = 0;
    maxElements = DEFAULT_SIZE;
    elements = new int[DEFAULT_SIZE];
    this.elementIntMap = elementIntMap;
    isSorted = true;
    isComplemented = false;
    this.flowUniverse = flowUniverse;
    this.tmpArrayHolder = tmpArrayHolder;
  }

  private BoundedArraySparseSet(BoundedArraySparseSet other) {
    numElements = other.numElements;
    maxElements = other.maxElements;
    elements = (int[])other.elements.clone();
    elementIntMap = other.elementIntMap;
    isSorted = other.isSorted;
    isComplemented = other.isComplemented;
    flowUniverse = other.flowUniverse;
    tmpArrayHolder = other.tmpArrayHolder;
  }

    /** Returns true if flowSet is the same type of flow set as this. */
  private boolean sameType(Object flowSet) {
    return (flowSet instanceof BoundedArraySparseSet) &&
      ((BoundedArraySparseSet)flowSet).elementIntMap == elementIntMap &&
      ((BoundedArraySparseSet)flowSet).flowUniverse == flowUniverse;
  }

  private void increaseCapacity() {        
    int newSize = maxElements * 2;
    int[] newElements = new int[newSize];
    System.arraycopy(elements, 0, newElements, 0, numElements);
    elements = newElements;
    maxElements = newSize;
  }   

  /**
   * sorts the <code>array</code> using insertion-sort.
   *
   * @param array the int-array to sort
   * @param from inclusive
   * @param to exclusive
   */
  private void insertionSort(int[] array, int from, int to) {
    for (int i = from + 1; i < to; i++) {
      int j = i;
      int cur = array[i];
      while (j > from && array[j - 1] > cur) {
        array[j] = array[j - 1];
        j--;
      }
      array[j] = cur;
    }
  }

  /**
   * debug-function
   */
  private void testSort() {
    if (!isSorted) return;
    if (numElements <= 1) return;
    int last = elements[0];
    for (int i = 1; i < numElements; i++) {
      if (last > elements[i]) {
        System.err.println("ERROR: flowSet is not sorted");
        System.err.print("elements: ");
        for (int j = 0; j < numElements; j++) System.err.print(elements[j]);
        System.exit(-1);
      }
      last = elements[i];
    }
  }

  /**
   * sorts the <code>numElements</code> elements of the elements-array.
   */
  private void sort() {
    if (isSorted) return;
    if (numElements < INSERTION_SORT_LIMIT)
      insertionSort(elements, 0, numElements);
    else
      Arrays.sort(elements, 0, numElements);
    isSorted = true;
  }

  /**
   * rans through the array, to find i.
   *
   * @param i the int to search in the array.
   * @param array the array in which we search.
   * @param from from where we search.
   * @param to exclusive of the search.
   * @return <code>-1</code> if not found, or the index of <code>i</code>
   */
  private int linearSearch(int i, int[] array, int from, int to) {
    for (int j = from; j < to; j++)
      if (array[j] == i) return j;
    return -1;
  }

  /**
   * searches, using binary search, <code>i</code> in the <code>array</code>.<br>
   * if <code>i</code> is not in the array, <code>-1</code> is returned.
   *
   * @param i the int to search in the array.
   * @param array the array in which we search.
   * @param from from where we search.
   * @param to exclusive of the search.
   * @return <code>-1</code> if not found, or the index of <code>i</code>
   */
  private int binarySearch(int i, int[] array, int from, int to) {
    if (from == to) return -1;
    int lower = from;
    int upper = to - 1;
    while (lower < upper) {
      int med = (lower + upper) / 2;
      if (array[med] >= i)
        upper = med;
      else
        lower = med + 1;
    }
    if (array[upper] == i) return upper;
    return -1;
  }

  /**
   * searches <code>i</code> in the elements-table. if it is not in the table
   * <code>-1</code> is returned.
   *
   * @param i the int to search in the elements-table.
   * @return the index of <code>i</code> or <code>-1</code> if not in the table.
   */
  private int search(int i) {
    if (numElements < SEARCH_LINEAR_LIMIT || !isSorted)
      return linearSearch(i, elements, 0, numElements);
    else
      return binarySearch(i, elements, 0, numElements);
  }
  private int search(Object o) {
    return search(elementIntMap.getInt(o));
  }

  public Object clone() {
    return new BoundedArraySparseSet(this);
  }

  public void clear() {
    numElements = 0;
    isComplemented = false;
    isSorted = true;
  }

  public Object emptySet() {
    return new BoundedArraySparseSet(elementIntMap, flowUniverse,
                                     tmpArrayHolder);
  }

  public int size() {
    if (isComplemented && flowUniverse == null) {
      String m = "don't know size of set without flowUniverse"; 
      throw new UnsupportedOperationException(m);
    }
    if (isComplemented)
      return flowUniverse.size() - numElements;
    else
      return numElements;
  }

  public boolean isEmpty() {
    return size() == 0;
  }

  /**
   * the returned iterator implements the <code>remove()</code>-method.
   */
  public Iterator iterator() {
    return new BoundedArraySparseSetIterator();
  }

  public List toList() {
    if (isComplemented && flowUniverse == null) {
      String m = "can't do toList() without flowUniverse"; 
      throw new UnsupportedOperationException(m);
    }
    List list;
    if (isComplemented) {
      list = new ArrayList(flowUniverse.size() - numElements);
      Iterator it = flowUniverse.iterator();
      sort();
      while (it.hasNext()) {
        Object o = it.next();
        if (contains(o))
          list.add(o);
      }
    } else {
      list = new ArrayList(numElements);
      for (int i = 0; i < numElements; i++)
        list.add(elementIntMap.getObject(elements[i]));
    }
    return list;
  }

  public void complement(FlowSet dest) {
    if (sameType(dest)) {
      if (this == dest)
        isComplemented = !isComplemented;
      else {
        copy(dest);
        ((BoundedArraySparseSet)dest).complement();
      }
    } else
      super.complement(dest);
  }

  /**
   * appends <code>i</code> to the elements-array. doesn't verify if it is
   * already in the array.
   *
   * @param i the integer associated to the object we insert in the array.
   */
  private void appendToElements(int i) {
    // Expand array if necessary
    if (numElements == maxElements)
      increaseCapacity();

    elements[numElements++] = i;

    if (numElements > 1 &&
        isSorted &&
        i > elements[numElements - 2]) {
      /* isSorted = true; */
    } else
      isSorted = false;
  }

  /**
   * appends <code>i</code> to the elements-array, if it is not already there.
   *
   * @param i the integer associated to the object we insert in the array.
   */
  private void addToElements(int i) {
    int index = search(i);
    if (index < 0)
      appendToElements(i);
  }

  /**
   * removes <code>i</code> from the elements-array, if it is in the array.
   *
   * @param i the integer associated to the object we remove from the array.
   */
  private void removeFromElements(int i) {
    int index = search(i);
    if (index >= 0) {
      elements[index] = elements[--numElements];
      isSorted = false;
    }
  }

  public void add(Object obj) {
    int i = elementIntMap.getInt(obj);
    if (isComplemented)
      removeFromElements(i);
    else
      addToElements(i);
  }

  public void remove(Object obj) {
    int i = elementIntMap.getInt(obj);
    if (isComplemented)
      addToElements(i);
    else
      removeFromElements(i);
  }

  /**
   * performs union (a + b), intersection (a * b), difference (a - b) and
   * inverse difference (b - a) in O(n.log(n)). If the sets are sorted, it is
   * even in O(n). (n = max(numElements, other.numElements)).
   */
  private void elementsBinOp(BoundedArraySparseSet other, BoundedArraySparseSet
                             dest, int action) {
    sort();
    other.sort();
    int[] sourceThis = elements;
    int numElementsThis = numElements;
    int[] sourceOther = other.elements;
    int numElementsOther = other.numElements;

    if (this == dest || other == dest) {
      //swap tmpArray and dest.elements, so we won't overwrite a source-array.
      dest.elements = tmpArrayHolder.exchange(dest.elements);
      dest.maxElements = dest.elements.length;
    }
    dest.numElements = 0;
    dest.isSorted = true;

    int iThis = 0;
    int iOther = 0;
    switch (action) {
    case UNION:
      while (iThis < numElementsThis &&
             iOther < numElementsOther) {
        if (sourceThis[iThis] < sourceOther[iOther])
          dest.appendToElements(sourceThis[iThis++]);
        else if (sourceThis[iThis] > sourceOther[iOther])
          dest.appendToElements(sourceOther[iOther++]);
        else
          iThis++;
      }
      while (iThis < numElementsThis)
        dest.appendToElements(sourceThis[iThis++]);
      while (iOther < numElementsOther)
        dest.appendToElements(sourceOther[iOther++]);
      break;

    case INTERSECTION:
      while (iThis < numElementsThis &&
             iOther < numElementsOther) {
        if (sourceThis[iThis] < sourceOther[iOther])
          iThis++;
        else if (sourceThis[iThis] > sourceOther[iOther])
          iOther++;
        else {
          dest.appendToElements(sourceThis[iThis]);
          iThis++;
          iOther++;
        }
      }
      break;

    case DIFFERENCE:
      while (iThis < numElementsThis &&
             iOther < numElementsOther) {
        if (sourceThis[iThis] < sourceOther[iOther])
          dest.appendToElements(sourceThis[iThis++]);
        else if (sourceThis[iThis] > sourceOther[iOther])
          iOther++;
        else {
          iThis++;
          iOther++;
        }
      }
      while (iThis < numElementsThis)
        dest.appendToElements(sourceThis[iThis++]);
      break;

    case INVERSE_DIFFERENCE:
      while (iThis < numElementsThis &&
             iOther < numElementsOther) {
        if (sourceThis[iThis] < sourceOther[iOther])
          iThis++;
        else if (sourceThis[iThis] > sourceOther[iOther])
          dest.appendToElements(sourceOther[iOther++]);
        else {
          iThis++;
          iOther++;
        }
      }
      while (iOther < numElementsOther)
        dest.appendToElements(sourceOther[iOther++]);
      break;
    }
  }
    
  public void union(FlowSet otherFlow, FlowSet destFlow) {
    if (this == destFlow && this == otherFlow) return;

    if (sameType(otherFlow) &&
        sameType(destFlow)) {
      BoundedArraySparseSet other = (BoundedArraySparseSet)otherFlow;
      BoundedArraySparseSet dest = (BoundedArraySparseSet)destFlow;
      if (!isComplemented && !other.isComplemented) {
        elementsBinOp(other, dest, UNION);
        dest.isComplemented = false;
      } else if (isComplemented && other.isComplemented) {
        elementsBinOp(other, dest, INTERSECTION);
        dest.isComplemented = true;
      } else if (isComplemented && !other.isComplemented) {
        elementsBinOp(other, dest, DIFFERENCE);
        dest.isComplemented = true;
      } else { //(!isComplemented && other.isComplemented)
        elementsBinOp(other, dest, INVERSE_DIFFERENCE);
        dest.isComplemented = true;
      }
    } else
      super.union(otherFlow, destFlow);
  }

  public void intersection(FlowSet otherFlow, FlowSet destFlow) {
    if (this == destFlow && this == otherFlow) return;

    if (sameType(otherFlow) &&
        sameType(destFlow)) {
      BoundedArraySparseSet other = (BoundedArraySparseSet)otherFlow;
      BoundedArraySparseSet dest = (BoundedArraySparseSet)destFlow;
      if (!isComplemented && !other.isComplemented) {
        elementsBinOp(other, dest, INTERSECTION);
        dest.isComplemented = false;
      } else if (isComplemented && other.isComplemented) {
        elementsBinOp(other, dest, UNION);
        dest.isComplemented = true;
      } else if (isComplemented && !other.isComplemented) {
        elementsBinOp(other, dest, INVERSE_DIFFERENCE);
        dest.isComplemented = false;
      } else { //(!isComplemented && other.isComplemented)
        elementsBinOp(other, dest, DIFFERENCE);
        dest.isComplemented = false;
      }
    } else
      super.intersection(otherFlow, destFlow);
  }

  public void difference(FlowSet otherFlow, FlowSet destFlow) {
    if (this == otherFlow) {
      destFlow.clear();
      return;
    }
    if (sameType(otherFlow) &&
        sameType(destFlow)) {
      /* both FlowSets must be sorted (and if they aren't we
       * sort them), and then we go linearly through them */
      BoundedArraySparseSet other = (BoundedArraySparseSet)otherFlow;
      BoundedArraySparseSet dest = (BoundedArraySparseSet)destFlow;
      if (!isComplemented && !other.isComplemented) {
        elementsBinOp(other, dest, DIFFERENCE);
        dest.isComplemented = false;
      } else if (isComplemented && other.isComplemented) {
        elementsBinOp(other, dest, INVERSE_DIFFERENCE);
        dest.isComplemented = false;
      } else if (isComplemented && !other.isComplemented) {
        elementsBinOp(other, dest, UNION);
        dest.isComplemented = true;
      } else { //(!isComplemented && other.isComplemented)
        elementsBinOp(other, dest, INTERSECTION);
        dest.isComplemented = false;
      }
    } else
      super.difference(otherFlow, destFlow);
  }
    
  public boolean contains(Object obj) {
    boolean inElements = (search(obj) >= 0);
    return isComplemented? !inElements: inElements;
  }

  public boolean equals(Object otherFlow) {
    if (sameType(otherFlow)) {
      BoundedArraySparseSet other = (BoundedArraySparseSet) otherFlow;
      if (!isComplemented && !other.isComplemented ||
          isComplemented && other.isComplemented) {
        if (numElements != other.numElements) return false;
        sort();
        other.sort();

        for (int i = 0; i < numElements; i++)
          if (elements[i] != other.elements[i]) return false;

        return true;
      } else {
        if (flowUniverse == null) {
          String m = "can't test for equality without flowUniverse"; 
          throw new UnsupportedOperationException(m);
        } else {
          if (numElements + other.numElements != flowUniverse.size())
            return false;
          sort();
          other.sort();
          int iThis = 0;
          int iOther = 0;
          while (iThis < numElements &&
                 iOther < other.numElements) {
            if (elements[iThis] < other.elements[iOther])
              iThis++;
            else if (elements[iThis] > other.elements[iOther])
              iOther++;
            else
              return false;
          }
          return true;
        }
      }
    } else
      return super.equals(otherFlow);
  }

  public void copy(FlowSet destFlow) {
    if (destFlow == this) return;
    if (sameType(destFlow)) {
      BoundedArraySparseSet dest = (BoundedArraySparseSet)destFlow;
      if (dest.maxElements >= numElements) { //do a array-copy
        dest.numElements = numElements;
        System.arraycopy(elements, 0, dest.elements, 0, numElements);
      } else { //make a clone
        dest.elements = (int[])elements.clone();
        dest.numElements = numElements;
        dest.maxElements = maxElements;
      }
      dest.isComplemented = isComplemented;
      dest.isSorted = isSorted;
    } else
      super.copy(destFlow);
  }

  /**
   * we want to share a temporary array between the sets. a possible way would
   * be to declare it static, but then it stays, even if all instances, that
   * used this tmp-array are "closed".<br>
   * using this Holder, it is now possible to share the array.
   */
  private static class TmpArrayHolder {
    public int[] tmpArray;

    public TmpArrayHolder() {
      this(new int[DEFAULT_SIZE]);
    }

    public TmpArrayHolder(int[] tmpArray) {
      this.tmpArray = tmpArray;
    }

    /**
     * exchanges the new array with the stored one.
     */
    public int[] exchange(int[] newTmp) {
      int[] tmp = tmpArray;
      tmpArray = newTmp;
      return tmp;
    }
  }

  /**
   * a simple implementation of an Iterator.
   */
  private class BoundedArraySparseSetIterator implements Iterator {
    private int index;
    private boolean removeAllowed;

    BoundedArraySparseSetIterator() {
      index = 0;
      removeAllowed = false;
    }

    public boolean hasNext() {
      return (index < numElements);
    }

    public Object next() {
      if (index < numElements) {
        removeAllowed = true;
        return elementIntMap.getObject(elements[index++]);
      } else
        throw new NoSuchElementException();
    }

    public void remove() {
      if (removeAllowed) {
        removeAllowed = false;
        elements[--index] = elements[--numElements];
        isSorted = false;
      } else
        throw new IllegalStateException();
    }
  }
}
