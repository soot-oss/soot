package soot.toolkits.scalar;

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
import soot.jimple.IdentityStmt;
import soot.jimple.Jimple;
import soot.jimple.toolkits.scalar.ConstantPropagatorAndFolder;
import soot.jimple.toolkits.scalar.CopyPropagator;
import soot.jimple.toolkits.scalar.DeadAssignmentEliminator;
import soot.options.Options;
import soot.toolkits.exceptions.ThrowAnalysis;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.util.HashMultiMap;
import soot.util.MultiMap;

//@formatter:off
/**
 *
 * With the following code <code>
 * $u2#6 = 0; interfaceinvoke $u5#30.<Foo: void setMomentary(android.view.View,boolean)>($u4, $u2#6);
 * interfaceinvoke $u5#56.<Foo: void setSelectedIndex(android.view.View,int)>($u4, $u2#6);
 * </code>
 *
 * there is a problem since $u2#6 will be boolean as well as int. A cast from boolean to int or vice versa is not valid in
 * Java. The local splitter does not split the local since it would require the introduction of a new initialization
 * statement. Therefore, we split for each usage of a constant variable, such as: <code>
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

  static class Cluster {
    List<Unit> constantInitializers;
    private Unit use;

    public Cluster(Unit use, List<Unit> constantInitializers) {
      this.use = use;
      this.constantInitializers = constantInitializers;
    }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("Constant intializers:\n");
      for (Unit r : constantInitializers) {
        sb.append("\n - " + toStringUnit(r));
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

    ConstantPropagatorAndFolder.v().transform(body);

    DexNullThrowTransformer.v().transform(body);
    DexNullArrayRefTransformer.v().transform(body);
    FlowSensitiveConstantPropagator.v().transform(body);
    CopyPropagator.v().transform(body);

    DexNullThrowTransformer.v().transform(body);
    DexNullArrayRefTransformer.v().transform(body);

    DeadAssignmentEliminator.v().transform(body);
    CopyPropagator.v().transform(body);

    ExceptionalUnitGraph graph = new ExceptionalUnitGraph(body, throwAnalysis, omitExceptingUnitEdges);

    final LocalDefs defs = G.v().soot_toolkits_scalar_LocalDefsFactory().newLocalDefs(graph, true);

    MultiMap<Local, Cluster> clustersPerLocal = new HashMultiMap<Local, Cluster>();

    for (Unit s : body.getUnits()) {
      nextUse: for (ValueBox useBox : s.getUseBoxes()) {
        Value v = useBox.getValue();
        if (v instanceof Local) {
          Local luse = (Local) v;
          List<Unit> allAffectingDefs = defs.getDefsOfAt(luse, s);
          for (Unit def : allAffectingDefs) {
            if (def instanceof IdentityStmt) {
              continue nextUse;
            }

            AssignStmt assign = (AssignStmt) def;
            if (!(assign.getRightOp() instanceof Constant)) {
              // Make sure we are only affected by constant definitions
              continue nextUse;
            }
          }
          Cluster c = new Cluster(s, allAffectingDefs);
          clustersPerLocal.put(luse, c);
        }
      }
    }

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
        for (Unit u : cluster.constantInitializers) {
          AssignStmt assign = (AssignStmt) u;
          AssignStmt newAssign = Jimple.v().newAssignStmt(newLocal, (Value) assign.getRightOp());
          body.getUnits().insertAfter(newAssign, assign);
        }

        newLocal.setName(newLocal.getName() + '_' + ++w);
        body.getLocals().add(newLocal);
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
