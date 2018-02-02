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

import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.iface.ClassDef;
import org.jf.dexlib2.iface.DexFile;
import soot.*;
import soot.javaToJimple.IInitialResolver.Dependencies;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * DexlibWrapper provides an entry point to the dexlib library from the smali
 * project. Given a dex file, it will use dexlib to retrieve all classes for
 * further processing A call to getClass retrieves the specific class to analyze
 * further.
 */
public class DexlibWrapper {

    private final static Set<String> systemAnnotationNames;

    static {
        Set<String> systemAnnotationNamesModifiable = new HashSet<String>();
        // names as defined in the ".dex - Dalvik Executable Format" document
        systemAnnotationNamesModifiable.add("dalvik.annotation.AnnotationDefault");
        systemAnnotationNamesModifiable.add("dalvik.annotation.EnclosingClass");
        systemAnnotationNamesModifiable.add("dalvik.annotation.EnclosingMethod");
        systemAnnotationNamesModifiable.add("dalvik.annotation.InnerClass");
        systemAnnotationNamesModifiable.add("dalvik.annotation.MemberClasses");
        systemAnnotationNamesModifiable.add("dalvik.annotation.Signature");
        systemAnnotationNamesModifiable.add("dalvik.annotation.Throws");
        systemAnnotationNames = Collections.unmodifiableSet(systemAnnotationNamesModifiable);
    }

    private final DexClassLoader dexLoader = new DexClassLoader();
    private final Map<String, ClassDef> classesToDefItems = new HashMap<String, ClassDef>();
    private final Collection<DexBackedDexFile> dexFiles;


    /**
     * Construct a DexlibWrapper from a dex file and stores its classes
     * referenced by their name. No further process is done here.
     */

    public DexlibWrapper(File dexSource) {
        try {
            List<DexFileProvider.DexContainer> containers = DexFileProvider.v().getDexFromSource(dexSource);
            this.dexFiles = new ArrayList<>(containers.size());
            for (DexFileProvider.DexContainer container : containers) {
                this.dexFiles.add(container.getBase());
            }
        } catch (IOException e) {
            throw new CompilationDeathException("IOException during dex parsing", e);
        }
    }

    public void initialize() {
        // resolve classes in dex files
        for (DexBackedDexFile dexFile : dexFiles) {
            for (ClassDef defItem : dexFile.getClasses()) {
                String forClassName = Util.dottedClassName(defItem.getType());
                classesToDefItems.put(forClassName, defItem);
            }
        }

        // It is important to first resolve the classes, otherwise we will produce an error during type resolution.
        for (DexBackedDexFile dexFile : dexFiles) {
            for (int i = 0; i < dexFile.getTypeCount(); i++) {
                String t = dexFile.getType(i);

                Type st = DexType.toSoot(t);
                if (st instanceof ArrayType) {
                    st = ((ArrayType) st).baseType;
                }
                String sootTypeName = st.toString();
                if (!Scene.v().containsClass(sootTypeName)) {
                    if (st instanceof PrimType || st instanceof VoidType
                            || systemAnnotationNames.contains(sootTypeName)) {
                        // dex files contain references to the Type IDs of void
                        // primitive types - we obviously do not want them
                        // to be resolved
                                /*
                                 * dex files contain references to the Type IDs of
                                 * the system annotations. They are only visible to
                                 * the Dalvik VM (for reflection, see
                                 * vm/reflect/Annotations.cpp), and not to the user
                                 * - so we do not want them to be resolved.
                                 */
                        continue;
                    }
                    SootResolver.v().makeClassRef(sootTypeName);
                }
                SootResolver.v().resolveClass(sootTypeName, SootClass.SIGNATURES);
            }
        }
    }


    public Dependencies makeSootClass(SootClass sc, String className) {
        if (Util.isByteCodeClassName(className)) {
            className = Util.dottedClassName(className);
        }

        for (DexFile dexFile : dexFiles) {
            ClassDef defItem = classesToDefItems.get(className);
            if (dexFile.getClasses().contains(defItem))
                return dexLoader.makeSootClass(sc, defItem, dexFile);
        }

        throw new RuntimeException("Error: class not found in DEX files: " + className);
    }
}
