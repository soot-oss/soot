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
 *         <p>
 *         Created on 31-May-2006
 */
public class CollectConstants extends SceneTransformer implements IJbcoTransform {

  int updatedConstants = 0;
  int constants = 0;

  public void outputSummary() {
    out.println(constants + " constants found");
    out.println(updatedConstants + " static fields created");
  }

  public static String dependancies[] = new String[] { "wjtp.jbco_cc" };

  public String[] getDependencies() {
    return dependancies;
  }

  public static String name = "wjtp.jbco_cc";

  public String getName() {
    return name;
  }

  public static HashMap<Constant, SootField> constantsToFields = new HashMap<Constant, SootField>();
  public static HashMap<Type, List<Constant>> typesToValues = new HashMap<Type, List<Constant>>();

  public static SootField field = null;

  protected void internalTransform(String phaseName, Map<String, String> options) {
    if (output) {
      out.println("Collecting Constant Data");
    }

    BodyBuilder.retrieveAllNames();

    Chain<SootClass> appClasses = Scene.v().getApplicationClasses();

    for (SootClass sc : appClasses) {
      for (SootMethod m : sc.getMethods()) {
        if (!m.hasActiveBody() || m.getName().contains(SootMethod.staticInitializerName)) {
          continue;
        }

        for (ValueBox useBox : m.getActiveBody().getUseBoxes()) {
          Value v = useBox.getValue();
          if (v instanceof Constant) {
            Constant constant = (Constant) v;
            Type type = constant.getType();
            List<Constant> constants = typesToValues.computeIfAbsent(type, t -> new ArrayList<>());

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
    SootClass[] classes = appClasses.toArray(new SootClass[appClasses.size()]);
    for (Type type : typesToValues.keySet()) {
      if (type instanceof NullType) {
        continue; // type = RefType.v("java.lang.Object");
      }
      for (Constant constant : typesToValues.get(type)) {
        name += "_";
        SootClass randomClass;
        do {
          randomClass = classes[Rand.getInt(classes.length)];
        } while (!isSuitableClassToAddFieldConstant(randomClass, constant));

        SootField newField = Scene.v().makeSootField(FieldRenamer.getNewName(), type, Modifier.STATIC ^ Modifier.PUBLIC);
        randomClass.addField(newField);
        FieldRenamer.sootFieldsRenamed.add(newField);
        FieldRenamer.addOldAndNewName(name, newField.getName());
        constantsToFields.put(constant, newField);
        addInitializingValue(randomClass, newField, constant);
        FieldRenamer.addOldAndNewName("addedConstant" + count++, newField.getName());
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