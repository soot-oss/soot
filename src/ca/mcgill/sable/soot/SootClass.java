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
 The reference version is: $BafVersion: 0.4 $

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
 
package ca.mcgill.sable.soot;

import ca.mcgill.sable.util.*;
import java.io.PrintStream;

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
                
    public SootClass(String name, int modifiers)
    {
        this.name = name;
        this.modifiers = modifiers;
        isResolved = false;
    }

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
    
    public boolean isResolved()
    {
        return isResolved;
    }
    
    public void setResolved(boolean flag)
    {
        isResolved = flag;   
    }
    
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
    
    public void resolveIfNecessary()
    {
        if(!isResolved)
            resolve();
    }
    
    public boolean isManaged()
    {
        return isManaged;
    }

    public SootClassManager getManager() throws NotManagedException
    {
        return manager;
    }
            
    public int getFieldCount()
    {
        return fields.size();
    }
    
    public List getFields()
    {
        resolveIfNecessary();

        return Collections.unmodifiableList(fields);
    }

    /*    
    public void setFields(Field[] fields) 
    {
        this.fields = new ArraySet(fields);
    }
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

    public void removeField(SootField f) throws IncorrectDeclarerException
    {
        resolveIfNecessary();

        if(!f.isDeclared() || f.getDeclaringClass() != this)
            throw new IncorrectDeclarerException(f.getName());
        
        fields.remove(f);
        f.isDeclared = false;
    }
    
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
    
    public int getMethodCount()
    {
        return methods.size();
    }
    
    public List getMethods()
    {
        resolveIfNecessary();
        
        return Collections.unmodifiableList(methods);
    }
    
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
    
    public void removeMethod(SootMethod m) throws IncorrectDeclarerException
    {
        resolveIfNecessary();

        if(!m.isDeclared() || m.getDeclaringClass() != this)
            throw new IncorrectDeclarerException(m.getName());
            
        methods.remove(m);
        m.isDeclared = false;
    }
    
    public int getModifiers() 
    {
        resolveIfNecessary();
        return modifiers;
    }
    
    public void setModifiers(int modifiers) 
    {
        resolveIfNecessary();
        this.modifiers = modifiers;
    }
    
    public int getInterfaceCount()
    {
        return interfaces.size();
    }
    
    public List getInterfaces() 
    {
        resolveIfNecessary();
        
        return Collections.unmodifiableList(interfaces);
    }
    
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
     * @exception DuplicateNameException    if this class already implements the given interface
     */
     
    public void addInterface(SootClass interfaceClass) throws DuplicateNameException
    {
        resolveIfNecessary();
        if(implementsInterface(interfaceClass.getName()))
            throw new DuplicateNameException(interfaceClass.getName());
            
        interfaces.add(interfaceClass);
    }
    
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
    
    public boolean hasSuperClass()
    {
        resolveIfNecessary();
        
        return superClass != null;
    }
    
    public SootClass getSuperClass() throws NoSuperClassException 
    {
        resolveIfNecessary();
        if(superClass == null)
            throw new NoSuperClassException();
        else
            return superClass;
    }
    
    /** 
     * Pass this function a null to unset the super class.
     */
         
    public void setSuperClass(SootClass c) 
    {
        resolveIfNecessary();
        superClass = c;
    }
    
    public String getName()
    {
        return name;
    }   
    
    public void setName(String name) throws DuplicateNameException
    {
        this.name = name;
    }

}





