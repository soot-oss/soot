/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package soot.jbco.bafTransformations;

import java.util.*;
import soot.*;
import soot.baf.*;
import soot.jbco.IJbcoTransform;
import soot.jbco.util.*;

/**
 * @author Michael Batchelder
 * 
 * Created on 22-Mar-2006
 * 
 * This transformer transforms gotos/ifs into JSRS, but not all of them.
 */
public class AddJSRs extends BodyTransformer implements IJbcoTransform {

  int jsrcount = 0;

  public static String dependancies[] = new String[] { "jtp.jbco_jl", "bb.jbco_cb2ji", "bb.jbco_ful", "bb.lp" };
  
  public String[] getDependancies() {
    return dependancies;
  }
  
  public static String name = "bb.jbco_cb2ji";
  
  public String getName() {
    return name;
  }

  public void outputSummary() {
    out.println("If/Gotos replaced with JSRs: " + jsrcount);
  }

  protected void internalTransform(Body b, String phaseName, Map options) {
    int weight = soot.jbco.Main.getWeight(phaseName, b.getMethod().getSignature());
    if (weight == 0) return;

    // TODO: introduce switch statement to all pops which never happens?
    // TODO: introduce if-jsr opaque jumps that never happen?

    boolean fallsthrough = false;
    HashMap<Trap,Unit> trapsToHandler = new HashMap<Trap,Unit>();
    Iterator it = b.getTraps().iterator();
    while (it.hasNext()) {
      Trap t = (Trap) it.next();
      trapsToHandler.put(t, t.getHandlerUnit());
    }

    ArrayList<Unit> targets = new ArrayList<Unit>();
    ArrayList seenUts = new ArrayList();
    HashMap<Unit,List<Unit>> switches = new HashMap<Unit,List<Unit>>();
    HashMap<Unit,Unit> switchDefs = new HashMap<Unit,Unit>();
    HashMap<TargetArgInst,Unit> ignoreJumps = new HashMap<TargetArgInst,Unit>();
    PatchingChain u = b.getUnits();
    it = u.snapshotIterator();
    while (it.hasNext()) {
      Unit unit = (Unit) it.next();
      if (unit instanceof TargetArgInst) {
        TargetArgInst ti = (TargetArgInst) unit;
        Unit tu = ti.getTarget();

        // test if we've already seen the target - if so, it might be a loop so
        // let's not slow things down
        if (Rand.getInt(10) > weight) //seenUts.contains(tu)) {
          ignoreJumps.put(ti, tu);
        else if (!targets.contains(tu))
          targets.add(tu);
      }

      if (unit instanceof TableSwitchInst) {
        List targs = new ArrayList();
        TableSwitchInst ts = (TableSwitchInst) unit;
        targs.addAll(ts.getTargets());
        switches.put(unit, targs);
        switchDefs.put(unit, ts.getDefaultTarget());
      } else if (unit instanceof LookupSwitchInst) {
        List targs = new ArrayList();
        LookupSwitchInst ls = (LookupSwitchInst) unit;
        targs.addAll(ls.getTargets());
        switches.put(unit, targs);
        switchDefs.put(unit, ls.getDefaultTarget());
      }

      seenUts.add(unit);
    }

    it = u.snapshotIterator();
    ArrayList<Unit> processedLabels = new ArrayList<Unit>();
    HashMap<Unit, JSRInst> builtJsrs = new HashMap<Unit, JSRInst>();
    HashMap<Unit,Unit> popsBuilt = new HashMap<Unit,Unit>();
    Unit prev = null;
    while (it.hasNext()) {
      Unit unit = (Unit) it.next();

      // check if prev unit falls through to this unit (non-jump). If so, and
      // it's ALSO a target
      // we need to make a jsr from previous unit to this one, to deal with pop.
      // ignore GOTOs as they will, themselves, become a jsr.
      if (targets.contains(unit)) {
        if (fallsthrough) {
          JSRInst ji = Baf.v().newJSRInst(unit);
          builtJsrs.put(unit, ji);
          u.insertAfter(ji, prev);
          jsrcount++;
        }
        PopInst pop = Baf.v().newPopInst(RefType.v());
        u.insertBefore(pop, unit);
        processedLabels.add(unit);
        popsBuilt.put(pop, unit);
      }
      fallsthrough = unit.fallsThrough();
      prev = unit;
    }

    it = u.snapshotIterator();
    while (it.hasNext()) {
      Unit unit = (Unit) it.next();
      if (builtJsrs.containsValue(unit))
        continue;

      if (unit instanceof TargetArgInst && !ignoreJumps.containsKey(unit)) {
        TargetArgInst ti = (TargetArgInst) unit;
        Unit tu = ti.getTarget();
        // if we haven't dealt with a target yet, add the pop inst
        if (!popsBuilt.containsKey(tu))
          throw new RuntimeException("It appears a target was found that was not updated with a POP.\n\"This makes no sense,\" said the bug as it flew through the code.");

        JSRInst ji = builtJsrs.get(popsBuilt.get(tu));
        if (BodyBuilder.isBafIf(unit)) {
          if (Rand.getInt(10) > weight) {
            ti.setTarget(popsBuilt.get(tu));
          } else if (ji != null) {
            ti.setTarget(ji);
          } else {
            ji = Baf.v().newJSRInst(tu);
            u.insertAfter(ji, u.getPredOf(tu));
            ti.setTarget(ji);

            builtJsrs.put(popsBuilt.get(tu), ji);
            jsrcount++;
          }
        } else if (unit instanceof GotoInst) {
          if (ji != null) {
            if (Rand.getInt(10) < weight)
              ((GotoInst) unit).setTarget(ji);
            else
              ((GotoInst) unit).setTarget(popsBuilt.get(tu));
          } else {
            ((GotoInst) unit).setTarget(popsBuilt.get(tu));
          }
        }
      }
    }

    it = trapsToHandler.keySet().iterator();
    while (it.hasNext()) {
      Trap t = (Trap) it.next();
      t.setHandlerUnit(trapsToHandler.get(t));
    }

    it = ignoreJumps.keySet().iterator();
    while (it.hasNext()) {
      TargetArgInst ti = (TargetArgInst) it.next();
      if (popsBuilt.containsKey(ti.getTarget()))
        ti.setTarget(popsBuilt.get(ti.getTarget()));
    }

    targets.clear();
    it = u.snapshotIterator();
    while (it.hasNext()) {
      Unit unit = (Unit) it.next();
      if (!(unit instanceof TargetArgInst))
        continue;
      Unit targ = ((TargetArgInst) unit).getTarget();
      if (!targets.contains(targ))
        targets.add(targ);
    }

    it = popsBuilt.keySet().iterator();
    while (it.hasNext()) {
      Unit pop = (Unit) it.next();
      if (!targets.contains(pop))
        u.remove(pop);
    }

    it = switches.keySet().iterator();
    while (it.hasNext()) {
      Unit sw = (Unit) it.next();
      List<Unit> targs = switches.get(sw);

      for (int i = 0; i < targs.size(); i++) {
        if (Rand.getInt(10) > weight)
          continue;

        Unit unit = targs.get(i);
        Unit ji = builtJsrs.get(unit);
        if (ji != null)
          targs.set(i, ji);
      }

      Unit def = switchDefs.get(sw);
      if (Rand.getInt(10) < weight && builtJsrs.get(def) != null)
        def = builtJsrs.get(def);

      if (sw instanceof TableSwitchInst) {
        ((TableSwitchInst) sw).setTargets(targs);
        ((TableSwitchInst) sw).setDefaultTarget(def);
      } else if (sw instanceof LookupSwitchInst) {
        ((LookupSwitchInst) sw).setTargets(targs);
        ((LookupSwitchInst) sw).setDefaultTarget(def);
      }
    }
    
    if (debug)
      StackTypeHeightCalculator.calculateStackHeights(b);
  }
}