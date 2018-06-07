package soot.jimple.toolkits.thread.mhp;

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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Timers;
import soot.jimple.toolkits.thread.mhp.stmt.JPegStmt;
import soot.tagkit.Tag;
import soot.toolkits.scalar.ArraySparseSet;
import soot.toolkits.scalar.FlowSet;
import soot.toolkits.scalar.ForwardFlowAnalysis;

// *** USE AT YOUR OWN RISK ***
// May Happen in Parallel (MHP) analysis by Lin Li.
// This code should be treated as beta-quality code.
// It was written in 2003, but not incorporated into Soot until 2006.
// As such, it may contain incorrect assumptions about the usage
// of certain Soot classes.
// Some portions of this MHP analysis have been quality-checked, and are
// now used by the Transactions toolkit.
//
// -Richard L. Halpert, 2006-11-30

//STEP 1: What are we computing?
//SETS OF STMTS INSIDE MONITORS => Use MonitorSet.
//
//STEP 2: Precisely define what we are computing.
//Set of objects inside a monitor reaches a program point.
//
//STEP 3: Decide whether it is a backwards or forwards analysis.
//FORWARDS
//
public class MonitorAnalysis extends ForwardFlowAnalysis {
  private static final Logger logger = LoggerFactory.getLogger(MonitorAnalysis.class);

  private PegGraph g;
  private final HashMap<String, FlowSet> monitor = new HashMap<String, FlowSet>();
  private final Vector<Object> nodes = new Vector<Object>();
  private final Vector<Object> valueBefore = new Vector<Object>();
  private final Vector<Object> valueAfter = new Vector<Object>();

  public MonitorAnalysis(PegGraph g) {
    super(g);
    this.g = g;
    doAnalysis();
    // computeSynchNodes();
    g.setMonitor(monitor);
    // testMonitor();
  }

  protected void doAnalysis() {
    LinkedList<Object> changedUnits = new LinkedList<Object>();
    HashSet<Object> changedUnitsSet = new HashSet<Object>();

    int numNodes = graph.size();
    int numComputations = 0;

    // Set initial values and nodes to visit.

    createWorkList(changedUnits, changedUnitsSet);

    // testWorkList(changedUnits);

    // Set initial values for entry points
    {
      Iterator it = graph.getHeads().iterator();

      while (it.hasNext()) {
        Object s = it.next();

        // unitToBeforeFlow.put(s, entryInitialFlow());
        nodes.add(s);
        valueBefore.add(entryInitialFlow());
      }
    }

    // Perform fixed point flow analysis
    {
      Object previousAfterFlow = newInitialFlow();

      while (!changedUnits.isEmpty()) {
        Object beforeFlow;
        Object afterFlow;

        Object s = changedUnits.removeFirst();
        // Tag tag = (Tag)((JPegStmt)s).getTags().get(0);
        // System.out.println("===unit is: "+tag+" "+s);
        changedUnitsSet.remove(s);

        // copy(unitToAfterFlow.get(s), previousAfterFlow);
        // add for debug april 6
        int pos = nodes.indexOf(s);
        copy(valueAfter.elementAt(pos), previousAfterFlow);
        // end add for debug april
        // Compute and store beforeFlow
        {
          List preds = graph.getPredsOf(s);

          // beforeFlow = unitToBeforeFlow.get(s);

          beforeFlow = valueBefore.elementAt(pos);

          if (preds.size() == 1) {
            // copy(unitToAfterFlow.get(preds.get(0)), beforeFlow);
            copy(valueAfter.elementAt(nodes.indexOf(preds.get(0))), beforeFlow);
          }

          else if (preds.size() != 0) {
            Iterator predIt = preds.iterator();
            Object obj = predIt.next();

            // copy(unitToAfterFlow.get(obj), beforeFlow);
            copy(valueAfter.elementAt(nodes.indexOf(obj)), beforeFlow);

            while (predIt.hasNext()) {
              JPegStmt stmt = (JPegStmt) predIt.next();
              if (stmt.equals(obj)) {
                // System.out.println("!!!same object!!!");
                continue;
              }
              // Tag tag1 = (Tag)stmt.getTags().get(0);
              // System.out.println("pred: "+tag1+" "+stmt);

              // Object otherBranchFlow = unitToAfterFlow.get(stmt);
              if (nodes.indexOf(stmt) >= 0) // RLH
              {
                Object otherBranchFlow = valueAfter.elementAt(nodes.indexOf(stmt));

                merge(beforeFlow, otherBranchFlow, beforeFlow);
              }

            }
          }
        }

        // Compute afterFlow and store it.
        {
          // afterFlow = unitToAfterFlow.get(s);
          afterFlow = valueAfter.elementAt(nodes.indexOf(s));
          flowThrough(beforeFlow, s, afterFlow);

          // unitToAfterFlow.put(s, afterFlow);
          valueAfter.set(nodes.indexOf(s), afterFlow);
          // System.out.println("update afterFlow nodes: "+s);
          // System.out.println("afterFlow: "+afterFlow);
          // ((MonitorSet)unitToAfterFlow.get(s)).test();

          numComputations++;
        }

        // Update queue appropriately

        if (!afterFlow.equals(previousAfterFlow)) {

          Iterator succIt = graph.getSuccsOf(s).iterator();

          while (succIt.hasNext()) {
            Object succ = succIt.next();

            if (!changedUnitsSet.contains(succ)) {
              changedUnits.addLast(succ);
              changedUnitsSet.add(succ);
              /*
               * if (succ instanceof JPegStmt){ Tag tag1 = (Tag)((JPegStmt)succ).getTags().get(0);
               *
               * System.out.println("add to worklist: "+tag1+" "+succ); } else System.out.println("add to worklist: "+succ);
               */
            }
          }
        }

      }
    }

    // logger.debug(""+graph.getBody().getMethod().getSignature() + " numNodes: " + numNodes +
    // " numComputations: " + numComputations + " avg: " + Main.truncatedOf((double) numComputations / numNodes, 2));

    Timers.v().totalFlowNodes += numNodes;
    Timers.v().totalFlowComputations += numComputations;
  }

  // STEP 4: Is the merge operator union or intersection?
  // UNION
  protected void merge(Object in1, Object in2, Object out) {
    MonitorSet inSet1 = (MonitorSet) in1;
    MonitorSet inSet2 = (MonitorSet) in2;
    MonitorSet outSet = (MonitorSet) out;

    inSet1.intersection(inSet2, outSet);

  }

  // STEP 5: Define flow equations.
  // in(s) = ( out(s) minus defs(s) ) union uses(s)
  //
  protected void flowThrough(Object inValue, Object unit, Object outValue) {
    MonitorSet in = (MonitorSet) inValue;
    MonitorSet out = (MonitorSet) outValue;
    JPegStmt s = (JPegStmt) unit;
    Tag tag = (Tag) s.getTags().get(0);
    // System.out.println("s: "+tag+" "+s);
    // Copy in to out
    // if (in.contains("&")) in.remove("&");

    in.copy(out);
    // System.out.println("-----in: ");
    // in.test();

    if (in.size() > 0) {

      if (!s.getName().equals("waiting") && !s.getName().equals("notified-entry")) {
        updateMonitor(in, unit);
      }
    }
    String objName = s.getObject();
    // if (objName == null) throw new RuntimeException("null object: "+s.getUnit());
    if (s.getName().equals("entry") || s.getName().equals("exit")) {
      if (out.contains("&")) {
        out.remove("&");
      }

      Object obj = out.getMonitorDepth(objName);

      if (obj == null) {

        if (s.getName().equals("entry")) {
          MonitorDepth md = new MonitorDepth(objName, 1);
          out.add(md);
          // System.out.println("add to out: "+md.getObjName()+" "+md.getDepth());
        }
        /*
         * else{ throw new RuntimeException("The monitor depth can not be decreased at  "+
         * (Tag)((JPegStmt)s).getTags().get(0)+" "+unit); }
         */

      } else {
        // System.out.println("obj: "+obj);
        if (obj instanceof MonitorDepth) {
          MonitorDepth md = (MonitorDepth) obj;

          if (s.getName().equals("entry")) {
            md.increaseDepth();
            // System.out.println("===increase depth===");
          } else {

            if (md.getDepth() > 1) {
              // System.out.println("===decrease depth==");
              md.decreaseDepth();
            } else if (md.getDepth() == 1) {
              // System.out.println("===remove monitordepth: "+md);

              out.remove(md);
            } else {
              throw new RuntimeException("The monitor depth can not be decreased at  " + unit);
            }

          }
        } else {
          throw new RuntimeException("MonitorSet contains non MonitorDepth element!");
        }

      }

    }

    // System.out.println("-----out: "+out);
    // out.test();
    // testForDebug();
  }

  /*
   * private void testForDebug(){ System.out.println("--------test for debug-------"); int i = 0; for
   * (i=0;i<nodes.size();i++){ JPegStmt stmt = (JPegStmt)nodes.elementAt(i); //System.out.println("Tag: "+
   * ((Tag)stmt.getTags().get(0)).toString()); if (((Tag)stmt.getTags().get(0)).toString().equals("8")){ int pos =
   * nodes.indexOf(stmt); if (((MonitorSet)valueAfter.elementAt(pos)).size() >0 &&
   * !((MonitorSet)valueAfter.elementAt(pos)).contains("&")){
   * System.out.println("sp"+stmt.getTags().get(0)+" "+nodes.elementAt(pos));
   *
   * ((MonitorSet)valueAfter.elementAt(pos)).test(); } } } System.out.println("--------test for debug end------"); }
   */
  protected void copy(Object source, Object dest) {
    MonitorSet sourceSet = (MonitorSet) source;
    MonitorSet destSet = (MonitorSet) dest;

    sourceSet.copy(destSet);
  }

  // STEP 6: Determine value for start/end node, and
  // initial approximation.
  //
  // start node: empty set
  // initial approximation: empty set
  protected Object entryInitialFlow() {
    return new MonitorSet();
  }

  protected Object newInitialFlow() {
    MonitorSet fullSet = new MonitorSet();
    fullSet.add("&");
    return fullSet;

    // return fullSet.clone();

  }

  private void updateMonitor(MonitorSet ms, Object unit) {
    // System.out.println("===inside updateMonitor===");
    // ml.test();
    Iterator it = ms.iterator();
    while (it.hasNext()) {
      Object obj = it.next();
      if (obj instanceof MonitorDepth) {
        MonitorDepth md = (MonitorDepth) obj;
        String objName = md.getObjName();
        if (monitor.containsKey(objName)) {
          if (md.getDepth() > 0) {
            monitor.get(objName).add(unit);
            // System.out.println("add to monitorset "+unit);
          }
        } else {
          FlowSet monitorObjs = new ArraySparseSet();
          monitorObjs.add(unit);
          monitor.put(objName, monitorObjs);
          // System.out.println("put into monitor: "+objName);
        }
      }
    }

  }

  private void createWorkList(LinkedList<Object> changedUnits, HashSet<Object> changedUnitsSet) {
    createWorkList(changedUnits, changedUnitsSet, g.getMainPegChain());

    Set maps = g.getStartToThread().entrySet();
    for (Iterator iter = maps.iterator(); iter.hasNext();) {
      Map.Entry entry = (Map.Entry) iter.next();
      List runMethodChainList = (List) entry.getValue();
      Iterator it = runMethodChainList.iterator();
      while (it.hasNext()) {
        PegChain chain = (PegChain) it.next();
        createWorkList(changedUnits, changedUnitsSet, chain);
      }
    }

  }

  public void computeSynchNodes() {
    int num = 0;
    Set maps = monitor.entrySet();

    for (Iterator iter = maps.iterator(); iter.hasNext();) {
      Map.Entry entry = (Map.Entry) iter.next();
      FlowSet fs = (FlowSet) entry.getValue();
      num += fs.size();
    }
    System.err.println("synch objects: " + num);
  }
  /*
   * private void createWorkList(LinkedList changedUnits, HashSet changedUnitsSet, PegChain chain ){ //breadth first scan
   * Iterator it = chain.getHeads().iterator();
   *
   * while (it.hasNext()) { Object head = it.next(); Set gray = new HashSet(); LinkedList queue = new LinkedList();
   * queue.add(head); changedUnits.addLast(head); changedUnitsSet.add(head);
   *
   * //unitToBeforeFlow.put(head, newInitialFlow()); //unitToAfterFlow.put(head, newInitialFlow()); // add for debug April 6
   * nodes.add(head); valueBefore.add(newInitialFlow()); valueAfter.add(newInitialFlow()); // end add for debug April 6
   *
   * while (queue.size()>0){ Object root = queue.getFirst();
   *
   * Iterator succsIt = graph.getSuccsOf(root).iterator(); while (succsIt.hasNext()){ Object succ = succsIt.next(); if
   * (!gray.contains(succ)){ gray.add(succ); queue.addLast(succ); changedUnits.addLast(succ); changedUnitsSet.add(succ);
   *
   * // unitToBeforeFlow.put(succ, newInitialFlow()); //unitToAfterFlow.put(succ, newInitialFlow()); // add for debug April 6
   * nodes.add(succ); valueBefore.add(newInitialFlow()); valueAfter.add(newInitialFlow()); // end add for debug April 6
   *
   * } } queue.remove(root); } }
   *
   * }
   */

  private void createWorkList(LinkedList<Object> changedUnits, HashSet<Object> changedUnitsSet, PegChain chain) {
    // Depth first scan
    Iterator it = chain.getHeads().iterator();
    Set<Object> gray = new HashSet<Object>();

    while (it.hasNext()) {
      Object head = it.next();
      if (!gray.contains(head)) {

        visitNode(gray, head, changedUnits, changedUnitsSet);
      }
    }
  }

  private void visitNode(Set<Object> gray, Object obj, LinkedList<Object> changedUnits, HashSet<Object> changedUnitsSet) {

    gray.add(obj);
    changedUnits.addLast(obj);
    changedUnitsSet.add(obj);
    nodes.add(obj);
    valueBefore.add(newInitialFlow());
    valueAfter.add(newInitialFlow());
    Iterator succsIt = graph.getSuccsOf(obj).iterator();
    if (g.getSuccsOf(obj).size() > 0) {
      while (succsIt.hasNext()) {
        Object succ = succsIt.next();
        if (!gray.contains(succ)) {

          visitNode(gray, succ, changedUnits, changedUnitsSet);
        }
      }
    }
  }

  public Map<String, FlowSet> getMonitor() {
    return monitor;
  }

  public void testMonitor() {
    System.out.println("=====test monitor size: " + monitor.size());
    Set maps = monitor.entrySet();
    for (Iterator iter = maps.iterator(); iter.hasNext();) {
      Map.Entry entry = (Map.Entry) iter.next();
      String key = (String) entry.getKey();

      System.out.println("---key=  " + key);
      FlowSet list = (FlowSet) entry.getValue();
      if (list.size() > 0) {

        System.out.println("**set:  " + list.size());
        Iterator it = list.iterator();
        while (it.hasNext()) {
          JPegStmt stmt = (JPegStmt) it.next();
          Tag tag1 = (Tag) stmt.getTags().get(0);
          System.out.println(tag1 + " " + stmt);

        }

      }
    }
    System.out.println("=========monitor--ends--------");
  }
}
