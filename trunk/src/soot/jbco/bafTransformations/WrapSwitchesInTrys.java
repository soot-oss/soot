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
import soot.util.*;
import soot.jbco.IJbcoTransform;
import soot.jbco.util.*;
/**
 * @author Michael Batchelder 
 * 
 * Created on 24-May-2006 
 */
public class WrapSwitchesInTrys extends BodyTransformer implements IJbcoTransform {

  int totaltraps = 0;
  
  public static String dependancies[] = new String[] {"bb.jbco_ptss", "bb.jbco_ful", "bb.lp" };

  public String[] getDependancies() {
    return dependancies;
  }
  
  public static String name = "bb.jbco_ptss";
  
  public String getName() {
    return name;
  }
  
  public void outputSummary() {
    out.println("Switches wrapped in Tries: "+totaltraps);
  }
  
  protected void internalTransform(Body b, String phaseName, Map options) 
  {
    int weight = soot.jbco.Main.getWeight(phaseName, b.getMethod().getSignature());
    if (weight == 0) return;
    
    int i = 0;
    Unit handler = null;
    Chain traps = b.getTraps();
    PatchingChain units = b.getUnits();
    Iterator it = units.snapshotIterator();
    while (it.hasNext()) {
      Unit u = (Unit)it.next();
      if (u instanceof TableSwitchInst) {
        TableSwitchInst twi = (TableSwitchInst)u;
        
        if (!BodyBuilder.isExceptionCaughtAt(units,twi,traps.iterator()) && Rand.getInt(10) <= weight) {
	        if (handler==null) {
	          Iterator uit = units.snapshotIterator();
	          while (uit.hasNext()) {
	            Unit uthrow = (Unit)uit.next();
	            if (uthrow instanceof ThrowInst &&
	                !BodyBuilder.isExceptionCaughtAt(units,uthrow,traps.iterator())) {
	              handler = uthrow;
	              break;
	            }
	          }
	          
	          if (handler==null) {
	            handler = Baf.v().newThrowInst();
	            units.add(handler);
	          }
	        }
        
	        int size = 4;
	        Unit succ = (Unit)units.getSuccOf(twi);
	        while (!BodyBuilder.isExceptionCaughtAt(units,succ,traps.iterator()) && size-->0) {
	          Object o = units.getSuccOf(succ);
	          if (o != null) succ = (Unit)o;
	          else break;
	        }
	        
	        traps.add(Baf.v().newTrap(ThrowSet.getRandomThrowable(), twi, succ, handler));
	        i++;
        }
      }
    }
    
    totaltraps+=i;
    if (i>0 && debug) {
        StackTypeHeightCalculator.calculateStackHeights(b);
    }
  }
}
