/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997 Clark Verbrugge
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place - Suite 330,
 * Boston, MA 02111-1307, USA.
 */

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */







package soot.coffi;

import soot.jimple.*;
import soot.util.*;
import java.util.*;
import java.io.*;
import soot.baf.*;
import soot.*;


public class Util
{
    static Map classNameToAbbreviation;
    static Scene scene;
    static Set markedClasses;
    static LinkedList classesToResolve;

    static int activeOriginalIndex = -1;
    static cp_info[] activeConstantPool = null;
    static LocalVariableTable_attribute activeVariableTable;
    static boolean useFaithfulNaming = false;
    static boolean isLocalStore = false;  // global variable used 
    static boolean isWideLocalStore = false;
    public static void setFaithfulNaming(boolean v)
    {
        useFaithfulNaming = v;
    }    
    static void setActiveClassManager(Scene manager)
    {
        scene = manager;
    }

    public static void assertResolvedClass(String className)
    {
        if(!scene.containsClass(className))
        {
            SootClass newClass = new SootClass(className);
            scene.addClass(newClass);
            newClass.setContextClass();
            
            markedClasses.add(newClass);
            classesToResolve.addLast(newClass);
        }
    }

    public static void assertResolvedClassForType(Type type)
    {
        if(type instanceof RefType)
            assertResolvedClass(((RefType) type).className);
        else if(type instanceof ArrayType)
            assertResolvedClassForType(((ArrayType) type).baseType);
    }
    
    public static SootClass getResolvedClass(String className)
    {
        if(scene.containsClass(className))
            return scene.getSootClass(className);
            
        SootClass newClass = new SootClass(className);
        scene.addClass(newClass);
        newClass.setContextClass();
        
        markedClasses.add(newClass);
        classesToResolve.addLast(newClass);
           
        return newClass;
    }

    public static SootClass getResolvedClass2(String className)
    {
        if(scene.containsClass(className))
            return scene.getSootClass(className);
            
        SootClass newClass = new SootClass(className);
        scene.addClass(newClass);
        newClass.setContextClass();
        
        return newClass;
    }

    
    public static SootClass resolveClassAndSupportClasses2(String className, InputStream is)
    {
        SootClass newClass = null;
        Scene cm = Scene.v();
        scene = cm;
        if(scene.containsClass(className))
            newClass =  scene.getSootClass(className);
        else {
            newClass = new SootClass(className);
            scene.addClass(newClass);
            newClass.setContextClass();
        
            //markedClasses.add(newClass);
            //classesToResolve.addLast(newClass);
        }

        
            
        SootClass bclass = newClass;
                
        className = bclass.getName();
            
            
        if(soot.Main.isVerbose)
            System.out.println("Resolving " + className + "...");
    
        ClassFile coffiClass = new ClassFile(className);
    
        // Load up class file, and retrieve bclass from class manager.
        {
            boolean success = coffiClass.loadClassFile(is);
                
               


                

            if(!success)
                {
                    if(!Scene.v().allowsPhantomRefs())
                        throw new RuntimeException("Could not load classfile: " + bclass.getName());
                    else {

                
                        System.out.println("Warning: " + className + " is a phantom class!");
                        bclass.setPhantom(true);                                                                
                        //                continue;
                        return newClass;
                    } 

                }
            
                
                
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
    
                    bclass.setSuperclass(getResolvedClass2(superName));
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
    
                    SootClass interfaceClass = getResolvedClass2(interfaceName);
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
                Type fieldType = jimpleTypeOfFieldDescriptor(cm, fieldDescriptor);
                    
                bclass.addField(new SootField(fieldName,
                                              fieldType, modifiers));
                    
                //                assertResolvedClassForType(fieldType);
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
                        {
                            //                            assertResolvedClassForType(types[j]);
                            parameterTypes.add(types[j]);
                        }
                        
                    returnType = types[types.length - 1];
                    //                    assertResolvedClassForType(returnType);
                }
    
                int modifiers = methodInfo.access_flags;
    
                SootMethod method;
    
                method = new SootMethod(methodName,
                                        parameterTypes, returnType, modifiers);
                bclass.addMethod(method);
    
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
    
                                        method.addException(getResolvedClass2(exceptionName));
                                    }
                            }
                }
                    
                
            }

        // Set coffi source of method
        for(int i = 0; i < coffiClass.methods_count; i++)
            {
                method_info methodInfo = coffiClass.methods[i];
                //                methodInfo.jmethod.setSource(coffiClass, methodInfo);
                methodInfo.jmethod.setSource(new CoffiMethodSource(coffiClass, methodInfo));
            }
            
        
        

        //System.out.println("loading" + timer.getTime());
        //System.out.println("building" + buildTimer.getTime());


        
        return newClass;
    }
    

    public static void resolveFromClassFile(SootClass aClass, InputStream is, soot.SootResolver sootResolver, Scene cm)
    {
        SootClass bclass = aClass;                
        String className = bclass.getName();

        setActiveClassManager(cm);
    
        ClassFile coffiClass = new ClassFile(className);
        
        // Load up class file, and retrieve bclass from class manager.
        {
            boolean success = coffiClass.loadClassFile(is);                                  
            if(!success)
                {
                    if(!Scene.v().allowsPhantomRefs())
                        throw new RuntimeException("Could not load classfile: " + bclass.getName());
                    else {                        
                        System.out.println("Warning: " + className + " is a phantom class!");
                        bclass.setPhantom(true);                                                                
                        return;
                    } 
                    
                }
            
            CONSTANT_Class_info c = (CONSTANT_Class_info) coffiClass.constant_pool[coffiClass.this_class];
    
            String name = ((CONSTANT_Utf8_info) (coffiClass.constant_pool[c.name_index])).convert();
            name = name.replace('/', '.');
                	    
            bclass.setName(name);
            // replace this classe'ss name with its fully qualified version.    
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
    
                    bclass.setSuperclass(sootResolver.getResolvedClass(superName));
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
    
                    SootClass interfaceClass = sootResolver.getResolvedClass(interfaceName);
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
                Type fieldType = jimpleTypeOfFieldDescriptor(cm, fieldDescriptor);
                    
                bclass.addField(new SootField(fieldName,
                                              fieldType, modifiers));
                    
                sootResolver.assertResolvedClassForType(fieldType);
            }
    
        // Add every method to the bclass
        for(int i = 0; i < coffiClass.methods_count; i++)
            {
                method_info methodInfo = coffiClass.methods[i];
		
		
		if( (coffiClass.constant_pool[methodInfo.name_index]) == null) {
		    System.err.println("method index: " + methodInfo.toName(coffiClass.constant_pool));
		    throw new RuntimeException("method has no name");
		}

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
                        {
                            sootResolver.assertResolvedClassForType(types[j]);
                            parameterTypes.add(types[j]);
                        }
                        
                    returnType = types[types.length - 1];
                    sootResolver.assertResolvedClassForType(returnType);
                }
    
                int modifiers = methodInfo.access_flags;
    
                SootMethod method;
    
                method = new SootMethod(methodName,
                                        parameterTypes, returnType, modifiers);
                bclass.addMethod(method);
    
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
    
                                        method.addException(sootResolver.getResolvedClass(exceptionName));
                                    }
                            }
                }
                    
                // Go through the constant pool, forcing all mentioned classes to be resolved. 
                {
                    for(int k = 0; k < coffiClass.constant_pool_count; k++)
                        if(coffiClass.constant_pool[k] instanceof CONSTANT_Class_info)
                            {
                                CONSTANT_Class_info c = (CONSTANT_Class_info) coffiClass.constant_pool[k];

                                String desc = ((CONSTANT_Utf8_info) (coffiClass.constant_pool[c.name_index])).convert();
                                String name = desc.replace('/', '.');

                                if(name.startsWith("["))
                                    sootResolver.assertResolvedClassForType(jimpleTypeOfFieldDescriptor(cm, desc));
                                else
                                    sootResolver.assertResolvedClass(name);
                            }
                }
            }

        // Set coffi source of method
        for(int i = 0; i < coffiClass.methods_count; i++)
            {
                method_info methodInfo = coffiClass.methods[i];
                //                methodInfo.jmethod.setSource(coffiClass, methodInfo);
                methodInfo.jmethod.setSource(new CoffiMethodSource(coffiClass, methodInfo));
            }
        
    }
    





    public static SootClass resolveClassAndSupportClasses(String className, Scene cm)
    {
        soot.Timer timer = new soot.Timer("timer");
        soot.Timer buildTimer = new soot.Timer("build");

        soot.Main.resolverTimer.start();
        
        setActiveClassManager(cm);
        
        classesToResolve = new LinkedList();
        markedClasses = new HashSet();
        
        SootClass newClass = getResolvedClass(className);
        
        while(!classesToResolve.isEmpty())
        {
            SootClass bclass = (SootClass) classesToResolve.removeFirst();
                
            className = bclass.getName();
            
            timer.start();
            
            if(soot.Main.isVerbose)
                System.out.println("Resolving " + className + "...");
    
            ClassFile coffiClass = new ClassFile(className);
    
            // Load up class file, and retrieve bclass from class manager.
            {
                boolean success = coffiClass.loadClassFile();
                
               
                timer.end();

                

                if(!success)
                    {
                    if(!Scene.v().allowsPhantomRefs())
                        throw new RuntimeException("Could not load classfile: " + bclass.getName());
                    else {
                        System.out.println("Warning: " + className + " is a phantom class!");
                        bclass.setPhantom(true);                                                                
                        continue;
                    } 

                }
                buildTimer.start();
                
                
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
    
                    bclass.setSuperclass(getResolvedClass(superName));
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
    
                    SootClass interfaceClass = getResolvedClass(interfaceName);
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
                    Type fieldType = jimpleTypeOfFieldDescriptor(cm, fieldDescriptor);
                    
                    bclass.addField(new SootField(fieldName,
                        fieldType, modifiers));
                    
                    assertResolvedClassForType(fieldType);
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
                        {
                            assertResolvedClassForType(types[j]);
                            parameterTypes.add(types[j]);
                        }
                        
                        returnType = types[types.length - 1];
                        assertResolvedClassForType(returnType);
                    }
    
                    int modifiers = methodInfo.access_flags;
    
                    SootMethod method;
    
                    method = new SootMethod(methodName,
                                            parameterTypes, returnType, modifiers);
                    bclass.addMethod(method);
    
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
    
                                    method.addException(getResolvedClass(exceptionName));
                                }
                            }
                    }
                    
                // Go through the constant pool, forcing all mentioned classes to be resolved. 
                {
                    for(int k = 0; k < coffiClass.constant_pool_count; k++)
                        if(coffiClass.constant_pool[k] instanceof CONSTANT_Class_info)
                        {
                            CONSTANT_Class_info c = (CONSTANT_Class_info) coffiClass.constant_pool[k];

                            String desc = ((CONSTANT_Utf8_info) (coffiClass.constant_pool[c.name_index])).convert();
                            String name = desc.replace('/', '.');

                            if(name.startsWith("["))
                                assertResolvedClassForType(jimpleTypeOfFieldDescriptor(cm, desc));
                            else
                                assertResolvedClass(name);
                        }
                }
            }

        // Set coffi source of method
            for(int i = 0; i < coffiClass.methods_count; i++)
            {
                method_info methodInfo = coffiClass.methods[i];
                //                methodInfo.jmethod.setSource(coffiClass, methodInfo);
                methodInfo.jmethod.setSource(new CoffiMethodSource(coffiClass, methodInfo));
            }
            
            buildTimer.end();
        }

        //System.out.println("loading" + timer.getTime());
        //System.out.println("building" + buildTimer.getTime());

        soot.Main.resolverTimer.end();

        return newClass;
    }




    static Type jimpleReturnTypeOfMethodDescriptor(Scene cm,
        String descriptor)
    {
        Type[] types = jimpleTypesOfFieldOrMethodDescriptor(cm, descriptor);

        return types[types.length - 1];
    }

    static private ArrayList conversionTypes = new ArrayList();
    
    static public Type[] jimpleTypesOfFieldOrMethodDescriptor(Scene cm,
        String descriptor)
    {
        conversionTypes.clear();

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

            conversionTypes.add(t);
        }

        return (Type[]) conversionTypes.toArray(new Type[0]);
    }

    public static Type jimpleTypeOfFieldDescriptor(Scene cm,
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
            else if(descriptor.equals("V"))
                baseType = VoidType.v();
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

    static Local getLocalForStackOp(JimpleBody listBody, TypeStack typeStack,
        int index)
    {
        if(typeStack.get(index).equals(Double2ndHalfType.v()) ||
            typeStack.get(index).equals(Long2ndHalfType.v()))
        {
            index--;
        }

        return getLocalCreatingIfNecessary(listBody, "$stack" + index, UnknownType.v());
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

    static public Local getLocal(Body b, String name) 
        throws soot.jimple.NoSuchLocalException
    {
        Iterator localIt = b.getLocals().iterator();

        while(localIt.hasNext())
        {
            Local local = (Local) localIt.next();

            if(local.getName().equals(name))
                return local;
        }

        throw new soot.jimple.NoSuchLocalException();
    }


    static public boolean declaresLocal(Body b, String localName)
    {
        Iterator localIt = b.getLocals().iterator();

        while(localIt.hasNext())
        {
            Local local = (Local) localIt.next();

            if(local.getName().equals(localName))
                return true;
        }

        return false;
    }

     static Local
        getLocalCreatingIfNecessary(JimpleBody listBody, String name, Type type)
    {
        if(declaresLocal(listBody, name))
        {
            return getLocal(listBody, name);
        }
        else {
            Local l = Jimple.v().newLocal(name, type);
            listBody.getLocals().add(l);

            return l;
        }
    }

    static Local getLocalForIndex(JimpleBody listBody, int index)
    {
        String name = null;
        boolean assignedName = false;
        
        if(useFaithfulNaming && activeVariableTable != null)
        {
            if(activeOriginalIndex != -1)
            {

	      // Feng asks: why this is necessary? it does wrong thing
	      //            for searching local variable names.
	      // It is going to be verified with plam.
                if(isLocalStore)
                    activeOriginalIndex++;
                if(isWideLocalStore)
                    activeOriginalIndex++;

                name = activeVariableTable.getLocalVariableName(activeConstantPool,
                    index, activeOriginalIndex);
               
                if(name != null) 
                    assignedName = true;
            }
        }  
        
        if(!assignedName)
            name = "l" + index;

        if(declaresLocal(listBody, name))
            return getLocal(listBody, name);
        else {
            Local l = Jimple.v().newLocal(name,
                UnknownType.v());

            listBody.getLocals().add(l);

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

    /** Verifies the prospective name for validity as a Jimple name.
     * In particular, first-char is alpha | _ | $, subsequent-chars 
     * are alphanum | _ | $. 
     *
     * We could use isJavaIdentifier, except that Jimple's grammar
     * doesn't support all of those, just ASCII.
     *
     * I'd put this in soot.Local, but that's an interface.
     *
     * @author Patrick Lam
     */
    static boolean isValidJimpleName(String prospectiveName) {
	for (int i = 0; i < prospectiveName.length(); i++) {
	    char c = prospectiveName.charAt(i);
	    if (i == 0 && c >= '0' && c <= '9')
		return false;

	    if (!((c >= '0' && c <= '9') ||
		  (c >= 'a' && c <= 'z') ||
		  (c >= 'A' && c <= 'Z') ||
		  (c == '_' || c == '$')))
		return false;
	}
	return true;
    }
}
