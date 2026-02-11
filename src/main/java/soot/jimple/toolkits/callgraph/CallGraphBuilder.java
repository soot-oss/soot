package soot.jimple.toolkits.callgraph;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2002 Ondrej Lhotak
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

import com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.AnySubType;
import soot.EntryPoints;
import soot.FastHierarchy;
import soot.Local;
import soot.MethodOrMethodContext;
import soot.PointsToAnalysis;
import soot.PointsToSet;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.Type;
import soot.Value;
import soot.jimple.IntConstant;
import soot.jimple.NewArrayExpr;
import soot.jimple.spark.pag.AllocNode;
import soot.jimple.spark.pag.ArrayElement;
import soot.jimple.spark.pag.Node;
import soot.jimple.spark.sets.P2SetVisitor;
import soot.jimple.spark.sets.PointsToSetInternal;
import soot.util.queue.QueueReader;

/**
 * Models the call graph.
 *
 * @author Ondrej Lhotak
 */
public class CallGraphBuilder {
  private static final Logger logger = LoggerFactory.getLogger(CallGraphBuilder.class);

  private final PointsToAnalysis pa;
  private final ReachableMethods reachables;
  private final OnFlyCallGraphBuilder ofcgb;
  private final CallGraph cg;

  private final Scene scene = Scene.v();

  /**
   * This constructor builds the incomplete hack call graph for the Dava ThrowFinder. It uses all application class methods
   * as entry points, and it ignores any calls by non-application class methods. Don't use this constructor if you need a
   * real call graph.
   */
  public CallGraphBuilder() {
    logger.warn("using incomplete callgraph containing " + "only application classes.");
    this.pa = soot.jimple.toolkits.pointer.DumbPointerAnalysis.v();
    this.cg = Scene.v().internalMakeCallGraph();
    Scene.v().setCallGraph(cg);
    List<MethodOrMethodContext> entryPoints = new ArrayList<MethodOrMethodContext>();
    entryPoints.addAll(EntryPoints.v().methodsOfApplicationClasses());
    entryPoints.addAll(EntryPoints.v().implicit());
    this.reachables = new ReachableMethods(cg, entryPoints);
    this.ofcgb = new OnFlyCallGraphBuilder(new ContextInsensitiveContextManager(cg), reachables, true);
  }

  /**
   * This constructor builds a complete call graph using the given PointsToAnalysis to resolve virtual calls.
   */
  public CallGraphBuilder(PointsToAnalysis pa) {
    this.pa = pa;
    this.cg = Scene.v().internalMakeCallGraph();
    Scene.v().setCallGraph(cg);
    this.reachables = Scene.v().getReachableMethods();
    this.ofcgb = createCGBuilder(makeContextManager(cg), reachables);
  }

  protected OnFlyCallGraphBuilder createCGBuilder(ContextManager cm, ReachableMethods reachables2) {
    return new OnFlyCallGraphBuilder(cm, reachables);
  }

  public CallGraph getCallGraph() {
    return cg;
  }

  public ReachableMethods reachables() {
    return reachables;
  }

  public static ContextManager makeContextManager(CallGraph cg) {
    return new ContextInsensitiveContextManager(cg);
  }

  public void build() {
    for (QueueReader<MethodOrMethodContext> worklist = reachables.listener();;) {
      ofcgb.processReachables();
      reachables.update();
      if (!worklist.hasNext()) {
        break;
      }
      final MethodOrMethodContext momc = worklist.next();
      if (momc != null && !process(momc)) {
        break;
      }
    }
  }

  /**
   * Processes one item.
   * 
   * @param momc
   *          the method or method context
   * @return true if the next item should be processed.
   */
  protected boolean process(MethodOrMethodContext momc) {
    processReceivers(momc);
    processBases(momc);
    processArrays(momc);
    processStringConstants(momc);
    return true;
  }

  protected void processStringConstants(final MethodOrMethodContext momc) {
    List<Local> stringConstants = ofcgb.methodToStringConstants().get(momc.method());
    if (stringConstants != null) {
      for (Local stringConstant : stringConstants) {
        Collection<String> possibleStringConstants = pa.reachingObjects(stringConstant).possibleStringConstants();
        if (possibleStringConstants == null) {
          ofcgb.addStringConstant(stringConstant, momc.context(), null);
        } else {
          for (String constant : possibleStringConstants) {
            ofcgb.addStringConstant(stringConstant, momc.context(), constant);
          }
        }
      }
    }
  }

  protected void processArrays(final MethodOrMethodContext momc) {
    List<Local> argArrays = ofcgb.methodToInvokeBases().get(momc.method());
    if (argArrays != null) {
      for (final Local argArray : argArrays) {
        PointsToSet pts = pa.reachingObjects(argArray);
        if (pts instanceof PointsToSetInternal) {
          PointsToSetInternal ptsi = (PointsToSetInternal) pts;
          ptsi.forall(new P2SetVisitor() {
            @Override
            public void visit(Node n) {
              assert (n instanceof AllocNode);
              AllocNode an = (AllocNode) n;
              Object newExpr = an.getNewExpr();
              ofcgb.addInvokeArgDotField(argArray, an.dot(ArrayElement.v()));
              if (newExpr instanceof NewArrayExpr) {
                NewArrayExpr nae = (NewArrayExpr) newExpr;
                Value size = nae.getSize();
                if (size instanceof IntConstant) {
                  IntConstant arrSize = (IntConstant) size;
                  ofcgb.addPossibleArgArraySize(argArray, arrSize.value, momc.context());
                } else {
                  ofcgb.setArgArrayNonDetSize(argArray, momc.context());
                }
              }
            }
          });
        }
        for (Type t : pa.reachingObjectsOfArrayElement(pts).possibleTypes()) {
          ofcgb.addInvokeArgType(argArray, momc.context(), t);
        }
      }
    }
  }

  protected void processBases(final MethodOrMethodContext momc) {
    List<Local> bases = ofcgb.methodToInvokeArgs().get(momc.method());
    if (bases != null) {
      for (Local base : bases) {
        for (Type ty : getPossibleTypes(base)) {
          ofcgb.addBaseType(base, momc.context(), ty);
        }
      }
    }
  }

  protected void processReceivers(final MethodOrMethodContext momc) {
    List<Local> receivers = ofcgb.methodToReceivers().get(momc.method());
    if (receivers != null) {
      for (Local receiver : receivers) {
        for (Type type : getPossibleTypes(receiver)) {
          ofcgb.addType(receiver, momc.context(), type, null);
        }
      }
    }
  }

  protected Set<Type> getPossibleTypes(Local receiver) {
    Set<Type> possibleTypes = pa.reachingObjects(receiver).possibleTypes();
    FastHierarchy fh = scene.getOrMakeFastHierarchy();
    final Type receiverType = receiver.getType();
    for (Type i : possibleTypes) {
      if (!fh.canStoreType(i, receiverType)) {
        //a mismatch, so we restrict the set
        Set<Type> filteredPossibleTypes = new HashSet<>(possibleTypes.size());

        next: for (Type t : possibleTypes) {
          if (fh.canStoreType(t, receiverType)) {
            if (t instanceof AnySubType) {
              AnySubType at = (AnySubType) t;
              RefType atB = at.getBase();
              Iterable<SootClass> allSubClasses;
              SootClass base = atB.getSootClass();
              allSubClasses = getSubClasses(fh, base);
              for (SootClass sub : allSubClasses) {
                if (!fh.canStoreType(sub.getType(), receiverType)) {
                  //In this case, we have found a class that does not correspond
                  //to the sub type
                  //So, we'll just add all possible types instead
                  if (receiverType instanceof RefType) {
                    RefType rt = (RefType) receiverType;
                    for (SootClass sc : getSubClasses(fh, rt.getSootClass())) {
                      filteredPossibleTypes.add(sc.getType());
                    }
                  } else {
                    filteredPossibleTypes.add(receiverType);
                  }
                  continue next;
                }
              }
            }
            filteredPossibleTypes.add(t);
          }
        }
        return filteredPossibleTypes;
      }
    }
    return possibleTypes;
  }

  protected Iterable<SootClass> getSubClasses(FastHierarchy fh, SootClass base) {
    Iterable<SootClass> allSubClasses;
    if (base.isInterface()) {
      allSubClasses = Iterables.concat(Collections.singleton(base), fh.getAllSubinterfaces(base),
          fh.getAllImplementersOfInterface(base));
    } else {
      allSubClasses = Iterables.concat(Collections.singleton(base), fh.getSubclassesOf(base));
    }
    return allSubClasses;
  }
}
