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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Body;
import soot.BodyTransformer;
import soot.BooleanType;
import soot.IntType;
import soot.Local;
import soot.PatchingChain;
import soot.RefType;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.StmtAddressType;
import soot.Trap;
import soot.Type;
import soot.Unit;
import soot.baf.Baf;
import soot.baf.GotoInst;
import soot.baf.IdentityInst;
import soot.baf.JSRInst;
import soot.baf.TargetArgInst;
import soot.jbco.IJbcoTransform;
import soot.jbco.jimpleTransformations.FieldRenamer;
import soot.jbco.util.Rand;
import soot.jimple.IntConstant;
import soot.jimple.NullConstant;
import soot.toolkits.graph.BriefUnitGraph;

public class TryCatchCombiner extends BodyTransformer implements IJbcoTransform {

  private static final Logger logger = LoggerFactory.getLogger(TryCatchCombiner.class);
  int totalcount = 0;
  int changedcount = 0;

  public static String dependancies[] = new String[] { "bb.jbco_j2bl", "bb.jbco_ctbcb", "bb.jbco_ful", "bb.lp" };

  public String[] getDependencies() {
    return dependancies;
  }

  public static String name = "bb.jbco_ctbcb";

  public String getName() {
    return name;
  }

  public void outputSummary() {
    out.println("Total try blocks found: " + totalcount);
    out.println("Combined TryCatches: " + changedcount);
  }

  protected void internalTransform(Body b, String phaseName, Map<String, String> options) {

    int weight = soot.jbco.Main.getWeight(phaseName, b.getMethod().getSignature());
    if (weight == 0) {
      return;
    }

    int trapCount = 0;
    PatchingChain<Unit> units = b.getUnits();
    ArrayList<Unit> headList = new ArrayList<Unit>();
    ArrayList<Trap> trapList = new ArrayList<Trap>();
    Iterator<Trap> traps = b.getTraps().iterator();

    // build list of heads and corresponding traps
    while (traps.hasNext()) {
      Trap t = traps.next();
      totalcount++;
      // skip runtime exceptions
      if (!isRewritable(t)) {
        continue;
      }

      headList.add(t.getBeginUnit());
      trapList.add(t);
      trapCount++;
    }

    if (trapCount == 0) {
      return;
    }

    // check if any traps have same head, if so insert dumby NOP to disambiguate
    for (int i = 0; i < headList.size(); i++) {
      for (int j = 0; j < headList.size(); j++) {
        if (i == j) {
          continue;
        }
        if (headList.get(i) == headList.get(j)) {
          Trap t = trapList.get(i);
          Unit nop = Baf.v().newNopInst();
          units.insertBeforeNoRedirect(nop, headList.get(i));
          headList.set(i, nop);
          t.setBeginUnit(nop);
        }
      }
    }

    Unit first = null;
    Iterator<Unit> uit = units.iterator();
    while (uit.hasNext()) {
      Unit unit = (Unit) uit.next();
      if (!(unit instanceof IdentityInst)) {
        break;
      }
      first = unit;
    }
    if (first == null) {
      first = Baf.v().newNopInst();
      units.insertBefore(first, units.getFirst());
    } else {
      first = (Unit) units.getSuccOf(first);
    }

    Collection<Local> locs = b.getLocals();
    Map<Unit, Stack<Type>> stackHeightsBefore = null;
    Map<Local, Local> bafToJLocals = soot.jbco.Main.methods2Baf2JLocals.get(b.getMethod());
    int varCount = trapCount + 1;
    traps = b.getTraps().snapshotIterator();
    while (traps.hasNext()) {
      Trap t = traps.next();
      Unit begUnit = t.getBeginUnit();
      if (!isRewritable(t) || Rand.getInt(10) > weight) {
        continue;
      }

      stackHeightsBefore = StackTypeHeightCalculator.calculateStackHeights(b, bafToJLocals);
      boolean badType = false;

      @SuppressWarnings("unchecked")
      Stack<Type> s = (Stack<Type>) stackHeightsBefore.get(begUnit).clone();

      if (s.size() > 0) {
        for (int i = 0; i < s.size(); i++) {
          if (s.pop() instanceof StmtAddressType) {
            badType = true;
            break;
          }
        }
      }
      if (badType) {
        continue;
      }

      // local to hold control flow flag (0=try, 1=catch)
      Local controlLocal = Baf.v().newLocal("controlLocal_tccomb" + trapCount, IntType.v());
      locs.add(controlLocal);

      // initialize local to 0=try
      Unit pushZero = Baf.v().newPushInst(IntConstant.v(0));
      Unit storZero = Baf.v().newStoreInst(IntType.v(), controlLocal);

      // this is necessary even though it seems like it shouldn't be
      units.insertBeforeNoRedirect((Unit) pushZero.clone(), first);
      units.insertBeforeNoRedirect((Unit) storZero.clone(), first);

      BriefUnitGraph graph = new BriefUnitGraph(b);
      List<Unit> l = graph.getPredsOf(begUnit);

      // add initializer seq for try - sets local to zero and loads null exc
      units.add(pushZero);
      units.add(storZero);

      Stack<Local> varsToLoad = new Stack<Local>();
      s = stackHeightsBefore.get(begUnit);
      if (s.size() > 0) {
        for (int i = 0; i < s.size(); i++) {
          Type type = s.pop();

          Local varLocal = Baf.v().newLocal("varLocal_tccomb" + varCount++, type);
          locs.add(varLocal);
          varsToLoad.push(varLocal);
          units.add(Baf.v().newStoreInst(type, varLocal));

          units.insertBeforeNoRedirect(FixUndefinedLocals.getPushInitializer(varLocal, type), first);
          units.insertBeforeNoRedirect(Baf.v().newStoreInst(type, varLocal), first);
        }
      }
      units.add(Baf.v().newPushInst(NullConstant.v()));
      units.add(Baf.v().newGotoInst(begUnit));

      // for each pred of the beginUnit of the try, we must insert goto initializer
      for (int i = 0; i < l.size(); i++) {
        Unit pred = l.get(i);
        if (isIf(pred)) {
          TargetArgInst ifPred = ((TargetArgInst) pred);
          if (ifPred.getTarget() == begUnit) {
            ifPred.setTarget(pushZero);
          }

          Unit succ = units.getSuccOf(ifPred);
          if (succ == begUnit) {
            units.insertAfter(Baf.v().newGotoInst(pushZero), ifPred);
          }
        } else if (pred instanceof GotoInst && ((GotoInst) pred).getTarget() == begUnit) {
          ((GotoInst) pred).setTarget(pushZero);
        } else {
          units.insertAfter(Baf.v().newGotoInst(pushZero), pred);
        }
      }

      Unit handlerUnit = t.getHandlerUnit();
      Unit newBeginUnit = Baf.v().newLoadInst(IntType.v(), controlLocal);
      units.insertBefore(newBeginUnit, begUnit);
      units.insertBefore(Baf.v().newIfNeInst(handlerUnit), begUnit);
      units.insertBefore(Baf.v().newPopInst(RefType.v()), begUnit);

      while (varsToLoad.size() > 0) {
        Local varLocal = (Local) varsToLoad.pop();
        units.insertBefore(Baf.v().newLoadInst(varLocal.getType(), varLocal), begUnit);
      }

      try {
        SootField f[] = FieldRenamer.v().getRandomOpaques();
        if (f[0] != null && f[1] != null) {
          loadBooleanValue(units, f[0], begUnit);
          loadBooleanValue(units, f[1], begUnit);

          units.insertBeforeNoRedirect(Baf.v().newIfCmpEqInst(BooleanType.v(), begUnit), begUnit);
        }
      } catch (NullPointerException npe) {
        logger.debug(npe.getMessage(), npe);
      }

      // randomize the increment - sometimes store one, sometimes just set to 1
      if (Rand.getInt() % 2 == 0) {
        units.insertBeforeNoRedirect(Baf.v().newPushInst(IntConstant.v(Rand.getInt(3) + 1)), begUnit);
        units.insertBeforeNoRedirect(Baf.v().newStoreInst(IntType.v(), controlLocal), begUnit);
      } else {
        units.insertBeforeNoRedirect(Baf.v().newIncInst(controlLocal, IntConstant.v(Rand.getInt(3) + 1)), begUnit);
      }

      trapCount--;
      t.setBeginUnit(newBeginUnit);
      t.setHandlerUnit(newBeginUnit);

      changedcount++;

      if (debug) {
        StackTypeHeightCalculator.printStack(units, StackTypeHeightCalculator.calculateStackHeights(b), false);
      }
    }
  }

  private void loadBooleanValue(PatchingChain<Unit> units, SootField f, Unit insert) {
    units.insertBefore(Baf.v().newStaticGetInst(f.makeRef()), insert);
    if (f.getType() instanceof RefType) {
      SootMethod boolInit = ((RefType) f.getType()).getSootClass().getMethod("boolean booleanValue()");
      units.insertBefore(Baf.v().newVirtualInvokeInst(boolInit.makeRef()), insert);
    }
  }

  private boolean isIf(Unit u) {
    // TODO: will a RET statement be a TargetArgInst?
    return (u instanceof TargetArgInst) && !(u instanceof GotoInst) && !(u instanceof JSRInst);
  }

  private boolean isRewritable(Trap t) {
    // ignore traps that already catch their own begin unit
    if (t.getBeginUnit() == t.getHandlerUnit()) {
      return false;
    }

    // ignore runtime try blocks - these may have weird side-effects do to asynchronous exceptions
    SootClass exc = t.getException();
    if (exc.getPathPlusClassName().equals("java.lang.Throwable")) {
      return false;
    }

    do {
      if (exc.getPathPlusClassName().equals("java.lang.RuntimeException")) {
        return false;
      }
    } while (exc.hasSuperclass() && (exc = exc.getSuperclass()) != null);

    return true;
  }
}
