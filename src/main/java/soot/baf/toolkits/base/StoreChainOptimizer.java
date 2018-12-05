package soot.baf.toolkits.base;

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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import soot.Body;
import soot.BodyTransformer;
import soot.G;
import soot.Local;
import soot.Singletons;
import soot.Unit;
import soot.baf.PushInst;
import soot.baf.StoreInst;
import soot.toolkits.scalar.Pair;

/**
 * Due to local packing, we may have chains of assignments to the same local.
 * 
 * push null; store.r $r2;
 *
 * push null; store.r $r2;
 * 
 * This transformer eliminates the redundant push/store instructions.
 * 
 * @author Steven Arzt
 *
 */
public class StoreChainOptimizer extends BodyTransformer {

  public StoreChainOptimizer(Singletons.Global g) {
  }

  public static StoreChainOptimizer v() {
    return G.v().soot_baf_toolkits_base_StoreChainOptimizer();
  }

  @Override
  protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
    // We keep track of all the stored values
    Map<Local, Pair<Unit, Unit>> stores = new HashMap<Local, Pair<Unit, Unit>>();
    Set<Unit> toRemove = new HashSet<Unit>();

    Unit lastPush = null;
    for (Unit u : b.getUnits()) {
      // If we can jump here from somewhere, do not modify this code
      if (!u.getBoxesPointingToThis().isEmpty()) {
        stores.clear();
        lastPush = null;
      }
      // Emulate pushing stuff on the stack
      else if (u instanceof PushInst) {
        lastPush = u;
      } else if (u instanceof StoreInst && lastPush != null) {
        StoreInst si = (StoreInst) u;
        Pair<Unit, Unit> pushStorePair = stores.get(si.getLocal());
        if (pushStorePair != null) {
          // We can remove the push and the store
          toRemove.add(pushStorePair.getO1());
          toRemove.add(pushStorePair.getO2());
        }

        stores.put(si.getLocal(), new Pair<Unit, Unit>(lastPush, u));
      } else {
        // We're outside of the trivial initialization chain
        stores.clear();
        lastPush = null;
      }
    }

    b.getUnits().removeAll(toRemove);
  }

}
