 /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Soot, a Java(TM) classfile optimization framework.                *
 * Copyright (C) 1997, 1998 Raja Vallee-Rai (kor@sable.mcgill.ca)    *
 * All rights reserved.                                              *
 *                                                                   *
 * Modifications by Vijay Sundaresan (vijay@sable.mcgill.ca) are     *
 * Copyright (C) 1999 Vijay Sundaresan.  All rights reserved.        *
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

 - Modified on March 27, 1999 by Raja Vallee-Rai (rvalleerai@sable.mcgill.ca) (*)
   Changed the way classes are retrieved and loaded in.  
   Added a getPackageName() method.
   
 - Modified on March 2, 1999 by Patrick Lam (plam@sable.mcgill.ca)
   Added a toString method.
   
 - Modified on January 26, 1998 by Vijay Sundaresan (vija@sable.mcgill.ca) (*)
   Made the write() method name the output jasmin files according to the classfile
   being output.
   
 - Modified on November 21, 1998 by Raja Vallee-Rai (kor@sable.mcgill.ca) (*)
   Changed the default resolution state of new classes.

 - Modified on November 2, 1998 by Raja Vallee-Rai (kor@sable.mcgill.ca) (*)
   Repackaged all source files and performed extensive modifications.
   First initial release of Soot.

 - Modified on 15-Jun-1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   First internal release (Version 0.1).
*/

package ca.mcgill.sable.soot;

import ca.mcgill.sable.util.*;
import java.util.*;
import java.io.*;

/*
 * Incomplete and inefficient implementation.
 *
 * Implementation notes:
 *
 * 1. The getFieldOf() method is slow because it traverses the list of fields, comparing the names,
 * one by one.  If you establish a Dictionary of Name->Field, you will need to add a
 * notifyOfNameChange() method, and register fields which belong to classes, because the hashtable
 * will need to be updated.  I will do this later. - kor  16-Sep-97
 */
/**
    Instances of this class represent Java classes.  They are usually created by a Scene,
    but can also be constructed manually through the given constructors.

*/

public class SootClass
{
    private static char fileSeparator = System.getProperty("file.separator").charAt(0);

    String name;
    int modifiers;
    List fields = new ArrayList();
    List methods = new ArrayList();
    List interfaces = new ArrayList();

    Scene scene;
    boolean isInScene;

    SootClass superClass;

    /**
        Constructs an empty SootClass with the given name and modifiers.
    */

    public SootClass(String name, int modifiers)
    {
        this.name = name;
        this.modifiers = modifiers;

    }

    /**
        Constructs an empty SootClass with the given name and no modifiers.
    */

    public SootClass(String name)
    {
        this.name = name;
        this.modifiers = 0;
    }

    /**
        Is this class being managed by a Scene? A class may be unmanaged  while it is being constructed.
    */

    public boolean isInScene()
    {
        return isInScene;
    }

    /**
        Returns the number of fields in this class.
    */

    public int getFieldCount()
    {
        return fields.size();
    }

    /**
     * Returns a backed list of fields.
     */

    public List getFields()
    {
        return fields;
    }

    /*
    public void setFields(Field[] fields)
    {
        this.fields = new ArraySet(fields);
    }
    */

    /**
        Adds the given field to this class.
    */

    public void addField(SootField f) throws AlreadyDeclaredException 
    {
        if(f.isDeclared())
            throw new AlreadyDeclaredException(f.getName());

            /*
        if(declaresField(f.getName()))
            throw new DuplicateNameException(f.getName());
 */
 
        fields.add(f);
        
        f.isDeclared = true;
        f.declaringClass = this;
        
        scene.fieldSignatureToField.put(f.getSignature(), f);
        
    }

    /**
        Removes the given field from this class.
    */

    public void removeField(SootField f) throws IncorrectDeclarerException
    {
        if(!f.isDeclared() || f.getDeclaringClass() != this)
            throw new IncorrectDeclarerException(f.getName());

        fields.remove(f);
        f.isDeclared = false;
    }

    /**
        Returns the field of this class with the given name and type. 
    */

    public SootField getField(String name, Type type) throws ca.mcgill.sable.soot.NoSuchFieldException
    {
        Iterator fieldIt = getFields().iterator();

        while(fieldIt.hasNext())
        {
            SootField field = (SootField) fieldIt.next();

            if(field.name.equals(name) && field.type.equals(type))
                return field;
        }

        throw new ca.mcgill.sable.soot.NoSuchFieldException("No field " + name + " in class " + getName());
    }

    
    /**
        Returns the field of this class with the given name.  May throw an AmbiguousFieldException if there
        are more than one.
    */

    public SootField getFieldByName(String name) throws ca.mcgill.sable.soot.NoSuchFieldException, ca.mcgill.sable.soot.AmbiguousFieldException
    {
        boolean found = false;
        SootField foundField = null;

        Iterator fieldIt = getFields().iterator();

        while(fieldIt.hasNext())
        {
            SootField field = (SootField) fieldIt.next();

            if(field.name.equals(name))
            {
                if(found)
                    throw new AmbiguousFieldException();
                else {
                    found = true;
                    foundField = field;
                }
            }
        }

        if(found)
            return foundField;
        else
            throw new ca.mcgill.sable.soot.NoSuchFieldException("No field " + name + " in class " + getName());
    }

    
    /*    
        Returns the field of this class with the given subsignature.
    */

    public SootField getField(String subsignature) throws ca.mcgill.sable.soot.NoSuchFieldException
    {
        SootField toReturn = (SootField) scene.fieldSignatureToField.get("<" + getName() + ": " + subsignature + ">");
        
        if(toReturn == null)
            throw new ca.mcgill.sable.soot.NoSuchFieldException("No field " + name + " in class " + getName());
        else
            return toReturn;
    }

    
    /**
        Does this class declare a field with the given subsignature?
    */

    public boolean declaresField(String subsignature)
    {
        return scene.fieldSignatureToField.containsKey("<" + getName() + ": " + subsignature + ">");
    }

    
    /*    
        Returns the method of this class with the given subsignature.
    */

    public SootMethod getMethod(String subsignature) throws ca.mcgill.sable.soot.NoSuchMethodException
    {
        SootMethod toReturn = (SootMethod) scene.methodSignatureToMethod.get("<" + getName() + ": " + subsignature + ">");
        
        if(toReturn == null)
            throw new ca.mcgill.sable.soot.NoSuchMethodException("No method " + name + " in class " + getName());
        else
            return toReturn;
    }

    /**
        Does this class declare a method with the given subsignature?
    */

    public boolean declaresMethod(String subsignature)
    {
        return scene.methodSignatureToMethod.containsKey("<" + getName() + ": " + subsignature + ">");
    }
    
    
    /**
        Does this class declare a field with the given name?
    */

    public boolean declaresFieldByName(String name)
    {
        Iterator fieldIt = getFields().iterator();

        while(fieldIt.hasNext())
        {
            SootField field = (SootField) fieldIt.next();

            if(field.name.equals(name))
                return true;
        }

        return false;
    }

    
    /**
        Does this class declare a field with the given name and type.
    */

    public boolean declaresField(String name, Type type)
    {
        Iterator fieldIt = getFields().iterator();

        while(fieldIt.hasNext())
        {
            SootField field = (SootField) fieldIt.next();

            if(field.name.equals(name) &&
                field.type.equals(type))
                return true;
        }

        return false;
    }

    /**
        Returns the number of methods in this class.
    */

    public int getMethodCount()
    {
        return methods.size();
    }

    /**
     * Returns a backed list of methods.
     */

    public List getMethods()
    {
        return methods;
    }

    
    /**
        Attempts to retrieve the method with the given name, parameters and return type.  
        
    */

    public SootMethod getMethod(String name, List parameterTypes, Type returnType) throws
        ca.mcgill.sable.soot.NoSuchMethodException
    {

        Iterator methodIt = getMethods().iterator();

        while(methodIt.hasNext())
        {
            SootMethod method = (SootMethod) methodIt.next();

            if(method.getName().equals(name) &&
                parameterTypes.equals(method.getParameterTypes()) &&
                returnType.equals(method.getReturnType()))
            {
                return method;
            }
        }

        throw new ca.mcgill.sable.soot.NoSuchMethodException(getName() + "." + name + "(" + 
            parameterTypes + ")" + " : " + returnType);
    }

    /**
        Attempts to retrieve the method with the given name and parameters.  This method
        may throw an AmbiguousMethodException if there are more than one method with the
        given name and parameter.
    */

    public SootMethod getMethod(String name, List parameterTypes) throws
        ca.mcgill.sable.soot.NoSuchMethodException, ca.mcgill.sable.soot.AmbiguousMethodException
    {
        boolean found = false;
        SootMethod foundMethod = null;
        
        Iterator methodIt = getMethods().iterator();

        while(methodIt.hasNext())
        {
            SootMethod method = (SootMethod) methodIt.next();

            if(method.getName().equals(name) &&
                parameterTypes.equals(method.getParameterTypes()))
            {
                if(found)
                    throw new ca.mcgill.sable.soot.AmbiguousMethodException();
                else {                    
                    found = true;
                    foundMethod = method;
                }
            }
        }

        if(found)
            return foundMethod;
        else
            throw new ca.mcgill.sable.soot.NoSuchMethodException();
    }

    
     /**
        Attempts to retrieve the method with the given name.  This method
        may throw an AmbiguousMethodException if there are more than one method with the
        given name.
    */

    public SootMethod getMethodByName(String name) throws
        ca.mcgill.sable.soot.NoSuchMethodException, ca.mcgill.sable.soot.AmbiguousMethodException
    {
        boolean found = false;
        SootMethod foundMethod = null;
        
        Iterator methodIt = getMethods().iterator();

        while(methodIt.hasNext())
        {
            SootMethod method = (SootMethod) methodIt.next();

            if(method.getName().equals(name))
            {
                if(found)
                    throw new ca.mcgill.sable.soot.AmbiguousMethodException();
                else {                    
                    found = true;
                    foundMethod = method;
                }
            }
        }

        if(found)
            return foundMethod;
        else
            throw new ca.mcgill.sable.soot.NoSuchMethodException();
    }

    /**
        Does this class declare a method with the given name and parameter types?
    */

    public boolean declaresMethod(String name, List parameterTypes)
    {
        Iterator methodIt = getMethods().iterator();

        while(methodIt.hasNext())
        {
            SootMethod method = (SootMethod) methodIt.next();

            if(method.getName().equals(name) &&
                method.getParameterTypes().equals(parameterTypes))
                return true;
        }
        
        return false;
    }

    /**
        Does this class declare a method with the given name, parameter types, and return type?
    */

    public boolean declaresMethod(String name, List parameterTypes, Type returnType)
    {
        Iterator methodIt = getMethods().iterator();

        while(methodIt.hasNext())
        {
            SootMethod method = (SootMethod) methodIt.next();

            if(method.getName().equals(name) &&
                method.getParameterTypes().equals(parameterTypes) &&
                method.getReturnType().equals(returnType))
                
                return true;
        }
        
        return false;
    }

    /**
        Does this class declare a method with the given name?
    */

    public boolean declaresMethodByName(String name)
    {
        Iterator methodIt = getMethods().iterator();

        while(methodIt.hasNext())
        {
            SootMethod method = (SootMethod) methodIt.next();

            if(method.getName().equals(name))
                return true;
        }
        
        return false;
    }

    /*
    public void setMethods(Method[] method)
    {
        methods = new ArraySet(method);
    }
    */

    /**
        Adds the given method to this class.
    */

    public void addMethod(SootMethod m) throws AlreadyDeclaredException
    {
        if(m.isDeclared())
            throw new AlreadyDeclaredException(m.getName());

        /*
        if(declaresMethod(m.getName(), m.getParameterTypes()))
            throw new DuplicateNameException("duplicate signature for: " + m.getName());
        */
        
        methods.add(m);
        
        m.isDeclared = true;
        m.declaringClass = this;
        
        scene.methodSignatureToMethod.put(m.getSignature(), m);
        
    }

    /**
        Removes the given method from this class.
    */

    public void removeMethod(SootMethod m) throws IncorrectDeclarerException
    {
        if(!m.isDeclared() || m.getDeclaringClass() != this)
            throw new IncorrectDeclarerException(m.getName());

        methods.remove(m);
        m.isDeclared = false;
    }

    /**
        Returns the modifiers of this class.
    */

    public int getModifiers()
    {
        return modifiers;
    }

    /**
        Sets the modifiers for this class.
    */

    public void setModifiers(int modifiers)
    {
        this.modifiers = modifiers;
    }

    /**
        Returns the number of interfaces being directly implemented by this class.  Note that direct
        implementation corresponds to an "implements" keyword in the Java class file and that this class may
        still be implementing additional interfaces in the usual sense by being a subclass of a class
        which directly implements some interfaces.
    */

    public int getInterfaceCount()
    {
        return interfaces.size();
    }

    /**
     * Returns a backed list of the  interfaces that are direclty implemented by this class. (see getInterfaceCount())
     */

    public List getInterfaces()
    {
        return interfaces;
    }

    /**
        Does this class directly implement the given interface? (see getInterfaceCount())
    */

    public boolean implementsInterface(String name)
    {
        Iterator interfaceIt = getInterfaces().iterator();

        while(interfaceIt.hasNext())
        {
            SootClass SootClass = (SootClass) interfaceIt.next();

            if(SootClass.getName().equals(name))
                return true;
        }

        return false;
    }

    /**
        Add the given class to the list of interfaces which are directly implemented by this class.
    */

    public void addInterface(SootClass interfaceClass) throws DuplicateNameException
    {
        if(implementsInterface(interfaceClass.getName()))
            throw new DuplicateNameException(interfaceClass.getName());

        interfaces.add(interfaceClass);
    }

    /**
        Removes the given class from the list of interfaces which are direclty implemented by this class.
    */

    public void removeInterface(SootClass interfaceClass) throws NoSuchInterfaceException
    {
        if(!implementsInterface(interfaceClass.getName()))
            throw new NoSuchInterfaceException(interfaceClass.getName());

        interfaces.remove(interfaceClass);
    }

    /*
    public void setInterfaces(SootClass[] interfaces)
    {
        this.interfaces = new ArraySet(interfaces);
    }
    */

    /**
        Does this class have a superclass? False implies that this is the java.lang.Object class.  Note that interfaces are subclasses
        of the java.lang.Object class.
    */


    public boolean hasSuperclass()
    {
        return superClass != null;
    }

    /**
        Returns the superclass of this class. (see hasSuperclass())
    */

    public SootClass getSuperclass() throws NoSuperclassException
    {
        if(superClass == null)
            throw new NoSuperclassException();
        else
            return superClass;
    }

    /**
        Sets the superclass of this class.  Note that passing a null will cause the class to have no superclass.
    */

    public void setSuperclass(SootClass c)
    {
        superClass = c;
    }

    /**
        Returns the name of this class.
    */

    public String getName()
    {
        return name;
    }

    /**
        Returns the package name of this class.
    */

    public String getPackageName()
    {
        int index = getName().lastIndexOf(".");
        
        if(index == -1)
            return "";
        else
            return name.substring(0, index);
    }

    /**
        Sets the name of this class.
    */

    public void setName(String name) throws DuplicateNameException
    {
        this.name = name;
    }

    public boolean isInterface()
    {
        return Modifier.isInterface(this.getModifiers());
    }

    public void printTo(PrintWriter out)
    {
        printTo(out, 0);
    }

    public void printTo(PrintWriter out, int printBodyOptions)
    {
        // Print class name + modifiers
        {
            String classPrefix = "";

            classPrefix = classPrefix + " " + Modifier.toString(this.getModifiers());
            classPrefix = classPrefix.trim();

            if(!isInterface())
            {
                classPrefix = classPrefix + " class";
                classPrefix = classPrefix.trim();
            }

            out.print(classPrefix + " " + this.getName() + "");
        }

        // Print extension
        {
            if(this.hasSuperclass())
                out.print(" extends " + this.getSuperclass().getName() + "");
        }

        // Print interfaces
        {
            Iterator interfaceIt = this.getInterfaces().iterator();

            if(interfaceIt.hasNext())
            {
                out.print(" implements ");

                out.print("" + ((SootClass) interfaceIt.next()).getName() + "");

                while(interfaceIt.hasNext())
                {
                    out.print(",");
                    out.print(" " + ((SootClass) interfaceIt.next()).getName() + "");
                }
            }
        }

        out.println();
        out.println("{");

        // Print fields
        {
            Iterator fieldIt = this.getFields().iterator();

            if(fieldIt.hasNext())
            {
                while(fieldIt.hasNext())
                    out.println("    " + ((SootField) fieldIt.next()).getDeclaration() + ";");
            }
        }

        // Print methods
        {
            Iterator methodIt = this.getMethods().iterator();

            if(methodIt.hasNext())
            {
                if(this.getMethods().size() != 0)
                    out.println();

                while(methodIt.hasNext())
                {
                    SootMethod method = (SootMethod) methodIt.next();

                    if(!Modifier.isAbstract(method.getModifiers()) &&
                        !Modifier.isNative(method.getModifiers()))
                    {
                        if(!method.hasActiveBody())
                            throw new RuntimeException("method " + method.getName() + " has no active body!");
                        else
                            method.getActiveBody().printTo(out, printBodyOptions);
                            // ((ca.mcgill.sable.soot.jimple.GrimpBody) method.getActiveBody()).printDebugTo(out);
                        
                            

                        if(methodIt.hasNext())
                            out.println();
                    }
                    else {
                        out.print("    ");
                        out.print(method.getDeclaration());
                        out.println(";");

                        if(methodIt.hasNext())
                            out.println();
                    }
                }
            }
        }
        out.println("}");

    }

    /**
        Writes the class out to a file.
     */


    public void write()
    {
        write("");
    }

    boolean containsBafBody()
    {
        Iterator methodIt = getMethods().iterator();
        
        if(!methodIt.hasNext())
            return false;
        else
        {
            SootMethod m = (SootMethod) methodIt.next();
            
            if(m.hasActiveBody() && 
                m.getActiveBody() instanceof ca.mcgill.sable.soot.baf.BafBody)
            {
                return true;
            }
        }
        
        return false;
    }
    /**
        Writes the class out to a file.
     */

    public void write(String outputDir)
    {
        String outputDirWithSep = "";
            
        if(!outputDir.equals(""))
            outputDirWithSep = outputDir + fileSeparator;
            
        try {
            File tempFile = new File(outputDirWithSep + this.getName() + ".jasmin");
 
            FileOutputStream streamOut = new FileOutputStream(tempFile);
            PrintWriter writerOut = new EscapedPrintWriter(streamOut);

            if(containsBafBody())
                new ca.mcgill.sable.soot.baf.JasminClass(this).print(writerOut);
            else
                new ca.mcgill.sable.soot.jimple.JasminClass(this).print(writerOut);

            writerOut.close();

            if(ca.mcgill.sable.soot.Main.isProfilingOptimization)
                ca.mcgill.sable.soot.Main.assembleJasminTimer.start(); 

            // Invoke jasmin
            {
                String[] args;
                
                if(outputDir.equals(""))
                {
                    args = new String[1];
                    
                    args[0] = this.getName() + ".jasmin";
                }
                else
                {
                    args = new String[3];
                    
                    args[0] = "-d";
                    args[1] = outputDir;
                    args[2] = outputDirWithSep + this.getName() + ".jasmin";
                }
                
                jasmin.Main.main(args);
            }
                /*        
            Process p;
            
            if(outputDir.equals(""))
                p = Runtime.getRuntime().exec("java jasmin.Main " + this.getName() + ".jasmin");
            else 
                p = Runtime.getRuntime().exec("java jasmin.Main -d " + outputDir + " " + outputDirWithSep + this.getName() + ".jasmin");
            
            try {
                p.waitFor();
            } catch(InterruptedException e)
            {
            }
            */
            
            tempFile.delete();
            
            if(ca.mcgill.sable.soot.Main.isProfilingOptimization)
                ca.mcgill.sable.soot.Main.assembleJasminTimer.end(); 
            
        } catch(IOException e)
        {
            throw new RuntimeException("Could not produce new classfile! (" + e + ")");
        }

         
    }
    
    public String toString()
    {
        return getName();
    }

    // gives numeric names to private fields and methods.
    public void renameFieldsAndMethods(boolean privateOnly)
    {
        // Rename fields.  Ignore collisions for now.
        {
            Iterator fieldIt = this.getFields().iterator();
            int fieldCount = 0;

            if(fieldIt.hasNext())
            {
                while(fieldIt.hasNext())
                  {
                      SootField f = (SootField)fieldIt.next();
                      if (!privateOnly || Modifier.isPrivate(f.getModifiers()))
                        {
                          String newFieldName = "__field"+(fieldCount++);
                          f.setName(newFieldName);
                        }
                  }
            }
        }

        // Rename methods.  Again, ignore collisions for now.
        {
            Iterator methodIt = this.getMethods().iterator();
            int methodCount = 0;

            if(methodIt.hasNext())
            {
                while(methodIt.hasNext())
                  {
                      SootMethod m = (SootMethod)methodIt.next();
                      if (!privateOnly || Modifier.isPrivate(m.getModifiers()))
                        {
                          String newMethodName = "__method"+(methodCount++);
                          m.setName(newMethodName);
                        }
                  }
            }
        }
    }

}





