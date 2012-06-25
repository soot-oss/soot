/* Soot - a Java Optimization Framework
 * Copyright (C) 2012 Michael Markert, Frank Hartmann
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

package soot.dex;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.jf.dexlib.DexFile;
import org.jf.dexlib.ClassDefItem;
import org.jf.dexlib.StringIdItem;
import org.jf.dexlib.TypeIdItem;

import soot.Scene;
import soot.Type;
import soot.SootClass;
import soot.SootResolver;


/**
 * DexlibWrapper provides an entry point to the dexlib library from the smali project.
 * Given a dex file, it will use dexlib to retrieve all classes for further processing
 * A call to getClass retrieves the specific class to analyze further.
 *
 */
public class DexlibWrapper {

    private DexFile dexFile;

    private Map<String, ClassDefItem> dexClasses;

    private Map<String, DexClass> classesByName;

    /**
     * Construct a DexlibWrapper from a dex file and stores its classes referenced by their name.
     * No further process is done here.
     *
     * @param inputDexFileName the dex file.
     */

    public DexlibWrapper(File inputDexFile) {

        this.dexClasses = new HashMap<String, ClassDefItem>();
        this.classesByName = new HashMap<String, DexClass>();

        try {
            this.dexFile = new DexFile(inputDexFile);
        } catch (Exception e) {
            throw new RuntimeException(e.toString());
        }

        for (ClassDefItem defItem : this.dexFile.ClassDefsSection.getItems()) {
            this.dexClasses.put(Util.dottedClassName(defItem.getClassType().getTypeDescriptor()), defItem);
        }

				for (TypeIdItem t: this.dexFile.TypeIdsSection.getItems()) {
					DexType dt = new DexType (t);
					Type st = dt.toSoot();
					System.out.println("Type: "+ t +" soot type:"+ st);
					if (!Scene.v().containsClass(st.toString())) {
						SootResolver.v().makeClassRef(st.toString());
					}
				}
				for (StringIdItem i: this.dexFile.StringIdsSection.getItems()) {
				  System.out.println("String: "+ i);
				}

    }

    /**
     * Returns and processes the dex class by its fully classified name.
     * All subsequent elements of that class are retrieved.
     *
     * @param className the class name, can be either in dotted (package.Class)
     *                  or in byte code (Lpackage/Class;) format
     * @return the dex class with this name
     */
    public DexClass getClass(String className) {
        if (Util.isByteCodeClassName(className)) {
            className = Util.dottedClassName(className);
        }
        DexClass dexClass = this.classesByName.get(className);

        if (dexClass != null)
            return dexClass;

        // try to get class from dex file
        ClassDefItem defItem = dexClasses.get(className);

        if (defItem != null) {
            DexClass newDexClass = new DexClass(defItem);
            this.classesByName.put(className, newDexClass);
            return newDexClass;
        }
        return null;
    }

    /**
     * Get a map that contains the class name and the class definition item of dexlib
     * @return Map<String, ClassDefItem>
     */
    public Map<String, ClassDefItem> getDexClasses() {
        return dexClasses;
    }

}
