/* Soot - a Java Optimization Framework
 * Copyright (C) 2012 Michael Markert, Frank Hartmann
 * 
 * (c) 2012 University of Luxembourg - Interdisciplinary Centre for
 * Security Reliability and Trust (SnT) - All rights reserved
 * Alexandre Bartel
 * 
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

package soot.dexpler;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

import soot.G;
import soot.Singletons;
import soot.SootClass;
import soot.SootResolver;
import soot.javaToJimple.IInitialResolver.Dependencies;

public class DexResolver {
	
	private Map<File,DexlibWrapper> cache = new TreeMap<File, DexlibWrapper>();
	
    public DexResolver(Singletons.Global g) {}

    public static DexResolver v() { return G.v().soot_dexpler_DexResolver(); }
	
    /**
     * Resolve the class contained in file into the passed soot class.
     *
     * @param file the path to the dex/apk file to resolve
     * @param className the name of the class to resolve
     * @param sc the soot class that will represent the class
     * @return the dependencies of this class.
     */
    public Dependencies resolveFromFile(File file, String className, SootClass sc) {
    	DexlibWrapper wrapper = cache.get(file);
    	if(wrapper==null) {
    		wrapper = new DexlibWrapper(file);
    		cache.put(file, wrapper);
    		wrapper.initialize();
    	}
        DexClass c = wrapper.getClass(className);
        if (c == null)
            throw new RuntimeException("Class " + className + " not found at " + file.getPath());

        sc.setModifiers(c.getModifiers());
        Dependencies deps = new Dependencies();
        // interfaces for hierarchy level
        for (String interfaceName : c.getInterfaces()) {
            String interfaceClassName = Util.dottedClassName(interfaceName);
            SootClass interfaceClass = SootResolver.v().makeClassRef(interfaceClassName);
            sc.addInterface(interfaceClass);
            deps.typesToHierarchy.add(interfaceClass.getType());
        }
        // super class for hierarchy level
        String superClassName = Util.dottedClassName(c.getSuperclass());
        SootClass superClass = SootResolver.v().makeClassRef(superClassName);
        sc.setSuperclass(superClass);
        deps.typesToHierarchy.add(superClass.getType());

        // all types for signature level
        for (DexType t : c.getAllTypes()) {
            deps.typesToSignature.add(t.toSoot());
        }

        // fields
        for (DexField f : c.getDeclaredFields()) {
            sc.addField(f.toSoot());
        }

        // methods
        for (DexMethod m : c.getDeclaredMethods()) {
            sc.addMethod(m.toSoot());
        }

        addSourceFileTag(sc, file.getAbsolutePath());
        return deps;
    }

    /**
     *  adds source file tag to each sootclass
     */
    protected static void addSourceFileTag(SootClass sc, String fileName){
        soot.tagkit.SourceFileTag tag = null;
        if (sc.hasTag("SourceFileTag")) {
            tag = (soot.tagkit.SourceFileTag)sc.getTag("SourceFileTag");
        }
        else {
            tag = new soot.tagkit.SourceFileTag();
            sc.addTag(tag);
        }
        tag.setSourceFile(fileName); 
    }
}
