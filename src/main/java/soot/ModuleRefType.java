/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

/*
 * Modified by the Sable Research Group and others 1997-1999.
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */

package soot;

import com.google.common.base.Optional;

import java.util.LinkedList;

import soot.options.Options;

/**
 * A class that models Java's reference types. RefTypes are parametrized by a class name. Two RefType are equal iff they are
 * parametrized by the same class name as a String. Extends RefType in order to deal with Java 9 modules.
 * 
 * @author adann
 */

@SuppressWarnings("serial")
public class ModuleRefType extends RefType {
  public ModuleRefType(Singletons.Global g) {
    super(g);
  }

  public String getModuleName() {
    return moduleName;
  }

  private String moduleName;

  protected ModuleRefType(String className, String moduleName) {
    super(className);
    this.moduleName = moduleName;
  }

  public static RefType v(String className) {
    ModuleUtil.ModuleClassNameWrapper wrapper = ModuleUtil.v().makeWrapper(className);
    return v(wrapper.getClassName(), wrapper.getModuleNameOptional());
  }

  public static RefType v(String className, Optional<String> moduleName) {
    String module = null;
    if (moduleName.isPresent()) {
      module = ModuleUtil.v().findModuleThatExports(className, moduleName.get());
    }
    if (!moduleName.isPresent() && Options.v().verbose()) {
      G.v().out.println("[WARN] ModuleRefType called with empty module for: " + className);
    }
    RefType rt = ModuleScene.v().getRefTypeUnsafe(className, Optional.fromNullable(module));
    if (rt == null) {
      if (!moduleName.isPresent()) {
        rt = new ModuleRefType(className, null);
      } else {
        rt = new ModuleRefType(className, module);
      }
      ModuleScene.v().addRefType(rt);
    }
    return rt;
  }

  /**
   * Get the SootClass object corresponding to this RefType.
   *
   * @return the corresponding SootClass
   */
  @Override
  public SootClass getSootClass() {
    if (super.sootClass == null) {
      sootClass = SootModuleResolver.v().makeClassRef(getClassName(), Optional.fromNullable(this.moduleName));
    }
    return sootClass;
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

      SootClass thisClass = ((ModuleScene) cm).getSootClass(getClassName(), Optional.fromNullable(this.moduleName));
      SootClass otherClass
          = ((ModuleScene) cm).getSootClass(((RefType) other).getClassName(), Optional.fromNullable(this.moduleName));
      SootClass javalangObject = cm.getObjectType().getSootClass();

      LinkedList<SootClass> thisHierarchy = new LinkedList<>();
      LinkedList<SootClass> otherHierarchy = new LinkedList<>();

      // Build thisHierarchy
      {
        SootClass SootClass = thisClass;

        for (;;) {
          thisHierarchy.addFirst(SootClass);

          if (SootClass == javalangObject) {
            break;
          }

          if (SootClass.hasSuperclass()) {
            SootClass = SootClass.getSuperclass();
          } else {
            SootClass = javalangObject;
          }
        }
      }

      // Build otherHierarchy
      {
        SootClass SootClass = otherClass;

        for (;;) {
          otherHierarchy.addFirst(SootClass);

          if (SootClass == javalangObject) {
            break;
          }

          if (SootClass.hasSuperclass()) {
            SootClass = SootClass.getSuperclass();
          } else {
            SootClass = javalangObject;
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

  @Override
  public Type getArrayElementType() {
    if (getClassName().equals("java.lang.Object") || getClassName().equals("java.io.Serializable")
        || getClassName().equals("java.lang.Cloneable")) {
      return ModuleRefType.v("java.lang.Object", Optional.of("java.base"));
    }
    throw new RuntimeException("Attempt to get array base type of a non-array");
  }

}
