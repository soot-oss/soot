/* Soot - a J*va Optimization Framework
 * Copyright (C) 2002 Florian Loitsch
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */


package soot.util;
import soot.toolkits.graph.*;
import soot.*;
import java.util.*;

/**
 * Maps each unit to the result of <code>mapTo</code>.
 */
public abstract class UnitMap implements Map {
  private Hashtable<Object, Object> unitToResult;

  /**
   * maps each unit of this body to the result of <code>mapTo</code>.<br>
   * before the mapping the method <code>init</code> is called.<br>
   * the internal hashtable is initialized without any parameter.
   *
   * @param b a Body
   */
  public UnitMap(Body b) {
    unitToResult = new Hashtable<Object, Object>();
    map(b);
  }

  /**
   * maps each unit of the graph to the result of <code>mapTo</code>.<br>
   * before the mapping the method <code>init</code> is called.<br>
   * the internal hashtable is initialized without any parameter.
   *
   * @param g a UnitGraph
   */
  public UnitMap(UnitGraph g) {
    this(g.getBody());
  }

  /**
   * maps each unit of this body to the result of <code>mapTo</code>.<br>
   * before the mapping the method <code>init</code> is called.<br>
   * the internal hashtable is initialized to <code>initialCapacity</code>.
   *
   * @param b a Body
   * @param initialCapacity the initialCapacity of the internal hashtable.
   */
  public UnitMap(Body b, int initialCapacity) {
    unitToResult = new Hashtable<Object, Object>(initialCapacity);
    map(b);
  }

  /**
   * maps each unit of the graph to the result of <code>mapTo</code>.<br>
   * before the mapping the method <code>init</code> is called.<br>
   * the internal hashtable is initialized to <code>initialCapacity</code>.
   *
   * @param g a UnitGraph
   * @param initialCapacity the initialCapacity of the internal hashtable.
   */
  public UnitMap(UnitGraph g, int initialCapacity) {
    this(g.getBody(), initialCapacity);
  }

  /**
   * maps each unit of this body to the result of <code>mapTo</code>.<br>
   * before the mapping the method <code>init</code> is called.<br>
   * the internal hashtable is initialized to <code>initialCapacity</code> and
   * <code>loadFactor</code>.
   *
   * @param b a Body
   * @param initialCapacity the initialCapacity of the internal hashtable.
   * @param loadFactor the loadFactor of the internal hashtable.
   */
  public UnitMap(Body b, int initialCapacity, float loadFactor) {
    unitToResult = new Hashtable<Object, Object>(initialCapacity);
    init();
    map(b);
  }

  /**
   * maps each unit of the graph to the result of <code>mapTo</code>.<br>
   * before the mapping the method <code>init</code> is called.<br>
   * the internal hashtable is initialized to <code>initialCapacity</code> and
   * <code>loadFactor</code>.
   *
   * @param g a UnitGraph
   * @param initialCapacity the initialCapacity of the internal hashtable.
   * @param loadFactor the loadFactor of the internal hashtable.
   */
  public UnitMap(UnitGraph g, int initialCapacity, float loadFactor) {
    this(g.getBody(), initialCapacity);
  }

  /**
   * does the actual mapping. assumes, that the hashtable is already initialized.
   */
  private void map(Body b) {
    Iterator unitIt = b.getUnits().iterator();
    while (unitIt.hasNext()) {
      Unit currentUnit = (Unit)unitIt.next();
      Object o = mapTo(currentUnit);
      if (o != null)
        unitToResult.put(currentUnit, o);
    }
  }

  /**
   * allows one-time initialization before any mapping. This method is called
   * before any mapping of a unit (but only once in the beginning).<br>
   * If not overwritten does nothing.
   */
  protected void init() {};

  /**
   * maps a unit to an object. This method is called for every unit. If
   * the returned object is <code>null</code> no object will be mapped.<br>
   *
   * @param the Unit to which <code>o</code> should be mapped.
   * @return an object that is mapped to the unit, or <code>null</code>.
   */
  protected abstract Object mapTo(Unit unit);

  /*====== the Map-interface. all methods are deleguated tp the hashmap======*/

  public void clear() {
    unitToResult.clear();
  }

  public boolean containsKey(Object key) {
    return unitToResult.containsKey(key);
  }

  public boolean containsValue(Object value) {
    return unitToResult.containsValue(value);
  }

  public Set entrySet() {
    return unitToResult.entrySet();
  }

  public boolean equals(Object o) {
    return unitToResult.equals(o);
  }

  public Object get(Object key) {
    return unitToResult.get(key);
  }

  public int hashCode() {
    return unitToResult.hashCode();
  }

  public boolean isEmpty() {
    return unitToResult.isEmpty();
  }

  public Set<Object> keySet() {
    return unitToResult.keySet();
  }

  public Object put(Object key, Object value) {
    return unitToResult.put(key, value);
  }

  public void putAll(Map t) {
    unitToResult.putAll(t);
  }

  public Object remove(Object key) {
    return unitToResult.remove(key);
  }

  public int size() {
    return unitToResult.size();
  }

  public Collection<Object> values() {
    return unitToResult.values();
  }
}

