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
import ca.mcgill.sable.soot.baf.*;

public class SootMethod
{
    String name;
    List parameterTypes;
    Type returnType;
    
    boolean isDeclared;
    SootClass declaringClass;
    
    int modifiers;

    List exceptions = new ArrayList();
    
    InstListBody instListBody;

    ca.mcgill.sable.soot.coffi.ClassFile coffiClass;
    ca.mcgill.sable.soot.coffi.method_info coffiMethod;
            
    public SootMethod(String name, List parameterTypes, Type returnType)
    {
        this.name = name;
        this.parameterTypes = new ArrayList();
        this.parameterTypes.addAll(parameterTypes);
        this.returnType = returnType; 
    }
    
    public SootMethod(String name, List parameterTypes, Type returnType, int modifiers)
    {
        this.name = name;
        this.parameterTypes = new ArrayList();
        this.parameterTypes.addAll(parameterTypes);
        
        this.returnType = returnType;
        this.modifiers = modifiers;
    }
    
    public SootMethod(String name, List parameterTypes, Type returnType, int modifiers, 
        InstListBody instListBody)
    {
        this.name = name;
        this.parameterTypes = new ArrayList();
        this.parameterTypes.addAll(parameterTypes);
        this.returnType = returnType;
        this.modifiers = modifiers;
        
        this.instListBody = instListBody;
    }
    
    public SootMethod(String name, List parameterTypes, Type returnType, InstListBody instListBody)
    {
        this.name = name;
        this.parameterTypes = new ArrayList();
        this.parameterTypes.addAll(parameterTypes);
        this.returnType = returnType;
        this.instListBody = instListBody;
    }
    
    public void setSource(ca.mcgill.sable.soot.coffi.ClassFile coffiClass, 
        ca.mcgill.sable.soot.coffi.method_info coffiMethod)
    {
        this.coffiClass = coffiClass;
        this.coffiMethod = coffiMethod; 
    }
    
    public String getName()
    {
        return name;
    }
        
    public SootClass getDeclaringClass() throws NotDeclaredException
    {
        if(!isDeclared)
            throw new NotDeclaredException(getName());
            
        return declaringClass;
    }
         
    public boolean isDeclared()
    {
        return isDeclared;
    }
    
    
    public void setName(String name) 
    {
        this.name = name;
    }
    
    public int getModifiers() 
    {
        return modifiers;
    }
    
    public void setModifiers(int modifiers) 
    {
        this.modifiers = modifiers;
    }    
    
    public Type getReturnType() 
    {
        return returnType;
    }
    
    public void setReturnType(Type t)
    {
        returnType = t;
    }
    
    public int getParameterCount()
    {
        return parameterTypes.size();
    }

    public Type getParameterType(int n)
    {
        return (Type) parameterTypes.get(n);
    }
    
    public List getParameterTypes()
    {
        return Collections.unmodifiableList(parameterTypes);
    }

    public void setInstListBody(InstListBody instListBody)
    {
        this.instListBody = instListBody;
    }
    
    public InstListBody getInstListBody()
    {
        if(instListBody == null)
        {
            instListBody = new InstListBody(this, coffiClass, coffiMethod);
            coffiClass = null;
            coffiMethod = null;
        }       
        
        return instListBody;
    }

    public void addException(SootClass e) throws AlreadyThrowsException
    {
        if(exceptions.contains(e))
            throw new AlreadyThrowsException(e.getName());
        
        exceptions.add(e);
    }
    
    public void removeException(SootClass e) throws DoesNotThrowException
    {
        if(!exceptions.contains(e))
            throw new DoesNotThrowException(e.getName());
    }
    
    public boolean throwsException(SootClass e)
    {
        return exceptions.contains(e);
    }
    
    public List getExceptions()
    {
        return Collections.unmodifiableList(exceptions);
    }
    
    public void setParameterTypes(List parameterTypes)
    {
        this.parameterTypes = new ArrayList();
        this.parameterTypes.addAll(parameterTypes);
    }
    
    
    /** 
     * For convenience.  
     */
    
    public boolean isStatic()
    {
        return Modifier.isStatic(this.getModifiers());
    }
    
    public String getSignature()
    {
        StringBuffer buffer = new StringBuffer();
        
        buffer.append(getDeclaringClass().getName());
        buffer.append(".");
        buffer.append(getName());
        buffer.append("(");
        
        Iterator typeIt = getParameterTypes().iterator();
        
        if(typeIt.hasNext())
        {
            buffer.append(typeIt.next());
            
            while(typeIt.hasNext())
            {
                buffer.append(",");
                buffer.append(typeIt.next());
            }
        }
        
        buffer.append(")");
        
        buffer.append(":" + getReturnType().toString());
        
        return buffer.toString();
    }
}

