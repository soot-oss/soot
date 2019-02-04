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
import soot.tagkit.SourceFileTag;

/**
 * {@link SceneTransformer} that renames class names as well as packages.
 *
 * @author Michael Batchelder, Pavel Nesterovich
 * @since 26-Jan-2006
 */
public class ClassRenamer extends SceneTransformer implements IJbcoTransform {

  private static final Logger logger = LoggerFactory.getLogger(ClassRenamer.class);

  private boolean removePackages = false;
  private boolean renamePackages = false;

  public static final String name = "wjtp.jbco_cr";

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
    return new String[] { ClassRenamer.name };
  }

  @Override
  public void outputSummary() {
    final StringBuilder stringBuilder = new StringBuilder("ClassName mapping:").append(System.lineSeparator());
    oldToNewClassNames.forEach(
        (oldName, newName) -> stringBuilder.append(oldName).append(" -> ").append(newName).append(System.lineSeparator()));
    logger.info(stringBuilder.toString());
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
      logger.info("Transforming Class Names...");
    }

    BodyBuilder.retrieveAllBodies();
    BodyBuilder.retrieveAllNames();

    final SootClass mainClass = getMainClassSafely();

    for (SootClass applicationClass : Scene.v().getApplicationClasses()) {

      final String fullyQualifiedName = applicationClass.getName();

      if (applicationClass.equals(mainClass) || oldToNewClassNames.containsValue(fullyQualifiedName)
          || soot.jbco.Main.getWeight(phaseName, fullyQualifiedName) == 0) {
        continue;
      }

      String newClassName = getOrAddNewName(getPackageName(fullyQualifiedName), getClassName(fullyQualifiedName));

      applicationClass.setName(newClassName);
      RefType crt = RefType.v(newClassName);
      crt.setSootClass(applicationClass);
      applicationClass.setRefType(crt);
      applicationClass.setResolvingLevel(SootClass.BODIES);
      // will this fix dangling classes?
      // Scene.v().addRefType(applicationClass.getType());

      // set source name
      SourceFileTag sourceFileTag = (SourceFileTag) applicationClass.getTag(SourceFileTag.NAME);
      if (sourceFileTag == null) {
        logger.info("Adding SourceFileTag for class {}", fullyQualifiedName);
        sourceFileTag = new SourceFileTag();
        applicationClass.addTag(sourceFileTag);
      }
      sourceFileTag.setSourceFile(newClassName);

      newNameToClass.put(newClassName, applicationClass);

      if (isVerbose()) {
        logger.info("Renaming {} to {}", fullyQualifiedName, newClassName);
      }
    }

    Scene.v().releaseActiveHierarchy();
    Scene.v().setFastHierarchy(new FastHierarchy());

    if (isVerbose()) {
      logger.info("Updating bytecode class references");
    }

    for (SootClass sootClass : Scene.v().getApplicationClasses()) {
      for (SootMethod sootMethod : sootClass.getMethods()) {
        if (!sootMethod.isConcrete()) {
          continue;
        }

        if (isVerbose()) {
          logger.info(sootMethod.getSignature());
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
   * Generates new <strong>unique</strong> name that have not existed before and mapping for it or gets already generated
   * name if one was generated before.
   *
   * @param packageName
   *          the package where class is located. Can be {@code null}
   * @param className
   *          the class name (without package) to create mapping for. Can be {@code null}
   * @return the new <strong>unique</strong> name
   */
  public String getOrAddNewName(final String packageName, final String className) {
    int size = 5;
    int tries = 0;

    String newFqn = "";

    // if className == null we must generate new name
    // so check for already existing mapping is not applicable
    if (className != null) {
      newFqn = removePackages ? className : packageName == null ? className : packageName + '.' + className;
      if (oldToNewClassNames.containsKey(newFqn)) {
        return oldToNewClassNames.get(newFqn);
      }
    }

    synchronized (classNamesMapLock) {
      // check one more time when oldToNewClassNames is locked
      if (oldToNewClassNames.containsKey(newFqn)) {
        return oldToNewClassNames.get(newFqn);
      }

      while (newFqn.length() < NameGenerator.NAME_MAX_LENGTH) {
        final String name = nameGenerator.generateName(size);
        newFqn = name; // when removePackages is true
        if (!removePackages) {
          final String newPackage = renamePackages ? getOrAddNewPackageName(packageName) : packageName;
          newFqn = newPackage == null ? name : newPackage + '.' + name;
        }

        final String oldFqn;
        if (className == null) {
          // there were no old name, so create dumb mapping: newFqn -> newFqn
          oldFqn = newFqn;
        } else {
          oldFqn = (packageName == null ? "" : packageName + '.') + className;
        }

        if (!oldToNewClassNames.containsKey(oldFqn) && !oldToNewClassNames.containsValue(newFqn)
            && !BodyBuilder.nameList.contains(newFqn)) {

          addClassNameMapping(oldFqn, newFqn);

          return newFqn;
        }

        if (tries++ > size) {
          size++;
          tries = 0;
        }
      }

    }

    throw new IllegalStateException("Cannot generate unique package name part: too long for JVM.");
  }

  /**
   * Extracts package name from class name.
   *
   * @param fullyQualifiedClassName
   *          the fully qualified class name. Can be {@code null}
   * @return package name or {@code null} when class name is {@code null}, no '.' in the name, or package name is '.'
   */
  public static String getPackageName(String fullyQualifiedClassName) {
    if (fullyQualifiedClassName == null || fullyQualifiedClassName.isEmpty()) {
      return null;
    }

    int idx = fullyQualifiedClassName.lastIndexOf('.');
    return idx >= 1 ? fullyQualifiedClassName.substring(0, idx) : null;
  }

  /**
   * Extracts class name from fully qualified name.
   *
   * @param fullyQualifiedClassName
   *          the fully qualified class name. Can be {@code null}
   * @return class name or {@code null} when class name is {@code null}, no '.' in the name
   */
  public static String getClassName(String fullyQualifiedClassName) {
    if (fullyQualifiedClassName == null || fullyQualifiedClassName.isEmpty()) {
      return null;
    }

    int idx = fullyQualifiedClassName.lastIndexOf('.');
    if (idx < 0) {
      return fullyQualifiedClassName;
    }
    return idx < fullyQualifiedClassName.length() - 1 ? fullyQualifiedClassName.substring(idx + 1) : null;
  }

  private static SootClass getMainClassSafely() {
    if (Scene.v().hasMainClass()) {
      return Scene.v().getMainClass();
    } else {
      return null;
    }
  }

  private String getOrAddNewPackageName(String packageName) {
    if (packageName == null || packageName.isEmpty()) {
      return getNewPackageNamePart("");
    }

    final String[] packageNameParts = packageName.split("\\.");

    final StringBuilder newPackageName = new StringBuilder((int) (5 * (packageNameParts.length + 1) * 1.5));

    for (int i = 0; i < packageNameParts.length; i++) {
      newPackageName.append(getNewPackageNamePart(packageNameParts[i]));

      if (i < packageNameParts.length - 1) {
        newPackageName.append('.');
      }
    }

    return newPackageName.toString();
  }

  private String getNewPackageNamePart(String oldPackageNamePart) {
    if (oldPackageNamePart != null && oldToNewPackageNames.containsKey(oldPackageNamePart)) {
      return oldToNewPackageNames.get(oldPackageNamePart);
    }

    int size = 5;
    int tries = 0;

    String newPackageNamePart = "";
    while (newPackageNamePart.length() < NameGenerator.NAME_MAX_LENGTH) {
      synchronized (packageNamesMapLock) {
        if (oldToNewPackageNames.containsValue(newPackageNamePart)) {
          return oldToNewPackageNames.get(newPackageNamePart);
        }

        newPackageNamePart = nameGenerator.generateName(size);

        if (!oldToNewPackageNames.containsValue(newPackageNamePart)) {

          final String key = oldPackageNamePart == null ? newPackageNamePart : oldPackageNamePart;

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
