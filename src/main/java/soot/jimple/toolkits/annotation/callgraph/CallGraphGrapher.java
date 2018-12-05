package soot.jimple.toolkits.annotation.callgraph;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2004 Jennifer Lhotak
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
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Body;
import soot.G;
import soot.MethodOrMethodContext;
import soot.MethodToContexts;
import soot.Scene;
import soot.SceneTransformer;
import soot.Singletons;
import soot.SootClass;
import soot.SootMethod;
import soot.jimple.Stmt;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.options.CGGOptions;
import soot.options.Options;
import soot.toolkits.graph.interaction.InteractionHandler;

/** A scene transformer that creates a graphical callgraph. */
public class CallGraphGrapher extends SceneTransformer {
  private static final Logger logger = LoggerFactory.getLogger(CallGraphGrapher.class);

  public CallGraphGrapher(Singletons.Global g) {
  }

  public static CallGraphGrapher v() {
    return G.v().soot_jimple_toolkits_annotation_callgraph_CallGraphGrapher();
  }

  private MethodToContexts methodToContexts;
  private CallGraph cg;
  private boolean showLibMeths;

  private ArrayList<MethInfo> getTgtMethods(SootMethod method, boolean recurse) {
    // logger.debug("meth for tgts: "+method);
    if (!method.hasActiveBody()) {
      return new ArrayList<MethInfo>();
    }
    Body b = method.getActiveBody();
    ArrayList<MethInfo> list = new ArrayList<MethInfo>();
    Iterator sIt = b.getUnits().iterator();
    while (sIt.hasNext()) {
      Stmt s = (Stmt) sIt.next();
      Iterator edges = cg.edgesOutOf(s);
      while (edges.hasNext()) {
        Edge e = (Edge) edges.next();
        SootMethod sm = e.tgt();
        // logger.debug("found target method: "+sm);

        if (sm.getDeclaringClass().isLibraryClass()) {
          if (isShowLibMeths()) {
            if (recurse) {
              list.add(new MethInfo(sm, hasTgtMethods(sm) | hasSrcMethods(sm), e.kind()));
            } else {
              list.add(new MethInfo(sm, true, e.kind()));
            }
          }
        } else {
          if (recurse) {
            list.add(new MethInfo(sm, hasTgtMethods(sm) | hasSrcMethods(sm), e.kind()));
          } else {
            list.add(new MethInfo(sm, true, e.kind()));
          }
        }
      }
    }
    return list;
  }

  private boolean hasTgtMethods(SootMethod meth) {
    ArrayList<MethInfo> list = getTgtMethods(meth, false);
    if (!list.isEmpty()) {
      return true;
    } else {
      return false;
    }
  }

  private boolean hasSrcMethods(SootMethod meth) {
    ArrayList<MethInfo> list = getSrcMethods(meth, false);
    if (list.size() > 1) {
      return true;
    } else {
      return false;
    }
  }

  private ArrayList<MethInfo> getSrcMethods(SootMethod method, boolean recurse) {
    // logger.debug("meth for srcs: "+method);
    ArrayList<MethInfo> list = new ArrayList<MethInfo>();

    for (Iterator momcIt = methodToContexts.get(method).iterator(); momcIt.hasNext();) {
      final MethodOrMethodContext momc = (MethodOrMethodContext) momcIt.next();
      Iterator callerEdges = cg.edgesInto(momc);
      while (callerEdges.hasNext()) {
        Edge callEdge = (Edge) callerEdges.next();
        SootMethod methodCaller = callEdge.src();
        if (methodCaller.getDeclaringClass().isLibraryClass()) {
          if (isShowLibMeths()) {
            if (recurse) {
              list.add(
                  new MethInfo(methodCaller, hasTgtMethods(methodCaller) | hasSrcMethods(methodCaller), callEdge.kind()));
            } else {
              list.add(new MethInfo(methodCaller, true, callEdge.kind()));
            }
          }
        } else {
          if (recurse) {
            list.add(new MethInfo(methodCaller, hasTgtMethods(methodCaller) | hasSrcMethods(methodCaller), callEdge.kind()));
          } else {
            list.add(new MethInfo(methodCaller, true, callEdge.kind()));
          }
        }
      }
    }
    return list;
  }

  protected void internalTransform(String phaseName, Map options) {

    CGGOptions opts = new CGGOptions(options);
    if (opts.show_lib_meths()) {
      setShowLibMeths(true);
    }
    cg = Scene.v().getCallGraph();
    if (Options.v().interactive_mode()) {
      reset();
    }
  }

  public void reset() {
    if (methodToContexts == null) {
      methodToContexts = new MethodToContexts(Scene.v().getReachableMethods().listener());
    }

    if (Scene.v().hasCallGraph()) {
      SootClass sc = Scene.v().getMainClass();
      SootMethod sm = getFirstMethod(sc);
      // logger.debug("got first method");
      ArrayList<MethInfo> tgts = getTgtMethods(sm, true);
      // logger.debug("got tgt methods");
      ArrayList<MethInfo> srcs = getSrcMethods(sm, true);
      // logger.debug("got src methods");
      CallGraphInfo info = new CallGraphInfo(sm, tgts, srcs);
      // logger.debug("will handle new call graph");
      InteractionHandler.v().handleCallGraphStart(info, this);
    }
  }

  private SootMethod getFirstMethod(SootClass sc) {
    ArrayList paramTypes = new ArrayList();
    paramTypes.add(soot.ArrayType.v(soot.RefType.v("java.lang.String"), 1));
    SootMethod sm = sc.getMethodUnsafe("main", paramTypes, soot.VoidType.v());
    if (sm != null) {
      return sm;
    } else {
      return (SootMethod) sc.getMethods().get(0);
    }
  }

  public void handleNextMethod() {
    if (!getNextMethod().hasActiveBody()) {
      return;
    }
    ArrayList<MethInfo> tgts = getTgtMethods(getNextMethod(), true);
    // System.out.println("for: "+getNextMethod().getName()+" tgts: "+tgts);
    ArrayList<MethInfo> srcs = getSrcMethods(getNextMethod(), true);
    // System.out.println("for: "+getNextMethod().getName()+" srcs: "+srcs);
    CallGraphInfo info = new CallGraphInfo(getNextMethod(), tgts, srcs);
    // System.out.println("sending next method");
    InteractionHandler.v().handleCallGraphPart(info);
    // handleNextMethod();
  }

  private SootMethod nextMethod;

  public void setNextMethod(SootMethod m) {
    nextMethod = m;
  }

  public SootMethod getNextMethod() {
    return nextMethod;
  }

  public void setShowLibMeths(boolean b) {
    showLibMeths = b;
  }

  public boolean isShowLibMeths() {
    return showLibMeths;
  }

}
