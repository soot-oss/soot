package soot.toolkits.scalar;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2021 Marc Miltenberger
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
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

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

    protected final Set<AssignStmt> constantInitializers;
    protected final Set<Unit> uses;
    protected final TreeSet<Integer> nonConstantDefs;
    public boolean invalid;
    private Unit[] indexToStmt;

    public Cluster(Set<Unit> uses, Set<AssignStmt> constantDefs, TreeSet<Integer> nonConstantDefs, Unit[] indexToStmt) {
      this.uses = uses;
      this.constantInitializers = constantDefs;
      this.nonConstantDefs = nonConstantDefs;
      this.indexToStmt = indexToStmt;
    }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder("Constant Initializers:\n");
      for (Unit i : constantInitializers) {
        sb.append("\t").append(i).append("\n");
      }
      sb.append("Non-Constant Definitions:\n");
      for (Integer i : nonConstantDefs) {
        sb.append("\t").append(indexToStmt[i]).append("\n");
      }
      sb.append("Uses:\n");
      for (Unit i : uses) {
        sb.append("\t").append(i).append("\n");
      }
      return sb.toString();
    }

  }

  @Override
  protected void internalTransform(Body body, String phaseName, Map<String, String> options) {
    final Options o = Options.v();
    if (o.verbose()) {
      logger.debug("[" + body.getMethod().getName() + "] Splitting for shared initialization of locals...");
    }

    if (throwAnalysis == null) {
      throwAnalysis = Scene.v().getDefaultThrowAnalysis();
    }

    if (!omitExceptingUnitEdges) {
      omitExceptingUnitEdges = o.omit_excepting_unit_edges();
    }

    DexNullThrowTransformer dexNull = DexNullThrowTransformer.v();
    dexNull.transform(body);
    CopyPropagator cp = CopyPropagator.v();
    cp.transform(body);
    dexNull.transform(body);
    ConstantPropagatorAndFolder.v().transform(body);

    DexNullArrayRefTransformer dexNullArrayRef = DexNullArrayRefTransformer.v();
    dexNullArrayRef.transform(body);
    FlowSensitiveConstantPropagator.v().transform(body);
    cp.transform(body);

    dexNull.transform(body);
    dexNullArrayRef.transform(body);

    DeadAssignmentEliminator.v().transform(body);
    cp.transform(body);
    transformOnly(body);
  }

  /**
   * Sets a value on whether to act as a normal local splitter, making soot.toolkits.scalar.LocalSplitter redundant.
   * 
   * @param actAsLocalSplitter
   * @return this
   */
  public SharedInitializationLocalSplitter setActAsNormalLocalSplitter(boolean actAsLocalSplitter) {
    this.actAsNormalLocalSplitter = actAsLocalSplitter;
    return this;
  }

  static class ClusterSet extends HashSet<Cluster> {

    private int max = -1;
    private int min = -1;

    @Override
    public boolean add(Cluster e) {
      updateMinMax(e.nonConstantDefs);
      return super.add(e);
    }

    public boolean mayOverlapWith(TreeSet<Integer> s) {
      int myMax = max;
      int myMin = min;
      int otherMax = s.last();
      int otherMin = s.first();
      if (myMax < otherMin || otherMax < myMin || myMin > otherMax || otherMin > myMax) {
        return false;
      }
      return true;
    }

    public void updateMinMax(TreeSet<Integer> nonConstantDefs) {
      min = Math.min(min, nonConstantDefs.first());
      max = Math.max(max, nonConstantDefs.last());
    }

  }

  public void transformOnly(Body body) {
    final ExceptionalUnitGraph graph
        = ExceptionalUnitGraphFactory.createExceptionalUnitGraph(body, throwAnalysis, omitExceptingUnitEdges);
    final LocalDefs defs = G.v().soot_toolkits_scalar_LocalDefsFactory().newLocalDefs(graph, true);
    final MultiMap<Local, Cluster> clustersPerLocal = new HashMultiMap<Local, Cluster>();
    final MultiMap<Local, Cluster> nonConstantClustersPerLocal = new HashMultiMap<Local, Cluster>() {
      @Override
      protected Set<Cluster> newSet() {
        return new ClusterSet();
      }
    };

    final Map<Unit, Integer> stmtToIndex = new HashMap<>();
    final Chain<Unit> units = body.getUnits();
    final Unit[] indexToStmt = new Unit[units.size()];
    int idx = 0;
    for (Unit s : units) {
      stmtToIndex.put(s, idx);
      indexToStmt[idx] = s;
      idx++;

    }
    for (Unit s : units) {
      nextUse: for (Iterator<ValueBox> iterator = s.getUseBoxesIterator(); iterator.hasNext();) {
        ValueBox useBox = iterator.next();
        Value v = useBox.getValue();
        if (v instanceof Local) {
          Local luse = (Local) v;
          Iterator<Unit> allAffectingDefs = defs.getDefsOfAtIterator(luse, s);

          Set<AssignStmt> constantDefs = new HashSet<>();
          TreeSet<Integer> nonConstantDefs = null;

          while (allAffectingDefs.hasNext()) {
            Unit affect = allAffectingDefs.next();
            if (affect instanceof DefinitionStmt) {
              DefinitionStmt def = (DefinitionStmt) affect;
              Value rop = def.getRightOp();
              if (rop instanceof Constant) {
                constantDefs.add((AssignStmt) def);
              } else {
                if (nonConstantDefs == null) {
                  nonConstantDefs = new TreeSet<>();
                }
                int actualidx = stmtToIndex.get(def);
                nonConstantDefs.add(actualidx);
              }
            }
          }
          Cluster use = null;
          if (nonConstantDefs != null) {
            Set<Cluster> set = nonConstantClustersPerLocal.get(luse);
            if (set instanceof ClusterSet) {
              ClusterSet c = (ClusterSet) set;
              if (c.mayOverlapWith(nonConstantDefs)) {
                Iterator<Cluster> it = c.iterator();
                while (it.hasNext()) {
                  Cluster existing = it.next();

                  // the idea is: When there is an overlap in any non-constant definition units,
                  // we need to merge them, since two different usages have overlapping definitions,
                  // i.e. we can only change all these uses
                  if (!existing.invalid && intersects(existing.nonConstantDefs, nonConstantDefs)) {
                    if (use == null) {
                      use = existing;
                    }
                    // we have an overlap
                    use.uses.add(s);
                    use.constantInitializers.addAll(constantDefs);
                    use.nonConstantDefs.addAll(nonConstantDefs);
                    c.updateMinMax(nonConstantDefs);

                    if (use != existing) {
                      clustersPerLocal.remove(luse, existing);
                      // we only keep the new definition with an overlap
                      it.remove();
                      existing.invalid = true;
                    }
                  }

                }
              }
            }
          }
          if (use == null) {
            Set<Unit> useset = new HashSet<>();
            useset.add(s);
            Cluster c = new Cluster(useset, constantDefs, nonConstantDefs, indexToStmt);
            clustersPerLocal.put(luse, c);
            if (nonConstantDefs != null) {
              nonConstantClustersPerLocal.put(luse, c);
            }
          }
        }
      }
    }

    final Chain<Local> locals = body.getLocals();
    int w = 0;
    for (Local lcl : clustersPerLocal.keySet()) {
      Set<Cluster> clusters = clustersPerLocal.get(lcl);
      if (clusters.size() <= 1) {
        // Not interesting
        continue;
      }
      for (Cluster cluster : clusters) {
        if (cluster.invalid) {
          continue;
        }
        Set<AssignStmt> constantInit = cluster.constantInitializers;
        if (!actAsNormalLocalSplitter && constantInit.isEmpty()) {
          continue;
        }
        // we have an overlap, we need to split.
        Local newLocal = (Local) lcl.clone();
        newLocal.setName(newLocal.getName() + '_' + ++w);
        locals.add(newLocal);
        for (Unit assignS : constantInit) {
          AssignStmt assign = (AssignStmt) assignS;
          if (assign == null) {
            throw new AssertionError("Wrong indice");
          }
          AssignStmt newAssign = Jimple.v().newAssignStmt(newLocal, assign.getRightOp());
          units.insertAfter(newAssign, assign);
          CopyPropagator.copyLineTags(newAssign.getUseBoxesIterator().next(), assign);
        }

        Set<Unit> uses = cluster.uses;
        for (Unit use : uses) {
          if (use == null) {
            throw new AssertionError("Wrong indice");
          }
          replaceLocalsInUnitUses(use, lcl, newLocal);
        }
        TreeSet<Integer> nonConstantDefs = cluster.nonConstantDefs;
        if (nonConstantDefs != null) {
          for (int i : nonConstantDefs) {
            DefinitionStmt def = (DefinitionStmt) indexToStmt[i];
            if (def == null) {
              throw new AssertionError("Wrong indice");
            }
            final ValueBox box = def.getLeftOpBox();
            if (box.getValue() == lcl) {
              box.setValue(newLocal);
            }
          }
        }
      }
    }
    UnusedLocalEliminator.v().transform(body);
  }

  private boolean intersects(TreeSet<Integer> t1, TreeSet<Integer> t2) {
    int m1Min = t1.first();
    int m2Min = t2.first();
    int m1Max = t1.last();
    int m2Max = t2.last();
    if (m1Max < m2Min || m2Max < m1Min || m1Min > m2Max || m2Min > m1Max) {
      return false;
    } else {
      Set<Integer> smaller;
      Set<Integer> larger;
      if (t1.size() < t2.size()) {
        smaller = t1;
        larger = t2;
      } else {
        smaller = t2;
        larger = t1;
      }
      for (Integer i : smaller) {
        if (larger.contains(i)) {
          return true;
        }
      }
      return false;
    }
  }

  private void replaceLocalsInUnitUses(Unit change, Value oldLocal, Local newLocal) {
    for (Iterator<ValueBox> iterator = change.getUseBoxesIterator(); iterator.hasNext();) {
      ValueBox u = iterator.next();
      if (u.getValue() == oldLocal) {
        u.setValue(newLocal);
      }
    }
  }
}
