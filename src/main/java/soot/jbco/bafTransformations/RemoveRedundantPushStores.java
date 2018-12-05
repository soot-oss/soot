package soot.jbco.bafTransformations;

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

import java.util.Iterator;
import java.util.Map;

import soot.Body;
import soot.BodyTransformer;
import soot.Local;
import soot.PatchingChain;
import soot.Trap;
import soot.Unit;
import soot.baf.PushInst;
import soot.baf.StoreInst;
import soot.jbco.IJbcoTransform;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.util.Chain;

/**
 * @author Michael Batchelder
 *
 *         Created on 16-Jun-2006
 */
public class RemoveRedundantPushStores extends BodyTransformer implements IJbcoTransform {

  public static String dependancies[] = new String[] { "bb.jbco_rrps" };

  public String[] getDependencies() {
    return dependancies;
  }

  public static String name = "bb.jbco_rrps";

  public String getName() {
    return name;
  }

  public void outputSummary() {
  }

  protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
    // removes all redundant load-stores
    boolean changed = true;
    PatchingChain<Unit> units = b.getUnits();
    while (changed) {
      changed = false;
      Unit prevprevprev = null, prevprev = null, prev = null;
      ExceptionalUnitGraph eug = new ExceptionalUnitGraph(b);
      Iterator<Unit> it = units.snapshotIterator();
      while (it.hasNext()) {
        Unit u = it.next();
        if (prev != null && prev instanceof PushInst && u instanceof StoreInst) {
          if (prevprev != null && prevprev instanceof StoreInst && prevprevprev != null
              && prevprevprev instanceof PushInst) {
            Local lprev = ((StoreInst) prevprev).getLocal();
            Local l = ((StoreInst) u).getLocal();
            if (l == lprev && eug.getSuccsOf(prevprevprev).size() == 1 && eug.getSuccsOf(prevprev).size() == 1) {
              fixJumps(prevprevprev, prev, b.getTraps());
              fixJumps(prevprev, u, b.getTraps());
              units.remove(prevprevprev);
              units.remove(prevprev);
              changed = true;
              break;
            }
          }
        }
        prevprevprev = prevprev;
        prevprev = prev;
        prev = u;
      }
    } // end while changes have been made
  }

  private void fixJumps(Unit from, Unit to, Chain<Trap> t) {
    from.redirectJumpsToThisTo(to);
    for (Trap trap : t) {
      if (trap.getBeginUnit() == from) {
        trap.setBeginUnit(to);
      }
      if (trap.getEndUnit() == from) {
        trap.setEndUnit(to);
      }
      if (trap.getHandlerUnit() == from) {
        trap.setHandlerUnit(to);
      }
    }
  }
}
