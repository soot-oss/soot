package soot.jimple.toolkits.annotation.arraycheck;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2000 Feng Qian
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
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.ArrayType;
import soot.Body;
import soot.G;
import soot.Local;
import soot.Scene;
import soot.SceneTransformer;
import soot.Singletons;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.ArrayRef;
import soot.jimple.AssignStmt;
import soot.jimple.CastExpr;
import soot.jimple.DefinitionStmt;
import soot.jimple.FieldRef;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.NewArrayExpr;
import soot.jimple.NewMultiArrayExpr;
import soot.jimple.ParameterRef;
import soot.jimple.ReturnStmt;
import soot.jimple.Stmt;
import soot.jimple.internal.JArrayRef;
import soot.jimple.internal.JNewMultiArrayExpr;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Targets;
import soot.options.Options;
import soot.util.Chain;

/**
 * Interprocedural analysis to identify rectangular multi-dimension array locals. It is based on the call graph.
 */
public class RectangularArrayFinder extends SceneTransformer {
  private static final Logger logger = LoggerFactory.getLogger(RectangularArrayFinder.class);

  public RectangularArrayFinder(Singletons.Global g) {
  }

  public static RectangularArrayFinder v() {
    return G.v().soot_jimple_toolkits_annotation_arraycheck_RectangularArrayFinder();
  }

  private final ExtendedHashMutableDirectedGraph<Object> agraph = new ExtendedHashMutableDirectedGraph<Object>();

  private final Set<Object> falseSet = new HashSet<Object>();
  private final Set<Object> trueSet = new HashSet<Object>();
  private CallGraph cg;

  @Override
  protected void internalTransform(String phaseName, Map<String, String> opts) {
    Scene sc = Scene.v();

    cg = sc.getCallGraph();

    Date start = new Date();
    if (Options.v().verbose()) {
      logger.debug("[ra] Finding rectangular arrays, start on " + start);
    }

    for (SootClass c : sc.getApplicationClasses()) {
      for (Iterator<SootMethod> methodIt = c.methodIterator(); methodIt.hasNext();) {
        SootMethod method = methodIt.next();
        if (!method.isConcrete() || !sc.getReachableMethods().contains(method)) {
          continue;
        }

        recoverRectArray(method);
        addInfoFromMethod(method);// initializes the graph
      }
    }

    /*
     * MutableDirectedGraph methodGraph = ig.newMethodGraph(); HashSet visitedMethods = new HashSet(); LinkedList
     * tovisitMethods = new LinkedList();
     *
     * List heads = methodGraph.getHeads(); Iterator headIt = heads.iterator(); while (headIt.hasNext()) { SootMethod entry =
     * (SootMethod)headIt.next(); String sig = entry.getSubSignature();
     *
     * if (sig.equals(mainSignature)) tovisitMethods.add(entry); }
     *
     * while (!tovisitMethods.isEmpty()) { SootMethod visiting = (SootMethod)tovisitMethods.removeFirst();
     * visitedMethods.add(visiting);
     *
     * recoverRectArray(visiting); addInfoFromMethod(visiting);
     *
     * List succs = methodGraph.getSuccsOf(visiting); Iterator succIt = succs.iterator(); while (succIt.hasNext()) { Object
     * succ = succIt.next(); if (!visitedMethods.contains(succ)) tovisitMethods.add(succ); } }
     */

    /* propagate the graph info from FALSE node. */
    if (agraph.containsNode(BoolValue.v(false))) {
      List<Object> startNodes = agraph.getSuccsOf(BoolValue.v(false));
      falseSet.addAll(startNodes);

      List<Object> changedNodeList = new ArrayList<Object>(startNodes);
      while (!changedNodeList.isEmpty()) {
        Object node = changedNodeList.remove(0);
        for (Object succ : agraph.getSuccsOf(node)) {
          if (!falseSet.contains(succ)) {
            falseSet.add(succ);
            changedNodeList.add(succ);
          }
        }
      }
    }

    /* propagate graph info from TRUE node then. */
    if (agraph.containsNode(BoolValue.v(true))) {
      List<Object> changedNodeList = new ArrayList<Object>();
      for (Object node : agraph.getSuccsOf(BoolValue.v(true))) {
        if (!falseSet.contains(node)) {
          changedNodeList.add(node);
          trueSet.add(node);
        }
      }

      while (!changedNodeList.isEmpty()) {
        Object node = changedNodeList.remove(0);
        for (Object succ : agraph.getSuccsOf(node)) {
          if (falseSet.contains(succ) || trueSet.contains(succ)) {
            continue;
          }

          trueSet.add(succ);
          changedNodeList.add(succ);
        }
      }
    }

    /* For verification, print out true set and false set. */
    if (Options.v().debug()) {
      logger.debug("Rectangular Array :");
      for (Object node : trueSet) {
        logger.debug("" + node);
      }

      logger.debug("\nNon-rectangular Array :");
      for (Object node : falseSet) {
        logger.debug("" + node);
      }
    }

    if (Options.v().verbose()) {
      Date finish = new Date();
      long runtime = finish.getTime() - start.getTime();
      long mins = runtime / 60000;
      long secs = (runtime % 60000) / 1000;
      logger.debug("[ra] Rectangular array finder finishes. It took " + mins + " mins and " + secs + " secs.");
    }
  }

  private void addInfoFromMethod(SootMethod method) {
    if (Options.v().verbose()) {
      logger.debug("[ra] Operating " + method.getSignature());
    }

    boolean needTransfer = true;
    boolean trackReturn = false;

    /* check the return type of method, if it is multi-array. */
    Type rtnType = method.getReturnType();
    if (rtnType instanceof ArrayType) {
      if (((ArrayType) rtnType).numDimensions > 1) {
        trackReturn = true;
        needTransfer = true;
      }
    }

    final Body body = method.getActiveBody();

    Set<Object> tmpNode = new HashSet<Object>();
    Set<Value> arrayLocal = new HashSet<Value>();

    /* Collect the multi-array locals */
    for (Local local : body.getLocals()) {
      Type type = local.getType();
      if (type instanceof ArrayType) {
        if (((ArrayType) type).numDimensions > 1) {
          arrayLocal.add(local);
        } else {
          tmpNode.add(new MethodLocal(method, local));
        }
      }
    }

    /* The method has a local graph. It will be merged to the whole graph after simplification. */
    ExtendedHashMutableDirectedGraph<Object> ehmdg = new ExtendedHashMutableDirectedGraph<Object>();
    for (Iterator<Unit> unitIt = body.getUnits().snapshotIterator(); unitIt.hasNext();) {
      Stmt s = (Stmt) unitIt.next();

      /* for each invoke site, add edges from local parameter to the target methods' parameter node. */
      if (s.containsInvokeExpr()) {
        InvokeExpr iexpr = s.getInvokeExpr();
        int argnum = iexpr.getArgCount();
        for (int i = 0; i < argnum; i++) {
          Value arg = iexpr.getArg(i);
          if (arrayLocal.contains(arg)) {
            needTransfer = true;

            /* from node, it is a local */
            MethodLocal ml = new MethodLocal(method, (Local) arg);
            for (Targets targetIt = new Targets(cg.edgesOutOf(s)); targetIt.hasNext();) {
              SootMethod target = (SootMethod) targetIt.next();
              MethodParameter mp = new MethodParameter(target, i);

              /* add edge to the graph. */
              ehmdg.addMutualEdge(ml, mp);
            }
          }
        }
      }

      /* if the return type is multiarray, add an mutual edge from local to return node. */
      if (trackReturn && (s instanceof ReturnStmt)) {
        Value op = ((ReturnStmt) s).getOp();
        if (op instanceof Local) {
          ehmdg.addMutualEdge(new MethodLocal(method, (Local) op), new MethodReturn(method));
        }
      }

      /* examine each assign statement. build edge relationship between them. */
      if (s instanceof DefinitionStmt) {
        Value leftOp = ((DefinitionStmt) s).getLeftOp();
        Value rightOp = ((DefinitionStmt) s).getRightOp();

        if (!(leftOp.getType() instanceof ArrayType) && !(rightOp.getType() instanceof ArrayType)) {
          continue;
        }

        /* kick out the possible cast. */
        if ((leftOp instanceof Local) && (rightOp instanceof Local)) {
          if (arrayLocal.contains(leftOp) && arrayLocal.contains(rightOp)) {
            int leftDims = ((ArrayType) ((Local) leftOp).getType()).numDimensions;
            int rightDims = ((ArrayType) ((Local) rightOp).getType()).numDimensions;

            Object to = new MethodLocal(method, (Local) leftOp);
            Object from = new MethodLocal(method, (Local) rightOp);
            ehmdg.addMutualEdge(from, to);

            if (leftDims != rightDims) {
              ehmdg.addEdge(BoolValue.v(false), from);
            }
          } else if (!arrayLocal.contains(leftOp)) {
            /* implicitly cast from right side to left side, and the left side declare type is Object ... */
            ehmdg.addEdge(BoolValue.v(false), new MethodLocal(method, (Local) rightOp));
          }
        } else if ((leftOp instanceof Local) && (rightOp instanceof ParameterRef)) {
          if (arrayLocal.contains(leftOp)) {
            Object to = new MethodLocal(method, (Local) leftOp);
            int index = ((ParameterRef) rightOp).getIndex();
            Object from = new MethodParameter(method, index);
            ehmdg.addMutualEdge(from, to);

            needTransfer = true;
          }
        } else if ((leftOp instanceof Local) && (rightOp instanceof ArrayRef)) {
          Local base = (Local) ((ArrayRef) rightOp).getBase();

          /* it may include one-dimension array into the graph, */
          if (arrayLocal.contains(base)) {
            /* add 'a' to 'a[' first */
            Object to = new ArrayReferenceNode(method, base);
            Object from = new MethodLocal(method, base);
            ehmdg.addMutualEdge(from, to);

            /* put 'a[' into temporary object pool. */
            tmpNode.add(to);

            /* add 'a[' to 'p' then */
            from = to;
            to = new MethodLocal(method, (Local) leftOp);
            ehmdg.addMutualEdge(from, to);
          }
        } else if ((leftOp instanceof ArrayRef) && (rightOp instanceof Local)) {
          Local base = (Local) ((ArrayRef) leftOp).getBase();

          if (arrayLocal.contains(base)) {
            /* to recover the SWAP of array dimensions. */
            Object suspect = new MethodLocal(method, (Local) rightOp);

            boolean addEdge = true;
            if (ehmdg.containsNode(suspect)) {
              Set<Object> neighbor = new HashSet<Object>();
              neighbor.addAll(ehmdg.getSuccsOf(suspect));
              neighbor.addAll(ehmdg.getSuccsOf(suspect));

              if (neighbor.size() == 1) {
                Object arrRef = new ArrayReferenceNode(method, base);
                if (arrRef.equals(neighbor.iterator().next())) {
                  addEdge = false;
                }
              }
            }

            if (addEdge) {
              ehmdg.addEdge(BoolValue.v(false), new MethodLocal(method, base));
            }
          }
        } else if ((leftOp instanceof Local) && (rightOp instanceof InvokeExpr)) {
          if (arrayLocal.contains(leftOp)) {
            Object to = new MethodLocal(method, (Local) leftOp);

            for (Targets targetIt = new Targets(cg.edgesOutOf(s)); targetIt.hasNext();) {
              SootMethod target = (SootMethod) targetIt.next();
              ehmdg.addMutualEdge(new MethodReturn(target), to);
            }
          }
        } else if ((leftOp instanceof FieldRef) && (rightOp instanceof Local)) {
          /* For field reference, we can make conservative assumption that all instance fieldRef use the same node. */
          if (arrayLocal.contains(rightOp)) {
            Object to = ((FieldRef) leftOp).getField();
            Object from = new MethodLocal(method, (Local) rightOp);

            ehmdg.addMutualEdge(from, to);

            Type ftype = ((FieldRef) leftOp).getType();
            Type ltype = ((Local) rightOp).getType();
            if (!ftype.equals(ltype)) {
              ehmdg.addEdge(BoolValue.v(false), to);
            }

            needTransfer = true;
          }
        } else if ((leftOp instanceof Local) && (rightOp instanceof FieldRef)) {
          if (arrayLocal.contains(leftOp)) {
            Object to = new MethodLocal(method, (Local) leftOp);
            Object from = ((FieldRef) rightOp).getField();

            ehmdg.addMutualEdge(from, to);

            Type ftype = ((FieldRef) rightOp).getType();
            Type ltype = ((Local) leftOp).getType();
            if (!ftype.equals(ltype)) {
              ehmdg.addEdge(BoolValue.v(false), to);
            }

            needTransfer = true;
          }
        } else if ((leftOp instanceof Local)
            && ((rightOp instanceof NewArrayExpr) || (rightOp instanceof NewMultiArrayExpr))) {
          if (arrayLocal.contains(leftOp)) {
            ehmdg.addEdge(BoolValue.v(true), new MethodLocal(method, (Local) leftOp));
          }
        } else if ((leftOp instanceof Local) && (rightOp instanceof CastExpr)) {
          /* Cast express, we will use conservative solution. */
          Local rOp = (Local) ((CastExpr) rightOp).getOp();

          Object to = new MethodLocal(method, (Local) leftOp);
          Object from = new MethodLocal(method, rOp);

          if (arrayLocal.contains(leftOp) && arrayLocal.contains(rOp)) {
            ArrayType lat = (ArrayType) leftOp.getType();
            ArrayType rat = (ArrayType) rOp.getType();

            if (lat.numDimensions == rat.numDimensions) {
              ehmdg.addMutualEdge(from, to);
            } else {
              ehmdg.addEdge(BoolValue.v(false), from);
              ehmdg.addEdge(BoolValue.v(false), to);
            }
          } else if (arrayLocal.contains(leftOp)) {
            ehmdg.addEdge(BoolValue.v(false), to);
          } else if (arrayLocal.contains(rOp)) {
            ehmdg.addEdge(BoolValue.v(false), from);
          }
        }
      }
    }

    /* Compute the graph locally, it will skip all locals */
    if (needTransfer) {
      for (Object next : tmpNode) {
        ehmdg.skipNode(next);
      }
      /* Add local graph to whole graph */
      agraph.mergeWith(ehmdg);
    }
  }

  private void recoverRectArray(final SootMethod method) {
    final Body body = method.getActiveBody();
    HashSet<Value> malocal = new HashSet<Value>();
    for (Local local : body.getLocals()) {
      Type type = local.getType();
      if (type instanceof ArrayType) {
        if (((ArrayType) type).numDimensions == 2) {
          malocal.add(local);
        }
      }
    }
    if (malocal.isEmpty()) {
      return;
    }

    Chain<Unit> units = body.getUnits();
    for (Stmt stmt = (Stmt) units.getFirst(); true;) {
      /* only deal with the first block */
      if ((stmt == null) || !stmt.fallsThrough()) {
        break;
      }

      searchblock: {
        /* possible candidates */
        if (!(stmt instanceof AssignStmt)) {
          break searchblock;
        }

        Value leftOp = ((AssignStmt) stmt).getLeftOp();
        Value rightOp = ((AssignStmt) stmt).getRightOp();
        if (!malocal.contains(leftOp) || !(rightOp instanceof NewArrayExpr)) {
          break searchblock;
        }

        Local local = (Local) leftOp;
        NewArrayExpr naexpr = (NewArrayExpr) rightOp;
        Value size = naexpr.getSize();
        if (!(size instanceof IntConstant)) {
          break searchblock;
        }

        int firstdim = ((IntConstant) size).value;
        if (firstdim > 100) {
          break searchblock;
        }

        ArrayType localtype = (ArrayType) local.getType();
        Type basetype = localtype.baseType;

        Local[] tmplocals = new Local[firstdim];
        int seconddim = lookforPattern(units, stmt, firstdim, local, basetype, tmplocals);
        if (seconddim >= 0) {
          transferPattern(units, stmt, firstdim, seconddim, local, basetype, tmplocals);
        }
      }

      stmt = (Stmt) units.getSuccOf(stmt);
    }
  }

  /*
   * if the local is assigned a rect array, return back the second dimension length, else return -1
   */
  private int lookforPattern(Chain<Unit> units, Stmt startpoint, int firstdim, Local local, Type baseTy, Local[] tmplocals) {
    /* It is a state machine to match the pattern */
    /*
     * state input goto start r1 = new(A[])[c] 1 1 r2 = newA[d] 2 2 r2[?] = ... 2 r1[e] = r2 (e = c-1) 3 r1[e] = r2 (e =
     * e'+1) 2 3 end
     */

    int seconddim = -1;
    int curdim = 0;
    Value curtmp = local; // Local, I have to initialize it. It should not be this value.

    Stmt curstmt = startpoint;

    int fault = 99;
    int state = 1;
    while (true) {
      curstmt = (Stmt) units.getSuccOf(curstmt);
      if ((curstmt == null) || !(curstmt instanceof AssignStmt)) {
        return -1;
      }

      Value leftOp = ((AssignStmt) curstmt).getLeftOp();
      Value rightOp = ((AssignStmt) curstmt).getRightOp();

      switch (state) {
        case 0:
          /* we already did state 0 outside */
          break;

        case 1: {
          /* make sure it is a new array expr */
          state = fault;

          if (!(rightOp instanceof NewArrayExpr)) {
            break;
          }

          NewArrayExpr naexpr = (NewArrayExpr) rightOp;
          Type type = naexpr.getBaseType();
          Value size = naexpr.getSize();

          if (!type.equals(baseTy) || !(size instanceof IntConstant)) {
            break;
          }

          if (curdim == 0) {
            seconddim = ((IntConstant) size).value;
          } else if (((IntConstant) size).value != seconddim) {
            break;
          }

          curtmp = leftOp;
          state = 2;
          break;
        }

        case 2: {
          state = fault;

          if (!(leftOp instanceof ArrayRef)) {
            break;
          }

          Value base = ((ArrayRef) leftOp).getBase();
          Value idx = ((ArrayRef) leftOp).getIndex();

          /* curtmp[?] = ? */
          if (base.equals(curtmp)) {
            state = 2;
          } else if (base.equals(local)) {
            /* local[?] = curtmp? */
            if (!(idx instanceof IntConstant) || (curdim != ((IntConstant) idx).value) || !rightOp.equals(curtmp)) {
              break;
            }

            tmplocals[curdim] = (Local) curtmp;
            curdim++;
            if (curdim >= firstdim) {
              state = 3;
            } else {
              state = 1;
            }
          }
          break;
        }

        case 3:
          return seconddim;

        default:
          return -1;
      }
    }
  }

  private void transferPattern(Chain<Unit> units, Stmt startpoint, int firstdim, int seconddim, Local local, Type baseTy,
      Local[] tmplocals) {
    /* sequentially search and replace the sub dimension assignment */
    {
      /* change the first one, reset the right op */
      List<Value> sizes = new ArrayList<Value>(2);
      sizes.add(IntConstant.v(firstdim));
      sizes.add(IntConstant.v(seconddim));
      Value nmexpr = new JNewMultiArrayExpr((ArrayType) local.getType(), sizes);
      ((AssignStmt) startpoint).setRightOp(nmexpr);
    }

    int curdim = 0;
    Local tmpcur = local;
    Stmt curstmt = (Stmt) units.getSuccOf(startpoint);
    while (curdim < firstdim) {
      Value leftOp = ((AssignStmt) curstmt).getLeftOp();
      Value rightOp = ((AssignStmt) curstmt).getRightOp();

      if (tmplocals[curdim].equals(leftOp) && (rightOp instanceof NewArrayExpr)) {
        ArrayRef arexpr = new JArrayRef(local, IntConstant.v(curdim));
        ((AssignStmt) curstmt).setRightOp(arexpr);
        tmpcur = (Local) leftOp;
      } else if ((leftOp instanceof ArrayRef) && (rightOp.equals(tmpcur))) {
        /* delete current stmt */
        Stmt tmpstmt = curstmt;
        curstmt = (Stmt) units.getSuccOf(curstmt);
        units.remove(tmpstmt);

        curdim++;
      } else {
        curstmt = (Stmt) units.getSuccOf(curstmt);
      }
    }
  }

  public boolean isRectangular(Object obj) {
    return trueSet.contains(obj);
  }
}
