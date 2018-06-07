
package soot.jimple.toolkits.thread.synchronization;

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

import java.util.Collections;
import java.util.List;

import soot.NullType;
import soot.SootClass;
import soot.Type;
import soot.UnitPrinter;
import soot.Value;
import soot.ValueBox;
import soot.util.Switch;

// Written by Richard L. Halpert on August 11, 2007
// Acts as a dummy value that gets put in a transaction's lockset,
// indicating that a new static object needs to be inserted into the
// program for use as a lock.

public class NewStaticLock implements Value {
  SootClass sc; // The class to which to add a static lock.
  static int nextidnum = 1;
  int idnum;

  public NewStaticLock(SootClass sc) {
    this.sc = sc;
    this.idnum = nextidnum++;
  }

  public SootClass getLockClass() {
    return sc;
  }

  @Override
  public List<ValueBox> getUseBoxes() {
    return Collections.emptyList();
  }

  /** Clones the object. Not implemented here. */
  public Object clone() {
    return new NewStaticLock(sc);
  }

  /**
   * Returns true if this object is structurally equivalent to c. AbstractDataSources are equal and equivalent if their
   * sourcename is the same
   */
  public boolean equivTo(Object c) {
    return equals(c);
  }

  public boolean equals(Object c) {
    if (c instanceof NewStaticLock) {
      return ((NewStaticLock) c).idnum == idnum;
    }
    return false;
  }

  /** Returns a hash code consistent with structural equality for this object. */
  public int equivHashCode() {
    return hashCode();
  }

  public int hashCode() {
    return idnum;
  }

  public void toString(UnitPrinter up) {
  }

  public Type getType() {
    return NullType.v();
  }

  public void apply(Switch sw) {
    throw new RuntimeException("Not Implemented");
  }

  public String toString() {
    return "<new static lock in " + sc.toString() + ">";
  }
}
