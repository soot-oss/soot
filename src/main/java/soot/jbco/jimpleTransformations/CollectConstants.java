package soot.jbco.jimpleTransformations;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 1999 Raja Vallee-Rai
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

import static java.util.Collections.emptyList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Body;
import soot.Hierarchy;
import soot.Modifier;
import soot.NullType;
import soot.PatchingChain;
import soot.RefType;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.VoidType;
import soot.jbco.IJbcoTransform;
import soot.jbco.util.BodyBuilder;
import soot.jbco.util.Rand;
import soot.jimple.ClassConstant;
import soot.jimple.Constant;
import soot.jimple.DoubleConstant;
import soot.jimple.FloatConstant;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;
import soot.jimple.LongConstant;
import soot.jimple.NullConstant;
import soot.jimple.StringConstant;
import soot.util.Chain;

/**
 * @author Michael Batchelder
 * @since 31-May-2006
 */
public class CollectConstants extends SceneTransformer implements IJbcoTransform {

  private static final Logger logger = LoggerFactory.getLogger(FieldRenamer.class);

  public static final String name = "wjtp.jbco_cc";

  private final Map<Type, List<Constant>> typeToConstants = new HashMap<>();
  public static HashMap<Constant, SootField> constantsToFields = new HashMap<>();

  private int constants = 0;
  private int updatedConstants = 0;

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String[] getDependencies() {
    return new String[] { name };
  }

  @Override
  public void outputSummary() {
    logger.info("Found {} constants, updated {} ones", constants, updatedConstants);
  }

  @Override
  protected void internalTransform(String phaseName, Map<String, String> options) {
    if (isVerbose()) {
      logger.info("Collecting Constant Data");
    }

    BodyBuilder.retrieveAllNames();

    final Chain<SootClass> applicationClasses = Scene.v().getApplicationClasses();

    for (SootClass applicationClass : applicationClasses) {
      for (SootMethod method : applicationClass.getMethods()) {
        if (!method.hasActiveBody() || method.getName().contains(SootMethod.staticInitializerName)) {
          continue;
        }

        for (ValueBox useBox : method.getActiveBody().getUseBoxes()) {
          final Value value = useBox.getValue();
          if (value instanceof Constant) {
            final Constant constant = (Constant) value;
            Type type = constant.getType();
            List<Constant> constants = typeToConstants.computeIfAbsent(type, t -> new ArrayList<>());

            if (!constants.contains(constant)) {
              this.constants++;
              constants.add(constant);
            }
          }
        }
      }
    }

    int count = 0;
    String name = "newConstantJbcoName";
    SootClass[] classes = applicationClasses.toArray(new SootClass[applicationClasses.size()]);
    for (Type type : typeToConstants.keySet()) {
      if (type instanceof NullType) {
        continue; // type = RefType.v("java.lang.Object");
      }
      for (Constant constant : typeToConstants.get(type)) {
        name += "_";
        SootClass randomClass;
        do {
          randomClass = classes[Rand.getInt(classes.length)];
        } while (!isSuitableClassToAddFieldConstant(randomClass, constant));

        final SootField newField
            = Scene.v().makeSootField(FieldRenamer.v().getOrAddNewName(name), type, Modifier.STATIC ^ Modifier.PUBLIC);
        randomClass.addField(newField);
        constantsToFields.put(constant, newField);
        addInitializingValue(randomClass, newField, constant);
        count++;
      }
    }

    updatedConstants += count;
  }

  private boolean isSuitableClassToAddFieldConstant(SootClass sc, Constant constant) {
    if (sc.isInterface()) {
      return false;
    }
    if (constant instanceof ClassConstant) {
      ClassConstant classConstant = (ClassConstant) constant;
      RefType type = (RefType) classConstant.toSootType();
      SootClass classFromConstant = type.getSootClass();
      Hierarchy hierarchy = Scene.v().getActiveHierarchy();
      return hierarchy.isVisible(sc, classFromConstant);
    }
    return true;
  }

  private void addInitializingValue(SootClass sc, SootField f, Constant constant) {
    if (constant instanceof NullConstant) {
      return;
    } else if (constant instanceof IntConstant) {
      if (((IntConstant) constant).value == 0) {
        return;
      }
    } else if (constant instanceof LongConstant) {
      if (((LongConstant) constant).value == 0) {
        return;
      }
    } else if (constant instanceof StringConstant) {
      if (((StringConstant) constant).value == null) {
        return;
      }
    } else if (constant instanceof DoubleConstant) {
      if (((DoubleConstant) constant).value == 0) {
        return;
      }
    } else if (constant instanceof FloatConstant) {
      if (((FloatConstant) constant).value == 0) {
        return;
      }
    }

    Body b;
    boolean newInit = false;
    if (!sc.declaresMethodByName(SootMethod.staticInitializerName)) {
      SootMethod m = Scene.v().makeSootMethod(SootMethod.staticInitializerName, emptyList(), VoidType.v(), Modifier.STATIC);
      sc.addMethod(m);
      b = Jimple.v().newBody(m);
      m.setActiveBody(b);
      newInit = true;
    } else {
      SootMethod m = sc.getMethodByName(SootMethod.staticInitializerName);
      if (!m.hasActiveBody()) {
        b = Jimple.v().newBody(m);
        m.setActiveBody(b);
        newInit = true;
      } else {
        b = m.getActiveBody();
      }
    }

    PatchingChain<Unit> units = b.getUnits();

    units.addFirst(Jimple.v().newAssignStmt(Jimple.v().newStaticFieldRef(f.makeRef()), constant));
    if (newInit) {
      units.addLast(Jimple.v().newReturnVoidStmt());
    }
  }
}