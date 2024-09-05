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

import java.util.ArrayDeque;

import soot.dotnet.types.DotNetBasicTypes;
import soot.options.Options;
import soot.util.Switch;

/**
 * A class that models Java's reference types. RefTypes are parameterized by a class name. Two RefType are equal iff they are
 * parameterized by the same class name as a String.
 */
@SuppressWarnings("serial")
public class RefType extends RefLikeType implements Comparable<RefType> {

  /**
   * the class name that parameterizes this RefType
   */
  private String className;
  private AnySubType anySubType;
  protected volatile SootClass sootClass;

  public RefType(Singletons.Global g) {
    this.className = "";
  }

  protected RefType(String className) {
    if (!className.isEmpty()) {
      if (className.charAt(0) == '[') {
        throw new RuntimeException("Attempt to create RefType whose name starts with [ --> " + className);
      }
      if (className.indexOf('/') >= 0) {
        throw new RuntimeException("Attempt to create RefType containing a / --> " + className);
      }
      if (className.indexOf(';') >= 0) {
        throw new RuntimeException("Attempt to create RefType containing a ; --> " + className);
      }
    }
    this.className = className;
  }

  public static RefType v() {
    G g = G.v();
    if (g.soot_ModuleUtil().isInModuleMode()) {
      return g.soot_ModuleRefType();
    } else {
      return g.soot_RefType();
    }
  }

  /**
   * Create a RefType for a class.
   *
   * @param className
   *          The name of the class used to parameterize the created RefType.
   * @return a RefType for the given class name.
   */
  public static RefType v(String className) {
    if (ModuleUtil.module_mode()) {
      return ModuleRefType.v(className);
    } else {
      return Scene.v().getOrAddRefType(className);
    }
  }

  /**
   * Create a RefType for a class.
   *
   * @param c
   *          A SootClass for which to create a RefType.
   * @return a RefType for the given SootClass..
   */
  public static RefType v(SootClass c) {
    if (ModuleUtil.module_mode()) {
      return ModuleRefType.v(c.getName(), Optional.fromNullable(c.moduleName));
    } else {
      return v(c.getName());
    }
  }

  public String getClassName() {
    return className;
  }

  @Override
  public int compareTo(RefType t) {
    return this.toString().compareTo(t.toString());
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
   * Two RefTypes are considered equal if they are parameterized by the same class name String.
   *
   * @param t
   *          an object to test for equality. @ return true if t is a RefType parameterized by the same name as this.
   */
  @Override
  public boolean equals(Object t) {
    return ((t instanceof RefType) && className.equals(((RefType) t).className));
  }

  @Override
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

  @Override
  public int hashCode() {
    return className.hashCode();
  }

  @Override
  public void apply(Switch sw) {
    ((TypeSwitch) sw).caseRefType(this);
  }

  /**
   * Returns the least common superclass of this type and other.
   */
  @Override
  public Type merge(Type other, Scene cm) {
    if (other.equals(UnknownType.v()) || this.equals(other)) {
      return this;
    }

    if (!(other instanceof RefType)) {
      throw new RuntimeException("illegal type merge: " + this + " and " + other);
    }

    {
      // Return least common superclass
      final SootClass javalangObject = cm.getObjectType().getSootClass();

      ArrayDeque<SootClass> thisHierarchy = new ArrayDeque<>();
      ArrayDeque<SootClass> otherHierarchy = new ArrayDeque<>();

      // Build thisHierarchy
      // This should never be null, so we could also use "while
      // (true)"; but better be safe than sorry.
      for (SootClass sc = cm.getSootClass(this.className); sc != null;) {
        thisHierarchy.addFirst(sc);
        if (sc == javalangObject) {
          break;
        }
        sc = sc.getSuperclassUnsafe();
        if (sc == null) {
          sc = javalangObject;
        }
      }

      // Build otherHierarchy
      // This should never be null, so we could also use "while
      // (true)"; but better be safe than sorry.
      for (SootClass sc = cm.getSootClass(((RefType) other).className); sc != null;) {
        otherHierarchy.addFirst(sc);
        if (sc == javalangObject) {
          break;
        }
        sc = sc.getSuperclassUnsafe();
        if (sc == null) {
          sc = javalangObject;
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

  @Override
  public Type getArrayElementType() {
    if (Options.v().src_prec() == Options.src_prec_dotnet) {
      if (DotNetBasicTypes.SYSTEM_OBJECT.equals(className) || DotNetBasicTypes.SYSTEM_ICLONEABLE.equals(className)) {
        return Scene.v().getObjectType();
      }
    }

    if (JavaBasicTypes.JAVA_LANG_OBJECT.equals(className) || JavaBasicTypes.JAVA_IO_SERIALIZABLE.equals(className)
        || JavaBasicTypes.JAVA_LANG_CLONABLE.equals(className)) {
      return Scene.v().getObjectType();
    }
    throw new RuntimeException("Attempt to get array base type of a non-array");
  }

  public AnySubType getAnySubType() {
    return anySubType;
  }

  public void setAnySubType(AnySubType anySubType) {
    this.anySubType = anySubType;
  }

  @Override
  public boolean isAllowedInFinalCode() {
    return true;
  }
}
