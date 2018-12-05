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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.Body;
import soot.Hierarchy;
import soot.IntType;
import soot.Local;
import soot.LongType;
import soot.RefType;
import soot.SootClass;
import soot.SootMethod;
import soot.Trap;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.EnterMonitorStmt;
import soot.jimple.ExitMonitorStmt;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.MonitorStmt;
import soot.jimple.NewExpr;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.internal.JIdentityStmt;
import soot.jimple.spark.pag.AllocNode;
import soot.jimple.spark.pag.Node;
import soot.jimple.spark.pag.PAG;
import soot.jimple.spark.sets.P2SetVisitor;
import soot.jimple.spark.sets.PointsToSetInternal;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.thread.mhp.stmt.BeginStmt;
import soot.jimple.toolkits.thread.mhp.stmt.JPegStmt;
import soot.jimple.toolkits.thread.mhp.stmt.JoinStmt;
import soot.jimple.toolkits.thread.mhp.stmt.MonitorEntryStmt;
import soot.jimple.toolkits.thread.mhp.stmt.MonitorExitStmt;
import soot.jimple.toolkits.thread.mhp.stmt.NotifiedEntryStmt;
import soot.jimple.toolkits.thread.mhp.stmt.NotifyAllStmt;
import soot.jimple.toolkits.thread.mhp.stmt.NotifyStmt;
import soot.jimple.toolkits.thread.mhp.stmt.OtherStmt;
import soot.jimple.toolkits.thread.mhp.stmt.StartStmt;
import soot.jimple.toolkits.thread.mhp.stmt.WaitStmt;
import soot.jimple.toolkits.thread.mhp.stmt.WaitingStmt;
import soot.tagkit.StringTag;
import soot.toolkits.graph.CompleteUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.ArraySparseSet;
import soot.toolkits.scalar.FlowSet;
import soot.util.Chain;
import soot.util.HashChain;

//add for add tag
//import soot.util.cfgcmd.*;

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

// NOTE that this graph builder will only run to completion if all virtual
// method calls can be resolved to a single target method.  This is a severely
// limiting caveat.

public class PegChain extends HashChain {

  CallGraph callGraph;
  private final List heads = new ArrayList();
  private final List tails = new ArrayList();
  private final FlowSet pegNodes = new ArraySparseSet();

  private final Map<Unit, JPegStmt> unitToPeg = new HashMap<Unit, JPegStmt>();
  private final Map<String, FlowSet> waitingNodes;
  private final PegGraph pg;
  private final Set<List<Object>> joinNeedReconsidered = new HashSet<List<Object>>();
  public Body body; // body from which this peg chain was created
  // private Map startToThread;

  Hierarchy hierarchy;
  PAG pag;
  Set threadAllocSites;
  Set methodsNeedingInlining;
  Set allocNodes;
  List<List> inlineSites;
  Map<SootMethod, String> synchObj;
  Set multiRunAllocNodes;
  Map<AllocNode, String> allocNodeToObj;

  PegChain(CallGraph callGraph, Hierarchy hierarchy, PAG pag, Set threadAllocSites, Set methodsNeedingInlining,
      Set allocNodes, List<List> inlineSites, Map<SootMethod, String> synchObj, Set multiRunAllocNodes,
      Map<AllocNode, String> allocNodeToObj, Body unitBody, SootMethod sm, String threadName, boolean addBeginNode,
      PegGraph pegGraph) {
    this.allocNodeToObj = allocNodeToObj;
    this.multiRunAllocNodes = multiRunAllocNodes;
    this.synchObj = synchObj;
    this.inlineSites = inlineSites;
    this.allocNodes = allocNodes;
    this.methodsNeedingInlining = methodsNeedingInlining;
    this.threadAllocSites = threadAllocSites;
    this.hierarchy = hierarchy;
    this.pag = pag;
    this.callGraph = callGraph;
    body = unitBody;
    pg = pegGraph;
    waitingNodes = pegGraph.getWaitingNodes();
    // Find exception handlers
    Iterator trapIt = unitBody.getTraps().iterator();
    Set<Unit> exceHandlers = pg.getExceHandlers();
    while (trapIt.hasNext()) {
      Trap trap = (Trap) trapIt.next();
      Unit handlerUnit = trap.getHandlerUnit();
      exceHandlers.add(handlerUnit);
    }

    // System.out.println("entering buildPegChain");
    UnitGraph graph = new CompleteUnitGraph(unitBody);

    Iterator unitIt = graph.iterator();
    // HashMap unitToPeg = new HashMap((graph.size())*2+1,0.7f);
    // June 19 add begin node

    if (addBeginNode) {
      // create PEG begin statement
      JPegStmt beginStmt = new BeginStmt("*", threadName, graph, sm);
      pg.getCanNotBeCompacted().add(beginStmt);
      addNode(beginStmt);
      heads.add(beginStmt);

    }
    // end June 19 add begin node

    Iterator it = graph.getHeads().iterator();

    while (it.hasNext()) {
      Object head = it.next();
      // breadth first scan
      Set<Unit> gray = new HashSet<Unit>();
      LinkedList<Object> queue = new LinkedList<Object>();
      queue.add(head);

      visit((Unit) queue.getFirst(), graph, sm, threadName, addBeginNode);
      while (queue.size() > 0) {
        Unit root = (Unit) queue.getFirst();

        Iterator succsIt = graph.getSuccsOf(root).iterator();
        while (succsIt.hasNext()) {
          Unit succ = (Unit) succsIt.next();

          if (!gray.contains(succ)) {
            gray.add(succ);
            queue.addLast(succ);
            visit(succ, graph, sm, threadName, addBeginNode);

          }
        }
        queue.remove(root);
      }

    }

    postHandleJoinStmt();
    pg.getUnitToPegMap().put(this, unitToPeg);

  }

  private void visit(Unit unit, UnitGraph graph, SootMethod sm, String threadName, boolean addBeginNode) {
    /*
     * if (unit instanceof JIdentityStmt){ System.out.println("JIdentityStmt left: "+((JIdentityStmt)unit).getLeftOp());
     * System.out.println("JIdentityStmt right: "+((JIdentityStmt)unit).getRightOp()); }
     */
    // System.out.println("unit: "+unit);
    if (unit instanceof MonitorStmt) {
      Value value = ((MonitorStmt) unit).getOp();
      if (value instanceof Local) {
        Type type = ((Local) value).getType();

        if (type instanceof RefType) {

          SootClass sc = ((RefType) type).getSootClass();
          if (unit instanceof EnterMonitorStmt) {

            String objName = makeObjName(value, type, unit);

            JPegStmt pegStmt = new MonitorEntryStmt(objName, threadName, unit, graph, sm);
            addAndPutNonCompacted(unit, pegStmt);
            return;
          }
          if (unit instanceof ExitMonitorStmt) {
            String objName = makeObjName(value, type, unit);
            JPegStmt pegStmt = new MonitorExitStmt(objName, threadName, unit, graph, sm);
            addAndPutNonCompacted(unit, pegStmt);

            return;
          }
        } // end if RefType

      } // end if Local

    } // end if MonitorStmt

    if (((Stmt) unit).containsInvokeExpr()) {

      Value invokeExpr = ((Stmt) unit).getInvokeExpr();

      SootMethod method = ((InvokeExpr) invokeExpr).getMethod();

      String name = method.getName();
      Value value = null;
      Type type = null;
      List paras = method.getParameterTypes();
      String objName = null;
      if (invokeExpr instanceof InstanceInvokeExpr) {

        value = ((InstanceInvokeExpr) invokeExpr).getBase();

        if (value instanceof Local) {
          // Type type = ((Local)value).getType();
          type = ((Local) value).getType();

          if (type instanceof RefType) {

            SootClass sc = ((RefType) type).getSootClass();

            // sc = ((RefType)type).getSootClass();
            objName = sc.getName();

          }
        }
      } else {
        if (!(invokeExpr instanceof StaticInvokeExpr)) {
          throw new RuntimeException("Error: new type of invokeExpre: " + invokeExpr);
        } else {
          // static invoke
        }
      }
      // Check if a method belongs to a thread.
      boolean find = false;
      if (method.getName().equals("start")) {
        // System.out.println("Test method is: "+method);
        // System.out.println("DeclaringClass: "+method.getDeclaringClass());
        List<SootClass> superClasses = hierarchy.getSuperclassesOfIncluding(method.getDeclaringClass());
        Iterator<SootClass> it = superClasses.iterator();

        while (it.hasNext()) {
          String className = it.next().getName();
          if (className.equals("java.lang.Thread")) {
            find = true;
            break;
          }
        }
      }
      if (method.getName().equals("run")) {
        // System.out.println("method name: "+method.getName());
        // System.out.println("DeclaringClass name: "+method.getDeclaringClass().getName());
        if ((method.getDeclaringClass().getName()).equals("java.lang.Runnable")) {
          // System.out.println("find: "+find);

          find = true;
        }
      }

      if (name.equals("wait") && (paras.size() == 0 || (paras.size() == 1 && (Type) paras.get(0) instanceof LongType)
          || (paras.size() == 2 && (Type) paras.get(0) instanceof LongType && (Type) paras.get(1) instanceof IntType))) {

        /*
         * special modeling for wait() method call which transforms wait() node to 3 node.
         */
        objName = makeObjName(value, type, unit);
        transformWaitNode(objName, name, threadName, unit, graph, sm);
      }

      else {
        if ((name.equals("start") || name.equals("run")) && find) {

          // System.out.println("DeclaringClass: "+method.getDeclaringClass().getName());
          // System.out.println("====start method: "+method);
          // System.out.println("unit: "+unit);
          List<AllocNode> mayAlias = null;
          PointsToSetInternal pts = (PointsToSetInternal) pag.reachingObjects((Local) value);
          mayAlias = findMayAlias(pts, unit);

          JPegStmt pegStmt = new StartStmt(value.toString(), threadName, unit, graph, sm);

          if (pg.getStartToThread().containsKey(pegStmt)) {
            throw new RuntimeException("map startToThread contain duplicated start() method call");
          }
          pg.getCanNotBeCompacted().add(pegStmt);
          addAndPut(unit, pegStmt);
          List<PegChain> runMethodChainList = new ArrayList<PegChain>();
          List<AllocNode> threadAllocNodesList = new ArrayList<AllocNode>();
          // add Feb 01
          if (mayAlias.size() < 1) {

            throw new RuntimeException("The may alias set of " + unit + "is empty!");

          }
          Iterator<AllocNode> mayAliasIt = mayAlias.iterator();
          // System.out.println("mayAlias: "+mayAlias);
          while (mayAliasIt.hasNext()) {
            AllocNode allocNode = mayAliasIt.next();
            // System.out.println("allocNode toString: "+allocNode.toString());
            RefType refType = ((NewExpr) allocNode.getNewExpr()).getBaseType();
            SootClass maySootClass = refType.getSootClass();
            // remeber to modify here!!! getMethodByName is unsafe!
            // if (method.getDeclaringClass()
            /*
             * TargetMethodsFinder tmd = new TargetMethodsFinder(); List targetList = tmd.find(unit, callGraph, false);
             * SootMethod meth=null; if (targetList.size()>1) { System.out.println("targetList: "+targetList); throw new
             * RuntimeException("target of start >1!"); } else meth = (SootMethod)targetList.get(0);
             */
            SootMethod meth
                = hierarchy.resolveConcreteDispatch(maySootClass, method.getDeclaringClass().getMethodByName("run"));
            // System.out.println("==method is: "+meth);

            Body mBody = meth.getActiveBody();

            // Feb 2 modify thread name
            int threadNo = Counter.getThreadNo();
            String callerName = "thread" + threadNo;
            // System.out.println("Adding thread start point: " + "thread" + threadNo + " pegStmt: " + pegStmt);

            // map caller ()-> start pegStmt
            pg.getThreadNameToStart().put(callerName, pegStmt);
            PegChain newChain = new PegChain(callGraph, hierarchy, pag, threadAllocSites, methodsNeedingInlining, allocNodes,
                inlineSites, synchObj, multiRunAllocNodes, allocNodeToObj, mBody, sm, callerName, true, pg);

            pg.getAllocNodeToThread().put(allocNode, newChain);

            runMethodChainList.add(newChain);
            threadAllocNodesList.add(allocNode);
          }

          // end add Feb 01

          // System.out.println("Adding something to startToThread");
          pg.getStartToThread().put(pegStmt, runMethodChainList);
          pg.getStartToAllocNodes().put(pegStmt, threadAllocNodesList);

        } // end if (name.equals("start") )
        else {
          if (name.equals("join") && method.getDeclaringClass().getName().equals("java.lang.Thread")) {

            // If the may-alias of "join" has more that one elements, we can NOT kill anything.
            PointsToSetInternal pts = (PointsToSetInternal) pag.reachingObjects((Local) value);
            // System.out.println("pts: "+pts);
            List<AllocNode> mayAlias = findMayAlias(pts, unit);

            // System.out.println("=====mayAlias for thread: "+unit +" is:\n"+mayAlias);

            if (mayAlias.size() != 1) {
              if (mayAlias.size() < 1) {
                // System.out.println("===points to set: "+pts);
                // System.out.println("the size of mayAlias <0 : \n"+mayAlias);
                throw new RuntimeException("==threadAllocaSits==\n" + threadAllocSites.toString());

              }

              JPegStmt pegStmt = new JoinStmt(value.toString(), threadName, unit, graph, sm);

              addAndPutNonCompacted(unit, pegStmt);
              pg.getSpecialJoin().add(pegStmt);

            } else {

              Iterator<AllocNode> mayAliasIt = mayAlias.iterator();

              while (mayAliasIt.hasNext()) {

                AllocNode allocNode = mayAliasIt.next();
                // System.out.println("allocNode toString: "+allocNode.toString());
                JPegStmt pegStmt = new JoinStmt(value.toString(), threadName, unit, graph, sm);
                if (!pg.getAllocNodeToThread().containsKey(allocNode)) {
                  List<Object> list = new ArrayList<Object>();
                  list.add(pegStmt);
                  list.add(allocNode);
                  list.add(unit);
                  joinNeedReconsidered.add(list);
                  // throw new RuntimeException("allocNodeToThread does not contains key: "+allocNode);
                } else {
                  // If the mayAlias contains one 1 element, then use the threadName as
                  // the Obj of the JPegStmt.
                  // String callerName = (String)allocNodeToCaller.get(allocNode);
                  Chain thread = pg.getAllocNodeToThread().get(allocNode);

                  addAndPutNonCompacted(unit, pegStmt);
                  pg.getJoinStmtToThread().put(pegStmt, thread);

                }
              }

            }

          } else {
            // June 17 add for build obj->notifiyAll map.
            if (name.equals("notifyAll") && paras.size() == 0) {
              objName = makeObjName(value, type, unit);
              JPegStmt pegStmt = new NotifyAllStmt(objName, threadName, unit, graph, sm);

              addAndPutNonCompacted(unit, pegStmt);
              // build notifyAll Map
              if (pg.getNotifyAll().containsKey(objName)) {
                Set<JPegStmt> notifyAllSet = pg.getNotifyAll().get(objName);
                notifyAllSet.add(pegStmt);
                pg.getNotifyAll().put(objName, notifyAllSet);
              } else {
                Set<JPegStmt> notifyAllSet = new HashSet<JPegStmt>();
                notifyAllSet.add(pegStmt);
                pg.getNotifyAll().put(objName, notifyAllSet);
              }

              // end build notifyAll Map

            } else {

              // add Oct 8, for building pegs with inliner.
              if (name.equals("notify") && paras.size() == 0
                  && method.getDeclaringClass().getName().equals("java.lang.Thread")) {
                objName = makeObjName(value, type, unit);
                JPegStmt pegStmt = new NotifyStmt(objName, threadName, unit, graph, sm);
                addAndPutNonCompacted(unit, pegStmt);
              }

              else {
                // //System.out.println("******method before extend: "+method);
                // System.out.println("isConcretemethod: "+method.isConcrete());
                // System.out.println("isLibraryClass: "+method.getDeclaringClass().isLibraryClass());
                if (method.isConcrete() && !method.getDeclaringClass().isLibraryClass()) {

                  List<SootMethod> targetList = new LinkedList<SootMethod>();
                  SootMethod targetMethod = null;
                  if (invokeExpr instanceof StaticInvokeExpr) {
                    targetMethod = method;
                  } else {
                    TargetMethodsFinder tmd = new TargetMethodsFinder();
                    targetList = tmd.find(unit, callGraph, true, false);

                    if (targetList.size() > 1) {
                      System.out.println("target: " + targetList);
                      System.out.println("unit is: " + unit);
                      System.err.println("exit because target is bigger than 1.");
                      System.exit(1); // What SHOULD be done is that all possible targets are inlined
                      // as though each method body is in a big switch on the type of
                      // the receiver object. The infrastructure to do this is not
                      // currently available, so instead we exit. Continuing would
                      // yield wrong answers.
                    } else if (targetList.size() < 1) {
                      System.err.println("targetList size <1");
                      // System.exit(1);
                      // continue;
                    } else {
                      targetMethod = targetList.get(0);
                    }
                  }

                  if (methodsNeedingInlining == null) {
                    System.err.println("methodsNeedingInlining is null at " + unit);
                  } else if (targetMethod == null) {
                    System.err.println("targetMethod is null at " + unit);
                  } else if (methodsNeedingInlining.contains(targetMethod)) {
                    inlineMethod(targetMethod, objName, name, threadName, unit, graph, sm);
                  } else {
                    JPegStmt pegStmt = new OtherStmt(objName, name, threadName, unit, graph, sm);
                    addAndPut(unit, pegStmt);
                  }
                }

                else {
                  JPegStmt pegStmt = new OtherStmt(objName, name, threadName, unit, graph, sm);
                  addAndPut(unit, pegStmt);

                }

              }

              // end add Oct 8, for building pegs with inliner.

            }
          }
        }

      } // end else if ("wait")

    } // end if containsInvokeExpr()

    else {

      newAndAddElement(unit, graph, threadName, sm);
    }

  }
  // end buildPegChain()

  private void transformWaitNode(String objName, String name, String threadName, Unit unit, UnitGraph graph, SootMethod sm) {
    JPegStmt pegStmt = new WaitStmt(objName, threadName, unit, graph, sm);

    addAndPutNonCompacted(unit, pegStmt);

    JPegStmt pegWaiting = new WaitingStmt(objName, threadName, sm);
    pg.getCanNotBeCompacted().add(pegWaiting);
    addNode(pegWaiting);
    // build waitingNodesMap
    if (waitingNodes.containsKey(objName)) {
      FlowSet waitingNodesSet = waitingNodes.get(objName);
      if (!waitingNodesSet.contains(pegWaiting)) {
        waitingNodesSet.add(pegWaiting);
        waitingNodes.put(pegWaiting.getObject(), waitingNodesSet);
        // System.out.println("get a waiting nodes set");
      } else {
        // throw an run time exception
      }
    } else {
      FlowSet waitingNodesSet = new ArraySparseSet();
      waitingNodesSet.add(pegWaiting);
      waitingNodes.put(pegWaiting.getObject(), waitingNodesSet);
      // System.out.println("new a waiting nodes set");
    }
    // end build waitingNodes Map

    {
      List successors = new ArrayList();
      successors.add(pegWaiting);
      pg.getUnitToSuccs().put(pegStmt, successors);
    }

    JPegStmt pegNotify = new NotifiedEntryStmt(objName, threadName, sm);
    pg.getCanNotBeCompacted().add(pegNotify);
    addNode(pegNotify);

    {
      List successors = new ArrayList();
      successors.add(pegNotify);
      pg.getUnitToSuccs().put(pegWaiting, successors);
    }
  }

  private List<AllocNode> findMayAlias(PointsToSetInternal pts, Unit unit) {
    // returns a list of reaching objects' AllocNodes that are contained in the set of known AllocNodes
    List<AllocNode> list = new ArrayList<AllocNode>();
    Iterator<AllocNode> it = makePtsIterator(pts);
    while (it.hasNext()) {
      AllocNode obj = it.next();

      list.add(obj);
    }
    return list;
  }

  private void inlineMethod(SootMethod targetMethod, String objName, String name, String threadName, Unit unit,
      UnitGraph graph, SootMethod sm) {
    // System.out.println("inside extendMethod "+ targetMethod);

    Body unitBody = targetMethod.getActiveBody();

    JPegStmt pegStmt = new OtherStmt(objName, name, threadName, unit, graph, sm);

    if (targetMethod.isSynchronized()) {
      // System.out.println(unit+" is synchronized========");

      String synchObj = findSynchObj(targetMethod);
      JPegStmt enter = new MonitorEntryStmt(synchObj, threadName, graph, sm);
      JPegStmt exit = new MonitorExitStmt(synchObj, threadName, graph, sm);
      pg.getCanNotBeCompacted().add(enter);
      pg.getCanNotBeCompacted().add(exit);

      List list = new ArrayList();
      list.add(pegStmt);
      list.add(enter);
      list.add(exit);
      // System.out.println("add list to synch: "+list);
      pg.getSynch().add(list);
    }
    addAndPut(unit, pegStmt);

    PegGraph pG = new PegGraph(callGraph, hierarchy, pag, methodsNeedingInlining, allocNodes, inlineSites, synchObj,
        multiRunAllocNodes, allocNodeToObj, unitBody, threadName, targetMethod, true, false);
    // pg.addPeg(pG, this); // RLH
    // PegToDotFile printer1 = new PegToDotFile(pG, false, targetMethod.getName());
    // System.out.println("NeedInlining for "+targetMethod +": "+pG.getNeedInlining());

    // if (pG.getNeedInlining()){
    List list = new ArrayList();
    list.add(pegStmt);
    list.add(this);
    list.add(pg);
    list.add(pG);
    inlineSites.add(list);
    // System.out.println("----add list to inlineSites !---------");
    // }

  }

  private String findSynchObj(SootMethod targetMethod) {

    if (synchObj.containsKey(targetMethod)) {
      return synchObj.get(targetMethod);
    } else {
      String objName = null;
      if (targetMethod.isStatic()) {
        objName = targetMethod.getDeclaringClass().getName();
      } else {
        Iterator it = ((Chain) (targetMethod.getActiveBody()).getUnits()).iterator();

        while (it.hasNext()) {
          Object obj = it.next();
          if (obj instanceof JIdentityStmt) {
            Value thisRef = ((JIdentityStmt) obj).getLeftOp();
            if (thisRef instanceof Local) {
              Type type = ((Local) thisRef).getType();
              if (type instanceof RefType) {
                objName = makeObjName(thisRef, type, (Unit) obj);
                synchObj.put(targetMethod, objName);
                break;
              }
            }
          }
        }
      }
      return objName;
    }
  }

  private void addNode(JPegStmt stmt) {
    this.addLast(stmt);
    pegNodes.add(stmt);
    pg.getAllNodes().add(stmt);
  }

  private void addAndPut(Unit unit, JPegStmt stmt) {
    unitToPeg.put(unit, stmt);
    addNode(stmt);
  }

  private void addAndPutNonCompacted(Unit unit, JPegStmt stmt) {
    pg.getCanNotBeCompacted().add(stmt);
    addAndPut(unit, stmt);
  }

  private void newAndAddElement(Unit unit, UnitGraph graph, String threadName, SootMethod sm) {
    JPegStmt pegStmt = new OtherStmt("*", unit.toString(), threadName, unit, graph, sm);
    addAndPut(unit, pegStmt);
  }

  public List getHeads() {
    return heads;
  }

  public List getTails() {
    return tails;
  }

  protected void addTag() {
    // add tag for each stmt
    Iterator it = iterator();
    while (it.hasNext()) {
      JPegStmt stmt = (JPegStmt) it.next();
      int count = Counter.getTagNo();
      StringTag t = new StringTag(Integer.toString(count));
      stmt.addTag(t);
    }
  }

  private Iterator<AllocNode> makePtsIterator(PointsToSetInternal pts) {
    final HashSet<AllocNode> ret = new HashSet<AllocNode>();
    pts.forall(new P2SetVisitor() {
      public void visit(Node n) {

        ret.add((AllocNode) n);
      }
    });
    // testPtsIterator(ret.iterator());
    return ret.iterator();
  }

  /*
   * public List getPredsOf(Object s) { if(!unitToPreds.containsKey(s)) throw new RuntimeException("Invalid stmt" + s);
   *
   * return (List) unitToPreds.get(s); }
   *
   * public List getSuccsOf(Object s) {
   *
   * if(!unitToSuccs.containsKey(s)) throw new RuntimeException("Invalid stmt:" + s);
   *
   * return (List) unitToSuccs.get(s); }
   */
  // Sometimes, we can not find the target of join(). Now we should handle it.
  private void postHandleJoinStmt() {
    Iterator<List<Object>> it = joinNeedReconsidered.iterator();
    while (it.hasNext()) {
      List list = it.next();
      JPegStmt pegStmt = (JPegStmt) list.get(0);
      AllocNode allocNode = (AllocNode) list.get(1);
      Unit unit = (Unit) list.get(2);
      if (!pg.getAllocNodeToThread().containsKey(allocNode)) {

        throw new RuntimeException("allocNodeToThread does not contains key: " + allocNode);
      } else {
        // If the mayAlias contains one 1 element, then use the threadName as
        // the Obj of the JPegStmt.
        // String callerName = (String)allocNodeToCaller.get(allocNode);
        Chain thread = pg.getAllocNodeToThread().get(allocNode);

        addAndPutNonCompacted(unit, pegStmt);
        pg.getJoinStmtToThread().put(pegStmt, thread);

      }
    }
  }

  private String makeObjName(Value value, Type type, Unit unit) {
    // System.out.println("unit: "+unit);
    PointsToSetInternal pts = (PointsToSetInternal) pag.reachingObjects((Local) value);
    // System.out.println("pts for makeobjname: "+pts);
    List<AllocNode> mayAlias = findMayAlias(pts, unit);

    String objName = null;
    if (allocNodeToObj == null) {
      throw new RuntimeException("allocNodeToObj is null!");
    }

    if (mayAlias.size() == 1) {
      // System.out.println("unit: "+unit);

      AllocNode an = mayAlias.get(0);
      // System.out.println("alloc node: "+an);
      // if (!multiRunAllocNodes.contains(an)){

      if (allocNodeToObj.containsKey(an)) {
        objName = allocNodeToObj.get(an);
      } else {
        // System.out.println("===AllocNodeToObj does not contain key allocnode: "+an);
        // objName = type.toString()+Counter.getObjNo();
        objName = "obj" + Counter.getObjNo();
        allocNodeToObj.put(an, objName);
      }
      // System.out.println("objName: "+objName);
      // }
      // else
      // throw new RuntimeException("The size of object corresponds to site "+ unit + " is not 1.");

    } else {
      AllocNode an = mayAlias.get(0);
      // System.out.println("alloc node: "+an);
      // if (!multiRunAllocNodes.contains(an)){

      if (allocNodeToObj.containsKey(an)) {
        objName = "MULTI" + allocNodeToObj.get(an);
      } else {
        // System.out.println("===AllocNodeToObj does not contain key allocnode: "+an);
        // objName = type.toString()+Counter.getObjNo();
        objName = "MULTIobj" + Counter.getObjNo();
        allocNodeToObj.put(an, objName);
      }
      // System.out.println("objName: "+objName);
      // }
      // else
      // throw new RuntimeException("The size of object corresponds to site "+ unit + " is not 1.");
      // System.out.println("pts: "+pts);
      // throw new RuntimeException("The program exit because the size of object corresponds to site "+ unit + "is not 1.");
    }
    // System.out.println("==return objName: "+objName);
    if (objName == null) {
      throw new RuntimeException("Can not find target object for " + unit);
    }
    return objName;
  }

  protected Map<String, FlowSet> getWaitingNodes() {
    return waitingNodes;
  }

  protected void testChain() {
    System.out.println("******** chain********");
    Iterator it = iterator();
    while (it.hasNext()) {

      JPegStmt stmt = (JPegStmt) it.next();
      System.out.println(stmt.toString());

    }

  }
}
