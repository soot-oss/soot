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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import soot.Local;
import soot.PatchingChain;
import soot.RefType;
import soot.SootClass;
import soot.SootMethod;
import soot.SootField;
import soot.Trap;
import soot.Type;
import soot.Unit;
import soot.baf.IfCmpEqInst;
import soot.baf.IfCmpGeInst;
import soot.baf.IfCmpGtInst;
import soot.baf.IfCmpLeInst;
import soot.baf.IfCmpLtInst;
import soot.baf.IfCmpNeInst;
import soot.baf.IfEqInst;
import soot.baf.IfGeInst;
import soot.baf.IfGtInst;
import soot.baf.IfLeInst;
import soot.baf.IfLtInst;
import soot.baf.IfNeInst;
import soot.baf.IfNonNullInst;
import soot.baf.IfNullInst;
import soot.jimple.Jimple;
import soot.jimple.ThisRef;
import soot.util.Chain;

/**
 * @author Michael Batchelder 
 * 
 * Created on 7-Feb-2006 
 */
public class BodyBuilder {

  public static boolean bodiesHaveBeenBuilt = false;
  public static boolean namesHaveBeenRetrieved = false;
  public static ArrayList nameList = new ArrayList();
  public static void retrieveAllBodies() 
  {
    if (bodiesHaveBeenBuilt) return;
      
    //  iterate through application classes, rename fields with junk
    Iterator it = soot.Scene.v().getApplicationClasses().iterator();
    while (it.hasNext())
    {
      SootClass c = (SootClass)it.next();

      Iterator mIt = c.getMethods().iterator();
      while (mIt.hasNext())
      {
        SootMethod m = (SootMethod)mIt.next();
        if (!m.isConcrete()) continue;
        
        if (!m.hasActiveBody())
          m.retrieveActiveBody();
      }
    }
    
    bodiesHaveBeenBuilt = true;
  }
  
  public static void retrieveAllNames() 
  {
    if (namesHaveBeenRetrieved) return;
      
    //  iterate through application classes, rename fields with junk
    Iterator it = soot.Scene.v().getApplicationClasses().iterator();
    while (it.hasNext())
    {
      SootClass c = (SootClass)it.next();
      nameList.add(c.getName());
      
      Iterator _it = c.getMethods().iterator();
      while (_it.hasNext())
      {
        SootMethod m = (SootMethod)_it.next();
        nameList.add(m.getName());
      }
      _it = c.getFields().iterator();
      while (_it.hasNext())
      {
        SootField f = (SootField)_it.next();
        nameList.add(f.getName());
      }
    }
    
    namesHaveBeenRetrieved = true;
  }
  
  public static Local buildThisLocal(PatchingChain units, ThisRef tr, Chain locals)
  {
    Local ths = Jimple.v().newLocal("ths", tr.getType());
    locals.add(ths);
    units.add(Jimple.v().newIdentityStmt(ths,
        Jimple.v().newThisRef((RefType) tr.getType())));
    return ths;
  }
  
  public static ArrayList buildParameterLocals(PatchingChain units, Chain locals, List paramTypes)
  {
    ArrayList args = new ArrayList();
    for (int k = 0; k < paramTypes.size(); k++) {
      Type type = (Type) paramTypes.get(k);
      Local loc = Jimple.v().newLocal("l" + k, type);
      locals.add(loc);

      units.add(Jimple.v().newIdentityStmt(loc,
          Jimple.v().newParameterRef(type, k)));

      args.add(loc);
    }
    return args;
  }
  
  public static void updateTraps(Unit oldu, Unit newu, Chain traps) {
    int size = traps.size();
    if (size == 0) return;
    
    Trap t = (Trap)traps.getFirst();
    do {
      if (t.getBeginUnit() == oldu)
        t.setBeginUnit(newu);
      if (t.getEndUnit() == oldu)
        t.setEndUnit(newu);
      if (t.getHandlerUnit() == oldu)
        t.setHandlerUnit(newu);
    } while ((--size > 0) && (t = (Trap)traps.getSuccOf(t)) != null);
  }
  
  public static boolean isExceptionCaughtAt(Chain units, Unit u, Iterator trapsIt)
  {
    while (trapsIt.hasNext())
    {
      Trap t = (Trap)trapsIt.next();
      Iterator it = units.iterator(t.getBeginUnit(),units.getPredOf(t.getEndUnit()));
      while (it.hasNext())
        if (u.equals(it.next()))
          return true;
    }
    
    return false;
  }
  
  public static int getIntegerNine() {
    int r1 = Rand.getInt(8388606) * 256;
    
    int r2 = Rand.getInt(28) * 9;
    
    if (r2 > 126)
      r2 += 4; 
    
    return r1 + r2;
  }
  
  public static boolean isBafIf(Unit u) {
    if (u instanceof IfCmpEqInst || u instanceof IfCmpGeInst
        || u instanceof IfCmpGtInst || u instanceof IfCmpLeInst
        || u instanceof IfCmpLtInst || u instanceof IfCmpNeInst
        || u instanceof IfEqInst || u instanceof IfGeInst
        || u instanceof IfGtInst || u instanceof IfLeInst
        || u instanceof IfLtInst || u instanceof IfNeInst
        || u instanceof IfNonNullInst || u instanceof IfNullInst)
      return true;
    return false;
  }
}
