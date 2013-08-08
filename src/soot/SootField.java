/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
 * Copyright (C) 2004 Ondrej Lhotak
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

/*
 * Modified by the Sable Research Group and others 1997-1999.  
 * See the 'credits' file distributed with Soot for the complete list of
 * contributors.  (Soot is distributed at http://www.sable.mcgill.ca/soot)
 */


package soot;

import soot.tagkit.*;
import soot.util.*;
import soot.jimple.paddle.PaddleField;
import soot.jimple.spark.pag.SparkField;

/**
    Soot representation of a Java field.  Can be declared to belong to a SootClass.
*/
public class SootField extends AbstractHost implements ClassMember, SparkField, Numberable, PaddleField
{
    String name;
    Type type;
    int modifiers;

    boolean isDeclared = false;
    SootClass declaringClass;
    protected boolean isPhantom = false;

    /** Constructs a Soot field with the given name, type and modifiers. */
    public SootField(String name, Type type, int modifiers)
    {
        this.name = name;
        this.type = type;
        this.modifiers = modifiers;
        if( type instanceof RefLikeType ) Scene.v().getFieldNumberer().add(this);
    }

    /** Constructs a Soot field with the given name, type and no modifiers. */
    public SootField(String name, Type type)
    {
        this.name = name;
        this.type = type;
        this.modifiers = 0;
        if( type instanceof RefLikeType ) Scene.v().getFieldNumberer().add(this);
    }

    public int equivHashCode()
    {
        return type.hashCode() * 101 + modifiers * 17 + name.hashCode();
    }

    public String getName()
    {
        return name;
    }

    public String getSignature() {
        return getSignature(declaringClass, getName(), getType());
    }
    public static String getSignature(SootClass cl, String name, Type type)
    {
        StringBuffer buffer = new StringBuffer();

        buffer.append("<" + Scene.v().quotedNameOf(cl.getName()) + ": ");
        buffer.append(type + " " + Scene.v().quotedNameOf(name) + ">");

        return buffer.toString().intern();

    }
  
    public String getSubSignature()
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append(getType() + " " + Scene.v().quotedNameOf(getName()));
        return buffer.toString().intern();
    }

    public SootClass getDeclaringClass() 
    {
        if(!isDeclared)
            throw new RuntimeException("not declared: "+getName()+" "+getType());

        return declaringClass;
    }

    @Override
    public boolean isPhantom()
    {
        return isPhantom;
    }
    
    @Override
    public void setPhantom(boolean value)
    {
        if( value ) {
            if( !Scene.v().allowsPhantomRefs() ) 
                throw new RuntimeException( "Phantom refs not allowed" );
            if( declaringClass != null && !declaringClass.isPhantom() )
                throw new 
                    RuntimeException( "Declaring class would have to be phantom" );
        }
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

    /**
     * Convenience method returning true if this field is public.
     */
    public boolean isPublic()
    {
        return Modifier.isPublic(this.getModifiers());
    }

    /**
     * Convenience method returning true if this field is protected.
     */
    public boolean isProtected()
    {
        return Modifier.isProtected(this.getModifiers());
    }

    /**
     * Convenience method returning true if this field is private.
     */
    public boolean isPrivate()
    {
        return Modifier.isPrivate(this.getModifiers());
    }

    /**
     * Convenience method returning true if this field is static.
     */
    public boolean isStatic()
    {
        return Modifier.isStatic(this.getModifiers());
    }

    /**
     * Convenience method returning true if this field is final.
     */
    public boolean isFinal()
    {
        return Modifier.isFinal(this.getModifiers());
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
    public final int getNumber() {
        return number;
    }
    public final void setNumber(int number) {
        this.number = number;
    }
    private int number = 0;
    public SootFieldRef makeRef() {
        return Scene.v().makeFieldRef(declaringClass, name, type, isStatic());
    }
}






