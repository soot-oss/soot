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

import soot.Context;
import soot.G;
import soot.Local;
import soot.PointsToAnalysis;
import soot.PointsToSet;
import soot.RefType;
import soot.Singletons;
import soot.SootField;
import soot.Type;

/**
 * A very naive pointer analysis that just reports that any points can point to any object.
 */
public class DumbPointerAnalysis implements PointsToAnalysis {

  public DumbPointerAnalysis(Singletons.Global g) {
  }

  public static DumbPointerAnalysis v() {
    return G.v().soot_jimple_toolkits_pointer_DumbPointerAnalysis();
  }

  /** Returns the set of objects pointed to by variable l. */
  @Override
  public PointsToSet reachingObjects(Local l) {
    Type t = l.getType();
    return (t instanceof RefType) ? FullObjectSet.v((RefType) t) : FullObjectSet.v();
  }

  /** Returns the set of objects pointed to by variable l in context c. */
  @Override
  public PointsToSet reachingObjects(Context c, Local l) {
    return reachingObjects(l);
  }

  /** Returns the set of objects pointed to by static field f. */
  @Override
  public PointsToSet reachingObjects(SootField f) {
    Type t = f.getType();
    return (t instanceof RefType) ? FullObjectSet.v((RefType) t) : FullObjectSet.v();
  }

  /**
   * Returns the set of objects pointed to by instance field f of the objects in the PointsToSet s.
   */
  @Override
  public PointsToSet reachingObjects(PointsToSet s, SootField f) {
    return reachingObjects(f);
  }

  /**
   * Returns the set of objects pointed to by instance field f of the objects pointed to by l.
   */
  @Override
  public PointsToSet reachingObjects(Local l, SootField f) {
    return reachingObjects(f);
  }

  /**
   * Returns the set of objects pointed to by instance field f of the objects pointed to by l in context c.
   */
  @Override
  public PointsToSet reachingObjects(Context c, Local l, SootField f) {
    return reachingObjects(f);
  }

  /**
   * Returns the set of objects pointed to by elements of the arrays in the PointsToSet s.
   */
  @Override
  public PointsToSet reachingObjectsOfArrayElement(PointsToSet s) {
    return FullObjectSet.v();
  }
}
