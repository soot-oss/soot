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

import soot.util.*;
import java.util.*;


/**
 *   A class that models Java's reference types. RefTypes are parametrized by a class name.
 *   Two RefType are equal iff they are parametrized by the same class name as a String.
 */

public class RefType extends BaseType implements ToBriefString
{
    /** the class name that parametrizes this RefType */
    public final String className;

    private static RefType singleton = new RefType("");

    private RefType(String className)
    {
        this.className = className;
    }

    /** 
     *  Create a RefType for a class. 
     *  @param className The name of the class used to parametrize the created RefType.
     *  @return a RefType for the given class name.
     */
    public static RefType v(String className)
    {
        return new RefType(className);
    }
    
    /** 
     *  Create a RefType for a class. 
     *  @param c A SootClass for which to create a RefType.
     *  @return a RefType for the given SootClass..
     */
    public static RefType v(SootClass c)
    {
        return v(c.getName());
    }
    
    /** 
     *  Get the default RefType. It is parametrized by an empty class name string.Implemented as
     *  a singleton.
     *  @return a default RefType
     */
    public static RefType v()
    {
        return singleton;
    }
    
     /** 
      *  Get the SootClass object corresponding to this RefType.
      *  @return the corresponding SootClass
      */    
    public SootClass getSootClass()
    {
        return Scene.v().getSootClass(className);
    }

    /** 
     *  2 RefTypes are considered equal if they are parametrized by the same class name String.
     *  @param t an object to test for equality.
     *  @ return true if t is a RefType parametrized by the same name as this.
     */
    public boolean equals(Object t)
    {
        return ((t instanceof RefType) && className.equals(((RefType) t).className));
    }

    public String toString()
    {
        return className;
    }

    public String toBriefString()
    {
            return className;
    }

    public int hashCode()
    {
        return className.hashCode();
    }

    public void apply(Switch sw)
    {
        ((TypeSwitch) sw).caseRefType(this);
    }
}
