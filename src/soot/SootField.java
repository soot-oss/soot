/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
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


package soot;

import soot.tagkit.*;
import soot.jimple.*;
import java.util.*;

/**
    Soot representation of a Java field.  Can be declared to belong to a SootClass.
*/
public class SootField extends AbstractHost implements ClassMember
{
    String name;
    Type type;
    int modifiers;

    boolean isDeclared = false;
    SootClass declaringClass;
    boolean isPhantom = false;

    /** Constructs a Soot field with the given name, type and modifiers. */
    public SootField(String name, Type type, int modifiers)
    {
        this.name = name;
        this.type = type;
        this.modifiers = modifiers;
    }

    /** Constructs a Soot field with the given name, type and no modifiers. */
    public SootField(String name, Type type)
    {
        this.name = name;
        this.type = type;
        this.modifiers = 0;
    }

    public int equivHashCode()
    {
        return type.hashCode() * 101 + modifiers * 17 + name.hashCode();
    }

    public String getName()
    {
        return name;
    }

    public String getSignature()
    {
        StringBuffer buffer = new StringBuffer();

        buffer.append("<" + Scene.v().quotedNameOf(getDeclaringClass().getName()) + ": ");
        buffer.append(getType() + " " + Scene.v().quotedNameOf(getName()) + ">");

        return buffer.toString();

    }
  
    public String getSubSignature()
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append(getType() + " " + Scene.v().quotedNameOf(getName()));
        return buffer.toString();
    }
    
    public SootClass getDeclaringClass() 
    {
        if(!isDeclared)
            throw new RuntimeException("not declared: "+getName()+" "+getType());

        return declaringClass;
    }

    public boolean isPhantom()
    {
        return isPhantom;
    }
    
    public void setPhantom(boolean value)
    {
        isPhantom = value;
    }

    public boolean isDeclared()
    {
        return isDeclared;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Type getType()
    {
        return type;
    }

    public void setType(Type t)
    {
        this.type = t;
    }

    public boolean isPublic()
    {
        return Modifier.isPublic(this.getModifiers());
    }

    public boolean isProtected()
    {
        return Modifier.isProtected(this.getModifiers());
    }

    public boolean isPrivate()
    {
        return Modifier.isPrivate(this.getModifiers());
    }

    public void setModifiers(int modifiers)
    {
        if (!declaringClass.isApplicationClass())
            throw new RuntimeException("Cannot set modifiers of a field from a non-app class!");
            
        this.modifiers = modifiers;
    }

    public int getModifiers()
    {
        return modifiers;
    }

    public String toString()
    {
        return getSignature();
    }


    private String getOriginalStyleDeclaration()
    {
        String qualifiers = Modifier.toString(modifiers) + " " + type.toString();
        qualifiers = qualifiers.trim();

        if(qualifiers.equals(""))
            return Scene.v().quotedNameOf(name);
        else
            return qualifiers + " " + Scene.v().quotedNameOf(name) + "";

    }


    public String getDeclaration()
    {
        return getOriginalStyleDeclaration();
    }
}






