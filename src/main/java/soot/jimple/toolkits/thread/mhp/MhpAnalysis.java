
/*0815 use complete graph, success
 *0801 add the special treatment for waitng->notified-entry,--- and localSucc(n)
 *0730 add MSym set for each node to store the nodes added to the m set during symmetry step.
 */
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

import soot.jimple.toolkits.thread.mhp.stmt.BeginStmt;
import soot.jimple.toolkits.thread.mhp.stmt.JPegStmt;
import soot.jimple.toolkits.thread.mhp.stmt.JoinStmt;
import soot.jimple.toolkits.thread.mhp.stmt.MonitorEntryStmt;
import soot.jimple.toolkits.thread.mhp.stmt.NotifiedEntryStmt;
import soot.jimple.toolkits.thread.mhp.stmt.NotifyAllStmt;
import soot.jimple.toolkits.thread.mhp.stmt.NotifyStmt;
import soot.jimple.toolkits.thread.mhp.stmt.StartStmt;
import soot.jimple.toolkits.thread.mhp.stmt.WaitingStmt;
import soot.tagkit.Tag;
import soot.toolkits.scalar.ArraySparseSet;
import soot.toolkits.scalar.FlowSet;
import soot.util.Chain;

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

/**
 * @author Lin Li This is a synchronization-aware May Happen in Parallel (MHP) analysis. It works by analyzing a PegGraph
 *         (simplified whole-program control flow graph that includes thread executions and synchronization).
 */
class MhpAnalysis {

  private PegGraph g;
  private final Map<Object, FlowSet> unitToGen;
  private final Map<Object, FlowSet> unitToKill;
  private final Map<Object, FlowSet> unitToM;
  // private Map unitToMSym;
  private final Map<Object, FlowSet> unitToOut;
  private final Map<Object, FlowSet> notifySucc;
  private final Map<String, FlowSet> monitor;
  private final Map<JPegStmt, Set<JPegStmt>> notifyPred;
  FlowSet fullSet = new ArraySparseSet();
  LinkedList<Object> workList = new LinkedList<Object>();

  MhpAnalysis(PegGraph g) {
    // System.out.println("******entering MhpAnalysis");
    this.g = g;
    int size = g.size();
    Map startToThread = g.getStartToThread();
    unitToGen = new HashMap<Object, FlowSet>(size * 2 + 1, 0.7f);
    unitToKill = new HashMap<Object, FlowSet>(size * 2 + 1, 0.7f);
    unitToM = new HashMap<Object, FlowSet>(size * 2 + 1, 0.7f);
    // unitToMSym = new HashMap(size*2+1, 0.7f);
    unitToOut = new HashMap<Object, FlowSet>(size * 2 + 1, 0.7f);
    notifySucc = new HashMap<Object, FlowSet>(size * 2 + 1, 0.7f);
    // notifyEdge = new HashMap(size*2+1,0.7f);
    notifyPred = new HashMap<JPegStmt, Set<JPegStmt>>(size * 2 + 1, 0.7f);
    // monitor = new HashMap(size*2+1,0.7f);
    monitor = g.getMonitor();

    // testMap(monitor, "monitor");

    /*
     * Initialize the KILL, GEN, M, and OUT set to empty set for all nodes.
     */
    Iterator it = g.iterator();

    while (it.hasNext()) {

      Object stmt = it.next();
      FlowSet genSet = new ArraySparseSet();
      FlowSet killSet = new ArraySparseSet();
      FlowSet mSet = new ArraySparseSet();
      FlowSet outSet = new ArraySparseSet();
      // stupidly add notifySucc for every node
      FlowSet notifySuccSet = new ArraySparseSet();

      unitToGen.put(stmt, genSet);
      unitToKill.put(stmt, killSet);
      unitToM.put(stmt, mSet);
      unitToOut.put(stmt, outSet);
      notifySucc.put(stmt, notifySuccSet);

    }
    // System.out.println("before init worklist");
    // testM();
    // System.err.println("finish initializing kill,gen,m,out to empty set");

    /*
     * Initialize the worklist to include all start nodes in the main thread that are reachable from the begin node of the
     * main thread
     */
    Set keys = startToThread.keySet();
    Iterator keysIt = keys.iterator();

    while (keysIt.hasNext()) {
      JPegStmt stmt = (JPegStmt) keysIt.next();
      if (!workList.contains(stmt)) {
        workList.addLast(stmt);
      }
      // System.out.println("add"+stmt+"to worklist");
    }
    // System.err.println("finish initializing worklist");
    // testWorkList();
    /*
     * computer gen-set, and kill-set for each node
     */

    it = g.iterator();

    while (it.hasNext()) {
      FlowSet genSet = new ArraySparseSet();
      FlowSet killSet = new ArraySparseSet();
      Object o = it.next();

      // System.err.println(s);

      if (o instanceof JPegStmt) {

        JPegStmt s = (JPegStmt) o;
        if (s instanceof JoinStmt) {
          // if (s.getName().equals("join")){
          // If specialJoin of Peg contains s, skip this node.
          // Otherwise,compute kill set for (t,join,*).
          if (g.getSpecialJoin().contains(s)) {
            // System.out.println("==specialJoin contains: "+s);
            continue;
          } else {
            // compute kill set for (t,join,*)

            Chain chain = (g.getJoinStmtToThread().get(s));
            Iterator nodesIt = chain.iterator();
            if (nodesIt.hasNext()) {
              while (nodesIt.hasNext()) {
                killSet.add(nodesIt.next());
              }

            }
          }

          unitToGen.put(s, genSet);
          unitToKill.put(s, killSet);

        } else if (s instanceof MonitorEntryStmt || s instanceof NotifiedEntryStmt) {
          // else if (s.getName().equals("entry") || s.getName().equals("notified-entry")){

          Iterator It = g.iterator();
          if (monitor.containsKey(s.getObject())) {
            killSet = monitor.get(s.getObject());
          }
          unitToGen.put(s, genSet);
          unitToKill.put(s, killSet);

        } else if (s instanceof NotifyAllStmt) {
          Map<String, FlowSet> waitingNodes = g.getWaitingNodes();

          if (waitingNodes.containsKey(s.getObject())) {
            // System.out.println("******find object:"+s.getObject());
            FlowSet killNodes = waitingNodes.get(s.getObject());
            Iterator nodesIt = killNodes.iterator();
            while (nodesIt.hasNext()) {
              killSet.add(nodesIt.next());
            }
          }
          unitToGen.put(s, genSet);
          unitToKill.put(s, killSet);

          // stem.out.println("put "+s+"into set");

        } else if (s instanceof NotifyStmt) {
          // else if (s.getName().equals("notify")){
          Map<String, FlowSet> waitingNodes = g.getWaitingNodes();
          if (waitingNodes.containsKey(s.getObject())) {
            FlowSet killNodes = waitingNodes.get(s.getObject());
            if (killNodes.size() == 1) {
              Iterator nodesIt = killNodes.iterator();
              while (nodesIt.hasNext()) {
                killSet.add(nodesIt.next());
              }
            }
          }
          unitToGen.put(s, genSet);
          unitToKill.put(s, killSet);
          // System.out.println("put "+s+"into set");

        } else if ((s instanceof StartStmt) && g.getStartToThread().containsKey(s)) {
          // modify Feb 5

          Iterator chainIt = g.getStartToThread().get(s).iterator();
          while (chainIt.hasNext()) {
            PegChain chain = (PegChain) chainIt.next();
            Iterator beginNodesIt = chain.getHeads().iterator();
            while (beginNodesIt.hasNext()) {

              genSet.add(beginNodesIt.next());
            }
          }
          /*
           * Iterator localSuccIt =((List)g.getSuccsOf(s)).iterator();
           *
           * while (localSuccIt.hasNext()){
           *
           * Object localSucc = localSuccIt.next(); genSet.add(localSucc); }
           */
          unitToGen.put(s, genSet);
          unitToKill.put(s, killSet);
        }

      }
    } // end while
      // System.err.println("finish compute genset and kill set for each nodes");
      // testmaps();
      // testGen();
      // testKill();

    doAnalysis();
    // testNotifySucc();
    // -----------
    long beginTime = System.currentTimeMillis();
    // testM();
    computeMPairs();
    computeMSet();
    long buildPegDuration = (System.currentTimeMillis() - beginTime);
    System.err.println("compute parir + mset: " + buildPegDuration);
    // ------------
  }

  protected void doAnalysis() {

    while (workList.size() > 0) {
      // get the head of the worklist and remove the head
      Object currentObj = workList.removeFirst();
      // System.out.println("curObj: "+currentObj);
      /*
       * if (currentObj instanceof JPegStmt){ Tag tag = (Tag)((JPegStmt)currentObj).getTags().get(0);
       * System.out.println("=====current node is:==="+tag+" "+currentObj); } else{
       * System.out.println("===current node is: list==="); Iterator listIt = ((List)currentObj).iterator();
       *
       * while (listIt.hasNext()){ Object oo = listIt.next(); if (oo instanceof JPegStmt){ JPegStmt unit = (JPegStmt)oo; Tag
       * tag = (Tag)unit.getTags().get(0); System.out.println(tag+" "+unit); } else System.out.println(oo); }
       * System.out.println("===list==end=="); }
       */
      // get kill, gen, m and out set.

      FlowSet killSet = unitToKill.get(currentObj);
      FlowSet genSet = unitToGen.get(currentObj);
      // FlowSet mSet = (FlowSet)unitToM.get(currentNode);
      FlowSet mSet = new ArraySparseSet();

      FlowSet outSet = unitToOut.get(currentObj);
      FlowSet notifySuccSet = notifySucc.get(currentObj);
      /*
       * if (unitToMSym.containsKey(currentObj)){ FlowSet mSetSym = (FlowSet)unitToMSym.get(currentObj);
       * //test("mSetSym",mSetSym);
       *
       * mSet.union(mSetSym);
       *
       * }
       */
      FlowSet mOld = unitToM.get(currentObj);
      FlowSet outOld = outSet.clone();

      FlowSet notifySuccSetOld = notifySuccSet.clone();
      FlowSet genNotifyAllSet = new ArraySparseSet();
      JPegStmt waitingPred = null;

      // testSet(mOld, "mOld");
      // testSet(outOld, "outOld");
      // testSet(genSet, "genSet");
      // testWorkList();
      if (!(currentObj instanceof JPegStmt)) {
        // compute M Set
        Iterator localPredIt = (g.getPredsOf(currentObj)).iterator();

        while (localPredIt.hasNext()) {

          Object tempStmt = localPredIt.next();
          FlowSet out = unitToOut.get(tempStmt);
          // testSet(out,"out of localPred");
          if (out != null) {
            mSet.union(out);

          }

        }
        /*
         * if (unitToMSym.containsKey(currentObj)){ FlowSet mSetSym = (FlowSet)unitToMSym.get(currentObj);
         * mSet.union(mSetSym); //testSet(mSetSym,"mSetSyn");
         *
         * }
         */
        mSet.union(mOld);
        unitToM.put(currentObj, mSet);

        // end compute M(n) set

        /*
         * compute out set
         */

        mSet.union(genSet, outSet);

        if (killSet.size() > 0) {
          Iterator killIt = killSet.iterator();
          while (killIt.hasNext()) {
            Object tempStmt = killIt.next();
            if (outSet.contains(tempStmt)) {
              outSet.remove(tempStmt);
            }
          }
        }

        // end compute out set

        /*
         * do the symmetry step for all new nodes in M(n)
         */
        // test("######mSet old:",mOld);
        // test("######mSet:",mSet);
        // System.out.println("======entering symmetry====");
        if (!mOld.equals(mSet)) {
          // System.out.println("entering mold <> mset");
          Iterator mSetIt = mSet.iterator();
          while (mSetIt.hasNext()) {
            Object tempM = mSetIt.next();
            if (!mOld.contains(tempM)) {
              if (!unitToM.containsKey(tempM)) {
                // System.out.println("unitToM does not contain: "+tempM);
              } else {

                FlowSet mSetMSym = unitToM.get(tempM);
                if (!(mSetMSym.size() == 0)) {
                  if (!mSetMSym.contains(currentObj)) {

                    mSetMSym.add(currentObj);
                    /*
                     * Tag tag1 = (Tag)((JPegStmt)tempM).getTags().get(0); System.out.println("add "+tag+" "+currentNode
                     * +"to the mset of "+tag1+" "+tempM); testSet((FlowSet)unitToM.get(tempM), "mset of "+tag1+" "+tempM);
                     */
                  }
                } else {
                  mSetMSym.add(currentObj);
                }

                /*
                 * ADD ======== FlowSet mSetMSym=null; if (unitToMSym.containsKey(tempM)){ mSetMSym =
                 * (FlowSet)unitToMSym.get(tempM);
                 *
                 * } else{ mSetMSym = new ArraySparseSet(); } if (!mSetMSym.contains(currentObj)){
                 *
                 * mSetMSym.add(currentObj);
                 *
                 * //Tag tag1 = (Tag)tempM.getTags().get(0); //System.out.println("add "+currentObj
                 * +"to the mset of "+tempM); } else{ //System.out.println("the mset of "+tempM+" contains "+currentNode); }
                 * unitToMSym.put(tempM, mSetMSym);
                 */
              }

            }

            /*
             * add m to the worklist because the change in M(m) may lead to a change in OUT(m)
             */
            if (!workList.contains(tempM)) {
              workList.addLast(tempM);

            }
            // System.out.println("add in symmetry"+tempM+"to worklist");

          }

        } // System.out.println("======end symmetry====");

        // end do the symmetry step for all new nodes in M(n)

        /*
         * if new nodes has been addedd to the OUT set of n, add n's successors to the worklist
         */
        if (!outOld.equals(outSet)) {
          // compute LocalSucc(n)
          Iterator localSuccIt = (g.getSuccsOf(currentObj)).iterator();

          while (localSuccIt.hasNext()) {

            Object localSucc = localSuccIt.next();
            // System.out.println("localSucc: "+localSucc);

            if (localSucc instanceof JPegStmt) {
              if ((JPegStmt) localSucc instanceof NotifiedEntryStmt) {
                // if (((JPegStmt)localSucc).getName().equals("notified-entry")){
                continue;

              }

              else if (!workList.contains(localSucc)) {

                workList.addLast(localSucc);

                // System.out.println("add "+localSucc+"to worklist---local succ");
              }

            } else {
              if (!workList.contains(localSucc)) {

                workList.addLast(localSucc);

                // System.out.println("add to worklist---local succ");
                /*
                 * Iterator it = ((List)localSucc).iterator(); while (it.hasNext()){ JPegStmt ss = (JPegStmt)it.next(); Tag
                 * tag1 = (Tag)ss.getTags().get(0); System.out.println(tag1+" "+ss); }
                 * System.out.println("--------------------");
                 */ }
            }
          }

        }

        // System.out.println("===========after=============");
        // testSet(mSet, "mSet:");

        // testSet(genSet, "genSet");

        // test("######killSet:",killSet);

        // testSet(outSet, "outSet:");

        // testWorkList();
        // System.out.print("c");

      }
      // if the current node is JPegStmt
      else {
        JPegStmt currentNode = (JPegStmt) currentObj;

        Tag tag = (Tag) currentNode.getTags().get(0);

        if (currentNode instanceof NotifyStmt || currentNode instanceof NotifyAllStmt) {
          // if (currentNode.getName().equals("notify") ||currentNode.getName().equals("notifyAll") ){
          Map<String, FlowSet> waitingNodes = g.getWaitingNodes();
          if (waitingNodes.containsKey(currentNode.getObject())) {
            FlowSet waitingNodeSet = waitingNodes.get(currentNode.getObject());
            // test("waitingNodeSet",waitingNodeSet);
            Iterator waitingNodesIt = waitingNodeSet.iterator();
            while (waitingNodesIt.hasNext()) {
              JPegStmt tempNode = (JPegStmt) waitingNodesIt.next();
              // test("mSet",mSet);
              // System.out.println("tempNode: "+tempNode);
              if (mOld.contains(tempNode)) {
                // System.out.println("mSet contains waiting node");
                List waitingSuccList = g.getSuccsOf(tempNode);
                Iterator waitingSuccIt = waitingSuccList.iterator();
                while (waitingSuccIt.hasNext()) {
                  JPegStmt waitingSucc = (JPegStmt) waitingSuccIt.next();
                  notifySuccSet.add(waitingSucc);
                  if (waitingSucc instanceof NotifiedEntryStmt) {
                    // build notifySucc Map

                    FlowSet notifySet = notifySucc.get(currentNode);
                    notifySet.add(waitingSucc);
                    notifySucc.put(currentNode, notifySet);

                    // end build notifySucc Map

                    // build notifyPred Map
                    // Apr 12 Fix bug notifyPredSet.add(waitingSucc)->Pred.get(waitingSucc);

                    if (notifyPred.containsKey(waitingSucc)) {
                      Set<JPegStmt> notifyPredSet = notifyPred.get(waitingSucc);
                      notifyPredSet.add(currentNode);
                      notifyPred.put(waitingSucc, notifyPredSet);
                    } else {
                      Set<JPegStmt> notifyPredSet = new HashSet<JPegStmt>();
                      notifyPredSet.add(currentNode);
                      // notifyPredSet.add(waitingSucc);
                      notifyPred.put(waitingSucc, notifyPredSet);
                    }
                    // end modify April 12
                    // end build notifyPred Map
                    // testMap(notifyPred,"notifyPred of: "+waitingSucc);
                  }

                }
              }
            }

          } else {
            // System.out.println("waitingNodes "+waitingNodes);
            throw new RuntimeException("Fail to find waiting node for: " + currentObj);
          }

        } // end if notifynodes

        // testNotifySucc();

        /*
         * if new notify edges were added from this node, add all notify successors of this node to the worklist
         */

        if (!notifySuccSetOld.equals(notifySuccSet)) {
          Iterator notifySuccIt = notifySuccSet.iterator();
          while (notifySuccIt.hasNext()) {
            Object notifySuccNode = notifySuccIt.next();
            if (!workList.contains(notifySuccNode)) {
              workList.addLast(notifySuccNode);
            }
            // System.out.println("add"+notifySuccNode+"to worklist");
          }
        }

        // compute GENnotifyAll(n) for (obj, notified-entry,*)
        // if (currentNode.getName().equals("notified-entry")){
        if (currentNode instanceof NotifiedEntryStmt) {
          Iterator waitingPredIt = (g.getPredsOf(currentNode)).iterator();
          while (waitingPredIt.hasNext()) {
            waitingPred = (JPegStmt) waitingPredIt.next();
            if ((waitingPred instanceof WaitingStmt) && waitingPred.getObject().equals(currentNode.getObject())
                && waitingPred.getCaller().equals(currentNode.getCaller())) {
              break;
            }
          } // end while

          /*
           * compute the notified-entry set for "obj" in (obj, notified-entry, *) because notified-entry nodes always follow
           * the corresponding waiting nodes, we can find waitingNodes for obj, then find the notified-entry nodes.
           */

          Map<String, FlowSet> waitingNodes = g.getWaitingNodes();
          FlowSet notifyEntrySet = new ArraySparseSet();
          if (waitingNodes.containsKey(currentNode.getObject())) {
            FlowSet waitingNodesSet = waitingNodes.get(currentNode.getObject());
            Iterator waitingNodesIt = waitingNodesSet.iterator();
            while (waitingNodesIt.hasNext()) {
              List waitingNodesSucc = g.getSuccsOf(waitingNodesIt.next());
              Iterator waitingNodesSuccIt = waitingNodesSucc.iterator();
              while (waitingNodesSuccIt.hasNext()) {
                JPegStmt notifyEntry = (JPegStmt) waitingNodesSuccIt.next();
                if (notifyEntry instanceof NotifiedEntryStmt) {
                  notifyEntrySet.add(notifyEntry);
                }
              }
            }
          }

          /*
           * compute the m set for WaitingPred(notifyEntry node)
           */

          Iterator notifyEntrySetIt = notifyEntrySet.iterator();
          while (notifyEntrySetIt.hasNext()) {
            JPegStmt notifyEntry = (JPegStmt) notifyEntrySetIt.next();
            Iterator waitingPredIterator = (g.getPredsOf(notifyEntry)).iterator();

            JPegStmt waitingPredNode = null;
            // find the WaitingPred(notified-entry node)
            while (waitingPredIterator.hasNext()) {
              waitingPredNode = (JPegStmt) waitingPredIterator.next();
              if ((waitingPredNode instanceof WaitingStmt) && waitingPredNode.getObject().equals(currentNode.getObject())
                  && waitingPredNode.getCaller().equals(currentNode.getCaller())) {
                break;
              }
            }
            if (!unitToM.containsKey(waitingPredNode)) {

            } else {
              FlowSet mWaitingPredM = unitToM.get(waitingPredNode);
              if (mWaitingPredM.contains(waitingPred)) {
                // get r: r is (obj,notifyAll,*)
                Map<String, Set<JPegStmt>> notifyAll = g.getNotifyAll();
                if (notifyAll.containsKey(currentNode.getObject())) {
                  Set notifyAllSet = notifyAll.get(currentNode.getObject());
                  Iterator notifyAllIt = notifyAllSet.iterator();
                  while (notifyAllIt.hasNext()) {
                    JPegStmt notifyAllStmt = (JPegStmt) notifyAllIt.next();
                    if (unitToM.containsKey(waitingPred)) {
                      FlowSet mWaitingPredN = unitToM.get(waitingPred);
                      if (mWaitingPredM.contains(notifyAllStmt) && mWaitingPredN.contains(notifyAllStmt)) {
                        genNotifyAllSet.add(notifyEntry);
                      }
                    }
                  }
                }
              }
            }
          }

        } // end compute GENnotifyAll(n)

        // compute M(n) set
        FlowSet notifyPredUnion = new ArraySparseSet();
        if (currentNode instanceof NotifiedEntryStmt) {
          // System.out.println("===notified-entry stmt== \n"+((JPegStmt)currentNode).getTags().get(0)+" "+currentNode);
          if (!unitToOut.containsKey(waitingPred)) {
            throw new RuntimeException("unitToOut does not contains " + waitingPred);
          } else {

            FlowSet mSetOfNotifyEntry = new ArraySparseSet();
            // compute the Union of out(NotifyPred(n))
            Set notifyPredSet = notifyPred.get(currentNode);
            // System.out.println("notifyPredSet: "+notifyPredSet);
            if (notifyPredSet == null) {
              // System.out.println(currentNode+"has no notifyPredset");
            } else {
              Iterator notifyPredSetIt = notifyPredSet.iterator();
              while (notifyPredSetIt.hasNext()) {
                JPegStmt notifyPred = (JPegStmt) notifyPredSetIt.next();
                // System.out.println("notifyPred: "+notifyPred.getTags().get(0)+" "+notifyPred);
                FlowSet outWaitingPredTemp = unitToOut.get(notifyPred);
                // testSet(outWaitingPredTemp, "out of notifyPred");
                outWaitingPredTemp.copy(notifyPredUnion);
              }
              // testSet(notifyPredUnion, "Union of out of notifyPred");

              // compute OUT(waitingPred(n)) waitingPred=waitingPred(n)
              FlowSet outWaitingPredSet = unitToOut.get(waitingPred);
              // testSet(outWaitingPredSet, "out of WaitingPred");

              // compute the intersection of (the Union of out(NotifyPred(n)) ) and (OUT(waitingPred(n)))
              notifyPredUnion.intersection(outWaitingPredSet, mSetOfNotifyEntry);
              // testSet(mSetOfNotifyEntry, "intersection of notify and waiting");
              // compute the union of above and GENnotifyAll(n)
              // testSet(genNotifyAllSet, "GenNotifyAll(n)");
              mSetOfNotifyEntry.union(genNotifyAllSet, mSet);
            }

          }

        } else if (currentNode instanceof BeginStmt) {
          // compute StartPred(n)
          // modify Feb 6
          mSet = new ArraySparseSet();
          Map<JPegStmt, List> startToThread = g.getStartToThread();
          Set<JPegStmt> keySet = startToThread.keySet();
          Iterator<JPegStmt> it = keySet.iterator();
          while (it.hasNext()) {
            JPegStmt tempStmt = it.next();
            Iterator chainListIt = startToThread.get(tempStmt).iterator();
            while (chainListIt.hasNext()) {

              List beginNodes = ((PegChain) chainListIt.next()).getHeads();
              if (beginNodes.contains(currentNode)) {

                // compute OUT(p)
                Iterator outStartPredIt = unitToOut.get(tempStmt).iterator();
                while (outStartPredIt.hasNext()) {
                  Object startPred = outStartPredIt.next();
                  // System.out.println("add startPred to mSet: "+startPred);
                  mSet.add(startPred);

                }

              }

            }
          }
          // remove N(t) from m set

          Iterator iter = startToThread.keySet().iterator();

          while (iter.hasNext()) {

            JPegStmt tempStmt = (JPegStmt) iter.next();
            Iterator chainListIt = startToThread.get(tempStmt).iterator();
            while (chainListIt.hasNext()) {

              Chain chain = (Chain) chainListIt.next();
              if (chain.contains(currentNode)) {

                Iterator nodesIt = chain.iterator();
                while (nodesIt.hasNext()) {
                  Object stmt = nodesIt.next();
                  if (mSet.contains(stmt)) {
                    mSet.remove(stmt);
                  }
                }
              }
            }
          }

        }

        else {
          // System.out.println("=======entering");

          Iterator localPredIt = (g.getPredsOf(currentNode)).iterator();
          if (!(currentNode instanceof NotifiedEntryStmt)) {
            while (localPredIt.hasNext()) {

              Object tempStmt = localPredIt.next();
              FlowSet out = unitToOut.get(tempStmt);
              // testSet(out,"out of localPred");
              if (out != null) {
                mSet.union(out);

              }
            }
          }

          // testSet(mSet, "mSet");
          // System.out.println("after compute mset");
          // testSet(mSet, "mSet");

        }
        // System.out.println("before add msetNew");
        // testSet(mSet, "mSet");
        /*
         * if (mSetNew != null){ Iterator mSetNewIt = mSetNew.iterator(); while(mSetNewIt.hasNext()){ JPegStmt mNew =
         * (JPegStmt)mSetNewIt.next(); if(!mSet.contains(mNew)){ mSet.add(mNew); } } }
         */
        // else System.out.println("null mSetNew for :"+currentNode);
        /*
         * modify July 6 2004 if (unitToMSym.containsKey(currentNode)){ FlowSet mSetSym =
         * (FlowSet)unitToMSym.get(currentNode); mSet.union(mSetSym); }
         */

        mSet.union(mOld);

        unitToM.put(currentNode, mSet);

        // end compute M(n) set

        /*
         * compute GEN(n) set for notify and notifyAll nodes GEN(n) = NotifySucc(n)
         */
        if (currentNode instanceof NotifyStmt || currentNode instanceof NotifyAllStmt) {
          notifySuccSet.copy(genSet);
          // test("===notifySuccSet:",notifySuccSet);

          unitToGen.put(currentNode, genSet);
        }

        // end compute GEN(n) set for notify and notifyAll nodes

        /*
         * compute out set
         */

        mSet.union(genSet, outSet);

        if (killSet.size() > 0) {
          Iterator killIt = killSet.iterator();
          while (killIt.hasNext()) {

            Object tempStmt = killIt.next();
            if (outSet.contains(tempStmt)) {
              outSet.remove(tempStmt);
            }
          }
        }
        // testSet(outSet, "outSet");
        // end compute out set

        /*
         * do the symmetry step for all new nodes in M(n)
         */
        // test("######mSet old:",mOld);
        // test("######mSet:",mSet);
        // testSet(mOld, "oldMset");
        // testSet(mSet, "mSet");
        if (!mOld.equals(mSet)) {
          // System.out.println("entering mold <> mset");
          Iterator mSetIt = mSet.iterator();
          while (mSetIt.hasNext()) {

            Object tempM = mSetIt.next();
            if (!mOld.contains(tempM)) {
              if (!unitToM.containsKey(tempM)) {
                throw new RuntimeException("unitToM does not contain: " + tempM);
              } else {
                FlowSet mSetMSym = unitToM.get(tempM);
                if (!(mSetMSym.size() == 0)) {
                  if (!mSetMSym.contains(currentNode)) {

                    mSetMSym.add(currentNode);
                    /*
                     * Tag tag1 = (Tag)((JPegStmt)tempM).getTags().get(0); System.out.println("add "+tag+" "+currentNode
                     * +"to the mset of "+tag1+" "+tempM); testSet((FlowSet)unitToM.get(tempM), "mset of "+tag1+" "+tempM);
                     */
                  }
                } else {
                  mSetMSym.add(currentNode);
                  /*
                   * Tag tag1 = (Tag)((JPegStmt)tempM).getTags().get(0); System.out.println("add "+tag+" "+currentNode
                   * +"to the mset of "+tag1+" "+tempM); testSet((FlowSet)unitToM.get(tempM), "mset of "+tag1+" "+tempM);
                   */
                }

                /*
                 * FlowSet mSetMSym=null; if (unitToMSym.containsKey(tempM)){ mSetMSym = (FlowSet)unitToMSym.get(tempM);
                 *
                 * } else{ mSetMSym = new ArraySparseSet(); } if (!mSetMSym.contains(currentNode)){
                 *
                 * mSetMSym.add(currentNode);
                 *
                 * //Tag tag1 = (Tag)tempM.getTags().get(0); //System.out.println("add "+currentNode
                 * +"to the mset of "+tag1+" "+tempM); } else{
                 * //System.out.println("the mset of "+tempM+" contains "+currentNode); } unitToMSym.put(tempM, mSetMSym);
                 */
              }

            }

            /*
             * add m to the worklist because the change in M(m) may lead to a change in OUT(m)
             */
            if (!workList.contains(tempM)) {
              workList.addLast(tempM);
            }
            // System.out.println("add"+tempM+"to worklist");

          }

        }

        // end do the symmetry step for all new nodes in M(n)

        /*
         * if new nodes has been addedd to the OUT set of n, add n's successors to the worklist
         */

        if (!outOld.equals(outSet)) {
          // compute LocalSucc(n)
          Iterator localSuccIt = (g.getSuccsOf(currentNode)).iterator();

          while (localSuccIt.hasNext()) {

            Object localSucc = localSuccIt.next();
            if (localSucc instanceof JPegStmt) {
              if ((JPegStmt) localSucc instanceof NotifiedEntryStmt) {
                continue;
              } else {
                if (!workList.contains(localSucc)) {
                  workList.addLast(localSucc);
                }
              }

            }

            else if (!workList.contains(localSucc)) {

              workList.addLast(localSucc);
              /*
               * System.out.println("add to worklist---local succ"); Iterator it = ((List)localSucc).iterator(); while
               * (it.hasNext()){ Object obj = it.next(); if (obj instanceof JPegStmt){ JPegStmt ss = (JPegStmt)obj; Tag tag1
               * = (Tag)ss.getTags().get(0); System.out.println(tag1+" "+ss); } else System.out.println(obj); }
               * System.out.println("--------------------");
               */
            }
          }

          // compute StartSucc(n)
          if (currentNode instanceof StartStmt) {
            // if (currentNode.getName().equals("start")){
            Map<JPegStmt, List> startToThread = g.getStartToThread();
            if (!startToThread.containsKey(currentNode)) {
            } else {

              Iterator it = startToThread.get(currentNode).iterator();
              while (it.hasNext()) {
                Iterator chainIt = ((Chain) it.next()).iterator();
                while (chainIt.hasNext()) {
                  // JPegStmt tempStmt = (JPegStmt)chainIt.next();
                  Object tempStmt = chainIt.next();
                  if (tempStmt instanceof JPegStmt) {
                    if ((JPegStmt) tempStmt instanceof BeginStmt) {
                      // if (((JPegStmt)tempStmt).getName().equals("begin")){
                      if (!workList.contains(tempStmt)) {
                        workList.addLast(tempStmt);
                      }
                      break;
                    }
                  }
                }
              }
            }
          }

        }

        // testSet(mSet, "mSet");

        // test("######genSet",genSet);

        // test("######killSet:",killSet);

        // test("######outSet:",outSet);

        // testWorkList();
        // System.out.print("c");
      }

    } // end while

  }

  protected Object entryInitialFlow() {
    return new ArraySparseSet();
  }

  protected Object newInitialFlow() {
    return fullSet.clone();
  }

  // add for debug
  protected Map<Object, FlowSet> getUnitToM() {
    return unitToM;
  }
  // end add for debug

  private void computeMPairs() {
    Set<Set<Object>> mSetPairs = new HashSet<Set<Object>>();
    Set maps = unitToM.entrySet();
    for (Iterator iter = maps.iterator(); iter.hasNext();) {
      Map.Entry entry = (Map.Entry) iter.next();
      Object obj = entry.getKey();
      FlowSet fs = (FlowSet) entry.getValue();
      Iterator it = fs.iterator();

      while (it.hasNext()) {
        /*
         * for test Object a = it.next(); Set s1 = new HashSet(); s1.add(a); s1.add(obj); mSetPairs.add(s1); Set s2 = new
         * HashSet(); s2.add(obj); s2.add(a); System.out.println("equals: "+s1.equals(s2));
         * System.out.println("contains: "+mSetPairs.contains(s2)); System.exit(1);
         */

        Object m = it.next();
        Set<Object> pair = new HashSet<Object>();
        pair.add(obj);
        pair.add(m);
        if (!mSetPairs.contains(pair)) {
          mSetPairs.add(pair);
        }

      }
    }
    System.err.println("Number of pairs: " + mSetPairs.size());

  }

  private void computeMSet() {
    long min = 0;
    long max = 0;
    long nodes = 0;
    long totalNodes = 0;
    Set maps = unitToM.entrySet();
    boolean first = true;
    for (Iterator iter = maps.iterator(); iter.hasNext();) {

      Map.Entry entry = (Map.Entry) iter.next();
      Object obj = entry.getKey();
      FlowSet fs = (FlowSet) entry.getValue();
      if (fs.size() > 0) {
        totalNodes += fs.size();
        nodes++;
        if (fs.size() > max) {
          max = fs.size();
        }
        if (first) {
          min = fs.size();
          first = false;
        } else {
          if (fs.size() < min) {
            min = fs.size();
          }
        }
      }

    }

    System.err.println("average: " + totalNodes / nodes);
    System.err.println("min: " + min);
    System.err.println("max: " + max);

  }

}
