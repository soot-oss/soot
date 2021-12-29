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

import java.util.HashSet;
import java.util.Set;

import soot.G;
import soot.PointsToSet;
import soot.SootField;

/** Represents the read or write set of a statement. */
public class SiteRWSet extends RWSet {

  public HashSet<RWSet> sets = new HashSet<RWSet>();
  protected boolean callsNative = false;

  @Override
  public int size() {
    Set globals = getGlobals();
    Set fields = getFields();
    if (globals == null) {
      return (fields == null) ? 0 : fields.size();
    } else if (fields == null) {
      return globals.size();
    } else {
      return globals.size() + fields.size();
    }
  }

  @Override
  public String toString() {
    StringBuilder ret = new StringBuilder("SiteRWSet: ");
    boolean empty = true;
    for (RWSet key : sets) {
      ret.append(key.toString());
      empty = false;
    }
    if (empty) {
      ret.append("empty");
    }
    return ret.toString();
  }

  @Override
  public boolean getCallsNative() {
    return callsNative;
  }

  @Override
  public boolean setCallsNative() {
    boolean ret = !callsNative;
    callsNative = true;
    return ret;
  }

  /** Returns an iterator over any globals read/written. */
  @Override
  public Set<Object> getGlobals() {
    HashSet<Object> ret = new HashSet<Object>();
    for (RWSet s : sets) {
      ret.addAll(s.getGlobals());
    }
    return ret;
  }

  /** Returns an iterator over any fields read/written. */
  @Override
  public Set<Object> getFields() {
    HashSet<Object> ret = new HashSet<Object>();
    for (RWSet s : sets) {
      ret.addAll(s.getFields());
    }
    return ret;
  }

  /** Returns a set of base objects whose field f is read/written. */
  @Override
  public PointsToSet getBaseForField(Object f) {
    Union ret = null;
    for (RWSet s : sets) {
      PointsToSet os = s.getBaseForField(f);
      if (os == null || os.isEmpty()) {
        continue;
      }
      if (ret == null) {
        ret = G.v().Union_factory.newUnion();
      }
      ret.addAll(os);
    }
    return ret;
  }

  @Override
  public boolean hasNonEmptyIntersection(RWSet oth) {
    if (sets.contains(oth)) {
      return true;
    }
    for (RWSet s : sets) {
      if (oth.hasNonEmptyIntersection(s)) {
        return true;
      }
    }
    return false;
  }

  /** Adds the RWSet other into this set. */
  @Override
  public boolean union(RWSet other) {
    if (other == null) {
      return false;
    }
    boolean ret = false;
    if (other.getCallsNative()) {
      ret = setCallsNative();
    }
    if (other.getFields().isEmpty() && other.getGlobals().isEmpty()) {
      return ret;
    }
    return sets.add(other) | ret;
  }

  @Override
  public boolean addGlobal(SootField global) {
    throw new RuntimeException("Not implemented; try MethodRWSet");
  }

  @Override
  public boolean addFieldRef(PointsToSet otherBase, Object field) {
    throw new RuntimeException("Not implemented; try MethodRWSet");
  }

  @Override
  public boolean isEquivTo(RWSet other) {
    if (!(other instanceof SiteRWSet)) {
      return false;
    }
    SiteRWSet o = (SiteRWSet) other;
    if (o.callsNative != this.callsNative) {
      return false;
    }
    return o.sets.equals(this.sets);
  }
}
