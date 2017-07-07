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

import soot.JastAddJ.*;
import soot.JastAddJ.Program;
import soot.javaToJimple.IInitialResolver.Dependencies;
import soot.options.Options;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import com.google.common.base.Optional;

/**
 * Loads symbols for SootClasses from either class files or jimple files.
 */
public class SootModuleResolver extends SootResolver{
    /**
     * Maps each resolved class to a list of all references in it.
     */
    private final Map<SootClass, Collection<Type>> classToTypesSignature = new HashMap<SootClass, Collection<Type>>();

    /**
     * Maps each resolved class to a list of all references in it.
     */
    private final Map<SootClass, Collection<Type>> classToTypesHierarchy = new HashMap<SootClass, Collection<Type>>();

    /**
     * SootClasses waiting to be resolved.
     */
    @SuppressWarnings("unchecked")
    private final Deque<SootClass>[] worklist = new Deque[4];

    private Program program = null;

    public SootModuleResolver(Singletons.Global g) {
        super(g);
	}

	protected void initializeProgram() {
        if (Options.v().src_prec() != Options.src_prec_apk_c_j) {
            program = new Program();
            program.state().reset();

            program.initBytecodeReader(new BytecodeParser());
            program.initJavaParser(new JavaParser() {
                public CompilationUnit parse(InputStream is, String fileName)
                        throws IOException, beaver.Parser.Exception {
                    return new JastAddJavaParser().parse(is, fileName);
                }
            });

            program.options().initOptions();
            program.options().addKeyValueOption("-classpath");
            program.options().setValueForOption(Scene.v().getSootClassPath(),
                    "-classpath");
            if (Options.v().src_prec() == Options.src_prec_java)
                program.setSrcPrec(Program.SRC_PREC_JAVA);
            else if (Options.v().src_prec() == Options.src_prec_class)
                program.setSrcPrec(Program.SRC_PREC_CLASS);
            else if (Options.v().src_prec() == Options.src_prec_only_class)
                program.setSrcPrec(Program.SRC_PREC_CLASS);
            program.initPaths();
        }
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
            newClass = ModuleScene.v().makeSootClass(className,module);
        }
        newClass.setResolvingLevel(SootClass.DANGLING);
        Scene.v().addClass(newClass);

        return newClass;
    }

    public SootClass makeClassRef(String className) {
        if (Options.v().verbose())
            G.v().out.println("makeClass Ref without module was called");
        return makeClassRef(className, Optional.fromNullable(null));
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
        if (Options.v().verbose())
            G.v().out.println("Resolve class without module was called");
        return resolveClass(className, desiredLevel, Optional.fromNullable(null));
    }


    /**
     * Resolve all classes on toResolveWorklist.
     */
    private void processResolveWorklist() {
        for (int i = SootClass.BODIES; i >= SootClass.HIERARCHY; i--) {
            while (!worklist[i].isEmpty()) {
                SootClass sc = worklist[i].pop();
                if (resolveEverything()) { // Whole program mode
                    boolean onlySignatures = sc.isPhantom()
                            || (Options.v().no_bodies_for_excluded()
                            && Scene.v().isExcluded(sc) && !Scene.v()
                            .getBasicClasses().contains(sc.getName()));
                    if (onlySignatures) {
                        bringToSignatures(sc);
                        sc.setPhantomClass();
                        for (SootMethod m : sc.getMethods()) {
                            m.setPhantom(true);
                        }
                        for (SootField f : sc.getFields()) {
                            f.setPhantom(true);
                        }
                    } else
                        bringToBodies(sc);
                } else { // No transitive
                    switch (i) {
                        case SootClass.BODIES:
                            bringToBodies(sc);
                            break;
                        case SootClass.SIGNATURES:
                            bringToSignatures(sc);
                            break;
                        case SootClass.HIERARCHY:
                            bringToHierarchy(sc);
                            break;
                    }
                }
            }
        }
    }

    private void addToResolveWorklist(Type type, int level) {
        // We go from Type -> SootClass directly, since RefType.getSootClass
        // calls makeClassRef anyway
        if (type instanceof RefType)
            addToResolveWorklist(((RefType) type).getSootClass(), level);
        else if (type instanceof ArrayType)
            addToResolveWorklist(((ArrayType) type).baseType, level);
        // Other types ignored
    }

    private void addToResolveWorklist(SootClass sc, int desiredLevel) {
        if (sc.resolvingLevel() >= desiredLevel)
            return;
        worklist[desiredLevel].add(sc);
    }

    /**
     * Hierarchy - we know the hierarchy of the class and that's it requires at
     * least Hierarchy for all supertypes and enclosing types.
     */
    private void bringToHierarchy(SootClass sc) {
        if (sc.resolvingLevel() >= SootClass.HIERARCHY)
            return;
        if (Options.v().debug_resolver())
            G.v().out.println("bringing to HIERARCHY: " + sc);
        sc.setResolvingLevel(SootClass.HIERARCHY);

        String className = sc.getName();
        ClassSource is = SourceLocator.v().getClassSource(className, Optional.fromNullable(sc.moduleName));
        try {
            boolean modelAsPhantomRef = is == null;
            // || (
            // Options.v().no_jrl() &&
            // Scene.v().isExcluded(sc) &&
            // !Scene.v().getBasicClasses().contains(sc.getName())
            // );
            if (modelAsPhantomRef) {
                if (!Scene.v().allowsPhantomRefs()) {
                    String suffix = "";
                    if (className.equals("java.lang.Object")) {
                        suffix = " Try adding rt.jar to Soot's classpath, e.g.:\n"
                                + "java -cp sootclasses.jar soot.Main -cp "
                                + ".:/path/to/jdk/jre/lib/rt.jar <other options>";
                    } else if (className.equals("javax.crypto.Cipher")) {
                        suffix = " Try adding jce.jar to Soot's classpath, e.g.:\n"
                                + "java -cp sootclasses.jar soot.Main -cp "
                                + ".:/path/to/jdk/jre/lib/rt.jar:/path/to/jdk/jre/lib/jce.jar <other options>";
                    }
                    throw new SootClassNotFoundException("couldn't find class: "
                            + className
                            + " (is your soot-class-path set properly?)" + suffix);
                } else {
                    G.v().out.println("Warning: " + className
                            + " is a phantom class!");
                    sc.setPhantomClass();
                    classToTypesSignature.put(sc, Collections.emptyList());
                    classToTypesHierarchy.put(sc, Collections.emptyList());
                }
            } else {
                Dependencies dependencies = is.resolve(sc);
                if (!dependencies.typesToSignature.isEmpty())
                    classToTypesSignature.put(sc, dependencies.typesToSignature);
                if (!dependencies.typesToHierarchy.isEmpty())
                    classToTypesHierarchy.put(sc, dependencies.typesToHierarchy);
            }
        } finally {
            if (is != null)
                is.close();
        }
        reResolveHierarchy(sc);
    }

    public void reResolveHierarchy(SootClass sc) {
        // Bring superclasses to hierarchy
        if (sc.hasSuperclass())
            addToResolveWorklist(sc.getSuperclass(), SootClass.HIERARCHY);
        if (sc.hasOuterClass())
            addToResolveWorklist(sc.getOuterClass(), SootClass.HIERARCHY);
        for (SootClass iface : sc.getInterfaces()) {
            addToResolveWorklist(iface, SootClass.HIERARCHY);
        }
    }

    /**
     * Signatures - we know the signatures of all methods and fields requires at
     * least Hierarchy for all referred to types in these signatures.
     */
    private void bringToSignatures(SootClass sc) {
        if (sc.resolvingLevel() >= SootClass.SIGNATURES)
            return;
        bringToHierarchy(sc);
        if (Options.v().debug_resolver())
            G.v().out.println("bringing to SIGNATURES: " + sc);
        sc.setResolvingLevel(SootClass.SIGNATURES);

        for (SootField f : sc.getFields()) {
            addToResolveWorklist(f.getType(), SootClass.HIERARCHY);
        }
        for (SootMethod m : sc.getMethods()) {
            addToResolveWorklist(m.getReturnType(), SootClass.HIERARCHY);
            for (Type ptype : m.getParameterTypes()) {
                addToResolveWorklist(ptype, SootClass.HIERARCHY);
            }
            for (SootClass exception : m.getExceptions()) {
                addToResolveWorklist(exception, SootClass.HIERARCHY);
            }
        }

        // Bring superclasses to signatures
        if (sc.hasSuperclass())
            addToResolveWorklist(sc.getSuperclass(), SootClass.SIGNATURES);
        for (SootClass iface : sc.getInterfaces()) {
            addToResolveWorklist(iface, SootClass.SIGNATURES);
        }
    }

    /**
     * Bodies - we can now start loading the bodies of methods for all referred
     * to methods and fields in the bodies, requires signatures for the method
     * receiver and field container, and hierarchy for all other classes
     * referenced in method references. Current implementation does not
     * distinguish between the receiver and other references. Therefore, it is
     * conservative and brings all of them to signatures. But this could/should
     * be improved.
     */
    private void bringToBodies(SootClass sc) {
        if (sc.resolvingLevel() >= SootClass.BODIES)
            return;
        bringToSignatures(sc);
        if (Options.v().debug_resolver())
            G.v().out.println("bringing to BODIES: " + sc);
        sc.setResolvingLevel(SootClass.BODIES);

        {
            Collection<Type> references = classToTypesHierarchy.get(sc);
            if (references != null) {
                // This must be an iterator, not a for-all since the underlying
                // collection may change as we go
                Iterator<Type> it = references.iterator();
                while (it.hasNext()) {
                    final Type t = it.next();
                    addToResolveWorklist(t, SootClass.HIERARCHY);
                }
            }
        }

        {
            Collection<Type> references = classToTypesSignature.get(sc);
            if (references != null) {
                // This must be an iterator, not a for-all since the underlying
                // collection may change as we go
                Iterator<Type> it = references.iterator();
                while (it.hasNext()) {
                    final Type t = it.next();
                    addToResolveWorklist(t, SootClass.SIGNATURES);
                }
            }
        }
    }

    public void reResolve(SootClass cl, int newResolvingLevel) {
        int resolvingLevel = cl.resolvingLevel();
        if (resolvingLevel >= newResolvingLevel)
            return;
        reResolveHierarchy(cl);
        cl.setResolvingLevel(newResolvingLevel);
        addToResolveWorklist(cl, resolvingLevel);
        processResolveWorklist();
    }

    public void reResolve(SootClass cl) {
        reResolve(cl, SootClass.HIERARCHY);
    }

    public Program getProgram() {
        return program;
    }

    private class SootClassNotFoundException extends RuntimeException {
        /**
         *
         */
        private static final long serialVersionUID = 1563461446590293827L;

        private SootClassNotFoundException(String s) {
            super(s);
        }
    }
}
