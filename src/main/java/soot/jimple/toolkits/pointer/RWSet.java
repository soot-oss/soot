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

import java.util.Set;

import soot.PointsToSet;
import soot.SootField;

/** Represents the read or write set of a statement. */
public abstract class RWSet {
  public abstract boolean getCallsNative();

  public abstract boolean setCallsNative();

  /** Returns an iterator over any globals read/written. */
  public abstract int size();

  public abstract Set<?> getGlobals();

  public abstract Set<?> getFields();

  public abstract PointsToSet getBaseForField(Object f);

  public abstract boolean hasNonEmptyIntersection(RWSet other);

  /** Adds the RWSet other into this set. */
  public abstract boolean union(RWSet other);

  public abstract boolean addGlobal(SootField global);

  public abstract boolean addFieldRef(PointsToSet otherBase, Object field);

  public abstract boolean isEquivTo(RWSet other);
}
