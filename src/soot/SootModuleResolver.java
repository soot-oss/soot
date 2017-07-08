/* Soot - a J*va Optimization Framework
 * Copyright (C) 2000 Patrice Pominville
 * Copyright (C) 2004 Ondrej Lhotak, Ganesh Sittampalam
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

/**
 * Loads symbols for SootClasses from either class files or jimple files.
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
        className = Scene.v().unescapeName(className);


        String module = null;
        if (moduleName.isPresent()) {
            module = ModuleGraphUtil.findModuleThatExports(className, moduleName.get());
        }

        //if no module return first one found
        if (ModuleScene.v().containsClass(className, Optional.fromNullable(module)))
            return ModuleScene.v().getSootClass(className, Optional.fromNullable(module));

        SootClass newClass;
        if (className.endsWith(SootModuleInfo.MODULE_INFO)) {
            newClass = new SootModuleInfo(className, module);
        } else {
            newClass = ModuleScene.v().makeSootClass(className, module);
        }
        newClass.setResolvingLevel(SootClass.DANGLING);
        ModuleScene.v().addClass(newClass);

        return newClass;
    }

    public SootClass makeClassRef(String className) {
        String moduleName = null;
        String refinedClassName = className;
        if (className.contains(":")) {
            String split[] = className.split(":");
            refinedClassName = split[1];
            moduleName = split[0];
        }

        return makeClassRef(refinedClassName, Optional.fromNullable(moduleName));
    }

    /**
     * Resolves the given class. Depending on the resolver settings, may decide
     * to resolve other classes as well. If the class has already been resolved,
     * just returns the class that was already resolved.
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
                assert resolvedClass.resolvingLevel() == SootClass.DANGLING;
                ModuleScene.v().removeClass(resolvedClass);
            }
            throw e;
        }
    }

    public SootClass resolveClass(String className, int desiredLevel) {
        String moduleName = null;
        String refinedClassName = className;
        if (className.contains(":")) {
            String split[] = className.split(":");
            refinedClassName = split[1];
            moduleName = split[0];
        }
        return resolveClass(refinedClassName, desiredLevel, Optional.fromNullable(moduleName));
    }


}
