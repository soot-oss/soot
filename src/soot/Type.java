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


package soot;

import soot.util.*;
import java.util.*;
import java.io.*;

/** Represents types within Soot, eg <code>int</code>, <code>java.lang.String</code>. */
public abstract class Type implements Switchable, Serializable, Numberable
{
    public Type() {
        Scene.v().getTypeNumberer().add( this );
    }
    /** Returns a textual representation of this type. */
    public abstract String toString();
    
    /** Converts the int-like types (short, byte, boolean and char) to IntType. */
    public static Type toMachineType(Type t)
    {
        if(t.equals(ShortType.v()) || t.equals(ByteType.v()) ||
            t.equals(BooleanType.v()) || t.equals(CharType.v()))
        {
            return IntType.v();
        }
        else
            return t;
    }


    /** Returns the least common superclass of this type and other. */
    public Type merge(Type other, Scene cm)
    {
        // method overriden in subclasses UnknownType and RefType 
        throw new RuntimeException("illegal type merge: "
                                   + this + " and " + other);
    }

    /** Method required for use of Switchable. */
    public void apply(Switch sw)
    {
    }

    public void setArrayType( ArrayType at ) {
        arrayType = at;
    }
    public ArrayType getArrayType() {
        return arrayType;
    }
    public ArrayType makeArrayType() {
        return ArrayType.v( this, 1 );
    }

    public final int getNumber() { return number; }
    public final void setNumber( int number ) { this.number = number; }

    protected ArrayType arrayType;
    private int number = 0;
}
