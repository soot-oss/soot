package soot.jimple.toolkits.infoflow;

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

import soot.Local;
import soot.Type;
import soot.jimple.internal.JimpleLocal;

// A wrapper for a JimpleLocal that defines equivalence and equality
// as having the same name and type.  This is useful for comparing
// InstanceFieldRefs and ArrayRefs from different parts of a program
// (without removing the FieldRef part, which is not a Jimple Value).
// FakeJimpleLocal can also hold a real JimpleLocal
// and some additional object, which together can make it easier to
// later reconstruct the original piece of Jimple code, or to construct
// a new meaningful piece of Jimple code base on this one.

public class FakeJimpleLocal extends JimpleLocal {
  Local realLocal;
  Object info; // whatever you want to attach to it...

  /** Constructs a FakeJimpleLocal of the given name and type. */
  public FakeJimpleLocal(String name, Type t, Local realLocal) {
    this(name, t, realLocal, null);
  }

  public FakeJimpleLocal(String name, Type t, Local realLocal, Object info) {
    super(name, t);
    this.realLocal = realLocal;
    this.info = info;
  }

  /** Returns true if the given object is structurally equal to this one. */
  public boolean equivTo(Object o) {
    if (o == null) {
      return false;
    }
    if (o instanceof JimpleLocal) {
      if (getName() != null && getType() != null) {
        return getName().equals(((Local) o).getName()) && getType().equals(((Local) o).getType());
      } else if (getName() != null) {
        return getName().equals(((Local) o).getName()) && ((Local) o).getType() == null;
      } else if (getType() != null) {
        return ((Local) o).getName() == null && getType().equals(((Local) o).getType());
      } else {
        return ((Local) o).getName() == null && ((Local) o).getType() == null;
      }
    }
    return false;
  }

  public boolean equals(Object o) {
    return equivTo(o);
  }

  /** Returns a clone of the current JimpleLocal. */
  public Object clone() {
    return new FakeJimpleLocal(getName(), getType(), realLocal, info);
  }

  public Local getRealLocal() {
    return realLocal;
  }

  public Object getInfo() {
    return info;
  }

  public void setInfo(Object o) {
    info = o;
  }
}
