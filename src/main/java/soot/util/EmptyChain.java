package soot.util;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vallée-Rai and others
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

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Class representing an unmodifiable empty chain
 * 
 * @author Steven Arzt
 *
 */
public class EmptyChain implements Chain {

  private static final Iterator emptyIterator = new Iterator() {

    @Override
    public void remove() {
      throw new NoSuchElementException();
    }

    @Override
    public boolean hasNext() {
      return false;
    }

    @Override
    public Object next() {
      throw new NoSuchElementException();
    }

  };

  private static EmptyChain instance = null;

  public static EmptyChain v() {
    if (instance == null) {
      instance = new EmptyChain();
    }
    return instance;
  }

  @Override
  public boolean isEmpty() {
    return true;
  }

  @Override
  public boolean contains(Object o) {
    return false;
  }

  @Override
  public Object[] toArray() {
    return new Object[0];
  }

  @Override
  public Object[] toArray(Object[] a) {
    return new Object[0];
  }

  @Override
  public boolean add(Object e) {
    throw new RuntimeException("Cannot add elements to an unmodifiable chain");
  }

  @Override
  public boolean containsAll(Collection c) {
    return false;
  }

  @Override
  public boolean addAll(Collection c) {
    throw new RuntimeException("Cannot add elements to an unmodifiable chain");
  }

  @Override
  public boolean removeAll(Collection c) {
    throw new RuntimeException("Cannot remove elements from an unmodifiable chain");
  }

  @Override
  public boolean retainAll(Collection c) {
    throw new RuntimeException("Cannot add elements to an unmodifiable chain or remove ones from such chain");
  }

  @Override
  public void clear() {
    throw new RuntimeException("Cannot remove elements from an unmodifiable chain");
  }

  @Override
  public void insertBefore(List toInsert, Object point) {
    throw new RuntimeException("Cannot add elements to an unmodifiable chain");
  }

  @Override
  public void insertAfter(List toInsert, Object point) {
    throw new RuntimeException("Cannot add elements to an unmodifiable chain");
  }

  @Override
  public void insertAfter(Object toInsert, Object point) {
    throw new RuntimeException("Cannot add elements to an unmodifiable chain");
  }

  @Override
  public void insertBefore(Object toInsert, Object point) {
    throw new RuntimeException("Cannot add elements to an unmodifiable chain");
  }

  @Override
  public void insertBefore(Chain toInsert, Object point) {
    throw new RuntimeException("Cannot add elements to an unmodifiable chain");
  }

  @Override
  public void insertAfter(Chain toInsert, Object point) {
    throw new RuntimeException("Cannot add elements to an unmodifiable chain");
  }

  @Override
  public void insertOnEdge(Object toInsert, Object point_src, Object point_tgt) {
    throw new RuntimeException("Cannot add elements to an unmodifiable chain");
  }

  @Override
  public void insertOnEdge(List toInsert, Object point_src, Object point_tgt) {
    throw new RuntimeException("Cannot add elements to an unmodifiable chain");
  }

  @Override
  public void insertOnEdge(Chain toInsert, Object point_src, Object point_tgt) {
    throw new RuntimeException("Cannot add elements to an unmodifiable chain");
  }

  @Override
  public void swapWith(Object out, Object in) {
    throw new RuntimeException("Cannot replace elements in an unmodifiable chain");
  }

  @Override
  public boolean remove(Object u) {
    throw new RuntimeException("Cannot remove elements from an unmodifiable chain");
  }

  @Override
  public void addFirst(Object u) {
    throw new RuntimeException("Cannot add elements to an unmodifiable chain");
  }

  @Override
  public void addLast(Object u) {
    throw new RuntimeException("Cannot add elements to an unmodifiable chain");
  }

  @Override
  public void removeFirst() {
    throw new RuntimeException("Cannot remove elements from an unmodifiable chain");
  }

  @Override
  public void removeLast() {
    throw new RuntimeException("Cannot remove elements from an unmodifiable chain");
  }

  @Override
  public boolean follows(Object someObject, Object someReferenceObject) {
    return false;
  }

  @Override
  public Object getFirst() {
    throw new NoSuchElementException();
  }

  @Override
  public Object getLast() {
    throw new NoSuchElementException();
  }

  @Override
  public Object getSuccOf(Object point) {
    throw new NoSuchElementException();
  }

  @Override
  public Object getPredOf(Object point) {
    throw new NoSuchElementException();
  }

  @Override
  public Iterator snapshotIterator() {
    return emptyIterator;
  }

  @Override
  public Iterator iterator() {
    return emptyIterator;
  }

  @Override
  public Iterator iterator(Object u) {
    return emptyIterator;
  }

  @Override
  public Iterator iterator(Object head, Object tail) {
    return emptyIterator;
  }

  @Override
  public int size() {
    return 0;
  }

  @Override
  public long getModificationCount() {
    return 0;
  }

  @Override
  public Collection getElementsUnsorted() {
    return Collections.emptyList();
  }

}
