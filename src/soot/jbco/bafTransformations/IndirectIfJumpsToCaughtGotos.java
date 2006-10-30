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
 * Created on 2-May-2006
 * 
 * This transformer takes a portion of gotos/ifs and moves them into a TRY/CATCH block 
 */
public class IndirectIfJumpsToCaughtGotos extends BodyTransformer implements IJbcoTransform {

  int count = 0;
  
  public static String dependancies[] = new String[] {"bb.jbco_iii", "bb.jbco_ful", "bb.lp"};

  public String[] getDependancies() {
    return dependancies;
  }
  
  public static String name = "bb.jbco_iii";
  
  public String getName() {
    return name;
  }

  
  public void outputSummary() {
    out.println("Indirected Ifs through Traps: "+count);
  }
  
  protected void internalTransform(Body b, String phaseName, Map options) {
    
    int weight = soot.jbco.Main.getWeight(phaseName, b.getMethod().getSignature());
    if (weight == 0) return;
    
    PatchingChain units = b.getUnits();
    Unit nonTrap = findNonTrappedUnit(units,b.getTraps());
    if (nonTrap == null) {
      Unit last = null;
      nonTrap = Baf.v().newNopInst();
      for (Iterator it = units.iterator(); it.hasNext(); ) {
        Unit u = (Unit)it.next();
        if (u instanceof IdentityInst && ((IdentityInst)u).getLeftOp() instanceof Local) {
          last = u;
          continue;
        } else {
          if (last!=null) 
            units.insertAfter(nonTrap,last);
          else
            units.addFirst(nonTrap);
          break;
        }
      }
    }
    
    Stack stack = null;
    try {
      stack = StackTypeHeightCalculator.getAfterStack(b,nonTrap);
    } catch (Exception exc) {
      out.println(exc);
      Debugger.printUnits(b, b.getMethod().getSignature());
      System.exit(1);
    }
    
    ArrayList addedUnits = new ArrayList();
    Iterator it = units.snapshotIterator();
    while (it.hasNext()) {
      Unit u = (Unit)it.next();
      if (isIf(u) && Rand.getInt(10) <= weight) {
        TargetArgInst ifu = (TargetArgInst)u;
        Unit newTarg = Baf.v().newGotoInst(ifu.getTarget());
        units.add(newTarg);
        ifu.setTarget(newTarg);
        addedUnits.add(newTarg);
      }
    }
    
    if (addedUnits.size()<=0) return;
    
    Unit nop = Baf.v().newNopInst();
    units.add(nop);
    
    ArrayList toinsert = new ArrayList();
    SootField field = null;
    try {
      field = soot.jbco.jimpleTransformations.FieldRenamer.getRandomOpaques()[Rand.getInt(2)];
    } catch (NullPointerException npe) {}
    
    if (field != null && Rand.getInt(3) > 0) {
      toinsert.add(Baf.v().newStaticGetInst(field.makeRef()));
      if (field.getType() instanceof IntegerType) {
        toinsert.add(Baf.v().newIfGeInst((Unit)units.getSuccOf(nonTrap)));
      } else {
        SootMethod boolInit = ((RefType)field.getType()).getSootClass().getMethod("boolean booleanValue()");
        toinsert.add(Baf.v().newVirtualInvokeInst(boolInit.makeRef()));
        toinsert.add(Baf.v().newIfGeInst((Unit)units.getSuccOf(nonTrap)));
      }
    } else {
      toinsert.add(Baf.v().newPushInst(soot.jimple.IntConstant.v(BodyBuilder.getIntegerNine())));
      toinsert.add(Baf.v().newPrimitiveCastInst(IntType.v(),ByteType.v()));
      toinsert.add(Baf.v().newPushInst(soot.jimple.IntConstant.v(Rand.getInt() % 2 == 0 ? 9 : 3)));
      toinsert.add(Baf.v().newRemInst(ByteType.v()));
      
      /*toinsert.add(Baf.v().newDup1Inst(ByteType.v()));
      toinsert.add(Baf.v().newPrimitiveCastInst(ByteType.v(),IntType.v()));
      toinsert.add(Baf.v().newStaticGetInst(sys.getFieldByName("out").makeRef()));
      toinsert.add(Baf.v().newSwapInst(IntType.v(),RefType.v()));
      ArrayList parms = new ArrayList();
      parms.add(IntType.v());
      toinsert.add(Baf.v().newVirtualInvokeInst(out.getMethod("println",parms).makeRef()));
      */
      toinsert.add(Baf.v().newIfEqInst((Unit)units.getSuccOf(nonTrap)));
    }

    ArrayList toinserttry = new ArrayList();
    while (stack.size()>0)
      toinserttry.add(Baf.v().newPopInst((Type)stack.pop()));
    toinserttry.add(Baf.v().newPushInst(soot.jimple.NullConstant.v()));
    
    Unit handler = Baf.v().newThrowInst();
    int rand = Rand.getInt(toinserttry.size());
    while (rand++ < toinserttry.size()) {
      toinsert.add(toinserttry.get(0));
      toinserttry.remove(0);
    }
    if (toinserttry.size()>0) {
      toinserttry.add(Baf.v().newGotoInst(handler));
      toinsert.add(Baf.v().newGotoInst((Inst)toinserttry.get(0)));
      units.insertBefore(toinserttry,nop);
    }
    
    toinsert.add(handler);
    units.insertAfter(toinsert,nonTrap);
    
    b.getTraps().add(Baf.v().newTrap(ThrowSet.getRandomThrowable(),(Unit)addedUnits.get(0),nop,handler));
    
    count+=addedUnits.size();
    if (addedUnits.size() > 0 && debug) {
      StackTypeHeightCalculator.calculateStackHeights(b);
      //StackTypeHeightCalculator.printStack(units, StackTypeHeightCalculator.calculateStackHeights(b), false);
    }
  }
  
  private Unit findNonTrappedUnit(PatchingChain units, soot.util.Chain traps) {
    int intrap = 0;
    ArrayList untrapped = new ArrayList();
    Iterator it = units.snapshotIterator();
    while (it.hasNext()) {
      Unit u = (Unit)it.next();
      Iterator tit = traps.iterator();
      while (tit.hasNext()) {
        Trap t = (Trap)tit.next();
        if (u == t.getBeginUnit()) intrap++;
        if (u == t.getEndUnit()) intrap--;
      }
      
      if (intrap == 0) untrapped.add(u);
    }
    
    Unit result = null;
    if (untrapped.size() > 0) {
      int count = 0;
      while (result == null && count <10) {
        count++;
        result = (Unit)untrapped.get(Rand.getInt(999999) % untrapped.size());
        if (!result.fallsThrough() || units.getSuccOf(result) == null || units.getSuccOf(result) instanceof ThrowInst) 
          result = null;
      }
    }
    
    return result;
  }
  
  private boolean isIf(Unit u) {
    // TODO: will a RET statement be a TargetArgInst? 
    return (u instanceof TargetArgInst) && !(u instanceof GotoInst)
        && !(u instanceof JSRInst);
  }
}
