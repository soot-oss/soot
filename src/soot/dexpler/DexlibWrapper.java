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

import lanchon.multidexlib2.BasicDexFileNamer;
import lanchon.multidexlib2.DexIO;
import lanchon.multidexlib2.MultiDexIO;
import lanchon.multidexlib2.WrappingMultiDexFile;
import org.jf.dexlib2.Opcodes;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.iface.ClassDef;
import org.jf.dexlib2.iface.DexFile;
import org.jf.dexlib2.iface.MultiDexContainer;
import soot.*;
import soot.javaToJimple.IInitialResolver.Dependencies;
import soot.options.Options;

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
    private final File inputDexFile;
    private MultiDexContainer<WrappingMultiDexFile<DexBackedDexFile>> dexContainer;

    /**
     * Construct a DexlibWrapper from a dex file and stores its classes
     * referenced by their name. No further process is done here.
     *
     * @param inputDexFile the dex file or an apk/zip/jar with multiple dex files.
     */

    public DexlibWrapper(File inputDexFile) {
        this.inputDexFile = inputDexFile;
    }

    public void initialize() {
        try {
            int api = Scene.v().getAndroidAPIVersion();
            boolean multiDex = Options.v().process_multiple_dex();
            DexIO.Logger logger = new DexIO.Logger() {
                @Override
                public void log(File file, String entryName, int typeCount) {
                    G.v().out.println(String.format("Found dex file '%s' with %d classes in '%s'", entryName, typeCount, file.getName()));
                }
            };
            dexContainer = MultiDexIO.readMultiDexContainer(inputDexFile, new BasicDexFileNamer(), Opcodes.forApi(api), logger);

            List<String> dexEntries = dexContainer.getDexEntryNames();
            int num_dex_files = dexEntries.size();

            if (!multiDex && num_dex_files > 1) {
                G.v().out.println("WARNING: Multiple dex files detected, only processing first dex file (" + dexEntries.get(0) + "). Use '-process-multiple-dex' option to process them all.");
                // restrict processed dex files to the first
                num_dex_files = 1;
            }

            for (int dexIndex = 0; dexIndex < num_dex_files; dexIndex++) {
                DexFile dexFile = dexContainer.getEntry(dexEntries.get(dexIndex));
                for (ClassDef defItem : dexFile.getClasses()) {
                    String forClassName = Util.dottedClassName(defItem.getType());
                    classesToDefItems.put(forClassName, defItem);
                }
            }

            // It is important to first resolve the classes, otherwise we will produce an error during type resolution.
            for (int dexIndex = 0; dexIndex < num_dex_files; dexIndex++) {
                DexBackedDexFile dexFile = dexContainer.getEntry(dexContainer.getDexEntryNames().get(dexIndex)).getWrappedDexFile();

                for (int i = 0; i < dexFile.getTypeCount(); i++) {
                    String t = dexFile.getType(i);

                    Type st = DexType.toSoot(t);
                    if (st instanceof ArrayType) {
                        st = ((ArrayType) st).baseType;
                    }
                    Debug.printDbg("Type: ", t, " soot type:", st);
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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public Dependencies makeSootClass(SootClass sc, String className) {
        if (Util.isByteCodeClassName(className)) {
            className = Util.dottedClassName(className);
        }

        try {
            for (String dexEntry : dexContainer.getDexEntryNames()) {
                DexFile dexFile = dexContainer.getEntry(dexEntry);
                ClassDef defItem = classesToDefItems.get(className);
                if (dexFile.getClasses().contains(defItem))
                    return dexLoader.makeSootClass(sc, defItem, dexFile);
            }
        } catch (IOException e) {
            // happens when dex file is not available
            throw new RuntimeException(e);
        }

        throw new RuntimeException("Error: class not found in DEX files: " + className);
    }

}
