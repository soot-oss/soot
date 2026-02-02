package soot.toolkits.scalar;

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

import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import soot.Body;
import soot.Local;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.UnitPatchingChain;
import soot.VoidType;
import soot.jimple.AssignStmt;
import soot.jimple.Constant;
import soot.jimple.DoubleConstant;
import soot.jimple.FloatConstant;
import soot.jimple.InstanceFieldRef;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.Jimple;
import soot.jimple.LongConstant;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.StringConstant;
import soot.tagkit.DoubleConstantValueTag;
import soot.tagkit.FloatConstantValueTag;
import soot.tagkit.IntegerConstantValueTag;
import soot.tagkit.LongConstantValueTag;
import soot.tagkit.StringConstantValueTag;
import soot.tagkit.Tag;
import soot.util.Chain;

/**
 * Transformer that creates a static initializer which sets constant values into final static fields to emulate the
 * initializations that are done through the constant table in CLASS and DEX code, but that are not supported by Jimple.
 *
 * @author Steven Arzt
 */
public class ConstantValueToInitializerTransformer extends SceneTransformer {

  public static ConstantValueToInitializerTransformer v() {
    return new ConstantValueToInitializerTransformer();
  }

  @Override
  protected void internalTransform(String phaseName, Map<String, String> options) {
    for (SootClass sc : Scene.v().getClasses()) {
      transformClass(sc);
    }
  }

  public void transformClass(SootClass sc) {
    final Jimple jimp = Jimple.v();

    SootMethod smInit = null;
    Set<SootField> alreadyInitialized = new HashSet<SootField>();

    for (SootField sf : sc.getFields()) {
      // We can only create an initializer for all fields that have the
      // constant value tag. In case of non-static fields, this provides
      // a default value
      // If there is already an initializer for this field, we do not
      // generate a second one (this does not concern overwriting in
      // user code)
      if (alreadyInitialized.contains(sf)) {
        continue;
      }

      // Look for constant values
      for (Tag t : sf.getTags()) {
        Constant constant = null;
        if (t instanceof DoubleConstantValueTag) {
          double value = ((DoubleConstantValueTag) t).getDoubleValue();
          constant = DoubleConstant.v(value);
        } else if (t instanceof FloatConstantValueTag) {
          float value = ((FloatConstantValueTag) t).getFloatValue();
          constant = FloatConstant.v(value);
        } else if (t instanceof IntegerConstantValueTag) {
          int value = ((IntegerConstantValueTag) t).getIntValue();
          constant = IntConstant.v(value);
        } else if (t instanceof LongConstantValueTag) {
          long value = ((LongConstantValueTag) t).getLongValue();
          constant = LongConstant.v(value);
        } else if (t instanceof StringConstantValueTag) {
          String value = ((StringConstantValueTag) t).getStringValue();
          constant = StringConstant.v(value);
        }

        if (constant != null) {
          if (sf.isStatic()) {
            Stmt initStmt = jimp.newAssignStmt(jimp.newStaticFieldRef(sf.makeRef()), constant);
            if (smInit == null) {
              smInit = getOrCreateInitializer(sc);
            }
            if (smInit != null) {
              smInit.getActiveBody().getUnits().addFirst(initStmt);
            }
          } else {
            // We have a default value for a non-static field
            // So we have to get it into all <init>s, which
            // do not call other constructors of the same class.
            // It has to be after the constructor call to the super class
            // so that it can be potentially overwritten within the method,
            // without the default value taking precedence.
            // If the constructor body already has the constant assignment,
            // e.g. for final instance fields, we do not add another assignment.
            for (SootMethod m : sc.getMethods()) {
              if (m.isConstructor()) {
                final Body body = m.retrieveActiveBody();
                final UnitPatchingChain units = body.getUnits();
                Local thisLocal = null;
                if (isInstanceFieldAssignedConstantInBody(sf, constant, body)) {
                  continue;
                }
                for (Unit u : units) {
                  if (u instanceof Stmt) {
                    final Stmt s = (Stmt) u;
                    if (s.containsInvokeExpr()) {
                      final InvokeExpr expr = s.getInvokeExpr();
                      if (expr instanceof SpecialInvokeExpr) {
                        if (expr.getMethod().getDeclaringClass() == sc) {
                          // Calling another constructor in the same class
                          break;
                        }
                        if (thisLocal == null) {
                          thisLocal = body.getThisLocal();
                        }
                        Stmt initStmt = jimp.newAssignStmt(jimp.newInstanceFieldRef(thisLocal, sf.makeRef()), constant);
                        units.insertAfter(initStmt, s);
                        break;
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }

    if (smInit != null) {
      Chain<Unit> units = smInit.getActiveBody().getUnits();
      if (units.isEmpty() || !(units.getLast() instanceof ReturnVoidStmt)) {
        units.add(jimp.newReturnVoidStmt());
      }
    }
  }

  private boolean isInstanceFieldAssignedConstantInBody(SootField sf, Constant constant, Body body) {
    for (Unit u : body.getUnits()) {
      if (u instanceof AssignStmt) {
        final AssignStmt as = ((AssignStmt) u);
        if (as.containsFieldRef() && as.getFieldRef() instanceof InstanceFieldRef
            && as.getLeftOpBox().equals(as.getFieldRefBox()) && as.getRightOp().equivTo(constant)) {
          final InstanceFieldRef ifr = ((InstanceFieldRef) as.getFieldRef());
          if (ifr.getField().equals(sf) && ifr.getBase().equivTo(body.getThisLocal())) {
            return true;
          }
        }
      }
    }
    return false;
  }

  private SootMethod getOrCreateInitializer(SootClass sc) {
    // Create a static initializer if we don't already have one
    SootMethod smInit = sc.getMethodByNameUnsafe(SootMethod.staticInitializerName);
    if (smInit == null) {
      smInit = Scene.v().makeSootMethod(SootMethod.staticInitializerName, Collections.<Type>emptyList(), VoidType.v());
      smInit.setActiveBody(Jimple.v().newBody(smInit));
      sc.addMethod(smInit);
      smInit.setModifiers(Modifier.PUBLIC | Modifier.STATIC);
    } else if (smInit.isPhantom()) {
      return null;
    }
    return smInit;
  }
}
