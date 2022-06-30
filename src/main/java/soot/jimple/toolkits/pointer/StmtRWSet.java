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

import soot.PointsToSet;
import soot.SootField;

/** Represents the read or write set of a statement. */
public class StmtRWSet extends RWSet {

  protected Object field;
  protected PointsToSet base;
  protected boolean callsNative = false;

  @Override
  public String toString() {
    return "[Field: " + field + base + "]\n";
  }

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
    return (base == null) ? Collections.singleton(field) : Collections.emptySet();
  }

  /** Returns an iterator over any fields read/written. */
  @Override
  public Set<Object> getFields() {
    return (base != null) ? Collections.singleton(field) : Collections.emptySet();
  }

  /** Returns a set of base objects whose field f is read/written. */
  @Override
  public PointsToSet getBaseForField(Object f) {
    return field.equals(f) ? base : null;
  }

  @Override
  public boolean hasNonEmptyIntersection(RWSet other) {
    if (field == null) {
      return false;
    }
    if (other instanceof StmtRWSet) {
      StmtRWSet o = (StmtRWSet) other;
      if (!this.field.equals(o.field)) {
        return false;
      } else if (this.base == null) {
        return o.base == null;
      } else {
        return Union.hasNonEmptyIntersection(this.base, o.base);
      }
    } else if (other instanceof MethodRWSet) {
      if (this.base == null) {
        return other.getGlobals().contains(this.field);
      } else {
        return Union.hasNonEmptyIntersection(this.base, other.getBaseForField(this.field));
      }
    } else {
      return other.hasNonEmptyIntersection(this);
    }
  }

  /** Adds the RWSet other into this set. */
  @Override
  public boolean union(RWSet other) {
    throw new RuntimeException("Can't do that");
  }

  @Override
  public boolean addGlobal(SootField global) {
    if (field != null || base != null) {
      throw new RuntimeException("Can't do that");
    }
    field = global;
    return true;
  }

  @Override
  public boolean addFieldRef(PointsToSet otherBase, Object field) {
    if (this.field != null || base != null) {
      throw new RuntimeException("Can't do that");
    }
    this.field = field;
    base = otherBase;
    return true;
  }

  @Override
  public boolean isEquivTo(RWSet other) {
    if (!(other instanceof StmtRWSet)) {
      return false;
    }
    StmtRWSet o = (StmtRWSet) other;
    if ((this.callsNative != o.callsNative) || !this.field.equals(o.field)) {
      return false;
    }
    if (this.base instanceof FullObjectSet && o.base instanceof FullObjectSet) {
      return true;
    }
    return this.base == o.base;
  }
}
