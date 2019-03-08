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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.Body;
import soot.BooleanType;
import soot.G;
import soot.IntegerType;
import soot.Local;
import soot.Modifier;
import soot.PatchingChain;
import soot.RefType;
import soot.Scene;
import soot.SceneTransformer;
import soot.Singletons;
import soot.SootClass;
import soot.SootField;
import soot.SootFieldRef;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.VoidType;
import soot.jbco.IJbcoTransform;
import soot.jbco.name.JunkNameGenerator;
import soot.jbco.name.NameGenerator;
import soot.jbco.util.BodyBuilder;
import soot.jbco.util.Rand;
import soot.jimple.FieldRef;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;
import soot.tagkit.SignatureTag;

/**
 * @author Michael Batchelder, Pavel Nesterovich
 * @since 26-Jan-2006
 */
public class FieldRenamer extends SceneTransformer implements IJbcoTransform {

  private static final Logger logger = LoggerFactory.getLogger(FieldRenamer.class);

  public static final String name = "wjtp.jbco_fr";

  private static final String BOOLEAN_CLASS_NAME = Boolean.class.getName();
  private static final SootField[] EMPTY_ARRAY = new SootField[0];

  private final NameGenerator nameGenerator;

  private final Map<String, String> oldToNewFieldNames = new HashMap<>();

  private final Map<SootClass, SootField> opaquePredicate1ByClass = new HashMap<>();
  private final Map<SootClass, SootField> opaquePredicate2ByClass = new HashMap<>();
  private SootField[][] opaquePairs = null;

  private final Set<String> skipFields = new HashSet<>();

  public static int handedOutPairs[] = null;
  public static int handedOutRunPairs[] = null;

  private boolean renameFields = false;

  private final Object fieldNamesLock = new Object();

  /**
   * Singleton constructor.
   *
   * @param global
   *          the singletons container. Must not be {@code null}
   * @throws NullPointerException
   *           when {@code global} argument is {@code null}
   */
  public FieldRenamer(Singletons.Global global) {
    if (global == null) {
      throw new NullPointerException("Cannot instantiate FieldRenamer with null Singletons.Global");
    }

    this.nameGenerator = new JunkNameGenerator();
  }

  /**
   * Singleton getter.
   *
   * @return returns instance of {@link FieldRenamer}
   */
  public static FieldRenamer v() {
    return G.v().soot_jbco_jimpleTransformations_FieldRenamer();
  }

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
    logger.info("Processed field mapping:");
    oldToNewFieldNames.forEach((oldName, newName) -> logger.info("{} -> {}", oldName, newName));
  }

  public boolean isRenameFields() {
    return renameFields;
  }

  public void setRenameFields(boolean renameFields) {
    this.renameFields = renameFields;
  }

  public void setSkipFields(Collection<String> fields) {
    if (!skipFields.isEmpty()) {
      skipFields.clear();
    }

    if (fields != null && !fields.isEmpty()) {
      skipFields.addAll(fields);
    }
  }

  public Set<String> getSkipFields() {
    return skipFields;
  }

  @Override
  protected void internalTransform(String phaseName, Map<String, String> options) {
    if (isVerbose()) {
      logger.info(renameFields ? "Transforming Field Names and Adding Opaque Predicates..." : "Adding Opaques...");
    }

    final RefType booleanWrapperRefType = Scene.v().getRefType(BOOLEAN_CLASS_NAME);

    BodyBuilder.retrieveAllBodies();
    BodyBuilder.retrieveAllNames();

    for (SootClass applicationClass : Scene.v().getApplicationClasses()) {
      String className = applicationClass.getName();
      if (className.contains(".")) {
        className = className.substring(className.lastIndexOf(".") + 1);
      }

      oldToNewFieldNames.put(className, className);

      if (renameFields) {
        if (isVerbose()) {
          logger.info("Class [{}]", applicationClass.getName());
        }
        // rename all the fields in the class
        for (SootField field : applicationClass.getFields()) {
          if (soot.jbco.Main.getWeight(phaseName, field.getSignature()) > 0) {
            renameField(applicationClass, field);

            field.removeTag(SignatureTag.NAME);
          }
        }
      }

      // skip interfaces - they can only hold final fields
      if (applicationClass.isInterface()) {
        continue;
      }

      // add one opaque predicate for true and one for false to each class
      String opaquePredicate = getOrAddNewName(null);
      Type type = Rand.getInt() % 2 == 0 ? BooleanType.v() : booleanWrapperRefType;
      SootField opaquePredicateField = Scene.v().makeSootField(opaquePredicate, type, Modifier.PUBLIC | Modifier.STATIC);
      renameField(applicationClass, opaquePredicateField);
      opaquePredicate1ByClass.put(applicationClass, opaquePredicateField);
      applicationClass.addField(opaquePredicateField);

      setBooleanTo(applicationClass, opaquePredicateField, true);

      opaquePredicate = getOrAddNewName(null);
      type = type == BooleanType.v() ? booleanWrapperRefType : BooleanType.v();
      opaquePredicateField = Scene.v().makeSootField(opaquePredicate, type, Modifier.PUBLIC | Modifier.STATIC);
      renameField(applicationClass, opaquePredicateField);
      opaquePredicate2ByClass.put(applicationClass, opaquePredicateField);
      applicationClass.addField(opaquePredicateField);

      if (type == booleanWrapperRefType) {
        setBooleanTo(applicationClass, opaquePredicateField, false);
      }
    }

    buildOpaquePairings();

    if (!renameFields) {
      return;
    }

    if (isVerbose()) {
      logger.info("Updating field references in bytecode");
    }

    for (SootClass applicationClass : Scene.v().getApplicationClasses()) {
      for (SootMethod method : applicationClass.getMethods()) {
        if (!method.isConcrete()) {
          continue;
        }

        if (!method.hasActiveBody()) {
          method.retrieveActiveBody();
        }

        for (Unit unit : method.getActiveBody().getUnits()) {
          for (ValueBox box : unit.getUseAndDefBoxes()) {
            final Value value = box.getValue();
            if (value instanceof FieldRef) {
              final FieldRef fieldRef = (FieldRef) value;
              SootFieldRef sootFieldRef = fieldRef.getFieldRef();
              if (sootFieldRef.declaringClass().isLibraryClass()) {
                continue;
              }

              final String oldName = sootFieldRef.name();
              final String fullyQualifiedName = sootFieldRef.declaringClass().getName() + '.' + oldName;
              if (skipFields.contains(fullyQualifiedName)) {
                continue;
              }

              String newName = oldToNewFieldNames.get(oldName);
              if (!oldToNewFieldNames.containsKey(oldName)) {
                // We ran into already renamed field.
                // To update related references proceed with oldName
                newName = oldName;
              } else if (newName == null) {
                throw new IllegalStateException("Found incorrect field mapping [" + fullyQualifiedName + "] -> [null].");
              } else if (newName.equals(oldName)) {
                logger.warn("The new name of the field \"{}\" is equal to the old one. Check if it is a mistake.",
                    fullyQualifiedName);
              }

              sootFieldRef = Scene.v().makeFieldRef(sootFieldRef.declaringClass(), newName, sootFieldRef.type(),
                  sootFieldRef.isStatic());
              fieldRef.setFieldRef(sootFieldRef);
              try {
                sootFieldRef.resolve();
              } catch (Exception exception) {
                logger.error("Cannot rename field \"" + oldName + "\" to \"" + newName + "\" due to error.", exception);
                logger.info("Fields of {}: {}", sootFieldRef.declaringClass().getName(),
                    sootFieldRef.declaringClass().getFields());
                throw new RuntimeException(exception);
              }
            }
          }
        }
      }
    }
  }

  protected void setBooleanTo(SootClass sootClass, SootField field, boolean value) {
    if (!value && field.getType() instanceof IntegerType && Rand.getInt() % 2 > 0) {
      return;
    }

    final RefType booleanWrapperRefType = Scene.v().getRefType(BOOLEAN_CLASS_NAME);

    final boolean addStaticInitializer = !sootClass.declaresMethodByName(SootMethod.staticInitializerName);
    final Body body;
    if (addStaticInitializer) {
      final SootMethod staticInitializerMethod = Scene.v().makeSootMethod(SootMethod.staticInitializerName,
          Collections.emptyList(), VoidType.v(), Modifier.STATIC);
      sootClass.addMethod(staticInitializerMethod);
      body = Jimple.v().newBody(staticInitializerMethod);
      staticInitializerMethod.setActiveBody(body);
    } else {
      body = sootClass.getMethodByName(SootMethod.staticInitializerName).getActiveBody();
    }

    final PatchingChain<Unit> units = body.getUnits();
    if (field.getType() instanceof IntegerType) {
      units.addFirst(Jimple.v().newAssignStmt(Jimple.v().newStaticFieldRef(field.makeRef()), IntConstant.v(value ? 1 : 0)));
    } else {
      Local bool = Jimple.v().newLocal("boolLcl", booleanWrapperRefType);
      body.getLocals().add(bool);

      final SootMethod booleanWrapperConstructor = booleanWrapperRefType.getSootClass().getMethod("void <init>(boolean)");
      units.addFirst(Jimple.v().newAssignStmt(Jimple.v().newStaticFieldRef(field.makeRef()), bool));
      units.addFirst(Jimple.v().newInvokeStmt(
          Jimple.v().newSpecialInvokeExpr(bool, booleanWrapperConstructor.makeRef(), IntConstant.v(value ? 1 : 0))));
      units.addFirst(Jimple.v().newAssignStmt(bool, Jimple.v().newNewExpr(booleanWrapperRefType)));
    }
    if (addStaticInitializer) {
      units.addLast(Jimple.v().newReturnVoidStmt());
    }
  }

  protected void renameField(SootClass sootClass, SootField field) {
    final String fullyQualifiedName = sootClass.getName() + "." + field.getName();

    final String newName = getOrAddNewName(field.getName());
    if (isVerbose()) {
      logger.info("Changing {} to {}", fullyQualifiedName, newName);
    }
    field.setName(newName);
  }

  /**
   * Generates new <strong>unique</strong> name that have not existed before and mapping for it, or gets new name if one was
   * already generated.
   *
   * @param originalName
   *          the original field name. If {@code null} then will be the same as generated one
   * @return the new <strong>unique</strong> name
   */
  public String getOrAddNewName(final String originalName) {
    int size = 5;
    int tries = 0;

    String newName = "";
    if (originalName != null) {
      newName = originalName;

      if (oldToNewFieldNames.containsKey(newName)) {
        return oldToNewFieldNames.get(originalName);
      }
    }

    synchronized (fieldNamesLock) {
      // check one more time when oldToNewFieldNames is locked
      if (oldToNewFieldNames.containsKey(newName)) {
        return oldToNewFieldNames.get(newName);
      }

      while (newName.length() < NameGenerator.NAME_MAX_LENGTH) {
        newName = nameGenerator.generateName(size);
        final String key = originalName == null ? newName : originalName;

        if (!oldToNewFieldNames.containsKey(key) && !oldToNewFieldNames.containsValue(newName)
            && !BodyBuilder.nameList.contains(newName)) {

          oldToNewFieldNames.put(key, newName);
          BodyBuilder.nameList.add(newName);

          return newName;
        }

        if (tries++ > size) {
          size++;
          tries = 0;
        }
      }

    }

    throw new IllegalStateException("Cannot generate unique package name part: too long for JVM.");
  }

  public SootField[] getRandomOpaques() {
    if (handedOutPairs == null) {
      handedOutPairs = new int[opaquePairs.length];
    }

    int lowValue = 99999;
    List<Integer> available = new ArrayList<>();
    for (int element : handedOutPairs) {
      if (lowValue > element) {
        lowValue = element;
      }
    }
    for (int i = 0; i < handedOutPairs.length; i++) {
      if (handedOutPairs[i] == lowValue) {
        available.add(i);
      }
    }

    int index = available.get(Rand.getInt(available.size()));
    handedOutPairs[index]++;
    return opaquePairs[index];
  }

  public int getRandomOpaquesForRunnable() {
    if (handedOutRunPairs == null) {
      handedOutRunPairs = new int[opaquePairs.length];
    }

    int lowValue = 99999;
    List<Integer> available = new ArrayList<>();
    for (int element : handedOutRunPairs) {
      if (lowValue > element) {
        lowValue = element;
      }
    }
    if (lowValue > 2) {
      return -1;
    }
    for (int i = 0; i < handedOutRunPairs.length; i++) {
      if (handedOutRunPairs[i] == lowValue) {
        available.add(i);
      }
    }

    return available.get(Rand.getInt(available.size()));
  }

  public static void updateOpaqueRunnableCount(int i) {
    handedOutRunPairs[i]++;
  }

  private void buildOpaquePairings() {
    final SootField[] fields1 = opaquePredicate1ByClass.values().toArray(EMPTY_ARRAY);
    final SootField[] fields2 = opaquePredicate2ByClass.values().toArray(EMPTY_ARRAY);

    int length = fields1.length;

    for (int i = 0; i < fields1.length * 2 && fields1.length > 1; i++) {
      swap(fields1);
      swap(fields2);
    }

    opaquePairs = new SootField[length][2];
    for (int i = 0; i < length; i++) {
      opaquePairs[i] = new SootField[] { fields1[i], fields2[i] };
    }
  }

  /**
   * Swaps random elements.
   */
  private static <T> void swap(T[] x) {
    final int a = Rand.getInt(x.length);
    int b = Rand.getInt(x.length);
    while (a == b) {
      b = Rand.getInt(x.length);
    }

    T t = x[a];
    x[a] = x[b];
    x[b] = t;
  }

}
