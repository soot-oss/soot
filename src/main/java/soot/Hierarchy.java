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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.jimple.SpecialInvokeExpr;
import soot.util.ArraySet;
import soot.util.Chain;

/**
 * Represents the class hierarchy. It is closely linked to a Scene, and must be recreated if the Scene changes.
 *
 * The general convention is that if a method name contains "Including", then it returns the non-strict result; otherwise, it
 * does a strict query (e.g. strict superclass).
 */
public class Hierarchy {

  // These two maps are not filled in the constructor.
  protected Map<SootClass, List<SootClass>> classToSubclasses;
  protected Map<SootClass, List<SootClass>> interfaceToSubinterfaces;
  protected Map<SootClass, List<SootClass>> interfaceToSuperinterfaces;

  protected Map<SootClass, List<SootClass>> classToDirSubclasses;
  protected Map<SootClass, List<SootClass>> interfaceToDirSubinterfaces;
  protected Map<SootClass, List<SootClass>> interfaceToDirSuperinterfaces;

  // This holds the direct implementers.
  protected Map<SootClass, List<SootClass>> interfaceToDirImplementers;

  final Scene sc;
  final int state;

  /** Constructs a hierarchy from the current scene. */
  public Hierarchy() {
    this.sc = Scene.v();
    this.state = sc.getState();

    // Well, this used to be describable by 'Duh'.
    // Construct the subclasses hierarchy and the subinterfaces hierarchy.
    {
      Chain<SootClass> allClasses = sc.getClasses();
      final int mapSize = allClasses.size() * 2 + 1;

      this.classToSubclasses = new HashMap<SootClass, List<SootClass>>(mapSize, 0.7f);
      this.interfaceToSubinterfaces = new HashMap<SootClass, List<SootClass>>(mapSize, 0.7f);
      this.interfaceToSuperinterfaces = new HashMap<SootClass, List<SootClass>>(mapSize, 0.7f);

      this.classToDirSubclasses = new HashMap<SootClass, List<SootClass>>(mapSize, 0.7f);
      this.interfaceToDirSubinterfaces = new HashMap<SootClass, List<SootClass>>(mapSize, 0.7f);
      this.interfaceToDirSuperinterfaces = new HashMap<SootClass, List<SootClass>>(mapSize, 0.7f);
      this.interfaceToDirImplementers = new HashMap<SootClass, List<SootClass>>(mapSize, 0.7f);

      initializeHierarchy(allClasses);
    }
  }

  /**
   * Initializes the hierarchy given a chain of all classes that shall be included in the hierarchy
   *
   * @param allClasses
   *          The chain of all classes to be included in the hierarchy
   */
  protected void initializeHierarchy(Chain<SootClass> allClasses) {
    for (SootClass c : allClasses) {
      if (c.resolvingLevel() < SootClass.HIERARCHY) {
        continue;
      }

      if (c.isInterface()) {
        interfaceToDirSubinterfaces.put(c, new ArrayList<SootClass>());
        interfaceToDirSuperinterfaces.put(c, new ArrayList<SootClass>());
        interfaceToDirImplementers.put(c, new ArrayList<SootClass>());
      } else {
        classToDirSubclasses.put(c, new ArrayList<SootClass>());
      }
    }

    for (SootClass c : allClasses) {
      if (c.resolvingLevel() < SootClass.HIERARCHY) {
        continue;
      }

      if (c.hasSuperclass()) {
        if (c.isInterface()) {
          List<SootClass> l2 = interfaceToDirSuperinterfaces.get(c);
          for (SootClass i : c.getInterfaces()) {
            if (c.resolvingLevel() < SootClass.HIERARCHY) {
              continue;
            }
            List<SootClass> l = interfaceToDirSubinterfaces.get(i);
            if (l != null) {
              l.add(c);
            }
            if (l2 != null) {
              l2.add(i);
            }
          }
        } else {
          List<SootClass> l = classToDirSubclasses.get(c.getSuperclass());
          if (l != null) {
            l.add(c);
          }

          for (SootClass i : c.getInterfaces()) {
            if (c.resolvingLevel() < SootClass.HIERARCHY) {
              continue;
            }
            List<SootClass> l2 = interfaceToDirImplementers.get(i);
            if (l2 != null) {
              l2.add(c);
            }
          }
        }
      }
    }

    // Fill the directImplementers lists with subclasses.
    for (SootClass c : allClasses) {
      if (c.resolvingLevel() < SootClass.HIERARCHY) {
        continue;
      }
      if (c.isInterface()) {
        Set<SootClass> s = new ArraySet<SootClass>();
        for (SootClass c0 : interfaceToDirImplementers.get(c)) {
          if (c.resolvingLevel() < SootClass.HIERARCHY) {
            continue;
          }
          s.addAll(getSubclassesOfIncluding(c0));
        }
        interfaceToDirImplementers.put(c, new ArrayList<SootClass>(s));
      } else if (c.hasSuperclass()) {
        List<SootClass> l = classToDirSubclasses.get(c);
        if (l != null) {
          classToDirSubclasses.put(c, new ArrayList<>(l));
        }
      }
    }
  }

  protected void checkState() {
    if (state != sc.getState()) {
      throw new ConcurrentModificationException("Scene changed for Hierarchy!");
    }
  }

  // This includes c in the list of subclasses.
  /** Returns a list of subclasses of c, including itself. */
  public List<SootClass> getSubclassesOfIncluding(SootClass c) {
    c.checkLevel(SootClass.HIERARCHY);
    if (c.isInterface()) {
      throw new RuntimeException("class needed!");
    }

    List<SootClass> subclasses = getSubclassesOf(c);
    List<SootClass> result = new ArrayList<SootClass>(subclasses.size() + 1);
    result.addAll(subclasses);
    result.add(c);

    return Collections.unmodifiableList(result);
  }

  /** Returns a list of subclasses of c, excluding itself. */
  public List<SootClass> getSubclassesOf(SootClass c) {
    c.checkLevel(SootClass.HIERARCHY);
    if (c.isInterface()) {
      throw new RuntimeException("class needed!");
    }

    checkState();

    // If already cached, return the value.
    List<SootClass> retVal = classToSubclasses.get(c);
    if (retVal != null) {
      return retVal;
    }

    // Otherwise, build up the hashmap.
    {
      ArrayList<SootClass> l = new ArrayList<SootClass>();
      for (SootClass cls : classToDirSubclasses.get(c)) {
        if (cls.resolvingLevel() < SootClass.HIERARCHY) {
          continue;
        }
        l.addAll(getSubclassesOfIncluding(cls));
      }
      l.trimToSize();
      retVal = Collections.unmodifiableList(l);
    }

    classToSubclasses.put(c, retVal);
    return retVal;
  }

  /**
   * Returns a list of superclasses of {@code sootClass}, including itself.
   *
   * @param sootClass
   *          the <strong>class</strong> of which superclasses will be taken. Must not be {@code null} or interface
   * @return list of superclasses, including itself
   * @throws IllegalArgumentException
   *           when passed class is an interface
   * @throws NullPointerException
   *           when passed argument is {@code null}
   */
  public List<SootClass> getSuperclassesOfIncluding(SootClass sootClass) {
    List<SootClass> superclasses = getSuperclassesOf(sootClass);
    List<SootClass> result = new ArrayList<>(superclasses.size() + 1);
    result.add(sootClass);
    result.addAll(superclasses);

    return Collections.unmodifiableList(result);
  }

  /**
   * Returns a list of <strong>direct</strong> superclasses of passed class in reverse order, starting with its parent.
   *
   * @param sootClass
   *          the <strong>class</strong> of which superclasses will be taken. Must not be {@code null} or interface
   * @return list of superclasses
   * @throws IllegalArgumentException
   *           when passed class is an interface
   * @throws NullPointerException
   *           when passed argument is {@code null}
   */
  public List<SootClass> getSuperclassesOf(SootClass sootClass) {
    sootClass.checkLevel(SootClass.HIERARCHY);
    if (sootClass.isInterface()) {
      throw new IllegalArgumentException(sootClass.getName() + " is an interface, but class is expected");
    }

    checkState();

    final List<SootClass> superclasses = new ArrayList<>();
    for (SootClass current = sootClass; current.hasSuperclass();) {
      SootClass superclass = current.getSuperclass();
      superclasses.add(superclass);
      current = superclass;
    }

    return Collections.unmodifiableList(superclasses);
  }

  /**
   * Returns a list of subinterfaces of sootClass, including itself.
   *
   * @param sootClass
   *          the <strong>interface</strong> of which subinterfaces will be taken. Must not be {@code null} or class
   * @return list of subinterfaces, including passed one
   * @throws IllegalArgumentException
   *           when passed class is a class
   * @throws NullPointerException
   *           when passed argument is {@code null}
   */
  public List<SootClass> getSubinterfacesOfIncluding(SootClass sootClass) {
    List<SootClass> subinterfaces = getSubinterfacesOf(sootClass);
    List<SootClass> result = new ArrayList<>(subinterfaces.size() + 1);
    result.addAll(subinterfaces);
    result.add(sootClass);

    return Collections.unmodifiableList(result);
  }

  /**
   * Returns a list of subinterfaces of sootClass, excluding itself.
   *
   * @param sootClass
   *          the <strong>interface</strong> of which subinterfaces will be taken. Must not be {@code null} or class
   * @return list of subinterfaces, including passed one
   * @throws IllegalArgumentException
   *           when passed sootClass is a class
   * @throws NullPointerException
   *           when passed argument is {@code null}
   */
  public List<SootClass> getSubinterfacesOf(SootClass sootClass) {
    sootClass.checkLevel(SootClass.HIERARCHY);
    if (!sootClass.isInterface()) {
      throw new IllegalArgumentException(sootClass.getName() + " is a class, but interface is expected");
    }

    checkState();

    // If already cached, return the value.
    List<SootClass> retVal = interfaceToSubinterfaces.get(sootClass);
    if (retVal != null) {
      return retVal;
    }

    // Otherwise, build up the hashmap.
    {
      List<SootClass> directSubInterfaces = interfaceToDirSubinterfaces.get(sootClass);
      if (directSubInterfaces == null || directSubInterfaces.isEmpty()) {
        return Collections.emptyList();
      }
      final ArrayList<SootClass> l = new ArrayList<>();
      for (SootClass si : directSubInterfaces) {
        l.addAll(getSubinterfacesOfIncluding(si));
      }
      l.trimToSize();
      retVal = Collections.unmodifiableList(l);
    }

    interfaceToSubinterfaces.put(sootClass, retVal);
    return retVal;
  }

  /** Returns a list of superinterfaces of c, including itself. */
  public List<SootClass> getSuperinterfacesOfIncluding(SootClass c) {
    c.checkLevel(SootClass.HIERARCHY);
    if (!c.isInterface()) {
      throw new RuntimeException("interface needed!");
    }

    List<SootClass> superinterfaces = getSuperinterfacesOf(c);
    List<SootClass> result = new ArrayList<SootClass>(superinterfaces.size() + 1);
    result.addAll(superinterfaces);
    result.add(c);

    return Collections.unmodifiableList(result);
  }

  /** Returns a list of superinterfaces of c, excluding itself. */
  public List<SootClass> getSuperinterfacesOf(SootClass c) {
    c.checkLevel(SootClass.HIERARCHY);
    if (!c.isInterface()) {
      throw new RuntimeException("interface needed!");
    }

    checkState();

    // If already cached, return the value.
    List<SootClass> retVal = interfaceToSuperinterfaces.get(c);
    if (retVal != null) {
      return retVal;
    }

    // Otherwise, build up the hashmap.
    {
      ArrayList<SootClass> l = new ArrayList<SootClass>();
      for (SootClass si : interfaceToDirSuperinterfaces.get(c)) {
        l.addAll(getSuperinterfacesOfIncluding(si));
      }
      l.trimToSize();
      retVal = Collections.unmodifiableList(l);
    }

    interfaceToSuperinterfaces.put(c, retVal);
    return retVal;
  }

  /** Returns a list of direct superclasses of c, excluding c. */
  public List<SootClass> getDirectSuperclassesOf(SootClass c) {
    throw new RuntimeException("Not implemented yet!");
  }

  /** Returns a list of direct subclasses of c, excluding c. */
  public List<SootClass> getDirectSubclassesOf(SootClass c) {
    c.checkLevel(SootClass.HIERARCHY);
    if (c.isInterface()) {
      throw new RuntimeException("class needed!");
    }

    checkState();

    return Collections.unmodifiableList(classToDirSubclasses.get(c));
  }

  // This includes c in the list of subclasses.
  /** Returns a list of direct subclasses of c, including c. */
  public List<SootClass> getDirectSubclassesOfIncluding(SootClass c) {
    c.checkLevel(SootClass.HIERARCHY);
    if (c.isInterface()) {
      throw new RuntimeException("class needed!");
    }

    checkState();

    List<SootClass> subclasses = classToDirSubclasses.get(c);
    List<SootClass> l = new ArrayList<SootClass>(subclasses.size() + 1);
    l.addAll(subclasses);
    l.add(c);

    return Collections.unmodifiableList(l);
  }

  /** Returns a list of direct superinterfaces of c. */
  public List<SootClass> getDirectSuperinterfacesOf(SootClass c) {
    throw new RuntimeException("Not implemented yet!");
  }

  /** Returns a list of direct subinterfaces of c. */
  public List<SootClass> getDirectSubinterfacesOf(SootClass c) {
    c.checkLevel(SootClass.HIERARCHY);
    if (!c.isInterface()) {
      throw new RuntimeException("interface needed!");
    }

    checkState();

    return Collections.unmodifiableList(interfaceToDirSubinterfaces.get(c));
  }

  /** Returns a list of direct subinterfaces of c, including itself. */
  public List<SootClass> getDirectSubinterfacesOfIncluding(SootClass c) {
    c.checkLevel(SootClass.HIERARCHY);
    if (!c.isInterface()) {
      throw new RuntimeException("interface needed!");
    }

    checkState();

    List<SootClass> subinterfaces = interfaceToDirSubinterfaces.get(c);
    List<SootClass> l = new ArrayList<SootClass>(subinterfaces.size() + 1);
    l.addAll(subinterfaces);
    l.add(c);

    return Collections.unmodifiableList(l);
  }

  /** Returns a list of direct implementers of c, excluding itself. */
  public List<SootClass> getDirectImplementersOf(SootClass i) {
    i.checkLevel(SootClass.HIERARCHY);
    if (!i.isInterface()) {
      throw new RuntimeException("interface needed; got " + i);
    }

    checkState();

    return Collections.unmodifiableList(interfaceToDirImplementers.get(i));
  }

  /** Returns a list of implementers of c, excluding itself. */
  public List<SootClass> getImplementersOf(SootClass i) {
    i.checkLevel(SootClass.HIERARCHY);
    if (!i.isInterface()) {
      throw new RuntimeException("interface needed; got " + i);
    }

    checkState();

    ArraySet<SootClass> set = new ArraySet<SootClass>();
    for (SootClass c : getSubinterfacesOfIncluding(i)) {
      set.addAll(getDirectImplementersOf(c));
    }
    return Collections.unmodifiableList(new ArrayList<SootClass>(set));
  }

  /**
   * Returns true if child is a subclass of possibleParent. If one of the known parent classes is phantom, we conservatively
   * assume that the current class might be a child.
   */
  public boolean isClassSubclassOf(SootClass child, SootClass possibleParent) {
    child.checkLevel(SootClass.HIERARCHY);
    possibleParent.checkLevel(SootClass.HIERARCHY);

    List<SootClass> parentClasses = getSuperclassesOf(child);
    if (parentClasses.contains(possibleParent)) {
      return true;
    }

    for (SootClass sc : parentClasses) {
      if (sc.isPhantom()) {
        return true;
      }
    }

    return false;
  }

  /**
   * Returns true if child is, or is a subclass of, possibleParent. If one of the known parent classes is phantom, we
   * conservatively assume that the current class might be a child.
   */
  public boolean isClassSubclassOfIncluding(SootClass child, SootClass possibleParent) {
    child.checkLevel(SootClass.HIERARCHY);
    possibleParent.checkLevel(SootClass.HIERARCHY);

    List<SootClass> parentClasses = getSuperclassesOfIncluding(child);
    if (parentClasses.contains(possibleParent)) {
      return true;
    }

    for (SootClass sc : parentClasses) {
      if (sc.isPhantom()) {
        return true;
      }
    }

    return false;
  }

  /** Returns true if child is a direct subclass of possibleParent. */
  public boolean isClassDirectSubclassOf(SootClass c, SootClass c2) {
    throw new RuntimeException("Not implemented yet!");
  }

  /** Returns true if child is a superclass of possibleParent. */
  public boolean isClassSuperclassOf(SootClass parent, SootClass possibleChild) {
    parent.checkLevel(SootClass.HIERARCHY);
    possibleChild.checkLevel(SootClass.HIERARCHY);
    return getSubclassesOf(parent).contains(possibleChild);
  }

  /** Returns true if parent is, or is a superclass of, possibleChild. */
  public boolean isClassSuperclassOfIncluding(SootClass parent, SootClass possibleChild) {
    parent.checkLevel(SootClass.HIERARCHY);
    possibleChild.checkLevel(SootClass.HIERARCHY);
    return getSubclassesOfIncluding(parent).contains(possibleChild);
  }

  /** Returns true if child is a subinterface of possibleParent. */
  public boolean isInterfaceSubinterfaceOf(SootClass child, SootClass possibleParent) {
    child.checkLevel(SootClass.HIERARCHY);
    possibleParent.checkLevel(SootClass.HIERARCHY);
    return getSubinterfacesOf(possibleParent).contains(child);
  }

  /** Returns true if child is a direct subinterface of possibleParent. */
  public boolean isInterfaceDirectSubinterfaceOf(SootClass child, SootClass possibleParent) {
    child.checkLevel(SootClass.HIERARCHY);
    possibleParent.checkLevel(SootClass.HIERARCHY);
    return getDirectSubinterfacesOf(possibleParent).contains(child);
  }

  /** Returns true if parent is a superinterface of possibleChild. */
  public boolean isInterfaceSuperinterfaceOf(SootClass parent, SootClass possibleChild) {
    parent.checkLevel(SootClass.HIERARCHY);
    possibleChild.checkLevel(SootClass.HIERARCHY);
    return getSuperinterfacesOf(possibleChild).contains(parent);
  }

  /** Returns true if parent is a direct superinterface of possibleChild. */
  public boolean isInterfaceDirectSuperinterfaceOf(SootClass parent, SootClass possibleChild) {
    parent.checkLevel(SootClass.HIERARCHY);
    possibleChild.checkLevel(SootClass.HIERARCHY);
    return getDirectSuperinterfacesOf(possibleChild).contains(parent);
  }

  /**
   * Returns the most specific type which is an ancestor of both c1 and c2.
   */
  public SootClass getLeastCommonSuperclassOf(SootClass c1, SootClass c2) {
    c1.checkLevel(SootClass.HIERARCHY);
    c2.checkLevel(SootClass.HIERARCHY);
    throw new RuntimeException("Not implemented yet!");
  }

  // Questions about method invocation.

  /**
   * Checks whether check is a visible class in view of the from class. It assumes that protected and private classes do not
   * exit. If they exist and check is either protected or private, the check will return false.
   */
  public boolean isVisible(SootClass from, SootClass check) {
    if (check.isPublic()) {
      return true;
    }

    if (check.isProtected() || check.isPrivate()) {
      return false;
    }

    // package visibility
    return from.getJavaPackageName().equals(check.getJavaPackageName());
  }

  /**
   * Returns true if the classmember m is visible from code in the class from.
   */
  public boolean isVisible(SootClass from, ClassMember m) {
    from.checkLevel(SootClass.HIERARCHY);
    final SootClass declaringClass = m.getDeclaringClass();
    declaringClass.checkLevel(SootClass.HIERARCHY);

    if (!isVisible(from, declaringClass)) {
      return false;
    }
    if (m.isPublic()) {
      return true;
    }
    if (m.isPrivate()) {
      return from.equals(declaringClass);
    }
    if (m.isProtected()) {
      return isClassSubclassOfIncluding(from, declaringClass)
          || from.getJavaPackageName().equals(declaringClass.getJavaPackageName());
    }
    // m is package
    return from.getJavaPackageName().equals(declaringClass.getJavaPackageName());
  }

  /**
   * Given an object of actual type C (o = new C()), returns the method which will be called on an o.f() invocation.
   */
  public SootMethod resolveConcreteDispatch(SootClass concreteType, SootMethod m) {
    concreteType.checkLevel(SootClass.HIERARCHY);
    m.getDeclaringClass().checkLevel(SootClass.HIERARCHY);
    checkState();

    if (concreteType.isInterface()) {
      throw new RuntimeException("class needed!");
    }

    final String methodSig = m.getSubSignature();
    for (SootClass c : getSuperclassesOfIncluding(concreteType)) {
      SootMethod sm = c.getMethodUnsafe(methodSig);
      if (sm != null && isVisible(c, m)) {
        return sm;
      }
    }
    throw new RuntimeException("could not resolve concrete dispatch!\nType: " + concreteType + "\nMethod: " + m);
  }

  /**
   * Given a set of definite receiver types, returns a list of possible targets.
   */
  public List<SootMethod> resolveConcreteDispatch(List<Type> classes, SootMethod m) {
    m.getDeclaringClass().checkLevel(SootClass.HIERARCHY);
    checkState();

    Set<SootMethod> s = new ArraySet<SootMethod>();
    for (Type cls : classes) {
      if (cls instanceof RefType) {
        s.add(resolveConcreteDispatch(((RefType) cls).getSootClass(), m));
      } else if (cls instanceof ArrayType) {
        s.add(resolveConcreteDispatch(RefType.v("java.lang.Object").getSootClass(), m));
      } else {
        throw new RuntimeException("Unable to resolve concrete dispatch of type " + cls);
      }
    }

    return Collections.unmodifiableList(new ArrayList<SootMethod>(s));
  }

  // what can get called for c & all its subclasses
  /**
   * Given an abstract dispatch to an object of type c and a method m, gives a list of possible receiver methods.
   */
  public List<SootMethod> resolveAbstractDispatch(SootClass c, SootMethod m) {
    c.checkLevel(SootClass.HIERARCHY);
    m.getDeclaringClass().checkLevel(SootClass.HIERARCHY);
    checkState();

    Collection<SootClass> classes;
    if (c.isInterface()) {
      classes = new HashSet<SootClass>();
      for (SootClass sootClass : getImplementersOf(c)) {
        classes.addAll(getSubclassesOfIncluding(sootClass));
      }
    } else {
      classes = getSubclassesOfIncluding(c);
    }

    Set<SootMethod> s = new ArraySet<SootMethod>();
    for (SootClass cl : classes) {
      if (!Modifier.isAbstract(cl.getModifiers())) {
        s.add(resolveConcreteDispatch(cl, m));
      }
    }

    return Collections.unmodifiableList(new ArrayList<SootMethod>(s));
  }

  // what can get called if you have a set of possible receiver types
  /**
   * Returns a list of possible targets for the given method and set of receiver types.
   */
  public List<SootMethod> resolveAbstractDispatch(List<SootClass> classes, SootMethod m) {
    m.getDeclaringClass().checkLevel(SootClass.HIERARCHY);

    Set<SootMethod> s = new ArraySet<SootMethod>();
    for (SootClass sootClass : classes) {
      s.addAll(resolveAbstractDispatch(sootClass, m));
    }

    return Collections.unmodifiableList(new ArrayList<SootMethod>(s));
  }

  /** Returns the target for the given SpecialInvokeExpr. */
  public SootMethod resolveSpecialDispatch(SpecialInvokeExpr ie, SootMethod container) {
    final SootClass containerClass = container.getDeclaringClass();
    containerClass.checkLevel(SootClass.HIERARCHY);
    final SootMethod target = ie.getMethod();
    final SootClass targetClass = target.getDeclaringClass();
    targetClass.checkLevel(SootClass.HIERARCHY);

    /*
     * This is a bizarre condition! Hopefully the implementation is correct. See VM Spec, 2nd Edition, Chapter 6, in the
     * definition of invokespecial.
     */
    if ("<init>".equals(target.getName()) || target.isPrivate()) {
      return target;
    } else if (isClassSubclassOf(targetClass, containerClass)) {
      return resolveConcreteDispatch(containerClass, target);
    } else {
      return target;
    }
  }
}
