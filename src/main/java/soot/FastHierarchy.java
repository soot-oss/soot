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
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.options.Options;
import soot.util.ConcurrentHashMultiMap;
import soot.util.MultiMap;

/**
 * Represents the class hierarchy. It is closely linked to a Scene, and must be recreated if the
 * Scene changes.
 *
 * <p>This version supercedes the old soot.Hierarchy class.
 *
 * @author Ondrej Lhotak
 */
public class FastHierarchy {
  private static final Logger LOGGER = LoggerFactory.getLogger(FastHierarchy.class);

  /**
   * This map holds all key,value pairs such that value.getSuperclass() == key. This is one of the
   * three maps that hold the inverse of the relationships given by the getSuperclass and
   * getInterfaces methods of SootClass.
   */
  protected MultiMap<SootClass, SootClass> classToSubclasses =
      new ConcurrentHashMultiMap<SootClass, SootClass>();

  /**
   * This map holds all key,value pairs such that value is an interface and key is in
   * value.getInterfaces(). This is one of the three maps that hold the inverse of the relationships
   * given by the getSuperclass and getInterfaces methods of SootClass.
   */
  protected MultiMap<SootClass, SootClass> interfaceToSubinterfaces =
      new ConcurrentHashMultiMap<SootClass, SootClass>();

  /**
   * This map holds all key,value pairs such that value is a class (NOT an interface) and key is in
   * value.getInterfaces(). This is one of the three maps that hold the inverse of the relationships
   * given by the getSuperclass and getInterfaces methods of SootClass.
   */
  protected MultiMap<SootClass, SootClass> interfaceToImplementers =
      new ConcurrentHashMultiMap<SootClass, SootClass>();

  /**
   * This map is a transitive closure of interfaceToSubinterfaces, and each set contains its
   * superinterface itself.
   */
  protected MultiMap<SootClass, SootClass> interfaceToAllSubinterfaces =
      new ConcurrentHashMultiMap<SootClass, SootClass>();

  /**
   * This map gives, for an interface, all concrete classes that implement that interface and all
   * its subinterfaces, but NOT their subclasses.
   */
  protected MultiMap<SootClass, SootClass> interfaceToAllImplementers =
      new ConcurrentHashMultiMap<SootClass, SootClass>();

  /**
   * For each class (NOT interface), this map contains a Interval, which is a pair of numbers giving
   * a preorder and postorder ordering of classes in the inheritance tree.
   */
  protected Map<SootClass, Interval> classToInterval = new HashMap<SootClass, Interval>();

  protected Scene sc;

  protected final RefType rtObject;
  protected final RefType rtSerializable;
  protected final RefType rtCloneable;

  protected class Interval {
    int lower;
    int upper;

    public Interval() {}

    public Interval(int lower, int upper) {
      this.lower = lower;
      this.upper = upper;
    }

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

    this.rtObject = Scene.v().getObjectType();
    this.rtSerializable = RefType.v("java.io.Serializable");
    this.rtCloneable = RefType.v("java.lang.Cloneable");

    /* First build the inverse maps. */
    buildInverseMaps();

    /* Now do a dfs traversal to get the Interval numbers. */
    int r = dfsVisit(0, sc.getSootClass("java.lang.Object"));
    /*
     * also have to traverse for all phantom classes because they also can be roots of the type hierarchy
     */
    for (final Iterator<SootClass> phantomClassIt = sc.getPhantomClasses().snapshotIterator();
        phantomClassIt.hasNext(); ) {
      SootClass phantomClass = phantomClassIt.next();
      if (!phantomClass.isInterface()) {
        r = dfsVisit(r, phantomClass);
      }
    }
  }

  protected void buildInverseMaps() {
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
  }

  /**
   * Return true if class child is a subclass of class parent, neither of them being allowed to be
   * interfaces. If we don't know any of the classes, we always return false
   */
  public boolean isSubclass(SootClass child, SootClass parent) {
    child.checkLevel(SootClass.HIERARCHY);
    parent.checkLevel(SootClass.HIERARCHY);
    Interval parentInterval = classToInterval.get(parent);
    Interval childInterval = classToInterval.get(child);
    return parentInterval != null
        && childInterval != null
        && parentInterval.isSubrange(childInterval);
  }

  /**
   * For an interface parent (MUST be an interface), returns set of all implementers of it but NOT
   * their subclasses.
   *
   * <p>This method can be used concurrently (is thread safe).
   *
   * @param parent the parent interface.
   * @return an set, possibly empty
   */
  public Set<SootClass> getAllImplementersOfInterface(SootClass parent) {
    parent.checkLevel(SootClass.HIERARCHY);
    Set<SootClass> result = interfaceToAllImplementers.get(parent);
    if (result.size() > 0) {
      return result;
    }
    result = new HashSet<>();
    for (SootClass subinterface : getAllSubinterfaces(parent)) {
      if (subinterface == parent) {
        continue;
      }
      result.addAll(getAllImplementersOfInterface(subinterface));
    }
    result.addAll(interfaceToImplementers.get(parent));
    interfaceToAllImplementers.putAll(parent, result);
    return result;
  }

  /**
   * For an interface parent (MUST be an interface), returns set of all subinterfaces including
   * <code>parent</code>.
   *
   * <p>This method can be used concurrently (is thread safe).
   *
   * @param parent the parent interface.
   * @return an set, possibly empty
   */
  public Set<SootClass> getAllSubinterfaces(SootClass parent) {
    parent.checkLevel(SootClass.HIERARCHY);
    if (!parent.isInterface()) {
      return Collections.emptySet();
    }
    Set<SootClass> result = interfaceToAllSubinterfaces.get(parent);
    if (result.size() > 0) {
      return result;
    }
    result = new HashSet<>();
    result.add(parent);
    for (SootClass si : interfaceToSubinterfaces.get(parent)) {
      result.addAll(getAllSubinterfaces(si));
    }
    interfaceToAllSubinterfaces.putAll(parent, result);
    return result;
  }

  /**
   * Given an object of declared type child, returns true if the object can be stored in a variable
   * of type parent. If child is an interface that is not a subinterface of parent, this method will
   * return false even though some objects implementing the child interface may also implement the
   * parent interface.
   */
  public boolean canStoreType(Type child, Type parent) {
    if (child == parent || child.equals(parent)) {
      return true;
    } else if (parent instanceof NullType) {
      return false;
    } else if (child instanceof NullType) {
      return parent instanceof RefLikeType;
    } else if (child instanceof RefType) {
      if (parent == rtObject) {
        return true;
      } else if (parent instanceof RefType) {
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
        return base == rtObject || base == rtSerializable || base == rtCloneable;
      } else {
        SootClass base = ((AnySubType) child).getBase().getSootClass();
        SootClass parentClass = ((RefType) parent).getSootClass();
        Deque<SootClass> worklist = new ArrayDeque<SootClass>();
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
          } else if (!workset.add(cl)) {
            continue;
          } else if (cl.isConcrete() && canStoreClass(cl, parentClass)) {
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
        return parent == rtObject || parent == rtSerializable || parent == rtCloneable;
      } else if (!(parent instanceof ArrayType)) {
        return false;
      }
      ArrayType aparent = (ArrayType) parent;

      // You can store a int[][] in a Object[]. Yuck!
      // Also, you can store a Interface[] in a Object[]
      if (achild.numDimensions == aparent.numDimensions) {
        if (achild.baseType.equals(aparent.baseType)) {
          return true;
        } else if (!(achild.baseType instanceof RefType)) {
          return false;
        } else if (!(aparent.baseType instanceof RefType)) {
          return false;
        } else {
          return canStoreType(achild.baseType, aparent.baseType);
        }
      } else if (achild.numDimensions > aparent.numDimensions) {
        if (aparent.baseType == rtObject) {
          return true;
        } else if (aparent.baseType == rtSerializable || aparent.baseType == rtCloneable) {
          return true;
        } else {
          return false;
        }
      } else {
        return false;
      }
    } else {
      return false;
    }
  }

  /**
   * Given an object of declared type child, returns true if the object can be stored in a variable
   * of type parent. If child is an interface that is not a subinterface of parent, this method will
   * return false even though some objects implementing the child interface may also implement the
   * parent interface.
   */
  public boolean canStoreClass(SootClass child, SootClass parent) {
    parent.checkLevel(SootClass.HIERARCHY);
    child.checkLevel(SootClass.HIERARCHY);
    Interval parentInterval = classToInterval.get(parent);
    Interval childInterval = classToInterval.get(child);
    if (parentInterval != null && childInterval != null) {
      return parentInterval.isSubrange(childInterval);
    } else if (childInterval == null) { // child is interface
      if (parentInterval != null) { // parent is not interface
        return parent == rtObject.getSootClass();
      } else {
        return getAllSubinterfaces(parent).contains(child);
      }
    } else {
      final Set<SootClass> impl = getAllImplementersOfInterface(parent);
      if (impl.size() > 1000) {
        // If we have more than 1000 entries it is quite time consuming to check each and every
        // implementing class
        // if it is the "child" class. Therefore we use an alternative implementation which just
        // checks the client
        // class it's super classes and all the interfaces it implements.

        return canStoreClassClassic(child, parent);
      } else {
        // If we only have a few entries, you can't beat the performance of a plain old loop
        // in combination with the interval approach.
        for (SootClass c : impl) {
          Interval interval = classToInterval.get(c);
          if (interval != null && interval.isSubrange(childInterval)) {
            return true;
          }
        }
        return false;
      }
    }
  }

  /**
   * "Classic" implementation using the intuitive approach (without using {@link Interval}) to check
   * whether <code>child</code> can be stored in a type of <code>parent</code>:
   *
   * <p>If <code>parent</code> is not an interface we simply traverse and check the super-classes of
   * <code>child</code>.
   *
   * <p>If <code>parent</code> is an interface we traverse the super-classes of <code>child</code>
   * and check each interface implemented by this class. Also each interface is checked recursively
   * for super interfaces it implements.
   *
   * <p>This implementation can be much faster (compared to the interval based implementation of
   * {@link #canStoreClass(SootClass, SootClass)} in cases where one interface is implemented in
   * thousands of classes.
   *
   * @param child
   * @param parent
   * @return
   */
  protected boolean canStoreClassClassic(final SootClass child, final SootClass parent) {
    SootClass sc = child;
    final boolean parentIsInterface = parent.isInterface();
    while (sc != null) {
      if (sc == parent) {
        // We finally found the correct class/interface
        return true;
      }
      if (parentIsInterface) {
        // Interfaces can only extend other interfaces - therefore we only have to consider the
        // interfaces of the child class if parent is an interface.
        for (SootClass interf : sc.getInterfaces()) {
          if (canStoreClassClassic(interf, parent)) {
            return true;
          }
        }
      }
      sc = sc.getSuperclassUnsafe();
    }
    return false;
  }

  public Collection<SootMethod> resolveConcreteDispatchWithoutFailing(
      Collection<Type> concreteTypes, SootMethod m, RefType declaredTypeOfBase) {

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
          {
            Set<SootClass> subclasses = classToSubclasses.get(c);
            if (subclasses != null) {
              s.addAll(subclasses);
            }
          }
          {
            Set<SootClass> subinterfaces = interfaceToSubinterfaces.get(c);
            if (subinterfaces != null) {
              s.addAll(subinterfaces);
            }
          }
          {
            Set<SootClass> implementers = interfaceToImplementers.get(c);
            if (implementers != null) {
              s.addAll(implementers);
            }
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

  public Collection<SootMethod> resolveConcreteDispatch(
      Collection<Type> concreteTypes, SootMethod m, RefType declaredTypeOfBase) {

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
          {
            Set<SootClass> subclasses = classToSubclasses.get(c);
            if (subclasses != null) {
              s.addAll(subclasses);
            }
          }
          {
            Set<SootClass> subinterfaces = interfaceToSubinterfaces.get(c);
            if (subinterfaces != null) {
              s.addAll(subinterfaces);
            }
          }
          {
            Set<SootClass> implementers = interfaceToImplementers.get(c);
            if (implementers != null) {
              s.addAll(implementers);
            }
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
        SootMethod concreteM = resolveConcreteDispatch(rtObject.getSootClass(), m);
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
  public boolean isVisible(SootClass from, SootMethod m) {
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
   * Given an object of declared type C, returns the methods which could be called on an o.f()
   * invocation.
   *
   * @param baseType The declared type C
   */
  public Set<SootMethod> resolveAbstractDispatch(SootClass baseType, SootMethod m) {
    HashSet<SootClass> resolved = new HashSet<>();
    HashSet<SootMethod> ret = new HashSet<>();
    ArrayDeque<SootClass> worklist = new ArrayDeque<>();

    worklist.add(baseType);
    while (true) {
      SootClass concreteType = worklist.poll();
      if (concreteType == null) {
        break;
      }

      if (concreteType.isInterface()) {
        worklist.addAll(getAllImplementersOfInterface(concreteType));
        // since java 8 we can have default methods in interfaces
        if (isHandleDefaultMethods()) {
          // we need to thus search in all subinterfaces and in the current interface
          worklist.addAll(getAllSubinterfaces(concreteType));
        } else {
          break;
        }
      } else {
        Collection<SootClass> c = classToSubclasses.get(concreteType);
        if (c != null) {
          worklist.addAll(c);
        }
      }

      SootMethod resolvedMethod = internalConcreteDispatch(baseType, m, resolved);
      if (resolvedMethod != null) {
        ret.add(resolvedMethod);
      }
    }

    return ret;
  }

  /**
   * Given an object of actual type C (o = new C()), returns the method which will be called on an
   * o.f() invocation.
   *
   * @param baseType The actual type C
   */
  @Nullable
  public SootMethod resolveConcreteDispatch(SootClass baseType, SootMethod m) {
    baseType.checkLevel(SootClass.HIERARCHY);
    if (baseType.isInterface()) {
      throw new RuntimeException("A concrete type cannot be an interface: " + baseType);
    }

    return internalConcreteDispatch(baseType, m);
    // throw new RuntimeException("could not resolve concrete
    // dispatch!\nType: "+baseType+"\nMethod: "+m);
  }

  /**
   * Conducts the actual dispatch by searching up the baseType's superclass hierarchy and interface
   * hierarchy if the sourcecode level is beyond Java 7 (due to defautl interface methods.) Given an
   * object of actual type C (o = new C()), returns the method which will be called on an o.f()
   * invocation.
   *
   * @param baseType The type C
   * @param m The method f to resolve
   * @return The concrete method o.f() to call
   */
  private SootMethod internalConcreteDispatch(SootClass baseType, SootMethod m) {
    return internalConcreteDispatch(baseType, m, new HashSet<>());
  }

  /**
   * Conducts the actual dispatch by searching up the baseType's superclass hierarchy and interface
   * hierarchy if the sourcecode level is beyond Java 7 (due to defautl interface methods.) Given an
   * object of actual type C (o = new C()), returns the method which will be called on an o.f()
   * invocation.
   *
   * @param baseType The type C
   * @param m The method f to resolve
   * @param ignoreList A set of classes that should be ignored during dispatch. This set will also
   *     be modified since every traversed class/interface will be added. This is required for the
   *     abstract dispatch to not do additional resolving effort by resolving the same classes
   *     multiple times.
   * @return The concrete method o.f() to call
   */
  private SootMethod internalConcreteDispatch(
      SootClass baseType, SootMethod m, Set<SootClass> ignoreList) {
    SootClass concreteType = baseType;

    while (!ignoreList.contains(concreteType)) {
      ignoreList.add(concreteType);

      SootMethod method = getSignaturePolymorphicMethod(concreteType, m);
      if (method != null) {
        if (isVisible(concreteType, m)) {
          if (method.isAbstract()) {
            break;
          }
          return method;
        }
      }

      SootClass superClass = concreteType.getSuperclassUnsafe();
      if (superClass == null) {
        break;
      }

      concreteType = superClass;
    }

    // for java > 7 we have to go through the interface hierarchy after the superclass hierarchy to
    // look for default methods:
    // https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-5.html#jvms-5.4.3.3
    if (isHandleDefaultMethods()) {
      concreteType = baseType;

      while (concreteType != null) {
        SootMethod candidate = null;

        // we have to determine the "most specific interface"
        for (SootClass iFace : concreteType.getInterfaces()) {
          SootMethod method = getSignaturePolymorphicMethod(iFace, m);
          if (method != null) {
            if (isVisible(iFace, m)) {
              if (method.isAbstract()) {
                // abstract method cannot be dispatched
                continue;
              }
              // check if the found method is more specific than our current candidate
              else if (candidate == null
                  || canStoreClass(method.getDeclaringClass(), candidate.getDeclaringClass())) {
                candidate = method;
              }
            }
          }
        }

        if (candidate != null) {
          // we found the most specific interface in this class
          return candidate;
        }

        // otherwise, we have to search upwards the class hierarchy again
        concreteType = concreteType.getSuperclassUnsafe();
      }
    }

    // When there is no proper dispatch found, we simply return null to let
    // the caller decide what to do
    LOGGER.warn("Could not resolve dispatch!\nBase Type: " + baseType + "\nMethod: " + m);
    return null;
  }

  private boolean isHandleDefaultMethods() {
    int version = Options.v().java_version();
    return version == 0 || version > 7;
  }

  /** Returns the target for the given SpecialInvokeExpr. */
  public SootMethod resolveSpecialDispatch(SootMethod callee, SootMethod container) {
    /*
     * This is a bizarre condition! Hopefully the implementation is correct. See VM Spec, 2nd Edition, Chapter 6, in the
     * definition of invokespecial.
     */
    if (callee.getName().equals(SootMethod.constructorName) || callee.isPrivate()) {
      return callee;
    } else if (isSubclass(callee.getDeclaringClass(), container.getDeclaringClass())) {
      return resolveConcreteDispatch(container.getDeclaringClass(), callee);
    } else {
      return callee;
    }
  }

  /**
   * Searches the given class for a method that is signature polymorphic to the given method, i.e.,
   * matches name and parameter types and ensures that the return type is a an equal or subtype of
   * the given mehtod's subtype.
   *
   * @param concreteType
   * @param dispatchMethod
   * @return
   */
  private SootMethod getSignaturePolymorphicMethod(
      SootClass concreteType, SootMethod dispatchMethod) {
    for (SootMethod method : concreteType.getMethods()) {
      if (method.getName().equals(dispatchMethod.getName())
          && method.getParameterTypes().equals(dispatchMethod.getParameterTypes())
          && canStoreType(method.getReturnType(), dispatchMethod.getReturnType())) {
        return method;
      }
    }
    return null;
  }

  /**
   * Gets the direct subclasses of a given class. The class needs to be resolved at least at the
   * HIERARCHY level.
   *
   * @param c the class
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
