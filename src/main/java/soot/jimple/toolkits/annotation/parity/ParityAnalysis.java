package soot.jimple.toolkits.annotation.parity;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Jennifer Lhotak
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

import static soot.jimple.toolkits.annotation.parity.ParityAnalysis.Parity.BOTTOM;
import static soot.jimple.toolkits.annotation.parity.ParityAnalysis.Parity.EVEN;
import static soot.jimple.toolkits.annotation.parity.ParityAnalysis.Parity.ODD;
import static soot.jimple.toolkits.annotation.parity.ParityAnalysis.Parity.TOP;
import static soot.jimple.toolkits.annotation.parity.ParityAnalysis.Parity.valueOf;

import java.util.HashMap;
import java.util.Map;

import soot.Body;
import soot.IntegerType;
import soot.Local;
import soot.LongType;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.AddExpr;
import soot.jimple.ArithmeticConstant;
import soot.jimple.BinopExpr;
import soot.jimple.DefinitionStmt;
import soot.jimple.IntConstant;
import soot.jimple.LongConstant;
import soot.jimple.MulExpr;
import soot.jimple.SubExpr;
import soot.options.Options;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.ForwardFlowAnalysis;
import soot.toolkits.scalar.LiveLocals;

// STEP 1: What are we computing?
// SETS OF PAIRS of form (X, T) => Use ArraySparseSet.
//
// STEP 2: Precisely define what we are computing.
// For each statement compute the parity of all variables
// in the program.
//
// STEP 3: Decide whether it is a backwards or forwards analysis.
// FORWARDS
//
public class ParityAnalysis extends ForwardFlowAnalysis<Unit, Map<Value, ParityAnalysis.Parity>> {
  public enum Parity {
    TOP, BOTTOM, EVEN, ODD;

    static Parity valueOf(int v) {
      return (v % 2) == 0 ? EVEN : ODD;
    }

    static Parity valueOf(long v) {
      return (v % 2) == 0 ? EVEN : ODD;
    }
  }

  private final Body body;
  private final LiveLocals filter;

  public ParityAnalysis(UnitGraph g, LiveLocals filter) {
    super(g);
    this.body = g.getBody();
    this.filter = filter;

    filterUnitToBeforeFlow = new HashMap<Unit, Map<Value, Parity>>();
    filterUnitToAfterFlow = new HashMap<Unit, Map<Value, Parity>>();
    buildBeforeFilterMap();

    doAnalysis();
  }

  public ParityAnalysis(UnitGraph g) {
    super(g);
    this.body = g.getBody();
    this.filter = null;

    doAnalysis();
  }

  private void buildBeforeFilterMap() {
    for (Unit s : body.getUnits()) {
      // if (!(s instanceof DefinitionStmt)) continue;
      // Value left = ((DefinitionStmt)s).getLeftOp();
      // if (!(left instanceof Local)) continue;

      // if (!((left.getType() instanceof IntegerType) || (left.getType() instanceof LongType))) continue;

      Map<Value, Parity> map = new HashMap<Value, Parity>();
      for (Local l : filter.getLiveLocalsBefore(s)) {
        map.put(l, BOTTOM);
      }

      filterUnitToBeforeFlow.put(s, map);
    }
    // System.out.println("init filtBeforeMap: "+filterUnitToBeforeFlow);
  }

  // STEP 4: Is the merge operator union or intersection?
  //
  // merge | bottom | even | odd | top
  // -------+--------+--------+-------+--------
  // bottom | bottom | even | odd | top
  // -------+--------+--------+-------+--------
  // even | even | even | top | top
  // -------+--------+--------+-------+--------
  // odd | odd | top | odd | top
  // -------+--------+--------+-------+--------
  // top | top | top | top | top
  //

  @Override
  protected void merge(Map<Value, Parity> inMap1, Map<Value, Parity> inMap2, Map<Value, Parity> outMap) {
    for (Value var1 : inMap1.keySet()) {
      // System.out.println(var1);
      Parity inVal1 = inMap1.get(var1);
      // System.out.println(inVal1);
      Parity inVal2 = inMap2.get(var1);
      // System.out.println(inVal2);
      // System.out.println("before out "+outMap.get(var1));

      if (inVal2 == null) {
        outMap.put(var1, inVal1);
      } else if (BOTTOM.equals(inVal1)) {
        outMap.put(var1, inVal2);
      } else if (BOTTOM.equals(inVal2)) {
        outMap.put(var1, inVal1);
      } else if (EVEN.equals(inVal1) && EVEN.equals(inVal2)) {
        outMap.put(var1, EVEN);
      } else if (ODD.equals(inVal1) && ODD.equals(inVal2)) {
        outMap.put(var1, ODD);
      } else {
        outMap.put(var1, TOP);
      }
    }
  }

  // STEP 5: Define flow equations.
  // in(s) = ( out(s) minus defs(s) ) union uses(s)
  //

  @Override
  protected void copy(Map<Value, Parity> sourceIn, Map<Value, Parity> destOut) {
    destOut.clear();
    destOut.putAll(sourceIn);
  }

  // Parity Tests: even + even = even
  // even + odd = odd
  // odd + odd = even
  //
  // even * even = even
  // even * odd = even
  // odd * odd = odd
  //
  // constants are tested mod 2
  //

  private Parity getParity(Map<Value, Parity> in, Value val) {
    // System.out.println("get Parity in: "+in);
    if ((val instanceof AddExpr) | (val instanceof SubExpr)) {
      Parity resVal1 = getParity(in, ((BinopExpr) val).getOp1());
      Parity resVal2 = getParity(in, ((BinopExpr) val).getOp2());

      if (TOP.equals(resVal1) | TOP.equals(resVal2)) {
        return TOP;
      } else if (BOTTOM.equals(resVal1) | BOTTOM.equals(resVal2)) {
        return BOTTOM;
      } else if (resVal1.equals(resVal2)) {
        return EVEN;
      } else {
        return ODD;
      }
    } else if (val instanceof MulExpr) {
      Parity resVal1 = getParity(in, ((BinopExpr) val).getOp1());
      Parity resVal2 = getParity(in, ((BinopExpr) val).getOp2());
      if (TOP.equals(resVal1) | TOP.equals(resVal2)) {
        return TOP;
      } else if (BOTTOM.equals(resVal1) | BOTTOM.equals(resVal2)) {
        return BOTTOM;
      } else if (resVal1.equals(resVal2)) {
        return resVal1;
      } else {
        return EVEN;
      }
    } else if (val instanceof IntConstant) {
      int value = ((IntConstant) val).value;
      return valueOf(value);
    } else if (val instanceof LongConstant) {
      long value = ((LongConstant) val).value;
      return valueOf(value);
    } else {
      Parity p = in.get(val);
      return (p != null) ? p : TOP;
    }
  }

  @Override
  protected void flowThrough(Map<Value, Parity> in, Unit s, Map<Value, Parity> out) {
    // copy in to out
    out.putAll(in);

    // for each stmt where leftOp is defintionStmt find the parity
    // of rightOp and update parity to EVEN, ODD or TOP

    // boolean useS = false;
    if (s instanceof DefinitionStmt) {
      DefinitionStmt sDefStmt = (DefinitionStmt) s;
      Value left = sDefStmt.getLeftOp();
      if (left instanceof Local) {
        Type leftType = left.getType();
        if ((leftType instanceof IntegerType) || (leftType instanceof LongType)) {
          // useS = true;
          out.put(left, getParity(out, sDefStmt.getRightOp()));
        }
      }
    }

    // get all use and def boxes of s
    // if use or def is int or long constant add their parity
    for (ValueBox next : s.getUseAndDefBoxes()) {
      Value val = next.getValue();
      // System.out.println("val: "+val.getClass());
      if (val instanceof ArithmeticConstant) {
        out.put(val, getParity(out, val));
        // System.out.println("out map: "+out);
      }
    }

    // if (useS){
    if (Options.v().interactive_mode()) {
      buildAfterFilterMap(s);
      updateAfterFilterMap(s);
    }
    // }
  }

  private void buildAfterFilterMap(Unit s) {
    Map<Value, Parity> map = new HashMap<Value, Parity>();
    for (Local local : filter.getLiveLocalsAfter(s)) {
      map.put(local, BOTTOM);
    }
    filterUnitToAfterFlow.put(s, map);
    // System.out.println("built afterflow filter map: "+filterUnitToAfterFlow);
  }

  // STEP 6: Determine value for start/end node, and
  // initial approximation.
  //
  // start node: locals with BOTTOM
  // initial approximation: locals with BOTTOM
  @Override
  protected Map<Value, Parity> entryInitialFlow() {
    /*
     * HashMap initMap = new HashMap();
     *
     * Chain locals = body.getLocals(); Iterator it = locals.iterator(); while (it.hasNext()) { initMap.put(it.next(),
     * BOTTOM); } return initMap;
     */
    return newInitialFlow();
  }

  private void updateBeforeFilterMap() {
    for (Unit s : filterUnitToBeforeFlow.keySet()) {
      Map<Value, Parity> allData = getFlowBefore(s);
      Map<Value, Parity> filterData = filterUnitToBeforeFlow.get(s);
      filterUnitToBeforeFlow.put(s, updateFilter(allData, filterData));
    }
  }

  private void updateAfterFilterMap(Unit s) {
    Map<Value, Parity> allData = getFlowAfter(s);
    Map<Value, Parity> filterData = filterUnitToAfterFlow.get(s);
    filterUnitToAfterFlow.put(s, updateFilter(allData, filterData));
  }

  private Map<Value, Parity> updateFilter(Map<Value, Parity> allData, Map<Value, Parity> filterData) {
    if (allData != null) {
      for (Value v : filterData.keySet()) {
        Parity d = allData.get(v);
        if (d == null) {
          filterData.remove(v);
        } else {
          filterData.put(v, d);
        }
      }
    }
    return filterData;
  }

  @Override
  protected Map<Value, Parity> newInitialFlow() {
    Map<Value, Parity> initMap = new HashMap<Value, Parity>();

    for (Local l : body.getLocals()) {
      Type t = l.getType();
      // System.out.println("next local: "+next);
      if ((t instanceof IntegerType) || (t instanceof LongType)) {
        initMap.put(l, BOTTOM);
      }
    }

    for (ValueBox vb : body.getUseAndDefBoxes()) {
      Value val = vb.getValue();
      if (val instanceof ArithmeticConstant) {
        initMap.put(val, getParity(initMap, val));
      }
    }

    if (Options.v().interactive_mode()) {
      updateBeforeFilterMap();
    }

    return initMap;
  }
}
