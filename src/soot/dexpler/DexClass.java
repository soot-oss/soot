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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.jf.dexlib.AnnotationDirectoryItem;
import org.jf.dexlib.ClassDataItem;
import org.jf.dexlib.ClassDataItem.EncodedField;
import org.jf.dexlib.ClassDefItem;
import org.jf.dexlib.ClassDefItem.StaticFieldInitializer;
import org.jf.dexlib.EncodedArrayItem;
import org.jf.dexlib.TypeIdItem;
import org.jf.dexlib.Code.Analysis.ClassPath.ClassDef;
import org.jf.dexlib.EncodedValue.BooleanEncodedValue;
import org.jf.dexlib.EncodedValue.ByteEncodedValue;
import org.jf.dexlib.EncodedValue.CharEncodedValue;
import org.jf.dexlib.EncodedValue.DoubleEncodedValue;
import org.jf.dexlib.EncodedValue.EncodedValue;
import org.jf.dexlib.EncodedValue.EnumEncodedValue;
import org.jf.dexlib.EncodedValue.FloatEncodedValue;
import org.jf.dexlib.EncodedValue.IntEncodedValue;
import org.jf.dexlib.EncodedValue.LongEncodedValue;
import org.jf.dexlib.EncodedValue.NullEncodedValue;
import org.jf.dexlib.EncodedValue.ShortEncodedValue;
import org.jf.dexlib.EncodedValue.StringEncodedValue;

import soot.BooleanType;
import soot.ByteType;
import soot.CharType;
import soot.G;
import soot.IntType;
import soot.Modifier;
import soot.Type;
import soot.jimple.NullConstant;
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
        this.types = new HashSet<DexType>();
        TypeIdItem superClass = classDef.getSuperclass();
		this.superClassName = superClass.getTypeDescriptor();
		this.types.add(new DexType(superClass));
        this.accessFlags = classDef.getAccessFlags();

        // Retrieve interface names
        if (classDef.getInterfaces() == null)
            this.interfaceNames = new String[0];
        else {
            this.interfaceNames = new String[classDef.getInterfaces().getTypes().size()];
            int i = 0;
            for (TypeIdItem interfaceName : classDef.getInterfaces().getTypes()) {
                this.interfaceNames[i++] = interfaceName.getTypeDescriptor();
                this.types.add(new DexType(interfaceName));
            }
        }

        // retrieve methods, fields and annotations
        ClassDataItem classData = classDef.getClassData();
        if (classData == null) {
            this.methods = Collections.emptySet();
            this.fields = Collections.emptySet();
        } else {
            int numMethods = classData.getDirectMethods().length + classData.getVirtualMethods().length;
            int numFields = classData.getInstanceFields().length + classData.getStaticFields().length;

            this.methods = new HashSet<DexMethod>(numMethods);
            this.fields = new HashSet<DexField>(numFields);
            this.annotations = classDef.getAnnotations();

            // get the fields of the class
            ClassDataItem.EncodedField[] fields = Util.concat(classData.getInstanceFields(), classData.getStaticFields());
            int fieldIndex = 0;
            for (ClassDataItem.EncodedField field : fields) {
                DexField dexField = new DexField(field, this);
                if (field.isStatic()) {
                  if (Modifier.isFinal(field.accessFlags))
                    addConstantTag(classDef, dexField, fieldIndex);
                  fieldIndex++;
                }
                this.fields.add(dexField);
               
            }
            
         
            ClassDataItem.EncodedMethod[] methods = Util.concat(classData.getDirectMethods(), classData.getVirtualMethods());
            // get the methods of the class
            for (ClassDataItem.EncodedMethod method : methods) {
                DexMethod dexMethod;
//                try {
                  dexMethod = new DexMethod(classDef.getDexFile(), method, this);
                  this.methods.add(dexMethod);
//                } catch (Exception e) {
//                  e.printStackTrace();
//                  G.v().out.println("Warning: method '"+ this.name +"."+ method.method.getMethodString() +"generated an Exception!");
//                }
            }
        }
    }
    
    private void addConstantTag(ClassDefItem classDef, DexField df, int fieldIndex) {
      Tag tag = null;
      
      EncodedArrayItem fieldInitsArray = classDef.getStaticFieldInitializers();
      if (null == fieldInitsArray)
        return;
      final EncodedValue[]  fieldInits = fieldInitsArray.getEncodedArray().values;
      
      if (fieldInits.length < fieldIndex + 1) { // put default value 0/null
        Type t = df.fieldType.toSoot();
        if (t instanceof IntType || t instanceof CharType || t instanceof ByteType || t instanceof BooleanType) {
          tag = new IntegerConstantValueTag(0);
        } else if (t.toString().equals("java.lang.String")) {
          G.v().out.println("warning: final static String initialized to null!");
        }
        
        if (tag != null)
          df.tags.add(tag);
        return;
      }

      EncodedValue ev = fieldInits[fieldIndex];
      
      if (ev instanceof BooleanEncodedValue) {
        tag = new IntegerConstantValueTag(((BooleanEncodedValue) ev).value==true?1:0);
      } else if (ev instanceof ByteEncodedValue) {
        tag = new IntegerConstantValueTag(((ByteEncodedValue) ev).value);
      } else if (ev instanceof CharEncodedValue) {
        tag = new IntegerConstantValueTag(((CharEncodedValue) ev).value);
      } else if (ev instanceof DoubleEncodedValue) {
        tag = new DoubleConstantValueTag(((DoubleEncodedValue) ev).value);
      } else if (ev instanceof FloatEncodedValue) {
        tag = new FloatConstantValueTag(((FloatEncodedValue) ev).value);
      } else if (ev instanceof IntEncodedValue) {
        tag = new IntegerConstantValueTag(((IntEncodedValue) ev).value);
      } else if (ev instanceof LongEncodedValue) {
        tag = new LongConstantValueTag(((LongEncodedValue) ev).value);
      } else if (ev instanceof ShortEncodedValue) {
        tag = new IntegerConstantValueTag(((ShortEncodedValue) ev).value);
      } else if (ev instanceof StringEncodedValue) {
        tag = new StringConstantValueTag(((StringEncodedValue) ev).value.getStringValue());
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
    
    public DexField getFieldByName(String fname) {
      DexField f = null;
      for (DexField df: getDeclaredFields()) {
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
    public Set<DexType> getAllTypes() {
        return this.types;
    }


}
