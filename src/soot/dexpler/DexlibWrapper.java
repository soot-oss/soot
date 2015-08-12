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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.jf.dexlib2.DexFileFactory;
import org.jf.dexlib2.dexbacked.DexBackedDexFile;
import org.jf.dexlib2.iface.ClassDef;
import org.jf.dexlib2.iface.DexFile;

import soot.ArrayType;
import soot.PrimType;
import soot.Scene;
import soot.SootClass;
import soot.SootResolver;
import soot.Type;
import soot.VoidType;
import soot.javaToJimple.IInitialResolver.Dependencies;


/**
 * DexlibWrapper provides an entry point to the dexlib library from the smali project.
 * Given a dex file, it will use dexlib to retrieve all classes for further processing
 * A call to getClass retrieves the specific class to analyze further.
 *
 */
public class DexlibWrapper {

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

    private DexFile dexFile;

    //private Map<String, ClassDef> dexClasses;

    //private Map<String, DexClass> classesByName;

    private final static Set<String> systemAnnotationNames;

	private final File inputDexFile;

    /**
     * Construct a DexlibWrapper from a dex file and stores its classes referenced by their name.
     * No further process is done here.
     *
     * @param inputDexFileName the dex file.
     */

    public DexlibWrapper(File inputDexFile) {
        this.inputDexFile = inputDexFile;
    }

    public void initialize() {

        try {
            int api = 1; // TODO:
            this.dexFile = DexFileFactory.loadDexFile(inputDexFile, api, false);
        } catch (Exception e) {
            throw new RuntimeException(e.toString());
        }

        if (dexFile instanceof DexBackedDexFile) {
            DexBackedDexFile dbdf = (DexBackedDexFile)dexFile;
            for (int i = 0; i < dbdf.getTypeCount(); i++) {
            	String t = dbdf.getType(i);

            	Type st = DexType.toSoot(t);
            	if (st instanceof ArrayType) {
            		st = ((ArrayType) st).baseType;
            	}
            	Debug.printDbg("Type: ", t ," soot type:", st);
            	String sootTypeName = st.toString();
            	if (!Scene.v().containsClass(sootTypeName)) {
            		if (st instanceof PrimType || st instanceof VoidType || systemAnnotationNames.contains(sootTypeName)) {
            			// dex files contain references to the Type IDs of void / primitive types - we obviously do not want them to be resolved
            			/*
            			 * dex files contain references to the Type IDs of the system annotations.
            			 * They are only visible to the Dalvik VM (for reflection, see vm/reflect/Annotations.cpp), and not to
            			 * the user - so we do not want them to be resolved.
            			 */
            			continue;
            		}
            		SootResolver.v().makeClassRef(sootTypeName);
            	}
            	SootResolver.v().resolveClass(sootTypeName, SootClass.SIGNATURES);
            }
        } else {
            System.out.println("Warning: DexFile not instance of DexBackedDexFile! Not resolving types!");
            System.out.println("type: "+ dexFile.getClass());
        }
    }
    
    public Dependencies makeSootClass(SootClass sc, String className) {
        if (Util.isByteCodeClassName(className)) {
            className = Util.dottedClassName(className);
        }

      for (ClassDef defItem : this.dexFile.getClasses()) {
          String forClassName = Util.dottedClassName(defItem.getType());
          if (className.equals(forClassName)) {
              return DexClass.makeSootClass(sc, defItem, dexFile);
          }
      }
      throw new RuntimeException("Error: class not found in classes.dex: "+ className);
    }

}
