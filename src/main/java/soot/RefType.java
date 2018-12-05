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

import java.util.ArrayDeque;

import soot.util.Switch;

/**
 * A class that models Java's reference types. RefTypes are parametrized by a class name. Two RefType are equal iff they are
 * parametrized by the same class name as a String.
 */

@SuppressWarnings("serial")
public class RefType extends RefLikeType implements Comparable<RefType> {
  public RefType(Singletons.Global g) {
    className = "";
  }

  public static RefType v() {
    return G.v().soot_RefType();
  }

  /** the class name that parameterizes this RefType */
  private String className;

  public String getClassName() {
    return className;
  }

  private volatile SootClass sootClass;
  private AnySubType anySubType;

  private RefType(String className) {
    if (className.startsWith("[")) {
      throw new RuntimeException("Attempt to create RefType whose name starts with [ --> " + className);
    }
    if (className.indexOf("/") >= 0) {
      throw new RuntimeException("Attempt to create RefType containing a / --> " + className);
    }
    if (className.indexOf(";") >= 0) {
      throw new RuntimeException("Attempt to create RefType containing a ; --> " + className);
    }
    this.className = className;
  }

  /**
   * Create a RefType for a class.
   *
   * @param className
   *          The name of the class used to parametrize the created RefType.
   * @return a RefType for the given class name.
   */
  public static RefType v(String className) {
    RefType rt = Scene.v().getRefTypeUnsafe(className);
    if (rt == null) {
      rt = new RefType(className);
      return Scene.v().getOrAddRefType(rt);
    }
    return rt;
  }

  public int compareTo(RefType t) {
    return this.toString().compareTo(t.toString());
  }

  /**
   * Create a RefType for a class.
   *
   * @param c
   *          A SootClass for which to create a RefType.
   * @return a RefType for the given SootClass..
   */
  public static RefType v(SootClass c) {
    return v(c.getName());
  }

  /**
   * Get the SootClass object corresponding to this RefType.
   *
   * @return the corresponding SootClass
   */
  public SootClass getSootClass() {
    if (sootClass == null) {
      // System.out.println( "wrning: "+this+" has no sootclass" );
      sootClass = SootResolver.v().makeClassRef(className);
    }
    return sootClass;
  }

  public boolean hasSootClass() {
    return sootClass != null;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  /**
   * Set the SootClass object corresponding to this RefType.
   *
   * @param sootClass
   *          The SootClass corresponding to this RefType.
   */
  public void setSootClass(SootClass sootClass) {
    this.sootClass = sootClass;
  }

  /**
   * 2 RefTypes are considered equal if they are parametrized by the same class name String.
   *
   * @param t
   *          an object to test for equality. @ return true if t is a RefType parametrized by the same name as this.
   */
  public boolean equals(Object t) {
    return ((t instanceof RefType) && className.equals(((RefType) t).className));
  }

  public String toString() {
    return className;
  }

  /**
   * Returns a textual representation, quoted as needed, of this type for serialization, e.g. to .jimple format
   */
  @Override
  public String toQuotedString() {
    return Scene.v().quotedNameOf(className);
  }

  public int hashCode() {
    return className.hashCode();
  }

  public void apply(Switch sw) {
    ((TypeSwitch) sw).caseRefType(this);
  }

  /** Returns the least common superclass of this type and other. */
  public Type merge(Type other, Scene cm) {
    if (other.equals(UnknownType.v()) || this.equals(other)) {
      return this;
    }

    if (!(other instanceof RefType)) {
      throw new RuntimeException("illegal type merge: " + this + " and " + other);
    }

    {
      // Return least common superclass

      SootClass thisClass = cm.getSootClass(this.className);
      SootClass otherClass = cm.getSootClass(((RefType) other).className);
      SootClass javalangObject = cm.getObjectType().getSootClass();

      ArrayDeque<SootClass> thisHierarchy = new ArrayDeque<>();
      ArrayDeque<SootClass> otherHierarchy = new ArrayDeque<>();

      // Build thisHierarchy
      {
        SootClass sootClass = thisClass;

        // This should never be null, so we could also use "while
        // (true)"; but better be safe than sorry.
        while (sootClass != null) {
          thisHierarchy.addFirst(sootClass);
          if (sootClass == javalangObject) {
            break;
          }

          sootClass = sootClass.getSuperclassUnsafe();
          if (sootClass == null) {
            sootClass = javalangObject;
          }
        }
      }

      // Build otherHierarchy
      {
        SootClass sootClass = otherClass;

        // This should never be null, so we could also use "while
        // (true)"; but better be safe than sorry.
        while (sootClass != null) {
          otherHierarchy.addFirst(sootClass);
          if (sootClass == javalangObject) {
            break;
          }

          sootClass = sootClass.getSuperclassUnsafe();
          if (sootClass == null) {
            sootClass = javalangObject;
          }
        }
      }

      // Find least common superclass
      {
        SootClass commonClass = null;

        while (!otherHierarchy.isEmpty() && !thisHierarchy.isEmpty()
            && otherHierarchy.getFirst() == thisHierarchy.getFirst()) {
          commonClass = otherHierarchy.removeFirst();
          thisHierarchy.removeFirst();
        }

        if (commonClass == null) {
          throw new RuntimeException("Could not find a common superclass for " + this + " and " + other);
        }

        return commonClass.getType();
      }
    }

  }

  public Type getArrayElementType() {
    if (className.equals("java.lang.Object") || className.equals("java.io.Serializable")
        || className.equals("java.lang.Cloneable")) {
      return RefType.v("java.lang.Object");
    }
    throw new RuntimeException("Attempt to get array base type of a non-array");
  }

  public AnySubType getAnySubType() {
    return anySubType;
  }

  public void setAnySubType(AnySubType anySubType) {
    this.anySubType = anySubType;
  }

  public boolean isAllowedInFinalCode() {
    return true;
  }

}
