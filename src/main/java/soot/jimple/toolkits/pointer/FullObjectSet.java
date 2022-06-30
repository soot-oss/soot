package soot.jimple.toolkits.pointer;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Ondrej Lhotak
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

import java.util.Collections;
import java.util.Set;

import soot.AnySubType;
import soot.G;
import soot.PointsToSet;
import soot.PrimType;
import soot.RefType;
import soot.Scene;
import soot.Singletons;
import soot.Type;
import soot.jimple.ClassConstant;

public class FullObjectSet extends Union {

  private final Set<Type> types;

  public static FullObjectSet v() {
    return G.v().soot_jimple_toolkits_pointer_FullObjectSet();
  }

  public static FullObjectSet v(RefType t) {
    return (Scene.v().getObjectType().toString().equals(t.getClassName())) ? v() : new FullObjectSet(t);
  }

  public static FullObjectSet v(PrimType t) {
    return new FullObjectSet(t);
  }

  public FullObjectSet(Singletons.Global g) {
    this(Scene.v().getObjectType());
  }

  private FullObjectSet(PrimType declaredType) {
    this.types = Collections.singleton(declaredType);
  }

  private FullObjectSet(RefType declaredType) {
    this.types = Collections.singleton(AnySubType.v(declaredType));
  }

  public Type type() {
    return types.iterator().next();
  }

  /** Returns true if this set contains no run-time objects. */
  @Override
  public boolean isEmpty() {
    return false;
  }

  /** Returns true if this set is a subset of other. */
  @Override
  public boolean hasNonEmptyIntersection(PointsToSet other) {
    return other != null;
  }

  /** Set of all possible run-time types of objects in the set. */
  @Override
  public Set<Type> possibleTypes() {
    return types;
  }

  /**
   * Adds all objects in s into this union of sets, returning true if this union was changed.
   */
  @Override
  public boolean addAll(PointsToSet s) {
    return false;
  }

  @Override
  public Set<String> possibleStringConstants() {
    return null;
  }

  @Override
  public Set<ClassConstant> possibleClassConstants() {
    return null;
  }

  public int depth() {
    return 0;
  }
}
