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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.G;
import soot.PointsToSet;
import soot.SootField;

/** Represents the read or write set of a statement. */
public class MethodRWSet extends RWSet {
  private static final Logger logger = LoggerFactory.getLogger(MethodRWSet.class);

  public static final int MAX_SIZE = Integer.MAX_VALUE;

  public Set<SootField> globals;
  public Map<Object, PointsToSet> fields;
  protected boolean callsNative = false;
  protected boolean isFull = false;

  // static int count = 0;
  public MethodRWSet() {
    /*
     * count++; if( 0 == (count % 1000) ) { logger.debug(""+ "Created "+count+"th MethodRWSet" ); }
     */
  }

  @Override
  public String toString() {
    StringBuilder ret = new StringBuilder();
    boolean empty = true;
    if (fields != null) {
      for (Map.Entry<Object, PointsToSet> e : fields.entrySet()) {
        ret.append("[Field: ").append(e.getKey()).append(' ').append(e.getValue()).append("]\n");
        empty = false;
      }
    }
    if (globals != null) {
      for (SootField global : globals) {
        ret.append("[Global: ").append(global).append("]\n");
        empty = false;
      }
    }
    if (empty) {
      ret.append("empty");
    }
    return ret.toString();
  }

  @Override
  public int size() {
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
  public Set<SootField> getGlobals() {
    if (isFull) {
      return G.v().MethodRWSet_allGlobals;
    }
    return (globals == null) ? Collections.emptySet() : globals;
  }

  /** Returns an iterator over any fields read/written. */
  @Override
  public Set<Object> getFields() {
    if (isFull) {
      return G.v().MethodRWSet_allFields;
    }
    return (fields == null) ? Collections.emptySet() : fields.keySet();
  }

  /** Returns a set of base objects whose field f is read/written. */
  @Override
  public PointsToSet getBaseForField(Object f) {
    if (isFull) {
      return FullObjectSet.v();
    }
    return (fields == null) ? null : fields.get(f);
  }

  @Override
  public boolean hasNonEmptyIntersection(RWSet oth) {
    if (isFull) {
      return oth != null;
    }
    if (!(oth instanceof MethodRWSet)) {
      return oth.hasNonEmptyIntersection(this);
    }
    MethodRWSet other = (MethodRWSet) oth;
    if (globals != null && other.globals != null && !globals.isEmpty() && !other.globals.isEmpty()) {
      for (SootField next : other.globals) {
        if (globals.contains(next)) {
          return true;
        }
      }
    }
    if (fields != null && other.fields != null && !fields.isEmpty() && !other.fields.isEmpty()) {
      for (Object field : other.fields.keySet()) {
        if (fields.containsKey(field)) {
          if (Union.hasNonEmptyIntersection(getBaseForField(field), other.getBaseForField(field))) {
            return true;
          }
        }
      }
    }
    return false;
  }

  /** Adds the RWSet other into this set. */
  @Override
  public boolean union(RWSet other) {
    if (other == null || isFull) {
      return false;
    }
    boolean ret = false;
    if (other instanceof MethodRWSet) {
      MethodRWSet o = (MethodRWSet) other;
      if (o.getCallsNative()) {
        ret = !getCallsNative() | ret;
        setCallsNative();
      }
      if (o.isFull) {
        ret = !isFull | ret;
        isFull = true;
        if (true) {
          throw new RuntimeException("attempt to add full set " + o + " into " + this);
        }
        globals = null;
        fields = null;
        return ret;
      }
      if (o.globals != null) {
        if (globals == null) {
          globals = new HashSet<SootField>();
        }
        ret = globals.addAll(o.globals) | ret;
        if (globals.size() > MAX_SIZE) {
          globals = null;
          isFull = true;
          throw new RuntimeException("attempt to add full set " + o + " into " + this);
        }
      }
      if (o.fields != null) {
        for (Object field : o.fields.keySet()) {
          ret = addFieldRef(o.getBaseForField(field), field) | ret;
        }
      }
    } else {
      StmtRWSet oth = (StmtRWSet) other;
      if (oth.base != null) {
        ret = addFieldRef(oth.base, oth.field) | ret;
      } else if (oth.field != null) {
        ret = addGlobal((SootField) oth.field) | ret;
      }
    }
    if (!getCallsNative() && other.getCallsNative()) {
      setCallsNative();
      return true;
    }
    return ret;
  }

  @Override
  public boolean addGlobal(SootField global) {
    if (globals == null) {
      globals = new HashSet<SootField>();
    }
    boolean ret = globals.add(global);
    if (globals.size() > MAX_SIZE) {
      globals = null;
      isFull = true;
      throw new RuntimeException("attempt to add more than " + MAX_SIZE + " globals into " + this);
    }
    return ret;
  }

  @Override
  public boolean addFieldRef(PointsToSet otherBase, Object field) {
    boolean ret = false;
    if (fields == null) {
      fields = new HashMap<Object, PointsToSet>();
    }
    PointsToSet base = getBaseForField(field);
    if (base instanceof FullObjectSet) {
      return false;
    }
    if (otherBase instanceof FullObjectSet) {
      fields.put(field, otherBase);
      return true;
    }
    if (otherBase.equals(base)) {
      return false;
    }
    Union u;
    if (base == null || !(base instanceof Union)) {
      u = G.v().Union_factory.newUnion();
      if (base != null) {
        u.addAll(base);
      }
      fields.put(field, u);
      if (base == null) {
        addedField(fields.size());
      }
      ret = true;
      if (fields.keySet().size() > MAX_SIZE) {
        fields = null;
        isFull = true;
        if (true) {
          throw new RuntimeException("attempt to add more than " + MAX_SIZE + " fields into " + this);
        }
        return true;
      }
    } else {
      u = (Union) base;
    }
    ret = u.addAll(otherBase) | ret;
    return ret;
  }

  static void addedField(int size) {
  }

  @Override
  public boolean isEquivTo(RWSet other) {
    return other == this;
  }
}
