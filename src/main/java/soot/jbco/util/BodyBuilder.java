package soot.jbco.util;

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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import soot.Local;
import soot.PatchingChain;
import soot.RefType;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Trap;
import soot.Type;
import soot.Unit;
import soot.baf.IfCmpEqInst;
import soot.baf.IfCmpGeInst;
import soot.baf.IfCmpGtInst;
import soot.baf.IfCmpLeInst;
import soot.baf.IfCmpLtInst;
import soot.baf.IfCmpNeInst;
import soot.baf.IfEqInst;
import soot.baf.IfGeInst;
import soot.baf.IfGtInst;
import soot.baf.IfLeInst;
import soot.baf.IfLtInst;
import soot.baf.IfNeInst;
import soot.baf.IfNonNullInst;
import soot.baf.IfNullInst;
import soot.jimple.Jimple;
import soot.jimple.ThisRef;
import soot.util.Chain;

/**
 * @author Michael Batchelder
 *
 *         Created on 7-Feb-2006
 */
public class BodyBuilder {

  public static boolean bodiesHaveBeenBuilt = false;
  public static boolean namesHaveBeenRetrieved = false;
  public static List<String> nameList = new ArrayList<String>();

  public static void retrieveAllBodies() {
    if (bodiesHaveBeenBuilt) {
      return;
    }

    // iterate through application classes, rename fields with junk
    for (SootClass c : soot.Scene.v().getApplicationClasses()) {

      for (SootMethod m : c.getMethods()) {
        if (!m.isConcrete()) {
          continue;
        }

        if (!m.hasActiveBody()) {
          m.retrieveActiveBody();
        }
      }
    }

    bodiesHaveBeenBuilt = true;
  }

  public static void retrieveAllNames() {
    if (namesHaveBeenRetrieved) {
      return;
    }

    // iterate through application classes, rename fields with junk

    for (SootClass c : soot.Scene.v().getApplicationClasses()) {
      nameList.add(c.getName());

      for (SootMethod m : c.getMethods()) {
        nameList.add(m.getName());
      }
      for (SootField m : c.getFields()) {
        nameList.add(m.getName());
      }
    }

    namesHaveBeenRetrieved = true;
  }

  public static Local buildThisLocal(PatchingChain<Unit> units, ThisRef tr, Collection<Local> locals) {
    Local ths = Jimple.v().newLocal("ths", tr.getType());
    locals.add(ths);
    units.add(Jimple.v().newIdentityStmt(ths, Jimple.v().newThisRef((RefType) tr.getType())));
    return ths;
  }

  public static List<Local> buildParameterLocals(PatchingChain<Unit> units, Collection<Local> locals,
      List<Type> paramTypes) {
    List<Local> args = new ArrayList<Local>();
    for (int k = 0; k < paramTypes.size(); k++) {
      Type type = paramTypes.get(k);
      Local loc = Jimple.v().newLocal("l" + k, type);
      locals.add(loc);

      units.add(Jimple.v().newIdentityStmt(loc, Jimple.v().newParameterRef(type, k)));

      args.add(loc);
    }
    return args;
  }

  public static void updateTraps(Unit oldu, Unit newu, Chain<Trap> traps) {
    int size = traps.size();
    if (size == 0) {
      return;
    }

    Trap t = traps.getFirst();
    do {
      if (t.getBeginUnit() == oldu) {
        t.setBeginUnit(newu);
      }
      if (t.getEndUnit() == oldu) {
        t.setEndUnit(newu);
      }
      if (t.getHandlerUnit() == oldu) {
        t.setHandlerUnit(newu);
      }
    } while ((--size > 0) && (t = traps.getSuccOf(t)) != null);
  }

  public static boolean isExceptionCaughtAt(Chain<Unit> units, Unit u, Iterator<Trap> trapsIt) {
    while (trapsIt.hasNext()) {
      Trap t = trapsIt.next();
      Iterator<Unit> it = units.iterator(t.getBeginUnit(), units.getPredOf(t.getEndUnit()));
      while (it.hasNext()) {
        if (u.equals(it.next())) {
          return true;
        }
      }
    }

    return false;
  }

  public static int getIntegerNine() {
    int r1 = Rand.getInt(8388606) * 256;

    int r2 = Rand.getInt(28) * 9;

    if (r2 > 126) {
      r2 += 4;
    }

    return r1 + r2;
  }

  public static boolean isBafIf(Unit u) {
    if (u instanceof IfCmpEqInst || u instanceof IfCmpGeInst || u instanceof IfCmpGtInst || u instanceof IfCmpLeInst
        || u instanceof IfCmpLtInst || u instanceof IfCmpNeInst || u instanceof IfEqInst || u instanceof IfGeInst
        || u instanceof IfGtInst || u instanceof IfLeInst || u instanceof IfLtInst || u instanceof IfNeInst
        || u instanceof IfNonNullInst || u instanceof IfNullInst) {
      return true;
    }
    return false;
  }
}
