package soot.toolkits.scalar;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2002 Florian Loitsch
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

/**
 * gives an injection of Objects to ints. Different instances of <code>ObjectIntMap</code> may map different ints to the same
 * object.
 */
public class ObjectIntMapper<E> {
  private Vector<E> intToObjects;
  private int counter;
  private Map<E, Integer> objectToInts;

  public ObjectIntMapper() {
    intToObjects = new Vector<E>();
    objectToInts = new HashMap<E, Integer>();
    counter = 0;
  }

  public ObjectIntMapper(FlowUniverse<E> flowUniverse) {
    this(flowUniverse.iterator(), flowUniverse.size());
  }

  public ObjectIntMapper(Collection<E> collection) {
    this(collection.iterator(), collection.size());
  }

  private ObjectIntMapper(Iterator<E> it, int initSize) {
    intToObjects = new Vector<E>(initSize);
    objectToInts = new HashMap<E, Integer>(initSize);
    counter = 0;
    while (it.hasNext()) {
      add(it.next());
    }
  }

  /**
   * adds <code>o</code> into the map. no test are made, if it is already in the map.
   */
  public int add(E o) {
    objectToInts.put(o, counter);
    intToObjects.add(o);
    return counter++;
  }

  /**
   * returns the mapping of <code>o</code>. if there has been a call to <code>objectToInt</code> with the same <code>o</code>
   * before, the same value will be returned.
   *
   * @param o
   * @return <code>o</code>'s mapping
   */
  public int getInt(E o) {
    Integer i = objectToInts.get(o);
    if (i != null) {
      return i;
    }
    return add(o);
  }

  /**
   * returns the object associated to <code>i</code>.
   *
   * @param i
   * @return <code>i</code>'s object
   */
  public E getObject(int i) {
    return intToObjects.get(i);
  }

  /**
   * returns true, if <code>o</code> has already been mapped.
   *
   * @param o
   * @return true if <code>o</code> has already a number.
   */
  public boolean contains(Object o) {
    return objectToInts.containsKey(o);
  }

  /**
   * returns the number of mapped objects.
   */
  public int size() {
    return counter;
  }

}
