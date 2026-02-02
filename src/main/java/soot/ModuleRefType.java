package soot;

/*-
 * #%L
 * Soot - a J*va Optimization Framework
 * %%
 * Copyright (C) 1997 - 2014 Raja Vallee-Rai and others
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

import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.dotnet.types.DotNetBasicTypes;
import soot.options.Options;

/**
 * A class that models Java's reference types. RefTypes are parameterized by a class name. Two RefType are equal iff they are
 * parameterized by the same class name as a String. Extends RefType in order to deal with Java 9 modules.
 * 
 * @author Andreas Dann
 */
public class ModuleRefType extends RefType {
  private static final Logger logger = LoggerFactory.getLogger(ModuleRefType.class);

  private String moduleName;

  public ModuleRefType(Singletons.Global g) {
    super(g);
  }

  protected ModuleRefType(String className, String moduleName) {
    super(className);
    this.moduleName = moduleName;
  }

  public static RefType v(String className) {
    ModuleUtil.ModuleClassNameWrapper wrapper = ModuleUtil.v().makeWrapper(className);
    return v(wrapper.getClassName(), wrapper.getModuleNameOptional());
  }

  public static RefType v(String className, Optional<String> moduleName) {
    final boolean isPresent = moduleName.isPresent();
    String module = isPresent ? ModuleUtil.v().declaringModule(className, moduleName.get()) : null;

    if (!isPresent && Options.v().verbose()) {
      logger.warn("ModuleRefType called with empty module for: " + className);
    }
    RefType rt = ModuleScene.v().getRefTypeUnsafe(className, Optional.fromNullable(module));
    if (rt == null) {
      rt = new ModuleRefType(className, isPresent ? module : null);
      ModuleScene.v().addRefType(rt);
    }
    return rt;
  }

  public String getModuleName() {
    return moduleName;
  }

  /**
   * Get the SootClass object corresponding to this RefType.
   *
   * @return the corresponding SootClass
   */
  @Override
  public SootClass getSootClass() {
    if (super.sootClass == null) {
      super.setSootClass(SootModuleResolver.v().makeClassRef(getClassName(), Optional.fromNullable(this.moduleName)));
    }
    return super.getSootClass();
  }

  /**
   * Returns the least common superclass of this type and other.
   */
  @Override
  public Type merge(Type other, Scene cm) {
    if (UnknownType.v().equals(other) || this.equals(other)) {
      return this;
    }

    if (!(other instanceof RefType)) {
      throw new RuntimeException("illegal type merge: " + this + " and " + other);
    }

    {
      // Return least common superclass
      final ModuleScene cmMod = (ModuleScene) cm;

      final SootClass javalangObject = cm.getObjectType().getSootClass();

      LinkedList<SootClass> thisHierarchy = new LinkedList<>();
      LinkedList<SootClass> otherHierarchy = new LinkedList<>();

      // Build thisHierarchy
      for (SootClass sc = cmMod.getSootClass(getClassName(), Optional.fromNullable(this.moduleName));;) {
        thisHierarchy.addFirst(sc);
        if (sc == javalangObject) {
          break;
        }
        sc = sc.hasSuperclass() ? sc.getSuperclass() : javalangObject;
      }

      // Build otherHierarchy
      for (SootClass sc = cmMod.getSootClass(((RefType) other).getClassName(), Optional.fromNullable(this.moduleName));;) {
        otherHierarchy.addFirst(sc);
        if (sc == javalangObject) {
          break;
        }
        sc = sc.hasSuperclass() ? sc.getSuperclass() : javalangObject;
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
    if (getClassName().equals(Scene.v().getObjectType().toString())) {
      return ModuleRefType.v(Scene.v().getObjectType().toString());
    }
    switch (getClassName()) {
      case "java.lang.Object":
      case "java.io.Serializable":
      case "java.lang.Cloneable":
        return ModuleRefType.v("java.lang.Object", Optional.of("java.base"));
      case DotNetBasicTypes.SYSTEM_ARRAY:
        return ModuleRefType.v(DotNetBasicTypes.SYSTEM_OBJECT);
      default:
        throw new RuntimeException("Attempt to get array base type of a non-array");
    }
  }
}
