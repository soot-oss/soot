package soot.jbco.bafTransformations;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 1999 Raja Vallee-Rai
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
import soot.PatchingChain;
import soot.Trap;
import soot.Unit;
import soot.baf.Baf;
import soot.baf.TableSwitchInst;
import soot.baf.ThrowInst;
import soot.jbco.IJbcoTransform;
import soot.jbco.util.BodyBuilder;
import soot.jbco.util.Rand;
import soot.jbco.util.ThrowSet;
import soot.util.Chain;

/**
 * @author Michael Batchelder
 * 
 *         Created on 24-May-2006
 */
public class WrapSwitchesInTrys extends BodyTransformer implements IJbcoTransform {

  int totaltraps = 0;

  public static String dependancies[] = new String[] { "bb.jbco_ptss", "bb.jbco_ful", "bb.lp" };

  public String[] getDependencies() {
    return dependancies;
  }

  public static String name = "bb.jbco_ptss";

  public String getName() {
    return name;
  }

  public void outputSummary() {
    out.println("Switches wrapped in Tries: " + totaltraps);
  }

  protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
    int weight = soot.jbco.Main.getWeight(phaseName, b.getMethod().getSignature());
    if (weight == 0) {
      return;
    }

    int i = 0;
    Unit handler = null;
    Chain<Trap> traps = b.getTraps();
    PatchingChain<Unit> units = b.getUnits();
    Iterator<Unit> it = units.snapshotIterator();
    while (it.hasNext()) {
      Unit u = (Unit) it.next();
      if (u instanceof TableSwitchInst) {
        TableSwitchInst twi = (TableSwitchInst) u;

        if (!BodyBuilder.isExceptionCaughtAt(units, twi, traps.iterator()) && Rand.getInt(10) <= weight) {
          if (handler == null) {
            Iterator<Unit> uit = units.snapshotIterator();
            while (uit.hasNext()) {
              Unit uthrow = (Unit) uit.next();
              if (uthrow instanceof ThrowInst && !BodyBuilder.isExceptionCaughtAt(units, uthrow, traps.iterator())) {
                handler = uthrow;
                break;
              }
            }

            if (handler == null) {
              handler = Baf.v().newThrowInst();
              units.add(handler);
            }
          }

          int size = 4;
          Unit succ = (Unit) units.getSuccOf(twi);
          while (!BodyBuilder.isExceptionCaughtAt(units, succ, traps.iterator()) && size-- > 0) {
            Object o = units.getSuccOf(succ);
            if (o != null) {
              succ = (Unit) o;
            } else {
              break;
            }
          }

          traps.add(Baf.v().newTrap(ThrowSet.getRandomThrowable(), twi, succ, handler));
          i++;
        }
      }
    }

    totaltraps += i;
    if (i > 0 && debug) {
      StackTypeHeightCalculator.calculateStackHeights(b);
    }
  }
}
