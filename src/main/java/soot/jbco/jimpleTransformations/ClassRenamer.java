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

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.ArrayType;
import soot.Body;
import soot.FastHierarchy;
import soot.G;
import soot.RefType;
import soot.Scene;
import soot.SceneTransformer;
import soot.Singletons;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jbco.IJbcoTransform;
import soot.jbco.name.JunkNameGenerator;
import soot.jbco.name.NameGenerator;
import soot.jbco.util.BodyBuilder;
import soot.jimple.CastExpr;
import soot.jimple.ClassConstant;
import soot.jimple.Expr;
import soot.jimple.InstanceOfExpr;
import soot.jimple.Ref;

/**
 * {@link SceneTransformer} that renames class names as well as packages.
 *
 * @author Michael Batchelder
 *         <p>
 *         Created on 26-Jan-2006
 */
public class ClassRenamer extends SceneTransformer implements IJbcoTransform {

  private static final Logger logger = LoggerFactory.getLogger(ClassRenamer.class);

  private boolean removePackages = false;
  private boolean renamePackages = false;

  public static final String name = "wjtp.jbco_cr";
  private static final String dependencies[] = new String[] { ClassRenamer.name };

  private final Map<String, String> oldToNewPackageNames = new HashMap<>();
  private final Map<String, String> oldToNewClassNames = new HashMap<>();
  private final Map<String, SootClass> newNameToClass = new HashMap<>();

  private final Object classNamesMapLock = new Object();
  private final Object packageNamesMapLock = new Object();

  private final NameGenerator nameGenerator;

  /**
   * Singleton constructor.
   *
   * @param global
   *          the singletons container. Must not be {@code null}
   * @throws NullPointerException
   *           when {@code global} argument is {@code null}
   */
  public ClassRenamer(Singletons.Global global) {
    if (global == null) {
      throw new NullPointerException("Cannot instantiate ClassRenamer with null Singletons.Global");
    }

    this.nameGenerator = new JunkNameGenerator();
  }

  /**
   * Singleton getter.
   *
   * @return returns instance of {@link ClassRenamer}
   */
  public static ClassRenamer v() {
    return G.v().soot_jbco_jimpleTransformations_ClassRenamer();
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String[] getDependencies() {
    return dependencies;
  }

  @Override
  public void outputSummary() {
  }

  /**
   * Checks if transformer must remove package from fully qualified class name.
   *
   * @return {@code true} if {@link ClassRenamer} removes packages from fully qualified class name; {@code false} otherwise
   */
  public boolean isRemovePackages() {
    return removePackages;
  }

  /**
   * Sets flag indicating that transformer must remove package from fully qualified class name.
   *
   * @param removePackages
   *          the flag value
   */
  public void setRemovePackages(boolean removePackages) {
    this.removePackages = removePackages;
  }

  /**
   * Checks if transformer must rename package in fully qualified class name.
   *
   * @return {@code true} if {@link ClassRenamer} renames packages in fully qualified class name; {@code false} otherwise
   */
  public boolean isRenamePackages() {
    return renamePackages;
  }

  /**
   * Sets flag indicating that transformer must rename package in fully qualified class name.
   *
   * @param renamePackages
   *          the flag value
   */
  public void setRenamePackages(boolean renamePackages) {
    this.renamePackages = renamePackages;
  }

  /**
   * Adds mapping for class name.
   *
   * @param classNameSource
   *          the class name to rename
   * @param classNameTarget
   *          the new class name
   */
  public void addClassNameMapping(String classNameSource, String classNameTarget) {
    synchronized (classNamesMapLock) {
      if (!oldToNewClassNames.containsKey(classNameSource) && !oldToNewClassNames.containsValue(classNameTarget)
          && !BodyBuilder.nameList.contains(classNameTarget)) {

        oldToNewClassNames.put(classNameSource, classNameTarget);
        BodyBuilder.nameList.add(classNameTarget);

        return;
      }
    }

    throw new IllegalStateException("Cannot generate unique name: too long for JVM.");
  }

  /**
   * Gets mapping by predicate.
   *
   * @param predicate
   *          the predicate to decide if mapping should be filtered. Can be {@code null}
   */
  public Map<String, String> getClassNameMapping(BiPredicate<String, String> predicate) {
    if (predicate == null) {
      return new HashMap<>(oldToNewClassNames);
    }

    return oldToNewClassNames.entrySet().stream().filter(entry -> predicate.test(entry.getKey(), entry.getValue()))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  @Override
  protected void internalTransform(String phaseName, Map<String, String> options) {
    if (isVerbose()) {
      logger.debug("Transforming Class Names...");
    }

    BodyBuilder.retrieveAllBodies();
    BodyBuilder.retrieveAllNames();

    final SootClass mainClass = getMainClassSafely();

    // iterate through application classes, rename classes with junk
    for (SootClass sootClass : Scene.v().getApplicationClasses()) {

      final String className = sootClass.getName();

      if (sootClass.equals(mainClass) || oldToNewClassNames.containsValue(className)
          || soot.jbco.Main.getWeight(phaseName, className) == 0) {
        continue;
      }

      String newClassName = oldToNewClassNames.get(className);
      if (newClassName == null) {
        newClassName = getNewName(getPackageName(className), className);
      }

      sootClass.setName(newClassName);
      RefType crt = RefType.v(newClassName);
      crt.setSootClass(sootClass);
      sootClass.setRefType(crt);
      sootClass.setResolvingLevel(SootClass.BODIES);
      // will this fix dangling classes?
      // scene.addRefType(sootClass.getType());

      newNameToClass.put(newClassName, sootClass);

      if (isVerbose()) {
        logger.info("\tRenaming " + className + " to " + newClassName);
      }
    }

    Scene.v().releaseActiveHierarchy();
    Scene.v().setFastHierarchy(new FastHierarchy());

    if (isVerbose()) {
      logger.info("\r\tUpdating bytecode class references");
    }

    for (SootClass sootClass : Scene.v().getApplicationClasses()) {
      for (SootMethod sootMethod : sootClass.getMethods()) {
        if (!sootMethod.isConcrete()) {
          continue;
        }

        if (isVerbose()) {
          logger.info("\t\t" + sootMethod.getSignature());
        }
        Body aBody;
        try {
          aBody = sootMethod.getActiveBody();
        } catch (Exception e) {
          continue;
        }

        for (Unit u : aBody.getUnits()) {
          for (ValueBox vb : u.getUseAndDefBoxes()) {
            Value v = vb.getValue();
            if (v instanceof ClassConstant) {
              ClassConstant constant = (ClassConstant) v;
              RefType type = (RefType) constant.toSootType();
              RefType updatedType = type.getSootClass().getType();
              vb.setValue(ClassConstant.fromType(updatedType));
            } else if (v instanceof Expr) {
              if (v instanceof CastExpr) {
                CastExpr castExpr = (CastExpr) v;
                updateType(castExpr.getCastType());
              } else if (v instanceof InstanceOfExpr) {
                InstanceOfExpr instanceOfExpr = (InstanceOfExpr) v;
                updateType(instanceOfExpr.getCheckType());
              }
            } else if (v instanceof Ref) {
              updateType(v.getType());
            }
          }
        }
      }
    }

    Scene.v().releaseActiveHierarchy();
    Scene.v().setFastHierarchy(new FastHierarchy());
  }

  private void updateType(Type type) {
    if (type instanceof RefType) {
      RefType rt = (RefType) type;

      if (!rt.getSootClass().isLibraryClass() && oldToNewClassNames.containsKey(rt.getClassName())) {
        rt.setSootClass(newNameToClass.get(oldToNewClassNames.get(rt.getClassName())));
        rt.setClassName(oldToNewClassNames.get(rt.getClassName()));
      }
    } else if (type instanceof ArrayType) {
      ArrayType at = (ArrayType) type;
      if (at.baseType instanceof RefType) {
        RefType rt = (RefType) at.baseType;
        if (!rt.getSootClass().isLibraryClass() && oldToNewClassNames.containsKey(rt.getClassName())) {
          rt.setSootClass(newNameToClass.get(oldToNewClassNames.get(rt.getClassName())));
        }
      }
    }
  }

  /**
   * Generates new <strong>unique</strong> name that have not existed before and mapping for it.
   *
   * @param packageName
   *          the package where class is located. Can be {@code null}
   * @param className
   *          the class name (without package) to create mapping for. Can be {@code null}
   * @return the new <strong>unique</strong> name
   */
  public String getNewName(final String packageName, final String className) {
    int size = 5;
    int tries = 0;

    while (true) {
      final String junkName = nameGenerator.generateName(size);
      final String newClassName
          = removePackages ? junkName : (renamePackages ? getNewPackageName(packageName) : packageName) + junkName;

      synchronized (classNamesMapLock) {
        if (!oldToNewClassNames.containsKey(newClassName) && !oldToNewClassNames.containsValue(newClassName)
            && !BodyBuilder.nameList.contains(newClassName)) {

          final String classNameSource = className == null || className.isEmpty() ? newClassName : className;

          addClassNameMapping(classNameSource, newClassName);

          return newClassName;
        }
      }

      if (tries++ > size) {
        size++;
        tries = 0;
      }
    }
  }

  /**
   * Extracts package name from class name.
   *
   * @param fullyQualifiedClassName
   *          the fully qualified class name. Can be {@code null}
   * @return package name or empty string when class name is {@code null} or no '.' in name
   */
  public static String getPackageName(String fullyQualifiedClassName) {
    if (fullyQualifiedClassName == null || fullyQualifiedClassName.isEmpty()) {
      return "";
    }

    int idx = fullyQualifiedClassName.lastIndexOf('.');
    return idx >= 0 ? fullyQualifiedClassName.substring(0, idx + 1) : "";
  }

  private static SootClass getMainClassSafely() {
    if (Scene.v().hasMainClass()) {
      return Scene.v().getMainClass();
    } else {
      return null;
    }
  }

  private String getNewPackageName(String packageName) {
    if (packageName == null || packageName.isEmpty()) {
      return getNewPackageNamePart(null);
    }

    final String[] packageNameParts = packageName.split("\\.");

    StringBuilder newPackageName = new StringBuilder((int) (5 * (packageNameParts.length + 1) * 1.5));

    for (String packageNamePart : packageNameParts) {
      newPackageName.append(getNewPackageNamePart(packageNamePart)).append('.');
    }

    return newPackageName.toString();
  }

  private String getNewPackageNamePart(String oldPackageNamePart) {
    if (oldPackageNamePart != null && !oldPackageNamePart.isEmpty()
        && oldToNewPackageNames.containsKey(oldPackageNamePart)) {
      return oldToNewPackageNames.get(oldPackageNamePart);
    }

    int size = 5;
    int tries = 0;

    String newPackageNamePart = "";
    while (newPackageNamePart.length() < NameGenerator.NAME_MAX_LENGTH) {

      newPackageNamePart = nameGenerator.generateName(size);

      synchronized (packageNamesMapLock) {
        if (!oldToNewPackageNames.containsValue(newPackageNamePart)
            || !oldToNewPackageNames.containsKey(newPackageNamePart)) {

          final String key
              = oldPackageNamePart == null || oldPackageNamePart.isEmpty() ? newPackageNamePart : oldPackageNamePart;

          oldToNewPackageNames.put(key, newPackageNamePart);

          return newPackageNamePart;
        }
      }

      if (tries++ > size) {
        size++;
        tries = 0;
      }
    }

    throw new IllegalStateException("Cannot generate unique package name part: too long for JVM.");
  }

}
