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

/** Represents types within Soot, e.g. int, java.lang.String. */
public abstract class Type implements Switchable, ToBriefString
{
    /** Returns a textual representation of this type. */
    public abstract String toString();
    
    /** Returns a brief textual representation of this type. */
    public String toBriefString()
    {
        return toString();
    }

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

    /** Returns the XML representation of this type. */
    public String getXML() 
    {
	return XMLManager.getXML(this);
    }

    /** Returns the least common superclass of this type and other. */
    public Type merge(Type other, Scene cm)
    {
        if(this.equals(UnknownType.v()))
            return other;
        else if(other.equals(UnknownType.v()))
            return this;
        else if(this.equals(other))
            return this;
        else if(this instanceof RefType && other instanceof RefType)
        {
            // Return least common superclass

            SootClass thisClass = cm.getSootClass(((RefType) this).className);
            SootClass otherClass = cm.getSootClass(((RefType) other).className);
            SootClass javalangObject = cm.getSootClass("java.lang.Object");

            LinkedList thisHierarchy = new LinkedList();
            LinkedList otherHierarchy = new LinkedList();

            // Build thisHierarchy
            {
                SootClass SootClass = thisClass;

                for(;;)
                {
                    thisHierarchy.addFirst(SootClass);

                    if(SootClass == javalangObject)
                        break;

                    SootClass = SootClass.getSuperclass();
                }
            }

            // Build otherHierarchy
            {
                SootClass SootClass = otherClass;

                for(;;)
                {
                    otherHierarchy.addFirst(SootClass);

                    if(SootClass == javalangObject)
                        break;

                    SootClass = SootClass.getSuperclass();
                }
            }

            // Find least common superclass
            {
                SootClass commonClass = null;

                while(!otherHierarchy.isEmpty() && !thisHierarchy.isEmpty() &&
                    otherHierarchy.getFirst() == thisHierarchy.getFirst())
                {
                    commonClass = (SootClass) otherHierarchy.removeFirst();
                    thisHierarchy.removeFirst();
                }

                return RefType.v(commonClass.getName());
            }
        }
        else
            throw new IllegalTypeMergeException(this + " and " + other);
    }

    /** Method required for use of Switchable. */
    public void apply(Switch sw)
    {
    }
}
