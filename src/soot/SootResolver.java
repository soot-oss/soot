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
import soot.*;
import soot.options.*;

import soot.coffi.*;
import java.util.*;
import java.io.*;
import soot.util.*;
import soot.jimple.*;
import soot.javaToJimple.*;

/** Loads symbols for SootClasses from either class files or jimple files. */
public class SootResolver 
{
    /** Maps each resolved class to a list of all references in it. */
    private Map classToReferences = new HashMap();
    
    /** SootClasses waiting to be resolved. */
    private LinkedList/*SootClass*/[] worklist = new LinkedList[4];

    public SootResolver (Singletons.Global g) {
        worklist[SootClass.HIERARCHY] = new LinkedList();
        worklist[SootClass.SIGNATURES] = new LinkedList();
        worklist[SootClass.BODIES] = new LinkedList();
    }

    public static SootResolver v() { return G.v().soot_SootResolver();}
    
    /** Returns true if we are resolving all class refs recursively. */
    private boolean resolveEverything() {
        return( Options.v().whole_program() || Options.v().whole_shimple()
	|| Options.v().full_resolver() 
	|| Options.v().output_format() == Options.v().output_format_dava );
    }

    /** Returns a (possibly not yet resolved) SootClass to be used in references
     * to a class. If/when the class is resolved, it will be resolved into this
     * SootClass.
     * */
    public SootClass makeClassRef(String className)
    {
        if(Scene.v().containsClass(className))
            return Scene.v().getSootClass(className);

        SootClass newClass;
        newClass = new SootClass(className);
        newClass.setResolvingLevel(SootClass.DANGLING);
        Scene.v().addClass(newClass);

        return newClass;
    }


    /**
     * Resolves the given class. Depending on the resolver settings, may
     * decide to resolve other classes as well. If the class has already
     * been resolved, just returns the class that was already resolved.
     * */
    public SootClass resolveClass(String className, int desiredLevel) {
        SootClass resolvedClass = makeClassRef(className);
        addToResolveWorklist(resolvedClass, desiredLevel);
        processResolveWorklist();
        return resolvedClass;
    }

    /** Resolve all classes on toResolveWorklist. */
    private void processResolveWorklist() {
        for( int i = SootClass.BODIES; i >= SootClass.HIERARCHY; i-- ) {
            while( !worklist[i].isEmpty() ) {
                SootClass sc = (SootClass) worklist[i].removeFirst();
                if( resolveEverything() ) {
                    if( sc.isPhantom() ) bringToSignatures(sc);
                    else bringToBodies(sc);
                } else {
                    switch(i) {
                        case SootClass.BODIES: bringToBodies(sc); break;
                        case SootClass.SIGNATURES: bringToSignatures(sc); break;
                        case SootClass.HIERARCHY: bringToHierarchy(sc); break;
                    }
                }
            }
        }
    }

    private void addToResolveWorklist(Type type, int level) {
        if( type instanceof RefType )
            addToResolveWorklist(((RefType) type).getClassName(), level);
        else if( type instanceof ArrayType )
            addToResolveWorklist(((ArrayType) type).baseType, level);
    }
    private void addToResolveWorklist(String className, int level) {
        addToResolveWorklist(makeClassRef(className), level);
    }
    private void addToResolveWorklist(SootClass sc, int desiredLevel) {
        if( sc.resolvingLevel() >= desiredLevel ) return;
        worklist[desiredLevel].add(sc);
    }

    /** Hierarchy - we know the hierarchy of the class and that's it
     * requires at least Hierarchy for all supertypes and enclosing types.
     * */
    private void bringToHierarchy(SootClass sc) {
        if(sc.resolvingLevel() >= SootClass.HIERARCHY ) return;
        if(Options.v().debug_resolver())
            G.v().out.println("bringing to HIERARCHY: "+sc);
        sc.setResolvingLevel(SootClass.HIERARCHY);

        String className = sc.getName();
        ClassSource is = SourceLocator.v().getClassSource(className);
        if( is == null ) {
            if(!Scene.v().allowsPhantomRefs()) {
                throw new RuntimeException("couldn't find class: " +
                    className + " (is your soot-class-path set properly?)");
            } else {
                G.v().out.println(
                        "Warning: " + className + " is a phantom class!");
                sc.setPhantomClass();
                classToReferences.put( sc, new ArrayList() );
            }
        } else {
            Collection references = is.resolve(sc);
            classToReferences.put( sc, new ArrayList(new HashSet(references)) );
        }
        reResolveHierarchy(sc);
    }

    public void reResolveHierarchy(SootClass sc) {
        // Bring superclasses to hierarchy
        if(sc.hasSuperclass()) 
            addToResolveWorklist(sc.getSuperclass(), SootClass.HIERARCHY);
        if(sc.hasOuterClass()) 
            addToResolveWorklist(sc.getOuterClass(), SootClass.HIERARCHY);
        for( Iterator ifaceIt = sc.getInterfaces().iterator(); ifaceIt.hasNext(); ) {
            final SootClass iface = (SootClass) ifaceIt.next();
            addToResolveWorklist(iface, SootClass.HIERARCHY);
        }

    }

    /** Signatures - we know the signatures of all methods and fields
    * requires at least Hierarchy for all referred to types in these signatures.
    * */
    private void bringToSignatures(SootClass sc) {
        if(sc.resolvingLevel() >= SootClass.SIGNATURES ) return;
        bringToHierarchy(sc);
        if(Options.v().debug_resolver()) 
            G.v().out.println("bringing to SIGNATURES: "+sc);
        sc.setResolvingLevel(SootClass.SIGNATURES);

        for( Iterator fIt = sc.getFields().iterator(); fIt.hasNext(); ) {

            final SootField f = (SootField) fIt.next();
            addToResolveWorklist( f.getType(), SootClass.HIERARCHY );
        }
        for( Iterator mIt = sc.getMethods().iterator(); mIt.hasNext(); ) {
            final SootMethod m = (SootMethod) mIt.next();
            addToResolveWorklist( m.getReturnType(), SootClass.HIERARCHY );
            for( Iterator ptypeIt = m.getParameterTypes().iterator(); ptypeIt.hasNext(); ) {
                final Type ptype = (Type) ptypeIt.next();
                addToResolveWorklist( ptype, SootClass.HIERARCHY );
            }
            for( Iterator exceptionIt = m.getExceptions().iterator(); exceptionIt.hasNext(); ) {
                final SootClass exception = (SootClass) exceptionIt.next();
                addToResolveWorklist( exception, SootClass.HIERARCHY );
            }
        }

        // Bring superclasses to signatures
        if(sc.hasSuperclass()) 
            addToResolveWorklist(sc.getSuperclass(), SootClass.SIGNATURES);
        for( Iterator ifaceIt = sc.getInterfaces().iterator(); ifaceIt.hasNext(); ) {
            final SootClass iface = (SootClass) ifaceIt.next();
            addToResolveWorklist(iface, SootClass.SIGNATURES);
        }
    }

    /** Bodies - we can now start loading the bodies of methods
    * for all referred to methods and fields in the bodies, requires
    * signatures for the method receiver and field container, and
    * hierarchy for all other classes referenced in method references.
    * Current implementation does not distinguish between the receiver
    * and other references. Therefore, it is conservative and brings all
    * of them to signatures. But this could/should be improved.
    * */
    private void bringToBodies(SootClass sc) {
        if(sc.resolvingLevel() >= SootClass.BODIES ) return;
        bringToSignatures(sc);
        if(Options.v().debug_resolver()) 
            G.v().out.println("bringing to BODIES: "+sc);
        sc.setResolvingLevel(SootClass.BODIES);

        Collection references = (Collection) classToReferences.get(sc);
        if( references == null ) return;

        Iterator it = references.iterator();
        while( it.hasNext() ) {
            final Object o = it.next();

            if( o instanceof String ) {
                addToResolveWorklist((String) o, SootClass.SIGNATURES);
            } else if( o instanceof Type ) {
                addToResolveWorklist((Type) o, SootClass.SIGNATURES);
            } else throw new RuntimeException(o.toString());
        }
    }

    public void reResolve(SootClass cl) {
        int resolvingLevel = cl.resolvingLevel();
        if( resolvingLevel < SootClass.HIERARCHY ) return;
        reResolveHierarchy(cl);
        cl.setResolvingLevel(SootClass.HIERARCHY);
        addToResolveWorklist(cl, resolvingLevel);
        processResolveWorklist();
    }
}


