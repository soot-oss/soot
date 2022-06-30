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

    protected final List<Unit> constantInitializers;
    protected final Unit use;

    public Cluster(Unit use, List<Unit> constantInitializers) {
      this.use = use;
      this.constantInitializers = constantInitializers;
    }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("Constant intializers:\n");
      for (Unit r : constantInitializers) {
        sb.append("\n - ").append(toStringUnit(r));
      }
      return sb.toString();
    }

    private String toStringUnit(Unit u) {
      return u + " (" + System.identityHashCode(u) + ")";
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

    if (omitExceptingUnitEdges == false) {
      omitExceptingUnitEdges = Options.v().omit_excepting_unit_edges();
    }

    CopyPropagator.v().transform(body);
    ConstantPropagatorAndFolder.v().transform(body);

    DexNullThrowTransformer.v().transform(body);
    DexNullArrayRefTransformer.v().transform(body);
    FlowSensitiveConstantPropagator.v().transform(body);
    CopyPropagator.v().transform(body);

    DexNullThrowTransformer.v().transform(body);
    DexNullArrayRefTransformer.v().transform(body);

    DeadAssignmentEliminator.v().transform(body);
    CopyPropagator.v().transform(body);

    final ExceptionalUnitGraph graph
        = ExceptionalUnitGraphFactory.createExceptionalUnitGraph(body, throwAnalysis, omitExceptingUnitEdges);
    final LocalDefs defs = G.v().soot_toolkits_scalar_LocalDefsFactory().newLocalDefs(graph, true);
    final MultiMap<Local, Cluster> clustersPerLocal = new HashMultiMap<Local, Cluster>();

    final Chain<Unit> units = body.getUnits();
    for (Unit s : units) {
      for (ValueBox useBox : s.getUseBoxes()) {
        Value v = useBox.getValue();
        if (v instanceof Local) {
          Local luse = (Local) v;
          List<Unit> allAffectingDefs = defs.getDefsOfAt(luse, s);
          if (allAffectingDefs.isEmpty()) {
            continue;
          }
          // Make sure we are only affected by Constant definitions via AssignStmt
          if (!allAffectingDefs.stream()
              .allMatch(u -> (u instanceof AssignStmt) && (((AssignStmt) u).getRightOp() instanceof Constant))) {
            continue;
          }
          clustersPerLocal.put(luse, new Cluster(s, allAffectingDefs));
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
        // we have an overlap, we need to split.
        Local newLocal = (Local) lcl.clone();
        newLocal.setName(newLocal.getName() + '_' + ++w);
        locals.add(newLocal);

        for (Unit u : cluster.constantInitializers) {
          AssignStmt assign = (AssignStmt) u;
          AssignStmt newAssign = Jimple.v().newAssignStmt(newLocal, assign.getRightOp());
          units.insertAfter(newAssign, assign);
          CopyPropagator.copyLineTags(newAssign.getUseBoxes().get(0), assign);
        }

        replaceLocalsInUnitUses(cluster.use, lcl, newLocal);
      }
    }
  }

  private void replaceLocalsInUnitUses(Unit change, Value oldLocal, Local newLocal) {
    for (ValueBox u : change.getUseBoxes()) {
      if (u.getValue() == oldLocal) {
        u.setValue(newLocal);
      }
    }
  }
}
