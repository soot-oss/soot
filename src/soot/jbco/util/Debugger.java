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

package soot.jbco.util;

import java.util.HashMap;
import java.util.Iterator;

import soot.baf.JSRInst;
import soot.baf.TableSwitchInst;
import soot.baf.TargetArgInst;
import soot.Body;
import soot.PatchingChain;
import soot.Trap;
import soot.Unit;

public class Debugger {
  
  public static void printBaf(Body b) {
    
    System.out.println(b.getMethod().getName()+"\n");
    int i = 0;
    HashMap index = new HashMap();
    Iterator it = b.getUnits().iterator();
    while (it.hasNext())
      index.put(it.next(),new Integer(i++));
    it = b.getUnits().iterator();
    while (it.hasNext()) {
      Object o = it.next();
      System.out.println(((Integer)index.get(o)).toString() 
          + " " + o + " " + (o instanceof TargetArgInst ? 
              		((Integer)index.get(((TargetArgInst)o).getTarget())).toString() 
              		: ""));
    }
    System.out.println("\n");
  }
  
  public static void printUnits(Body b, String msg) {
    int i = 0;
    HashMap numbers = new HashMap();
    PatchingChain u = b.getUnits();
    Iterator it = u.snapshotIterator();
    while (it.hasNext())
      numbers.put(it.next(),new Integer(i++));
    
    int jsr = 0;
    System.out.println("\r\r"+b.getMethod().getName() + "  "+msg);
    Iterator udit = u.snapshotIterator();
    while (udit.hasNext()) {
      Unit unit = (Unit)udit.next();
      Integer numb = (Integer)numbers.get(unit);
      
      if (numb.intValue() == 149) 
        System.out.println("hi");
      
      if (unit instanceof TargetArgInst) {
        if(unit instanceof JSRInst) jsr++;
        TargetArgInst ti = (TargetArgInst)unit;
        if (ti.getTarget() == null)
        {
          System.out.println(unit + " null null null null null null null null null");
          continue;
        }
        System.out.println(numbers.get(unit).toString() + " " + unit + "   #"+ numbers.get(ti.getTarget()).toString());
        continue;
      } else if (unit instanceof TableSwitchInst) {
        TableSwitchInst tswi = (TableSwitchInst)unit;
        System.out.println(numbers.get(unit).toString() + " SWITCH:");
        System.out.println("\tdefault: " + tswi.getDefaultTarget() + "  "+numbers.get(tswi.getDefaultTarget()).toString());
        int index = 0;
        for (int x = tswi.getLowIndex(); x <= tswi.getHighIndex(); x++)
          System.out.println("\t "+x+": " + tswi.getTarget(index) + "  "+numbers.get(tswi.getTarget(index++)).toString());
        continue;
      }
      System.out.println(numb.toString() + " " + unit);
    }
    
    Iterator tit = b.getTraps().iterator();
    while (tit.hasNext()) {
      Trap t = (Trap)tit.next();
      System.out.println(numbers.get(t.getBeginUnit())+" "+t.getBeginUnit() + " to "+ numbers.get(t.getEndUnit())+" "+t.getEndUnit() + "  at "+numbers.get(t.getHandlerUnit())+" "+t.getHandlerUnit());
    }
    if (jsr>0) System.out.println("\r\tJSR Instructions: "+jsr);
  }
  
  public static void printUnits(PatchingChain u, String msg) {
  int i = 0;
  HashMap numbers = new HashMap();
  Iterator it = u.snapshotIterator();
  while (it.hasNext())
    numbers.put(it.next(),new Integer(i++));
  
  int jsr = 0;
  System.out.println("\r\r***********  "+msg);
  Iterator udit = u.snapshotIterator();
  while (udit.hasNext()) {
    Unit unit = (Unit)udit.next();
    Integer numb = (Integer)numbers.get(unit);
    
    if (numb.intValue() == 149) 
      System.out.println("hi");
    
    if (unit instanceof TargetArgInst) {
      if(unit instanceof JSRInst) jsr++;
      TargetArgInst ti = (TargetArgInst)unit;
      if (ti.getTarget() == null)
      {
        System.out.println(unit + " null null null null null null null null null");
        continue;
      }
      System.out.println(numbers.get(unit).toString() + " " + unit + "   #"+ numbers.get(ti.getTarget()).toString());
      continue;
    } else if (unit instanceof TableSwitchInst) {
      TableSwitchInst tswi = (TableSwitchInst)unit;
      System.out.println(numbers.get(unit).toString() + " SWITCH:");
      System.out.println("\tdefault: " + tswi.getDefaultTarget() + "  "+numbers.get(tswi.getDefaultTarget()).toString());
      int index = 0;
      for (int x = tswi.getLowIndex(); x <= tswi.getHighIndex(); x++)
        System.out.println("\t "+x+": " + tswi.getTarget(index) + "  "+numbers.get(tswi.getTarget(index++)).toString());
      continue;
    }
    System.out.println(numb.toString() + " " + unit);
  }
  }
}
