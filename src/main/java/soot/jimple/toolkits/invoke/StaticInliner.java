package soot.jimple.toolkits.invoke;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1999 Patrick Lam, Raja Vallee-Rai
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
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.G;
import soot.Hierarchy;
import soot.PackManager;
import soot.PhaseOptions;
import soot.Scene;
import soot.SceneTransformer;
import soot.Singletons;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.JimpleBody;
import soot.jimple.Stmt;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.ExplicitEdgesPred;
import soot.jimple.toolkits.callgraph.Filter;
import soot.jimple.toolkits.callgraph.Targets;
import soot.jimple.toolkits.callgraph.TopologicalOrderer;
import soot.options.Options;
import soot.tagkit.Host;

/** Uses the Scene's currently-active InvokeGraph to inline monomorphic call sites. */
public class StaticInliner extends SceneTransformer {
  private static final Logger logger = LoggerFactory.getLogger(StaticInliner.class);

  public StaticInliner(Singletons.Global g) {
  }

  public static StaticInliner v() {
    return G.v().soot_jimple_toolkits_invoke_StaticInliner();
  }

  protected void internalTransform(String phaseName, Map options) {
    Filter explicitInvokesFilter = new Filter(new ExplicitEdgesPred());
    if (Options.v().verbose()) {
      logger.debug("[] Inlining methods...");
    }

    boolean enableNullPointerCheckInsertion = PhaseOptions.getBoolean(options, "insert-null-checks");
    boolean enableRedundantCastInsertion = PhaseOptions.getBoolean(options, "insert-redundant-casts");
    String modifierOptions = PhaseOptions.getString(options, "allowed-modifier-changes");
    float expansionFactor = PhaseOptions.getFloat(options, "expansion-factor");
    int maxContainerSize = PhaseOptions.getInt(options, "max-container-size");
    int maxInlineeSize = PhaseOptions.getInt(options, "max-inlinee-size");
    boolean rerunJb = PhaseOptions.getBoolean(options, "rerun-jb");

    HashMap instanceToStaticMap = new HashMap();

    CallGraph cg = Scene.v().getCallGraph();
    Hierarchy hierarchy = Scene.v().getActiveHierarchy();

    ArrayList<List<Host>> sitesToInline = new ArrayList<List<Host>>();

    computeAverageMethodSizeAndSaveOriginalSizes();
    // Visit each potential site in reverse pseudo topological order.
    {
      TopologicalOrderer orderer = new TopologicalOrderer(cg);
      orderer.go();
      List<SootMethod> order = orderer.order();
      ListIterator<SootMethod> it = order.listIterator(order.size());

      while (it.hasPrevious()) {
        SootMethod container = it.previous();
        if (methodToOriginalSize.get(container) == null) {
          continue;
        }

        if (!container.isConcrete()) {
          continue;
        }

        if (!explicitInvokesFilter.wrap(cg.edgesOutOf(container)).hasNext()) {
          continue;
        }

        JimpleBody b = (JimpleBody) container.retrieveActiveBody();

        List<Unit> unitList = new ArrayList<Unit>();
        unitList.addAll(b.getUnits());
        Iterator<Unit> unitIt = unitList.iterator();

        while (unitIt.hasNext()) {
          Stmt s = (Stmt) unitIt.next();
          if (!s.containsInvokeExpr()) {
            continue;
          }

          Iterator targets = new Targets(explicitInvokesFilter.wrap(cg.edgesOutOf(s)));
          if (!targets.hasNext()) {
            continue;
          }
          SootMethod target = (SootMethod) targets.next();
          if (targets.hasNext()) {
            continue;
          }

          if (!target.getDeclaringClass().isApplicationClass() || !target.isConcrete()) {
            continue;
          }

          if (!InlinerSafetyManager.ensureInlinability(target, s, container, modifierOptions)) {
            continue;
          }

          List<Host> l = new ArrayList<Host>();
          l.add(target);
          l.add(s);
          l.add(container);

          sitesToInline.add(l);
        }
      }
    }

    // Proceed to inline the sites, one at a time, keeping track of
    // expansion rates.
    {

      Iterator<List<Host>> sitesIt = sitesToInline.iterator();
      while (sitesIt.hasNext()) {
        List l = sitesIt.next();
        SootMethod inlinee = (SootMethod) l.get(0);
        int inlineeSize = ((JimpleBody) (inlinee.retrieveActiveBody())).getUnits().size();

        Stmt invokeStmt = (Stmt) l.get(1);

        SootMethod container = (SootMethod) l.get(2);
        int containerSize = ((JimpleBody) (container.retrieveActiveBody())).getUnits().size();

        if (inlineeSize + containerSize > maxContainerSize) {
          continue;
        }

        if (inlineeSize > maxInlineeSize) {
          continue;
        }

        if (inlineeSize + containerSize > expansionFactor * methodToOriginalSize.get(container).intValue()) {
          continue;
        }

        if (InlinerSafetyManager.ensureInlinability(inlinee, invokeStmt, container, modifierOptions)) {
          // Not that it is important to check right before inlining if the site is still valid.

          SiteInliner.inlineSite(inlinee, invokeStmt, container, options);
          if (rerunJb) {
            PackManager.v().getPack("jb").apply(container.getActiveBody());
          }
        }
      }
    }
  }

  private final HashMap<SootMethod, Integer> methodToOriginalSize = new HashMap<SootMethod, Integer>();

  private void computeAverageMethodSizeAndSaveOriginalSizes() {
    long sum = 0, count = 0;
    Iterator classesIt = Scene.v().getApplicationClasses().iterator();

    while (classesIt.hasNext()) {
      SootClass c = (SootClass) classesIt.next();

      Iterator methodsIt = c.methodIterator();
      while (methodsIt.hasNext()) {
        SootMethod m = (SootMethod) methodsIt.next();
        if (m.isConcrete()) {
          int size = ((JimpleBody) m.retrieveActiveBody()).getUnits().size();
          sum += size;
          methodToOriginalSize.put(m, new Integer(size));
          count++;
        }
      }
    }
    if (count == 0) {
      return;
    }
  }
}
