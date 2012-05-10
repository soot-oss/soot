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

import java.util.Set;
import java.util.HashSet;

import soot.dex.Util;

import org.jf.dexlib.AnnotationDirectoryItem;
import org.jf.dexlib.ClassDataItem;
import org.jf.dexlib.ClassDefItem;
import org.jf.dexlib.TypeIdItem;

/**
 * DexClass is a container for all relevant information of that class
 * the name of the superclass, interfaces and its annotations, and modifier are stored, as well as all fields, methods, and types that are referenced throughout the class are available here.
 *
 */

public class DexClass {

    protected String name;
    protected TypeIdItem type;

    protected String superClassName;
    protected String[] interfaceNames;

    protected AnnotationDirectoryItem annotations;
    protected Set<DexField> fields;
    protected Set<DexMethod> methods;
    protected Set<DexType> types;

    protected int accessFlags;

    /**
     * The constructor consumes a class definition item of dexlib and retrieves all subsequent methods, types and fields.
     * @param classDef
     */
    public DexClass(ClassDefItem classDef) {
    	type = classDef.getClassType();
        this.name = classDef.getClassType().getTypeDescriptor();
        this.superClassName = classDef.getSuperclass().getTypeDescriptor();
        this.accessFlags = classDef.getAccessFlags();

        // Retrieve interface names
        if (classDef.getInterfaces() == null)
            this.interfaceNames = new String[0];
        else {
            this.interfaceNames = new String[classDef.getInterfaces().getTypes().size()];
            int i = 0;
            for (TypeIdItem interfaceName : classDef.getInterfaces().getTypes()) {
                this.interfaceNames[i++] = interfaceName.getTypeDescriptor();
            }
        }

        // retrieve methods, fields and annotations
        ClassDataItem classData = classDef.getClassData();
        if (classData == null) {
            this.methods = new HashSet<DexMethod>(0);
            this.fields = new HashSet<DexField>(0);
        } else {
            int numMethods = classData.getDirectMethods().length + classData.getVirtualMethods().length;
            int numFields = classData.getInstanceFields().length + classData.getStaticFields().length;

            this.methods = new HashSet<DexMethod>(numMethods);
            this.fields = new HashSet<DexField>(numFields);
            // types are filled within the DexFields but are also available here
            this.types = new HashSet<DexType>();
            this.annotations = classDef.getAnnotations();

            // get the fields of the class
            ClassDataItem.EncodedField[] fields = Util.concat(classData.getInstanceFields(), classData.getStaticFields());
            for (ClassDataItem.EncodedField field : fields) {
                DexField dexField = new DexField(field, this);
                this.fields.add(dexField);
            }
            ClassDataItem.EncodedMethod[] methods = Util.concat(classData.getDirectMethods(), classData.getVirtualMethods());
            // get the methods of the class
            for (ClassDataItem.EncodedMethod method : methods) {
                DexMethod dexMethod = new DexMethod(method, this);
                this.methods.add(dexMethod);
            }
        }
    }
    /**
     *
     * @return modifiers of the class
     */
    public int getModifiers() {
        return this.accessFlags;
    }

    /**
     *
     * @return the name of the class
     */
    public String getName() {
        return this.name;
    }

    public TypeIdItem getType() {
        return type;
    }

    /**
     *
     * @return the name of the super class
     */
    public String getSuperclass() {
        return superClassName;
    }

    /**
     *
     * @return an array of all implemented interfaces
     */
    public String[] getInterfaces() {
        return this.interfaceNames;
    }

    /**
     *
     * @return all methods that are declared in this class
     */
    public Set<DexMethod> getDeclaredMethods() {
        return this.methods;
    }
    /**
     *
     * @return all fields that are declared in this class
     */
    public Set<DexField> getDeclaredFields() {
        return this.fields;
    }

    /**
     * Return all types that are referenced in this class.
     *
     * This includes Types of local variables as well.
     */
    public Set<DexType> getAllTypes() {
        return this.types;
    }


}
