package soot;

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

import com.google.common.base.Optional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.dava.toolkits.base.misc.PackageNamer;
import soot.options.Options;
import soot.tagkit.AbstractHost;
import soot.util.Chain;
import soot.util.EmptyChain;
import soot.util.HashChain;
import soot.util.Numberable;
import soot.util.NumberedString;
import soot.util.SmallNumberedMap;
import soot.validation.ClassFlagsValidator;
import soot.validation.ClassValidator;
import soot.validation.MethodDeclarationValidator;
import soot.validation.OuterClassValidator;
import soot.validation.ValidationException;

/*
 * Incomplete and inefficient implementation.
 *
 * Implementation notes:
 *
 * 1. The getFieldOf() method is slow because it traverses the list of fields, comparing the names,
 * one by one.  If you establish a Dictionary of Name->Field, you will need to add a
 * notifyOfNameChange() method, and register fields which belong to classes, because the hashtable
 * will need to be updated.  I will do this later. - kor  16-Sep-97
 *
 * 2. Note 1 is kept for historical (i.e. amusement) reasons.  In fact, there is no longer a list of fields;
 * these are kept in a Chain now.  But that's ok; there is no longer a getFieldOf() method,
 * either.  There still is no efficient way to get a field by name, although one could establish
 * a Chain of EquivalentValue-like objects and do an O(1) search on that.  - plam 2-24-00
 */

/**
 * Soot representation of a Java class. They are usually created by a Scene, but can also be constructed manually through the
 * given constructors.
 */
public class SootClass extends AbstractHost implements Numberable {
  private static final Logger logger = LoggerFactory.getLogger(SootClass.class);

  public final static String INVOKEDYNAMIC_DUMMY_CLASS_NAME = "soot.dummy.InvokeDynamic";
  public final static int DANGLING = 0;
  public final static int HIERARCHY = 1;
  public final static int SIGNATURES = 2;
  public final static int BODIES = 3;

  protected String name, shortName, fixedShortName, packageName, fixedPackageName;
  protected int modifiers;
  protected Chain<SootField> fields;
  protected SmallNumberedMap<NumberedString, SootMethod> subSigToMethods;
  // methodList is just for keeping the methods in a consistent order. It
  // needs to be kept consistent with subSigToMethods.
  protected List<SootMethod> methodList;
  protected Chain<SootClass> interfaces;

  protected boolean isInScene;
  protected SootClass superClass;
  protected SootClass outerClass;

  protected boolean isPhantom;

  public final String moduleName;
  protected SootModuleInfo moduleInformation;

  private RefType refType;

  private volatile int resolvingLevel = DANGLING;

  protected volatile int number = 0;

  /**
   * Lazy initialized array containing some validators in order to validate the SootClass.
   */
  private static class LazyValidatorsSingleton {
    static final ClassValidator[] V
        = new ClassValidator[] { OuterClassValidator.v(), MethodDeclarationValidator.v(), ClassFlagsValidator.v() };

    private LazyValidatorsSingleton() {
    }
  }

  /**
   * Constructs an empty SootClass with the given name and modifiers.
   */
  public SootClass(String name, int modifiers) {
    this(name, modifiers, null);
  }

  public SootClass(String name, String moduleName) {
    this(name, 0, moduleName);
  }

  public SootClass(String name) {
    this(name, 0, null);
  }

  public SootClass(String name, int modifiers, String moduleName) {
    if (name.length() > 0 && name.charAt(0) == '[') {
      throw new RuntimeException("Attempt to make a class whose name starts with [");
    }
    this.moduleName = moduleName;
    setName(name);
    this.modifiers = modifiers;
    initializeRefType(name, moduleName);
    if (Options.v().debug_resolver()) {
      logger.debug("created " + name + " with modifiers " + modifiers);
    }
    setResolvingLevel(BODIES);
  }

  /**
   * Makes sure that there is a RefType pointing to this SootClass. Client code that provides its own SootClass
   * implementation can override and modify this behavior.
   *
   * @param name
   *          The name of the new class
   */
  protected void initializeRefType(String name, String moduleName) {
    if (ModuleUtil.module_mode()) {
      this.refType = ModuleRefType.v(name, Optional.fromNullable(this.moduleName));
    } else {
      this.refType = RefType.v(name);
    }
    this.refType.setSootClass(this);
  }

  protected static String levelToString(int level) {
    switch (level) {
      case DANGLING:
        return "DANGLING";
      case HIERARCHY:
        return "HIERARCHY";
      case SIGNATURES:
        return "SIGNATURES";
      case BODIES:
        return "BODIES";
      default:
        throw new RuntimeException("unknown resolving level");
    }
  }

  /**
   * Checks if the class has at least the resolving level specified. This check does nothing is the class resolution process
   * is not completed.
   *
   * @param level
   *          the resolution level, one of DANGLING, HIERARCHY, SIGNATURES, and BODIES
   * @throws java.lang.RuntimeException
   *           if the resolution is at an insufficient level
   */
  public void checkLevel(int level) {
    // Fast check: e.g. FastHierarchy.canStoreClass calls this method quite often
    if (resolvingLevel() >= level) {
      return;
    }
    if (!Scene.v().doneResolving() || Options.v().ignore_resolving_levels()) {
      return;
    }
    checkLevelIgnoreResolving(level);
  }

  /**
   * Checks if the class has at least the resolving level specified. This check ignores the resolution completeness.
   *
   * @param level
   *          the resolution level, one of DANGLING, HIERARCHY, SIGNATURES, and BODIES
   * @throws java.lang.RuntimeException
   *           if the resolution is at an insufficient level
   */
  public void checkLevelIgnoreResolving(int level) {
    int currentLevel = resolvingLevel();
    if (currentLevel < level) {
      String hint = "\nIf you are extending Soot, try to add the following call before calling soot.Main.main(..):\n"
          + "Scene.v().addBasicClass(" + getName() + "," + levelToString(level) + ");\n"
          + "Otherwise, try whole-program mode (-w).";
      throw new RuntimeException("This operation requires resolving level " + levelToString(level) + " but " + name
          + " is at resolving level " + levelToString(currentLevel) + hint);
    }
  }

  public int resolvingLevel() {
    return resolvingLevel;
  }

  public void setResolvingLevel(int newLevel) {
    resolvingLevel = newLevel;
  }

  public boolean isInScene() {
    return isInScene;
  }

  /**
   * Tells this class if it is being managed by a Scene.
   */
  public void setInScene(boolean isInScene) {
    this.isInScene = isInScene;
    Scene.v().getClassNumberer().add(this);
  }

  /**
   * Returns the number of fields in this class.
   */
  public int getFieldCount() {
    checkLevel(SIGNATURES);
    return fields == null ? 0 : fields.size();
  }

  /**
   * Returns a backed Chain of fields.
   */
  public Chain<SootField> getFields() {
    checkLevel(SIGNATURES);
    return fields == null ? EmptyChain.v() : fields;
  }

  /*
   * public void setFields(Field[] fields) { this.fields = new ArraySet(fields); }
   */

  /**
   * Adds the given field to this class.
   */
  public void addField(SootField f) {
    checkLevel(SIGNATURES);
    if (f.isDeclared()) {
      throw new RuntimeException("already declared: " + f.getName());
    }

    if (declaresField(f.getName(), f.getType())) {
      throw new RuntimeException("Field already exists : " + f.getName() + " of type " + f.getType());
    }

    if (fields == null) {
      fields = new HashChain<>();
    }
    fields.add(f);
    f.setDeclared(true);
    f.setDeclaringClass(this);
  }

  /**
   * Removes the given field from this class.
   */
  public void removeField(SootField f) {
    checkLevel(SIGNATURES);
    if (!f.isDeclared() || f.getDeclaringClass() != this) {
      throw new RuntimeException("did not declare: " + f.getName());
    }

    if (fields != null) {
      fields.remove(f);
    }
    f.setDeclared(false);
    f.setDeclaringClass(null);
  }

  /**
   * Returns the field of this class with the given name and type. If the field cannot be found, an exception is thrown.
   */
  public SootField getField(String name, Type type) {
    SootField sf = getFieldUnsafe(name, type);
    if (sf == null) {
      throw new RuntimeException("No field " + name + " in class " + getName());
    }
    return sf;
  }

  /**
   * Returns the field of this class with the given name and type. If the field cannot be found, null is returned.
   */
  public SootField getFieldUnsafe(String name, Type type) {
    checkLevel(SIGNATURES);
    if (fields != null) {
      for (SootField field : fields.getElementsUnsorted()) {
        if (name.equals(field.getName()) && type.equals(field.getType())) {
          return field;
        }
      }
    }
    return null;
  }

  /**
   * Returns the field of this class with the given name. Throws a RuntimeException if there is more than one field with the
   * given name or if no such field exists at all.
   */
  public SootField getFieldByName(String name) {
    SootField foundField = getFieldByNameUnsafe(name);
    if (foundField == null) {
      throw new RuntimeException("No field " + name + " in class " + getName());
    }
    return foundField;
  }

  /**
   * Returns the field of this class with the given name. Throws a RuntimeException if there is more than one field with the
   * given name. Returns null if no field with the given name exists.
   */
  public SootField getFieldByNameUnsafe(String name) {
    assert (name != null);
    checkLevel(SIGNATURES);
    SootField foundField = null;
    if (fields != null) {
      for (SootField field : fields.getElementsUnsorted()) {
        if (name.equals(field.getName())) {
          if (foundField == null) {
            foundField = field;
          } else {
            throw new AmbiguousFieldException(name, this.name);
          }
        }
      }
    }
    return foundField;
  }

  /**
   * Returns the field of this class with the given subsignature. If such a field does not exist, an exception is thrown.
   */
  public SootField getField(String subsignature) {
    // NOTE: getFieldUnsafe(String) calls checkLevel(SIGNATURES)
    SootField sf = getFieldUnsafe(subsignature);
    if (sf == null) {
      throw new RuntimeException("No field " + subsignature + " in class " + getName());
    }
    return sf;
  }

  /**
   * Returns the field of this class with the given subsignature. If such a field does not exist, null is returned.
   */
  public SootField getFieldUnsafe(String subsignature) {
    checkLevel(SIGNATURES);
    if (fields != null) {
      for (SootField field : fields.getElementsUnsorted()) {
        if (subsignature.equals(field.getSubSignature())) {
          return field;
        }
      }
    }
    return null;
  }

  /**
   * Does this class declare a field with the given subsignature?
   */
  public boolean declaresField(String subsignature) {
    // NOTE: getFieldUnsafe(String) calls checkLevel(SIGNATURES)
    return getFieldUnsafe(subsignature) != null;
  }

  /**
   * Returns the method of this class with the given subsignature. If no method with the given subsignature can be found, an
   * exception is thrown.
   */
  public SootMethod getMethod(NumberedString subsignature) {
    // NOTE: getMethodUnsafe(NumberedString) calls checkLevel(SIGNATURES)
    SootMethod ret = getMethodUnsafe(subsignature);
    if (ret == null) {
      throw new RuntimeException("No method " + subsignature + " in class " + getName());
    } else {
      return ret;
    }
  }

  /**
   * Returns the method of this class with the given subsignature. If no method with the given subsignature can be found,
   * null is returned.
   */
  public SootMethod getMethodUnsafe(NumberedString subsignature) {
    checkLevel(SIGNATURES);
    return (subSigToMethods != null) ? subSigToMethods.get(subsignature) : null;
  }

  /**
   * Does this class declare a method with the given subsignature?
   */
  public boolean declaresMethod(NumberedString subsignature) {
    // NOTE: getMethodUnsafe(NumberedString) calls checkLevel(SIGNATURES)
    return getMethodUnsafe(subsignature) != null;
  }

  /*
   * Returns the method of this class with the given subsignature. If no method with the given subsignature can be found, an
   * exception is thrown.
   */
  public SootMethod getMethod(String subsignature) {
    // NOTE: getMethodUnsafe(NumberedString) calls checkLevel(SIGNATURES)
    NumberedString numberedString = Scene.v().getSubSigNumberer().find(subsignature);
    if (numberedString == null) {
      throw new RuntimeException("No method " + subsignature + " in class " + getName());
    }
    return getMethod(numberedString);
  }

  /*
   * Returns the method of this class with the given subsignature. If no method with the given subsignature can be found,
   * null is returned.
   */
  public SootMethod getMethodUnsafe(String subsignature) {
    // NOTE: getMethodUnsafe(NumberedString) calls checkLevel(SIGNATURES)
    NumberedString numberedString = Scene.v().getSubSigNumberer().find(subsignature);
    return numberedString == null ? null : getMethodUnsafe(numberedString);
  }

  /**
   * Does this class declare a method with the given subsignature?
   */
  public boolean declaresMethod(String subsignature) {
    // NOTE: getMethodUnsafe(NumberedString) calls checkLevel(SIGNATURES)
    NumberedString numberedString = Scene.v().getSubSigNumberer().find(subsignature);
    return numberedString == null ? false : declaresMethod(numberedString);
  }

  /**
   * Does this class declare a field with the given name?
   */
  public boolean declaresFieldByName(String name) {
    checkLevel(SIGNATURES);
    if (fields != null) {
      for (SootField field : fields) {
        if (name.equals(field.getName())) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Does this class declare a field with the given name and type.
   */
  public boolean declaresField(String name, Type type) {
    checkLevel(SIGNATURES);
    if (fields != null) {
      for (SootField field : fields) {
        if (name.equals(field.getName()) && type.equals(field.getType())) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Returns the number of methods in this class.
   */
  public int getMethodCount() {
    checkLevel(SIGNATURES);
    return (subSigToMethods == null) ? 0 : subSigToMethods.nonNullSize();
  }

  /**
   * Returns an iterator over the methods in this class.
   */
  public Iterator<SootMethod> methodIterator() {
    checkLevel(SIGNATURES);
    if (methodList == null) {
      return Collections.emptyIterator();
    }

    return new Iterator<SootMethod>() {
      final Iterator<SootMethod> internalIterator = methodList.iterator();
      private SootMethod currentMethod;

      @Override
      public boolean hasNext() {
        return internalIterator.hasNext();
      }

      @Override
      public SootMethod next() {
        currentMethod = internalIterator.next();
        return currentMethod;
      }

      @Override
      public void remove() {
        internalIterator.remove();

        subSigToMethods.put(currentMethod.getNumberedSubSignature(), null);
        currentMethod.setDeclared(false);
      }
    };
  }

  public List<SootMethod> getMethods() {
    checkLevel(SIGNATURES);
    return (methodList != null) ? methodList : Collections.emptyList();
  }

  /**
   * Attempts to retrieve the method with the given name, parameters and return type. If no matching method can be found, an
   * exception is thrown.
   */
  public SootMethod getMethod(String name, List<Type> parameterTypes, Type returnType) {
    SootMethod sm = getMethodUnsafe(name, parameterTypes, returnType);
    if (sm != null) {
      return sm;
    }

    throw new RuntimeException("Class " + getName() + " doesn't have method \""
        + SootMethod.getSubSignature(name, parameterTypes, returnType) + "\"");
  }

  /**
   * Attempts to retrieve the method with the given name, parameters and return type. If no matching method can be found,
   * null is returned.
   */
  public SootMethod getMethodUnsafe(String name, List<Type> parameterTypes, Type returnType) {
    checkLevel(SIGNATURES);
    if (methodList != null) {
      for (SootMethod method : new ArrayList<>(methodList)) {
        if (name.equals(method.getName()) && returnType.equals(method.getReturnType())
            && parameterTypes.equals(method.getParameterTypes())) {
          return method;
        }
      }
    }
    return null;
  }

  /**
   * Attempts to retrieve the method with the given name and parameters. This method may throw an AmbiguousMethodException if
   * there is more than one method with the given name and parameter.
   */
  public SootMethod getMethod(String name, List<Type> parameterTypes) {
    checkLevel(SIGNATURES);

    if (methodList != null) {
      SootMethod foundMethod = null;
      for (SootMethod method : methodList) {
        if (name.equals(method.getName()) && parameterTypes.equals(method.getParameterTypes())) {
          if (foundMethod == null) {
            foundMethod = method;
          } else {
            throw new AmbiguousMethodException(name, this.name);
          }
        }
      }
      if (foundMethod != null) {
        return foundMethod;
      }
    }
    throw new RuntimeException("couldn't find method " + name + "(" + parameterTypes + ") in " + this);
  }

  /**
   * Attempts to retrieve the method with the given name. This method may throw an AmbiguousMethodException if there are more
   * than one method with the given name. If no method with the given is found, null is returned.
   */
  public SootMethod getMethodByNameUnsafe(String name) {
    checkLevel(SIGNATURES);
    SootMethod foundMethod = null;
    if (methodList != null) {
      for (SootMethod method : methodList) {
        if (name.equals(method.getName())) {
          if (foundMethod == null) {
            foundMethod = method;
          } else {
            throw new AmbiguousMethodException(name, this.name);
          }
        }
      }
    }
    return foundMethod;
  }

  /**
   * Attempts to retrieve the method with the given name. This method may throw an AmbiguousMethodException if there are more
   * than one method with the given name. If no method with the given is found, an exception is thrown as well.
   */
  public SootMethod getMethodByName(String name) {
    SootMethod foundMethod = getMethodByNameUnsafe(name);
    if (foundMethod == null) {
      throw new RuntimeException("couldn't find method " + name + "(*) in " + this);
    }
    return foundMethod;
  }

  /**
   * Does this class declare a method with the given name and parameter types?
   */
  public boolean declaresMethod(String name, List<Type> parameterTypes) {
    checkLevel(SIGNATURES);
    if (methodList != null) {
      for (SootMethod method : methodList) {
        if (name.equals(method.getName()) && parameterTypes.equals(method.getParameterTypes())) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Does this class declare a method with the given name, parameter types, and return type?
   */
  public boolean declaresMethod(String name, List<Type> parameterTypes, Type returnType) {
    checkLevel(SIGNATURES);
    if (methodList != null) {
      for (SootMethod method : methodList) {
        if (name.equals(method.getName()) && returnType.equals(method.getReturnType())
            && parameterTypes.equals(method.getParameterTypes())) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Does this class declare a method with the given name?
   */
  public boolean declaresMethodByName(String name) {
    checkLevel(SIGNATURES);
    if (methodList != null) {
      for (SootMethod method : methodList) {
        if (name.equals(method.getName())) {
          return true;
        }
      }
    }
    return false;
  }

  /*
   * public void setMethods(Method[] method) { methods = new ArraySet(method); }
   */

  /**
   * Adds the given method to this class.
   */
  public void addMethod(SootMethod m) {
    checkLevel(SIGNATURES);
    if (m.isDeclared()) {
      throw new RuntimeException("already declared: " + m.getName());
    }

    /*
     * if(declaresMethod(m.getName(), m.getParameterTypes())) throw new RuntimeException("duplicate signature for: " +
     * m.getName());
     */

    if (methodList == null) {
      this.methodList = Collections.synchronizedList(new ArrayList<>());
      this.subSigToMethods = new SmallNumberedMap<>();
    }

    if (this.subSigToMethods.get(m.getNumberedSubSignature()) != null) {
      throw new RuntimeException("Attempting to add method " + m.getSubSignature() + " to class " + this
          + ", but the class already has a method with that signature.");
    }
    this.subSigToMethods.put(m.getNumberedSubSignature(), m);
    this.methodList.add(m);
    m.setDeclared(true);
    m.setDeclaringClass(this);
  }

  public synchronized SootMethod getOrAddMethod(SootMethod m) {
    checkLevel(SIGNATURES);
    if (m.isDeclared()) {
      throw new RuntimeException("already declared: " + m.getName());
    }

    if (methodList == null) {
      this.methodList = Collections.synchronizedList(new ArrayList<>());
      this.subSigToMethods = new SmallNumberedMap<>();
    }

    SootMethod old = this.subSigToMethods.get(m.getNumberedSubSignature());
    if (old != null) {
      return old;
    }
    this.subSigToMethods.put(m.getNumberedSubSignature(), m);
    this.methodList.add(m);
    m.setDeclared(true);
    m.setDeclaringClass(this);
    return m;
  }

  public synchronized SootField getOrAddField(SootField f) {
    checkLevel(SIGNATURES);
    if (f.isDeclared()) {
      throw new RuntimeException("already declared: " + f.getName());
    }
    SootField old = getFieldUnsafe(f.getName(), f.getType());
    if (old != null) {
      return old;
    }

    if (this.fields == null) {
      this.fields = new HashChain<>();
    }

    this.fields.add(f);
    f.setDeclared(true);
    f.setDeclaringClass(this);
    return f;
  }

  /**
   * Removes the given method from this class.
   */
  public void removeMethod(SootMethod m) {
    checkLevel(SIGNATURES);
    if (!m.isDeclared() || m.getDeclaringClass() != this) {
      throw new RuntimeException("incorrect declarer for remove: " + m.getName());
    }

    if (subSigToMethods.get(m.getNumberedSubSignature()) == null) {
      throw new RuntimeException("Attempt to remove method " + m.getSubSignature() + " which is not in class " + this);
    }
    subSigToMethods.put(m.getNumberedSubSignature(), null);
    methodList.remove(m);
    m.setDeclared(false);
    m.setDeclaringClass(null);
    Scene scene = Scene.v();
    scene.getMethodNumberer().remove(m);

    // We have caches for resolving default methods in the FastHierarchy, which are no longer valid
    scene.modifyHierarchy();
  }

  /**
   * Returns the modifiers of this class.
   */
  public int getModifiers() {
    return modifiers;
  }

  /**
   * Sets the modifiers for this class.
   */
  public void setModifiers(int modifiers) {
    this.modifiers = modifiers;
  }

  /**
   * Returns the number of interfaces being directly implemented by this class. Note that direct implementation corresponds
   * to an "implements" keyword in the Java class file and that this class may still be implementing additional interfaces in
   * the usual sense by being a subclass of a class which directly implements some interfaces.
   */
  public int getInterfaceCount() {
    checkLevel(HIERARCHY);
    return interfaces == null ? 0 : interfaces.size();
  }

  /**
   * Returns a backed Chain of the interfaces that are directly implemented by this class. (see getInterfaceCount())
   */
  public Chain<SootClass> getInterfaces() {
    checkLevel(HIERARCHY);
    return interfaces == null ? EmptyChain.v() : interfaces;
  }

  /**
   * Does this class directly implement the given interface? (see getInterfaceCount())
   */
  public boolean implementsInterface(String name) {
    checkLevel(HIERARCHY);
    if (interfaces != null) {
      for (SootClass sc : interfaces) {
        if (name.equals(sc.getName())) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * Add the given class to the list of interfaces which are directly implemented by this class.
   */
  public void addInterface(SootClass interfaceClass) {
    // NOTE: implementsInterface(String) calls checkLevel(HIERARCHY)
    if (implementsInterface(interfaceClass.getName())) {
      throw new RuntimeException("duplicate interface on class " + this.getName() + ": " + interfaceClass.getName());
    }
    if (this.interfaces == null) {
      // Use a small initial size to reduce excess memory usage in the HashChain.
      // The HashChain uses an underlying ConcurrentHashMap whose default table
      // size is 16. However, classes tend to implement very few interfaces in
      // practice (often just 1) which can lead to a significant wasted memory
      // allocation when the default table size is used. Using an initial table
      // size of 4 allows up to 2 interfaces to be added before the table is
      // resized (an initial table size smaller than 4 will be resized up to 4
      // when the first element is added due to the load factor in the map).
      this.interfaces = new HashChain<>(4);
    }
    this.interfaces.add(interfaceClass);
  }

  /**
   * Removes the given class from the list of interfaces which are directly implemented by this class.
   */
  public void removeInterface(SootClass interfaceClass) {
    // NOTE: implementsInterface(String) calls checkLevel(HIERARCHY)
    if (!implementsInterface(interfaceClass.getName())) {
      throw new RuntimeException("no such interface on class " + this.getName() + ": " + interfaceClass.getName());
    }
    interfaces.remove(interfaceClass);
    if (interfaces.isEmpty()) {
      this.interfaces = null;
    }
  }

  /**
   * WARNING: interfaces are subclasses of the java.lang.Object class! Does this class have a superclass? False implies that
   * this is the java.lang.Object class. Note that interfaces are subclasses of the java.lang.Object class.
   */
  public boolean hasSuperclass() {
    checkLevel(HIERARCHY);
    return superClass != null;
  }

  /**
   * WARNING: interfaces are subclasses of the java.lang.Object class! Returns the superclass of this class. (see
   * hasSuperclass())
   */
  public SootClass getSuperclass() {
    checkLevel(HIERARCHY);
    if (superClass == null && !isPhantom() && !Options.v().ignore_resolution_errors()) {
      throw new RuntimeException("no superclass for " + getName());
    } else {
      return superClass;
    }
  }

  /**
   * This method returns the superclass, or null if no superclass has been specified for this class.
   *
   * WARNING: interfaces are subclasses of the java.lang.Object class! Returns the superclass of this class. (see
   * hasSuperclass())
   */
  public SootClass getSuperclassUnsafe() {
    checkLevel(HIERARCHY);
    return superClass;
  }

  /**
   * Sets the superclass of this class. Note that passing a null will cause the class to have no superclass.
   */
  public void setSuperclass(SootClass c) {
    checkLevel(HIERARCHY);
    superClass = c;
  }

  public boolean hasOuterClass() {
    checkLevel(HIERARCHY);
    return outerClass != null;
  }

  public SootClass getOuterClass() {
    checkLevel(HIERARCHY);
    if (outerClass == null) {
      throw new RuntimeException("no outer class");
    } else {
      return outerClass;
    }
  }

  /**
   * This method returns the outer class, or null if no outer class has been specified for this class.
   */
  public SootClass getOuterClassUnsafe() {
    checkLevel(HIERARCHY);
    return outerClass;
  }

  public void setOuterClass(SootClass c) {
    checkLevel(HIERARCHY);
    outerClass = c;
  }

  public boolean isInnerClass() {
    return hasOuterClass();
  }

  /**
   * Returns the name of this class.
   */
  public String getName() {
    return name;
  }

  public String getJavaStyleName() {
    if (PackageNamer.v().has_FixedNames()) {
      if (fixedShortName == null) {
        fixedShortName = PackageNamer.v().get_FixedClassName(name);
      }
      if (!PackageNamer.v().use_ShortName(getJavaPackageName(), fixedShortName)) {
        return getJavaPackageName() + '.' + fixedShortName;
      }
      return fixedShortName;
    } else {
      return shortName;
    }
  }

  public String getShortJavaStyleName() {
    if (PackageNamer.v().has_FixedNames()) {
      if (fixedShortName == null) {
        fixedShortName = PackageNamer.v().get_FixedClassName(name);
      }
      return fixedShortName;
    } else {
      return shortName;
    }
  }

  public String getShortName() {
    return shortName;
  }

  /**
   * Returns the package name of this class.
   */
  public String getPackageName() {
    return packageName;
  }

  public String getJavaPackageName() {
    if (PackageNamer.v().has_FixedNames()) {
      if (fixedPackageName == null) {
        fixedPackageName = PackageNamer.v().get_FixedPackageName(packageName);
      }
      return fixedPackageName;
    } else {
      return packageName;
    }
  }

  /**
   * Sets the name of this class.
   */
  public void setName(String name) {
    this.name = name.intern();

    int index = name.lastIndexOf('.');
    if (index > 0) {
      this.shortName = name.substring(index + 1);
      this.packageName = name.substring(0, index);
    } else {
      this.shortName = name;
      this.packageName = "";
    }

    this.fixedShortName = null;
    this.fixedPackageName = null;
  }

  /**
   * Convenience method; returns true if this class is an interface.
   */
  public boolean isInterface() {
    checkLevel(HIERARCHY);
    return Modifier.isInterface(this.getModifiers());
  }

  /**
   * Convenience method; returns true if this class is an enumeration.
   */
  public boolean isEnum() {
    checkLevel(HIERARCHY);
    return Modifier.isEnum(this.getModifiers());
  }

  /**
   * Convenience method; returns true if this class is synchronized.
   */
  public boolean isSynchronized() {
    checkLevel(HIERARCHY);
    return Modifier.isSynchronized(this.getModifiers());
  }

  /**
   * Returns true if this class is not an interface and not abstract.
   */
  public boolean isConcrete() {
    return !isInterface() && !isAbstract();
  }

  /**
   * Convenience method; returns true if this class is public.
   */
  public boolean isPublic() {
    return Modifier.isPublic(this.getModifiers());
  }

  /**
   * Returns true if some method in this class has an active Baf body.
   */
  public boolean containsBafBody() {
    for (Iterator<SootMethod> methodIt = methodIterator(); methodIt.hasNext();) {
      SootMethod m = methodIt.next();
      if (m.hasActiveBody() && m.getActiveBody() instanceof soot.baf.BafBody) {
        return true;
      }
    }
    return false;
  }

  // made public for obfuscator..
  public void setRefType(RefType refType) {
    this.refType = refType;
  }

  public boolean hasRefType() {
    return refType != null;
  }

  /**
   * Returns the RefType corresponding to this class.
   */
  public RefType getType() {
    return refType;
  }

  /**
   * Returns the name of this class.
   */
  @Override
  public String toString() {
    return getName();
  }

  /**
   * Renames private fields and methods with numeric names.
   */
  public void renameFieldsAndMethods(boolean privateOnly) {
    checkLevel(SIGNATURES);
    // Rename fields. Ignore collisions for now.
    {
      int fieldCount = 0;
      for (SootField f : this.getFields()) {
        if (!privateOnly || Modifier.isPrivate(f.getModifiers())) {
          f.setName("__field" + (fieldCount++));
        }
      }
    }

    // Rename methods. Again, ignore collisions for now.
    {
      int methodCount = 0;
      for (Iterator<SootMethod> methodIt = methodIterator(); methodIt.hasNext();) {
        SootMethod m = methodIt.next();
        if (!privateOnly || Modifier.isPrivate(m.getModifiers())) {
          m.setName("__method" + (methodCount++));
        }
      }
    }
  }

  /**
   * Convenience method returning true if this class is an application class.
   *
   * @see Scene#getApplicationClasses()
   */
  public boolean isApplicationClass() {
    return Scene.v().getApplicationClasses().contains(this);
  }

  /** Makes this class an application class. */
  public void setApplicationClass() {
    if (isApplicationClass()) {
      return;
    }
    Chain<SootClass> c = Scene.v().getContainingChain(this);
    if (c != null) {
      c.remove(this);
    }
    Scene.v().getApplicationClasses().add(this);

    isPhantom = false;
  }

  /**
   * Convenience method returning true if this class is a library class.
   *
   * @see Scene#getLibraryClasses()
   */
  public boolean isLibraryClass() {
    return Scene.v().getLibraryClasses().contains(this);
  }

  /** Makes this class a library class. */
  public void setLibraryClass() {
    if (isLibraryClass()) {
      return;
    }
    Chain<SootClass> c = Scene.v().getContainingChain(this);
    if (c != null) {
      c.remove(this);
    }
    Scene.v().getLibraryClasses().add(this);

    isPhantom = false;
  }

  /**
   * Sometimes we need to know which class is a JDK class. There is no simple way to distinguish a user class and a JDK
   * class, here we use the package prefix as the heuristic.
   *
   * @author xiao
   */
  public boolean isJavaLibraryClass() {
    return name.startsWith("java.") || name.startsWith("sun.") || name.startsWith("javax.") || name.startsWith("com.sun.")
        || name.startsWith("org.omg.") || name.startsWith("org.xml.") || name.startsWith("org.w3c.dom");
  }

  /**
   * Convenience method returning true if this class is a phantom class.
   *
   * @see Scene#getPhantomClasses()
   */
  public boolean isPhantomClass() {
    return Scene.v().getPhantomClasses().contains(this);
  }

  /** Makes this class a phantom class. */
  public void setPhantomClass() {
    Chain<SootClass> c = Scene.v().getContainingChain(this);
    if (c != null) {
      c.remove(this);
    }
    Scene.v().getPhantomClasses().add(this);
    isPhantom = true;
  }

  /**
   * Convenience method returning true if this class is phantom.
   */
  public boolean isPhantom() {
    return isPhantom;
  }

  /**
   * Convenience method returning true if this class is private.
   */
  public boolean isPrivate() {
    return Modifier.isPrivate(this.getModifiers());
  }

  /**
   * Convenience method returning true if this class is protected.
   */
  public boolean isProtected() {
    return Modifier.isProtected(this.getModifiers());
  }

  /**
   * Convenience method returning true if this class is abstract.
   */
  public boolean isAbstract() {
    return Modifier.isAbstract(this.getModifiers());
  }

  /**
   * Convenience method returning true if this class is final.
   */
  public boolean isFinal() {
    return Modifier.isFinal(this.getModifiers());
  }

  /**
   * Convenience method returning true if this class is static.
   */
  public boolean isStatic() {
    return Modifier.isStatic(this.getModifiers());
  }

  @Override
  public final int getNumber() {
    return number;
  }

  @Override
  public void setNumber(int number) {
    this.number = number;
  }

  public void rename(String newName) {
    this.name = newName;
    // resolvingLevel = BODIES;

    if (this.refType != null) {
      this.refType.setClassName(name);
    } else if (ModuleUtil.module_mode()) {
      this.refType = ModuleScene.v().getOrAddRefType(name, Optional.fromNullable(this.moduleName));
    } else {
      this.refType = Scene.v().getOrAddRefType(name);
    }
  }

  /**
   * Validates this SootClass for logical errors. Note that this does not validate the method bodies, only the class
   * structure.
   */
  public void validate() {
    final List<ValidationException> exceptionList = new ArrayList<ValidationException>();
    validate(exceptionList);
    if (!exceptionList.isEmpty()) {
      throw exceptionList.get(0);
    }
  }

  /**
   * Validates this SootClass for logical errors. Note that this does not validate the method bodies, only the class
   * structure. All found errors are saved into the given list.
   */
  public void validate(List<ValidationException> exceptionList) {
    final boolean runAllValidators = Options.v().debug() || Options.v().validate();
    for (ClassValidator validator : LazyValidatorsSingleton.V) {
      if (runAllValidators || validator.isBasicValidator()) {
        validator.validate(this, exceptionList);
      }
    }
  }

  public String getFilePath() {
    if (ModuleUtil.module_mode()) {
      return moduleName + ':' + this.getName();
    } else {
      return this.getName();
    }
  }

  public SootModuleInfo getModuleInformation() {
    return moduleInformation;
  }

  public void setModuleInformation(SootModuleInfo moduleInformation) {
    this.moduleInformation = moduleInformation;
  }

  /**
   * Checks if this class is exported by it's module
   *
   * @return true if the class is public exported
   */
  public boolean isExportedByModule() {
    if (this.getModuleInformation() == null && ModuleUtil.module_mode()) {
      // we are in module mode and obviously the class has not been resolved, therefore we have to resolve it
      Scene.v().forceResolve(this.getName(), SootClass.BODIES);
    }
    SootModuleInfo moduleInfo = this.getModuleInformation();
    // for dummy classes moduleInfo could be null
    return (moduleInfo == null) ? true : moduleInfo.exportsPackagePublic(this.getJavaPackageName());
  }

  /**
   * Checks if this class is exported by it's module
   *
   * @return true if the class is public exported
   */
  public boolean isExportedByModule(String toModule) {
    if (this.getModuleInformation() == null && ModuleUtil.module_mode()) {
      // we are in module mode and obviously the class has not been resolved, therefore we have to resolve it
      ModuleScene.v().forceResolve(this.getName(), SootClass.BODIES, Optional.of(this.moduleName));
    }
    return this.getModuleInformation().exportsPackage(this.getJavaPackageName(), toModule);
  }

  public boolean isOpenedByModule() {
    if (this.getModuleInformation() == null && ModuleUtil.module_mode()) {
      // we are in module mode and obviously the class has not been resolved, therefore we have to resolve it
      Scene.v().forceResolve(this.getName(), SootClass.BODIES);
    }
    SootModuleInfo moduleInfo = this.getModuleInformation();
    return (moduleInfo == null) ? true : moduleInfo.openPackagePublic(this.getJavaPackageName());
  }
}
