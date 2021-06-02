package soot.jimple.toolkits.pointer;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Ondrej Lhotak
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

import java.util.HashMap;
import java.util.List;

import soot.G;
import soot.Local;
import soot.PointsToAnalysis;
import soot.PointsToSet;
import soot.Scene;
import soot.SideEffectTester;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.ArrayRef;
import soot.jimple.Constant;
import soot.jimple.Expr;
import soot.jimple.InstanceFieldRef;
import soot.jimple.StaticFieldRef;
import soot.jimple.Stmt;

//  ArrayRef, 
//  CaughtExceptionRef, 
//  FieldRef, 
//  IdentityRef, 
//  InstanceFieldRef, 
//  InstanceInvokeExpr, 
//  Local,  
//  StaticFieldRef

public class PASideEffectTester implements SideEffectTester {

  private final PointsToAnalysis pa = Scene.v().getPointsToAnalysis();
  private final SideEffectAnalysis sea = Scene.v().getSideEffectAnalysis();
  private HashMap<Unit, RWSet> unitToRead;
  private HashMap<Unit, RWSet> unitToWrite;
  private HashMap<Local, PointsToSet> localToReachingObjects;
  private SootMethod currentMethod;

  public PASideEffectTester() {
    if (G.v().Union_factory == null) {
      G.v().Union_factory = new UnionFactory() {
        @Override
        public Union newUnion() {
          return FullObjectSet.v();
        }
      };
    }
  }

  /** Call this when starting to analyze a new method to setup the cache. */
  @Override
  public void newMethod(SootMethod m) {
    this.unitToRead = new HashMap<Unit, RWSet>();
    this.unitToWrite = new HashMap<Unit, RWSet>();
    this.localToReachingObjects = new HashMap<Local, PointsToSet>();
    this.currentMethod = m;
    this.sea.findNTRWSets(m);
  }

  protected RWSet readSet(Unit u) {
    RWSet ret = unitToRead.get(u);
    if (ret == null) {
      unitToRead.put(u, ret = sea.readSet(currentMethod, (Stmt) u));
    }
    return ret;
  }

  protected RWSet writeSet(Unit u) {
    RWSet ret = unitToWrite.get(u);
    if (ret == null) {
      unitToWrite.put(u, ret = sea.writeSet(currentMethod, (Stmt) u));
    }
    return ret;
  }

  protected PointsToSet reachingObjects(Local l) {
    PointsToSet ret = localToReachingObjects.get(l);
    if (ret == null) {
      localToReachingObjects.put(l, ret = pa.reachingObjects(l));
    }
    return ret;
  }

  /**
   * Returns true if the unit can read from v. Does not deal with expressions; deals with Refs.
   */
  @Override
  public boolean unitCanReadFrom(Unit u, Value v) {
    return valueTouchesRWSet(readSet(u), v, u.getUseBoxes());
  }

  /**
   * Returns true if the unit can read from v. Does not deal with expressions; deals with Refs.
   */
  @Override
  public boolean unitCanWriteTo(Unit u, Value v) {
    return valueTouchesRWSet(writeSet(u), v, u.getDefBoxes());
  }

  protected boolean valueTouchesRWSet(RWSet s, Value v, List<ValueBox> boxes) {
    for (ValueBox use : v.getUseBoxes()) {
      if (valueTouchesRWSet(s, use.getValue(), boxes)) {
        return true;
      }
    }
    // This doesn't really make any sense, but we need to return something.
    if (v instanceof Constant) {
      return false;
    } else if (v instanceof Expr) {
      throw new RuntimeException("can't deal with expr");
    }

    for (ValueBox box : boxes) {
      if (box.getValue().equivTo(v)) {
        return true;
      }
    }

    if (v instanceof Local) {
      return false;
    } else if (v instanceof InstanceFieldRef) {
      if (s == null) {
        return false;
      }
      InstanceFieldRef ifr = (InstanceFieldRef) v;
      PointsToSet o1 = s.getBaseForField(ifr.getField());
      if (o1 == null) {
        return false;
      }
      PointsToSet o2 = reachingObjects((Local) ifr.getBase());
      if (o2 == null) {
        return false;
      }
      return o1.hasNonEmptyIntersection(o2);
    } else if (v instanceof ArrayRef) {
      if (s == null) {
        return false;
      }
      PointsToSet o1 = s.getBaseForField(PointsToAnalysis.ARRAY_ELEMENTS_NODE);
      if (o1 == null) {
        return false;
      }
      ArrayRef ar = (ArrayRef) v;
      PointsToSet o2 = reachingObjects((Local) ar.getBase());
      if (o2 == null) {
        return false;
      }
      return o1.hasNonEmptyIntersection(o2);
    } else if (v instanceof StaticFieldRef) {
      if (s == null) {
        return false;
      }
      StaticFieldRef sfr = (StaticFieldRef) v;
      return s.getGlobals().contains(sfr.getField());
    }

    throw new RuntimeException("Forgot to handle value " + v);
  }
}
