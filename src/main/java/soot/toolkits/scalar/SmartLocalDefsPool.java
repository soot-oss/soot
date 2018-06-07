package soot.toolkits.scalar;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2015 Eric Bodden and others
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

import com.google.common.collect.Maps;

import java.util.Map;

import soot.Body;
import soot.G;
import soot.Singletons;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;

/**
 * This class implements a pool for {@link SmartLocalDefs} instances. This is useful, as these analyses are expensive to
 * compute. A {@link SmartLocalDefs} instance requires a {@link UnitGraph} (usually a {@link ExceptionalUnitGraph}), and
 * creating these repeatedly, and applying the {@link SmartLocalDefs} analysis repeatedly costs time. Therefore in this class
 * we pool these instances in cases in which the respective body is still the same.
 *
 * @author Eric Bodden
 */
public class SmartLocalDefsPool {

  protected Map<Body, Pair<Long, SmartLocalDefs>> pool = Maps.newHashMap();

  /**
   * This method returns a fresh instance of a {@link SmartLocalDefs} analysis, based on a freshly created
   * {@link ExceptionalUnitGraph} for b, with standard parameters. If the body b's modification count has not changed since
   * the last time such an analysis was requested for b, then the previously created analysis is returned instead.
   *
   * @see Body#getModificationCount()
   */
  public SmartLocalDefs getSmartLocalDefsFor(Body b) {
    Pair<Long, SmartLocalDefs> modCountAndSLD = pool.get(b);
    if (modCountAndSLD != null && modCountAndSLD.o1.longValue() == b.getModificationCount()) {
      return modCountAndSLD.o2;
    } else {
      ExceptionalUnitGraph g = new ExceptionalUnitGraph(b);
      SmartLocalDefs newSLD = new SmartLocalDefs(g, new SimpleLiveLocals(g));
      pool.put(b, new Pair<Long, SmartLocalDefs>(b.getModificationCount(), newSLD));
      return newSLD;
    }
  }

  public void clear() {
    pool.clear();
  }

  public static SmartLocalDefsPool v() {
    return G.v().soot_toolkits_scalar_SmartLocalDefsPool();
  }

  public SmartLocalDefsPool(Singletons.Global g) {
  }

  public void invalidate(Body b) {
    pool.remove(b);
  }

}
