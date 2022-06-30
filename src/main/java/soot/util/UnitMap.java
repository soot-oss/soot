package soot.util;

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
import java.util.Map;
import java.util.Set;

import soot.Body;
import soot.Unit;
import soot.toolkits.graph.UnitGraph;

/**
 * Maps each unit to the result of <code>mapTo</code>.
 */
public abstract class UnitMap<T> implements Map<Unit, T> {

  private final Map<Unit, T> unitToResult;

  /**
   * maps each unit of this body to the result of <code>mapTo</code>.<br>
   * before the mapping the method <code>init</code> is called.<br>
   * the internal map is initialized without any parameter.
   *
   * @param b
   *          a Body
   */
  public UnitMap(Body b) {
    this.unitToResult = new HashMap<Unit, T>();
    map(b);
  }

  /**
   * maps each unit of the graph to the result of <code>mapTo</code>.<br>
   * before the mapping the method <code>init</code> is called.<br>
   * the internal map is initialized without any parameter.
   *
   * @param g
   *          a UnitGraph
   */
  public UnitMap(UnitGraph g) {
    this(g.getBody());
  }

  /**
   * maps each unit of this body to the result of <code>mapTo</code>.<br>
   * before the mapping the method <code>init</code> is called.<br>
   * the internal map is initialized to <code>initialCapacity</code>.
   *
   * @param b
   *          a Body
   * @param initialCapacity
   *          the initialCapacity of the internal map.
   */
  public UnitMap(Body b, int initialCapacity) {
    this.unitToResult = new HashMap<Unit, T>(initialCapacity);
    map(b);
  }

  /**
   * maps each unit of the graph to the result of <code>mapTo</code>.<br>
   * before the mapping the method <code>init</code> is called.<br>
   * the internal map is initialized to <code>initialCapacity</code>.
   *
   * @param g
   *          a UnitGraph
   * @param initialCapacity
   *          the initialCapacity of the internal map.
   */
  public UnitMap(UnitGraph g, int initialCapacity) {
    this(g.getBody(), initialCapacity);
  }

  /**
   * maps each unit of this body to the result of <code>mapTo</code>.<br>
   * before the mapping the method <code>init</code> is called.<br>
   * the internal map is initialized to <code>initialCapacity</code> and <code>loadFactor</code>.
   *
   * @param b
   *          a Body
   * @param initialCapacity
   *          the initialCapacity of the internal map.
   * @param loadFactor
   *          the loadFactor of the internal map.
   */
  public UnitMap(Body b, int initialCapacity, float loadFactor) {
    this.unitToResult = new HashMap<Unit, T>(initialCapacity);
    init();
    map(b);
  }

  /**
   * maps each unit of the graph to the result of <code>mapTo</code>.<br>
   * before the mapping the method <code>init</code> is called.<br>
   * the internal map is initialized to <code>initialCapacity</code> and <code>loadFactor</code>.
   *
   * @param g
   *          a UnitGraph
   * @param initialCapacity
   *          the initialCapacity of the internal map.
   * @param loadFactor
   *          the loadFactor of the internal map.
   */
  public UnitMap(UnitGraph g, int initialCapacity, float loadFactor) {
    this(g.getBody(), initialCapacity);
  }

  /**
   * does the actual mapping. assumes, that the map is already initialized.
   */
  private void map(Body b) {
    for (Unit currentUnit : b.getUnits()) {
      T o = mapTo(currentUnit);
      if (o != null) {
        unitToResult.put(currentUnit, o);
      }
    }
  }

  /**
   * allows one-time initialization before any mapping. This method is called before any mapping of a unit (but only once in
   * the beginning).<br>
   * If not overwritten does nothing.
   */
  protected void init() {
  }

  /**
   * maps a unit to an object. This method is called for every unit. If the returned object is <code>null</code> no object
   * will be mapped.<br>
   *
   * @param unit
   *          Unit to which <code>o</code> should be mapped.
   * @return an object that is mapped to the unit, or <code>null</code>.
   */
  protected abstract T mapTo(Unit unit);

  /* ====== the Map-interface. all methods are deleguated to the hashmap====== */

  @Override
  public void clear() {
    unitToResult.clear();
  }

  @Override
  public boolean containsKey(Object key) {
    return unitToResult.containsKey(key);
  }

  @Override
  public boolean containsValue(Object value) {
    return unitToResult.containsValue(value);
  }

  @Override
  public Set<Map.Entry<Unit, T>> entrySet() {
    return unitToResult.entrySet();
  }

  @Override
  public boolean equals(Object o) {
    return unitToResult.equals(o);
  }

  @Override
  public T get(Object key) {
    return unitToResult.get(key);
  }

  @Override
  public int hashCode() {
    return unitToResult.hashCode();
  }

  @Override
  public boolean isEmpty() {
    return unitToResult.isEmpty();
  }

  @Override
  public Set<Unit> keySet() {
    return unitToResult.keySet();
  }

  @Override
  public T put(Unit key, T value) {
    return unitToResult.put(key, value);
  }

  @Override
  public void putAll(Map<? extends Unit, ? extends T> t) {
    unitToResult.putAll(t);
  }

  @Override
  public T remove(Object key) {
    return unitToResult.remove(key);
  }

  @Override
  public int size() {
    return unitToResult.size();
  }

  @Override
  public Collection<T> values() {
    return unitToResult.values();
  }
}
