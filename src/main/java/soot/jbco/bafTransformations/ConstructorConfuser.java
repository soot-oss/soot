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
import soot.jimple.*;
import soot.jbco.IJbcoTransform;
import soot.jbco.util.*;

public class ConstructorConfuser extends BodyTransformer implements
    IJbcoTransform {

  static int count = 0;
  
  static int instances[] = new int[4];
  
  public static String dependancies[] = new String[] { "bb.jbco_dcc", "bb.jbco_ful", "bb.lp" };

  public String[] getDependancies() {
    return dependancies;
  }
  
  public static String name = "bb.jbco_dcc";
  
  public String getName() {
    return name;
  }

  public void outputSummary() {
    out.println("Constructor methods have been jumbled: " + count);
  }

  @SuppressWarnings("fallthrough")
  protected void internalTransform(Body b, String phaseName, Map<String,String> options) {
    if (!b.getMethod().getSubSignature().equals("void <init>()")) return;
    int weight = soot.jbco.Main.getWeight(phaseName, b.getMethod().getSignature());
    if (weight == 0) return;
    
    SootClass origClass = b.getMethod().getDeclaringClass();
    SootClass c = origClass;
    if (c.hasSuperclass()) {
      c = c.getSuperclass();
    } else {
      c = null;
    }
    
    PatchingChain<Unit> units = b.getUnits();
    Iterator<Unit> it = units.snapshotIterator();
    Unit prev = null;
    SpecialInvokeInst sii = null;
    while (it.hasNext()) {
      Unit u = (Unit)it.next();
      if (u instanceof SpecialInvokeInst) {
        sii = (SpecialInvokeInst)u;
        SootMethodRef smr = sii.getMethodRef();
        if (c == null || !smr.declaringClass().getName().equals(c.getName()) || !smr.name().equals("<init>")) {
          sii = null;
        } else {
          break;
        }
      }
      prev = u;
    }
    
    if (sii == null) return;
    
    int lowi = -1, lowest = 99999999, rand = Rand.getInt(4);
    for (int i = 0; i < instances.length; i++)
      if (lowest>instances[i]) {
        lowest = instances[i];
        lowi = i;
      }
    if (instances[rand]>instances[lowi])
      rand = lowi;
    
    boolean done = false;
    switch (rand) {
    case 0:
      if (prev != null && prev instanceof LoadInst &&
        sii.getMethodRef().parameterTypes().size() == 0 &&
        !BodyBuilder.isExceptionCaughtAt(units, sii, b.getTraps().iterator())) {
        
        Local bl = ((LoadInst)prev).getLocal();
        Map<Local,Local> locals = soot.jbco.Main.methods2Baf2JLocals.get(b.getMethod());
        if (locals != null && locals.containsKey(bl)) {
          Type t = ((Local)locals.get(bl)).getType();
          if (t instanceof RefType && ((RefType)t).getSootClass().getName().equals(origClass.getName())) {
            units.insertBefore(Baf.v().newDup1Inst(RefType.v()), sii);
            Unit ifinst = Baf.v().newIfNullInst(sii);
            units.insertBeforeNoRedirect(ifinst, sii);
            units.insertAfter(Baf.v().newThrowInst(), ifinst);
            units.insertAfter(Baf.v().newPushInst(NullConstant.v()), ifinst);
          
            Unit pop = Baf.v().newPopInst(RefType.v());
            units.add(pop);
            units.add((Unit)prev.clone());
            b.getTraps().add(Baf.v().newTrap(ThrowSet.getRandomThrowable(), ifinst, sii, pop));
            if (Rand.getInt() % 2 == 0) {
              pop = (Unit)pop.clone();
              units.insertBefore(pop,sii);
              units.insertBefore(Baf.v().newGotoInst(sii),pop);
              units.add(Baf.v().newJSRInst(pop));
            } else
              units.add(Baf.v().newGotoInst(sii));
            done = true;
            break;
          }
        }
      }
    case 1:
      if (!BodyBuilder.isExceptionCaughtAt(units, sii, b.getTraps().iterator())) {
        Unit handler = Baf.v().newThrowInst();
        units.add(handler);
        b.getTraps().add(Baf.v().newTrap(ThrowSet.getRandomThrowable(), sii, (Unit)units.getSuccOf(sii), handler));
        done = true;
        break;
      }
    case 2:
      if (sii.getMethodRef().parameterTypes().size() == 0 && !BodyBuilder.isExceptionCaughtAt(units, sii, b.getTraps().iterator())) {
        while (c != null) {
          if (c.getName().equals("java.lang.Throwable")) {
            Unit throwThis = Baf.v().newThrowInst();
            units.insertBefore(throwThis, sii);
            b.getTraps().add(Baf.v().newTrap(origClass, throwThis, sii, sii));
            done = true;
            break;
          }
        
          if (c.hasSuperclass())
            c = c.getSuperclass();
          else c = null;
        }
      }
      if (done)
        break;
    case 3:
      Unit pop = Baf.v().newPopInst(RefType.v());
      units.insertBefore(pop,sii);
      units.insertBeforeNoRedirect(Baf.v().newJSRInst(pop),pop);
      done = true;
      break;
    }
    
    if (done) {
      instances[rand]++;
      count++;
    }
    
    if (debug)
      StackTypeHeightCalculator.calculateStackHeights(b);
  }
}
