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

import java.util.HashSet;
import java.util.Set;

import org.jf.dexlib2.iface.Annotation;
import org.jf.dexlib2.iface.ClassDef;
import org.jf.dexlib2.iface.Field;
import org.jf.dexlib2.iface.Method;
import org.jf.dexlib2.iface.value.BooleanEncodedValue;
import org.jf.dexlib2.iface.value.ByteEncodedValue;
import org.jf.dexlib2.iface.value.CharEncodedValue;
import org.jf.dexlib2.iface.value.DoubleEncodedValue;
import org.jf.dexlib2.iface.value.EncodedValue;
import org.jf.dexlib2.iface.value.FloatEncodedValue;
import org.jf.dexlib2.iface.value.IntEncodedValue;
import org.jf.dexlib2.iface.value.LongEncodedValue;
import org.jf.dexlib2.iface.value.ShortEncodedValue;
import org.jf.dexlib2.iface.value.StringEncodedValue;

import soot.Modifier;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import soot.tagkit.DoubleConstantValueTag;
import soot.tagkit.FloatConstantValueTag;
import soot.tagkit.IntegerConstantValueTag;
import soot.tagkit.LongConstantValueTag;
import soot.tagkit.StringConstantValueTag;
import soot.tagkit.Tag;

/**
 * DexClass is a container for all relevant information of that class
 * the name of the superclass, interfaces and its annotations, and modifier are stored, as well as all fields, methods, and types that are referenced throughout the class are available here.
 *
 */

public class DexClass {

    protected String name;
    protected String type;

    protected String superClassName;
    protected String[] interfaceNames;

    protected Set<? extends Annotation> annotations;
    protected Set<SootField> fields;
    protected Set<SootMethod> soot_methods;
    protected Set<Type> types;

    protected int accessFlags;

    /**
     * The constructor consumes a class definition item of dexlib and retrieves all subsequent methods, types and fields.
     * @param classDef
     */
    public DexClass(ClassDef classDef) {
        type = classDef.getType();
        this.name = classDef.getType();
        this.types = new HashSet<Type>();
        String superClass = classDef.getSuperclass();
		this.superClassName = classDef.getSuperclass();
		this.types.add(DexType.toSoot(superClass));
        this.accessFlags = classDef.getAccessFlags();

        // Retrieve interface names
        if (classDef.getInterfaces() == null)
            this.interfaceNames = new String[0];
        else {
            this.interfaceNames = new String[classDef.getInterfaces().size()];
            int i = 0;
            for (String interfaceName : classDef.getInterfaces()) {
                this.interfaceNames[i++] = interfaceName;
                this.types.add(DexType.toSoot(interfaceName));
            }
        }

        // retrieve methods, fields and annotations
        this.soot_methods = new HashSet<SootMethod>(); // size of 16 by default
        this.fields = new HashSet<SootField>(); // size of 16 by default
        this.annotations = classDef.getAnnotations();

        // get the fields of the class
        int fieldIndex = 0;
        for (Field sf : classDef.getStaticFields()) {
            DexField dexField = new DexField(sf, this);
            if (Modifier.isFinal(sf.getAccessFlags()))
                addConstantTag(classDef, dexField, sf);
            fieldIndex++;
            this.fields.add(dexField.toSoot());
        }
        for (Field f: classDef.getInstanceFields()) {
            DexField dexField = new DexField(f, this);
            fieldIndex++;
            this.fields.add(dexField.toSoot());
        }


        // get the methods of the class
        for (Method method : classDef.getDirectMethods()) {
            DexMethod dexMethod = new DexMethod(classDef.getSourceFile(), method, this);
            this.soot_methods.add(dexMethod.toSoot());
        }
        for (Method method : classDef.getVirtualMethods()) {
            DexMethod dexMethod = new DexMethod(classDef.getSourceFile(), method, this);
            this.soot_methods.add(dexMethod.toSoot());
        }

    }

    private void addConstantTag(ClassDef classDef, DexField df, Field sf) {
      Tag tag = null;

      EncodedValue ev = sf.getInitialValue();

      if (ev instanceof BooleanEncodedValue) {
        tag = new IntegerConstantValueTag(((BooleanEncodedValue) ev).getValue() ==true?1:0);
      } else if (ev instanceof ByteEncodedValue) {
        tag = new IntegerConstantValueTag(((ByteEncodedValue) ev).getValue());
      } else if (ev instanceof CharEncodedValue) {
        tag = new IntegerConstantValueTag(((CharEncodedValue) ev).getValue());
      } else if (ev instanceof DoubleEncodedValue) {
        tag = new DoubleConstantValueTag(((DoubleEncodedValue) ev).getValue());
      } else if (ev instanceof FloatEncodedValue) {
        tag = new FloatConstantValueTag(((FloatEncodedValue) ev).getValue());
      } else if (ev instanceof IntEncodedValue) {
        tag = new IntegerConstantValueTag(((IntEncodedValue) ev).getValue());
      } else if (ev instanceof LongEncodedValue) {
        tag = new LongConstantValueTag(((LongEncodedValue) ev).getValue());
      } else if (ev instanceof ShortEncodedValue) {
        tag = new IntegerConstantValueTag(((ShortEncodedValue) ev).getValue());
      } else if (ev instanceof StringEncodedValue) {
        tag = new StringConstantValueTag(((StringEncodedValue) ev).getValue());
      }

      if (tag != null)
        df.tags.add(tag);

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

    public String getType() {
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
    public Set<SootMethod> getDeclaredMethods() {
        return this.soot_methods;
    }
    /**
     *
     * @return all fields that are declared in this class
     */
    public Set<SootField> getDeclaredFields() {
        return this.fields;
    }

    public SootField getFieldByName(String fname) {
      SootField f = null;
      for (SootField df: getDeclaredFields()) {
        if (df.getName().equals(fname))
          return df;
      }
      throw new RuntimeException("error: no field named '"+ fname +"' in class '"+ this.name);
    }

    /**
     * Return all types that are referenced in this class.
     *
     * This includes Types of local variables as well.
     */
    public Set<Type> getAllTypes() {
        return this.types;
    }


}
