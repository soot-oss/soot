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
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.baf.JSRInst;
import soot.baf.TableSwitchInst;
import soot.baf.TargetArgInst;
import soot.Body;
import soot.PatchingChain;
import soot.Trap;
import soot.Unit;

public class Debugger {
	final static Logger logger = LoggerFactory.getLogger(Debugger.class);

  public static void printBaf(Body b) {
    
    logger.debug("{}\n",b.getMethod().getName());
    int i = 0;
    Map<Unit,Integer> index = new HashMap<Unit,Integer>();
    Iterator<Unit> it = b.getUnits().iterator();
    while (it.hasNext())
      index.put(it.next(),new Integer(i++));
    it = b.getUnits().iterator();
    while (it.hasNext()) {
      Object o = it.next();
      String s = (o instanceof TargetArgInst ?  index.get(((TargetArgInst)o).getTarget()).toString() : "");
      logger.debug("{} {} {}",index.get(o).toString(),o, s);
    }
    logger.debug("\n");
  }
  
  public static void printUnits(Body b, String msg) {
    int i = 0;
    Map<Unit,Integer> numbers = new HashMap<Unit,Integer>();
    PatchingChain<Unit> u = b.getUnits();
    Iterator<Unit> it = u.snapshotIterator();
    while (it.hasNext())
      numbers.put(it.next(),new Integer(i++));
    
    int jsr = 0;
    logger.debug("\r\r{}  {}",b.getMethod().getName(),msg);
    Iterator<Unit> udit = u.snapshotIterator();
    while (udit.hasNext()) {
      Unit unit = (Unit)udit.next();
      Integer numb = numbers.get(unit);
      
      if (numb.intValue() == 149) 
        logger.debug("hi");
      
      if (unit instanceof TargetArgInst) {
        if(unit instanceof JSRInst) jsr++;
        TargetArgInst ti = (TargetArgInst)unit;
        if (ti.getTarget() == null)
        {
          logger.debug("{} null null null null null null null null null", unit);
          continue;
        }
        logger.debug(numbers.get(unit).toString() + " " + unit + "   #"+ numbers.get(ti.getTarget()).toString());
        continue;
      } else if (unit instanceof TableSwitchInst) {
        TableSwitchInst tswi = (TableSwitchInst)unit;
        logger.debug("{} SWITCH:",numbers.get(unit).toString());
        logger.debug("\tdefault: {}  {}",tswi.getDefaultTarget(),numbers.get(tswi.getDefaultTarget()).toString());
        int index = 0;
        for (int x = tswi.getLowIndex(); x <= tswi.getHighIndex(); x++)
          logger.debug("\t {}: {} {}  ",x, tswi.getTarget(index), numbers.get(tswi.getTarget(index++)).toString());
        continue;
      }
      logger.debug(numb.toString() + " " + unit);
    }
    
    Iterator<Trap> tit = b.getTraps().iterator();
    while (tit.hasNext()) {
      Trap t = tit.next();
      logger.debug("{} {} to {}  {} at {} {}", numbers.get(t.getBeginUnit()), t.getBeginUnit(), numbers.get(t.getEndUnit()), t.getEndUnit(), numbers.get(t.getHandlerUnit()), t.getHandlerUnit());
    }
    if (jsr>0) logger.debug("\r\tJSR Instructions: {}",jsr);
  }
  
  public static void printUnits(PatchingChain<Unit> u, String msg) {
  int i = 0;
  HashMap<Unit,Integer> numbers = new HashMap<Unit,Integer>();
  Iterator<Unit> it = u.snapshotIterator();
  while (it.hasNext())
    numbers.put(it.next(),new Integer(i++));
  
  logger.debug("\r\r***********  {}",msg);
  Iterator<Unit> udit = u.snapshotIterator();
  while (udit.hasNext()) {
    Unit unit = (Unit)udit.next();
    Integer numb = numbers.get(unit);
    
    if (numb.intValue() == 149) 
      logger.debug("hi");
    
    if (unit instanceof TargetArgInst) {
      TargetArgInst ti = (TargetArgInst)unit;
      if (ti.getTarget() == null)
      {
        logger.debug(unit + " null null null null null null null null null");
        continue;
      }
      logger.debug("{} {}  #{}",numbers.get(unit).toString(),unit,  numbers.get(ti.getTarget()).toString());
      continue;
    } else if (unit instanceof TableSwitchInst) {
      TableSwitchInst tswi = (TableSwitchInst)unit;
      logger.debug("{} SWITCH:", numbers.get(unit).toString());
      logger.debug("\tdefault: {} {}", tswi.getDefaultTarget(), numbers.get(tswi.getDefaultTarget()).toString());
      int index = 0;
      for (int x = tswi.getLowIndex(); x <= tswi.getHighIndex(); x++)
        logger.debug("\t {}: {}  {}",x,tswi.getTarget(index), numbers.get(tswi.getTarget(index++)).toString());
      continue;
    }
    logger.debug("{} {}",numb.toString(), unit);
  }
  }
}
