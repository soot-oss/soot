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

package soot.jbco.jimpleTransformations;

import soot.*;
import soot.util.*;
import soot.jbco.IJbcoTransform;
import soot.jbco.util.Rand;
import soot.jimple.*;
import java.util.*;

/**
 * @author Michael Batchelder 
 * 
 * Created on 15-Feb-2006 
 */
public class GotoInstrumenter extends BodyTransformer implements IJbcoTransform {

  private int trapsAdded = 0;
  private int gotosInstrumented = 0;

  public static String dependancies[] = new String[] { "jtp.jbco_gia" };

  public String[] getDependancies() {
    return dependancies;
  }
  
  public static String name = "jtp.jbco_gia";
  
  public String getName() {
    return name;
  }
  
  public void outputSummary() {
    out.println("Gotos Instrumented "+gotosInstrumented);
    out.println("Traps Added "+trapsAdded);
  }

  static boolean verbose = G.v().soot_options_Options().verbose();
  
  protected void internalTransform(Body b, String phaseName, Map<String,String> options) 
  { 
    if (b.getMethod().getName().indexOf("<init>")>=0) return;
    
    int weight = soot.jbco.Main.getWeight(phaseName, b.getMethod().getSignature());
    if (weight == 0) return;
    
    PatchingChain<Unit> units = b.getUnits();
    int size = units.size();
    Unit first = null;
    Iterator<Unit> uit = units.iterator();
    while (uit.hasNext()) {
      Unit o = uit.next();
      if (o instanceof IdentityStmt) {
        first=o;
        size--;
      } else
        break;
    }
    
    if (size < 8) return;
    
    if (first == null)
      first = (Unit)units.getFirst();
    
    Chain<Trap> traps = b.getTraps();
    int i = 0, rand = 0;
    while (i++ < 10)
    {
      rand = Rand.getInt(size);
      if (rand<1) 
        rand = 1;
      else if (rand == size - 1)
        rand = size - 2;
     
      if (isExceptionCaughtAt(units, rand + (units.size() - size), traps.iterator()))
        continue;
      break;
    }
    
    // if 10 tries, we give up
    if (i>=10) return;
    
    
    i = 0;
    
    if (output) {
    	out.println("Applying Gotos to "+b.getMethod().getName());
    }
 
    
    /*Iterator it = units.iterator();
    while(it.hasNext()) {
      Unit x = (Unit)it.next();
      System.out.println(i+++":  "+x.toString() + "  : "+isExceptionCaughtAt(units, x,traps.iterator()));
    }*/
  
    // move random-size chunk at beginning to end
    first = (Unit)units.getSuccOf(first);
    Unit u = first;
    do {
      Object toU[] = u.getBoxesPointingToThis().toArray();
      for (Object element : toU)
		u.removeBoxPointingToThis((UnitBox)element);
      
      // unit box targets stay with a unit even if the unit is removed.
      Unit u2 = (Unit)units.getSuccOf(u);
      units.remove(u);
      units.add(u);
      
      for (Object element : toU)
		u.addBoxPointingToThis((UnitBox)element);
      
      u = u2;
    } while (++i < rand);

    Unit oldFirst = first;
    // add goto as FIRST unit to point to new chunk location    
    if (first instanceof GotoStmt) {
      oldFirst = ((GotoStmt)first).getTargetBox().getUnit();
      first = Jimple.v().newGotoStmt(((GotoStmt)first).getTargetBox().getUnit());
    } else
      first = Jimple.v().newGotoStmt(first);
    units.insertBeforeNoRedirect(first,u);

    // add goto as LAST unit to point to new position of second chunk
    if (((Unit)units.getLast()).fallsThrough()) {
      Stmt gtS = null;
      if (u instanceof GotoStmt)
        gtS = Jimple.v().newGotoStmt(((GotoStmt)u).getTargetBox().getUnit());
      else
        gtS = Jimple.v().newGotoStmt(u);
    
      units.add(gtS);
    }
    
    RefType throwable = G.v().soot_Scene().getRefType("java.lang.Throwable");
    CaughtExceptionRef cexc = Jimple.v().newCaughtExceptionRef();
    Local excLocal = Jimple.v().newLocal("jbco_gi_caughtExceptionLocal", throwable);
    b.getLocals().add(excLocal);
    
    Unit handler = Jimple.v().newIdentityStmt(excLocal,cexc);
    units.add(handler);
    units.add(Jimple.v().newThrowStmt(excLocal));
    
    Unit trapEnd = (Unit)units.getSuccOf(oldFirst);
    try {
	    while (trapEnd instanceof IdentityStmt)
	      trapEnd = (Unit)units.getSuccOf(trapEnd);
	    trapEnd = (Unit)units.getSuccOf(trapEnd);
	    b.getTraps().add(Jimple.v().newTrap(throwable.getSootClass(), (Unit)units.getPredOf(oldFirst), trapEnd, handler));
	    trapsAdded++;
    } catch (Exception exc) {}
    gotosInstrumented++;
  }
  
  private boolean isExceptionCaughtAt(Chain<Unit> units, int idx, Iterator<Trap> trapsIt)
  {
    Object u = null;
    Iterator<Unit> it = units.iterator();
    while (it.hasNext())
    {
      if (idx--==0) {
        u = it.next();
        break;
      }
      it.next();
    }
    
    if (u == null)
    	return false;
  
    //System.out.println("\r\tselected unit is "+u);
    while (trapsIt.hasNext())
    {
      Trap t = (Trap)trapsIt.next();
      it = units.iterator(t.getBeginUnit(),units.getPredOf(t.getEndUnit()));
      while (it.hasNext())
        if (u.equals(it.next()))
          return true;
      if (t.getEndUnit().equals(u))
        return true;
    }
    
    return false;
  }
}