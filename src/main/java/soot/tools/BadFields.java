package soot.tools;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2003 Ondrej Lhotak
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

import soot.PackManager;
import soot.PrimType;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Transform;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.FieldRef;
import soot.jimple.InvokeExpr;
import soot.jimple.StaticFieldRef;
import soot.jimple.Stmt;

public class BadFields extends SceneTransformer {
  private static final Logger logger = LoggerFactory.getLogger(BadFields.class);

  public static void main(String[] args) {
    PackManager.v().getPack("cg").add(new Transform("cg.badfields", new BadFields()));
    soot.Main.main(args);
  }

  private SootClass lastClass;
  private SootClass currentClass;

  protected void internalTransform(String phaseName, Map<String, String> options) {
    lastClass = null;

    for (Iterator<SootClass> clIt = Scene.v().getApplicationClasses().iterator(); clIt.hasNext();) {

      final SootClass cl = clIt.next();
      currentClass = cl;
      handleClass(cl);
      for (Iterator<SootMethod> it = cl.methodIterator(); it.hasNext();) {
        handleMethod(it.next());
      }
    }
    Scene.v().setCallGraph(Scene.v().internalMakeCallGraph());
  }

  private void handleClass(SootClass cl) {
    for (Iterator<SootField> fIt = cl.getFields().iterator(); fIt.hasNext();) {
      final SootField f = fIt.next();
      if (!f.isStatic()) {
        continue;
      }
      String typeName = f.getType().toString();
      if (typeName.equals("java.lang.Class")) {
        continue;
      }
      if (f.isFinal()) {
        if (f.getType() instanceof PrimType) {
          continue;
        }
        if (typeName.equals("java.io.PrintStream")) {
          continue;
        }
        if (typeName.equals("java.lang.String")) {
          continue;
        }
        if (typeName.equals("java.lang.Object")) {
          continue;
        }
        if (typeName.equals("java.lang.Integer")) {
          continue;
        }
        if (typeName.equals("java.lang.Boolean")) {
          continue;
        }
      }
      warn("Bad field " + f);
    }
  }

  private void warn(String warning) {
    if (lastClass != currentClass) {
      logger.debug("" + "In class " + currentClass);
    }
    lastClass = currentClass;
    logger.debug("" + "  " + warning);
  }

  private void handleMethod(SootMethod m) {
    if (!m.isConcrete()) {
      return;
    }
    for (Iterator<ValueBox> bIt = m.retrieveActiveBody().getUseAndDefBoxes().iterator(); bIt.hasNext();) {
      final ValueBox b = bIt.next();
      Value v = b.getValue();
      if (!(v instanceof StaticFieldRef)) {
        continue;
      }
      StaticFieldRef sfr = (StaticFieldRef) v;
      SootField f = sfr.getField();
      if (!f.getDeclaringClass().getName().equals("java.lang.System")) {
        continue;
      }
      if (f.getName().equals("err")) {
        logger.debug("" + "Use of System.err in " + m);
      }
      if (f.getName().equals("out")) {
        logger.debug("" + "Use of System.out in " + m);
      }
    }
    for (Iterator<Unit> sIt = m.getActiveBody().getUnits().iterator(); sIt.hasNext();) {
      final Stmt s = (Stmt) sIt.next();
      if (!s.containsInvokeExpr()) {
        continue;
      }
      InvokeExpr ie = s.getInvokeExpr();
      SootMethod target = ie.getMethod();
      if (target.getDeclaringClass().getName().equals("java.lang.System") && target.getName().equals("exit")) {
        warn("" + m + " calls System.exit");
      }
    }
    if (m.getName().equals("<clinit>")) {
      for (Iterator<Unit> sIt = m.getActiveBody().getUnits().iterator(); sIt.hasNext();) {
        final Stmt s = (Stmt) sIt.next();
        for (Iterator<ValueBox> bIt = s.getUseBoxes().iterator(); bIt.hasNext();) {
          final ValueBox b = bIt.next();
          Value v = b.getValue();
          if (v instanceof FieldRef) {
            warn(m.getName() + " reads field " + v);
          }
        }
        if (!s.containsInvokeExpr()) {
          continue;
        }
        InvokeExpr ie = s.getInvokeExpr();
        SootMethod target = ie.getMethod();
        calls(target);
      }
    }
  }

  private void calls(SootMethod target) {
    if (target.getName().equals("<init>")) {
      if (target.getDeclaringClass().getName().equals("java.io.PrintStream")) {
        return;
      }
      if (target.getDeclaringClass().getName().equals("java.lang.Boolean")) {
        return;
      }
      if (target.getDeclaringClass().getName().equals("java.lang.Integer")) {
        return;
      }
      if (target.getDeclaringClass().getName().equals("java.lang.String")) {
        return;
      }
      if (target.getDeclaringClass().getName().equals("java.lang.Object")) {
        return;
      }
    }
    if (target.getName().equals("getProperty")) {
      if (target.getDeclaringClass().getName().equals("java.lang.System")) {
        return;
      }
    }
    if (target.getName().equals("charAt")) {
      if (target.getDeclaringClass().getName().equals("java.lang.String")) {
        return;
      }
    }
    warn("<clinit> invokes " + target);
  }
}
