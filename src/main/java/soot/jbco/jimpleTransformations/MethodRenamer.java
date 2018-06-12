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

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import soot.SootField;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.VoidType;
import soot.jbco.IJbcoTransform;
import soot.jbco.name.JunkNameGenerator;
import soot.jbco.name.NameGenerator;
import soot.jbco.util.BodyBuilder;
import soot.jbco.util.HierarchyUtils;
import soot.jbco.util.Rand;
import soot.jimple.InvokeExpr;

/**
 * Creates new method names using names of fields or generates randomly. New names are <strong>unique</strong> through the
 * whole application.
 * <p>
 * Some methods cannot be renamed:
 * <ul>
 * <li>{@code main} methods</li>
 * <li>constructors</li>
 * <li>static initializers</li>
 * </ul>
 * (these methods are filtered by names)
 * <ul>
 * <li>ones that override/implement library methods</li>
 * </ul>
 * <p>
 * To find methods from last group the next approach is used. All superclasses and interfaces that processing class extends /
 * implements are collected. The children of that items are taken and united together along with processing class. The same
 * action is performed for every item of the obtained result until the full "tree" is not created. Then from this tree, the
 * classes that do not have methods with <similar>similar</similar> to searching signature, are removed. The left items that
 * have {@link SootClass#isLibraryClass()} {@code true} are searching ones.
 * <p>
 * This complex approach is used to detect <i>indirect</i> inheritance. Consider next example:
 *
 * <pre>
 *                            ,--------.
 *                            |A       |
 *                            |--------|
 *                            |--------|
 *                            |method()|
 *                            `--------'
 *                              /    \
 *                             /      \
 * ,-----------------------------.  ,--------.       ,--------. ,--------------------------------.
 * |D                            |  |B       |       |E       | | Note: E is a library interface |
 * |-----------------------------|  |--------|       |--------| `--------------------------------'
 * |-----------------------------|  |--------|       |--------|
 * |method()                     |  |method()|       |method()|
 * |method(java.lang.String, int)|  `--------'       `--------'
 * `-----------------------------'           \      /
 *                                  ,------------------------.
 *                                  |C                       |
 *                                  |------------------------|
 *                                  |------------------------|
 *                                  |method()                |
 *                                  |method(java.lang.String)|
 *                                  |method(long, int)       |
 *                                  `------------------------'
 * </pre>
 *
 * Thus when {@code D#method()} is processed, it must not be renamed as there is class {@code C} that implements library one
 * ({@code E#method()}).
 * <p>
 * After applying this transformer the next result is expected:
 * <ul>
 * <li>{@code #method()} is not renamed in any application classes as it overrides one from library</li>
 * <li>{@code #method(java.lang.String)}, {@code #method(long, int)} and {@code #method(java.lang.String, int)} are renamed
 * and have the <strong>same</strong> name. Such renaming behaviour allows having <i>renaming map</i> for every class with
 * old method name as a key and new method name as a value</li>
 * </ul>
 *
 * @author Michael Batchelder, Pavel Nesterovich
 * @since 24-Jan-2006
 */
public class MethodRenamer extends SceneTransformer implements IJbcoTransform {

  private static final Logger logger = LoggerFactory.getLogger(MethodRenamer.class);

  public static final String name = "wjtp.jbco_mr";
  public static final String dependencies[] = new String[] { MethodRenamer.name };

  private static final String MAIN_METHOD_SUB_SIGNATURE
      = SootMethod.getSubSignature("main", singletonList(ArrayType.v(RefType.v("java.lang.String"), 1)), VoidType.v());

  private static final Function<SootClass, Map<String, String>> RENAMING_MAP_CREATOR = key -> new HashMap<>();

  private final Map<SootClass, Map<String, String>> classToRenamingMap = new HashMap<>();

  private final NameGenerator nameGenerator;

  /**
   * Singleton constructor.
   *
   * @param global
   *          the singletons container. Must not be {@code null}
   * @throws NullPointerException
   *           when {@code global} argument is {@code null}
   */
  public MethodRenamer(Singletons.Global global) {
    if (global == null) {
      throw new NullPointerException("Cannot instantiate MethodRenamer with null Singletons.Global");
    }

    nameGenerator = new JunkNameGenerator();
  }

  /**
   * Singleton getter.
   *
   * @return returns instance of {@link MethodRenamer}
   */
  public static MethodRenamer v() {
    return G.v().soot_jbco_jimpleTransformations_MethodRenamer();
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String[] getDependencies() {
    return Arrays.copyOf(dependencies, dependencies.length);
  }

  @Override
  public void outputSummary() {
    final Integer newNames = classToRenamingMap.values().stream().map(Map::values).flatMap(Collection::stream)
        .collect(collectingAndThen(toSet(), Set::size));
    logger.info("{} methods were renamed.", newNames);
  }

  /**
   * Gets renaming map for specific class.
   *
   * @param className
   *          the name of class to get renaming map for
   * @return the map where the key is old method name, the value - new method name. Never {@code null}
   */
  public Map<String, String> getRenamingMap(String className) {
    return classToRenamingMap.entrySet().stream().filter(entry -> entry.getKey().getName().equals(className))
        .flatMap(entry -> entry.getValue().entrySet().stream())
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  @Override
  protected void internalTransform(String phaseName, Map<String, String> options) {
    if (isVerbose()) {
      logger.info("Transforming method names...");
    }

    BodyBuilder.retrieveAllBodies();
    BodyBuilder.retrieveAllNames();

    Scene.v().releaseActiveHierarchy();

    // iterate through application classes and create new junk names
    // but DO NOT RENAME METHODS YET as it might break searching of declaring classes
    for (SootClass applicationClass : Scene.v().getApplicationClasses()) {
      final List<String> fieldNames = applicationClass.getFields().stream().map(SootField::getName)
          .collect(collectingAndThen(toList(), Collections::unmodifiableList));
      final List<String> leftFieldNames = new ArrayList<>(fieldNames);

      // create local copy to avoid ConcurrentModificationException -- methods are being updated
      final List<SootMethod> methods = new ArrayList<>(applicationClass.getMethods());
      for (SootMethod method : methods) {

        if (!isRenamingAllowed(method)) {
          continue;
        }

        final Set<SootClass> declaringClasses = getDeclaringClasses(applicationClass, method);
        if (declaringClasses.isEmpty()) {
          throw new IllegalStateException("Cannot find classes that declare " + method.getSignature() + ".");
        }

        final Optional<SootClass> libraryClass = declaringClasses.stream().filter(SootClass::isLibraryClass).findAny();
        if (libraryClass.isPresent()) {
          if (isVerbose()) {
            logger.info("Skipping renaming {} method as it overrides library one from {}.", method.getSignature(),
                libraryClass.get().getName());
          }

          continue;
        }

        // we unite declaringClasses with parents of application class (excluding library ones)
        // to be sure that every class in this hierarchy has the only new name
        // for all methods (in this hierarchy) with the same old name and different parameters
        final Set<SootClass> union = uniteWithApplicationParents(applicationClass, declaringClasses);

        String newName = getNewName(union, method.getName());
        if (newName == null) {
          if (leftFieldNames.isEmpty()) {
            newName = getNewName();
          } else {
            final int randomIndex = Rand.getInt(leftFieldNames.size());
            final String randomFieldName = leftFieldNames.remove(randomIndex);

            // check both value and existing methods, if class already contains method and field with
            // same name then we likely will fall in trouble when renaming this method before previous
            if (isNotUnique(randomFieldName) || fieldNames.contains(randomFieldName)) {
              newName = getNewName();
            } else {
              newName = randomFieldName;
            }
          }
        }

        // It is important to share renaming between different class trees as they might be intersecting.
        // For example, pretend we have class tree (A,B) with renaming ["aa"=>"bb", "cc"=>"dd"].
        // When we receive tree (B,C) with method "aa" and realize that (B,C) ∩ (A,B),
        // we not just skip generating new name but share mapping ["aa"=>"bb"] of tree (A,B)
        // with tree (B,C). As a result we will have (B,C) with mapping ["aa"=>"bb"].
        // We share this to handle case when the methods are renamed and it is impossible to get
        // this indirect connection between classes (with A and C in example above), we will still be able
        // to rename methods (and their calls) correctly
        for (SootClass declaringClass : union) {
          classToRenamingMap.computeIfAbsent(declaringClass, RENAMING_MAP_CREATOR).put(method.getName(), newName);
        }
      }
    }

    // rename methods AFTER creating mapping
    for (SootClass applicationClass : Scene.v().getApplicationClasses()) {
      final List<SootMethod> methods = new ArrayList<>(applicationClass.getMethods());
      for (SootMethod method : methods) {
        final String newName = getNewName(Collections.singleton(applicationClass), method.getName());
        if (newName != null) {
          if (isVerbose()) {
            logger.info("Method \"{}\" is being renamed to \"{}\".", method.getSignature(), newName);
          }

          method.setName(newName);
        }
      }
    }

    // iterate through application classes, update references of renamed methods
    for (SootClass applicationClass : Scene.v().getApplicationClasses()) {
      final List<SootMethod> methods = new ArrayList<>(applicationClass.getMethods());
      for (SootMethod method : methods) {

        if (!method.isConcrete() || method.getDeclaringClass().isLibraryClass()) {
          continue;
        }

        final Body body = getActiveBodySafely(method);
        if (body == null) {
          continue;
        }

        for (Unit unit : body.getUnits()) {
          for (ValueBox valueBox : unit.getUseBoxes()) {
            Value v = valueBox.getValue();
            if (!(v instanceof InvokeExpr)) {
              continue;
            }

            final InvokeExpr invokeExpr = (InvokeExpr) v;
            final SootMethodRef methodRef = invokeExpr.getMethodRef();

            final Set<SootClass> parents = getParents(methodRef.declaringClass());

            // 1. we check if method overrides one from library directly
            // Note: we cannot use getDeclaringClasses(applicationClass, method) as method can be renamed
            final Optional<SootClass> declaringLibraryClass = findDeclaringLibraryClass(parents, methodRef);
            if (declaringLibraryClass.isPresent()) {
              if (isVerbose()) {
                logger.info("Skipping replacing method call \"{}\" in \"{}\" as it is overrides one " + " from library {}.",
                    methodRef.getSignature(), method.getSignature(), declaringLibraryClass.get().getName());
              }
              continue;
            }

            final String newName = getNewName(parents, methodRef.name());
            // 2. we indirectly check that method is not overrides one from library indirectly:
            // we will get new name only if no one from class tree do not overrides library method
            if (newName == null) {
              continue;
            }

            final SootMethodRef newMethodRef = Scene.v().makeMethodRef(methodRef.declaringClass(), newName,
                methodRef.parameterTypes(), methodRef.returnType(), methodRef.isStatic());
            invokeExpr.setMethodRef(newMethodRef);

            if (isVerbose()) {
              logger.info("Method call \"{}\" is being replaced with \"{}\" in {}.", methodRef.getSignature(),
                  newMethodRef.getSignature(), method.getSignature());
            }
          }
        }
      }
    }

    Scene.v().releaseActiveHierarchy();
    Scene.v().setFastHierarchy(new FastHierarchy());

    if (isVerbose()) {
      logger.info("Transforming method names is completed.");
    }
  }

  /**
   * Creates new <strong>unique</strong> method name.
   *
   * @return newly generated junk name that DOES NOT exist yet
   */
  public String getNewName() {
    int size = 5;
    int tries = 0;

    String newName = nameGenerator.generateName(size);

    while (isNotUnique(newName) || BodyBuilder.nameList.contains(newName)) {
      if (tries++ > size) {
        size++;
        tries = 0;
      }

      newName = nameGenerator.generateName(size);
    }

    BodyBuilder.nameList.add(newName);

    return newName;
  }

  private boolean isRenamingAllowed(SootMethod method) {
    if (soot.jbco.Main.getWeight(MethodRenamer.name, method.getSignature()) == 0) {
      return false;
    }

    final String subSignature = method.getSubSignature();
    if (MAIN_METHOD_SUB_SIGNATURE.equals(subSignature) && method.isPublic() && method.isStatic()) {
      if (isVerbose()) {
        logger.info("Skipping renaming \"{}\" method as it is main one.", subSignature);
      }
      return false; // skip the main method
    }

    if (method.getName().equals(SootMethod.constructorName) || method.getName().equals(SootMethod.staticInitializerName)) {
      if (isVerbose()) {
        logger.info("Skipping renaming \"{}\" method as it is constructor or static initializer.", subSignature);
      }
      return false; // skip constructors/initializers
    }

    return true;
  }

  private boolean isNotUnique(String methodName) {
    return classToRenamingMap.values().stream().map(Map::values).flatMap(Collection::stream).anyMatch(methodName::equals);
  }

  private Set<SootClass> uniteWithApplicationParents(SootClass applicationClass, Collection<SootClass> classes) {
    final Set<SootClass> parents = getApplicationParents(applicationClass);

    final Set<SootClass> result = new HashSet<>(parents.size() + classes.size());
    result.addAll(parents);
    result.addAll(classes);

    return result;
  }

  private Optional<SootClass> findDeclaringLibraryClass(Collection<SootClass> classes, SootMethodRef methodRef) {
    return classes.stream().filter(SootClass::isLibraryClass)
        .filter(sootClass -> isDeclared(sootClass, methodRef.name(), methodRef.parameterTypes())).findAny();
  }

  private Set<SootClass> getDeclaringClasses(SootClass applicationClass, SootMethod method) {
    return getTree(applicationClass).stream()
        .filter(sootClass -> isDeclared(sootClass, method.getName(), method.getParameterTypes())).collect(toSet());
  }

  private Set<SootClass> getTree(SootClass applicationClass) {
    final Set<SootClass> children = getChildrenOfIncluding(getParentsOfIncluding(applicationClass));

    int count = 0;
    do {
      count = children.size();
      children.addAll(getChildrenOfIncluding(getParentsOfIncluding(children)));
    } while (count < children.size());

    return children;
  }

  private Set<SootClass> getParents(SootClass applicationClass) {
    final Set<SootClass> parents = new HashSet<>(getParentsOfIncluding(applicationClass));

    int count = 0;
    do {
      count = parents.size();
      parents.addAll(getParentsOfIncluding(parents));
    } while (count < parents.size());

    return parents;
  }

  private Set<SootClass> getApplicationParents(SootClass applicationClass) {
    return getParents(applicationClass).stream().filter(parent -> !parent.isLibraryClass()).collect(toSet());
  }

  private List<SootClass> getParentsOfIncluding(SootClass applicationClass) {
    // result contains of interfaces that implements passed applicationClass
    final List<SootClass> result = HierarchyUtils.getAllInterfacesOf(applicationClass);

    // and superclasses (superinterfaces) of passed applicationClass
    result.addAll(
        applicationClass.isInterface() ? Scene.v().getActiveHierarchy().getSuperinterfacesOfIncluding(applicationClass)
            : Scene.v().getActiveHierarchy().getSuperclassesOfIncluding(applicationClass));

    return result;
  }

  private Set<SootClass> getChildrenOfIncluding(Collection<SootClass> classes) {
    return Stream
        .concat(classes.stream().filter(c -> !c.getName().equals("java.lang.Object"))
            .map(c -> c.isInterface() ? Scene.v().getActiveHierarchy().getImplementersOf(c)
                : Scene.v().getActiveHierarchy().getSubclassesOf(c))
            .flatMap(Collection::stream), classes.stream())
        .collect(toSet());
  }

  private Set<SootClass> getParentsOfIncluding(Collection<SootClass> classes) {
    return classes.stream()
        .map(sootClass -> sootClass.isInterface() ? Scene.v().getActiveHierarchy().getSuperinterfacesOfIncluding(sootClass)
            : Scene.v().getActiveHierarchy().getSuperclassesOfIncluding(sootClass))
        .flatMap(Collection::stream).collect(toSet());
  }

  private String getNewName(Collection<SootClass> classes, String name) {
    final Set<String> names = classToRenamingMap.entrySet().stream().filter(entry -> classes.contains(entry.getKey()))
        .map(Map.Entry::getValue).map(Map::entrySet).flatMap(Collection::stream).filter(entry -> entry.getKey().equals(name))
        .map(Map.Entry::getValue).collect(toSet());

    if (names.size() > 1) {
      logger.warn("Found {} names for method \"{}\": {}.", names.size(), name, String.join(", ", names));
    }

    return names.isEmpty() ? null : names.iterator().next();
  }

  /**
   * Checks that method is declared in class. We assume that method is declared in class if class contains method with the
   * same name and the same number of arguments. The exact types are not compared.
   *
   * @param sootClass
   *          the class to search in
   * @param methodName
   *          the searching method name
   * @param parameterTypes
   *          the searching method parameters
   * @return {@code true} if passed class contains similar method; {@code false} otherwise
   */
  private boolean isDeclared(SootClass sootClass, String methodName, List<Type> parameterTypes) {
    for (SootMethod declared : sootClass.getMethods()) {
      if (declared.getName().equals(methodName) && declared.getParameterCount() == parameterTypes.size()) {
        return true;
      }
    }

    return false;
  }

  private static Body getActiveBodySafely(SootMethod method) {
    try {
      return method.getActiveBody();
    } catch (Exception exception) {
      logger.warn("Getting Body from SootMethod {} caused exception that was suppressed.", exception);
    }

    return null;
  }

}
