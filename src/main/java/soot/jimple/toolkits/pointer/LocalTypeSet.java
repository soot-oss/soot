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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.FastHierarchy;
import soot.Local;
import soot.RefType;
import soot.Scene;
import soot.Type;

/** Represents a set of (local,type) pairs using a bit-vector. */
class LocalTypeSet extends java.util.BitSet {
  private static final Logger logger = LoggerFactory.getLogger(LocalTypeSet.class);

  protected final List<Local> locals;
  protected final List<Type> types;

  /**
   * Constructs a new empty set given a list of all locals and types that may ever be in the set.
   */
  public LocalTypeSet(List<Local> locals, List<Type> types) {
    super(locals.size() * types.size());
    this.locals = locals;
    this.types = types;

    // Make sure that we have a hierarchy
    Scene.v().getOrMakeFastHierarchy();
  }

  /** Returns the number of the bit corresponding to the pair (l,t). */
  protected int indexOf(Local l, RefType t) {
    int index_l = locals.indexOf(l);
    int index_t = types.indexOf(t);
    if (index_l == -1 || index_t == -1) {
      throw new RuntimeException("Invalid local or type in LocalTypeSet");
    }
    return index_l * types.size() + index_t;
  }

  /** Removes all pairs corresponding to local l from the set. */
  public void killLocal(Local l) {
    int typesSize = types.size();
    int base = typesSize * locals.indexOf(l);
    for (int i = 0; i < typesSize; i++) {
      clear(i + base);
    }
  }

  /** For each pair (from,t), adds a pair (to,t). */
  public void localCopy(Local to, Local from) {
    int typesSize = types.size();
    int baseTo = typesSize * locals.indexOf(to);
    int baseFrom = typesSize * locals.indexOf(from);
    for (int i = 0; i < typesSize; i++) {
      if (get(i + baseFrom)) {
        set(i + baseTo);
      } else {
        clear(i + baseTo);
      }
    }
  }

  /** Empties the set. */
  public void clearAllBits() {
    for (int i = 0, e = types.size() * locals.size(); i < e; i++) {
      clear(i);
    }
  }

  /** Fills the set to contain all possible (local,type) pairs. */
  public void setAllBits() {
    for (int i = 0, e = types.size() * locals.size(); i < e; i++) {
      set(i);
    }
  }

  /** Adds to the set all pairs (l,type) where type is any supertype of t. */
  public void localMustBeSubtypeOf(Local l, RefType t) {
    FastHierarchy fh = Scene.v().getFastHierarchy();
    for (Type type : types) {
      RefType supertype = (RefType) type;
      if (fh.canStoreType(t, supertype)) {
        set(indexOf(l, supertype));
      }
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (Local l : locals) {
      for (Type t : types) {
        RefType rt = (RefType) t;
        int index = indexOf(l, rt);
        // logger.debug("for: " + l + " and type: " + rt + " at: " + index);
        if (get(index)) {
          sb.append("((").append(l).append(',').append(rt).append(") -> elim cast check) ");
        }
      }
    }
    return sb.toString();
  }
}
