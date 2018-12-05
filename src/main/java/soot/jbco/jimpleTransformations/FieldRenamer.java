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
import soot.BooleanType;
import soot.IntegerType;
import soot.Local;
import soot.Modifier;
import soot.PatchingChain;
import soot.RefType;
import soot.Scene;
import soot.SceneTransformer;
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
import soot.jbco.util.BodyBuilder;
import soot.jbco.util.Rand;
import soot.jimple.FieldRef;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;

/**
 * @author Michael Batchelder
 *         <p>
 *         Created on 26-Jan-2006
 */
public class FieldRenamer extends SceneTransformer implements IJbcoTransform {
  private static final Logger logger = LoggerFactory.getLogger(FieldRenamer.class);

  public void outputSummary() {
  }

  public static String dependancies[] = new String[] { "wjtp.jbco_fr" };

  public String[] getDependencies() {
    return dependancies;
  }

  public static String name = "wjtp.jbco_fr";

  public String getName() {
    return name;
  }

  private static final char stringChars[][] = { { 'S', '5', '$' }, { 'l', '1', 'I' }, { '_' } };

  private static final String booleanClassName = Boolean.class.getName();

  public static List<String> namesToNotRename = new ArrayList<>();
  public static Map<String, String> oldToNewFieldNames = new HashMap<>();
  public static Map<SootClass, SootField> opaquePreds1ByClass = new HashMap<>();
  public static Map<SootClass, SootField> opaquePreds2ByClass = new HashMap<>();
  public static List<SootField> sootFieldsRenamed = new ArrayList<>();
  public static SootField opaquePairs[][] = null;
  public static int handedOutPairs[] = null;
  public static int handedOutRunPairs[] = null;
  public static boolean rename_fields = false;

  protected void internalTransform(String phaseName, Map<String, String> options) {
    if (output) {
      if (rename_fields) {
        out.println("Transforming Field Names and Adding Opaque Predicates...");
      } else {
        out.println("Adding Opaques...");
      }
    }

    RefType boolRef = Scene.v().getRefType(booleanClassName);

    BodyBuilder.retrieveAllBodies();
    BodyBuilder.retrieveAllNames();

    for (SootClass sc : Scene.v().getApplicationClasses()) {
      String className = sc.getName();
      if (className.contains(".")) {
        className = className.substring(className.lastIndexOf(".") + 1, className.length());
      }
      oldToNewFieldNames.put(className, className);

      if (rename_fields) {
        if (output) {
          out.println("\tClassName: " + className);
        }
        // rename all the fields in the class
        for (SootField f : sc.getFields()) {
          int weight = soot.jbco.Main.getWeight(phaseName, f.getSignature());
          if (weight > 0) {
            renameField(className, f);
          }
        }
      }

      // skip interfaces - they can only hold final fields
      if (sc.isInterface()) {
        continue;
      }

      // add one opaq predicate for true and one for false to each class
      String bool = "opPred1";
      Type t = Rand.getInt() % 2 == 0 ? BooleanType.v() : boolRef;
      while (oldToNewFieldNames.containsKey(bool)) {
        bool += "_";
      }
      SootField f = Scene.v().makeSootField(bool, t, Modifier.PUBLIC | Modifier.STATIC);
      renameField(className, f);
      opaquePreds1ByClass.put(sc, f);
      sc.addField(f);

      setBooleanTo(sc, f, true);

      bool = "opPred2";
      t = t == BooleanType.v() ? boolRef : BooleanType.v();
      while (oldToNewFieldNames.containsKey(bool)) {
        bool += "_";
      }
      f = Scene.v().makeSootField(bool, t, Modifier.PUBLIC | Modifier.STATIC);
      renameField(className, f);
      opaquePreds2ByClass.put(sc, f);
      sc.addField(f);

      if (t == boolRef) {
        setBooleanTo(sc, f, false);
      }
    }

    buildOpaquePairings();

    if (!rename_fields) {
      return;
    }

    if (output) {
      out.println("\r\tUpdating field references in bytecode");
    }

    for (SootClass sc : Scene.v().getApplicationClasses()) {
      for (SootMethod m : sc.getMethods()) {
        if (!m.isConcrete()) {
          continue;
        }

        if (!m.hasActiveBody()) {
          m.retrieveActiveBody();
        }

        for (Unit unit : m.getActiveBody().getUnits()) {
          for (ValueBox box : unit.getUseAndDefBoxes()) {
            Value value = box.getValue();
            if (value instanceof FieldRef) {
              FieldRef fieldRef = (FieldRef) value;
              SootFieldRef sootFieldRef = fieldRef.getFieldRef();
              if (sootFieldRef.declaringClass().isLibraryClass()) {
                continue;
              }

              String oldName = sootFieldRef.name();
              String fullName = sootFieldRef.declaringClass().getName() + '.' + oldName;
              String newName = oldToNewFieldNames.get(oldName);
              if (newName == null || namesToNotRename.contains(fullName)) {
                continue;
              }

              if (newName.equals(oldName)) {
                System.out.println("Strange.. Should not find a field with the same old and new name.");
              }
              sootFieldRef = Scene.v().makeFieldRef(sootFieldRef.declaringClass(), newName, sootFieldRef.type(),
                  sootFieldRef.isStatic());
              fieldRef.setFieldRef(sootFieldRef);
              try {
                sootFieldRef.resolve();
              } catch (Exception e) {
                System.err.println("********ERROR Updating " + sootFieldRef.name() + " to " + newName);
                System.err.println("Fields of " + sootFieldRef.declaringClass().getName() + ": "
                    + sootFieldRef.declaringClass().getFields());
                throw new RuntimeException(e);
              }
            }
          }
        }
      }
    }
  }

  protected void setBooleanTo(SootClass sc, SootField f, boolean value) {
    if (!value && f.getType() instanceof IntegerType && Rand.getInt() % 2 > 0) {
      return;
    }

    RefType boolRef = Scene.v().getRefType(booleanClassName);

    Body body;
    boolean newInit = false;
    if (!sc.declaresMethodByName(SootMethod.staticInitializerName)) {
      SootMethod m = Scene.v().makeSootMethod(SootMethod.staticInitializerName, emptyList(), VoidType.v(), Modifier.STATIC);
      sc.addMethod(m);
      body = Jimple.v().newBody(m);
      m.setActiveBody(body);
      newInit = true;
    } else {
      SootMethod m = sc.getMethodByName(SootMethod.staticInitializerName);
      body = m.getActiveBody();
    }

    PatchingChain<Unit> units = body.getUnits();
    if (f.getType() instanceof IntegerType) {
      units.addFirst(Jimple.v().newAssignStmt(Jimple.v().newStaticFieldRef(f.makeRef()), IntConstant.v(value ? 1 : 0)));
    } else {
      Local bool = Jimple.v().newLocal("boolLcl", boolRef);
      body.getLocals().add(bool);

      SootMethod boolInit = boolRef.getSootClass().getMethod("void <init>(boolean)");
      units.addFirst(Jimple.v().newAssignStmt(Jimple.v().newStaticFieldRef(f.makeRef()), bool));
      units.addFirst(
          Jimple.v().newInvokeStmt(Jimple.v().newSpecialInvokeExpr(bool, boolInit.makeRef(), IntConstant.v(value ? 1 : 0))));
      units.addFirst(Jimple.v().newAssignStmt(bool, Jimple.v().newNewExpr(boolRef)));
    }
    if (newInit) {
      units.addLast(Jimple.v().newReturnVoidStmt());
    }
  }

  protected void renameField(String className, SootField f) {
    if (sootFieldsRenamed.contains(f)) {
      return;
    }

    String newName = oldToNewFieldNames.get(f.getName());
    if (newName == null) {
      newName = getNewName();
      oldToNewFieldNames.put(f.getName(), newName);
    }
    if (output) {
      logger.debug("\t\tChanged " + f.getName() + " to " + newName);
    }
    f.setName(newName);
    sootFieldsRenamed.add(f);
  }

  /*
   * @return String newly generated junk name that DOES NOT exist yet
   */
  public static String getNewName() {
    int size = 3;
    int tries = 0;
    int index = Rand.getInt(stringChars.length);
    int length = stringChars[index].length;

    String result;
    char cNewName[] = new char[size];
    do {
      if (tries == 10) {
        cNewName = new char[++size];
        index = Rand.getInt(stringChars.length);
        length = stringChars[index].length;
        tries = 0;
      }

      if (size < 12) {
        do {
          int rand = Rand.getInt(length);
          cNewName[0] = stringChars[index][rand];
        } while (!Character.isJavaIdentifierStart(cNewName[0]));

        // generate random string
        for (int i = 1; i < cNewName.length; i++) {
          int rand = Rand.getInt(length);
          cNewName[i] = stringChars[index][rand];
        }
        result = String.copyValueOf(cNewName);
      } else {
        cNewName = new char[size - 6]; // size will always be at least 8
        // here

        // generate more random string
        while (true) {
          for (int i = 0; i < cNewName.length; i++) {
            cNewName[i] = (char) Rand.getInt();
          }
          result = String.copyValueOf(cNewName);
          if (isJavaIdentifier(result)) {
            break;
          }
        }
      }
      tries++;
    } while (oldToNewFieldNames.containsValue(result) || BodyBuilder.nameList.contains(result));

    BodyBuilder.nameList.add(result);

    return result;
  }

  public static void addOldAndNewName(String oldn, String newn) {
    oldToNewFieldNames.put(oldn, newn);
  }

  public static boolean isJavaIdentifier(String s) {
    if (s == null || s.length() == 0 || !Character.isJavaIdentifierStart(s.charAt(0))) {
      return false;
    }
    for (int i = 1; i < s.length(); i++) {
      if (!Character.isJavaIdentifierPart(s.charAt(i))) {
        return false;
      }
    }
    return true;
  }

  public static SootField[] getRandomOpaques() {
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

  public static int getRandomOpaquesForRunnable() {
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
    Object fields1[] = opaquePreds1ByClass.values().toArray();
    Object fields2[] = opaquePreds2ByClass.values().toArray();

    int length = fields1.length;

    if (length > 1) {
      int i = length * 2;
      while (i > 1) {
        int rand1 = Rand.getInt(length);
        int rand2 = Rand.getInt(length);
        int rand3 = Rand.getInt(length);
        int rand4 = Rand.getInt(length);
        while (rand1 == rand2) {
          rand2 = Rand.getInt(length);
        }

        while (rand3 == rand4) {
          rand4 = Rand.getInt(length);
        }

        Object value = fields1[rand1];
        fields1[rand1] = fields1[rand2];
        fields1[rand2] = value;
        value = fields2[rand3];
        fields2[rand3] = fields2[rand4];
        fields2[rand4] = value;
        i--;
      }
    }
    opaquePairs = new SootField[length][2];
    for (int i = 0; i < length; i++) {
      opaquePairs[i] = new SootField[] { (SootField) fields1[i], (SootField) fields2[i] };
    }
  }
}
