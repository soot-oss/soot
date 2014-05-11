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
import soot.jbco.util.*;
import soot.jbco.*;
import soot.baf.*;
import soot.jimple.NullConstant;
/**
 * @author Michael Batchelder 
 * 
 * Created on 20-Jun-2006 
 */
public class IfNullToTryCatch extends BodyTransformer implements IJbcoTransform {

  int count = 0;
  int totalifs = 0;
  
  public static String dependancies[] = new String[] {"bb.jbco_riitcb","bb.jbco_ful","bb.lp"};

  public String[] getDependancies() {
    return dependancies;
  }
  
  public static String name = "bb.jbco_riitcb";
  
  public String getName() {
    return name;
  }
  
  public void outputSummary() {
    out.println("If(Non)Nulls changed to traps: "+ count);
    out.println("Total ifs found: "+ totalifs);
  }
  
  protected void internalTransform(Body b, String phaseName, Map<String,String> options) {
    int weight = soot.jbco.Main.getWeight(phaseName, b.getMethod().getSignature());
    if (weight == 0) return;
    
    SootClass exc = G.v().soot_Scene().getSootClass("java.lang.NullPointerException");
    SootClass obj = G.v().soot_Scene().getSootClass("java.lang.Object");
    SootMethod toStrg = obj.getMethodByName("toString");
    SootMethod eq = obj.getMethodByName("equals");
    boolean change = false;
    PatchingChain<Unit> units = b.getUnits();
    Iterator<Unit> uit = units.snapshotIterator();
    while (uit.hasNext()) {
      Unit u = uit.next();
      if (BodyBuilder.isBafIf(u))   totalifs++;
      
      if (u instanceof IfNullInst && Rand.getInt(10) <= weight) {
        Unit targ = ((IfNullInst)u).getTarget();
        Unit succ = units.getSuccOf(u);
        Unit pop = Baf.v().newPopInst(RefType.v());
        Unit popClone = (Unit)pop.clone();
        units.insertBefore(pop,targ);
        
        Unit gotoTarg = Baf.v().newGotoInst(targ);
        units.insertBefore(gotoTarg,pop);

        if (Rand.getInt(2) == 0) {
          Unit methCall = Baf.v().newVirtualInvokeInst(toStrg.makeRef());
          units.insertBefore(methCall,u);
        
          if (Rand.getInt(2) == 0) {
            units.remove(u);
            units.insertAfter(popClone,methCall);
          }
        
          b.getTraps().add(Baf.v().newTrap(exc, methCall, succ, pop));
        } else {
          Unit throwu = Baf.v().newThrowInst();
          units.insertBefore(throwu, u);
          units.remove(u);
          
          units.insertBefore(Baf.v().newPushInst(NullConstant.v()), throwu);
          Unit ifunit = Baf.v().newIfCmpNeInst(RefType.v(), succ);
          units.insertBefore(ifunit, throwu);
          units.insertBefore(Baf.v().newPushInst(NullConstant.v()), throwu);
          
          b.getTraps().add(Baf.v().newTrap(exc, throwu, succ, pop));
        }
        count++;
        change = true;
      } else if (u instanceof IfNonNullInst && Rand.getInt(10) <= weight) {
        Unit targ = ((IfNonNullInst)u).getTarget();

        Unit methCall = Baf.v().newVirtualInvokeInst(eq.makeRef());
        units.insertBefore(methCall,u);
        units.insertBefore(Baf.v().newPushInst(NullConstant.v()),methCall);
        if (Rand.getInt(2) == 0) {
	      Unit pop = Baf.v().newPopInst(BooleanType.v());
          units.insertBefore(pop, u);
          Unit gotoTarg = Baf.v().newGotoInst(targ);
          units.insertBefore(gotoTarg, u);

          pop = Baf.v().newPopInst(RefType.v());
          units.insertAfter(pop, u);
          units.remove(u);

          // add first, so it is always checked first in the exception table
          b.getTraps().addFirst(Baf.v().newTrap(exc, methCall, gotoTarg, pop));
        } else {
          Unit iffalse = Baf.v().newIfEqInst(targ);
          units.insertBefore(iffalse,u);
          units.insertBefore(Baf.v().newPushInst(NullConstant.v()),u);
          Unit pop = Baf.v().newPopInst(RefType.v());
	      units.insertAfter(pop,u);
	      units.remove(u);
	
          // add first, so it is always checked first in the exception table
	      b.getTraps().addFirst(Baf.v().newTrap(exc, methCall, iffalse, pop));
        }
        count++;
        change = true;
      }
    }
    
    if (change && debug)
      StackTypeHeightCalculator.calculateStackHeights(b);
    //StackTypeHeightCalculator.printStack(units, StackTypeHeightCalculator.calculateStackHeights(b), true)
  }
}
