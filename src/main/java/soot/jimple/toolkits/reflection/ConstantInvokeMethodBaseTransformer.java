package soot.jimple.toolkits.reflection;

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

import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Body;
import soot.G;
import soot.Local;
import soot.Scene;
import soot.SceneTransformer;
import soot.Singletons;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.InvokeExpr;
import soot.jimple.Jimple;
import soot.jimple.Stmt;
import soot.jimple.StringConstant;
import soot.util.Chain;

/**
 * This class creates a local for each string constant that is used as a base object to a reflective Method.invoke call.
 * Therefore, {@link soot.jimple.toolkits.callgraph.OnFlyCallGraphBuilder.TypeBasedReflectionModel} can handle such cases and
 * extend the call graph for edges to the specific java.lang.String method invoked by the reflective call.
 *
 * @author Manuel Benz created on 02.08.17
 */
public class ConstantInvokeMethodBaseTransformer extends SceneTransformer {
  private static final Logger logger = LoggerFactory.getLogger(ConstantInvokeMethodBaseTransformer.class);

  private final static String INVOKE_SIG
      = "<java.lang.reflect.Method: java.lang.Object invoke(java.lang.Object,java.lang.Object[])>";

  public ConstantInvokeMethodBaseTransformer(Singletons.Global g) {
  }

  public static ConstantInvokeMethodBaseTransformer v() {
    return G.v().soot_jimple_toolkits_reflection_ConstantInvokeMethodBaseTransformer();
  }

  @Override
  protected void internalTransform(String phaseName, Map<String, String> options) {
    final boolean verbose = options.containsKey("verbose");
    final Jimple jimp = Jimple.v();

    for (SootClass sootClass : Scene.v().getApplicationClasses()) {
      // In some rare cases we will have application classes that are not resolved due to being located in excluded packages
      // (e.g., the ServiceConnection class constructed by FlowDroid:
      // soot.jimple.infoflow.cfg.LibraryClassPatcher#patchServiceConnection)
      if (sootClass.resolvingLevel() < SootClass.BODIES) {
        continue;
      }
      for (SootMethod sootMethod : sootClass.getMethods()) {
        Body body = sootMethod.retrieveActiveBody();
        final Chain<Local> locals = body.getLocals();
        final Chain<Unit> units = body.getUnits();
        for (Iterator<Unit> iterator = units.snapshotIterator(); iterator.hasNext();) {
          Stmt s = (Stmt) iterator.next();

          if (s.containsInvokeExpr()) {
            InvokeExpr invokeExpr = s.getInvokeExpr();
            if (INVOKE_SIG.equals(invokeExpr.getMethod().getSignature())) {
              Value arg0 = invokeExpr.getArg(0);
              if (arg0 instanceof StringConstant) {
                Local newLocal = jimp.newLocal("sc" + locals.size(), arg0.getType());
                locals.add(newLocal);
                units.insertBefore(jimp.newAssignStmt(newLocal, (StringConstant) arg0), s);
                invokeExpr.setArg(0, newLocal);

                if (verbose) {
                  logger.debug("Replaced constant base object of Method.invoke() by local in: " + sootMethod.toString());
                }
              }
            }
          }
        }
      }
    }
  }
}
