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
    public SootResolver (Singletons.Global g) {}

    public static SootResolver v() { return G.v().soot_SootResolver();}
    
    /** Returns true if we are resolving all class refs recursively. */
    private boolean resolveEverything() {
        return( Options.v().whole_program() || Options.v().full_resolver() );
    }

    /** Maps each resolved class to a list of all references in it. */
    private Map classToReferences = new HashMap();
    
    /** Set of classes that have been resolved. */
    private Set resolvedClasses = new HashSet();

    /** SootClasses waiting to be resolved. */
    private LinkedList/*SootClass*/ toResolveWorklist = new LinkedList();

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
        Scene.v().addClass(newClass);

        return newClass;
    }


    /**
     * Resolves the given class. Depending on the resolver settings, may
     * decide to resolve other classes as well. If the class has already
     * been resolved, just returns the class that was already resolved.
     * */
    public SootClass resolveClass(String className) {
        SootClass resolvedClass = makeClassRef(className);
        addToResolveWorklist(resolvedClass);
        processResolveWorklist();
        return resolvedClass;
    }

    /**
     * Resolves the given class and any classes referenced by it.
     * Depending on the resolver settings, may decide to resolve other
     * classes as well. If the class has already been resolved, just
     * returns the class that was already resolved.
     * */
    public SootClass resolveClassAndSupportClasses(String className) {
        SootClass resolvedClass = resolveClass(className);
        addReferencesOfClass(resolvedClass);
        processResolveWorklist();
        return resolvedClass;
    }

    /** Resolve all classes on toResolveWorklist. */
    private void processResolveWorklist() {
        while( !toResolveWorklist.isEmpty() ) {
            SootClass sc = (SootClass) toResolveWorklist.removeFirst();
            if( resolvedClasses.contains(sc) ) continue;
            if(Options.v().debug_resolver()) System.out.println("resolving "+sc);
            resolvedClasses.add(sc);
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

            if( resolveEverything() ) {
                addReferencesOfClass(sc);
            }

            // We always resolve the superclass and the outer class
            if(sc.hasSuperclass()) addToResolveWorklist(sc.getSuperclass());
            if(sc.hasOuterClass()) addToResolveWorklist(sc.getOuterClass());
            for( Iterator ifaceIt = sc.getInterfaces().iterator(); ifaceIt.hasNext(); ) {
                final SootClass iface = (SootClass) ifaceIt.next();
                addToResolveWorklist(iface);
            }
        }
    }

    private void addReferencesOfClass(SootClass sc) {
        Collection references = (Collection) classToReferences.get(sc);
        if(Options.v().debug_resolver()) System.out.println("resolving refs of "+sc);
        if( references == null ) return;

        Iterator it = references.iterator();
        while( it.hasNext() ) {
            final Object o = it.next();

            if( o instanceof String ) {
                addToResolveWorklist((String) o);
            } else if( o instanceof Type ) {
                addToResolveWorklist((Type) o);
            } else throw new RuntimeException(o.toString());
        }
    }
       
    private void addToResolveWorklist(Type type) {
        if( type instanceof RefType )
            addToResolveWorklist(((RefType) type).getClassName());
        else if( type instanceof ArrayType )
            addToResolveWorklist(((ArrayType) type).baseType);
    }
    private void addToResolveWorklist(String className) {
        addToResolveWorklist(makeClassRef(className));
    }
    private void addToResolveWorklist(SootClass sc) {
        if( !resolvedClasses.contains(sc) ) toResolveWorklist.add(sc);
    }

    /** Returns the list of SootClasses that have been resolved. */
    public List/*SootClass*/ resolvedClasses() {
        return Collections.unmodifiableList(new ArrayList(resolvedClasses));
    }
}


