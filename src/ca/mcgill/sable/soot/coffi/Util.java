/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Jimple, a 3-address code Java(TM) bytecode representation.        *
 * Copyright (C) 1997, 1998 Raja Vallee-Rai (kor@sable.mcgill.ca)    *
 * All rights reserved.                                              *
 *                                                                   *
 * This work was done as a project of the Sable Research Group,      *
 * School of Computer Science, McGill University, Canada             *
 * (http://www.sable.mcgill.ca/).  It is understood that any         *
 * modification not identified as such is not covered by the         *
 * preceding statement.                                              *
 *                                                                   *
 * This work is free software; you can redistribute it and/or        *
 * modify it under the terms of the GNU Library General Public       *
 * License as published by the Free Software Foundation; either      *
 * version 2 of the License, or (at your option) any later version.  *
 *                                                                   *
 * This work is distributed in the hope that it will be useful,      *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of    *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU *
 * Library General Public License for more details.                  *
 *                                                                   *
 * You should have received a copy of the GNU Library General Public *
 * License along with this library; if not, write to the             *
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,      *
 * Boston, MA  02111-1307, USA.                                      *
 *                                                                   *
 * Java is a trademark of Sun Microsystems, Inc.                     *
 *                                                                   *
 * To submit a bug report, send a comment, or get the latest news on *
 * this project and other Sable Research Group projects, please      *
 * visit the web site: http://www.sable.mcgill.ca/                   *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
  
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Coffi, a bytecode parser for the Java(TM) language.               *
 * Copyright (C) 1996, 1997 Clark Verbrugge (clump@sable.mcgill.ca). *
 * All rights reserved.                                              *
 *                                                                   *
 * This work was done as a project of the Sable Research Group,      *
 * School of Computer Science, McGill University, Canada             *
 * (http://www.sable.mcgill.ca/).  It is understood that any         *
 * modification not identified as such is not covered by the         *
 * preceding statement.                                              *
 *                                                                   *
 * This work is free software; you can redistribute it and/or        *
 * modify it under the terms of the GNU Library General Public       *
 * License as published by the Free Software Foundation; either      *
 * version 2 of the License, or (at your option) any later version.  *
 *                                                                   *
 * This work is distributed in the hope that it will be useful,      *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of    *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU *
 * Library General Public License for more details.                  *
 *                                                                   *
 * You should have received a copy of the GNU Library General Public *
 * License along with this library; if not, write to the             *
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,      *
 * Boston, MA  02111-1307, USA.                                      *
 *                                                                   *
 * Java is a trademark of Sun Microsystems, Inc.                     *
 *                                                                   *
 * To submit a bug report, send a comment, or get the latest news on *
 * this project and other Sable Research Group projects, please      *
 * visit the web site: http://www.sable.mcgill.ca/                   *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

/*
 Reference Version
 -----------------
 This is the latest official version on which this file is based.
 The reference version is: $CoffiVersion: 1.1 $
                           $JimpleVersion: 0.5 $

 Change History
 --------------
 A) Notes:

 Please use the following template.  Most recent changes should
 appear at the top of the list.

 - Modified on [date (March 1, 1900)] by [name]. [(*) if appropriate]
   [description of modification].

 Any Modification flagged with "(*)" was done as a project of the
 Sable Research Group, School of Computer Science,
 McGill University, Canada (http://www.sable.mcgill.ca/).

 You should add your copyright, using the following template, at
 the top of this file, along with other copyrights.

 *                                                                   *
 * Modifications by [name] are                                       *
 * Copyright (C) [year(s)] [your name (or company)].  All rights     *
 * reserved.                                                         *
 *                                                                   *

 B) Changes:

 - Modified on 15-Jun-1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   First internal release (Version 0.1).
*/

package ca.mcgill.sable.soot.coffi;

import ca.mcgill.sable.soot.jimple.*;
import ca.mcgill.sable.util.*;
import ca.mcgill.sable.soot.baf.*;
import ca.mcgill.sable.soot.*;

public class Util
{
    static Map classNameToAbbreviation;
    
    static SootClassManager classManager;
    
    static void setActiveClassManager(SootClassManager manager)
    {
        classManager = manager;
    }
        
    public static void resolveClass(SootClass bclass)
    {
        if(ca.mcgill.sable.soot.jimple.Main.isVerbose)
            System.out.println("Resolving " + bclass.getName() + "...");
        
        SootClassManager cm = bclass.getManager();
        
        ClassFile coffiClass = new ClassFile(bclass.getName());
        
        // Load up class file, and retrieve bclass from class manager. 
        {
            boolean success = coffiClass.loadClassFile();
            
            if(!success)
                throw new RuntimeException("Couldn't load class file.");

            CONSTANT_Class_info c = (CONSTANT_Class_info) coffiClass.constant_pool[coffiClass.this_class];
            
            String name = ((CONSTANT_Utf8_info) (coffiClass.constant_pool[c.name_index])).convert();
            name = name.replace('/', '.');
            
            bclass.setName(name);
                // replace this classes name with its fully qualified version.
                
        }
        
        // Set modifier
            bclass.setModifiers(coffiClass.access_flags & (~0x0020));
                // don't want the ACC_SUPER flag, it is always supposed to be set anyways 
                
        // Set superclass
        {
            if(coffiClass.super_class != 0)
            {
                // This object is not java.lang.Object, so must have a super class
                
                CONSTANT_Class_info c = (CONSTANT_Class_info) coffiClass.constant_pool[coffiClass.
                    super_class];
            
                String superName = ((CONSTANT_Utf8_info) (coffiClass.constant_pool[c.name_index])).convert();
                superName = superName.replace('/', '.');

                bclass.setSuperClass(cm.getClass(superName));
            }
        }
                 
        // Add interfaces to the bclass
        {
            for(int i = 0; i < coffiClass.interfaces_count; i++)
            {
                CONSTANT_Class_info c = (CONSTANT_Class_info) coffiClass.constant_pool[coffiClass.
                    interfaces[i]];
                
                String interfaceName = 
                    ((CONSTANT_Utf8_info) (coffiClass.constant_pool[c.name_index])).convert();
                
                interfaceName = interfaceName.replace('/', '.');
                
                SootClass interfaceClass = cm.getClass(interfaceName);
                
                interfaceClass.setModifiers(Modifier.INTERFACE);                
                bclass.addInterface(interfaceClass);
            }
        }
              
        // Add every field to the bclass
            for(int i = 0; i < coffiClass.fields_count; i++)
            {
                field_info fieldInfo = coffiClass.fields[i];
                
                String fieldName = ((CONSTANT_Utf8_info) 
                    (coffiClass.constant_pool[fieldInfo.name_index])).convert();
                
                String fieldDescriptor = ((CONSTANT_Utf8_info) 
                    (coffiClass.constant_pool[fieldInfo.descriptor_index])).convert();
                    
                int modifiers = fieldInfo.access_flags;
                
                if(bclass.declaresField(fieldName))
                {
                    SootField field = bclass.getField(fieldName);
                    
                    field.setType(jimpleTypeOfFieldDescriptor(cm, fieldDescriptor));
                    field.setModifiers(modifiers);
                }
                else {
                    bclass.addField(new SootField(fieldName, 
                        jimpleTypeOfFieldDescriptor(cm, fieldDescriptor), modifiers));
                } 
            } 
        
        // Add every method to the bclass
            for(int i = 0; i < coffiClass.methods_count; i++)
            {
                method_info methodInfo = coffiClass.methods[i];
                
                String methodName = ((CONSTANT_Utf8_info) 
                    (coffiClass.constant_pool[methodInfo.name_index])).convert();
                
                String methodDescriptor = ((CONSTANT_Utf8_info) 
                    (coffiClass.constant_pool[methodInfo.descriptor_index])).convert();
                
                List parameterTypes;
                Type returnType;
                
                // Generate parameterTypes & returnType
                {
                    Type[] types = jimpleTypesOfFieldOrMethodDescriptor(cm, 
                        methodDescriptor);
                    
                    parameterTypes = new ArrayList();
                    
                    for(int j = 0; j < types.length - 1; j++)
                        parameterTypes.add(types[j]);
                    
                    returnType = types[types.length - 1];
                }
                
                int modifiers = methodInfo.access_flags;
                
                SootMethod method;
                
                if(bclass.declaresMethod(methodName, parameterTypes))
                {
                    method = bclass.getMethod(methodName, parameterTypes);
                    method.setParameterTypes(parameterTypes);
                    method.setReturnType(returnType);
                    method.setModifiers(modifiers);
                }
                else {
                    method = new SootMethod(methodName, 
                        parameterTypes, returnType, modifiers);
                    bclass.addMethod(method);
                }
                
                methodInfo.jmethod = method;
                
                // add exceptions to method
                {
                    for(int j = 0; j < methodInfo.attributes_count; j++)
                        if(methodInfo.attributes[j] instanceof Exception_attribute)
                        {
                            Exception_attribute exceptions = (Exception_attribute) methodInfo.attributes[j];
                                
                            for(int k = 0; k < exceptions.number_of_exceptions; k++)
                            {
                                CONSTANT_Class_info c = (CONSTANT_Class_info) coffiClass.
                                    constant_pool[exceptions.exception_index_table[k]];
    
                                String exceptionName = ((CONSTANT_Utf8_info) 
                                    (coffiClass.constant_pool[c.name_index])).convert();
                                    
                                exceptionName = exceptionName.replace('/', '.');
                                
                                method.addException(cm.getClass(exceptionName));
                            }
                        }
                }
            }
        
        // Set coffi source of method
            for(int i = 0; i < coffiClass.methods_count; i++)
            {
                method_info methodInfo = coffiClass.methods[i];
                methodInfo.jmethod.setSource(coffiClass, methodInfo);
            }            
    }
           
    static Type jimpleReturnTypeOfMethodDescriptor(SootClassManager cm, 
        String descriptor)
    {
        Type[] types = jimpleTypesOfFieldOrMethodDescriptor(cm, descriptor);
        
        return types[types.length - 1];
    }
    
    static Type[] jimpleTypesOfFieldOrMethodDescriptor(SootClassManager cm, 
        String descriptor)
    {
        java.util.Vector types = new java.util.Vector();
        
        while(descriptor.length() != 0)
        {
            boolean isArray = false;
            int numDimensions = 0;
            Type baseType;

            // Skip parenthesis
                if(descriptor.startsWith("(") || descriptor.startsWith(")"))
                {
                    descriptor = descriptor.substring(1);
                    continue;
                }
                
            // Handle array case
                while(descriptor.startsWith("["))
                {
                    isArray = true;
                    numDimensions++;
                    descriptor = descriptor.substring(1);
                }

            // Determine base type            
                if(descriptor.startsWith("B"))
                {
                    baseType = ByteType.v();
                    descriptor = descriptor.substring(1);
                }
                else if(descriptor.startsWith("C"))
                {
                    baseType = CharType.v();
                    descriptor = descriptor.substring(1);
                }
                else if(descriptor.startsWith("D"))
                {
                    baseType = DoubleType.v();
                    descriptor = descriptor.substring(1);
                }
                else if(descriptor.startsWith("F"))
                {
                    baseType = FloatType.v();
                    descriptor = descriptor.substring(1);
                }
                else if(descriptor.startsWith("I"))
                {
                    baseType = IntType.v();
                    descriptor = descriptor.substring(1);
                }
                else if(descriptor.startsWith("J"))
                {
                    baseType = LongType.v();
                    descriptor = descriptor.substring(1);
                }
                else if(descriptor.startsWith("L"))
                {
                    int index = descriptor.indexOf(';');
                    
                    if(index == -1)
                        throw new RuntimeException("Class reference has no ending ;");
                    
                    String className = descriptor.substring(1, index);
                
                    baseType = RefType.v(className.replace('/', '.'));
                    
                    descriptor = descriptor.substring(index + 1);
                }
                else if(descriptor.startsWith("S"))
                {
                    baseType = ShortType.v();
                    descriptor = descriptor.substring(1);
                }
                else if(descriptor.startsWith("Z"))
                {
                    baseType = BooleanType.v();
                    descriptor = descriptor.substring(1);
                }
                else if(descriptor.startsWith("V"))
                {
                    baseType = VoidType.v();
                    descriptor = descriptor.substring(1);
                }
                else
                    throw new RuntimeException("Unknown field type!");
                  
            Type t;
            
            // Determine type  
                if(isArray)
                    t = ArrayType.v((BaseType) baseType, numDimensions);
                else
                    t = baseType;

            types.addElement(t);
        }
        
        // Generate array containing types
        {
            Type[] typesArray = new Type[types.size()];
            
            types.copyInto(typesArray);
            
            return typesArray; 
        }
    }
        
    static Type jimpleTypeOfFieldDescriptor(SootClassManager cm, 
        String descriptor)
    {
        boolean isArray = false;
        int numDimensions = 0;
        Type baseType;
        
        // Handle array case
            while(descriptor.startsWith("["))
            {
                isArray = true;
                numDimensions++;
                descriptor = descriptor.substring(1);
            }

        // Determine base type            
            if(descriptor.equals("B"))
                baseType = ByteType.v();
            else if(descriptor.equals("C"))
                baseType = CharType.v();
            else if(descriptor.equals("D"))
                baseType = DoubleType.v();
            else if(descriptor.equals("F"))
                baseType = FloatType.v();
            else if(descriptor.equals("I"))
                baseType = IntType.v();
            else if(descriptor.equals("J"))
                baseType = LongType.v();
            else if(descriptor.startsWith("L"))
            {
                if(!descriptor.endsWith(";"))
                    throw new RuntimeException("Class reference does not end with ;");
                
                String className = descriptor.substring(1, descriptor.length() - 1);
                
                baseType = RefType.v(className.replace('/', '.'));
            }
            else if(descriptor.equals("S"))
                baseType = ShortType.v();
            else if(descriptor.equals("Z"))
                baseType = BooleanType.v();
            else
                throw new RuntimeException("Unknown field type: " + descriptor);
                
        // Return type
            if(isArray)
                return ArrayType.v((BaseType) baseType, numDimensions);
            else
                return baseType;
    }
    
    static int nextEasyNameIndex;
    
    static void resetEasyNames()
    {
        nextEasyNameIndex = 0;
    }
    
    static String getNextEasyName()
    {
        final String[] easyNames = 
            {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", 
             "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};
    
        int justifiedIndex = nextEasyNameIndex++;
        
        if(justifiedIndex >= easyNames.length)
            return "local" + (justifiedIndex - easyNames.length);
        else
            return easyNames[justifiedIndex];
    }    
    
    static void setClassNameToAbbreviation(Map map)
    {
        classNameToAbbreviation = map;
    }
    
    static Local getLocalForStackOp(StmtListBody listBody, TypeStack typeStack,
        int index)
    {
        if(typeStack.get(index).equals(Double2ndHalfType.v()) || 
            typeStack.get(index).equals(Long2ndHalfType.v()))
        {
            index--;
        }
           
        return getLocalCreatingIfNecessary(listBody, "op" + index, UnknownType.v());
    }
    
    static String getAbbreviationOfClassName(String className)
    {    
        StringBuffer buffer = new StringBuffer(new Character(className.charAt(0)).toString());
        int periodIndex = 0;
        
        for(;;) 
        {
            periodIndex = className.indexOf('.', periodIndex + 1);
        
            if(periodIndex == -1)
                break;
                
            buffer.append(Character.toLowerCase(className.charAt(periodIndex + 1)));
        } 
        
        return buffer.toString();   
    }

    static String getNormalizedClassName(String className)
    {
        className = className.replace('/', '.');
        
        if(className.endsWith(";"))
            className = className.substring(0, className.length() - 1);
        
        // Handle array case
        {    
            int numDimensions = 0;
            
            while(className.startsWith("["))
            {
                numDimensions++;
                className = className.substring(1, className.length());
                className = className + "[]";
            }
            
            if(numDimensions != 0)
            {
                if(!className.startsWith("L"))
                    throw new RuntimeException("For some reason an array reference does not start with L");
                
                className = className.substring(1, className.length());
            }
        }
        
        
        return className;
    }

     static Local 
        getLocalCreatingIfNecessary(StmtListBody listBody, String name, Type type)
    {        
        if(listBody.declaresLocal(name))
        {
            return listBody.getLocal(name);            
        }
        else {
            Local l = new Local(name, type);
            listBody.addLocal(l);
                        
            return l;
        }
    } 
        
    static Local getLocalForIndex(StmtListBody listBody, int index)
    {
        String name = "l" + index;
        
        if(listBody.declaresLocal(name))
            return listBody.getLocal(name);
        else {
            Local l = new Local(name, 
                UnknownType.v());
                
            listBody.addLocal(l);
            
            return l;
        }
    }

    /*    
    static void setLocalType(Local local, List locals, 
        int localIndex, Type type)
    {
        if(local.getType().equals(UnknownType.v()) ||
            local.getType().equals(type))
        {
            local.setType(type);
            
            if(local.getType().equals(DoubleType.v()) ||
                local.getType().equals(LongType.v()))
            {
                // This means the next local becomes voided, since these types occupy two 
                // words.
                
                Local secondHalf = (Local) locals.get(localIndex + 1);
                
                secondHalf.setType(VoidType.v());
            }
            
            return;
        }
        
        if(type.equals(IntType.v()))
        {
            if(local.getType().equals(BooleanType.v()) ||
               local.getType().equals(CharType.v()) ||
               local.getType().equals(ShortType.v()) ||
               local.getType().equals(ByteType.v()))
            {
                // Even though it's not the same, it's ok, because booleans, chars, shorts, and
                // bytes are all sort of treated like integers by the JVM.
                return;
            }
            
        }
        
        throw new RuntimeException("required and actual types do not match: " + type.toString() + 
                " with " + local.getType().toString());
    }    */
}





















