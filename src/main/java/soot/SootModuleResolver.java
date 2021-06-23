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

/**
 * Loads symbols for SootClasses from either class files or jimple files.
 * 
 * @author Andreas Dann
 */
public class SootModuleResolver extends SootResolver {

  public SootModuleResolver(Singletons.Global g) {
    super(g);
  }

  public static SootModuleResolver v() {
    return G.v().soot_SootModuleResolver();
  }

  public SootClass makeClassRef(String className, Optional<String> moduleName) {
    // If this class name is escaped, we need to un-escape it
    className = Scene.unescapeName(className);

    String module = null;
    if (moduleName.isPresent()) {
      module = ModuleUtil.v().declaringModule(className, moduleName.get());
    }

    // if no module return first one found
    final ModuleScene modScene = ModuleScene.v();
    if (modScene.containsClass(className, Optional.fromNullable(module))) {
      return modScene.getSootClass(className, Optional.fromNullable(module));
    }

    SootClass newClass;
    if (className.endsWith(SootModuleInfo.MODULE_INFO)) {
      newClass = new SootModuleInfo(className, module);
    } else {
      newClass = modScene.makeSootClass(className, module);
    }
    newClass.setResolvingLevel(SootClass.DANGLING);
    modScene.addClass(newClass);

    return newClass;
  }

  @Override
  public SootClass makeClassRef(String className) {
    ModuleUtil.ModuleClassNameWrapper wrapper = ModuleUtil.v().makeWrapper(className);
    return makeClassRef(wrapper.getClassName(), wrapper.getModuleNameOptional());
  }

  /**
   * Resolves the given class. Depending on the resolver settings, may decide to resolve other classes as well. If the class
   * has already been resolved, just returns the class that was already resolved.
   */
  public SootClass resolveClass(String className, int desiredLevel, Optional<String> moduleName) {
    SootClass resolvedClass = null;
    try {
      resolvedClass = makeClassRef(className, moduleName);
      addToResolveWorklist(resolvedClass, desiredLevel);
      processResolveWorklist();
      return resolvedClass;
    } catch (SootClassNotFoundException e) {
      // remove unresolved class and rethrow
      if (resolvedClass != null) {
        assert (resolvedClass.resolvingLevel() == SootClass.DANGLING);
        ModuleScene.v().removeClass(resolvedClass);
      }
      throw e;
    }
  }

  @Override
  public SootClass resolveClass(String className, int desiredLevel) {
    ModuleUtil.ModuleClassNameWrapper wrapper = ModuleUtil.v().makeWrapper(className);
    return resolveClass(wrapper.getClassName(), desiredLevel, wrapper.getModuleNameOptional());
  }
}
