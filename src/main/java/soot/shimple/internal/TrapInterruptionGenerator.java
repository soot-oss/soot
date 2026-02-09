package soot.shimple.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.Body;
import soot.Trap;
import soot.Unit;
import soot.options.Options;
import soot.util.HashMultiMap;
import soot.util.MultiMap;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2026 Marc Miltenberger
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

/**
 * Allows to alter statements in trapped regions so that they are removed from all trap handlers, while the code around these
 * statements stays in the trap region.
 * 
 * Example: Before: <code>
 *  label1:
 *    a = b + c;
 *    d = e * f; (1)
 *    x = y + z;
 *  label2:
 *  
 *  catch java.lang.Throwable from label1 to label2 ...
 * </code>
 * 
 * After running TrapInterruptionGenerator to remove statement (1) <code>d = e * f;</code> from the trap regions: <code>
 *  label1:
 *    a = b + c;
 *  label2:
 *    d = e * f;
 *  label3:
 *    x = y + z;
 *  label4:
 *  
 *  catch java.lang.Throwable from label1 to label2 ...
 *  catch java.lang.Throwable from label3 to label4 ...
 * </code>
 * 
 * Note that this changes the semantics of the method! It is primarily used during deshimplification, since the newly added
 * assignments cannot trigger an exception (a = b), but the JVM assumes that they might trigger exceptions, which could trip
 * verification (see soot.shimple.Shimple1Test.testComplexPhi_1A)

 * @author Marc Miltenberger
 */
public class TrapInterruptionGenerator {
  private Body body;
  private MultiMap<Unit, Trap> trapsInRange;

  public TrapInterruptionGenerator(Body body) {
    this.body = body;
    trapsInRange = getTrapsInRange();
    this.body = body;
  }

  private MultiMap<Unit, Trap> getTrapsInRange() {
    MultiMap<Unit, Trap>  trapsInRange= new HashMultiMap<>();
    MultiMap<Unit, Trap> trapStarts = new HashMultiMap<>();
    for (Trap t : body.getTraps()) {
      trapStarts.put(t.getBeginUnit(), t);
    }
    List<Trap> currentTraps = new LinkedList<>();
    for (Unit u : body.getUnits()) {
      currentTraps.addAll(trapStarts.get(u));
      Iterator<Trap> it = currentTraps.iterator();
      while (it.hasNext()) {
        Trap t = it.next();
        if (u == t.getEndUnit()) {
          it.remove();
          continue;
        }
      }
      trapsInRange.putAll(u, currentTraps);
    }
    return trapsInRange;
  }

  void removeTrapsFromChecked(Unit u) {
    MultiMap<Unit, Trap> prev = getTrapsInRange();
    if (!prev.equals(trapsInRange))
      throw new IllegalStateException();
    
    doRemoveTrapsFrom(u);
    MultiMap<Unit, Trap> after = getTrapsInRange();
    if (!after.equals(trapsInRange))
      throw new IllegalStateException();
    
    for (Unit i : body.getUnits()) {
      if (i == u) {
        if (!after.get(i).isEmpty())
          throw new IllegalStateException();
      } else {
        Set<Trap> trapPrev = prev.get(i);
        Set<Trap> trapAfter = after.get(i);
        if (trapPrev.size() != trapAfter.size()) {
          throw new IllegalStateException();
        }
        for (Trap p : trapPrev) {
          boolean foundEquiv = false;
          for (Trap a : trapAfter) {
            if (p == a) {
              foundEquiv = true;
              break;
            }
            if (a.getException() == p.getException() &&
                a.getHandlerUnit() == p.getHandlerUnit()) {
              foundEquiv = true;
              break;
            }
          }
          if (!foundEquiv)
            throw new IllegalStateException("");
        }
      }
    }
    
  }
  
  /**
   * Removes all traps on the given unit
   * @param u the unit
   */
  public void removeTrapsFrom(Unit u) {
    if (Options.v().validate()) {
      removeTrapsFromChecked(u);
    } else {
      doRemoveTrapsFrom(u);      
    }
  }
  protected void doRemoveTrapsFrom(Unit u) {
    Unit after = body.getUnits().getSuccOf(u);
    Set<Trap> r = trapsInRange.get(u);
    if (r.isEmpty()) {
      return;
    }
    if (after != null) {
      //there's a unit after ours, so we need to adjust all other traps
      Map<Trap, Trap> newTraps = new HashMap<>();
      Set<Unit> leftTrapEnds = new HashSet<>();
      for (Trap t : r) {
        //these will be the new traps after u
        Trap clone = (Trap) t.clone();
        newTraps.put(t, clone);
        body.getTraps().insertAfter(clone, t);
        leftTrapEnds.add(t.getEndUnit());
      }

      Unit currentUnit = after;
      while (true) {
        if (leftTrapEnds.remove(currentUnit) && leftTrapEnds.isEmpty()) {
          //no other trap can be affected after this
          break;
        }
        List<Trap> oldTraps = new ArrayList<>(trapsInRange.get(currentUnit));
        for (Trap old : oldTraps) {
          Trap newTrap = newTraps.get(old);
          if (newTrap != null) {
            trapsInRange.remove(currentUnit, old);
            trapsInRange.put(currentUnit, newTrap);
          }
        }
        currentUnit = body.getUnits().getSuccOf(currentUnit);
      }
      for (Trap t : newTraps.values()) {
        t.setBeginUnit(after);
        if (t.getBeginUnit() == t.getEndUnit()) {
          body.getTraps().remove(t);
        }
      }
    }
    Iterator<Trap> rit = r.iterator();
    while (rit.hasNext()) {
      Trap t = rit.next();
      t.setEndUnit(u); //exclude u
      if (t.getBeginUnit() == t.getEndUnit()) {
        rit.remove();
        body.getTraps().remove(t);
      }
    }
    trapsInRange.remove(u);
  }

  public void removeTrapsFrom(Collection<? extends Unit> stmts) {
    for (Unit stmt : stmts) {
      removeTrapsFrom(stmt);
    }
  }
}
