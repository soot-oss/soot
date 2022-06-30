package soot.jimple.toolkits.thread.synchronization;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2018 Raja Vallée-Rai and others
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

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import soot.Body;
import soot.G;
import soot.Scene;
import soot.Unit;
import soot.jimple.Stmt;
import soot.jimple.toolkits.pointer.FullObjectSet;
import soot.jimple.toolkits.pointer.RWSet;
import soot.jimple.toolkits.pointer.SideEffectAnalysis;
import soot.jimple.toolkits.pointer.Union;
import soot.jimple.toolkits.pointer.UnionFactory;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.ArraySparseSet;
import soot.toolkits.scalar.BackwardFlowAnalysis;
import soot.toolkits.scalar.FlowSet;

/**
 * @author Richard L. Halpert StrayRWFinder - Analysis to locate reads/writes to shared data that appear outside
 *         synchronization
 */
public class StrayRWFinder extends BackwardFlowAnalysis {
  FlowSet emptySet = new ArraySparseSet();
  Map unitToGenerateSet;
  Body body;
  SideEffectAnalysis sea;
  List tns;

  StrayRWFinder(UnitGraph graph, Body b, List tns) {
    super(graph);
    body = b;
    this.tns = tns;
    if (G.v().Union_factory == null) {
      G.v().Union_factory = new UnionFactory() {
        public Union newUnion() {
          return FullObjectSet.v();
        }
      };
    }
    sea = Scene.v().getSideEffectAnalysis();
    sea.findNTRWSets(body.getMethod());
    doAnalysis();
  }

  /**
   * All INs are initialized to the empty set.
   **/
  protected Object newInitialFlow() {
    return emptySet.clone();
  }

  /**
   * IN(Start) is the empty set
   **/
  protected Object entryInitialFlow() {
    return emptySet.clone();
  }

  /**
   * OUT is the same as (IN minus killSet) plus the genSet.
   **/
  protected void flowThrough(Object inValue, Object unit, Object outValue) {
    FlowSet in = (FlowSet) inValue, out = (FlowSet) outValue;

    RWSet stmtRead = sea.readSet(body.getMethod(), (Stmt) unit);
    RWSet stmtWrite = sea.writeSet(body.getMethod(), (Stmt) unit);

    Boolean addSelf = Boolean.FALSE;

    Iterator tnIt = tns.iterator();
    while (tnIt.hasNext()) {
      CriticalSection tn = (CriticalSection) tnIt.next();
      if (stmtRead.hasNonEmptyIntersection(tn.write) || stmtWrite.hasNonEmptyIntersection(tn.read)
          || stmtWrite.hasNonEmptyIntersection(tn.write)) {
        addSelf = Boolean.TRUE;
      }
    }

    in.copy(out);
    if (addSelf.booleanValue()) {
      CriticalSection tn = new CriticalSection(false, body.getMethod(), 0);
      tn.entermonitor = (Stmt) unit;
      tn.units.add((Unit) unit);
      tn.read.union(stmtRead);
      tn.write.union(stmtWrite);
      out.add(tn);
    }
  }

  /**
   * union, except for transactions in progress. They get joined
   **/
  protected void merge(Object in1, Object in2, Object out) {
    FlowSet inSet1 = ((FlowSet) in1).clone(), inSet2 = ((FlowSet) in2).clone(), outSet = (FlowSet) out;
    /*
     * boolean hasANull1 = false; Transaction tn1 = null; Iterator inIt1 = inSet1.iterator(); while(inIt1.hasNext()) { tn1 =
     * (Transaction) inIt1.next(); if(tn1.entermonitor == null) { hasANull1 = true; break; } }
     *
     * boolean hasANull2 = false; Transaction tn2 = null; Iterator inIt2 = inSet2.iterator(); while(inIt2.hasNext()) { tn2 =
     * (Transaction) inIt2.next(); if(tn2.entermonitor == null) { hasANull2 = true; break; } } if(hasANull1 && hasANull2) {
     * inSet1.remove(tn1); Iterator itends = tn1.exitmonitors.iterator(); while(itends.hasNext()) { Stmt stmt = (Stmt)
     * itends.next(); if(!tn2.exitmonitors.contains(stmt)) tn2.exitmonitors.add(stmt); } tn2.read.union(tn1.read);
     * tn2.write.union(tn1.write); }
     */
    inSet1.union(inSet2, outSet);
  }

  protected void copy(Object source, Object dest) {
    FlowSet sourceSet = (FlowSet) source, destSet = (FlowSet) dest;

    sourceSet.copy(destSet);
  }
}
