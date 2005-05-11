/* Soot - a J*va Optimization Framework
 * Copyright (C) 1997-1999 Raja Vallee-Rai
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




// Incomplete class

package soot;




/**
 *   A class that provides static methods and constants to represent and
 *   work with with Java modifiers (ie public, final,...)
 *   Represents Java modifiers as int constants that can be packed and
 *   combined by bitwise operations and methods to query these.
 *   
 */
public class Modifier
{
    public static final int ABSTRACT =     0x0400;
    public static final int FINAL =        0x0010;
    public static final int INTERFACE =    0x0200;
    public static final int NATIVE =       0x0100;
    public static final int PRIVATE =      0x0002;
    public static final int PROTECTED =    0x0004;
    public static final int PUBLIC =       0x0001;
    public static final int STATIC =       0x0008;
    public static final int SYNCHRONIZED = 0x0020;
    public static final int TRANSIENT =    0x0080; /* VARARGS for methods */
    public static final int VOLATILE =     0x0040; /* BRIDGE for methods */
    public static final int STRICTFP =     0x0800; 
    public static final int ANNOTATION =   0x2000; 
    public static final int ENUM =         0x4000; 
    
    private Modifier()
    {
    }

    public static boolean isAbstract(int m)
    {
        return (m & ABSTRACT) != 0;
    }

    public static boolean isFinal(int m )
    {
        return (m & FINAL) != 0;
    }

    public static boolean isInterface(int m)
    {
        return (m & INTERFACE) != 0;
    }

    public static boolean isNative(int m)
    {
        return (m & NATIVE) != 0;
    }

    public static boolean isPrivate(int m)
    {
        return (m & PRIVATE) != 0;
    }

    public static boolean isProtected(int m)
    {
        return (m & PROTECTED) != 0;
    }

    public static boolean isPublic(int m)
    {
        return (m & PUBLIC) != 0;
    }

    public static boolean isStatic(int m)
    {
        return (m & STATIC) != 0;
    }

    public static boolean isSynchronized(int m)
    {
        return (m & SYNCHRONIZED) != 0;
    }

    public static boolean isTransient(int m )
    {
        return (m & TRANSIENT) != 0;
    }

    public static boolean isVolatile(int m)
    {
        return (m & VOLATILE) != 0;
    }

    public static boolean isStrictFP(int m)
    {
        return (m & STRICTFP) != 0;
    }
    
    public static boolean isAnnotation(int m)
    {
        return (m & ANNOTATION) != 0;
    }
    
    public static boolean isEnum(int m)
    {
        return (m & ENUM) != 0;
    }
    
    /**
     *  Converts the given modifiers to their string representation, in canonical form.
     *   @param m  a modifier set
     *   @return a textual representation of the modifiers.
     */
    public static String toString(int m)
    {
        StringBuffer buffer = new StringBuffer();

        if(isPublic(m))
            buffer.append("public ");
        else if(isPrivate(m))
            buffer.append("private ");
        else if(isProtected(m))
            buffer.append("protected ");

        if(isAbstract(m))
            buffer.append("abstract ");

        if(isInterface(m))
            buffer.append("interface ");

        if(isStatic(m))
            buffer.append("static ");

        if(isFinal(m))
            buffer.append("final ");

        if(isSynchronized(m))
            buffer.append("synchronized ");

        if(isNative(m))
            buffer.append("native ");

        if(isTransient(m))
            buffer.append("transient ");

        if(isVolatile(m))
            buffer.append("volatile ");

        if(isStrictFP(m))
            buffer.append("strictfp ");
        
        if(isAnnotation(m))
            buffer.append("annotation ");
        
        if(isEnum(m))
            buffer.append("enum ");
        
        return (buffer.toString()).trim();
    }

}
