package soot.toolkits.scalar;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1999 Phong Co
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

import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Body;
import soot.BodyTransformer;
import soot.G;
import soot.Local;
import soot.Scene;
import soot.Singletons;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.dexpler.DexNullArrayRefTransformer;
import soot.dexpler.DexNullThrowTransformer;
import soot.jimple.AssignStmt;
import soot.jimple.Constant;
import soot.jimple.DefinitionStmt;
import soot.jimple.Jimple;
import soot.jimple.toolkits.scalar.ConstantPropagatorAndFolder;
import soot.jimple.toolkits.scalar.CopyPropagator;
import soot.jimple.toolkits.scalar.DeadAssignmentEliminator;
import soot.options.Options;
import soot.toolkits.exceptions.ThrowAnalysis;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.ExceptionalUnitGraphFactory;
import soot.util.Chain;
import soot.util.HashMultiMap;
import soot.util.MultiMap;

//@formatter:off
/**
 * There is a problem with the following code <code>
 * $u2#6 = 0;
 * interfaceinvoke $u5#30.<Foo: void setMomentary(android.view.View,boolean)>($u4, $u2#6);
 * interfaceinvoke $u5#56.<Foo: void setSelectedIndex(android.view.View,int)>($u4, $u2#6);
 * </code>
 *
 * since $u2#6 will be boolean as well as int. A cast from boolean to int or vice versa is not valid in Java. The local
 * splitter does not split the local since it would require the introduction of a new initialization statement. Therefore, we
 * split for each usage of a constant variable, such as: <code>
 * $u2#6 = 0;
 * $u2#6_2 = 0; 
 * interfaceinvoke $u5#30.<Foo: void setMomentary(android.view.View,boolean)>($u4, $u2#6);
 * interfaceinvoke $u5#56.<Foo: void setSelectedIndex(android.view.View,int)>($u4, $u2#6_2);
 * </code>
 * 
 * @author Marc Miltenberger
 */

// @formatter:on
public class SharedInitializationLocalSplitter extends BodyTransformer {
  private static final Logger logger = LoggerFactory.getLogger(SharedInitializationLocalSplitter.class);

  protected ThrowAnalysis throwAnalysis;
  protected boolean omitExceptingUnitEdges;

  private boolean actAsNormalLocalSplitter;

  public SharedInitializationLocalSplitter(Singletons.Global g) {
  }

  public SharedInitializationLocalSplitter(ThrowAnalysis ta) {
    this(ta, false);
  }

  public SharedInitializationLocalSplitter(ThrowAnalysis ta, boolean omitExceptingUnitEdges) {
    this.throwAnalysis = ta;
    this.omitExceptingUnitEdges = omitExceptingUnitEdges;
  }

  public static SharedInitializationLocalSplitter v() {
    return G.v().soot_toolkits_scalar_SharedInitializationLocalSplitter();
  }

  private static final class Cluster {

    protected final BitSet constantInitializers;
    protected final BitSet uses;
    protected final BitSet nonConstantDefs;

    public Cluster(BitSet uses, BitSet constantDefs, BitSet nonConstantDefs) {
      this.uses = uses;
      this.constantInitializers = constantDefs;
      this.nonConstantDefs = nonConstantDefs;
    }

  }

  @Override
  protected void internalTransform(Body body, String phaseName, Map<String, String> options) {
    if (Options.v().verbose()) {
      logger.debug("[" + body.getMethod().getName() + "] Splitting for shared initialization of locals...");
    }

    if (throwAnalysis == null) {
      throwAnalysis = Scene.v().getDefaultThrowAnalysis();
    }

    if (!omitExceptingUnitEdges) {
      omitExceptingUnitEdges = Options.v().omit_excepting_unit_edges();
    }

    DexNullThrowTransformer.v().transform(body);
    CopyPropagator.v().transform(body);
    DexNullThrowTransformer.v().transform(body);
    ConstantPropagatorAndFolder.v().transform(body);

    DexNullArrayRefTransformer.v().transform(body);
    FlowSensitiveConstantPropagator.v().transform(body);
    CopyPropagator.v().transform(body);

    DexNullThrowTransformer.v().transform(body);
    DexNullArrayRefTransformer.v().transform(body);

    DeadAssignmentEliminator.v().transform(body);
    CopyPropagator.v().transform(body);
    transformOnly(body);
  }

  /**
   * Sets a value on whether to act as a normal local splitter, making 
   * soot.toolkits.scalar.LocalSplitter redundant.
   * @param actAsLocalSplitter
   * @return this
   */
  public SharedInitializationLocalSplitter setActAsNormalLocalSplitter(boolean actAsLocalSplitter) {
    this.actAsNormalLocalSplitter = actAsLocalSplitter;
    return this;
  }

  public void transformOnly(Body body) {

    final ExceptionalUnitGraph graph
        = ExceptionalUnitGraphFactory.createExceptionalUnitGraph(body, throwAnalysis, omitExceptingUnitEdges);
    final LocalDefs defs = G.v().soot_toolkits_scalar_LocalDefsFactory().newLocalDefs(graph, true);
    final MultiMap<Local, Cluster> clustersPerLocal = new HashMultiMap<Local, Cluster>();

    final Map<Unit, Integer> stmtToIndex = new HashMap<>();
    final Map<Integer, Unit> indexToStmt = new HashMap<>();
    final Chain<Unit> units = body.getUnits();
    int idx = 0;
    for (Unit s : units) {
      stmtToIndex.put(s, idx);
      indexToStmt.put(idx, s);
      idx++;

    }

    for (Unit s : units) {
      nextUse: for (ValueBox useBox : s.getUseBoxes()) {
        Value v = useBox.getValue();
        if (v instanceof Local) {
          Local luse = (Local) v;
          List<Unit> allAffectingDefs = defs.getDefsOfAt(luse, s);

          BitSet constantDefs = new BitSet(idx);
          BitSet nonConstantDefs = null;

          for (Unit affect : allAffectingDefs) {
            if (affect instanceof DefinitionStmt) {
              DefinitionStmt def = (DefinitionStmt) affect;
              int actualidx = stmtToIndex.get(def);
              if (def.getRightOp() instanceof Constant) {
                constantDefs.set(actualidx);
              } else {
                if (nonConstantDefs == null) {
                  nonConstantDefs = new BitSet(idx);
                }
                nonConstantDefs.set(actualidx);
              }
            }
          }
          int useidx = stmtToIndex.get(s);
          BitSet useset = new BitSet(useidx);
          useset.set(useidx);
          if (nonConstantDefs != null) {
            Iterator<Cluster> it = clustersPerLocal.get(luse).iterator();
            while (it.hasNext()) {
              Cluster existing = it.next();
              if (existing.nonConstantDefs == null) {
                continue;
              }

              //the idea is: When there is an overlap in any non-constant definition units,
              //we need to merge them, since two different usages have overlapping definitions, 
              //i.e. we can only change all these uses 
              if (existing.nonConstantDefs.intersects(nonConstantDefs)) {
                //we have an overlap
                useset.or(existing.uses);
                constantDefs.or(existing.constantInitializers);
                nonConstantDefs.or(existing.nonConstantDefs);

                //we only keep the new definition with an overlap
                it.remove();
              }
            }
          }
          clustersPerLocal.put(luse, new Cluster(useset, constantDefs, nonConstantDefs));
        }
      }
    }

    final Chain<Local> locals = body.getLocals();
    int w = 0;
    for (Local lcl : clustersPerLocal.keySet()) {
      Set<Cluster> clusters = clustersPerLocal.get(lcl);
      if (clusters.size() == 1) {
        // Not interesting
        continue;
      }
      for (Cluster cluster : clusters) {
        // we have an overlap, we need to split.
        Local newLocal = (Local) lcl.clone();
        newLocal.setName(newLocal.getName() + '_' + ++w);
        locals.add(newLocal);
        BitSet constantInit = cluster.constantInitializers;
        if (!actAsNormalLocalSplitter && constantInit.isEmpty()) {
          continue;
        }
        for (int i = constantInit.nextSetBit(0); i != -1; i = constantInit.nextSetBit(i + 1)) {
          AssignStmt assign = (AssignStmt) indexToStmt.get(i);
          if (assign == null) {
            throw new AssertionError("Wrong indice");
          }
          AssignStmt newAssign = Jimple.v().newAssignStmt(newLocal, assign.getRightOp());
          units.insertAfter(newAssign, assign);
          CopyPropagator.copyLineTags(newAssign.getUseBoxes().get(0), assign);
        }

        BitSet uses = cluster.uses;
        for (int i = uses.nextSetBit(0); i != -1; i = uses.nextSetBit(i + 1)) {
          Unit use = indexToStmt.get(i);
          if (use == null) {
            throw new AssertionError("Wrong indice");
          }
          replaceLocalsInUnitUses(use, lcl, newLocal);
        }
        BitSet nonConstantDefs = cluster.nonConstantDefs;
        if (nonConstantDefs != null) {
          for (int i = nonConstantDefs.nextSetBit(0); i != -1; i = nonConstantDefs.nextSetBit(i + 1)) {
            DefinitionStmt def = (DefinitionStmt) indexToStmt.get(i);
            if (def == null) {
              throw new AssertionError("Wrong indice");
            }
            if (def.getLeftOp() == lcl) {
              def.getLeftOpBox().setValue(newLocal);
            }
          }
        }
      }
    }
    UnusedLocalEliminator.v().transform(body);
  }

  private void replaceLocalsInUnitUses(Unit change, Value oldLocal, Local newLocal) {
    for (ValueBox u : change.getUseBoxes()) {
      if (u.getValue() == oldLocal) {
        u.setValue(newLocal);
      }
    }
  }
}
