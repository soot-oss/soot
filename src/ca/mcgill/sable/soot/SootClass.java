/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * Soot, a Java(TM) classfile optimization framework.                *
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

 - Modified on November 2, 1998 by Raja Vallee-Rai (kor@sable.mcgill.ca) (*)
   Repackaged all source files and performed extensive modifications.
   First initial release of Soot.

 - Modified on 15-Jun-1998 by Raja Vallee-Rai (kor@sable.mcgill.ca). (*)
   First internal release (Version 0.1).
*/

package ca.mcgill.sable.soot;

import ca.mcgill.sable.util.*;
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
    Instances of this class represent Java classes.  They are usually created by a SootClassManager,
    but can also be constructed manually through the given constructors.

*/

public class SootClass
{
    String name;
    int modifiers;
    List fields = new ArrayList();
    List methods = new ArrayList();
    List interfaces = new ArrayList();

    SootClassManager manager;
    boolean isManaged;

    SootClass superClass;
    boolean isResolved;

    /**
        Constructs an empty SootClass with the given name and modifiers.
    */

    public SootClass(String name, int modifiers)
    {
        this.name = name;
        this.modifiers = modifiers;
        isResolved = false;
    }

    /**
        Constructs an empty SootClass with the given name and no modifiers.
    */

    public SootClass(String name)
    {
        this.name = name;
        this.modifiers = 0;
        isResolved = false;
    }

    /*
    public void jimplifyMethods()
    {
        resolveIfNecessary();

        Iterator methodIt = getMethods().iterator();

        while(methodIt.hasNext())
        {
            SootMethod m = (SootMethod) methodIt.next();

            if(!m.isJimplified())
                m.jimplify();
        }
    }
    */

    /**
        Have the methods and fields for this class been loaded? False indicates that the class has been referred
        to but is not resolved in this sense.
    */

    public boolean isResolved()
    {
        return isResolved;
    }

    /**
        Establishes the resolution state of the class (see isResolved()).  This is useful when
        constructing the class such as with Coffi.
    */

    public void setResolved(boolean flag)
    {
        isResolved = flag;
    }

    /**
        Resolves the class by loading the fields and methods from the original class file.  This creates SootFields and SootMethods.
    */

    public void resolve()
    {
        if(isResolved)
            throw new RuntimeException("SootClass " + getName() + " already resolved!");

        isResolved = true;

        /*
        if(Main.isProfilingOptimization)
            Main.resolveTimer.start();
          */

        ca.mcgill.sable.soot.coffi.Util.resolveClass(this);

        /*
        if(Main.isProfilingOptimization)
            Main.resolveTimer.end(); */
    }

    /**
        Resolves the class if it has not been resolved yet.
    */

    public void resolveIfNecessary()
    {
        if(!isResolved)
            resolve();
    }

    /**
        Is this class being managed by a SootClassManager? A class may be unmanaged  while it is being constructed.
    */

    public boolean isManaged()
    {
        return isManaged;
    }

    /**
        Returns the SootClassManager of this class.
    */

    public SootClassManager getManager() throws NotManagedException
    {
        return manager;
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
        resolveIfNecessary();

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

    public void addField(SootField f) throws AlreadyDeclaredException, DuplicateNameException
    {
        resolveIfNecessary();

        if(f.isDeclared())
            throw new AlreadyDeclaredException(f.getName());

        if(declaresField(f.getName()))
            throw new DuplicateNameException(f.getName());

        fields.add(f);

        f.isDeclared = true;
        f.declaringClass = this;
    }

    /**
        Removes the given field from this class.
    */

    public void removeField(SootField f) throws IncorrectDeclarerException
    {
        resolveIfNecessary();

        if(!f.isDeclared() || f.getDeclaringClass() != this)
            throw new IncorrectDeclarerException(f.getName());

        fields.remove(f);
        f.isDeclared = false;
    }

    /**
        Returns the field of this class with the given name.
    */

    public SootField getField(String name) throws ca.mcgill.sable.soot.NoSuchFieldException
    {
        resolveIfNecessary();

        Iterator fieldIt = getFields().iterator();

        while(fieldIt.hasNext())
        {
            SootField field = (SootField) fieldIt.next();

            if(field.name.equals(name))
                return field;
        }

        throw new ca.mcgill.sable.soot.NoSuchFieldException("No field " + name + " in class " + getName());
    }

    /**
        Does this class declare a field with the given name?
    */

    public boolean declaresField(String name)
    {
        resolveIfNecessary();

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
        resolveIfNecessary();

        return methods;
    }

    /**
        Returns the method of this class with the given signature.  The signature consists of a name
        and a list of parameter types.
    */

    public SootMethod getMethod(String name, List parameterTypes) throws
        ca.mcgill.sable.soot.NoSuchMethodException
    {
        resolveIfNecessary();
        // inefficient

        Iterator methodIt = getMethods().iterator();

        while(methodIt.hasNext())
        {
            SootMethod method = (SootMethod) methodIt.next();

            if(method.getName().equals(name) &&
                parameterTypes.equals(method.getParameterTypes()))
                return method;
        }

        throw new ca.mcgill.sable.soot.NoSuchMethodException();
    }

    /**
        Does this class declare a method with the given signature? (see getMethod(String, List))
    */

    public boolean declaresMethod(String name, List parameterTypes)
    {
        resolveIfNecessary();
        // inefficient

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

    /*
    public void setMethods(Method[] method)
    {
        methods = new ArraySet(method);
    }
    */

    /**
        Adds the given method to this class.
    */

    public void addMethod(SootMethod m) throws AlreadyDeclaredException, DuplicateNameException
    {
        resolveIfNecessary();

        if(m.isDeclared())
            throw new AlreadyDeclaredException(m.getName());

        if(declaresMethod(m.getName(), m.getParameterTypes()))
            throw new DuplicateNameException("duplicate signature for: " + m.getName());

        methods.add(m);
        m.isDeclared = true;
        m.declaringClass = this;
    }

    /**
        Removes the given method from this class.
    */

    public void removeMethod(SootMethod m) throws IncorrectDeclarerException
    {
        resolveIfNecessary();

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
        resolveIfNecessary();
        return modifiers;
    }

    /**
        Sets the modifiers for this class.
    */

    public void setModifiers(int modifiers)
    {
        resolveIfNecessary();
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
        resolveIfNecessary();

        return interfaces;
    }

    /**
        Does this class directly implement the given interface? (see getInterfaceCount())
    */

    public boolean implementsInterface(String name)
    {
        resolveIfNecessary();

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
        resolveIfNecessary();
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


    public boolean hasSuperClass()
    {
        resolveIfNecessary();

        return superClass != null;
    }

    /**
        Returns the superclass of this class. (see hasSuperClass())
    */

    public SootClass getSuperClass() throws NoSuperClassException
    {
        resolveIfNecessary();
        if(superClass == null)
            throw new NoSuperClassException();
        else
            return superClass;
    }

    /**
        Sets the superclass of this class.  Note that passing a null will cause the class to have no superclass.
    */

    public void setSuperClass(SootClass c)
    {
        resolveIfNecessary();
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
        Sets the name of this class.
    */

    public void setName(String name) throws DuplicateNameException
    {
        this.name = name;
    }

    public void printTo(BodyExpr bodyExpr, PrintWriter out)
    {
        printTo(bodyExpr, out, 0);
    }

    public void printTo(BodyExpr bodyExpr, PrintWriter out, int printBodyOptions)
    {
        // Print class name + modifiers
        {
            String classPrefix = "";

            classPrefix = classPrefix + " " + Modifier.toString(this.getModifiers());
            classPrefix = classPrefix.trim();

            if(!Modifier.isInterface(this.getModifiers()))
            {
                classPrefix = classPrefix + " class";
                classPrefix = classPrefix.trim();
            }

            out.print(classPrefix + " " + this.getName());
        }

        // Print extension
        {
            if(this.hasSuperClass())
                out.print(" extends " + this.getSuperClass().getName());
        }

        // Print interfaces
        {
            Iterator interfaceIt = this.getInterfaces().iterator();

            if(interfaceIt.hasNext())
            {
                out.print(" implements ");

                out.print(((SootClass) interfaceIt.next()).getName());

                while(interfaceIt.hasNext())
                {
                    out.print(",");
                    out.print(" " + ((SootClass) interfaceIt.next()).getName());
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
                        bodyExpr.resolveFor(method).printTo(out, printBodyOptions);

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

    public void write(BodyExpr bodyExpr)
    {
        try {
            File tempFile = new File("jimpleClass.jasmin");

            FileOutputStream streamOut = new FileOutputStream(tempFile);
            PrintWriter writerOut = new PrintWriter(streamOut);

            new ca.mcgill.sable.soot.jimple.JasminClass(this, bodyExpr).print(writerOut);

            writerOut.close();

            Runtime.getRuntime().exec("jasmin jimpleClass.jasmin");
            //tempFile.delete();
        } catch(IOException e)
        {
            throw new RuntimeException("Could not produce new classfile! (" + e + ")");
        }

    }
}





