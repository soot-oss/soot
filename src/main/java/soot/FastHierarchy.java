package soot;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 2002 Ondrej Lhotak
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

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import soot.jimple.SpecialInvokeExpr;
import soot.util.ConcurrentHashMultiMap;
import soot.util.MultiMap;

/**
 * Represents the class hierarchy. It is closely linked to a Scene, and must be recreated if the Scene changes.
 *
 * This version supercedes the old soot.Hierarchy class.
 *
 * @author Ondrej Lhotak
 */
public class FastHierarchy {
  /**
   * This map holds all key,value pairs such that value.getSuperclass() == key. This is one of the three maps that hold the
   * inverse of the relationships given by the getSuperclass and getInterfaces methods of SootClass.
   */
  protected MultiMap<SootClass, SootClass> classToSubclasses = new ConcurrentHashMultiMap<SootClass, SootClass>();

  /**
   * This map holds all key,value pairs such that value is an interface and key is in value.getInterfaces(). This is one of
   * the three maps that hold the inverse of the relationships given by the getSuperclass and getInterfaces methods of
   * SootClass.
   */
  protected MultiMap<SootClass, SootClass> interfaceToSubinterfaces = new ConcurrentHashMultiMap<SootClass, SootClass>();

  /**
   * This map holds all key,value pairs such that value is a class (NOT an interface) and key is in value.getInterfaces().
   * This is one of the three maps that hold the inverse of the relationships given by the getSuperclass and getInterfaces
   * methods of SootClass.
   */
  protected MultiMap<SootClass, SootClass> interfaceToImplementers = new ConcurrentHashMultiMap<SootClass, SootClass>();

  /**
   * This map is a transitive closure of interfaceToSubinterfaces, and each set contains its superinterface itself.
   */
  protected MultiMap<SootClass, SootClass> interfaceToAllSubinterfaces = new ConcurrentHashMultiMap<SootClass, SootClass>();

  /**
   * This map gives, for an interface, all concrete classes that implement that interface and all its subinterfaces, but NOT
   * their subclasses.
   */
  protected MultiMap<SootClass, SootClass> interfaceToAllImplementers = new ConcurrentHashMultiMap<SootClass, SootClass>();

  /**
   * For each class (NOT interface), this map contains a Interval, which is a pair of numbers giving a preorder and postorder
   * ordering of classes in the inheritance tree.
   */
  protected Map<SootClass, Interval> classToInterval = new HashMap<SootClass, Interval>();

  protected Scene sc;

  protected class Interval {
    int lower;
    int upper;

    public boolean isSubrange(Interval potentialSubrange) {
      if (potentialSubrange == null) {
        return false;
      }
      if (lower > potentialSubrange.lower) {
        return false;
      }
      if (upper < potentialSubrange.upper) {
        return false;
      }
      return true;
    }
  }

  protected int dfsVisit(int start, SootClass c) {
    Interval r = new Interval();
    r.lower = start++;
    Collection<SootClass> col = classToSubclasses.get(c);
    if (col != null) {
      for (SootClass sc : col) {
        // For some awful reason, Soot thinks interface are subclasses
        // of java.lang.Object
        if (sc.isInterface()) {
          continue;
        }
        start = dfsVisit(start, sc);
      }
    }
    r.upper = start++;
    if (c.isInterface()) {
      throw new RuntimeException("Attempt to dfs visit interface " + c);
    }
    if (!classToInterval.containsKey(c)) {
      classToInterval.put(c, r);
    }
    return start;
  }

  /** Constructs a hierarchy from the current scene. */
  public FastHierarchy() {
    this.sc = Scene.v();

    /* First build the inverse maps. */
    for (SootClass cl : sc.getClasses().getElementsUnsorted()) {
      if (cl.resolvingLevel() < SootClass.HIERARCHY) {
        continue;
      }
      if (!cl.isInterface()) {
        SootClass superClass = cl.getSuperclassUnsafe();
        if (superClass != null) {
          classToSubclasses.put(superClass, cl);
        }
      }
      for (final SootClass supercl : cl.getInterfaces()) {
        if (cl.isInterface()) {
          interfaceToSubinterfaces.put(supercl, cl);
        } else {
          interfaceToImplementers.put(supercl, cl);
        }
      }
    }

    /* Now do a dfs traversal to get the Interval numbers. */
    dfsVisit(0, sc.getSootClass("java.lang.Object"));
    /*
     * also have to traverse for all phantom classes because they also can be roots of the type hierarchy
     */
    for (final Iterator<SootClass> phantomClassIt = sc.getPhantomClasses().snapshotIterator(); phantomClassIt.hasNext();) {
      SootClass phantomClass = phantomClassIt.next();
      if (!phantomClass.isInterface()) {
        dfsVisit(0, phantomClass);
      }
    }
  }

  /**
   * Return true if class child is a subclass of class parent, neither of them being allowed to be interfaces. If we don't
   * know any of the classes, we always return false
   */
  public boolean isSubclass(SootClass child, SootClass parent) {
    child.checkLevel(SootClass.HIERARCHY);
    parent.checkLevel(SootClass.HIERARCHY);
    Interval parentInterval = classToInterval.get(parent);
    Interval childInterval = classToInterval.get(child);
    return parentInterval != null && childInterval != null && parentInterval.isSubrange(childInterval);
  }

  /**
   * For an interface parent (MUST be an interface), returns set of all implementers of it but NOT their subclasses.
   */
  public Set<SootClass> getAllImplementersOfInterface(SootClass parent) {
    parent.checkLevel(SootClass.HIERARCHY);
    if (!interfaceToAllImplementers.containsKey(parent)) {
      for (SootClass subinterface : getAllSubinterfaces(parent)) {
        if (subinterface == parent) {
          continue;
        }
        interfaceToAllImplementers.putAll(parent, getAllImplementersOfInterface(subinterface));
      }
      interfaceToAllImplementers.putAll(parent, interfaceToImplementers.get(parent));
    }
    return interfaceToAllImplementers.get(parent);
  }

  /**
   * For an interface parent (MUST be an interface), returns set of all subinterfaces.
   *
   * @param parent
   *          the parent interface.
   * @return an set, possibly empty
   */
  public Set<SootClass> getAllSubinterfaces(SootClass parent) {
    parent.checkLevel(SootClass.HIERARCHY);
    if (!parent.isInterface()) {
      return Collections.<SootClass>emptySet();
    }
    if (!interfaceToAllSubinterfaces.containsKey(parent)) {
      interfaceToAllSubinterfaces.put(parent, parent);
      for (SootClass si : interfaceToSubinterfaces.get(parent)) {
        interfaceToAllSubinterfaces.putAll(parent, getAllSubinterfaces(si));
      }
    }
    return interfaceToAllSubinterfaces.get(parent);
  }

  /**
   * Given an object of declared type child, returns true if the object can be stored in a variable of type parent. If child
   * is an interface that is not a subinterface of parent, this method will return false even though some objects
   * implementing the child interface may also implement the parent interface.
   */
  public boolean canStoreType(Type child, Type parent) {
    if (child.equals(parent)) {
      return true;
    }
    if (parent instanceof NullType) {
      return false;
    }
    if (child instanceof NullType) {
      return parent instanceof RefLikeType;
    }
    if (child instanceof RefType) {
      if (parent.equals(sc.getObjectType())) {
        return true;
      }
      if (parent instanceof RefType) {
        return canStoreClass(((RefType) child).getSootClass(), ((RefType) parent).getSootClass());
      } else {
        return false;
      }
    } else if (child instanceof AnySubType) {
      if (!(parent instanceof RefLikeType)) {
        throw new RuntimeException("Unhandled type " + parent);
      } else if (parent instanceof ArrayType) {
        Type base = ((AnySubType) child).getBase();
        // From Java Language Spec 2nd ed., Chapter 10, Arrays
        return base.equals(sc.getObjectType()) || base.equals(RefType.v("java.io.Serializable"))
            || base.equals(RefType.v("java.lang.Cloneable"));
      } else {
        SootClass base = ((AnySubType) child).getBase().getSootClass();
        SootClass parentClass = ((RefType) parent).getSootClass();
        ArrayDeque<SootClass> worklist = new ArrayDeque<SootClass>();
        if (base.isInterface()) {
          worklist.addAll(getAllImplementersOfInterface(base));
        } else {
          worklist.add(base);
        }
        Set<SootClass> workset = new HashSet<>();
        while (true) {
          SootClass cl = worklist.poll();
          if (cl == null) {
            break;
          }
          if (!workset.add(cl)) {
            continue;
          }
          if (cl.isConcrete() && canStoreClass(cl, parentClass)) {
            return true;
          }
          worklist.addAll(getSubclassesOf(cl));
        }
        return false;
      }
    } else if (child instanceof ArrayType) {
      ArrayType achild = (ArrayType) child;
      if (parent instanceof RefType) {
        // From Java Language Spec 2nd ed., Chapter 10, Arrays
        return parent.equals(sc.getObjectType()) || parent.equals(RefType.v("java.io.Serializable"))
            || parent.equals(RefType.v("java.lang.Cloneable"));
      }
      if (!(parent instanceof ArrayType)) {
        return false;
      }
      ArrayType aparent = (ArrayType) parent;

      // You can store a int[][] in a Object[]. Yuck!
      // Also, you can store a Interface[] in a Object[]
      if (achild.numDimensions == aparent.numDimensions) {
        if (achild.baseType.equals(aparent.baseType)) {
          return true;
        }
        if (!(achild.baseType instanceof RefType)) {
          return false;
        }
        if (!(aparent.baseType instanceof RefType)) {
          return false;
        }
        return canStoreType(achild.baseType, aparent.baseType);
      } else if (achild.numDimensions > aparent.numDimensions) {
        if (aparent.baseType.equals(sc.getObjectType())) {
          return true;
        }
        if (aparent.baseType.equals(RefType.v("java.io.Serializable"))) {
          return true;
        }
        if (aparent.baseType.equals(RefType.v("java.lang.Cloneable"))) {
          return true;
        }
        return false;
      } else {
        return false;
      }
    } else {
      return false;
    }
  }

  /**
   * Given an object of declared type child, returns true if the object can be stored in a variable of type parent. If child
   * is an interface that is not a subinterface of parent, this method will return false even though some objects
   * implementing the child interface may also implement the parent interface.
   */
  public boolean canStoreClass(SootClass child, SootClass parent) {
    parent.checkLevel(SootClass.HIERARCHY);
    child.checkLevel(SootClass.HIERARCHY);
    Interval parentInterval = classToInterval.get(parent);
    Interval childInterval = classToInterval.get(child);
    if (parentInterval != null && childInterval != null) {
      return parentInterval.isSubrange(childInterval);
    }
    if (childInterval == null) { // child is interface
      if (parentInterval != null) { // parent is not interface
        return parent.equals(sc.getObjectType().getSootClass());
      } else {
        return getAllSubinterfaces(parent).contains(child);
      }
    } else {
      Set<SootClass> impl = getAllImplementersOfInterface(parent);
      // If we have more than 1000 entries use multi-threaded search
      Stream<SootClass> stream = (impl.size() > 1000) ? impl.parallelStream() : impl.stream();
      return stream.anyMatch(c -> {
        Interval interval = classToInterval.get(c);
        return (interval != null && interval.isSubrange(childInterval));
      });
    }
  }

  public Collection<SootMethod> resolveConcreteDispatchWithoutFailing(Collection<Type> concreteTypes, SootMethod m,
      RefType declaredTypeOfBase) {

    Set<SootMethod> ret = new HashSet<SootMethod>();
    SootClass declaringClass = declaredTypeOfBase.getSootClass();
    declaringClass.checkLevel(SootClass.HIERARCHY);
    for (final Type t : concreteTypes) {
      if (t instanceof AnySubType) {
        HashSet<SootClass> s = new HashSet<SootClass>();
        s.add(declaringClass);
        while (!s.isEmpty()) {
          SootClass c = s.iterator().next();
          s.remove(c);
          if (!c.isInterface() && !c.isAbstract() && canStoreClass(c, declaringClass)) {
            SootMethod concreteM = resolveConcreteDispatch(c, m);
            if (concreteM != null) {
              ret.add(concreteM);
            }
          }
          if (classToSubclasses.containsKey(c)) {
            s.addAll(classToSubclasses.get(c));
          }
          if (interfaceToSubinterfaces.containsKey(c)) {
            s.addAll(interfaceToSubinterfaces.get(c));
          }
          if (interfaceToImplementers.containsKey(c)) {
            s.addAll(interfaceToImplementers.get(c));
          }
        }
        return ret;
      } else if (t instanceof RefType) {
        RefType concreteType = (RefType) t;
        SootClass concreteClass = concreteType.getSootClass();
        if (!canStoreClass(concreteClass, declaringClass)) {
          continue;
        }
        SootMethod concreteM = null;
        try {
          concreteM = resolveConcreteDispatch(concreteClass, m);
        } catch (Exception e) {
          concreteM = null;
        }
        if (concreteM != null) {
          ret.add(concreteM);
        }
      } else if (t instanceof ArrayType) {
        SootMethod concreteM = null;
        try {
          concreteM = resolveConcreteDispatch(RefType.v("java.lang.Object").getSootClass(), m);
        } catch (Exception e) {
          concreteM = null;
        }
        if (concreteM != null) {
          ret.add(concreteM);
        }
      } else {
        throw new RuntimeException("Unrecognized reaching type " + t);
      }
    }
    return ret;
  }

  public Collection<SootMethod> resolveConcreteDispatch(Collection<Type> concreteTypes, SootMethod m,
      RefType declaredTypeOfBase) {

    Set<SootMethod> ret = new HashSet<SootMethod>();
    SootClass declaringClass = declaredTypeOfBase.getSootClass();
    declaringClass.checkLevel(SootClass.HIERARCHY);
    for (final Type t : concreteTypes) {
      if (t instanceof AnySubType) {
        HashSet<SootClass> s = new HashSet<SootClass>();
        s.add(declaringClass);
        while (!s.isEmpty()) {
          SootClass c = s.iterator().next();
          s.remove(c);
          if (!c.isInterface() && !c.isAbstract() && canStoreClass(c, declaringClass)) {
            SootMethod concreteM = resolveConcreteDispatch(c, m);
            if (concreteM != null) {
              ret.add(concreteM);
            }
          }
          if (classToSubclasses.containsKey(c)) {
            s.addAll(classToSubclasses.get(c));
          }
          if (interfaceToSubinterfaces.containsKey(c)) {
            s.addAll(interfaceToSubinterfaces.get(c));
          }
          if (interfaceToImplementers.containsKey(c)) {
            s.addAll(interfaceToImplementers.get(c));
          }
        }
        return ret;
      } else if (t instanceof RefType) {
        RefType concreteType = (RefType) t;
        SootClass concreteClass = concreteType.getSootClass();
        if (!canStoreClass(concreteClass, declaringClass)) {
          continue;
        }
        SootMethod concreteM = resolveConcreteDispatch(concreteClass, m);
        if (concreteM != null) {
          ret.add(concreteM);
        }
      } else if (t instanceof ArrayType) {
        SootMethod concreteM = resolveConcreteDispatch(RefType.v("java.lang.Object").getSootClass(), m);
        if (concreteM != null) {
          ret.add(concreteM);
        }
      } else {
        throw new RuntimeException("Unrecognized reaching type " + t);
      }
    }
    return ret;
  }

  // Questions about method invocation.

  /** Returns true if the method m is visible from code in the class from. */
  private boolean isVisible(SootClass from, SootMethod m) {
    from.checkLevel(SootClass.HIERARCHY);
    if (m.isPublic()) {
      return true;
    }
    if (m.isPrivate()) {
      return from.equals(m.getDeclaringClass());
    }
    if (m.isProtected()) {
      return canStoreClass(from, m.getDeclaringClass());
    }
    // m is package
    return from.getJavaPackageName().equals(m.getDeclaringClass().getJavaPackageName());
    // || canStoreClass( from, m.getDeclaringClass() );
  }

  /**
   * Given an object of declared type C, returns the methods which could be called on an o.f() invocation.
   */
  public Set<SootMethod> resolveAbstractDispatch(SootClass abstractType, SootMethod m) {
    String methodSig = m.getSubSignature();
    HashSet<SootClass> resolved = new HashSet<SootClass>();
    HashSet<SootMethod> ret = new HashSet<SootMethod>();
    ArrayDeque<SootClass> worklist = new ArrayDeque<SootClass>();
    worklist.add(abstractType);
    while (true) {
      SootClass concreteType = worklist.poll();
      if (concreteType == null) {
        break;
      }
      SootClass savedConcreteType = concreteType;
      if (concreteType.isInterface()) {
        worklist.addAll(getAllImplementersOfInterface(concreteType));
        continue;
      }
      Collection<SootClass> c = classToSubclasses.get(concreteType);
      if (c != null) {
        worklist.addAll(c);
      }
      if (!concreteType.isAbstract()) {
        while (true) {
          if (resolved.contains(concreteType)) {
            break;
          }
          resolved.add(concreteType);
          SootMethod method = concreteType.getMethodUnsafe(methodSig);
          if (method != null) {
            if (isVisible(concreteType, m)) {
              if (method.isAbstract()) {
                throw new RuntimeException("abstract dispatch resolved to abstract method!\nAbstract Type: " + abstractType
                    + "\nConcrete Type: " + savedConcreteType + "\nMethod: " + m);
              } else {
                ret.add(method);
                break;
              }
            }
          }
          SootClass superClass = concreteType.getSuperclassUnsafe();
          if (superClass == null) {
            if (concreteType.isPhantom()) {
              break;
            } else {
              throw new RuntimeException("could not resolve abstract dispatch!\nAbstract Type: " + abstractType
                  + "\nConcrete Type: " + savedConcreteType + "\nMethod: " + m);
            }
          }
          concreteType = superClass;
        }
      }
    }
    return ret;
  }

  /**
   * Given an object of actual type C (o = new C()), returns the method which will be called on an o.f() invocation.
   */
  public SootMethod resolveConcreteDispatch(SootClass concreteType, SootMethod m) {
    concreteType.checkLevel(SootClass.HIERARCHY);
    if (concreteType.isInterface()) {
      throw new RuntimeException("A concrete type cannot be an interface: " + concreteType);
    }

    String methodSig = m.getSubSignature();
    while (true) {
      SootMethod method = concreteType.getMethodUnsafe(methodSig);
      if (method != null) {
        if (isVisible(concreteType, m)) {
          if (method.isAbstract()) {
            throw new RuntimeException("Error: Method call resolves to abstract method!");
          }
          return method;
        }
      }
      concreteType = concreteType.getSuperclassUnsafe();
      if (concreteType == null) {
        break;
      }
    }
    // When there is no proper dispatch found, we simply return null to let
    // the caller decide what to do
    return null;
    // throw new RuntimeException("could not resolve concrete
    // dispatch!\nType: "+concreteType+"\nMethod: "+m);
  }

  /** Returns the target for the given SpecialInvokeExpr. */
  public SootMethod resolveSpecialDispatch(SpecialInvokeExpr ie, SootMethod container) {
    SootMethod target = ie.getMethod();

    /*
     * This is a bizarre condition! Hopefully the implementation is correct. See VM Spec, 2nd Edition, Chapter 6, in the
     * definition of invokespecial.
     */
    if (target.getName().equals("<init>") || target.isPrivate()) {
      return target;
    } else if (isSubclass(target.getDeclaringClass(), container.getDeclaringClass())) {
      return resolveConcreteDispatch(container.getDeclaringClass(), target);
    } else {
      return target;
    }
  }

  /**
   * Gets the direct subclasses of a given class. The class needs to be resolved at least at the HIERARCHY level.
   *
   * @param c
   *          the class
   * @return a collection (possibly empty) of the direct subclasses
   */
  public Collection<SootClass> getSubclassesOf(SootClass c) {
    c.checkLevel(SootClass.HIERARCHY);
    Collection<SootClass> ret = classToSubclasses.get(c);
    if (ret == null) {
      return Collections.emptyList();
    }
    return ret;
  }
}
