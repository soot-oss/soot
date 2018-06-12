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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import soot.ArrayType;
import soot.Body;
import soot.BodyTransformer;
import soot.DoubleType;
import soot.FloatType;
import soot.IntType;
import soot.IntegerType;
import soot.Local;
import soot.LongType;
import soot.PatchingChain;
import soot.RefLikeType;
import soot.StmtAddressType;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.baf.Baf;
import soot.baf.DoubleWordType;
import soot.baf.IdentityInst;
import soot.baf.IncInst;
import soot.baf.NopInst;
import soot.baf.OpTypeArgInst;
import soot.baf.PushInst;
import soot.baf.WordType;
import soot.baf.internal.AbstractOpTypeInst;
import soot.jbco.IJbcoTransform;
import soot.jimple.DoubleConstant;
import soot.jimple.FloatConstant;
import soot.jimple.IntConstant;
import soot.jimple.LongConstant;
import soot.jimple.NullConstant;
import soot.toolkits.scalar.GuaranteedDefs;

/**
 * @author Michael Batchelder
 *
 *         Created on 16-Jun-2006
 */
public class FixUndefinedLocals extends BodyTransformer implements IJbcoTransform {

  private int undefined = 0;

  public static String dependancies[] = new String[] { "bb.jbco_j2bl", "bb.jbco_ful", "bb.lp" };

  public String[] getDependencies() {
    return dependancies;
  }

  public static String name = "bb.jbco_ful";

  public String getName() {
    return name;
  }

  public void outputSummary() {
    out.println("Undefined Locals fixed with pre-initializers: " + undefined);
  }

  protected void internalTransform(Body b, String phaseName, Map<String, String> options) {
    // deal with locals not defined at all used points

    int icount = 0;
    boolean passedIDs = false;
    Map<Local, Local> bafToJLocals = soot.jbco.Main.methods2Baf2JLocals.get(b.getMethod());
    ArrayList<Value> initialized = new ArrayList<Value>();
    PatchingChain<Unit> units = b.getUnits();
    GuaranteedDefs gd = new GuaranteedDefs(new soot.toolkits.graph.ExceptionalUnitGraph(b));
    Iterator<Unit> unitIt = units.snapshotIterator();
    Unit after = null;
    while (unitIt.hasNext()) {
      Unit u = unitIt.next();
      if (!passedIDs && u instanceof IdentityInst) {
        Value v = ((IdentityInst) u).getLeftOp();
        if (v instanceof Local) {
          initialized.add(v);
          icount++;
        }
        after = u;
        continue;
      }

      passedIDs = true;

      if (after == null) {
        after = Baf.v().newNopInst();
        units.addFirst(after);
      }

      List<?> defs = gd.getGuaranteedDefs(u);
      Iterator<ValueBox> useIt = u.getUseBoxes().iterator();
      while (useIt.hasNext()) {
        Value v = ((ValueBox) useIt.next()).getValue();
        if (!(v instanceof Local) || defs.contains(v) || initialized.contains(v)) {
          continue;
        }

        Type t = null;
        Local l = (Local) v;
        Local jl = (Local) bafToJLocals.get(l);
        if (jl != null) {
          t = jl.getType();
        } else {
          // We should hopefully never get here. There should be a jimple
          // local unless it's one of our ControlDups
          t = l.getType();
          if (u instanceof OpTypeArgInst) {
            OpTypeArgInst ota = (OpTypeArgInst) u;
            t = ota.getOpType();
          } else if (u instanceof AbstractOpTypeInst) {
            AbstractOpTypeInst ota = (AbstractOpTypeInst) u;
            t = ota.getOpType();
          } else if (u instanceof IncInst) {
            t = IntType.v();
          }

          if (t instanceof DoubleWordType || t instanceof WordType) {
            throw new RuntimeException("Shouldn't get here (t is a double or word type: in FixUndefinedLocals)");
          }
        }
        Unit store = Baf.v().newStoreInst(t, l);
        units.insertAfter(store, after);

        // TODO: is this necessary if I fix the other casting issues?
        if (t instanceof ArrayType) {
          Unit tmp = Baf.v().newInstanceCastInst(t);
          units.insertBefore(tmp, store);
          store = tmp;
        }
        /////

        Unit pinit = getPushInitializer(l, t);
        units.insertBefore(pinit, store);
        /*
         * if (t instanceof RefType) { SootClass sc = ((RefType)t).getSootClass(); if (sc != null)
         * units.insertAfter(Baf.v().newInstanceCastInst(t), pinit); }
         */

        initialized.add(l);
      }
    }

    if (after instanceof NopInst) {
      units.remove(after);
    }
    undefined += initialized.size() - icount;
  }

  public static PushInst getPushInitializer(Local l, Type t) {
    if (t instanceof IntegerType) {
      return Baf.v().newPushInst(IntConstant.v(soot.jbco.util.Rand.getInt()));
    } else if (t instanceof RefLikeType || t instanceof StmtAddressType) {
      return Baf.v().newPushInst(NullConstant.v());
    } else if (t instanceof LongType) {
      return Baf.v().newPushInst(LongConstant.v(soot.jbco.util.Rand.getLong()));
    } else if (t instanceof FloatType) {
      return Baf.v().newPushInst(FloatConstant.v(soot.jbco.util.Rand.getFloat()));
    } else if (t instanceof DoubleType) {
      return Baf.v().newPushInst(DoubleConstant.v(soot.jbco.util.Rand.getDouble()));
    }

    return null;
  }

  /*
   *
   * private Unit findInitializerSpotFor(Value v, Unit u, UnitGraph ug, GuaranteedDefs gd) { List preds = ug.getPredsOf(u);
   * while (preds.size() == 1) { Unit p = (Unit) preds.get(0); //if (p instanceof IdentityInst) // break;
   *
   * u = p; preds = ug.getPredsOf(u); }
   *
   * if (preds.size() <= 1) return u;
   *
   * ArrayList nodef = new ArrayList(); Iterator pIt = preds.iterator(); while (pIt.hasNext()) { Unit u1 = (Unit) pIt.next();
   * if (!gd.getGuaranteedDefs(u1).contains(v)) { nodef.add(u1); } }
   *
   * if (nodef.size() == preds.size()) return u;
   *
   * if (nodef.size() == 1) return findInitializerSpotFor(v, (Unit) nodef.get(0), ug, gd);
   *
   * throw new RuntimeException("Shouldn't Ever Get Here!"); }
   */
}
